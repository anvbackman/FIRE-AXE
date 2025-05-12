import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {

        GeminiCall geminiCall = new GeminiCall();
        LlamaCall llamaCall = new LlamaCall();
        String filePath = "Code.txt";
        try {
            geminiCall.send(filePath, "Application12.java"); // Pass the file path and class name
            llamaCall.send(filePath, "Application12.java"); // Pass the file path and class name
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
