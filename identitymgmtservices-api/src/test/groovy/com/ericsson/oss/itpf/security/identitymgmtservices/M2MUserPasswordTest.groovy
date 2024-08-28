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

class M2MUserPasswordTest extends CdiSpecification {

    private static final String USER_DN_UNDER_TEST = "userDN"
    private static final String PWD_UNDER_TEST = "password"
    private static final String GROUP_NAME_UNDER_TEST = "groupName"
    private static final String HOME_DIR_UNDER_TEST = "homeDir"
    private static final String EXPIRY_TIMESTAMP_UNDER_TEST = "2025-03-12"
    private static final int UID_NUMBER_UNDER_TEST = 1
    private static final int GID_NUMBER_UNDER_TEST = 1

    private static final String BUILD_MSG_PWD_OK = "NodeM2MUser{ userName=userDN, groupName=groupName, uidNumber=1, gidNumber=1, homeDir=homeDir, expiryTimestamp=2025-03-12}"

    M2MUser m2MUser = new M2MUser(
            USER_DN_UNDER_TEST,
            GROUP_NAME_UNDER_TEST,
            UID_NUMBER_UNDER_TEST,
            GID_NUMBER_UNDER_TEST,
            HOME_DIR_UNDER_TEST,
            EXPIRY_TIMESTAMP_UNDER_TEST
    )

    M2MUserPassword m2MUserPassword = new M2MUserPassword(m2MUser, PWD_UNDER_TEST)
    M2MUserPassword m2MUserPasswordSpecific = new M2MUserPassword(
            USER_DN_UNDER_TEST,
            GROUP_NAME_UNDER_TEST,
            UID_NUMBER_UNDER_TEST,
            GID_NUMBER_UNDER_TEST,
            HOME_DIR_UNDER_TEST,
            EXPIRY_TIMESTAMP_UNDER_TEST,
            PWD_UNDER_TEST,
            )

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def setup() {}

    def 'verify m2MUserPassword getter ' () {
        given:
        when:
            String pwd = m2MUserPassword.getPassword()
        then:
            pwd == PWD_UNDER_TEST
    }


    def 'verify m2MUserPassword string builder ' () {
        given:
        when:
            String message = m2MUserPasswordSpecific.toString()
        then:
            message.equals(BUILD_MSG_PWD_OK)
    }
}
