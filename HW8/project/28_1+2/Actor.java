import java.util.concurrent.*;
import java.io.*;
import java.util.*;

public class Actor {
    
    public static void main(String[] args) {
        
        WordFrequencyManager wfm = new WordFrequencyManager();
        StopWordManager swm = new StopWordManager(wfm);
        DataStorageManager dsm = new DataStorageManager(args, swm);
        
        //instead of creating a WFcontroller, give message directly to one actor
        dsm.queue.add(new message("process_word", ""));
        
    }
    
}

//the prototyoe of actor, include a thread-safe queue
class ActiveWFObject implements Runnable {
    
    ConcurrentLinkedQueue<message> queue = new  ConcurrentLinkedQueue<message>();
    boolean flag = true;
    
    public void run() {
        while(flag) {
            if(!queue.isEmpty()) {
                message m = queue.poll();
                dispatch(m);
            }
            else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //System.out.println(this.getClass().getName() + ": Finally Stopped");
    }
    
    public void dispatch(message m) {
        if (m.key.equals("die")) {
            flag = false;
        }
    }
    
    public void sendMessage(message m, ActiveWFObject ao) {
        ao.queue.add(m);
    }
}

class DataStorageManager extends ActiveWFObject {
    
    String _data = "";
    StopWordManager swm = null;
    
    //when construct, read the file and start a thread
    public DataStorageManager(String[] args, StopWordManager swm) {
        
        this.swm = swm;
        try(Scanner sc = new Scanner(new File(args[0]));) {
            this._data = sc.useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            System.out.print("Fail to read file " + args[0] + "\n");
        }
        (new Thread(this)).start();
        
    }
    
    //when receive message "process_word", start sending word to StopWordManager
    public void dispatch(message m) {
        super.dispatch(m);
        if (m.key.equals("process_word")) {
            String[] tokens = _data.toLowerCase().replaceAll("[0-9]","").split("[\\W_]+");
            for (String t : tokens) {
                sendMessage(new message("filter", t), swm);
            }
            //after all word is done, send message "top25" and suiside
            sendMessage(new message("top25", ""), swm);
            sendMessage(new message("die", ""), this);
        }
    }
    
}

class StopWordManager extends ActiveWFObject {
    
    List<String> stoplist = null;
    WordFrequencyManager wfm = null;
    
    public StopWordManager(WordFrequencyManager wfm) {
        
        this.wfm = wfm;
        String content_sl = "";
        String[] stopword = null;
        
        try(Scanner sc_sl = new Scanner(new File("..//stop_words.txt"));) {
            content_sl = sc_sl.useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            System.out.print("Fail to read file " + "..//stop_words.txt\n");
        }
        
        stopword = content_sl.toLowerCase().split("[\\W]+");
        stoplist = new ArrayList<String>(Arrays.asList(stopword));
        stoplist.add("s");
        
        (new Thread(this)).start();
        
    }
    
    //if the message is "filter", check if the word is stop-word and send to WordFrequencyManager
    public void dispatch(message m) {
        super.dispatch(m);
        if (m.key.equals("filter")) {
            String word = (String) m.value;
            if (stoplist.indexOf(word)==-1) {
                sendMessage(new message("word", word), wfm);
            }
        }
        //if the message is not "filter", pass the message to WordFrequencyManager and suiside
        else {
            sendMessage(m, wfm);
            sendMessage(new message("die", ""), this);
        }
    }
    
}

class WordFrequencyManager extends ActiveWFObject {
    
    Map<String,Integer> wordlist = new HashMap<String, Integer>();
    
    public WordFrequencyManager() {
        (new Thread(this)).start();
    }
    
    public void dispatch(message m) {
        super.dispatch(m);
        if (m.key.equals("word")) {
            String word = (String) m.value;
            Integer freq = wordlist.get(word);
			wordlist.put(word, (freq!=null)?freq+1:1);
        }
        if (m.key.equals("top25")) {
            List<message> sortedlist = new ArrayList<message>();
            for( String key : wordlist.keySet() ) {
                sortedlist.add(new message(key,wordlist.get(key)));
            }
            Collections.sort(sortedlist, new Comparator<message>() {
                public int compare(message pair1, message pair2) {
                    return ((Integer)(pair2.value)).compareTo((Integer)(pair1.value));
                }
            });
            for (int i=0; i<25; i++) {
            System.out.println(sortedlist.get(i).key + " - " + sortedlist.get(i).value);
            }
            sendMessage(new message("die", ""), this);
        }
    }
    
}

class message {
    public String key;
    public Object value;
    public message(String k, Object v) {
        this.key = k;
        this.value = v;
    }
}