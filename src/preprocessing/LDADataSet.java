package preprocessing;

/** This class is a data container 
 * for data used by the linear discriminant analysis method.
 *
 * @author Stephan Neese
 */
public class LDADataSet {
	
	private double[][] inverseCovarianceMatrix;
	private double[] globalMean;
	private double[] fractions;
	
	/** init an LDADataSet
	 * 
	 * @param inverseCovarianceMatrix the inverse of the pooled cov. matrix
	 * @param globalMean the mean values of the dimensions of the non-centered data
	 * @param fractions the fraction each of the groups amount to the whole dataset
	 */
	public LDADataSet(double[][] inverseCovarianceMatrix, double[] globalMean, double[] fractions){
		this.inverseCovarianceMatrix = inverseCovarianceMatrix;
		this.globalMean = globalMean;
		this.fractions = fractions;
	}

	/** getter for the inverse cov. matrix
	 * 
	 * @return the inverse cov. matrix as 2d double array
	 */
	public double[][] getInverseCovarianceMatrix() {
		return inverseCovarianceMatrix;
	}

	/** sets teh inverse cov. matrix
	 * 
	 * @param inverseCovarianceMatrix the inverse cov. matrix as 2d double array 
	 */
	public void setInverseCovarianceMatrix(double[][] inverseCovarianceMatrix) {
		this.inverseCovarianceMatrix = inverseCovarianceMatrix;
	}

	/** returns the global means of the dimensions
	 * 
	 * @return means of the dimensions
	 */
	public double[] getGlobalMean() {
		return globalMean;
	}

	/** sets the means of the dimensions
	 * 
	 * @param globalMean the mean values of the dimensions
	 */
	public void setGlobalMean(double[] globalMean) {
		this.globalMean = globalMean;
	}

	/** getter for the fractions
	 * 
	 * @return the fractions of all groups
	 */
	public double[] getFractions() {
		return fractions;
	}

	/** sets the fractions 
	 * 
	 * @param fractions the fractions of all groups
	 */
	public void setFractions(double[] fractions) {
		this.fractions = fractions;
	}

	@Override
	public String toString() {
		String res = "";
		res += "Fractions:\n";
		for(int i=0; i<fractions.length; i++){
			res += fractions[i] + "\n";
		}
		res += "\ninv covariance matrix\n";
		for(int i=0; i<inverseCovarianceMatrix.length; i++){
			res += inverseCovarianceMatrix[i][0];
			for(int j=1; j<inverseCovarianceMatrix[i].length; j++){
				res += "\t" + inverseCovarianceMatrix[i][j];
			}
			res += "\n";
		}
		
		return res;
	}
}
