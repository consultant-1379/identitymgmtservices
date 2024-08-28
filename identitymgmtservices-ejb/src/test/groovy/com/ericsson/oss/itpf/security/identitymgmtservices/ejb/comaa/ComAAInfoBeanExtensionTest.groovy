package com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.ConnectionData


class ComAAInfoBeanExtensionTest extends CdiSpecification {

    @ObjectUnderTest
    ComAAInfoBean comAAInfoBean

    def primary_ipv4_address = "192.168.0.1"
    def fallback_ipv4_address = "192.168.0.2"
    def primary_ipv6_address = "::ffff:10.120.78.40"
    def fallback_ipv6_address = "::ffff:10.120.78.41"

    def primary_ipv4_address_split = "192.168.0.1/"
    def fallback_ipv4_address_split = "192.168.0.2/"
    def primary_ipv6_address_split = "::ffff:10.120.78.40/"
    def fallback_ipv6_address_split = "::ffff:10.120.78.41/"


    def primary_ipv4_address_lb = "10.0.0.1"
    def primary_ipv6_address_lb = "2001:0db8:85a3:0000:0000:8a2e:0370:7334"

    def ldapTlsPort = "1636"
    def ldapsPort = "1389"

    @ImplementationInstance
    ComAAInfoUtils comAAInfoUtilsL4IpAddrErrorsPhase1 = [
            getSystemParemeters: { var1, var2 ->
                if (var1 == "SECURITY_LB_IP") {
                    return null
                } else if ( var1 == "SECURITY_LB_IP6") {
                    return null
                } else if ( var1 == "comaa_primary_vip") {
                    return ComAAConstants.BLANKED_STRING
                } else if ( var1 == "comaa_secondary_vip") {
                    return null
                } else if ( var1 == "comaa_ipv6_primary_vip") {
                    return ComAAConstants.EMPTY_STRING
                } else if ( var1 == "comaa_ipv6_secondary_vip") {
                    return ComAAConstants.BLANKED_STRING
                } else if ( var1 == "comaa_ldap_tls_port" ) {
                    return ldapTlsPort
                } else {
                    return ldapsPort
                }
            }
    ] as ComAAInfoUtils

    @ImplementationInstance
    ComAAInfoUtils comAAInfoUtilsL4PortErrorsPhase1 = [
            getSystemParemeters: { var1, var2 ->
                if (var1 == "SECURITY_LB_IP") {
                    return null
                } else if ( var1 == "SECURITY_LB_IP6") {
                    return null
                } else if ( var1 == "comaa_primary_vip") {
                    return primary_ipv4_address
                } else if ( var1 == "comaa_secondary_vip") {
                    return fallback_ipv4_address
                } else if ( var1 == "comaa_ipv6_primary_vip") {
                    return primary_ipv6_address
                } else if ( var1 == "comaa_ipv6_secondary_vip") {
                    return fallback_ipv6_address
                } else if ( var1 == "comaa_ldap_tls_port" ) {
                    return null
                } else {
                    return ComAAConstants.EMPTY_STRING
                }
            }
    ] as ComAAInfoUtils

    @ImplementationInstance
    ComAAInfoUtils comAAInfoUtilsL4IpAddrToBeSplit = [
            getSystemParemeters: { var1, var2 ->
                if (var1 == "SECURITY_LB_IP") {
                    return null
                } else if ( var1 == "SECURITY_LB_IP6") {
                    return null
                } else if ( var1 == "comaa_primary_vip") {
                    return primary_ipv4_address_split
                } else if ( var1 == "comaa_secondary_vip") {
                    return fallback_ipv4_address_split
                } else if ( var1 == "comaa_ipv6_primary_vip") {
                    return primary_ipv6_address_split
                } else if ( var1 == "comaa_ipv6_secondary_vip") {
                    return fallback_ipv6_address_split
                } else if ( var1 == "comaa_ldap_tls_port" ) {
                    return ldapTlsPort
                } else {
                    return ldapsPort
                }
            }
    ] as ComAAInfoUtils

