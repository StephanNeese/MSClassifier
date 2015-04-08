package preprocessing;

import Spectrum.SpectraMatrix;

public class ProfileBuilder {
	
	public static void build(SpectraMatrix matrix){
		// get each dimension and calculate mean and variance
		for(int i=0; i<matrix.getNumDimensions(); i++){
			double[] dim = matrix.getDimension(i);
			double sum = 0;
			double varSum = 0;
			double mean = 0;
			double variance = 0;
			for(int j=0; j<dim.length; j++){
				sum += dim[j];
			}
			mean = sum/dim.length;
			for(int j=0; j<dim.length; j++){
				varSum += ((dim[j]-mean)*(dim[j]-mean));
			}
			variance = varSum/(dim.length-1);
			System.out.println("Dimension: " + matrix.getSpectrum(0).getMZ(i) + "\tmean: " + mean + "\tvariance: " + variance);
		}
		
		// dimensionen sortieren nach groesse des peaks
		
		// 
	}
}
