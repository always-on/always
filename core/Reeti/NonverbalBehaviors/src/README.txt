First step is to compile all the source files to generate required .so files.

The MakeFile.linux is going to first compile and then copy configurations and 
.so files to the appropriate locations.

NOTE: In case umake-shared is not found, update the PATH variable:

reeti@reeti:~$ which umake-shared
reeti@reeti:~$ export PATH=/usr/local/gostai/bin:$PATH
reeti@reeti:~$ which umake-shared
/usr/local/gostai/bin/umake-shared