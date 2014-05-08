cd C:\Dropbox\release\plugins\Plugins.Startup\bin
..\..\..\bin\nircmdc screensaver
:always
   taskkill /F /IM Plugins.Startup.exe
   Plugins.Startup.exe
   echo RESTARTING CLIENT...
goto always   
