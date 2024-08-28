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
package com.ericsson.oss.itpf.security.identitymgmtservices.ejb.statistics;

import com.ericsson.oss.itpf.sdk.instrument.annotation.InstrumentedBean;
import com.ericsson.oss.itpf.sdk.instrument.annotation.MonitoredAttribute;

import javax.ejb.Singleton;


@InstrumentedBean(description = "Proxy Agent Account Monitored Data", displayName = "Proxy Agent Account Statistics")
@Singleton
public class ProxyAccountMonitoredData {
    private int numberOfCreatedProxyAccountOnLockableSubtree = 0;
    private int numberOfCreatedProxyAccountOnLegacySubtree = 0;
    private int numberOfDeletedProxyAccountOnLockableSubtree = 0;
    private int numberOfDeletedProxyAccountOnLegacySubtree = 0;

    private int numberOfEnabledProxyAccountOnLockableSubtree = 0;
    private int numberOfEnabledProxyAccountOnLegacySubtree = 0;
    private int numberOfDisabledProxyAccountOnLockableSubtree = 0;
    private int numberOfDisabledProxyAccountOnLegacySubtree = 0;

    private int numberOfTotalProxyAccount = 0;
    private int maxNumberOfTotalProxyAccountThreshold = 0;

    @MonitoredAttribute(displayName = "getNumberOfCreatedLockableProxyAccountAttribute", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.PERFORMANCE,
            interval = MonitoredAttribute.Interval.ONE_MIN, collectionType = MonitoredAttribute.CollectionType.TRENDSUP)
    public int getNumberOfCreatedProxyAccountOnLockableSubtree() {
        return numberOfCreatedProxyAccountOnLockableSubtree;
    }

    private void setNumberOfCreatedProxyAccountOnLockableSubtree(int numberOfCreatedProxyAccountOnLockableSubtree) {
        this.numberOfCreatedProxyAccountOnLockableSubtree = numberOfCreatedProxyAccountOnLockableSubtree;
    }

    @MonitoredAttribute(displayName = "getNumberOfCreatedLegacyProxyAccountAttribute", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.PERFORMANCE,
            interval = MonitoredAttribute.Interval.ONE_MIN, collectionType = MonitoredAttribute.CollectionType.TRENDSUP)
    public int getNumberOfCreatedProxyAccountOnLegacySubtree() {
        return numberOfCreatedProxyAccountOnLegacySubtree;
    }

    private void setNumberOfCreatedProxyAccountOnLegacySubtree(int numberOfCreatedProxyAccountOnLegacySubtree) {
        this.numberOfCreatedProxyAccountOnLegacySubtree = numberOfCreatedProxyAccountOnLegacySubtree;
    }

    @MonitoredAttribute(displayName = "getNumberOfDeletedLockableProxyAccountAttribute", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.PERFORMANCE,
            interval = MonitoredAttribute.Interval.ONE_MIN, collectionType = MonitoredAttribute.CollectionType.TRENDSUP)
    public int getNumberOfDeletedProxyAccountOnLockableSubtree() {
        return numberOfDeletedProxyAccountOnLockableSubtree;
    }

    private void setNumberOfDeletedProxyAccountOnLockableSubtree(int numberOfDeletedProxyAccountOnLockableSubtree) {
        this.numberOfDeletedProxyAccountOnLockableSubtree = numberOfDeletedProxyAccountOnLockableSubtree;
    }

    @MonitoredAttribute(displayName = "getNumberOfDeletedLegacyProxyAccountAttribute", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.PERFORMANCE,
            interval = MonitoredAttribute.Interval.ONE_MIN, collectionType = MonitoredAttribute.CollectionType.TRENDSUP)
    public int getNumberOfDeletedProxyAccountOnLegacySubtree() {
        return numberOfDeletedProxyAccountOnLegacySubtree;
    }

    private void setNumberOfDeletedProxyAccountOnLegacySubtree(int numberOfDeletedProxyAccountOnLegacySubtree) {
        this.numberOfDeletedProxyAccountOnLegacySubtree = numberOfDeletedProxyAccountOnLegacySubtree;
    }

    @MonitoredAttribute(displayName = "getNumberOfEnabledLockableProxyAccountAttribute", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.PERFORMANCE,
            interval = MonitoredAttribute.Interval.ONE_MIN, collectionType = MonitoredAttribute.CollectionType.TRENDSUP)
    public int getNumberOfEnabledProxyAccountOnLockableSubtree() {
        return numberOfEnabledProxyAccountOnLockableSubtree;
    }

    public void setNumberOfEnabledProxyAccountOnLockableSubtree(int numberOfEnabledProxyAccountOnLockableSubtree) {
        this.numberOfEnabledProxyAccountOnLockableSubtree = numberOfEnabledProxyAccountOnLockableSubtree;
    }

    @MonitoredAttribute(displayName = "getNumberOfEnabledLegacyProxyAccountAttribute", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.PERFORMANCE,
            interval = MonitoredAttribute.Interval.ONE_MIN, collectionType = MonitoredAttribute.CollectionType.TRENDSUP)
    public int getNumberOfEnabledProxyAccountOnLegacySubtree() {
        return numberOfEnabledProxyAccountOnLegacySubtree;
    }

