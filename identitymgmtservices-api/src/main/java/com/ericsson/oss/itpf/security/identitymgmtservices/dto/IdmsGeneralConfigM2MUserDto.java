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

public class IdmsGeneralConfigM2MUserDto extends IdmsUserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String groupName;
    private String homeDir;

    public IdmsGeneralConfigM2MUserDto() {}

    public IdmsGeneralConfigM2MUserDto(String userName, String groupName, String homeDir) {
        super(userName);
        this.groupName = groupName;
        this.homeDir = homeDir;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getHomeDir() {
        return homeDir;
    }

    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }

}
