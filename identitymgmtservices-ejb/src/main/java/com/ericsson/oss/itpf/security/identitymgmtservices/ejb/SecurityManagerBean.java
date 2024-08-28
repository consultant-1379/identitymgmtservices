/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.itpf.security.identitymgmtservices.ejb;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.naming.directory.SearchControls;

import com.ericsson.oss.itpf.security.identitymgmtservices.*;
import com.ericsson.oss.itpf.security.identitymgmtservices.ejb.statistics.ProxyAccountMonitoredDataManager;
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus;
import com.ericsson.oss.itpf.security.identitymgmtservices.proxyaccount.ProxyAgentAccountGetDataManager;
import org.forgerock.opendj.ldap.GeneralizedTime;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;
import org.slf4j.Logger;

import com.ericsson.oss.itpf.sdk.cluster.lock.LockManager;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;
import com.ericsson.oss.itpf.sdk.recording.EventLevel;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.security.cryptography.CryptographyService;
import com.ericsson.oss.itpf.security.identitymgmtservices.crypto.CryptographyServiceClient;

import java.nio.charset.StandardCharsets;

import static com.ericsson.oss.itpf.security.identitymgmtservices.ejb.statistics.ProxyAccountMonitoredDataConstants.*;
import static com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus.valueOfProxyAgentAccoountAdminStatus;

@ApplicationScoped
public class SecurityManagerBean {
    private static final String LOWER_CASE_PATTERN = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER_CASE_PATTERN = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBER_CASE_PATTERN = "0123456789";

    @Inject
    protected LockManager lockManager;

    @Inject
    protected SystemRecorder systemRecorder;

    @Inject
    protected Logger logger;

    protected DSCommunicator communicator = new DSCommunicator();

    @Inject
    protected CryptographyService cryptographyService;

    @Inject
    protected IdentityManagementListener identityManagementListener;

    @Inject
    protected ProxyAccountMonitoredDataManager proxyAccountMonitoredDataManager;

    protected CryptographyServiceClient cryptoClient = null;

    private String dnPostfix = null;
    private String m2mUserContainerName = null;

    private static final ArrayList<String> attributeList = new ArrayList<>();

    //list of all posix groups
    private static final List<String> POSIX_ROLES = Arrays.asList(
            "mm-smrsusers", "amos_users", "element-manager_users", "com_users", "scripting_users");

    static {
        attributeList.add(IdmConstants.LDAP_UID);
        attributeList.add(IdmConstants.LDAP_FULL_NAME);
        attributeList.add(IdmConstants.LDAP_LAST_NAME);
        attributeList.add(IdmConstants.LDAP_UID_NUMBER);
        attributeList.add(IdmConstants.LDAP_GID_NUMBER);
        attributeList.add(IdmConstants.LDAP_HOME_DIRECTORY);
        attributeList.add(IdmConstants.LDAP_LOGIN_SHELL);
        attributeList.add(IdmConstants.LDAP_ACCOUNT_EXPIRATION_TIME);
    }

    @PostConstruct
    public void init() {
        logger.debug("Init");
        dnPostfix = getDnPostfix();   // Inizialise postfix
        if (logger.isDebugEnabled()) {
            logger.debug("Init finished: {}", dnPostfix);
        }
    }

    ProxyAgentAccountData addProxyAccount() {
        final StringBuilder securityLogMsg = new StringBuilder("User Information: userName=");
        final Lock proxyLock = getDistributedLock(IdmConstants.PROXY_LOCK_NAME);
        if (logger.isDebugEnabled()) {
            logger.debug("[IDMS_DISTRIBUTED_LOCK] acquiring lock {}", IdmConstants.PROXY_LOCK_NAME);
        }
        proxyLock.lock();
        if (logger.isDebugEnabled()) {
            logger.debug("[IDMS_DISTRIBUTED_LOCK] acquired lock {}", IdmConstants.PROXY_LOCK_NAME);
        }

        try {
            String userName;
            logger.debug("addProxyUser: generateRandomUniqueCn for new Proxy Account");
            userName = generateRandomUniqueCn();
            securityLogMsg.append(userName);
            final String userPassword = generateExtendedPassword();

            final String userDN = makeProxyUserDN(userName);
            final HashMap<String, ArrayList<String>> attributeDNPairs = prepareProxyAccountAttributesExtended(userName,
                    userPassword, "false");
            logger.debug("addProxyUser: invoking communicator addEntry operation for user {}", userName);
            communicator.addEntry(userDN, attributeDNPairs);
            communicator.modifyEntryReplace(userDN, IdmConstants.LDAP_CRED_RESET, "false");
            logSecurityEvent(securityLogMsg.toString(), IdmConstants.SECURITY_EVENT_PROXY_USER_CREATION, IdmConstants.SECURITY_EVENT_STATUS_SUCCESS);
            increaseProxyAccountMonitoredData(userDN, CREATE_CMD);
            return new ProxyAgentAccountData(userDN, userPassword);
        } catch (final DSCommunicatorException e) {
            logger.error("addProxyAccount excp", e);
            final String errMsg = "Problem adding new Proxy Account due to DSCommunicatorException: " + e.getMessage();
            logSecurityEvent(securityLogMsg.toString(), IdmConstants.SECURITY_EVENT_PROXY_USER_CREATION, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
            throw new IdentityManagementServiceException(errMsg, convertLDAPErrorToIdmError(e.ldapErrorCode()));
        } catch (final IdentityManagementServiceException e) {
            logSecurityEvent(securityLogMsg.toString(), IdmConstants.SECURITY_EVENT_PROXY_USER_CREATION, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
            throw e;
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("[IDMS_DISTRIBUTED_LOCK] releasing lock {}", IdmConstants.PROXY_LOCK_NAME);
            }
            proxyLock.unlock();
            if (logger.isDebugEnabled()) {
                logger.debug("[IDMS_DISTRIBUTED_LOCK] released lock {}", IdmConstants.PROXY_LOCK_NAME);
            }
        }
    }

    private String encryptPassword(final String userPassword) {

        if (cryptoClient == null) {
            cryptoClient = new CryptographyServiceClient(cryptographyService);
        }
        final String encryptedPassword = cryptoClient.getEncryptedPassword(userPassword);
        if (encryptedPassword == null) {
            final String errMsg = "problem in encrypting the password using cryptography service";
            throw new IdentityManagementServiceException(errMsg, IdentityManagementServiceException.Error.UNEXPECTED_ERROR);
        }
        return encryptedPassword;
    }

    private String generatePassword(final String userName) {
        final String password = communicator.getRandomPassword();

        if (password == null) {
            final String errMsg = "Failed to generate password for User " + userName + ".";
            logger.error("generatePassword: {}", errMsg);
            throw new IdentityManagementServiceException(errMsg, IdentityManagementServiceException.Error.UNEXPECTED_ERROR);
        }
        return password;
    }

    private String generateExtendedPassword() {

        return getExtendedRandomPassword(
                identityManagementListener.getPasswordLength());
    }

    private String getExtendedRandomPassword(int len) {
        String chars = new StringBuilder().append(NUMBER_CASE_PATTERN).
                append(UPPER_CASE_PATTERN).append(LOWER_CASE_PATTERN).toString();
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private HashMap<String, ArrayList<String>> prepareProxyAccountAttributesExtended(final String userName,
                                                                                     final String password, final String accountDisabled) {
        final HashMap<String, ArrayList<String>> attributeDNPairs = new HashMap<>();
        final ArrayList<String> userObjectClassList = getProxyUserObjectClassesForLDAP();
        attributeDNPairs.put("objectClass", userObjectClassList);
        attributeDNPairs.put(IdmConstants.LDAP_FULL_NAME, getArrayList(userName));
        attributeDNPairs.put(IdmConstants.LDAP_LAST_NAME, getArrayList(userName));
        attributeDNPairs.put(IdmConstants.LDAP_USER_CRED, getArrayList(password));

        attributeDNPairs.put("ds-pwp-account-disabled", getArrayList(accountDisabled));


        return attributeDNPairs;
    }

    void addM2MUser(final String userName, final String homeDir, final String groupName, final int lifeSpan) {
        logger.debug("addM2MUser: user[{}] group[{}] home[{}] validDays[{}]", userName, groupName, homeDir, lifeSpan);
        createM2MUser(userName, homeDir, groupName, lifeSpan);
    }

    private ArrayList<String> getArrayList(final String value) {
        final ArrayList<String> objs = new ArrayList<>();
        objs.add(value);
        return objs;
    }

    private ArrayList<String> getLDAPUserObjectClasses() {
        final ArrayList<String> objs = new ArrayList<>();
        objs.add("top");
        objs.add("organizationalPerson");
        objs.add("inetorgperson");
        objs.add("person");
        objs.add("posixAccount");
        return objs;
    }

    /* This method generates and returns a list of objectClass parameters used to create proxy account */
    private ArrayList<String> getProxyUserObjectClassesForLDAP() {
        final ArrayList<String> objs = new ArrayList<>();
        objs.add("top");
        objs.add("inetorgperson");
        objs.add("person");
        return objs;
    }

    private void addM2MUserToGroup(final String userName, final String groupName) {
        final String groupDN = makeGroupDN(groupName);
        try {
            // assuming group entry already exist, user has not been listed under uniqueMember in the group
            communicator.modifyEntryAdd(groupDN, getProperAttribute(groupName), makeUserDN(userName));
        } catch (final DSCommunicatorException e) {
            final String errMsg = "addM2MUserToGroup fails";
            logger.error(errMsg, e);
            throw new IdentityManagementServiceException(errMsg, convertLDAPErrorToIdmError(e.ldapErrorCode()));
        }
    }

    /* This method is used to remove a proxy user from LDAP */
    boolean removeProxyAccount(final String userDN) {
        final String securityLogMsg = "User Information: userDN=" + userDN;
        try {
            logger.debug("Proxy user Start deleting ");
            communicator.deleteEntry(userDN);
            logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_PROXY_USER_DELETION, IdmConstants.SECURITY_EVENT_STATUS_SUCCESS);
            logger.debug("Proxy user {} has been deleted successfuly", userDN);
            increaseProxyAccountMonitoredData(userDN, DELETE_CMD);
            return true;
        } catch (final DSCommunicatorException e) {
            logger.error("removeProxyAccount excp", e);
            if (e.ldapErrorCode() == IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY) {
                final String errMsg = "User " + userDN + " does not exist";
                logger.warn(errMsg);
            } else {
                final String errMsg = "removeProxyAccount: Problem occurred on removing user " + userDN;
                logger.error("{}, {}", errMsg, e.getMessage());
                logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_PROXY_USER_DELETION, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
                throw new IdentityManagementServiceException(errMsg, convertLDAPErrorToIdmError(e.ldapErrorCode()));
            }
        }
        return false;
    }

    boolean removeM2MUser(final String userName) {
        final String securityLogMsg = "User Information: userName=" + userName;
        final String userDN = makeUserDN(userName);
        try {
            communicator.deleteEntry(userDN);
            logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_M2M_USER_DELETION, IdmConstants.SECURITY_EVENT_STATUS_SUCCESS);
            return true;
        } catch (final DSCommunicatorException e) {
            logger.error("removeM2MUser excp", e);
            if (e.ldapErrorCode() == IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY) {
                final String errMsg = "User " + userName + " does not exist";
                logger.warn(errMsg);
            } else {
                final String errMsg = "removeM2MUser: Problem occurred on removing user " + userName;
                logger.error("{}, {}", errMsg, e.getMessage());
                logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_M2M_USER_DELETION, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
                throw new IdentityManagementServiceException(errMsg, convertLDAPErrorToIdmError(e.ldapErrorCode()));
            }
        }
        return false;
    }

