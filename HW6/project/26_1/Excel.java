import java.io.*;
import java.util.*;

public class Excel {
    
    public static void main (String[] args) {
        
        AllWords aw = new AllWords(args[0]);
        StopWords sw = new StopWords();
        UnstopWords uw = new UnstopWords(aw, sw);
        Unique uni = new Unique(uw);
        Count c = new Count(uw, uni);
        SortedData sd = new SortedData(uni, c);
        
        
        for(int i=0; i<25; i++) {
            System.out.println(sd.sortedlist.get(i).value + " - " + sd.sortedlist.get(i).count);
        }
        
    }
    
}

class AllWords {
    
    String[] tokens = null;
    
    public AllWords (String path) {
        
        String content = null;		
        try(Scanner sc = new Scanner(new File(path));) {
            content = sc.useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            System.out.print("Fail to read file " + path);
        }
        this.tokens = content.toLowerCase().replaceAll("[0-9]","").split("[\\W_]+");
    }
    
    public AllWords () {
        this("pride-and-prejudice.txt");
    }
    
}

class StopWords {
    
    List<String> stoplist = null;
    
    public StopWords () {
        String content_sl = "";
        String[] stopword = null;
        
        try(Scanner sc_sl = new Scanner(new File("..//stop_words.txt"));) {
            content_sl = sc_sl.useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            System.out.print("Fail to read file " + "stop_words.txt");
        }
        
        stopword = content_sl.toLowerCase().split("[\\W]+");
        this.stoplist = new ArrayList<String>(Arrays.asList(stopword));
        stoplist.add("s");
    }
    
}

class UnstopWords {
    
    List<String> unstoplist = new ArrayList<String>();
    
    public UnstopWords (AllWords aw, StopWords sw) {
        
        for(String w : aw.tokens) {
            if (sw.stoplist.indexOf(w)==-1)
                this.unstoplist.add(w);
        }
        
    }
    
}

class Unique {
    
    List<String> uniquelist = new ArrayList<String>();
    
    public Unique (UnstopWords uw) {
        
        for(String w : uw.unstoplist) {
            if (this.uniquelist.indexOf(w)==-1)
                this.uniquelist.add(w);
        }
        
    }
    
}

class Count {
    
    List<Integer> countlist = new ArrayList<Integer>();
    
    public Count (UnstopWords uw, Unique uni) {
        
        for(String w : uni.uniquelist){
            int c = 0;
            for(String word : uw.unstoplist){
                if(word.equals(w))
                    c += 1;
            }
            this.countlist.add(c);
        }
        
    }
    
}

class SortedData {
    
    List<pair> sortedlist = new ArrayList<pair>();
    
    public SortedData (Unique uni, Count c) {
        
        for(int i=0; i<uni.uniquelist.size(); i++){
            sortedlist.add(new pair(uni.uniquelist.get(i), c.countlist.get(i)));
        }
        
        Collections.sort(sortedlist, new Comparator<pair>() {
            public int compare(pair pair1, pair pair2) {
                return pair2.count.compareTo(pair1.count);
            }
        });
    }
    
}

class pair {
    
    String value = "";
    Integer count = 0;
    
    public pair(String value, int count) {
        this.value = value;
        this.count = count;
    }
    
}