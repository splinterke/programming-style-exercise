import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class Frequencies2 implements TFFreqs{
	
	public List<pair> top25(String[] tokens){
		
		String content_sl = "";
		String[] stopword = null;

		try(Scanner sc_sl = new Scanner(new File("..//stop_words.txt"));) {
		    	content_sl = sc_sl.useDelimiter("\\Z").next();
		  } catch (FileNotFoundException e) {
		System.out.print("Fail to read file " + "stop_words.txt");}

		stopword = content_sl.toLowerCase().split("[\\W]+");
		List<String> stoplist = new ArrayList<String>(Arrays.asList(stopword));
		stoplist.add("s");
		
		Map<String,Integer> wordlist = new HashMap<String, Integer>();
		List<pair> sortedlist = new ArrayList<pair>();
		for(int i=0;i<tokens.length;i++) {
			Integer freq = wordlist.get(tokens[i]);
			if(stoplist.indexOf(tokens[i])==-1)
				wordlist.put(tokens[i], (freq!=null)?freq+1:1);
		}
		
		for( String key : wordlist.keySet() ) {
            sortedlist.add(new pair(key,wordlist.get(key)));
        }
        Collections.sort(sortedlist, new Comparator<pair>() {
            public int compare(pair pair1, pair pair2) {
                return pair2.count.compareTo(pair1.count);
            }
        });
        
		return sortedlist.subList(0, 25);
	}

}