    private int getGroupGidNumber(final String groupName) throws DSCommunicatorException {
        // expect a list that contains only one gidNumber
        final List<String> gidNumberStrings = communicator.querySingleAttribute(makeGroupDN(groupName), SearchControls.SUBTREE_SCOPE,
                IdmConstants.LDAP_GID_NUMBER, null);
        if ((gidNumberStrings == null) || (gidNumberStrings.isEmpty())) {
            final String errMsg = "getGroupGidNumber: gidNumber not found for group " + groupName;
            logger.error(errMsg);
            throw new DSCommunicatorException(errMsg, IdmConstants.LDAP_ERROR_NO_SUCH_ATTRIBUTE);
        }
        return Integer.parseInt(gidNumberStrings.get(0));
    }

    private void validateM2MuserAttributes(final String userName, final String groupName, final String homeDir,
                                           int uidNumber, int gidNumber) {
        if ((groupName == null) || (homeDir == null) || (uidNumber == -1) || (gidNumber == -1)) {
            final StringBuilder errMsg = new StringBuilder();
            errMsg.append("Problem retrieving user ").append(userName).append(" either one or more attributes have no value ::");
            errMsg.append("groupName: ").append(groupName);
            errMsg.append(" homeDirectory: ").append(homeDir);
            errMsg.append(" uidNumber: ").append(uidNumber);
            errMsg.append(gidNumber).append(" gidNumber: ");
            if (logger.isErrorEnabled()) {
                logger.error(errMsg.toString());
            }
            throw new IdentityManagementServiceException(errMsg.toString(), IdentityManagementServiceException.Error.UNEXPECTED_ERROR);
        }
    }

    /**
     * This will return an instance M2MUser if an object exists in LDAP,
     *
     * @param userName userName
     * @return M2M users
     */
    M2MUser getM2MUser(final String userName) {
        Map<String, ArrayList<String>> avPairs;
        try {
            // expect an hash map if user exists otherwise exception thrown including user not found
            avPairs = communicator.queryMultipleAttributes(makeUserDN(userName), SearchControls.OBJECT_SCOPE, attributeList);
        } catch (final DSCommunicatorException e) {
            final String errMsg = "Problem retrieving user " + userName + ": " + e.getMessage();
            logger.error(errMsg, e);
            throw new IdentityManagementServiceException(errMsg, convertLDAPErrorToIdmError(e.ldapErrorCode()));
        }

        logger.debug("USER EXISTS {} Now READING ATTRS and avPairs: {}", userName, avPairs.size());
        String groupName = null;

        int uidNumber = -1;
        String homeDir = null;
        int gidNumber = -1;
        String expiryTimestamp = "";
        for (final String attrName : attributeList) {
            logger.debug("USER {} attrName {}", userName, attrName);
            final ArrayList<String> attrValues = avPairs.get(attrName);
            logger.debug("number value {}", attrValues);
            if (!isValidCollection(attrValues)) {
                continue;
            }
            final String value = attrValues.get(0);
            if (attrName.equalsIgnoreCase(IdmConstants.LDAP_HOME_DIRECTORY)) {
                homeDir = value;
            } else if (attrName.equalsIgnoreCase(IdmConstants.LDAP_ACCOUNT_EXPIRATION_TIME)) {
                expiryTimestamp = value;
            } else if (attrName.equalsIgnoreCase(IdmConstants.LDAP_GID_NUMBER)) {
                gidNumber = Integer.parseInt(value);
                try {
                    groupName = getGroupName(value);
                } catch (final DSCommunicatorException e) {
                    final String errMsg = "Problem retrieving user " + userName + " reason: " + e.getMessage();
                    logger.error(errMsg, e);
                    throw new IdentityManagementServiceException(errMsg, convertLDAPErrorToIdmError(e.ldapErrorCode()));
                }
            } else if (attrName.equalsIgnoreCase(IdmConstants.LDAP_UID_NUMBER)) {
                uidNumber = Integer.parseInt(value);
            }
        }
        validateM2MuserAttributes(userName, groupName, homeDir, uidNumber, gidNumber);

        return new M2MUser(userName, groupName, uidNumber, gidNumber, homeDir, expiryTimestamp);
    }

    private String getGroupName(final String gidNumber) throws DSCommunicatorException {
        List<String> groupDNs;
        final String baseDN = "ou=Groups" + getDnPostfix();
        final String filterString = IdmConstants.LDAP_GID_NUMBER + "=" + gidNumber;

        groupDNs = communicator.querySingleAttribute(baseDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_FULL_NAME, filterString);
        //A.P.: Expect DSCommunicatorException from querySingleAttribute() if there is no entry found from the group container(baseDN)
        //Need to confirm with Steven whether it would return an empty list or throw Exception
        if (groupDNs.isEmpty()) {
            final String errMsg = "No mapping group found for gidNumber " + gidNumber;
            logger.error(errMsg);
            throw new DSCommunicatorException(errMsg, IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY);
        }
        if (groupDNs.size() == 1) {
            return groupDNs.get(0);
        } else {
            final StringBuilder groupNames = new StringBuilder("Number of groups : " + groupDNs.size());
            for (final String gdn : groupDNs) {
                groupNames.append(", " + gdn);
            }
            //this should not be happening, log warning and choose the first one
            final String errMsg = "Returning the first group name as multiple groups matches for gidNumber " + gidNumber + ": "
                    + groupNames.toString();
            logger.warn(errMsg);
            return groupDNs.get(0);
        }

    }

    boolean isExistingM2MUser(final String userName) {
        boolean isUserExist = false;
        try {
            // expect true if user exists, false if not, or throw exception on any other failure cases
            isUserExist = communicator.searchEntry(makeUserDN(userName));
        } catch (final DSCommunicatorException e) {
            final String errMsg = "Problem retrieving user " + userName + " info: " + e.getMessage();
            logger.error(errMsg, e);
            throw new IdentityManagementServiceException(errMsg, convertLDAPErrorToIdmError(e.ldapErrorCode()));
        }
        return isUserExist;
    }

    private String makeProxyUserDN(final String name) {
        final StringBuilder userDN = new StringBuilder(IdmConstants.LDAP_FULL_NAME + "=" + name);
        userDN.append("," + getProxyUserContainerName());
        userDN.append(getDnPostfix());
        logger.debug("Created proxyUserDN: {}", userDN);
        return userDN.toString();
    }

    private String makeUserDN(final String name) {
        final StringBuilder userDN = new StringBuilder(IdmConstants.LDAP_UID + "=" + name);
        userDN.append("," + getM2MUserContainerName());
        userDN.append(getDnPostfix());
        return userDN.toString();
    }

    private String getProxyUserContainerName() {
        String proxyAgentAccountSubtree;
        Boolean switchOnLegacySubtree = identityManagementListener.getProxyAccountRdnSubTree();
        if((switchOnLegacySubtree == null) || (!switchOnLegacySubtree)) {
            proxyAgentAccountSubtree = "ou=proxyagentlockable,ou=com";
        } else {
            proxyAgentAccountSubtree = "ou=proxyagent,ou=com";
        }
        String proxyUserContainerName = ConfigurationBean.get(IdmConstants.COM_INF_LDAP_PROXY_USER_CONTAINER_PROPERTY, proxyAgentAccountSubtree);
        logger.debug("Value on PIB is: {} and the created proxyUserContainerName is: {}", switchOnLegacySubtree, proxyUserContainerName);

        return proxyUserContainerName;
    }

    String getM2MUserContainerName() {
        if (m2mUserContainerName == null) {
            //A.P:: code below will be updated to read from clientConfig entry in LDAP when it is available
            m2mUserContainerName = ConfigurationBean.get(IdmConstants.COM_INF_LDAP_M2M_USER_CONTAINER_PROPERTY, "ou=M2MUsers");
        }
        return m2mUserContainerName;
    }

    private String makeGroupDN(final String name) {
        //A.P:: code below will be updated to read from clientConfig entry in LDAP when it is available
        final StringBuilder groupDN = new StringBuilder(IdmConstants.LDAP_FULL_NAME + "=" + name);
        groupDN.append(",ou=Groups");
        groupDN.append(getDnPostfix());

        return groupDN.toString();
    }

    String getDnPostfix() {
        if (dnPostfix == null) {
            final String baseDN = ConfigurationBean.get(IdmConstants.COM_INF_LDAP_ROOT_SUFFIX_PROPERTY, "dc=apache,dc=com");
            final StringBuilder bufDnPostfix = new StringBuilder(",");
            bufDnPostfix.append(baseDN);
            dnPostfix = bufDnPostfix.toString();
        }
        return dnPostfix;
    }

