__GP 2.1__ | Initialize Git Repository 
1. Constructor calls generateGitDirectory() in gitRepository file (also defines global var for compression on/off)
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

Generate Hash -> createSha1Hash(String inputData) *Taken from GeeksforGeeks*
1. takes data (from previous function) as an input
2. getInstance() method is called with SHA-1 Algorithm (try catch necessary in case algorithm doesnt exist)
3. digest() method is called to calculate byte array of message digest
4. byte array is converted to signum representation
5. converted to hex value (precedding 0s added to make it 40 digits long) and returned

__GP-2.3__ | Create Blob File & Store -> BLOB(String fileName)
1. Gets contents from fileName -> getFileContents(String fileName)
2. Generates Hash from Contents -> createSha1Hash(String inputData)
3. Writes to File (doesnt return anything)
4. Tester projectTester.java
    1. starts with fresh repo / does full reset
    2. blobs file and sees if its in the objects folder (prints true / false for success)

*Tester* -> projectTester.java, testBLOB();
1. Resets repo with resetRepo(boolean compression); -> reset repo clears all files and constructs new repo (returns it for future use), true and false both tested to test compression on and off
2. Generates files with generateTestFiles() -> uses pre-determined global constants for file names and uses the same phrase + random number for the contents so that each trial will have different contents to be hashed
3. Prints out trial number, results per file 
4. Resets multiple times to ensure robustness >:|

*Compression* -> public static void compressContents(String fileName) *Taken from stack overflow https://stackoverflow.com/questions/3649485/how-to-compress-a-string-in-java*
1. global var enabled
2. getFileContents(String fileName) will make a temp file to compress then get the contents of the compressed version

__GP-2.4__ | index(String fileName)
1. gets hash and add its to index w/ file name

*Tester* -> projectTester.java, testBLOB();
1. Resets repo with resetRepo(boolean compression); -> reset repo clears all files and constructs new repo (returns it for future use), true and false both tested to test compression on and off
2. Generates files with generateTestFiles() -> uses pre-determined global constants for file names and uses the same phrase + random number for the contents so that each trial will have different contents to be hashed
3. Prints out trial number, results per file in index
4. Resets multiple times to ensure robustness >:|

__GP-3.0__ | addTree(String directoryPath)
1. reads first line of index and trims whitespace
2. If the line exists, generates a SHA-1 hash of the content 
3. Creates a new file in the "git/objects/" directory, using the hash as the filename.
4. Writes the tree entry into this newly created file.
5. Prints the created tree object’s hash, or notifies if index is empty.

__GP-3.1.0__ | addTreeRecursive() 
1. Starts with a flat working list of file and directory entries, representing paths and their hashes.
2. Repeatedly identifies the deepest-level directories in the list, grouping entries by their parent directory.
3. For each such directory group, calls writeTreeObj to format their contents, compute a SHA-1 hash, and write a tree object file under "git/objects/" with that hash as the filename.
4. Replaces all grouped entries in the working list with a single reference to the newly created tree object (using its hash and directory name).
5. Continues this process recursively, ascending directory levels, until the working list is collapsed to one or more entries at the root level.
6. If multiple root entries remain, combines and writes a final root tree object, then prints and returns its hash as the reference to the entire directory tree.
7. The writeTreeObj method takes a list of entries, extracts relevant parts, formats the tree content, hashes it, writes it to the "git/objects/" folder, and returns the hash.