import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

public class SearchServer {
    private List<Object> results;

    public void performSearch() {
        QueryEngine queryEngine = new QueryEngine();
        results = queryEngine.search("\"hello world\"", "score");
    }

    public void startServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new SearchHandler());
        server.start();
        System.out.println("Server is running on port " + port);
    }

    private class SearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = results.toString();

            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(response.getBytes());
            output.close();
        }
    }

    public static void main(String[] args) throws IOException {
        SearchServer searchServer = new SearchServer();
        searchServer.performSearch();
        searchServer.startServer(8080);
    }
}
