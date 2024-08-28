/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.oss.itpf.security.identitymgmtservices.integration.jee.test;

import com.ericsson.oss.itpf.security.identitymgmtservices.integration.jee.test.deployment.IdentityMgmtServiceDependencies;
import com.ericsson.oss.itpf.security.identitymgmtservices.integration.jee.test.deployment.IntegrationTestDeploymentFactory;
import org.jboss.arquillian.container.test.api.*;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@RunAsClient
public class IdentityMgmtServiceTest extends Arquillian {

    private static final Logger log = LoggerFactory.getLogger(IdentityMgmtServiceTest.class);

    /**
     * Starts OpenDJ before test is started.
     */
    @BeforeSuite
    public static void startOpenDj() {
        OpenDjUtils.startOpenDj();
        OpenDjUtils.updateOpenDj();
    }

    /**
     * Stops OpenDJ after test is finished.
     */
    @AfterSuite(alwaysRun=true)
    public static void stopOpenDj() {
        log.debug("stopping opendj");
        OpenDjUtils.stopOpenDj();
    }

    @Deployment(name = "identitymgmtservices-ear", testable = false)
    public static Archive<?> deployEARService() {
        return IntegrationTestDeploymentFactory.createEARDeploymentFromMavenCoordinates(
                IdentityMgmtServiceDependencies.IDENTITYMGMT_SERVICE_EAR);
    }

    @Test
    @OperateOnDeployment("identitymgmtservices-ear")
    public void nullTest() {
        log.debug("TC: null Test deployment passed");
    }
}