
# TODO Automate the build of benchmarktool in a temporary container

FROM ubuntu:20.04
RUN apt-get update
RUN apt-get install -y openjdk-11-jdk
RUN apt-get install -y unzip
RUN apt-get install -y vim

# SMT Solver 
RUN apt-get update && \
    apt-get install -y cvc5 || echo "⚠️  cvc5 not available in apt — install manually if needed"
# Copy the utility scripts to run the infrastructure
COPY infrastructure/scripts/ /usr/local/bin/

# [R](https://www.r-project.org)
RUN apt-get install -y libgmp-dev libmpfr-dev
RUN apt-get install -y r-base
RUN Rscript /usr/local/bin/get-libraries.R

# Copy dependencies
RUN mkdir -p /usr/local/bin/lib/
COPY infrastructure/lib/junit-4.12.jar /usr/local/bin/lib/junit.jar
COPY infrastructure/lib/mockito-core-4.11.0.jar /usr/local/bin/lib/mockito-core-4.11.0.jar
COPY infrastructure/lib/hamcrest-core-1.3.jar /usr/local/bin/lib/hamcrest-core.jar
COPY infrastructure/lib/pitest-1.15.2.jar /usr/local/bin/lib/pitest.jar
COPY infrastructure/lib/pitest-command-line-1.15.2.jar /usr/local/bin/lib/pitest-command-line.jar
COPY infrastructure/lib/pitest-entry-1.15.2.jar /usr/local/bin/lib/pitest-entry.jar
COPY infrastructure/lib/jacocoagent.jar /usr/local/bin/lib/jacocoagent.jar

# Copy the last version of the benchmarktool utilities
COPY benchmarktool/target/benchmarktool-1.0.0-shaded.jar /usr/local/bin/lib/benchmarktool-shaded.jar

# Copy the projects and configuration file to run the tools on a set of CUTs
RUN mkdir /var/benchmarks
COPY infrastructure/benchmarks/ /var/benchmarks/
RUN for f in /var/benchmarks/projects/*.zip; do \
    dirname=$(basename "$f" .zip); \
    mkdir -p "/var/benchmarks/projects/$dirname"; \
    unzip "$f" -d "/var/benchmarks/projects/$dirname"; \
done
RUN rm -f /var/benchmarks/projects/*.zip
RUN rm -f /var/benchmarks/projects/*_split.z*

ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
#ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-arm64