    @ImplementationInstance
    ComAAInfoUtils comAAInfoUtilsL4PortErrorsPhase2 = [
            getSystemParemeters: { var1, var2 ->
                if (var1 == "SECURITY_LB_IP") {
                    return null
                } else if ( var1 == "SECURITY_LB_IP6") {
                    return null
                } else if ( var1 == "comaa_primary_vip") {
                    return primary_ipv4_address
                } else if ( var1 == "comaa_secondary_vip") {
                    return fallback_ipv4_address
                } else if ( var1 == "comaa_ipv6_primary_vip") {
                    return primary_ipv6_address
                } else if ( var1 == "comaa_ipv6_secondary_vip") {
                    return fallback_ipv6_address
                } else {
                    return ComAAConstants.BLANKED_STRING
                }
            }
    ] as ComAAInfoUtils


    @ImplementationInstance
    ComAAInfoUtils comAAInfoUtilsLbHp = [
            getSystemParemeters: { var1, var2 ->
                if (var1 == "SECURITY_LB_IP") {
                    return primary_ipv4_address_lb
                } else if ( var1 == "SECURITY_LB_IP6") {
                    return primary_ipv6_address_lb
                } else if ( var1 == "comaa_ldap_tls_port" ) {
                    return ldapTlsPort
                } else {
                    return ldapsPort
                }
            }
    ] as ComAAInfoUtils

    @ImplementationInstance
    ComAAInfoUtils comAAInfoUtilsLbOnlyIpv4 = [
            getSystemParemeters: { var1, var2 ->
                if (var1 == "SECURITY_LB_IP") {
                    return primary_ipv4_address_lb
                } else if ( var1 == "SECURITY_LB_IP6") {
                    return null
                } else if ( var1 == "comaa_ldap_tls_port" ) {
                    return ldapTlsPort
                } else {
                    return ldapsPort
                }
            }
    ] as ComAAInfoUtils

    @ImplementationInstance
    ComAAInfoUtils comAAInfoUtilsLbOnlyIpv6 = [
            getSystemParemeters: { var1, var2 ->
                if (var1 == "SECURITY_LB_IP") {
                    return null
                } else if ( var1 == "SECURITY_LB_IP6") {
                    return primary_ipv6_address_lb
                } else if ( var1 == "comaa_ldap_tls_port" ) {
                    return ldapTlsPort
                } else {
                    return ldapsPort
                }
            }
    ] as ComAAInfoUtils

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def setup() {}

    def ' get ComAAInfo connection data INGRESS_L4 Happy path'() {
        given:
        comAAInfoBean.comAAInfoUtils = new ComAAInfoUtils()

        System.setProperty(ComAALdapPort.COMAA_LDAP_TLS_PORT.getLdapPort(), ldapTlsPort)
        System.setProperty(ComAALdapPort.COMAA_LDAPS_PORT.getLdapPort(), ldapsPort)

        System.setProperty(ComAAVipAddress.COMAA_PRIMARY_VIP.getVipAddress(), primary_ipv4_address)
        System.setProperty(ComAAVipAddress.COMAA_SECONDARY_VIP.getVipAddress(), fallback_ipv4_address)
        System.setProperty(ComAAVipAddress.COMAA_IPV6_PRIMARY_VIP.getVipAddress(), primary_ipv6_address)
        System.setProperty(ComAAVipAddress.COMAA_IPV6_SECONDARY_VIP.getVipAddress(), fallback_ipv6_address)

        when:
        final ConnectionData connectionData = comAAInfoBean.getConnectionData()
        then:
        connectionData.ipv4AddressData.primary == primary_ipv4_address &&
                connectionData.ipv4AddressData.fallback == fallback_ipv4_address &&
                connectionData.ipv6AddressData.primary == primary_ipv6_address &&
                connectionData.ipv6AddressData.fallback == fallback_ipv6_address &&
                connectionData.ldapTlsPort == Integer.valueOf(ldapTlsPort) &&
                connectionData.ldapsPort == Integer.valueOf(ldapsPort)
    }

    def ' get ComAAInfo connection data LOAD_BALANCER Happy Path' () {
        given:
        comAAInfoBean.comAAInfoUtils = comAAInfoUtilsLbHp
        when:
        final ConnectionData connectionData = comAAInfoBean.getConnectionData()
        then:
        connectionData.ipv4AddressData.primary == primary_ipv4_address_lb &&
                connectionData.ipv6AddressData.primary == primary_ipv6_address_lb &&
                connectionData.ldapTlsPort == Integer.valueOf(ldapTlsPort) &&
                connectionData.ldapsPort == Integer.valueOf(ldapsPort)
    }

