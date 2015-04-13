package preprocessing;

public class EigenVector implements Comparable<EigenVector> {
	
	private final double[] data;
	private double eigenValue;
	
	/**
	 * 
	 * @param data
	 * @param eigenValue 
	 */
	public EigenVector(double[] data, double eigenValue){
		this.data = data;
		this.eigenValue = eigenValue;
	}
	
	/**
	 * 
	 * @return 
	 */
	public double[] getData(){
		return data;
	}
	
	/**
	 * 
	 * @param index
	 * @return 
	 */
	public double getData(int index){
		return data[index];
	}
	
	/**
	 * 
	 * @return 
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
}
