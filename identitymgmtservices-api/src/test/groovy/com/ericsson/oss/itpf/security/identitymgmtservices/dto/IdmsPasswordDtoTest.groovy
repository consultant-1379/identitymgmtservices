package com.ericsson.oss.itpf.security.identitymgmtservices.dto

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class IdmsPasswordDtoTest extends CdiSpecification {

    def encryptedPwd = "fooencryptedpassword"

    def "no-args constructor" () {
        given:
        when:
        IdmsPasswordDto idmsPasswordDto = new IdmsPasswordDto()
        then:
        idmsPasswordDto !=null
        and:
        idmsPasswordDto.getEncryptedPwd() == null
    }

    def "constructor with parameters" () {
        given:
        when:
        IdmsPasswordDto idmsPasswordDto = new IdmsPasswordDto(encryptedPwd)
        then:
        idmsPasswordDto.getEncryptedPwd() == encryptedPwd
    }

    def "set parameters" () {
        given:
        IdmsPasswordDto idmsPasswordDto = new IdmsPasswordDto()
        when:
        idmsPasswordDto.setEncryptedPwd(encryptedPwd)
        then:
        idmsPasswordDto.getEncryptedPwd() == encryptedPwd
    }

}
