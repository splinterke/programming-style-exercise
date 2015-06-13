import java.io.*;
import java.net.*;
import java.util.*;


public class Noc {
	
	String JarLoca;
	String TFWords_name;
	String TFFreqs_name;
	String TFPrint_name;
	
	public static void main(String[] args) {
	    TFWords tfwords = null;
		TFFreqs tffreqs = null;
		TFPrint tfprint = null;
		Scanner sc = new Scanner(System.in);
		int option;
		do {

			System.out.println();
			System.out.println("Menu:");
			System.out.println("1.- Run Term Frequency with Combo1");
			System.out.println("2.- Run Term Frequency with Combo2");
			System.out.println("3.- Exit");
			System.out.print("Option: ");
			option = sc.nextInt();
			switch (option) {
			case 1:
			    tfwords = new Words1();
			    tffreqs = new Frequencies1();
			    tfprint = new Printit1();
				tfprint.show(tffreqs.top25(tfwords.extract_words(args[0])));
				break;
			case 2:
			    tfwords = new Words2();
			    tffreqs = new Frequencies2();
			    tfprint = new Printit2();
				tfprint.show(tffreqs.top25(tfwords.extract_words(args[0])));
				break;
			case 3:
				System.exit(0);
			default:
				System.out.println("Invalid option");
			}
		} while (option != 3);
	}

}

interface TFFreqs {
	public List<pair> top25(String[] tokens);
}

interface TFPrint {
	public void show(List<pair> result);
}

interface TFWords {
	public String[] extract_words(String path);
}

class Words1 implements TFWords{
	
	public String[] extract_words(String path) {
		String content = null;		
        try(Scanner sc = new Scanner(new File(path));) {
            content = sc.useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            System.out.print("Fail to read file " + path);
        }
        String[] tokens = content.toLowerCase().replaceAll("[0-9]","").split("[\\W_]+");
        
        String content_sl = "";
		String[] stopword = null;

		try(Scanner sc_sl = new Scanner(new File("..//stop_words.txt"));) {
		    	content_sl = sc_sl.useDelimiter("\\Z").next();
		  } catch (FileNotFoundException e) {
		System.out.print("Fail to read file " + "stop_words.txt");}

		stopword = content_sl.toLowerCase().split("[\\W]+");
		List<String> stoplist = new ArrayList<String>(Arrays.asList(stopword));
		stoplist.add("s");
		
		for(int i=0;i<tokens.length;i++) {
			if (stoplist.indexOf(tokens[i])!=-1) {
				tokens[i]="0";
			}
		}
               
        return tokens;
	}

}

class Frequencies1 implements TFFreqs{
	
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

class Printit1 implements TFPrint{
	
	public void show(List<pair> result) {
		for(pair p : result) {
			System.out.println(p.value + " - " + p.count);
		}
	}

}

class Words2 implements TFWords{

	public String[] extract_words(String path) {
		String content = null;		
        try(Scanner sc = new Scanner(new File(path));) {
            content = sc.useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            System.out.print("Fail to read file " + path);
        }
        String[] tokens = content.toLowerCase().replaceAll("[0-9]","").split("[\\W_]+");
        return tokens;
	}
}

class Frequencies2 implements TFFreqs{
	
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

class Printit2 implements TFPrint{
	
	public void show(List<pair> result) {
		for(pair p : result)
			System.out.println(p.toString());
	}

}

class pair {
    public String value;
    public Integer count;
    
    public pair(String value, int count) {
        this.value = value;
        this.count = count;
    }
    
    @Override
    public String toString() {
        return (this.value + " -- " + this.count);
    }
}
