Initializes Git Repository -> generateGitDirectory() in gitRepository file
1. Returns message if it all already exists (doesn't continue to the rest of the method)
2. Attempts to creating whatever is missing
3. Returns Output (Git Repository Created) ._. (go print it yourself, see tester for sample)
4. Tester -> projectTester.java
    1. (removes any files, creates them, tests already made case) 
    2. ests multiple times to ensure robustness >:|

Reads in Data from file -> getFileContents(String fileName)
1. Takes file name as input returns string w/ contentes

Generate Hash -> createShah1Hash(String inputData)
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