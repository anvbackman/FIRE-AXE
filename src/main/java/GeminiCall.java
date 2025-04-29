import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GeminiCall {

    private final ObjectMapper mapper = new ObjectMapper();

    public String send(String prompt) throws IOException {

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
        StringBuilder full = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                full.append(line);
            }
        }

        // --- parse JSON and extract only the model’s text ---
        JsonNode root = mapper.readTree(full.toString());
        JsonNode parts = root
                .path("candidates")
                .get(0)
                .path("content")
                .path("parts");
        String text = parts.get(0).path("text").asText();

        return text;
    }
}
