/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa;

public enum ComAAVipAddress {

    COMAA_PRIMARY_VIP("comaa_primary_vip"),
    COMAA_SECONDARY_VIP("comaa_secondary_vip"),
    COMAA_IPV6_PRIMARY_VIP("comaa_ipv6_primary_vip"),
    COMAA_IPV6_SECONDARY_VIP("comaa_ipv6_secondary_vip"),

    COMAA_LOAD_BALANCER_VIP("SECURITY_LB_IP"),
    COMAA_IPV6_LOAD_BALANCER_VIP("SECURITY_LB_IP6");

    public final String vipAddress;

    ComAAVipAddress(String vipAddress) {
        this.vipAddress = vipAddress;
    }

    public String getVipAddress() {
        return vipAddress;
    }
}
