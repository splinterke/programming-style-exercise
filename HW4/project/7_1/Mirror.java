import java.io.*;
import java.util.*;

public class Mirror {

  //Set the limit of recursion
  public static int limit = 1000;
  
  public static String[] content = null;
  public static List<String> stoplist = new ArrayList<String>();
  public static Map<String,Integer> wordlist = new HashMap<String, Integer>();
  
  public static List<String> words = new ArrayList<String>();
  public static List<Integer> freqs = new ArrayList<Integer>();
  public static List<Integer> tosort = new ArrayList<Integer>();
  
  public static void main(String[] args) {

    try(Scanner sc = new Scanner(new File(args[0]));) {
        content = sc.useDelimiter("\\Z").next().toLowerCase().replaceAll("[0-9]","").split("[\\W_]+");
    } catch (FileNotFoundException e) {}

    try(Scanner sc = new Scanner(new File("..//stop_words.txt"));) {
        stoplist = Arrays.asList((sc.useDelimiter("\\Z").next() + "s").toLowerCase().split("[\\W]+"));
    } catch (FileNotFoundException e) {}

    //Use induction to count frequency
    for (int i=0; i<content.length-limit;i+=limit) {
        frequency(Arrays.copyOfRange(content,i,i+limit));
    }
    int tail = ( content.length / limit ) * limit;
    frequency(Arrays.copyOfRange(content,tail,content.length));
    
    
    for (String key : wordlist.keySet()) {
        words.add(key); 
        freqs.add(wordlist.get(key)); 
        tosort.add(wordlist.get(key));
    }
    Collections.sort(tosort, Collections.reverseOrder());
    
    //Use induction to print result
    printtop(25);

  }


  public static void frequency(String[] content) {
      if (content.length<1)
          return;
      if (content.length==1) {
          if (stoplist.indexOf(content[0])==-1)
            wordlist.put(content[0], (wordlist.get(content[0])!=null)?wordlist.get(content[0])+1:1);
      }
      else {
          frequency(Arrays.copyOfRange(content,0,1));
          frequency(Arrays.copyOfRange(content,1,content.length));
      }
  }
  
  public static void printtop(int top) {
      if (top<1)
        return;
      if (top==1) {
          int target=tosort.get(0); 
          int position=freqs.indexOf(target);
          System.out.println(words.get(position) + " - " + target);
          freqs.set(position,-1);
      }
      else {
          printtop(1);
          tosort.remove(0);
          printtop(top-1);
      }
  }
  
}
