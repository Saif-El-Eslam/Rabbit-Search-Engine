
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.util.List;
// HttpHandler
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
// HTTP Server
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class QuerySuggestion {
    private DBManager dbManager;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> savedQueries;

    public QuerySuggestion() {
        dataBase();
    }

    private void dataBase() {
        dbManager = new DBManager();
        mongoClient = dbManager.connectToDatabase();
        database = dbManager.getDatabase(mongoClient, "searchEngine");
        savedQueries = DBManager.getCollection(database, "SavedQueries");
    }

    public void addQuery(String query) {
        Document existingQuery = savedQueries.find(new Document("query", query.trim().toLowerCase())).first();
        if (existingQuery != null) {
            System.out.println("Query already exists in the database.");
            return;
        }
        Document document = new Document("query", query.trim().toLowerCase());
        savedQueries.insertOne(document);
        System.out.println("Query saved successfully: " + query);
    }

    public List<String> getQueries(String prefix) {
        List<String> matchingQueries = new ArrayList<>();

        // Create a regular expression pattern to match documents starting with the
        // given prefix
        String regexPattern = "^" + prefix.trim().toLowerCase();

        // Build the query using the $regex operator
        Document query = new Document("query", new Document("$regex", regexPattern));

        // Retrieve the matching documents from the collection and convert the iterator
        // to a list
        FindIterable<Document> result = savedQueries.find(query);
        List<Document> resultList = new ArrayList<>();
        result.into(resultList);

        for (Document queryDoc : resultList) {
            matchingQueries.add(queryDoc.getString("query"));
        }

        return matchingQueries;
    }

    // Handler for processing suggestion requests
    private class SuggestionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                // Extract the prefix from the request
                String prefix = extractPrefixFromRequest(exchange);

                // Get matching queries for the prefix
                List<String> matchingQueries = getQueries(prefix);

                // Convert the matching queries to a string representation
                String response = matchingQueries.toString();

                // Set the response headers and send the response
                setCorsHeaders(exchange);
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(response.getBytes());
                output.close();
                System.out.println("Response sent for suggestion: " + response);
            } else if ("POST".equals(exchange.getRequestMethod())) {
                // Extract the query from the request
                String query = extractQueryFromRequest(exchange);

                // Add the query to the database
                addQuery(query);

                // Set the response headers and send the response
                setCorsHeaders(exchange);
                exchange.sendResponseHeaders(200, 0);
                OutputStream output = exchange.getResponseBody();
                output.close();
                System.out.println("Query added successfully: " + query);
            } else {
                // Handle unsupported HTTP methods
                String response = "Unsupported HTTP method";
                exchange.sendResponseHeaders(405, response.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(response.getBytes());
                output.close();
                System.out.println("Unsupported HTTP method: " + exchange.getRequestMethod());
            }
        }

        // Sets the CORS headers for the response
        private void setCorsHeaders(HttpExchange exchange) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        }

        // Extracts the prefix from the request URI
        private String extractPrefixFromRequest(HttpExchange exchange) {
            String requestURI = exchange.getRequestURI().toString();
            String prefix = requestURI.substring(requestURI.lastIndexOf('/') + 1);
            return prefix;
        }

        // Extracts the query from the request body
        private String extractQueryFromRequest(HttpExchange exchange) throws IOException {
            StringBuilder requestBody = new StringBuilder();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = exchange.getRequestBody().read(buffer)) != -1) {
                requestBody.append(new String(buffer, 0, bytesRead));
            }
            return requestBody.toString();
        }
    }

    public static void main(String[] args) throws IOException {
        // Create an instance of the QuerySuggestion
        QuerySuggestion querySuggestion = new QuerySuggestion();
        querySuggestion.addQuery("hello");

        // Create an HTTP server instance
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // Set up the request handler for the "/suggestion" context (with CORS support)
        server.createContext("/suggestion", querySuggestion.new SuggestionHandler());

        // Start the server
        server.start();
        System.out.println("Server is running on port 8000");
    }
}