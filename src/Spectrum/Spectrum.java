package Spectrum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class Spectrum {
	
	private double[] mz;
	private double[] voltage;
	HashMap<Integer, Double> values;
	private String filename;
	private int length;
	
	/**
	 * 
	 * @param mz
	 * @param voltage
	 * @param filename 
	 */
	public Spectrum(double[] mz, double[] voltage, String filename){
		this.mz = mz;
		this.voltage = voltage;
		this.filename = filename;
		length = mz.length;
	}
	
	/**
	 * 
	 * @param path
	 * @param bin
	 * @throws FileNotFoundException
	 * @throws IOException 
	 */
	public Spectrum(String path, int bin) throws FileNotFoundException, IOException{
		readCSV(path, bin);
	}
	
	/**
	 * 
	 * @param path
	 * @param bin
	 * @throws FileNotFoundException
	 * @throws IOException 
	 */
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
		values = new HashMap<>();
		for(int i=0; i<mzTmp2.size(); i++){
			mz[i] = mzTmp2.get(i);
			voltage[i] = voltageTmp2.get(i);
			values.put(mzTmp2.get(i), voltageTmp2.get(i));
		}
		
		String[] pathTmp = path.split(File.separator);
		filename = pathTmp[pathTmp.length-1];
		
		length = mzTmp2.size();
	}

	/**
	 * 
	 * @return 
	 */
	public double[] getMz() {
		return mz;
	}
	
	/**
	 * 
	 * @param index
	 * @return 
	 */
	public double getMZ(int index){
		return mz[index];
	}

	/**
	 * 
	 * @param mz 
	 */
	public void setMz(double[] mz) {
		this.mz = mz;
	}

	/**
	 * 
	 * @return 
	 */
	public double[] getVoltage() {
		return voltage;
	}

	/**
	 * 
	 * @param voltage 
	 */
	public void setVoltage(double[] voltage) {
		this.voltage = voltage;
	}
	
	/**
	 * 
	 * @param index
	 * @return 
	 */
	public double getVoltage(int index) {
		return voltage[index];
	}

	/**
	 * 
	 * @param voltage
	 * @param index 
	 */
	public void setVoltage(double voltage, int index) {
		this.voltage[index] = voltage;
	}
	
	/**
	 * 
	 * @return 
	 */
	public String getFilename(){
		return filename;
	}
	
	/**
	 * 
	 * @return 
	 */
	public int getLength(){
		return length;
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
