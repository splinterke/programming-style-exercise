import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Words1 implements TFWords{
	
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
