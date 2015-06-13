import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;


public class NoCommit {
	
	String JarLoca;
	String TFWords_name;
	String TFFreqs_name;
	String TFPrint_name;
	
	public static void main(String[] args) {
		NoCommit nc = new NoCommit();
		nc.ReadPFile();
		
		try {
			URL url = new File(nc.JarLoca).toURI().toURL();
			URLClassLoader clazzLoader = new URLClassLoader(new URL[]{url});
			TFWords tfwords = (TFWords) clazzLoader.loadClass(nc.TFWords_name).newInstance();
			TFFreqs tffreqs = (TFFreqs) clazzLoader.loadClass(nc.TFFreqs_name).newInstance();
			TFPrint tfprint = (TFPrint) clazzLoader.loadClass(nc.TFPrint_name).newInstance();
			
			tfprint.show(tffreqs.top25(tfwords.extract_words(args[0])));
			
		} catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			System.out.println(e);
		}
	}
	
	public void ReadPFile() {
		try{
	      Properties p = new Properties();
	      p.load(new FileInputStream("TF.props"));
	      this.JarLoca = p.getProperty("Jar");
	      this.TFWords_name = p.getProperty("TFWords_name");
	      this.TFFreqs_name = p.getProperty("TFFreqs_name");
	      this.TFPrint_name = p.getProperty("TFPrint_name");
	      }
	    catch (Exception e) {
	      System.out.println(e);
	      }
	}

}
