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
	
	public static void main(String[] args) {
		// testing here
		double mz[] = {80.0, 82.0, 84.0, 86.0, 88.0, 90.0, 92.0, 94.0, 96.0, 98.0, 100.0, 102.0};
		Spectrum x = new Spectrum(
				"/home/wens/exactive/spectrum_2_III.csv",
				null,
				mz,
				"exactive",
				false);
		System.out.println(x);
	}
	
	/** Constructor to create a Spectrum for classification (do not use for Profile creation). 
	 * 
	 * @param path path to csv file
	 * @param group which group this spectrum belongs to (can be null if not known)
	 * @param mz mz range of the profile
	 * @param device mass spec device ( Mini 11 or exactive)
	 * @param log log transformation if true
	 */
	public Spectrum(String path, String group, double[] mz, String device, boolean log){
		this.mz = mz;
		voltage = new double[mz.length];
		this.log = log;
		this.group = group;
		readCSVFromRange(path, mz, device, log, mz[1] - mz[0]);
	}
	
	/** reads the content of a csv file for a given mz range.
	 * 
	 * @param path path to csv file
	 * @param mz mz range to be read in. Non existing bins in this csv will be 0.
	 * @param device mass spec device ( Mini 11 or exactive)
	 * @param log log transformation if true
	 */
	private void readCSVFromRange(String path, double[] mz, String device, boolean log, double binSize){
		File csv = new File(path);
		// column splitter symbol
		String split = "";
		if(device.equals("Mini 11")){
			split = ",";
		}else{
			split = "\t";
		}
		
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
				lineTmp = lines.get(0).split(split);
				double start = Double.parseDouble(lineTmp[0]);
				if(start<mz[0]){
					lines = cutOffStart(lines, mz[0], split);
				}
				// check if data in csv ends later than our given mz range
				lineTmp = lines.get(lines.size()-1).split(split);
				double end = Double.parseDouble(lineTmp[0]);
				if(end>mz[mz.length-1]+binSize){
					lines = cutOffEnd(lines, mz[mz.length-1]+binSize, split);
				}
				
				/** if our csv has a smaller mz range than our profile
				 * we need to fill it up with empty values. 
				 */
				lineTmp = lines.get(0).split(split);
				start = Double.parseDouble(lineTmp[0]);
				if(((start - mz[0]) / binSize) >= 1.0){
					lines = fillEmptyBinsAtStart(lines, mz, split);
				}
				lineTmp = lines.get(lines.size() - 1).split(split);
				end = Double.parseDouble(lineTmp[0]);
				if(((mz[mz.length - 1] + binSize - end) / binSize) >= 1.0){
					lines = fillEmptyBinsAtEnd(lines, mz, split);
				}
				
				/** finally we can bin the data. **/
				binning(lines, split);
				check = true;
			}catch(Exception ex){
				// print nothing
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param data
	 * @param split 
	 */
	private void binning(List<String> data, String split){
		// new mz array including the upper boundry of the last bin
		double[] mz2 = new double[mz.length + 1];
		for(int i=0; i<mz.length; i++){
			mz2[i] = mz[i];
		}
		mz2[mz2.length - 1] = mz[mz.length - 1] + (mz[1] - mz[0]);
		
		// make temp lists to load data into
		double[] mzData = new double[data.size()];
		double[] voltageData = new double[data.size()];
		for(int i=0; i<data.size(); i++){
			String[] tmp = data.get(i).split(split);
			mzData[i] = Double.parseDouble(tmp[0]);
			voltageData[i] = Double.parseDouble(tmp[1].replaceAll("\n", ""));
		}

		int low = -1;
		int cnt = -1;
		for(int i=1; i<mz2.length-1; i++){
			low = cnt+1;
			// increase upper limit if value is still below bin border
			while((cnt+1)<mzData.length && mzData[cnt+1]<mz2[i]){
				cnt++;
			}
			// sum up all values that lie in the mz range of the bin
			if(cnt>-1){
				double sum = 0.0;
				for(int x=low; x<=cnt; x++){
					sum += voltageData[x];
				}
				voltage[i-1] = sum;
			}
		}
		
	}
	
	/**
	 * 
	 * @param data
	 * @param mz
	 * @param split
	 * @return 
	 */
	private List<String> fillEmptyBinsAtStart(List<String> data, double[] mz, String split){
		String[] lineTmp = data.get(0).split(split);
		double start = Double.parseDouble(lineTmp[0]);
		// first csv mz value - first mz value from profile / binsize = how many bins must be filled up
		int diff = (int)((Double.parseDouble(data.get(0).split(split)[0]) - mz[0]) / (mz[1] - mz[0]));
		
		// fill bins
		for(int i=0; i<diff; i++){
			data.add(i, mz[i] + split + "0.0");
		}
		
		return data;
	}
	
	/**
	 * 
	 * @param data
	 * @param mz
	 * @param split
	 * @return 
	 */
	private List<String> fillEmptyBinsAtEnd(List<String> data, double[] mz, String split){
		String[] lineTmp = data.get(data.size() - 1).split(split);
		double end = Double.parseDouble(lineTmp[0]);
		double binSize = (mz[1] - mz[0]);
		// last csv mz value - last mz value from profile / binsize = how many bins must be filled up at end
		int diff = (int)((mz[mz.length-1] + binSize - end) / binSize);
		
		// fill bins at end
		for(int i=diff; i>0; i--){
			data.add(mz[mz.length - i] + split + "0.0");
		}
		
		return data;
	}
	
	/**
	 * 
	 * @param data
	 * @param mz
	 * @param split
	 * @return 
	 */
	private List<String> cutOffStart(List<String> data, double mz, String split){
		// find first index equal or bigger than first mz bin
		int cnt = 0;
		while(Double.parseDouble(data.get(cnt).split(split)[0])<mz){
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
	 * @param split
	 * @return 
	 */
	private List<String> cutOffEnd(List<String> data, double mz, String split){
		// find last element that is smaller or equal to given mz value
		int cnt = data.size()-1;
		while(Double.parseDouble(data.get(cnt).split(split)[0])>mz){
			cnt--;
		}
		
		// push all values below and including cnt on new list
		List<String> res = new ArrayList<>();
		for(int i=0; i<=cnt; i++){
			res.add(data.get(i));
		}
		
		return res;
	}
	
	
	/** constructs a spectrum from a file using a given binsize
	 * and within a given mz range.
	 * 
	 * @param path path to the csv file containing the spectral data
	 * @param group assigned group of this spectrum
	 * @param bin size of a bin
	 * @param device name of the ms device
	 * @param log log transformation of data if true
	 * @param start start of mz range
	 * @param end end of mz range (upper limit of last bin)
	 */
	public Spectrum(String path, String group, double bin, String device, boolean log, double start, double end){
		readCSV(path, bin, device, log, start, end);
		this.group = group;
		this.log = log;
	}
	
	/** reads a csv file and initializes the spectrum object (called in constructor)
	 * 
	 * @param path the complete path to the csv file
	 * @param bin size of a bin
	 * @throws FileNotFoundException
	 * @throws IOException 
	 */
	private void readCSV(String path, double bin, String device, boolean log, double start, double end){
		File csv = new File(path);
		
		/** continuous loop until file can be opened
		* files cant be read when an output stream 
		* of another program is still open.
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
						double mzLine = Double.parseDouble(lineTmp[0]);
						double voltLine = Double.parseDouble(lineTmp[1]);
						if(mzLine>=start && mzLine<=end){
							mzTmp.add(mzLine);
							voltageTmp.add(voltLine);
						}
					}
					// check if we reached the headline of the csv table
					// check after parse => when headline reached only next line is parsed
					if(text.matches("^(Masse).*") ^ text.matches("^(M/Z,Voltage).*")){
						headlineReached = true;
					}
				}
			
				/** create bins **/
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
				}
				
				// loop through the created bins
				// and bin the voltage values
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
					// check for log transformation
					if(log){
						if(voltageTmp2.get(i) > 0){
							voltage[i] = Math.log(voltageTmp2.get(i));
						}else{
							voltage[i] = 0.0;
						}
					}else{
						voltage[i] = voltageTmp2.get(i);
					}
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
				// print nothing
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
