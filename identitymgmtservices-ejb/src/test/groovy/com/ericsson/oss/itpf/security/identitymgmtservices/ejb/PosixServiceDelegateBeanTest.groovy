package com.ericsson.oss.itpf.security.identitymgmtservices.ejb

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementService
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementServiceException
import com.ericsson.oss.itpf.security.identitymgmtservices.PosixService
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountCounters
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountData
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountDetails
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountGetData
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsComUserGroupDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.IdmsGetAllProxyAccountDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.IdmsGetAllProxyAccountGetDataDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.IdmsReadProxyAccountUserDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsUserDnDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsUserDnStateDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsUserDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.IdmsConfigGetProxyAccountBaseDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.IdmsConfigGetProxyAccountByAdminStatusDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.IdmsConfigGetProxyAccountByInactivityDateDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount.IdmsConfigUpdateProxyAccountDto
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.IdmsUserState
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus


class PosixServiceDelegateBeanTest extends CdiSpecification {

    @ObjectUnderTest
    PosixServiceDelegateBean posixServiceDelegateBean

    def userDn = "cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com"
    def userPassword = "uvu42tgu"
    def encryptedUserPassword = "encryptedUserPassword"

    def isDeleted = true
    def userName = "foouser"

    ProxyAgentAccountGetData proxyAgentAccountGetData = new ProxyAgentAccountGetData()
    ProxyAgentAccountDetails proxyAgentAccountDetails2 = new ProxyAgentAccountDetails()

    @ImplementationInstance
    PosixService posixService = [
            isComUser : { String userName ->
                return true
            }
    ] as PosixService

    @ImplementationInstance
    IdentityManagementService identityManagementService = [
            createProxyAgentAccount : {
                return new ProxyAgentAccountData(userDn, userPassword)
            },
            deleteProxyAgentAccount : { String userDN ->
                return isDeleted
            },
            getProxyAgentAccount : { Boolean isLegacy, Boolean isSummary ->
                return proxyAgentAccountGetData
            },
            getProxyAgentAccountByAdminStatus : { ProxyAgentAccountAdminStatus adminStatus, Boolean isLegacy, Boolean isSummary ->
                return proxyAgentAccountGetData
            },
            getProxyAgentAccountByInactivityPeriod : { Long inactivityPeriod, Boolean isLegacy, Boolean isSummary ->
                return proxyAgentAccountGetData
            },
            updateProxyAgentAccountAdminStatus : { String userNameDn, ProxyAgentAccountAdminStatus adminStatus ->
                return true;
            },
            getProxyAgentAccountDetails : { String userDn ->
                return proxyAgentAccountDetails2
            }
    ] as IdentityManagementService

    @ImplementationInstance
    PasswordHelper passwordHelper = [
            encryptEncode : { String text ->
                return encryptedUserPassword
            }
    ] as PasswordHelper

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def setup (){
        ProxyAgentAccountCounters proxyAgentAccountCounters = new ProxyAgentAccountCounters()
        List<ProxyAgentAccountDetails> proxyAgentAccountDetailsList = new ArrayList<>()

        ProxyAgentAccountDetails proxyAgentAccountDetails = new ProxyAgentAccountDetails()
        proxyAgentAccountDetails.setUserDn("cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com")
        proxyAgentAccountDetails.setAdminStatus(ProxyAgentAccountAdminStatus.ENABLED)
        proxyAgentAccountDetails.setCreateTimestamp(100000)
        proxyAgentAccountDetails.setLastLoginTime(80000)
        proxyAgentAccountDetailsList.add(proxyAgentAccountDetails)

        proxyAgentAccountGetData.setProxyAgentAccountCounters(proxyAgentAccountCounters)
        proxyAgentAccountGetData.setProxyAgentAccountDetailsList(proxyAgentAccountDetailsList)

        proxyAgentAccountCounters.setNumOfProxyAccount(1)
        proxyAgentAccountCounters.setNumOfRequestedProxyAccount(1)
        proxyAgentAccountCounters.setNumOfProxyAccountLegacy(1)
        proxyAgentAccountCounters.setNumOfRequestedProxyAccountLegacy(1)

        proxyAgentAccountDetails2.setUserDn("cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com")
        proxyAgentAccountDetails2.setAdminStatus(ProxyAgentAccountAdminStatus.ENABLED)
        proxyAgentAccountDetails2.setCreateTimestamp(100000)
        proxyAgentAccountDetails2.setLastLoginTime(80000)
    }

