import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;

public class gitRepository {

    private static File gitDIR = new File("git");
    private static File OBJECTS = new File("git/objects");
    private static File INDEX = new File("git/index");
    private static File HEAD = new File("git/HEAD");

    public static String attemptCreatingGitRepository() {
        if (HEAD.exists() && INDEX.exists() && OBJECTS.exists() && gitDIR.exists())
            return "Git Repository Already Exists";

        if (!gitDIR.exists())
            gitDIR.mkdir();

        if (!OBJECTS.exists())
            OBJECTS.mkdir();

        if (!INDEX.exists()) {
            try {
                INDEX.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        if (!HEAD.exists()) {
            try {
                HEAD.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        return "Git Repository Created";
    }
    
    public static void commit(String fileName) {


        String data = getFileContents(fileName);

        // String sha1Hash = generateSHA1Hash(data);

        // // 1. Using BufferedWriter
        // try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("git/index"))) {
        //     bufferedWriter.write(sha1Hash);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
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