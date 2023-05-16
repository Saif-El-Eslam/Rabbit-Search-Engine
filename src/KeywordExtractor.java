import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class KeywordExtractor{

    public static List<String> extractKeywords(String content) {
        List<String> keywords = new ArrayList<>();

        try {
            // Parse the HTML content string
            Document doc = Jsoup.parse(content);

            // Extract meta keywords from the document
            Elements metaTags = doc.select("meta[name=keywords]");
            for (Element metaTag : metaTags) {
                String keywordsContent = metaTag.attr("content");
                if (!keywordsContent.isEmpty()) {
                    // Split the keywords content into individual keywords
                    String[] keywordArray = keywordsContent.split(",");
                    for (String keyword : keywordArray) {
                        keywords.add(keyword.trim());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return keywords;
    }

}
