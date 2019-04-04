import java.util.ArrayList;
import java.util.Arrays;

public class Driver {
	public static void main(String[] args) {
        String[] wordsArray = {"rabbit", "water", "kangaroo", "trees", "dogs", "houses", "shelters", "god", "humans", "cancer", "leo", "virgo", "capricorn"};
        ArrayList<String> words = new ArrayList<String>(Arrays.asList(wordsArray));
        WordSearchPuzzle test = new WordSearchPuzzle(words);

		System.out.println(test.getWordSearchList());
		System.out.println();
		System.out.println(test.getPuzzleAsString());
		System.out.println("\n" + Arrays.deepToString(test.getPuzzleAsGrid()));

		test.showWordSearchPuzzle(false);

		WordSearchPuzzle test1 = new WordSearchPuzzle("words.txt", 10, 1, 10);
		System.out.println(test1.getWordSearchList());
		test1.showWordSearchPuzzle(false);
	}
}
