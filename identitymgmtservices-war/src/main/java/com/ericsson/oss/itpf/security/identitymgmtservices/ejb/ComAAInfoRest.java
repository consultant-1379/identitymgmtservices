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
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb;

import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.dto.ComAAInfoDto;
import com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa.ComAAInfoDelegate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

/**
 *
 * Rest Services for ComAAInfo Interface beloging to IdentityManagementService
 *
 */
@Path("/comaainfo")
public class ComAAInfoRest {

    private static final String COMMAND_START_INFO = "{} : request {} : uri {}";
    private static final String IDMS_REST_SOURCE = "ComAAInfo";
    private static final String IDMS_REST_RESOURCE = ComAAInfoDelegate.class.getSimpleName();

    private final Logger logger = LoggerFactory.getLogger(IdentityManagementServiceRest.class);

    @Inject
    ComAAInfoDelegate comAAInfoDelegate;

    @GET
    @Path("readconnectiondata")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response readConnectionData (final @Context HttpHeaders headers, final @Context UriInfo uriInfo,
                                 @Context final Request request) throws JsonProcessingException {

        final String info = "GET readcomaainfo";
        logger.debug(COMMAND_START_INFO, info, request.getMethod(), uriInfo.getRequestUri());

        final ComAAInfoDto comAAInfoDto = comAAInfoDelegate.getComAAInfoConnectionData(IDMS_REST_SOURCE, IDMS_REST_RESOURCE, info);

        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(comAAInfoDto);
        return Response.status(Response.Status.OK).entity(result).build();
    }

}
