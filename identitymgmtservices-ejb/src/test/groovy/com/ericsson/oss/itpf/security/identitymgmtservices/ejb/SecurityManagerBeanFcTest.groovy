/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.itpf.security.identitymgmtservices.ejb

import com.ericsson.oss.itpf.sdk.core.retry.RetriableCommandException
import com.ericsson.oss.itpf.sdk.core.retry.RetryManager
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity
import com.ericsson.oss.itpf.security.identitymgmtservices.DSCommunicatorException
import com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementServiceException
import com.ericsson.oss.itpf.security.identitymgmtservices.M2MUser
import spock.lang.Shared
import spock.lang.Specification
import java.util.concurrent.locks.Lock;
import javax.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.sdk.cluster.lock.LockManager
import com.ericsson.oss.itpf.security.cryptography.CryptographyService
import com.ericsson.oss.itpf.security.identitymgmtservices.DSCommunicator
import com.ericsson.oss.itpf.security.identitymgmtservices.M2MUserPassword
import com.ericsson.oss.itpf.security.identitymgmtservices.IdmConstants;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.sdk.recording.EventLevel

class SecurityManagerBeanFcTest extends CdiSpecification {

    @ObjectUnderTest
    SecurityManagerBean securityManagerBean

    @MockedImplementation
    DSCommunicator mockedCommunicator

    @MockedImplementation
    Lock m2mLock

    @MockedImplementation
    LockManager lockManager

    @MockedImplementation
    CryptographyService cryptographyService;

    @MockedImplementation
    Logger logger

    @MockedImplementation
    SystemRecorder systemRecorder

    def user = "TestUser"
    def netsimUser = "mm-cert--1536871905"
    def password = "TestPassw0rd"
    def base64Password = "VGVzdFBhc3N3MHJk"
    byte[] base64PasswordByte = [77, -21, 45, 61, -85, 44, -61, 74, -35];
    def gidNumberStringList = ["100", "150"]
    def uidNumberStringList = ["200"]
    def encryptedPassword = "RW5jcnlwdGVkUGFzc293cmQ="
    def authPasswordStrings = ["SHA1\$AESCBC128\$EncryptedPassowrd"]

    def setup () {
        lockManager.getDistributedLock(_ as String) >> m2mLock
        mockedCommunicator.searchEntry(_ as String) >>> [false, true]
        mockedCommunicator.getRandomPassword() >> password
        mockedCommunicator.querySingleAttribute(_, _, IdmConstants.LDAP_GID_NUMBER, _) >> gidNumberStringList
        mockedCommunicator.querySingleAttribute(_, _, IdmConstants.LDAP_UID_NUMBER, _) >> uidNumberStringList
        mockedCommunicator.querySingleAttribute(_, _, IdmConstants.LDAP_AUTH_CRED, _) >> authPasswordStrings
        cryptographyService.encrypt(_ as byte[]) >> encryptedPassword.getBytes()
        cryptographyService.decrypt(_ as byte[]) >> base64PasswordByte
        securityManagerBean.communicator = mockedCommunicator;
        securityManagerBean.init()
    }

    def 'Given an existing M2M user, when get password by API, then returned string match with created user password' () {
    given: 'M2M user with password created for user TestUser'
        M2MUserPassword m2mUserPwd = securityManagerBean.createM2MUser(user, "/home/TestUser", "TestGroup", 1000)
    when:  'get M2M password for user TestUser'
        String readPassword = securityManagerBean.getM2MPassword(user)
    then:  'the password returned by API is not null'
        readPassword != null
    and:  'the password matches with that contained in the M2M user object'
        readPassword.equals(m2mUserPwd.getPassword())
    }

    def 'Given a netsim user name, when M2M user is created with password, then user info is system recorded'() {
    given:
    when: 'M2M user with password is created for netsim user'
        M2MUserPassword m2mUserPwd = securityManagerBean.createM2MUser(netsimUser, "/home/TestUser", "TestGroup", 1000)
    then: 'user info is system recorded'
        m2mUserPwd != null
        1 * systemRecorder.recordEvent(_ as String, EventLevel.COARSE, _ as String, IdmConstants.IDM_SERVICES_DISTINGUISHED_NAME, "")
        1 * systemRecorder.recordSecurityEvent(IdmConstants.SECURITY_EVENT_TARGET_SYSTEM, IdmConstants.IDM_SERVICES_DISTINGUISHED_NAME, _ as String,
                _ as String, ErrorSeverity.INFORMATIONAL, _ as String);

    }

    def 'Given an existing M2M user, when get user by security manager, then returned user match with created user' () {
        given: 'M2M user with password created for user TestUser'
        mockedCommunicator.querySingleAttribute(_, _, IdmConstants.LDAP_FULL_NAME, _) >> ["TestGroup"]
        mockedCommunicator.queryMultipleAttributes(_ as String, _, _ as ArrayList<String>) >> [(IdmConstants.LDAP_GID_NUMBER) : gidNumberStringList,
                                                                                               (IdmConstants.LDAP_UID_NUMBER) : uidNumberStringList,
                                                                                               (IdmConstants.LDAP_HOME_DIRECTORY) : ["/home/users"]]
        securityManagerBean.addM2MUser(user, "/home/TestUser", "TestGroup", 1000)
        when:  'get M2M user'
        M2MUser m2mUser = securityManagerBean.getM2MUser(user)
        then:  'the user returned by API is not null'
        m2mUser != null
        and:  'the user name matches'
        user == m2mUser.getUserName()
    }

    def 'Given an existing M2M user, when get user by security manager with homeDir null , then returned user match with created user' () {
        given:
            mockedCommunicator.querySingleAttribute(_, _, IdmConstants.LDAP_FULL_NAME, _) >> ["TestGroup"]
            mockedCommunicator.queryMultipleAttributes(_ as String, _, _ as ArrayList<String>) >> [(IdmConstants.LDAP_GID_NUMBER) : gidNumberStringList,
                                                                                               (IdmConstants.LDAP_UID_NUMBER) : uidNumberStringList,
                                                                                               (IdmConstants.LDAP_HOME_DIRECTORY) : [null]]
            securityManagerBean.addM2MUser(user, "/home/TestUser", "TestGroup", 1000)
        when:
            securityManagerBean.getM2MUser(user)
        then:
            thrown IdentityManagementServiceException
    }

}
