package Spectrum;

public class ClassificationResult {
	
	private final String assignedClass;
	private final double distance;
	private final double score;

	public ClassificationResult(String assignedClass, double distance, double score) {
		this.assignedClass = assignedClass;
		this.distance = distance;
		this.score = score;
	}

	public String getAssignedClass() {
		return assignedClass;
	}

	public double getDistance() {
		return distance;
	}

	public double getScore() {
		return score;
	}

	@Override
	public String toString() {
		return "ClassificationResult{" + "assignedClass=" + assignedClass + ", distance=" + distance + ", score=" + score + '}';
	}
}
