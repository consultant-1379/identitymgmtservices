/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
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
 * This class contains attributes of an M2M (e.g.,SFTP) user account except the password. There are no setter methods for the fields of this class,
 * since the user object should not be updated once created.
 * 
 * @author TOR/ENM Identity Management/Access Control
 * @version 1.0
 *
 *  Now used by :
 *      identitymgmt-services
 *      ecim-pm-handlers-code
 *      smrs.service
 *      ap-workflow-ecim-ear
 *      ap-workflow-macro
 *      nodecmd-job-service
 *      pmul-service
 *      nodecmd-admin-service
 *      node-security-ear
 *      network-model-retriever-tool
 */
public class M2MUser implements Serializable {

    private static final long serialVersionUID = 3351290029699072782L;

    private String userName;
    private String groupName;
    private int uidNumber;
    private int gidNumber;
    private String homeDir;
    private String expiryTimestamp;

    /**
     * @param userName
     *            - String User Name
     * @param groupName
     *            - String Group Name
     * @param uidNumber
     *            - int User Id Number
     * @param gidNumber
     *            - int Group Id Number
     * @param homeDir
     *            - String Home Directory (absolute path)
     * @param expiryTimestamp
     *            - String Account Expiration Timestamp (in "Zulu" format; e.g., 20140319141844Z empty string if not set)
     */
    public M2MUser(final String userName, final String groupName, final int uidNumber, final int gidNumber, final String homeDir,
                   final String expiryTimestamp) {
        this.userName = userName;
        this.groupName = groupName;
        this.uidNumber = uidNumber;
        this.gidNumber = gidNumber;
        this.homeDir = homeDir;
        this.expiryTimestamp = expiryTimestamp;
    }

    public String getUserName() {
        return userName;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getUidNumber() {
        return uidNumber;
    }

    public int getGidNumber() {
        return gidNumber;
    }

    public String getHomeDir() {
        return homeDir;
    }

    public String getExpiryTimestamp() {
        return expiryTimestamp;
    }

}
