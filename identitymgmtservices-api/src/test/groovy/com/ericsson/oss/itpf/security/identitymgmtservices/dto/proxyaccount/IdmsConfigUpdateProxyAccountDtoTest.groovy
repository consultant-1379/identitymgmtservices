package com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class IdmsConfigUpdateProxyAccountDtoTest extends CdiSpecification {
    def adminStatus = "enabled"

    def "no-args constructor" () {
        given:
        when:
        IdmsConfigUpdateProxyAccountDto idmsConfigUpdateProxyAccountDto = new IdmsConfigUpdateProxyAccountDto()
        then:
        idmsConfigUpdateProxyAccountDto !=null
        and:
        idmsConfigUpdateProxyAccountDto.getAdminStatus() == null
    }

    def "constructor with parameters" () {
        given:
        when:
        IdmsConfigUpdateProxyAccountDto idmsConfigGetProxyAccountBaseDto = new IdmsConfigUpdateProxyAccountDto(adminStatus)
        then:
        idmsConfigGetProxyAccountBaseDto.getAdminStatus() == adminStatus
    }

    def "set parameters" () {
        given:
        IdmsConfigUpdateProxyAccountDto idmsConfigUpdateProxyAccountDto = new IdmsConfigUpdateProxyAccountDto()
        when:
        idmsConfigUpdateProxyAccountDto.setAdminStatus(adminStatus)
        then:
        idmsConfigUpdateProxyAccountDto.getAdminStatus() == adminStatus
    }

}
