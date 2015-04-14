package Main;

import Spectrum.SpectraMatrix;
import java.io.FileNotFoundException;
import preprocessing.PCA;
import preprocessing.Reader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import preprocessing.PCADataSet;
import preprocessing.ProfileBuilder;

public class Main {
	
	public static void main(String[] args) throws IOException, Exception {
//		Spectrum x = new Spectrum("/home/wens/Pflaume_32AVG5.csv", 1);
//		System.out.println(x.toString());
//		x.normalizationMeanSubstraction();
//		System.out.println("AFTER NORMALIZATION:");
//		System.out.println(x.toString());
//		
//		 read data from a folder
		SpectraMatrix data = Reader.readData("/home/wens/MINI_samples", 1);
		PCADataSet pca_data = PCA.performPCA(data, 0.5);
		ProfileBuilder.build(pca_data, "/home/wens/testprofile");
		//data.toCSV("/home/wens/samples4u.csv");
		
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
