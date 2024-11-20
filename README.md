# lost-found-registry-service
Lost and found registry service

# Introduction:

This service exposes different operations like upload and save lostitems,read lostitems,claim lostitems and read claimedlostitems

# Getting Started:

Dependencies:

- For Development
  - Java 21
  - Maven
 
- For Mongo db
  images from docker 


# Build and Test

The application is build using maven.Full lifecycle is triggered by running mvn clean verify .These are the parameters available to cntrol the execution

skip.unit.tests [default-false] if set to true then the unit tests will be skipped

# unit test

mvn clean test

# unit test + build jar

mvn clean package

# docker for mongo db

use the docker image for mongo db, please refer docker-compose.yml file

Please run the following commands

 docker pull mongo
 docker run -d --name mongodb -p 27017:27017 mongo

To stop docker compose

docker-compose down










