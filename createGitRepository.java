import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;

public class createGitRepository {

    private static File dir = new File("git");
    private static File objects = new File("git/objects");
    private static File index = new File("git/index");

    public static void generateGitDirectory() {
        if (!dir.exists() && !objects.exists() && !index.exists()) {
            dir.mkdir();
            objects.mkdir();
            try {
                index.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Git Repository Already Exists");
        }

    }
    
    public static void commit(String fileName) {


        String data = getFileContents(fileName);

        String sha1Hash = generateSHA1Hash(data);

        // 1. Using BufferedWriter
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("git/index"))) {
            bufferedWriter.write(sha1Hash);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFileContents(String fileName) {
        StringBuilder data = new StringBuilder("");
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                data.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data.toString();
	}
    
}