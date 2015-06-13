import java.io.*;
import java.util.*;

public class Hollywood {
	
	public static void main(String[] args){
		
		FrameWork fw = new FrameWork();
		ReadFile rf = new ReadFile(fw);
		StopWord sw = new StopWord(fw,rf);
		FrequencyCounter fc = new FrequencyCounter(fw,rf);
		fw.run(args[0]);
		
	}
	
}


class FrameWork {
	
	List<dosomething> _load_event_handler = new ArrayList<dosomething>();
	List<dosomething> _dowork_event_handler = new ArrayList<dosomething>();
	List<dosomething> _print_event_handler = new ArrayList<dosomething>();
	
	public void register_for_load (dosomething d){
		_load_event_handler.add(d);
	}
	
	public void register_for_dowork (dosomething d){
		_dowork_event_handler.add(d);
	}
	
	public void register_for_print (dosomething d){
		_print_event_handler.add(d);
	}
	
	public void run(String path){
	    for (dosomething d : _load_event_handler){
	        d.act(path);
	    }
	    for (dosomething d : _dowork_event_handler){
	        d.act(path);
	    }
	    for (dosomething d : _print_event_handler){
	        d.act(path);
	    }
	}

}


class dosomething {
	public void act(String information){}
}


class ReadFile extends dosomething {
    
    String[] token = {}; 
	
	ReadFile(FrameWork fw) {
		fw.register_for_load(this);
	}
	
	public void act(String information){
		String path = information;
		String content = "";
 
	    try(Scanner sc = new Scanner(new File(path));) {
	    	content = sc.useDelimiter("\\Z").next();
	    } catch (FileNotFoundException e) {}

	    token = content.toLowerCase().replaceAll("[0-9]","").split("[\\W_]+");
	}
}


class StopWord extends dosomething {
    FrameWork _f_w = new FrameWork();
    ReadFile _r_f = new ReadFile(_f_w);
	
	StopWord(FrameWork fw, ReadFile rf) {
		fw.register_for_dowork(this);
		_r_f = rf;
	}

	public void act(String information){
	  	String[] token = _r_f.token;
	  	String content_sl = "";
	  	String[] stopword = null;

	  	try(Scanner sc_sl = new Scanner(new File("..//stop_words.txt"));) {
		    	content_sl = sc_sl.useDelimiter("\\Z").next();
		  } catch (FileNotFoundException e) {}

		stopword = content_sl.toLowerCase().split("[\\W]+");
		List<String> stoplist = new ArrayList<String>(Arrays.asList(stopword));
		stoplist.add("s");
		
		for(int i=0;i<token.length;i++) {
			if (stoplist.indexOf(token[i])!=-1) {
				token[i]="0";
			}
		}
		
	}
}


class FrequencyCounter extends dosomething {
	FrameWork _f_w = new FrameWork();
    ReadFile _r_f = new ReadFile(_f_w);

	FrequencyCounter(FrameWork fw, ReadFile rf) {
		fw.register_for_print(this);
		_r_f = rf;
	}
	
	public void act(String information){
		String[] token = _r_f.token;
		Map<String,Integer> wordlist = new HashMap<String, Integer>();
		for(int i=0;i<token.length;i++) {
			Integer freq = wordlist.get(token[i]);
			wordlist.put(token[i], (freq!=null)?freq+1:1);
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

	}
}
