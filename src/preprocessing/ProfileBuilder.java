package preprocessing;

import Spectrum.SpectraMatrix;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileBuilder {
	
	public static void build(PCADataSet data, SpectraMatrix originalData, String device, String inputPath, String path) throws FileNotFoundException, UnsupportedEncodingException, ParseException{
		double[][] dataValues = data.getData();
		PrintWriter writer = new PrintWriter(path, "UTF-8");
		
		// obtain the classes from the samples filenames
		String[] sampleFiles = data.getClasses();
		HashMap<String, Integer> classes = new HashMap<>();
		for(int i=0; i<sampleFiles.length; i++){
			String[] tmp = sampleFiles[i].split("_");
			classes.put(tmp[0], 1);
		}
		
		// print classes to file
		writer.print("classes:");
		for(Map.Entry<String, Integer> e : classes.entrySet()){
			writer.print("\t" + e.getKey());
		}
		writer.println("\n//#");
		// print datetime
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy:HH:mm");
		String date = formatter.format(new Date());
		writer.println("date:\t" + date + "\n//#");
		// print devicename
		writer.println("device:\t" + device + "\n//#");
		// print input path
		writer.println("path:\t" + inputPath + "\n//#");
		// print variance covered
		writer.println("variance:\t" + data.getVariance() + "\n//#");
		// print filenames in same order as data
		writer.println("filenames:");
		for(int i=0; i<sampleFiles.length; i++){
			writer.println(sampleFiles[i]);
		}
		writer.println("//#");
		// print the mean values of the original untransformed data for normalization
		writer.println("original-means:");
		double[] originalMeans = originalData.getDimensionsMean();
		for(int i=0; i<originalMeans.length; i++){
			writer.println(originalMeans[i]);
		}
		writer.println("//#");
		// print the mean of the untransformed spectraMatrix for normalization
		writer.println("original-mean:\t" + originalData.getMean());
		writer.println("//#");
		// print the bin size
		double[] bins = originalData.getMz();
		double binSize = bins[1] - bins[0];
		writer.println("bin:\t" + binSize);
		writer.println("//#");
		// print transformed data matrix
		writer.println("data:");
		for(int i=0; i<dataValues.length; i++){
			writer.print(dataValues[i][0]);
			for(int j=1; j<dataValues[i].length; j++){
				writer.print("\t" + dataValues[i][j]);
			}
			writer.println();
		}
		writer.println("//#");
		// print transformed feature matrix
		writer.println("features:");
		double[][] features = data.getTransformedFeatureMatrix();
		for(int i=0; i<features.length; i++){
			writer.print(features[i][0]);
			for(int j=1; j<features[i].length; j++){
				writer.print("\t" + features[i][j]);
			}
			writer.println();
		}
		writer.println("//#");
		writer.println("mean:");
		// calculate and print means of dimensions
		// loop through the classes
		for(Map.Entry<String, Integer> e : classes.entrySet()){
			String cls = e.getKey();
			double[] mean = calcMeans(dataValues, sampleFiles, cls);
			writer.print(mean[0]);
			for(int i=1; i<mean.length; i++){
				writer.print("\t" + mean[i]);
			}
			writer.println();
		}
		
		writer.close();
	}
	
	private static double[] calcMeans(double[][] dataValues, String[] sampleFiles, String cls){
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
		
		return mean;
	}
}
