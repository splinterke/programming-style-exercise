import java.io.*;
import java.util.*;

public class tantrum {
	
	public static void main(String[] args){
		
	    String[] filepath = new String[1];
	    if (args.length > 0)
	        filepath = args;
	    else {
	        System.out.print("I need a file address!\n");
	        System.exit(1);
	    }
	    
	    TFTheOne monad = new TFTheOne(filepath);
	    ReadFile _r_f = new ReadFile();
	    StopWord _s_w = new StopWord();
	    FrequencyCounter _f_c = new FrequencyCounter();
	    
	    monad.bind(_r_f).bind(_s_w).bind(_f_c).printme();
	}
	
}


class TFTheOne {
	
	public String[] value = null;
	
	public TFTheOne (String[] info) {
		this.value = info;
	}
	
	public TFTheOne bind (dosomething d) {
		this.value = d.act(value);
		return this;
	}
	
	public void printme (){
		for (int i=0;i<value.length;i+=2) {
			System.out.println(value[i] + " - " + value[i+1]);
		}
	}
}


abstract class dosomething {
	abstract String[] act(String[] info);
}


class ReadFile extends dosomething {
	
	public String[] act(String[] info){
		
		String path = info[0];
		String content = "";
		String[] token = null; 
		
		try(Scanner sc = new Scanner(new File(path));) {
		content = sc.useDelimiter("\\Z").next();
		} catch (FileNotFoundException e) {
		System.out.print("Fail to read file " + path + "\n");}

		token = content.toLowerCase().replaceAll("[0-9]","").split("[\\W_]+");
	
		return token;
	}
}


class StopWord extends dosomething {
	
	public String[] act(String[] info){
		
		if (info.length < 1) {
		    System.out.print("There is nothing in the file!\n");
		    System.exit(2);
		}
		
		String content_sl = "";
		String[] stopword = null;

		try(Scanner sc_sl = new Scanner(new File("..//stop_words.txt"));) {
		    	content_sl = sc_sl.useDelimiter("\\Z").next();
		  } catch (FileNotFoundException e) {
		System.out.print("Fail to read file " + "..//stop_words.txt\n");}

		stopword = content_sl.toLowerCase().split("[\\W]+");
		List<String> stoplist = new ArrayList<String>(Arrays.asList(stopword));
		stoplist.add("s");
		
		for(int i=0;i<info.length;i++) {
			if (stoplist.indexOf(info[i])!=-1) {
				info[i]="0";
			}
		}
		
		return info;
	}
}


class FrequencyCounter extends dosomething {
	
	public String[] act(String[] info){
	    
		Map<String,Integer> wordlist = new HashMap<String, Integer>();
		for(int i=0;i<info.length;i++) {
			Integer freq = wordlist.get(info[i]);
			wordlist.put(info[i], (freq!=null)?freq+1:1);
		}
		wordlist.remove("0");
		
		List<String> words = new ArrayList<String>();
		List<Integer> freqs = new ArrayList<Integer>();
		List<Integer> tosort = new ArrayList<Integer>();
		
		if (wordlist.size() < 1) {
		    System.out.print("The file is full of stop words!\n");
		    System.exit(3);
		}

		for (String key : wordlist.keySet()) {
		    words.add(key);
			freqs.add(wordlist.get(key));
			tosort.add(wordlist.get(key));
		}
		Collections.sort(tosort, Collections.reverseOrder());
		
		if (words.size() < 1) {
		    System.out.print("There is too few words in the file!\n");
		    System.exit(4);
		}
		
		String[] toPrint = new String[50];
		for (int j=0;j<25;j++) {
			int target=tosort.get(j);
			int position=freqs.indexOf(target);
			toPrint[j*2]=words.get(position);
			toPrint[j*2+1]=Integer.toString(target);
			freqs.set(position,-1);
		}
		
		return toPrint;
	}
}
