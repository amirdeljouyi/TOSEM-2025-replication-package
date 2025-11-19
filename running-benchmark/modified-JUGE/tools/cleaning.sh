TOOL=$1
FOLDER="results_$2"

cd ${TOOL}/${FOLDER}

find . -type d -name "metrics" -exec rm -r {} +
find . -type d -name "bin" -exec rm -r {} +
find . -type d -name "mutated_code" -exec rm -r {} +
find . -type d -name "classes_instrumented" -exec rm -r {} +
find . -type f -name "mutation_results.txt" -exec rm -r {} +
find . -type f -name "transcript.csv" -exec rm -r {} +
find . -type f -name "SBSTDummyForCoverageAndMutationCalculation.java" -exec rm -r {} +