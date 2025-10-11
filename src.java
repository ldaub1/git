import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class src {
    public static void main(String[] args) throws IOException {

        gitRepository test = new gitRepository(false);
        File index = new File("git/index");
        index.delete();

        test.addFile("src.java");
        test.addFile("testFolder/one_more.txt");
        // test.addFile("testFolder/another/here.txt");
        // test.addFile("testFolder/another.txt");

        test.addTreeRecursive();
        System.out.println(new File("git/HEAD").length());
        test.commit();
        
    }
}
