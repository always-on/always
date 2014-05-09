#!/bin/bash

export LD_LIBRARY_PATH="/usr/local/gostai/lib"

until /home/reeti/Always-On/always/core/Reeti/build/src/ReetiServer_Capturing 192.168.1.1 27017; do
   echo "Server crashed with exit code $?. Respawning..." >&2
   sleep 1
done
