package com.ericsson.oss.itpf.security.identitymgmtservices.enums

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import spock.lang.Unroll

class IdmsUserStateTest extends CdiSpecification {

    @Unroll
    def "get idms user state with state '#state'" () {
        given:
        when:
        def idmsUserState = IdmsUserState.getIdmsUserState(state)
        then:
        idmsUserState == expected
        where:
        state      || expected
        "userExists" || IdmsUserState.IDMS_USER_EXISTING
        "userDeleted"  || IdmsUserState.IDMS_USER_DELETED
        "userNotDeleted"  ||IdmsUserState.IDMS_USER_NOT_DELETED
        ""         || null
        null       || null
    }
}
