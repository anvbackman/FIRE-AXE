import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GeminiCall {

    private final ObjectMapper mapper = new ObjectMapper();

    public void send(String prompt) throws IOException {

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
        String resultText = parts.getJSONObject(0).getString("text");

        // Create output
        JSONObject resultWrapper = new JSONObject();
        resultWrapper.put("result", resultText.trim());

        JSONObject output = new JSONObject();
        output.put("Application.java", resultWrapper);

        try (FileWriter file = new FileWriter("output.json")) {
            file.write(output.toString(2)); // Pretty print with indentation
            System.out.println("Written to output.json:");
            System.out.println(output.toString(2));
        }


    }
}
