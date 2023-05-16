
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

public class QueryEngine {

    MongoClient mongoClient;

    public static List<Object> search(String query) {
        // Phrase search
        List<String> tokens = null;
        List<Object> results = null;
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
                    // System.out.println("Found");
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
            doc.remove("content");
            doc.remove("popularity");
            doc.remove("score");
        }
        return results;
    }

    public static void main(String[] args) {
        String query = "java programming";

        List<Object> results = search(query);
        for (Object result : results) {
            Document doc = (Document) result;
            // pring url and score
            System.out.println(doc.getString("url") + " " + doc.getDouble("score"));
            System.out.println(doc.getString("popularity"));
        }
    }

}
