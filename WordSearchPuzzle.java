/** WordSearchPuzzle class for CS4222 Spring Programming Assignment
* Author: Edward Lynch-Milner
* ID: 18222021
**/
import java.util.List;
import java.util.ArrayList;
import java.io.*;

public class WordSearchPuzzle {
	private char[][] puzzle;
	private List<String> puzzleWords;

	public wordSearchPuzzle(List<String> userSpecifiedWords) {
		puzzleWords = userSpecifiedWords;
	}

	public WordSearchPuzzle(String wordFile, int wordCount, int shortest, int longest) {
		puzzleWords = new ArrayList<String>(wordCount);
		readFromFile(wordFile, wordCount, shortest, longest);
	}

	public List<String> getWordSearchList() {
		return puzzleWords;
	}


	public char[][] getPuzzleAsGrid() {
	}

	public String getPuzzleAsString() {
	}

	public void showWordSearchPuzzle(boolean hide) {
	}

	private void generateWordSearchPuzzle() {
	}

	private void readFromFile(String wordFile, int WordCount, int shortest, int longest) {
		List<String> chosenWords = new ArrayList<String>();
		try {
			FileReader aFileReader = new FileReader(wordFile);
			BufferedReader aBufferReader = new BufferedReader(aFileReader);
			String lineFromFile;
			lineFromFile = aBufferReader.readLine();
			while (lineFromFile != null) {
				if (lineFromFile.length() >= shortest && lineFromFile.length() <= longest) {
					chosenWords.add(lineFromFile);
				}
				lineFromFile = aBufferReader.readLine();
			}
			aBufferReader.close();
			aFileReader.close();
		} catch (IOException e) {
		}
		storeIntoPuzzleArray(chosenWords, WordCount);
	}

	private void storeIntoPuzzleArray(List<String> chosenWords, int WordCount) {
		int i = 0;
		while (i < chosenWords.size()) {
			int randPos = (int)(Math.random() * chosenWords.size());
			if (!puzzleWords.contains(chosenWords.get(randPos))) {
				if (puzzleWords.size() < WordCount) {
					puzzleWords.add(chosenWords.get(randPos));
				}
				i++;
			}
		}
		chosenWords = null;
	}
}
