package com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class IdmsConfigGetProxyAccountByAdminStatusDtoTest extends CdiSpecification {
    def isLegacy = "false"
    def isSumamry = "false"
    def adminStatus = "enabled"

    def "no-args constructor" () {
        given:
        when:
        IdmsConfigGetProxyAccountByAdminStatusDto idmsConfigGetProxyAccountByAdminStatusDto = new IdmsConfigGetProxyAccountByAdminStatusDto()
        then:
        idmsConfigGetProxyAccountByAdminStatusDto !=null
        and:
        idmsConfigGetProxyAccountByAdminStatusDto.getIsLegacy() == null &&
                idmsConfigGetProxyAccountByAdminStatusDto.getIsSummary() == null
    }

    def "constructor with parameters" () {
        given:
        when:
        IdmsConfigGetProxyAccountByAdminStatusDto idmsConfigGetProxyAccountByAdminStatusDto =
                new IdmsConfigGetProxyAccountByAdminStatusDto(isLegacy, isSumamry, adminStatus)
        then:
        idmsConfigGetProxyAccountByAdminStatusDto.getIsLegacy() == isLegacy &&
                idmsConfigGetProxyAccountByAdminStatusDto.getIsSummary() == isSumamry &&
                idmsConfigGetProxyAccountByAdminStatusDto.getAdminStatus() == adminStatus
    }

    def "set parameters" () {
        given:
        IdmsConfigGetProxyAccountByAdminStatusDto idmsConfigGetProxyAccountByAdminStatusDto = new IdmsConfigGetProxyAccountByAdminStatusDto()
        when:
        idmsConfigGetProxyAccountByAdminStatusDto.setIsLegacy(isLegacy)
        idmsConfigGetProxyAccountByAdminStatusDto.setIsSummary(isSumamry)
        idmsConfigGetProxyAccountByAdminStatusDto.setAdminStatus(adminStatus)
        then:
        idmsConfigGetProxyAccountByAdminStatusDto.getIsLegacy() == isLegacy &&
                idmsConfigGetProxyAccountByAdminStatusDto.getIsSummary() == isSumamry &&
                idmsConfigGetProxyAccountByAdminStatusDto.getAdminStatus() == adminStatus
    }

}
