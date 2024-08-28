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

import com.ericsson.oss.itpf.security.identitymgmtservices.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 *
 * Rest Services for Identity Managemenet Service
 *
 */
@Path("/m2m")
public class IdentityManagementServiceRest {

    private static final String COMMAND_START_INFO = "{} : request {} : uri {}";
    private static final String IDMS_REST_SOURCE = "IdentityManagementService";
    private static final String IDMS_REST_RESOURCE = IdentityManagementServiceDelegate.class.getSimpleName();

    private final Logger logger = LoggerFactory.getLogger(IdentityManagementServiceRest.class);

    @Inject
    private IdentityManagementServiceDelegate identityManagementServiceDelegate;

    @POST
    @Path("configm2muser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response configM2MUser (final IdmsConfigM2MUserDto idmsConfigM2MUserDto, final @Context HttpHeaders headers, final @Context UriInfo uriInfo,
                                   @Context final Request request) throws JsonProcessingException {

        final String info = "POST configm2muser";
        logger.debug(COMMAND_START_INFO, info, request.getMethod(), uriInfo.getRequestUri());

        final IdmsReadM2MUserExtDto idmsReadM2MUserExtDto = identityManagementServiceDelegate.configM2MUserPassword(idmsConfigM2MUserDto, IDMS_REST_SOURCE, IDMS_REST_RESOURCE, info);

        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(idmsReadM2MUserExtDto);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @DELETE
    @Path("deletem2muser/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteM2MUser (@PathParam("username") final String userName, final @Context HttpHeaders headers, final @Context UriInfo uriInfo,
                                   @Context final Request request) throws JsonProcessingException {

        final String info = "DELETE delete2muser";
        logger.debug(COMMAND_START_INFO, info, request.getMethod(), uriInfo.getRequestUri());

        final IdmsUserStateDto idmsUserStateDto = identityManagementServiceDelegate.deleteM2MUser(new IdmsUserDto(userName), IDMS_REST_SOURCE, IDMS_REST_RESOURCE, info);

        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(idmsUserStateDto);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("readm2muser/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readM2MUser (@PathParam("username") final String userName, final @Context HttpHeaders headers, final @Context UriInfo uriInfo,
                                   @Context final Request request) throws JsonProcessingException {

        final String info = "GET readm2muser";
        logger.debug(COMMAND_START_INFO, info, request.getMethod(), uriInfo.getRequestUri());

        final IdmsReadM2MUserDto idmsReadM2MUserDto = identityManagementServiceDelegate.getM2MUser(new IdmsUserDto(userName), IDMS_REST_SOURCE, IDMS_REST_RESOURCE, info);

        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(idmsReadM2MUserDto);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("checkm2muser/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkM2MUser (@PathParam("username") String userName, final @Context HttpHeaders headers, final @Context UriInfo uriInfo,
                                 @Context final Request request) throws JsonProcessingException {

        final String info = "GET checkm2muser";
        logger.debug(COMMAND_START_INFO, info, request.getMethod(), uriInfo.getRequestUri());

        final IdmsUserStateDto idmsUserStateDto = identityManagementServiceDelegate.isExistingM2MUser(new IdmsUserDto(userName), IDMS_REST_SOURCE, IDMS_REST_RESOURCE, info);

        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(idmsUserStateDto);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("readm2muserpassword/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readM2MUserPassword (@PathParam("username") String userName, final @Context HttpHeaders headers, final @Context UriInfo uriInfo,
                                  @Context final Request request) throws JsonProcessingException {

        final String info = "GET readm2muserpassword";
        logger.debug(COMMAND_START_INFO, info, request.getMethod(), uriInfo.getRequestUri());

        final IdmsPasswordDto idmsPasswordDto = identityManagementServiceDelegate.getM2MPassword(new IdmsUserDto(userName), IDMS_REST_SOURCE, IDMS_REST_RESOURCE, info);

        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(idmsPasswordDto);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @PUT
    @Path("updatem2muserpassword/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateM2MUserPassword (@PathParam("username") String userName, final @Context HttpHeaders headers, final @Context UriInfo uriInfo,
                                         @Context final Request request) throws JsonProcessingException {

        final String info = "PUT updatem2muserpassword";
        logger.debug(COMMAND_START_INFO, info, request.getMethod(), uriInfo.getRequestUri());

        final IdmsPasswordDto idmsUpdatedPasswordDto = identityManagementServiceDelegate.updateM2MPassword(new IdmsUserDto(userName), IDMS_REST_SOURCE, IDMS_REST_RESOURCE, info);

        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(idmsUpdatedPasswordDto);
        return Response.status(Response.Status.OK).entity(result).build();
    }

}
