Postgres,redis,mongodb and kafka are required to be running for this project to start



//Redis
brew services start redis

//Start Mongo
mongod --dbpath ~/data/db


source ~/.bash_profile

Run app with profile:
mvn spring-boot:run -Dspring-boot.run.profiles=foo,bar
mvn spring-boot:run -Drun.profiles=test
mvn clean spring-boot:run -Dspring.profiles.active=dev


//run below code - from inside restful-api
mvn clean spring-boot:run -Dspring.profiles.active=test