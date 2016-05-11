package Spectrum;

import io.Reader;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

/** This class provides a datastructure for an array of
 * spectras. During construction of the object the spectras
 * are binned. 
 * 
 * @author Stephan Neese
 */
public class SpectraMatrix {
	
	private double[] mz;				// mz values (dimensions)
	private String[] groups;			// the groups of the samples
	private double[][] voltage;			// [spectrum][dimension/bin]
	private final int numSpectra;		// no of spectras
	private int numDimensions;			// no of dimensions (mz bins)
	private double mean;				// mean of all values in the matrix
	private double[] dimensionsMean;	// mean for each dimension
	private boolean log;				// logarithmic scaling or not
	private double[] mzBackground;
	private double[] voltBackground;	// voltage values of the background
	
	/** constructs a SpectraMatrix from a Spectrum array
	 * 
	 * @param spectra the spectrum array
	 * @param bin the size of a bin
	 */
	public SpectraMatrix(Spectrum[] spectra, double bin){
		numSpectra = spectra.length;
		groups = new String[numSpectra];
		log = spectra[0].getLog();
		
		// obtain the smallest bin and the biggest bin from all spectra
		double smallest = spectra[0].getMz()[0];
		double biggest = spectra[0].getMz()[spectra[0].getLength()-1];
		for(int i=1; i<spectra.length; i++){
			if(smallest>spectra[i].getMz()[0]){
				smallest = spectra[i].getMz()[0];
			}
			double[] tmp = spectra[i].getMz();
			if(biggest<tmp[tmp.length-1]){
				biggest = tmp[tmp.length-1];
			}
		}
		// write mz bins
		double currentBin = smallest;
		ArrayList<Double> binTmp = new ArrayList<>();
		while(currentBin<=biggest){
			binTmp.add(currentBin);
			currentBin += bin;
		}
		mz = new double[binTmp.size()];
		for(int i=0; i<binTmp.size(); i++){
			mz[i] = binTmp.get(i);
		}
		
		// write number of dimensions
		numDimensions = mz.length;
		
		// init the groups for each sample
		for(int i=0; i<spectra.length; i++){
			groups[i] = spectra[i].getGroup();
		}
		
		// init voltage array
		voltage = new double[numSpectra][numDimensions];
		for(int i=0; i<numSpectra; i++){
			for(int j=0; j<numDimensions; j++){
				voltage[i][j] = 0.0;
			}
		}
		
		// write voltage values
		for(int i=0; i<numSpectra; i++){
			// calc the offset of the beginning of the spectrum
			double[] volt = spectra[i].getVoltage();
			double[] mass = spectra[i].getMz();
			Double diffTmp = (mass[0] - mz[0])/bin;
			// inserting the first element from spectrum at this index
			int diff = (int)Math.round(diffTmp);
			for(int j=0; j<volt.length; j++){
				voltage[i][j+diff] = volt[j];
			}
		}
		
		// init background as empty
		mzBackground = Arrays.copyOf(mz, mz.length);
		voltBackground = new double[mz.length];
		for(int i=0; i<mz.length; i++){
			voltBackground[i] = 0.0;
		}
		
		// mean centering
		mean = calculateMean();
		this.normalizationDivideByMean();
		calculateDimensionMeans();
	}
	
	
	/** constructs a SpectraMatrix from a Spectrum array
	 * and substracts the background data of another matrix
	 * 
	 * @param spectra the spectrum array
	 * @param background the background matrix
	 * @param bin the binSize of booth matrices
	 */
	public SpectraMatrix(Spectrum[] spectra, SpectraMatrix background, double bin){
		numSpectra = spectra.length;
		groups = new String[numSpectra];
		log = spectra[0].getLog();
		// init background as null
		mzBackground = null;
		voltBackground = null;
		
		// obtain the smallest bin and the biggest bin from all spectra
		double smallest = spectra[0].getMz()[0];
		double biggest = spectra[0].getMz()[spectra[0].getLength()-1];
		for(int i=1; i<spectra.length; i++){
			if(smallest>spectra[i].getMz()[0]){
				smallest = spectra[i].getMz()[0];
			}
			double[] tmp = spectra[i].getMz();
			if(biggest<tmp[tmp.length-1]){
				biggest = tmp[tmp.length-1];
			}
		}
		// write mz bins
		double currentBin = smallest;
		ArrayList<Double> binTmp = new ArrayList<>();
		while(currentBin<=biggest){
			binTmp.add(currentBin);
			currentBin += bin;
		}
		mz = new double[binTmp.size()];
		for(int i=0; i<binTmp.size(); i++){
			mz[i] = binTmp.get(i);
		}
		
		// write number of dimensions
		numDimensions = mz.length;
		
		// init the groups for each sample
		for(int i=0; i<spectra.length; i++){
			groups[i] = spectra[i].getGroup();
		}
		
		// init voltage array
		voltage = new double[numSpectra][numDimensions];
		for(int i=0; i<numSpectra; i++){
			for(int j=0; j<numDimensions; j++){
				voltage[i][j] = 0.0;
			}
		}
		// write voltage values
		for(int i=0; i<numSpectra; i++){
			// calc the offset of the beginning of the spectrum
			double[] volt = spectra[i].getVoltage();
			double[] mass = spectra[i].getMz();
			Double diffTmp = (mass[0] - mz[0])/bin;
			// inserting the first element from spectrum at this index
			int diff = (int)Math.round(diffTmp);
			for(int j=0; j<volt.length; j++){
				voltage[i][j+diff] = volt[j];
			}
		}
		
		// mz bins are also mz bins for background 
		mzBackground = Arrays.copyOf(mz, mz.length);
		
		// mean centering
		mean = calculateMean();
		this.normalizationDivideByMean();
		calculateDimensionMeans();
		// substract the background from all the values
		substractBackground(background);
	}
	
