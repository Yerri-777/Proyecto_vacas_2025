@echo off
setlocal
REM Script to build and run the backend on port 8080
cd /d "%~dp0"

echo Building backend (maven package)...
mvn -DskipTests package

set WAR_DIR=target\tienda-backend-1.0.0
if not exist "%WAR_DIR%\WEB-INF\classes" (
  echo ERROR: %WAR_DIR% not found. Build failed?
  pause
  exit /b 1
)

REM Prepare classpath: classes + all jars in lib
set CP=%WAR_DIR%\WEB-INF\classes;%WAR_DIR%\WEB-INF\lib\*

if defined JAVA_HOME (
  set JAVA_CMD=%JAVA_HOME%\bin\java.exe
) else (
  set JAVA_CMD=java
)

echo Starting backend using %JAVA_CMD%
echo Classpath: %CP%

REM Launch Java in a detached/background process so Tomcat keeps running
start "tienda-backend" /B "%JAVA_CMD%" -cp "%CP%" com.example.backend.AppMain 8080

if errorlevel 1 (
  echo Backend failed to start.
  pause
)
endlocal
