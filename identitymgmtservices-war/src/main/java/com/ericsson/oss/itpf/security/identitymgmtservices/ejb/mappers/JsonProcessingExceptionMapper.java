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

import com.ericsson.oss.services.security.genericidentity.commons.model.rest.ErrorResponse;
import com.ericsson.oss.services.security.genericidentity.commons.model.rest.HttpStatusCode;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Date;

@Provider
public class JsonProcessingExceptionMapper implements ExceptionMapper<JsonProcessingException> {

    @Override
    public Response toResponse(final JsonProcessingException exception) {

        final ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setTime(new Date());
        errorResponse.setHttpStatusCode(HttpStatusCode.BAD_REQUEST.getStatusCode());
        errorResponse.setInternalErrorCode("");
        errorResponse.setUserMessage("");
        errorResponse.setDeveloperMessage(exception.getLocalizedMessage());
        return Response.status(HttpStatusCode.BAD_REQUEST.getStatusCode()).entity(errorResponse).build();
    }
}
