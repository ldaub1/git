import java.io.*;
import java.util.ArrayList;

public class projectTester {

    private static File gitDIR = new File("git");
    private static File OBJECTS = new File("git/objects");
    private static File INDEX = new File("git/index");
    private static File HEAD = new File("git/HEAD");
    private static String[] TEST_FILE_NAMES = {"test1.txt", "wow_another_test.txt", "testFolder/one_more.txt"};

    public static void main(String[] args) throws IOException {
        // testGenerateGitDirectory();
        // testBLOB();
        // testBLOBAndIndex();
        testTreeGeneration();
    }

    public static void generateTestFiles() {
        for (String fileName : TEST_FILE_NAMES) {
            File testFolder = new File("testFolder");
            testFolder.mkdir();
            File testFile = new File(fileName);
            try {
                testFile.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
                bufferedWriter.write("wow what a great test\n#" + (int) (Math.random() * 10000000) + "._.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void testBLOBAndIndex() throws IOException {
        System.out.println("\nTESTING INDEXING OF BLOBS GENERATED FROM FILES");
        generateTestFiles();

        for (int i = 0; i < 100; i++) {
            System.out.println("*Trial " + i + "*");
            gitRepository testRepo = resetRepo(false);
            for (String fileName : TEST_FILE_NAMES) {
                testRepo.index(fileName);
                testRepo.BLOB(fileName);
                System.out.println("BLOB File Name matches Index: "+ (testRepo.createSha1Hash(testRepo.getFileContents(fileName)) + " " + fileName).equals(testRepo.seeLastIndexEntry()));
            }

            gitRepository testRepoCompressed = resetRepo(true);
            for (String fileName : TEST_FILE_NAMES) {
                testRepoCompressed.index(fileName);
                testRepoCompressed.BLOB(fileName);
                System.out.println("BLOB File Name matches Index: "+ (testRepoCompressed.createSha1Hash(testRepoCompressed.getFileContents(fileName)) + " " + fileName).equals(testRepoCompressed.seeLastIndexEntry()));
            }
        }
    }

    public static void testBLOB() throws IOException {

        System.out.println("\nTESTING BLOB GENERATION FROM FILES");
        gitRepository testRepo = resetRepo(false);
        generateTestFiles();

        for (int i = 0; i < 100; i++) {
            System.out.println("*Trial " + i + "*");
            for (String fileName : TEST_FILE_NAMES) {
                testRepo.BLOB(fileName);
                File fileBLOB = new File("git/objects/" + testRepo.createSha1Hash(testRepo.getFileContents(fileName)));
                System.out.println("(UNCOMPRESSED) BLOB (" + fileName + ") created in objects: " + fileBLOB.exists());
            }
        }

        gitRepository testRepoCompressed = resetRepo(true);

        for (int i = 0; i < 1; i++) {
            System.out.println("*Trial " + i + "*");
            for (String fileName : TEST_FILE_NAMES) {
                testRepoCompressed.BLOB(fileName);
                File fileBLOB = new File("git/objects/" + testRepoCompressed.createSha1Hash(testRepoCompressed.getFileContents(fileName)));
                System.out.println("(COMPRESSED) BLOB (" + fileName + ") created in objects: " + fileBLOB.exists());
            }
        }
    }

    public static void testGenerateGitDirectory() throws IOException {

        System.out.println("\nTESTING GIT DIRECTORY (AND OTHER ACCOMPANYING FILES) GENERATION");

        for (int i = 0; i < 100; i++) {
            System.out.println("*Trial " + i + "*");
            gitRepository testRepo = resetRepo(false);

            System.out.println("Tester Confirming Git Repository Created: " + gitDIR.exists());
            System.out.println("Tester Confirming Objects Directory Created Inside git Directory: " + OBJECTS.exists());
            System.out.println("Tester Confirming Index File Created Inside git Directory: " + INDEX.exists());
            System.out.println("Tester Confirming Head File Created Inside git Directory: " + HEAD.exists());

            System.out.println("\nError Sucessfully Thrown If Exists?");
            gitRepository testRepo2 = new gitRepository(false);
        }
    }

    public static gitRepository resetRepo(boolean compression) throws IOException {
        if (gitDIR.exists())
            deleteDirectoryRecursive(gitDIR);
        gitRepository newRepo = new gitRepository(compression);
        return newRepo;
    }
    
    public static void deleteDirectoryRecursive(File file)
    {
        for (File subfile : file.listFiles()) {
            if (subfile.isDirectory()) {
                deleteDirectoryRecursive(subfile);
            }
            subfile.delete();
        }
    }

    public static void testTreeGeneration() throws IOException {
        System.out.println("testing tree generation");
        resetRepo(false);
        generateTestFiles(); 
        gitRepository repo = new gitRepository(false);
        for (String fileName : TEST_FILE_NAMES) {
            repo.index(fileName);
            repo.BLOB(fileName);
        }
        ArrayList<String> workingList = repo.addTreeRecursive();
        System.out.println("Final working list (should be root tree): " + workingList);
    }   

}
