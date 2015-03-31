package Spectrum;

public class SpectraMatrix {
	
	private double[] mz;
	private double[][] voltage;		// [spectrum][mz]
	
	public SpectraMatrix(Spectrum[] spectra){
		this.mz = spectra[0].getMz();
		for(int i=0; i<spectra.length; i++){
			voltage[i] = spectra[i].getVoltage();
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
}
