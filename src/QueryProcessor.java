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

public class QueryProcessor {

    private static MongoClient mongoClient;

    public static List<Object> processQuery(String query) {
        List<Object> results = new ArrayList<Object>();
        // tokenize
        Tokenizer tokenizer = new Tokenizer();
        List<String> tokens = tokenizer.tokenizeText(query);
        // stop words removal
        StopWordsRemoval stopWordsRemover = new StopWordsRemoval();
        List<String> filteredTokens = stopWordsRemover.removeStopWordsFromTokens(tokens);
        // stemming
        Stemmer stemmer = new Stemmer();
        List<String> stemmedTokens = stemmer.stemToken(filteredTokens);

        DBManager dbManager = new DBManager();
        mongoClient = dbManager.connectToDatabase();
        MongoDatabase database = dbManager.getDatabase(mongoClient, "searchEngine");
        MongoCollection<Document> invertedIndex = dbManager.getCollection(database, "invertedIndex");
        MongoCollection<Document> documents = dbManager.getCollection(database, "documents");
        // get documents that contain the first token
        for (String token : stemmedTokens) {
            // word = token
            // get the document that contains the word
            Document document = invertedIndex.find(Filters.eq("word", token)).first();
            // get the documents in the document
            List<Document> documentsList = (List<Document>) document.get("documents");
            for (Document doc : documentsList) {
                // get the documents from the documents collection that have the same url
                Document documentContent = dbManager.getDocument(documents, "url", doc.get("url").toString());
                if (documentContent == null) {
                    continue;
                }
                // append a key value pair to the document
                documentContent.append("score", doc.get("score"));
                results.add(documentContent);

            }
        }
        return results;
    }

    public static void main(String[] args) {
        // List<Object> results = processQuery("hello world");
        // for (Object doc : results) {
        // System.out.println(doc);
        // }
    }

}
