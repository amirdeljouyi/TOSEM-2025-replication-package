#!/usr/bin/env bash

set -e

if [ $# -ne 6 ]; then
  echo "Usage: $0 <Project> <test_root_dir> <outputDir> <reason: coverage|mutation|both> <INCLUDE_CLASS_IN_PACKAGE> <JAVA>"
  exit 1
fi

BENCHMARK_FOLDER="coverage-benchmark"
INPUT_CSV="$BENCHMARK_FOLDER/coverage.$1.csv"
LIB_FOLDER="$BENCHMARK_FOLDER/lib-$1"
TEST_ROOT_DIR="$2"
OutputDir=coverage/"$3"
REASON="$4"
INCLUDE_CLASS_IN_PACKAGE=$5
JAVA="$6"

if [[ "$REASON" != "coverage" && "$REASON" != "mutation" && "$REASON" != "both" ]]; then
  echo "❌ Invalid reason: $REASON. Must be one of: coverage, mutation, both"
  exit 1
fi

rm -rf ./bin
mkdir -p bin
#mkdir -p bin/edu
rm -f llm-tests.jar

echo "📑 Reading CUTs from: $INPUT_CSV"
echo "📁 Test root directory: $TEST_ROOT_DIR"
echo "📁 Output directory: $OutputDir"

{
tail -n +2 "$INPUT_CSV" | while IFS=',' read -r PROJECT CLASSNAME CUT_CLASS PACKAGE_PATH; do
  if [[ "$INCLUDE_CLASS_IN_PACKAGE" == "True" ]]; then
    PACKAGE_PATH="${PACKAGE_PATH}/${CLASSNAME}"
  fi

  echo "🔍 CUT: $CUT_CLASS"
  echo "📦 Looking in package: $PACKAGE_PATH"

  MATCHING_DIRS=$(find "$TEST_ROOT_DIR" -type d -path "*/$PACKAGE_PATH")

  for DIR in $MATCHING_DIRS; do
    echo "📂 Searching in: $DIR"
    echo "📂 CLASSNAME in: $CLASSNAME"

    find "$DIR" -type f -name "${CLASSNAME}_[0-9]*_*Test.java" | while read -r TEST_FILE; do
#    find "$DIR" -type f -name "${CLASSNAME}_llmsuite_[0-9]*_*Test.java" | while read -r TEST_FILE; do
      BASENAME=$(basename "$TEST_FILE" .java)
      echo "📂 BASENAME in: $BASENAME"

      if [[ "$BASENAME" =~ ${CLASSNAME}_([0-9]+)_([A-Za-z0-9]+Test) ]]; then
#      if [[ "$BASENAME" =~ ${CLASSNAME}_llmsuite_([0-9]+)_([A-Za-z0-9]+Test) ]]; then
        INDEX="${BASH_REMATCH[1]}"
        SUFFIX="${BASH_REMATCH[2]}"
        echo "🔢 Found test: $BASENAME (index: $INDEX, suffix: $SUFFIX)"

        if [[ "$SUFFIX" == "ESTest" ]]; then
          SCAFFOLDING_FILE="${TEST_FILE/_ESTest.java/_ESTest_scaffolding.java}"
          if [[ -f "$SCAFFOLDING_FILE" ]]; then
            echo "  ⤷ Compiling EvoSuite test + scaffolding"
            javac -cp "$LIB_FOLDER/*" -d bin "$TEST_FILE" "$SCAFFOLDING_FILE"
          else
            echo "  ⚠️  Scaffolding missing for $TEST_FILE"
          fi
        else
          echo "  ⤷ Compiling general/manual test"
          javac -cp "$LIB_FOLDER/*" -d bin "$TEST_FILE"
        fi
      else
        echo "⚠️  Could not extract test index or suffix from: $BASENAME"
      fi
    done
  done

  echo "📦 Packaging llm-tests.jar"
  echo "📦 ClassName ${CLASSNAME}"
  jar cf llm-tests.jar -C bin .

  find bin -name "${CLASSNAME}_[0-9]*_*Test.class" | while read -r CLASS_FILE; do
#  find bin -name "${CLASSNAME}_llmsuite_[0-9]*_*Test.class" | while read -r CLASS_FILE; do
    TEST_CLASS=$(echo "$CLASS_FILE" | sed 's|bin/||;s|/|.|g;s|.class$||')
    TEST_INDEX=$(echo "$TEST_CLASS" | grep -oE "${CLASSNAME}_[0-9]+" | grep -oE "[0-9]+")
#    TEST_INDEX=$(echo "$TEST_CLASS" | grep -oE "${CLASSNAME}_llmsuite_[0-9]+" | grep -oE "[0-9]+")

    binaryDir="/Users/amirdeljouyi/se/llm-powered-unit-test-generation-for-nlp-libraries/benchmark/binary/$PROJECT"
    sourceDir="/Users/amirdeljouyi/se/llm-powered-unit-test-generation-for-nlp-libraries/benchmark/source/$PROJECT"


    ORIGINAL_DIR=$(pwd)
    if [ -f "${binaryDir}/evosuite-files/evosuite.properties" ]; then
      source ${binaryDir}/evosuite-files/evosuite.properties
    else
      echo "⚠️ Warning: evosuite.properties file not found in $PROJECT."
    fi

    newCP=""
    IFS=':' read -ra jars <<< "$CP"
    for jar in "${jars[@]}"; do
      newCP+="$binaryDir/$jar:"
    done

    newCP+="llm-tests.jar"

    echo "newCP: $newCP"

#    CLASS_FILE=${CLASS_FILE#/bin}

    if [[ "$REASON" == "coverage" || "$REASON" == "both" ]]; then
      echo "🧪 Running coverage for $TEST_CLASS (index: $TEST_INDEX)"

      if [[ "$JAVA" == "8" ]]; then
        java \
          -Devosuite.runtime.sandbox=false \
          -Devosuite.runtime.mock=false \
          -jar llmsuite-coverage-8.jar \
          -projectCP "${newCP}" \
          -class "$CUT_CLASS" \
          -Djunit "$CLASS_FILE" \
          -Dcriterion=BRANCH:LINE:CBRANCH \
          -Doutput_variables=TARGET_CLASS,attempt,criterion,Coverage,Total_Goals,BranchCoverage,LineCoverage,CBranchCoverage,Covered_Goals,Tests_Executed \
          -Dminimize=false -Dcoverage=false -Dtest_format=JUNIT4 \
          -Ddefuse_debug_mode=true \
          -Dattempt="$TEST_INDEX" \
          -Dreport_dir=$OutputDir
      else
        java \
          -Devosuite.runtime.sandbox=false \
          -Devosuite.runtime.mock=false \
          --add-opens java.base/java.lang=ALL-UNNAMED \
          --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
          --add-opens java.base/java.util=ALL-UNNAMED \
          --add-opens java.base/java.net=ALL-UNNAMED \
          --add-opens java.desktop/java.awt=ALL-UNNAMED \
          -jar llmsuite-coverage.jar \
          -projectCP "${newCP}" \
          -class "$CUT_CLASS" \
          -Djunit "$CLASS_FILE" \
          -Dcriterion=BRANCH:LINE:CBRANCH \
          -Doutput_variables=TARGET_CLASS,attempt,criterion,Coverage,Total_Goals,BranchCoverage,LineCoverage,CBranchCoverage,Covered_Goals,Tests_Executed \
          -Dminimize=false -Dcoverage=false -Dtest_format=JUNIT4 \
          -Ddefuse_debug_mode=true \
          -Dattempt="$TEST_INDEX" \
          -Dreport_dir=$OutputDir
      fi
  fi

    if [[ "$REASON" == "mutation" || "$REASON" == "both" ]]; then
      echo "📦 Combining CUT + test classes for PIT"

      CUT_JAR="${binaryDir}/$CP"
      TEST_JAR="$ORIGINAL_DIR/llm-tests.jar"
      COMBINED_JAR="combined-${PROJECT}-${CLASSNAME}-${TEST_INDEX}.jar"
      TEMP_DIR=$(mktemp -d)

      mkdir -p "$TEMP_DIR/cut" "$TEMP_DIR/test" "$TEMP_DIR/combined"
      (cd "$TEMP_DIR/cut" && jar xf "$CUT_JAR")
      (cd "$TEMP_DIR/test" && jar xf "$TEST_JAR")
      cp -r "$TEMP_DIR/cut/"* "$TEMP_DIR/combined/" 2>/dev/null || true
      cp -r "$TEMP_DIR/test/"* "$TEMP_DIR/combined/" 2>/dev/null || true
      jar cf "$COMBINED_JAR" -C "$TEMP_DIR/combined" .
      rm -rf "$TEMP_DIR"

      echo "🧬 Running PIT for $TEST_CLASS (index: $TEST_INDEX)"
      MUTATION_DIR="mutation/$PROJECT/$CLASSNAME/$TEST_INDEX"
      mkdir -p "$MUTATION_DIR"

      echo "$sourceDir/src/edu/stanford/nlp"

      java -cp "pitest-command-line-1.15.2.jar:$COMBINED_JAR:lib/*" \
            org.pitest.mutationtest.commandline.MutationCoverageReport \
            --reportDir "$MUTATION_DIR" \
            --targetClasses "edu.stanford.nlp.*" \
            --targetTests "edu.stanford.nlp.pipeline.*" \
            --sourceDirs "$sourceDir/src/" \
            --mutators ALL \
            --timeoutConst 4000 \
            --outputFormats HTML \
            --testPlugin junit \
            --threads 2 \
            --verbose

      echo "✅ PIT mutation report stored in $MUTATION_DIR"
    fi
  done

  echo "✅ Done for $CUT_CLASS"
  echo "---------------------------------------------"
done
} > $OutputDir/global_output_$1.log 2> $OutputDir/global_error_$1.log
#--add-opens java.base/java.lang=ALL-UNNAMED;
#--add-opens java.base/java.lang.reflect=ALL-UNNAMED;
#--add-opens java.base/java.util=ALL-UNNAMED;
#--add-opens java.base/java.net=ALL-UNNAMED;
#--add-opens java.desktop/java.awt=ALL-UNNAMED;
