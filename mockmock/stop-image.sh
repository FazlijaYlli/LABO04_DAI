docker rm $(docker stop 1 $(docker ps -a -q --filter ancestor=mockmock-server))