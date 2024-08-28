/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.comaa;

/**
 *  Now used by :
 *      identitymgmt-services
 *      node-security
 *      ap-workflow-ecim
 */
import java.io.Serializable;

public class ConnectionData implements Serializable {

    private static final long serialVersionUID = -4013477263062699057L;

    private LdapAddress ipv4AddressData;
    private LdapAddress ipv6AddressData;
    int ldapTlsPort;
    int ldapsPort;

    public ConnectionData(final LdapAddress ipv4AddressData, final LdapAddress ipv6AddressData, final int ldapTlsPort, final int ldapsPort) {
        super();
        this.ipv4AddressData = ipv4AddressData;
        this.ipv6AddressData = ipv6AddressData;
        this.ldapTlsPort = ldapTlsPort;
        this.ldapsPort = ldapsPort;
    }

    public ConnectionData() {
        super();
    }

    /**
     * This method returns pair (primary and fallback) of IPv4 addresses. This addresses can be used to communicate with
     * com-aa-service
     * 
     * @return - pair (primary and fallback) of IPv4 addresses.
     */
    public LdapAddress getIpv4AddressData() {
        return ipv4AddressData;
    }

    /**
     * This method returns pair (primary and fallback) of IPv6 addresses. This addresses can be used to communicate with
     * com-aa-service
     * 
     * @return - pair (primary and fallback) of IPv6 addresses.
     */
    public LdapAddress getIpv6AddressData() {
        return ipv6AddressData;
    }

    /**
     * This method returns port for LDAP (startTLS) connections
     * 
     * @return - LDAPs port or -1 when it is not defined
     */
    public int getLdapTlsPort() {
        return ldapTlsPort;
    }

    /**
     * This method returns port for LDAPS connections
     * 
     * @return - LDAPS port or -1 when it is not defined
     */
    public int getLdapsPort() {
        return ldapsPort;
    }

}