import java.util.ArrayList;

public class Driver {
	public static void main(String[] args) {
		WordSearchPuzzle test = new WordSearchPuzzle("words.txt", 12, 1, 5);
		System.out.println(test.getWordSearchList());
	}
}
