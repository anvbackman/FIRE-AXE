import java.io.IOException;

public interface LLMCall {
    void send(String filePath, String className, String taskType) throws IOException;
}
