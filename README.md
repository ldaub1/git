Wrote a program that initializes a Git repository -> generateGitDirectory() in gitRepository file
1. Returns message if it all already exists (doesn't continue to the rest of the method)
2. Attempts to creating whatever is missing
3. Returns Output (Git Repository Created) ._. (go print it yourself, see tester for sample)

Wrote a tester (removes any files, creates them, tests already made case) 
    -> tests multiple times to ensure robustness >:(

Reads in Data from file (getFileContents(String fileName))

Generates Hash
1. reads in data using previous function
2. getInstance() method is called with SHA-1 Algorithm (try catch necessary in case algorithm doesnt exist)
3. digest() method is called to calculate byte array of message digest
4. byte array is converted to signum representation
5. converted to hex value (precedding 0s added to make it 40 digits long) and returned