
import com.mongodb.MongoClient;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Accumulators;

import org.bson.Document;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.IOException;
//Collections
import java.util.Collections;
import com.mongodb.client.FindIterable;

public class Indexer {

    private static MongoClient mongoClient; // The MongoDB Client
    private static MongoDatabase database; // The MongoDB Database
    private static Map<String, String> documentsMap; // The Documents Map
    private static Map<String, List<Document>> invertedIndexMap; // The Inverted Index
    private static MongoCollection<Document> invertedIndex; // The MongoDB Collection
    // *************************Database Connection****************************

    public static void connectToDatabase() {
        // Connect to the MongoDB Server
        mongoClient = new MongoClient("localhost", 27017);
        // Connect to the Database
        database = mongoClient.getDatabase("SearchEngine");
        // Get the Inverted Index Collection
        invertedIndex = database.getCollection("InvertedIndex");
    }

    // ********************************Parsening********************************
    // input: path to the folder containing the documents
    // output: a list of documents
    public static Map<String, Object> parseDocument(String url, String content) {
        // Parse the Documents
        Map<String, Object> documentMap = new HashMap<>();
        Parser.parseDocument(url, content, documentMap);
        // Add the Document to the Documents List
        // documentsMap.put(url, documentMap);
        // for (Map.Entry<String, Object> entry : documentMap.entrySet()) {
        // System.out.println(entry.getKey() + " : " + entry.getValue());
        // }
        // System.out.println("*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*");
        return documentMap;
    }

    // ********************************Tokenization********************************
    // input: a list of documents
    // output: a list of tokens
    public static Map<String, List<String>> tokenize(Map<String, Object> documents) {
        Map<String, List<String>> tokenizedDocument = new HashMap<>();
        tokenizedDocument = Tokenizer.tokenize(documents);
        // for (Map.Entry<String, List<String>> entry : tokenizedDocument.entrySet()) {
        // System.out.println(entry.getKey() + " : " + entry.getValue());
        // }
        // System.out.println("****************************************************************************************");
        return tokenizedDocument;
    }

    // ********************************Stop Words********************************
    // input: a list of tokens
    // output: a list of tokens without stop words
    public static Map<String, List<String>> removeStopWords(Map<String, List<String>> tokenizedDocument) {
        Map<String, List<String>> filteredDocument = new HashMap<>();
        filteredDocument = StopWordsRemoval.removeStopWords(tokenizedDocument);
        // for (Map.Entry<String, List<String>> entry : filteredDocument.entrySet()) {
        // System.out.println(entry.getKey() + " : " + entry.getValue());
        // }
        // System.out.println("****************************************************************************************");
        return filteredDocument;
    }
    // ********************************Stemming********************************
    // input: a list of tokens
    // output: a list of tokens after stemming

    public static Map<String, List<String>> stemTokens(Map<String, List<String>> tokenizedDocument) {
        Map<String, List<String>> stemmedDocument = new HashMap<>();
        stemmedDocument = Stemmer.stemTokens(tokenizedDocument);
        // for (Map.Entry<String, List<String>> entry : stemmedDocument.entrySet()) {
        // System.out.println(entry.getKey() + " : " + entry.getValue());
        // }
        // System.out.println("****************************************************************************************");
        return stemmedDocument;
    }

