#!/bin/bash

#This file was automatically generated from build.sh

# convert second parameter from relative file path to absolute file path
if [ -n "$2" ]; then
  abs_path=$(readlink -e "$2")
else
  abs_path=""
fi

cd build/bin/
java -Djava.library.path=./ edu.fandm.enovak.ParcelSendVCardMessage $1 $abs_path
