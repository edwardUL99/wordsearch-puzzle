/** WordSearchPuzzle class for CS4222 Spring Programming Assignment
* Author: Edward Lynch-Milner
* ID: 18222021
**/
import java.util.ArrayList;
import java.io.*;

public class WordSearchPuzzle {
	private char[][] puzzle;
	private ArrayList<String> puzzleWords;
	
	public WordSearchPuzzle(ArrayList<String> userSpecifiedWords) {
		
		puzzleWords = userSpecifiedWords;
	}
	
	public WordSearchPuzzle(String wordFile, int wordCount, int shortest, int longest) {
		puzzleWords = new ArrayList<String>(wordCount);
		readFromFile(wordFile, wordCount, shortest, longest);
	}
	
	public ArrayList<String> getWordSearchList() {
		return puzzleWords;
	}
	
	private void readFromFile(String wordFile, int WordCount, int shortest, int longest) {
		ArrayList<String> chosenWords = new ArrayList<String>();
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
		
		while (puzzleWords.size() < WordCount) {
			int randPos = (int)(Math.random() * chosenWords.size());
			if (!puzzleWords.contains(chosenWords.get(randPos))) {
				puzzleWords.add(chosenWords.get(randPos));
			}
		}
	}
}