package Spectrum;

/** This class provides all nessessary information of a classification process
 * of a Spectrum with a profile,
 * such as the assigned class from the profile used, distance and the score value.
 * 
 * @author Stephan Neese
 */
public class ClassificationResult {
	
	private final String assignedClass;
	private final double distance;
	private final double score;

	/** constructs a ClassificationResult object
	 * 
	 * @param assignedClass class that was assigned after classification
	 * @param distance the distance from the class
	 * @param score the score for the calculation
	 */
	public ClassificationResult(String assignedClass, double distance, double score) {
		this.assignedClass = assignedClass;
		this.distance = distance;
		this.score = score;
	}

	/** returns the assigned class
	 * 
	 * @return the assigned class
	 */
	public String getAssignedClass() {
		return assignedClass;
	}

	/** returns the calculated distance from the assigned class
	 * 
	 * @return the distance
	 */
	public double getDistance() {
		return distance;
	}

	/** returns the score of the calculation
	 * 
	 * @return the score (a number between 0 and 1)
	 */
	public double getScore() {
		return score;
	}

	@Override
	public String toString() {
		return "ClassificationResult{" + "assignedClass=" + assignedClass + ", distance=" + distance + ", score=" + score + '}';
	}
}
