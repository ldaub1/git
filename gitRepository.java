import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public static String createShah1Hash(String inputData) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(inputData.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);

            while (hashtext.length() < 40)
                hashtext = "0" + hashtext;

            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException();
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

    public static void BLOB(String fileName) {
        String fileContents = getFileContents(fileName);
        String hashName = createShah1Hash(fileContents);
        File newBLOB = new File(hashName);
        try {
            newBLOB.createNewFile();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(hashName))) {
            bufferedWriter.write(fileContents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}