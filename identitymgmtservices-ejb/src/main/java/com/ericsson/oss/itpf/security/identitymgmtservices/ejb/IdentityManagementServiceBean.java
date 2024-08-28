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

package com.ericsson.oss.itpf.security.identitymgmtservices.ejb;

import com.ericsson.oss.itpf.security.identitymgmtservices.*;
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

/**
 *
 * @see com.ericsson.oss.itpf.security.identitymgmtservices.IdentityManagementService
 *
 */
@Stateless
public class IdentityManagementServiceBean implements IdentityManagementService {

    @Inject
    private SecurityManagerBean secManager;

    @Override
    public M2MUser createM2MUser(final String userName, final String groupName, final String homeDir,
                                 final int lifespan) {
        secManager.addM2MUser(userName, homeDir, groupName, lifespan);
        return secManager.getM2MUser(userName);
    }

    @Override
    public M2MUserPassword createM2MUserPassword(String userName, String groupName, String homeDir, int validDays) {
        return secManager.createM2MUser(userName, homeDir, groupName, validDays);
    }

    @Override
    public boolean deleteM2MUser(final String userName) {
        return secManager.removeM2MUser(userName);
    }

    @Override
    public M2MUser getM2MUser(final String userName) {
        return secManager.getM2MUser(userName);
    }

    @Override
    public boolean isExistingM2MUser(final String userName) {
        return secManager.isExistingM2MUser(userName);
    }

    @Override
    public char[] getM2MPassword(final String userName) {
        final String pwd = secManager.getM2MPassword(userName);
        return (pwd != null) ? pwd.toCharArray() : null;
    }

    @Override
    public char[] updateM2MPassword(final String userName) {
        final String pwd = secManager.updateM2MPassword(userName);
        return (pwd != null) ? pwd.toCharArray() : null;
    }

    @Override
    public List<String> getAllTargetGroups() {
        return secManager.getAllTargetGroups();
    }

    @Override
    public String getDefaultTargetGroup() {
        return secManager.getDefaultTargetGroup();
    }

    @Override
    public List<String> validateTargetGroups(final List<String> targetGroups) {
        return secManager.validateTargetGroups(targetGroups);
    }

    @Override
    public ProxyAgentAccountData createProxyAgentAccount() {
        return secManager.addProxyAccount();
    }

    @Override
    public boolean deleteProxyAgentAccount(final String userDN) {
        return secManager.removeProxyAccount(userDN);
    }

    /********************* 105 65-0334/79156 New interface methods *******************/
    @Override
    public ProxyAgentAccountGetData getProxyAgentAccount(Boolean isLegacy, Boolean isSummary) {
        if(!isValid(isLegacy, isSummary)) {
            final String errMsg = "getProxyAgentAccount invalid input parameters";
            throw new IdentityManagementServiceException(errMsg);
        }
        return secManager.getProxyAgentAccount(isLegacy, isSummary);
    }

    @Override
    public ProxyAgentAccountGetData getProxyAgentAccountByAdminStatus(ProxyAgentAccountAdminStatus adminStatus,
                                                                      Boolean isLegacy, Boolean isSummary) {
        if(!isValid(adminStatus, isLegacy, isSummary)) {
            final String errMsg = "getProxyAgentAccountByAdminStatus invalid input parameters";
            throw new IdentityManagementServiceException(errMsg);
        }
        return secManager.getProxyAgentAccountByAdminStatus(adminStatus, isLegacy, isSummary);
    }

    @Override
    public ProxyAgentAccountGetData getProxyAgentAccountByInactivityPeriod(Long inactivityPeriod, Boolean isLegacy,
                                                                           Boolean isSummary) {
        if(!isValid(inactivityPeriod, isLegacy, isSummary)) {
            final String errMsg = "getProxyAgentAccountByInactivityPeriod invalid input parameters";
            throw new IdentityManagementServiceException(errMsg);
        }
        return secManager.getProxyAgentAccountByInactivityPeriod(inactivityPeriod, isLegacy, isSummary);
    }

    @Override
    public ProxyAgentAccountDetails getProxyAgentAccountDetails(String userDn) {
        return secManager.getProxyAgentAccountDetails(userDn);
    }

    @Override
    public Boolean updateProxyAgentAccountAdminStatus(String userDn, ProxyAgentAccountAdminStatus adminStatus) {
        if (!isValid(adminStatus)) {
            final String errMsg = "updateProxyAgentAccountAdminStatus invalid input parameters";
            throw new IdentityManagementServiceException(errMsg);
        }
        return secManager.updateProxyAgentAccountAdminStatus(userDn, adminStatus);
    }

    private Boolean isValid(Boolean isLegacy, Boolean isSummary) {
        return (isLegacy != null && isSummary != null);
    }

    private Boolean isValid (ProxyAgentAccountAdminStatus adminStatus,
                     Boolean isLegacy, Boolean isSummary) {
        return (isValid(isLegacy, isSummary) && (adminStatus != null));
    }

    private Boolean isValid (Long inactivityPeriod,
                     Boolean isLegacy, Boolean isSummary) {
        return (isValid(isLegacy, isSummary) && (inactivityPeriod != null));
    }

    private Boolean isValid (ProxyAgentAccountAdminStatus adminStatus) {
        return (adminStatus != null);
    }
}
