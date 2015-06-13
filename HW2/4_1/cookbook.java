import java.io.*;
import java.util.*;

public class cookbook {
	
//global variables start here

  static String pathIn = "";
  static String pathIn_sl = "..//..//stop_words.txt";
  static String[] token = null;
  static String[] stopword = null;
  static Map<String, Integer> wordlist = null;
    
  public static void main(String[] args) {
  
//main functions start here

    pathIn = args[0];
    readfile();
    readstop();
    frequency();
    getrid();
    sortmap();
    
  }
  
//function declararions start here
  
  public static void readfile() {
  	
	  String content = "";
	  token = null;
	    
	  try(Scanner sc = new Scanner(new File(pathIn));) {
	  	content = sc.useDelimiter("\\Z").next();
	  } catch (FileNotFoundException e) {}

	  token = content.toLowerCase().replaceAll("[0-9]","").split("[\\W_]+");
  }
  
  
  public static void readstop() {
	  String content_sl = "";
	  stopword = null;
	    
	  try(Scanner sc_sl = new Scanner(new File(pathIn_sl));) {
	  	content_sl = sc_sl.useDelimiter("\\Z").next();
	  } catch (FileNotFoundException e) {}
	  stopword = content_sl.toLowerCase().split("[\\W]+");
  }
  
  
  public static void frequency() {
	  wordlist = new HashMap<String, Integer>();
	  for(int i=0;i<token.length;i++) {
		  Integer freq = wordlist.get(token[i]);
		  wordlist.put(token[i], (freq!=null)?freq+1:1);		  
	  }
  }
  
  public static void getrid() {
	  for(int i=0;i<stopword.length;i++) {
		  wordlist.remove(stopword[i]);
	  }
	  wordlist.remove("s");
  }
  
  
  public static void sortmap() {
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
