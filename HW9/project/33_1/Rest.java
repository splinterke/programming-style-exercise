import java.io.*;
import java.util.*;


public class Rest {

	public static void main(String[] args) {
		Server myServer = new Server();
		Client myClient = new Client();
		
		//use the default command to initialize interface
		Command myCommand = new Command("get", "default", null);
		while(true) {
			myCommand= myClient.clientCore(myServer.serverCore(myCommand));
		}
	}
}

//the class of server, it receives command from client and sends packages, which include
//information to display, and commands the client may send back
class Server {
	
	HashMap<String, List<Pair>> data = new HashMap<String, List<Pair>>();
	
	//since java can not pass by function, the handler is written by a switch
	public Package[] serverCore(Command c) {
		if(c==null) {
			c = new Command("get", "default", null);
		}
		String parsedCommand = c.verb + "_" + c.uri;
		switch (parsedCommand) {
			case "get_default":
				return doDefault();
			case "post_execution":
				return doQuit();
			case "get_file_form":
				return doUpload();
			case "get_word":
				return doFreq((String)c.argument);
			case "get_more_word":
				return doSearch((Pair)c.argument);
		}
		return null;
	}
	
	//this function is to first print an error message, then show the default interface	
	private Package[] doError(String s) {
		Package[] errorPack = new Package[3];
		errorPack[0] = new Package(s, null);
		errorPack[1] = new Package("1. Quit ", new Command("post", "execution", null));
		errorPack[2] = new Package("2. Upload file ", new Command("get", "file_form", null));
		return errorPack;
	}
	
	//this function is to search word in data based on (document,index) information
	private Package[] doSearch(Pair p) {
		String doc = p.value;
		Integer num = p.count;
		if(num>=data.get(doc).size()) {
		    return doError("No more words!");
		}
		Package[] freqPack = new Package[4];
        freqPack[0] = new Package(data.get(doc).get(num).toString(), null);
        freqPack[1] = new Package("1. Quit ", new Command("post", "execution", null));
        freqPack[2] = new Package("2. Upload file ", new Command("get", "file_form", null));
        freqPack[3] = new Package("2. See next most-frequent word ", new Command("get", "more_word", new Pair(doc,num + 1)));
		return freqPack;
	}
	
	//this function is to calculate the frequency, and return the most frequent word
	private Package[] doFreq(String path) {
		
		if(data.keySet().contains(path)) {
			return doError("Error: This file already exists. ");
		}
		
		List<String> word_space = new ArrayList<String>();
		List<String> stoplist = new ArrayList<String>();
		HashMap<String, Integer> word_freqs = new HashMap<String, Integer>();
		
		try(Scanner sc = new Scanner(new File(path));) {
            String content = sc.useDelimiter("\\Z").next();
            for(String s : content.toLowerCase().replaceAll("[0-9]","").split("[\\W_]+")) {
                if(s.length()>0)
                    word_space.add(s);
            }
        } catch (FileNotFoundException e) {
            System.out.print("Fail to read file " + path + "\n");
        }
		
		try(Scanner sc_sl = new Scanner(new File("..//stop_words.txt"));) {
            String content_sl = sc_sl.useDelimiter("\\Z").next();
            stoplist = new ArrayList<String>(Arrays.asList(content_sl.toLowerCase().split("[\\W]+")));
            stoplist.add("s");
        } catch (FileNotFoundException e) {
            System.out.print("Fail to read file " + "..//stop_words.txt\n");
        }
		
		for(String word : word_space) {
			if(stoplist.indexOf(word)==-1) {
	            Integer freq = word_freqs.get(word);
	            word_freqs.put(word, (freq!=null)?freq+1:1);
	        }
		}
		
		List<Pair> sortedlist = new ArrayList<Pair>();
        for( String key : word_freqs.keySet() ) {
            sortedlist.add(new Pair(key,word_freqs.get(key)));
        }
        Collections.sort(sortedlist, new Comparator<Pair>() {
            public int compare(Pair pair1, Pair pair2) {
                return (pair2.count).compareTo(pair1.count);
            }
        });
        
        data.put(path, sortedlist);
        
        Package[] freqPack = new Package[4];
        freqPack[0] = new Package(sortedlist.get(0).toString(), null);
        freqPack[1] = new Package("1. Quit ", new Command("post", "execution", null));
        freqPack[2] = new Package("2. Upload file ", new Command("get", "file_form", null));
        freqPack[3] = new Package("3. See next most-frequent word ", new Command("get", "more_word", new Pair(path,1)));
		return freqPack;
	}
	
	//this function is to show the "file_form"
	private Package[] doUpload() {
		Package[] uploadPack = new Package[1];
		uploadPack[0] = new Package("Name of file to upload? ", new Command("get", "word", null));
		return uploadPack;
	}
	
	//this is the option of quit
	private Package[] doQuit() {
		System.exit(0);
		return null;
	}
	
	//this is the default interaface
	private Package[] doDefault() {
		Package[] defaultPack = new Package[3];
		defaultPack[0] = new Package("", null);
		defaultPack[1] = new Package("1. Quit ", new Command("post", "execution", null));
		defaultPack[2] = new Package("2. Upload file ", new Command("get", "file_form", null));
		return defaultPack;
	}
	
}

//the class of client, it receives package and returns command
class Client {
	
	public Command clientCore(Package[] pack) {	
		Scanner sc = new Scanner(System.in);
		System.out.println("");
		
		//choose the file to upload, append the path to the command
		if(pack[0].view.equals("Name of file to upload? ")) {
			System.out.println(pack[0].view);
			System.out.print("File path: ");
			Command cmd = pack[0].cmd;
			String path = sc.next();
			cmd.argument = path;
			return cmd;
		}
		
		//other situations, no need to append information to the command
		else {
			for(Package p : pack) {
				if(p.view.length()>0) {
					System.out.println(p.view);
				}			
				if(p.cmd == null) {
					System.out.println("What would you like to do:");
				}
			}
			System.out.print("Your choice is: ");
			int choice = sc.nextInt();
			while(choice<=0 || choice>=pack.length) {
				System.out.print("Your choice is: ");
				choice = sc.nextInt();
			}
			Command cmd = pack[choice].cmd;
			return cmd;
		}
	}
	
}

class Command {
	String verb;
	String uri;
	Object argument;
	public Command(String verb, String uri, Object argument) {
		this.verb = verb;
		this.uri = uri;
		this.argument = argument;
	}
}

class Package {
	String view;
	Command cmd;
	public Package(String view, Command cmd) {
		this.view = view;
		this.cmd = cmd;
	}
}

class Pair {
    public String value;
    public Integer count;
    
    public Pair(String value, int count) {
        this.value = value;
        this.count = count;
    }
    
    @Override
    public String toString() {
        return (this.value + " - " + this.count);
    }
}