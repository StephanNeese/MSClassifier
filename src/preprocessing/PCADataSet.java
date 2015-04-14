package preprocessing;

public class PCADataSet {
	
	private double[][] data;						// data[dimensions][samples]
	private String[] classes;						// classes of the samples
	private double[][] transformedFeatureMatrix;	// [vectors][dimensions]

	/** constructs a PCADataSet from a given array of data 
	 * and a given array of the feature matrix
	 * 
	 * @param data the PCA transformed data 
	 * @param transformedFeatureMatrix the feature matrix used for transformation
	 */
	public PCADataSet(double[][] data, String[] classes, double[][] transformedFeatureMatrix) {
		this.data = data;
		this.classes = classes;
		this.transformedFeatureMatrix = transformedFeatureMatrix;
	}

	/** returns the data
	 * 
	 * @return the data as 2d array
	 */
	public double[][] getData() {
		return data;
	}

	/** sets the data
	 * 
	 * @param data the data as 2d array
	 */
	public void setData(double[][] data) {
		this.data = data;
	}

	/** returns the classes of the samples
	 * 
	 * @return the classes of the samples as string array
	 */
	public String[] getClasses() {
		return classes;
	}

	/** sets the classes of the samples
	 * 
	 * @param classes the classes of the samples as string array
	 */
	public void setClasses(String[] classes) {
		this.classes = classes;
	}

	/** returns the transformed feature matrix
	 * 
	 * @return the feature matrix transformed
	 */
	public double[][] getTransformedFeatureMatrix() {
		return transformedFeatureMatrix;
	}

	/** sets the transformed feature matrix
	 * 
	 * @param transformedFeatureMatrix the feature matrix transformed
	 */
	public void setTransformedFeatureMatrix(double[][] transformedFeatureMatrix) {
		this.transformedFeatureMatrix = transformedFeatureMatrix;
	}
}
