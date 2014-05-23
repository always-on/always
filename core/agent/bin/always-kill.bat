tasklist /FI "IMAGENAME eq Plugins.Startup.exe" 2>NUL | find /I /N "Plugins.Startup.exe">NUL
if "%ERRORLEVEL%"=="0" taskkill /F /IM Plugins.Startup.exe
tasklist /FI "IMAGENAME eq java.exe" 2>NUL | find /I /N "java.exe">NUL
if "%ERRORLEVEL%"=="0" taskkill /F /IM java.exe
