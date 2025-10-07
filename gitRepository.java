import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

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
        String fileType;
        File file = new File(fileName);
        if (file.isDirectory()) {
            fileType = "tree";
        } else {
            fileType = "blob";
        }
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

    public void addTree(String directoryPath) {
        try (BufferedReader br = new BufferedReader(new FileReader(INDEX))) {
            String line = br.readLine();
            if (line != null) {
                String treeEntry = line.trim();
                String treeHash = createSha1Hash(treeEntry);
                try (BufferedWriter bw = new BufferedWriter(new FileWriter("git/objects/" + treeHash))) {
                    bw.write(treeEntry);
                }
                System.out.println("Tree object created: " + treeHash);
            } else {
                System.out.println("Index is empty.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> buildWorkingList() {
        ArrayList<String> workingList = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader(INDEX))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("blob") && !line.startsWith("tree")) {
                    workingList.add("blob " + line.trim());
                } else {
                    workingList.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workingList;
    }

    public int getDepth(String entry) {
        String[] chunks = entry.split(" ");
        String path = chunks[2];
        return path.split("/").length;
    }

    public int findDeepest(ArrayList<String> workingList) {
        int max = 1;
        for (String entry : workingList) {
            int depth = getDepth(entry);
            if (depth > max) {
                max = depth;
            }
        }
        return max;
    }

    public String getDirName(String entry) {
        String[] chunk = entry.split(" ");
        String path = chunk[2];
        int lastSlash = path.lastIndexOf("/");
        if (lastSlash == -1) {
            return "";
        } else {
            return path.substring(0, lastSlash);
        }
    }

    public String removeLastComponent(String dir) {
        int idx = dir.lastIndexOf("/");
        return idx == -1 ? "" : dir.substring(0, idx);
    }

    public void addTreeRecursive() {
        ArrayList<String> workingList = buildWorkingList();
        while (workingList.size() > 1) {
            int maxDepth = findDeepest(workingList);
            HashMap<String, ArrayList<String>> dirEntriesMap = new HashMap<String, ArrayList<String>>();
            for (String entry : workingList) {
                int depth = getDepth(entry);
                if (depth == maxDepth) {
                    String dir = getDirName(entry);
                    if (!dirEntriesMap.containsKey(dir)) {
                        dirEntriesMap.put(dir, new ArrayList<String>());
                    }
                    dirEntriesMap.get(dir).add(entry);
                }
            }
            ArrayList<String> newWorkingList = new ArrayList<>(workingList);
            for (String dir : dirEntriesMap.keySet()) {
                ArrayList<String> dirEntries = dirEntriesMap.get(dir);
                String treeHash = writeTreeObj(dirEntries);
                newWorkingList.removeAll(dirEntries);
                newWorkingList.add("tree " + treeHash + " " + dir);
            }
            workingList = newWorkingList;
        }
        if (!workingList.isEmpty()) {
            System.out.println("Root tree entry: " + workingList.get(0));
        }
    }

    public String writeTreeObj(ArrayList<String> entries) {
        StringBuilder treeContent = new StringBuilder();
        for (int i = 0; i < entries.size(); i++) {
            String[] parts = entries.get(i).split(" ");
            String type = parts[0];
            String hash = parts[1];
            String path = parts[2];
            String name = path.substring(path.lastIndexOf("/") + 1);
            treeContent.append(type).append(" ").append(hash).append(" ").append(name);
            if (i < entries.size() - 1) treeContent.append("\n");
        }
        String treeString = treeContent.toString();
        String treeHash = createSha1Hash(treeString);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("git/objects/" + treeHash))) {
        bw.write(treeString); 
        } catch (IOException e) {
        e.printStackTrace();
        }
        return treeHash; 


    }
    
}