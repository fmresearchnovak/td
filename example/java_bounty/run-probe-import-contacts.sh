#!/bin/bash

# This file was automatically generated from build.sh

cd build/bin/
java -Djava.library.path=./ edu.fandm.enovak.ParcelProbeImportContacts $1
