#!/bin/bash
# -----------------------------------------------------------------------------
# csv_import.sh - Log Analyser Script into PALMA TASB
#
# Environment Variable Prequisites
#
#   JAVA_HOME       Must point at your Java Development Kit installation.
# 
# 
#   CLASSPATH    	(Optional) This batch file will automatically add
#                	what jar needs to the CLASSPATH.  This consists
#                	of APP_HOME\classes and all the jar files in
#                	APP_HOME\lib
#
#   PROPS_FILE_NAME	The file containing the values for the arguments
#			needed for executing the tool	

APP_HOME=.
PROPS_FILE_NAME=csv_import.properties

fileName=$APP_HOME/$PROPS_FILE_NAME
argsLine=""

echo "props file : " $fileName 

#If the properties file is Not there then stop the script
#Otherwise source the file in order to obtain the key values (thanks to Bash shell)
if [ ! -f "$fileName" ] || [ ! -s "$fileName" ]
then
	echo "Properties file Not existant or Empty ($fileName)"
	exit 1
else
	source $fileName
fi



if [ -z "$JAVA_HOME" ] ; then
  	echo "You must set JAVA_HOME to point at your Java Development Kit installation."
	echo "Warning it must be JDK 1.5.0 minimum!"
	exit 1
fi


if [ -r "$JAVA_HOME"/bin/java ]; then
  _RUNJAVA="$JAVA_HOME"/bin/java
fi


if [ ! -r "$APP_HOME"/lib/commons-cli-1.1.jar ]; then
 echo Your APP_HOME appears to not be correct.
 echo Unable to set CLASSPATH dynamically.
fi

#Then proceed to build the arguments line to be passed to the program: Mandatory first
argsLine="${argsLine} -cla ${csv_import_cla} -id ${csv_import_id} -wt ${csv_import_wt} -vm ${csv_import_vm} -old ${csv_import_old}"
#Optional arguments after
argsLine="${argsLine} -dt ${csv_import_dt} -oc ${csv_import_oc}"
echo "argsLine : " $argsLine


# First clear out the user classpath
CLASSPATH=

CLASSPATH="$JAVA_HOME"/lib/tools.jar:"$APP_HOME"/classes

# Then proceed  with the rest of jars on the lib folder
# Set Lib jars
for i in `ls "$APP_HOME"/lib/*.jar`
do
  CLASSPATH=${CLASSPATH}:${i}
done

echo "CLASSPATH -> ${CLASSPATH}"


"$_RUNJAVA"   \
      -Xmx512m -classpath ${CLASSPATH} \
      com.thales.palma.logsmigration.LogsMigrationAction $argsLine





