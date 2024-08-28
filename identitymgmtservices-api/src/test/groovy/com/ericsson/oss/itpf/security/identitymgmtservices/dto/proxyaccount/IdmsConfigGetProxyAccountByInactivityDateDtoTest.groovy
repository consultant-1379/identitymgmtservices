package com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class IdmsConfigGetProxyAccountByInactivityDateDtoTest extends CdiSpecification {
    def isLegacy = "false"
    def isSumamry = "false"
    def inactivityDate = "2023-01-12 12:30:45"

    def "no-args constructor" () {
        given:
        when:
        IdmsConfigGetProxyAccountByInactivityDateDto idmsConfigGetProxyAccountByInactivityDateDto = new IdmsConfigGetProxyAccountByInactivityDateDto()
        then:
        idmsConfigGetProxyAccountByInactivityDateDto !=null
        and:
        idmsConfigGetProxyAccountByInactivityDateDto.getIsLegacy() == null &&
                idmsConfigGetProxyAccountByInactivityDateDto.getIsSummary() == null &&
                idmsConfigGetProxyAccountByInactivityDateDto.getInactivityDate() == null
    }

    def "constructor with parameters" () {
        given:
        when:
        IdmsConfigGetProxyAccountByInactivityDateDto idmsConfigGetProxyAccountByInactivityDateDto =
                new IdmsConfigGetProxyAccountByInactivityDateDto(isLegacy, isSumamry, inactivityDate)
        then:
        idmsConfigGetProxyAccountByInactivityDateDto.getIsLegacy() == isLegacy &&
                idmsConfigGetProxyAccountByInactivityDateDto.getIsSummary() == isSumamry &&
                idmsConfigGetProxyAccountByInactivityDateDto.getInactivityDate() == inactivityDate
    }

    def "set parameters" () {
        given:
        IdmsConfigGetProxyAccountByInactivityDateDto idmsConfigGetProxyAccountByInactivityDateDto = new IdmsConfigGetProxyAccountByInactivityDateDto()
        when:
        idmsConfigGetProxyAccountByInactivityDateDto.setIsLegacy(isLegacy)
        idmsConfigGetProxyAccountByInactivityDateDto.setIsSummary(isSumamry)
        idmsConfigGetProxyAccountByInactivityDateDto.setInactivityDate(inactivityDate)
        then:
        idmsConfigGetProxyAccountByInactivityDateDto.getIsLegacy() == isLegacy &&
                idmsConfigGetProxyAccountByInactivityDateDto.getIsSummary() == isSumamry &&
                idmsConfigGetProxyAccountByInactivityDateDto.getInactivityDate() == inactivityDate
    }
}
