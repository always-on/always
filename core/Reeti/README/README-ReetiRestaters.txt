Use this instruction to configure "restarter" services on Reeti:

+ URBI SERVER:
==============

1. Go to startup Application in Ubuntu.

2. Enable "Urbi Restarter" which will run the following:

   /reetiPrograms/bin/restarter/restarterUrbi


+ ReetiServer_Capturing:
========================

1. Compile Reeti's code in ALways on project using CMake (see build 
instruction).

2. Give execute permission to "RestartReetiServerCapturingService"
file in /etc/init.d/

   > sudo chmod a+x RestartReetiServerCapturingService

3. Update "RestartReetiServerCapturingService" in all run levels to
make Capturing Server process to automatically restart on Ubuntu 
reboot.

   > sudo update-rc.d RestartReetiServerCapturingService defaults

NOTE: To make any change into this file and update it do the following:

   > sudo update-rc.d -f RestartReetiServerCapturingService remove

Then, again:

   > sudo update-rc.d RestartReetiServerCapturingService defaults
