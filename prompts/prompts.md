# Prompts 

The following prompts was used for both LLMs. 

- Hardcoded Values: "Consider you are responsible for a team responsible to alter the following legacy code to 
remove all unnecessary hardcoded values. These include methods that simply return a field or delegate directly to 
another method without adding logic. Do not change the class's functionality. For each refactoring, 
respond in this format: [Original Method] → [Refactored Method]. Do not send the whole class."

- Inline Method Wrappers: "Imagine you are a developer responsible for updating the following legacy code 
to remove all unnecessary inline method wrappers. These include methods that simply return a field or 
delegate directly to another method without adding logic. Do not change the class's functionality. 
For each refactoring, respond in this format: [Original Method] → [Refactored Method]. Do not send the whole class."