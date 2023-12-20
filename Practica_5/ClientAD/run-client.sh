#!/bin/bash

#systemctl start tomcat.service
#cd ./ClientAD
#mvn clean install
#sudo cp target/ClientAD-1.0.war /opt/tomcat/webapps/
#sudo systemctl restart tomcat
#sudo journalctl -u tomcat -xe

docker build -t tomcat-image .
docker run -p 8080:8080 -d tomcat-image
sleep 1
google-chrome --incognito http://localhost:8080/ClientAD-1.0
