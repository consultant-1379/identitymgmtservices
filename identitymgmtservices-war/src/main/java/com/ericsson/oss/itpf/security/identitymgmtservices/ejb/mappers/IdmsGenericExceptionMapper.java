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
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb.mappers;

import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementServiceException;
import com.ericsson.oss.itpf.security.identitymgmtservices.PosixServiceException;
import com.ericsson.oss.services.security.genericidentity.commons.exception.exceptions.NotAuthorizedException;
import com.ericsson.oss.services.security.genericidentity.commons.model.rest.ErrorResponse;
import com.ericsson.oss.services.security.genericidentity.commons.model.rest.HttpStatusCode;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Date;

/**
 * Identity Management Service Exceptions mapper
 */
public final class IdmsGenericExceptionMapper {

    private IdmsGenericExceptionMapper() {
    }

    @Provider
    public static class EJBTransactionRolledbackExceptionHandler implements ExceptionMapper<EJBTransactionRolledbackException> {

        @Override
        public Response toResponse(final EJBTransactionRolledbackException exception) {

            final ErrorResponse errorResponse = getErrorResponse(exception);
            return Response.status(HttpStatusCode.UNPROCESSABLE_ENTITY.getStatusCode()).entity(errorResponse).build();
        }

        /**
         *
         */
        private static ErrorResponse getErrorResponse(final EJBTransactionRolledbackException exception) {
            final ErrorResponse errorResponse = new ErrorResponse();

            errorResponse.setTime(new Date());
            errorResponse.setHttpStatusCode(HttpStatusCode.UNPROCESSABLE_ENTITY.getStatusCode());
            errorResponse.setUserMessage("");
            errorResponse.setDeveloperMessage(exception.getLocalizedMessage());

            return errorResponse;
        }
    }

    @Provider
    public static class IdentityManagementServiceExceptionHandler implements ExceptionMapper<IdentityManagementServiceException> {

        @Override
        public Response toResponse(final IdentityManagementServiceException exception) {

            final ErrorResponse errorResponse = getErrorResponse(exception);
            return Response.status(HttpStatusCode.UNPROCESSABLE_ENTITY.getStatusCode()).entity(errorResponse).build();
        }
    }

    @Provider
    public static class PosixServiceExceptionHandler implements ExceptionMapper<PosixServiceException> {

        @Override
        public Response toResponse(final PosixServiceException exception) {

            final ErrorResponse errorResponse = getErrorResponse(exception);
            return Response.status(HttpStatusCode.UNPROCESSABLE_ENTITY.getStatusCode()).entity(errorResponse).build();
        }
    }

    @Provider
    public static class NotAuthorizedExceptionHandler implements ExceptionMapper<NotAuthorizedException> {

        @Override
        public Response toResponse(final NotAuthorizedException exception) {

            final ErrorResponse errorResponse = getErrorResponse(exception);
            return Response.status(HttpStatusCode.UNPROCESSABLE_ENTITY.getStatusCode()).entity(errorResponse).build();
        }

        /**
         *
         */
        private static ErrorResponse getErrorResponse(final NotAuthorizedException exception) {
            final ErrorResponse errorResponse = new ErrorResponse();

            errorResponse.setTime(new Date());
            errorResponse.setHttpStatusCode(HttpStatusCode.UNPROCESSABLE_ENTITY.getStatusCode());
            errorResponse.setUserMessage("");
            errorResponse.setDeveloperMessage(exception.getLocalizedMessage());

            return errorResponse;
        }
    }

    private static ErrorResponse getErrorResponse(final IdentityManagementServiceException exception) {
        final ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setTime(new Date());
        errorResponse.setHttpStatusCode(HttpStatusCode.UNPROCESSABLE_ENTITY.getStatusCode());
        errorResponse.setInternalErrorCode(exception.getError().toString());
        errorResponse.setUserMessage("");
        errorResponse.setDeveloperMessage(exception.getLocalizedMessage());

        return errorResponse;
    }
}