package Spectrum;

import java.util.ArrayList;
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
	private final boolean log;				// log transformation of data or not?
	/** the variable mzBackground is from an old version of this program
	* bins and mzBackground contain the same numbers now as the background
	* is now adjusted to the mz length of the main data
	* removing it makes a lot of refactoring nessessary though. **/
	private final double[] mzBackground;	// mz bins for the background data substracted from this profiles raw data
	private final double[] voltBackground;	// voltage for the background data substracted from this profiles raw data
	private final String[] sampleGroups;	// sampleGroups of original csv files 
	private final double[][] data;			// pca transformed data [dimensions][samples]
	private final double[][] features;		// feature vector (used for transformation) [vectors][dimensions]
	private final double mzStart;
	private final double mzEnd;
	private final double[] bins;			// all m/z bins
	// inv. cov. matrices of classes
	private HashMap<String, double[][]> invertedCovarianceMatrices;
	private final double[][] mean;			// [class][dimension]
	private final double[] originalMeans;	// mean of dimensions of untransformed dataset
	private final double originalMean;		// mean of all spectras and dimensions of untransformed
	private final double binSize;			// size of a bin
	// lda data
	private final double[][] ldaCovarianceMatrix;	// pooled cov. matrix
	private final double[] globalMean;				// means of pca dimensions - used for centering sample
	private final double[] fractions;				// fraction of samples of all samples that belong to this dataset
													// a single group
	
	private final String separator;					// colum separator for csv files
	private final String algorithm;					// name of the used algorithm to find eigenvectors
	private final String comment;					// a comment string - write in it whatever you want

	/** constructs a Profile Object
	 * 
	 * @param classes the names of the classes
	 * @param datetime the date and time
	 * @param device the name of the device
	 * @param path the path to the csv files the profile has been constructed from
	 * @param variance the amount of covered variance by the PCA
	 * @param log boolean value to indicate if original data is log transformed (true if yes)
	 * @param mzBackground the mz bins of the background data
	 * @param voltBackground the intensities of the background data
	 * @param sampleGroups the groups of each input spectra - same order as samples in data matrix
	 * @param data pca transformed data
	 * @param features the feature matrix
	 * @param mzStart the value of the first mz bin
	 * @param mzEnd the value of the last mz bin
	 * @param bins all mz bins in an array
	 * @param invertedCovarianceMatrices the inverted covariance matrices of the pca data classes
	 * @param mean the mean values (centroids) of the pca data
	 * @param originalMeans mean values of the dimensions of the original data
	 * @param originalMean mean value of the mean dataset (all dimensions)
	 * @param binSize size of a bin in u
	 * @param ldaCovarianceMatrix the covariance matrix for the linear discriminant analysis
	 * @param globalMean means of pca dimensions - used for centering sample
	 * @param fractions fraction of samples of all samples that belong to this dataset
	 * @param separator colum separator for csv files
	 * @param algorithm name of the used algorithm to find eigenvectors
	 * @param comment a comment string - write in it whatever you want
	 */
	public Profile(
			String[] classes, 
			Date datetime, 
			String device, 
			String path, 
			double variance, 
			boolean log,
			double[] mzBackground,
			double[] voltBackground,
			String[] sampleGroups, 
			double[][] data, 
			double[][] features,
			double mzStart,
			double mzEnd,
			double[] bins,
			HashMap<String, double[][]> invertedCovarianceMatrices,
			double[][] mean,
			double[] originalMeans,
			double originalMean,
			double binSize,
			double[][] ldaCovarianceMatrix,
			double[] globalMean,
			double[] fractions,
			String separator,
			String algorithm,
			String comment) {
		this.classes = classes;
		this.datetime = datetime;
		this.device = device;
		this.path = path;
		this.variance = variance;
		this.log = log;
		this.mzBackground = mzBackground;
		this.voltBackground = voltBackground;
		this.sampleGroups = sampleGroups;
		this.data = data;
		this.features = features;
		this.mzStart = mzStart;
		this.mzEnd = mzEnd;
		this.bins = bins;
		this.invertedCovarianceMatrices = invertedCovarianceMatrices;
		this.mean = mean;
		this.originalMeans = originalMeans;
		this.originalMean = originalMean;
		this.binSize = binSize;
		this.ldaCovarianceMatrix = ldaCovarianceMatrix;
		this.globalMean = globalMean;
		this.fractions = fractions;
		this.separator = separator;
		this.algorithm = algorithm;
		this.comment = comment;
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

	/** returns all the sampleGroups of the original csv files
	 * 
	 * @return the csv file names as array
	 */
	public String[] getSampleGroups() {
		return sampleGroups;
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
	 * @return a hash with class(string) mapped to inv-covariancematrix(double[][])
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
	
	/** returns the boolean value to declare if
	 * log transformation was applied on this profiles data.
	 * 
	 * @return boolean true if log transformation was applied, false otherwise
	 */
	public boolean getLog(){
		return log;
	}
	
	/** getter for the first mz bin
	 * 
	 * @return the value of the first mz bin
	 */
	public double getMzStart(){
		return mzStart;
	}
	
	/** getter for the last mz bin
	 * 
	 * @return the value of the last mz bin
	 */
	public double getMzEnd(){
		return mzEnd;
	}
	
	/** getter for the mz bin array
	 * 
	 * @return all mz bins as array
	 */
	public double[] getMzBins(){
		return bins;
	}
	
	/** getter for the csv column separator
	 * 
	 * @return the column separator
	 */
	public String getSeparator(){
		return separator;
	}
	
	/** getter for the name of the algorithm used to find the eigenvector
	 * for PCA.
	 * 
	 * @return the name of the algorithm
	 */
	public String getAlgorithm(){
		return algorithm;
	}
	
	/** getter for the comment string of the profile
	 * 
	 * @return the comment string
	 */
	public String getComment(){
		return comment;
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
		// preprocessing of spectrum to adjust range and delete bins
		// keep this order even if changes in the method are made
		spectrum.normalizationDivideByMean(originalMean);
		substractBackgroundFromSpectrum(spectrum);
		deleteEmptyBins(spectrum);
		
		// normalize and center the spectrum
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
		// preprocessing of spectrum to adjust range and delete bins
		// keep this order even if changes in the method are made
		spectrum.normalizationDivideByMean(originalMean);
		substractBackgroundFromSpectrum(spectrum);
		deleteEmptyBins(spectrum);
		
		// normalize and center the spectrum
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
		// preprocessing of spectrum to adjust range and delete bins
		// keep this order even if changes in the method are made
		spectrum.normalizationDivideByMean(originalMean);
		substractBackgroundFromSpectrum(spectrum);
		deleteEmptyBins(spectrum);
		
		// normalize and center the spectrum
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
	
	
	/** Deletes all the bins in the spectrum that are not in the
	 * profile. These bins are missing 
	 * because they were empty on creation of the profile
	 * 
	 * @param spectrum the spectrum to delete bins from
	 */
	public void deleteEmptyBins(Spectrum spectrum){
		double[] mzSpec = spectrum.getMz();
		double[] voltSpec = spectrum.getVoltage();
		HashMap<Double, Boolean> exists = new HashMap<>();
		// load all values from spectrum into hash
		for(int i=0; i<mzSpec.length; i++){
			exists.put(mzSpec[i], false);
		}
		// check if they are in the profile
		for(int i=0; i<bins.length; i++){
			if(exists.containsKey(bins[i])){
				exists.put(bins[i], true);
			}
		}
		// load existing into tmp variables and then convert to arrays
		ArrayList<Double> mzTmp = new ArrayList<>();
		ArrayList<Double> voltTmp = new ArrayList<>();
		for(int i=0; i<mzSpec.length; i++){
			if(exists.get(mzSpec[i])){
				mzTmp.add(mzSpec[i]);
				voltTmp.add(voltSpec[i]);
			}
		}
		// convert to Arrays
		double[] mzFinal = new double[mzTmp.size()];
		double[] voltFinal = new double[voltTmp.size()];
		for(int i=0; i<mzTmp.size(); i++){
			mzFinal[i] = mzTmp.get(i);
			voltFinal[i] = voltTmp.get(i);
		}
		
		spectrum.setMz(mzFinal);
		spectrum.setVoltage(voltFinal);
	}
	
	/** substracts the background data from a given spectrum.
	 * 
	 * @param spectrum the spectrum to process
	 */
	private void substractBackgroundFromSpectrum(Spectrum spectrum){
		double[] mz = spectrum.getMz();
		double[] voltage = spectrum.getVoltage();
		
//		// find beginning of background bins in this matrix
//		int indexMatrixStart = 0;
//		int indexBGStart = 0;
//		if((int)((mz[0] - mzBackground[0])/(mz[1] - mz[0]))>0){
//			// background bins start earlier
//			indexBGStart = (int) ((mz[0] - mzBackground[0])/(mz[1] - mz[0]));
//		}else if((int)((mz[0] - mzBackground[0])/(mz[1] - mz[0]))<0){
//			// background bins start later
//			indexMatrixStart = (int)((mz[0] - mzBackground[0])/(mz[1] - mz[0]));
//		}
//		// find end of background bins in this matrix
//		int EndIndex = 0;
//		if((mzBackground.length + indexBGStart) >= (mz.length + indexMatrixStart)){
//			EndIndex = mz.length-1;
//		}else{
//			EndIndex = mzBackground.length + indexBGStart;
//		}
//		
//		// use found starting and ending points in matrices to substract the background
//		for(int i=0; i<EndIndex; i++){
//			voltage[indexMatrixStart + i] -= voltBackground[indexBGStart + i];
//		}

		for(int i=0; i<mzBackground.length; i++){
			if((voltage[i] - voltBackground[i]) >= 0){
				voltage[i] -= voltBackground[i];
			}else{
				voltage[i] = 0.0;
			}
		}
		
		spectrum.setMz(mz);
		spectrum.setVoltage(voltage);
	}
}
