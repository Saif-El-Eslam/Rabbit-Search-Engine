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

    public static List<String> processQuery(String query) {
        // List<Object> results = new ArrayList<Object>();
        // tokenize
        Tokenizer tokenizer = new Tokenizer();
        List<String> tokens = tokenizer.tokenizeText(query);
        // stop words removal
        StopWordsRemoval stopWordsRemover = new StopWordsRemoval();
        List<String> filteredTokens = stopWordsRemover.removeStopWordsFromTokens(tokens);
        // stemming
        Stemmer stemmer = new Stemmer();
        List<String> stemmedTokens = stemmer.stemToken(filteredTokens);

        return stemmedTokens;

    }

    public static void main(String[] args) {
        QueryProcessor queryProcessor = new QueryProcessor();
        List<String> results = queryProcessor.processQuery("hello world");
        System.out.println(results);
    }

}
