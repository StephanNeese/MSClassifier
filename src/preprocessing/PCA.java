package preprocessing;

import Spectrum.SpectraMatrix;

public class PCA {
	
	public static SpectraMatrix performPCA(SpectraMatrix data){
		data.substractMean();
		double[][] covarianceMatrix = calcCovarianceMatrix(data);
		EigenVector[] eigenVectors = calcEigenVectors(covarianceMatrix);
		double[] eigenValues = calcEigenValues(eigenVectors, covarianceMatrix);
		EigenVector[] features = choseFeatures(eigenVectors, eigenValues);
		SpectraMatrix transposed = transpose(features, data);
		return transposeBack(features, transposed);
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

	private static EigenVector[] calcEigenVectors(double[][] covarianceMatrix) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private static double[] calcEigenValues(EigenVector[] eigenVectors, double[][] covarianceMatrix) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private static EigenVector[] choseFeatures(EigenVector[] eigenVectors, double[] eigenValues) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private static SpectraMatrix transpose(EigenVector[] features, SpectraMatrix data) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private static SpectraMatrix transposeBack(EigenVector[] features, SpectraMatrix transposed) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
