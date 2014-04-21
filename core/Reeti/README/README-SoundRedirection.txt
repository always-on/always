To redirect sound from PC on Reeti's speaker.

1. Use the 4 feet male/male aux audio cable to connect the aux audio jack on 
Reeti (red) to headphone jack on the PC.

2. Do the followings on Reeti:

2.a. Create a new file:
   > sudo gedit /etc/init.d/speaker-proxy

2.b. Add this content to the file:

#!/bin/sh
# /etc/init.d/speaker-loopback -  startup script for the speaker loopback
 
case $1 in
start)
pactl load-module module-loopback latency_msec=1
;;

*)
echo "Usage: /etc/init.d/speaker-loopback start"
exit 1
;;
 
esac
 
exit 0
   
2.c. Change the permission of the file:

   > sudo chmod +x /etc/init.d/speaker-proxy
   > sudo chmod 755 /etc/init.d/speaker-proxy


   > sudo update-rc.d speaker-proxy defaults
   
3. Run "Startup Applications" program on Ubuntu.

3.a. Add a new startup application, and add the followings to the three fields:

Name: Speaker
Command: /etc/init.d/speaker-proxy start
Comment: To play sound by Reeti's speaker.

4. Restart the robot!
