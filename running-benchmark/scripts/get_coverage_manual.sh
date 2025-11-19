#!/usr/bin/env bash

set -e

# === Validate Arguments ===
if [ $# -ne 7 ]; then
  echo "Usage: $0 <Project> <test_root_dir> <outputDir> <reason: coverage|mutation|both> <JAVA> <MODE: ES|llm|manual|union> <JUnit: 4 | 5>"
  exit 1
fi

# === Input Arguments ===
PROJECT="$1"
TEST_ROOT_DIR="$2"
OUTPUT_DIR="coverage/$3"
REASON="$4"
JAVA_VERSION="$5"
MODE="$6"
JUNIT="$7"

# === Paths and Constants ===
BENCHMARK_FOLDER="coverage-benchmark"
INPUT_CSV="$BENCHMARK_FOLDER/coverage.$PROJECT.csv"
LIB_FOLDER="$BENCHMARK_FOLDER/lib-$PROJECT"

# === Validate REASON argument ===
if [[ "$REASON" != "coverage" && "$REASON" != "mutation" && "$REASON" != "both" ]]; then
  echo "❌ Invalid reason: $REASON. Must be one of: coverage, mutation, both"
  exit 1
fi

# === Utility Functions ===

run_coverage() {
  local CUT_CLASS="$1" CLASS_FILE="$2" TEST_INDEX="$3" CLASSPATH="$4" JUNIT="$5"

  JAVA_OPTS=(
    -projectCP "$CLASSPATH"
    -class "$CUT_CLASS"
    -Djunit "$CLASS_FILE"
    -Dcriterion=BRANCH:LINE:CBRANCH
    -Doutput_variables=TARGET_CLASS,attempt,criterion,Coverage,Total_Goals,BranchCoverage,LineCoverage,CBranchCoverage,Covered_Goals,Tests_Executed
    -Dminimize=false
    -Dcoverage=false
    -Dtest_format="JUNIT$JUNIT"
    -Ddefuse_debug_mode=true
    -Dattempt="$TEST_INDEX"
    -Dreport_dir="$OUTPUT_DIR"
  )

  if [[ "$JAVA_VERSION" == "8" ]]; then
    java -Devosuite.runtime.sandbox=false \
      -Devosuite.runtime.mock=false \
      -jar llmsuite-coverage-8.jar "${JAVA_OPTS[@]}"
  else
    java \
      -Devosuite.runtime.sandbox=false \
      -Devosuite.runtime.mock=false \
      --add-opens java.base/java.lang=ALL-UNNAMED \
      --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
      --add-opens java.base/java.util=ALL-UNNAMED \
      --add-opens java.base/java.net=ALL-UNNAMED \
      --add-opens java.desktop/java.awt=ALL-UNNAMED \
      -jar llmsuite-coverage.jar "${JAVA_OPTS[@]}"
  fi
}

run_mutation() {
  local PROJECT="$1" CLASSNAME="$2" TEST_INDEX="$3" BINARY_DIR="$4" TEST_JAR="$5" CP="$6" SOURCE_DIR="$7"

  local CUT_JAR="$BINARY_DIR/$CP"
  local COMBINED_JAR="combined-${PROJECT}-${CLASSNAME}-${TEST_INDEX}.jar"
  local TEMP_DIR
  TEMP_DIR=$(mktemp -d)

  mkdir -p "$TEMP_DIR"/{cut,test,combined}
  (cd "$TEMP_DIR/cut" && jar xf "$CUT_JAR")
  (cd "$TEMP_DIR/test" && jar xf "$TEST_JAR")
  cp -r "$TEMP_DIR/cut/"* "$TEMP_DIR/combined/" 2>/dev/null || true
  cp -r "$TEMP_DIR/test/"* "$TEMP_DIR/combined/" 2>/dev/null || true
  jar cf "$COMBINED_JAR" -C "$TEMP_DIR/combined" .
  rm -rf "$TEMP_DIR"

  local MUTATION_DIR="mutation/$PROJECT/$CLASSNAME/$TEST_INDEX"
  mkdir -p "$MUTATION_DIR"

  java -cp "pitest-command-line-1.15.2.jar:$COMBINED_JAR:lib/*" \
    org.pitest.mutationtest.commandline.MutationCoverageReport \
    --reportDir "$MUTATION_DIR" \
    --targetClasses "edu.stanford.nlp.*" \
    --targetTests "edu.stanford.nlp.pipeline.*" \
    --sourceDirs "$SOURCE_DIR/src/" \
    --mutators ALL \
    --timeoutConst 4000 \
    --outputFormats HTML \
    --testPlugin junit \
    --threads 2 \
    --verbose
}

# === Preparation ===
rm -rf ./bin
mkdir -p bin
rm -f llm-tests.jar
mkdir -p "$OUTPUT_DIR"

echo "📑 Reading CUTs from: $INPUT_CSV"
echo "📁 Test root directory: $TEST_ROOT_DIR"
echo "📁 Output directory: $OUTPUT_DIR"

# === Main Processing Loop ===
{
  tail -n +2 "$INPUT_CSV" | while IFS=',' read -r _ CUT_CLASS _ _; do
    CLASSNAME="${CUT_CLASS##*.}"
    PACKAGE_NAME="${CUT_CLASS%.*}"
    PACKAGE_PATH=$(echo "$PACKAGE_NAME" | tr '.' '/')

    if [[ "$MODE" == "llm" || "$MODE" == "manual" || "$MODE" == "union" ]]; then
      PACKAGE_PATH="${PACKAGE_PATH}/${CLASSNAME}"
    fi

    echo "🔍 CUT: $CUT_CLASS"
    MATCHING_DIRS=$(find "$TEST_ROOT_DIR" -type d -path "*/$PACKAGE_PATH")

    for DIR in $MATCHING_DIRS; do
      INDEX="" ELSE_NAME="" MANUAL_NAME=""
      echo "📂 DIR in: $DIR"
      mapfile -t TEST_FILES < <(find "$DIR" -type f -name "*.java")

      echo "🛠 Compiling all test files together"
      javac -cp "$LIB_FOLDER/*" -d bin "${TEST_FILES[@]}"

      echo "📦 Creating test JAR..."
      jar cf llm-tests.jar -C bin .

      for TEST_FILE in "${TEST_FILES[@]}"; do
        BASENAME=$(basename "$TEST_FILE" .java)
        if [[ "$MODE" == "manual" || "$MODE" == "union" ]]; then
          if [[ "$BASENAME" =~ ^Test.* || "$BASENAME" =~ .*Test$ ]]; then
              MANUAL_NAME=$BASENAME
          fi
        fi
      done

      for TEST_FILE in "${TEST_FILES[@]}"; do
        BASENAME=$(basename "$TEST_FILE" .java)
        if [[ "$BASENAME" =~ ${CLASSNAME}_([0-9]+)_([A-Za-z0-9]+Test) ]]; then
            INDEX="${BASH_REMATCH[1]}"
            SUFFIX="${BASH_REMATCH[2]}"
            if [[ "$MODE" == "llm" || "$MODE" == "ES" ]]; then
              CLASS_FILE=$(find bin -name "${CLASSNAME}_$INDEX*.class")
            elif [[ "$MODE" == "union" ]]; then
              ES_CLASSES=$(find bin -name "${CLASSNAME}_${INDEX}*.class")
              MANUAL_CLASSES=$(find bin -name "${MANUAL_NAME}.class")
              CLASS_FILE=$(printf "%s\n%s\n" "$MANUAL_CLASSES"  "$ES_CLASSES" | tr '\n' ':' | sed 's/:$//')
              TEST_INDEX="$INDEX"
            fi
        elif [[ "$MODE" == "manual" ]]; then
          CLASS_FILE=$(find bin -name "${MANUAL_NAME}.class")
          TEST_INDEX=1
        else
          continue
        fi

        TEST_CLASS=$(echo "$CLASS_FILE" | tr ':' '\n' | sed 's|^bin/||;s|/|.|g;s|\.class$||' | paste -sd, -)
        echo "TEST_CLASS: $TEST_CLASS"

        BINARY_DIR="/Users/amirdeljouyi/se/llm-powered-unit-test-generation-for-nlp-libraries/benchmark/binary/$PROJECT"
        SOURCE_DIR="/Users/amirdeljouyi/se/llm-powered-unit-test-generation-for-nlp-libraries/benchmark/source/$PROJECT"

        [[ -f "${BINARY_DIR}/evosuite-files/evosuite.properties" ]] && source "${BINARY_DIR}/evosuite-files/evosuite.properties"

        newCP=""
        IFS=':' read -ra jars <<< "$CP"
        for jar in "${jars[@]}"; do
          newCP+="$BINARY_DIR/$jar:"
        done
        newCP+="llm-tests.jar"

        [[ "$REASON" == "coverage" || "$REASON" == "both" ]] && run_coverage "$CUT_CLASS" "$TEST_CLASS" "$TEST_INDEX" "$newCP" "$JUNIT"
#        [[ "$REASON" == "coverage" || "$REASON" == "both" ]] && run_coverage "$CUT_CLASS" "$CLASS_FILE" "$TEST_INDEX" "$newCP" "$JUNIT"
        [[ "$REASON" == "mutation" || "$REASON" == "both" ]] && run_mutation "$PROJECT" "$CLASSNAME" "$TEST_INDEX" "$BINARY_DIR" "$(pwd)/llm-tests.jar" "$CP" "$SOURCE_DIR"
      done
    done

    echo "✅ Done with $CUT_CLASS"
    echo "---------------------------------------------"
  done
} > "$OUTPUT_DIR/global_output_$PROJECT.log" 2> "$OUTPUT_DIR/global_error_$PROJECT.log"