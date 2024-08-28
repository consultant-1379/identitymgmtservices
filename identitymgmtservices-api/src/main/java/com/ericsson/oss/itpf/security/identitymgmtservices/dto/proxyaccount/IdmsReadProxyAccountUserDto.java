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
package com.ericsson.oss.itpf.security.identitymgmtservices.dto.proxyaccount;

import com.ericsson.oss.itpf.security.identitymgmtservices.dto.IdmsUserDnStateDto;

import java.io.Serializable;

public class IdmsReadProxyAccountUserDto extends IdmsUserDnStateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String proxyEncryptedPwd;

    public IdmsReadProxyAccountUserDto() {}

    public IdmsReadProxyAccountUserDto(String userNameDn, String userStateDn, String encryptedPwd) {
        super(userNameDn, userStateDn);
        this.proxyEncryptedPwd = encryptedPwd;
    }

    public String getEncryptedPwd() {
        return proxyEncryptedPwd;
    }

    public void setEncryptedPwd(String encryptedPwd) {
        this.proxyEncryptedPwd = encryptedPwd;
    }
}
