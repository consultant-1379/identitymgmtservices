/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.enums;

public enum ProxyAgentAccountAdminStatus {

    DISABLED("disabled"),
    ENABLED("enabled");

    private final String proxyAgentAccountAdminStatusItem;

    ProxyAgentAccountAdminStatus(String proxyAgentAccountAdminStatusItem) {
        this.proxyAgentAccountAdminStatusItem = proxyAgentAccountAdminStatusItem;
    }

    public String getProxyAgentAccountAdminStatus() {
        return proxyAgentAccountAdminStatusItem;
    }

    public static ProxyAgentAccountAdminStatus valueOfProxyAgentAccoountAdminStatus(String proxyAgentAccountAdminStatusItem) {
        for (ProxyAgentAccountAdminStatus e : values()) {
            if (e.proxyAgentAccountAdminStatusItem.equals(proxyAgentAccountAdminStatusItem)) {
                return e;
            }
        }
        return null;
    }
}
