import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageRank {
    
    private final double dampingFactor;
    private final double threshold;
    private final Map<String, List<String>> linkMap; // from to
    private final Map<String, List<String>> linkMap2; // to from 
    private final Map<String, Double> pageRankMap;
    
    public PageRank(double dampingFactor , double threshold) {
        this.dampingFactor = dampingFactor; 
        // The parameter that governs the probability of a user clicking on a link on the web page
        this.threshold = threshold;
        this.linkMap = new HashMap<>();
        this.linkMap2 = new HashMap<>();
        this.pageRankMap = new HashMap<>();
    }
    
    public void addLink(String from, String to) {
        if (!linkMap.containsKey(from)) {
            linkMap.put(from, new ArrayList<>());
        }
        if(!linkMap2.containsKey(to)){
            linkMap2.put(to, new ArrayList<>());
        }
        linkMap.get(from).add(to);
        linkMap2.get(to).add(from);
    }
    
    public void calculatePageRank() {
        //PageRank is a probability distribution, and the sum of all probabilities must be equal to 1.0.
        double initialPageRank = 1.0 / linkMap.size();
        for (String page : linkMap.keySet()) {
            pageRankMap.put(page, initialPageRank);
        }
        Boolean possible = true;
        while(possible) {
            Map<String, Double> newPageRankMap = new HashMap<>();
            for (String page : pageRankMap.keySet()) 
            {
            Double newRanking = 1 - dampingFactor ;// 0.85 0.95 ! 0.5 0.15 -> out
            List<String> toPages = getToPages(page);
            for (String topage : toPages)
            {
                //PR(A) = (1-d) + d * (PR(T1)/C(T1) + ... + PR(Tn)/C(Tn))
                newRanking += (pageRankMap.get(topage) * dampingFactor / getFromPagesSize(topage)); 
            }
           
            newPageRankMap.put(page, newRanking);
            
            }

            //System.out.println(i);
            for (String page : newPageRankMap.keySet())
            {
                Boolean flag = true; 
            /*
             * If one the difference between new rank and old rank of one  page is less than threshold 
             * then continue looping until all pages ranks differences are less than threshold 
             */
                if(newPageRankMap.get(page) - pageRankMap.get(page) < threshold && flag )
                possible= false;
                else 
                flag = false;
                System.out.println("Page " +page +" PageRank: " + newPageRankMap.get(page));
                pageRankMap.put(page, newPageRankMap.get(page));
            }
            
        }
    }
    
    private double getPageRank(String page) {
        return pageRankMap.getOrDefault(page, 0.0);
    }
    
    private int  getFromPagesSize(String page) {
        return linkMap2.get(page).size();
    }
    private List<String> getToPages(String page) {
        return linkMap.get(page);
    }

    private Map<String, Double> getPagesRanks(){ 
        return pageRankMap;
    }

    public static void main(String[] args) throws Exception {
        PageRank pr = new PageRank(0.85,0.00001);
        //TODO: 
        // www.fox -> 1- 2- 3- 4- 5- 6- 7- 8- 9

       // pr.addlink(www.fox,1)
       // pr.addlink(www.fox,2)
       // pr.addlink(www.fox,3)
       // pr.addlink(www.fox,4)

        pr.addLink("A", "B");
        pr.addLink("A", "C");
        pr.addLink("B", "C");
        pr.addLink("B", "D");
        pr.addLink("C", "D");
        pr.addLink("D", "A");

        pr.calculatePageRank();
        //pp = Map<String, Double> = pr.getPagesRanks() 
        ///TODO: database - > link , popularity       
         System.out.println("lol LOOL");
     }
      
}
