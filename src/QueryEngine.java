
// QueryEngine java class
import java.util.List;
import javax.print.Doc;

import com.mongodb.MongoClient;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.Collections;
import com.mongodb.client.FindIterable;
// Document 
import org.bson.Document;
import java.util.ArrayList;
//Jsoup
import org.jsoup.Jsoup;

public class QueryEngine {

    MongoClient mongoClient;

    public static List<Object> search(String query) {
        // Phrase search
        System.out.println("Phrase search");
        List<String> tokens = null;
        List<Object> results = null;
        String description = null;
        if (query.contains("\"")) {
            // Phrase search
            query = query.replace("\"", "");
            QueryProcessor queryProcessor = new QueryProcessor();
            tokens = queryProcessor.processQuery(query); // This is the list of documents that contain
            // Ranking
            Ranker ranker = new Ranker();
            results = ranker.rank(tokens);
            List<Object> newResults = new ArrayList<Object>();
            for (int i = 0; i < results.size(); i++) {
                Document doc = (Document) results.get(i);
                String content = doc.getString("content");
                if (content.contains(query)) {
                    newResults.add(doc);
                }
            }

        } else {
            // Query search
            QueryProcessor queryProcessor = new QueryProcessor();
            tokens = queryProcessor.processQuery(query); // This is the list of documents that contain
            // Ranking
            Ranker ranker = new Ranker();
            results = ranker.rank(tokens);
        }
        // remove the content from the results
        for (int i = 0; i < results.size(); i++) {
            Document doc = (Document) results.get(i);
            String content = doc.getString("content");
            String snippet = getSnippet(content, query);
            doc.append("description", snippet);
            doc.remove("content");
            doc.remove("popularity");
            doc.remove("score");
        }
        return results;
    }

    private static String getSnippet(String content, String query) {
        String snippet = "";
        // use JSoup to parse the content
        org.jsoup.nodes.Document doc = Jsoup.parse(content);
        // get the text only
        String text = doc.text();
        Boolean isPhrase = false;
        if (query.contains("\"")) {
            isPhrase = true;
            query = query.replace("\"", "");
        }
        if (isPhrase) {
            // Phrase search
            int index = text.indexOf(query);
            if (index != -1) {
                int start = index - 100;
                int end = index + 100;
                if (start < 0) {
                    start = 0;
                }
                if (end > text.length()) {
                    end = text.length();
                }
                snippet = text.substring(start, end);
            }

        } else {
            // Query search
            String[] queryTokens = query.split(" ");
            for (String token : queryTokens) {
                int index = text.indexOf(token);
                if (index != -1) {
                    int start = index - 100;
                    int end = index + 100;
                    if (start < 0) {
                        start = 0;
                    }
                    if (end > text.length()) {
                        end = text.length();
                    }
                    snippet = text.substring(start, end);
                    break;
                }
            }
            if (snippet.length() == 0) {
                if (text.length() > 200) {
                    snippet = text.substring(0, 200);
                } else {
                    snippet = text;
                }
            }
        }

        return snippet;
    }

    // public static void main(String[] args) {
    // String query = "java programming";

    // List<Object> results = search(query);
    // for (Object result : results) {
    // Document doc = (Document) result;
    // // pring url and score
    // System.out.println(doc.getString("url") + " " + doc.getDouble("score"));
    // System.out.println(doc.getString("snippet"));
    // }
    // }

}
