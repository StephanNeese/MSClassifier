package Spectrum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/** This class provides a data structure for a spectrum.
 * 
 * @author Stephan Neese
 */
public class Spectrum {
	
	private double[] mz;			// mz values
	private double[] voltage;		// voltage values
	private String filename;		// filename of csv
	private String group;			// the group of this sample
	private int length;				// num dimensions
	
	/** constructs a spectrum from given values
	 * 
	 * @param mz array with mz values
	 * @param voltage array with voltage values
	 * @param filename the filename as identifier
	 * @param group ssigned group of this spectrum
	 */
	public Spectrum(double[] mz, double[] voltage, String filename, String group){
		this.mz = mz;
		this.voltage = voltage;
		this.filename = filename;
		this.group = group;
		length = mz.length;
	}
	
	/** constructs a spectrum from a file
	 * 
	 * @param path path to the csv file containing the spectral data
	 * @param group assigned group of this spectrum
	 * @param bin size of a bin
	 */
	public Spectrum(String path, String group, double bin, String device){
		readCSV(path, bin, device);
		this.group = group;
	}
	
	/** reads a csv file and initializes the spectrum object (called in constructor)
	 * 
	 * @param path the complete path to the csv file
	 * @param bin size of a bin
	 * @throws FileNotFoundException
	 * @throws IOException 
	 */
	private void readCSV(String path, double bin, String device){
		File csv = new File(path);
		
		/* continuous loop until file can be opened
		* files cant be read when an output stream 
		* of another program is still open
		*/
		boolean check = false;
		while(!check){
			try{
				BufferedReader buff = new BufferedReader(new FileReader(csv));
		
				/** read in **/
				String text = null;
				boolean headlineReached = false;
				ArrayList<Double> mzTmp = new ArrayList<>();
				ArrayList<Double> voltageTmp = new ArrayList<>();
				while ((text = buff.readLine()) != null) {
					// parse values to hashmap
					if(headlineReached && !(text.isEmpty())){
						String[] lineTmp = null;
						// different separators for different machines
						if(device.equals("Mini 11")){
							lineTmp = text.split(",");
						}else{
							lineTmp = text.split("\t");
						}
						mzTmp.add(Double.parseDouble(lineTmp[0])); 
						voltageTmp.add(Double.parseDouble(lineTmp[1]));
					}
					// check if we reached the headline of the csv table
					// check after parse => when headline reached only next line is parsed
					if(text.matches("^(Masse).*") ^ text.matches("^(M/Z,Voltage).*")){
						headlineReached = true;
					}
				}
			
				/** binning **/
				ArrayList<Double> mzTmp2 = new ArrayList<>();
				ArrayList<Double> voltageTmp2 = new ArrayList<>();
				// first bin
				double from = mzTmp.get(0).intValue();
				double to = from + bin;
				// init all bins until the end of our mz temp list
				while(from<=mzTmp.get(mzTmp.size()-1)){
					mzTmp2.add(from);
					voltageTmp2.add(0.0);
					from = to;
					to = from + bin;
//					System.out.println("bin = " + from);
				}
				
//				if(mzTmp.size()>0){
//					for(Double d : mzTmp2){
//						System.out.println("bin = " + d);
//					}
//				}
				// loop through the created bins
				int low = -1;
				int cnt = -1;
				for(int i=1; i<mzTmp2.size(); i++){
					low = cnt+1;
					// increase upper limit if value is still below bin border
					while((cnt+1)<mzTmp.size() && mzTmp.get(cnt+1)<mzTmp2.get(i)){
						cnt++;
					}
					// sum up all values that lie in the mz range of the bin
					if(cnt>-1){
						double sum = 0.0;
						for(int x=low; x<=cnt; x++){
							sum += voltageTmp.get(x);
					    }
						voltageTmp2.set(i-1, sum);
					}
				}
				// last bin should also be filled
				if(cnt<(voltageTmp.size()-1)){
					double sum = 0.0;
					for(int i=cnt+1; i<=voltageTmp.size()-1; i++){
						sum += voltageTmp.get(i);
					}
					voltageTmp2.set(mzTmp2.size()-1, sum);
				}
		
				/** parse from ArrayList to arrays **/
				mz = new double[mzTmp2.size()];
				voltage = new double[voltageTmp2.size()];
				for(int i=0; i<mzTmp2.size(); i++){
					mz[i] = mzTmp2.get(i);
					voltage[i] = voltageTmp2.get(i);
				}
		
				if(System.getProperty("os.name").startsWith("Windows")){
					String[] pathTmp = path.split("\\\\");
					filename = pathTmp[pathTmp.length-1];
				}else{
					String[] pathTmp = path.split("/");
					filename = pathTmp[pathTmp.length-1];
				}
		
				length = mzTmp2.size();
				check = true;
			}catch(Exception ex){
				//ex.printStackTrace();
			}
		}
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
	
	public String getGroup(){
		return group;
	}
	
	/** returns the size of this object in bins
	 * 
	 * @return the size/length
	 */
	public int getLength(){
		return length;
	}
	
	public void setLength(int length){
		this.length = length;
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
			+ "Please adjust the device."
			+ " Corrupted file: " + filename);
		}else{
			for(int i=0; i<voltage.length; i++){
				voltage[i] = voltage[i] - means[i];
			}
		}
	}
}
