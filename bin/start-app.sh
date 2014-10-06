#!/bin/bash

nohup java -Dconfig.file=prod.conf \
 -Dlogback.configurationFile=logback.prod.xml \
 -cp election-api/target/scala-2.11/elections-api-assembly-1.0.jar \
 ba.zastone.elections.web.ElectionsWeb >/dev/null 2>&1 & echo $! > pids/election-api.pid
