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

class PosixServiceExceptionTest extends CdiSpecification {

    private static final String USER_DN_UNDER_TEST = "userDN"
    private static final String GROUP_NAME_UNDER_TEST = "groupName"
    private static final String HOME_DIR_UNDER_TEST = "homeDir"
    private static final String EXPIRY_TIMESTAMP_UNDER_TEST = "2025-03-12"
    private static final int UID_NUMBER_UNDER_TEST = 1
    private static final int GID_NUMBER_UNDER_TEST = 1


    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def setup() {}

    def 'verify posixServiceException with field  ' () {
        given:
            String msgExcp = "idmMsgExcpTest"
            IdentityManagementServiceException idmMgmtExcp = new IdentityManagementServiceException(msgExcp)
        when:
            PosixServiceException posixServiceException = new PosixServiceException(idmMgmtExcp)
        then:
            posixServiceException !=null
        and:
            posixServiceException.getMessage() == msgExcp
    }
}
