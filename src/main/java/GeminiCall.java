import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GeminiCall {

    private final ObjectMapper mapper = new ObjectMapper();

    public void send(String prompt, String className) throws IOException {

        URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro:generateContent?key=AIzaSyAJl_pMMjxaUhYnbhS9c9ijtGAMQwQC8S4");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

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
            """.formatted(prompt);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(inputJson.getBytes(StandardCharsets.UTF_8));
        }

        // --- read full JSON response into a string ---
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
        }

        // Parse the JSON response
        JSONObject fullJson = new JSONObject(response.toString());
        JSONArray candidates = fullJson.getJSONArray("candidates");
        JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
        JSONArray parts = content.getJSONArray("parts");
        String resultText = parts.getJSONObject(0).getString("text").trim();

        // Create output
        JSONObject existingOutput = new JSONObject();
        File outputFile = new File("output.json");
        if (outputFile.exists()) {
            try (FileReader reader = new FileReader(outputFile)) {
                existingOutput = new JSONObject(new JSONTokener(reader));
            } catch (Exception e) {
                System.err.println("Failed to parse existing output.json. Starting fresh.");
            }
        }

        JSONObject resultWrapper = new JSONObject();
        resultWrapper.put("result", resultText);
        existingOutput.put(className, resultWrapper);

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(existingOutput.toString(2));
            System.out.println("Updated output.json:");
            System.out.println(existingOutput.toString(2));
        }


    }
}
