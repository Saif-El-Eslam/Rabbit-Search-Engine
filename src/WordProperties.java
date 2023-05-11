import java.util.Map;
import java.util.HashMap;

public class WordProperties {
    private String word;
    private int titleFrequency;
    private int paragraphFrequency;
    private int metaFrequency;
    private Map<String, Integer> headerFrequencies;
    private double termFrequency;
    private double inverseDocumentFrequency;
    private double tfidf;

    // Constructor
    public WordProperties(String word, int titleFrequency, int paragraphFrequency, int metaFrequency,
            Map<String, Integer> headerFrequencies, double termFrequency, double inverseDocumentFrequency, double tfidf) {
        this.word = word;
        this.titleFrequency = titleFrequency;
        this.paragraphFrequency = paragraphFrequency;
        this.metaFrequency = metaFrequency;
        this.headerFrequencies = headerFrequencies;
        this.termFrequency = termFrequency;
        this.inverseDocumentFrequency = inverseDocumentFrequency;
        this.tfidf = tfidf;
    }

    // Getters
    public String getWord() {
        return word;
    }

    public int getTitleFrequency() {
        return titleFrequency;
    }

    public int getParagraphFrequency() {
        return paragraphFrequency;
    }

    public int getMetaFrequency() {
        return metaFrequency;
    }

    public Map<String, Integer> getHeaderFrequencies() {
        return headerFrequencies;
    }

    public double getTermFrequency() {
        return termFrequency;
    }

    public double getInverseDocumentFrequency() {
        return inverseDocumentFrequency;
    }

    public double getTfidf() {
        return tfidf;
    }
    // Setters
    public void setWord(String word) {
        this.word = word;
    }

    public void setTitleFrequency(int titleFrequency) {
        this.titleFrequency = titleFrequency;
    }

    public void setParagraphFrequency(int paragraphFrequency) {
        this.paragraphFrequency = paragraphFrequency;
    }

    public void setMetaFrequency(int metaFrequency) {
        this.metaFrequency = metaFrequency;
    }

    public void setHeaderFrequency(Map<String, Integer> headerFrequencies) {
        this.headerFrequencies = headerFrequencies;
    }

    public void setTermFrequency(double termFrequency) {
        this.termFrequency = termFrequency;
    }

    public void setInverseDocumentFrequency(double inverseDocumentFrequency) {
        this.inverseDocumentFrequency = inverseDocumentFrequency;
    }


    // setTfIdf
    public void setTfIdf(double tfidf) {
        this.tfidf = tfidf;
    }

}
