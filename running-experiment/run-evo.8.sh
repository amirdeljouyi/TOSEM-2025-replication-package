#!/bin/bash

export JDK_JAVA_OPTIONS="-Djdk.attach.allowAttachSelf=true"

echo "RUNNING ON THE $direction DATASET"

if [ $# -lt 2 ]; then
  fileDirectory="classes"
else
  fileDirectory="$1-classes"
fi

if [ $# -lt 1 ]; then
  attempt=""
else
  attempt="$1"
fi

outputDir="/app/dataset/evosuite/"

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

    sourceDir="/app/dataset/$proj/"


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

    # Convert CP into full paths
    newCP=""
    IFS=':' read -ra jars <<< "$CP"
    for jar in "${jars[@]}"; do
      newCP+="$sourceDir/$jar:"
    done

    # Remove trailing colon
    newCP="${newCP%:}"

    echo "newCP: $newCP"

    java -jar llmsuite-8.jar -projectCP "$newCP" -class $class -Dcriterion=BRANCH:LINE:OUTPUT:METHOD:CBRANCH \
     -Dtest_naming_strategy=coverage -Dvariable_naming_strategy=TYPE_BASED -Dassertion_timeout=100000 \
     -Dsearch_budget=900 -Dminimize=false -Dcoverage=true -Dwrite_junit_timeout=100000 -Dextra_timeout=10000 \
     -Dalgorithm=DYNAMOSA $dattempt \
     -Doutput_variables=TARGET_CLASS,attempt,criterion,Coverage,Total_Goals,BranchCoverage,LineCoverage,OutputCoverage,CBranchCoverage,MethodCoverage,Covered_Goals,CoverageTimeline,Fitness,FitnessTimeline,BranchCoverageTimeline,LineCoverageTimeline,Tests_Executed,Total_Time \
     -Dllm_test_generation_approach=FILES -Dllm_test_file_source_directory=llmsuite-files-tests/llm-tests/$llm_test -Dtimeline_interval=5000 \
     -Ddefuse_debug_mode=true -Dtest_format=JUNIT4 -Djunit_check_timeout=10000 -Dllm_source_directory=source/$project/$src \
     -Dcheck_contracts=false -Dllm_static_constant_pool=false -Dsandbox=false -Dno_runtime_dependency=false -Dreset_static_fields=false \
     -Dreport_dir=$outputDir/evosuite-report -Dtest_dir=$outputDir/generated-tests$postfix -Djunit_suffix=$junit_suffix \
     -Dbytecode_logging_mode=FILE_DUMP \
     > $logDir/$class-$prefix-stat.log 2> $logDir/$class-$prefix-error.log

  done < <(tail -n +2 $fileDirectory.csv)
} > $logDir/global_output.log 2> $logDir/global_error.log