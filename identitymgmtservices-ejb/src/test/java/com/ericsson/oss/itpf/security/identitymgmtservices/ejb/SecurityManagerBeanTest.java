/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb;

import static com.ericsson.oss.itpf.security.identitymgmtservices.IdmConstants.LDAP_ERROR_CONNECTION_FAILURE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;

import javax.naming.directory.SearchControls;
import javax.xml.bind.DatatypeConverter;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.cluster.lock.LockManager;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.security.cryptography.CryptographyService;
import com.ericsson.oss.itpf.security.identitymgmtservices.ConfigurationBean;
import com.ericsson.oss.itpf.security.identitymgmtservices.DSCommunicator;
import com.ericsson.oss.itpf.security.identitymgmtservices.DSCommunicatorException;
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementServiceException;
import com.ericsson.oss.itpf.security.identitymgmtservices.IdmConstants;
import com.ericsson.oss.itpf.security.identitymgmtservices.M2MUser;
import com.ericsson.oss.itpf.security.identitymgmtservices.crypto.CryptographyServiceClient;

public class SecurityManagerBeanTest {

    private SystemRecorder systemRecorder = null;
    private Logger logger = LoggerFactory.getLogger(SecurityManagerBeanTest.class);
    public ExpectedException exception = ExpectedException.none();
    private StringBuffer dnPostfix = null;
    private static ArrayList<String> attrList = new ArrayList<String>();
    public final static String CHARSET = "UTF-8";

    static {
        attrList.add(IdmConstants.LDAP_UID);
        attrList.add(IdmConstants.LDAP_FULL_NAME);
        attrList.add(IdmConstants.LDAP_LAST_NAME);
        attrList.add(IdmConstants.LDAP_UID_NUMBER);
        attrList.add(IdmConstants.LDAP_GID_NUMBER);
        attrList.add(IdmConstants.LDAP_HOME_DIRECTORY);
        attrList.add(IdmConstants.LDAP_LOGIN_SHELL);
        attrList.add(IdmConstants.LDAP_ACCOUNT_EXPIRATION_TIME);
    }

    private SecurityManagerBean secManager = null;
    private CryptographyServiceClient cryptographyServiceClient = null;

    @Mock
    private String baseDN;
    private String m2mUserContainer;
    private String m2mUserContainerGroup;
    private String m2mUserContainerGroup1;

    private Lock M2MLock;
    private Lock proxyLock;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        System.setProperty(IdmConstants.CONFIGURATION_PROPERTY, "./src/test/resources/datastore.properties");
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        secManager = new SecurityManagerBean();
        secManager.logger = logger;
        secManager.lockManager = mock(LockManager.class);
        M2MLock = mock(Lock.class);
        proxyLock = mock(Lock.class);
        when(secManager.getDistributedLock(IdmConstants.PROXY_LOCK_NAME)).thenReturn(proxyLock);
        when(secManager.getDistributedLock(IdmConstants.M2M_LOCK_NAME)).thenReturn(M2MLock);

