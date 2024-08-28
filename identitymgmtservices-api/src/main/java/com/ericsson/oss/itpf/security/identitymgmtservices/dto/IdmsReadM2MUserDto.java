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

public class IdmsReadM2MUserDto extends IdmsGeneralConfigM2MUserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userState;
    private int uidNumber;
    private int gidNumber;
    private String expiryTimestamp;

    public IdmsReadM2MUserDto() {}

    public IdmsReadM2MUserDto(String userName, String userState, String groupName, String homeDir,
                              int uidNumber, int gidNumber, String expiryTimestamp ) {
        super(userName, groupName, homeDir);
        this.uidNumber = uidNumber;
        this.gidNumber = gidNumber;
        this.userState = userState;
        this.expiryTimestamp = expiryTimestamp;
    }

    public String getUserState() { return userState;}

    public void setUserState(String userState) { this.userState = userState;}

    public int getUidNumber() {
        return uidNumber;
    }

    public void setUidNumber(int uidNumber) {
        this.uidNumber = uidNumber;
    }

    public int getGidNumber() {
        return gidNumber;
    }

    public void setGidNumber(int gidNumber) {
        this.gidNumber = gidNumber;
    }

    public String getExpiryTimestamp() { return expiryTimestamp; }

    public void setExpiryTimestamp(String expiryTimestamp) { this.expiryTimestamp = expiryTimestamp;}
}
