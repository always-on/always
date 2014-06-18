cd C:\Dropbox\%COMPUTERNAME%\release
set ALWAYS_RELEASE=1
bin\nircmdc screensaver
:always
   java -Djava.library.path="." -jar always.jar %*
   echo RESTARTING JAVA...
   bin\nircmdc screensaver 
goto always   
