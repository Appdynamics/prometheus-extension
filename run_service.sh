#!/bin/bash

nohup java -DserviceConfigFilePath=./conf/config.yaml -jar ./Prometheus-AppD.jar &
