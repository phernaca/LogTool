@echo off
rem -------------------------------------------------------------------------
rem file_import.bat - File Import Script into FTA(Alfresco)
rem
rem Environment Variable Prerequisites:
rem
rem   JAVA_HOME    Must point at your Java Development Kit installation.
rem
rem   CLASSPATH    (Optional) This batch file will automatically add
rem                what jar needs to the CLASSPATH.  This consists
rem                of APP_HOME\classes and all the jar files in
rem                APP_HOME\lib


set APP_HOME=.


rem ----- Save Environment Variables That May Change ------------------------

set _CP=%CP%
set _CLASSPATH=%CLASSPATH%

rem ----- Verify and Set Required Environment Variables ---------------------

if not "%JAVA_HOME%" == "" goto gotJavaHome
echo You must set JAVA_HOME to point at your Java Development Kit installation
echo Warning it must be JDK 1.5.0 minimum!
goto cleanup


rem ----- Prepare Appropriate Java Execution Commands -----------------------
:gotJavaHome
set _RUNJAVA="%JAVA_HOME%\bin\java"


rem ----- Set Up The Runtime Classpath --------------------------------------
set CP=%APP_HOME%\classes
rem set CP=%CP%;%CLASSPATH%

rem Try to determine if APP_HOME contains spaces
if exist %APP_HOME%\lib\commons-cli-1.1.jar goto dynClasspath
echo Your APP_HOME appears to contain spaces.
echo Unable to set CLASSPATH dynamically.
goto staticClasspath


:dynClasspath
set _LIBJARS=
for %%i in (%APP_HOME%\lib\*.*) do call %APP_HOME%\cpappend.bat %%i
if not "%_LIBJARS%" == "" goto gotLibJars
echo Unable to set CLASSPATH dynamically.
if "%OS%" == "Windows_NT" goto staticClasspath
echo Note: To set the CLASSPATH dynamically on Win9x systems
echo       only DOS 8.3 names may be used in APP_HOME!
goto staticClasspath

:gotLibJars
echo Including all jars in %APP_HOME%\lib in your CLASSPATH.
rem Note: _LIBJARS already contains a leading semicolon
set CP=%CP%%_LIBJARS%
goto chkClasspath

:staticClasspath
echo Setting your CLASSPATH statically.
if exist "%APP_HOME%\lib\commons-beanutils-1.7.0.jar" set CP=%CP%;%APP_HOME%\lib\commons-beanutils-1.7.0.jar
if exist "%APP_HOME%\lib\commons-beanutils-core-1.8.0.jar" set CP=%CP%;%APP_HOME%\lib\commons-beanutils-core-1.8.0.jar
if exist "%APP_HOME%\lib\commons-cli-1.1.jar" set CP=%CP%;%APP_HOME%\lib\commons-cli-1.1.jar
if exist "%APP_HOME%\lib\commons-codec-1.2.jar" set CP=%CP%;%APP_HOME%\lib\commons-codec-1.2.jar
if exist "%APP_HOME%\lib\commons-collections-3.2.1.jar" set CP=%CP%;%APP_HOME%\lib\commons-collections-3.2.1.jar
if exist "%APP_HOME%\lib\commons-configuration-1.6.jar" set CP=%CP%;%APP_HOME%\lib\commons-configuration-1.6.jar
if exist "%APP_HOME%\lib\commons-dbcp-1.2.2.jar" set CP=%CP%;%APP_HOME%\lib\commons-dbcp-1.2.2.jar
if exist "%APP_HOME%\lib\commons-digester-1.8.jar" set CP=%CP%;%APP_HOME%\lib\commons-digester-1.8.jar
if exist "%APP_HOME%\lib\commons-discovery-0.2.jar" set CP=%CP%;%APP_HOME%\lib\commons-discovery-0.2.jar
if exist "%APP_HOME%\lib\commons-httpclient-3.1.jar" set CP=%CP%;%APP_HOME%\lib\commons-httpclient-3.1.jar
if exist "%APP_HOME%\lib\commons-io-1.4.jar" set CP=%CP%;%APP_HOME%\lib\commons-io-1.4.jar
if exist "%APP_HOME%\lib\commons-lang-2.4.jar" set CP=%CP%;%APP_HOME%\lib\commons-lang-2.4.jar
if exist "%APP_HOME%\lib\commons-logging-1.1.jar" set CP=%CP%;%APP_HOME%\lib\commons-logging-1.1.jar
if exist "%APP_HOME%\lib\commons-pool-1.5.3.jar" set CP=%CP%;%APP_HOME%\lib\commons-pool-1.5.3.jar
if exist "%APP_HOME%\lib\${artifactId}-${version}.jar" set CP=%CP%;%APP_HOME%\lib\${artifactId}-${version}.jar




:chkClasspath
if "%CLASSPATH%" == "" goto installClasspath
set CP=%CLASSPATH%;%CP%
:installClasspath
echo.
echo Using CLASSPATH: %CP%
echo.
set CLASSPATH=%CP%


rem ----- Execute The Requested Command -------------------------------------
"%JAVA_HOME%\bin\java.exe" -Xmx512m -classpath %CLASSPATH% com.thales.palma.logsmigration.LogsMigrationAction %*


:cleanup
set _LIBJARS=
set _RUNJAVA=
set CLASSPATH=%_CLASSPATH%
set _CLASSPATH=
set CP=%_CP%
set _CP=
:finish
