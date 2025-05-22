# LLM Refactoring Evaluation Toolkit

This repository contains the dataset, prompt templates, tool source code, and evaluation results from 
our bachelor thesis.

It supports reproducibility of the experiments conducted using Gemini 1.5 Pro and LLaMA 2 for refactoring 
hardcoded values and inline method wrappers in legacy Java code.


## Artifact Structure
- "/src/" – Source code for the Java-based tool used to send code to LLMs and collect responses.
- "/prompts/" – LLM prompt templates used during the experiments.
- "/datasets/" – Raw Java classes (.txt format) containing technical debt patterns.
- "/evaluation/" – PDF files with scoring results (per class and per model).
- "/results/" – Charts and visual summaries (.pdf).

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

