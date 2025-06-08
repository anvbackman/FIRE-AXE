# LLM Refactoring Evaluation Toolkit

This repository contains the dataset, prompt templates, tool source code, and evaluation results from 
our bachelor thesis.

It supports reproducibility of the experiments conducted using Gemini 1.5 Pro and LLaMA 2 for refactoring 
hardcoded values and inline method wrappers in legacy Java code. 

## FIRE AXE

**FIRE AXE** stands for *Fine-grained Refactoring using API and Experimental Evaluation*.

It is the core Java-based tool developed to interact with LLMs and automate the refactoring and evaluation process.



## Artifact Structure
- "/src/" – Source code for the Java-based tool used to send code to LLMs and collect responses.
- "/prompts/" – LLM prompt templates used during the experiments.
- "/datasets/" – Raw Java classes (.txt format) containing technical debt patterns.
- "/evaluation/" – PDF files with scoring results (per class and per model).
- "/results/" – Charts and visual summaries (.pdf).

## How to Use

### Requirements
- Java 17+
- Internet access for API communication (Gemini or local LLaMA 2)
- Ollama (https://ollama.com/) installed with Code Llama (`codellama`) model:
  ```bash
  # Install Ollama if not already installed
  curl -fsSL https://ollama.com/install.sh | sh

  # Pull the Code Llama model
  ollama run codellama
  
- Gemini API Setup:
Open the file src/main/java/GeminiCall.java.
Locate the line that says:
```
URL url = new URL("REPLACE WITH URL AND KEY");
```
And replace it with your actual Gemini API URL and Key.

### Build & Run
```bash
cd src/main/java
javac Main.java
java Main
```

### Replication of study
1. Clone the reproduction package:
```
git clone https://github.com/anvbackman/FIRE-AXE.git
```
2. Follow the setup steps above to configure Java, Gemini, and Ollama.
3. Run the tool using the legacy Java classes located in the /datasets/classes folder.
4. Evaluate the output using the scoring rubric.


## Dataset Description

The dataset consists of 24 legacy Java classes gathered from open-source GitHub repositories.
Each ".txt" file contains one class.

Each class is manually inspected and contains one or more instances of:
- Hardcoded values
- Inline method wrappers

All files are included in "/datasets/classes".

References to original repositories are listed in "datasets/sources.md".

## Prompts

All prompts used are available in "/prompts/prompts.md"

## LLM Responses

All raw outputs returned from the LLMs (Gemini 1.5 Pro and LLaMA 2) are stored in "/llm-responses/" in JSON format.

Each file contains:
- Class Name
- LLM output (refactored code)

## Evaluation Criteria

We evaluated each LLM-generated refactoring using a structured rubric with the following criteria:

### Accuracy (Gating Criterion: TRUE/FALSE)
- Determines whether the refactoring is functionally correct.
- If the refactored code introduces bugs, removes required logic, or alters intended behavior, it is marked FALSE.
- Only refactorings that pass this check (i.e., are marked TRUE) are included in further scoring.

### Clarity (1–5 points)
- Measures how readable and understandable the refactored code is.
- Based on use of naming conventions, simplicity, consistent formatting, and overall legibility.
- 1 = unreadable/confusing, 5 = very clear.

### Maintainability (1–5 points)
- Evaluates how well the refactoring improves or maintains code structure and long-term manageability.
- Rewards removal of redundancy, better separation of concerns, and use of constants or helper methods.
- 1 = introduces technical debt, 5 = clean, extensible structure.

### Instruction Fidelity (0–1 points)
- Checks whether the LLM followed the task instructions precisely.
- 1 = task completed as instructed (e.g., only hardcoded values replaced),  
  0 = ignored or misunderstood the prompt.


