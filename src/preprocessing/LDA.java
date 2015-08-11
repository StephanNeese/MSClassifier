package preprocessing;

import Spectrum.SpectraMatrix;
import java.util.ArrayList;
import java.util.HashMap;
import weka.core.matrix.Matrix;

/** This class holds algorithms to process a
 * linear discriminant analysis.
 *
 * @author Stephan Neese
 */
public class LDA {
	
	/**
	 * 
	 * @param data
	 * @param original
	 * @return 
	 */
	public static LDADataSet performLDA(PCADataSet data, SpectraMatrix original){
		// transpose to have [samples][dimensions]
		double[][] pca_data = transpose(data.getData());
		String[] classes = data.getClasses();
		String[] groups = original.getGroups();
		
		// calc means for groups
		double[][] means = new double[classes.length][pca_data[0].length];
		for(int i=0; i<classes.length; i++){
			means[i] = calcMeans(pca_data, groups, classes[i]);
		}
		// mean center data
		double[] globalMean = globalMean(pca_data);
		double[][] centeredData = center(pca_data, globalMean);
		// calc global covariance matrix of dataset
		double[][] covarianceMatrix = calcCovarianceMatrix(centeredData, groups, classes);
		Matrix tmp = new Matrix(covarianceMatrix);
		Matrix inverseCovarianceMatrix = tmp.inverse();
		double[] fractions = calcFractions(groups, classes);
		
		return new LDADataSet(inverseCovarianceMatrix.getArray(), globalMean, fractions);
	}	
	
	/** transposes a matrix
	 * 
	 * @param matrix matrix to transpose
	 * @return transposed matrix
	 */
	private static double[][] transpose(double[][] matrix){
		double[][] res = new double[matrix[0].length][matrix.length];
		
		for(int i=0; i<matrix.length; i++){
			for(int j=0; j<matrix[0].length; j++){
				res[j][i] = matrix[i][j];
			}
		}
		
		return res;
	}
	
	/** calculates and returns the global mean
	 * of a data set
	 * 
	 * @param data the data set
	 * @return the global mean values for the dimensions
	 */
	private static double[] globalMean(double[][] data){
		double[] res = new double[data[0].length];
		
		// calc global mean
		for(int i=0; i<data[0].length; i++){
			double sum = 0;
			for(int j=0; j<data.length; j++){
				sum += data[j][i];
			}
			res[i] = sum/(double)(data.length);
		}
		
		return res;
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
		// loop through the samples of the data array (rows)
		for(int i=0; i<dataValues.length; i++){
			// new array for the sample
			double[] sample = new double[dataValues[i].length];
			// check if the name of the csv starts with our class name
			// filenames and samples in data array are in same order
			if(sampleFiles[i].equals(cls)){
				for(int dim=0; dim<dataValues[i].length; dim++){
					sample[dim] = dataValues[i][dim];
				}
				picked.add(sample);
			}
		}
		// calculate mean for dimensions
		double[] mean = new double[dataValues[0].length];
		for(int i=0; i<mean.length; i++){
			double sum = 0;
			for(int j=0; j<picked.size(); j++){
				sum += picked.get(j)[i];
			}
			mean[i] = sum/picked.size();
		}
		
		return mean;
	}
	
	/**
	 * 
	 * @param data
	 * @return 
	 */
	public static double[][] center(double[][] data, double[] mean){
		double[][] res = new double[data.length][data[0].length];
		
		// substract global mean from data
		for(int i=0; i<data.length; i++){
			for(int j=0; j<data[i].length; j++){
				res[i][j] = data[i][j] - mean[j];
			}
		}
		
		return res;
	}
	
	/**
	 * 
	 * @param data
	 * @param samples
	 * @param classes
	 * @return 
	 */
	private static double[][] calcCovarianceMatrix(double[][] data, String[] groups, String[] classes){
		double[][] res = new double[data[0].length][data[0].length];
		HashMap<String, double[][]> matrices = new HashMap<>();
		HashMap<String, Double> fractions = new HashMap<>();
		
		// calc covariance matrices for groups
		for(String cls : classes){
			double[][] matrix = getGroupMatrix(data, groups, cls);
			double[][] covMatrix = calcCovarianceMatrix(matrix);
			matrices.put(cls, covMatrix);
			fractions.put(cls, ((double)matrix.length)/((double)data.length));
		}
		
		// calc global pooled covariance matrix
		for(int i=0; i<res.length; i++){
			for(int j=0; j<res[0].length; j++){
				// add up the values from all covariance matrices * their fraction
				for(String cls : classes){
					res[i][j] += fractions.get(cls) * matrices.get(cls)[i][j];
				}
			}
		}
		
		return res;
	}
	
	/**
	 * 
	 * @param dataValues
	 * @param sampleFiles
	 * @param cls
	 * @return 
	 */
	private static double[][] getGroupMatrix(double[][] dataValues, String[] groups, String cls){
		ArrayList<double[]> picked = new ArrayList<>();
		
		// loop through the samples of the data array (rows)
		for(int i=0; i<dataValues.length; i++){
			// new array for the sample
			double[] sample = new double[dataValues[i].length];
			// check if the name of the csv starts with our class name
			// filenames and samples in data array are in same order
			if(groups[i].equals(cls)){
				for(int dim=0; dim<dataValues[i].length; dim++){
					sample[dim] = dataValues[i][dim];
				}
				picked.add(sample);
			}
		}
		
		// transform into array
		double[][] res = new double[picked.size()][dataValues[0].length];
		for(int i=0; i<res.length; i++){
			for(int j=0; j<res[i].length; j++){
				res[i][j] = picked.get(i)[j];
			}
		}
		
		return res;
	}
	
	/** calculates a covariance matrix from the spectra data
	 * 
	 * @param data the array for which to calculate the covariance matrix
	 * @return covariance matrix as 2d double array
	 */
	private static double[][] calcCovarianceMatrix(double[][] data) {
		double[][] covariance = new double[data[0].length][data[0].length];
		
		// loop through dimension twice to create the matrix
		for(int i=0; i<data[0].length; i++){
			// calc mean of dimension
			double xMean = 0;
			double xSum = 0;
			for(int k=0; k<data.length; k++){
				xSum += data[k][i];
			}
			xMean = xSum/data.length;
			
			for(int j=0; j<data[0].length; j++){
				// calc mean of dimension
				double yMean = 0;
				double ySum = 0;
				for(int k=0; k<data.length; k++){
					ySum += data[k][j];
				}
				yMean = ySum/data.length;
				// calculate covariance
				double num = 0;
				for(int k=0; k<data.length; k++){
					num += (data[k][i] - xMean)*(data[k][j] - yMean);
				}
				covariance[i][j] = num/(data.length-1);
			}
		}
		
		return covariance;
	}
	
	/**
	 * 
	 * @param samples
	 * @param classes
	 * @return 
	 */
	private static double[] calcFractions(String[] groups, String[] classes){
		double[] res = new double[classes.length];
		
		for(int i=0; i<classes.length; i++){
			int cnt = 0;
			for(int j=0; j<groups.length; j++){
				if(groups[j].equals(classes[i])){
					cnt++;
				}
			}
			res[i] = ((double)cnt)/((double)groups.length);
		}
		
		return res;
	}
}