	/** Normalizes the data by division of each value
	 * by the mean over the whole matrix
	 */
	private void normalizationDivideByMean(){
		// divide all values by mean
		for(int i=0; i<voltage.length; i++){
			for(int j=0; j<voltage[0].length; j++){
				voltage[i][j] = voltage[i][j]/mean;
			}
		}
	}
	
	/** calculates the mean value over the whole SpectraMatrix
	 * 
	 * @return the mean value
	 */
	private double calculateMean(){
		double sum = 0;
		int num = voltage.length * voltage[0].length;
		// calculate mean of all values in the matrix
		for(int i=0; i<voltage.length; i++){
			for(int j=0; j<voltage[0].length; j++){
				sum += voltage[i][j];
			}
		}
		return sum/num;
	}
	
	/** calculates the mean values for all dimensions
	 * represented in the spectraMatrix.
	 * 
	 */
	public void calculateDimensionMeans(){
		dimensionsMean = new double[voltage[0].length];
		// loop though dimensions
		for(int dim=0; dim<voltage[0].length; dim++){
			dimensionsMean[dim] = calculateMean(dim);
		}
	}
	
	/** calculates the mean for a given dimension
	 * 
	 * @param dimension the index of the dimension
	 * @return the mean as double
	 */
	private double calculateMean(int dimension){
		double sum = 0;
		int num = voltage.length;
		// calculate mean of all values in the matrix
		for(int i=0; i<voltage.length; i++){
			sum += voltage[i][dimension];
		}
		return sum/num;
	}
	
	/** centers the dataset for PCA processing.
	 * 
	 */
	public void center(){
		// substract dimension-mean value from all dimensions
		for(int dim=0; dim<voltage[0].length; dim++){
			for(int sampl=0; sampl<voltage.length; sampl++){
				voltage[sampl][dim] = voltage[sampl][dim] - dimensionsMean[dim];
			}
		}
	}
	
	/** searches for bins in the SpectraMatrix
	 * that are empty among all spectras 
	 * and deletes them. <b>CAUTION: For consistency always
	 * call this method after substractBackground(SpectraMatrix background) </b>
	 * 
	 */
	public void deleteEmptyBins(){
		ArrayList<Double> mzTmp = new ArrayList<>();
		double[][] voltTmp = new double[voltage.length][voltage[0].length];
		
		int cnt = 0;
		for(int mzCnt=0; mzCnt<voltage[0].length; mzCnt++){
			// loop through all spectras for this mz bin
			// if the bin is not 0.0000001 in at least one spectra
			// then its not empty
			boolean empty = true;
			for(int spec=0; spec<voltage.length; spec++){
				if(voltage[spec][mzCnt]>0.0000001){
					// not empty
					empty = false;
				}
			}
			// if the bin is empty do not load it into tmp variables
			if(!empty){
				mzTmp.add(mz[mzCnt]);
				for(int spec=0; spec<voltage.length; spec++){
					voltTmp[spec][cnt] = voltage[spec][mzCnt];
				}
				cnt++;
			}
		}
		
		// load tmp variables into class variables
		mz = new double[mzTmp.size()];
		for(int i=0; i<mzTmp.size(); i++){
			mz[i] = mzTmp.get(i);
		}
		voltage = new double[voltTmp.length][cnt];
		for(int spec=0; spec<voltTmp.length; spec++){
			for(int mzCnt=0; mzCnt<cnt; mzCnt++){
				voltage[spec][mzCnt] = voltTmp[spec][mzCnt];
			}
		}
		
		// new overall mean value
		calculateMean();
		
		// new number of dimensions
		numDimensions = cnt;
	}
	
	/** returns the mz array
	 * 
	 * @return mz values as double array
	 */
	public double[] getMz(){
		return mz;
	}
	
	/** returns the groups for each spectrum of the spectra matrix
	 * 
	 * @return the groups as string array
	 */
	public String[] getGroups(){
		return groups;
	}
	
	/** sets a spectrum at a given index
	 * 
	 * @param spectrum the spectrum object
	 * @param index the index in the spectraMatrix
	 */
	public void setSpectrum(Spectrum spectrum, int index){
		voltage[index] = spectrum.getVoltage();
	}
	
