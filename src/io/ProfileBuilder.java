package io;

import Spectrum.SpectraMatrix;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import preprocessing.LDADataSet;
import preprocessing.PCADataSet;
import weka.core.matrix.Matrix;

/** This class contains the static method build() 
 * that builds a profile from given parameters 
 * and writes a pofile file to a specified path on the file system.
 * 
 * @author Stephan Neese
 */
public class ProfileBuilder {
	
	/** prints a profile file from the given information
	 * 
	 * @param data the result of the PCA transformation
	 * @param lda the result of the LDA
	 * @param originalData the original dataset before PCA
	 * @param device the name of the MS device
	 * @param inputPath path to the original csv files of the spectras
	 * @param path output path for the profile
	 * @param adjustment variable that determines 
	 * amount of PCA transformed datapoints 
	 * to take into calculation of the mean of the groups
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws ParseException 
	 */
	public static void build(
			PCADataSet data, 
			LDADataSet lda,
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
		
		// print classes to file
		String[] classes = data.getClasses();
		writer.print("classes:");
		for(String s : classes){
			writer.print("\t" + s);
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
		String[] groups = originalData.getGroups();
		writer.println("groups:");
		for(int i=0; i<groups.length; i++){
			writer.println(groups[i]);
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
		// print the inverse covariance matrices for each group
		writer.println("covariances:");
		for(String s : classes){
			double[][] cov = calcCovarianceMatrix(dataValues, groups, s);
			Matrix covarianceMatrix = new Matrix(cov);
			Matrix inverseCovarianceMatrix = covarianceMatrix.inverse();
			double[][] invCov = inverseCovarianceMatrix.getArray();
			for(int i=0; i<invCov.length; i++){
				writer.print(s);
				for(int j=0; j<invCov[i].length; j++){
					writer.print("\t" + invCov[i][j]);
				}
				writer.println();
			}
		}
		writer.println("//#");
		// calculate and print means of dimensions
		writer.println("mean:");
		// loop through the classes
		if(adjustment==1.0){
			for(String s : classes){
				double[] mean = calcMeans(dataValues, groups, s);
				writer.print(mean[0]);
				for(int i=1; i<mean.length; i++){
					writer.print("\t" + mean[i]);
				}
				writer.println();
			}
		}else{
			for(String s : classes){
				double[] mean = calcMeans(dataValues, groups, s, adjustment);
				writer.print(mean[0]);
				for(int i=1; i<mean.length; i++){
					writer.print("\t" + mean[i]);
				}
				writer.println();
			}
		}
		writer.println("//#");
		// print lda global mean
		writer.println("lda-mean:");
		double[] globalMean = lda.getGlobalMean();
		for(int i=0; i<globalMean.length; i++){
			writer.println(globalMean[i]);
		}
		writer.println("//#");
		// print inverse covariance Matrix derived from LDA
		writer.println("lda-covariance:");
		double[][] lda_cov = lda.getInverseCovarianceMatrix();
		for(int i=0; i<lda_cov.length; i++){
			writer.print(lda_cov[i][0]);
			for(int j=1; j<lda_cov[i].length; j++){
				writer.print("\t" + lda_cov[i][j]);
			}
			writer.println();
		}
		writer.println("//#");
		// print the fractions of the groups
		writer.println("lda-fractions:");
		double[] fractions = lda.getFractions();
		for(int i=0; i<fractions.length; i++){
			writer.println(fractions[i]);
		}
		
		writer.close();
	}
	
	/** takes a set of data values (2d array with dimensions along the rows),
	 * filters all the samples belonging to a given class 
	 * and calculates the means for all dimensions of these samples
	 * 
	 * @param dataValues the data array
	 * @param sampleFiles the list with the file names 
	 * (must be same order as samples/columns in data array)
	 * @param cls the class
	 * @return an array with mean values
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
	
	/** takes a set of data values (2d array with dimensions along the rows),
	 * filters a partition the best samples belonging to a given class (ex. 90 % of one class)
	 * and calculates the means for all dimensions of these samples
	 * 
	 * @param dataValues the data array
	 * @param sampleFiles the list with the file names 
	 * (must be same order as samples/columns in data array)
	 * @param cls the class
	 * @param partition what fraction of the classes best samples should be used
	 * @return an array with mean values
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
	
	/** takes a set of data values (2d array with dimensions along the rows),
	 * filters all the samples belonging to a given class 
	 * and calculates the covariance matrix for this class
	 * 
	 * @param dataValues the data array
	 * @param sampleFiles the list with the file names 
	 * (must be same order as samples/columns in data array)
	 * @param cls the class
	 * @return an array containing the covariance matrix
	 */
	private static double[][] calcCovarianceMatrix(double[][] dataValues, String[] sampleFiles, String cls){
		double[][] covariance = new double[dataValues.length][dataValues.length];
		
		/*
		find all samples for the class
		loop through the samples of the data array (columns)
		*/
		ArrayList<double[]> picked = new ArrayList<>();
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
		
		// calculate covariance matrix for the found samples
		for(int i=0; i<covariance.length; i++){
			double[] x = new double[picked.size()];
			double xMean = 0;
			double xSum = 0;
			
			for(int k=0; k<picked.size(); k++){
				x[k] = picked.get(k)[i];
				xSum += x[k];
			}
			xMean = xSum/x.length;
			for(int j=0; j<covariance.length; j++){
				double[] y = new double[picked.size()];
				double yMean = 0;
				double ySum = 0;
				
				for(int k=0; k<picked.size(); k++){
					y[k] = picked.get(k)[j];
					ySum += y[k];
				}
				yMean = ySum/y.length;
				// calculate covariance for x and y
				double num = 0;
				for(int k=0; k<x.length; k++){
					num += (x[k] - xMean)*(y[k] - yMean);
				}
				covariance[i][j] = num/(x.length-1);
			}
		}
		
		return covariance;
	}
}
