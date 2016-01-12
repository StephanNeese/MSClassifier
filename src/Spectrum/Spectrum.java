package Spectrum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	private boolean log;			// log scaling applied
	
	
	/** Constructor to create a Spectrum for classification (do not use for Profile creation). 
	 * 
	 * @param path path to csv file
	 * @param group which group this spectrum belongs to (can be null if not known)
	 * @param mz mz range of the profile
	 * @param device mass spec device ( Mini 11 or exactive)
	 * @param log log transformation if true
	 * @param separator 
	 */
	public Spectrum(String path, String group, double[] mz, double binSize, String device, boolean log, String separator){
		this.mz = mz;
		voltage = new double[mz.length];
		length = mz.length;
		this.log = log;
		this.group = group;
		readCSVFromRange(path, mz, device, log, binSize, separator);
	}
	
	/** reads the content of a csv file for a given mz range.
	 * 
	 * @param path path to csv file
	 * @param mz mz range to be read in. Non existing bins in this csv will be 0.
	 * @param device mass spec device ( Mini 11 or exactive)
	 * @param log log transformation if true
	 */
	private void readCSVFromRange(String path, double[] mz, String device, boolean log, double binSize, String separator){
		if(System.getProperty("os.name").startsWith("Windows")){
			String[] pathTmp = path.split("\\\\");
			filename = pathTmp[pathTmp.length-1];
		}else{
			String[] pathTmp = path.split("/");
			filename = pathTmp[pathTmp.length-1];
		}
		
		File csv = new File(path);
		
		/** continuous loop until file can be opened
		* files cant be read when an output stream 
		* of another program is still open.
		*/
		boolean check = false;
		while(!check){
			try{
				List<String> lines = new ArrayList<>();
				BufferedReader buff = new BufferedReader(new FileReader(csv));
		
				/** read in. **/
				String text = null;
				boolean headlineReached = false;
				while ((text = buff.readLine()) != null) {
					// read and push lines
					if(headlineReached && !(text.isEmpty())){
						lines.add(text);
					}
					// check if we reached the headline of the csv table
					// check after parse => when headline reached only next line is parsed
					if(text.matches("^(Masse).*") || text.matches("^(M/Z,Voltage).*")){
						headlineReached = true;
					}
				}
				
				/** Cut off data, that is smaller or bigger
				 * than the given mz range (we can't use anyway). 
				 */
				
				// check if data in csv starts earlier than our given mz range
				String[] lineTmp;
				lineTmp = lines.get(0).split(separator);
				double start = Double.parseDouble(lineTmp[0]);
				if(start<mz[0]){
					lines = cutOffStart(lines, mz[0], separator);
				}
				// check if data in csv ends later than our given mz range
				lineTmp = lines.get(lines.size()-1).split(separator);
				double end = Double.parseDouble(lineTmp[0]);
				if(end>mz[mz.length-1]+binSize){
					lines = cutOffEnd(lines, mz[mz.length-1]+binSize, separator);
				}
				
				/** if our csv has a smaller mz range than our profile
				 * we need to fill it up with empty values. 
				 */
				lineTmp = lines.get(0).split(separator);
				start = Double.parseDouble(lineTmp[0]);
				if(((start - mz[0]) / binSize) >= 1.0){
					lines = fillEmptyBinsAtStart(lines, mz, binSize, separator);
				}
				lineTmp = lines.get(lines.size() - 1).split(separator);
				end = Double.parseDouble(lineTmp[0]);
				if(((mz[mz.length - 1] + binSize - end) / binSize) >= 1.0){
					lines = fillEmptyBinsAtEnd(lines, mz, binSize, separator);
				}
				
				/** finally we can bin the data. **/
				binning(lines, binSize, separator);
				check = true;
			}catch(Exception ex){
				// print nothing
				ex.printStackTrace();
			}
		}
		
		// remove reference for garbage collection later
		// nessessary to remove files in windows
		csv = null;
	}
	
	/**
	 * 
	 * @param data
	 * @param separator 
	 */
	private void binning(List<String> data, double binSize, String separator){
		// make temp lists to load data into
		double[] mzData = new double[data.size()];
		double[] voltageData = new double[data.size()];
		for(int i=0; i<data.size(); i++){
			String[] tmp = data.get(i).split(separator);
			mzData[i] = Double.parseDouble(tmp[0]);
			voltageData[i] = Double.parseDouble(tmp[1].replaceAll("\n", ""));
		}

		int low = -1;
		int cnt = -1;
		for(int i=0; i<mz.length; i++){
			low = cnt+1;
			// increase upper limit if value is still below bin border mz[i]+binsize
			while((cnt+1)<mzData.length && mzData[cnt+1]<(mz[i]+binSize)){
				cnt++;
			}
			// sum up all values that lie in the mz range of the bin
			double sum = 0.0;
			for(int x=low; x<=cnt; x++){
				sum += voltageData[x];
			}
			if(log){
				if(sum > 0){
					voltage[i] = Math.log(sum);
				}else{
					voltage[i] = 0.0;
				}
			}else{
				voltage[i] = sum;
			}
		}
		
	}
	
	/**
	 * 
	 * @param data
	 * @param mz
	 * @param separator
	 * @return 
	 */
	private List<String> fillEmptyBinsAtStart(List<String> data, double[] mz, double binSize, String separator){
		String[] lineTmp = data.get(0).split(separator);
		double start = Double.parseDouble(lineTmp[0]);
		// first csv mz value - first mz value from profile / binsize = how many bins must be filled up
		int diff = (int)((Double.parseDouble(data.get(0).split(separator)[0]) - mz[0]) / binSize);
		
		// fill bins
		for(int i=0; i<diff; i++){
			data.add(i, mz[i] + separator + "0.0");
		}
		
		return data;
	}
	
	/**
	 * 
	 * @param data
	 * @param mz
	 * @param separator
	 * @return 
	 */
	private List<String> fillEmptyBinsAtEnd(List<String> data, double[] mz, double binSize, String separator){
		String[] lineTmp = data.get(data.size() - 1).split(separator);
		double end = Double.parseDouble(lineTmp[0]);
		// last csv mz value - last mz value from profile / binsize = how many bins must be filled up at end
		int diff = (int)((mz[mz.length-1] + binSize - end) / binSize);
		
		// fill bins at end
		for(int i=diff; i>0; i--){
			data.add(mz[mz.length - i] + separator + "0.0");
		}
		
		return data;
	}
	
	/**
	 * 
	 * @param data
	 * @param mz
	 * @param separator
	 * @return 
	 */
	private List<String> cutOffStart(List<String> data, double mz, String separator){
		// find first index equal or bigger than first mz bin
		int cnt = 0;
		while(Double.parseDouble(data.get(cnt).split(separator)[0])<mz){
			cnt++;
		}
		
		// push all value above and including cnt on new list
		List<String> res = new ArrayList<>();
		for(int i=cnt; i<data.size(); i++){
			res.add(data.get(i));
		}
		
		return res;
	}
	
	/**
	 * 
	 * @param data
	 * @param mz
	 * @param separator
	 * @return 
	 */
	private List<String> cutOffEnd(List<String> data, double mz, String separator){
		// find last element that is smaller or equal to given mz value
		int cnt = data.size()-1;
		while(Double.parseDouble(data.get(cnt).split(separator)[0])>mz){
			cnt--;
		}
		
		// push all values below and including cnt on new list
		List<String> res = new ArrayList<>();
		for(int i=0; i<=cnt; i++){
			res.add(data.get(i));
		}
		
		return res;
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
	
	/** returns wether the data in this spectraMatrix
	 * has been log transformed or not
	 * 
	 * @return true if log transformed, false if not
	 */
	public boolean getLog(){
		return log;
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
			+ "Number of dimensions in spectrum: " + voltage.length + ", number of dimensions in untransformed dataset: " + means.length
			+ ". Please adjust the device."
			+ " Corrupted file: " + filename);
		}else{
			for(int i=0; i<voltage.length; i++){
				voltage[i] = voltage[i] - means[i];
			}
		}
	}
}
