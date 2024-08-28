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

public class IdmsReadM2MUserExtDto extends IdmsReadM2MUserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String m2mEncryptedPassword;

    public IdmsReadM2MUserExtDto() {}

    public IdmsReadM2MUserExtDto(String userName, String userState, String groupName, String homeDir,
                                 int uidNumber, int gidNumber, String expiryTimestamp) {
        super(userName, userState, groupName, homeDir, uidNumber, gidNumber, expiryTimestamp);
        this.m2mEncryptedPassword = null;
    }

    public String getM2mencryptedPassword() {
        return m2mEncryptedPassword;
    }

    public void setM2mencryptedPassword(String m2mEncryptedPassword) {
        this.m2mEncryptedPassword = m2mEncryptedPassword;
    }

    public void setAdditionalParameters ( final String encryptedPassword) {
        setM2mencryptedPassword(encryptedPassword);
    }
}
