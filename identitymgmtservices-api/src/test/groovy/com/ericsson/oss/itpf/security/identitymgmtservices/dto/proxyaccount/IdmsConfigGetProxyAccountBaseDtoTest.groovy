package com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class IdmsConfigGetProxyAccountBaseDtoTest extends CdiSpecification {
    def isLegacy = "false"
    def isSumamry = "false"

    def "no-args constructor" () {
        given:
        when:
        IdmsConfigGetProxyAccountBaseDto idmsConfigGetProxyAccountBaseDto = new IdmsConfigGetProxyAccountBaseDto()
        then:
        idmsConfigGetProxyAccountBaseDto !=null
        and:
        idmsConfigGetProxyAccountBaseDto.getIsLegacy() == null &&
                idmsConfigGetProxyAccountBaseDto.getIsSummary() == null
    }

    def "constructor with parameters" () {
        given:
        when:
        IdmsConfigGetProxyAccountBaseDto idmsConfigGetProxyAccountBaseDto = new IdmsConfigGetProxyAccountBaseDto(isLegacy, isSumamry)
        then:
        idmsConfigGetProxyAccountBaseDto.getIsLegacy() == isLegacy &&
                idmsConfigGetProxyAccountBaseDto.getIsSummary() == isSumamry
    }

    def "set parameters" () {
        given:
        IdmsConfigGetProxyAccountBaseDto idmsConfigGetProxyAccountBaseDto = new IdmsConfigGetProxyAccountBaseDto()
        when:
        idmsConfigGetProxyAccountBaseDto.setIsLegacy(isLegacy)
        idmsConfigGetProxyAccountBaseDto.setIsSummary(isSumamry)
        then:
        idmsConfigGetProxyAccountBaseDto.getIsLegacy() == isLegacy &&
                idmsConfigGetProxyAccountBaseDto.getIsSummary() == isSumamry
    }

}
