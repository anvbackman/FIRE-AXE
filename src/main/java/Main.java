import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {


//        ProcessFiles processFiles = new ProcessFiles();

        GeminiCall geminiCall = new GeminiCall();
//        String filePath = "Code2.txt";
        LlamaCall llamaCall = new LlamaCall();
//        String filePath2 = "Code.txt";

        File geminiHardcodeFolder = new File("src/Classes/Gemini/hardcode");
        File geminiInlineMethodFolder = new File("src/Classes/Gemini/inline");
        File llamaHardcodeFolder = new File("src/Classes/llama/hardcode");
        File llamaInlineMethodFolder = new File("src/Classes/llama/inline");

        ProcessFiles.processFolder(geminiHardcodeFolder, geminiCall, "hardcode");
        ProcessFiles.processFolder(geminiInlineMethodFolder, geminiCall, "inline");

        ProcessFiles.processFolder(llamaHardcodeFolder, llamaCall, "hardcode");
        ProcessFiles.processFolder(llamaInlineMethodFolder, llamaCall, "inline");


    }

}
