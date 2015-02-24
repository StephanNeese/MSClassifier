
import java.io.IOException;


public class Main {
	
	public static void main(String[] args) throws IOException {
		Spectrum x = new Spectrum("/home/wens/Pflaume_32AVG5.csv", 1);
		System.out.println(x.toString());
		x.normalizationMeanSubstraction();
		System.out.println("AFTER NORMALIZATION:");
		System.out.println(x.toString());
	}
}
