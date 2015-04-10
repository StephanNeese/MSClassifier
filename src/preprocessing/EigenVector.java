package preprocessing;

public class EigenVector implements Comparable<EigenVector> {
	
	private final double[] data;
	private double eigenValue;
	
	public EigenVector(double[] data, double eigenValue){
		this.data = data;
		this.eigenValue = eigenValue;
	}
	
	public double[] getData(){
		return data;
	}
	
	public double getData(int index){
		return data[index];
	}
	
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
}
