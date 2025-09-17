import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public class createGitRepository {

    public void generateGitDirectory() {
        File dir = new File("git");
        if (!dir.exists())
            dir.mkdir();
    }
    
}