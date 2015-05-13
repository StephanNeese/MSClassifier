package preprocessing;

/**
 *
 * @author Stephan Neese
 */
public class LDADataSet {
	
	private double[][] inverseCovarianceMatrix;
	private double[] fractions;
	
	public LDADataSet(double[][] inverseCovarianceMatrix, double[] fractions){
		this.inverseCovarianceMatrix = inverseCovarianceMatrix;
		this.fractions = fractions;
	}

	public double[][] getInverseCovarianceMatrix() {
		return inverseCovarianceMatrix;
	}

	public void setInverseCovarianceMatrix(double[][] inverseCovarianceMatrix) {
		this.inverseCovarianceMatrix = inverseCovarianceMatrix;
	}

	public double[] getFractions() {
		return fractions;
	}

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
