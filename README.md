# Lightcel
A mini excel engine that takes .csv input and outputs .csv with expected excel behavior written in Java.

## How it works:
- Parse input.csv and read in Table data.
- Tokenize each cell which is a formula.
- Parser goes through the token list and creates an AST (Abstract Syntax Tree) or in other words the resulting Expression and returns it.
- The expression gets evaluated by the Evaluator class and the result gets written into output.csv
- Non formula cell values dont go through the whole process and just get rewritten in.
- User gets output.csv

https://github.com/user-attachments/assets/4390eb32-7bde-41cd-b6e2-f4c9751817f0

## Dependencies
 - Java SDK 25+
 - Gradle will be installed if missing via the Gradle Wrapper
 - Git

## Build from source

### 1. Clone the repository
```
git clone git@github.com:simonhareter/Lightcel.git
```
### 2. Navigate to the repository
```
cd Lightcel
```
### 3. Build & Run
## Linux / macOS
```
./gradlew installDist
./app/build/install/lightcel/bin/lightcel parse input.csv
```
## Windows
```
gradlew.bat installDist
app\\build\\install\\lightcel\\bin\\lightcel.bat parse input.csv
```

### (Optional) To use lightcel parse input.csv add ../app/build/install/lightcel/bin to your path
