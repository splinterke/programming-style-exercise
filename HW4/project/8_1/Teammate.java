import java.io.*;
import java.util.*;

public class Teammate {
	
	public static void main(String[] args){
	    FrequencyCounter _f_c = new FrequencyCounter();
	    StopWord _s_w = new StopWord(_f_c);
	    ReadFile _r_f = new ReadFile(_s_w);
	    _r_f.act(args);
	}
	
}


class dosomething {
	public void act(String[] info){}
}


class ReadFile extends dosomething {

	dosomething _d_s = new dosomething();
	
	ReadFile(dosomething ds) {
		_d_s = ds;
	}
	
	public void act(String[] info){
		
		String path = info[0];
		String content = "";
	    String[] token = null;  

	    try(Scanner sc = new Scanner(new File(path));) {
	    	content = sc.useDelimiter("\\Z").next();
	    } catch (FileNotFoundException e) {}

	    token = content.toLowerCase().replaceAll("[0-9]","").split("[\\W_]+");
	    
	    _d_s.act(token);
	}
}


class StopWord extends dosomething {

	dosomething _d_s = new dosomething();
	
	StopWord(dosomething ds) {
		_d_s = ds;
	}
	
	public void act(String[] info){
	  	String content_sl = "";
	  	String[] stopword = null;

	  	try(Scanner sc_sl = new Scanner(new File("..//stop_words.txt"));) {
		    	content_sl = sc_sl.useDelimiter("\\Z").next();
		  } catch (FileNotFoundException e) {}

		stopword = content_sl.toLowerCase().split("[\\W]+");
		List<String> stoplist = new ArrayList<String>(Arrays.asList(stopword));
		stoplist.add("s");
		
		for(int i=0;i<info.length;i++) {
			if (stoplist.indexOf(info[i])!=-1) {
				info[i]="0";
			}
		}
		
		_d_s.act(info);
	}
}


class FrequencyCounter extends dosomething {
	
	public void act(String[] info){
		Map<String,Integer> wordlist = new HashMap<String, Integer>();
		for(int i=0;i<info.length;i++) {
			Integer freq = wordlist.get(info[i]);
			wordlist.put(info[i], (freq!=null)?freq+1:1);
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
