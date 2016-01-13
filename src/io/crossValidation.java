package io;

import Spectrum.ClassificationResult;
import Spectrum.Profile;
import Spectrum.SpectraMatrix;
import Spectrum.Spectrum;
import gui.NewProfileWindow;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileDeleteStrategy;
import preprocessing.LDA;
import preprocessing.LDADataSet;
import preprocessing.PCA;
import preprocessing.PCADataSet;

public class crossValidation {
	
	public static void validate(
			CrossValidationParameterSet params) throws IOException, 
			FileNotFoundException, 
			ParseException{
		HashMap<String, String[]> files = new HashMap<>();
		
		// load files into hash
		for(String path : params.paths){
			// group names as identifiers
			String groupName = path.replace(params.rootPath+File.separator, "").replaceAll("/", "-").replaceAll("\\\\", "-");
			files.put(groupName, Reader.readFolder(path));
		}
		
		// load 90% of files per folder into profile subfolder
		// and 10% into data subfolder for classification
		// repeat until everything has been used for profile creation and classification
		for(int i=0; i<10; i++){
			// create folders for profile csv files and classification csv files
			File profileDir = new File(params.cvDir + File.separator + "profile");
			File classDir = new File(params.cvDir + File.separator + "data");
			if(profileDir.exists()){
				while(!FileDeleteStrategy.FORCE.deleteQuietly(profileDir)){
					System.gc();
					System.out.println("failed to delete pofileDir. Retry.");
				}
				profileDir.mkdir();
			}else{
				profileDir.mkdir();
			}
			if(classDir.exists()){
				while(!FileDeleteStrategy.FORCE.deleteQuietly(classDir)){
					System.gc();
					System.out.println("failed to delete classDir. Retry.");
				}
				classDir.mkdir();
			}else{
				classDir.mkdir();
			}
			
			// split content of groups
			// into a profile part and a classification part
			for(Map.Entry<String, String[]> e : files.entrySet()){
				// create folder for group
				File subDir = new File(profileDir + File.separator + e.getKey());
				subDir.mkdir();
				// split content from paths
				// start and end index for files to classify
				double startTmp = ((double)e.getValue().length/10*i)+1;
				int start = (int)startTmp;
				double endTmp = ((double)e.getValue().length/10)*(i+1);
				int end = (int)endTmp;
				// load into files from subdir into 
				// classification dir if they fall inbetween
				// start and end
				for(int j=0; j<e.getValue().length; j++){
					// only filename not entire path
					File file = new File(e.getValue()[j]);
					String fileName = file.getName();
					if(j>=(start-1) && j<=(end-1)){
						// into classification folder
						// with correct group in front of filename
						// for evaluation of the cross validation process
						Files.copy(new File(e.getValue()[j]).toPath(), 
								new File(classDir.getAbsolutePath() + File.separator + e.getKey() + "_" + fileName).toPath(), 
								StandardCopyOption.REPLACE_EXISTING);
					}else{
						// into profile folder
						Files.copy(new File(e.getValue()[j]).toPath(), 
								new File(subDir.getAbsolutePath() + File.separator + fileName).toPath(), 
								StandardCopyOption.REPLACE_EXISTING);
					}
				}
			}
			
			// now make profile and classify
			String profileName = params.cvDir
					+ File.separator 
					+ "profiles" 
					+ File.separator 
					+ "profile" 
					+ i 
					+ ".profile";
			String algorithm = params.algorithm==CrossValidationParameterSet.Algorithm.QR ? "QR algorithm" : "NIPALS";
			if(params.algorithm==CrossValidationParameterSet.Algorithm.QR){
				makeProfileQR(
						params.paths, 
						params.rootPath, 
						params.binSize, 
						params.variance, 
						params.machine, 
						profileName, 
						params.background, 
						params.log, 
						params.separator, 
						algorithm);
			}else{
				makeProfileNIPALS(
						params.paths, 
						params.rootPath, 
						params.binSize, 
						params.dimensions, 
						params.machine, 
						profileName, 
						params.background, 
						params.log, 
						params.separator, 
						algorithm);
			}
			
			// classify
			String results = params.resultsDir + File.separator + "results" + i + ".csv";
			classify(
					profileName, 
					classDir.getAbsolutePath(),
					results,
					params.machine,
					params.log,
					params.separator);
			
			// remove references for garbage collector
			profileDir = null;
			classDir = null;
			// call garbage collector to remove old references from memory
			// this is nessessary to remove files in windows
			System.gc();
		}
		
		// evaluate everything
		evaluate(params.resultsDir);
	}
	
