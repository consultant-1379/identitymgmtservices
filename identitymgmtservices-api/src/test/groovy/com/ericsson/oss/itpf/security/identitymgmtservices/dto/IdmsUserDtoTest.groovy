package com.ericsson.oss.itpf.security.identitymgmtservices.dto

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class IdmsUserDtoTest extends CdiSpecification {

    def userName = "foousername"

    def "no-args constructor" () {
        given:
        when:
        IdmsUserDto idmsUserDto = new IdmsUserDto()
        then:
        idmsUserDto !=null
        and:
        idmsUserDto.getUserName() == null
    }

    def "constructor with parameters" () {
        given:
        when:
        IdmsUserDto idmsUserDto = new IdmsUserDto(userName)
        then:
        idmsUserDto.getUserName() == userName
    }

    def "set parameters" () {
        given:
        IdmsUserDto idmsUserDto = new IdmsUserDto()
        when:
        idmsUserDto.setUserName(userName)
        then:
        idmsUserDto.getUserName() == userName
    }

}
