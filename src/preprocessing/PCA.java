package preprocessing;

import Spectrum.SpectraMatrix;

public class PCA {
	
	public static SpectraMatrix performPCA(SpectraMatrix data){
		substractMean(data);
		double[][] covarianceMatrix = calcCovarianceMatrix(data);
		EigenVector[] eigenVectors = calcEigenVectors(covarianceMatrix);
		double[] eigenValues = calcEigenValues(eigenVectors, covarianceMatrix);
		EigenVector[] features = choseFeatures(eigenVectors, eigenValues);
		SpectraMatrix transposed = transpose(features, data);
		return transposeBack(features, transposed);
	}

	private static void substractMean(SpectraMatrix data) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private static double[][] calcCovarianceMatrix(SpectraMatrix data) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
