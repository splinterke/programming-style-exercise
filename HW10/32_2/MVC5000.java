import java.io.*;
import java.util.*;

public class MVC5000 {
    
    public static void main(String[] args) {
        Model m = new Model(args[0]);
        View v = new View(m);
        Controller c = new Controller(m,v);
        c.run();
    }
    
}

class Model {
    
    HashMap<String, Integer> word_freqs = new HashMap<String, Integer>();
    List<String> word_space = new ArrayList<String>();
    //"round" is to mark how many "More" as be requested by the user
    int round;
    //"word_length" is to record the length of the current file, and to decide when there is no more word from the file
    int word_length;
    //"overflow" is to mark if all words have been processed. 
    //Since Model can not do IO by itself, the View will check this variable for IO purpose
    boolean overflow;
    
    public Model(String path) {
        update(path);
    }
    
    //when there is a new file comes in, "update" initialize the Model
    public void update(String path) {

		List<String> stoplist = new ArrayList<String>();
		word_space = new ArrayList<String>();
		
		try(Scanner sc_sl = new Scanner(new File("..//stop_words.txt"));) {
            String content_sl = sc_sl.useDelimiter("\\Z").next();
            stoplist = new ArrayList<String>(Arrays.asList(content_sl.toLowerCase().split("[\\W]+")));
            stoplist.add("s");
        } catch (FileNotFoundException e) {
            System.out.print("Fail to read file " + "..//stop_words.txt\n");
        }
		
		try(Scanner sc = new Scanner(new File(path));) {
            String content = sc.useDelimiter("\\Z").next();
            for(String s : content.toLowerCase().replaceAll("[0-9]","").split("[\\W_]+")) {
                if(s.length()>0 && stoplist.indexOf(s)==-1)
                    word_space.add(s);
            }
        } catch (FileNotFoundException e) {
            System.out.print("Fail to read file " + path + "\n");
        }
        
        round = 0;
        word_length = word_space.size();
        overflow = false;
    }
    
    //"readmore" read 5000 words each time. If there is less than 5000 words but ar least 1 word, it runs as well.
    public void readmore() {
        
        int num = 5000;
        
        if(num*round>=word_length) {
            overflow = true;
        }
        else {
            for(int i=num*round; i<num*(round+1); i++) {
                if(i>=word_length) {
                    break;
                }
                String word = word_space.get(i);
                Integer freq = word_freqs.get(word);
                word_freqs.put(word, (freq!=null)?freq+1:1);
            }
            round += 1;
        }
    }
}

class View {
    
    Model m;
    
    public View(Model _m) {
        this.m = _m;
    }
    
    public void render() {
        List<Pair> sortedlist = new ArrayList<Pair>();
        for( String key : m.word_freqs.keySet() ) {
            sortedlist.add(new Pair(key,m.word_freqs.get(key)));
        }
        Collections.sort(sortedlist, new Comparator<Pair>() {
            public int compare(Pair pair1, Pair pair2) {
                return (pair2.count).compareTo(pair1.count);
            }
        });
        for(int i=0;i<25;i++) {
            System.out.println(sortedlist.get(i));
        }
        if(m.overflow==true) {
            System.out.println("Reach the end of the file!");
        }
    }
    
}

class Controller {
    
    Model m;
    View v;
    
    public Controller(Model _m, View _v) {
        this.m = _m;
        this.v = _v;
    }
    
    public void run() {
        boolean flag = true;
        Scanner sc = new Scanner(System.in);
        while(flag) {
            m.readmore();
            v.render();
            System.out.print("More? [y/n]: ");
            String choice = sc.next();
            if(choice.equals("n")) {
                System.out.print("Next file (input N to quit): ");
                String path = sc.next();
                if(path.equals("N")) {
                    System.exit(0);
                }
                m.update(path);
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