#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

PIDFILE="${DIR}/../election-api.pid"

if [ -f ${PIDFILE} ]; then
    kill `cat ${PIDFILE}`
fi

