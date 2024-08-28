/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices;

import java.io.Serializable;

/**
 * This class extends the M2M user account, including the password. There are no setter methods for the fields,
 * since the user object should not be updated once created.
 *
 *  Now used by :
 *      identitymgmt-services
 *      ecim-pm-handlers-code
 *      node-security-ear
 *      ap-workflow-ecim-ear
 *      ap-workflow-macro
 *      smrs.service
 *      network-model-retriever-tool
 */
public class M2MUserPassword extends M2MUser implements Serializable {

    private static final long serialVersionUID = -1036452485900879218L;

    private final String password;

    public M2MUserPassword(String userName, String groupName, int uidNumber, int gidNumber,
                       String homeDir, String expiryTimestamp, String password) {
        super(userName, groupName, uidNumber, gidNumber, homeDir, expiryTimestamp);
        this.password = password;
    }

    public M2MUserPassword(M2MUser user, String password) {
        super(user.getUserName(), user.getGroupName(), user.getUidNumber(), user.getGidNumber(),
              user.getHomeDir(), user.getExpiryTimestamp());
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "NodeM2MUser{ userName=" + getUserName() + ", groupName=" + getGroupName()
                + ", uidNumber=" + getUidNumber() + ", gidNumber=" + getGidNumber()
                + ", homeDir=" + getHomeDir() + ", expiryTimestamp=" + getExpiryTimestamp() + '}';
    }

}
