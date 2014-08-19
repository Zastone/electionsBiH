#!/bin/bash

for FILE in `ls gpx*`; do
    ID=`echo $FILE | cut -d "=" -f 2`;
    mv $FILE $ID.gpx
    done
