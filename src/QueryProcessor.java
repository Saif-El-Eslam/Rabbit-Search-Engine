import java.util.List;

public class QueryProcessor {

    public static void main(String[] args) {
        String query = "a quick brown fox jumps over the lazy dog";
        Tokenizer tokenizer = new Tokenizer();
        List<String> tokens = tokenizer.tokenizeText(query);
        System.out.println(tokens);
        // stop words removal
        StopWordsRemoval stopWordsRemover = new StopWordsRemoval();
        List<String> filteredTokens = stopWordsRemover.removeStopWordsFromTokens(tokens);
        System.out.println(filteredTokens);
        // stemming
        Stemmer stemmer = new Stemmer();
        List<String> stemmedTokens = stemmer.stemToken(filteredTokens);
        System.out.println(stemmedTokens);
        // TODO: move database connection to a separate class
    }

}
