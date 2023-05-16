import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageRank {

    private final double dampingFactor;
    private final double threshold;
    private final Map<String, List<String>> linkMap; // from to
    private final Map<String, List<String>> linkMap2; // to from
    private final Map<String, Double> pageRankMap;
    private final ArrayList<String> links;

    public PageRank(double dampingFactor, double threshold) {
        this.dampingFactor = dampingFactor;
        this.links = new ArrayList<>();
        // The parameter that governs the probability of a user clicking on a link on
        // the web page
        this.threshold = threshold;
        this.linkMap = new HashMap<>();
        this.linkMap2 = new HashMap<>();
        this.pageRankMap = new HashMap<>();
    }

    public void addLink(String from, String to) {
        if (!links.contains(to)) {
            links.add(to);
            linkMap.put(to, new ArrayList<>());
            linkMap2.put(to, new ArrayList<>());
            linkMap.get(to).add(to);
            linkMap2.get(to).add(to);
        }
        if (!links.contains(from)) {
            links.add(from);
            linkMap.put(from, new ArrayList<>());
            linkMap2.put(from, new ArrayList<>());
            linkMap.get(from).add(from);
            linkMap2.get(from).add(from);
        }

        linkMap.get(from).add(to);
        linkMap2.get(to).add(from);
    }

    public void calculatePageRank() {
        // PageRank is a probability distribution, and the sum of all probabilities must
        // be equal to 1.0.
        System.out.println("Calculating PageRank...");
        double initialPageRank = 1.0 / linkMap.size();
        for (String page : linkMap.keySet()) {
            pageRankMap.put(page, initialPageRank);
        }
        Boolean possible = true;
        System.out.println("PageRank calculation started.");
        while (possible) {
            Map<String, Double> newPageRankMap = new HashMap<>();
            for (String page : pageRankMap.keySet()) {
                Double newRanking = 1 - dampingFactor;// 0.85 0.95 ! 0.5 0.15 -> out
                List<String> toPages = getToPages(page);
                for (String topage : toPages) {
                    newRanking += (pageRankMap.get(topage) * dampingFactor / getFromPagesSize(topage));
                }
                newPageRankMap.put(page, newRanking);

            }

            Boolean flag = true;
            for (String page : newPageRankMap.keySet()) {
                /*
                 * If one the difference between new rank and old rank of one page is less than
                 * threshold
                 * then continue looping until all pages ranks differences are less than
                 * threshold
                 */
                if (newPageRankMap.get(page) - pageRankMap.get(page) < threshold && flag)
                    possible = false;
                else
                    flag = false;
                // System.out.println("Page " + page + " PageRank: " +
                // newPageRankMap.get(page));ystem

                // System.out.println(newPageRankMap.get(page));

                pageRankMap.put(page, newPageRankMap.get(page));
            }

        }
        System.out.println("PageRank calculation finished.");
    }

    private double getPageRank(String page) {
        return pageRankMap.getOrDefault(page, 0.0);
    }

    private int getFromPagesSize(String page) {
        if (linkMap2.get(page).size() != 0)
            return linkMap2.get(page).size();
        else
            return 1;
    }

    private List<String> getToPages(String page) {
        return linkMap.get(page);
    }

    private Map<String, Double> getPagesRanks() {
        return pageRankMap;
    }

    private List<String> processLine(String line, String baseUrl) {
        List<String> links = new ArrayList<>();

        String regex = "(?i)<a\\s+(?:[^>]*?\\s+)?href=(\"|')([^\"'>\\s]+)\\1";
        Matcher matcher = Pattern.compile(regex).matcher(line);
        while (matcher.find()) {

            String url = matcher.group(2);

            URLNormalizer urlNormalizer = new URLNormalizer();
            // convert string to stringbuilder
            StringBuilder url_builder = new StringBuilder(url);
            Boolean test = urlNormalizer.Normalize(url_builder);
            // System.out.println(test);
            url = url_builder.toString();
            if (url != null && !url.isEmpty() && !url.startsWith("#") && !url.startsWith("javascript:")
                    && !url.startsWith("mailto:") && !url.startsWith("tel:")) {
                if (url.startsWith("/")) {
                    url = baseUrl + url;
                } else if (!url.startsWith("http")) {
                    url = baseUrl + "/" + url;
                }
                links.add(url);
            }

        }
        return links;
    }

    public static void main(String[] args) throws Exception {
        PageRank pr = new PageRank(0.85, 0.001);
        DBManager dbManager = new DBManager();
        MongoClient mongoClient = dbManager.connectToDatabase();
        MongoDatabase database = dbManager.getDatabase(mongoClient, "searchEngine");
        MongoCollection<Document> documents = dbManager.getCollection(database, "documents");
        int count = 0;
        for (Document document : documents.find()) {
            String url = document.getString("url");
            String content = document.getString("content");
            // extract links from content
            List<String> links = pr.processLine(content, url);
            // print number of links in each url
            // System.out.println("Number of links in " + url + " is " + links.size());
            for (String link : links) {
                pr.addLink(url, link);
            }
            count++;
            // if (count == 100)
            // break;

        }
        System.out.println("Number of pages: " + pr.linkMap.size());
        System.out.println("Number of links: " + pr.linkMap2.size());
        // pr.addLink("A", "B");
        // pr.addLink("A", "C");
        // pr.addLink("B", "C");
        // pr.addLink("B", "D");
        // pr.addLink("C", "D");
        // pr.addLink("D", "A");
        // pr.addLink("A", "K");
        pr.calculatePageRank();

        for (String page : pr.pageRankMap.keySet()) {
            dbManager.updateDocument(documents, "url", page, "popularity", String.valueOf(pr.pageRankMap.get(page)));
        }

        System.out.println("DONE");
    }

}