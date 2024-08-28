package com.ericsson.oss.itpf.security.identitymgmtservices.enums

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import spock.lang.Unroll

import static com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus.valueOfProxyAgentAccoountAdminStatus

class ProxyAgentAccountAdminStatusTest extends  CdiSpecification {

    @Unroll
    def "get ProxyAgentAccountAdminStatus with admin status '#state' " () {
        given:
        when:
        def adminStatus = ProxyAgentAccountAdminStatus.valueOfProxyAgentAccoountAdminStatus(state)
        then:
        adminStatus == expected
        where:
        state      || expected
        "disabled" || ProxyAgentAccountAdminStatus.DISABLED
        "enabled"  || ProxyAgentAccountAdminStatus.ENABLED
        null       || null
    }

    @Unroll
    def "get ProxyAgentAccountAdminStatus with input #state " () {
        given:
        when:
        ProxyAgentAccountAdminStatus adminStatus = valueOfProxyAgentAccoountAdminStatus(state);
        then:
        adminStatus.getProxyAgentAccountAdminStatus() == expected
        where:
        state      | expected
        "enabled"  | "enabled"
        "disabled" | "disabled"
    }
}
