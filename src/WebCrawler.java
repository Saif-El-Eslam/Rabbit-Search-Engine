
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileReader;
import java.net.URL;

public class WebCrawler implements Runnable {

    private static final int MAX_URLS = 7000;
    private static final int NUM_THREADS = 10;

    private final Queue<String> urlsToCrawl;
    private final Set<String> crawledUrls;
    private final String outputFile;
    private final String seedFile;
    private final Object lock = new Object();

    public WebCrawler(Queue<String> urlsToCrawl, Set<String> crawledUrls,
            String outputFile, String seedFile) {
        this.urlsToCrawl = urlsToCrawl;
        this.crawledUrls = crawledUrls;
        this.outputFile = outputFile;
        this.seedFile = seedFile;

        System.out.println("urlsToCrawl: " + urlsToCrawl.size());
        System.out.println("crawledUrls: " + crawledUrls.size());

    }

    @Override
    public void run() {
        while (!urlsToCrawl.isEmpty() && crawledUrls.size() < MAX_URLS) {
            String url;
            synchronized (lock) {
                url = urlsToCrawl.poll();
            }

            try {
                if (isUrlAllowedByRobots(url)) {
                    HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
                    connection.setRequestMethod("GET");

                    int status = connection.getResponseCode();
                    if (status >= 200 && status < 300) {
                        // read the HTML content of the page
                        StringBuilder contentBuilder = new StringBuilder();
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(connection.getInputStream()))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                contentBuilder.append(line).append("\n");
                                processLine(line, url);
                            }
                        }
                        String htmlContent = contentBuilder.toString();

                        // save the HTML content to a file
                        Boolean isSaved = saveHtmlContentToFile(url, htmlContent, "data");
                        if (isSaved) {
                            crawledUrls.add(url);

                            synchronized (lock) {
                                // update the output file with the crawled URLs
                                File file = new File(outputFile);
                                file.delete();
                                file.createNewFile();
                                try (BufferedWriter writer = new BufferedWriter(
                                        new FileWriter(outputFile, true))) {
                                    for (String s : crawledUrls) {
                                        writer.write(s);
                                        writer.newLine();
                                    }
                                }
                            }

                            synchronized (lock) {
                                // update the seed file with the remaining URLs to crawl
                                File file = new File(seedFile);
                                file.delete();
                                file.createNewFile();
                                try (BufferedWriter writer = new BufferedWriter(new FileWriter(seedFile, true))) {
                                    // add only the first 1000 urls to the seed file
                                    int count = 0;
                                    for (String s : urlsToCrawl) {
                                        if (count < 6000) {
                                            writer.write(s);
                                            writer.newLine();
                                            count++;
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Boolean saveHtmlContentToFile(String url, String htmlContent, String folderPath) {

        try {
            URL urlObject = new URL(url);
            String fileName = urlObject.getHost() + urlObject.getPath();
            // replace space with %20
            // replace ? with %3F
            // replace / with %2F
            // replace \ with %5C
            // replace | with %7C
            // replace < with %3C
            // replace > with %3E
            // replace : with %3A
            // replace * with %2A
            // replace " with %22
            fileName = fileName.replace(" ", "%20").replace("?", "%3F").replace("/", "_").replace("\\", "%5C")
                    .replace("|", "%7C").replace("<", "%3C").replace(">", "%3E").replace(":", "%3A").replace("*", "%2A")
                    .replace("\"", "%22");

            // Create the folder if it doesn't exist
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Create the file
            File file = new File(folderPath + File.separator + fileName + ".html");
            file.createNewFile();

            // Write the HTML content to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(htmlContent);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean isUrlAllowedByRobots(String url) throws IOException {
        String baseUrl = url.split("/")[2];
        HttpURLConnection connection = (HttpURLConnection) new URL("http://" + baseUrl + "/robots.txt")
                .openConnection();
        connection.setRequestMethod("GET");

        int status = connection.getResponseCode();
        String contentType = connection.getContentType();
        if (contentType != null && !contentType.startsWith("text/html")) {
            // Skip this URL as it is not an HTML document
            return false;
        } else {

            if (status == 404) {
                // if robots.txt does not exist, allow all URLs
                return true;
            } else if (status == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("Disallow:")) {
                            String disallowedUrl = line.substring("Disallow:".length()).trim();
                            if (url.contains(disallowedUrl)) {
                                return false;
                            }
                        }
                    }
                }
            }

        }
        return true;
    }

    private void processLine(String line, String baseUrl) {
        String regex = "(?i)<a\\s+(?:[^>]*?\\s+)?href=(\"|')([^\"'>\\s]+)\\1";
        Matcher matcher = Pattern.compile(regex).matcher(line);
        while (matcher.find()) {

            String url = matcher.group(2);
            // System.out.println("url before: " + url);
            // url = normalizeUrl(url);
            URLNormalizer urlNormalizer = new URLNormalizer();
            // convert string to stringbuilder
            StringBuilder url_builder = new StringBuilder(url);
            Boolean test = urlNormalizer.Normalize(url_builder);
            // System.out.println(test);
            url = url_builder.toString();
            // System.out.println("url after: " + url);
            if (url != null && !url.isEmpty() && !url.startsWith("#") && !url.startsWith("javascript:")
                    && !url.startsWith("mailto:") && !url.startsWith("tel:")) {
                if (url.startsWith("/")) {
                    url = baseUrl + url;
                } else if (!url.startsWith("http")) {
                    url = baseUrl + "/" + url;
                }
                synchronized (lock) {
                    if (!crawledUrls.contains(url) && !urlsToCrawl.contains(url)) {
                        urlsToCrawl.add(url);
                    }
                }
            }

        }
    }

    public static void main(String[] args) throws InterruptedException,
            IOException {
        Queue<String> urlsToCrawl = new LinkedList<>();
        Set<String> crawledUrls = new HashSet<>();

        // Read the seed.txt and add the urls to the queue
        try (BufferedReader reader = new BufferedReader(new FileReader(
                "seed.txt"))) {
            // remove contents of seed.txt
            String line;
            while ((line = reader.readLine()) != null) {
                urlsToCrawl.add(line);
            }
        }
        // check if output.txt exists (means that the crawler was stopped)
        if (new File("output.txt").exists()) {
            // if it exists, read the output.txt and add the urls to the crawledUrls
            try (BufferedReader reader = new BufferedReader(new FileReader(
                    "output.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    crawledUrls.add(line);
                }
            }
        }

        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(new WebCrawler(urlsToCrawl, crawledUrls,
                    "output.txt", "seed.txt"));
            threads[i].start();
        }

        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i].join();
        }
    }
}
