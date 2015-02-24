
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Spectrum {
	
	private double[] mz;
	private double[] voltage;
	
	public Spectrum(String path, int bin) throws FileNotFoundException, IOException{
		readCSV(path, bin);
	}
	
	private void readCSV(String path, int bin) throws FileNotFoundException, IOException{
		File csv = new File(path);
		BufferedReader buff = new BufferedReader(new FileReader(csv));
		
		/** read in **/
		String text = null;
		boolean headlineReached = false;
		ArrayList<Double> mzTmp = new ArrayList<>();
		ArrayList<Double> voltageTmp = new ArrayList<>();
		while ((text = buff.readLine()) != null) {
			// parse values to hashmap
			if(headlineReached && !(text.isEmpty())){
				String[] lineTmp = text.split(",");
				mzTmp.add(Double.parseDouble(lineTmp[0])); 
				voltageTmp.add(Double.parseDouble(lineTmp[1]));
			}
			// check if we reached the headline of the csv table
			// check after parse => when headline reached only next line is parsed
			if(text.matches("^(M/Z,Voltage).*")){
				headlineReached = true;
			}
			
		}
		
		/** binning **/
		ArrayList<Integer> mzTmp2 = new ArrayList<>();
		ArrayList<Double> voltageTmp2 = new ArrayList<>();
		// add first element to list
		int number = mzTmp.get(0).intValue();
		mzTmp2.add(number);
		voltageTmp2.add(voltageTmp.get(0));
		for(int i=1; i<mzTmp.size(); i++){
			// if we reach a higher mz value make new bin
			if(number+bin<=mzTmp.get(i).intValue()){
				number = mzTmp.get(i).intValue();
				mzTmp2.add(number);
				voltageTmp2.add(voltageTmp.get(i));
			}else{
				// add up the values in one bin
				double tmp = voltageTmp2.get(mzTmp2.indexOf(number)) + voltageTmp.get(i);
				voltageTmp2.set(mzTmp2.indexOf(number), tmp);
			}
		}
		
		/** parse from hashmap to arrays **/
		mz = new double[mzTmp2.size()];
		voltage = new double[voltageTmp2.size()];
		for(int i=0; i<mzTmp2.size(); i++){
			mz[i] = mzTmp2.get(i);
			voltage[i] = voltageTmp2.get(i);
		}
	}
	
	
	public void normalizationMeanSubstraction(){
		double sum = 0.0;
		double mean = 0.0;
		// calculate mean of intensities
		for(int i=0; i<voltage.length; i++){
			sum += voltage[i];
		}
		mean = sum/voltage.length;
		
		// substract mean from every value in voltage
		// or set 0 if negative value would arise
		for(int i=0; i<voltage.length; i++){
			if((voltage[i] - mean)>0){
				voltage[i] = voltage[i] - mean;
			}else{
				voltage[i] = 0;
			}
		}
	}

	public double[] getMz() {
		return mz;
	}

	public void setMz(double[] mz) {
		this.mz = mz;
	}

	public double[] getVoltage() {
		return voltage;
	}

	public void setVoltage(double[] voltage) {
		this.voltage = voltage;
	}

	@Override
	public String toString() {
		String res = "m/z\tvoltage\n";
		
		for(int i=0; i<mz.length; i++){
			res += mz[i] + "\t" + voltage[i] + "\n";
		}
		
		return res;
	}
}