    private void setNumberOfEnabledProxyAccountOnLegacySubtree(int numberOfEnabledProxyAccountOnLegacySubtree) {
        this.numberOfEnabledProxyAccountOnLegacySubtree = numberOfEnabledProxyAccountOnLegacySubtree;
    }

    @MonitoredAttribute(displayName = "getNumberOfEnabledLockableProxyAccountAttribute", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.PERFORMANCE,
            interval = MonitoredAttribute.Interval.ONE_MIN, collectionType = MonitoredAttribute.CollectionType.TRENDSUP)
    public int getNumberOfDisabledProxyAccountOnLockableSubtree() {
        return numberOfDisabledProxyAccountOnLockableSubtree;
    }

    private void setNumberOfDisabledProxyAccountOnLockableSubtree(int numberOfDisabledProxyAccountOnLockableSubtree) {
        this.numberOfDisabledProxyAccountOnLockableSubtree = numberOfDisabledProxyAccountOnLockableSubtree;
    }

    @MonitoredAttribute(displayName = "getNumberOfEnabledLegacyProxyAccountAttribute", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.PERFORMANCE,
            interval = MonitoredAttribute.Interval.ONE_MIN, collectionType = MonitoredAttribute.CollectionType.TRENDSUP)
    public int getNumberOfDisabledProxyAccountOnLegacySubtree() {
        return numberOfDisabledProxyAccountOnLegacySubtree;
    }

    private void setNumberOfDisabledProxyAccountOnLegacySubtree(int numberOfDisabledProxyAccountOnLegacySubtree) {
        this.numberOfDisabledProxyAccountOnLegacySubtree = numberOfDisabledProxyAccountOnLegacySubtree;
    }

    @MonitoredAttribute(displayName = "getNumberOfTotalProxyAccountAttribute", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.PERFORMANCE,
            interval = MonitoredAttribute.Interval.ONE_MIN, collectionType = MonitoredAttribute.CollectionType.DYNAMIC)
    public int getNumberOfTotalProxyAccount() { return numberOfTotalProxyAccount;}

    public void setNumberOfTotalProxyAccount(int numberOfTotalProxyAccount) {
        this.numberOfTotalProxyAccount = numberOfTotalProxyAccount;
    }

    @MonitoredAttribute(displayName = "getMaxNumberOfProxyAccountThreshold", visibility = MonitoredAttribute.Visibility.ALL,
            units = MonitoredAttribute.Units.NONE, category = MonitoredAttribute.Category.PERFORMANCE,
            interval = MonitoredAttribute.Interval.ONE_MIN, collectionType = MonitoredAttribute.CollectionType.DYNAMIC)
    public int getMaxNumberOfTotalProxyAccountThreshold() { return maxNumberOfTotalProxyAccountThreshold;}

    public void setMaxNumberOfTotalProxyAccountThreshold(int maxNumberOfTotalProxyAccountThreshold) {
        this.maxNumberOfTotalProxyAccountThreshold = maxNumberOfTotalProxyAccountThreshold;
    }

    public void increaseNumberOfCreatedProxyAccountOnLockableSubtree() {
        this.setNumberOfCreatedProxyAccountOnLockableSubtree(this.getNumberOfCreatedProxyAccountOnLockableSubtree() + 1);
    }
    public void increaseNumberOfCreatedProxyAccountOnLegacySubtree() {
        this.setNumberOfCreatedProxyAccountOnLegacySubtree(this.getNumberOfCreatedProxyAccountOnLegacySubtree() + 1);
    }

    public void increaseNumberOfDeletedProxyAccountOnLockableSubtree() {
        this.setNumberOfDeletedProxyAccountOnLockableSubtree(this.getNumberOfDeletedProxyAccountOnLockableSubtree() + 1);
    }
    public void increaseNumberOfDeletedProxyAccountOnLegacySubtree() {
        this.setNumberOfDeletedProxyAccountOnLegacySubtree(this.getNumberOfDeletedProxyAccountOnLegacySubtree() + 1);
    }

    public void increaseNumberOfEnabledProxyAccountOnLockableSubtree() {
        this.setNumberOfEnabledProxyAccountOnLockableSubtree(this.getNumberOfEnabledProxyAccountOnLockableSubtree() + 1);
    }
    public void increaseNumberOfEnabledProxyAccountOnLegacySubtree() {
        this.setNumberOfEnabledProxyAccountOnLegacySubtree(this.getNumberOfEnabledProxyAccountOnLegacySubtree() + 1);
    }

    public void increaseNumberOfDisabledProxyAccountOnLockableSubtree() {
        this.setNumberOfDisabledProxyAccountOnLockableSubtree(this.getNumberOfDisabledProxyAccountOnLockableSubtree() + 1);
    }

    public void increaseNumberOfDisabledProxyAccountOnLegacySubtree() {
        this.setNumberOfDisabledProxyAccountOnLegacySubtree(this.getNumberOfDisabledProxyAccountOnLegacySubtree() + 1);
    }
}
