package com.ericsson.oss.itpf.security.identitymgmtservices.ejb

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification

import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.IdmsGetAllProxyAccountDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.IdmsGetAllProxyAccountGetDataDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.IdmsReadProxyAccountUserDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsUserDnDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsUserDnStateDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.IdmsConfigGetProxyAccountBaseDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.IdmsConfigGetProxyAccountByAdminStatusDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.IdmsConfigGetProxyAccountByInactivityDateDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.IdmsConfigUpdateProxyAccountDto

import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Request
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo
import java.text.ParseException


class ProxyAccountRestTest extends CdiSpecification {

    @ObjectUnderTest
    ProxyAccountRest proxyAccountRest

    @MockedImplementation
    PosixServiceDelegate posixServiceDelegate

    @MockedImplementation
    HttpHeaders headers

    @MockedImplementation
    UriInfo uriInfo

    @MockedImplementation
    Request request

    @ImplementationInstance
    PosixServiceDelegate posixServiceDelegateException= [
            getAllProxyAgentAccountByInactivityPeriod : { IdmsConfigGetProxyAccountByInactivityDateDto idto, String s1, String s2, String s3 ->
                throw new ParseException("Parse Exception Format Error", 1)
            }
    ] as PosixServiceDelegate


    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def setup (){}

    def "createproxyuseraccount"() {
        given:
            def dto = Mock(IdmsReadProxyAccountUserDto)
            posixServiceDelegate.createProxyAgentAccount(_ as String, _ as String, _ as String) >> dto
        when:
            Response response = proxyAccountRest.createProxyUserAccount(headers, uriInfo, request)
        then:
            response != null
        and: "response status should be OK"
            response.getStatus() == Response.Status.OK.getStatusCode()
        and: "response entity should not be null"
            response.getEntity() != null
    }

    def "deleteproxyuseraccount"() {
        given:
            def username = "any string"
            def iDto = Mock(IdmsUserDnDto)
            def odto = Mock(IdmsUserDnStateDto)
            posixServiceDelegate.deleteProxyAgentAccount(iDto, _ as String, _ as String, _ as String) >> odto
        when:
            Response response = proxyAccountRest.deleteProxyUserAccount(username, headers, uriInfo, request)
        then:
            response != null
        and: "response status should be OK"
            response.getStatus() == Response.Status.OK.getStatusCode()
        and: "response entity should not be null"
            response.getEntity() != null
    }

    def "checkcomuser   "() {
        given:
            def username = "any string"
            def iDto = Mock(IdmsUserDnDto)
            def odto = Mock(IdmsUserDnStateDto)
            posixServiceDelegate.isComUser(iDto, _ as String, _ as String, _ as String) >> odto
        when:
            Response response = proxyAccountRest.checkComUser(username, headers, uriInfo, request)
        then:
            response != null
        and: "response status should be OK"
            response.getStatus() == Response.Status.OK.getStatusCode()
        and: "response entity should not be null"
            response.getEntity() != null
    }

    def "getAllProxyserAccount ALL "() {
        given:
        def iDto = Mock(IdmsConfigGetProxyAccountBaseDto)
        def idmsGetAllProxyAccountGetDataDto = Mock(IdmsGetAllProxyAccountGetDataDto)
        posixServiceDelegate.getAllProxyAgentAccount(iDto, _ as String, _ as String, _ as String) >> idmsGetAllProxyAccountGetDataDto
        when:
        Response response = proxyAccountRest.getAllProxyUserAccount(iDto,headers, uriInfo, request)
        then:
        response != null
        and: "response status should be OK"
        response.getStatus() == Response.Status.OK.getStatusCode()
        and: "response entity should not be null"
        response.getEntity() != null
    }

    def "getAllProxyserAccount by admin status "() {
        given:
        def iDto = Mock(IdmsConfigGetProxyAccountByAdminStatusDto)
        def idmsGetAllProxyAccountGetDataDto = Mock(IdmsGetAllProxyAccountGetDataDto)
        posixServiceDelegate.getAllProxyAgentAccountByAdminStatus(iDto, _ as String, _ as String, _ as String) >> idmsGetAllProxyAccountGetDataDto
        when:
        Response response = proxyAccountRest.getAllProxyUserAccountByAdminStatus(iDto,headers, uriInfo, request)
        then:
        response != null
        and: "response status should be OK"
        response.getStatus() == Response.Status.OK.getStatusCode()
        and: "response entity should not be null"
        response.getEntity() != null
    }

    def "getAllProxyserAccount by inactivity date "() {
        given:
        def iDto = Mock(IdmsConfigGetProxyAccountByInactivityDateDto)
        def idmsGetAllProxyAccountGetDataDto = Mock(IdmsGetAllProxyAccountGetDataDto)
        posixServiceDelegate.getAllProxyAgentAccountByInactivityPeriod(iDto, _ as String, _ as String, _ as String) >> idmsGetAllProxyAccountGetDataDto
        when:
        Response response = proxyAccountRest.getAllProxyUserAccountByInactivityDate(iDto,headers, uriInfo, request)
        then:
        response != null
        and: "response status should be OK"
        response.getStatus() == Response.Status.OK.getStatusCode()
        and: "response entity should not be null"
        response.getEntity() != null
    }

    def "getAllProxyserAccount by inactivity date with format error "() {
        given:
        def iDto = new IdmsConfigGetProxyAccountByInactivityDateDto()
        proxyAccountRest.posixServiceDelegate = posixServiceDelegateException
        when:
        Response response = proxyAccountRest.getAllProxyUserAccountByInactivityDate(iDto,headers, uriInfo, request)
        then:
        response != null
        and: "response status should be NOT OK"
        response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()
        and: "response entity should not be null"
        response.getEntity() != null
    }

    def "updateProxyUserAccount by admin status "() {
        given:
        def iDto = Mock(IdmsConfigUpdateProxyAccountDto)
        posixServiceDelegate.updateProxyAgentAccountAdminStatus(_ as String, iDto,  _ as String, _ as String, _ as String) >> true
        when:
        Response response =
                proxyAccountRest.updateProxyUserAccount(
                        "cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com",
                        iDto,headers, uriInfo, request)
        then:
        response != null
        and: "response status should be OK"
        response.getStatus() == Response.Status.OK.getStatusCode()
        and: "response entity should not be null"
        response.getEntity() != null
    }

    def "getSingleProxyUserAccount "() {
        given:
        def idmsGetAllProxyAccountDto = Mock(IdmsGetAllProxyAccountDto)
        posixServiceDelegate.getProxyAgentAccountDetailsNew(_ as String, _ as String, _ as String, _ as String) >> idmsGetAllProxyAccountDto
        when:
        Response response = proxyAccountRest.getSingleProxyUserAccount("cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com",headers, uriInfo, request)
        then:
        response != null
        and: "response status should be OK"
        response.getStatus() == Response.Status.OK.getStatusCode()
        and: "response entity should not be null"
        response.getEntity() != null
    }
}
