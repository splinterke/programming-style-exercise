import java.io.*;
import java.util.*;

public class River {
    
    public static void main(String[] args) {
        
        Char_iterator ci = new Char_iterator(args[0]);
        Word_iterator wi = new Word_iterator(ci);
        Frequency fq = new Frequency(wi);
        fq.count();
        fq.sort();
        for (int i=0; i<25; i++) {
            System.out.println(fq.sortedlist.get(i).value + " - " + fq.sortedlist.get(i).count);
        }
    }
    
}

class Char_iterator {
    
    String path = "";
    String[] lines = null;
    int current = 0;
    
    public Char_iterator(String path) {
        
        String content = "";
        try(Scanner sc = new Scanner(new File(path));) {
            content = sc.useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            System.out.print("Fail to read file " + path);
        }
        this.lines = content.split("\n");
        
    }
    
    public boolean hasNext() {
        if(current >= lines.length)
            return false;
        else 
            return true;
    }
    
    public String next() {
        String result = lines[current];
        current += 1;
        return result;
    }
    
}

class Word_iterator {
    
    Char_iterator _c_i = null;
    List<String> stoplist = null;
    boolean hasNext = true;
    String[] line = {};
    int current = 0; 
    
    public Word_iterator(Char_iterator ci) {
        this._c_i = ci;
        
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
    
    public String next() {
        if(_c_i.hasNext()==false) {
            hasNext = false;
            return "";
        }
        else if(current >= line.length) {
            line = _c_i.next().toLowerCase().replaceAll("[0-9]","").split("[\\W_]+");
            current = 0;
            return "";
        }
        else {
            String result = line[current];
            current += 1;
            if ( stoplist.indexOf(result)==-1 ) {
                return result;
            }
            else
                return "";
        }
    }
    
    public boolean hasNext() {
        return hasNext;
    }
    
}

class Frequency {
    
    Word_iterator _w_i = null;
    Map<String,Integer> wordlist = new HashMap<String, Integer>();
    List<pair> sortedlist = new ArrayList<pair>();
    
    public Frequency(Word_iterator wi) {
        this._w_i = wi;
    }
    
    public void count() {
        while(_w_i.hasNext()) {
            String word = _w_i.next();
            if (word.length() > 0) {
                Integer freq = wordlist.get(word);
			    wordlist.put(word, (freq!=null)?freq+1:1);
            }
        }
    }
    
    public void sort() {
        for( String key : wordlist.keySet() ) {
            sortedlist.add(new pair(key,wordlist.get(key)));
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