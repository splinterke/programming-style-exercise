import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Words2 implements TFWords{

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