    def ' get ComAAInfo connection data LOAD_BALANCER OnlyIpv4 ' () {
        given:
        comAAInfoBean.comAAInfoUtils = comAAInfoUtilsLbOnlyIpv4
        when:
        final ConnectionData connectionData = comAAInfoBean.getConnectionData()
        then:
        connectionData.ipv4AddressData.primary == primary_ipv4_address_lb &&
                connectionData.ipv6AddressData.primary == ComAAConstants.EMPTY_STRING &&
                connectionData.ldapTlsPort == Integer.valueOf(ldapTlsPort) &&
                connectionData.ldapsPort == Integer.valueOf(ldapsPort)
    }

    def ' get ComAAInfo connection data LOAD_BALANCER OnlyIpv6 ' () {
        given:
        comAAInfoBean.comAAInfoUtils = comAAInfoUtilsLbOnlyIpv6
        when:
        final ConnectionData connectionData = comAAInfoBean.getConnectionData()
        then:
        connectionData.ipv4AddressData.primary == ComAAConstants.EMPTY_STRING &&
                connectionData.ipv6AddressData.primary == primary_ipv6_address_lb &&
                connectionData.ldapTlsPort == Integer.valueOf(ldapTlsPort) &&
                connectionData.ldapsPort == Integer.valueOf(ldapsPort)
    }

    def ' get ComAAInfo connection data INGRESS_L4 IpAddress Error' () {
        given:
        comAAInfoBean.comAAInfoUtils = comAAInfoUtilsL4IpAddrErrorsPhase1
        when:
        final ConnectionData connectionData = comAAInfoBean.getConnectionData()
        then:
        connectionData.ipv4AddressData.primary == ComAAConstants.EMPTY_STRING &&
                connectionData.ipv4AddressData.fallback == ComAAConstants.EMPTY_STRING &&
                connectionData.ipv6AddressData.primary == ComAAConstants.EMPTY_STRING &&
                connectionData.ipv6AddressData.fallback == ComAAConstants.EMPTY_STRING &&
                connectionData.ldapTlsPort == Integer.valueOf(ldapTlsPort) &&
                connectionData.ldapsPort == Integer.valueOf(ldapsPort)
    }

    def ' get ComAAInfo connection data INGRESS_L4 IpAddress to be split' () {
        given:
        comAAInfoBean.comAAInfoUtils = comAAInfoUtilsL4IpAddrToBeSplit
        when:
        final ConnectionData connectionData = comAAInfoBean.getConnectionData()
        then:
        connectionData.ipv4AddressData.primary == primary_ipv4_address &&
                connectionData.ipv4AddressData.fallback == fallback_ipv4_address &&
                connectionData.ipv6AddressData.primary == primary_ipv6_address &&
                connectionData.ipv6AddressData.fallback == fallback_ipv6_address &&
                connectionData.ldapTlsPort == Integer.valueOf(ldapTlsPort) &&
                connectionData.ldapsPort == Integer.valueOf(ldapsPort)
    }

    def ' get ComAAInfo connection data INGRESS_L4 Port Error Phase 1'() {
        given:
        comAAInfoBean.comAAInfoUtils = comAAInfoUtilsL4PortErrorsPhase1
        when:
        final ConnectionData connectionData = comAAInfoBean.getConnectionData()
        then:
        connectionData.ipv4AddressData.primary == primary_ipv4_address &&
                connectionData.ipv4AddressData.fallback == fallback_ipv4_address &&
                connectionData.ipv6AddressData.primary == primary_ipv6_address &&
                connectionData.ipv6AddressData.fallback == fallback_ipv6_address &&
                connectionData.ldapTlsPort == ComAAConstants.PORT_UNDEFINED &&
                connectionData.ldapsPort == ComAAConstants.PORT_UNDEFINED
    }

    def ' get ComAAInfo connection data INGRESS_L4 Port Error Phase 2'() {
        given:
        comAAInfoBean.comAAInfoUtils = comAAInfoUtilsL4PortErrorsPhase2
        when:
        final ConnectionData connectionData = comAAInfoBean.getConnectionData()
        then:
        connectionData.ipv4AddressData.primary == primary_ipv4_address &&
                connectionData.ipv4AddressData.fallback == fallback_ipv4_address &&
                connectionData.ipv6AddressData.primary == primary_ipv6_address &&
                connectionData.ipv6AddressData.fallback == fallback_ipv6_address &&
                connectionData.ldapTlsPort == ComAAConstants.PORT_UNDEFINED&&
                connectionData.ldapsPort == ComAAConstants.PORT_UNDEFINED
    }
}