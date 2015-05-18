package Spectrum;

import java.util.Date;
import java.util.HashMap;
import preprocessing.LDA;
import preprocessing.PCA;
import weka.core.matrix.Matrix;

/** This class provides a data structure for
 * the saved data from pca transformation and
 * some of the raw data as well as basic information
 * on the creation of this dataset.
 * 
 * @author Stephan Neese
 */

public class Profile {
	
	private final String[] classes;			// names of the classes
	private final Date datetime;			// date the profile was created
	private final String device;			// MS device
	private final String path;				// path to profile (original path)
	private final double variance;			// covered variance by profile
	private final String[] filenames;		// filenames of original csv files 
	private final double[][] data;			// pca transformed data [dimensions][samples]
	private final double[][] features;		// feature vector (used for transformation)
	// inv. cov. matrices of classes
	private HashMap<String, double[][]> invertedCovarianceMatrices;
	private final double[][] mean;			// [class][dimension]
	private final double[] originalMeans;	// mean of dimensions of untransformed dataset
	private final double originalMean;		// mean of all spectras and dimensions of untransformed
	private final double binSize;			// size of a bin
	// lda data
	private final double[][] ldaCovarianceMatrix;
	private final double[] globalMean;
	private final double[] fractions;

	/** constructs a Profile Object
	 * 
	 * @param classes the names of the classes
	 * @param datetime the date and time
	 * @param device the name of the device
	 * @param path the path to the csv files the profile has been constructed from
	 * @param variance the amount of covered variance by the PCA
	 * @param filenames the filenames of the csv files
	 * @param data pca transformed data
	 * @param features the feature matrix
	 * @param invertedCovarianceMatrices the inverted covariance matrices of the pca data classes
	 * @param mean the mean values (centroids) of the pca data
	 * @param originalMeans mean values of the dimensions of the original data
	 * @param originalMean mean value of the mean dataset (all dimensions)
	 * @param binSize size of a bin in u
	 * @param ldaCovarianceMatrix
	 * @param fractions
	 */
	public Profile(
			String[] classes, 
			Date datetime, 
			String device, 
			String path, 
			double variance, 
			String[] filenames, 
			double[][] data, 
			double[][] features,
			HashMap<String, double[][]> invertedCovarianceMatrices,
			double[][] mean,
			double[] originalMeans,
			double originalMean,
			double binSize,
			double[][] ldaCovarianceMatrix,
			double[] globalMean,
			double[] fractions) {
		this.classes = classes;
		this.datetime = datetime;
		this.device = device;
		this.path = path;
		this.variance = variance;
		this.filenames = filenames;
		this.data = data;
		this.features = features;
		this.invertedCovarianceMatrices = invertedCovarianceMatrices;
		this.mean = mean;
		this.originalMeans = originalMeans;
		this.originalMean = originalMean;
		this.binSize = binSize;
		this.ldaCovarianceMatrix = ldaCovarianceMatrix;
		this.globalMean = globalMean;
		this.fractions = fractions;
	}

	/** returns the classes
	 * 
	 * @return the classes as String array
	 */
	public String[] getClasses() {
		return classes;
	}

	/** returns the Date and time
	 * 
	 * @return datetime object
	 */
	public Date getDatetime() {
		return datetime;
	}

	/** returns teh device name
	 * 
	 * @return device name
	 */
	public String getDevice() {
		return device;
	}

	/** returns the path of the csv files the profile has been constructed from
	 * 
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/** returns the covered variance
	 * 
	 * @return the variance as double
	 */
	public double getVariance() {
		return variance;
	}

	/** returns all the filenames of the original csv files
	 * 
	 * @return the csv file names as array
	 */
	public String[] getFilenames() {
		return filenames;
	}

	/** returns the pca transformed data matrix. 
	 * The columns are in the same order as the elements of the filename array
	 * 
	 * @return data matrix as double array [dimensions][samples]
	 */
	public double[][] getData() {
		return data;
	}

	/** returns the feature matrix. 
	 * 
	 * @return the feature matrix as 2d array
	 */
	public double[][] getFeatures() {
		return features;
	}
	
	/** returns the Hash containing the covariance matrices for the classes
	 * 
	 * @return a hash with class(string) => inv-covariancematrix(double[][])
	 */
	public HashMap<String, double[][]> getInvertedCovarianceMatrices(){
		return invertedCovarianceMatrices;
	}
	
	/** returns the inverted covariance matrix for the given class
	 * 
	 * @param cls the class name
	 * @return the inv. covariance matrix
	 */
	public double[][] getInvertedCovarianceMatrix(String cls){
		return invertedCovarianceMatrices.get(cls);
	}

