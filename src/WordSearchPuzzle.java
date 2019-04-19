/** WordSearchPuzzle class for CS4222 Spring Programming Assignment
 @Author: Edward Lynch-Milner
 ID: 18222021
**/
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;

public class WordSearchPuzzle {
    private char[][] puzzle;
    private List<String> puzzleWords;
    private final Random random = new Random();
    private String directions = "";
    private int dimensions;

    /**
     * Takes a list already created by the user and uses those words to populate the puzzle
     * @param userSpecifiedWords The list of words to eb inserted into the puzzleWords list
     */
    public WordSearchPuzzle(List<String> userSpecifiedWords) {
        this.puzzleWords = userSpecifiedWords;
        checkPuzzleWords(); //Ensures the words will be uppercase even if the words in the List given aren't and if any of the words contains numbers they'll be excluded
        generateWordSearchPuzzle();
    }

    /**
     * Takes a file parameter and properties for the words to be chosen from the file
     * @param wordFile The name of the file in the format fileName.txt
     * @param wordCount The amount of words to be randomly chosen from the file
     * @param shortest  The length of the shortest word to be included
     * @param longest   The length of the longest word to be included
     */
    public WordSearchPuzzle(String wordFile, int wordCount, int shortest, int longest) {
        this.puzzleWords = new ArrayList<String>(wordCount);
        readFromFile(wordFile, wordCount, shortest, longest);
        generateWordSearchPuzzle();
    }

    /**
     * @return the list of words used in the puzzle in a List
     */
    public List<String> getWordSearchList() {
        return this.puzzleWords;
    }

    /**
     * @return The puzzle as a two-dimensional char array
     */
    public char[][] getPuzzleAsGrid() {
        return this.puzzle;
    }

    /**
     * @return The puzzle is returned as a formatted String with just the wordsearch puzzle
     */
    public String getPuzzleAsString() {
        String puzzleString = null;
        if (this.puzzle != null) { //If the puzzle hasn't been generated yet because no possible words were available for insertion, we will return null
            puzzleString = "";
            for (char[] i : puzzle) {
                for (char ch : i) {
                    puzzleString += "\t" + ch;
                }
                puzzleString += "\n";
            }
        }
        return puzzleString;
    }

    /**
     * Prints the wordsearch puzzle out on the console showing the words used and optionally their directions
     * @param hide Hides/Unhides the directions to find the words in
     */
    public void showWordSearchPuzzle(boolean hide) {
        if (this.puzzleWords.size() != 0) {
            System.out.printf("WordSearch Puzzle\n------------------\nUnused positions filled with random characters\n%s\n", getPuzzleAsString());
            if (!hide) {
                System.out.printf("Words used with directions shown\n%s\n", this.directions);
            } else {
                System.out.println("Words used");
                for (String word : this.puzzleWords) {
                    System.out.println(word);
                }
            }
        } else { //The following line will be printed if a)No words were able to be inserted at all or b)No words were picked and inserted into the puzzleWords list
            System.out.println("No wordsearch puzzle was generated. Try the following: \n" +
                    " 1)Try increasing the word count to be used \n" +
                    " 2)Reduce/Increase the longest word length \n" +
                    " 3)Reduce/Increase the shortest word length \n" +
                    " 4)Ensure that the words chosen do not contain numbers");
        }
    }

    /**
     * The main private ethod which is responsible for populating the grid with help of the numerous helper methods
     */
    private void generateWordSearchPuzzle() {
       if (this.puzzleWords.size() != 0) {
           this.dimensions = getDimensions();
           puzzle = new char[this.dimensions][this.dimensions];
           for (int i = 0; i < this.puzzleWords.size(); i++) {
               String word = this.puzzleWords.get(i);
               if (!insert(word)) {
                   i--; //If word wasn't inserted, decrement the i counter as a word was removed from the puzzleWords list
               }
           }
           fillUnused();
        }
    }

    /**
     *Converts row and column coordinates, vertical, reversed, diagonal booleans and word to a single direction string
     * @param row row coordinates
     * @param col col coordinates
     * @param word word coordinates
     * @param vertical vertical or horizontal
     * @param reversed reversed or normal orientation
     * @param diagonal diagonal or normal insertion
     * @return A string containing the coordinates, word and direction to find the word
     */
    private String dirsToString(int row, int col, String word, boolean vertical, boolean reversed, boolean diagonal) {
        String dir;
        if ((vertical && reversed) && !diagonal) {
            row += word.length() - 1;
            dir = "U";
        } else if (vertical && !diagonal) {
            dir = "D";
        } else if (reversed && !diagonal) {
            col += word.length() - 1;
            dir = "L";
        } else if (!reversed && !diagonal) {
            dir = "R";
        } else {
            if (reversed && (col >= (this.dimensions / 2))) {
                col -= word.length() - 1;
                row += word.length() - 1;
                dir = "RightUpDiag"; //If the word is inserted left but reversed i.e the last letter of the word starts somewhere after this.dimensions/2
            } else if (reversed) {
                col += (word.length() - 1);
                row += (word.length() - 1);
                dir = "LeftUpDiag"; //If the word is inserted right but reversed
            } else if (col >= (this.dimensions / 2)) {
                dir = "LeftDownDiag";
            } else {
                dir = "RightDownDiag";
            }
        }

        return String.format("%s[%d][%d]%s", word, row, col, dir);
    }

