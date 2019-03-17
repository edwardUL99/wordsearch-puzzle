/* WordSearchPuzzle class for CS4222 Spring Programming Assignment
 Author: Edward Lynch-Milner
 ID: 18222021
*/
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;

public class WordSearchPuzzle {
	private char[][] puzzle;
	private List<String> puzzleWords;
	private Random random = new Random();

	public WordSearchPuzzle(List<String> userSpecifiedWords) {
		puzzleWords = userSpecifiedWords;
		capitalise();
		generateWordSearchPuzzle();
	}

	public WordSearchPuzzle(String wordFile, int wordCount, int shortest, int longest) {
		puzzleWords = new ArrayList<String>(wordCount);
		readFromFile(wordFile, wordCount, shortest, longest);
		generateWordSearchPuzzle();
	}

	public List<String> getWordSearchList() {
		return puzzleWords;
	}

	public char[][] getPuzzleAsGrid() {
		return puzzle;
	}

	public String getPuzzleAsString() {
	    if (puzzle != null) {
            String puzzleString = "";
            for (char[] i : puzzle) {
                for (char ch : i) {
                    puzzleString = puzzleString + "\t" + ch;
                }
                puzzleString = puzzleString + "\n";
            }
            return puzzleString;
        }
		return null;
	}

	private void generateWordSearchPuzzle() {
		if (puzzleWords.size() != 0) {
			final int dimensions = getDimensions();
			puzzle = new char[dimensions][dimensions];
			int i = puzzleWords.size() - 1, row, col, attempts = 0;
			while (i >= 0) {
			    String word = puzzleWords.get(i);
				char[] wordArray = word.toCharArray();
				while (!puzzleContainsWord(word, false) && !puzzleContainsWord(word, true)) {
					boolean vertical = random.nextBoolean();
					if (attempts < 100) {
						System.out.println(word);
						row = (int) (Math.random() * puzzle.length);
						col = (int) (Math.random() * puzzle[0].length);
						System.out.printf("%d + %d\n",row, col);
						System.out.println(ableToInsert(row, col, vertical, word));
						if (ableToInsert(row, col, vertical, word)) {
							insert(wordArray, row, col, vertical, random.nextBoolean());
						}
						attempts++;
					} else {
						i--;
						word = puzzleWords.get(i);
						attempts = 0;
					}
				}
				attempts = 0;
				i--;
			}
			fillUnused();
		}
	}

	public void insert(char[] wordArray, int startingRow, int startingCol, boolean vertical, boolean reversed) {
		int index = 0;
		if (reversed) {
			wordArray = reverse(new String(wordArray)).toCharArray();
		}
		while (index < wordArray.length) {
			if (vertical) {
				puzzle[startingRow++][startingCol] = wordArray[index++];
			} else {
				puzzle[startingRow][startingCol++] = wordArray[index++];
			}
		}
	}

	private void fillUnused() {
        for (int row = 0; row < puzzle.length; row++) {
            for (int col = 0; col < puzzle[0].length; col++) {
                if (puzzle[row][col] == '\u0000') {
                    puzzle[row][col] = (char)(Math.random() * (91 - 65) + 65);
                }
            }
        }
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

	private boolean ableToInsert(int row, int col, boolean vertical, String word) {
		if (word.length() > getDimensions()) {
			return false;
		}
	    if (vertical) {
            if (word.length() > (puzzle.length - row)) {
                return false;
            }
	        for (int i = row; i < puzzle.length; i++) {
	            if (puzzle[i][col] != '\u0000') {
	                return false;
                }
            }
	        return true;
        } else {
            if (word.length() > (puzzle.length - col)) {
                return false;
            }
            for (int i = col; i < puzzle[0].length; i++) {
                if (puzzle[row][col] != '\u0000') {
                    return false;
                }
            }
            return true;
        }
	}

	private String reverse(String word) {
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

	private void capitalise() {
		for (int i = 0; i < puzzleWords.size(); i++) {
			puzzleWords.set(i, puzzleWords.get(i).toUpperCase());
		}
	}

	//Returns the sum of all the words being used multiplied by a scaling factor
	private int getDimensions() {
		int sum = 0;
		double scalingFactor = 1.75;
		for (String i : puzzleWords) {
			sum += i.length();
		}
		return ((int)(Math.sqrt((sum + 1) * scalingFactor)) + 1); //Incrementing it by 1 will always round it up
	}
}
