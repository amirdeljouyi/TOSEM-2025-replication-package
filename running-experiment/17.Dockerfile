# Base image
FROM eclipse-temurin:17-jdk AS llmsuite-nlp-client
#FROM eclipse-temurin:11-jdk AS llmsuite-nlp-client
LABEL authors="amirdeljouyi"

RUN apt-get update && apt-get clean

# Set workdir
WORKDIR /app

# Copy binary folder to /app/dataset
COPY binary/ /app/dataset/
COPY source/ /app/dataset/source/
COPY llm-tests/ /app/llm-tests/
COPY llm-tests-wosr/ /app/llm-tests-wosr/
COPY llm-tests-codamosa/ /app/llm-tests-codamosa/
COPY llm-pool/ /app/llm-pool/

# Copy necessary scripts
COPY *.sh *.csv *.jar /app/

# Ensure scripts are executable
RUN chmod +x /app/*.sh /app/*.csv /app/*.jar

# Set workdir to dataset if needed
WORKDIR /app

# Run entrypoint script that picks the right one
ENTRYPOINT ["bash", "/app/run-experiment.sh"]
CMD ["bash", "/app/run-experiment.sh"]