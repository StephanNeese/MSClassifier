package preprocessing;

import Spectrum.SpectraMatrix;
import java.util.Arrays;
import weka.core.Matrix;

public class PCA {
	
	public static void performPCA(SpectraMatrix data, double varianceCovered) throws Exception{
		data.center();
		double[][] covarianceMatrix = calcCovarianceMatrix(data);
		EigenVector[] eigenVectors = calcEigenVectors(covarianceMatrix);
		EigenVector[] features = choseFeatures(eigenVectors, varianceCovered);
		double[][] finalData = getFinalData(features, data);
	}

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
			for(int k=0; k<x.length; k++){
				xSum += x[k];
			}
			xMean = xSum/x.length;
			
			for(int j=0; j<data.getNumDimensions(); j++){
				// get dimension y for covariance calc
				double[] y = data.getDimension(j);
				// calc mean of dimension
				double yMean = 0;
				double ySum = 0;
				for(int k=0; k<y.length; k++){
					ySum += y[k];
				}
				yMean = ySum/y.length;
				// calculate covariance
				double num = 0;
				for(int k=0; k<x.length; k++){
					num += (x[k] - xMean)*(y[k] - yMean);
				}
				covariance[i][j] = num/(n-1);
			}
		}
		
		return covariance;
	}

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

	private static double[][] getFinalData(EigenVector[] features, SpectraMatrix data) {
		double[][] originalData = data.getData();
		double[][] originalDataTransposed = transpose(originalData);
		// already transposed when extracted
		double[][] featuresTransposed = new double[features.length][features[0].getData().length];
		for(int i=0; i<features.length; i++){
			featuresTransposed[i] = features[i].getData();
		}
		
		// multiply the matrices
		double[][] finalData = new double[features.length][data.getNumSpectra()];
		
		
		return finalData;
	}
	
	private static double[][] transpose(double[][] matrix){
		double[][] res = new double[matrix[0].length][matrix.length];
		
		for(int i=0; i<matrix.length; i++){
			for(int j=0; j<matrix[0].length; j++){
				res[j][i] = matrix[i][j];
			}
		}
		
		return res;
	}

	private static SpectraMatrix transposeBack(EigenVector[] features, SpectraMatrix transposed) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
