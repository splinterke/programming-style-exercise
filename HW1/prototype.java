import java.io.*;
import java.util.*;
import java.net.*;

public class prototype {
  public static void main(String[] args) {
  
    System.out.println("Start");
    String pathIn = "..//pride-and-prejudice.txt";
    String pathIn_sl = "..//stop_words.txt";
    String[] token = null;
    String[] stopword = null;
    Map<String, Integer> wordlist = null;
    List<pair> sorted = null;
    
    token = readfile(pathIn);
    stopword = readfile(pathIn_sl);
    
    wordlist = frequency(token);
    wordlist = getrid(stopword,wordlist);
    
    sorted = sortmap(wordlist);
    
    System.out.println(sorted.subList(0,25));
    
    System.out.println("End.");  
    
  }
  
  
  public static String[] readfile(String path) {
	  
	    String content = "";
	    String[] token = null;  

	    try(Scanner sc = new Scanner(new File(path));) {
	    	content = sc.useDelimiter("\\Z").next();
	    } catch (FileNotFoundException e) {}

	    token = content.toLowerCase().replaceAll("[0-9]","").split("[\\W_]+");
	    
	    return token;
	    
  }
  
  
  public static Map<String, Integer> frequency(String[] token) {
	  
	  Map<String, Integer> wordlist = new HashMap<String, Integer>();
	  
	  for(int i=0;i<token.length;i++) {
		  Integer freq = wordlist.get(token[i]);
		  wordlist.put(token[i], (freq!=null)?freq+1:1);		  
	  }
	  
	  return wordlist;
	  
  }
  
  public static Map<String, Integer> getrid(String[] stopword, Map<String, Integer> wordlist) {
	  
	  for(int i=0;i<stopword.length;i++) {
		  wordlist.remove(stopword[i]);
	  }
	  wordlist.remove("s");
	  return wordlist;
	  
  }
  
  public static class pair {
	  String word;
	  Integer freq;
      public pair(String word, Integer freq) {
	    this.word = word;
	    this.freq = freq;
      }
      
      @Override
      public String toString() {
    	  return (this.word + "---" + this.freq);
      }
  }
  
  public static List<pair> sortmap(Map<String, Integer> wordlist) {
	  
	  List<pair> sorted = new ArrayList<pair>();
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
	  //System.out.println(Arrays.toString(sortedfreqs));
	  for (int j=0;j<n;j++) {
		  int target = sortedfreqs[n-1-j];
		  int position = freqs.indexOf(target);
		  //System.out.println(target);
		  //System.out.println(position);
		  sorted.add(new pair(words[position],target));
		  freqs.set(position,-1);
	  }
	  
	  return sorted;
	  
  } 
  

}
