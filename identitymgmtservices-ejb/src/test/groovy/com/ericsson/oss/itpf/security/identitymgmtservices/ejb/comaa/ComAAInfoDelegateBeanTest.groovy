package com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.ComAAInfo
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.ConnectionData
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.LdapAddress
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.dto.ComAAInfoDto

class ComAAInfoDelegateBeanTest extends CdiSpecification {

    @ObjectUnderTest
    ComAAInfoDelegateBean comAAInfoDelegateBean

    def primary_ipv4_address = "192.168.0.1"
    def fallback_ipv4_address = "192.168.0.2"
    def primary_ipv6_address = "::ffff:10.120.78.40"
    def fallback_ipv6_address = "::ffff:10.120.78.41"
    def ldapTlsPort = 1636
    def ldapsPort = 1389

    @ImplementationInstance
    ComAAInfo comAAInfo = [
            getConnectionData : {
                return new ConnectionData(new LdapAddress(primary_ipv4_address, fallback_ipv4_address),
                                            new LdapAddress(primary_ipv6_address, fallback_ipv6_address),
                                            ldapTlsPort, ldapsPort
                )
            }
    ] as ComAAInfo

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def setup (){}

    def "getComAAInfoConnectionData"() {
        given:
        when:
            ComAAInfoDto comAAInfoDto = comAAInfoDelegateBean.getComAAInfoConnectionData(_ as String, _ as String, _ as String)
        then:
            comAAInfoDto != null
        and:
            comAAInfoDto.ipv4AddressPrimary == primary_ipv4_address &&
                    comAAInfoDto.ipv4AddressFallback == fallback_ipv4_address &&
                    comAAInfoDto.ipv6AddressPrimary == primary_ipv6_address &&
                    comAAInfoDto.ipv6AddressFallback == fallback_ipv6_address &&
                    comAAInfoDto.ldapTlsPort == ldapTlsPort &&
                    comAAInfoDto.ldapsPort == ldapsPort
    }
}
