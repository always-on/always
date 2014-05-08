cd C:\Dropbox\release
bin\nircmdc screensaver
:always
   java -Djava.library.path="." -jar always.jar %1
   echo RESTARTING JAVA...
   if NOT ERRORLEVEL 1 bin\nircmdc screensaver 
goto always   
