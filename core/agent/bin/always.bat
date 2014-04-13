cd C:\Dropbox-Always\release\plugins\Plugins.Startup\bin
taskkill /F /IM Plugins.Startup.exe
start Plugins.Startup.exe
cd ..\..\..\
:always
   java -Djava.library.path="." -jar always.jar
   echo "Restarting"
goto always   
