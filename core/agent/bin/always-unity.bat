cd C:\Dropbox\%COMPUTERNAME%\release
call bin\always-kill.bat
start bin\always-client.bat
timeout /T 90
bin\always-java.bat UNITY %*

