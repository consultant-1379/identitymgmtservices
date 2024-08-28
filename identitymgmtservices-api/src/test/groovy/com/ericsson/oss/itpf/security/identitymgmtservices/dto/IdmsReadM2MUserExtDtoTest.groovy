package com.ericsson.oss.itpf.security.identitymgmtservices.dto

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.IdmsUserState


class IdmsReadM2MUserExtDtoTest extends CdiSpecification {

    def username = "fooUserName"
    def groupName = "mm-smrsusers"
    def homeDir = "/home/foousername"

    def userState = IdmsUserState.IDMS_USER_EXISTING.toString()
    def uid = 4000
    def gid = 5000
    def expiryTimestamp = "timestamp"
    def encryptedUserPassword = "encryptedUserPassword"

    def "no-args constructor" () {
        given:
        when:
        IdmsReadM2MUserExtDto idmsReadM2MUserExtDto = new IdmsReadM2MUserExtDto()
        then:
        idmsReadM2MUserExtDto !=null
        and:
        idmsReadM2MUserExtDto.getUserName() == null &&
                idmsReadM2MUserExtDto.getGroupName() == null &&
                idmsReadM2MUserExtDto.getHomeDir() == null &&
                idmsReadM2MUserExtDto.getUserState() == null &&
                idmsReadM2MUserExtDto.getUidNumber() == 0 &&
                idmsReadM2MUserExtDto.getGidNumber() == 0 &&
                idmsReadM2MUserExtDto.getExpiryTimestamp() == null &&
                idmsReadM2MUserExtDto.getM2mencryptedPassword() == null
    }

    def "constructor with parameters" () {
        given:
        when:
        IdmsReadM2MUserExtDto idmsReadM2MUserExtDto = new IdmsReadM2MUserExtDto(username, userState, groupName, homeDir, uid, gid, expiryTimestamp)
        then:
        idmsReadM2MUserExtDto.getUserName() == username &&
                idmsReadM2MUserExtDto.getGroupName() == groupName &&
                idmsReadM2MUserExtDto.getHomeDir() == homeDir &&
                idmsReadM2MUserExtDto.getUserState() == userState &&
                idmsReadM2MUserExtDto.getUidNumber() == uid &&
                idmsReadM2MUserExtDto.getGidNumber() == gid &&
                idmsReadM2MUserExtDto.getExpiryTimestamp() == expiryTimestamp &&
                idmsReadM2MUserExtDto.getM2mencryptedPassword() == null
    }

    def "set parameters" () {
        given:
        IdmsReadM2MUserExtDto idmsReadM2MUserExtDto = new IdmsReadM2MUserExtDto()
        when:
        idmsReadM2MUserExtDto.setUserName(username)
        idmsReadM2MUserExtDto.setGroupName(groupName)
        idmsReadM2MUserExtDto.setHomeDir(homeDir)
        idmsReadM2MUserExtDto.setUserState(userState)
        idmsReadM2MUserExtDto.setUidNumber(uid)
        idmsReadM2MUserExtDto.setGidNumber(gid)
        idmsReadM2MUserExtDto.setExpiryTimestamp(expiryTimestamp)
        idmsReadM2MUserExtDto.setM2mencryptedPassword(encryptedUserPassword)

        then:
        idmsReadM2MUserExtDto.getUserName() == username &&
                idmsReadM2MUserExtDto.getGroupName() == groupName &&
                idmsReadM2MUserExtDto.getHomeDir() == homeDir &&
                idmsReadM2MUserExtDto.getUserState() == userState &&
                idmsReadM2MUserExtDto.getUidNumber() == uid &&
                idmsReadM2MUserExtDto.getGidNumber() == gid &&
                idmsReadM2MUserExtDto.getExpiryTimestamp() == expiryTimestamp &&
                idmsReadM2MUserExtDto.getM2mencryptedPassword() == encryptedUserPassword
    }

}
