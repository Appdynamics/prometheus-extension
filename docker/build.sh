#!/usr/bin/env bash

cp -a -r ../src ./src

cp -a -r ../pom.xml ./pom.xml


docker build -t appd_prometheus_extension .

pwd

rm -r -f ./src
rm -r -f ./pom.xml
