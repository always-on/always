cd C:\Dropbox\%COMPUTERNAME%\release
bin\nircmdc screensaver
:always
   java -Djava.library.path="." -jar always.jar %*
   echo RESTARTING JAVA...
   bin\nircmdc screensaver 
goto always   
