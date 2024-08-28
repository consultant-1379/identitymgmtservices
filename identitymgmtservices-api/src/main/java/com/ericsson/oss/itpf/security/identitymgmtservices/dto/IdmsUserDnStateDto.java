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

public class IdmsUserDnStateDto extends IdmsUserDnDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userStateDn;

    public IdmsUserDnStateDto() {}

    public IdmsUserDnStateDto(String userNameDn, String userStateDn) {
        super(userNameDn);
        this.userStateDn = userStateDn;
    }

    public String getUserStateDn() {
        return userStateDn;
    }

    public void setUserStateDn(String userStateDn) {
        this.userStateDn = userStateDn;
    }
}
