# Rabbit-Search-Engine

## How to setup the project

As this is a java project, the needed library are stored in the lib folder.
If you are using eclipse, you can add the libraries by right clicking on the project, then go to Build Path -> Configure Build Path -> Libraries -> Add JARs -> Select all the JAR files in the lib folder.
If you are using maven, you can add the libraries by adding the following lines to the pom.xml file:

```
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.13.1</version>
</dependency>
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongo-java-driver</artifactId>
    <version>3.12.2</version>
</dependency>
```

## How to run the project

1. Web Crawler
   - In the crawler file, specifiy MAX_URLS and MAX_THREADS
   - Run the crawler file
   - The crawler will create a file called "output.txt" which contains all the urls that were crawled and a folder called "data" which contains the html files of the crawled urls
2. Indexer

   - Start the Server connection on localhost:27017 to access the database
   - Run the indexer file
   - The indexer will create the inverted index and store it in the database  
     Example of the output:

   ```
   {
   "_id": {
    "$oid": "645cc4c15820753b5483af43"
   },
   "word": "globe",
   "documents": [
    {
      "paragraph": "0", //count
      "header h5": "0",
      "header h6": "0",
      "idf": "2.1972245773362196",
      "title": "0",
      "url": "www.reuters.com",
      "score": "0.022119710510096167",
      "tf": "0.010067114093959731",
      "header h1": "0",
      "header h2": "0",
      "tf-idf": "0.022119710510096167",
      "meta": "3",
      "header h3": "0",
      "header h4": "0"
    }
   ]
   }

   ```
