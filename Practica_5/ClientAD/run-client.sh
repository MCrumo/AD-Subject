#!/bin/bash

cd ./ClientAD

mvn clean install

sudo cp target/ClientAD-1.0.war /opt/tomcat/webapps/

sudo systemctl restart tomcat

sudo journalctl -u tomcat -xe
