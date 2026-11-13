@echo off
setlocal enabledelayedexpansion
title CyberGuard AI Automation Test Suite Runner

echo ======================================================================
echo    CYBERGUARD AI AUTOMATION TESTING SUITE (25CAH-705)
echo    Framework: Java 17 + Selenium 4.28.1 + TestNG 7.10.2 + REST-Assured
echo ======================================================================
echo.

where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [INFO] Apache Maven detected on PATH.
    echo [INFO] Running TestNG suites via Maven Surefire plugin...
    echo.
    call mvn clean test -DsuiteXmlFile=testng.xml
    goto FINISH
)

:: Check for Maven in standard installation directories
set "MAVEN_FOUND=0"
for /d %%D in ("C:\Program Files\apache-maven*" "C:\apache-maven*" "%USERPROFILE%\apache-maven*" "%USERPROFILE%\.m2\wrapper\dists\*") do (
    if exist "%%D\bin\mvn.cmd" (
        echo [INFO] Found Maven at %%D\bin\mvn.cmd
        set "MAVEN_FOUND=1"
        call "%%D\bin\mvn.cmd" clean test -DsuiteXmlFile=testng.xml
        goto FINISH
    )
)

:: Fallback to built-in Test Runner if Maven CLI is not installed on system PATH
echo [NOTICE] Maven CLI ('mvn') is not found in system PATH.
echo [INFO] Executing integrated TestNG test runner...
echo.
node test_runner.js

:FINISH
echo.
echo ======================================================================
echo   Execution Finished. 
echo   View HTML Report: target\surefire-reports\index.html
echo ======================================================================
echo.
pause
