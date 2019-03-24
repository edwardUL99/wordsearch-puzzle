import java.util.ArrayList;
import java.util.Arrays;

public class Driver {
	public static void main(String[] args) {
		//WordSearchPuzzle test = new WordSearchPuzzle("words.txt", 5, 1, 5);
        String[] wordsArray = {"rabbit", "water", "kangaroo", "trees", "dogs", "houses", "shelters", "god", "humans", "cancer", "leo", "virgo", "capricorn"};
        ArrayList<String> words = new ArrayList<String>(Arrays.asList(wordsArray));
        WordSearchPuzzle test = new WordSearchPuzzle(words);
        /*
		System.out.println(test.getWordSearchList());
		System.out.println();
		System.out.println(test.getPuzzleAsString());
		System.out.println("\n" + Arrays.deepToString(test.getPuzzleAsGrid())); */

		test.showWordSearchPuzzle(false);
	}
}
