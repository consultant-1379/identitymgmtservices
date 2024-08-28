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
package com.ericsson.oss.itpf.security.identitymgmtservices.dto;

import java.io.Serializable;

public class IdmsUserStateDto extends IdmsUserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userState;

    public IdmsUserStateDto() {}

    public IdmsUserStateDto(String userName, String userState) {
        super(userName);
        this.userState = userState;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

}