    // ********************************Indexing********************************
    // input: a list of tokens
    // output: a list of tokens with their documents and positions
    // fill the invertedIndexMap
    public static void addDocumentToInvertedIndex(Map<String, List<String>> stemmedDocument) {
        // get words positions in a stemmed document
        // input : Map<String, List<String>> stemmedDocument
        // output:
        // word1:title(3),paragraph(15),meta(1),header h1(1),header h2(1),header
        // h3(1),header h4(1),header h5(1),header h6(1),tf(5),idf(0.5),tf-idf(2.5)
        // word2:title(3),paragraph(15),meta(1),header h1(1),header h2(1),header
        // h3(1),header h4(1),header h5(1),header h6(1),tf(5),idf(0.5),tf-idf(2.5)
        // word3:title(3),paragraph(15),meta(1),header h1(1),header h2(1),header
        // h3(1),header h4(1),header h5(1),header h6(1),tf(5),idf(0.5),tf-idf(2.5)
        // number between brackets is the frequency of the word in the document or count
        // of the word in the document
        // drop the image and link key of the stemmedDocument map
        // create copy of the stemmedDocument map
        Map<String, List<String>> stemmedDocumentCopy = new HashMap<>();
        stemmedDocumentCopy.putAll(stemmedDocument);
        // remove the image and link key from the stemmedDocumentCopy map
        stemmedDocumentCopy.remove("image");
        stemmedDocumentCopy.remove("link");
        String url = stemmedDocumentCopy.get("url").get(0);
        stemmedDocumentCopy.remove("url");
        // print url
        // System.out.println(url);
        String word = getWordsPositions(stemmedDocumentCopy, url);
        // System.out.println(word);
        // convert the string to a dictionary
        Map<String, Map<String, String>> wordPositions = new HashMap<>();
        wordPositions = convertStringToDictionary(word);
        // save to database
        saveToDatabase(wordPositions);
        // TODO: till now each document is saved in the database as a separate document
        // SEE DATABASE FOR MORE DETAILS
        // calculate score for each document and save it in the database invertedIndex
        // collection
        // example:
        // word1:{"url1": 0.5, "url2": 0.3, "url3": 0.2}
        // explanation: word1 has a score of 0.5 in url1, 0.3 in url2 and 0.2 in url3
        // should the scores added up to 1? or should we calculate the score for each
        // document separately?
        // answer: calculate the score for each document separately

    }

    // ********************************Database********************************
    // input: a list of tokens with their documents and positions
    // output: a database containing the inverted index

    public static void saveToDatabase(Map<String, Map<String, String>> wordPositions) {
        // connect to the database
        connectToDatabase();

        // get the database and collection
        MongoDatabase database = mongoClient.getDatabase("searchEngine");
        MongoCollection<Document> collection = database.getCollection("invertedIndex");

        // add each word to the collection
        for (Map.Entry<String, Map<String, String>> entry : wordPositions.entrySet()) {
            String word = entry.getKey();
            Map<String, String> document = entry.getValue();

            // check if the word already exists in the collection
            Document existingWord = collection.find(Filters.eq("word", word)).first();

            if (existingWord != null) {
                // check if the document already exists
                List<Document> documents = (List<Document>) existingWord.get("documents");
                boolean documentExists = false;
                for (Document d : documents) {
                    if (d.getString("url").equals(document.get("url"))) {
                        documentExists = true;
                        break;
                    }
                }

                if (!documentExists) {
                    // add the document to the existing word's document list
                    collection.updateOne(
                            Filters.eq("word", word),
                            Updates.push("documents", document));
                }
            } else {
                // create a new word and add it to the collection
                List<Document> documents = new ArrayList<>();
                // add the document to the documents list
                Document doc = new Document();
                for (Map.Entry<String, String> e : document.entrySet()) {
                    doc.append(e.getKey(), e.getValue());
                }
                documents.add(doc);

                Document newWord = new Document("word", word)
                        .append("documents", documents);
                collection.insertOne(newWord);
            }

        }

        // close the connection
        closeDatabaseConnection();
    }

    public static void saveDocumentToDatabase(String url, String content) {
        // connect to the database
        connectToDatabase();

        // get the database and collection
        MongoDatabase database = mongoClient.getDatabase("searchEngine");
        MongoCollection<Document> collection = database.getCollection("documents");

        // create a new document
        Document document = new Document("url", url)
                .append("content", content);

        // add the document to the collection
        collection.insertOne(document);

        // close the connection
        closeDatabaseConnection();
    }

