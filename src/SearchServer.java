import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

public class SearchServer {
    private List<Object> results;

    public void performSearch(String query) {
        QueryEngine queryEngine = new QueryEngine();
        results = queryEngine.search(query);
    }

    public void startServer(int port) throws IOException {
        // Create an HTTP server instance
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Set up the request handler for the root context
        server.createContext("/", new SearchHandler());

        // Set up the request handler for the "/search" context (with CORS support)
        server.createContext("/search", new CorsHandler());

        // Start the server
        server.start();
        System.out.println("Server is running on port " + port);
    }

    // Handler for processing search requests
    private class SearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Extract the query from the request
            String query = extractQueryFromRequest(exchange);

            // Perform the search with the extracted query
            performSearch(query);

            // Convert the results to a string representation
            String response = results.toString();

            // Set the response headers and send the response
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(response.getBytes());
            output.close();
        }

        // Extracts the query string from the request body
        private String extractQueryFromRequest(HttpExchange exchange) throws IOException {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                requestBody.append(line);
            }
            br.close();
            isr.close();
            return requestBody.toString();
        }
    }

    // Handler for handling CORS (Cross-Origin Resource Sharing) requests
    private class CorsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Set the necessary CORS headers
            Headers headers = exchange.getResponseHeaders();
            headers.set("Access-Control-Allow-Origin", "*");
            headers.set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            headers.set("Access-Control-Allow-Headers", "Content-Type");

            // Send an empty response with status 200
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        }
    }

    public static void main(String[] args) throws IOException {
        // Create an instance of the SearchServer
        SearchServer searchServer = new SearchServer();

        // Start the server on port 8080
        searchServer.startServer(8080);
    }
}
