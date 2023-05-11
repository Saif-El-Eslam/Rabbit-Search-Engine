import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tokenizer {

    public static Map<String, List<String>> tokenize(Map<String, Object> input) {
        Map<String, List<String>> tokensMap = new HashMap<>();

        // Tokenize the paragraph
        String paragraph = (String) input.get("paragraph");
        if (paragraph != null) {
            List<String> paragraphTokens = tokenizeText(paragraph);
            tokensMap.put("paragraph", paragraphTokens);
        }
        else{
            tokensMap.put("paragraph", new ArrayList<>());
        }

        // Tokenize the title
        String title = (String) input.get("title");
        if (title != null) {
            List<String> titleTokens = tokenizeText(title);
            tokensMap.put("title", titleTokens);
        }
        else{
            tokensMap.put("title", new ArrayList<>());
        }

        // Tokenize the header tags (h1 to h6)
        for (int i = 1; i <= 6; i++) {
            String headerKey = "header h" + i;
            String headerValue = (String) input.get(headerKey);
            if (headerValue != null) {
                List<String> headerTokens = tokenizeText(headerValue);
                tokensMap.put(headerKey, headerTokens);
            }
            else{
                tokensMap.put(headerKey, new ArrayList<>());
            }
        }
        // tokensMap.put()
        // Tokenize the meta tags
        for (int i = 1; i <= 6; i++) {
            String metaKey = "meta";
            String metaValue = (String) input.get(metaKey);
            if (metaValue != null) {
                List<String> metaTokens = tokenizeText(metaValue);
                tokensMap.put(metaKey, metaTokens);
            }
            else{
                tokensMap.put(metaKey, new ArrayList<>());
            }
        }
        // print the type of url
        String url = (String) input.get("url");
        //don't tokenize the url
        List<String> urlTokens = new ArrayList<>();
        urlTokens.add(url);
        tokensMap.put("url", urlTokens);

        String links = (String) input.get("link");
        // links are string separated by space
        if (links != null) {
            String[] linksArray = links.split(" ");
            // don't tokenize the links
            List<String> linksTokens = new ArrayList<>();
            for (String link : linksArray) {
                linksTokens.add(link);
            }
            tokensMap.put("link", linksTokens);

        }
        else{
            tokensMap.put("link", new ArrayList<>());
        }
        String images = (String) input.get("image");
        // images are string separated by space
        if (images != null) {
            String[] imagesArray = images.split(" ");
            // don't tokenize the images
            List<String> imagesTokens = new ArrayList<>();
            for (String image : imagesArray) {
                imagesTokens.add(image);
            }
            tokensMap.put("image", imagesTokens);

        }
        else{
            tokensMap.put("image", new ArrayList<>());
        }

        return tokensMap;
    }

    private static List<String> tokenizeText(String text) {
        // Split the text into tokens using whitespace and punctuation as delimiters
        String[] words = text.split("[\\s\\p{Punct}]+");

        // Convert each token to lowercase and add it to the list
        List<String> tokens = new ArrayList<>();
        for (String word : words) {
            tokens.add(word.toLowerCase());
        }

        return tokens;
    }
}
