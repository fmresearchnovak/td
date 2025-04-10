#!/bin/bash
cd build/bin/
java -Djava.library.path=./ edu.fandm.enovak.ParcelLogin $1 "$2"
