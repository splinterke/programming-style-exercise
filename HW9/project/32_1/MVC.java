import java.io.*;
import java.util.*;

public class MVC {
    
    public static void main(String[] args) {
        Model m = new Model(args[0]);
        View v = new View(m);
        Controller c = new Controller(m,v);
        c.run();
    }
    
}

class Model {
    
    HashMap<String, Integer> word_freqs = new HashMap<String, Integer>();
    
    public Model(String path) {
        update(path);
    }
    
    public void update(String path) {
        List<String> word_space = new ArrayList<String>();
		List<String> stoplist = new ArrayList<String>();
		
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
		
		for(String word : word_space) {
			if(stoplist.indexOf(word)==-1) {
	            Integer freq = word_freqs.get(word);
	            word_freqs.put(word, (freq!=null)?freq+1:1);
	        }
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
        v.render();
        while(flag) {
            System.out.print("Next file (input N to quit): ");
            String path = sc.next();
            if(path.equals("N")) {
                System.exit(0);
            }
            m.update(path);
            v.render();
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