import java.io.*;
import java.util.*;

public class golf {

  public static void main(String[] args) {

    String[] content = null;
    List<String> stoplist = new ArrayList<String>();
    Map<String,Integer> wordlist = new HashMap<String, Integer>();
    List<String> words = new ArrayList<String>();
    List<Integer> freqs = new ArrayList<Integer>();
    List<Integer> tosort = new ArrayList<Integer>();

// Line 1: read pride-and-prejudice into String[]
    try(Scanner sc = new Scanner(new File(args[0]));) {content = sc.useDelimiter("\\Z").next().toLowerCase().replaceAll("[0-9]","").split("[\\W_]+");} catch (FileNotFoundException e) {}

// Line 2: read stop_words into String[]
    try(Scanner sc = new Scanner(new File("..//..//stop_words.txt"));) {stoplist = Arrays.asList((sc.useDelimiter("\\Z").next() + "s").toLowerCase().split("[\\W]+"));} catch (FileNotFoundException e) {}

// Line 3: compare with stop_words, put into a HushMap
    for(int i=0;i<content.length;i++) { if(stoplist.indexOf(content[i])==-1) { wordlist.put(content[i], (wordlist.get(content[i])!=null)?wordlist.get(content[i])+1:1);} }
    
// Line 4: output HushMap into Lists for sorting and searching
    for (String key : wordlist.keySet()) {words.add(key); freqs.add(wordlist.get(key)); tosort.add(wordlist.get(key));}
    
// Line 5: sort a list for location
    Collections.sort(tosort, Collections.reverseOrder());
    
// Line 6: use location to output results
    for(int j=0;j<25;j++) {int target=tosort.get(j); int position=freqs.indexOf(target); System.out.println(words.get(position) + " - " + target);}

  }
}
