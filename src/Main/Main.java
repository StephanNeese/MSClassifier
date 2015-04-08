package Main;


import Spectrum.SpectraMatrix;
import preprocessing.PCA;
import preprocessing.Reader;
import java.io.IOException;
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
		SpectraMatrix data = Reader.readData("/home/wens/pflaume");
//		data.normalizationDivideByMean();
//		 transform data via PCA
//		 SpectraMatrix transformed = PCA.performPCA(data);
//		 build profile
//		ProfileBuilder.build(data);
		
		// WEKA PCA Test
		writeToARFF(data);
		weka.core.Instances PCA_dataset = new Instances(new DataSource("/home/wens/test.arff").getDataSet());
		weka.attributeSelection.PrincipalComponents PCA = new weka.attributeSelection.PrincipalComponents();
		PCA.setTransformBackToOriginal(false);
		PCA.buildEvaluator(PCA_dataset);
		weka.core.Instances transformed_data = PCA.transformedData(PCA_dataset);
//		String[] options = PCA.getOptions();
//		for(String s : options){
//			System.out.println(s);
//		}
		
	}
	
	private static void writeToARFF(SpectraMatrix data){
		
	}
}
