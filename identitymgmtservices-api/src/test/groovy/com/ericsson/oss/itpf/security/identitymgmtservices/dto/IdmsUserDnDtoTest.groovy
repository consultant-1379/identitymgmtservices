package com.ericsson.oss.itpf.security.identitymgmtservices.dto

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class IdmsUserDnDtoTest extends CdiSpecification {

    def userNameDn = "fooUserDn"

    def "no-args constructor" () {
        given:
        when:
        IdmsUserDnDto idmsUserDnDto = new IdmsUserDnDto()
        then:
        idmsUserDnDto !=null
        and:
        idmsUserDnDto.getUserNameDn() == null
    }

    def "constructor with parameters" () {
        given:
        when:
        IdmsUserDnDto idmsUserDnDto = new IdmsUserDnDto(userNameDn)
        then:
        idmsUserDnDto.getUserNameDn() == userNameDn
    }

    def "set parameters" () {
        given:
        IdmsUserDnDto idmsUserDnDto = new IdmsUserDnDto()
        when:
        idmsUserDnDto.setUserNameDn(userNameDn)
        then:
        idmsUserDnDto.getUserNameDn() == userNameDn
    }

}
