import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class StopWordsRemoval {
    private static List<String> stopWords = new ArrayList<>();

    public static void loadStopWords() {
        try {
            stopWords = Files.readAllLines(Paths.get("stopwords.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static Map<String,List<String>> removeStopWords(Map<String,List<String>> tokenizedDocument) {
        loadStopWords();
        Map<String,List<String>> filteredDocument = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : tokenizedDocument.entrySet()) {
            String key = entry.getKey();
            List<String> tokens = entry.getValue();
            List<String> filteredTokens = removeStopWordsFromTokens(tokens);
            filteredDocument.put(key, filteredTokens);
        }
        return filteredDocument;
    }

    public static List<String> removeStopWordsFromTokens(List<String> tokens) {
        List<String> filteredTokens = new ArrayList<>();
        for (String token : tokens) {
            if (!stopWords.contains(token)) {
                filteredTokens.add(token);
            }
        }
        return filteredTokens;
    }
 
    
}