	/** returns the mean values (centroids) for the pca data
	 * 
	 * @return the mean values in the form [class][dimension]
	 */
	public double[][] getMean() {
		return mean;
	}
	
	/** returns the mean values for the dimensions 
	 * of the original (untransformed) data
	 * 
	 * @return the mean values of the original data
	 */
	public double[] getOriginalMeans() {
		return originalMeans;
	}

	/** returns the mean value for the original data
	 * 
	 * @return mean value
	 */
	public double getOriginalMean() {
		return originalMean;
	}

	/** returns the bin size
	 * 
	 * @return the bin size
	 */
	public double getBinSize() {
		return binSize;
	}

	/** returns the global covariance matrix created during the LDA
	 * 
	 * @return the inv. cov. matrix
	 */
	public double[][] getLdaCovarianceMatrix() {
		return ldaCovarianceMatrix;
	}

	/** returns the fractions of the classes.
	 * This is needed for the discriminant function
	 * for LDA classification.
	 * 
	 * @return the fractions of the classes
	 */
	public double[] getFractions() {
		return fractions;
	}

	@Override
	public String toString() {
		String res = "";
		
		res += "classes:";
		for(String s : classes){
			res += "\t" + s;
		}
		res += "\n"
				+ "created:\t" + datetime.toString() + "\n"
				+ "sourcepath:\t" + path + "\n"
				+ "device:\t" + device + "\n"
				+ "variance covered:\t" + variance + "\n";
		
		res += "transformed data:\n";
		for(int i=0; i<data.length; i++){
			for(int j=0; j<data[i].length; j++){
				res += data[i][j] + "\t";
			}
			res += "\n";
		}
		res += "feature matrix:\n";
		for(int i=0; i<features.length; i++){
			for(int j=0; j<features[i].length; j++){
				res += features[i][j] + "\t";
			}
			res += "\n";
		}
		res += "mean matrix:\n";
		for(int i=0; i<mean.length; i++){
			res += classes[i];
			for(int j=0; j<mean[i].length; j++){
				res += "\t" + mean[i][j];
			}
			res += "\n";
		}
		
		return res;
	}

	/** calculates the mahalanobis distance between the profiles classes and a given spectrum
	 * 
	 * @param spectrum the spectrum to calculate the distance for
	 * @return a ClassificationResult object
	 */
	public ClassificationResult mahalanobisDistance(Spectrum spectrum){
		// check if same length
		if(spectrum.getLength()!=originalMeans.length){
			throw new IllegalArgumentException("Spectrum and Data in the profile do not have the same M/Z range. "
			+ "Please adjust the device.");
		}
		// normalize and center the spectrum
		spectrum.normalizationDivideByMean(originalMean);
		spectrum.center(originalMeans);
		// transform the spectrum into PCA space
		double[] pca_spectrum = PCA.transformSpectrum(spectrum, features);
		
		double[] distances = new double[classes.length];
		
		// loop through all classes to pick the same row in the mean array
		for(int i=0; i<classes.length; i++){
			// differences between the dimensions
			double[] differences = new double[pca_spectrum.length];
			for(int j=0; j<differences.length; j++){
				differences[j] = pca_spectrum[j] - mean[i][j];
			}
			// transform into matrix and its inverse
			double[][] tmp = new double[1][differences.length];
			tmp[0] = differences;
			Matrix U = new Matrix(tmp);
			Matrix UTransposed = U.transpose();
			
			// multiply U with covariance matrix
			Matrix covarianceMatrix = new Matrix(invertedCovarianceMatrices.get(classes[i]));
			Matrix left = U.times(covarianceMatrix);
			// multiply left with transposed U
			Matrix res = left.times(UTransposed);
			
			distances[i] = Math.sqrt(res.get(0, 0));
		}
		
		// lookup the smallest distance
		double smallest = distances[0];
		int index = 0;
		for(int i=1; i<distances.length; i++){
			if(distances[i]<smallest){
				smallest = distances[i];
				index = i;
			}
		}
		// calculate score
		double sum = 0;
		for(int i=0; i<distances.length; i++){
			sum += distances[i];
		}
		double best = (1.0 - (distances[index]/sum));
		double worst = (1.0 - (1.0/(double)(distances.length)));
		double score = (best - worst)/(1 - worst);
		
		return new ClassificationResult(
				classes[index], 
				distances[index], 
				score);
	}
	
