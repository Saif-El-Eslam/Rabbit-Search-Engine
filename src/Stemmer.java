import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
// Porter stemmer algorithm
import org.tartarus.snowball.ext.PorterStemmer;


public class Stemmer {

    public static Map<String, List<String>> stemTokens(Map<String, List<String>> tokenizedDocument) {
        Map<String, List<String>> stemmedDocument = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : tokenizedDocument.entrySet()) {
            String key = entry.getKey();
            List<String> tokens = entry.getValue();
            List<String> stemmedTokens = stemToken(tokens);
            stemmedDocument.put(key, stemmedTokens);
        }
        return stemmedDocument;
    }
    
    public static List<String> stemToken(List<String> tokens) {
        List<String> stemmedTokens = new ArrayList<>();
        for (String token : tokens) {
            String stemmedToken = stem(token);
            stemmedTokens.add(stemmedToken);
        }
        return stemmedTokens;
    }
    
    private static String stem(String token) {
        // Perform stemming algorithm on token and return the stemmed form
        // e.g. using Porter stemmer algorithm:
        PorterStemmer stemmer = new PorterStemmer();
        stemmer.setCurrent(token);
        stemmer.stem();
        return stemmer.getCurrent();
    }
}    