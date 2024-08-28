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

package com.ericsson.oss.itpf.security.identitymgmtservices;

import com.ericsson.oss.itpf.sdk.core.annotation.EService;
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus;

import javax.ejb.Remote;
import java.util.List;

/**
 * This interface defines the Identity Management Service APIs in the TOR/ENM product. The IMS is an internal service intended for the
 * machine-to-machine (M2M) Identity management.
 * <p>
 * The IdentityManagementServiceException is intended to wrap all internal exceptions into a single exception for use by calling parties.
 *
 * @author TOR/ENM Identity Management/Access Control
 * @version 1.0
 * <p>
 * Interface used now by:
 * identitymgmt-services
 * ecim-pm-handler-code
 * ap-workflow-ecim
 * smrs-service
 * node-security
 * ap-workflow-macro
 * netlog-service
 * network-model-retriever-tool
 * pmul-service
 * nodecmd-job-service
 * nodecmd-admin-service
 * mini-link-outdoor-shm-handlers
 * stn-cm-handlers
 * stn-pm-handlers
 * ap-service-core
 */
@EService
@Remote
public interface IdentityManagementService {

    /**
     * This method creates an M2M user account to be used for machine-to-machine communications. Accounts with a positive validity period specified
     * will expire in the specified number of days but will remain present in an expired status, and may need to be cleaned-up before being
     * re-created.
     *
     * @param userName  - String representing the userName to be created.
     * @param groupName - String representing the name of the group that the user belongs to.
     * @param homeDir   - String representing the user home directory absolute path.
     * @param validDays - integer representing the account lifetime in days before it expires. - set greater than zero for a "temporary" M2M user account that expires in N
     *                  days - set less or equal to zero for a "permanent" (non-expiring) M2M user account
     * @return - M2M user instance upon successful creation of user.
     * @throws IdentityManagementServiceException - raised for any errors, failures and invalid cases (including "User Already Exists" cases).
     * @deprecated Replaced by  {@link #createM2MUserPassword(String, String, String, int)}
     */
    @Deprecated
    M2MUser createM2MUser(String userName, String groupName, String homeDir, int validDays);

    /**
     * This method creates an M2M  account to be used for machine-to-machine communications.
     * The return object includes the user password. Accounts with a positive validity period specified
     * will expire in the specified number of days but will remain present in an expired status, and may need to be cleaned-up before being
     * re-created.
     *
     * @param userName  - String representing the userName to be created.
     * @param groupName - String representing the name of the group that the user belongs to.
     * @param homeDir   - String representing the user home directory absolute path.
     * @param validDays - integer representing the account lifetime in days before it expires. - set greater than zero for a "temporary" M2M user account that expires in N
     *                  days - set less or equal to zero for a "permanent" (non-expiring) M2M user account
     * @return - M2M user instance with password upon successful creation of user.
     * @throws IdentityManagementServiceException - raised for any errors, failures and invalid cases (including "User Already Exists" cases).
     */
    M2MUserPassword createM2MUserPassword(String userName, String groupName, String homeDir, int validDays);

    /**
     * This method deletes an existing M2M user account from the active identity management repository. Note that the actual removal of associated
     * files and directories from the underlying filesystem is the responsibility of the calling application.
     *
     * @param userName - String representing the userName of the account to be deleted.
     * @return - boolean "true" if the user exists and deleted successfully ; "false" indicating that the user does not exist.
     * @throws IdentityManagementServiceException - raised for any errors, failures and invalid cases (including "User Not Found" cases).
     */
    boolean deleteM2MUser(String userName);

    /**
     * This method returns an existing M2M user details.
     *
     * @param userName - String representing the name of the user to be retrieved.
     * @return - M2MUser instance which contains all the user data except the password
     * @throws IdentityManagementServiceException - raised for any errors, failures and invalid cases (including "User Not Found" cases).
     */
    M2MUser getM2MUser(String userName);

    /**
     * This methods verifies if an M2M user does exist in the LDAP server.
     *
     * @param userName - String representing the name of the user to be verified.
     * @return - boolean "true" if the user exists; "false" indicating that the user does not exist.
     * @throws IdentityManagementServiceException - raised for any errors, failures and invalid cases.
     */
    boolean isExistingM2MUser(String userName);

    /**
     * This method returns the selected M2M user account password in plaintext.
     *
     * @param userName - String the M2M user name whose password is returned.
     * @return - char[] the password in plaintext.
     * @throws IdentityManagementServiceException - raised for any errors, failures and invalid cases (including "User Not Found" cases).
     */
    char[] getM2MPassword(String userName);

    /**
     * This method updates the password for an existing M2M user.
     *
     * @param userName - String: The M2M user name whose password is to be updated.
     * @return - char[] the updated password in plaintext.
     * @throws IdentityManagementServiceException - raised for any errors, failures and invalid cases (including "User Not Found" cases).
     */
    char[] updateM2MPassword(String userName);

    /**
     * @return - String list of all Target Group names.
     * @throws IdentityManagementServiceException - raised for any errors, failures and invalid cases.
     * @deprecated Replaced by GIM application
     * This method returns the list of currently defined/valid Target Groups.
     */
    @Deprecated
    List<String> getAllTargetGroups();

    /**
     * @return - String name of the "default" Target Group.
     * @throws IdentityManagementServiceException - raised for any errors, failures and invalid cases.
     * @deprecated Replaced by GIM application
     * This method returns the name of the "default" Target Group.
     */
    @Deprecated
    String getDefaultTargetGroup();

