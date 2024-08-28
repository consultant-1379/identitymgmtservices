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

package com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.ComAAInfo;
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.ConnectionData;
import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.LdapAddress;

import static com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa.ComAAConstants.*;
import static com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa.ComAAInfoSystemType.COM_AA_INFO_SYSTEM_ENV;
import static com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa.ComAAInfoSystemType.COM_AA_INFO_SYSTEM_PROPERTIES;
import static com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa.ComAAInfoVipType.INGRESS_L4;
import static com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa.ComAAInfoVipType.LOAD_BALANCER;
import static com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa.ComAALdapPort.COMAA_LDAPS_PORT;
import static com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa.ComAALdapPort.COMAA_LDAP_TLS_PORT;
import static com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa.ComAAVipAddress.*;

@Stateless
public class ComAAInfoBean implements ComAAInfo {
    private static final Logger logger = LoggerFactory.getLogger(ComAAInfoBean.class);

    @Inject
    ComAAInfoUtils comAAInfoUtils;

    @Override
    public String getCOMAAIpAddress() {
        final String ip = System.getProperty(COMAA_PRIMARY_VIP.getVipAddress());
        logger.debug("COM AA IP: {}", ip);
        return ip;
    }

    @Override
    public String getCOMAAFallbackIPAddress() {
        final String ip = System.getProperty(COMAA_SECONDARY_VIP.getVipAddress());
        logger.debug("COM AA IP FALLBACK: {}", ip);
        return ip;
    }

    /**
     * getConnectionData: build ConnectionData class with the vIP addresses (ipv4 and ipv6)
     *                    and the ldap ports
     * @return the ConnectionData class with the vIP addresses (ipv4 and ipv6) and the ldap ports
     */
    @Override
    public ConnectionData getConnectionData() {
        final String primary;
        final String secondary;
        final String primaryIpv6;
        final String secondaryIpv6;

        ComAAInfoVipType vipType = getVipType();
        logger.info("vip interface type is: {}", vipType);
        switch (vipType) {
            case LOAD_BALANCER:
                primary = secondary = getVipAddress(COMAA_LOAD_BALANCER_VIP, COM_AA_INFO_SYSTEM_ENV);
                primaryIpv6 = secondaryIpv6 = getVipAddress(COMAA_IPV6_LOAD_BALANCER_VIP, COM_AA_INFO_SYSTEM_ENV);
                break;
            case INGRESS_L4:
            default:
                primary = getVipAddress(COMAA_PRIMARY_VIP, COM_AA_INFO_SYSTEM_PROPERTIES);
                secondary = getVipAddress(COMAA_SECONDARY_VIP, COM_AA_INFO_SYSTEM_PROPERTIES);
                primaryIpv6 = getVipAddress(COMAA_IPV6_PRIMARY_VIP, COM_AA_INFO_SYSTEM_PROPERTIES);
                secondaryIpv6 = getVipAddress(COMAA_IPV6_SECONDARY_VIP, COM_AA_INFO_SYSTEM_PROPERTIES);
                break;
        }

        final LdapAddress ipv4 = new LdapAddress(primary, secondary);
        final LdapAddress ipv6 = new LdapAddress(primaryIpv6, secondaryIpv6);
        final int ldapTlsPort = getPort(COMAA_LDAP_TLS_PORT);
        final int ldapPort = getPort(COMAA_LDAPS_PORT);

        return new ConnectionData(ipv4, ipv6, ldapTlsPort, ldapPort);
    }

    /**
     * getVipType: retrieve the interface in which vIP address are configured. We can have:
     *              LOAD_BALANCER - for cENM
     *              INGRESS_L4 - for pENM/vENM
     *              INGRESS_L4 will be considered active if no system environment variable
     *              related to LOAD_BALANCER won't be set
     * @return the configured vIP interface
     */
    private ComAAInfoVipType getVipType () {
        String lbPrimary = comAAInfoUtils.getSystemParemeters(COMAA_LOAD_BALANCER_VIP.getVipAddress(), COM_AA_INFO_SYSTEM_ENV);
        String lbFallback = comAAInfoUtils.getSystemParemeters(COMAA_IPV6_LOAD_BALANCER_VIP.getVipAddress(), COM_AA_INFO_SYSTEM_ENV);

        if((lbPrimary == null) && (lbFallback == null)) {
            return INGRESS_L4;
        } else {
            return LOAD_BALANCER;
        }
    }

    /**
     * getVipAddress: get vIP address value from system parameters
     * @param key the key related to vIP address in the system
     * @param sysParamType the source of system parameters (ENV of SYSTEM_PROPERTIES)
     * @return the vIP address
     */
    private String getVipAddress (final ComAAVipAddress key, final ComAAInfoSystemType sysParamType) {
        String property = comAAInfoUtils.getSystemParemeters(key.getVipAddress(), sysParamType);

        if(property == null || property.isEmpty() || property.trim().isEmpty()) {
            logger.warn("getVipAddress - set to empty to property =  {}",property);
            return EMPTY_STRING;
        }

        if (property.contains("/")) {
            logger.warn("reset property due to split = {}",property);
            property = property.split("/")[0];
        }

        return property;
    }

    /**
     * getPort: get ldap port value from system parameters
     * @param key the key related to ldap port in the system (always from SYSTEM_PROPERTIES)
     * @return the ldap port
     */
    private int getPort (final ComAALdapPort key) {
        String property = comAAInfoUtils.getSystemParemeters(key.getLdapPort(), ComAAInfoSystemType.COM_AA_INFO_SYSTEM_PROPERTIES);
        if (property == null || property.isEmpty() || property.trim().isEmpty()) {
            logger.warn("getPort - set to undefined to property =  {}",property);
            return PORT_UNDEFINED;
        }
        return Integer.parseInt(property);
    }
}