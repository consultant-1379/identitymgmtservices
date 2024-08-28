/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2021
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.comaa.dto;

import java.io.Serializable;

public class ComAAInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    String ipv4AddressPrimary;
    String ipv4AddressFallback;
    String ipv6AddressPrimary;
    String ipv6AddressFallback;
    int ldapTlsPort;
    int ldapsPort;

    public ComAAInfoDto() {}

    public ComAAInfoDto(String ipv4AddressPrimary, String ipv4AddressFallback,
                        String ipv6AddressPrimary, String ipv6AddressFallback,
                        int ldapTlsPort, int ldapsPort) {
        this.ipv4AddressPrimary = ipv4AddressPrimary;
        this.ipv4AddressFallback = ipv4AddressFallback;
        this.ipv6AddressPrimary = ipv6AddressPrimary;
        this.ipv6AddressFallback = ipv6AddressFallback;
        this.ldapTlsPort = ldapTlsPort;
        this.ldapsPort = ldapsPort;
    }

    public String getIpv4AddressPrimary() {
        return ipv4AddressPrimary;
    }

    public void setIpv4AddressPrimary(String ipv4AddressPrimary) {
        this.ipv4AddressPrimary = ipv4AddressPrimary;
    }

    public String getIpv4AddressFallback() {
        return ipv4AddressFallback;
    }

    public void setIpv4AddressFallback(String ipv4AddressFallback) {
        this.ipv4AddressFallback = ipv4AddressFallback;
    }

    public String getIpv6AddressPrimary() {
        return ipv6AddressPrimary;
    }

    public void setIpv6AddressPrimary(String ipv6AddressPrimary) {
        this.ipv6AddressPrimary = ipv6AddressPrimary;
    }

    public String getIpv6AddressFallback() {
        return ipv6AddressFallback;
    }

    public void setIpv6AddressFallback(String ipv6AddressFallback) {
        this.ipv6AddressFallback = ipv6AddressFallback;
    }

    public int getLdapTlsPort() {
        return ldapTlsPort;
    }

    public void setLdapTlsPort(int ldapTlsPort) {
        this.ldapTlsPort = ldapTlsPort;
    }

    public int getLdapsPort() {
        return ldapsPort;
    }

    public void setLdapsPort(int ldapsPort) {
        this.ldapsPort = ldapsPort;
    }
}
