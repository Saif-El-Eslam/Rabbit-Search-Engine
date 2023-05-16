
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
import java.util.Comparator;
import java.util.List;

public class Ranker {
    private static MongoClient mongoClient;

    // sort
    public static List<Object> sort(List<Object> results) {
        Collections.sort(results, new Comparator<Object>() {
            // sort according to score
            @Override
            public int compare(Object o1, Object o2) {
                return ((Document) o2).getDouble("score").compareTo(((Document) o1).getDouble("score"));
            }

        });
        return results;
    }

    public static List<Object> rank(List<String> stemmedTokens) {
        List<Object> results = new ArrayList<Object>();

        DBManager dbManager = new DBManager();
        mongoClient = dbManager.connectToDatabase();
        MongoDatabase database = dbManager.getDatabase(mongoClient, "searchEngine");
        MongoCollection<Document> invertedIndex = dbManager.getCollection(database,
                "invertedIndex");
        MongoCollection<Document> documents = dbManager.getCollection(database,
                "documents");
        // get documents that contain the first token
        for (String token : stemmedTokens) {
            System.out.println(token);
            // word = token
            // get the document that contains the word
            Document document = invertedIndex.find(Filters.eq("word", token)).first();
            if (document == null) {
                continue;
            }
            // get the documents in the document
            List<Document> documentsList = (List<Document>) document.get("documents");

            for (Document doc : documentsList) {
                // get the documents from the documents collection that have the same url
                Document documentContent = dbManager.getDocument(documents, "url",
                        doc.get("url").toString());
                if (documentContent == null) {
                    continue;
                }
                // if popularity is null, skip the document
                if (documentContent.get("popularity") == null) {
                    continue;
                }

                double popularity = Double.parseDouble(documentContent.get("popularity").toString()) / 100;
                if (results.contains(documentContent)) {
                    // update the score
                    // get the document from the results
                    Document docInResults = (Document) results.get(results.indexOf(documentContent));
                    // get the score
                    double score = docInResults.getDouble("score");
                    // update the score
                    docInResults.put("score", score + (double) doc.get("score") * popularity);
                    // update the document in the results
                    results.set(results.indexOf(documentContent), docInResults);
                } else {
                    // add the document to the results
                    double newScore = popularity * (double) doc.get("score");
                    documentContent.put("score", newScore);
                    results.add(documentContent);
                }

            }
        }

        // sort the results
        results = sort(results);
        return results;
    }

    public static void main(String[] args) {
        List<String> stemmedTokens = new ArrayList<String>();
        stemmedTokens.add("inform");
        stemmedTokens.add("nation");
        List<Object> results = rank(stemmedTokens);
        int count = 0;
        for (Object doc : results) {
            System.out.println(((Document) doc).get("url") + " " + ((Document) doc).get("score"));
            count++;
            if (count == 5) {
                break;
            }
        }
    }

}
