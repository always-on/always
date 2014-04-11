#!/bin/bash

export LD_LIBRARY_PATH="/usr/local/gostai/lib"

echo "Setting Library Path"

echo $LD_LIBRARY_PATH

echo "Running Capturing Server..."

/home/reeti/Always-On/always/core/Reeti/build/src/ReetiServer_Capturing $1 $2
