Use this instruction to configure "restarter" services on Reeti:

+ URBI SERVER:
==============

1. Go to Startup Application in Ubuntu.

2. Enable "Urbi Restarter" which will run the following executable:

   /reetiPrograms/bin/restarter/restarterUrbi

NOTE:
+ To check whether you set it up correctly:

a) Check the path "/home/reeti/.config/autostart/" whether the 
"urbi.desktop" file exists. If not continue:

b) Create a file in "/home/reeti/.config/autostart/" and call it
"urbi.desktop".

c) Copy and paste the following eight lines into the file:

[Desktop Entry]
Name[en_US]=urbi
Type=Application
Exec=/reetiPrograms/bin/restarter/restarterUrbi
Terminal=false
X-GNOME-Autostart-enabled=true
Comment[en_US]=URBI Restarter
Comment=URBI Restarter

d) Make sure there is no duplicates for "urbi" in the Startup 
Application. If there is, get read of the older one (the one 
without urbi.desktop).


NOTE:
+ To test whether urbi rstarter works or not:

a) Restart the Ubuntu on robot.

b) Wait for 30 seconds after Ubuntu boots up.

c) Open a terminal and see whether the urbi server process is 
running in back ground:

   > ps aux | grep urbi

d) If there is, read PID number and kill the process (not a 
normal kill):

   > sudo kill -SIGQUIT <PID>

e) Check if the restarter works. If it does, you should be 
able to see the urbi process with new PID:

   > ps aux | grep urbi

   
NOTE:
+ You can check the capturing server process as it is described 
above.



+ ReetiServer_Capturing
=======================

1. Compile Reeti's code in ALways on project using CMake (see 
build instruction).

2. Give execute permission to "RestartReetiServerCapturingService"
file in "CapturingRestarter" folder in Reeti folder on Github:

   > sudo chmod a+x RestartReetiServerCapturingService

3. Give execute permission to "restartCaptureServer.sh" file in 
/etc/init.d/
   > sudo chmod a+x restartCaptureServer.sh

4. Update "RestartReetiServerCapturingService" in all run levels to
make Capturing Server process to automatically restart on Ubuntu 
reboot.

   > sudo update-rc.d RestartReetiServerCapturingService defaults

NOTE: To make any change into this file and update it do the 
following:

   > sudo update-rc.d -f RestartReetiServerCapturingService remove

Then, again:

   > sudo update-rc.d RestartReetiServerCapturingService defaults
