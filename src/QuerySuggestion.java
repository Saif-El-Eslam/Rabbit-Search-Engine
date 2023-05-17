
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;


public class QuerySuggestion {
    private static DBManager dbManager ;
    private static MongoClient mongoClient;
    private static MongoDatabase database ;
    // gett all collections
    private static MongoCollection<org.bson.Document> SavedQueries ;
   
    private static void dataBase(){
       dbManager = new DBManager();
        mongoClient = dbManager.connectToDatabase();
         database = dbManager.getDatabase(mongoClient, "SearchEngine");
 
          SavedQueries = DBManager.getCollection(database,
                 "SavedQueries");
    }
    private   QuerySuggestion(){
        dataBase();
    }

    private  void addQuery(String query){
        Document existingQuery = SavedQueries.find(new Document("query", query.trim().toLowerCase())).first();
        if (existingQuery != null) {
            System.out.println("Query already exists in the database.");
            return;
        }
        Document document = new Document("query", query.trim().toLowerCase());
        SavedQueries.insertOne(document);
        System.out.println("Query saved successfully: " + query);

    }

    private static  List<Document>  getQueries(String prefix){
        List<Document> matchingQueries = new ArrayList<>();

        // Create a regular expression pattern to match documents starting with the given prefix
        String regexPattern = "^" + prefix.trim().toLowerCase();

        // Build the query using the $regex operator
        Document query = new Document("query", new Document("$regex", regexPattern));

        // Retrieve the matching documents from the collection and convert the iterator to a list
        FindIterable<Document> result = SavedQueries.find(query);
        List<Document> resultList = new ArrayList<>();
        result.into(resultList);

        return resultList;
    }

    
    public static void main(String[] args) {
        QuerySuggestion suggestion = new QuerySuggestion();

        suggestion.addQuery("       I apprecIate  YAsser");
        List<Document> matchingQueries = suggestion.getQueries(" i a ");
        for (Document query : matchingQueries) {
            String queryText = query.getString("query");
            System.out.println("Matching query: " + queryText);
        }
    }
}