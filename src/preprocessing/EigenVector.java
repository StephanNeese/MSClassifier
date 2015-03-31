package preprocessing;

public class EigenVector {
	
	private final double[] data;
	
	public EigenVector(double[] data){
		this.data = data;
	}
	
	public double[] getData(){
		return data;
	}
	
	public double getData(int index){
		return data[index];
	}
}
