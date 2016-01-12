package preprocessing;

/** This class provides a data structure
 * for an eigen vector.
 * 
 * @author Stephan Neese
 */
public class EigenVector implements Comparable<EigenVector> {
	
	private final double[] data;		// dimensions of vector
	private double eigenValue;
	
	/** consructs a EigenVector from a data array and an eigenvalue
	 * 
	 * @param data double array with the data of the dimensions
	 * @param eigenValue the corresponding eigenvalue for the vector
	 */
	public EigenVector(double[] data, double eigenValue){
		this.data = data;
		this.eigenValue = eigenValue;
	}
	
	/** returns the data array
	 * 
	 * @return the data as double array
	 */
	public double[] getData(){
		return data;
	}
	
	/** returns a specific element from the data array
	 * 
	 * @param index the index for the element
	 * @return the element as double
	 */
	public double getData(int index){
		return data[index];
	}
	
	/** returns the eigenvalue of the eigenvector
	 * 
	 * @return the eigenvalue
	 */
	public double getEigenValue(){
		return eigenValue;
	}

	@Override
	public int compareTo(EigenVector o) {
		if(eigenValue>o.getEigenValue()){
			return 1;
		}else if(eigenValue==o.getEigenValue()){
			return 0;
		}else{
			return -1;
		}
	}
	
	@Override
	public String toString(){
		String res = "";
		
		res += eigenValue + "\nValues:";
		for(double d : data){
			res += "\t" + d;
		}
		
		return res;
	}
	
	public void print(){
		System.out.println(eigenValue + "\nValues:");
		for(double d : data){
			System.out.print("\t" + d);
		}
		System.out.println("");
	}
}
