
docker build -t ad-node-image .
docker run -p 8082:8082 -d ad-node-image
sleep 1
google-chrome --incognito http://localhost:8082/