    def "check user is a comUser"() {
        given:
        def idmsUser = new IdmsUserDto(userName)
        when:
        IdmsComUserGroupDto idmsComUserGroupDto =
                posixServiceDelegateBean.isComUser(idmsUser, _ as String, _ as String, _ as String)
        then:
        idmsComUserGroupDto != null
        and:
        idmsComUserGroupDto.userName == userName &&
                idmsComUserGroupDto.comUserGroup ==  "true"
    }

    def "createProxyAgentAccount"() {
        given:
        when:
            IdmsReadProxyAccountUserDto idmsReadProxyAccountUserDto =
                    posixServiceDelegateBean.createProxyAgentAccount(_ as String, _ as String, _ as String)
        then:
            idmsReadProxyAccountUserDto != null
        and:
            idmsReadProxyAccountUserDto.userNameDn == userDn &&
                    idmsReadProxyAccountUserDto.encryptedPwd ==  encryptedUserPassword
    }

    def "deleteProxyAgentAccount with used created "() {
        given:
            isDeleted = true
            def idmsUserDn = new IdmsUserDnDto(userDn)
        when:
            IdmsUserDnStateDto idmsUserDnStateDto =
                posixServiceDelegateBean.deleteProxyAgentAccount(idmsUserDn, _ as String, _ as String, _ as String)
        then:
            idmsUserDnStateDto != null
        and:
            idmsUserDnStateDto.userNameDn == userDn &&
                    idmsUserDnStateDto.userStateDn ==  IdmsUserState.IDMS_USER_DELETED.toString()
    }

    def "deleteProxyAgentAccount with used not created "() {
        given:
            isDeleted = false
            def idmsUserDn = new IdmsUserDnDto(userDn)
        when:
            IdmsUserDnStateDto idmsUserDnStateDto =
                posixServiceDelegateBean.deleteProxyAgentAccount(idmsUserDn, _ as String, _ as String, _ as String)
        then:
            idmsUserDnStateDto != null
        and:
            idmsUserDnStateDto.userNameDn == userDn &&
                idmsUserDnStateDto.userStateDn ==  IdmsUserState.IDMS_USER_NOT_DELETED.toString()
    }

    def "getAllProxyAgentAccount happy path "() {
        given:
        def idmsConfigGetProxyAccountBaseDto = new IdmsConfigGetProxyAccountBaseDto("false", "false")
        when:
        IdmsGetAllProxyAccountGetDataDto idmsGetAllProxyAccountGetDataDto =
                posixServiceDelegateBean.getAllProxyAgentAccount(idmsConfigGetProxyAccountBaseDto, _ as String, _ as String, _ as String)
        then:
        idmsGetAllProxyAccountGetDataDto.getNumOfGetDataProxyAccount() == 1 &&
                idmsGetAllProxyAccountGetDataDto.getNumOfGetDataProxyAccountLegacy() == 1 &&
                idmsGetAllProxyAccountGetDataDto.getNumOfGetDataRequestedProxyAccount() == 1 &&
                idmsGetAllProxyAccountGetDataDto.getNumOfGetDataRequestedProxyAccountLegacy() == 1
    }

