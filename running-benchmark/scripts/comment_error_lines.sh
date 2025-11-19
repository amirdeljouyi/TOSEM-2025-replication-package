#!/usr/bin/env bash

set -e

if [ $# -ne 3 ]; then
  echo "Usage: $0 <input.csv> <test_root_dir> <lib_dir>"
  exit 1
fi

INPUT_CSV="$1"
TEST_ROOT_DIR="$2"
LIB_DIR="coverage-benchmark/$3"

# Ensure clean build directories and old jar is removed
rm -rf ./bin
mkdir -p bin
mkdir -p bin/edu
rm -f llm-tests.jar

echo "📑 Reading CUTs from: $INPUT_CSV"
echo "📁 Test root directory: $TEST_ROOT_DIR"

# Function to compile and comment errors iteratively
# Arguments:
# $1: classpath options
# $2: primary test file path
# $3: scaffolding file path (can be empty if not applicable)
# $4: CLASSNAME for reporting
# $5: INDEX for reporting
compile_and_comment() {
  local cp_opts="$1"
  local test_file="$2"
  local scaffolding_file="$3" # This will be empty if not an ESTest
  local class_name_for_report="$4"
  local index_for_report="$5"
  local MAX_ITERATIONS=50 # Increased from 0 to allow multiple passes
  local iteration=0
  local errors_log="errors_${class_name_for_report}_${index_for_report}.log"

  # 🧹 Flatten test file before compilation
  if [[ -f "flattener.jar" ]]; then
    echo "🧹 Flattening method/constructor calls in $test_file"
    java -jar flattener.jar "$test_file"
    if [[ -n "$scaffolding_file" && -f "$scaffolding_file" ]]; then
      echo "🧹 Flattening scaffolding file: $scaffolding_file"
      java -jar flattener.jar "$scaffolding_file"
    fi
  else
    echo "⚠️  flattener.jar not found. Skipping flattening step."
  fi

  # Prepare the list of files to compile
  local files_to_compile=("$test_file")
  if [[ -n "$scaffolding_file" ]]; then
    files_to_compile+=("$scaffolding_file")
  fi

  while true; do
    echo "Attempting compilation (Iteration: $((iteration + 1))) for $test_file"

    if (( iteration == 0 )); then
      local errors_log="errors_${class_name_for_report}_${index_for_report}.log"
    else
      local errors_log="errors.log"
    fi

    # Added -Xmaxerrs 0 to show all errors
    if javac -Xmaxerrs 0 -cp "$cp_opts" -d bin "${files_to_compile[@]}" 2> "$errors_log"; then
      echo "✅ All files compile cleanly."
      break # Compilation successful, exit loop
    fi

    # Categorize errors and report only on the first iteration (if compilation failed)
    if (( iteration == 0 )); then
      echo "📊 Categorizing errors from first compilation attempt:"
      declare -A error_categories=(
        ["Cannot Resolve Symbol/Method/Constructor"]=0
        ["Has Private Access in ...Parameter/Return"]=0
        ["is abstract. Cannot be instantiated."]=0 # This category will now include additional patterns
        ["Type/Implementation Mismatch & Incompatible Types"]=0
        ["Other/Uncategorized"]=0
      )

      echo "Errors Log: $errors_log"
      local total_categorized_errors=0

      # Read error log line by line to categorize
      while IFS= read -r line; do
        # Only process errors originating from the primary test_file
        # The line format from javac is typically "filename.java:line: error: message"
        if [[ "$line" =~ ^"$test_file":([0-9]+): ]]; then
          local matched=false
          if [[ "$line" =~ "cannot find symbol" || \
                "$line" =~ "cannot resolve symbol" ||
                "$line" =~ "cannot resolve method" ||
                "$line" =~ "does not exist" ||
                "$line" =~ "cannot resolve constructor" ]]; then
            error_categories["Cannot Resolve Symbol/Method/Constructor"]=$((error_categories["Cannot Resolve Symbol/Method/Constructor"] + 1))
            matched=true
          elif [[ "$line" =~ "has private access in"  || "$line" =~ "cannot assign a value to final" ]]; then
            error_categories["Has Private Access in ...Parameter/Return"]=$((error_categories["Has Private Access in ...Parameter/Return"] + 1))
            matched=true
          # Updated regex for "is abstract. Cannot be instantiated." category
          # Now includes: "is abstract; cannot be instantiated", "must implement abstract method", and "does not override method from its superclass"
          elif [[ "$line" =~ "is abstract; cannot be instantiated" || \
                  "$line" =~ "must implement abstract method" || \
                  "$line" =~ "does not override method from its superclass" || \
                  "$line" =~ "method does not override or implement a method from a supertype" ]]; then
            error_categories["is abstract. Cannot be instantiated."]=$((error_categories["is abstract. Cannot be instantiated."] + 1))
            matched=true
          # Combined Type/Implementation Mismatch & Incompatible Types category
          elif [[ "$line" =~ "incompatible types" || "$line" =~ "cannot be applied to" || "$line" =~ "clashes with" || "$line" =~ "no suitable method found for" || "$line" =~ "no suitable constructor found" ]]; then
            error_categories["Type/Implementation Mismatch & Incompatible Types"]=$((error_categories["Type/Implementation Mismatch & Incompatible Types"] + 1))
            matched=true
          fi

          if ! $matched; then
            error_categories["Other/Uncategorized"]=$((error_categories["Other/Uncategorized"] + 1))
          fi
          total_categorized_errors=$((total_categorized_errors + 1))
        fi
      done < "$errors_log"

      if (( total_categorized_errors > 0 )); then
        echo "  --- Error Summary (First Iteration) ---"
        for category in "${!error_categories[@]}"; do
          if (( ${error_categories[$category]} > 0 )); then
            printf "  %-40s : %d\n" "$category" "${error_categories[$category]}"
          fi
        done
        echo "  -------------------------------------"

        # --- CSV Reporting ---
        local REPORT_CSV_FILE="error_report.csv"
        echo "📝 Saving error categorization to $REPORT_CSV_FILE"

        # Check if CSV file exists or is empty, write header if needed
        if [[ ! -f "$REPORT_CSV_FILE" || ! -s "$REPORT_CSV_FILE" ]]; then
          printf "%s\n" "CLASSNAME,INDEX,Cannot Resolve Symbol/Method/Constructor,Has Private Access in ...Parameter/Return,is abstract. Cannot be instantiated.,Type/Implementation Mismatch & Incompatible Types,Other/Uncategorized" >> "$REPORT_CSV_FILE"
        fi

        # Prepare data row
        local csv_row="$class_name_for_report,$index_for_report"
        csv_row+=",${error_categories["Cannot Resolve Symbol/Method/Constructor"]}"
        csv_row+=",${error_categories["Has Private Access in ...Parameter/Return"]}"
        csv_row+=",${error_categories["is abstract. Cannot be instantiated."]}"
        csv_row+=",${error_categories["Type/Implementation Mismatch & Incompatible Types"]}"
        csv_row+=",${error_categories["Other/Uncategorized"]}"

        # Write data row to CSV
        printf "%s\n" "$csv_row" >> "$REPORT_CSV_FILE"
        # --- End CSV Reporting ---

      else
        echo "  No categorizable errors found in $test_file on first attempt."
      fi
    fi # End of first iteration categorization logic

    if (( iteration >= MAX_ITERATIONS )); then
      echo "❌ Reached max attempts ($MAX_ITERATIONS). Exiting for current file."
      cat "$errors_log" # Show remaining errors
      exit 1 # Exit for this test file
    fi

    # Continue with commenting logic if compilation failed and max iterations not reached
    local lines=$(grep -F "$test_file:" "$errors_log" \
      | cut -d ':' -f2 \
      | sort -n \
      | uniq)

    local line_array=()
    for ln in $lines; do
      line_array+=("$ln")
    done

    if [ ${#line_array[@]} -eq 0 ]; then
      echo "⚠️  No error lines found in $test_file for commenting, or parsing failed. Review errors.log."
      cat "$errors_log"
      exit 1
    fi

    echo "🔧 Commenting error lines in $test_file (Iteration: $((iteration + 1))): ${line_array[*]}"

    mapfile -t file_lines < "$test_file"
    declare -A lines_to_comment=()

    for ln in "${line_array[@]}"; do
      idx=$((ln - 1))
      # Ensure index is valid and not out of bounds
      if (( idx < 0 || idx >= ${#file_lines[@]} )); then
          echo "Skipping invalid line index: $ln"
          continue
      fi

      # **NEW CRITICAL CHECK:**
      # If this line (idx) is already marked for commenting, skip further processing for this specific error.
      # This prevents re-evaluating errors that fall within an already identified block.
      if [[ -v lines_to_comment[$idx] ]]; then
        echo "  Skipping line $((idx + 1)) (already marked for commenting by a previous error in this pass)."
        continue
      fi

      local line_content="${file_lines[$idx]}"

      # This check is still valid for lines that might be physically commented but not in lines_to_comment
      # (e.g., if lines_to_comment was reset and file was saved in between iterations - though not how this script works)
      if [[ "$line_content" == "//"* ]]; then
        echo "  Line $((idx + 1)) already physically commented in file: '$line_content' (this should be rare in a single pass)."
        continue
      fi

      # Start from the error line and comment until brackets balance, or just the line if simple
      local open=0
      local close=0
      local i=$idx
      local commented_block_start=$idx
      local commented_block_end=$idx
      local is_single_line_error=true # Heuristic: assume single line if no braces involved

      line_no_strings=$(echo "$line_content" | sed 's/\"[^\"]*\"/""/g')
      line_sanitized=$(echo "$line_no_strings" | sed -E 's/\{[^{}]*\}//g' | sed -E 's/\{[^{}]*\}//g')


      # Check if the error line itself has braces
      open_current=$(grep -o "{" <<< "$line_sanitized" | wc -l | xargs)
      close_current=$(grep -o "}" <<< "$line_sanitized" | wc -l | xargs)

      # If line contains balanced {} on the same line, treat it as single-line expression
      if (( open_current > 0 || close_current > 0 )); then
        if (( open_current == close_current )); then
          is_single_line_error=true
        else
          is_single_line_error=false
        fi
      fi

      # Echo "Marking line" message only if it wasn't already marked (due to the new check above)
      echo "  Marking line $((idx + 1)) for commenting: '${file_lines[$idx]}'"
      lines_to_comment[$idx]=1 # Mark this line as needing to be commented

      # If it seems to be a block-related error, try to balance brackets
      if ! $is_single_line_error; then
          open=$open_current
          close=$close_current
          commented_block_start=$idx # Start of the block we're trying to comment
          i=$((idx + 1)) # Start checking from next line
          while [[ $i -lt ${#file_lines[@]} ]]; do
            local line="${file_lines[$i]}"

            # Only consider uncommented lines for bracket balancing
            if [[ "$line" != "//"* ]]; then
                open_count=$(grep -o "{" <<< "$line" | wc -l | xargs)
                close_count=$(grep -o "}" <<< "$line" | wc -l | xargs)
                open=$((open + open_count))
                close=$((close + close_count))

                # Mark this line if not already marked by a previous, overlapping error in the same batch
                if [[ ! -v lines_to_comment[$i] ]]; then
                  lines_to_comment[$i]=1
                fi
                commented_block_end=$i

                # If brackets balance and we've processed at least one open brace
                if (( open > 0 && open == close )); then
                  break # Found the end of the block
                fi
            fi # End of if not commented

            i=$((i + 1))
          done # End of while loop for balancing
          echo "  Commented block from line $((commented_block_start + 1)) to $((commented_block_end + 1))."
      else
          echo "  Commenting single line $((idx + 1))."
      fi
    done

    # Write modified lines
    local original_lines_count=${#file_lines[@]}
    for (( i=0; i<original_lines_count; i++ )); do
      if [[ -v lines_to_comment[$i] ]]; then # Check if key exists in associative array
        if [[ "${file_lines[$i]}" != "//"* ]]; then
          file_lines[$i]="// ${file_lines[$i]}"
        fi
      fi
    done

    printf "%s\n" "${file_lines[@]}" > "$test_file"

    iteration=$((iteration + 1))
  done # End of while true loop

  # 🎨 Post-processing: Google Java Format
#  if [[ -f "google-java-format-1.27.0-all-deps.jar" ]]; then
#    echo "🎨 Formatting $test_file using Google Java Format..."
#    java -jar google-java-format-1.27.0-all-deps.jar --replace "$test_file"
#    if [[ -n "$scaffolding_file" && -f "$scaffolding_file" ]]; then
#      echo "🎨 Formatting $scaffolding_file using Google Java Format..."
#      java -jar google-java-format-1.27.0-all-deps.jar --replace "$scaffolding_file"
#    fi
#  else
#    echo "⚠️  google-java-format JAR not found — skipping formatting step."
#  fi

  echo "✅ $test_file compiles cleanly after commenting errors."
}

# Skip header and process each line from the CSV
tail -n +2 "$INPUT_CSV" | while IFS=',' read -r PROJECT CLASSNAME CUT_CLASS PACKAGE_PATH; do
  echo "---------------------------------------------"
  echo "🔍 CUT: $CUT_CLASS"
  echo "📦 Looking in package: $PACKAGE_PATH"
  echo "📦 Looking in TEST_ROOT_DIR: $TEST_ROOT_DIR"

  # Find matching directories for the package path
  MATCHING_DIRS=$(find "$TEST_ROOT_DIR" -type d -path "*/$PACKAGE_PATH")

  for DIR in $MATCHING_DIRS; do
    echo "📂 Searching in: $DIR"

    # Find test files in the matching directory
    find "$DIR" -type f -name "${CLASSNAME}_[0-9]*_*Test.java" | while read -r TEST_FILE; do
#    find "$DIR" -type f -name "${CLASSNAME}_llmsuite_[0-9]*_*Test.java" | while read -r TEST_FILE; do
      BASENAME=$(basename "$TEST_FILE" .java)

      # Extract INDEX and SUFFIX from the test file basename
#      if [[ "$BASENAME" =~ ${CLASSNAME}_llmsuite_([0-9]+)_([A-Za-z0-9]+Test) ]]; then
      if [[ "$BASENAME" =~ ${CLASSNAME}_([0-9]+)_([A-Za-z0-9]+Test) ]]; then
        INDEX="${BASH_REMATCH[1]}"
        SUFFIX="${BASH_REMATCH[2]}"
        echo "🔢 Found test: $BASENAME (index: $INDEX, suffix: $SUFFIX)"

        if [[ "$SUFFIX" == "ESTest" ]]; then
          SCAFFOLDING_FILE="${TEST_FILE/_ESTest.java/_ESTest_scaffolding.java}"
          if [[ -f "$SCAFFOLDING_FILE" ]]; then
            echo "  ⤷ Compiling EvoSuite test + scaffolding"
            # Pass CLASSNAME and INDEX to the compile_and_comment function for reporting
            compile_and_comment "${LIB_DIR}/*" "$TEST_FILE" "$SCAFFOLDING_FILE" "$CLASSNAME" "$INDEX"
          else
            echo "  ⚠️  Scaffolding missing for $TEST_FILE"
          fi
        else
          echo "  ⤷ Compiling general/manual test"
          # Pass CLASSNAME and INDEX to the compile_and_comment function for reporting
          compile_and_comment "${LIB_DIR}/*" "$TEST_FILE" "" "$CLASSNAME" "$INDEX" # Pass empty string for scaffolding
        fi
      else
        echo "⚠️  Could not extract test index or suffix from: $BASENAME"
      fi
    done
  done

#  rm errors.log
  echo "✅ Done for $CUT_CLASS"

done