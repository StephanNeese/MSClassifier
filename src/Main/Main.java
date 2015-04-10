package Main;


import Spectrum.SpectraMatrix;
import java.io.FileNotFoundException;
import preprocessing.PCA;
import preprocessing.Reader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import preprocessing.ProfileBuilder;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


public class Main {
	
	public static void main(String[] args) throws IOException, Exception {
//		Spectrum x = new Spectrum("/home/wens/Pflaume_32AVG5.csv", 1);
//		System.out.println(x.toString());
//		x.normalizationMeanSubstraction();
//		System.out.println("AFTER NORMALIZATION:");
//		System.out.println(x.toString());
//		
//		 read data from a folder
		SpectraMatrix data = Reader.readData("/home/wens/MINI_samples");
		//data.toCSV("/home/wens/samples4u.csv");
		
//		 transform data via PCA
//		 SpectraMatrix transformed = PCA.performPCA(data);
//		 build profile
//		ProfileBuilder.build(data);
		
		// WEKA PCA Test
		writeToARFF(data, "/home/wens/test.arff");
		weka.core.Instances PCA_dataset = new Instances(new DataSource("/home/wens/test.arff").getDataSet());
		weka.attributeSelection.PrincipalComponents PCA = new weka.attributeSelection.PrincipalComponents();
		PCA.setTransformBackToOriginal(false);
		PCA.setCenterData(true);
		PCA.setVarianceCovered(0.5);
		PCA.buildEvaluator(PCA_dataset);
		weka.core.Instances transformed_data = PCA.transformedData(PCA_dataset);
		PrintWriter writer = new PrintWriter("/home/wens/pcaResult.arff", "UTF-8");
		writer.print(transformed_data.toString());
		writer.close();
	}
	
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
