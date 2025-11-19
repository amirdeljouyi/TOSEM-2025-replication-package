#!/usr/bin/env bash

# ✅ Source SDKMAN if available
if [ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]; then
  source "$HOME/.sdkman/bin/sdkman-init.sh"
else
  echo "❌ SDKMAN! not installed or not found"
  exit 1
fi

# ✅ Use or switch SDKs
sdk use java 8.0.362-tem

./get_coverage.sh GateCore manually-written-tests/ manually-written-tests coverage 8 manual
./get_coverage.sh Mallet manually-written-tests/ manually-written-tests coverage 8 manual
./get_coverage.sh CogCompNLP manually-written-tests/ manually-written-tests coverage 8 manual

./get_coverage.sh GateCore MW+LLMSuite/ MW+LLMSuite coverage 8 union
./get_coverage.sh Mallet MW+LLMSuite/ MW+LLMSuite coverage 8 union
./get_coverage.sh CogCompNLP  MW+LLMSuite/ MW+LLMSuite coverage 8 union

sdk default java 11.0.25-tem

./get_coverage.sh CoreNLP manually-written-tests/ manually-written-tests coverage 11 manual
./get_coverage.sh CoreNLP MW+LLMSuite/ MW+LLMSuite coverage 11 union

sdk default java 17.0.13-tem

./get_coverage.sh OpenNLP manually-written-tests/ manually-written-tests coverage 11 manual
./get_coverage.sh OpenNLP MW+LLMSuite/ MW+LLMSuite coverage 11 union