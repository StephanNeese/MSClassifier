package Spectrum;

public class SpectraMatrix {
	
	private double[] mz;
	private double[][] voltage;		// [spectrum][mz]
	private final int numSpectra;
	private final int numDimensions;
	
	public SpectraMatrix(Spectrum[] spectra){
		this.mz = spectra[0].getMz();
		numSpectra = spectra.length;
		numDimensions = mz.length;
		voltage = new double[numSpectra][numDimensions];
		
		for(int i=0; i<spectra.length; i++){
			voltage[i] = spectra[i].getVoltage();
		}
	}
	
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
	
	public void substractMean(){
		// get mean value of all values in the matrix
		double mean = calculateMean();
		// substract mean value from all values
		for(int i=0; i<voltage.length; i++){
			for(int j=0; j<voltage[0].length; j++){
				voltage[i][j] = voltage[i][j]-mean;
			}
		}
	}
	
	public void setSpectrum(Spectrum spectrum, int index){
		voltage[index] = spectrum.getVoltage();
	}
	
	public Spectrum getSpectrum(int index){
		return new Spectrum(mz, voltage[index]);
	}
	
	public double[] getDimension(int index){
		double[] res = new double[voltage.length];
		for(int i=0; i<voltage.length; i++){
			res[0] = voltage[i][index];
		}
		return res;
	}

	public int getNumSpectra() {
		return numSpectra;
	}

	public int getNumDimensions() {
		return numDimensions;
	}
	
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
}
