#!/bin/bash
###############################################################################
# COPYRIGHT Ericsson 2016
#
# The copyright to the computer program(s) herein is the property of
# Ericsson Inc. The programs may be used and/or copied only with written
# permission from Ericsson Inc. or in accordance with the terms and
# conditions stipulated in the agreement/contract under which the
# program(s) have been supplied.
###############################################################################
# This script is used to copy and change permission for passkey file
#
# Author: Team ENMeshed
#
###############################################################################
MKDIR="/bin/mkdir -p"
CP="/bin/cp"
CHMOD="/bin/chmod"
CHOWN="/bin/chown"

KEY="opendj_passkey"
OLD_DIRECTORY="/ericsson/tor/data/idenmgmt"
DESTINATION="/opt/ericsson/com.ericsson.oss.itpf.security.identitymgmtservices"

###############################################################################
#
# Logs error to /var/log/messages
#
###############################################################################
log_error(){
    logger -s -t COPY_PASSKEYS -p user.err "ERROR: $@"
}

###############################################################################
#
# Main Program
#
###############################################################################
${MKDIR} ${DESTINATION}
if [ $? != 0 ] ; then
    log_error "Failed to create ${DESTINATION}"
    exit 1
fi

${CP} ${OLD_DIRECTORY}/${KEY} ${DESTINATION}
if [ $? != 0 ] ; then
   log_error "Failed to copy ${OLD_DIRECTORY}/${KEY}"
   exit 1
fi

${CHMOD} 600 ${DESTINATION}/${KEY}
if [ $? != 0 ] ; then
    log_error "Failed to harden ${DESTINATION}/${KEY}"
    exit 1
fi

${CHOWN} jboss_user:jboss ${DESTINATION}/${KEY}
if [ $? != 0 ] ; then
    log_error "Failed to change owner for ${DESTINATION}/${KEY}"
    exit 1
fi

exit 0

