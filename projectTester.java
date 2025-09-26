import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public class projectTester {

    private static File gitDIR = new File("git");
    private static File OBJECTS = new File("git/objects");
    private static File INDEX = new File("git/index");
    private static File HEAD = new File("git/HEAD");
    private static String[] TEST_FILE_NAMES = {"test1.txt", "wow_another_test.txt", "one_more.txt"};

    public static void main(String[] args) {
        // testGenerateGitDirectory();
        testBLOB();
        // testBLOBAndIndex();
    }

    public static void generateTestFiles() {
        for (String fileName : TEST_FILE_NAMES) {
            File testFile = new File(fileName);
            try {
                testFile.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
                bufferedWriter.write("wow what a great test" + Math.random());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void testBLOBAndIndex() {

        System.out.println("\nTESTING INDEXING OF BLOBS GENERATED FROM FILES");

        gitRepository testRepo = resetRepo(false);
        generateTestFiles();

        for (int i = 0; i < 1; i++) {
            System.out.println("*Trial " + i + "*");
            for (String fileName : TEST_FILE_NAMES) {
                testRepo.index(fileName);
                testRepo.BLOB(fileName);
                System.out.println("BLOB File Name matches Index: "+ (testRepo.createShah1Hash(fileName) + " " + fileName).equals(testRepo.seeLastIndexEntry()));
            }
        }

        gitRepository testRepoCompressed = resetRepo(true);

        // for (int i = 0; i < 1; i++) {
        //     System.out.println("*Trial " + i + "*");
        //     for (String fileName : TEST_FILE_NAMES) {
        //         testRepo.index(fileName);
        //         testRepo.BLOB(fileName);
        //         File fileBLOBCompressed = new File("git/objects/" + testRepoCompressed.createShah1Hash(gitRepository.getFileContents(fileName)));
        //         System.out.println("(COMPRESSED) BLOB created in objects: " + fileBLOBCompressed.exists());
        //     }
        // }
    }

    public static void testBLOB() {

        System.out.println("\nTESTING BLOB GENERATION FROM FILES");
        gitRepository testRepo = resetRepo(false);
        generateTestFiles();

        for (int i = 0; i < 1; i++) {
            System.out.println("*Trial " + i + "*");
            for (String fileName : TEST_FILE_NAMES) {
                testRepo.BLOB(fileName);
                File fileBLOB = new File("git/objects/" + testRepo.createShah1Hash(gitRepository.getFileContents(fileName)));
                System.out.println("(UNCOMPRESSED) BLOB (" + fileName + ") created in objects: " + fileBLOB.exists());
            }
        }

        gitRepository testRepoCompressed = resetRepo(true);

        for (int i = 0; i < 1; i++) {
            System.out.println("*Trial " + i + "*");
            for (String fileName : TEST_FILE_NAMES) {
                testRepoCompressed.BLOB(fileName);
                File fileBLOB = new File("git/objects/" + testRepoCompressed.createShah1Hash(gitRepository.getFileContents(fileName)));
                System.out.println("(COMPRESSED) BLOB (" + fileName + ") created in objects: " + fileBLOB.exists());
            }
        }
    }

    public static void testGenerateGitDirectory() {

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

    public static gitRepository resetRepo(boolean compression) {
        if (gitDIR.exists())
            deleteDirectoryRecursive(OBJECTS);
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
}
