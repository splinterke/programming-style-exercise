import java.io.*;
import java.util.*;

public class candy {

  public static void main(String[] args) {

    sortmap(frequency(filetotoken(args)));

  }


  public static String[] filetotoken(String[] input) {

  	String content_sl = "";
  	String[] stopword = null;

  	try(Scanner sc_sl = new Scanner(new File(input[1]));) {
	    	content_sl = sc_sl.useDelimiter("\\Z").next();
	  } catch (FileNotFoundException e) {}

	  stopword = content_sl.toLowerCase().split("[\\W]+");
	  List<String> stoplist = new ArrayList<String>(Arrays.asList(stopword));
	  stoplist.add("s");

	  String content = "";
	  String[] token = null;
	  try(Scanner sc = new Scanner(new File(input[0]));) {
	    	content = sc.useDelimiter("\\Z").next();
	  } catch (FileNotFoundException e) {}

	  token = content.toLowerCase().replaceAll("[0-9]","").split("[\\W_]+");

	  for(int i=0;i<token.length;i++) {
	    if (stoplist.indexOf(token[i])!=-1) {
	    token[i]="0";
	    }
	  }
	  
	  return token;
	  
	}

  public static Map<String,Integer> frequency(String[] token) {
	  Map<String,Integer> wordlist = new HashMap<String, Integer>();
	  for(int i=0;i<token.length;i++) {
	    Integer freq = wordlist.get(token[i]);
	    wordlist.put(token[i], (freq!=null)?freq+1:1);
	  }
	  wordlist.remove("0");
	  return wordlist;
  }

  public static void sortmap(Map<String,Integer> wordlist) {
	  int n = wordlist.size();
	  String[] words = new String[n];
	  List<Integer> freqs = new ArrayList<Integer>();
	  int[] sortedfreqs = new int[n];

    int i=0;
	  for (String key : wordlist.keySet()) {
	    words[i] = key;
	    freqs.add(wordlist.get(key));
	    sortedfreqs[i] = wordlist.get(key);
	    i+=1;
	  }

	  Arrays.sort(sortedfreqs);
	  for (int j=0;j<25;j++) {
	    int target = sortedfreqs[n-1-j];
	    int position = freqs.indexOf(target);
	    System.out.println(words[position] + " - " + target);
	    freqs.set(position,-1);
	  }
  }

}
