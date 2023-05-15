import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.util.Iterator;
import java.util.stream.StreamSupport;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.*;
class URL {
    public String url;
    public int score;

    URL(String u , int s)
    {
        url = u;
        score = s;
    }
}
public class Ranker {
    // public HashMap<String, ArrayList<singleURL>> allData;
    // public HashMap<String, URLWordsAndSentences> URLS;
    public String queryString;

    private static MongoClient mongoClient; // The MongoDB Client
    private static MongoDatabase database; // The MongoDB Database
    private static Map<String, String> documentsMap; // The Documents Map
    private static Map<String, List<Document>> invertedIndexMap; // The Inverted Index
    private static MongoCollection<Document> invertedIndex; // The MongoDB Collection
    // *************************Database Connection****************************

    public  void connectToDatabase() {
        try {
        // Connect to the MongoDB Server
        mongoClient = new MongoClient("localhost", 27017);
        // Connect to the Database
        database = mongoClient.getDatabase("SearchEngine");
        // Get the Inverted Index Collection
        invertedIndex = database.getCollection("InvertedIndex");
        System.out.println("Connected to database: " + database.getName());
        } catch (Exception e) {
            System.err.println("Error connecting to MongoDB server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public  Iterator<Document> getDocumentsByWord( String word) {
        // Create a query to find documents that contain the given word
        Document query = new Document("word", word);
    
        // Find the documents that match the query
        Iterator<Document> iterator = invertedIndex.find(query).iterator();
        
    
        // Extract the "documents" field from the matching documents and return an iterator to them
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                .flatMap(doc -> doc.getList("documents", Document.class).stream())
                .iterator();
    }


    public  Map<String,Double> getSortedURLS()
    {
        Map<String, Double> URLsWithScores = new HashMap<>();
      
       String[] myArray = this.queryString.split(" ");
    //    for(String word : myArray)
    //    {System.out.println(word);}
        for(String word : myArray)
        {
            System.out.println(word);
            int c= 0;
            Iterator <Document> iterator= getDocumentsByWord(word);
            while (iterator.hasNext() ) {
                c++;
                Document document = iterator.next();
                String urlString = document.getString("url");
                Double score =  Double.parseDouble(document.getString("score"));

                if (URLsWithScores.containsKey(urlString)) {
                    // If it does, add score to the url score
                    URLsWithScores.put(urlString, URLsWithScores.get(urlString) + score);
                 } else {
                    // If it doesn't, create a new key-value pair
                    URLsWithScores.put(urlString, score);
                 }
                //System.out.println(document.toJson()+"\n");
            }
        }


         // Create a TreeMap with a custom comparator that sorts the entries based on their value in descending order
         Map<String, Double> sortedURLsWithScores = new TreeMap<>(new Comparator<String>() {
            public int compare(String url1, String url2) {
                return Double.compare(URLsWithScores.get(url2), URLsWithScores.get(url1));
            }
        });
        
        // Add the entries of the HashMap to the TreeMap to sort them based on their scores
        sortedURLsWithScores.putAll(URLsWithScores);
        return sortedURLsWithScores;
    }

    Ranker(String query){
    queryString = query;
    connectToDatabase();

    }

    public static void main(String[] args) throws Exception {
       Ranker ranker = new Ranker("inform nation ");
       Map<String,Double> rankedURLS =  ranker.getSortedURLS();
       for (String url : rankedURLS.keySet()) {
        double score = rankedURLS.get(url);
        System.out.println(url + ":" + score);
     }
        System.out.println("lol LOOL");
    }
    // (score1 + score2) * pop/100    
}
