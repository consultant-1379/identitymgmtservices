/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa;

public enum ComAALdapPort {

    COMAA_LDAP_TLS_PORT("comaa_ldap_tls_port"),
    COMAA_LDAPS_PORT("comaa_ldaps_port");

    public final String ldapPort;

    ComAALdapPort(String ldapPort) {
        this.ldapPort = ldapPort;
    }

    public String getLdapPort() {
        return ldapPort;
    }
}
