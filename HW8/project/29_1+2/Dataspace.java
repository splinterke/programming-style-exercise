import java.util.concurrent.*;
import java.io.*;
import java.util.*;

public class Dataspace {
    
    //word_space is used to store all raw words
    public static ConcurrentLinkedQueue<String> word_space = new ConcurrentLinkedQueue<String>();
    
    //freq_space is used to store freqs result from individual Word_Worker
    public static ConcurrentLinkedQueue<HashMap<String, Integer>> freq_space = new ConcurrentLinkedQueue<HashMap<String, Integer>>();
    
    //word_freqs is used to store all-in-one result of freqs
    public static ConcurrentHashMap<String, Integer> word_freqs = new ConcurrentHashMap<String, Integer>();
    
    //stoplist is for stop words
    public static List<String> stoplist = new ArrayList<String>();
    
    public static void main(String[] args) {
        
        //fill in word_space and stoplist 
        readfile(args[0]);
        
        //create 5 Word_Worker and start thread
        Word_Worker[] ww = new Word_Worker[5];
        for(int i=0;i<5;i++) {
            ww[i] = new Word_Worker();
            ww[i].t.start();
        }
        for(int i=0;i<5;i++) {
            try {
            ww[i].t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        //create 5 Freq_Worker, divide task by first letter's alphabeta order
        Freq_Worker[] fw = new Freq_Worker[5];
        fw[0] = new Freq_Worker('a', 'e');
        fw[1] = new Freq_Worker('f', 'j');
        fw[2] = new Freq_Worker('k', 'o');
        fw[3] = new Freq_Worker('p', 't');
        fw[4] = new Freq_Worker('u', 'z');
        
        //start thread of Freq_Worker
        for(int i=0;i<5;i++) {
            fw[i].tt.start();
        }
        for(int i=0;i<5;i++) {
            try {
            fw[i].tt.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        //print out result
        print25();
    }
    
    public static void readfile(String path) {
        
        try(Scanner sc = new Scanner(new File(path));) {
            String content = sc.useDelimiter("\\Z").next();
            for(String s : content.toLowerCase().replaceAll("[0-9]","").split("[\\W_]+")) {
                if(s.length()>0)
                    word_space.add(s);
            }
        } catch (FileNotFoundException e) {
            System.out.print("Fail to read file " + path + "\n");
        }
        
        try(Scanner sc_sl = new Scanner(new File("..//stop_words.txt"));) {
            String content_sl = sc_sl.useDelimiter("\\Z").next();
            stoplist = new ArrayList<String>(Arrays.asList(content_sl.toLowerCase().split("[\\W]+")));
            stoplist.add("s");
        } catch (FileNotFoundException e) {
            System.out.print("Fail to read file " + "..//stop_words.txt\n");
        }
    }
    
    public static void print25() {
        List<Pair> sortedlist = new ArrayList<Pair>();
        for( String key : word_freqs.keySet() ) {
            sortedlist.add(new Pair(key,word_freqs.get(key)));
        }
        Collections.sort(sortedlist, new Comparator<Pair>() {
            public int compare(Pair pair1, Pair pair2) {
                return (pair2.count).compareTo(pair1.count);
            }
        });
        for (int i=0; i<25; i++) {
            System.out.println(sortedlist.get(i));
        }
    }
    
}

class Word_Worker implements Runnable {
    
    List<String> stoplist = Dataspace.stoplist;
    ConcurrentLinkedQueue<String> word_space = Dataspace.word_space;
    ConcurrentLinkedQueue<HashMap<String, Integer>> freq_space = Dataspace.freq_space;
    HashMap<String, Integer> freq_each = new HashMap<String, Integer>();
    Thread t = new Thread(this);
    
    public void run() {
        boolean flag = true;
        while(flag) {
            String word = word_space.poll();
            if(word == null) {
                flag = false;
            }
            else if(stoplist.indexOf(word)==-1) {
                Integer freq = freq_each.get(word);
                freq_each.put(word, (freq!=null)?freq+1:1);
            }
        }
        freq_space.add(freq_each);
    }
    
}

class Freq_Worker implements Runnable {
    
    ConcurrentHashMap<String, Integer> word_freqs = Dataspace.word_freqs;
    ConcurrentLinkedQueue<HashMap<String, Integer>> freq_space = Dataspace.freq_space;
    char charfrom;
    char charto;
    Thread tt = new Thread(this);
    
    public Freq_Worker(char charfrom, char charto) {
        this.charfrom = charfrom;
        this.charto = charto;
    }
    
    public void run() {
        for(HashMap<String, Integer> m : freq_space) {
            HashMap<String, Integer> map = m;
            for(String k : map.keySet()) {
                if(k.charAt(0)>=charfrom && k.charAt(0)<=charto) {
                    Integer freq = word_freqs.get(k);
                    word_freqs.put(k, (freq!=null)?freq+map.get(k):map.get(k));
                }
            }
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