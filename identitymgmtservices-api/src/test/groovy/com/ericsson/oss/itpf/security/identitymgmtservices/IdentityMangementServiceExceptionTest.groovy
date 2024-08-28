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

class IdentityMangementServiceExceptionTest extends CdiSpecification {

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def setup() {}

    def 'verify identityManagementServiceException no args  ' () {
        given:
        when:
            IdentityManagementServiceException identityManagementServiceException = new IdentityManagementServiceException()
        then:
            identityManagementServiceException !=null
        and:
            identityManagementServiceException.getError() == IdentityManagementServiceException.Error.UNEXPECTED_ERROR
    }

    def 'verify identityManagementServiceException fields constructor throwable ' () {
        given:
            String thrMsg = "throwable_message"
            Throwable throwable = new Throwable(thrMsg)
        when:
            IdentityManagementServiceException identityManagementServiceException = new IdentityManagementServiceException(throwable)
        then:
            identityManagementServiceException !=null
        and:
            identityManagementServiceException.getError() == IdentityManagementServiceException.Error.UNEXPECTED_ERROR
        and:
            identityManagementServiceException.getMessage() == "java.lang.Throwable: " + thrMsg
    }

    def 'verify identityManagementServiceException fields constructor msg ' () {
        given:
            String msgExcp = "message"
        when:
            IdentityManagementServiceException identityManagementServiceException = new IdentityManagementServiceException(msgExcp)
        then:
            identityManagementServiceException !=null
        and:
            identityManagementServiceException.getError() == IdentityManagementServiceException.Error.UNEXPECTED_ERROR
        and:
            identityManagementServiceException.getMessage() == msgExcp
    }

    def 'verify identityManagementServiceException fields constructor msg with input error ' () {
        given:
            String msgExcp = "message"
            IdentityManagementServiceException.Error error = IdentityManagementServiceException.Error.ATTRIBUTE_NOT_AVAILABLE
        when:
            IdentityManagementServiceException identityManagementServiceException = new IdentityManagementServiceException(
                msgExcp, error)
        then:
            identityManagementServiceException !=null
        and:
            identityManagementServiceException.getError() == IdentityManagementServiceException.Error.ATTRIBUTE_NOT_AVAILABLE
        and:
            identityManagementServiceException.getMessage() == msgExcp
    }

    def 'verify identityManagementServiceException fields constructor msg with input error and throwable ' () {
        given:
            String msgExcp = "message"
            String thrMsg = "throwable_message"
            IdentityManagementServiceException.Error error = IdentityManagementServiceException.Error.ATTRIBUTE_NOT_AVAILABLE
            Throwable throwable = new Throwable(thrMsg)
        when:
            IdentityManagementServiceException identityManagementServiceException = new IdentityManagementServiceException(
                msgExcp, throwable, error )
        then:
            identityManagementServiceException !=null
        and:
            identityManagementServiceException.getError() == IdentityManagementServiceException.Error.ATTRIBUTE_NOT_AVAILABLE
        and:
            identityManagementServiceException.getMessage() == msgExcp
    }

    def 'verify identityManagementServiceException string check ' () {
        given:
            String prefix = "Unexpected or unknown error encountered: com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementServiceException: "
            String msgExcp = "message"
        when:
            IdentityManagementServiceException identityManagementServiceException = new IdentityManagementServiceException(msgExcp)
        then:
            identityManagementServiceException !=null
        and:
            identityManagementServiceException.getError() == IdentityManagementServiceException.Error.UNEXPECTED_ERROR
        and:
            identityManagementServiceException.toString() == prefix + msgExcp
    }
}
