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

public class QueryProcessor {

    // List of documents to be returned to the user

    public static void main(String[] args) {
        String query = "a quick ";
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
        DBManager dbManager = new DBManager();
        MongoClient mongoClient = dbManager.connectToDatabase();
        MongoDatabase database = dbManager.getDatabase(mongoClient, "searchEngine");
        // gett all collections
        MongoCollection<Document> index = dbManager.getCollection(database, "invertedIndex");
        MongoCollection<Document> documents = dbManager.getCollection(database, "documents");
        // get the score of each word
        for (String token : stemmedTokens) {
            Document docContiningword = index.find(Filters.eq("word", token)).first();
            if (docContiningword != null) {
                List<Document> documentsContainingWord = (List<Document>) docContiningword.get("documents");
                for (Document document : documentsContainingWord) {
                    Double score = (Double) document.get("score");
                    String url = (String) document.get("url");
                    System.out.println(url + " " + score);
                }
            }
        }

    }

}
