import java.io.IOException;

public class GitTester {

    public static void main(String args[]) throws IOException {

        /* Your tester code goes here */
        GitWrapper gw = new GitWrapper();
        gw.init();

        gw.add("testFolder/another/here.txt");

        String hashOfOldCommit = gw.commit("miles", "1 commit");

        gw.add("testFolder/one_more.txt");
        gw.add("testFolder/another.txt");
        gw.add("testFolder/shouldbegone.txt");
        
        String hashOfNewCommit = gw.commit("miles", "2 commit");
        
        gw.checkout(hashOfOldCommit);
        gw.checkout(hashOfNewCommit);

        // in the end, nothing should change
        // we checked out an older commit and then came back to the current one
    }
}