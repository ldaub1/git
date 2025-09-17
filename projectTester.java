import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public class projectTester {

    public static void main(String[] args) {
        testGenerateGitDirectoryy();
    }

    public static void testGenerateGitDirectoryy() {

        File dir = new File("git");
        File objects = new File("git/objects");
        File index = new File("git/index");

        if (index.exists())
            index.delete();
        if (objects.exists())
            objects.delete();
        if (dir.exists())
            dir.delete();

        createGitRepository.generateGitDirectory();

        System.out.println("git Directory Created: " + dir.exists());
        System.out.println("objects Directory Created Inside git Directory: " + objects.exists());
        System.out.println("index File Created Inside git Directory: " + index.exists());

        System.out.println("\nError Sucessfully Thrown If Exists?");
        createGitRepository.generateGitDirectory();
    }
}
