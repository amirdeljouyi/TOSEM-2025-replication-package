#!/bin/bash

fileDirectory="classes"

while IFS="," read -r proj src
do
  echo "Class: $src"
  echo ""

  sourceDir="$proj/$src.java"

  python ./send_java_file_to_api_pool.py $sourceDir --api-url http://localhost:8000/graphql


  echo "Start"
  sleep 30  # wait for 3 seconds
  echo "30 seconds later..."

done < <(tail -n +2 $fileDirectory.csv)
