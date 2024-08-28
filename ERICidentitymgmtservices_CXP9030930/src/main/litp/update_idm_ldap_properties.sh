#!/bin/sh

#----------------------------------------------------------------------------
#############################################################################
# COPYRIGHT Ericsson 2014
# The copyright to the computer program herein is the property of
# conditions stipulated in the agreement/contract under which the
# program have been supplied.
#############################################################################
#----------------------------------------------------------------------------

#ATTENTION: THE BELOW ARE EXAMPLES
#This file can be used to obtain LDAP properties such as ldap port, ldap host, baseDN or root suffix
#which are stored in a file (or global.properties)
#The implementation below show how to read properties from a file and push its attr-value pair to PIB
#It uses RESTInterface to send av-pairs
#Properties that are uploaded to PIB can be accessed using @Configure annotation which is defined in sdk configuration

if [[ ( ! $LITP_JEE_DE_name =~ (^PlatformIntegrationBridge) ) && ( ! $LITP_JEE_DE_name =~ (^presentation-server) ) ]] ; then
  exit 0
fi

PROPERTY_FILE=/ericsson/tor/data/global.properties

#Check properties file exists
if [ ! -f $PROPERTY_FILE ]; then
  echo "*******Problem, UI Server properties file $PROPERTY_FILE missing, ****EXITING***"
  exit 0
fi
. $PROPERTY_FILE

#String returned from PIB if attribute does not already exist
PS_EXISTS_CHK="Did not find configuration"

# Set ICA ADDR
# Read and format properties from $PROPERTY_FILE
CITRIX_ADDR_PROPS=$(awk '/#CITRIX_ADDRS_END/{P=0}P;/#CITRIX_ADDRS_START/{P=1}' $PROPERTY_FILE)
CITRIX_ADDRS=$(echo $CITRIX_ADDR_PROPS | sed -e 's/\ /,/g')
CITRIX_ADDRS=$(echo $CITRIX_ADDRS | sed -e 's/=/:/g')

# Check to see if property exists (i.e. see if Presentation Server is deployed yet)
icaAddrReadRes=`/opt/ericsson/PlatformIntegrationBridge/etc/config.py read --name=PresentationService_icaAddr --service_identifier=Presentation_Server`
if [ $? -ne 0 ]; then
  echo "*******Problem, Cannot read ICA ADDR Properties, ****EXITING***"
  exit 0
fi

echo $icaAddrReadRes
if [[ $icaAddrReadRes =~ $PS_EXISTS_CHK ]] ; then
   echo Creating ICA ADDR $CITRIX_ADDRS
   /opt/ericsson/PlatformIntegrationBridge/etc/config.py create --name=PresentationService_icaAddr --value="${CITRIX_ADDRS}" --type=String[] --service_identifier=Presentation_Server --scope=SERVICE
else
   echo Updating ICA ADDR $CITRIX_ADDRS
   /opt/ericsson/PlatformIntegrationBridge/etc/config.py update --name=PresentationService_icaAddr --value="${CITRIX_ADDRS}" --service_identifier=Presentation_Server
fi

if [ $? -ne 0 ]; then
  echo "*******Problem, Cannot set ICA ADDR Properties, ****EXITING***"
  exit 0
fi


# Set WEB PORT
# Read and format properties from $PROPERTY_FILE
WEB_PORT_PROPS=$(awk '/#WEB_PORTS_END/{P=0}P;/#WEB_PORTS_START/{P=1}' $PROPERTY_FILE)
WEB_PORTS=$(echo $WEB_PORT_PROPS | sed -e 's/\ /,/g')
WEB_PORTS=$(echo $WEB_PORTS | sed -e 's/=/:/g')

# Check to see if property exists (i.e. see if Presentation Server is deployed yet)
webPortReadRes=`/opt/ericsson/PlatformIntegrationBridge/etc/config.py read --name=PresentationService_webPort --service_identifier=Presentation_Server`
if [ $? -ne 0 ]; then
  echo "*******Problem, Cannot read WEB PORT Properties, ****EXITING***"
  exit 0
fi


echo $webPortReadRes
if [[ $webPortReadRes =~ $PS_EXISTS_CHK ]] ; then
   echo Creating WEB PORT $WEB_PORTS
   /opt/ericsson/PlatformIntegrationBridge/etc/config.py create --name=PresentationService_webPort --value="${WEB_PORTS}" --type=String[] --service_identifier=Presentation_Server --scope=SERVICE
else
   echo Updating WEB PORT $WEB_PORTS
   /opt/ericsson/PlatformIntegrationBridge/etc/config.py update --name=PresentationService_webPort --value="${WEB_PORTS}" --service_identifier=Presentation_Server
fi
if [ $? -ne 0 ]; then
  echo "*******Problem, Cannot set WEB PORT Properties, ****EXITING***"
  exit 0
fi

echo PIB Configuration completed
exit 0

