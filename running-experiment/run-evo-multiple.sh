#!/bin/bash

# Set the dataset direction variable (adjust as needed)
export direction="some_direction"

# Path to your main script
main_script="./run-evo.sh"

# Loop for five attempts
for attempt in {1..5}
do
  echo "==============================="
  echo " Running Attempt $attempt "
  echo "==============================="
  bash "$main_script" "$attempt" > multiple.log
  echo ""
done