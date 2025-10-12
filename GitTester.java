import java.io.IOException;

public class GitTester {

    public static void main(String args[]) throws IOException {

        /* Your tester code goes here */
        GitWrapper gw = new GitWrapper();
        gw.init();

        gw.add("testFolder/another/here.txt");

        /*
         * The hash of this commit is 54f8ee50162ef60047399a1965618ae2cf33d723
         */
        gw.commit("miles", "1 commit");


        gw.add("testFolder/one_more.txt");
        gw.add("testFolder/another.txt");
        gw.add("testFolder/shouldbegone.txt");
        
        /*
         * The hash of this commit is 52a299566db85e06a6ed71b5ff64216abf67802d
         */
        gw.commit("miles", "2 commit");
        
        gw.checkout("54f8ee50162ef60047399a1965618ae2cf33d723");
        gw.checkout("52a299566db85e06a6ed71b5ff64216abf67802d");

        // in the end, nothing should change
        // we checked out an older commit and then came back to the current one
    }
}