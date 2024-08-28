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

public class IdmsConfigM2MUserDto extends IdmsGeneralConfigM2MUserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int validDays;

    public IdmsConfigM2MUserDto() {}

    public IdmsConfigM2MUserDto(String userName, String groupName, String homeDir, int validDays) {
        super(userName, groupName, homeDir);
        this.validDays = validDays;
    }

    public int getValidDays() { return validDays; }

    public void setValidDays(int validDays) { this.validDays = validDays;}

}
