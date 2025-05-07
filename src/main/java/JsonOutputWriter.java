import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonOutputWriter {

    public void writeFile(String fileName, String className, String response) throws IOException {
        // Parse the Gemini API response
        JSONObject fullJson = new JSONObject(response);
        JSONArray candidates = fullJson.getJSONArray("candidates");
        JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
        JSONArray parts = content.getJSONArray("parts");
        String rawText = parts.getJSONObject(0).getString("text").trim();
        String resultText = unescapeJsonString(rawText);

        // Read existing output file (if exists)
        JSONObject existingOutput = new JSONObject();
        File outputFile = new File(fileName);
        if (outputFile.exists()) {
            try (FileReader reader = new FileReader(outputFile)) {
                existingOutput = new JSONObject(new JSONTokener(reader));
            } catch (Exception e) {
                System.err.println("Failed to parse existing JSON. Starting fresh.");
            }
        }

        // Wrap the result text into an array of lines (each max 100 chars)
        JSONArray wrappedLines = wrapTextAsArray(resultText, 100);

        // Add/Update the result in the output JSON
        JSONObject resultWrapper = new JSONObject();
        resultWrapper.put("result", wrappedLines);
        existingOutput.put(className, resultWrapper);

        // Write updated JSON to file
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(existingOutput.toString(2));  // pretty print with indent
            System.out.println("Updated output.json:");
            System.out.println(existingOutput.toString(2));
        }
    }

    // Convert long string into array of strings, each up to maxLineLength
    private JSONArray wrapTextAsArray(String input, int maxLineLength) {
        JSONArray lines = new JSONArray();
        int index = 0;
        while (index < input.length()) {
            int end = Math.min(index + maxLineLength, input.length());
            lines.put(input.substring(index, end));
            index = end;
        }
        return lines;
    }

    // Turn escaped text into actual newlines, tabs, etc.
    private String unescapeJsonString(String input) {
        return input
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}
