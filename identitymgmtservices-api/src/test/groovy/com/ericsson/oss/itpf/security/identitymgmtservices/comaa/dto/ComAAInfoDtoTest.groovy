package com.ericsson.oss.itpf.security.identitymgmtservices.comaa.dto

import com.ericsson.cds.cdi.support.spock.CdiSpecification

class ComAAInfoDtoTest extends CdiSpecification {

    def primary_ipv4_address = "192.168.0.1"
    def fallback_ipv4_address = "192.168.0.2"
    def primary_ipv6_address = "::ffff:10.120.78.40"
    def fallback_ipv6_address = "::ffff:10.120.78.41"
    def ldapTlsPort = 1636
    def ldapsPort = 1389

    def "no-args constructor" () {
        given:
        when:
            ComAAInfoDto comAAInfoDto = new ComAAInfoDto()
        then:
            comAAInfoDto !=null
        and:
        comAAInfoDto.getIpv4AddressPrimary() == null &&
                comAAInfoDto.getIpv4AddressFallback() == null &&
                comAAInfoDto.getIpv6AddressPrimary() == null &&
                comAAInfoDto.getIpv6AddressFallback() == null &&
                comAAInfoDto.getLdapTlsPort() == 0 &&
                comAAInfoDto.getLdapsPort() == 0

    }

    def "constructor with parameters" () {
        given:
        when:
            ComAAInfoDto comAAInfoDto = new ComAAInfoDto(primary_ipv4_address, fallback_ipv4_address,
                    primary_ipv6_address, fallback_ipv6_address, ldapTlsPort, ldapsPort )
        then:
        comAAInfoDto.getIpv4AddressPrimary() == primary_ipv4_address &&
                comAAInfoDto.getIpv4AddressFallback() == fallback_ipv4_address &&
                comAAInfoDto.getIpv6AddressPrimary() == primary_ipv6_address &&
                comAAInfoDto.getIpv6AddressFallback() == fallback_ipv6_address &&
                comAAInfoDto.getLdapTlsPort() == ldapTlsPort &&
                comAAInfoDto.getLdapsPort() == ldapsPort
    }

    def "set parameters" () {
        given:
            ComAAInfoDto comAAInfoDto = new ComAAInfoDto()
        when:
        comAAInfoDto.setIpv4AddressPrimary(primary_ipv4_address)
                comAAInfoDto.setIpv4AddressFallback(fallback_ipv4_address)
                comAAInfoDto.setIpv6AddressPrimary(primary_ipv6_address)
                comAAInfoDto.setIpv6AddressFallback(fallback_ipv6_address)
                comAAInfoDto.setLdapTlsPort(ldapTlsPort)
                comAAInfoDto.setLdapsPort(ldapsPort)
        then:
        comAAInfoDto.getIpv4AddressPrimary() == primary_ipv4_address &&
                comAAInfoDto.getIpv4AddressFallback() == fallback_ipv4_address &&
                comAAInfoDto.getIpv6AddressPrimary() == primary_ipv6_address &&
                comAAInfoDto.getIpv6AddressFallback() == fallback_ipv6_address &&
                comAAInfoDto.getLdapTlsPort() == ldapTlsPort &&
                comAAInfoDto.getLdapsPort() == ldapsPort
    }

}
