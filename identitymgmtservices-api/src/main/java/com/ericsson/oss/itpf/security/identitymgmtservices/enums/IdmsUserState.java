/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2021
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.enums;

public enum IdmsUserState {

    IDMS_USER_EXISTING("userExists"),
    IDMS_USER_DELETED("userDeleted"),
    IDMS_USER_NOT_DELETED("userNotDeleted");

    private String userState;

    IdmsUserState(String userState) {
        this.userState = userState;
    }

    public String getUserState() {
        return userState;
    }

    /**
     *
     **/
    public static IdmsUserState getIdmsUserState(final String userState) {
        for (final IdmsUserState idmsUserState : IdmsUserState.values()) {
            if (idmsUserState.getUserState().equals(userState)) {
                return idmsUserState;
            }
        }
        return null;
    }
}
