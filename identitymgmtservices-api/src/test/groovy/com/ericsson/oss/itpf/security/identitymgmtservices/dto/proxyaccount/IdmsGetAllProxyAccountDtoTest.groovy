package com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.IdmsGetAllProxyAccountDto
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus

class IdmsGetAllProxyAccountDtoTest extends  CdiSpecification {
    def idmsUserDnTest = "cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
    def idmsAccountDisabled = ProxyAgentAccountAdminStatus.DISABLED.toString()
    // format is "yyMMddHHmmss"
    def idmsCreateTimestampTest = "230109143130"
    def idmsLastLoginTimeTest = "230109153130"

    def "set parameters " () {
        given:
        IdmsGetAllProxyAccountDto idmsGetAllProxyAccountDto = new IdmsGetAllProxyAccountDto()
        when:
        idmsGetAllProxyAccountDto.setIdmsUserDn(idmsUserDnTest)
        idmsGetAllProxyAccountDto.setIdmsCreateTimestamp(idmsCreateTimestampTest)
        idmsGetAllProxyAccountDto.setIdmsLastLoginTime(idmsLastLoginTimeTest)
        idmsGetAllProxyAccountDto.setIdmsAccountDisabled(idmsAccountDisabled)
        then:
        idmsGetAllProxyAccountDto.getIdmsUserDn() == idmsUserDnTest &&
                idmsGetAllProxyAccountDto.getIdmsCreateTimestamp() == idmsCreateTimestampTest &&
                idmsGetAllProxyAccountDto.getIdmsLastLoginTime() == idmsLastLoginTimeTest &&
                idmsGetAllProxyAccountDto.getIdmsAccountDisabled() == idmsAccountDisabled
    }
}
