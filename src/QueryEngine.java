
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

    public static List<Object> search(String query, String sortBy) {
        // Phrase search
        List<Object> results = null;
        if (query.contains("\"")) {
            System.out.println("Phrase search");
            // remove the quotes from the query
            query = query.replace("\"", "");
            QueryProcessor queryProcessor = new QueryProcessor();
            results = queryProcessor.processQuery(query); // This is the list of documents that contain the
            System.out.println(results.size());
            List<Object> newResults = new ArrayList<Object>();
            for (int i = 0; i < results.size(); i++) {
                Document doc = (Document) results.get(i);
                String content = doc.getString("content");
                if (content.contains(query)) {
                    System.out.println("Found");
                    newResults.add(doc);
                } else {
                    System.out.println("Not found");

                }
            }
            results = newResults;

        } else {
            QueryProcessor queryProcessor = new QueryProcessor();
            results = queryProcessor.processQuery(query); // This is the list of documents that contain the
            System.out.println("Query search");
        }
        // Collections.sort(results, new SortByScore()); // sort by popularity,
        // relevance(score)
        return results;
    }

    public static void main(String[] args) {
        List<Object> results = search("this article", "score");
        // print size of results
        System.out.println(results.size());
        // TODO: Ranking
    }

}