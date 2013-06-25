#!/bin/sh
# -----------------------------------------------------------------------------
# file_import.sh - File Import Script into FTA(Alfresco)
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

APP_HOME=.


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


# First clear out the user classpath
CLASSPATH=

CLASSPATH="$JAVA_HOME"/lib/tools.jar:"$APP_HOME"/classes

# Then proceed  with the rest of jars on the lib folder
# Set Alfresco jars
for i in `ls "$APP_HOME"/lib/*.jar`
do
  CLASSPATH=${CLASSPATH}:${i}
done

echo "CLASSPATH -> ${CLASSPATH}"


"$_RUNJAVA"   \
      -Xmx512m -classpath ${CLASSPATH} \
      com.thales.palma.logsmigration.LogsMigrationAction $*





