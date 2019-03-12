import java.util.ArrayList ;
import java.io.* ;

public class ReadFileExample {
    public static void main() {
        ArrayList<String> wordList = loadWordsFromFile("BasicEnglish.txt") ;
        System.out.println(wordList.size()) ;
    }
    
    private static ArrayList<String> loadWordsFromFile(String filename) {
        // BasicEnglish.txt - the 850 words of Basic English
        // BNCwords.txt - 5456 words
        try {
            FileReader aFileReader = new FileReader(filename);
            BufferedReader aBufferReader = new BufferedReader(aFileReader);
            String lineFromFile;
            ArrayList<String> words = new ArrayList<String>();
            lineFromFile = aBufferReader.readLine() ;
            while (lineFromFile != null) {  
                words.add(lineFromFile.toUpperCase());
                lineFromFile = aBufferReader.readLine() ;
            }
            aBufferReader.close();
            aFileReader.close();
            return words ;
        }
        catch(IOException x) {
            return null ;
        }
    }
}
