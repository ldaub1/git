import java.io.IOException;

public class GitTester {

    public static void main(String args[]) throws IOException {

        /* Your tester code goes here */
        GitWrapper gw = new GitWrapper();
        gw.init();

        gw.add("testFolder/another/here.txt");
        gw.add("testFolder/one_more.txt");
        gw.add("testFolder/another.txt");
        
        gw.commit("miles", "a commit");
        gw.checkout("1");
    }
}