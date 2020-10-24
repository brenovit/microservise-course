#!/bin/bash

HOST=http://localhost:8081/course-service/courses

curl --request GET $HOST

curl \
  --header "Content-type: application/json" \
  --request POST \
  --data '{"title":"Test course", "description":"Description test course"}' \
  $HOST
  
curl --request GET $HOST

curl \
  --header "Content-type: application/json" \
  --request POST \
  --data '{"title":"Test course", "description":"Description test course"}' \
  $HOST
  
curl \
  --header "Content-type: application/json" \
  --request POST \
  --data '{"title":"Test course", "description":"Description test course"}' \
  $HOST

curl --request GET $HOST/2

curl --request DELETE $HOST/1

curl \
  --header "Content-type: application/json" \
  --request PATCH \
  --data '{"title":"Test course 2", "description":"Description test course 2"}' \
  $HOST

curl --request GET $HOST