#!/bin/bash

input_log="$1"   # change this to your actual log file
output_csv="durations.csv"

# Write CSV header
echo "duration_seconds" > "$output_csv"

# Process each matching line
grep "request_duration=datetime.timedelta" "$input_log" | while read -r line; do
    # Extract seconds and microseconds using regex
    if [[ $line =~ seconds=([0-9]+),[[:space:]]*microseconds=([0-9]+) ]]; then
        sec="${BASH_REMATCH[1]}"
        micro="${BASH_REMATCH[2]}"

        # Calculate float total seconds using awk
        total=$(awk "BEGIN {printf \"%.6f\", $sec + $micro / 1000000}")
        echo "$total" >> "$output_csv"
    fi
done