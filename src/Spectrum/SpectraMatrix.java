package Spectrum;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/** This class provides a datastructure for an array of
 * spectras. During construction of the object the spectras
 * are binned. 
 * 
 * @author Stephan Neese
 */
public class SpectraMatrix {
	
	private double[] mz;				// mz values (dimensions)
	//private String[] samples;			// filenames of spectras
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
	public SpectraMatrix(Spectrum[] spectra){
		this.mz = spectra[0].getMz();
		//this.samples = new String[spectra.length];
		numSpectra = spectra.length;
		numDimensions = mz.length;
		voltage = new double[numSpectra][numDimensions];
		groups = new String[numSpectra];
		
		for(int i=0; i<spectra.length; i++){
			voltage[i] = spectra[i].getVoltage();
			//samples[i] = spectra[i].getFilename();
			groups[i] = spectra[i].getGroup();
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
//			writer.print(samples[spec]);
			for(int mzVal=0; mzVal<voltage[0].length; mzVal++){
				writer.print("\t" + voltage[spec][mzVal]);
			}
			writer.println();
		}
		
		writer.close();
	}
}
