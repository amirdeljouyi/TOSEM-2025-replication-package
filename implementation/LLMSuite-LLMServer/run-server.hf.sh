#!/bin/bash

source .env/bin/activate

logDir="log/"
attempt=$1

mkdir -p $logDir
python -m main --model CodeLlama-13b-Instruct-hf --port 8000 --log_level DEBUG --log_to_file True
