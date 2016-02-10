package Main;

import Spectrum.ClassificationResult;
import Spectrum.Profile;
import Spectrum.SpectraMatrix;
import Spectrum.Spectrum;
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
		
		int mb = 1024*1024;
		Runtime runtime = Runtime.getRuntime();
		
		
		// create profile
		System.out.println("total: " + runtime.totalMemory()/mb);
		String profilePaths[] = {"/home/wens/mini_all/cow-milk", "/home/wens/mini_all/goat-milk", "/home/wens/mini_all/soy-milk"};
		SpectraMatrix data = Reader.readData(
				profilePaths, 
				"/home/wens/mini_all", 
				2, 
				"Mini 11", 
				false, 
				"", 
				",");
			System.out.println("after spectramatrix: " + ((runtime.totalMemory() - runtime.freeMemory()) / mb));
		data.deleteEmptyBins();
		data.calculateDimensionMeans();
		PCADataSet pca_data = new PCADataSet();
		int dimensionsUsed = 60;
		pca_data = PCA.performPCAusingNIPALS(data, dimensionsUsed);
//		double variance = 0.9;
//		pca_data = PCA.performPCAusingQR(data, varianceCovered);
			System.out.println("after PCA: " + (runtime.totalMemory() - runtime.freeMemory()) / mb);
		LDADataSet lda_data = LDA.performLDA(pca_data, data);
			System.out.println("after LDA: " + (runtime.totalMemory() - runtime.freeMemory()) / mb);
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
			System.out.println("after building profile: " + (runtime.totalMemory() - runtime.freeMemory()) / mb);
		
		// open profile
		Profile profile = Reader.readProfile("/home/wens/profiles/speedtest1.profile");
		System.out.println("after profile reading: " + (runtime.totalMemory() - runtime.freeMemory()) / mb);
		
		// open folder with csv files to classify
		String[] csv = Reader.readFolder("/home/wens/mini_all/speedtest");
		// classify
		String method = "ed";
		for(int i=0; i<csv.length; i++){
			Spectrum spectrum = new Spectrum(csv[i], null, profile.getMzBins(), profile.getBinSize(), "Mini 11", profile.getLog(), profile.getSeparator());
			
			if(method.equals("ed")){
				ClassificationResult res_ed = profile.euclideanDistance(spectrum);
			}else if(method.equals("md")){
				ClassificationResult res_ed = profile.mahalanobisDistance(spectrum);
			}else if(method.equals("lda")){
				ClassificationResult res_ed = profile.ldaCoefficient(spectrum);
			}
		}
		
		
//		crossValidation.evaluate("/home/wens/cross-validation/crossValidation/results");
		
		/*
		args[0] = root directory path
		args[1] = binsize
		args[2] = covered Variance
		args[3] = profile output folder
		args[4] = profile name
		args[5] = folder containing csv files to classify
		args[6] = output file for results
		*/
		
//		data.center();
//		Matrix matrixData = new Matrix(data.getData());
//		
//		EigenVector[] eig = PCA.nipals(matrixData, 60);
//		for(EigenVector e : eig){
//			e.print();
//		}
		
//		PCADataSet pca_data = PCA.performPCA(data, Double.parseDouble(args[2]));
//		LDADataSet lda_data = LDA.performLDA(pca_data, data);
//
		
//		Profile profile = Reader.readProfile("/home/wens/profiles/profile-log.profile");
//		Spectrum spectrum = new Spectrum(
//									"/home/wens/live/Ziegenmilch_41.csv", 
//									null,
//									profile.getBinSize(),
//									profile.getDevice(), 
//									profile.getLog());
//		profile.adjustRangeOfSpectrum(spectrum);
//		profile.deleteEmptyBins(spectrum);
//		profile.euclideanDistance(spectrum);
//		profile.mahalanobisDistance(spectrum);
//		profile.ldaCoefficient(spectrum);
		
		
//		
//		// create profile
//		ProfileBuilder.build(
//				pca_data, 
//				lda_data,
//				data, 
//				"Mini 11", 
//				args[0], 
//				args[3]+"/"+args[4], 
//				1.0);
//		
//		// open profile
//		Profile profile = Reader.readProfile(args[3]+"/"+args[4]);
//		
//		// open folder with csv files to classify
//		String[] csv = Reader.readFolder(args[5]);
//		
//		// open output file
//		PrintWriter writer = new PrintWriter(args[6], "UTF-8");
//		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
//		Date date = new Date();
//		writer.println("created: " + df.format(date));
//		writer.println("csv files from: " + args[5]);
//		writer.println("profile used: " + args[3]+"/"+args[4]);
//		writer.println("Filename\tassigned class ED\tEDdistance\tEDscore"
//				+ "\tassigned class MD\tMDdistance\tMDscore"
//				+ "\tassigned class LDA\tLDAcoefficient\tLDAscore");
//		
//		
//		// classify
//		for(int i=0; i<csv.length; i++){
//			Spectrum spectrum = new Spectrum(csv[i], null, profile.getBinSize(), "Mini 11");
//			ClassificationResult res_ed = profile.euclideanDistance(spectrum);
//			spectrum = new Spectrum(csv[i], null, profile.getBinSize(), "Mini 11");
//			ClassificationResult res_md = profile.mahalanobisDistance(spectrum);
//			spectrum = new Spectrum(csv[i], null, profile.getBinSize(), "Mini 11");
//			ClassificationResult res_lda = profile.ldaCoefficient(spectrum);
//			writer.println(spectrum.getFilename() + "\t" 
//					+ res_ed.getAssignedClass() + "\t" 
//					+ res_ed.getDistance() + "\t" 
//					+ res_ed.getScore() + "\t" 
//					+ res_md.getAssignedClass() + "\t" 
//					+ res_md.getDistance() + "\t" 
//					+ res_md.getScore() + "\t" 
//					+ res_lda.getAssignedClass() + "\t" 
//					+ res_lda.getDistance() + "\t" 
//					+ res_lda.getScore() 
//			);
//		}
//		writer.close();
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
