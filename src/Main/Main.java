package Main;

import Spectrum.SpectraMatrix;
import preprocessing.PCA;
import io.Reader;
import java.io.IOException;
import preprocessing.PCADataSet;
import io.ProfileBuilder;
import java.io.File;
import preprocessing.LDA;
import preprocessing.LDADataSet;
import java.util.ArrayList;

/** This is a test class for methods and other classes.
 * 
 * @author Stephan Neese
 */

public class Main {
	
	public static void main(String[] args) throws IOException, Exception {
		
		Thread.sleep(30000);
		
		long startTime = System.currentTimeMillis();
		
		// create profile
		String profilePaths[] = {"/home/wens/mini_all/cow-milk", "/home/wens/mini_all/goat-milk", "/home/wens/mini_all/soy-milk"};
		SpectraMatrix data = Reader.readData(
				profilePaths, 
				"/home/wens/mini_all", 
				0.1, 
				"Mini 11", 
				false, 
				"", 
				",");
		data.deleteEmptyBins();
		data.calculateDimensionMeans();
		PCADataSet pca_data = new PCADataSet();
//		int dimensionsUsed = 60;
//		pca_data = PCA.performPCAusingNIPALS(data, dimensionsUsed);
		double variance = 0.9;
		pca_data = PCA.performPCAusingQR(data, variance);
		LDADataSet lda_data = LDA.performLDA(pca_data, data);
		// create profile
		ProfileBuilder.build(
				pca_data, 
				lda_data,
				data, 
				"Mini 11", 
				",",
				"NIPALS",
				"/home/wens/mini_all", 
				"/home/wens/profiles/speedtest1.profile", 
				1.0,
				"");
		
		long endTime = System.currentTimeMillis();
		long time = (endTime - startTime)/1000;
		System.out.println("time: " + time);
		
//		// open profile
//		Profile profile = Reader.readProfile("/home/wens/profiles/speedtest1.profile");
//		System.out.println("after profile reading:\t" + runtime.totalMemory()/mb + "\t" + runtime.freeMemory()/mb + "\t" + ((runtime.totalMemory() - runtime.freeMemory()) / mb));
//		
//		// open folder with csv files to classify
//		String[] csv = Reader.readFolder("/home/wens/mini_all/speedtest");
//		// classify
//		String method = "ed";
//		for(int i=0; i<csv.length; i++){
//			Spectrum spectrum = new Spectrum(csv[i], null, profile.getMzBins(), profile.getBinSize(), "Mini 11", profile.getLog(), profile.getSeparator());
//			
//			if(method.equals("ed")){
//				ClassificationResult res_ed = profile.euclideanDistance(spectrum);
//			}else if(method.equals("md")){
//				ClassificationResult res_ed = profile.mahalanobisDistance(spectrum);
//			}else if(method.equals("lda")){
//				ClassificationResult res_ed = profile.ldaCoefficient(spectrum);
//			}
//		}
	}
	
	public static String[] readProfilePaths(String root){
		File dir = new File(root);
		
		// get all the files from a directory
		File[] files = dir.listFiles();
		ArrayList<String> subDir = new ArrayList<>();
		for (File file : files) {
			if(file.isDirectory()) {
				subDir.add(file.getAbsolutePath());
			}
		}
		
		String[] res = new String[subDir.size()];
		for(int i=0; i<subDir.size(); i++){
			res[i] = subDir.get(i);
		}
		
		return res;
	}
}
