import java.io.File;
import java.io.IOException;

public class ProcessFiles {

    public static void processFolder(File folder, LLMCall call, String taskType) throws IOException {
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Skipping non-existent folder: " + folder.getAbsolutePath());
            return;
        }
        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                String className = file.getName().replace(".txt", ".java");
                call.send(file.getAbsolutePath(), className, taskType);
            }
        }
    }
}
