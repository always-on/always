To setup network connection on the PC and the robot:

[NOTE: Connect the robot and the PC using a "cross" network cable.]

1. Enable WIFI on the PC and use dhcp to establish an Internet connection.

2. To assign a static IP address to the robot:

2.a. Make a backup from "interfaces" file

   > sudo cp /etc/network/interfaces /etc/network/interfaces.backup

2.b. Edit the "interfaces" file:

   > sudo gedit /etc/network/interfaces

2.c. Find "iface eth0 inet dhcp" and change dhcp to static:

   iface eth0 inet static

2.d. Add the following lines after "iface eth0 inet static":

[NOTE: Make sure IP address is the same range as the IP address
assigned to the Ethernet address on PC.]

address 192.168.1.2
netmask 255.255.255.0
gateway 192.168.1.1

2.e. Do the following to take the robot's network down and to bring it up 
again to activate robot's network with the new setting:

   > sudo ifdown eth0
   > sudo ifup eth0

3. To assign a static IP address to the PC:

3.a. Go to the Local Area Connection Properties and add the following
addresses:

IP address: 192.168.1.1
Subnet mask: 255.255.255.0

[NOTE: You need to add same DNS server addresses as the one WIFI connection 
is using on the PC.]

Preferred DNS server: 130.215.32.18
Alternate DNS server: 130.215.39.18

4. Try to ping the robot from the PC and vice versa.

5. If the PC can ping the robot but robot can not ping the PC:

5.a. Go to Windows control panel,
5.b. Go to System and Security panel,
5.c. Click on Windows Firewall,
5.d. Click on Advanced settings,
5.e. Click on Inbound Rules in the left column,
5.f. Click on New Rule... in the right column to open the New Inbound Rule Wizard,
5.g. Choose custom rule radio button and click the Next button,
5.h. Keep All programs radio button activated and click the Next button,
5.i. Keep next page's setting unchanged and click the Next button,
5.j. At the bottom of the next page add robot's IP address as the remote IP address
and go to the next page,
5.k. Keep Allow Connection radio button activated and go to the next page,
5.l. Keep all the settings unchanged and go to the next page,
5.m. Assign a name, e.g. REETI, and go to the next page,
5.n. Click on Finish.