package com.ericsson.oss.itpf.security.identitymgmtservices.ejb.timers

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.rule.ImplementationInstance
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import spock.lang.Unroll

import javax.ejb.Timer
import javax.ejb.TimerConfig
import javax.ejb.TimerService

class IdentityManagementTimerManagerTest extends CdiSpecification {

    private static final String TIMER_ILLEGAL_STATE_EXCP_TAG = "Timer illegal state exception"
    private static final String TIMER_ILLEGAL_ARGS_EXCP_TAG = "Timer illegal argument exception"
    private static final String TIMER_NAME_TAG = "TIMER_TEST"

    @ObjectUnderTest
    IdentityManagementTimerManager idmsTimerManager

    @Override
    def addAdditionalInjectionProperties(InjectionProperties injectionProperties) {}

    def setup (){}

    @ImplementationInstance
    IdentityManagementTimerCallback idmsTimerCallback = [
            onTimerEvent : {}
    ] as IdentityManagementTimerCallback

    @ImplementationInstance
    TimerService timerServiceMockExcp = [
            createSingleActionTimer : { long var1, TimerConfig var3 ->
                def illegalStateException = new IllegalStateException(TIMER_ILLEGAL_STATE_EXCP_TAG)
                throw illegalStateException
            },
            createIntervalTimer: { long var1, long var2, TimerConfig var3 ->
                if (var1 < 0 || var2 < 0) {
                    def illegalArgsException = new IllegalArgumentException(TIMER_ILLEGAL_ARGS_EXCP_TAG)
                    throw illegalArgsException
                }
            }
    ] as TimerService

    @ImplementationInstance
    TimerService timerServiceMock = [
            createSingleActionTimer : { long var1, TimerConfig var3 ->
                    return timerMock
            },
            createIntervalTimer : { long var1, long var3, TimerConfig var5 ->
                return timerMock
            }
    ] as TimerService

    @ImplementationInstance
    Timer timerMock = [
            getInfo : {
                return TIMER_NAME_TAG
            },

    ] as Timer

    @Unroll
    def 'add timer happy path #type'(){
        given:
        idmsTimerManager.timerService = timerServiceMock
        def identityManagementTimerConfig = new IdentityManagementTimerConfig (TIMER_NAME_TAG, type,
                100L, 100L, idmsTimerCallback)
        when:
        idmsTimerManager.addTimer(identityManagementTimerConfig)
        def timerName = idmsTimerManager.idmsTimers.get(TIMER_NAME_TAG).getInfo()
        then:
        timerName == output
        where:
        type                                        | output
        IdentityManagementTimerType.INTERVAL        | TIMER_NAME_TAG
        IdentityManagementTimerType.SINGLE_ACTION   | TIMER_NAME_TAG
    }

    def 'add timer with null input config'(){
        given:
        def identityManagementTimerConfig = null
        when:
        idmsTimerManager.addTimer(identityManagementTimerConfig)
        then:
        Exception e = thrown()
        e.getMessage().contains(IdentityManagementTimerConstants.WRONG_TIMER_CFG)
    }

    @Unroll
    def 'add timer with invalid input config #name'(){
        given:
        def identityManagementTimerConfig = new IdentityManagementTimerConfig (name,
                IdentityManagementTimerType.INTERVAL,
                100L, 100L, idmsTimerCallback)
        when:
        idmsTimerManager.addTimer(identityManagementTimerConfig)
        then:
        Exception e = thrown()
        e.getMessage().contains(expected)
        where:
        name  | expected
        null  | IdentityManagementTimerConstants.WRONG_TIMER_CFG
        ""    | IdentityManagementTimerConstants.WRONG_TIMER_CFG
    }

    def 'add timer with invalid input config callback'(){
        given:
        def identityManagementTimerConfig = new IdentityManagementTimerConfig (TIMER_NAME_TAG,
                IdentityManagementTimerType.INTERVAL,
                100L, 100L, null)
        when:
        idmsTimerManager.addTimer(identityManagementTimerConfig)
        then:
        Exception e = thrown()
        e.getMessage().contains(IdentityManagementTimerConstants.WRONG_TIMER_CFG)
    }

    def 'add timer with timer service null'(){
        given:
        idmsTimerManager.timerService = null
        def identityManagementTimerConfig = new IdentityManagementTimerConfig (TIMER_NAME_TAG,
                IdentityManagementTimerType.INTERVAL,
                100L, 100L, idmsTimerCallback)
        when:
        idmsTimerManager.addTimer(identityManagementTimerConfig)
        then:
        Exception e = thrown()
        e.getMessage().contains(IdentityManagementTimerConstants.TIMER_SERVICE_UNAV)
    }

    def 'add timer with timer already created'(){
        given:
        idmsTimerManager.timerService = timerServiceMock
        idmsTimerManager.idmsTimers.put("TIMER_TEST", timerMock)
        def identityManagementTimerConfig = new IdentityManagementTimerConfig (TIMER_NAME_TAG,
                IdentityManagementTimerType.INTERVAL,
                100L, 100L, idmsTimerCallback)
        when:
        idmsTimerManager.addTimer(identityManagementTimerConfig)
        then:
        Exception e = thrown()
        e.getMessage().contains(IdentityManagementTimerConstants.TIMER_ALREADY_INIT)
    }

    def 'add timer throw IllegalArgumentException'(){
        given:
        idmsTimerManager.timerService = timerServiceMockExcp
        def identityManagementTimerConfig = new IdentityManagementTimerConfig (TIMER_NAME_TAG,
                IdentityManagementTimerType.SINGLE_ACTION,
                -1L, -1L, idmsTimerCallback)
        when:
        idmsTimerManager.addTimer(identityManagementTimerConfig)
        then:
        Exception e = thrown()
        e.getMessage().contains(TIMER_ILLEGAL_STATE_EXCP_TAG)
    }

    @Unroll
    def 'on event happy path #type'(){
        given:
        idmsTimerManager.timerService = timerServiceMock
        def identityManagementTimerConfig = new IdentityManagementTimerConfig (TIMER_NAME_TAG, type,
                100L, 100L, idmsTimerCallback)
        idmsTimerManager.idmsTimerConfigMap.put(TIMER_NAME_TAG, identityManagementTimerConfig)
        when:
        idmsTimerManager.onExpired(timerMock)
        then:
        notThrown(excp)
        where:
        type                                        | excp
        IdentityManagementTimerType.INTERVAL        | Exception.class
        IdentityManagementTimerType.SINGLE_ACTION   | Exception.class
    }

    def 'on event happy path with null timer'(){
        given:
        idmsTimerManager.timerService = timerServiceMock
        def identityManagementTimerConfig = new IdentityManagementTimerConfig (TIMER_NAME_TAG,
                IdentityManagementTimerType.INTERVAL,
                100L, 100L, idmsTimerCallback)
        idmsTimerManager.idmsTimerConfigMap.put(TIMER_NAME_TAG, identityManagementTimerConfig)
        when:
        idmsTimerManager.onExpired(null)
        then:
        notThrown(Exception.class)
    }
}
