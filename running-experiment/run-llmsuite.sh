#!/bin/bash

export JDK_JAVA_OPTIONS="-Djdk.attach.allowAttachSelf=true"

echo "RUNNING ON THE $direction DATASET"

if [ $# -lt 1 ]; then
  fileDirectory="classes"
else
  fileDirectory="$1-classes"
fi

while IFS="," read -r proj class src
do
  echo "Project: $proj"
  echo "Class: $class"
  echo ""

  sourceDir="/app/dataset/$proj/"
  outputDir="llmsuite-tests/"

  # Prepare log directory
  logDir="$outputDir/log/"
  mkdir -p "$logDir"

  # Source properties
  if [ -f "${sourceDir}/evosuite-files/evosuite.properties" ]; then
    source ${sourceDir}/evosuite-files/evosuite.properties
  else
    echo "Warning: evosuite.properties file not found in $proj."
  fi

  java -jar llmsuite-n.jar -projectCP "$sourceDir/$CP" -class $class -Dcriterion=BRANCH:LINE:OUTPUT:METHOD:CBRANCH \
   -Dtest_naming_strategy=llm_based -Dvariable_naming_strategy=HEURISTICS_BASED -Dassertion_timeout=100000 \
   -Dsearch_budget=1000 -Dminimization_timeout=100000 -Dwrite_junit_timeout=100000 -Dextra_timeout=10000 -Dalgorithm=LLM_DYNAMOSA -Dllm_stabled_budget=20 \
   -Ddefuse_debug_mode=true -Dtest_format=JUNIT5LLM -Djunit_check_timeout=10000 -Dllm_source_directory=source/$project/$src \
   -Dreport_dir=$outputDir/evosuite-report -Dtest_dir=$outputDir/generated-tests/$(date '+%Y-%m-%d-%H') \
   -Dbytecode_logging_mode=FILE_DUMP > $logDir/$class-$(date '+%Y-%m-%d-%H-%M').log

done < <(tail -n +2 $fileDirectory.csv)
