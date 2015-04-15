package Spectrum;

import java.util.Date;

public class Profile {
	
	private final String[] classes;
	private final Date datetime;
	private final String device;
	private final String path;
	private final double variance;
	private final String[] filenames;
	private final double[][] data;
	private final double[][] features;
	private final double[][] mean;

	public Profile(
			String[] classes, 
			Date datetime, 
			String device, 
			String path, 
			double variance, 
			String[] filenames, 
			double[][] data, 
			double[][] features, 
			double[][] mean) {
		this.classes = classes;
		this.datetime = datetime;
		this.device = device;
		this.path = path;
		this.variance = variance;
		this.filenames = filenames;
		this.data = data;
		this.features = features;
		this.mean = mean;
	}

	public String[] getClasses() {
		return classes;
	}

	public Date getDatetime() {
		return datetime;
	}

	public String getDevice() {
		return device;
	}

	public String getPath() {
		return path;
	}

	public double getVariance() {
		return variance;
	}

	public String[] getFilenames() {
		return filenames;
	}

	public double[][] getData() {
		return data;
	}

	public double[][] getFeatures() {
		return features;
	}

	public double[][] getMean() {
		return mean;
	}

	@Override
	public String toString() {
		String res = "";
		
		res += "classes:";
		for(String s : classes){
			res += "\t" + s;
		}
		res += "\n"
				+ "created:\t" + datetime.toString() + "\n"
				+ "sourcepath:\t" + path + "\n"
				+ "device:\t" + device + "\n"
				+ "variance covered:\t" + variance + "\n";
		
		res += "transformed data:\n";
		for(int i=0; i<data.length; i++){
			for(int j=0; j<data[i].length; j++){
				res += data[i][j] + "\t";
			}
			res += "\n";
		}
		res += "feature matrix:\n";
		for(int i=0; i<features.length; i++){
			for(int j=0; j<features[i].length; j++){
				res += features[i][j] + "\t";
			}
			res += "\n";
		}
		res += "mean matrix:\n";
		for(int i=0; i<mean.length; i++){
			res += classes[i];
			for(int j=0; j<mean[i].length; j++){
				res += "\t" + mean[i][j];
			}
			res += "\n";
		}
		
		return res;
	}
}
