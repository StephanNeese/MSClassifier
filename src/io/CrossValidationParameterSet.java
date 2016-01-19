package io;

/** This class is a data container for
 * cross validation input parameters
 * from the crossValidationWindow.
 *
 * @author Stephan Neese
 */
public class CrossValidationParameterSet {
	
	public enum Algorithm {QR, NIPALS};
	public final Algorithm algorithm;
	public final String[] paths;
	public final String rootPath;
	public final String background;
	public final String machine;
	public final String cvDir;
	public final String resultsDir;
	public final double binSize;
	public final double variance;
	public final int dimensions;
	public final boolean log;
	public final String separator;

	/** construct a crossValidationParameterSet
	 * 
	 * @param algorithm the algorithm used to find the eigenvectors
	 * @param paths the paths to the groups of foods
	 * @param rootPath the rootpath to the folder containing the food groups
	 * @param background the path to the folder containing the background spectra
	 * @param machine the name of the MS device used
	 * @param cvDir the folder to store the cross validation data in
	 * @param resultsDir the folder for the result files of the cross validation
	 * @param binSize the size of an mz bin
	 * @param variance the covered variance by the PCA (using QR algorithm)
	 * @param dimensions the number of dimensions to transform by PCA (using NIPALS)
	 * @param log will the input data be log tranformed (true if yes)
	 * @param separator the csv column separator
	 */
	public CrossValidationParameterSet(
			Algorithm algorithm, 
			String[] paths, 
			String rootPath, 
			String background, 
			String machine, 
			String cvDir, 
			String resultsDir, 
			String binSize, 
			String variance, 
			String dimensions, 
			boolean log,
			String separator) {
		this.algorithm = algorithm;
		this.paths = paths;
		this.rootPath = rootPath;
		this.background = background;
		this.machine = machine;
		this.cvDir = cvDir;
		this.resultsDir = resultsDir;
		this.binSize = Double.parseDouble(binSize);
		// what algorithm is used
		if(algorithm==Algorithm.QR){
			this.variance = Double.parseDouble(variance);
			this.dimensions = 0;
		}else{
			this.dimensions = Integer.parseInt(dimensions);
			this.variance = 0.0;
		}
		this.log = log;
		// what separator is used for data
		if(separator.equals(",")){
			this.separator = ",";
		}else if(separator.equals(";")){
			this.separator = ";";
		}else{
			this.separator = "\t";
		}
	}
}
