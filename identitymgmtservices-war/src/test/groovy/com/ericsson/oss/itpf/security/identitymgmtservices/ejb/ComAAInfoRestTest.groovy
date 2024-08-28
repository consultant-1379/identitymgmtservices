package com.ericsson.oss.itpf.security.identitymgmtservices.ejb

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.dto.ComAAInfoDto
import com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa.ComAAInfoDelegate

import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Request
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo


class ComAAInfoRestTest extends CdiSpecification{

    @ObjectUnderTest
    ComAAInfoRest comAAInfoRest

    @MockedImplementation
    ComAAInfoDelegate comAAInfoDelegate

    @MockedImplementation
    HttpHeaders headers

    @MockedImplementation
    UriInfo uriInfo

    @MockedImplementation
    Request request

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def setup (){}

    def "get comAAInfo connection data"() {
        given:
            def dto = Mock(ComAAInfoDto)
            comAAInfoDelegate.getComAAInfoConnectionData(_ as String, _ as String, _ as String) >> dto
        when:
            Response response = comAAInfoRest.readConnectionData(headers, uriInfo, request)
        then:
            response != null
        and: "response status should be OK"
            response.getStatus() == Response.Status.OK.getStatusCode()
        and: "response entity should not be null"
            response.getEntity() != null
    }
}
