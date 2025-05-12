import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GeminiCall implements LLMCall {

    private final ObjectMapper mapper = new ObjectMapper();

    public void send(String filePath, String className, String taskType) throws IOException {

        // Read file content as a String
        String code = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
//        String instruction = "Refactor the following legacy Java class to remove all unnecessary inline method wrappers. These include methods that simply return a field or delegate directly to another method without adding logic. Do not change the class's functionality. For each refactoring, respond in this format: [Original Method] → [Refactored Method]. Do not send the whole class.";
        String instruction = "";

        if (taskType.equals("inline")) {
            instruction = "Refactor the following legacy Java class to remove all unnecessary inline method wrappers. "
                    + "These include methods that simply return a field or delegate directly to another method without adding logic. "
                    + "Do not change the class's functionality. For each refactoring, respond in this format: [Original Method] → [Refactored Method]. Do not send the whole class.";

        }

        else if (taskType.equals("hardcode")) {
            instruction = "Refactor the following legacy Java class to remove all unnecessary hardcoded values. "
                    + "These include methods that simply return a field or delegate directly to another method without adding logic. "
                    + "Do not change the class's functionality. For each refactoring, respond in this format: [Original Method] → [Refactored Method]. Do not send the whole class.";

        }
        String fullText = instruction + code;

        // Escape the code content properly for JSON
        String escapedCode = escapeJsonString(fullText);

        // Prepare API URL
        URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro:generateContent?key=AIzaSyAJl_pMMjxaUhYnbhS9c9ijtGAMQwQC8S4");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Create JSON payload
        String inputJson = """
            {
              "contents": [
                {
                  "parts": [
                    {
                      "text": "%s"
                    }
                  ]
                }
              ]
            }
            """.formatted(escapedCode);

        System.out.println("Request JSON: " + inputJson);  // For debugging

        // Send the request
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = inputJson.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Check for successful response
        int statusCode = conn.getResponseCode();
        if (statusCode == HttpURLConnection.HTTP_OK) {
            // Read the response if successful
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
            }
            System.out.println("Response: " + response.toString());
            JsonOutputWriter fileWriter = new JsonOutputWriter();
            fileWriter.writeFile("gemini-results.json", className, response.toString(), taskType);
        } else {
            // Handle non-OK response
            System.out.println("Error: HTTP " + statusCode);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line.trim());
                }
            }
        }
    }

    private String extractAndUnescapeCode(String jsonResponse) {
        // Find the code block inside the triple backticks
        int start = jsonResponse.indexOf("```java");
        int end = jsonResponse.indexOf("```", start + 7);

        if (start != -1 && end != -1) {
            String codeBlock = jsonResponse.substring(start + 7, end); // Skip '```java'
            // Unescape special characters
            return codeBlock
                    .replace("\\n", "\n")
                    .replace("\\t", "\t")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
        }
        return "Could not extract code block.";
    }


    private String escapeJsonString(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

