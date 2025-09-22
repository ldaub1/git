import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public class projectTester {

    private static File gitDIR = new File("git");
    private static File OBJECTS = new File("git/objects");
    private static File INDEX = new File("git/index");
    private static File HEAD = new File("git/HEAD");

    public static void main(String[] args, boolean compress) {
        testGenerateGitDirectory();
        testBLOB();
    }

    public static void testBLOB() {
        clearRepo();
        gitRepository testRepo = new gitRepository(false);

        File testFile = new File("test.txt");
        try {
            testFile.createNewFile();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("test.txt"))) {
            bufferedWriter.write("wow what a great test");
        } catch (IOException e) {
            e.printStackTrace();
        }

        testRepo.BLOB("test.txt");
        File fileBLOB = new File("git/objects/" + testRepo.createShah1Hash(gitRepository.getFileContents("test.txt")));
        System.out.println("\nBLOB created in objects (uncompressed): " + fileBLOB.exists());

        clearRepo();
        gitRepository testRepoCompressed = new gitRepository(true);
        testRepo.BLOB("test.txt");
        File fileBLOBCompressed = new File("git/objects/" + testRepoCompressed.createShah1Hash(gitRepository.getFileContents("test.txt")));
        System.out.println("\nBLOB created in objects (compressed): " + fileBLOBCompressed.exists());
    }

    public static void testGenerateGitDirectory() {

        for (int i = 0; i < 100; i++) {
            clearRepo();

            gitRepository testRepo = new gitRepository(false);

            System.out.println("git Directory Created: " + gitDIR.exists());
            System.out.println("objects Directory Created Inside git Directory: " + OBJECTS.exists());
            System.out.println("index File Created Inside git Directory: " + INDEX.exists());
            System.out.println("head File Created Inside git Directory: " + HEAD.exists());

            System.out.println("\nError Sucessfully Thrown If Exists?");
            gitRepository testRepo2 = new gitRepository(false);
        }
    }

    public static void clearRepo() {
        if (INDEX.exists())
            INDEX.delete();
        if (OBJECTS.exists())
            OBJECTS.delete();
        if (gitDIR.exists())
            gitDIR.delete();
        if (HEAD.exists())
            HEAD.delete();
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
