package com.ericsson.oss.itpf.security.identitymgmtservices.dto

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.IdmsUserState

class IdmsUserStateDnDtoTest extends CdiSpecification{

    def userNameDn = "foousernamedn"
    def userStateDn = IdmsUserState.IDMS_USER_EXISTING.toString()

    def "no-args constructor" () {
        given:
        when:
        IdmsUserDnStateDto idmsUserDnStateDto = new IdmsUserDnStateDto()
        then:
        idmsUserDnStateDto !=null
        and:
        idmsUserDnStateDto.getUserNameDn() == null &&
                idmsUserDnStateDto.getUserNameDn() == null
    }

    def "constructor with parameters" () {
        given:
        when:
        IdmsUserDnStateDto idmsUserDnStateDto = new IdmsUserDnStateDto(userNameDn, userStateDn)
        then:
        idmsUserDnStateDto.getUserNameDn() == userNameDn &&
                idmsUserDnStateDto.getUserStateDn() == userStateDn

    }

    def "set parameters" () {
        given:
        IdmsUserDnStateDto idmsUserDnStateDto = new IdmsUserDnStateDto()
        when:
        idmsUserDnStateDto.setUserNameDn(userNameDn)
        idmsUserDnStateDto.setUserStateDn(userStateDn)
        then:
        idmsUserDnStateDto.getUserNameDn() == userNameDn &&
                idmsUserDnStateDto.getUserStateDn() == userStateDn
    }
}
