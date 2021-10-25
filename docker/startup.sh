#!/usr/bin/env bash

JAVA_OPTS="$JAVA_OPTS -Xms64m -Xmx512m -XX:MaxPermSize=256m -Djava.net.preferIPv4Stack=true"
JAVA_OPTS="$JAVA_OPTS -Djava.security.egd=file:/dev/./urandom"
JAVA_OPTS="$JAVA_OPTS -DserviceConfigFilePath=./conf/config.yaml"

java $JAVA_OPTS -jar /AppD-Prometheus-Extension-1.0.0.jar
