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
package com.ericsson.oss.itpf.security.identitymgmtservices

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.spock.CdiSpecification

class DSCommunicatorExceptionTest extends CdiSpecification {

    private static final int LDAP_ERROR_UNDER_TEST = 1003

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def setup() {}

    def 'verify DSCommunicatorException with field  ' () {
        given:
            String msgExcp = "dsMsgExcpTest"
        when:
            DSCommunicatorException dsCommExcp = new DSCommunicatorException(msgExcp, LDAP_ERROR_UNDER_TEST)
        then:
            dsCommExcp !=null
        and:
            dsCommExcp.getMessage() == msgExcp
        and:
            dsCommExcp.ldapErrorCode() == LDAP_ERROR_UNDER_TEST
    }

    def 'verify DSCommunicatorException with field and throwable  ' () {
        given:
            String thrMsg = "throwable_message"
            Throwable throwable = new Throwable(thrMsg)

            String msgExcp = "dsMsgExcpTest"
        when:
            DSCommunicatorException dsCommExcp = new DSCommunicatorException(msgExcp, LDAP_ERROR_UNDER_TEST, throwable)
        then:
            dsCommExcp !=null
        and:
            dsCommExcp.getMessage() == msgExcp
        and:
            dsCommExcp.ldapErrorCode() == LDAP_ERROR_UNDER_TEST
    }
}
