__GP 2.1__ | Initialize Git Repository 
1. Contructor calls generateGitDirectory() in gitRepository file (also defines global var for compression on/off)
2. Returns message if it all already exists (doesn't continue to the rest of the method)
3. Attempts to creating whatever is missing
4. Returns Output (Git Repository Created) (main branch prints, if called outside of that for some reason???? needs to be printed)

*Tester* -> projectTester.java, testGenerateGitDirectory();
1. Resets repo with resetRepo(false); -> reset repo clears all files and constructs new repo (returns it for future use), false bc not testing compression rn
2. Prints out trial number, results per file in repo initialization and if it recognizes that there is a repo that already exists
3. Resets multiple times to ensure robustness >:|

__GP-2.2__ | Hash Function
Reads in Data from file -> getFileContents(String fileName)
1. Takes file name as input returns string w/ contents of file provided

Generate Hash -> createShah1Hash(String inputData) *Taken from GeeksforGeeks*
1. takes data (from previous function) as an input
2. getInstance() method is called with SHA-1 Algorithm (try catch necessary in case algorithm doesnt exist)
3. digest() method is called to calculate byte array of message digest
4. byte array is converted to signum representation
5. converted to hex value (precedding 0s added to make it 40 digits long) and returned

Create Blob File & Store in (BLOB(String fileName))
1. Gets contents from fileName
2. Generates Hash from Contents
3. Writes to File (doesnt return anything)
4. Tester projectTester.java
    1. starts with fresh repo / does full reset
    2. blobs file and sees if its in the objects folder (prints true / false for success)