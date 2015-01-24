cd C:\Dropbox\%COMPUTERNAME%\release
ping -n 1 wunderground.com
if errorlevel 1 timeout /T 180
java -cp always.jar edu.wpi.always.weather.wunderground.WundergroundParser
