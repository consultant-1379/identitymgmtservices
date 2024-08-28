package com.ericsson.oss.itpf.security.identitymgmtservices.ejb.mappers

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementServiceException
import com.ericsson.oss.itpf.security.identitymgmtservices.PosixServiceException
import com.ericsson.oss.services.security.genericidentity.commons.exception.exceptions.NotAuthorizedException
import com.ericsson.oss.services.security.genericidentity.commons.model.rest.HttpStatusCode

import javax.ejb.EJBTransactionRolledbackException
import javax.ws.rs.core.Response

class IdmsGenericExceptionMapperTest extends CdiSpecification {

    def "build toResponse for EJBTransactionRolledbackException"() {
        given:
            def exception = new EJBTransactionRolledbackException()
        when:
            def handler = new IdmsGenericExceptionMapper.EJBTransactionRolledbackExceptionHandler()
            Response response = handler.toResponse(exception)
        then:
            response != null
        and:
            response.getStatus() == HttpStatusCode.UNPROCESSABLE_ENTITY.getStatusCode()
        and:
            response.getEntity() != null
    }

    def "build toResponse for IdentityManagementServiceException"() {
        given:
            def exception = new IdentityManagementServiceException()
        when:
            def handler = new IdmsGenericExceptionMapper.IdentityManagementServiceExceptionHandler()
            Response response = handler.toResponse(exception)
        then:
            response != null
        and:
            response.getStatus() == HttpStatusCode.UNPROCESSABLE_ENTITY.getStatusCode()
        and:
            response.getEntity() != null
    }

    def "build toResponse for PosixServiceException"() {
        given:
            def identityManagementServiceException = new IdentityManagementServiceException ()
            def exception = new PosixServiceException(identityManagementServiceException)
        when:
            def handler = new IdmsGenericExceptionMapper.PosixServiceExceptionHandler()
            Response response = handler.toResponse(exception)
        then:
            response != null
        and:
            response.getStatus() == HttpStatusCode.UNPROCESSABLE_ENTITY.getStatusCode()
        and:
            response.getEntity() != null
    }

    def "build toResponse for NotAuthorizedException"() {
        given:
        def notAuthorizedException = new NotAuthorizedException ("IdentityManagementServiceRest","ApplicationExceptionHandled",
                "The User does not have permissions to perform this action.")
        when:
        def handler = new IdmsGenericExceptionMapper.NotAuthorizedExceptionHandler()
        Response response = handler.toResponse(notAuthorizedException)
        then:
        response != null
        and:
        response.getStatus() == HttpStatusCode.UNPROCESSABLE_ENTITY.getStatusCode()
        and:
        response.getEntity() != null
    }
}
