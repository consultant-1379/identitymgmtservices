package com.ericsson.oss.itpf.security.identitymgmtservices

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus

class ProxyAgentAccountDetailsTest extends CdiSpecification {
    def userDnTest = "cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
    def adminStatusTest = ProxyAgentAccountAdminStatus.DISABLED
    // format is "yyMMddHHmmss"
    def createTimestampTest = 100000L
    def lastLoginTimeTest = 50000L

    def "set parameters " () {
        given:
        ProxyAgentAccountDetails proxyAgentAccountDetails = new ProxyAgentAccountDetails()
        when:
        proxyAgentAccountDetails.setUserDn(userDnTest)
        proxyAgentAccountDetails.setAdminStatus(adminStatusTest)
        proxyAgentAccountDetails.setCreateTimestamp(createTimestampTest)
        proxyAgentAccountDetails.setLastLoginTime(lastLoginTimeTest)
        then:
        proxyAgentAccountDetails.getUserDn() ==  userDnTest &&
                proxyAgentAccountDetails.getAdminStatus() == adminStatusTest &&
                proxyAgentAccountDetails.getCreateTimestamp() == createTimestampTest &&
                proxyAgentAccountDetails.getLastLoginTime() == lastLoginTimeTest
    }
}
