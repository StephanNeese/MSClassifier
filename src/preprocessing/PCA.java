package preprocessing;

import Spectrum.SpectraMatrix;
import Spectrum.Spectrum;
import io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import weka.core.matrix.Matrix;

/** This class provides static methods for pca.
 * 
 * @author Stephan Neese
 */
public class PCA {
	
	/** performs a principal component analysis on a SpectraMatrix 
	 * using the QR algorithm
	 * 
	 * @param data the SpectraMatrix
	 * @param varianceCovered the variance that should be covered with the resulting dataset
	 * @return a data matrix as 2d double array with samples in columns and dimensions along rows
	 * @throws Exception 
	 */
	public static PCADataSet performPCAusingQR(SpectraMatrix data, double varianceCovered) throws Exception{
		data.center();
		double[][] covarianceMatrix = calcCovarianceMatrix(data);
		EigenVector[] eigenVectors = QRFactorization(covarianceMatrix);
		EigenVector[] features = choseFeatures(eigenVectors, varianceCovered);
		PCADataSet finalData = getFinalData(features, data, varianceCovered);
		
		return finalData;
	}
	
	/** performs a principal component analysis on a SpectraMatrix
	 * using the NIPALS algorithm
	 * 
	 * @param data the SpectraMatrix
	 * @param dimensions the number of dimensions for the resulting dataset
	 * @return a data matrix as 2d double array with samples in columns and dimensions along rows
	 * @throws Exception 
	 */
	public static PCADataSet performPCAusingNIPALS(SpectraMatrix data, int dimensions) throws Exception{
		data.center();
		Matrix x = new Matrix(data.getData());
		EigenVector[] eig = nipals(x, dimensions);
		PCADataSet finalData = getFinalData(eig, data, 0.0);
		
		return finalData;
	}
	
	/** the NIPALS algorithm to find eigenvectors
	 * 
	 * @param data a matrix with the data
	 * @param count number of eigenvectors to be returned
	 * @return the eigenvectors as array of EigenVector objects
	 * @throws Exception 
	 */
	private static EigenVector[] nipals(Matrix data, int count) throws Exception{
		EigenVector[] eigenvector = new EigenVector[count];
		
		Matrix E = data.copy();
		for(int i=0; i<count; i++){
			Matrix t = getMatrixColumn(E, count);
			Matrix p = new Matrix(E.getColumnDimension(), t.getColumnDimension());
			double tau = 1;
			double tau_old = 0;
			int cnt = 0;
			while(difference(tau, tau_old) > 0.000001 && cnt < E.getRowDimension()){
				tau_old = tau;
				p = E.transpose().times(t).times(1.0 / (t.transpose().times(t)).get(0, 0));
				p= norm(p);
				t = (E.times(p)).times(1.0 / (p.transpose().times(p)).get(0, 0));
				tau = (t.transpose()).times(t).get(0, 0);
				E = E.minus(t.times(p.transpose()));
				cnt++;
			}
			System.out.println("iterations: " + cnt);
			eigenvector[i] = new EigenVector(
				p.getColumnPackedCopy(),
				tau);
		}
		
		
		return eigenvector;
	}
	
	/** calculate the difference between two eigenvalues
	 * from two iterations.
	 * 
	 * @param tau current eigenvalue
	 * @param tau_old eigenvalue from prior iteration
	 * @return the difference as a positive double value
	 */
	private static double difference(double tau, double tau_old){
		if(tau>tau_old){
			return tau-tau_old;
		}else{
			return tau_old-tau;
		}
	}
	
	
	/** norm a vector/matrixColumn to length 1.
	 * 
	 * @param x the matrix to be normed
	 */
	private static Matrix norm(Matrix x){
		double length = x.normF();
		return x.times(1/length);
	}
	
