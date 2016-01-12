package Main;

import Spectrum.ClassificationResult;
import Spectrum.Profile;
import Spectrum.SpectraMatrix;
import Spectrum.Spectrum;
import gui.liveWindow;
import java.io.FileNotFoundException;
import preprocessing.PCA;
import io.Reader;
import java.io.IOException;
import preprocessing.PCADataSet;
import io.ProfileBuilder;
import io.crossValidation;
import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import preprocessing.LDA;
import preprocessing.LDADataSet;
import java.util.ArrayList;
import java.util.Date;
import preprocessing.EigenVector;
import weka.core.matrix.Matrix;

public class Main {
	
	public static void main(String[] args) throws IOException, Exception {
		
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
//		
		String[] profilePath = readProfilePaths("/home/wens/mini_all");
//		
		SpectraMatrix data = Reader.readData(profilePath, "/home/wens/mini_all", 2.0, "Mini 11", true, "");
		data.deleteEmptyBins();
		data.calculateDimensionMeans();
		
		
		
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
