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
import com.ericsson.oss.itpf.security.identitymgmtservices.M2MUser

class LdapAddressTest extends CdiSpecification {

    private static final String PRIMARY_UNDER_TEST = "10.10.10.1"
    private static final String FALLBACK_UNDER_TEST = "10.10.10.2"


    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def setup() {}

    def 'verify ldapAddress with parameters getter ' () {
        given:
            LdapAddress ldapAddress = new LdapAddress(PRIMARY_UNDER_TEST, FALLBACK_UNDER_TEST)
        when:
            String primary = ldapAddress.getPrimary()
            String fallback = ldapAddress.getFallback()
        then:
            primary == PRIMARY_UNDER_TEST &&
                    fallback == FALLBACK_UNDER_TEST
    }

    def 'verify ldapAddress without parameters getter ' () {
        given:
        when:
            LdapAddress ldapAddress = new LdapAddress()
        then:
            ldapAddress !=null
    }
}