    synchronized int getUidNumber() {
        final List<String> assignedUidNumbers = new ArrayList<>();
        String dnFull;

        // List of 'ou' to search for uidNumbers
        final List<String> organizationalUnitList = new ArrayList<>();
        organizationalUnitList.add("M2MUsers");
        organizationalUnitList.add("Profiles");

        // Read assigned assignedUidNumbers from LDAP
        try {
            for (final String organizationalUnit : organizationalUnitList) {
                dnFull = "ou=" + organizationalUnit + getDnPostfix();
                assignedUidNumbers.addAll(communicator.querySingleAttribute(dnFull, SearchControls.SUBTREE_SCOPE,
                        IdmConstants.LDAP_UID_NUMBER, null));
            }
        } catch (final DSCommunicatorException e) {
            final String errMsg = "Problem retrieving all assignedUidNumbers from LDAP due to error " + e.getMessage();
            logger.error(errMsg, e);
            throw new IdentityManagementServiceException(errMsg, convertLDAPErrorToIdmError(e.ldapErrorCode()));
        }

        final Set<Integer> uidNumberList = new TreeSet<>();
        final List<Integer> uidNumberListOufOfBounds = new ArrayList<>();
        int intNumber;

        for (final String strNumber : assignedUidNumbers) {
            logger.debug("Converting string number to integer: {}", strNumber);
            intNumber = Integer.parseInt(strNumber);
            if (intNumber >= IdmConstants.MIN_UID_NUMBER && intNumber <= IdmConstants.MAX_UID_NUMBER) {
                uidNumberList.add(intNumber);
            } else {
                uidNumberListOufOfBounds.add(intNumber);
            }
        }

        if (!uidNumberListOufOfBounds.isEmpty() && logger.isWarnEnabled()) {
            logger.warn("There are existing attributes out of bounds. Found uid numbers: {}", uidNumberListOufOfBounds);
        }

        if (uidNumberList.isEmpty()) {
            logger.debug("No uidNumber found in dataStore in bounds, returning {}", IdmConstants.MIN_UID_NUMBER);
            return IdmConstants.MIN_UID_NUMBER;
        }

        if (uidNumberList.size() >= (IdmConstants.MAX_UID_NUMBER - IdmConstants.MIN_UID_NUMBER)) {
            final String errMsg = "None of uid numbers in bounds are available.";
            logger.error(errMsg);
            throw new IdentityManagementServiceException(errMsg,
                    IdentityManagementServiceException.Error.ATTRIBUTE_NOT_AVAILABLE);
        }

        int currentUidNumber = IdmConstants.MIN_UID_NUMBER;
        for (final int m2mUid : uidNumberList) {
            if (currentUidNumber != m2mUid) {
                break;
            }
            currentUidNumber++;
        }

        logger.debug("Returned uidNumber: {}", currentUidNumber);
        return currentUidNumber;
    }

