import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) throws IOException {

        GeminiCall geminiCall = new GeminiCall();
        try {
            String result = geminiCall.send("What is 4+4?");
            // prints just: 4 + 4 = 8
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
