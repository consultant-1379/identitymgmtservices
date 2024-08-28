package com.ericsson.oss.itpf.security.identitymgmtservices.dto

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.IdmsUserState

class IdmsUserStateDtoTest extends CdiSpecification{

    def userName = "foousername"
    def userState = IdmsUserState.IDMS_USER_EXISTING.toString()

    def "no-args constructor" () {
        given:
        when:
        IdmsUserStateDto idmsUserStateDto = new IdmsUserStateDto()
        then:
        idmsUserStateDto !=null
        and:
        idmsUserStateDto.getUserName() == null &&
                idmsUserStateDto.getUserState() == null
    }

    def "constructor with parameters" () {
        given:
        when:
        IdmsUserStateDto idmsUserStateDto = new IdmsUserStateDto(userName, userState)
        then:
        idmsUserStateDto.getUserName() == userName &&
                idmsUserStateDto.getUserState() == userState

    }

    def "set parameters" () {
        given:
        IdmsUserStateDto idmsUserStateDto = new IdmsUserStateDto()
        when:
        idmsUserStateDto.setUserName(userName)
        idmsUserStateDto.setUserState(userState)
        then:
        idmsUserStateDto.getUserName() == userName &&
                idmsUserStateDto.getUserState() == userState
    }
}
