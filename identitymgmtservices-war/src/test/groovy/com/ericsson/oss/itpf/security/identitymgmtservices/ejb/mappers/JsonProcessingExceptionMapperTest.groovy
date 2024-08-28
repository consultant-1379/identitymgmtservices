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
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb.mappers

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.fasterxml.jackson.core.JsonProcessingException

import javax.ws.rs.core.Response

class JsonProcessingExceptionMapperTest extends CdiSpecification {

    @ObjectUnderTest
    JsonProcessingExceptionMapper jsonProcessingExceptionMapper

    def JsonProcessingException exception = Mock()

    def "build toResponse"() {
        given:

        when: "building toResponse"
        Response response = jsonProcessingExceptionMapper.toResponse(exception)
        then: "response should not be null"
        response != null
        and: "response status should be BAD_REQUEST"
        response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()
    }
}
