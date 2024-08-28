package com.ericsson.oss.itpf.security.identitymgmtservices.ejb

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import spock.lang.Unroll

class IdentityManagementListenerTest extends CdiSpecification {

    @ObjectUnderTest
    IdentityManagementListener identityManagementListener

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def setup (){}

    @Unroll
    def 'proxy password length to be set'(){
        given:
        when:
        identityManagementListener.listenForPasswordLengthChange(newPasswordLength)
        int passwordLength = identityManagementListener.getPasswordLength()
        then:
        passwordLength == output
        where:
        newPasswordLength           | output
        24                          | 24
        8                           | 8
        64                          | 64
    }

    def 'proxy account excessive entries'(){
        given:
        def newExcessiveEntriesThreshold = 50000
        when:
        identityManagementListener.listenForProxyAccountExcessiveEntries(newExcessiveEntriesThreshold)
        int excessiveEntriesThreshold = identityManagementListener.getProxyAccountExcessiveEntriesThreshold()
        then:
        excessiveEntriesThreshold == newExcessiveEntriesThreshold
    }

    def 'switch on all proxy account subtree'(){
        given:
        when:
        identityManagementListener.listenForProxyAccountRdnSubTree(newSwitchOnLegacySubtree)
        Boolean switchOnLegacySubtree = identityManagementListener.getProxyAccountRdnSubTree()
        then:
        switchOnLegacySubtree == output
        where:
        newSwitchOnLegacySubtree    | output
        null                        | null
        true                        | true
        false                       | false
    }
}
