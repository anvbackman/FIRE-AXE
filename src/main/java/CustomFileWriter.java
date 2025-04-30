import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CustomFileWriter {

//    private String texti;

    public void writeFile(String fileName, String className, StringBuilder response) throws IOException {
        // Parse the JSON response
        JSONObject fullJson = new JSONObject(response.toString());
        JSONArray candidates = fullJson.getJSONArray("candidates");
        JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
        JSONArray parts = content.getJSONArray("parts");
        String resultText = parts.getJSONObject(0).getString("text").trim();

        // Create output
        JSONObject existingOutput = new JSONObject();
        File outputFile = new File(fileName);
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
