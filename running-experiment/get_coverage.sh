#!/bin/bash

set -e

if [ $# -ne 3 ]; then
  echo "Usage: $0 <input.csv> <test_root_dir> <outputDir>"
  exit 1
fi

INPUT_CSV="$1"
TEST_ROOT_DIR="$2"
OutputDir=coverage/"$3"

rm -rf ./bin
mkdir -p bin
rm -f llm-tests.jar

echo "📑 Reading CUTs from: $INPUT_CSV"
echo "📁 Test root directory: $TEST_ROOT_DIR"
echo "📁 Output directory: $OutputDir"

# Skip header
tail -n +2 "$INPUT_CSV" | while IFS=',' read -r PROJECT CLASSNAME CUT_CLASS PACKAGE_PATH; do
  echo "🔍 CUT: $CUT_CLASS"
  echo "📦 Looking in package: $PACKAGE_PATH"

  MATCHING_DIRS=$(find "$TEST_ROOT_DIR" -type d -path "*/$PACKAGE_PATH")

  for DIR in $MATCHING_DIRS; do
    echo "📂 Searching in: $DIR"

    find "$DIR" -type f -name "${CLASSNAME}_[0-9]*_*Test.java" | while read -r TEST_FILE; do
      BASENAME=$(basename "$TEST_FILE" .java)

      if [[ "$BASENAME" =~ ${CLASSNAME}_([0-9]+)_([A-Za-z0-9]+Test) ]]; then
        INDEX="${BASH_REMATCH[1]}"
        SUFFIX="${BASH_REMATCH[2]}"
        echo "🔢 Found test: $BASENAME (index: $INDEX, suffix: $SUFFIX)"

        if [[ "$SUFFIX" == "ESTest" ]]; then
          SCAFFOLDING_FILE="${TEST_FILE/_ESTest.java/_ESTest_scaffolding.java}"
          if [[ -f "$SCAFFOLDING_FILE" ]]; then
            echo "  ⤷ Compiling EvoSuite test + scaffolding"
            javac -cp "lib/*" -d bin "$TEST_FILE" "$SCAFFOLDING_FILE"
          else
            echo "  ⚠️  Scaffolding missing for $TEST_FILE"
          fi
        else
          echo "  ⤷ Compiling general/manual test"
          javac -cp "lib/*" -d bin "$TEST_FILE"
        fi
      else
        echo "⚠️  Could not extract test index or suffix from: $BASENAME"
      fi
    done
  done

  # Create JAR with all compiled test classes
  echo "📦 Packaging llm-tests.jar"
  jar cf llm-tests.jar -C bin edu

  # Run coverage on all matching compiled test classes
  find bin -name "${CLASSNAME}_[0-9]*_*Test.class" | while read -r CLASS_FILE; do
    TEST_CLASS=$(echo "$CLASS_FILE" | sed 's|bin/||;s|/|.|g;s|.class$||')
    TEST_INDEX=$(echo "$TEST_CLASS" | grep -oE "${CLASSNAME}_[0-9]+" | grep -oE "[0-9]+")

    echo "🧪 Running coverage for $TEST_CLASS (index: $TEST_INDEX)"

    sourceDir="binary/$PROJECT/"
    # Source properties
    if [ -f "${sourceDir}/evosuite-files/evosuite.properties" ]; then
      source ${sourceDir}/evosuite-files/evosuite.properties
    else
      echo "Warning: evosuite.properties file not found in $PROJECT."
    fi


    java \
      -Devosuite.runtime.sandbox=false \
      -Devosuite.runtime.mock=false \
      --add-opens java.base/java.lang=ALL-UNNAMED \
      --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
      --add-opens java.base/java.util=ALL-UNNAMED \
      --add-opens java.base/java.net=ALL-UNNAMED \
      --add-opens java.desktop/java.awt=ALL-UNNAMED \
      -jar llmsuite-coverage.jar \
      -projectCP "$sourceDir/$CP:llm-tests.jar" \
      -class "$CUT_CLASS" \
      -Djunit "$CLASS_FILE" \
      -Dcriterion=BRANCH:LINE:CBRANCH \
      -Doutput_variables=TARGET_CLASS,attempt,criterion,Coverage,Total_Goals,BranchCoverage,LineCoverage,CBranchCoverage,Covered_Goals,Tests_Executed \
      -Dminimize=false -Dcoverage=false -Dtest_format=JUNIT4 \
      -Ddefuse_debug_mode=true \
      -Dattempt="$TEST_INDEX" \
      -Dreport_dir=$OutputDir
  done

  echo "✅ Done for $CUT_CLASS"
  echo "---------------------------------------------"

done

