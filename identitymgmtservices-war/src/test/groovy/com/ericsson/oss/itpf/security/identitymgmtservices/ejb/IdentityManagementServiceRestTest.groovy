package com.ericsson.oss.itpf.security.identitymgmtservices.ejb

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsConfigM2MUserDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsPasswordDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsReadM2MUserDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsReadM2MUserExtDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsUserDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsUserStateDto

import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Request
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo


class IdentityManagementServiceRestTest extends CdiSpecification {

    @ObjectUnderTest
    IdentityManagementServiceRest identityManagementServiceRest

    @MockedImplementation
    IdentityManagementServiceDelegate identityManagementServiceDelegate

    @MockedImplementation
    IdmsConfigM2MUserDto idmsConfigM2MUserDto

    @MockedImplementation
    HttpHeaders headers

    @MockedImplementation
    UriInfo uriInfo

    @MockedImplementation
    Request request

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def setup (){}

    def "configm2muser"() {
        given:
            def iDto = Mock(IdmsConfigM2MUserDto)
            def oDto = Mock(IdmsReadM2MUserExtDto)
            identityManagementServiceDelegate.configM2MUserPassword(iDto, _ as String, _ as String, _ as String) >> oDto
        when:
            Response response = identityManagementServiceRest.configM2MUser(iDto, headers, uriInfo, request)
        then:
            response != null
        and: "response status should be OK"
            response.getStatus() == Response.Status.OK.getStatusCode()
        and: "response entity should not be null"
            response.getEntity() != null
    }

    def "deletem2muser "() {
        given:
            def username = "any string"
            def iDto = Mock(IdmsUserDto)
            def oDto = Mock(IdmsUserStateDto)
            identityManagementServiceDelegate.deleteM2MUser(iDto, _ as String, _ as String, _ as String) >> oDto
        when:
            Response response = identityManagementServiceRest.deleteM2MUser(username, headers, uriInfo, request)
        then:
            response != null
        and: "response status should be OK"
            response.getStatus() == Response.Status.OK.getStatusCode()
        and: "response entity should not be null"
            response.getEntity() != null
    }

    def "readm2muser "() {
        given:
            def username = "any string"
            def iDto = Mock(IdmsUserDto)
            def oDto = Mock(IdmsReadM2MUserDto)
            identityManagementServiceDelegate.getM2MUser(iDto, _ as String, _ as String, _ as String) >> oDto
        when:
            Response response = identityManagementServiceRest.readM2MUser(username, headers, uriInfo, request)
        then:
            response != null
        and: "response status should be OK"
            response.getStatus() == Response.Status.OK.getStatusCode()
        and: "response entity should not be null"
            response.getEntity() != null
    }

    def "checkm2muser "() {
        given:
            def username = "any string"
            def iDto = Mock(IdmsUserDto)
            def oDto = Mock(IdmsReadM2MUserDto)
            identityManagementServiceDelegate.isExistingM2MUser(iDto, _ as String, _ as String, _ as String) >> oDto
        when:
            Response response = identityManagementServiceRest.checkM2MUser(username, headers, uriInfo, request)
        then:
            response != null
        and: "response status should be OK"
            response.getStatus() == Response.Status.OK.getStatusCode()
        and: "response entity should not be null"
            response.getEntity() != null
    }

    def "readm2muserpassword "() {
        given:
            def username = "any string"
            def iDto = Mock(IdmsUserDto)
            def oDto = Mock(IdmsPasswordDto)
            identityManagementServiceDelegate.getM2MPassword(iDto, _ as String, _ as String, _ as String) >> oDto
        when:
            Response response = identityManagementServiceRest.readM2MUserPassword(username, headers, uriInfo, request)
        then:
            response != null
        and: "response status should be OK"
            response.getStatus() == Response.Status.OK.getStatusCode()
        and: "response entity should not be null"
            response.getEntity() != null
    }

    def "updatem2muserpassword "() {
        given:
            def username = "any string"
            def iDto = Mock(IdmsUserDto)
            def oDto = Mock(IdmsPasswordDto)
            identityManagementServiceDelegate.updateM2MPassword(iDto, _ as String, _ as String, _ as String) >> oDto
        when:
            Response response = identityManagementServiceRest.updateM2MUserPassword(username, headers, uriInfo, request)
        then:
            response != null
        and: "response status should be OK"
            response.getStatus() == Response.Status.OK.getStatusCode()
        and: "response entity should not be null"
            response.getEntity() != null
    }

}
