@echo off
title CyberGuard AI Platform Server
echo ========================================================
echo       CyberGuard AI Platform (AUT)
echo       Backend API:  http://localhost:8000/api
echo       Frontend UI:  http://localhost:5173
echo ========================================================
echo.
echo [INFO] Starting Backend REST API & Responsive Web Frontend...
echo [INFO] Automatically opening http://localhost:5173 in browser...
echo.

start "" "http://localhost:5173"
node app/start_cyberguard_app.js
pause