    /**
     * @param targetGroups - List of String representing Target Group names in question.
     * @return - List of String: any invalid Target Group names found.
     * @throws IdentityManagementServiceException - raised for any errors, failures and invalid cases.
     * @deprecated Replaced by GIM application
     * This method returns the subset of given Target Group names which are currently not known/invalid.
     * If all Target Group names are valid, then an empty list is returned.
     */
    @Deprecated
    List<String> validateTargetGroups(List<String> targetGroups);

    /**
     * This method creates a proxy LDAP account
     *
     * @return - DN and password (as object ProxyAgentAccountData).
     * @throws IdentityManagementServiceException - raised for any errors, failures and invalid cases.
     */
    ProxyAgentAccountData createProxyAgentAccount();

    /**
     * This method deletes an existing proxy LDAP account from the active identity management repository.
     * Note that the actual removal of associated files and directories from the underlying filesystem
     * is the responsibility of the calling application.
     *
     * @param userDN - String representing the user DN of the account to be deleted.
     * @return - boolean "true" if the user exists and deleted successfully ; "false" indicating that the user does not exist.
     * @throws IdentityManagementServiceException - raised for any errors, failures and invalid cases.
     */
    boolean deleteProxyAgentAccount(String userDN);

    /**
     * This method get all proxy account users stored on ENM internal ldap server (aka Opendj).
     *
     * @param isLegacy  - If TRUE : retrieves ONLY the proxy account users stored into old subtree
     *                    (ou=proxyagent,ou=com, <basedn>).
     *                  - If FALSE: retrieves ALL proxy account users on ALL proxy account subtrees
     *                      (ou=proxyagent,ou=com, <basedn> and ou=proxyagentlockable,ou=com, <basedn>).
     * @param isSummary - If TRUE : returns ONLY the counters structure.
     *                  - If FALSE: returns the global counters and
     *                              the proxy accounts users (it depends on isLegacy parameter).
     * @return - ProxyAgentAccountGetData it contains the details of each proxy account user and globalcounters
     * @throws IdentityManagementServiceException - raised for any errors, failures and invalid cases.
     */
    ProxyAgentAccountGetData getProxyAgentAccount(Boolean isLegacy, Boolean isSummary);

    /**
     * This method get the proxy user account users stored on ENM internal ldap server (aka Opendj)
     * with a defined admin status.
     *
     * @param adminStatus - the administrative status (ENABLED or DISABLED) used as filter
     *                      to get the proxy account users.
     * @param isLegacy  - If TRUE : retrieves ONLY the proxy account users stored into old subtree
     *                              (ou=proxyagent,ou=com, <basedn>).
     *                  - If FALSE: retrieves ALL proxy account users on ALL proxy account subtrees
     *                              (ou=proxyagent,ou=com, <basedn> and ou=proxyagentlockable,ou=com, <basedn>).
     * @param isSummary - If TRUE : returns ONLY the counters structure.
     *                  - If FALSE: returns the global counters and
     *                              the proxy accounts users (it depends on isLegacy parameter).
     * @return - ProxyAgentAccountGetData it contains the details of each proxy account user and globalcounters.
     * @throws IdentityManagementServiceException - raised for any errors, failures and invalid cases.
     */
    ProxyAgentAccountGetData getProxyAgentAccountByAdminStatus(ProxyAgentAccountAdminStatus adminStatus,
                                                               Boolean isLegacy,
                                                               Boolean isSummary);

    /**
     * This method get the proxy user account users stored on ENM internal ldap server (aka Opendj)
     * with a defined admin status.
     *
     * @param inactivityPeriod - the inactive period in msec used as filter
     *      *                    to get the proxy account users.
     *                           Only proxy account users inactive before the inactivityPeriod
     * @param isLegacy  - If TRUE : retrieves ONLY the proxy account users stored into old subtree
     *                              (ou=proxyagent,ou=com, <basedn>).
     *                  - If FALSE: retrieves ALL proxy account users on ALL proxy account subtrees
     *                              (ou=proxyagent,ou=com, <basedn> and ou=proxyagentlockable,ou=com, <basedn>).
     * @param isSummary - If TRUE : returns ONLY the counters structure
     *                  - If FALSE: returns the global counters and
     *                              the proxy accounts users (it depends on isLegacy parameter).
     * @return - ProxyAgentAccountGetData it contains the details of each proxy account user and globalcounters.
     * @throws IdentityManagementServiceException - raised for any errors, failures and invalid cases.
     */
    ProxyAgentAccountGetData getProxyAgentAccountByInactivityPeriod(Long inactivityPeriod,
                                                                    Boolean isLegacy,
                                                                    Boolean isSummary);

    /**
     * This method get the proxy user account users stored on ENM internal ldap server (aka Opendj)
     * with a defined admin status.
     *
     * @param userDn - String representing the user DN of the account to be retrieved.
     * @throws IdentityManagementServiceException - raised for any errors, failures and invalid cases.
     */
    ProxyAgentAccountDetails getProxyAgentAccountDetails(String userDn);

    /**
     * This method get the proxy user account users stored on ENM internal ldap server (aka Opendj)
     * with a defined admin status.
     *
     * @param userDn - String representing the user DN of the account to be retrieved.
     * @param adminStatus - the administrative status (ENABLED or DISABLED) to be updated
     *                       for the specified proxy account user.
     * @throws IdentityManagementServiceException - raised for any errors, failures and invalid cases.
     */
    Boolean updateProxyAgentAccountAdminStatus(String userDn,
                                            ProxyAgentAccountAdminStatus adminStatus);

}
