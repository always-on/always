cd C:\Dropbox\%COMPUTERNAME%
release\bin\nircmdc screensaver
call release\bin\always-kill.bat
if not exist release.new\count.bat goto continue
    call release.new\count.bat
    for /f %%i in ('dir /A-D /B /S release ^| find /C /V ""') do set ALWAYS-CHECK=%%i
    if not "%ALWAYS-COUNT%"=="%ALWAYS-CHECK%" goto continue
      erase /S/Q release.old
      rmdir /S/Q release.old
      move /-Y release release.old 
      move /-Y release.new release
:continue 
call release\bin\weather.bat
cd C:\Dropbox\%COMPUTERNAME%
always.bat


