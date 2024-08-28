/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.comaa

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.spock.CdiSpecification

class ConnectionDataTest extends CdiSpecification {

    private static final String PRIMARY_UNDER_TEST = "10.10.10.1"
    private static final String FALLBACK_UNDER_TEST = "10.10.10.2"
    private static final String PRIMARY_IPV6_UNDER_TEST = "2001:0db8:85a3:0000:0000:8a2e:0370:7334"
    private static final String FALLBACK_IPV6_UNDER_TEST = "2001:0db8:85a3:0000:0000:8a2e:0370:7335"
    private static final int LDAP_TLS_PORT_UNDER_TEST = 1636
    private static final int LDAP_PORT_UNDER_TEST = 1389

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def setup() {}

    def 'verify connectionData with parameters getter ' () {
        given:
            LdapAddress ldapAddress = new LdapAddress(PRIMARY_UNDER_TEST, FALLBACK_UNDER_TEST)
            LdapAddress ldapAddressIpv6 = new LdapAddress(PRIMARY_IPV6_UNDER_TEST, FALLBACK_IPV6_UNDER_TEST)
        when:
            ConnectionData connectionData = new ConnectionData(ldapAddress, ldapAddressIpv6, LDAP_TLS_PORT_UNDER_TEST, LDAP_PORT_UNDER_TEST)
        then:
            connectionData.getLdapTlsPort() == LDAP_TLS_PORT_UNDER_TEST &&
                    connectionData.getLdapsPort() == LDAP_PORT_UNDER_TEST &&
                    connectionData.getIpv4AddressData().getPrimary() == PRIMARY_UNDER_TEST &&
                    connectionData.getIpv4AddressData().getFallback() == FALLBACK_UNDER_TEST &&
                    connectionData.getIpv6AddressData().getPrimary() == PRIMARY_IPV6_UNDER_TEST &&
                    connectionData.getIpv6AddressData().getFallback() == FALLBACK_IPV6_UNDER_TEST
    }

    def 'verify connectionData without parameters ' () {
        given:
        when:
            ConnectionData connectionData = new ConnectionData()
        then:
            connectionData !=null
    }
}