    /**
     * Main insert method which randomly inserts the word given in the parameter into the puzzle. Performs checks if the word can be inserted into the grid space,
     * unlike its overloaded counterparts
     * @param word The word to be inserted
     * @return True if word was inserted or false if it wasn't
     */
    private boolean insert(String word) {
        int attempts = 0;
        boolean vertical = random.nextBoolean();
        boolean diagonal = random.nextBoolean();
        while (attempts < 1000 && word.length() < this.dimensions) { //If after 100 attempts, the likelihood of the word being able to be inserted is very low, so quit after that
            int row = random.nextInt(this.dimensions);
            int col = random.nextInt(this.dimensions);
            if (ableToInsert(row, col, vertical, word, diagonal)) {
                boolean reversed = random.nextBoolean();
                insert(word, row, col, vertical, reversed, diagonal);  //Uses the overloaded insert method to insert the word at the randomly(now known) chosen row and col coordinates
                return true;
            } else {
                attempts++;
                diagonal = random.nextBoolean();
            }
        }
        this.puzzleWords.remove(word); //If we could not insert the word after 100 attempts, return no coordinates and remove the word from the puzzleWords list
        return false;
    }



    /**
    * Overloaded insert method to insert the word if the parameters other then word are known in advance
     * NOTE: Does not check if the word can be inserted in the grid or now, so to ensure you avoid out of bounds exceptions, use ableToInsert method first
     * Main insert method ie insert(String word) has mechanisms to perform these checks
    * @param word The word to be inserted
    * @param row the row the word starts at
    * @param col the column the word starts at
    * @param vertical if the word is to be inserted vertically or not
    * @param reversed if the word is to be reversed
    * @param diagonal if the word is to be inserted diagonally
    */
    private void insert(String word, int row, int col, boolean vertical, boolean reversed, boolean diagonal) {
        int index = 0, startingRow = row, startingCol = col; //The row and col values are saved so they don't get incremented so can be used in the dirsToString method for the directions string
        char[] wordArray = word.toCharArray();

        if (reversed) {
            wordArray = reverse(word).toCharArray();
        }
        if (!diagonal) {
            while (index < wordArray.length) {
                if (vertical) {
                    puzzle[row++][col] = wordArray[index++];
                } else {
                    puzzle[startingRow][col++] = wordArray[index++];
                }
            }
        } else {
            insertDiagonally(wordArray, startingRow, startingCol);
        }

        this.directions = this.directions + dirsToString(row, col, word, vertical, reversed, diagonal) + "\n";
    }

    /**
     * Inserts the word diagonally. NOTE: Does not perform checks if the word can fit, so use canInsDiag method first to check
     * @param wordArray word to be inserted as char array
     * @param row row coordinates
     * @param col column coordinates
     */
    private void insertDiagonally(char[] wordArray, int row, int col) {
        int index = 0;
        int startingCol = col;
        while (index < wordArray.length) {
            if (startingCol < this.dimensions / 2) { //If col is less than dimensions /2 it can be inserted right diagonally, else it has to be inserted left diagonally
                puzzle[row++][col++] = wordArray[index++];
            } else {
                puzzle[row++][col--] = wordArray[index++];
            }
        }
    }

    /**
     * Fills unused(i.e. empty cells) in the grid with random characters from A-Z
     */
    private void fillUnused() {
        for (int row = 0; row < puzzle.length; row++) {
            for (int col = 0; col < puzzle[0].length; col++) {
                if (puzzle[row][col] == '\u0000') {
                    puzzle[row][col] = (char)(Math.random() * 26 + 'A');
                }
            }
        }
    }

    /**
     * Method providing the checks if the word can be inserted
     * @param row Row coordinate of insertion point
     * @param col Column coordinate of insertion point
     * @param vertical If word is horizontal or vertical
     * @param word The word to be inserted
     * @param diagonal Whether the word is to be inserted diagonally or not
     * @return whether the word can be inserted
     */
    private boolean ableToInsert(int row, int col, boolean vertical, String word, boolean diagonal) {
        if (!diagonal) {
            int rowOrCol = vertical ? row:col;
            if ((rowOrCol + word.length()) >= this.dimensions) {
                return false;
            }
            for (int i = rowOrCol; i < rowOrCol + word.length(); i++) {
                if ((vertical && puzzle[i][col] != '\u0000') || (!vertical && puzzle[row][i] != '\u0000')) {
                    return false;
                }
            }
            return true;
        } else {
            return canInsDiag(row, col, word);
        }
    }