    public String getM2MPassword(final String userName) {
        String decryptedPassword = null;
        logger.info("getM2MPassword() : userName={}", userName);
        final String m2mUserDN = makeUserDN(userName);
        final String securityLogMsg = "User Information: userName=" + userName;
        String actualEncryptedPwd;
        try {
            if (!isExistingM2MUser(userName)) {
                final String errMsg = "User " + userName + " does not exists.";
                logger.error("addM2MUser: {}", errMsg);
                logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_M2M_USER_CRED_READ, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
                throw new IdentityManagementServiceException(errMsg, IdentityManagementServiceException.Error.ENTRY_NOT_FOUND);
            }
            final List<String> authPasswordStrings = communicator.querySingleAttribute(m2mUserDN, SearchControls.SUBTREE_SCOPE,
                    IdmConstants.LDAP_AUTH_CRED, null);
            if (!isValidCollection(authPasswordStrings)) {
                final String errMsg = "Failed to retrive password for User " + userName;
                logger.error("getM2MPassword: {}", errMsg);
                logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_M2M_USER_CRED_READ, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
                throw new IdentityManagementServiceException(errMsg, IdentityManagementServiceException.Error.UNEXPECTED_ERROR);
            }
            // password string prefix SHA1$xxxx$gggg
            if (authPasswordStrings.get(0).length() > 5 && authPasswordStrings.get(0).startsWith(IdmConstants.LDAP_AUTH_CRED_SCHEME)) {
                logger.info("decrypting the M2M User password using the cryptography service in getM2MPassword()");
                List<String> targetList = Arrays.asList(authPasswordStrings.get(0).split(IdmConstants.LDAP_AUTH_CRED_SCHEME));
                // here password must be at minimum SHA1xx
                if (targetList.size() != 2 || !targetList.get(1).startsWith(IdmConstants.CIPHER_PREFIX_AES_CBC128)) {
                    final String errMsg = "encrypted password prefix format error " +
                            buildGetM2MPasswordLogMsg(targetList.size(), targetList, 1, IdmConstants.CIPHER_PREFIX_AES_CBC128.length());
                    logger.error("getM2MPassword: {}", errMsg);
                    throw new IdentityManagementServiceException(errMsg, IdentityManagementServiceException.Error.UNEXPECTED_ERROR);
                }
                //remove CIPHER_PREFIX_AES_CBC128 = $AESCBC128$, for getting the actual encrypted password, so start from 11.
                actualEncryptedPwd = targetList.get(1).substring(IdmConstants.CIPHER_PREFIX_AES_CBC128.length());
                if (null == cryptoClient) {
                    cryptoClient = new CryptographyServiceClient(cryptographyService);
                }
                decryptedPassword = cryptoClient.getDecryptedPassword(actualEncryptedPwd);
                if (null == decryptedPassword) {
                    final String errMsg = "problem in decrypting the password using cryptography service";
                    throw new IdentityManagementServiceException(errMsg, IdentityManagementServiceException.Error.UNEXPECTED_ERROR);
                }
                logger.debug("decrypted(decryption worked) the M2M User password using the cryptography service in getM2MPassword()");
            } else {
                final String warnMsg = "encrypted password ldap auth cred scheme format error " +
                        buildGetM2MPasswordLogMsg(authPasswordStrings.size(), authPasswordStrings, 0, 6);
                logger.warn("getM2MPassword: {}", warnMsg);
            }
        } catch (final DSCommunicatorException e) {
            final String errMsg = "Problem retrieving user password, error " + e.getMessage();
            this.logger.error(errMsg, e);
            logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_M2M_USER_CRED_READ, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
            throw new IdentityManagementServiceException(errMsg, convertLDAPErrorToIdmError(e.ldapErrorCode()));
        }
        logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_M2M_USER_CRED_READ, IdmConstants.SECURITY_EVENT_STATUS_SUCCESS);
        return decryptedPassword;
    }

    /**
     * buildGetM2MPasswordLogMsg : builds error msg
     *
     * @param lstSize        : size of the list
     * @param targetList     : list to analyse
     * @param listIdxTocheck : string instance of the list to check
     * @param idxToCompare   : max index of the string to log
     * @return
     */
    private String buildGetM2MPasswordLogMsg(int lstSize, List<String> targetList, int listIdxTocheck, int idxToCompare) {
        String initialChunck = "";
        // list size must be greater than of the index of the instance to check
        if (lstSize > listIdxTocheck) {
            int lastIndex = Math.min(idxToCompare, targetList.get(listIdxTocheck).length());
            initialChunck = targetList.get(listIdxTocheck).substring(0, lastIndex);
        }
        return "number of chunks are = " + lstSize + " and prefix value = " + initialChunck;
    }

    public String updateM2MPassword(final String userName) {
        String userPassword;
        final String m2mUserDN = makeUserDN(userName);
        final String securityLogMsg = "User Information: userName=" + userName;
        try {
            // check user with the given username first
            if (isExistingM2MUser(userName)) {
                userPassword = communicator.getRandomPassword();
                if (userPassword != null) {
                    if (cryptoClient == null) {
                        cryptoClient = new CryptographyServiceClient(cryptographyService);
                    }
                    final String encryptedPassword = cryptoClient.getEncryptedPassword(userPassword);
                    if (encryptedPassword == null) {
                        final String errMsg = "problem in encrypting the password using cryptography service";
                        throw new IdentityManagementServiceException(errMsg, IdentityManagementServiceException.Error.UNEXPECTED_ERROR);
                    }

                    communicator.modifyEntryReplace(m2mUserDN, IdmConstants.LDAP_USER_CRED, userPassword);
                    communicator.modifyEntryReplace(m2mUserDN, IdmConstants.LDAP_AUTH_CRED, encryptedPassword);
                    // update pwdReset flag to false to prevent forcing change password after the password is replaced above
                    // (directory communicator uses LDAP administrator credentials to bind, which may trigger password reset depending on the applicable policy)
                    communicator.modifyEntryReplace(m2mUserDN, IdmConstants.LDAP_CRED_RESET, String.valueOf(false));
                } else {
                    final String errMsg = "Failed to generate password for User " + userName + ".";
                    logger.error("addM2MUser: {}", errMsg);
                    logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_M2M_USER_CRED_UPDATE, "FAILURE");
                    throw new IdentityManagementServiceException(errMsg, IdentityManagementServiceException.Error.UNEXPECTED_ERROR);
                }
            } else {
                final String errMsg = "User " + userName + " does not exists.";
                logger.error("addM2MUser: {}", errMsg);
                logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_M2M_USER_CRED_UPDATE, "FAILURE");
                throw new IdentityManagementServiceException(errMsg, IdentityManagementServiceException.Error.ENTRY_NOT_FOUND);
            }
        } catch (final DSCommunicatorException e) {
            logger.error("updateM2MPassword excp", e);
            final String errMsg = "Error while updating password for M2M user " + userName + " exception:" + e.getMessage();
            logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_M2M_USER_CRED_UPDATE, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
            throw new IdentityManagementServiceException(errMsg, convertLDAPErrorToIdmError(e.ldapErrorCode()));
        } catch (final IdentityManagementServiceException e) {
            logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_M2M_USER_CRED_UPDATE, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
            throw e;
        } catch (final Exception e) {
            final String errMsg = "Error while updating password for M2M user " + userName + " exception:" + e.getMessage();
            this.logger.error(errMsg);
            logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_M2M_USER_CRED_UPDATE, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
            throw new IdentityManagementServiceException(errMsg, IdentityManagementServiceException.Error.UNEXPECTED_ERROR);
        }
        logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_M2M_USER_CRED_UPDATE, IdmConstants.SECURITY_EVENT_STATUS_SUCCESS);
        return userPassword;
    }

    public List<String> getAllTargetGroups() {
        final String tgDN = "ou=TargetGroups" + "," + ConfigurationBean.get(IdmConstants.COM_INF_LDAP_ROOT_SUFFIX_PROPERTY);
        try {
            final List<String> retList = communicator.querySingleAttribute(tgDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_ORGANISATIONALUNIT,
                    null);
            retList.remove(IdmConstants.LDAP_TARGET_GROUPS_NAME);
            return retList;
        } catch (final DSCommunicatorException e) {
            final String errMsg = "Problem retrieving Target Groups, error " + e.getMessage();
            this.logger.error(errMsg, e);
            throw new IdentityManagementServiceException(errMsg, convertLDAPErrorToIdmError(e.ldapErrorCode()));
        }
    }

    private String generateRandomUniqueCn() {
        logger.debug("generateRandomUniqueCn Starting generation of new uniqueCn");
        UUID uuid = UUID.randomUUID();
        int variant = uuid.variant();
        int version = uuid.version();
        logger.debug("generateRandomUniqueCn : variant=[{}], version=[{}]", variant, version);

        String proxyUserCounter = uuid.toString();

        return "ProxyAccount_" + proxyUserCounter;
    }

    public String getDefaultTargetGroup() {
        //A.P.: Value hard-coded right now. To be configured
        return IdmConstants.LDAP_DEFAULTTARGETGROUP;
    }

    public List<String> validateTargetGroups(final List<String> targetGroups) {
        if (!isValidCollection(targetGroups)) {
            final String errMsg = ("Problem validating Target Groups as input parameter is empty or NULL");
            throw new IdentityManagementServiceException(errMsg, IdentityManagementServiceException.Error.UNEXPECTED_ERROR);
        }
        try {
            final List<String> validList = getAllTargetGroups();
            // if there is no return, then all the entries in targetGroups are invalid
            if (validList.isEmpty()) {
                return targetGroups;
            }
            final List<String> invalidList = new ArrayList<>();
            // Compare two lists, add a entry into invalidList if it is not on the list of validList
            for (final String targetGroup : targetGroups) {
                if (!validList.contains(targetGroup)) {
                    invalidList.add(targetGroup);
                }
            }
            return invalidList;
        } catch (final IdentityManagementServiceException e) {
            final String errMsg = "Problem validating the Target Groups, error " + e.getMessage();
            this.logger.error(errMsg, e);
            throw e;
        }
    }

    private void addShadowAccountToCommunicator(final String userDN, final String groupName) throws DSCommunicatorException {
        if (IdmConstants.COM_GROUP_NAME.equals(groupName)) {
            try {
                communicator.modifyEntryAdd(userDN, IdmConstants.LDAP_OBJECTCLASS, IdmConstants.LDAP_SHADOWACCOUNT);
            } catch (final DSCommunicatorException e) {
                if (e.ldapErrorCode() == IdmConstants.LDAP_ERROR_ATTRIBUTE_OR_VALUE_EXIST) {
                    logger.info("user: {} already has shadowAccount objectClass", userDN);
                } else {
                    throw e;
                }
            }
        }
    }

    private void validateNewPosixAttributes(final String userName, final String userDN, final String homeDirectory,
                                            final String loginShell, final String securityLogMsg) throws DSCommunicatorException {
        final StringBuilder errMsg = new StringBuilder("Exception caught in addPosixAttributes for user ").append(userName)
                .append(".Detail message is :");
        // Check that new posix attributes are the same as current assign - exception thrown when different.
        final Map<String, ArrayList<String>> avPairs = communicator.queryMultipleAttributes(userDN,
                SearchControls.OBJECT_SCOPE, attributeList);
        if ((avPairs.containsKey(IdmConstants.LDAP_HOME_DIRECTORY)
                && (!avPairs.get(IdmConstants.LDAP_HOME_DIRECTORY).get(0).equals(homeDirectory)))
                || (avPairs.containsKey(IdmConstants.LDAP_LOGIN_SHELL)
                && (!avPairs.get(IdmConstants.LDAP_LOGIN_SHELL).get(0).equals(loginShell)))) {

            errMsg.append(". Assigned attributes are different from the new role attributes.");
            if (logger.isErrorEnabled()) {
                logger.error(errMsg.toString());
            }
            logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_POSIX_ATTRIBUTES_ADD,
                    IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
            throw new IdentityManagementServiceException(errMsg.toString(),
                    IdentityManagementServiceException.Error.ATTR_OR_VALUE_ALREADY_EXISTS);
        }
    }

    private String updateGroupMember(final String userName, final String userDN, final String groupName,
                                     final StringBuilder errorMsg) {
        String warningMsg = null;
        try {
            logger.info("Updating group unique member/memberUid");
            final String value = IdmConstants.COM_GROUP_NAME.equals(groupName) ? userName : userDN;
            final String attribute = getProperAttribute(groupName);
            logger.debug("value: {} of attribute: {}", value, attribute);
            communicator.modifyEntryAdd(makeGroupDN(groupName), attribute, value);
        } catch (final DSCommunicatorException e) {
            logger.error("addPosixAttributes excp", e);
            if (e.ldapErrorCode() == IdmConstants.LDAP_ERROR_ATTRIBUTE_OR_VALUE_EXIST) {
                logger.info("Adding uniquemember/memberUid entry of user {} on group {} is not performed as user entry already exist. ", userName,
                        groupName);
            } else {
                errorMsg.append(e.getMessage());
                logger.error(errorMsg.toString());
                warningMsg = "WARN: updating uniquemember/memberUid on the group entry failed due to error " + e.ldapErrorCode();
            }
        }
        return warningMsg;
    }

    /**
     * The method does two operations. 1. modify a given user entry by adding Posix attributes such as uidnumber,
     * gidnumber, homedirectory and loginshell for given user entry (and shadowAccount objectClass if group is
     * com_users). 2. modify the given group entry by adding the user dn to its uniquemember (or memberUid when it is
     * com_users group)
     *
     * @param container     which is a container in the datastore where the user belongs to
     * @param userName      - user login
     * @param groupName     - group name which user will be assign
     * @param homeDirectory - path to user home directory
     * @param loginShell    - shell assign to user (ex. /bin/bash)
     * @return null when both updates were successful, otherwise returns warning msg as no update made on uniquemember
     * (or memberUid when it is com_users group)
     */
    public String addPosixAttributes(final String container, final String userName, final String groupName, final String homeDirectory,
                                     final String loginShell) {

        logger.info("Run AddPosixAttributes...");
        final StringBuilder securityLogMsg = new StringBuilder("User Information: userName=").append(userName).append(" homeDir=")
                .append(homeDirectory).append(" groupName=").append(groupName).append(" loginShell=").append(loginShell);
        final StringBuilder errMsg = new StringBuilder("Exception caught in addPosixAttributes for user ").append(userName)
                .append(".Detail message is :");
        final List<String> gidNumbers;

        int gidNumber;
        int uidNumber;
        final StringBuilder userDN = new StringBuilder(IdmConstants.LDAP_UID).append("=").append(userName).append(",ou=").append(container)
                .append(getDnPostfix());

        //Retrieve uidNumber - when exist->read, when no exist->generate new value
        //Retrieve list of gidNumbers assigned to user - if user has any posix attributes i return gidNumber group
        //which was assign to him
        //Read gidNumber for the groupName
        try {
            uidNumber = getUidNumber(userName, container);
            logger.debug("Retrieved uidNumber: {}", uidNumber);
            gidNumbers = communicator.querySingleAttribute(userDN.toString(), SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_GID_NUMBER, null);
            logger.debug("Retrieved gidNumbers: {}", gidNumbers);
            logger.debug("Read gidNumber for groupName");
            gidNumber = getGroupGidNumber(groupName);
            logger.debug("gidNumber for groupName {} is {}", groupName, gidNumber);
        } catch (final DSCommunicatorException e) {
            logger.error("addPosixAttributes", e);
            errMsg.append(e.getMessage());
            logger.error(errMsg.toString());
            logSecurityEvent(securityLogMsg.toString(), IdmConstants.SECURITY_EVENT_POSIX_ATTRIBUTES_ADD, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
            throw new IdentityManagementServiceException(errMsg.toString(), convertLDAPErrorToIdmError(e.ldapErrorCode()));
        }
        try {
            if (gidNumbers.isEmpty()) {
                //no posixAttributes assigned to the user
                final HashMap<String, ArrayList<String>> attributesToModify = new HashMap<>();
                errMsg.append("User was already assigned to group");
                attributesToModify.put(IdmConstants.LDAP_OBJECTCLASS, getArrayList(IdmConstants.LDAP_POSIXACCOUNT));
                attributesToModify.put(IdmConstants.LDAP_UID_NUMBER, getArrayList(Integer.toString(uidNumber)));
                attributesToModify.put(IdmConstants.LDAP_GID_NUMBER, getArrayList(Integer.toString(gidNumber)));
                attributesToModify.put(IdmConstants.LDAP_HOME_DIRECTORY, getArrayList(homeDirectory));
                attributesToModify.put(IdmConstants.LDAP_LOGIN_SHELL, getArrayList(loginShell));
                communicator.modifyEntryAddMultipleAttributes(userDN.toString(), attributesToModify);
                logger.info("addPosixAttributes to the user entry done, userName {}, groupName {}, homeDirectory {} and logineShel {}", userName,
                        groupName, homeDirectory, loginShell);
            } else {

                logger.info("gid number was assigned. Compare with new one");
                final int oldGidNumber = Integer.parseInt(gidNumbers.get(0));
                final int nullGidNumber = Integer.parseInt(IdmConstants.LDAP_NULL_POSIX_GROUP_NUMBER);
                if (oldGidNumber == nullGidNumber) {
                    final HashMap<String, ArrayList<String>> attributesToReplace = new HashMap<>();
                    attributesToReplace.put(IdmConstants.LDAP_HOME_DIRECTORY, getArrayList(homeDirectory));
                    attributesToReplace.put(IdmConstants.LDAP_GID_NUMBER, getArrayList(Integer.toString(gidNumber)));
                    attributesToReplace.put(IdmConstants.LDAP_LOGIN_SHELL, getArrayList(loginShell));
                    communicator.modifyEntryReplaceMultipleAttributes(userDN.toString(), attributesToReplace);

                    logger.info("addPosixAttributes to the user entry done, userName {}, groupName {}, homeDirectory {} and logineShel {}", userName,
                            groupName, homeDirectory, loginShell);
                } else {
                    if (oldGidNumber == gidNumber) {
                        errMsg.append(" User was already asigned to group");
                        throw new IdentityManagementServiceException(errMsg.toString(),
                                IdentityManagementServiceException.Error.ATTR_OR_VALUE_ALREADY_EXISTS);
                    }
                    final boolean isUserOnlyInOneGroup = (1 == getUserGroups(userName, userDN.toString()).size());
                    if (IdmConstants.COM_GROUP_ID.equals(Integer.toString(oldGidNumber)) && isUserOnlyInOneGroup) {
                        modifyHomeDirectoryAndLoginShell(userDN.toString(), homeDirectory, loginShell);
                        logger.info("addPosixAttributes to the user entry done, userName {}, groupName {}, homeDirectory {} and logineShell {}",
                                userName, groupName, homeDirectory, loginShell);
                    } else if (!IdmConstants.COM_GROUP_ID.equals(Integer.toString(gidNumber))) {
                        validateNewPosixAttributes(userName, userDN.toString(), homeDirectory, loginShell, securityLogMsg.toString());
                    }
                }
            }

            //add ShadowAccount objectClass if group is com_users and user does not contain this objecClass already
            addShadowAccountToCommunicator(userDN.toString(), groupName);
        } catch (final DSCommunicatorException e) {
            logger.error("addPosixAttributes excp", e);
            errMsg.append(e.getMessage());
            logger.error(errMsg.toString());
            logSecurityEvent(securityLogMsg.toString(), IdmConstants.SECURITY_EVENT_POSIX_ATTRIBUTES_ADD, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
            throw new IdentityManagementServiceException(errMsg.toString(), convertLDAPErrorToIdmError(e.ldapErrorCode()));
        }

        final String warningMsg = updateGroupMember(userName, userDN.toString(), groupName, errMsg);
        logSecurityEvent(securityLogMsg.toString(), IdmConstants.SECURITY_EVENT_POSIX_ATTRIBUTES_ADD, IdmConstants.SECURITY_EVENT_STATUS_SUCCESS);
        return warningMsg;
    }

    private void modifyHomeDirectoryAndLoginShell(final String userDN, final String homeDirectory, final String loginShell)
            throws DSCommunicatorException {
        final HashMap<String, ArrayList<String>> attributesToReplace = new HashMap<>();
        attributesToReplace.put(IdmConstants.LDAP_HOME_DIRECTORY, getArrayList(homeDirectory));
        attributesToReplace.put(IdmConstants.LDAP_LOGIN_SHELL, getArrayList(loginShell));
        communicator.modifyEntryReplaceMultipleAttributes(userDN, attributesToReplace);

    }

    /**
     * The method does two operations. 1. modify a given user entry by adding Posix attributes such as uidnumber,
     * gidnumber, homedirectory and loginshell for given user entry. 2. modify the given group entry by adding the user
     * dn to its uniquemember (or memberUid when it is com_users group).
     *
     * @param container     - which is a container in the datastore where the user belongs to
     * @param userName      - user login
     * @param groupName     - group name which user will be assign
     * @param homeDirectory - path to user home directory
     * @return null when both updates were successful, otherwise returns warning msg as no update made on uniquemember
     * (or memberUid when it is com_users group)
     */
    public String addPosixAttributes(final String container, final String userName, final String groupName,
                                     final String homeDirectory) {
        return addPosixAttributes(container, userName, groupName, homeDirectory, IdmConstants.LDAP_NO_LOGIN_SHELL);
    }

    /**
     * This method returns user id if exist. If this user has no assign any id it will be generate for him
     *
     * @param userName  - user login
     * @param container - which is a container in the datastore where the user belongs to
     * @return user id greater than 5000
     * @throws DSCommunicatorException
     */
    private int getUidNumber(final String userName, final String container)
            throws DSCommunicatorException {
        logger.debug("Read uidNumeber for user {}", userName);
        final StringBuilder userDN = new StringBuilder(IdmConstants.LDAP_UID).append("=")
                .append(userName).append(",ou=").append(container).append(getDnPostfix());
        final List<String> usersList;
        int userUid;

        try {
            usersList = communicator.querySingleAttribute(userDN.toString(),
                    SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_UID_NUMBER, null);
            if (!usersList.isEmpty()) {
                logger.info("read uidNumber for user {} is {}", userName, usersList.get(0));
                userUid = Integer.parseInt(usersList.get(0));
            } else {
                logger.info("There is no uidNumber assigned for user {}. Generate new value", userName);
                userUid = getUidNumber();
            }
        } catch (final DSCommunicatorException e) {
            final StringBuilder errMsg = new StringBuilder("Problem retrieving all uidNumbers from LDAP due to error ")
                    .append(e.getMessage());
            logger.error(errMsg.toString(), e);
            throw new IdentityManagementServiceException(errMsg.toString(), convertLDAPErrorToIdmError(e.ldapErrorCode()));
        }
        logger.debug("Returning userUid: {}", userUid);
        return userUid;
    }

    private void removeUsersPosixAttributes(final List<String> userAssignToGroups, final String userDN, final String userName,
                                            final String groupName, final String gidNumberString) throws DSCommunicatorException {
        logger.debug("Removing users posix attributes ...");
        if (userAssignToGroups.size() == 1) {
            //first case - user has been assign only one posix role
            final HashMap<String, ArrayList<String>> attributesToReplace = new HashMap<>();

            attributesToReplace.put(IdmConstants.LDAP_LOGIN_SHELL, getArrayList(IdmConstants.LDAP_NO_LOGIN_SHELL));
            attributesToReplace.put(IdmConstants.LDAP_GID_NUMBER, getArrayList(IdmConstants.LDAP_NULL_POSIX_GROUP_NUMBER));
            attributesToReplace.put(IdmConstants.LDAP_HOME_DIRECTORY, getArrayList(IdmConstants.LDAP_NULL_HOME_DIRECTORY));
            communicator.modifyEntryReplaceMultipleAttributes(userDN, attributesToReplace);

            if (IdmConstants.COM_GROUP_NAME.equals(groupName)) {
                communicator.modifyEntryDelete(userDN, IdmConstants.LDAP_OBJECTCLASS, IdmConstants.LDAP_SHADOWACCOUNT);
            }

            logger.info("Users - {} - posix attributes has been removed", userName);
        } else if (userAssignToGroups.size() > 1) {

            if (userAssignToGroups.size() == 2 && !IdmConstants.COM_GROUP_NAME.equals(groupName)
                    && userAssignToGroups.contains(IdmConstants.COM_GROUP_NAME)) {
                final HashMap<String, ArrayList<String>> attributesToReplace = new HashMap<>();
                attributesToReplace.put(IdmConstants.LDAP_LOGIN_SHELL, getArrayList(IdmConstants.LDAP_NO_LOGIN_SHELL));
                attributesToReplace.put(IdmConstants.LDAP_GID_NUMBER, getArrayList(IdmConstants.COM_GROUP_ID));
                attributesToReplace.put(IdmConstants.LDAP_HOME_DIRECTORY, getArrayList(IdmConstants.LDAP_NULL_HOME_DIRECTORY));
                communicator.modifyEntryReplaceMultipleAttributes(userDN, attributesToReplace);
                logger.debug("user {} is only in com_roles group, changing login shell to {} and home directory to {}", userName,
                        IdmConstants.LDAP_NO_LOGIN_SHELL, IdmConstants.LDAP_NULL_HOME_DIRECTORY);
            } else {

                //second case - user has been assign many posix roles.
                //We need to check that assigned gid is the same as gid group we want to remove.
                //If they are the same then we need to remove the old one, and assign new from the groups which user belong.
                logger.debug("User {} was assign to many posix roles.", userName);
                final int gidAssignToUser = Integer.parseInt(gidNumberString);
                final int gidToRemove = getGroupGidNumber(groupName);
                if (gidAssignToUser == gidToRemove) {
                    userAssignToGroups.remove(groupName);
                    final int newGid = getGroupGidNumber(userAssignToGroups.get(0));
                    logger.debug("gid assigned to user is the same as gid group to remove. New gid number assign to user will be {}", newGid);
                    communicator.modifyEntryReplace(userDN, IdmConstants.LDAP_GID_NUMBER, String.valueOf(newGid));
                }
            }

            if (IdmConstants.COM_GROUP_NAME.equals(groupName)) {
                communicator.modifyEntryDelete(userDN, IdmConstants.LDAP_OBJECTCLASS, IdmConstants.LDAP_SHADOWACCOUNT);
            }

            logger.info("User - {} - posix attributes has been updated", userName);
        }
    }

    /**
     * The method does two operations. 1. modify a given user entry by removing Posix attributes such as uidnumber,
     * gidnumber, and homedirectory for given user entry (and shadowAccount objectClass if group is com_users). 2.
     * modify the given group entry by removing the user dn from its uniquemember (or memberUid when it is com_users
     * group)
     *
     * @param container which is a container in the datastore where the user belongs to
     * @param userName  - user login
     * @return null when both updates were successful, otherwise returns warning msg as no update made on uniquemember
     * (or memberUid when it is com_users group)
     */
    public String removePosixAttributes(final String container, final String userName, final String groupName) {

        logger.info("Start removeing PosixAttributes for user {}", userName);
        final StringBuilder securityLogMsg = new StringBuilder("User Information: userName=").append(userName);
        //list to keep all posix groups assign to user
        List<String> userAssignToGroups = null;
        final Map<String, ArrayList<String>> avPairs;
        final StringBuilder errMsg = new StringBuilder("Exception caught in removePosixAttributes for user ").append(userName)
                .append(". Detail message is :");
        final StringBuilder userDNsb = new StringBuilder(IdmConstants.LDAP_UID).append("=").append(userName).append(",ou=").append(container)
                .append(getDnPostfix());
        final String userDN = userDNsb.toString();

        //Create list of roles assign to user
        try {
            userAssignToGroups = getUserGroups(userName, userDN);
            logger.debug("Read all posix attributes assigend to user {}", userName);
            avPairs = communicator.queryMultipleAttributes(userDN, SearchControls.OBJECT_SCOPE, attributeList);
        } catch (DSCommunicatorException e) {
            logger.error("removePosixAttributes excp", e);
            errMsg.append(e.getMessage());
            logger.error(errMsg.toString());
            logSecurityEvent(securityLogMsg.toString(), IdmConstants.SECURITY_EVENT_POSIX_ATTRIBUTES_REMOVE,
                    IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
            throw new IdentityManagementServiceException(errMsg.toString(), convertLDAPErrorToIdmError(e.ldapErrorCode()));
        }

        if (!userAssignToGroups.contains(groupName)) {
            errMsg.append(" User was not a member of group.");
            if (logger.isErrorEnabled()) {
                logger.error(errMsg.toString());
            }
            logSecurityEvent(securityLogMsg.toString(), IdmConstants.SECURITY_EVENT_POSIX_ATTRIBUTES_REMOVE,
                    IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
            throw new IdentityManagementServiceException(errMsg.toString(), IdentityManagementServiceException.Error.NO_SUCH_ATTRIBUTE);
        }

        // checking homeDirectory if it is null, it means the posix account already has been removed.
        if (!isValidCollection(avPairs.get(IdmConstants.LDAP_HOME_DIRECTORY))) {
            errMsg.append(" posix attributes not found");
            if (logger.isErrorEnabled()) {
                logger.error(errMsg.toString());
            }
            logSecurityEvent(securityLogMsg.toString(), IdmConstants.SECURITY_EVENT_POSIX_ATTRIBUTES_REMOVE,
                    IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
            throw new IdentityManagementServiceException(errMsg.toString(), IdentityManagementServiceException.Error.NO_SUCH_ATTRIBUTE);
        }

        final String gidNumberString = avPairs.get(IdmConstants.LDAP_GID_NUMBER).get(0);

        try {
            removeUsersPosixAttributes(userAssignToGroups, userDN, userName, groupName, gidNumberString);
        } catch (final DSCommunicatorException e) {
            logger.error("removePosixAttributes excp", e);
            errMsg.append("Exception caught in removePosixAttributes for user ").append(userName).append(". Detail message is :");
            errMsg.append(e.getMessage());
            logger.error(errMsg.toString());
            logSecurityEvent(securityLogMsg.toString(), IdmConstants.SECURITY_EVENT_POSIX_ATTRIBUTES_REMOVE,
                    IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
            throw new IdentityManagementServiceException(errMsg.toString(), convertLDAPErrorToIdmError(e.ldapErrorCode()));
        }
        try {
            logger.debug("Removing user entry from the group...");
            final String value = IdmConstants.COM_GROUP_NAME.equals(groupName) ? userName : userDN;
            final String attribute = getProperAttribute(groupName);
            logger.debug("value: {} of attribute: {}", value, attribute);
            communicator.modifyEntryDelete(makeGroupDN(groupName), attribute, value);
            logger.info("User entry {} has been removed from group {}", userName, groupName);
        } catch (final DSCommunicatorException e) {
            logger.error("removePosixAttributes excp", e);
            switch (e.ldapErrorCode()) {
                case IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY:
                    logger.info("Removing uniquemember/memberUid entry of user {} on group {} is not performed as group entry does not exist.",
                            userName, groupName);
                    break;
                case IdmConstants.LDAP_ERROR_NO_SUCH_ATTRIBUTE:
                    logger.warn(
                            "Removing uniquemember/memberUid entry of user {}" + " is not performed as user entry does not exist on group {} entry. ",
                            userName, groupName);
                    break;
                default:
                    errMsg.append(e.getMessage());
                    logger.error(errMsg.toString());
                    logSecurityEvent(securityLogMsg.toString(), IdmConstants.SECURITY_EVENT_POSIX_ATTRIBUTES_REMOVE,
                            IdmConstants.SECURITY_EVENT_STATUS_SUCCESS);
                    return "WARN: updating uniquemember/memberUid on the group entry failed due to error " + e.ldapErrorCode();
            }
        }

        logSecurityEvent(securityLogMsg.toString(), IdmConstants.SECURITY_EVENT_POSIX_ATTRIBUTES_REMOVE, IdmConstants.SECURITY_EVENT_STATUS_SUCCESS);
        return null;
    }

    private List<String> getUserGroups(final String userName, final String userDN) throws DSCommunicatorException {
        logger.info("Read all posix groups assign to user {}", userName);
        final List<String> userAssignToGroups = new ArrayList<>();
        for (final String posixRole : POSIX_ROLES) {
            final List<String> membersOfGrop = communicator.querySingleAttribute(makeGroupDN(posixRole), SearchControls.OBJECT_SCOPE,
                    getProperAttribute(posixRole), null);
            final String user = IdmConstants.COM_GROUP_NAME.equals(posixRole) ? userName : userDN;
            if (membersOfGrop.contains(user)) {
                userAssignToGroups.add(posixRole);
            }
        }
        logger.debug("User was assign to {} groups", userAssignToGroups.size());
        return userAssignToGroups;
    }

    /*
     * In most cases members should be stored in uniqueMember attribute. However, in case of com_group (gidNumber=609)
     * members should be stored in memberUid attribute
     */
    private String getProperAttribute(final String groupName) {
        if (IdmConstants.COM_GROUP_NAME.equals(groupName)) {
            return IdmConstants.LDAP_MEMBER_UID;
        } else {
            return IdmConstants.LDAP_UNIQUE_MEMBER;
        }
    }

    public List<String> getComUsers() {
        try {
            final String domainName = IdmConstants.COM_GROUP_PREFIX + getDnPostfix();
            final List<String> members = communicator.querySingleAttribute(domainName, SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_MEMBER_UID, null);
            if (logger.isDebugEnabled()) {
                logger.debug("members: {}", Arrays.toString(members.toArray()));
            }
            logSecurityEvent("Get COM Users", IdmConstants.SECURITY_EVENT_COM_GROUP_MEMBERS_READ, IdmConstants.SECURITY_EVENT_STATUS_SUCCESS);
            return members;
        } catch (DSCommunicatorException e) {
            final String errMsg = "Problem retrieving COM users, error " + e.getMessage();
            this.logger.error(errMsg, e);
            logSecurityEvent("Get COM Users", IdmConstants.SECURITY_EVENT_COM_GROUP_MEMBERS_READ, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
            throw new IdentityManagementServiceException(errMsg, convertLDAPErrorToIdmError(e.ldapErrorCode()));
        }
    }

    IdentityManagementServiceException.Error convertLDAPErrorToIdmError(final int errCode) {
        switch (errCode) {
            case IdmConstants.LDAP_ERROR_ATTRIBUTE_OR_VALUE_EXIST:
                return IdentityManagementServiceException.Error.ATTR_OR_VALUE_ALREADY_EXISTS;
            case IdmConstants.LDAP_ERROR_CONNECTION_FAILURE:
                return IdentityManagementServiceException.Error.DATA_STORE_CONNECTION_FAILURE;
            case IdmConstants.LDAP_ERROR_INVALID_CREDENTIAL:
                return IdentityManagementServiceException.Error.INVALID_CREDENTIALS;
            case IdmConstants.LDAP_ERROR_NO_SUCH_ATTRIBUTE:
                return IdentityManagementServiceException.Error.NO_SUCH_ATTRIBUTE;
            case IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY:
                return IdentityManagementServiceException.Error.ENTRY_NOT_FOUND;
            default:
                return IdentityManagementServiceException.Error.UNEXPECTED_ERROR;
        }
    }

    private String calculateDateString(final int lifespan) {
        if (lifespan == 0) {
            return null;
        }
        if (lifespan < 0) {
            logger.warn("Negative value {} will be interpreted same as 0 value for lifespan", lifespan);
            return null;
        }

        final TimeZone timeZone = TimeZone.getTimeZone("UTC");
        final Calendar myCalendar = new GregorianCalendar(timeZone);

        final String dateFormat = "yyyyMMddHHmmss";
        final Date today = new Date();

        final SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        formatter.setCalendar(myCalendar);
        // millisecond for a day is 86400000
        final long lifespanMillisecond = 86400000 * (long) lifespan;
        final long newTimeMillisecond = today.getTime() + lifespanMillisecond;
        final Date newDate = new Date(newTimeMillisecond);
        return formatter.format(newDate) + "Z";
    }

    private void logSecurityEvent(final String detailMsg, final String eventType, final String eventStatus) {
        systemRecorder.recordSecurityEvent(IdmConstants.SECURITY_EVENT_TARGET_SYSTEM, IdmConstants.IDM_SERVICES_DISTINGUISHED_NAME, detailMsg,
                eventType, ErrorSeverity.INFORMATIONAL, eventStatus);
    }

    private void logEvent(final String eventDesc, final String paramValue) {
        systemRecorder.recordEvent(eventDesc, EventLevel.COARSE, "Parameter Values : " + paramValue, IdmConstants.IDM_SERVICES_DISTINGUISHED_NAME, "");
    }

    public void removeMemberUidFromComUsersGroup(final String memberUid) {
        logger.info("Removing {} from com_users", memberUid);
        try {
            communicator.modifyEntryDelete(makeGroupDN(IdmConstants.COM_GROUP_NAME), IdmConstants.LDAP_MEMBER_UID, memberUid);
            logger.info("{} has been removed from com_users", memberUid);
        } catch (DSCommunicatorException e) {
            logger.error("removeMemberUidFromComUsersGroup excp", e);
            final StringBuilder errMsg = new StringBuilder("Exception caught in removeMemberUidFromComUsersGroup for ")
                    .append(memberUid)
                    .append(". Detail message is :")
                    .append(e.getMessage());
            final String errorMessage = errMsg.toString();
            logger.error(errorMessage);
            throw new IdentityManagementServiceException(errorMessage, convertLDAPErrorToIdmError(e.ldapErrorCode()));
        }
    }

    M2MUserPassword createM2MUser(final String userName, final String homeDir, final String groupName,
                                  final int lifeSpan) {
        logger.info("createM2MUser: user[{}] group[{}] home[{}] validDays[{}]", userName, groupName, homeDir, lifeSpan);
        final StringBuilder securityLogMsg = new StringBuilder("User Information: userName=" + userName + " homeDir=" + homeDir + " groupName="
                + groupName + " validDays=" + lifeSpan + " uidNumber=");
        M2MUserPassword m2mUser = null;
        final Lock m2mLock = getDistributedLock(IdmConstants.M2M_LOCK_NAME);
        if (logger.isDebugEnabled()) {
            logger.debug("[IDMS_DISTRIBUTED_LOCK] acquiring lock {}", IdmConstants.M2M_LOCK_NAME);
        }
        m2mLock.lock();
        if (logger.isDebugEnabled()) {
            logger.debug("[IDMS_DISTRIBUTED_LOCK] acquired lock {}", IdmConstants.M2M_LOCK_NAME);
        }
        try {
            // get user with the given username first
            // if user already exists, throw IdentityManagementServiceException
            if (isExistingM2MUser(userName)) {
                final String errMsg = "User " + userName + " already exists.";
                logger.error("createM2MUser: {}", errMsg);
                throw new IdentityManagementServiceException(errMsg, IdentityManagementServiceException.Error.ENTRY_ALREADY_EXISTS);
            }

            // if user doesn't exist,
            // retrieve gidNumber of the groupName
            // if group does not exist, then it will throw exception
            logger.debug("createM2MUser: getGroupGidNumber for user {}", userName);
            final int gidNumber = getGroupGidNumber(groupName);

            // assign uidNumber
            logger.debug("createM2MUser: getUidNumber for user {}", userName);
            final int uidNumber = getUidNumber();
            securityLogMsg.append(uidNumber);

            // using ds-pwp-account-expiration-time:  20140319141844Z
            logger.debug("createM2MUser: calculateDateString for user {}", userName);
            final String expiryDate = calculateDateString(lifeSpan);
            logger.debug("createM2MUser: expiryDate calculated for user {} is {}", userName, expiryDate);

            final String userPassword = generatePassword(userName);

            logger.debug("createM2MUser: getEncryptedPassword for password {}", userPassword);
            final String encryptedPassword = encryptPassword(userPassword);

            final HashMap<String, ArrayList<String>> avPairs = new HashMap<>();
            final ArrayList<String> userObjectClassList = getLDAPUserObjectClasses();
            avPairs.put("objectClass", userObjectClassList);
            avPairs.put(IdmConstants.LDAP_UID, getArrayList(userName));
            avPairs.put(IdmConstants.LDAP_FULL_NAME, getArrayList(userName));
            avPairs.put(IdmConstants.LDAP_LAST_NAME, getArrayList(userName));
            avPairs.put(IdmConstants.LDAP_UID_NUMBER, getArrayList(Integer.toString(uidNumber)));
            avPairs.put(IdmConstants.LDAP_GID_NUMBER, getArrayList(Integer.toString(gidNumber)));
            avPairs.put(IdmConstants.LDAP_HOME_DIRECTORY, getArrayList(homeDir));
            avPairs.put(IdmConstants.LDAP_LOGIN_SHELL, getArrayList(IdmConstants.LDAP_NO_LOGIN_SHELL));
            avPairs.put(IdmConstants.LDAP_AUTH_CRED, getArrayList(encryptedPassword));
            avPairs.put(IdmConstants.LDAP_USER_CRED, getArrayList(userPassword));

            if (expiryDate != null) {
                avPairs.put(IdmConstants.LDAP_ACCOUNT_EXPIRATION_TIME, getArrayList(expiryDate));
            }

            // expect exception in any failure including user already exists in LDAP
            logger.debug("createM2MUser: invoking communicator addEntry operation for user [{}]", userName);
            final String userDN = makeUserDN(userName);
            communicator.addEntry(userDN, avPairs);

            // update pwdReset flag to false to prevent forcing change password on first login
            communicator.modifyEntryReplace(userDN, IdmConstants.LDAP_CRED_RESET, String.valueOf(false));

            // add user to the group
            logger.debug("createM2MUser: addM2MUserToGroup user {} to group {}", userName, groupName);
            addM2MUserToGroup(userName, groupName);
            logSecurityEvent(securityLogMsg.toString(), IdmConstants.SECURITY_EVENT_M2M_USER_CREATION, IdmConstants.SECURITY_EVENT_STATUS_SUCCESS);

            if (userName.startsWith("mm-cert")) {
                final StringBuilder logParam = new StringBuilder("UserName=");
                final String encodedHiddenWord = Base64.getEncoder().encodeToString(userPassword.getBytes(StandardCharsets.UTF_8));
                logParam.append(userName).append("  HiddenWord=").append(encodedHiddenWord);
                logEvent("[TORF480878] Create M2M netsim user ", logParam.toString());
                logger.info("Create M2M netsim user [{}] with encoded hidden word [{}]", userName, encodedHiddenWord);
            }

            m2mUser = new M2MUserPassword(userName, groupName, uidNumber, gidNumber, homeDir, expiryDate, userPassword);

        } catch (final DSCommunicatorException e) {
            logger.error("createM2MUser excp", e);
            final String errMsg = "Problem adding user " + userName + " due to DSCommunicatorException: " + e.getMessage();
            logSecurityEvent(securityLogMsg.toString(), IdmConstants.SECURITY_EVENT_M2M_USER_CREATION, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
            throw new IdentityManagementServiceException(errMsg, convertLDAPErrorToIdmError(e.ldapErrorCode()));
        } catch (final IdentityManagementServiceException e) {
            logSecurityEvent(securityLogMsg.toString(), IdmConstants.SECURITY_EVENT_M2M_USER_CREATION, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
            throw e;
        } catch (final Exception e) {
            final String errMsg = "Problem adding user " + userName + " due to Exception: " + e.getMessage();
            logger.error(errMsg);
            logSecurityEvent(securityLogMsg.toString(), IdmConstants.SECURITY_EVENT_M2M_USER_CREATION, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
            throw new IdentityManagementServiceException(errMsg, IdentityManagementServiceException.Error.UNEXPECTED_ERROR);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("[IDMS_DISTRIBUTED_LOCK] releasing lock {}", IdmConstants.M2M_LOCK_NAME);
            }
            m2mLock.unlock();
            if (logger.isDebugEnabled()) {
                logger.debug("[IDMS_DISTRIBUTED_LOCK] released lock {}", IdmConstants.M2M_LOCK_NAME);
            }
        }
        return m2mUser;

    }

    /**
     * Gets the distributed lock of given name in default cluster.
     * T.B.D : try to use Lock getDistributedLock(String var1, String var2)
     * where var2 = cluster name.
     *
     * @param lockName the lock name
     * @return the distributed lock
     */
    protected Lock getDistributedLock(final String lockName) {
        return lockManager.getDistributedLock(lockName);
    }

    private <T extends Object> boolean isValidCollection(Collection<T> coll) {
        return ((coll != null) && !coll.isEmpty());
    }

    /********************* MR 105 65-0334/79156 New interface methods *******************/
    private void getProxyAgentAccountGeneric(final ProxyAgentAccountGetDataManager proxyAgentAccountGetDataManager,
                                             final List<String> proxyAccountSubtrees,
                                             final List<String> attributesList,
                                             final String filterToApply,
                                             final Boolean skipGetOperation,
                                             final String securityLogMsg) {

        /* loop on desired subtree (it depend on isLegacy flag) */
        logger.debug("Getting with summary option=[{}]", skipGetOperation);
        for (String proxyAccountSubtree : proxyAccountSubtrees) {
            try {
                logger.debug("Start Getting Proxy Users from  subtree=[{}]", proxyAccountSubtree);
                List<SearchResultEntry> searchResultEntryList = communicator.queryGenericMultipleAttributes(proxyAccountSubtree,
                        SearchControls.ONELEVEL_SCOPE, attributesList, filterToApply);

                /* handle result of search on LDAP server */
                handleSearchResultEntryList(proxyAgentAccountGetDataManager, skipGetOperation, searchResultEntryList);

                /* save size for each subtree */
                Integer size = searchResultEntryList.size();
                logger.debug("Size of subtree=[{}] is=[{}] updating subtree counters ...", proxyAccountSubtree, size);

                buildProxyAccountSubtreeRequestedCounters(proxyAccountSubtree, proxyAgentAccountGetDataManager, size);
                logger.debug("End Getting Proxy Users from subtree=[{}]", proxyAccountSubtree);
            } catch (final DSCommunicatorException e) {
                final String errMsg = "Problem Getting Proxy users, error " + e.getMessage();
                logger.error(errMsg, e);
                logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_PROXY_USER_GET_ENTRIES, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
                throw new IdentityManagementServiceException(errMsg, convertLDAPErrorToIdmError(e.ldapErrorCode()));
            }
        }
    }

    private void handleSearchResultEntryList (final ProxyAgentAccountGetDataManager proxyAgentAccountGetDataManager,
                                              final Boolean skipGetOperation,
                                              final List<SearchResultEntry> searchResultEntryList) {
        if (!skipGetOperation) {
            for (SearchResultEntry searchResultEntry : searchResultEntryList) {
                String lastLoginTime = null;
                String dsPwpAccountDisabled = null;
                String createTimestamp = null;
                /* update output structure with proxy account entry details */
                if (searchResultEntry.getAttribute("createTimestamp") != null) {
                    createTimestamp = searchResultEntry.getAttribute("createTimestamp").firstValueAsString();
                }
                if (searchResultEntry.getAttribute("lastLoginTime") != null) {
                    lastLoginTime = searchResultEntry.getAttribute("lastLoginTime").firstValueAsString();
                }
                if (searchResultEntry.getAttribute("ds-pwp-account-disabled") != null) {
                    dsPwpAccountDisabled = searchResultEntry.getAttribute("ds-pwp-account-disabled").firstValueAsString();
                }

                proxyAgentAccountGetDataManager.updateProxyAgentAccountDetails(
                        searchResultEntry.getName().toString(),
                        createTimestamp,
                        lastLoginTime,
                        dsPwpAccountDisabled);
            }
        }
    }
    private ProxyAgentAccountGetData getProxyAgentAccountCommon(Boolean isLegacy, Boolean isSummary,
                                                                List<String> attributesList,
                                                                String filterToApply,
                                                                String securityLogMsg) {

        /* build structure chain for get operation */
        ProxyAgentAccountGetDataManager proxyAgentAccountGetDataManager = proxyAgentAccountGetDataBuilder(isLegacy, isSummary);

        List<String> subtrees = proxyAgentAccountGetDataManager.getProxyAgentAccountBuilder().getProxyAccountSubtrees();
        Boolean skipGetOperation = proxyAgentAccountGetDataManager.getProxyAgentAccountBuilder().getSkipProxyAccountGetOperation();

        /* if only summary report is required, getting of the details is skipped */
        getProxyAgentAccountGeneric(proxyAgentAccountGetDataManager, subtrees,
                attributesList, filterToApply, skipGetOperation, securityLogMsg);

        /* update the operation-specific counters into output structure */
        proxyAgentAccountGetDataManager.updateProxyAgentAccountRequestedCounters();

        /* save global subtree counters for all proxy account subtrres*/
        List<String> subtreesActive = proxyAgentAccountGetDataManager.getProxyAgentAccountBuilder().getProxyAccountSubtreesActive();
        buildProxyAccountSubtreeCounters(subtreesActive, proxyAgentAccountGetDataManager);

        /* update the global counters into output structure */
        proxyAgentAccountGetDataManager.updateProxyAgentAccountCounters();

        logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_PROXY_USER_GET_ENTRIES, IdmConstants.SECURITY_EVENT_STATUS_SUCCESS);
        return proxyAgentAccountGetDataManager.getProxyAgentAccountGetData();
    }

    public ProxyAgentAccountGetData getProxyAgentAccount(Boolean isLegacy, Boolean isSummary) {
        /* prepare attribute we interested in; filter by default is: "(objectclass=*) */
        final ArrayList<String> attributesList = new ArrayList<>();
        attributesList.add("+");

        final String securityLogMsg = "Get ALL Proxy Users";
        logger.debug("Get ALL Proxy Users : isLegacy=[{}], isSummary=[{}]", isLegacy, isSummary);

        return getProxyAgentAccountCommon(isLegacy, isSummary, attributesList, null, securityLogMsg);
    }

    public ProxyAgentAccountGetData getProxyAgentAccountByAdminStatus(ProxyAgentAccountAdminStatus adminStatus,
                                                                      Boolean isLegacy, Boolean isSummary) {
        /* prepare attribute list we interested in */
        final ArrayList<String> attributesList = new ArrayList<>();
        attributesList.add("+");
        /* prepare filter */
        String sAdminStatus = adminStatus.getProxyAgentAccountAdminStatus();
        String filterToApply = sAdminStatus.equals("enabled") ?
                "(&(objectclass=inetorgperson)(|(ds-pwp-account-disabled=false)(!(ds-pwp-account-disabled=*))))" :
                "(&(objectclass=inetorgperson)(ds-pwp-account-disabled=true))";

        final String securityLogMsg = "Get Proxy Users By Admin Status: adminStatus=" + adminStatus.name();
        logger.debug("Get Proxy Users By Admin Status: isLegacy=[{}], isSumamry=[{}], sAdminStatus=[{}]", isLegacy, isSummary, adminStatus);
        return getProxyAgentAccountCommon(isLegacy, isSummary, attributesList, filterToApply, securityLogMsg);
    }

    public ProxyAgentAccountGetData getProxyAgentAccountByInactivityPeriod(Long inactivityPeriod, Boolean isLegacy,
                                                                           Boolean isSummary) {
        /* prepare attribute list we interested in */
        final ArrayList<String> attributesList = new ArrayList<>();
        attributesList.add("+");

        /* prepare filter */
        Date date = new Date(inactivityPeriod);
        String inactivityDate = GeneralizedTime.valueOf(date).toString();
        String filterToApply = String.format("(&(objectclass=inetorgperson)(|(&(createTimestamp<=%s)(!(lastLoginTime=*)))" +
                "(lastLoginTime<=%s)))", inactivityDate, inactivityDate);

        final String securityLogMsg = "Get Proxy Users By Inactivity Period: inactivityDate=" + inactivityDate;
        logger.debug("Get Proxy Users By Inactivity Period: isLegacy=[{}], isSumamry=[{}], inactivityDate=[{}]",
                isLegacy, isSummary, inactivityDate);

        return getProxyAgentAccountCommon(isLegacy, isSummary, attributesList, filterToApply,securityLogMsg);
    }

    public ProxyAgentAccountDetails getProxyAgentAccountDetails(String userDn) {
        /* build structure chain for get operation */
        ProxyAgentAccountDetails proxyAgentAccountDetails = new ProxyAgentAccountDetails();

        /* prepare attribute list we interested in; filter by default is: "(objectclass=*) */
        final ArrayList<String> attributesList = new ArrayList<>();
        attributesList.add("+");

        final String securityLogMsg = "User Information: userDN=" + userDn;
        logger.debug("Get Proxy User Details: {}", userDn);

        /* get single entry */
        try {
            logger.debug("Single Entry Proxy User Start Getting");
            List<SearchResultEntry> searchResultEntryList = communicator.queryGenericMultipleAttributes(userDn,
                    SearchControls.SUBTREE_SCOPE, attributesList, null);

            for (SearchResultEntry searchResultEntry : searchResultEntryList) {
                String dsPwpAccountDisabled;
                proxyAgentAccountDetails.setUserDn(searchResultEntry.getName().toString());
                if (searchResultEntry.getAttribute("ds-pwp-account-disabled") != null) {
                    dsPwpAccountDisabled = searchResultEntry.getAttribute("ds-pwp-account-disabled").firstValueAsString();

                    String localAdminStatus = dsPwpAccountDisabled.equals("true") ? "disabled" : "enabled";
                    proxyAgentAccountDetails.setAdminStatus(valueOfProxyAgentAccoountAdminStatus(localAdminStatus));
                    logger.debug("userDn {} getAdminStatus {}", userDn, proxyAgentAccountDetails.getAdminStatus());
                } else {
                    proxyAgentAccountDetails.setAdminStatus(null);
                }
            }
            logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_PROXY_USER_GET_SINGLE_ENTRY, IdmConstants.SECURITY_EVENT_STATUS_SUCCESS);
            logger.debug("Single Entry  Proxy user {} has been got successfully", userDn);
        } catch (final DSCommunicatorException e) {
            final String errMsg = "Problem retrieving Single Entry  Proxy users, error " + e.getMessage();
            logger.error(errMsg, e);
            logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_PROXY_USER_GET_SINGLE_ENTRY, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
            throw new IdentityManagementServiceException(errMsg, convertLDAPErrorToIdmError(e.ldapErrorCode()));
        }
        return proxyAgentAccountDetails;
    }

    public Boolean updateProxyAgentAccountAdminStatus(String userDn, ProxyAgentAccountAdminStatus adminStatus) {
        final String attributeName = "ds-pwp-account-disabled";
        final String attributeNameValue = adminStatus.name().equals("DISABLED") ? "true" : "false";

        final String securityLogMsg = "User Information: userDN=" + userDn + " " + "admin status=" + adminStatus.name();
        if(logger.isDebugEnabled()) {
            logger.debug("Update Proxy User: {} with admin status: {}", userDn, adminStatus.name());
        }
        try {
            logger.debug("Proxy User Start Updating ");
            communicator.modifyEntryReplace(userDn, attributeName, attributeNameValue);
            logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_PROXY_USER_UPDATE, IdmConstants.SECURITY_EVENT_STATUS_SUCCESS);
            logger.debug("Proxy user {} has been updated successfully", userDn);

            String command = adminStatus == ProxyAgentAccountAdminStatus.ENABLED ? ENABLED_CMD : DISABLED_CMD;
            increaseProxyAccountMonitoredData(userDn, command);
            return true;
        } catch (final DSCommunicatorException e) {
            logger.error("Update Proxy User excp: {}", e.getMessage());
            if (e.ldapErrorCode() == IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY) {
                final String errMsg = "User " + userDn + " does not exist";
                logger.warn(errMsg);
            } else {
                final String errMsg = "Update Proxy User: Problem occurred on updating user " + userDn;
                logger.error("{}, {}", errMsg, e.getMessage());
                logSecurityEvent(securityLogMsg, IdmConstants.SECURITY_EVENT_PROXY_USER_UPDATE, IdmConstants.SECURITY_EVENT_STATUS_FAILURE);
                throw new IdentityManagementServiceException(errMsg, convertLDAPErrorToIdmError(e.ldapErrorCode()));
            }
        }
        return false;
    }

    private void buildProxyAccountSubtreeCounters(final List<String> proxyAccountSubtrees,
                                                  ProxyAgentAccountGetDataManager proxyAgentAccountGetDataManager
    ) {
        /* prepare attribute list we interested in */
        final ArrayList<String> attributesList = new ArrayList<>();
        attributesList.add("+");
        for (String proxyAccountSubtree : proxyAccountSubtrees) {
            try {
                logger.debug("Start Getting Global counter for subtree: {}",proxyAccountSubtree);
                List<SearchResultEntry> searchResultEntryList = communicator.queryGenericMultipleAttributes(proxyAccountSubtree,
                        SearchControls.OBJECT_SCOPE, attributesList, null);

                for (SearchResultEntry searchResultEntry : searchResultEntryList) {
                    if (searchResultEntry.getAttribute("numSubordinates") != null) {
                        String numSubordinates = searchResultEntry.getAttribute("numSubordinates").firstValueAsString();
                        proxyAgentAccountGetDataManager.updateTotalSubtreeEntries(proxyAccountSubtree, Integer.valueOf(numSubordinates));
                    }
                }
                logger.debug("End Getting Global counter for subtree: {} ",proxyAccountSubtree);
            } catch (final DSCommunicatorException e) {
                final String errMsg = "Problem getting subtrees global counters , error " + e.getMessage();
                logger.error(errMsg, e);
                throw new IdentityManagementServiceException(errMsg, convertLDAPErrorToIdmError(e.ldapErrorCode()));
            }
        }
    }

    private void buildProxyAccountSubtreeRequestedCounters(final String proxyAccountSubtree,
                                                           final ProxyAgentAccountGetDataManager proxyAgentAccountGetDataManager,
                                                           Integer size) {

        proxyAgentAccountGetDataManager.updateRequestedSubtreeEntries(proxyAccountSubtree, size);
    }

    private ProxyAgentAccountGetDataManager proxyAgentAccountGetDataBuilder (Boolean isLegacy, Boolean isSummary) {
        List<String> proxyAgentAccountSubtreeList = findAllProxyAccountSubtrees();

        /* initialize proxy account get data manager */
        ProxyAgentAccountGetDataManager proxyAgentAccountGetDataManager = new ProxyAgentAccountGetDataManager();
        proxyAgentAccountGetDataManager.initializeProxyAgentAccountGetData(isLegacy, isSummary, proxyAgentAccountSubtreeList);

        return proxyAgentAccountGetDataManager;
    }

    private List<String> findAllProxyAccountSubtrees() {

        final String baseSearchDn = "ou=com," + ConfigurationBean.get(IdmConstants.COM_INF_LDAP_ROOT_SUFFIX_PROPERTY);
        /* prepare attribute list we interested in */
        final ArrayList<String> attributesList = new ArrayList<>();
        attributesList.add("dn");

        List<String> proxyAgentAccountSubtreeList = new ArrayList<>();
        try {
            logger.debug("Start Discovering Subtrees");
            List<SearchResultEntry> searchResultEntryList = communicator.queryGenericMultipleAttributes(baseSearchDn,
                    SearchControls.ONELEVEL_SCOPE, attributesList, null);

            for (SearchResultEntry searchResultEntry : searchResultEntryList) {
                proxyAgentAccountSubtreeList.add(searchResultEntry.getName().toString());
            }
            logger.debug("End Discovering Subtrees, proxyAgentAccountSubtreeList is ={}", proxyAgentAccountSubtreeList);
        } catch (final DSCommunicatorException e) {
            final String errMsg = "Problem retrieving Proxy Subtrees, error " + e.getMessage();
            logger.error(errMsg, e);
            throw new IdentityManagementServiceException(errMsg, convertLDAPErrorToIdmError(e.ldapErrorCode()));
        }
        return proxyAgentAccountSubtreeList;
    }

    /********************* MR 105 65-0334/79156 New counter  *******************/
    private void increaseProxyAccountMonitoredData(final String userDN, final String commandType) {
        proxyAccountMonitoredDataManager.updateMonitoredDataCounters(userDN, commandType);
    }
}