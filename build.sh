#!/bin/bash

./mvnw clean package

docker build -t courses/course-service:1.0 .

docker rm course-service

docker run -d -p 8081:8081 --name course-service courses/course-service:1.0