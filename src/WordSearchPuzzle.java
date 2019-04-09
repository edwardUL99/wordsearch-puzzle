/** WordSearchPuzzle class for CS4222 Spring Programming Assignment
 @Author: Edward Lynch-Milner
 ID: 18222021h
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
        capitalise();
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
        String puzzleString = "";
        for (char[] i : puzzle) {
            for (char ch : i) {
                puzzleString += "\t" + ch;
            }
            puzzleString += "\n";
        }
        return puzzleString;
    }

    /**
     * Prints the wordsearch puzzle out on the console showing the words used and optionally their directions
     * @param hide Hides/Unhides the directions to find the words in
     */
    public void showWordSearchPuzzle(boolean hide) {
        System.out.println("WordSearch Puzzle");
        System.out.println("------------------");
        System.out.println("Unused positions filled with random characters");
        System.out.println(getPuzzleAsString() + "\n");
        if (!hide) {
            System.out.println("Words used with directions shown");
            System.out.println(this.directions);
        } else {
            System.out.println("Words used");
            for (int i = 0; i < puzzleWords.size(); i++) {
                System.out.println(puzzleWords.get(i));
            }
        }

    }

    /**
     * The main private ethod which is responsible for populating the grid with help of the numerous helper methods
     */
    private void generateWordSearchPuzzle() {
        if (puzzleWords.size() != 0) {
            this.dimensions = getDimensions();
            puzzle = new char[this.dimensions][this.dimensions];
            boolean reversed, vertical, inserted = false, diagonal;
            int[] coords;
            for (int i = 0; i < puzzleWords.size(); i++) {
                String word = puzzleWords.get(i);
                while (!inserted) {
                    vertical = random.nextBoolean();
                    diagonal = random.nextBoolean();
                    coords = generateCoordinates(word, vertical, diagonal); //coords is returned as an array i.e coords[0] = row, coords[1] = col
                    if (coords != null) {
                        reversed = random.nextBoolean();
                        insert(word, coords[0], coords[1], vertical, reversed, diagonal);
                        inserted = true; //If word was inserted successfully we want to exit the loop
                    }
                }
                inserted = false;
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
            row += (word.length() - 1);
            dir = "U";
        } else if (vertical && !diagonal) {
            dir = "D";
        } else if (reversed && !diagonal) {
            col = col + (word.length() - 1);
            dir = "L";
        } else if (!reversed && !diagonal) {
            dir = "R";
        } else {
            if (reversed && (col > (this.dimensions / 2))) {
                col -= (word.length() - 1);
                row += (word.length() - 1);
                dir = "LeftRevDiag";
            } else if (reversed) {
                col += (word.length() - 1);
                row += (word.length() - 1);
                dir = "RightRevDiag";
            } else if (col > (this.dimensions / 2)) {
                dir = "LeftDownDiag";
            } else {
                dir = "RightDownDiag";
            }
        }

        return String.format("%s[%d][%d]%s", word, row, col, dir);
    }

    /**
     * Inserts the word into the grid
     * @param word The word to be inserted
     * @param startingRow the row the word starts at
     * @param startingCol the column the word starts at
     * @param vertical if the word is to be inserted vertically or not
     * @param reversed if the word is to be reversed
     * @param diagonal if the word is to be inserted diagonally
     */
    private void insert(String word, int startingRow, int startingCol, boolean vertical, boolean reversed, boolean diagonal) {
        int index = 0, row = startingRow, col = startingCol;
        char[] wordArray = word.toCharArray();

        if (reversed) {
            wordArray = reverse(new String(wordArray)).toCharArray();
        }
        if (!diagonal) {
            while (index < wordArray.length) {
                if (vertical) {
                    puzzle[startingRow++][startingCol] = wordArray[index++];
                } else {
                    puzzle[startingRow][startingCol++] = wordArray[index++];
                }
            }
        } else {
            insertDiagonally(wordArray, startingRow, startingCol);
        }

        this.directions = this.directions + dirsToString(row, col, word, vertical, reversed, diagonal) + "\n";
    }

    /**
     * Inserts the word diagonally
     * @param wordArray word as a char array
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
        if (word.length() > this.dimensions) {
            return false;
        }
        if (!diagonal) {
            int rowOrCol = vertical ? row:col;
            if (word.length() > (puzzle.length - rowOrCol)) {
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
        if (row + word.length() < this.dimensions && row < this.dimensions/2) {
            int startingCol = col;
            int count = 0;
            for (int i = row; i < row + word.length() && (col < this.dimensions - 1 && col > 0); i++) {
                if (startingCol < this.dimensions / 2) {
                    if (puzzle[i][col++] != '\u0000') {
                        return false;
                    }
                } else {
                    if (puzzle[i][col--] != '\u0000') {
                        return false;
                    }
                }
                count++; //The count of unused positions available
            }
            return count == word.length();
        }
        return false;
    }

    /**
     * Randomly generates coordinates on the grid and checks if the word is able to fit, if so it returns an array containing the row and col values
     * @param word The word to be inserted
     * @param vertical Whether the word if vertical (true) or horizontal (false)
     * @param diagonal  Whether the word is to be inserted diagonally or not
     * @return int[] array of row and col coordinates i.e coords[0] = row, coords[1] = col
     */
    private int[] generateCoordinates(String word, boolean vertical, boolean diagonal) {
        int[] coords = new int[2];
        int attempts = 0;
        boolean canInsert = false;
        while (attempts < 100 && (!canInsert && word.length() <= this.dimensions)) {
            int row = random.nextInt(this.dimensions);
            int col = random.nextInt(this.dimensions);
            if (ableToInsert(row, col, vertical, word, diagonal)) {
                coords[0] = row;
                coords[1] = col;
                canInsert = true;
            } else {
                attempts++;
            }
        }
        if (attempts == 100) {
            coords = null;
            puzzleWords.remove(word); //If we could not insert the word after 100 attempts, return no coordinates and remove the word from the puzzleWords list
        }

        return coords;
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
        List<String> chosenWords = new ArrayList<String>();
        try {
            FileReader aFileReader = new FileReader(wordFile);
            BufferedReader aBufferReader = new BufferedReader(aFileReader);
            String lineFromFile = aBufferReader.readLine();
            while (lineFromFile != null) {
                if (lineFromFile.matches("([A-Za-z]{" + shortest + "," + longest + "})")) {
                    chosenWords.add(lineFromFile.toUpperCase());
                }
                lineFromFile = aBufferReader.readLine();
            }
            aBufferReader.close();
            aFileReader.close();
        } catch (IOException ignored) {
        }
        storeIntoPuzzleArray(chosenWords, wordCount);
    }

    /**
     * Stores words chosen to be of the correct length and all characters from the readFromFile method
     * @param chosenWords the list of words that match the length
     * @param wordCount the amount of words to RANDOMLY choose from the chosenWorss
     */
    private void storeIntoPuzzleArray(List<String> chosenWords, int wordCount) {
        int i = 0;
        while (i < chosenWords.size()) {
            int randPos = (int)(Math.random() * chosenWords.size());
            if (!puzzleWords.contains(chosenWords.get(randPos))) {
                if (puzzleWords.size() < wordCount) {
                    puzzleWords.add(chosenWords.get(randPos));
                }
                i++;
            }
        }
    }

    /**
     * Used if the userSpecifiedWords constructor is used to capitalise every word if not capitalised already
     */
    private void capitalise() {
        for (int i = 0; i < puzzleWords.size(); i++) {
            this.puzzleWords.set(i, puzzleWords.get(i).toUpperCase());
        }
    }

    /**
     *
     * @return the dimensions for the WordSearchPuzzle i.e. it is a square grid so rows and cols will be the same
     */
    private int getDimensions() {
        int sum = 0;
        double scalingFactor = 1.75;
        for (String i : puzzleWords) {
            sum += i.length();
        }
        return ((int)(Math.sqrt((sum + 1) * scalingFactor)) + 1); //Incrementing it by 1 will always round it up
    }
}
