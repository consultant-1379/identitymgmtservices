/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa;

import static com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa.ComAAConstants.*;
import static com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa.ComAALdapPort.COMAA_LDAP_TLS_PORT;
import static com.ericsson.oss.itpf.security.identitymgmtservices.ejb.comaa.ComAAVipAddress.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.*;

import com.ericsson.oss.itpf.security.identitymgmtservices.comaa.ConnectionData;

public class ComAAInfoBeanTest {

    private static final String DUMMY_PORT = "1389";
    private static final String EMPTY_STRING = "";
    private static final String SOME_IP = "some_ip";
    private static final String SOME_IPv6 = "some_ipv6";
    private static final String SOME_IP_MASK = "some_ip/mask";
    private static final String SOME_IPv6_MASK = "some_ipv6/mask";

    private ComAAInfoBean comAAInfo;

    @Before
    public void setUp() {
        comAAInfo = new ComAAInfoBean();
    }

    @Test
    public void getCOMAAIpAddress_Test_HappyPath() {
        System.setProperty(COMAA_PRIMARY_VIP.getVipAddress(), SOME_IP);
        final String comaaIpAddress = comAAInfo.getCOMAAIpAddress();
        assertEquals(SOME_IP, comaaIpAddress);
    }

    @Test
    public void getCOMAAFallbackIPAddress_Test_HappyPath() {
        System.setProperty(COMAA_SECONDARY_VIP.getVipAddress(), SOME_IP);
        final String comaaFallbackIPAddress = comAAInfo.getCOMAAFallbackIPAddress();
        assertEquals(SOME_IP, comaaFallbackIPAddress);
    }

    @After
    public void cleanUp() {
        System.clearProperty(COMAA_PRIMARY_VIP.getVipAddress());
        System.clearProperty(COMAA_IPV6_PRIMARY_VIP.getVipAddress());
        System.clearProperty(COMAA_LDAP_TLS_PORT.getLdapPort());
    }
}
