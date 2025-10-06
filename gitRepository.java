import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPOutputStream;

public class gitRepository {

    private File gitDIR = new File("git");
    private File OBJECTS = new File("git/objects");
    private File INDEX = new File("git/index");
    private File HEAD = new File("git/HEAD");
    private boolean compress;

    public gitRepository(boolean compress) {
        System.out.println(attemptCreatingGitRepository());
        this.compress = compress;
    }

    public String attemptCreatingGitRepository() {
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

    public String createSha1Hash(String inputData) {
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

    public String getFileContents(String fileName) {
        StringBuilder data = new StringBuilder("");
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                data.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (compress) {
            return compressContents(data.toString());
        }
        if (!data.isEmpty())
            return data.substring(0, data.length() - 1);  
        return data.toString();
    }

    public void BLOB(String fileName) {
        String fileContents = getFileContents(fileName);
        String hashName = createSha1Hash(fileContents);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("git/objects/" + hashName))) {
            bufferedWriter.write(fileContents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void index(String fileName) {
        StringBuilder fileIndex = new StringBuilder();
        if (INDEX.length() > 0)
            fileIndex.append("\n");
        //
        String fileType;
        File file = new File(fileName);
        if (file.isDirectory()) {
            fileType = "tree";
        } else {
            fileType = "blob";
        }
        //
        String fileContents = getFileContents(fileName);
        String fileHash = createSha1Hash(fileContents);
        fileIndex.append(fileType + " " + fileHash + " " + fileName);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(INDEX, true))) {
            bufferedWriter.write(fileIndex.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String seeLastIndexEntry() {
        String lastLine = "";
        try (BufferedReader br = new BufferedReader(new FileReader(INDEX))) {
            String line;
            while ((line = br.readLine()) != null) {
                lastLine = line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lastLine;
    }

    public static String compressContents(String contents) { 
        if (contents != null && contents.length() != 0) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(contents.getBytes());
            gzip.close();
            return out.toString("ISO-8859-1");
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
        return contents;
    }

    //
    public void addTree(String directoryPath) {
        File tree = new File(directoryPath);
        // figure out what's a directory based on (/)
        // base case --> files only in folder
    }
    //
}