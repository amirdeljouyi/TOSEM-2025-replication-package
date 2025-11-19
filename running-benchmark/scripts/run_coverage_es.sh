#!/usr/bin/env bash

# ✅ Source SDKMAN if available
if [ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]; then
  source "$HOME/.sdkman/bin/sdkman-init.sh"
else
  echo "❌ SDKMAN! not installed or not found"
  exit 1
fi

# ✅ Use or switch SDKs

#./get_coverage.sh GateCore llmsuite-aggregated/generated-tests/ llmSuite coverage False 8
./get_coverage.sh GateCore evosuite-aggregated/generated-tests/ evosuite coverage False 8
./get_coverage.sh GateCore llm-tests/ llm-tests coverage False 8

#./get_coverage.sh Mallet llmsuite-aggregated/generated-tests/ llmSuite coverage False 8
./get_coverage.sh Mallet evosuite-aggregated/generated-tests/ evosuite coverage False 8
./get_coverage.sh Mallet llm-tests/ llm-tests coverage False 8

#./get_coverage.sh CogCompNLP llmsuite-aggregated/generated-tests/ llmSuite coverage False 8
./get_coverage.sh CogCompNLP evosuite-aggregated/generated-tests/ evosuite coverage False 8
./get_coverage.sh CogCompNLP llm-tests/ llm-tests coverage False 8

sdk default java 11.0.25-tem

#./get_coverage.sh CoreNLP llmsuite-aggregated/generated-tests/ llmSuite coverage False 11
./get_coverage.sh CoreNLP evosuite-aggregated/generated-tests/ evosuite coverage False 11
./get_coverage.sh CoreNLP llm-tests/ llm-tests coverage False 11

sdk default java 17.0.13-tem

./get_coverage.sh OpenNLP evosuite-aggregated/generated-tests/ evosuite coverage False 17
#./get_coverage.sh OpenNLP llmsuite-aggregated/generated-tests/ llmSuite coverage False 17
./get_coverage.sh OpenNLP llm-tests/ llm-tests coverage False 17