    /**
     * Checks if a word can be inserted diagonally without overwriting any existing characters
     * @param row The row coordinate of the insertion point
     * @param col  The column coordinate of the insertion point
     * @param word The word to be inserted
     * @return whether the word can be inserted diagonally or not
     */
    private boolean canInsDiag(int row, int col, String word) {
        if (((col + word.length()) < this.dimensions) || (col - word.length()) >= 0)
            if (((row + word.length()) < this.dimensions) && (row + word.length() < this.dimensions)) {
                int startingCol = col;
                for (int i = row; i < row + word.length(); i++) {
                    if (startingCol < this.dimensions / 2 && puzzle[i][col++] != '\u0000') {
                        return false;
                    } else if (startingCol >= this.dimensions / 2 && puzzle[i][col--] != '\u0000') {
                        return false;
                    }
                }
                return true;
            }
        return false;
    }

    /**
     * Reverses a string
     * @param word the word to be reversed
     * @return the reversed word
     */
    private String reverse(String word) {
        String reversed = "";
        for (int i = word.length() - 1; i >= 0; i--) {
            char ch = word.charAt(i);
            reversed += ch;
        }
        return reversed;
    }

    /**
     * Reads words from a text file
     * @param wordFile the name of the file in the format "fileName.txt"
     * @param wordCount the amount of words to be randomly chosen
     * @param shortest  the length shortest word to be included
     * @param longest the length of the longest word to be included
     */
    private void readFromFile(String wordFile, int wordCount, int shortest, int longest) {
        int wordsInFile = 0;
        List<String> chosenWords = new ArrayList<String>();
        try {
            FileReader aFileReader = new FileReader(wordFile);
            BufferedReader aBufferReader = new BufferedReader(aFileReader);
            String lineFromFile = aBufferReader.readLine();
            while (lineFromFile != null && shortest > 0) {
                wordsInFile++;
                if (lineFromFile.matches("([A-Za-z]{" + shortest + "," + longest + "})")) { //The regex ensures the word does not contain any numbers and allows uppercase or lowercase letters
                    chosenWords.add(lineFromFile.toUpperCase());
                }
                lineFromFile = aBufferReader.readLine();
            }
            aBufferReader.close();
            aFileReader.close();
        } catch (IOException ignored) {
        }
        wordCount = wordsInFile < wordCount ? wordsInFile:wordCount; //If there is less words in the file than the wordCount specified, wordCount is replaced witht the amount of words in the file
        storeIntoPuzzleWords(chosenWords, wordCount); //The method responsible for randomly choosing words that match the regex criteria
    }

    /**
     * Stores words chosen to be of the correct length and all characters from the readFromFile method
     * @param chosenWords the list of words that match the length
     * @param wordCount the amount of words to RANDOMLY choose from the chosenWords
     */
    private void storeIntoPuzzleWords(List<String> chosenWords, int wordCount) {
        int i = 0;
        while (this.puzzleWords.size() < wordCount && i < chosenWords.size()) { //i <= chosenWords.size() prevents infinite loop if there are less words in the file than wordCount
            int randPos = random.nextInt(chosenWords.size());
            if (!this.puzzleWords.contains(chosenWords.get(randPos))) {
                this.puzzleWords.add(chosenWords.get(randPos));
            }
            i++;
        }
    }

    /**
     * Used if the userSpecifiedWords constructor is used to capitalise every word if not capitalised already
     */
    private void checkPuzzleWords() {
        for (int i = 0; i < this.puzzleWords.size(); i++) {
            if (this.puzzleWords.get(i).matches("([A-Za-z]+)")) {
                this.puzzleWords.set(i, this.puzzleWords.get(i).toUpperCase());
            } else {
                this.puzzleWords.remove(this.puzzleWords.get(i--)); //If the word does not match the criteria, we do not want to use the word in the Word Search
            }
        }
    }

    /**
     * @return the dimensions for the WordSearchPuzzle i.e. it is a square grid so rows and cols will be the same
     */
    private int getDimensions() {
        int sum = 0;
        double scalingFactor = 1.75;
        for (String str : this.puzzleWords) {
            sum += str.length();
        }
        return ((int)(Math.sqrt((sum + 1) * scalingFactor)) + 1); //Incrementing it by 1 will always round it up
    }
}
