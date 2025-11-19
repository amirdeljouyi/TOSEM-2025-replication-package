#!/bin/bash

export JDK_JAVA_OPTIONS="-Djdk.attach.allowAttachSelf=true"

echo "RUNNING ON THE $direction DATASET"

if [ $# -lt 2 ]; then
  fileDirectory="17.classes"
else
  fileDirectory="$1-classes"
fi

if [ $# -lt 1 ]; then
  attempt=""
else
  attempt="$1"
fi

outputDir="/app/dataset/codamosa"

# Prepare log directory
logDir="$outputDir/log/"
mkdir -p "$logDir"

{
  while IFS="," read -r proj class src llm_test
  do
    echo "Attempt: $attempt"
    echo "Project: $proj"
    echo "Class: $class"
    echo ""

    sourceDir="/app/dataset/$proj"

    # Source properties
    if [ -f "${sourceDir}/evosuite-files/evosuite.properties" ]; then
      source ${sourceDir}/evosuite-files/evosuite.properties
    else
      echo "Warning: evosuite.properties file not found in $proj."
    fi

    if [ $# -lt 1 ]; then
      prefix=$(date '+%Y-%m-%d-%H-%M')
      postfix=/$prefix
      junit_suffix="_ESTest"
      dattempt=""
    else
      prefix="$1"
      postfix=""
      junit_suffix=_${attempt}_ESTest
      dattempt="-Dattempt=$attempt"
    fi

    echo "junit_suffix: $junit_suffix"
    echo "postfix: $postfix"
    echo "prefix: $prefix"
    echo "projectCP: $sourceDir/$CP"

    # Convert CP into full paths
    newCP=""
    IFS=':' read -ra jars <<< "$CP"
    for jar in "${jars[@]}"; do
      newCP+="$sourceDir/$jar:"
    done

    # Remove trailing colon
    newCP="${newCP%:}"

    echo "newCP: $newCP"

    llmSource="/app/dataset/source/${proj}/${src}"
    echo "llmSource: $llmSource"

    java --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/sun.util.calendar=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED \
      -jar llmsuite-r.jar -projectCP "$newCP" -class $class -Dcriterion=BRANCH:LINE:OUTPUT:METHOD:CBRANCH \
      -Dtest_naming_strategy=coverage -Dvariable_naming_strategy=TYPE_BASED -Dassertion_timeout=100000 \
      -Dsearch_budget=900 -Dminimize=false -Dcoverage=true -Dwrite_junit_timeout=100000 -Dextra_timeout=10000 \
      -Dalgorithm=CODAMOSA -Dllm_stabled_budget=30 -Dllm_test_generation_budget=5 $dattempt \
      -Doutput_variables=TARGET_CLASS,attempt,criterion,Coverage,Total_Goals,BranchCoverage,LineCoverage,OutputCoverage,CBranchCoverage,MethodCoverage,Covered_Goals,CoverageTimeline,Fitness,FitnessTimeline,BranchCoverageTimeline,LineCoverageTimeline,Tests_Executed,Total_Time \
      -Dllm_test_generation_approach=FILES -Dllm_test_file_source_directory=llm-tests-codamosa/$llm_test -Dtimeline_interval=5000 \
      -Ddefuse_debug_mode=true -Dtest_format=JUNIT4 -Djunit_check_timeout=10000 -Dllm_source_directory=$llmSource \
      -Dcheck_contracts=false -Dllm_static_constant_pool=false -Dsandbox=false -Dno_runtime_dependency=false -Dreset_static_fields=false \
      -Dreport_dir=$outputDir/evosuite-report -DOUTPUT_DIR=$outputDir/evosuite-files -Dtest_dir=$outputDir/generated-tests$postfix -Djunit_suffix=$junit_suffix \
      -Dbytecode_logging_mode=FILE_DUMP -Dllm_endpoint="http://172.17.0.1:8000/graphql" \
          > $logDir/$class-$prefix-stat.log 2> $logDir/$class-$prefix-error.log
  done < <(tail -n +2 $fileDirectory.csv)
} > $logDir/global_output.log 2> $logDir/global_error.log
