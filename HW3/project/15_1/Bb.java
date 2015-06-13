import java.io.*;
import java.util.*;

public class Bb {
	
	public static void main(String[] args){
		
		EventManager em = new EventManager();
		new WFApplication(em);
		new ReadFile(em);
		new StopWord(em);
		new FrequencyCounter(em);
		em.publish("run", args);
		
	}
	
}


class EventManager {
	List<pair> board = new ArrayList<pair>();
	
	public void subscribe(String name, dosomething d){
		board.add(new pair(name,d));
	}
	
	public void publish(String task, String[] information){
		for (pair p : board){
			if(p.message.equals(task)){
				p.task.act(task, information);
			}
		}
	}
}


class dosomething {
	public void act(String task, String[] information){}
}


class ReadFile extends dosomething {
	EventManager _e_m = new EventManager();
	
	ReadFile(EventManager em) {
		em.subscribe("read",this);
		_e_m = em;
	}
	
	public void act(String task, String[] information){
		String path = information[0];
		
		String content = "";
	    String[] token = null;  

	    try(Scanner sc = new Scanner(new File(path));) {
	    	content = sc.useDelimiter("\\Z").next();
	    } catch (FileNotFoundException e) {}

	    token = content.toLowerCase().replaceAll("[0-9]","").split("[\\W_]+");
	    _e_m.publish("clean", token);
	}
}


class StopWord extends dosomething {
	EventManager _e_m = new EventManager();
	
	StopWord(EventManager em) {
		em.subscribe("clean",this);
		_e_m = em;
	}
	
	public void act(String task, String[] information){
	  	String content_sl = "";
	  	String[] stopword = null;

	  	try(Scanner sc_sl = new Scanner(new File("..//stop_words.txt"));) {
		    	content_sl = sc_sl.useDelimiter("\\Z").next();
		  } catch (FileNotFoundException e) {}

		stopword = content_sl.toLowerCase().split("[\\W]+");
		List<String> stoplist = new ArrayList<String>(Arrays.asList(stopword));
		stoplist.add("s");
		
		for(int i=0;i<information.length;i++) {
			if (stoplist.indexOf(information[i])!=-1) {
				information[i]="0";
			}
		}
		_e_m.publish("count", information);
		
	}
}


class FrequencyCounter extends dosomething {
	EventManager _e_m = new EventManager();
	
	FrequencyCounter(EventManager em) {
		em.subscribe("count",this);
		_e_m = em;
	}
	
	public void act(String task, String[] information){
		Map<String,Integer> wordlist = new HashMap<String, Integer>();
		for(int i=0;i<information.length;i++) {
			Integer freq = wordlist.get(information[i]);
			wordlist.put(information[i], (freq!=null)?freq+1:1);
		}
		wordlist.remove("0");
		
		List<String> words = new ArrayList<String>();
		List<Integer> freqs = new ArrayList<Integer>();
		List<Integer> tosort = new ArrayList<Integer>();

		for (String key : wordlist.keySet()) {
		    words.add(key);
			freqs.add(wordlist.get(key));
			tosort.add(wordlist.get(key));
		}

		Collections.sort(tosort, Collections.reverseOrder());
		for (int j=0;j<25;j++) {
			int target=tosort.get(j);
			int position=freqs.indexOf(target);
			System.out.println(words.get(position) + " - " + target);
			freqs.set(position,-1);
		}
		
		_e_m.publish("end", null);
	}
}


class WFApplication extends dosomething {
	EventManager _e_m = new EventManager();
	
	WFApplication(EventManager em) {
		em.subscribe("run",this);
		em.subscribe("end",this);
		_e_m = em;
	}

	public void act(String task, String[] information){
		if (task.equals("run")){
			System.out.println("Start");
			_e_m.publish("read", information);
		}
		else if (task.equals("end")){
			System.out.println("End");
		}
	}
}


class pair {
	String message;
	dosomething task;
	public pair(String message, dosomething task) {
		  this.message = message;
		  this.task = task;
	}
}