    def "getProxyAgentAccountByAdminStatus happy path "() {
        given:
        def idmsConfigGetProxyAccountByAdminStatusDto = new IdmsConfigGetProxyAccountByAdminStatusDto("false", "false", "enabled")
        when:
        IdmsGetAllProxyAccountGetDataDto idmsGetAllProxyAccountGetDataDto =
                posixServiceDelegateBean.getAllProxyAgentAccountByAdminStatus(idmsConfigGetProxyAccountByAdminStatusDto, _ as String, _ as String, _ as String)
        then:
        idmsGetAllProxyAccountGetDataDto.getNumOfGetDataProxyAccount() == 1 &&
                idmsGetAllProxyAccountGetDataDto.getNumOfGetDataProxyAccountLegacy() == 1 &&
                idmsGetAllProxyAccountGetDataDto.getNumOfGetDataRequestedProxyAccount() == 1 &&
                idmsGetAllProxyAccountGetDataDto.getNumOfGetDataRequestedProxyAccountLegacy() == 1
    }

    def "getProxyAgentAccountByInactivityPeriod happy path "() {
        given:
        def idmsConfigGetProxyAccountByInactivityDateDto = new IdmsConfigGetProxyAccountByInactivityDateDto("false", "false", "2023-01-11 12:30:30")
        when:
        IdmsGetAllProxyAccountGetDataDto idmsGetAllProxyAccountGetDataDto =
                posixServiceDelegateBean.getAllProxyAgentAccountByInactivityPeriod(idmsConfigGetProxyAccountByInactivityDateDto, _ as String, _ as String, _ as String)
        then:
        idmsGetAllProxyAccountGetDataDto.getNumOfGetDataProxyAccount() == 1 &&
                idmsGetAllProxyAccountGetDataDto.getNumOfGetDataProxyAccountLegacy() == 1 &&
                idmsGetAllProxyAccountGetDataDto.getNumOfGetDataRequestedProxyAccount() == 1 &&
                idmsGetAllProxyAccountGetDataDto.getNumOfGetDataRequestedProxyAccountLegacy() == 1
    }

    def "getProxyAgentAccountByInactivityPeriod invalid date "() {
        given:
        def idmsConfigGetProxyAccountByInactivityDateDto = new IdmsConfigGetProxyAccountByInactivityDateDto("false", "false", "2023-01-11 12:30")
        when:
        IdmsGetAllProxyAccountGetDataDto idmsGetAllProxyAccountGetDataDto =
                posixServiceDelegateBean.getAllProxyAgentAccountByInactivityPeriod(idmsConfigGetProxyAccountByInactivityDateDto, _ as String, _ as String, _ as String)
        then:
        thrown(IdentityManagementServiceException.class)
    }

    def "getProxyAgentAccountByInactivityPeriod date entry is null "() {
        given:
        def idmsConfigGetProxyAccountByInactivityDateDto = new IdmsConfigGetProxyAccountByInactivityDateDto("false", "false", null)
        when:
        IdmsGetAllProxyAccountGetDataDto idmsGetAllProxyAccountGetDataDto =
                posixServiceDelegateBean.getAllProxyAgentAccountByInactivityPeriod(idmsConfigGetProxyAccountByInactivityDateDto, _ as String, _ as String, _ as String)
        then:
        thrown(IdentityManagementServiceException.class)
    }


    def "updateProxyAgentAccountAdminStatusNew happy path "() {
        given:
        def idmsConfigUpdateProxyAccountDto = new IdmsConfigUpdateProxyAccountDto("enabled")
        when:
        Boolean ret =
                posixServiceDelegateBean.updateProxyAgentAccountAdminStatus(
                        "cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com",
                        idmsConfigUpdateProxyAccountDto,
                        _ as String, _ as String, _ as String)
        then:
        ret == true
    }

    def "getProxyAgentAccountDetailsNew happy path "() {
        given:
        when:
        IdmsGetAllProxyAccountDto idmsGetAllProxyAccountDto = posixServiceDelegateBean.getProxyAgentAccountDetails(
                "cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com",
                _ as String, _ as String, _ as String)
        then:
        idmsGetAllProxyAccountDto.getIdmsUserDn() == "cn=ProxyAccount_38,ou=proxyagent,ou=com,dc=ieatlms5223,dc=com" &&
                idmsGetAllProxyAccountDto.getIdmsAccountDisabled() == "ENABLED"
    }
}
