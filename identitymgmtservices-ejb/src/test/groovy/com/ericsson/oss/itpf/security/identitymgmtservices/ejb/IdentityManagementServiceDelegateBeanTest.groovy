package com.ericsson.oss.itpf.security.identitymgmtservices.ejb

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementService
import com.ericsson.oss.itpf.security.identitymgmtservices.M2MUser
import com.ericsson.oss.itpf.security.identitymgmtservices.M2MUserPassword
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsConfigM2MUserDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsPasswordDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsReadM2MUserDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsReadM2MUserExtDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsUserDto
import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsUserStateDto
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.IdmsUserState

class IdentityManagementServiceDelegateBeanTest extends CdiSpecification {

    @ObjectUnderTest
    IdentityManagementServiceDelegateBean identityManagementServiceDelegateBean

    def userName = "foousername"
    def groupName = "mm-smrsusers"
    def homeDir = "/home/foousername"
    def validDays = 60

    def uid = 4000
    def gid = 5000
    def expiryTimestamp = "timestamp"
    def userPassword = "uvu42tgu"
    def encryptedUserPassword = "encryptedUserPassword"
    def isDeleted = true
    def isExisting = true

    @ImplementationInstance
    IdentityManagementService identityManagementService = [
            createM2MUserPassword : { String username, String groupname, String homedir, int validDays ->
                return new M2MUserPassword (userName, groupName, uid, gid, homeDir, expiryTimestamp, userPassword)

            },
            deleteM2MUser : { String username ->
                return isDeleted
            },
            getM2MUser : { String username ->
                new M2MUser (userName, groupName, uid, gid, homeDir, expiryTimestamp)
            },
            isExistingM2MUser : { String username ->
                return isExisting
            },
            getM2MPassword : { String username ->
                return userPassword.toCharArray()
            },
            updateM2MPassword : { String username ->
                return userPassword.toCharArray()
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

    def setup (){}

    def "configM2MUserPassword"() {
        given:
            def idmsConfigM2MUser = new IdmsConfigM2MUserDto(userName, groupName, homeDir, validDays)
        when:
        IdmsReadM2MUserExtDto idmsReadM2MUserExtDto =
                identityManagementServiceDelegateBean.configM2MUserPassword(idmsConfigM2MUser, _ as String, _ as String, _ as String)
        then:
            idmsReadM2MUserExtDto != null
        and:
        idmsReadM2MUserExtDto.userName == userName &&
                idmsReadM2MUserExtDto.groupName ==  groupName &&
                idmsReadM2MUserExtDto.homeDir == homeDir &&
                idmsReadM2MUserExtDto.gidNumber == gid &&
                idmsReadM2MUserExtDto.uidNumber == uid &&
                idmsReadM2MUserExtDto.expiryTimestamp == expiryTimestamp &&
                idmsReadM2MUserExtDto.m2mEncryptedPassword == encryptedUserPassword &&
                idmsReadM2MUserExtDto.userState == IdmsUserState.IDMS_USER_EXISTING.toString()
    }

    def "deleteM2MUser when existing"() {
        given:
            isDeleted = true
            def idmsUser = new IdmsUserDto(userName)
        when:
            IdmsUserStateDto idmsUserStateDto =
                identityManagementServiceDelegateBean.deleteM2MUser(idmsUser, _ as String, _ as String, _ as String)
        then:
            idmsUserStateDto != null
        and:
            idmsUserStateDto.userName == userName &&
                    idmsUserStateDto.userState == IdmsUserState.IDMS_USER_DELETED.toString()
    }

    def "deleteM2MUser when not existing"() {
        given:
            isDeleted = false
            def idmsUser = new IdmsUserDto(userName)
        when:
            IdmsUserStateDto idmsUserStateDto =
                identityManagementServiceDelegateBean.deleteM2MUser(idmsUser, _ as String, _ as String, _ as String)
        then:
            idmsUserStateDto != null
        and:
            idmsUserStateDto.userName == userName &&
                idmsUserStateDto.userState == IdmsUserState.IDMS_USER_NOT_DELETED.toString()
    }

    def "getM2MUser"() {
        given:
            def idmsUser = new IdmsUserDto(userName)
        when:
            IdmsReadM2MUserDto idmsReadM2MUserDto =
                identityManagementServiceDelegateBean.getM2MUser(idmsUser, _ as String, _ as String, _ as String)
        then:
            idmsReadM2MUserDto != null
        and:
            idmsReadM2MUserDto.userName == userName &&
                    idmsReadM2MUserDto.groupName ==  groupName &&
                    idmsReadM2MUserDto.homeDir == homeDir &&
                    idmsReadM2MUserDto.gidNumber == gid &&
                    idmsReadM2MUserDto.uidNumber == uid &&
                    idmsReadM2MUserDto.expiryTimestamp == expiryTimestamp &&
                    idmsReadM2MUserDto.userState == IdmsUserState.IDMS_USER_EXISTING.toString()

    }

    def "isExistingM2MUser when user exists"() {
        given:
            isExisting = true
            def idmsUser = new IdmsUserDto(userName)
        when:
        IdmsUserStateDto idmsUserStateDto =
                identityManagementServiceDelegateBean.isExistingM2MUser(idmsUser, _ as String, _ as String, _ as String)
        then:
            idmsUserStateDto != null
        and:
            idmsUserStateDto.userName == userName &&
                    idmsUserStateDto.userState == IdmsUserState.IDMS_USER_EXISTING.toString()

    }

    def "isExistingM2MUser when user NOT exists"() {
        given:
            isExisting = false
            def idmsUser = new IdmsUserDto(userName)
        when:
            IdmsUserStateDto idmsUserStateDto =
                identityManagementServiceDelegateBean.isExistingM2MUser(idmsUser, _ as String, _ as String, _ as String)
        then:
            idmsUserStateDto != null
        and:
            idmsUserStateDto.userName == userName &&
                idmsUserStateDto.userState == IdmsUserState.IDMS_USER_DELETED.toString()

    }

    def "getM2MPassword"() {
        given:
            def idmsUser = new IdmsUserDto(userName)
        when:
            IdmsPasswordDto idmsPasswordDto =
                    identityManagementServiceDelegateBean.getM2MPassword(idmsUser, _ as String, _ as String, _ as String)
        then:
            idmsPasswordDto != null
        and:
            idmsPasswordDto.encryptedPwd == encryptedUserPassword
    }

    def "updateM2MPassword"() {
        given:
            def idmsUser = new IdmsUserDto(userName)
        when:
            IdmsPasswordDto idmsPasswordDto =
                identityManagementServiceDelegateBean.updateM2MPassword(idmsUser, _ as String, _ as String, _ as String)
        then:
            idmsPasswordDto != null
        and:
            idmsPasswordDto.encryptedPwd == encryptedUserPassword
    }
}
