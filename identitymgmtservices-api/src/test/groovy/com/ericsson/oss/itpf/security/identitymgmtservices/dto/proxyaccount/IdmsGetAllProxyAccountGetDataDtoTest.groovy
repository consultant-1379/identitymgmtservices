package com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.IdmsGetAllProxyAccountDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.IdmsGetAllProxyAccountGetDataDto
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus

class IdmsGetAllProxyAccountGetDataDtoTest extends  CdiSpecification{

    def numOfGetDataProxyAccountTest = 1;
    def numOfGetDataRequestedProxyAccount = 1;
    def numOfGetDataProxyAccountLegacy = 1;
    def numOfGetDataRequestedProxyAccountLegacy = 1;

    def idmsUserDnTest = "cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
    def idmsAccountDisabled = ProxyAgentAccountAdminStatus.DISABLED.toString()
    // format is "yyMMddHHmmss"
    def idmsCreateTimestampTest = "230109143130"
    def idmsLastLoginTimeTest = "230109153130"

    def "set parameters " () {
        given:
        IdmsGetAllProxyAccountDto idmsGetAllProxyAccountDto = new IdmsGetAllProxyAccountDto()
        idmsGetAllProxyAccountDto.setIdmsUserDn(idmsUserDnTest)
        idmsGetAllProxyAccountDto.setIdmsCreateTimestamp(idmsCreateTimestampTest)
        idmsGetAllProxyAccountDto.setIdmsLastLoginTime(idmsLastLoginTimeTest)
        idmsGetAllProxyAccountDto.setIdmsAccountDisabled(idmsAccountDisabled)

        List<IdmsGetAllProxyAccountDto> idmsGetAllProxyAccountDtos = new ArrayList<>()
        idmsGetAllProxyAccountDtos.add(idmsGetAllProxyAccountDto)

        IdmsGetAllProxyAccountGetDataDto idmsGetAllProxyAccountGetDataDto = new IdmsGetAllProxyAccountGetDataDto()

        when:
        idmsGetAllProxyAccountGetDataDto.setNumOfGetDataProxyAccount(numOfGetDataProxyAccountTest)
        idmsGetAllProxyAccountGetDataDto.setNumOfGetDataProxyAccountLegacy(numOfGetDataProxyAccountLegacy)
        idmsGetAllProxyAccountGetDataDto.setNumOfGetDataRequestedProxyAccount(numOfGetDataRequestedProxyAccount)
        idmsGetAllProxyAccountGetDataDto.setNumOfGetDataRequestedProxyAccountLegacy(numOfGetDataRequestedProxyAccountLegacy)

        idmsGetAllProxyAccountGetDataDto.setIdmsGetAllProxyAccountDtos(idmsGetAllProxyAccountDtos)
        then:
        idmsGetAllProxyAccountGetDataDto.getNumOfGetDataProxyAccount() == numOfGetDataProxyAccountTest &&
                idmsGetAllProxyAccountGetDataDto.getNumOfGetDataProxyAccountLegacy() == numOfGetDataProxyAccountLegacy &&
                idmsGetAllProxyAccountGetDataDto.getNumOfGetDataRequestedProxyAccount() == numOfGetDataRequestedProxyAccount &&
                idmsGetAllProxyAccountGetDataDto.getNumOfGetDataRequestedProxyAccountLegacy() == numOfGetDataRequestedProxyAccountLegacy &&
                idmsGetAllProxyAccountGetDataDto.getIdmsGetAllProxyAccountDtos().get(0).getIdmsUserDn() == idmsUserDnTest &&
                idmsGetAllProxyAccountGetDataDto.getIdmsGetAllProxyAccountDtos().get(0).getIdmsCreateTimestamp() == idmsCreateTimestampTest &&
                idmsGetAllProxyAccountGetDataDto.getIdmsGetAllProxyAccountDtos().get(0).getIdmsLastLoginTime() == idmsLastLoginTimeTest &&
                idmsGetAllProxyAccountGetDataDto.getIdmsGetAllProxyAccountDtos().get(0).getIdmsAccountDisabled() == idmsAccountDisabled
    }

    def "set parameters with null list " () {
        given:
        List<IdmsGetAllProxyAccountDto> idmsGetAllProxyAccountDtos = null
        IdmsGetAllProxyAccountGetDataDto idmsGetAllProxyAccountGetDataDto = new IdmsGetAllProxyAccountGetDataDto()

        when:
        idmsGetAllProxyAccountGetDataDto.setNumOfGetDataProxyAccount(numOfGetDataProxyAccountTest)
        idmsGetAllProxyAccountGetDataDto.setNumOfGetDataProxyAccountLegacy(numOfGetDataProxyAccountLegacy)
        idmsGetAllProxyAccountGetDataDto.setNumOfGetDataRequestedProxyAccount(numOfGetDataRequestedProxyAccount)
        idmsGetAllProxyAccountGetDataDto.setNumOfGetDataRequestedProxyAccountLegacy(numOfGetDataRequestedProxyAccountLegacy)

        idmsGetAllProxyAccountGetDataDto.setIdmsGetAllProxyAccountDtos(idmsGetAllProxyAccountDtos)
        then:
        idmsGetAllProxyAccountGetDataDto.getNumOfGetDataProxyAccount() == numOfGetDataProxyAccountTest &&
                idmsGetAllProxyAccountGetDataDto.getNumOfGetDataProxyAccountLegacy() == numOfGetDataProxyAccountLegacy &&
                idmsGetAllProxyAccountGetDataDto.getNumOfGetDataRequestedProxyAccount() == numOfGetDataRequestedProxyAccount &&
                idmsGetAllProxyAccountGetDataDto.getNumOfGetDataRequestedProxyAccountLegacy() == numOfGetDataRequestedProxyAccountLegacy &&
                idmsGetAllProxyAccountGetDataDto.getIdmsGetAllProxyAccountDtos() == Collections.emptyList()
    }
}
