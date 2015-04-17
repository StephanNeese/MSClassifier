package preprocessing;

import Spectrum.SpectraMatrix;
import Spectrum.Spectrum;
import java.util.Arrays;
import weka.core.Matrix;

public class PCA {
	
	/** performs a principal component analysis on a SpectraMatrix
	 * 
	 * @param data the SpectraMatrix
	 * @param varianceCovered the variance that should be covered with the resulting dataset
	 * @return a data matrix as 2d double array with samples in columns and dimensions along rows
	 * @throws Exception 
	 */
	public static PCADataSet performPCA(SpectraMatrix data, double varianceCovered) throws Exception{
		data.center();
		double[][] covarianceMatrix = calcCovarianceMatrix(data);
		EigenVector[] eigenVectors = calcEigenVectors(covarianceMatrix);
		EigenVector[] features = choseFeatures(eigenVectors, varianceCovered);
		PCADataSet finalData = getFinalData(features, data, varianceCovered);
		
		return finalData;
	}

	/** calculates a covariance matrix from the spectra data
	 * 
	 * @param data the SpectraMatrix for which to calculate the covariance matrix
	 * @return covariance matrix as 2d double array
	 */
	private static double[][] calcCovarianceMatrix(SpectraMatrix data) {
		double[][] covariance = new double[data.getNumDimensions()][data.getNumDimensions()];
		
		int n = data.getNumSpectra();
		// loop through dimension twice to create the matrix
		for(int i=0; i<data.getNumDimensions(); i++){
			// get dimension x for covariance calc
			double[] x = data.getDimension(i);
			// calc mean of dimension
			double xMean = 0;
			double xSum = 0;
			for(int k=0; k<n; k++){
				xSum += x[k];
			}
			xMean = xSum/n;
			
			for(int j=0; j<data.getNumDimensions(); j++){
				// get dimension y for covariance calc
				double[] y = data.getDimension(j);
				// calc mean of dimension
				double yMean = 0;
				double ySum = 0;
				for(int k=0; k<n; k++){
					ySum += y[k];
				}
				yMean = ySum/n;
				// calculate covariance
				double num = 0;
				for(int k=0; k<n; k++){
					num += (x[k] - xMean)*(y[k] - yMean);
				}
				covariance[i][j] = num/(n-1);
			}
		}
		
		return covariance;
	}

	/** calculates the eigenvectors and eigenvalues for a covariance matrix
	 * 
	 * @param covarianceMatrix the covariance matrix as 2d double array
	 * @return an array of Eigenvector objects
	 * @throws Exception 
	 */
	private static EigenVector[] calcEigenVectors(double[][] covarianceMatrix) throws Exception {
		// let WEKA Package calculate the eigenvalues and eigenvectors
		Matrix covariance = new Matrix(covarianceMatrix);
		double[] eigenValues = new double[covarianceMatrix.length];
		double[][] eigenVectors = new double[covarianceMatrix.length][covarianceMatrix.length];		//[dimension][eigenvector]
		covariance.eigenvalueDecomposition(eigenVectors, eigenValues);
		// put into data structure
		EigenVector[] res = new EigenVector[covarianceMatrix.length];
		for(int i=0; i<covarianceMatrix.length; i++){
			// get each single eigenvector from the columns
			double[] vector = new double[covarianceMatrix.length];
			for(int j=0; j<covarianceMatrix.length; j++){
				vector[j] = eigenVectors[j][i];
			}
			res[i] = new EigenVector(vector, eigenValues[i]);
		}
		
		return res;
	}

	/** choses as many features as needed to cover a given proportion of the variance
	 * 
	 * @param eigenVectors the array of calculated eigenvectors
	 * @param variance the proportion of variance to be covered
	 * @return an array of the most significant eigenvectors
	 */
	private static EigenVector[] choseFeatures(EigenVector[] eigenVectors, double variance) {
		// sort eigenvectors by eigenvalue
		Arrays.sort(eigenVectors);
		
		// look how much of the variance is covered
		double sum = 0;
		// sum up all eigenvalues
		for(int i=0; i<eigenVectors.length; i++){
			sum += eigenVectors[i].getEigenValue();
		}
		double varianceCovered = 0;
		int index = eigenVectors.length-1;
		while((varianceCovered/sum) <= variance){
			varianceCovered += eigenVectors[index].getEigenValue();
			index--;
		}
		
		// build the feature vector with a selection of the best eigenvectors
		EigenVector[] featureVectors = new EigenVector[(eigenVectors.length-1) - index];
		int cnt = 0;
		for(int i=eigenVectors.length-1; i>index; i--){
			featureVectors[cnt] = eigenVectors[i];
			cnt++;
		}
		
		return featureVectors;
	}

	/** returns the final dataset transformed into PCA space
	 * 
	 * @param features the feature matrix with the chosen eigenvectors
	 * @param data the raw data as SpectraMatrix object
	 * @return the final data as 2d double array 
	 * with samples in columns and dimensions along rows
	 */
	private static PCADataSet getFinalData(EigenVector[] features, SpectraMatrix data, double varianceCovered) {
		double[][] originalData = data.getData();
		double[][] originalDataTransposed = transpose(originalData);
		// already transposed when extracted
		double[][] featuresTransposed = new double[features.length][features[0].getData().length];
		for(int i=0; i<features.length; i++){
			featuresTransposed[i] = features[i].getData();
		}
		
		// multiply the matrices
		double[][] finalData = multiply(featuresTransposed, originalDataTransposed);
		
		PCADataSet res = new PCADataSet(
				finalData, 
				data.getSamples(), 
				featuresTransposed, 
				varianceCovered);
		return res;
	}
	
	/** multiplies two matrices
	 * 
	 * @param a matrix on the left
	 * @param b matrix on the right
	 * @return new matrix as 2d double array
	 */
	private static double[][] multiply(double[][] a, double[][] b){
		double[][] res = new double[a.length][b[0].length];

		for(int rows=0; rows<a.length; rows++){
			for(int cols=0; cols<b[0].length; cols++){
				// multiplication of a cell takes place here
				res[rows][cols] = 0;
				for(int s=0; s<b.length; s++){
					res[rows][cols] += a[rows][s] * b[s][cols];
				}
			}
		}
		
		return res;
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
	
	/** transformes a given spectrum into PCA space using a feature matrix
	 * 
	 * @param spectrum the spectrum to be transformed
	 * @param features the feature matrix to be used
	 * @return the transformed spectrum
	 */
	public static double[] transformSpectrum(Spectrum spectrum, double[][] features){
		double[][] data = new double[1][spectrum.getLength()];
		data[0] = spectrum.getVoltage();
		double[][] dataTransposed = transpose(data);
		
		// transform using the feature matrix
		double[][] tmp = multiply(features, dataTransposed);
		double[] finalData = new double[tmp.length];
		
		for(int i=0; i<tmp.length; i++){
			finalData[i] = tmp[i][0];
		}
		
		return finalData;
	}
}
