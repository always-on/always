cd C:\Dropbox\release\plugins\Plugins.Startup\bin
taskkill /F /IM Plugins.Startup.exe
start Plugins.Startup.exe
cd ..\..\..\
:always
   java -Djava.library.path="." -jar always.jar Unity null true
   echo "Restarting"
goto always   
