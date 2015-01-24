cd C:\Dropbox\%COMPUTERNAME%\release\plugins\Plugins.Startup\bin
..\..\..\bin\nircmdc screensaver
:always
   tasklist /FI "IMAGENAME eq Plugins.Startup.exe" 2>NUL | find /I /N "Plugins.Startup.exe">NUL
   if "%ERRORLEVEL%"=="0" taskkill /F /IM Plugins.Startup.exe
   ping -n 1 hangout.google.com
   if errorlevel 1 timeout /T 180
   Plugins.Startup.exe
   echo RESTARTING CLIENT...
goto always   
