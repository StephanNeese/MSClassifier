package Spectrum;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class SpectraMatrix {
	
	private double[] mz;
	private String[] samples;
	private double[][] voltage;		// [spectrum][mz]
	private final int numSpectra;
	private final int numDimensions;
	
	/**
	 * 
	 * @param spectra 
	 */
	public SpectraMatrix(Spectrum[] spectra){
		this.mz = spectra[0].getMz();
		this.samples = new String[spectra.length];
		numSpectra = spectra.length;
		numDimensions = mz.length;
		voltage = new double[numSpectra][numDimensions];
		
		for(int i=0; i<spectra.length; i++){
			voltage[i] = spectra[i].getVoltage();
			samples[i] = spectra[i].getFilename();
		}
	}
	
	/**
	 * 
	 */
	public void normalizationDivideByMean(){
		// get mean value of all values in the matrix
		double mean = calculateMean();
		// divide all values by mean
		for(int i=0; i<voltage.length; i++){
			for(int j=0; j<voltage[0].length; j++){
				voltage[i][j] = voltage[i][j]/mean;
			}
		}
	}
	
	/**
	 * 
	 * @return 
	 */
	public double calculateMean(){
		double sum = 0;
		double mean = 0;
		int num = voltage.length * voltage[0].length;
		// calculate mean of all values in the matrix
		for(int i=0; i<voltage.length; i++){
			for(int j=0; j<voltage[0].length; j++){
				sum += voltage[i][j];
			}
		}
		return sum/num;
	}
	
	/**
	 * 
	 * @param dimension
	 * @return 
	 */
	public double calculateMean(int dimension){
		double sum = 0;
		double mean = 0;
		int num = voltage.length;
		// calculate mean of all values in the matrix
		for(int i=0; i<voltage.length; i++){
			sum += voltage[i][dimension];
		}
		return sum/num;
	}
	
	/**
	 * 
	 */
	public void center(){
		// substract dimension-mean value from all dimensions
		for(int dim=0; dim<voltage[0].length; dim++){
			double mean = calculateMean(dim);			// mean of dimension j
			for(int sampl=0; sampl<voltage.length; sampl++){
				voltage[sampl][dim] = voltage[sampl][dim]-mean;
			}
		}
	}
	
	/**
	 * 
	 * @return 
	 */
	public double[] getMz(){
		return mz;
	}
	
	/**
	 * 
	 * @param spectrum
	 * @param index 
	 */
	public void setSpectrum(Spectrum spectrum, int index){
		voltage[index] = spectrum.getVoltage();
	}
	
	/**
	 * 
	 * @param index
	 * @return 
	 */
	public Spectrum getSpectrum(int index){
		return new Spectrum(mz, voltage[index], samples[index]);
	}
	
	/**
	 * 
	 * @param index
	 * @return 
	 */
	public double[] getDimension(int index){
		double[] res = new double[voltage.length];
		for(int i=0; i<voltage.length; i++){
			res[i] = voltage[i][index];
		}
		return res;
	}

	/**
	 * 
	 * @return 
	 */
	public int getNumSpectra() {
		return numSpectra;
	}

	/**
	 * 
	 * @return 
	 */
	public int getNumDimensions() {
		return numDimensions;
	}
	
	/**
	 * 
	 * @return 
	 */
	public double[][] getData(){
		return voltage;
	}

	@Override
	public String toString() {
		String res = "";
		for(int spec=0; spec<voltage.length; spec++){
			res += "Spectrum " + spec;
			for(int mzVal=0; mzVal<voltage[0].length; mzVal++){
				res += "\t" + voltage[spec][mzVal];
			}
			res += "\n";
		}
		
		return res;
	}
	
	/**
	 * 
	 * @param path
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
			writer.print(samples[spec]);
			for(int mzVal=0; mzVal<voltage[0].length; mzVal++){
				writer.print("\t" + voltage[spec][mzVal]);
			}
			writer.println();
		}
		
		writer.close();
	}
}
