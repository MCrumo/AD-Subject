#!/bin/bash

cd ./ClientAD/ClientAD
mvn clean install
sudo cp target/ClientAD-1.0.war /opt/tomcat/webapps/

cd ../../
docekr-compose up -d --build
sleep 2
google-chrome --incognito http://localhost:8080/ClientAD-1.0