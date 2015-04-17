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
	private String filename;
	private int length;
	
	/** constructs a spectrum from given values
	 * 
	 * @param mz array with mz values
	 * @param voltage array with voltage values
	 * @param filename the filename as identifier
	 */
	public Spectrum(double[] mz, double[] voltage, String filename){
		this.mz = mz;
		this.voltage = voltage;
		this.filename = filename;
		length = mz.length;
	}
	
	/** constructs a spectrum from a file
	 * 
	 * @param path path to the csv file containing the spectral data
	 * @param bin size of a bin
	 * @throws FileNotFoundException
	 * @throws IOException 
	 */
	public Spectrum(String path, int bin) throws FileNotFoundException, IOException{
		readCSV(path, bin);
	}
	
	/** reads a csv file and initializes the spectrum object (called in constructor)
	 * 
	 * @param path the complete path to the csv file
	 * @param bin size of a bin
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
		
		/** parse from ArrayList to arrays **/
		mz = new double[mzTmp2.size()];
		voltage = new double[voltageTmp2.size()];
		for(int i=0; i<mzTmp2.size(); i++){
			mz[i] = mzTmp2.get(i);
			voltage[i] = voltageTmp2.get(i);
		}
		
		String[] pathTmp = path.split(File.separator);
		filename = pathTmp[pathTmp.length-1];
		
		length = mzTmp2.size();
	}

	/** returns the mz values
	 * 
	 * @return mz values
	 */
	public double[] getMz() {
		return mz;
	}
	
	/** returns an mz value for a given index
	 * 
	 * @param index index of the mz value
	 * @return the mz value
	 */
	public double getMZ(int index){
		return mz[index];
	}

	/** sets the mz values for this spectrum
	 * 
	 * @param mz the mz values as double array
	 */
	public void setMz(double[] mz) {
		this.mz = mz;
	}

	/** returns the voltages as array
	 * 
	 * @return the voltage values as double array
	 */
	public double[] getVoltage() {
		return voltage;
	}

	/** sets the voltage values of this object
	 * 
	 * @param voltage the voltage values as array
	 */
	public void setVoltage(double[] voltage) {
		this.voltage = voltage;
	}
	
	/** returns a specific voltage value for this object
	 * 
	 * @param index index of the value
	 * @return the voltage value given by the index
	 */
	public double getVoltage(int index) {
		return voltage[index];
	}

	/** sets a specific voltage value
	 * 
	 * @param voltage the voltage value
	 * @param index the index in the spectrum
	 */
	public void setVoltage(double voltage, int index) {
		this.voltage[index] = voltage;
	}
	
	/** returns the filename of this objects csv data
	 * 
	 * @return the filename
	 */
	public String getFilename(){
		return filename;
	}
	
	/** returns the size of this object in bins
	 * 
	 * @return the size/length
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
	
	/** takes the mean from a spectraMatrix 
	 * and normalizes the spectrum over this mean
	 * 
	 * @param mean the mean from a SpectraMatrix
	 */
	public void normalizationDivideByMean(double mean){
		for(int i=0; i<voltage.length; i++){
			voltage[i] = voltage[i]/mean;
		}
	}
	
	/** takes the means of the dimensions of a specific spectraMatrix
	 * and substracts the mean values from the spectrums values
	 * to center the data along the axes
	 * 
	 * @param means the mean values of the dimensions of a spectraMatrix
	 */
	public void center(double[] means){
		if(voltage.length!=means.length){
			throw new IllegalArgumentException("Spectrum and Data in the profile do not have the same M/Z range. "
			+ "Please adjust the device.");
		}else{
			for(int i=0; i<voltage.length; i++){
				voltage[i] = voltage[i] - means[i];
			}
		}
	}
}
