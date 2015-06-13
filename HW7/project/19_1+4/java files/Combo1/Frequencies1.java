import java.util.*;


public class Frequencies1 implements TFFreqs{
	
	public List<pair> top25(String[] tokens){
		
	    Map<String,Integer> wordlist = new HashMap<String, Integer>();
	    List<pair> result = new ArrayList<pair>();
		
	    for(int i=0;i<tokens.length;i++) {
			Integer freq = wordlist.get(tokens[i]);
			wordlist.put(tokens[i], (freq!=null)?freq+1:1);
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
	    
	    for(int i=0; i<25; i++) {
	    	int target=tosort.get(i);
			int position=freqs.indexOf(target);
			result.add(new pair(words.get(position),target));
			freqs.set(position,-1);
	    }
	    
	    return result;
	}

}