	private static void makeProfileQR(
			String[] profilePaths,
			String rootPath,
			double binSize,
			double varianceCovered,
			String machineName,
			String profileName,
			String background,
			boolean log,
			String separator,
			String algorithm){
		try{
			SpectraMatrix data = Reader.readData(profilePaths, rootPath, binSize, machineName, log, background, separator);
			data.deleteEmptyBins();
			data.calculateDimensionMeans();
			PCADataSet pca_data = PCA.performPCAusingQR(data, varianceCovered);
			LDADataSet lda_data = LDA.performLDA(pca_data, data);
			// create profile
			ProfileBuilder.build(
				pca_data, 
				lda_data,
				data, 
				machineName, 
				separator,
				algorithm,
				rootPath, 
				profileName, 
				1.0);
		}catch (Exception ex) {
			Logger.getLogger(NewProfileWindow.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private static void makeProfileNIPALS(
			String[] profilePaths,
			String rootPath,
			double binSize,
			int dimensions,
			String machineName,
			String profileName,
			String background,
			boolean log,
			String separator,
			String algorithm){
		try{
			SpectraMatrix data = Reader.readData(profilePaths, rootPath, binSize, machineName, log, background, separator);
			data.deleteEmptyBins();
			data.calculateDimensionMeans();
			PCADataSet pca_data = PCA.performPCAusingNIPALS(data, dimensions);
			LDADataSet lda_data = LDA.performLDA(pca_data, data);
			// create profile
			ProfileBuilder.build(
				pca_data, 
				lda_data,
				data, 
				machineName, 
				separator,
				algorithm,
				rootPath, 
				profileName, 
				1.0);
		}catch (Exception ex) {
			Logger.getLogger(NewProfileWindow.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private static void classify(
			String profileName, 
			String classDir, 
			String results,
			String machine,
			boolean log,
			String separator)
			throws IOException, 
			FileNotFoundException, 
			ParseException{
		// open profile
		Profile profile = Reader.readProfile(profileName);
		
		// open folder with csv files to classify
		String[] csv = Reader.readFolder(classDir);
		
		// open output file
		PrintWriter writer = new PrintWriter(results, "UTF-8");
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		Date date = new Date();
		writer.println("created: " + df.format(date));
		writer.println("csv files from: " + classDir);
		writer.println("profile used: " + profileName);
		writer.println("Filename"
				+ "\tassigned class ED\tEDdistance\tEDscore"
				+ "\tassigned class MD\tMDdistance\tMDscore"
				+ "\tassigned class LDA\tLDAcoefficient\tLDAscore");
		
		// classify
		for(int i=0; i<csv.length; i++){
			Spectrum spectrum = new Spectrum(
					csv[i], 
					null, 
					profile.getMzBins(), 
					profile.getBinSize(),
					machine, 
					log,
					separator);
			ClassificationResult res_ed = profile.euclideanDistance(spectrum);
			spectrum = new Spectrum(
					csv[i], 
					null, 
					profile.getMzBins(), 
					profile.getBinSize(),
					machine, 
					log,
					separator);
			ClassificationResult res_md = profile.mahalanobisDistance(spectrum);
			spectrum = new Spectrum(
					csv[i], 
					null, 
					profile.getMzBins(), 
					profile.getBinSize(),
					machine, 
					log,
					separator);
			ClassificationResult res_lda = profile.ldaCoefficient(spectrum);
			writer.println(spectrum.getFilename() + "\t" 
					+ res_ed.getAssignedClass() + "\t" 
					+ res_ed.getDistance() + "\t" 
					+ res_ed.getScore() + "\t" 
					+ res_md.getAssignedClass() + "\t" 
					+ res_md.getDistance() + "\t" 
					+ res_md.getScore() + "\t" 
					+ res_lda.getAssignedClass() + "\t" 
					+ res_lda.getDistance() + "\t" 
					+ res_lda.getScore() 
			);
		}
		writer.close();
	}
	
	public static void evaluate(String resultsDir) 
			throws FileNotFoundException, IOException{
		// read all result csv files from folder
		String[] results = Reader.readFolder(resultsDir);
		
		// read results tables into List
		List<String> table = new ArrayList<>();
		for(String file : results){
			BufferedReader buff = new BufferedReader(new FileReader(file));
			String text = null;
			while ((text = buff.readLine()) != null) {
				if(!(text.startsWith("created:")) 
						&& !(text.startsWith("csv files from:"))
						&& !(text.startsWith("profile used:"))
						&& !(text.startsWith("Filename"))){
					table.add(text);
				}
			}
		}
		
		int EDcnt = 0;
		int MDcnt = 0;
		int LDAcnt = 0;
		// beginning of the filenames is their actual group
		for(String s : table){
			String[] line = s.split("\t");
			// count as correctly classified
			// iff name of file start with assigned group
			if(line[0].startsWith(line[1])){
				EDcnt++;
			}
			if(line[0].startsWith(line[4])){
				MDcnt++;
			}
			if(line[0].startsWith(line[7])){
				LDAcnt++;
			}
		}
		
		String msg = "Cross validation results:\n"
				+ "euclidean distance: " + new DecimalFormat("##.##").format((double)EDcnt/table.size()*100) + "%"
				+ " which are " + EDcnt + " out of " + table.size() + "\n"
				+ "mahalanobis distance: " + new DecimalFormat("##.##").format((double)MDcnt/table.size()*100) + "%"
				+ " which are " + MDcnt + " out of " + table.size() + "\n"
				+ "LDA coefficient: " + new DecimalFormat("##.##").format((double)LDAcnt/table.size()*100) + "%"
				+ " which are " + LDAcnt + " out of " + table.size() + "\n";
		
		JFrame frame = new JFrame();
		JOptionPane.showMessageDialog(frame, 
			msg, 
			"Results", 
			JOptionPane.INFORMATION_MESSAGE);
	}
}
