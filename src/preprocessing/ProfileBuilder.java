package preprocessing;

import Spectrum.SpectraMatrix;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileBuilder {
	
	public static void build(PCADataSet data, String path){
		double[][] dataValues = data.getData();
		
		// obtain the classes from the samples filenames
		String[] sampleFiles = data.getClasses();
		HashMap<String, Integer> classes = new HashMap<>();
		for(int i=0; i<sampleFiles.length; i++){
			String[] tmp = sampleFiles[i].split("_");
			classes.put(tmp[0], 1);
		}
		// loop through the classes
		for(Map.Entry<String, Integer> e : classes.entrySet()){
			String cls = e.getKey();
			ArrayList<double[]> picked = new ArrayList<>();
			// loop through the samples of the data array (columns)
			for(int i=0; i<dataValues[0].length; i++){
				// new array for the sample
				double[] sample = new double[dataValues.length];
				// check if the name of the csv starts with our class name
				// filenames and samples in data array are in same order
				if(sampleFiles[i].startsWith(cls)){
					for(int dim=0; dim<dataValues.length; dim++){
						sample[dim] = dataValues[dim][i];
					}
					picked.add(sample);
				}
			}
			// calculate mean for dimensions
			double[] mean = new double[dataValues.length];
			for(int i=0; i<mean.length; i++){
				double sum = 0;
				double meanVal = 0;
				for(int j=0; j<picked.size(); j++){
					sum += picked.get(j)[i];
				}
				mean[i] = sum/picked.size();
			}
		}
	}
}
