#!/bin/bash

CONTAINER_NAME="ollama"

docker stop "$CONTAINER_NAME"

sleep 2

docker start "$CONTAINER_NAME"
