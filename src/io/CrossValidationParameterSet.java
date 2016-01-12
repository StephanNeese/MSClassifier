package io;

/**
 *
 * @author wens
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
