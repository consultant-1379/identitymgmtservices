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
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.text.ParseException;

/**
 *
 * Rest Services for Proxy Account  belonging to IdentityManagementService
 *
 */
@Path("/proxyaccount")
public class ProxyAccountRest {

    private static final String COMMAND_START_INFO = "{} : request {} : uri {}";
    private static final String IDMS_REST_SOURCE = "ProxyAccount";
    private static final String IDMS_REST_RESOURCE = IdentityManagementServiceDelegate.class.getSimpleName();

    private final Logger logger = LoggerFactory.getLogger(ProxyAccountRest.class);

    @Inject
    private PosixServiceDelegate posixServiceDelegate;

    @POST
    @Path("createproxyuseraccount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProxyUserAccount (final @Context HttpHeaders headers, final @Context UriInfo uriInfo,
                                            @Context final Request request) throws JsonProcessingException {

        final String info = "POST createproxyuseraccount";
        logger.debug(COMMAND_START_INFO, info, request.getMethod(), uriInfo.getRequestUri());

        final IdmsReadProxyAccountUserDto idmsReadProxyAccountUserDto = posixServiceDelegate.createProxyAgentAccount(IDMS_REST_SOURCE, IDMS_REST_RESOURCE, info);

        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(idmsReadProxyAccountUserDto);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @DELETE
    @Path("deleteproxyuseraccount/{usernamedn}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProxyUserAccount (@PathParam("usernamedn") String userNameDn, final @Context HttpHeaders headers, final @Context UriInfo uriInfo,
                                            @Context final Request request) throws JsonProcessingException {

        final String info = "DELETE deleteproxyuseraccount";
        logger.debug(COMMAND_START_INFO, info, request.getMethod(), uriInfo.getRequestUri());

        final IdmsUserDnStateDto idmsUserDnStateDto = posixServiceDelegate.deleteProxyAgentAccount(new IdmsUserDnDto(userNameDn),IDMS_REST_SOURCE, IDMS_REST_RESOURCE, info);

        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(idmsUserDnStateDto);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("checkcomuser/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkComUser (@PathParam("username") String userName, final @Context HttpHeaders headers, final @Context UriInfo uriInfo,
                                  @Context final Request request) throws JsonProcessingException {

        final String info = "GET checkcomuser";
        logger.debug(COMMAND_START_INFO, info, request.getMethod(), uriInfo.getRequestUri());

        final IdmsComUserGroupDto idmsComUserGroupDto = posixServiceDelegate.isComUser(new IdmsUserDto(userName), IDMS_REST_SOURCE, IDMS_REST_RESOURCE, info);

        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(idmsComUserGroupDto);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("getallproxyuseraccount")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProxyUserAccount (final IdmsConfigGetProxyAccountBaseDto idmsConfigGetProxyAccountBaseDto,
                                            final @Context HttpHeaders headers, final @Context UriInfo uriInfo,
                                            @Context final Request request) throws JsonProcessingException {

        final String info = "GET getallproxyuseraccount";
        logger.debug(COMMAND_START_INFO, info, request.getMethod(), uriInfo.getRequestUri());

        final IdmsGetAllProxyAccountGetDataDto idmsGetAllProxyAccountGetDataDto =
                posixServiceDelegate.getAllProxyAgentAccount(idmsConfigGetProxyAccountBaseDto, IDMS_REST_SOURCE, IDMS_REST_RESOURCE, info);

        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(idmsGetAllProxyAccountGetDataDto);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @POST
    @Path("updateproxyuseraccount/{usernamedn}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProxyUserAccount (@PathParam("usernamedn") String userNameDn,
                                            final IdmsConfigUpdateProxyAccountDto idmsConfigGetProxyAccountDto,
                                            final @Context HttpHeaders headers, final @Context UriInfo uriInfo,
                                            @Context final Request request) throws JsonProcessingException {

        final String info = "POST updateproxyuseraccount";
        logger.debug(COMMAND_START_INFO, info, request.getMethod(), uriInfo.getRequestUri());

        Boolean idmsProxyAgentAccountAdminStatus =
                posixServiceDelegate.updateProxyAgentAccountAdminStatus(userNameDn, idmsConfigGetProxyAccountDto , IDMS_REST_SOURCE, IDMS_REST_RESOURCE, info);

        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(idmsProxyAgentAccountAdminStatus);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("getallproxyuseraccountbyadminstatus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProxyUserAccountByAdminStatus (final IdmsConfigGetProxyAccountByAdminStatusDto idmsConfigGetProxyAccountByAdminStatusDto,
                                                          final @Context HttpHeaders headers, final @Context UriInfo uriInfo,
                                                          @Context final Request request) throws JsonProcessingException {

        final String info = "GET getallproxyuseraccountbyadminstatus";
        logger.debug(COMMAND_START_INFO, info, request.getMethod(), uriInfo.getRequestUri());

        final IdmsGetAllProxyAccountGetDataDto idmsGetAllProxyAccountGetDataDto =
                posixServiceDelegate.getAllProxyAgentAccountByAdminStatus(idmsConfigGetProxyAccountByAdminStatusDto,IDMS_REST_SOURCE, IDMS_REST_RESOURCE, info);

        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(idmsGetAllProxyAccountGetDataDto);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("getallproxyuseraccountbyinactivitydate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProxyUserAccountByInactivityDate (final IdmsConfigGetProxyAccountByInactivityDateDto idmsConfigGetProxyAccountByInactivityDateDto,
                                                            final @Context HttpHeaders headers, final @Context UriInfo uriInfo,
                                                            @Context final Request request) throws JsonProcessingException  {
        final String result;
        final String info = "GET getallproxyuseraccountbyinactivitydate";
        logger.debug(COMMAND_START_INFO, info, request.getMethod(), uriInfo.getRequestUri());

        final IdmsGetAllProxyAccountGetDataDto idmsGetAllProxyAccountGetDataDto;
        try {
            idmsGetAllProxyAccountGetDataDto =
                    posixServiceDelegate.getAllProxyAgentAccountByInactivityPeriod(idmsConfigGetProxyAccountByInactivityDateDto,IDMS_REST_SOURCE, IDMS_REST_RESOURCE, info);
        } catch (ParseException e) {
            result = "Data parse exception";
            logger.error("error is : {} {} {}",result, e.getMessage(), e.getCause());
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }

        final ObjectMapper objectMapper = new ObjectMapper();
        result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(idmsGetAllProxyAccountGetDataDto);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("getsingleproxyuseraccount/{userdn}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSingleProxyUserAccount (@PathParam("userdn") String userDn,
                                              final @Context HttpHeaders headers, final @Context UriInfo uriInfo,
                                              @Context final Request request) throws JsonProcessingException {

        final String info = "GET getSingleProxyUserAccount";
        logger.debug(COMMAND_START_INFO, info, request.getMethod(), uriInfo.getRequestUri());

        final IdmsGetAllProxyAccountDto idmsGetAllProxyAccountDto = posixServiceDelegate.getProxyAgentAccountDetails(
                userDn,IDMS_REST_SOURCE, IDMS_REST_RESOURCE, info);

        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(idmsGetAllProxyAccountDto);
        return Response.status(Response.Status.OK).entity(result).build();
    }
}