	/** calculates the euclidian distance between the profiles classes and a given spectrum
	 * 
	 * @param spectrum the spectrum to calculate the distance for
	 * @return a ClassificationResult object
	 */
	public ClassificationResult euclideanDistance(Spectrum spectrum){
		// check if same length
		if(spectrum.getLength()!=originalMeans.length){
			throw new IllegalArgumentException("Spectrum and Data in the profile do not have the same M/Z range. "
			+ "Please adjust the device.");
		}
		// normalize and center the spectrum
		spectrum.normalizationDivideByMean(originalMean);
		spectrum.center(originalMeans);
		// transform the spectrum into PCA space
		double[] pca_spectrum = PCA.transformSpectrum(spectrum, features);
		// loop through all classes to pick the same row in the mean array
		double[] distances = new double[classes.length];
		for(int i=0; i<classes.length; i++){
			double sum = 0;
			for(int j=0; j<mean[i].length; j++){
				sum += (mean[i][j] - pca_spectrum[j])*(mean[i][j] - pca_spectrum[j]);
			}
			distances[i] = Math.sqrt(sum);
		}
		// lookup the smallest distance
		double smallest = distances[0];
		int index = 0;
		for(int i=1; i<distances.length; i++){
			if(distances[i]<smallest){
				smallest = distances[i];
				index = i;
			}
		}
		// calculate score
		double sum = 0;
		for(int i=0; i<distances.length; i++){
			sum += distances[i];
		}
		double best = (1.0 - (distances[index]/sum));
		double worst = (1.0 - (1.0/(double)(distances.length)));
		double score = (best - worst)/(1 - worst);
		
		return new ClassificationResult(
				classes[index], 
				distances[index], 
				score);
	}
	
	/** calculates the lda coefficient for the discriminant function.
	 * 
	 * @param spectrum the spectrum to calculate the coefficient for
	 * @return a ClassificationResult object
	 */
	public ClassificationResult ldaCoefficient(Spectrum spectrum){
		// check if same length
		if(spectrum.getLength()!=originalMeans.length){
			throw new IllegalArgumentException("Spectrum and Data in the profile do not have the same M/Z range. "
			+ "Please adjust the device.");
		}
		// normalize and center the spectrum
		spectrum.normalizationDivideByMean(originalMean);
		spectrum.center(originalMeans);
		// transform the spectrum into PCA space
		double[] pca_spectrum = PCA.transformSpectrum(spectrum, features);
		
		double[] coefficients = new double[classes.length];
		
		// loop through all classes to pick the same row in the mean array
		for(int i=0; i<classes.length; i++){
			// transpose pca vector and convert to matrix
			double[][] tmp = new double[1][pca_spectrum.length];
			tmp[0] = pca_spectrum;
			tmp = LDA.center(tmp, globalMean);
			Matrix X = new Matrix(tmp);
			Matrix XTransposed = X.transpose();
			
			// convert mean vector of class i to matrix
			double[][] tmp2 = new double[1][mean[i].length];
			tmp2[0] = mean[i];
			Matrix u = new Matrix(tmp2);
			
			// calc first term of equation
			Matrix covarianceMatrix = new Matrix(ldaCovarianceMatrix);
			Matrix firstLeft = u.times(covarianceMatrix);
			Matrix firstRight = firstLeft.times(XTransposed);
			
			// calc second term of equation
			Matrix secondLeft = u.times(0.5);
			Matrix secondMiddle = secondLeft.times(covarianceMatrix);
			Matrix UTransposed = u.transpose();
			Matrix secondRight = secondMiddle.times(UTransposed);
			
			// substract the terms and add the fraction of class i
			coefficients[i] = firstRight.get(0, 0) - secondRight.get(0, 0) + fractions[i];
		}
		
		// find biggest and smallest coefficient
		double biggest = coefficients[0];
		double smallest = coefficients[0];
		int index = 0;
		int indexSmall = 0;
		for(int i=1; i<coefficients.length; i++){
			if(coefficients[i]>biggest){
				biggest = coefficients[i];
				index = i;
			}
			if(coefficients[i]<smallest){
				smallest = coefficients[i];
				indexSmall = i;
			}
		}
		// scale everything up so that the smallest coefficient is at least +1
		double[] coefficientsScore = new double[coefficients.length];
		for(int i=0; i<coefficientsScore.length; i++){
			double scale = 0.0;
			if(coefficients[indexSmall]<1.0){
				scale = 1.0 - coefficients[indexSmall];
			}
			coefficientsScore[i] = coefficients[i] + scale;
		}
		
		// calculate score
		double sum = 0;
		for(int i=0; i<coefficientsScore.length; i++){
			sum += coefficientsScore[i];
		}
		double best = coefficientsScore[index]/sum;
		double worst = (1.0/(double)(coefficientsScore.length));
		double score = (best - worst)/(1 - worst);
		
		return new ClassificationResult(
				classes[index], 
				coefficients[index], 
				score
		);
	}
}
