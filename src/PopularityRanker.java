
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.jsoup.Jsoup;

import java.util.List;

import org.bson.Document;

public class PopularityRanker {

    // get first paragraph where

    public static void main(String[] args) {
        DBManager dbManager = new DBManager();
        MongoClient mongoClient = dbManager.connectToDatabase();
        MongoDatabase database = dbManager.getDatabase(mongoClient, "searchEngine");

        // gett all collections
        MongoCollection<Document> words = dbManager.getCollection(database,
                "invertedIndex");
        MongoCollection<Document> documents = dbManager.getCollection(database,
                "documents");
        for (Document word : words.find()) {
            String wordString = word.getString("word");
            List<Document> documentsList = (List<Document>) word.get("documents");
            // get the content associated to url from the documents collection
            for (Document document : documentsList) {
                String url = document.getString("url");
                Document documentContent = dbManager.getDocument(documents, "url", url);
                if (documentContent == null) {
                    continue;
                }
                String content = documentContent.getString("content");
                // get the number of times the word appears in the content
                int count = Jsoup.parse(content).select("body").text().split(wordString).length - 1;
                // update the count in the inverted index
                // dbManager.updateDocument(words, "word", wordString,
                // "count",String.valueOf(count));
            }

        }

    }

}
