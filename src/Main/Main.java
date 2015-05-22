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
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import preprocessing.PCADataSet;
import io.ProfileBuilder;
import java.io.File;
import preprocessing.LDA;
import preprocessing.LDADataSet;
import com.jidesoft.swing.CheckBoxTree;

public class Main {
	
	public static void main(String[] args) throws IOException, Exception {
		String[] profilePath = new String[2];
		profilePath[0] = "/home/wens/MINI_samples_2/dakapo";
		profilePath[1] = "/home/wens/MINI_samples_2/pflaume";
		
		SpectraMatrix data = Reader.readData(profilePath, "/home/wens/MINI_samples_2", 2);
		PCADataSet pca_data = PCA.performPCA(data, 0.6);
		LDADataSet lda_data = LDA.performLDA(pca_data, data);
		
		// make profile directory
								File dir = new File("/home/wens/MINI_samples_2"+File.separator+"profile");
								if(!dir.exists()){
									try{
										dir.mkdir();
									}catch(Exception ex){
										ex.printStackTrace();
									}
								}
		
		// create profile
								ProfileBuilder.build(
										pca_data, 
										lda_data,
										data, 
										"mini 11", 
										"/home/wens/MINI_samples_2", 
										"/home/wens/MINI_samples_2"+File.separator+"profile"+File.separator+"new-profile-multiple-folders.profile", 
										1.0);
		
		//File dir = new File("/home/wens/test");
		//liveWindow watch = new liveWindow("live", "/home/wens/test", "/home/wens/pflaume-traube.profile", "/home/wens/test", "euclidean distance");
		//watch.watchDirectoryPath();
        //DirWatch.watchDirectoryPath(dir, "/home/wens/pflaume-traube.profile");
		
//		Spectrum x = new Spectrum("/home/wens/Pflaume_32AVG5.csv", 1);
//		System.out.println(x.toString());
//		x.normalizationMeanSubstraction();
//		System.out.println("AFTER NORMALIZATION:");
//		System.out.println(x.toString());
		
//		SpectraMatrix data = Reader.readData("/home/wens/MINI_samples", 2);
//		// transform data via PCA
//		PCADataSet transformed = PCA.performPCA(data, 0.6);
//		LDADataSet lda = LDA.performLDA(transformed, data);
//		ProfileBuilder.build(
//										transformed, 
//										lda,
//										data, 
//										"mini", 
//										"/home/wens/MINI_samples", 
//										"/home/wens/lda-profile.profile", 
//										1.0);
		
//		Profile profile = Reader.readProfile("/home/wens/lda-profile.profile");
//		Spectrum test = new Spectrum("/home/wens/MINI_samples/Pflaume_49AVG5.csv", 2);
//		ClassificationResult x = profile.ldaCoefficient(test);
//		System.out.println(x);
		
//		CheckBoxTree checkboxTree = new CheckBoxTree();
		
		// build profile
		//ProfileBuilder.build(transformed, data, "test", "test", "/home/wens/testprofile_LDA", 1.0);
//		data.toCSV("/home/wens/samples_milk2u.csv");
		
//		
//		 read data from a folder
//		SpectraMatrix data = Reader.readData("/home/wens/MINI_samples", 1);
//		PCADataSet pca_data = PCA.performPCA(data, 0.6);
//		ProfileBuilder.build(pca_data, data, "MINI11", "/home/wens/MINI_samples", "/home/wens/testprofile2", 1.0);
//		Profile profile = Reader.readProfile("/home/wens/testprofile2");
//		Spectrum spectrum = new Spectrum("/home/wens/MINI_samples/Dakapo_Accent_5AVG5.csv", 1);
//		ClassificationResult res = profile.euclideanDistance(spectrum);
//		System.out.println(res);
//		res = profile.mahalanobisDistance(spectrum);
//		System.out.println(res);
		
//		 transform data via PCA
//		 SpectraMatrix transformed = PCA.performPCA(data);
//		 build profile
//		ProfileBuilder.build(data);
		
		// WEKA PCA Test
//		writeToARFF(data, "/home/wens/test.arff");
//		weka.core.Instances PCA_dataset = new Instances(new DataSource("/home/wens/test.arff").getDataSet());
//		weka.attributeSelection.PrincipalComponents PCA = new weka.attributeSelection.PrincipalComponents();
//		PCA.setTransformBackToOriginal(false);
//		PCA.setCenterData(true);
//		PCA.setVarianceCovered(0.9);
//		PCA.buildEvaluator(PCA_dataset);
//		weka.core.Instances transformed_data = PCA.transformedData(PCA_dataset);
//		PrintWriter writer = new PrintWriter("/home/wens/pcaResult_4u_var09.arff", "UTF-8");
//		writer.print(transformed_data.toString());
//		writer.close();
	}
	
	/** writes a SpectraMatrix to a WEKA ARFF file
	 * 
	 * @param data the SpectraMatrix to write
	 * @param filepath the complete path to the file
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException 
	 */
	private static void writeToARFF(SpectraMatrix data, String filepath) 
			throws FileNotFoundException, 
			UnsupportedEncodingException{
		double[][] intensities = data.getData();
		PrintWriter writer = new PrintWriter(filepath, "UTF-8");
		writer.print("@RELATION " + "test\n\n");
		double[] mz = data.getMz();
		for(int i=0; i<intensities[0].length; i++){
			writer.println("@ATTRIBUTE " + "mz" + mz[i] + "\tNUMERIC");
		}
		writer.println();
		writer.println("@DATA");
		for(int i=0; i<intensities.length; i++){
			for(int j=0; j<intensities[i].length-1; j++){
				writer.print(intensities[i][j] + ",");
			}
			writer.println(intensities[i][intensities[i].length-1]);
		}
		writer.close();
	}
}
