/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.spock.CdiSpecification

class ProxyAgentAccountDataTest extends CdiSpecification {

    private static final String USER_DN_UNDER_TEST = "userDN"
    private static final String PWD_UNDER_TEST = "password"
    private static final String BUILD_MSG_PWD_OK = "ProxyAgentAccountData = { userDN = userDN userPassword =pas***** "
    private static final String BUILD_MSG_PWD_TOO_SHORT = "ProxyAgentAccountData = { userDN = userDN userPassword =p* "
    private static final String BUILD_MSG_PWD_NULL = "ProxyAgentAccountData = { userDN = userDN userPassword = null "

    private static final int HASH_CODE_NULL_FIELDS = 31 * 31 * 31

    ProxyAgentAccountData proxyAgentAccountData = new ProxyAgentAccountData(USER_DN_UNDER_TEST,PWD_UNDER_TEST)
    ProxyAgentAccountData proxyAgentAccountDataToCompare = new ProxyAgentAccountData(USER_DN_UNDER_TEST,PWD_UNDER_TEST)

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def setup() {

    }

    def 'verify proxyAgentAccountData getter  ' () {
        given:
        when:
            String userDN = proxyAgentAccountData.getUserDN()
            String pwd = proxyAgentAccountData.getUserPassword()
        then:
            userDN == USER_DN_UNDER_TEST && pwd == PWD_UNDER_TEST
    }

    def 'verify proxyAgentAccountData hashcode when field are not null  ' () {
        given:
            int resultUnderTest = HASH_CODE_NULL_FIELDS
        when:
            int result = proxyAgentAccountData.hashCode()
        then:
            result != resultUnderTest
    }

    def 'verify proxyAgentAccountData are equal ' () {
        given:
        when:
            boolean result = proxyAgentAccountData == proxyAgentAccountDataToCompare
        then:
            result
    }

    def 'verify proxyAgentAccountData is equal to itself ' () {
        given:
        when:
            boolean result = proxyAgentAccountData.equals(proxyAgentAccountData)
        then:
            result
    }
    def 'verify proxyAgentAccountData is not equal when compare to null object ' () {
        given:
        when:
            def result = proxyAgentAccountData.equals(null)
        then:
            !result
    }

    def 'verify proxyAgentAccountData is not equal when compare to different class ' () {
        given:
        IdentityManagementServiceException identityManagementServiceException = new IdentityManagementServiceException()
        when:
            def result = proxyAgentAccountData == identityManagementServiceException
        then:
            !result
    }

    def 'verify proxyAgentAccountData is not equal with different userDN ' () {
        given:
            ProxyAgentAccountData proxyAgentAccountDataUserDnDiff = new ProxyAgentAccountData("userDn1",PWD_UNDER_TEST)
        when:
            boolean result = proxyAgentAccountData == proxyAgentAccountDataUserDnDiff
        then:
            !result
    }

    def 'verify proxyAgentAccountData is not equal with different password ' () {
        given:
            ProxyAgentAccountData proxyAgentAccountDataUserDnDiff = new ProxyAgentAccountData(USER_DN_UNDER_TEST,"pwd1")
        when:
            boolean result = proxyAgentAccountData == proxyAgentAccountDataUserDnDiff
        then:
            !result
    }

    def 'verify proxyAgentAccountData string with password not null and length higher than three ' () {
        given:
        when:
            String message = proxyAgentAccountData.toString()
        then:
            message.equals(BUILD_MSG_PWD_OK)
    }

    def 'verify proxyAgentAccountData string with password not null and length less than three ' () {
        given:
            ProxyAgentAccountData proxyAgentAccountDataUserDnDiff = new ProxyAgentAccountData(USER_DN_UNDER_TEST,"pw")
        when:
            String message = proxyAgentAccountDataUserDnDiff.toString()
        then:
            message.equals(BUILD_MSG_PWD_TOO_SHORT)
    }


    def 'verify proxyAgentAccountData string with password null' () {
        given:
            ProxyAgentAccountData proxyAgentAccountDataUserDnDiff = new ProxyAgentAccountData(USER_DN_UNDER_TEST,null)
        when:
            String message = proxyAgentAccountDataUserDnDiff.toString()
        then:
            message.equals(BUILD_MSG_PWD_NULL)
    }
}
