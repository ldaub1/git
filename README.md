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

__GP-2.3__ | Create Blob File & Store -> BLOB(String fileName)
1. Gets contents from fileName -> getFileContents(String fileName)
2. Generates Hash from Contents -> createShah1Hash(String inputData)
3. Writes to File (doesnt return anything)
4. Tester projectTester.java
    1. starts with fresh repo / does full reset
    2. blobs file and sees if its in the objects folder (prints true / false for success)

*Tester* -> projectTester.java, testBLOB();
1. Resets repo with resetRepo(boolean compression); -> reset repo clears all files and constructs new repo (returns it for future use), true and false both tested to test compression on and off
2. Generates files with generateTestFiles() -> uses pre-determined global constants for file names and uses the same phrase + random number for the contents so that each trial will have different contents to be hashed
3. Prints out trial number, results per file 
4. Resets multiple times to ensure robustness >:|

*Compression* -> public static void compressContents(String fileName) *Taken from baeldung*
1. global var enabled
2. getFileContents(String fileName) will make a temp file to compress then get the contents of the compressed version

__GP-2.4__ | index(String fileName)
1. gets hash and add its to index w/ file name

*Tester* -> projectTester.java, testBLOB();
1. Resets repo with resetRepo(boolean compression); -> reset repo clears all files and constructs new repo (returns it for future use), true and false both tested to test compression on and off
2. Generates files with generateTestFiles() -> uses pre-determined global constants for file names and uses the same phrase + random number for the contents so that each trial will have different contents to be hashed
3. Prints out trial number, results per file in index
4. Resets multiple times to ensure robustness >:|