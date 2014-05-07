cd C:\Dropbox\release
:always
   java -Djava.library.path="." -jar always.jar %1
   echo RESTARTING JAVA...
   if NOT ERRORLEVEL 1 bin\nircmdc screensaver 
goto always   
