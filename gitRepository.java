import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class gitRepository {

    private File gitDIR = new File("git");
    private File OBJECTS = new File("git/objects");
    private File INDEX = new File("git/index");
    private File HEAD = new File("git/HEAD");
    private String rootHash;
    private boolean compress;

    public gitRepository(boolean compress) throws IOException {
        System.out.println(attemptCreatingGitRepository());
        this.compress = compress;
        if (!Files.readString(HEAD.toPath()).isEmpty()) {
            this.rootHash = getRootHashFromCommitHash(Files.readString(HEAD.toPath()));
        }
    }

    public void addFile(String filename) throws IOException {
        if (index(filename)) {
            BLOB(filename);
        }
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
        } catch (NoSuchAlgorithmException e) {
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

    public boolean index(String fileName) throws IOException {

        File file = new File(fileName);
        String fileContents = getFileContents(fileName);
        String fileHash = createSha1Hash(fileContents);
        String indexContents = Files.readString(INDEX.toPath());

        // if the file has already been included in the index in that state
        if (indexContents.contains(fileName) && indexContents.contains(fileHash)) {
            return false;
        }

        StringBuilder fileIndex = new StringBuilder();
        if (INDEX.length() > 0)
            fileIndex.append("\n");
        String fileType;
        if (file.isDirectory()) {
            fileType = "tree";
        } else {
            fileType = "blob";
        }
        fileIndex.append(fileType + " " + fileHash + " " + fileName);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(INDEX, true))) {
            bufferedWriter.write(fileIndex.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
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

    public ArrayList<String> addTreeRecursive() {
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

        // to fix the edge case of the nested blob
        if (workingList.size() == 1 && workingList.get(0).contains("blob ")) {
            String entry = workingList.get(0);

            // thank u for the godsend getDepth method whoever wrote it
            while (getDepth(entry) > 1) {

                String dir = getDirName(entry);
                ArrayList<String> temp = new ArrayList<>();
                temp.add(entry);
                String treeHash = writeTreeObj(temp);
                entry = "tree " + treeHash + " " + dir;

            }

            workingList.clear();
            workingList.add(entry);
        }

        if (workingList.size() > 1) {
            // this code will never execute...
            String treeHash = writeTreeObj(workingList);
            workingList.clear();
            workingList.add("tree " + treeHash);
            System.out.println("Root tree entry: tree " + treeHash);

            rootHash = treeHash;

        } else if (!workingList.isEmpty()) {
            // in other words, if WL.size() == 1?
            // bug: if one file is committed, then a blob will be here

            if (workingList.get(0).split(" ")[0].equals("blob")) {
                // i think this is what hannah was trying to do
                String treeHash = writeTreeObj(workingList);
                workingList.clear();
                workingList.add("tree " + treeHash);
                System.out.println("Root tree entry: tree " + treeHash);
            } else {
                String treeHash = writeTreeObj(workingList);
                workingList.clear();
                workingList.add("tree " + treeHash);
                System.out.println("Root tree entry: " + workingList.get(0));
            }

            rootHash = workingList.get(0).split(" ")[1];
            System.out.println(rootHash);
        }
        return workingList;
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
            if (i < entries.size() - 1)
                treeContent.append("\n");
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

    public String commit(String inputAuthor, String message) throws IOException {
        addTreeRecursive();

        String treeField = "tree: " + rootHash + "\n";
        String author = "author: " + inputAuthor + "\n";
        String summary = "summary: " + message;
        String date = "date: " + java.time.LocalDateTime.now().toString() + "\n";

        String parent;
        // Efficient check to see if first commit or not
        if (HEAD.length() == 0) {
            parent = "";
        } else {
            parent = "parent: " + Files.readAllLines(HEAD.toPath()).get(0) + "\n";
        }

        File tempCommitFile = new File("tempCommitFile");
        BufferedWriter bw = new BufferedWriter(new FileWriter(tempCommitFile, true));
        bw.write(treeField);
        bw.write(parent);
        bw.write(author);
        bw.write(date);
        bw.write(summary);
        bw.close();

        BLOB(tempCommitFile.getPath());
        String hash = createSha1Hash(Files.readString(tempCommitFile.toPath()));
        tempCommitFile.delete();

        BufferedWriter bw2 = new BufferedWriter(new FileWriter(HEAD.getPath()));
        bw2.write(hash);
        bw2.close();

        return hash;
    }

    private String getRootHashFromCommitHash(String commitHash) throws IOException {
        ArrayList<String> lines = new ArrayList<String>(Files
                .readAllLines(new File("git" + File.separator + "objects" + File.separator + commitHash).toPath()));

        return lines.get(0).split(": ")[1];
    }

    public void deleteTrackedFilesFromCurrentCommit() throws IOException {
        deleteRootRecursive(rootHash, "");
    }

    /**
     * Going fancy with this message because its an important method.
     * This method recursively deletes everything from the top.
     * 
     * @param treeHash The SHA1 hash of the tree to start deleting from
     * @param path     The path that the method is currently deleting from
     */
    private void deleteRootRecursive(String treeHash, String path) throws IOException {
        File tree = new File("git" + File.separator + "objects" + File.separator + treeHash);
        ArrayList<String> lines = new ArrayList<String>(Files.readAllLines(tree.toPath()));

        for (String line : lines) {
            String[] parsedLine = line.split(" ");

            if (parsedLine[0].equals("blob")) {
                new File(path + parsedLine[2]).delete();
            }

            else {
                // Must delete everything inside the directory first
                // totally okay if not everything is deleted
                deleteRootRecursive(parsedLine[1], path + parsedLine[2] + File.separator);
                new File(path + parsedLine[2]).delete();
            }
        }
    }

    public void regenerateTrackedFilesFromCommit(String commitHash) throws IOException {
        regenRootRecursive(getRootHashFromCommitHash(commitHash), "");
    }

    /**
     * Going fancy with this message because its an important method.
     * It's basically the inverse of deleting
     * This method recursively deletes everything from the top.
     * 
     * @param treeHash The SHA1 hash of the tree to start deleting from
     * @param path     The path that the method is currently deleting from
     */
    private void regenRootRecursive(String treeHash, String path) throws IOException {
        File tree = new File("git" + File.separator + "objects" + File.separator + treeHash);
        ArrayList<String> lines = new ArrayList<String>(Files.readAllLines(tree.toPath()));

        for (String line : lines) {
            String[] parsedLine = line.split(" ");

            if (parsedLine[0].equals("blob")) {
                File output = new File(path + parsedLine[2]);
                String hashedPathToContents = "git" + File.separator + "objects" + File.separator + parsedLine[1];
                output.createNewFile();

                Files.copy(Paths.get(hashedPathToContents), new FileOutputStream(output));
            }

            else {
                // Must delete everything inside the directory first
                // totally okay if not everything is deleted
                new File(path + parsedLine[2]).mkdir();
                regenRootRecursive(parsedLine[1], path + parsedLine[2] + File.separator);
            }
        }
    }

    public boolean doesCommitHashExist(String commitHash) throws IOException {
        String currentCommitHash = Files.readString(Paths.get("git" + File.separator + "HEAD"));

        if (commitHash.equals(currentCommitHash)) {
            return true;
        }

        String parentCommit = getParentCommit(currentCommitHash);

        while (parentCommit != null) {
            if (parentCommit.equals(commitHash)) {
                return true;
            }
            parentCommit = getParentCommit(parentCommit);
        }

        return false;
    }

    // returns null if no parent commit exists
    private String getParentCommit(String commitHash) throws IOException {
        File commit = new File("git" + File.separator + "objects" + File.separator + commitHash);
        ArrayList<String> lines = new ArrayList<String>(Files.readAllLines(commit.toPath()));
        if (lines.get(1).contains("parent")) {
            return lines.get(1).split(": ")[1];
        } else {
            return null;
        }
    }
}