    public static void scoreDocuments() {
        // connect to the database
        connectToDatabase();
        // get the database and collection
        MongoDatabase database = mongoClient.getDatabase("searchEngine");
        MongoCollection<Document> collection = database.getCollection("invertedIndex");
        // get all the documents in the collection
        FindIterable<Document> documents = collection.find();
        // iterate over the documents
        for (Document document : documents) {

            // if the document have the key "idf" then it is already scored
            if (document.containsKey("idf")) {
                continue;
            }
            // get the word
            String word = document.getString("word");

            // get the documents
            List<Document> documentsList = (List<Document>) document.get("documents");
            // length of the documents list
            int length = documentsList.size();

            // idf = log10(total number of documents / number of documents containing the
            // word)
            double idf = Math.log10(6000 / length);

            // add the idf to the documents
            document.append("idf", idf);

            // iterate over the documents
            for (Document doc : documentsList) {

                // get the url
                String url = doc.getString("url");

                // get the tf
                double tf = Double.parseDouble(doc.getString("tf"));
                // get title
                double title = Double.parseDouble(doc.getString("title"));
                // get meta
                double meta = Double.parseDouble(doc.getString("meta"));
                // get header h1
                double headerH1 = Double.parseDouble(doc.getString("header h1"));
                // get header h2
                double headerH2 = Double.parseDouble(doc.getString("header h2"));
                // get header h3
                double headerH3 = Double.parseDouble(doc.getString("header h3"));
                // get header h4
                double headerH4 = Double.parseDouble(doc.getString("header h4"));
                // get header h5
                double headerH5 = Double.parseDouble(doc.getString("header h5"));
                // get header h6
                double headerH6 = Double.parseDouble(doc.getString("header h6"));
                // get paragraph
                double paragraph = Double.parseDouble(doc.getString("paragraph"));
                // Score = (tf * idf) * (w1 * h1 + w2 * h2 + w3 * h3 + w4 * h4 + w5 * h5 + w6 *
                // h6 + w7 * p + w8 * m + w9 * t)
                double score = (tf * idf) * (0.1 * paragraph + 0.2 * headerH6 + 0.3 * headerH5 + 0.4 * headerH4
                        + 0.5 * headerH3 + 0.6 * headerH2 + 0.7 * headerH1 + 0.8 * meta + 0.9 * title);
                // add the score to the document
                doc.append("score", score);

            }

            // update the document
            collection.updateOne(Filters.eq("word", word), Updates.set("documents", documentsList));
            collection.updateOne(Filters.eq("word", word), Updates.set("idf", idf));

        }
        // close the connection
        closeDatabaseConnection();
    }

    // ************************Database Closing********************************
    // Close the connection to the database
    public static void closeDatabaseConnection() {
        mongoClient.close();
    }

