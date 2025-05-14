import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LlamaCall implements LLMCall {

    private final ObjectMapper mapper = new ObjectMapper();

    public void send(String filePath, String className, String taskType) throws IOException {

        // Read file content as a String
        String code = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);

        String instruction = "";

        if (taskType.equals("inline")) {
            instruction = "Imagine you are a developer responsible for updating the following legacy code to remove all unnecessary inline method wrappers. "
                    + "These include methods that simply return a field or delegate directly to another method without adding logic. "
                    + "Do not change the class's functionality. For each refactoring, respond in this format: [Original Method] → [Refactored Method]. " +
                    "Do not send the whole class.";

        }

        else if (taskType.equals("hardcode")) {
            instruction = "Consider you are responsible for a team responsible to alter the following legacy code to remove all unnecessary hardcoded values. "
                    + "These include methods that simply return a field or delegate directly to another method without adding logic. "
                    + "Do not change the class's functionality. For each refactoring, respond in this format: [Original Method] → [Refactored Method]. " +
                    "Do not send the whole class.";
        }

        String fullPrompt = instruction + "\n\n" + code;

        // Escape JSON string
        String escapedPrompt = escapeJsonString(fullPrompt);

        // Connect to local Ollama API
        URL url = new URL("http://localhost:11434/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // JSON body
        String inputJson = """
        {
          "model": "codellama",
          "prompt": "%s",
          "stream": false
        }
        """.formatted(escapedPrompt);

        System.out.println("Request JSON: " + inputJson);  // For debugging

        // Send request
        try (OutputStream os = conn.getOutputStream()) {
            os.write(inputJson.getBytes(StandardCharsets.UTF_8));
        }

        // Read and parse response
        JsonNode responseJson;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            responseJson = mapper.readTree(br);
        }

        String result = responseJson.has("response") ? responseJson.get("response").asText() : "No response.";
        System.out.println("Response: " + result);

        // Save to file
        JsonOutputWriter fileWriter = new JsonOutputWriter();
        fileWriter.writeFile("llama-results.json", className, result, taskType);
    }

    private String escapeJsonString(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