	/** returns a spectrum for a given index
	 * 
	 * @param index the index in the spectraMatrix
	 * @return the specified spectrum
	 */
//	public Spectrum getSpectrum(int index){
//		return new Spectrum(mz, voltage[index], samples[index], groups[index]);
//	}
	
	/** returns a dimension given by an index
	 * 
	 * @param index the index of the dimension
	 * @return the specified dimension as double array
	 */
	public double[] getDimension(int index){
		double[] res = new double[voltage.length];
		for(int i=0; i<voltage.length; i++){
			res[i] = voltage[i][index];
		}
		return res;
	}

	/** returns the number of spectra in the matrix
	 * 
	 * @return the number of spectra
	 */
	public int getNumSpectra() {
		return numSpectra;
	}

	/** returns the number of dimensions in the matrix
	 * 
	 * @return the number of dimensions
	 */
	public int getNumDimensions() {
		return numDimensions;
	}
	
	/** returns the data as a 2d array 
	 * in the form [spectrum][mz-value]
	 * 
	 * @return the data array
	 */
	public double[][] getData(){
		return voltage;
	}

	/** returns the mean value for the data
	 * 
	 * @return the mean value
	 */
	public double getMean() {
		return mean;
	}

	/** returns the mean values for the dimensions
	 * 
	 * @return the mean values as array
	 */
	public double[] getDimensionsMean() {
		return dimensionsMean;
	}
	
	/** returns wether the data in this spectraMatrix
	 * has been log transformed or not
	 * 
	 * @return true if log transformed, false if not
	 */
	public boolean getLog(){
		return log;
	}
	
	/** getter for the mz bins of the background data
	 * 
	 * @return the mz bin array
	 */
	public double[] getMzBackground(){
		return mzBackground;
	}
	
	/** getter for the intensities of the background data
	 * 
	 * @return the intensity array
	 */
	public double[] getVoltBackground(){
		return voltBackground;
	}
	

	@Override
	public String toString() {
		String res = "";
		for(int bin=0; bin<mz.length; bin++){
			res += "\t" + mz[bin];
		}
		res += "\n";
		for(int spec=0; spec<voltage.length; spec++){
			res += "Spectrum " + spec;
			for(int mzVal=0; mzVal<voltage[spec].length; mzVal++){
				res += "\t" + voltage[spec][mzVal];
			}
			res += "\n";
		}
		
		return res;
	}

	
	/** writes the spectraMatrix to a csv file
	 * 
	 * @param path the complete path to the csv file
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException 
	 */
	public void toCSV(String path) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter(path, "UTF-8");
		// header with mz bins
		for(int i=0; i<mz.length; i++){
			writer.print("\t" + mz[i]);
		}
		writer.println();
		
		// datatable
		for(int spec=0; spec<voltage.length; spec++){
			writer.print(groups[spec]);
			for(int mzVal=0; mzVal<voltage[0].length; mzVal++){
				writer.print("\t" + voltage[spec][mzVal]);
			}
			writer.println();
		}
		
		writer.close();
	}
	
	
	/** substracts the given background matrix from this spectraMatrix object.
	 * It is nessessary for that the two matrices have the same mz range
	 * for this function to work.
	 * 
	 * @param background the background matrix to substract 
	 */
	private void substractBackground(SpectraMatrix background){
		double[] mzBg = background.getMz();
		voltBackground = background.getDimensionsMean();
		// check if the background matrix has the same mz range as our data
		if(mzBg[0]!=mz[0] || mzBg[mzBg.length-1]!=mz[mz.length-1]){
			throw new IllegalArgumentException("background mz range: " + mzBg[0] + " to " + mzBg[mzBg.length-1]
				+ "\ndata mz range: " + mz[0] + " to " + mz[mz.length-1]);
		}else{
			// if booth have the same mz range then proceed
			for(int dim=0; dim<voltage.length; dim++){
				for(int i=0; i<mz.length; i++){
					// substract
					if((voltage[dim][i] - voltBackground[i])>=0){
						voltage[dim][i] -= voltBackground[i];
					}else{
						voltage[dim][i] = 0.0;
					}
				}
			}
		}	
	}
	
	/** prints the values of this matrix to the console.
	 * This method was used for testing purposes
	 * as it is faster on a huge matrix than calling toString().
	 * 
	 */
	public void printMatrix(){
		System.out.println("SpectraMatrix:");
		System.out.println("log transformation: " + log);
		System.out.println("global mean value: " + mean);
		System.out.println("number of dimensions: " + numDimensions);
		System.out.println("number of Spectra: " + numSpectra);
		System.out.println("Data:");
		for(int i=0; i<mz.length; i++){
			System.out.print(mz[i]);
			for(int j=0; j<voltage.length; j++){
				System.out.print("\t" + voltage[j][i]);
			}
			System.out.println("");
		}
		System.out.println("Background:");
		for(int i=0; i<mz.length; i++){
			System.out.println(mz[i] + "\t" + voltBackground[i]);
		}
		System.out.println("");
	}
}
