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

public class IdmConstants {
    public static final int MIN_UID_NUMBER_PEOPLE = 5000;
    public static final int MAX_UID_NUMBER_LDAP = 65535;
    public static final int MIN_UID_NUMBER = 20000;
    public static final int MAX_UID_NUMBER = 40999;
    public static final String LDAP_UID = "uid";
    public static final String LDAP_FULL_NAME = "cn";
    public static final String LDAP_LAST_NAME = "sn";
    public static final String LDAP_UID_NUMBER = "uidNumber";
    public static final String LDAP_GID_NUMBER = "gidNumber";
    public static final String LDAP_HOME_DIRECTORY = "homeDirectory";
    public static final String LDAP_UNIQUE_MEMBER = "uniqueMember";
    public static final String LDAP_USER_CRED = "userPassword";
    public static final String LDAP_AUTH_CRED = "authPassword";
    public static final String LDAP_CONFIG_CN = "cn=config";
    public static final String LDAP_ISMEMBEROF = "isMemberOf";
    public static final String LDAP_OBJECTCLASS = "objectClass";
    public static final String LDAP_MEMBER_UID = "memberUid";
    public static final String LDAP_POSIXACCOUNT = "posixAccount";
    public static final String LDAP_SHADOWACCOUNT = "shadowAccount";
    public static final String LDAP_LOGIN_SHELL = "loginShell";
    public static final String LDAP_NO_LOGIN_SHELL = "/sbin/nologin";
    public static final String LDAP_PROXY_ACCOUNT_PREFIX = "ProxyAccount_";
    //A.P.: needs to be updated by a method that doesn't depend on the hardcoded value
    public static final String LDAP_DEFAULTTARGETGROUP = "NE_ACCESS";
    public static final String LDAP_TARGET_GROUPS_NAME = "TargetGroups";
    public static final String LDAP_AUTH_CRED_SCHEME = "SHA1";
    public static final String CIPHER_PREFIX_AES_CBC128 = "$AESCBC128$";
    public static final int LDAP_ERROR_NO_SUCH_ATTRIBUTE = 16;
    public static final int LDAP_ERROR_INVALID_CREDENTIAL = 49;
    public static final int LDAP_ERROR_NO_SUCH_ENTRY = 32;
    public static final int LDAP_ERROR_ATTRIBUTE_OR_VALUE_EXIST = 20;
    public static final int LDAP_ERROR_CONNECTION_FAILURE = 91;
    public static final int LDAP_OPERATION_SUCCESS = 0;
    public static final int IDMS_UNEXPECTED_ERROR = 999;
    public static final String LDAP_ORGANISATIONALUNIT = "ou";

    public static final String LDAP_ACCOUNT_EXPIRATION_TIME = "ds-pwp-account-expiration-time";
    public static final String LDAP_CFG_CRED_FORMAT = "ds-cfg-password-format";
    public static final String LDAP_CFG_CRED_CHARACTER_SET = "ds-cfg-password-character-set";
    public static final String LDAP_CRED_GENERATOR_FILTER = "(objectclass=*password-generator)";
    public static final String LDAP_CRED_RESET = "pwdReset";

    public static final String COM_INF_LDAP_PORT_PROPERTY = "COM_INF_LDAP_PORT";
    public static final String COM_INF_LDAP_HOST_PROPERTY = "COM_INF_LDAP_HOST";
    public static final String COM_INF_LDAP_ROOT_SUFFIX_PROPERTY = "COM_INF_LDAP_ROOT_SUFFIX";
    public static final String COM_INF_LDAP_SSL_ENABLED_PROPERTY = "COM_INF_LDAP_SSL_ENABLED";
    public static final String COM_INF_LDAP_M2M_USER_CONTAINER_PROPERTY = "COM_INF_LDAP_M2M_USER_CONTAINER";
    public static final String COM_INF_LDAP_PROXY_USER_CONTAINER_PROPERTY = "COM_INF_LDAP_PROXY_USER_CONTAINER";

