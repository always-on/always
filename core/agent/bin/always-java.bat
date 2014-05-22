cd C:\Dropbox\%COMPUTERNAME%\release
bin\nircmdc screensaver
:always
   java -Djava.library.path="." -jar always.jar %*
   echo RESTARTING JAVA...
   if NOT ERRORLEVEL 1 bin\nircmdc screensaver 
goto always   
