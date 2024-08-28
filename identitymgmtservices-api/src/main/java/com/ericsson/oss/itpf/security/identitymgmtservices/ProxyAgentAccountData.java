/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
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
import java.util.Objects;

/**
 * This class contains attributes of LDAP Proxy Agent Account (e.g.COM based nodes). There are no setter methods for the fields of this class,
 * since the user object should not be updated once created.
 *
 * @author TOR/ENM Identity Management/Access Control
 * @version 1.0
 *
 *  Now used by :
 *      identitymgmt-services
 *      node-security-ear
 *      ap-workflow-ecim-ear
 *      ap-service-core
 *      nodecmd-job-service
 *      pmul-service
 *      nodecmd-admin-service
 *      ap-workflow-macro
 *      smrs.service
 *      network-model-retriever-tool
 */

public class ProxyAgentAccountData implements Serializable{

    private static final long serialVersionUID = 2198345671203L;


    private final String userDN;
    private final String userPassword;

    /**
     * @param userDN
     *            - String User DN (Distinguished Name)
     * @param userPassword
     *            - String User Password in plain text
     */

    public ProxyAgentAccountData(final String userDN, final String userPassword) {
        this.userDN = userDN;
        this.userPassword = userPassword;

    }

    /**
     * @return user DN (Distinguished Name)
     */
    public String getUserDN() {
        return userDN;
    }

    /**
     * @return user Password in plain text
     */
    public String getUserPassword() {
        return userPassword;
    }

    private String replaceWithAllStar(final String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            sb.append('*');
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProxyAgentAccountData = { ");
        sb.append("userDN = ").append(getUserDN()).append(" ");

        if (userPassword != null) {
            if (userPassword.length() <= 3) {
                /*print out the first char of the password, and show the rest of them as "*" */
                sb.append("userPassword =").append(getUserPassword().substring(0, 1))
                        .append(replaceWithAllStar(getUserPassword().substring(1))).append(" ");
            } else {
                /*print out 3 first chars of the password, and show the rest of them as "*" */
                sb.append("userPassword =").append(getUserPassword().substring(0, 3))
                        .append(replaceWithAllStar(getUserPassword().substring(3))).append(" ");
            }
        }else {
            sb.append("userPassword = null ");
        }
        return sb.toString();
    }

    @Override
    public int hashCode(){
        return Objects.hash(userDN, userPassword);
    }


    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final ProxyAgentAccountData other = (ProxyAgentAccountData) obj;
        return Objects.equals(userDN, other.userDN) && Objects.equals(userPassword, other.userPassword);
    }
}
