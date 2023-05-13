import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/*
 * Example of a parsed document:
 * Input: https://www.w3schools.com/html/html_basic.asp
 * Output:
 * {
 * "title": "HTML Tutorial",
 * "header h1": "HTML Tutorial",
 * "header h2": "HTML HOME",
 * "header h3": "HTML Introduction",
 * "header h4": "HTML Editors",
 * "header h5": "HTML Basic",
 * "header h6": "HTML Elements",
 * "paragraph": "HTML stands for Hyper Text Markup Language",
 * "url": "https://www.w3schools.com/html/html_basic.asp",
 * "meta": "HTML Tutorial",
 * "image": "https://www.w3schools.com/html/img_notsupported.gif",
 * "link": "https://www.w3schools.com/html/html_basic.asp"
 * }
 * 
 */

public class Parser {
    // parseDocument with url and content. Append the parsed document to an existing
    // Map
    public static void parseDocument(String url, String content, Map<String, Object> documentMap) {
        if (documentMap == null) {
            documentMap = new HashMap<>();
        }

        Document doc = Jsoup.parse(content);
        // Extract the title
        String title = doc.title();
        if (documentMap.containsKey("title")) {
            documentMap.put("title", documentMap.get("title") + " " + title);
        } else {
            documentMap.put("title", title);
        }
        // Extract the headers
        Elements headers = doc.select("h1, h2, h3, h4, h5, h6");
        for (Element header : headers) {
            String headerKey = "header " + header.tagName();
            String headerText = header.text();
            if (documentMap.containsKey(headerKey)) {
                documentMap.put(headerKey, documentMap.get(headerKey) + " " + headerText);
            } else {
                documentMap.put(headerKey, headerText);
            }
        }
        // Extract the paragraphs
        Elements paragraphs = doc.select("p");
        for (Element paragraph : paragraphs) {
            String paragraphText = paragraph.text();
            if (documentMap.containsKey("paragraph")) {
                documentMap.put("paragraph", documentMap.get("paragraph") + " " + paragraphText);
            } else {
                documentMap.put("paragraph", paragraphText);
            }
        }
        // url
        url = url.replace(".html", "").replace("%20", " ").replace("%3F", "?").replace("%2F", "/")
                .replace("%5C", "\\").replace("%7C", "|").replace("%3C", "<").replace("%3E", ">")
                .replace("%3A", ":").replace("%2A", "*").replace("%22", "\"").replace("_", "/");

        documentMap.put("url", url);
        // tags form meta (keywords)
        Elements metaTags = doc.select("meta[name=keywords]");
        for (Element metaTag : metaTags) {
            String metaTagText = metaTag.attr("content");
            if (documentMap.containsKey("meta")) {
                documentMap.put("meta", documentMap.get("meta") + " " + metaTagText);
            } else {
                documentMap.put("meta", metaTagText);
            }
        }

        // images
        Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
        for (Element image : images) {
            String imageUrl = image.attr("src");
            if (documentMap.containsKey("image")) {
                documentMap.put("image", documentMap.get("image") + " " + imageUrl);
            } else {
                documentMap.put("image", imageUrl);
            }
        }
        // links
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            // add valid urls only
            String linkUrl = link.attr("href");
            if (linkUrl.startsWith("http")) {
                if (documentMap.containsKey("link")) {
                    documentMap.put("link", documentMap.get("link") + " " + linkUrl);
                } else {
                    documentMap.put("link", linkUrl);
                }
            }

        }
    }
}
