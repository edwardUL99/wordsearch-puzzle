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
                    puzzleString =  puzzleString + "\t" + ch;
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
			boolean reversed, vertical, inserted = false, diagonal;
			int[] coords;
			for (int i = 0; i < puzzleWords.size(); i++) {
				String word = puzzleWords.get(i);
				while (!inserted && word.length() <= this.dimensions) {
					vertical = random.nextBoolean();
					diagonal = random.nextBoolean();
					coords = generateCoordinates(word, vertical, diagonal);
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

	private String dirsToString(int row, int col, String word, boolean vertical, boolean reversed, boolean diagonal) {
		String dir;
		if ((vertical && reversed) && !diagonal) {
			row =  row + (word.length() -1);
			dir = "U";
		} else if (vertical && !diagonal) {
			dir = "D";
		} else if (reversed && !diagonal) {
			col = col + (word.length() - 1);
			dir = "L";
		} else if (!reversed && !diagonal) {
			dir = "R";
		} else {
			if (reversed && col > this.dimensions / 2) {
				col = col - (word.length() - 1);
				row = row + (word.length() - 1);
				dir = "LeftRevDiag";
			} else if (reversed) {
				col = col + (word.length() - 1);
				row = row + (word.length() - 1);
				dir = "RightRevDiag";
			} else if (col > this.dimensions / 2) {
				dir = "LeftDownDiag";
			} else {
				dir = "RightDownDiag";
			}
		}

		return String.format("%s[%d][%d]%s", word, row, col, dir);
	}

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
	
	private void insertDiagonally(char[] wordArray, int row, int col) {
		int index = 0;
		int startingCol = col;
		while (index < wordArray.length) {
			if (startingCol < this.dimensions / 2) {
				puzzle[row++][col++] = wordArray[index++];
			} else {
				puzzle[row++][col--] = wordArray[index++];
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
	            count++;
            }
	        if (count == word.length()) {
	            return true;
            }
        }
	    return false;
    }

    private int[] generateCoordinates(String word, boolean vertical, boolean diagonal) {
	    int[] coords = new int[2];
	    int attempts = 0;
	    boolean canInsert = false;
	    while (attempts < 100 && !canInsert) {
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
            puzzleWords.remove(word);
        }

	    return coords;
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
