import java.io.*;
import java.util.*;

public class MapReduce {
    
    public static void main(String[] args) {
        
        Word_Parser wp = new Word_Parser();
        
        //use mapping to parse the words
        mapping(wp, readfile(args[0], 200));
        
        Reducer[] r = new Reducer[5];
        for(int i=0; i<5; i++) {
            r[i] = new Reducer();
        }
        
        //send words to different Reducer based on first letter's alphabeta order
        suffling(wp, r);
        
        //start the reducing process, concurrently
        Thread[] t = new Thread[5];
        for(int i=0; i<5; i++) {
            t[i] = new Thread(r[i]);
            t[i].start();
        }
        
        for(int i=0;i<5;i++) {
            try {
            t[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        //merge result from different Reducer
        merging(r);
    }
    
    public static List<String[]> readfile(String path, int line_num) {
        
        List<String[]> paragraphs = new ArrayList<String[]>();
        try(Scanner sc = new Scanner(new File(path));) {
            String[] lines = sc.useDelimiter("\\Z").next().split("\n");
            
            int paragraph_num = lines.length / line_num;
            for(int i=0; i<paragraph_num; i++) {
                String[] paragraph = Arrays.copyOfRange(lines, i*line_num, i*line_num+line_num);
                paragraphs.add(paragraph);
            }
            
            if(lines.length > paragraph_num * line_num) {
                String[] paragraph = Arrays.copyOfRange(lines, paragraph_num * line_num, lines.length);
                paragraphs.add(paragraph);
            }
            
        } catch (FileNotFoundException e) {
            System.out.print("Fail to read file " + path + "\n");
        }
        
        return paragraphs;
    }
    
    public static void mapping(Word_Parser wp, List<String[]> paragraphs) {
        for(String[] p : paragraphs) {
            wp.parseWord(p);
        }
        //System.out.print(wp.simple_counts);
    }
    
    public static void suffling(Word_Parser wp, Reducer[] r) {
        for(Pair p : wp.simple_counts) {
            if(p.value.charAt(0)>='a' && p.value.charAt(0)<='e') {
                r[0].list.add(p);
            }
            if(p.value.charAt(0)>='f' && p.value.charAt(0)<='j') {
                r[1].list.add(p);
            }
            if(p.value.charAt(0)>='k' && p.value.charAt(0)<='o') {
                r[2].list.add(p);
            }
            if(p.value.charAt(0)>='p' && p.value.charAt(0)<='t') {
                r[3].list.add(p);
            }
            if(p.value.charAt(0)>='u' && p.value.charAt(0)<='z') {
                r[4].list.add(p);
            }
        }
    }
    
    public static void merging(Reducer[] r) {
        
        List<Pair> sortedlist = new ArrayList<Pair>();
        
        for(Reducer _r : r) {
            for( String key : _r.freq_each.keySet() ) {
                sortedlist.add(new Pair(key,_r.freq_each.get(key)));
            }
            
            Collections.sort(sortedlist, new Comparator<Pair>() {
                public int compare(Pair pair1, Pair pair2) {
                    return (pair2.count).compareTo(pair1.count);
                }
            });
        }
        
        for (int i=0; i<25; i++) {
            System.out.println(sortedlist.get(i));
        }
    }
    
}

class Word_Parser {
    
    List<Pair> simple_counts = new ArrayList<Pair>();
    List<String> stoplist = new ArrayList<String>();
    
    public Word_Parser() {
        try(Scanner sc_sl = new Scanner(new File("..//stop_words.txt"));) {
            String content_sl = sc_sl.useDelimiter("\\Z").next();
            stoplist = new ArrayList<String>(Arrays.asList(content_sl.toLowerCase().split("[\\W]+")));
            stoplist.add("s");
        } catch (FileNotFoundException e) {
            System.out.print("Fail to read file " + "..//stop_words.txt\n");
        }
    }
    
    public void parseWord(String[] lines) {
        for(String l : lines) {
            String[] tokens = l.toLowerCase().replaceAll("[0-9]","").split("[\\W_]+");
            for (String t : tokens) {
                if(stoplist.indexOf(t)==-1 && t.length()>0) {
                    simple_counts.add(new Pair(t, 1));
                }
            }
        }
    }
    
}

class Reducer implements Runnable{
    
    List<Pair> list = new ArrayList<Pair>();
    HashMap<String, Integer> freq_each = new HashMap<String, Integer>();
    
    public void run() {
        for(Pair p : list) {
            String word = p.value;
            Integer freq = freq_each.get(word);
            freq_each.put(word, (freq!=null)?freq+1:1);
        }
    }
    
}

class Pair {
    public String value;
    public Integer count;
    
    public Pair(String value, int count) {
        this.value = value;
        this.count = count;
    }
    
    @Override
    public String toString() {
        return (this.value + " - " + this.count);
    }
}