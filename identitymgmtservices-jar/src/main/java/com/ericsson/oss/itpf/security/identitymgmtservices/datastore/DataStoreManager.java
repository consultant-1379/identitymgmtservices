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

package com.ericsson.oss.itpf.security.identitymgmtservices.datastore;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.LDAPConnectionFactory;
import org.forgerock.opendj.ldap.LDAPOptions;
import org.forgerock.opendj.ldap.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.security.identitymgmtservices.ConfigurationBean;
import com.ericsson.oss.itpf.security.identitymgmtservices.DSCommunicatorException;
import com.ericsson.oss.itpf.security.identitymgmtservices.IdmConstants;
import com.ericsson.oss.services.security.keymgmt.command.RetrieveENMPasswordImpl;

public class DataStoreManager {

    /**
     * Instance of SecurityDataService
     */
    static RetrieveENMPasswordImpl retrieveENMPassword = new RetrieveENMPasswordImpl();

    private static Connection connection = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataStoreManager.class);

    public static synchronized Connection getAdminInstance() throws DSCommunicatorException {
        if (connection != null) {
            if (connection.isValid()) {
                return connection;
            }
            closeConnection();
        }
        return getNewAdminInstance();
    }

    private DataStoreManager() {
    }

    private static Connection tryAndGetConnection(final String hostName, final int port, final String userDN, final String userPwd,
            final boolean useSSL, final boolean useStartTLS) throws DSCommunicatorException {
        try {
            LOGGER.debug("Calling getConnect(): hostName=[{}] , port=[{}] , userDN=[{}] , useSSL=[{}]", hostName, port, userDN, useSSL);
            connection = getConnect(hostName, port, userDN, userPwd, useSSL, useStartTLS);
        } catch (final DSCommunicatorException e) {
            LOGGER.error("Problem instantiating DatastoreService for user [{}] due to error : {}", userDN, e.getMessage());
            closeConnection();
            throw e;
        }
        if (connection == null) {
            throw new DSCommunicatorException("Failed to bind to ldap host " + hostName + " port:" + port,
                    IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
        }
        return connection;
    }

    public static synchronized Connection getNewAdminInstance() throws DSCommunicatorException {

        final int port = Integer.parseInt(ConfigurationBean.get(IdmConstants.COM_INF_LDAP_PORT_PROPERTY));
        final String hostList = ConfigurationBean.get(IdmConstants.COM_INF_LDAP_HOST_PROPERTY);
        final String sslEnabledString = ConfigurationBean.get(IdmConstants.COM_INF_LDAP_SSL_ENABLED_PROPERTY, "true");
        final boolean useSSL = Boolean.parseBoolean(sslEnabledString.trim());

        String userDN = ConfigurationBean.get(IdmConstants.LDAP_ADMIN_CN_PROPERTY);
        userDN = userDN.replaceAll("\"", "");
        userDN = userDN.replaceAll("\'", "");

        final String userPwd = retrieveENMPassword.retrieveLdapAdminPassword();
        final boolean useStartTLS = false;

        LOGGER.info("getNewAdminInstance(): hostList=[{}] , port=[{}] , userDN=[{}] , useSSL=[{}]", hostList, port, userDN, useSSL);
        // Split up the hostList string by removing whitespace characters
        final List<String> hosts = Arrays.asList(hostList.split("\\s+"));

        final Iterator<String> hostItr = hosts.iterator();
        while (hostItr.hasNext()) {
            final String hostName = hostItr.next();
            try {
                connection = tryAndGetConnection(hostName, port, userDN, userPwd, useSSL, useStartTLS);
                if (connection != null) {
                    LOGGER.info("Connect to hostName:{} port:{}.", hostName, port);
                    break;
                }
            } catch (final DSCommunicatorException e) {
                if (hostItr.hasNext()) {
                    LOGGER.info("Trying to bind the next available host... ");
                } else {
                    throw e;
                }
            } catch (final Exception e) {
                LOGGER.error("Failed to bind to hostName [{}] , got exception [{}] .", hostName, e.getMessage());
                if (hostItr.hasNext()) {
                    LOGGER.info("Trying to bind the next available host... ");
                } else {
                    LOGGER.error("Tryed all the ldap hosts listed. Failed to bind to hostName:{} .", hostName);
                    throw new DSCommunicatorException("Exception:" + e.getClass() + " occurred with error " + e.getMessage(),
                            IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
                }
            }
        }
        return connection;
    }

    /**
     * Get cloud property
     * @return enmOnCloud flag
     */
    public static boolean enmOnCloud() {
        boolean ret = false;
        final String cloudProperty = ConfigurationBean.get(IdmConstants.LDAP_CLOUD_PROPERTY);
        if ((cloudProperty != null) && (cloudProperty.equalsIgnoreCase("true"))) {
            ret = true;
        }
        LOGGER.info("enm on cloud property = {} ret = {}",cloudProperty, ret);
        return ret;
    }

    /**
     * Authenticate over LDAP.
     */
    @SuppressWarnings("resource")
    private static Connection getConnect(final String ldapHost, final int ldapPort, final String bindDN, final String bindPassword,
                                         final boolean useSSL, final boolean useStartTLS) throws DSCommunicatorException {
        Connection conn;
        LDAPConnectionFactory factory = null;
        try {
            if (useSSL) {
                LOGGER.debug("Secure connection to host:{} port: {} .",ldapHost, ldapPort);
                factory = new LDAPConnectionFactory(ldapHost, ldapPort, getLDAPOptions(useStartTLS));
            } else {
                LOGGER.debug("Non-Secure connection to host:{} port: {} .",ldapHost, ldapPort);
                factory = new LDAPConnectionFactory(ldapHost, ldapPort);
            }
            conn = factory.getConnection();
            if (conn != null) {
                conn.bind(bindDN, bindPassword.toCharArray());
                LOGGER.info("Authenticated as {} .",bindDN);
            }
        } catch (final Exception e) {
            /**
             * ErrorResultException : - If the result code indicates that the request failed for some reason
             * UnsupportedOperationException : - If this connection does not support bind operations
             * IllegalStateException : - If this connection has already been closed, i.e. if isClosed() == true
             * NullPointerException : - If name or password was null
             */
            LOGGER.error("Connection to host {} port {} for user {} is failing due to error {}, exception is {}", ldapHost, ldapPort, bindDN,
                    e.getMessage(), e.getClass());
            throw new DSCommunicatorException(e.getMessage(), IdmConstants.LDAP_ERROR_CONNECTION_FAILURE);
        } finally {
            if (factory != null){
                factory.close();
            }
        }

        return conn;
    }

    private static void closeConnection() {
        if (connection != null) {
            LOGGER.debug("closeConnection , closing ldap connection.");
            connection.close();
            connection = null;
        }
    }

    /**
     * For StartTLS and SSL the connection factory needs SSL context options. In the general case, a trust manager in the SSL context serves to check
     * server certificates, and a key manager handles client keys when the server checks certificates from our client.
     *
     * OpenDJ directory server lets you install by default with a self-signed certificate that is not in the system trust store.
     */
    private static LDAPOptions getLDAPOptions(final boolean useStartTLS) throws GeneralSecurityException {
        final LDAPOptions lo = new LDAPOptions();
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, null, null);
        final String[] supportedCiphers = sslContext.getDefaultSSLParameters().getCipherSuites();
        
        lo.setSSLContext(sslContext);
        lo.setUseStartTLS(useStartTLS);
        lo.addEnabledProtocol("TLSv1.2");
        lo.addEnabledCipherSuite(supportedCiphers);
        return lo;
    }

}
