package com.ericsson.oss.itpf.security.identitymgmtservices.dto

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.IdmsUserState


class IdmsReadM2MUserDtoTest extends CdiSpecification {

    def username = "fooUserName"
    def groupName = "mm-smrsusers"
    def homeDir = "/home/foousername"

    def userState = IdmsUserState.IDMS_USER_EXISTING.toString()
    def uid = 4000
    def gid = 5000
    def expiryTimestamp = "timestamp"


    def "no-args constructor" () {
        given:
        when:
        IdmsReadM2MUserDto idmsReadM2MUserDto = new IdmsReadM2MUserDto()
        then:
        idmsReadM2MUserDto !=null
        and:
        idmsReadM2MUserDto.getUserName() == null &&
                idmsReadM2MUserDto.getGroupName() == null &&
                idmsReadM2MUserDto.getHomeDir() == null &&
                idmsReadM2MUserDto.getUserState() == null &&
                idmsReadM2MUserDto.getUidNumber() == 0 &&
                idmsReadM2MUserDto.getGidNumber() == 0 &&
                idmsReadM2MUserDto.getExpiryTimestamp() == null
    }

    def "constructor with parameters" () {
        given:
        when:
        IdmsReadM2MUserDto idmsReadM2MUserDto = new IdmsReadM2MUserDto(username, userState, groupName, homeDir, uid, gid, expiryTimestamp)
        then:
        idmsReadM2MUserDto.getUserName() == username &&
                idmsReadM2MUserDto.getGroupName() == groupName &&
                idmsReadM2MUserDto.getHomeDir() == homeDir &&
                idmsReadM2MUserDto.getUserState() == userState &&
                idmsReadM2MUserDto.getUidNumber() == uid &&
                idmsReadM2MUserDto.getGidNumber() == gid &&
                idmsReadM2MUserDto.getExpiryTimestamp() == expiryTimestamp
    }

    def "set parameters" () {
        given:
        IdmsReadM2MUserDto idmsReadM2MUserDto = new IdmsReadM2MUserDto()
        when:
        idmsReadM2MUserDto.setUserName(username)
        idmsReadM2MUserDto.setGroupName(groupName)
        idmsReadM2MUserDto.setHomeDir(homeDir)
        idmsReadM2MUserDto.setUserState(userState)
        idmsReadM2MUserDto.setUidNumber(uid)
        idmsReadM2MUserDto.setGidNumber(gid)
        idmsReadM2MUserDto.setExpiryTimestamp(expiryTimestamp)

        then:
        idmsReadM2MUserDto.getUserName() == username &&
                idmsReadM2MUserDto.getGroupName() == groupName &&
                idmsReadM2MUserDto.getHomeDir() == homeDir &&
                idmsReadM2MUserDto.getUserState() == userState &&
                idmsReadM2MUserDto.getUidNumber() == uid &&
                idmsReadM2MUserDto.getGidNumber() == gid &&
                idmsReadM2MUserDto.getExpiryTimestamp() == expiryTimestamp
    }

}
