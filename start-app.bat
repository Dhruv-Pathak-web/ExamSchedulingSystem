@echo off
setlocal

set "APP_ROOT=%~dp0"
set "CATALINA_HOME=%APP_ROOT%tools\apache-tomcat-10.1.52"
set "CATALINA_BASE=%CATALINA_HOME%"

if not exist "%CATALINA_HOME%\bin\startup.bat" (
    echo Tomcat not found at:
    echo   %CATALINA_HOME%
    echo.
    echo Please keep the tools folder as generated or update this script.
    exit /b 1
)

call "%CATALINA_HOME%\bin\startup.bat"
echo.
echo Server starting...
echo Open this URL in your browser:
echo   http://localhost:8080/ExamSchedulingSystem/login.jsp
