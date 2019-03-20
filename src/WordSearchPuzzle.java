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
	private String directions = "";
	private int dimensions;

	public WordSearchPuzzle(List<String> userSpecifiedWords) {
		this.puzzleWords = userSpecifiedWords;
		capitalise();
		generateWordSearchPuzzle();
	}

	public WordSearchPuzzle(String wordFile, int wordCount, int shortest, int longest) {
		this.puzzleWords = new ArrayList<String>(wordCount);
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

	public void showWordSearchPuzzle(boolean hide) {
		System.out.println("WordSearch Puzzle");
		System.out.println("------------------");
		System.out.println("Unused positions filled with random characters");
		System.out.println(getPuzzleAsString() + "\n");
		System.out.println("Words used");
		if (!hide) {
			System.out.println("Words used with directions shown");
			System.out.println(this.directions);
		} else {
			for (int i = puzzleWords.size() - 1; i >= 0; i--) {
				System.out.println(puzzleWords.get(i));
			}
		}

	}

	private void generateWordSearchPuzzle() {
		if (puzzleWords.size() != 0) {
			this.dimensions = getDimensions();
			puzzle = new char[this.dimensions][this.dimensions];
			int i = puzzleWords.size() - 1, row, col, attempts = 0;
			boolean reversed, vertical, inserted = false;
			String word;
			while (i >= 0) {
				word = puzzleWords.get(i);
				while (!inserted && i >= 0) {
					char[] wordArray = word.toCharArray();
					vertical = random.nextBoolean();
					if (attempts < 100 && !(word.length() > dimensions)) {
						row = (int) (Math.random() * puzzle.length);
						col = (int) (Math.random() * puzzle[0].length);
						if (ableToInsert(row, col, vertical, word)) {
							reversed = random.nextBoolean();
							insert(wordArray, row, col, vertical, reversed);
							this.directions = this.directions + dirsToString(row, col, word, vertical, reversed) + "\n";
							inserted = true; //If word was inserted successfully we want to exit the loop
						} else {
							attempts++;
						}
					} else {
						puzzleWords.remove(i);
						i--;
						if (i >= 0) {
							word = puzzleWords.get(i);
						}
						inserted = false;
						attempts = 0;
					}
				}
				inserted = false;
				attempts = 0;
				i--;
			}
			fillUnused();
		}
	}

	private String dirsToString(int row, int col, String word, boolean vertical, boolean reversed) {
		char dir;
		if (vertical && reversed) {
			row =  row + (word.length() -1);
			dir = 'U';
		} else if (vertical) {
			dir = 'D';
		} else if (reversed) {
			col = col + (word.length() - 1);
			dir = 'L';
		} else {
			dir = 'R';
		} 

		return String.format("%s[%d][%d]%c", word, row, col, dir);
	}

	private void insert(char[] wordArray, int startingRow, int startingCol, boolean vertical, boolean reversed) {
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

	private boolean ableToInsert(int row, int col, boolean vertical, String word) {
		if (word.length() > this.dimensions) {
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
                if (puzzle[row][i] != '\u0000') {
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
			this.puzzleWords.set(i, puzzleWords.get(i).toUpperCase());
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
