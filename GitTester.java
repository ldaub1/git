import java.io.IOException;

public class GitTester {

    public static void main(String args[]) throws IOException {

        /* Your tester code goes here */
        GitWrapper gw = new GitWrapper();
        gw.init();

        gw.add("testFolder/another/here.txt");
        // gw.add("testFolder/one_more.txt");
        // gw.add("testFolder/another.txt");
        // gw.add("testFolder/shouldbegone.txt");
        
        gw.commit("miles", "1 commit");
        // gw.checkout("3f545caefde58eecf403d3b31c8c456b9480b5f6");
    }
}