import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {

        GeminiCall geminiCall = new GeminiCall();
        String filePath = "Code2.txt";
        LlamaCall llamaCall = new LlamaCall();
        String filePath2 = "Code.txt";
        try {
            geminiCall.send(filePath, "Application26.java"); // Pass the file path and class name
            geminiCall.send(filePath, "Application12.java"); // Pass the file path and class name
            llamaCall.send(filePath, "Application12.java"); // Pass the file path and class name
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
