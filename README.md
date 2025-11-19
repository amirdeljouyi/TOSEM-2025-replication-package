# TOSEM-2025-replication-package for LLMSuite 

This repository contains the full replication package for our paper on **LLMSuite**, a hybrid framework that combines Large Language Models (LLMs) with Search-Based Software Testing (SBST) to generate high-quality test cases, especially for Natural Language Processing (NLP) libraries.

Everything you need to reproduce our experiments and results is included here—from dataset and implementation to experiment scripts and results.

---

## 📁 What's Inside

```
.
├── dataset/             # Benchmark dataset of NLP classes
├── implementation/      # LLMSuite components and EvoSuite integration
├── results/             # Outputs from all research questions (RQ1–RQ4)
├── running-benchmark/   # Tools and scripts for coverage/mutation evaluation
└── running-experiment/  # Scripts and Docker setup to run experiments
```

---

## 🧪 Dataset

- `dataset/Dataset.csv`: A curated list of 100 NLP-related classes used in our experiments.

---

## ⚙️ Implementation

### `LLMSuite-LLMServer/`
This Python module handles:
- Prompt generation for LLMs
- Parsing and validating LLM-generated test snippets

> To start the server:
```bash
cd implementation/LLMSuite-LLMServer
bash run-server.sh
```

---

### `LLMSuite-TestGeneration/`
An extended version of EvoSuite that:
- Accepts LLM-generated test cases
- Supports Java 8 and 11 environments

> To build the test generator:
```bash
cd implementation/LLMSuite-TestGeneration
mvn clean install
```

---

## 📊 Results

All experimental results are organized by research question:

- `RQ1-and-RQ3/`: Coverage and mutation results across test generators including EvoSuite, CodaMosa
- `RQ2/`: Comparisons with zero-shot prompting, and other variants
- `RQ4/`: Analysis of LLMSuite vs. manually-written tests
- `generated-tests/`: Collected test suites from each tool

## 🚀 Running Experiments

Inside `running-experiment/`, you’ll find everything you need to launch LLMSuite, EvoSuite, and other variants on the full benchmark:

- Dockerfiles for each setup (Java 8, 11, 17)
- Test scripts for each variant
- Precompiled binaries for NLP libraries
- Collected test outputs and reports

> To run the full suite of experiments:
```bash
cd ./build-docker
bash ./run-llmsuite-containers 11
```

---

## 🧪 Running Benchmarks

The `running-benchmark/` folder contains a customized version of the JUGE benchmark framework for evaluating mutation scores, and also the scripts to run coverage, and comments the erroneous lines of LLM-generated test cases.
To run JUGE, the framework requires downloading both source and binary files (included in `running-experiment/binary/`), zipping them, and placing the archive in `infrastructure/benchmarks/projects/`.

---

## 🔧 Building Requirements

- Python 3.8+
- Java 8 / Java 11 / Java 17
- Docker & Docker Compose
- Maven
- Access to an LLM API (e.g., OpenAI or HuggingFace, configured via `config.env`)

---

