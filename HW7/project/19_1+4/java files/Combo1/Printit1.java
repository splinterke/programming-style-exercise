import java.util.List;


public class Printit1 implements TFPrint{
	
	public void show(List<pair> result) {
		for(pair p : result) {
			System.out.println(p.value + " - " + p.count);
		}
	}

}