    // *************************************Utility*************************************
    // Read the content of a file
    public static String readFileContent(String path) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(java.nio.file.Paths.get(path)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static String getWordsPositions(Map<String, List<String>> stemmedDocument, String url) {
        StringBuilder sb = new StringBuilder();

        for (String word : getAllValues(stemmedDocument)) {

            int frequency = Collections.frequency(getAllValues(stemmedDocument), word);
            sb.append(word + ":");

            sb.append("title(" + frequencyTitle(stemmedDocument.get("title"), word) + "),");
            sb.append("paragraph(" + frequencyParagraph(stemmedDocument.get("paragraph"), word) + "),");
            sb.append("meta(" + frequencyMeta(stemmedDocument.get("meta"), word) + "),");

            for (int i = 1; i <= 6; i++) {
                sb.append("header h" + i + "(" + frequencyHeader(stemmedDocument.get("header h" + i), word) + "),");
            }

            double tf = computeTermFrequency(stemmedDocument, word);
            sb.append("tf(" + tf + "),");
            sb.append("url(" + url + ")");
            sb.append("\n");
        }

        return sb.toString();
    }

    private static int frequencyTitle(List<String> titleWords, String word) {
        return Collections.frequency(titleWords, word);
    }

    private static int frequencyParagraph(List<String> paragraphWords, String word) {
        return Collections.frequency(paragraphWords, word);
    }

    private static int frequencyMeta(List<String> metaWords, String word) {
        return Collections.frequency(metaWords, word);
    }

    private static int frequencyHeader(List<String> headerWords, String word) {
        return Collections.frequency(headerWords, word);
    }

    private static double computeTermFrequency(Map<String, List<String>> stemmedDocument, String word) {
        int count = 0;
        for (List<String> words : stemmedDocument.values()) {
            count += Collections.frequency(words, word);
        }
        return (double) count / getAllValues(stemmedDocument).size();
    }

    private static double computeInverseDocumentFrequency(Map<String, List<String>> stemmedDocument, String word) {
        int docsWithTerm = 0;
        for (List<String> words : stemmedDocument.values()) {
            if (Collections.frequency(words, word) > 0) {
                docsWithTerm++;
            }
        }
        if (docsWithTerm > 0) {
            return Math.log((double) stemmedDocument.size() / docsWithTerm);
        } else {
            return 0;
        }
    }

    private static List<String> getAllValues(Map<String, List<String>> map) {
        List<String> allValues = new ArrayList<>();
        for (List<String> values : map.values()) {
            allValues.addAll(values);
        }
        return allValues;
    }

    public static Map<String, Map<String, String>> convertStringToDictionary(String word) {
        Map<String, Map<String, String>> wordPositions = new HashMap<>();
        String[] words = word.split("\n");
        for (String w : words) {
            String[] wordInfo = w.split(":");
            String wordName = wordInfo[0];
            String[] wordPositionsInfo = wordInfo[1].split(",");
            Map<String, String> positions = new HashMap<>();
            for (String wordPositionInfo : wordPositionsInfo) {
                String[] positionInfo = wordPositionInfo.split("\\(");
                String positionName = positionInfo[0];
                String positionValue = positionInfo[1].substring(0, positionInfo[1].length() - 1);
                positions.put(positionName, positionValue);
            }
            wordPositions.put(wordName, positions);
        }
        return wordPositions;

    }

    private static String fileNameToUrl(String fileName) {
        String url = fileName.replace(".html", "").replace("%20", " ").replace("%3F", "?").replace("%2F", "/")
                .replace("%5C", "\\").replace("%7C", "|").replace("%3C", "<").replace("%3E", ">")
                .replace("%3A", ":").replace("%2A", "*").replace("%22", "\"").replace("_", "/");
        return url;
    }

    // private static Boolean isValidType(String url) {
    // String[] validTypes = { "html", "htm", "php", "asp", "aspx", "jsp" };
    // for (String type : validTypes) {
    // if (url.endsWith(type)) {
    // return true;
    // }
    // }
    // return false;

    // }

    public static void main(String[] args) {
        // Connect to the Database
        connectToDatabase();
        // read the documents
        File[] files = new File("data").listFiles();
        int count = 0;
        for (File file : files) {
            System.out.println(count++);
            if (file.isFile()) {
                if (file.isFile()) {
                    String name = file.getName();
                    String url = fileNameToUrl(name);
                    System.out.println(url);
                    String content = readFileContent(file.getPath());

                    // Parse the Documents
                    Map<String, Object> parsedDocument = parseDocument(name, content);
                    // Tokenize the Documents
                    Map<String, List<String>> tokenizedDocument = tokenize(parsedDocument);
                    // Remove Stop Words from the Documents
                    Map<String, List<String>> filteredDocument = removeStopWords(tokenizedDocument);
                    // Stem the Tokens
                    Map<String, List<String>> stemmedDocument = stemTokens(filteredDocument);
                    // add the document to the inverted index
                    addDocumentToInvertedIndex(stemmedDocument);
                    // save the url and content to the database
                    saveDocumentToDatabase(url, content);

                }

            }
            // delete the file after reading it
            file.delete();

        }
        // score the documents, and save the score to the databasse
        scoreDocuments();
        // print the inverted index
        // for (Map.Entry<String, List<Document>> entry : invertedIndexMap.entrySet()) {
        // System.out.println(entry.getKey() + " : " + entry.getValue());
        // }
        // Close the Database Connection
        closeDatabaseConnection();

    }

}
