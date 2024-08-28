package com.ericsson.oss.itpf.security.identitymgmtservices;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ConfigurationBean.class)
public class ConfigurationBeanTest {

    InetAddress inetAddr1;

    @Test
    public void hostBufferTest() throws UnknownHostException {
        System.setProperty(IdmConstants.CONFIGURATION_PROPERTY, "src/test/resources/global.properties");
        final byte[] ipAddr = new byte[] { 127, 0, 0, 1 };
        inetAddr1 = InetAddress.getByAddress(ipAddr);
        InetAddress[] inetAddr2 = { inetAddr1, inetAddr1};
        PowerMockito.mockStatic(InetAddress.class);
        PowerMockito.when(InetAddress.getAllByName("ldap-local"))
                .thenReturn(inetAddr2);
        PowerMockito.when(InetAddress.getAllByName("ldap-remote"))
                .thenReturn(inetAddr2);
        String hostBuffer = ConfigurationBean.get(IdmConstants.COM_INF_LDAP_HOST_PROPERTY);
        assertEquals("127.0.0.1", hostBuffer);
    }
}