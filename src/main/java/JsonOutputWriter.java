import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonOutputWriter {

    public void writeFile(String fileName, String className, String response, String taskType) throws IOException {
        String resultText;

        // Gemini-style JSON
        if (response.trim().startsWith("{")) {
            JSONObject fullJson = new JSONObject(response);
            if (fullJson.has("candidates")) {
                JSONArray candidates = fullJson.getJSONArray("candidates");
                JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");
                resultText = parts.getJSONObject(0).getString("text").trim();
            } else {
                resultText = fullJson.optString("response", "No response").trim(); // fallback for LLaMA
            }
        } else {
            resultText = response.trim(); // raw string for LLaMA
        }

        resultText = unescapeJsonString(resultText);

        // Load existing file
        File outputFile = new File(fileName);
        JSONObject existingOutput = new JSONObject();

        if (outputFile.exists()) {
            try (FileReader reader = new FileReader(outputFile)) {
                existingOutput = new JSONObject(new JSONTokener(reader));
            } catch (Exception e) {
                System.err.println("Failed to parse existing JSON. Starting fresh.");
            }
        }

        // Wrap into shorter lines
        JSONArray wrappedLines = wrapTextAsArray(resultText, 100);
        JSONObject resultWrapper = new JSONObject();
        resultWrapper.put("result", wrappedLines);

        // Insert under taskType and className
        JSONObject taskSection = existingOutput.optJSONObject(taskType);
        if (taskSection == null) {
            taskSection = new JSONObject();
        }

        taskSection.put(className, resultWrapper);
        existingOutput.put(taskType, taskSection);

        // Write updated JSON to file
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(existingOutput.toString(2));  // pretty print with indent
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
