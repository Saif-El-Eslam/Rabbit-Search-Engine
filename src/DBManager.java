import com.mongodb.MongoClient;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.Collections;
import com.mongodb.client.FindIterable;
// Document
import org.bson.Document;

public class DBManager {

    // connect to database
    public static MongoClient connectToDatabase() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        return mongoClient;
    }

    // get database
    public static MongoDatabase getDatabase(MongoClient mongoClient, String databaseName) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        return database;
    }

    // get collection
    public static MongoCollection<Document> getCollection(MongoDatabase database, String collectionName) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection;
    }

    // insert document
    public static void insertDocument(MongoCollection<Document> collection, Document document) {
        collection.insertOne(document);
    }

    // update document
    public static void updateDocument(MongoCollection<Document> collection, String key, String value, String updateKey,
            String updateValue) {
        collection.updateOne(Filters.eq(key, value), Updates.set(updateKey, updateValue));
    }

    // delete document
    public static void deleteDocument(MongoCollection<Document> collection, String key, String value) {
        collection.deleteOne(Filters.eq(key, value));
    }

    // get all documents
    public static FindIterable<Document> getAllDocuments(MongoCollection<Document> collection) {
        FindIterable<Document> documents = collection.find();
        return documents;
    }

    // disconnect from database
    public static void disconnectFromDatabase(MongoClient mongoClient) {
        mongoClient.close();
    }

    // getDocument
    public static Document getDocument(MongoCollection<Document> collection, String key, String value) {
        Document document = collection.find(Filters.eq(key, value)).first();
        return document;
    }

}
