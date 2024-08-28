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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


public class ConfigurationBean {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationBean.class);
    static ConfigurationBean instance = new ConfigurationBean();
    private static final String LDAP_LOCALHOST_NAME = "ldap-local";
    private static final String LDAP_REMOTEHOST_NAME = "ldap-remote";

    private Properties properties;

    /**
     * Search for the property with the specified key in this properties list. The method returns null if the key is not found
     * 
     * @param key
     *            - the property key.
     * @return the value in this property list with the specified key value
     */
    public static String get(final String key) {
        return instance.getProperty(key);
    }

    /**
     * Search for the property with the specified key in this properties list.The method returns the default value if the key is not found.
     * 
     * @param key
     *            - the property key.
     * @param defaultValue
     *            - a default value
     * @return the value in this property list with the specified key value
     */
    public static String get(final String key, final String defaultValue) {
        return instance.getProperty(key, defaultValue);
    }

    /**
     * Creates a properties list from the file /ericsson/tor/data/global.propertie. This default filename can be change by setting the system property
     * "datastore.java.properties".
     */

    ConfigurationBean() {
        final String ldapRemoteHostname = System.getProperty(IdmConstants.HOSTNAME_LDAP_REMOTE_PROPERTY, LDAP_REMOTEHOST_NAME);
        final String ldapLocalHostname = System.getProperty(IdmConstants.HOSTNAME_LDAP_LOCAL_PROPERTY, LDAP_LOCALHOST_NAME);
        final String[] addrLdapRemote = getInetAddresses(ldapRemoteHostname);
        final String[] addrLdapLocal = getInetAddresses(ldapLocalHostname);

        Set<String> ldapset = new HashSet<>(Arrays.asList(addrLdapRemote));
        ldapset.addAll(Arrays.asList(addrLdapLocal));
        List<String> ldapSorted = new ArrayList<>(ldapset);
        Collections.sort(ldapSorted);
        String hostsBuffer = String.join(" ", ldapSorted);

        try {
            properties = new Properties();
            final String filename = System.getProperty(IdmConstants.CONFIGURATION_PROPERTY, "/ericsson/tor/data/global.properties");
            properties.load(new FileInputStream(filename));
            if (hostsBuffer.length() > 0) {
                properties.setProperty(IdmConstants.COM_INF_LDAP_HOST_PROPERTY, hostsBuffer.toString());
            }
        } catch (final Exception e) {
            logger.error("ConfigurationBean generic excp",e);
        }
    }

    private String[] getInetAddresses(final String hostname) {
        String[] arStr = {};
        try {
            return Arrays.stream(InetAddress.getAllByName(hostname)).map(InetAddress::getHostAddress).toArray(String[]::new);
        } catch (final UnknownHostException e) {
            logger.error("getInetAddresses excp", e);
        }
        return arStr;
    }

    /**
     *
     */
    private String getProperty(final String key) {
        return properties.getProperty(key);
    }

    /*
     *
     */
    private String getProperty(final String key, final String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