	/** get a column from a matrix
	 * 
	 * @param x the matrix to extract a column from
	 * @param col index number of the column
	 * @return 
	 */
	private static Matrix getMatrixColumn(Matrix x, int col) throws Exception{
		int column[] = {col};
		return x.getMatrix(0, x.getRowDimension()-1, column);
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
	 * using the QR algorithm.
	 * 
	 * @param covarianceMatrix the covariance matrix as 2d double array
	 * @return an array of Eigenvector objects
	 * @throws Exception 
	 */
	private static EigenVector[] QRFactorization(double[][] covarianceMatrix) throws Exception {
		// calculate the eigenvalues and eigenvectors via QR Algorithm
		Matrix covariance = new Matrix(covarianceMatrix);
		double[] eigenValues = new double[covarianceMatrix.length];
		double[][] eigenVectors = new double[covarianceMatrix.length][covarianceMatrix.length];		//[dimension][eigenvector]
//		covariance.eigenvalueDecomposition(eigenVectors, eigenValues);
		eigenvalueDecomposition(new Matrix(covarianceMatrix), eigenVectors, eigenValues);
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
	
	/** 
	* Performs Eigenvalue Decomposition using Householder QR Factorization
	*
	* Matrix must be symmetrical.
	* Eigenvectors are return in parameter V, as columns of the 2D array.
	* (Real parts of) Eigenvalues are returned in parameter d.
	* 
	* this method is part of the WEKA package for machine learning.
	* It is extracted from the class weka.core.Matrix.java
	*
	* @param eigenVectors double array in which the eigenvectors are returned 
	* @param eigenValues array in which the eigenvalues are returned
	* @throws Exception if matrix is not symmetric
	*/
	private static void eigenvalueDecomposition(Matrix m, double[][] eigenVectors, double[] eigenValues) throws Exception{
		
		// old class only worked with symmetric matrices!
		if (!m.isSymmetric())
			throw new Exception("EigenvalueDecomposition: Matrix must be symmetric.");
    
		// perform eigenvalue decomposition
		weka.core.matrix.EigenvalueDecomposition eig = m.eig();
		weka.core.matrix.Matrix v = eig.getV();
		double[] d2 = eig.getRealEigenvalues();
    
		// transfer data
		int nr = m.getRowDimension();
		int nc = m.getColumnDimension();
		for (int i = 0; i < nr; i++)
			for (int j = 0; j < nc; j++)
				eigenVectors[i][j] = v.get(i, j);

		for (int i = 0; i < d2.length; i++)
			eigenValues[i] = d2[i];
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
		// get eigenvectors until variance cover is reached
		// OR the maximum Number of dimensions is reached (max. = 60)
		double varianceCovered = 0;
		int index = eigenVectors.length-1;
		while((varianceCovered/sum) <= variance && (((eigenVectors.length-1) - index)<=60)){
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
	private static PCADataSet getFinalData(
			EigenVector[] features, 
			SpectraMatrix data, 
			double varianceCovered) {
		double[][] originalData = data.getData();
		double[][] originalDataTransposed = transpose(originalData);
		// already transposed when extracted
		double[][] featuresTransposed = new double[features.length][features[0].getData().length];
		for(int i=0; i<features.length; i++){
			featuresTransposed[i] = features[i].getData();
		}
		
		// multiply the matrices
		double[][] finalData = multiply(featuresTransposed, originalDataTransposed);
		String[] classes = findClasses(data.getGroups());
		
		PCADataSet res = new PCADataSet(
				finalData, 
				classes, 
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
	
	/** for a list of classnames of files 
	 * finds all individual classnames
	 * and writes each name once in a result array removing repetitions
	 * 
	 * @param classes list of classnames for files
	 * @return list with the classnames, but each name only once.
	 */
	private static String[] findClasses(String[] classes){
		// get a factor variable for all groups of all samples
		HashMap<String, Integer> classesTmp = new HashMap<>();
		for(int i=0; i<classes.length; i++){
			classesTmp.put(classes[i], 1);
		}
		
		// load classnames into array to ensure the same order everytime
		String[] res = new String[classesTmp.size()];
		int cnt = 0;
		for(Map.Entry<String, Integer> e : classesTmp.entrySet()){
			res[cnt] = e.getKey();
			cnt++;
		}
		
		return res;
	}
}
