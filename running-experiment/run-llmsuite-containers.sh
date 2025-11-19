#!/bin/bash

JAVA=$1

# Number of containers
NUM_CONTAINERS=5

# CPU cores per container
CPUS_PER_CONTAINER=6

# Memory per container
MEMORY="16g"

# Starting CPU core index
START_CPU=0

# Base image
IMAGE_NAME="llmsuite-experiment-image"
CONTAINER_NAME="llmsuite"

if [ "$JAVA" == "8" ]; then
  IMAGE_NAME=$IMAGE_NAME-8
  CONTAINER_NAME=$CONTAINER_NAME-8
elif [ "$JAVA" == "17" ]; then
  IMAGE_NAME=$IMAGE_NAME-17
  CONTAINER_NAME=$CONTAINER_NAME-17
else
  JAVA=11
fi


echo "IMAGE_NAME: $IMAGE_NAME"

for i in $(seq 1 $NUM_CONTAINERS); do
  # Calculate core range
  CPU_START=$((START_CPU + (i - 1) * CPUS_PER_CONTAINER))
  CPU_END=$((CPU_START + CPUS_PER_CONTAINER - 1))

  # Volume path
  HOST_VOLUME="$(pwd)/output/llmsuite/$i"
  CONTAINER_VOLUME="/app/dataset/llmsuite"

  # Ensure the host directory exists
  mkdir -p "$HOST_VOLUME"

  echo "Starting container $CONTAINER_NAME-$i with CPUs $CPU_START-$CPU_END..."

  docker run -d \
    --cpus="$CPUS_PER_CONTAINER" \
    --cpuset-cpus="$CPU_START-$CPU_END" \
    --memory="$MEMORY" \
    --memory-swap="$MEMORY" \
    -v "$HOST_VOLUME":"$CONTAINER_VOLUME" \
    --name "$CONTAINER_NAME-$i" \
    "$IMAGE_NAME" \
    llmsuite $i $JAVA
done