        systemRecorder = mock(SystemRecorder.class);
        secManager.systemRecorder = systemRecorder;
        baseDN = ConfigurationBean.get(IdmConstants.COM_INF_LDAP_ROOT_SUFFIX_PROPERTY);
        m2mUserContainer = ConfigurationBean.get(IdmConstants.COM_INF_LDAP_M2M_USER_CONTAINER_PROPERTY);
        logger.debug("TEST m2mUserContainer: {}", m2mUserContainer);
        //secManager.communicator = communicator;
    }

    @After
    public void tearDown() {
        //communicator = null;
    }

    /**
     * test to see whether it returns M2MUser
     */
    @Test
    public void testGetM2MUser1Success() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "m2mUser1";
        final String dn = makeUserDN(userName);
        final String gidNumber = "5000";
        HashMap<String, ArrayList<String>> avpairs = new HashMap<String, ArrayList<String>>();
        final ArrayList<String> groupNames = getArrayList(m2mUserContainer);
        try {
            final String groupBaseDN = "ou=Groups" + getDnPostfix();
            final String filterString = IdmConstants.LDAP_GID_NUMBER + "=" + gidNumber;
            avpairs = getAVPairs(userName, userName, "/home/m2mUser1", gidNumber, "5000", "");
            logger.debug("TEST m2muser1 dn {} groupdn {} number of attrs : {}", dn, groupBaseDN, avpairs.size());
            when(communicator.queryMultipleAttributes(dn, SearchControls.OBJECT_SCOPE, attrList)).thenReturn(avpairs);
            when(communicator.querySingleAttribute(groupBaseDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_FULL_NAME, filterString))
                    .thenReturn(groupNames);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from communicator.queryMultipleAttributes(): " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            logger.debug("TEST memuser1:INPUT {}", avpairs.get(IdmConstants.LDAP_HOME_DIRECTORY));
            final M2MUser user = secManager.getM2MUser(userName);
            assertEquals(userName, user.getUserName());
        } catch (final IdentityManagementServiceException e) {
            fail("shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * When user does not exist, it should throw exception with LDAP_ERROR_NO_SUCH_ENTRY
     */
    @Test
    public void testGetM2MUser2NotExist() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "m2mUser2";
        final String dn = makeUserDN(userName);

        final int scope = SearchControls.OBJECT_SCOPE;
        try {
            logger.debug("TEST GetM2MUser2NotExist");
            final DSCommunicatorException dsException = new DSCommunicatorException("User does not exist", IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY);
            when(communicator.queryMultipleAttributes(dn, scope, attrList)).thenThrow(dsException);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from communicator.queryMultipleAttributes(): " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            secManager.getM2MUser(userName);
            fail("getM2MUser should throw exception with LDAP_ERROR_NO_SUCH_ENTRY");
        } catch (final IdentityManagementServiceException e) {
            //expected exception, assert it
            assertEquals(IdentityManagementServiceException.Error.ENTRY_NOT_FOUND, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
        }

    }

    /**
     * test to get M2M user when group name not exist
     */
    @Test
    public void testGetM2MUser1GroupNameNotExist() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "m2mUser1";
        final String dn = makeUserDN(userName);
        final String gidNumber = "5000";
        HashMap<String, ArrayList<String>> avpairs = new HashMap<String, ArrayList<String>>();
        final ArrayList<String> groupNames = new ArrayList<>();
        try {
            final String groupBaseDN = "ou=Groups" + getDnPostfix();
            final String filterString = IdmConstants.LDAP_GID_NUMBER + "=" + gidNumber;
            avpairs = getAVPairs(userName, userName, "/home/m2mUser1", gidNumber, "5000", "");
            logger.debug("TEST m2muser1 dn {} groupdn {} number of attrs : {}", dn, groupBaseDN, avpairs.size());
            when(communicator.queryMultipleAttributes(dn, SearchControls.OBJECT_SCOPE, attrList)).thenReturn(avpairs);
            when(communicator.querySingleAttribute(groupBaseDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_FULL_NAME, filterString))
                    .thenReturn(groupNames);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from communicator.queryMultipleAttributes(): " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            logger.debug("TEST memuser1:INPUT {}", avpairs.get(IdmConstants.LDAP_HOME_DIRECTORY));
            secManager.getM2MUser(userName);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.ENTRY_NOT_FOUND, e.getError());
            e.printStackTrace();
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * test to get M2M user when group name is greater than one
     */
    @Test
    public void testGetM2MUser1GroupNameSizeGreaterThanOne() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "m2mUser1";
        final String dn = makeUserDN(userName);
        final String gidNumber = "5000";
        HashMap<String, ArrayList<String>> avpairs = new HashMap<String, ArrayList<String>>();

        m2mUserContainerGroup = ConfigurationBean.get(IdmConstants.COM_INF_LDAP_M2M_USER_CONTAINER_PROPERTY);
        m2mUserContainerGroup1 = ConfigurationBean.get(IdmConstants.COM_INF_LDAP_M2M_USER_CONTAINER_PROPERTY);
        final ArrayList<String> groupNames = new ArrayList<>();
        groupNames.add(m2mUserContainerGroup);
        groupNames.add(m2mUserContainerGroup1);

        try {
            final String groupBaseDN = "ou=Groups" + getDnPostfix();
            final String filterString = IdmConstants.LDAP_GID_NUMBER + "=" + gidNumber;
            avpairs = getAVPairs(userName, userName, "/home/m2mUser1", gidNumber, "5000", "");
            logger.debug("TEST m2muser1 dn {} groupdn {} number of attrs : {}", dn, groupBaseDN, avpairs.size());
            when(communicator.queryMultipleAttributes(dn, SearchControls.OBJECT_SCOPE, attrList)).thenReturn(avpairs);
            when(communicator.querySingleAttribute(groupBaseDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_FULL_NAME, filterString))
                    .thenReturn(groupNames);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from communicator.queryMultipleAttributes(): " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            logger.debug("TEST memuser1:INPUT {}", avpairs.get(IdmConstants.LDAP_HOME_DIRECTORY));
            final M2MUser user = secManager.getM2MUser(userName);
            assertEquals(userName, user.getUserName());
        } catch (final IdentityManagementServiceException e) {
            fail("shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * test createM2MUser method
     */
    @Test
    public void testCreateM2MUser1Success() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final CryptographyService cryptographyService = mock(CryptographyService.class);
        cryptographyServiceClient = new CryptographyServiceClient(cryptographyService);
        secManager.cryptoClient = cryptographyServiceClient;
        final String userName = "m2mUser3";
        final String gidNumber = "5000";
        final String groupName = m2mUserContainer;
        final String homeDir = "/home/m2mUser3";
        final ArrayList<String> gidNumberList = new ArrayList<String>();
        gidNumberList.add(gidNumber);
        final ArrayList<String> uidNumberList = new ArrayList<String>();
        uidNumberList.add("5001");
        try {
            when(communicator.getRandomPassword()).thenReturn("changeit");
            final byte[] encryptedBytesArr = "changeit".getBytes(CHARSET);
            when(cryptographyService.encrypt(DatatypeConverter.parseBase64Binary("changeit"))).thenReturn(encryptedBytesArr);
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(false);
            when(communicator.querySingleAttribute(makeGroupDN(groupName), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                    .thenReturn(gidNumberList);
            when(communicator.querySingleAttribute(getDnPostfix().replaceFirst(",", ""), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_UID_NUMBER,
                    null)).thenReturn(uidNumberList);

        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            secManager.addM2MUser(userName, homeDir, groupName, 0);
        } catch (final IdentityManagementServiceException e) {
            fail("shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * test createM2MUser method with addM2MUserToGroup exception
     */
    @Test
    public void testCreateM2MUser1FailureDueToAddM2MUserToGroupException() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final CryptographyService cryptographyService = mock(CryptographyService.class);
        cryptographyServiceClient = new CryptographyServiceClient(cryptographyService);
        secManager.cryptoClient = cryptographyServiceClient;
        final String userName = "m2mUser3";
        final String gidNumber = "5000";
        final String groupName = m2mUserContainer;
        final String homeDir = "/home/m2mUser3";
        final ArrayList<String> gidNumberList = new ArrayList<String>();
        gidNumberList.add(gidNumber);
        final ArrayList<String> uidNumberList = new ArrayList<String>();
        uidNumberList.add("5001");

        try {
            when(communicator.getRandomPassword()).thenReturn("changeit");
            final byte[] encryptedBytesArr = "changeit".getBytes(CHARSET);
            when(cryptographyService.encrypt(DatatypeConverter.parseBase64Binary("changeit"))).thenReturn(encryptedBytesArr);
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(false);
            when(communicator.querySingleAttribute(makeGroupDN(groupName), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                    .thenReturn(gidNumberList);
            when(communicator.querySingleAttribute(getDnPostfix().replaceFirst(",", ""), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_UID_NUMBER,
                    null)).thenReturn(uidNumberList);

            DSCommunicatorException dsException = new DSCommunicatorException("error", -1);
            String groupDN = makeGroupDN(groupName);
            doThrow(dsException).when(communicator).modifyEntryAdd(groupDN, getProperAttribute(groupName), makeUserDN(userName));

        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            secManager.addM2MUser(userName, homeDir, groupName, 0);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.UNEXPECTED_ERROR, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * test createM2MUser method assuming that cryptography service does not work properly
     */
    @Test
    public void testCreateM2MUserCryptoFailure() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final CryptographyService cryptographyService = mock(CryptographyService.class);
        cryptographyServiceClient = new CryptographyServiceClient(cryptographyService);
        secManager.cryptoClient = cryptographyServiceClient;
        final String userName = "m2mUser3";
        final String gidNumber = "5000";
        final String groupName = m2mUserContainer;
        final String homeDir = "/home/m2mUser3";
        final ArrayList<String> gidNumberList = new ArrayList<String>();
        gidNumberList.add(gidNumber);
        final ArrayList<String> uidNumberList = new ArrayList<String>();
        uidNumberList.add("5001");
        try {
            when(communicator.getRandomPassword()).thenReturn("changeit");
            when(cryptographyService.encrypt("changeit".getBytes(CHARSET))).thenReturn(null);
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(false);
            when(communicator.querySingleAttribute(makeGroupDN(groupName), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                    .thenReturn(gidNumberList);
            when(communicator.querySingleAttribute(getDnPostfix().replaceFirst(",", ""), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_UID_NUMBER,
                    null)).thenReturn(uidNumberList);

        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            secManager.addM2MUser(userName, homeDir, groupName, 0);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.UNEXPECTED_ERROR, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * test createM2MUser method but user already exist
     */
    @Test
    public void testCreateM2MUser2UserExist() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final CryptographyService cryptographyService = mock(CryptographyService.class);
        cryptographyServiceClient = new CryptographyServiceClient(cryptographyService);
        secManager.cryptoClient = cryptographyServiceClient;
        final String userName = "m2mUser4";
        final String gidNumber = "5000";
        final String groupName = m2mUserContainer;
        final String homeDir = "/home/m2mUser4";
        final ArrayList<String> gidNumberList = new ArrayList<String>();
        gidNumberList.add(gidNumber);
        final ArrayList<String> uidNumberList = new ArrayList<String>();
        uidNumberList.add("5001");
        try {
            when(communicator.getRandomPassword()).thenReturn("changeit");
            final byte[] encryptedBytesArr = "changeit".getBytes(CHARSET);
            when(cryptographyService.encrypt("changeit".getBytes(CHARSET))).thenReturn(encryptedBytesArr);
            when(communicator.getRandomPassword()).thenReturn("changeit");
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(true);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            secManager.addM2MUser(userName, homeDir, groupName, 0);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.ENTRY_ALREADY_EXISTS, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * test createM2MUser method but groupName does not exist
     */
    @Test
    public void testCreateM2MUser3GroupNotFound() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final int errorCode = IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY;
        final String userName = "m2mUser5";
        final String groupName = m2mUserContainer;
        final String homeDir = "/home/m2mUser5";
        final String gidNumber = "5000";
        final ArrayList<String> gidNumberList = new ArrayList<String>();
        gidNumberList.add(gidNumber);
        final String filterString = IdmConstants.LDAP_GID_NUMBER + "=" + gidNumber;
        try {
            final DSCommunicatorException dsException = new DSCommunicatorException("No mapping group found for gidNumber " + gidNumber, errorCode);
            when(communicator.getRandomPassword()).thenReturn("changeit");
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(false);
            when(communicator.querySingleAttribute(baseDN, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_FULL_NAME, filterString))
                    .thenThrow(dsException);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            secManager.addM2MUser(userName, homeDir, groupName, 0);
        } catch (final IdentityManagementServiceException e) {
            //expected exception, assert it.
            assertEquals(IdentityManagementServiceException.Error.NO_SUCH_ATTRIBUTE, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * test createM2MUser method but gidNumber not found from the group entry
     */
    @Test
    public void testCreateM2MUser4GidNumberNotFound() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final int errorCode = IdmConstants.LDAP_ERROR_NO_SUCH_ATTRIBUTE;
        final String userName = "m2mUser6";
        final String groupName = m2mUserContainer;
        final String homeDir = "/home/m2mUser6";
        try {
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(false);
            final DSCommunicatorException dsException = new DSCommunicatorException(
                    "Problem retrieving attribute gidnumber for group" + groupName + " failing ", errorCode);
            when(communicator.querySingleAttribute(makeGroupDN(groupName), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                    .thenThrow(dsException);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            secManager.addM2MUser(userName, homeDir, groupName, 0);
        } catch (final IdentityManagementServiceException e) {
            //expected exception assert it
            assertEquals(IdentityManagementServiceException.Error.NO_SUCH_ATTRIBUTE, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * test createM2MUser method but userid already exists in group uniqueMember
     */
    @Test
    public void testCreateM2MUser5AssociationExists() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final int errorCode = IdmConstants.LDAP_ERROR_ATTRIBUTE_OR_VALUE_EXIST;
        final String userName = "m2mUser7";
        final String userDN = makeUserDN(userName);

        final String groupName = m2mUserContainer;
        final String groupDN = makeGroupDN(groupName);
        final String homeDir = "/home/m2mUser7";
        final ArrayList<String> uidNumberList = new ArrayList<String>();
        uidNumberList.add("5001");
        final DSCommunicatorException dsException = new DSCommunicatorException("Entry " + userName + " already listed as a group member", errorCode);
        try {
            when(communicator.searchEntry(userDN)).thenReturn(false);
            when(communicator.querySingleAttribute(makeGroupDN(groupName), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_GID_NUMBER, null))
                    .thenThrow(dsException);
            when(communicator.querySingleAttribute(getDnPostfix().replaceFirst(",", ""), SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_UID_NUMBER,
                    null)).thenReturn(uidNumberList);
            doThrow(dsException).when(communicator).modifyEntryAdd(groupDN, IdmConstants.LDAP_UNIQUE_MEMBER, userDN);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            secManager.addM2MUser(userName, homeDir, groupName, 0);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.ATTR_OR_VALUE_ALREADY_EXISTS, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * test deleteM2MUser method when user does not exist in LDAP
     */
    @Test
    public void testDeleteM2MUser() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "m2mUser8";
        final String dn = makeUserDN(userName);
        final int errorCode = IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY;
        try {
            final DSCommunicatorException dsException = new DSCommunicatorException("Entry " + userName + " does not exist", errorCode);
            doThrow(dsException).when(communicator).deleteEntry(dn);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            secManager.removeM2MUser(userName);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.ENTRY_NOT_FOUND, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * test deleteM2MUser method when connection failure exception is raised
     */
    @Test
    public void testDeleteM2MUserConnectionFailureFailure() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "m2mUser8";
        final String dn = makeUserDN(userName);
        final int errorCode = LDAP_ERROR_CONNECTION_FAILURE;
        try {
            final DSCommunicatorException dsException = new DSCommunicatorException("Entry " + userName + " connection failure", errorCode);
            doThrow(dsException).when(communicator).deleteEntry(dn);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            secManager.removeM2MUser(userName);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.DATA_STORE_CONNECTION_FAILURE, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * test deleteM2MUser method when user exists in LDAP
     */
    @Test
    public void testDeleteM2MUserSuccess() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "m2mUser8";
        final String dn = makeUserDN(userName);
        final int errorCode = IdmConstants.LDAP_ERROR_NO_SUCH_ENTRY;
        try {
            final DSCommunicatorException dsException = new DSCommunicatorException("Entry " + userName + " does not exist", errorCode);
            doNothing().when(communicator).deleteEntry(dn);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            final boolean isM2MUserDeleted = secManager.removeM2MUser(userName);
            assertEquals(true, isM2MUserDeleted);
            final String msg = "User successfully deleted";
            logger.debug(msg);

        } catch (final IdentityManagementServiceException e) {
            fail("shouldn't throw exception from mock communicator  " + e.getMessage());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * test isExistingM2MUser method when user does not exist in LDAP
     */
    @Test
    public void testIsExistingM2MUser() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "m2mUser9";
        final String dn = makeUserDN(userName);
        try {
            when(communicator.searchEntry(dn)).thenReturn(false);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            final boolean result = secManager.isExistingM2MUser(userName);
            assertFalse(result);
        } catch (final IdentityManagementServiceException e) {
            fail("should not throw this exception:" + e.getMessage());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * test isExistingM2MUser method when user exists in LDAP
     */
    @Test
    public void testIsExistingM2MUser2() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "m2mUser10";
        final String dn = makeUserDN(userName);
        try {
            when(communicator.searchEntry(dn)).thenReturn(true);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            final boolean result = secManager.isExistingM2MUser(userName);
            assertTrue(result);
        } catch (final IdentityManagementServiceException e) {
            fail("should not throw this exception:" + e.getMessage());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * test deleteM2MUser method when connection failure to LDAP occurs
     */
    @Test
    public void testIsExistingM2MUser3() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "m2mUser11";
        final String dn = makeUserDN(userName);
        final int errorCode = LDAP_ERROR_CONNECTION_FAILURE;
        try {
            final DSCommunicatorException dsException = new DSCommunicatorException("Query " + userName + " is failing due to ", errorCode);
            doThrow(dsException).when(communicator).searchEntry(dn);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            secManager.isExistingM2MUser(userName);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.DATA_STORE_CONNECTION_FAILURE, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }

    }


    /**
     * test getUidNumber() exception due connection failure
     */
    @Test
    public void testGetUidNumberConnectionFailure() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "m2mUser11";
        final String dnFull = "ou=" + "M2MUsers" + getDnPostfix();;
        final int errorCode = LDAP_ERROR_CONNECTION_FAILURE;
        try {
            final DSCommunicatorException dsException = new DSCommunicatorException("Query " + userName + " is failing due to ", errorCode);
            when(communicator.querySingleAttribute(dnFull, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_UID_NUMBER, null)).thenThrow(dsException);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            secManager.getUidNumber();
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.DATA_STORE_CONNECTION_FAILURE, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * test getUidNumber() success single gidNumber=20000
     */
    @Test
    public void testGetUidNumber() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "m2mUser11";
        final String dnFull = "ou=" + "M2MUsers" + getDnPostfix();;
        final int errorCode = LDAP_ERROR_CONNECTION_FAILURE;
        final ArrayList<String> uidNumberList = new ArrayList<String>();
        uidNumberList.add("20000");
        try {
            when(communicator.querySingleAttribute(dnFull, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_UID_NUMBER,
                    null)).thenReturn(uidNumberList);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            int currentUidNumber = secManager.getUidNumber();
            assertEquals(20001,currentUidNumber);
        } catch (final IdentityManagementServiceException e) {
            fail("should not throw this exception:" + e.getMessage());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * test getUidNumber() success single gidNumber=25000
     */
    @Test
    public void testGetUidNumber1() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "m2mUser11";
        final String dnFull = "ou=" + "M2MUsers" + getDnPostfix();;
        final int errorCode = LDAP_ERROR_CONNECTION_FAILURE;
        final ArrayList<String> uidNumberList = new ArrayList<String>();
        uidNumberList.add("25000");
        try {
            when(communicator.querySingleAttribute(dnFull, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_UID_NUMBER,
                    null)).thenReturn(uidNumberList);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }
        try {
            int currentUidNumber = secManager.getUidNumber();
            assertEquals(20000,currentUidNumber);
        } catch (final IdentityManagementServiceException e) {
            fail("should not throw this exception:" + e.getMessage());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * test getM2MPassword method
     */
    @Test
    public void testGetM2MPasswordSuccess() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final CryptographyService cryptographyService = mock(CryptographyService.class);
        cryptographyServiceClient = new CryptographyServiceClient(cryptographyService);
        secManager.cryptoClient = cryptographyServiceClient;
        final String userName = "m2mUser3";
        final String dn = makeUserDN(userName);
        final String decryptedString = "9F/0ZS2kTm82X4eJtm468XhItliRVDxJsloNiHS6I7k=";
        final ArrayList<String> password = new ArrayList<String>();
        password.add(decryptedString);

        try {
            when(communicator.getRandomPassword()).thenReturn("changeit");
            final byte[] encryptedBytesArr = DatatypeConverter.parseBase64Binary("changeit");
            when(cryptographyService.decrypt(DatatypeConverter.parseBase64Binary(decryptedString))).thenReturn(encryptedBytesArr);
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(true);
            when(communicator.querySingleAttribute(dn, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_AUTH_CRED, null)).thenReturn(password);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }

        try {
            secManager.getM2MPassword(userName);
        } catch (final IdentityManagementServiceException e) {
            fail("shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * test getM2MPassword method, assuming that cryptography service does not work properly
     */
    @Test
    public void testGetM2MPasswordCryptoFailure() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final CryptographyService cryptographyService = mock(CryptographyService.class);
        cryptographyServiceClient = new CryptographyServiceClient(cryptographyService);
        secManager.cryptoClient = cryptographyServiceClient;
        final String userName = "m2mUser3";
        final String dn = makeUserDN(userName);
        final String decryptedString = "SHA1$AESCBC128$9F/0ZS2kTm82X4eJtm468XhItliRVDxJsloNiHS6I7k=";
        final ArrayList<String> password = new ArrayList<String>();
        password.add(decryptedString);

        try {
            when(communicator.getRandomPassword()).thenReturn("changeit");
            when(cryptographyService.decrypt(decryptedString.getBytes(CHARSET))).thenReturn(null);
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(true);
            when(communicator.querySingleAttribute(dn, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_AUTH_CRED, null)).thenReturn(password);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }

        try {
            secManager.getM2MPassword(userName);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.UNEXPECTED_ERROR, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * test getM2MPassword method, connection failure
     */
    @Test
    public void testGetM2MPasswordConnectionFailure() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "m2mUser3";
        final String dn = makeUserDN(userName);
        final int errorCode = LDAP_ERROR_CONNECTION_FAILURE;
        try {
            final DSCommunicatorException dsException = new DSCommunicatorException("Query " + userName + " is failing due to ", errorCode);
            when(communicator.querySingleAttribute(dn, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_AUTH_CRED, null)).thenThrow(dsException);
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(true);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }

        try {
            secManager.getM2MPassword(userName);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.DATA_STORE_CONNECTION_FAILURE, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * test getM2MPassword method, assuming that not existing user
     */
    @Test
    public void testGetM2MPasswordFailureDueNotExistingUser() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "m2mUser3";
        final String dn = makeUserDN(userName);
        try {
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(false);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }

        try {
            secManager.getM2MPassword(userName);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.ENTRY_NOT_FOUND, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * test getM2MPassword method, auth password collection not valid
     */
    @Test
    public void testGetM2MPasswordAuthPasswordCollectionInvalid() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final CryptographyService cryptographyService = mock(CryptographyService.class);
        cryptographyServiceClient = new CryptographyServiceClient(cryptographyService);
        secManager.cryptoClient = cryptographyServiceClient;
        final String userName = "m2mUser3";
        final String dn = makeUserDN(userName);
        final String decryptedString = "SHA1$AESCBC128$9F/0ZS2kTm82X4eJtm468XhItliRVDxJsloNiHS6I7k=";
        final ArrayList<String> password = new ArrayList<String>();
        //password.add(decryptedString);

        try {
            when(communicator.getRandomPassword()).thenReturn("changeit");
            when(cryptographyService.decrypt(decryptedString.getBytes(CHARSET))).thenReturn(null);
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(true);
            when(communicator.querySingleAttribute(dn, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_AUTH_CRED, null)).thenReturn(password);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }

        try {
            secManager.getM2MPassword(userName);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.UNEXPECTED_ERROR, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * test getM2MPassword method, password with invalid prefix
     */
    @Test
    public void testGetM2MPasswordPasswordWithInvalidPrefix() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final CryptographyService cryptographyService = mock(CryptographyService.class);
        cryptographyServiceClient = new CryptographyServiceClient(cryptographyService);
        secManager.cryptoClient = cryptographyServiceClient;
        final String userName = "m2mUser3";
        final String dn = makeUserDN(userName);
        final String decryptedString = "SHA1$AESaaaaaa$9F/0ZS2kTm82X4eJtm468XhItliRVDxJsloNiHS6I7k=";
        final ArrayList<String> password = new ArrayList<String>();
        password.add(decryptedString);

        try {
            when(communicator.getRandomPassword()).thenReturn("changeit");
            when(cryptographyService.decrypt(decryptedString.getBytes(CHARSET))).thenReturn(null);
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(true);
            when(communicator.querySingleAttribute(dn, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_AUTH_CRED, null)).thenReturn(password);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }

        try {
            secManager.getM2MPassword(userName);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.UNEXPECTED_ERROR, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * test getM2MPassword method, password with multiple SHA1 prefix
     */
    @Test
    public void testGetM2MPasswordPasswordWithMultipleSHA1Prefix() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final CryptographyService cryptographyService = mock(CryptographyService.class);
        cryptographyServiceClient = new CryptographyServiceClient(cryptographyService);
        secManager.cryptoClient = cryptographyServiceClient;
        final String userName = "m2mUser3";
        final String dn = makeUserDN(userName);
        final String decryptedString = "SHA1$AESaaaaaa$SHA19F/0ZS2kTm82X4eJtm468XhItliRVDxJsloNiHS6I7k=";
        final ArrayList<String> password = new ArrayList<String>();
        password.add(decryptedString);

        try {
            when(communicator.getRandomPassword()).thenReturn("changeit");
            when(cryptographyService.decrypt(decryptedString.getBytes(CHARSET))).thenReturn(null);
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(true);
            when(communicator.querySingleAttribute(dn, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_AUTH_CRED, null)).thenReturn(password);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }

        try {
            secManager.getM2MPassword(userName);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.UNEXPECTED_ERROR, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * test getM2MPassword method, password with invalid  prefix chunk
     */
    @Test
    public void testGetM2MPasswordPasswordWithShortAndInvalidPrefixChunk() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final CryptographyService cryptographyService = mock(CryptographyService.class);
        cryptographyServiceClient = new CryptographyServiceClient(cryptographyService);
        secManager.cryptoClient = cryptographyServiceClient;
        final String userName = "m2mUser3";
        final String dn = makeUserDN(userName);
        final String decryptedString = "SHA1xx";
        final ArrayList<String> password = new ArrayList<String>();
        password.add(decryptedString);

        try {
            when(communicator.getRandomPassword()).thenReturn("changeit");
            when(cryptographyService.decrypt(decryptedString.getBytes(CHARSET))).thenReturn(null);
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(true);
            when(communicator.querySingleAttribute(dn, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_AUTH_CRED, null)).thenReturn(password);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }

        try {
            secManager.getM2MPassword(userName);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.UNEXPECTED_ERROR, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * test getM2MPassword method, password with null LDAP_AUTH_CRED_SCHEME
     */
    @Test
    public void testGetM2MPasswordPasswordWithNullLdapAuthCredScheme() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final CryptographyService cryptographyService = mock(CryptographyService.class);
        cryptographyServiceClient = new CryptographyServiceClient(cryptographyService);
        secManager.cryptoClient = cryptographyServiceClient;
        final String userName = "m2mUser3";
        final String dn = makeUserDN(userName);
        final String decryptedString = "";
        final ArrayList<String> password = new ArrayList<String>();
        password.add(decryptedString);

        try {
            when(communicator.getRandomPassword()).thenReturn("changeit");
            when(cryptographyService.decrypt(decryptedString.getBytes(CHARSET))).thenReturn(null);
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(true);
            when(communicator.querySingleAttribute(dn, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_AUTH_CRED, null)).thenReturn(password);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }

        try {
            secManager.getM2MPassword(userName);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.UNEXPECTED_ERROR, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * test getM2MPassword method, password with double attached ldap Auth Cred Scheme
     */
    @Test
    public void testGetM2MPasswordPasswordWithDoubleAttachedLdapAuthCredScheme() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final CryptographyService cryptographyService = mock(CryptographyService.class);
        cryptographyServiceClient = new CryptographyServiceClient(cryptographyService);
        secManager.cryptoClient = cryptographyServiceClient;
        final String userName = "m2mUser3";
        final String dn = makeUserDN(userName);
        final String decryptedString = "SHA1SHA1";
        final ArrayList<String> password = new ArrayList<String>();
        password.add(decryptedString);

        try {
            when(communicator.getRandomPassword()).thenReturn("changeit");
            when(cryptographyService.decrypt(decryptedString.getBytes(CHARSET))).thenReturn(null);
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(true);
            when(communicator.querySingleAttribute(dn, SearchControls.SUBTREE_SCOPE, IdmConstants.LDAP_AUTH_CRED, null)).thenReturn(password);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }

        try {
            secManager.getM2MPassword(userName);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.UNEXPECTED_ERROR, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * test updateM2MPassword method
     */
    @Test
    public void testUpdateM2MPasswordSuccess() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final CryptographyService cryptographyService = mock(CryptographyService.class);
        cryptographyServiceClient = new CryptographyServiceClient(cryptographyService);
        secManager.cryptoClient = cryptographyServiceClient;
        final String userName = "m2mUser3";
        final String dn = makeUserDN(userName);
        try {
            when(communicator.getRandomPassword()).thenReturn("changeit");
            final byte[] encryptedBytesArr = "changeit".getBytes(CHARSET);
            when(cryptographyService.encrypt(DatatypeConverter.parseBase64Binary("changeit"))).thenReturn(encryptedBytesArr);
            when(communicator.getRandomPassword()).thenReturn("changeit");
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(true);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }

        try {
            secManager.updateM2MPassword(userName);
        } catch (final IdentityManagementServiceException e) {
            fail("shouldn't throw exception : " + e.getMessage());
            e.printStackTrace();
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * test updateM2MPassword method, assuming that cryptography service does not work properly
     */
    @Test
    public void testUpdateM2MPasswordCryptoFailure() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final CryptographyService cryptographyService = mock(CryptographyService.class);
        cryptographyServiceClient = new CryptographyServiceClient(cryptographyService);
        secManager.cryptoClient = cryptographyServiceClient;
        final String userName = "m2mUser3";
        final String dn = makeUserDN(userName);
        try {
            when(communicator.getRandomPassword()).thenReturn("changeit");
            when(cryptographyService.encrypt("changeit".getBytes(CHARSET))).thenReturn(null);
            when(communicator.getRandomPassword()).thenReturn("changeit");
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(true);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }

        try {
            secManager.updateM2MPassword(userName);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.UNEXPECTED_ERROR, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * test updateM2MPassword method, assuming connection failure
     */
    @Test
    public void testUpdateM2MConnectionFailure() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final CryptographyService cryptographyService = mock(CryptographyService.class);
        cryptographyServiceClient = new CryptographyServiceClient(cryptographyService);
        secManager.cryptoClient = cryptographyServiceClient;
        final String userName = "m2mUser3";
        final String dn = makeUserDN(userName);
        final int errorCode = LDAP_ERROR_CONNECTION_FAILURE;
        final String userPassword = "changeit";
        try {
            when(communicator.getRandomPassword()).thenReturn("changeit");
            final byte[] encryptedBytesArr = "changeit".getBytes(CHARSET);
            when(cryptographyService.encrypt(DatatypeConverter.parseBase64Binary("changeit"))).thenReturn(encryptedBytesArr);
            when(communicator.getRandomPassword()).thenReturn("changeit");
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(true);
            final DSCommunicatorException dsException = new DSCommunicatorException("Query " + userName + " is failing due to ", errorCode);
            doThrow(dsException).when(communicator).modifyEntryReplace(dn, IdmConstants.LDAP_USER_CRED, userPassword);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }

        try {
            secManager.updateM2MPassword(userName);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.DATA_STORE_CONNECTION_FAILURE, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * test updateM2MPassword method, assuming that user not existing
     */
    @Test
    public void testUpdateM2MNotExistingUser() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "m2mUser3";
        try {
            when(communicator.getRandomPassword()).thenReturn(null);
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(false);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }

        try {
            secManager.updateM2MPassword(userName);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.ENTRY_NOT_FOUND, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * test updateM2MPassword method, assuming that user password is null
     */
    @Test
    public void testUpdateM2MPasswordNull() {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String userName = "m2mUser3";
        final String dn = makeUserDN(userName);
        try {
            when(communicator.getRandomPassword()).thenReturn(null);
            when(communicator.searchEntry(makeUserDN(userName))).thenReturn(true);
        } catch (final Exception e1) {
            fail("shouldn't throw exception from mock communicator  " + e1.getMessage());
            e1.printStackTrace();
        }

        try {
            secManager.updateM2MPassword(userName);
        } catch (final IdentityManagementServiceException e) {
            assertEquals(IdentityManagementServiceException.Error.UNEXPECTED_ERROR, e.getError());
        } catch (final Exception e) {
            fail("Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testGetComUsers() throws DSCommunicatorException {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        String dn = IdmConstants.COM_GROUP_PREFIX + getDnPostfix();
        ArrayList<String> users = getArrayList("com_user");
        when(communicator.querySingleAttribute(dn, SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_MEMBER_UID, null)).thenReturn(users);
        List<String> comUsers = secManager.getComUsers();
        assertEquals(users, comUsers);
        verify(communicator).querySingleAttribute(dn, SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_MEMBER_UID, null);
    }

    @Test
    public void testGetComUsersThrowException() throws DSCommunicatorException {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        String dn = IdmConstants.COM_GROUP_PREFIX + getDnPostfix();
        String message = "some message";
        DSCommunicatorException exception = new DSCommunicatorException(message, 0);
        when(communicator.querySingleAttribute(dn, SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_MEMBER_UID, null)).thenThrow(exception);
        try {
            secManager.getComUsers();
            fail();
        } catch (Exception e) {
            assertEquals("Problem retrieving COM users, error " + message, e.getMessage());
        }
        verify(communicator).querySingleAttribute(dn, SearchControls.OBJECT_SCOPE, IdmConstants.LDAP_MEMBER_UID, null);
    }

    @Test
    public void testRemoveMemberUidFromComUsersGroup() throws DSCommunicatorException {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        String dn = IdmConstants.COM_GROUP_PREFIX + getDnPostfix();
        String memberUid = "some_uid";

        doNothing().when(communicator).modifyEntryDelete(dn, IdmConstants.LDAP_MEMBER_UID, memberUid);

        secManager.removeMemberUidFromComUsersGroup(memberUid);

        verify(communicator).modifyEntryDelete(dn, IdmConstants.LDAP_MEMBER_UID, memberUid);
    }

    @Test(expected = IdentityManagementServiceException.class)
    public void testRemoveMemberUidFromComUsersGroupThrowException() throws DSCommunicatorException {
        final DSCommunicator communicator = mock(DSCommunicator.class);
        secManager.communicator = communicator;
        final String dn = IdmConstants.COM_GROUP_PREFIX + getDnPostfix();
        final String message = "some message";
        final String memberUid = "some_uid";
        final DSCommunicatorException toBeThrown = new DSCommunicatorException(message, 0);

        doThrow(toBeThrown).when(communicator).modifyEntryDelete(dn, IdmConstants.LDAP_MEMBER_UID, memberUid);

        secManager.removeMemberUidFromComUsersGroup(memberUid);

    }

    private HashMap<String, ArrayList<String>> getAVPairs(final String userName, final String cn, final String homeDir, final String uidNumber,
            final String gidNumber, final String expiryTimestamp) {
        this.logger.error("TEST:UserName:{} homeDir:{} uidNumber: {} ", userName, homeDir, uidNumber);
        final HashMap<String, ArrayList<String>> attrValuePairs = new HashMap<String, ArrayList<String>>();
        final ArrayList<String> userNameList = new ArrayList<String>();
        userNameList.add(userName);
        attrValuePairs.put(IdmConstants.LDAP_UID, userNameList);

        final ArrayList<String> cnList = new ArrayList<String>();
        cnList.add(cn);
        attrValuePairs.put(IdmConstants.LDAP_FULL_NAME, cnList);

        final ArrayList<String> uidNumberList = new ArrayList<String>();
        uidNumberList.add(uidNumber);
        attrValuePairs.put(IdmConstants.LDAP_UID_NUMBER, uidNumberList);

        final ArrayList<String> gidNumberList = new ArrayList<String>();
        gidNumberList.add(gidNumber);
        attrValuePairs.put(IdmConstants.LDAP_GID_NUMBER, gidNumberList);

        final ArrayList<String> homeDirList = new ArrayList<String>();
        homeDirList.add(homeDir);
        attrValuePairs.put(IdmConstants.LDAP_HOME_DIRECTORY, homeDirList);
        if (!expiryTimestamp.isEmpty()) {
            final ArrayList<String> expiryTime = new ArrayList<String>();
            expiryTime.add(expiryTimestamp);
            attrValuePairs.put(IdmConstants.LDAP_ACCOUNT_EXPIRATION_TIME, expiryTime);
        }
        this.logger.error("TEST END:UserName:{} homeDir:{} uidNumber: {} ", userName, homeDir, uidNumber);
        return attrValuePairs;
    }

    private ArrayList<String> getArrayList(final String value) {
        final ArrayList<String> valueList = new ArrayList<String>();
        valueList.add(value);
        return valueList;
    }

    private String makeUserDN(final String name) {
        final StringBuffer userDN = new StringBuffer("uid=" + name);
        userDN.append("," + m2mUserContainer);
        userDN.append(getDnPostfix());
        return userDN.toString();
    }

    private String makeGroupDN(final String name) {
        final StringBuffer groupDN = new StringBuffer("cn=" + name);
        groupDN.append(",ou=Groups");
        groupDN.append(getDnPostfix());
        return groupDN.toString();
    }

    private String getDnPostfix() {
        if (dnPostfix == null) {
            dnPostfix = new StringBuffer(",");
            dnPostfix.append(baseDN);
        }
        return dnPostfix.toString();
    }

    private String getProperAttribute(final String groupName) {
        if (IdmConstants.COM_GROUP_NAME.equals(groupName)) {
            return IdmConstants.LDAP_MEMBER_UID;
        } else {
            return IdmConstants.LDAP_UNIQUE_MEMBER;
        }
    }
}
