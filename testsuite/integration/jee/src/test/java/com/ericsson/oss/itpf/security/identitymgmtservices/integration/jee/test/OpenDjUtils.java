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
package com.ericsson.oss.itpf.security.identitymgmtservices.integration.jee.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.junit.Assert.assertTrue;

public class OpenDjUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenDjUtils.class);

    public static void startOpenDj() {
        final String osName = System.getProperty("os.name");
        final String opendjHome = System.getProperty("opendj.home");
        final String startCmd;

        LOGGER.info("startOpenDj: os.name is {}", osName);
        LOGGER.info("startOpenDj: opendj.home is {}", opendjHome);

        if (osName.equals("Linux")) {
            startCmd = String.format("%s/opendj/bin/start-ds", opendjHome);
        } else {
            startCmd = String.format("%s/opendj/bat/start-ds.bat", opendjHome);
        }
        LOGGER.info("startOpenDj: startCmd is <{}>", startCmd);
        assertTrue(runCmd(startCmd));
        LOGGER.info("startOpenDj: OpenDj has been started.");
    }

    // stop OpenDj
    public static void stopOpenDj() {
        final String osName = System.getProperty("os.name");
        final String opendjHome = System.getProperty("opendj.home");
        final String stopCmd;

        if (osName.equals("Linux")) {
            stopCmd = String.format("%s/opendj/bin/stop-ds", opendjHome);
        } else {
            stopCmd = String.format("%s/opendj/bat/stop-ds.bat", opendjHome);
        }
        LOGGER.info("stopOpenDj: stopCmd is <{}>", stopCmd);
        assertTrue(runCmd(stopCmd));
        LOGGER.info("stopOpenDj: OpenDj has been stopped.");
    }

    public static void updateOpenDj() {
        final String opendjHome = System.getProperty("opendj.home");
        String[] ldapModifyCmd;

        LOGGER.info("updateOpenDj: Update of OpenDj has been started.");

        ldapModifyCmd = new String[] {
                String.format("%s/opendj/bin/ldapmodify", opendjHome),
                "-p", "1636", "-w", "ldapadmin", "-f",
                String.format("%s/identitymgmt.ldif", opendjHome),
                "--trustAll", "--useSSL", "-D",
                "cn=directory manager" };
        LOGGER.info("updateOpenDj: ldapModifyCmd is <{}>", (Object[]) ldapModifyCmd);
        assertTrue(runCmd(ldapModifyCmd));
        LOGGER.info("updateOpenDj: OpenDj updated using identitymgmt.ldif script.");
    }

    /**
     * Runs commands given as a String parameter.
     * @param cmd command to be executed
     * @return    true if command executed successfully, false otherwise
     */
    private static boolean runCmd(final String cmd) {
        int exitCode = -1;
        try {
            final Process p = Runtime.getRuntime().exec(cmd);
            exitCode = p.waitFor();
            LOGGER.info("runCmd: cmd <{}> completed with exit status {}", cmd, exitCode);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = reader.readLine();
            while (line != null) {
                LOGGER.info(line);
                line = reader.readLine();
            }

        } catch (final Exception e) {
            LOGGER.error("exception from cmd: <{}>, Details: {}", cmd, e.getMessage());
            return false;
        }

        if (exitCode == 0) {
            return true;
        }
        return false;
    }

    /**
     * Runs commands given as a String parameter.
     * @param cmd command to be executed (given as an String Array)
     * @return    true if command executed successfully, false otherwise
     */
    private static boolean runCmd(final String[] cmd) {
        int exitCode = -1;
        try {
            final Process p = Runtime.getRuntime().exec(cmd);
            exitCode = p.waitFor();
            LOGGER.info("runCmd: cmd <{}> completed with exit status {}", cmd, exitCode);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = reader.readLine();
            while (line != null) {
                LOGGER.info(line);
                line = reader.readLine();
            }

        } catch (final Exception e) {
            LOGGER.error("exception from cmd: <{}>, Details: {}", cmd, e.getMessage());
            return false;
        }

        if (exitCode == 0) {
            return true;
        }
        return false;
    }

}