    public static final String LDAP_ADMIN_CN_PROPERTY = "LDAP_ADMIN_CN";
    public static final String LDAP_ADMIN_CRED_PROPERTY = "LDAP_ADMIN_PASSWORD";
    public static final String LDAP_CLOUD_PROPERTY = "DDC_ON_CLOUD";

    public static final String CONFIGURATION_PROPERTY = "configuration.java.properties";
    public static final String HOSTNAME_LDAP_LOCAL_PROPERTY = "configuration.idenmgmt.hostname.ldap.local.property";
    public static final String HOSTNAME_LDAP_REMOTE_PROPERTY = "configuration.idenmgmt.hostname.ldap.remote.property";

    // Security Event Log
    public static final String SECURITY_EVENT_PROXY_USER_CREATION = "IDM_MGMT_SERVICES.PROXY_USER.CREATION";
    public static final String SECURITY_EVENT_PROXY_USER_DELETION = "IDM_MGMT_SERVICES.PROXY_USER.DELETION";
    public static final String SECURITY_EVENT_PROXY_USER_UPDATE = "IDM_MGMT_SERVICES.PROXY_USER.UPDATING";
    public static final String SECURITY_EVENT_PROXY_USER_GET_SINGLE_ENTRY = "IDM_MGMT_SERVICES.PROXY_USER.GET.SINGLE.ENTRY";
    public static final String SECURITY_EVENT_PROXY_USER_GLOBAL_COUNT_PER_SUBTREE = "IDM_MGMT_SERVICES.PROXY_USER.GLOBAL_COUNT_PER_SUBTREE";

    public static final String SECURITY_EVENT_PROXY_USER_GET_ENTRIES = "IDM_MGMT_SERVICES.PROXY_USER.GET.ENTRIES";

    public static final String SECURITY_EVENT_M2M_USER_CREATION = "IDM_MGMT_SERVICES.M2M_USER.CREATION";
    public static final String SECURITY_EVENT_M2M_USER_DELETION = "IDM_MGMT_SERVICES.M2M_USER.DELETION";
    public static final String SECURITY_EVENT_M2M_USER_CRED_READ = "IDM_MGMT_SERVICES.M2M_USER.PWD_READ";
    public static final String SECURITY_EVENT_COM_GROUP_MEMBERS_READ = "IDM_MGMT_SERVICES.COM_GROUP.MEMBERS_READ";
    public static final String SECURITY_EVENT_M2M_USER_CRED_UPDATE = "IDM_MGMT_SERVICES.M2M_USER.PWD_UPDATE";
    public static final String SECURITY_EVENT_POSIX_ATTRIBUTES_ADD = "IDM_MGMT_SERVICES.POSIX_ATTRIBUTES_ADD";
    public static final String SECURITY_EVENT_POSIX_ATTRIBUTES_REMOVE = "IDM_MGMT_SERVICES.POSIX_ATTRIBUTES_REMOVE";
    public static final String SECURITY_EVENT_TARGET_SYSTEM = "SecurityRepository";
    public static final String IDM_SERVICES_DISTINGUISHED_NAME = "IdentityManagementServices";
    public static final String SECURITY_EVENT_STATUS_SUCCESS = "SUCCESS";
    public static final String SECURITY_EVENT_STATUS_FAILURE = "FAILURE";

    public static final String COM_GROUP_NAME = "com_users";
    public static final String COM_GROUP_PREFIX = "cn=" + COM_GROUP_NAME + ",ou=Groups";
    public static final String COM_GROUP_ID = "609";

    //null posix group number
    public static final String LDAP_NULL_POSIX_GROUP_NUMBER = "2147483647";

    //null home directory
    public static final String LDAP_NULL_HOME_DIRECTORY = "/dev/null";

    // distributed locks
    public static final String PROXY_LOCK_NAME = "proxyAccountCreationLock";
    public static final String M2M_LOCK_NAME = "m2mAccountCreationLock";

    // proxy account password
    public static final int PROXY_ACCOUNT_DEFAULT_PASSWORD_LENGTH = 24;
    public static final int PROXY_ACCOUNT_MIN_PASSWORD_LENGTH = 8;
    public static final int PROXY_ACCOUNT_MAX_PASSWORD_LENGTH = 64;

    private IdmConstants() {
    }
}
