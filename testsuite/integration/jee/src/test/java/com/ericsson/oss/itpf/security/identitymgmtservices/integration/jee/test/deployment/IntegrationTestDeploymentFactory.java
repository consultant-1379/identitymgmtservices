/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.oss.itpf.security.identitymgmtservices.integration.jee.test.deployment;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import com.ericsson.oss.itpf.security.identitymgmtservices.integration.jee.test.IdentityMgmtServiceTest;

/**
 *
 * @author ealemca
 */
public class IntegrationTestDeploymentFactory {

    private static final Logger log = LoggerFactory.getLogger(IntegrationTestDeploymentFactory.class);

    /**
     * Create deployment from given maven coordinates
     * 
     * @param mavenCoordinates
     *            Maven coordinates in form of groupId:artifactId:type
     * @return Deployment archive represented by this maven artifact
     */
    public static EnterpriseArchive createEARDeploymentFromMavenCoordinates(final String mavenCoordinates) {
        log.debug("******Creating deployment {} for test******", mavenCoordinates);
        final File archiveFile = IntegrationTestDependencies.resolveArtifactWithoutDependencies(mavenCoordinates);
        if (archiveFile == null) {
            throw new IllegalStateException("Unable to resolve artifact " + mavenCoordinates);
        }
        final EnterpriseArchive ear = ShrinkWrap.createFromZipFile(EnterpriseArchive.class, archiveFile);

        log.debug("******Created from maven artifact with coordinates {} ******", mavenCoordinates);
        return ear;
    }

    /**
     * Create deployment from given maven coordinates
     * 
     * @param mavenCoordinates
     *            Maven coordinates in form of groupId:artifactId:type
     * @return Deployment archive represented by this maven artifact
     */
    public static JavaArchive createJARDeploymentFromMavenCoordinates(final String mavenCoordinates) {
        log.debug("******Creating deployment {} for test******", mavenCoordinates);
        final File archiveFile = IntegrationTestDependencies.resolveArtifactWithoutDependencies(mavenCoordinates);
        if (archiveFile == null) {
            throw new IllegalStateException("Unable to resolve artifact " + mavenCoordinates);
        }
        final JavaArchive jar = ShrinkWrap.createFromZipFile(JavaArchive.class, archiveFile);

        log.debug("******Created from maven artifact with coordinates {} ******", mavenCoordinates);
        return jar;
    }

    /**
     * Create web archive
     * 
     * @param name
     * @return web archive
     */
    public static WebArchive createWarDeployment(final String name) {
        log.debug("******Creating war deployment {} for test******", name);
        final WebArchive war = ShrinkWrap.create(WebArchive.class, name);
        log.debug("******Created from maven artifact with coordinates {} ******..", name);
        return war;
    }

    public static Archive<?> createEarTestDeployment() {
        final EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "identity-mgmt-service-test-ear.ear");
        ear.addAsModule(createModuleArchive());
        ear.addAsApplicationResource(BEANS_XML_FILE);

        return ear;
    }

    public static Archive<?> createWarTestDeployment() {
        final WebArchive archive = ShrinkWrap.create(WebArchive.class, "identity-mgmt-service-test.war")
                .addAsWebInfResource("META-INF/beans.xml").addClass(IdentityMgmtServiceTest.class);
        return archive;
    }

    /**
     * This is used to setup the module configuration
     * 
     * @return Archive
     */
    public static Archive<?> createModuleArchive() {
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "identity-mgmt-service-test-bean-lib.jar")
                .addAsResource("META-INF/beans.xml", "META-INF/beans.xml").setManifest(MANIFEST_MF_FILE)

                //Test classes
                .addPackage(IdentityMgmtServiceTest.class.getPackage());
        return archive;
    }

    public static final File MANIFEST_MF_FILE = new File("src/test/resources/META-INF/MANIFEST.MF");

    public static final File BEANS_XML_FILE = new File("src/test/resources/META-INF/beans.xml");

}
