@echo off
setlocal

set "APP_ROOT=%~dp0"
set "CATALINA_HOME=%APP_ROOT%tools\apache-tomcat-10.1.52"
set "CATALINA_BASE=%CATALINA_HOME%"

if not exist "%CATALINA_HOME%\bin\shutdown.bat" (
    echo Tomcat not found at:
    echo   %CATALINA_HOME%
    exit /b 1
)

call "%CATALINA_HOME%\bin\shutdown.bat"
echo Tomcat stop command sent.
