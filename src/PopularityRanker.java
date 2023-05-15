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

public class PopularityRanker {

    public static void main(String[] args) {
        DBManager dbManager = new DBManager();
        MongoClient mongoClient = dbManager.connectToDatabase();
        MongoDatabase database = dbManager.getDatabase(mongoClient, "searchEngine");
        // gett all collections
        MongoCollection<Document> documents = dbManager.getCollection(database, "documents");

    }

}
