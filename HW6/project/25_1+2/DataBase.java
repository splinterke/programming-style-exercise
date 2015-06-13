import java.io.*;
import java.sql.*;
import java.util.*;


public class DataBase {
	
	public static void main(String[] args) {
	    
	    create_tables();
	    doc_writer(args[0]);
	    word_writer(doc_reader());
	    pop_words();
	    //show_table("words", "VALUE");
	}
	
	public static void create_tables() {
	    
	    Connection c = null;
    	Statement stmt = null;
		try {
	    	Class.forName("org.sqlite.JDBC");
	    	c = DriverManager.getConnection("jdbc:sqlite:tf.db");
	    	System.out.println("Opened database successfully");
	    	c.setAutoCommit(false);
	    	stmt = c.createStatement();
	    	String sql = null;
	    	
	    	sql = "DROP TABLE IF EXISTS documents; ";
	    	stmt.executeUpdate(sql);
	    	
	    	sql = "DROP TABLE IF EXISTS words; ";
	    	stmt.executeUpdate(sql);
	    		    	
	    	sql = "CREATE TABLE documents " +
	    	        "(ID INT PRIMARY KEY        NOT NULL," +
	    	        " TITLE         TEXT        NOT NULL," +
	    	        " CONTENT       TEXT        NOT NULL)"; 
	    	stmt.executeUpdate(sql);
	    	
	    	sql = "CREATE TABLE words " +
    	            "(ID INT PRIMARY KEY        NOT NULL," +
    	            " VALUE         TEXT        NOT NULL," +
    	            " DOC_ID         INT        NOT NULL)"; 
	    	stmt.executeUpdate(sql);    	
	    	
	    	c.commit();
	    	stmt.close();
	    	c.close();
	    	
	    } catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	System.exit(0);
	    }
	}
	
	public static void doc_writer(String path) {
	    
	    String content = null;		
		try(Scanner sc = new Scanner(new File(path));) {
			content = sc.useDelimiter("\\Z").next();
		} catch (FileNotFoundException e) {
			System.out.print("Fail to read file " + path);
		}
		content = content.replaceAll("'", " ");
	    
	    Connection c = null;
    	Statement stmt = null;
		try {
	    	Class.forName("org.sqlite.JDBC");
	    	c = DriverManager.getConnection("jdbc:sqlite:tf.db");
	    	c.setAutoCommit(false);
	    	stmt = c.createStatement();
	    	String sql = null;
	    	
	    	sql = "SELECT * FROM documents WHERE ID = ( SELECT MAX(ID) FROM documents);";
	    	ResultSet rs = stmt.executeQuery(sql);
	    	int id_max = 0;
	    	if ( rs.next() ) {
	    	    id_max = rs.getInt("ID");
	    	}
	    	id_max += 1;
	    	sql = "INSERT INTO documents (ID,TITLE,CONTENT) VALUES ('" + id_max + "','" + path + "','" + content + "');";
	    	stmt.executeUpdate(sql);
	    	
	    	c.commit();
	    	stmt.close();
	    	c.close();
	    	
	    } catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	System.exit(0);
	    }
	    
	}
	
	public static List<Object> doc_reader() {
	    
	    Connection c = null;
    	Statement stmt = null;
    	List<Object> result = new ArrayList<Object>();
    	
		try {
	    	Class.forName("org.sqlite.JDBC");
	    	c = DriverManager.getConnection("jdbc:sqlite:tf.db");
	    	c.setAutoCommit(false);
	    	stmt = c.createStatement();
	    	String sql = null;
	    	
	    	sql = "SELECT * FROM documents; ";
	    	ResultSet rs = stmt.executeQuery(sql);
	    	while ( rs.next() ) {
	    	    int id = rs.getInt("ID");
	    	    String content = rs.getString("CONTENT");
	    	    result.add(id);
	    	    result.add(content);
	    	}
	    	
	    	c.commit();
	    	stmt.close();
	    	c.close();
	    	
	    } catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	System.exit(0);
	    }
	    
	    return result;
	    
	}
	
	public static void word_writer(List<Object> result) {
	    
	    String content_sl = "";
		String[] stopword = null;
		
		try(Scanner sc_sl = new Scanner(new File("..//stop_words.txt"));) {
		    	content_sl = sc_sl.useDelimiter("\\Z").next();
		} catch (FileNotFoundException e) {
			System.out.print("Fail to read file " + "stop_words.txt");
		}
		
		stopword = content_sl.toLowerCase().split("[\\W]+");
		List<String> stoplist = new ArrayList<String>(Arrays.asList(stopword));
		stoplist.add("s");
	    
	    Connection c = null;
    	Statement stmt = null;
		try {
	    	Class.forName("org.sqlite.JDBC");
	    	c = DriverManager.getConnection("jdbc:sqlite:tf.db");
	    	c.setAutoCommit(false);
	    	stmt = c.createStatement();
	    	String sql = null;
	    	
	    	sql = "SELECT * FROM words WHERE ID = ( SELECT MAX(ID) FROM words);";
	    	ResultSet rs = stmt.executeQuery(sql);
	    	int id_max = 0;
	    	if ( rs.next() ) {
	    	    id_max = rs.getInt("ID");
	    	}
	    	
	    	for(int i=0;i<result.size();i+=2) {
	    	    int doc_id = (int)result.get(i);
	    	    String content = (String)result.get(i+1);
	    	    String[] token = content.toLowerCase().replaceAll("[0-9]","").split("[\\W_]+");
	    	    for (String word : token) {
	    	    	if (stoplist.indexOf(word)==-1) {
	    	    	    id_max += 1;
	    	    	    sql = "INSERT INTO words (ID,VALUE,DOC_ID) VALUES ('" + id_max + "','" + word + "','" + doc_id + "');";
	    	    	    stmt.executeUpdate(sql);
	    	    	}
	    	    }
	    	}
	    	
	    	c.commit();
	    	stmt.close();
	    	c.close();
	    	
	    } catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	System.exit(0);
	    }
	    
	}
	
	public static void pop_words() {
	    
	    Connection c = null;
    	Statement stmt = null;
		try {
	    	Class.forName("org.sqlite.JDBC");
	    	c = DriverManager.getConnection("jdbc:sqlite:tf.db");
	    	c.setAutoCommit(false);
	    	stmt = c.createStatement();
	    	String sql = null;
	    	
	    	sql = "SELECT VALUE, COUNT(*) as C FROM words GROUP BY VALUE ORDER BY C DESC;";
	    	ResultSet rs = stmt.executeQuery(sql);
	    	int i = 0;
	    	while ( rs.next() && i < 25) {
	    	    String word = rs.getString("VALUE");
	    	    int count = rs.getInt("C");
	    	    System.out.println(word + " - " + count);
	    	    i += 1;
	    	}
	    	
	    	c.commit();
	    	stmt.close();
	    	c.close();
	    	
	    } catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	System.exit(0);
	    }
	    
	}
	
	public static void show_table(String tab, String colume) {
	    
	    Connection c = null;
    	Statement stmt = null;
		try {
	    	Class.forName("org.sqlite.JDBC");
	    	c = DriverManager.getConnection("jdbc:sqlite:tf.db");
	    	c.setAutoCommit(false);
	    	stmt = c.createStatement();
	    	String sql = null;
	    	
	    	sql = "SELECT * FROM '" + tab + "';";
	    	ResultSet rs = stmt.executeQuery(sql);
	    	while ( rs.next() ) {
	    	    int id = rs.getInt("ID");
	    	    String text = rs.getString(colume);
	    	    System.out.println(id + "-" + text);
	    	}
	    	
	    	c.commit();
	    	stmt.close();
	    	c.close();
	    	
	    } catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	System.exit(0);
	    }
	    
	} 
	
}