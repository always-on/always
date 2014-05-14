Instructions to modify GRUB boot order:

1. Make a backup copy of the GRUB file:
	
	> sudo cp /etc/default/grub /etc/default/grub.backup

2. Now, edit the GRUB file:

	> sudo gedit /etc/default/grub

3. Find the following line in the grub file:
	
	> GRUB_DEFAULT=0
			
4. Replace the 0 value (zero-based index number) with "2>0" (quotation is 
	required). 2 means the third option in menu (which is for the older 
	Linux versions) and >0 means the first option in the submenu:
	
	> GRUB_DEFAULT="2>0"
	
	NOTE: To see/check these values reboot the Reeti and hold SHIFT until 
	you see the GRUB menu.
			
5. Lastly, save the file and build the updated GRUB menu:

	> sudo update-grub
			
6. Restart the Reeti and use the following command to check that the 
"Previous Linux Versions" is loaded:

	> uname -r
	
	NOTE: You should be able to see "3.0.0-12-generic".