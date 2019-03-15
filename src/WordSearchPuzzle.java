/* WordSearchPuzzle class for CS4222 Spring Programming Assignment
 Author: Edward Lynch-Milner
 ID: 18222021
*/
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;

public class WordSearchPuzzle {
	private char[][] puzzle;
	private List<String> puzzleWords;

	public WordSearchPuzzle(List<String> userSpecifiedWords) {
		puzzleWords = userSpecifiedWords;
	}

	public WordSearchPuzzle(String wordFile, int wordCount, int shortest, int longest) {
		puzzleWords = new ArrayList<String>(wordCount);
		readFromFile(wordFile, wordCount, shortest, longest);
		puzzle = new char[8][8];
		testFill();
	}

	public List<String> getWordSearchList() {
		return puzzleWords;
	}

	public char[][] getPuzzleAsGrid() {
		return puzzle;
	}

	public String getPuzzleAsString() {
		String puzzleString = "";
		for (char[] i : puzzle) {
			for (char ch : i) {
				puzzleString = puzzleString + "\t" + ch;
			}
			puzzleString = puzzleString + "\n";
		}
		return puzzleString;
	}

	private void testFill() {
		for (int row = 0; row < puzzle.length; row++) {
			for (int col = 0; col < puzzle[0].length; col++) {
				puzzle[row][col] = (char)(Math.random() * (91-65) + 65);
			}
		}
	}

	private void generateWordSearchPuzzle() {
		if (puzzleWords.size() != 0) {
			final int dimensions = getDimensions();
			puzzle = new char[dimensions][dimensions];
			int i = 0;
			while (puzzleWords.size() > 0) {
				char[] wordArray = puzzleWords.get(i).toCharArray();
				while (!puzzleContainsWord(puzzleWords.get(i), false) || !puzzleContainsWord(puzzleWords.get(i), true)) {
					int randRow = (int)(Math.random() * puzzle.length);
					int randCol = (int)(Math.random() * puzzle[0].length);
					boolean[] upOrDown = {true, false};
					boolean vertical = upOrDown[(int)(Math.random() * upOrDown.length)];
					if (isAreaAvailable(randRow, randCol, puzzleWords.get(i), vertical)) {
						insert(wordArray, randRow, randCol, vertical, upOrDown[(int)(Math.random() * upOrDown.length)]);
					}
				}
				puzzleWords.remove(i);
				i++;
			}
		}
	}

	private void insert(char[] wordArray, int startingRow, int startingCol, boolean vertical, boolean reversed) {
		if (reversed) {
			wordArray = reverse(new String(wordArray)).toCharArray();
		}
		if (vertical) {
			for (int row = startingRow; row < wordArray.length; row++) {
				puzzle[row][startingCol] = wordArray[row];
			}
		} else {
			for (int col = startingCol; col < wordArray.length; col++) {
				puzzle[startingRow][col] = wordArray[col];
			}
		}
	}

	private void fillUnused() {

	}

	/**
	 * This method checks if the puzzle contains the word or not
	 * @param word	the word to be checked
	 * @param vertical	whether to check along rows or check along columns
	 * @return	if the puzzle contains the word
	 */
	private boolean puzzleContainsWord(String word, boolean vertical) {
		char[] singleArray;
		String builtWord;
		singleArray = new char[puzzle.length];
		if (vertical) {
			for (int row = 0; row < puzzle.length; row++) {
				for (int col = 0; col < puzzle[0].length; col++) {
					singleArray[col] = puzzle[col][row];
				}
				builtWord = new String(singleArray);
				if (builtWord.contains(word) || builtWord.contains(reverse(word))) {
					return true;
				}
			}
		} else {
			for (char[] ch : puzzle) {
				builtWord = new String(ch);
				if (builtWord.contains(word) || builtWord.contains(reverse(word))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isAreaAvailable(int row, int col, String word, boolean vertical) {
		if ((row >= 0 && row < puzzle.length) && (col >= 0 && col < puzzle[0].length)) {
			if (vertical && areaNotUsed(col, true)) {
				return word.length() <= (puzzle.length - row);
			} else if (areaNotUsed(row, false)) {
				return word.length() <= (puzzle.length - col);
			}
		}
		return false;
	}

	private boolean areaNotUsed(int coordinate, boolean vertical) {
		if (coordinate >= 0 && coordinate < puzzle.length) {
			if (vertical) {
				for (int i = 0; i < puzzle.length; i++) {
					if (puzzle[i][coordinate] != '\u0000') {
						return false;
					}
				}
			} else {
				for (int i = 0; i < puzzle[0].length; i++) {
					if (puzzle[coordinate][i] != '\u0000') {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	public String reverse(String word) {
		String reversed = "";
		for (int i = word.length() - 1; i >= 0; i--) {
			char ch = word.charAt(i);
			reversed = reversed + ch;
		}
		return reversed;
	}

	private void readFromFile(String wordFile, int wordCount, int shortest, int longest) {
		List<String> chosenWords = new ArrayList<String>();
		try {
			FileReader aFileReader = new FileReader(wordFile);
			BufferedReader aBufferReader = new BufferedReader(aFileReader);
			String lineFromFile;
			lineFromFile = aBufferReader.readLine();
			while (lineFromFile != null) {
				if (lineFromFile.length() >= shortest && lineFromFile.length() <= longest) {
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

	//Returns the sum of all the words being used multiplied by a scaling factor
	private int getDimensions() {
		int sum = 0;
		double scalingFactor = 1.75;
		for (String i : puzzleWords) {
			sum += i.length();
		}
		return ((int)(Math.sqrt(sum * scalingFactor)) + 1); //Incrementing it by 1 will always round it up
	}
}
