#!/bin/bash

#This file was automatically generated from build.sh

# convert second parameter from relative file path to absolute file path
if [ -n "$2" ]; then
	ABS_PATH=$(readlink -f "$2" 2>/dev/null)
else
    echo "Missing required command line parameters"
    exit 1
fi

cd build/bin/
java -Djava.library.path=./ edu.fandm.enovak.ParcelSendVCardMessage $1 "$ABS_PATH" 

