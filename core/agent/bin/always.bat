cd C:\Dropbox-Always\release
start client\Plugins.Startup.exe
:always
   java -Djava.library.path="." -jar always.jar
   echo "Restarting"
goto always
