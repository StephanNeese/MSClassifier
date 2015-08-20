package Spectrum;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/** This class provides a datastructure for an array of
 * spectras. During construction of the object the spectras
 * are binned. 
 * 
 * @author Stephan Neese
 */
public class SpectraMatrix {
	
	private double[] mz;				// mz values (dimensions)
	private String[] groups;			// the groups of the samples
	private double[][] voltage;			// [spectrum][mz]
	private final int numSpectra;		// no of spectras
	private final int numDimensions;	// no of dimensions (mz bins)
	private double mean;				// mean of all values in the matrix
	private double[] dimensionsMean;	// mean for each dimension
	
	/** constructs a SpectraMatrix from a Spectrum array
	 * 
	 * @param spectra the spectrum array
	 */
	public SpectraMatrix(Spectrum[] spectra, double bin){
		numSpectra = spectra.length;
		groups = new String[numSpectra];
		
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
		
		mean = calculateMean();
		this.normalizationDivideByMean();
		dimensionsMean = calculateDimensionMeans();
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
	 * represented in the spectraMatrix
	 * 
	 * @return the mean values as double array
	 */
	private double[] calculateDimensionMeans(){
		double[] res = new double[voltage[0].length];
		// loop though dimensions
		for(int dim=0; dim<voltage[0].length; dim++){
			res[dim] = calculateMean(dim);
		}
		
		return res;
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
	
	/** centers the dataset for PCA processing
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
	
	/** returns the mz array
	 * 
	 * @return mz values as double array
	 */
	public double[] getMz(){
		return mz;
	}
	
	/** returns the sample names of the spectra matrix
	 * 
	 * @return the sample names as string array
	 */
//	public String[] getSamples(){
//		return samples;
//	}
	
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
		String res = "";
		
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
}
