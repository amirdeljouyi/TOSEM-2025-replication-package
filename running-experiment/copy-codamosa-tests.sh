#!/bin/bash

# Location of the generated files
SOURCE_DIR="output/codamosa"

# Root of your target directory tree
TARGET_ROOT="llm-tests-codamosa"

# Find all generated test files recursively
find "$SOURCE_DIR" -type f -name 'generated_test_output.*.java' | while read -r file; do
    # Extract class name from filename
    class_name=$(basename "$file" | sed -E 's/generated_test_output\.[0-9]+\.(.*)\.java/\1/')

    # Find matching directory named after the class
    target_dir=$(find "$TARGET_ROOT" -type d -name "$class_name" | head -n 1)

    if [ -n "$target_dir" ]; then
        target_file="$target_dir/$(basename "$file")"

        if [ -e "$target_file" ]; then
            echo "⚠️  Skipping $(basename "$file") — already exists in $target_dir"
        else
            echo "✅ Copying $(basename "$file") → $target_dir/"
            cp "$file" "$target_dir/"
        fi
    else
        echo "❌ No directory found for class: $class_name"
    fi
done