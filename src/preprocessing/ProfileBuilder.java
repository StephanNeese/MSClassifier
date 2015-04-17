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
	
	/**
	 * 
	 * @param data
	 * @param originalData
	 * @param device
	 * @param inputPath
	 * @param path
	 * @param adjustment
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws ParseException 
	 */
	public static void build(
			PCADataSet data, 
			SpectraMatrix originalData, 
			String device, 
			String inputPath, 
			String path,
			double adjustment) throws FileNotFoundException, UnsupportedEncodingException, ParseException{
		if(adjustment>1.0 || adjustment<0){
			throw new IllegalArgumentException("You cannot use more than 100 % "
					+ "of the samples to calculate the groups center."
					+ " Please use a number between 0 and 1.0.");
		}
		
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
		if(adjustment==1.0){
			for(Map.Entry<String, Integer> e : classes.entrySet()){
				String cls = e.getKey();
				double[] mean = calcMeans(dataValues, sampleFiles, cls);
				writer.print(mean[0]);
				for(int i=1; i<mean.length; i++){
					writer.print("\t" + mean[i]);
				}
				writer.println();
			}
		}else{
			for(Map.Entry<String, Integer> e : classes.entrySet()){
				String cls = e.getKey();
				double[] mean = calcMeans(dataValues, sampleFiles, cls, adjustment);
				writer.print(mean[0]);
				for(int i=1; i<mean.length; i++){
					writer.print("\t" + mean[i]);
				}
				writer.println();
			}
		}
		
		writer.close();
	}
	
	/**
	 * 
	 * @param dataValues
	 * @param sampleFiles
	 * @param cls
	 * @return 
	 */
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
	
	/**
	 * 
	 * @param dataValues
	 * @param sampleFiles
	 * @param cls
	 * @param partition
	 * @return 
	 */
	private static double[] calcMeans(double[][] dataValues, String[] sampleFiles, String cls, double partition){
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
		
		// calc how much of the worst samples will be dismissed and loop as many times
		int eject = (int)(picked.size() - (picked.size() * partition));
		for(int k=0; k<eject; k++){
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
			// calculate distance from center (mean) point from every sample
			double[] distances = new double[picked.size()];
			for(int i=0; i<picked.size(); i++){
				double sum = 0;
				for(int j=0; j<mean.length; j++){
					sum += (mean[j] - picked.get(i)[j])*(mean[j] - picked.get(i)[j]);
				}
				distances[i] = Math.sqrt(sum);
			}
			// find index of biggest distance and discard from list
			double biggest = distances[0];
			int index = 0;
			for(int i=1; i<distances.length; i++){
				if(distances[i]>biggest){
					biggest = distances[i];
					index = i;
				}
			}
			picked.remove(index);
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
