package com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.IdmsReadProxyAccountUserDto
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.IdmsUserState


class IdmsReadProxyAccountUserDtoTest extends  CdiSpecification {

    def userNameDn = "cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
    def userStateDn = IdmsUserState.IDMS_USER_EXISTING.toString()
    def encryptedUserPassword = "encryptedUserPassword"

    def "no-args constructor" () {
        given:
        when:
        IdmsReadProxyAccountUserDto idmsReadProxyAccountUserDto = new IdmsReadProxyAccountUserDto()
        then:
        idmsReadProxyAccountUserDto !=null
        and:
        idmsReadProxyAccountUserDto.getUserNameDn() == null &&
                idmsReadProxyAccountUserDto.getUserNameDn() == null &&
                idmsReadProxyAccountUserDto.getEncryptedPwd() == null
    }

    def "constructor with parameters" () {
        given:
        when:
        IdmsReadProxyAccountUserDto idmsReadProxyAccountUserDto = new IdmsReadProxyAccountUserDto(userNameDn, userStateDn, encryptedUserPassword)
        then:
        idmsReadProxyAccountUserDto.getUserNameDn() == userNameDn &&
                idmsReadProxyAccountUserDto.getUserStateDn() == userStateDn &&
                idmsReadProxyAccountUserDto.getEncryptedPwd() == encryptedUserPassword
    }

    def "set parameters" () {
        given:
        IdmsReadProxyAccountUserDto idmsReadProxyAccountUserDto = new IdmsReadProxyAccountUserDto()
        when:
        idmsReadProxyAccountUserDto.setUserNameDn(userNameDn)
        idmsReadProxyAccountUserDto.setUserStateDn(userStateDn)
        idmsReadProxyAccountUserDto.setEncryptedPwd(encryptedUserPassword)
        then:
        idmsReadProxyAccountUserDto.getUserNameDn() == userNameDn &&
                idmsReadProxyAccountUserDto.getUserStateDn() == userStateDn &&
                idmsReadProxyAccountUserDto.getEncryptedPwd() == encryptedUserPassword
    }
}
