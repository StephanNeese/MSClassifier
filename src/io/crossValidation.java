package io;

import Spectrum.SpectraMatrix;
import gui.NewProfileWindow;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import preprocessing.LDA;
import preprocessing.LDADataSet;
import preprocessing.PCA;
import preprocessing.PCADataSet;

public class crossValidation {
	
	public static void validate(
			String crossValidationLocation,
			String[] paths, 
			String rootPath, 
			double binSize,
			double varianceCovered,
			String machineName,
			File cvDir) throws IOException{
		HashMap<String, String[]> files = new HashMap<>();
		
		// load files into hash
		for(String path : paths){
			// group names as identifiers
			String groupName = path.replace(rootPath+File.separator, "").replaceAll("/", "-").replaceAll("\\\\", "-");
			files.put(groupName, Reader.readFolder(path));
		}
		
		// load 90% of files per folder into profile subfolder
		// and 10% into data subfolder for classification
		// repeat until everything has been used for profile creation and classification
		for(int i=0; i<10; i++){
			// create folders for profile csv files and classification csv files
			File profileDir = new File(cvDir.getAbsolutePath() + File.separator + "profile");
			File classDir = new File(cvDir.getAbsolutePath() + File.separator + "data");
			if(profileDir.exists()){
				profileDir.delete();
				profileDir.mkdir();
			}else{
				profileDir.mkdir();
			}
			if(classDir.exists()){
				classDir.delete();
				classDir.mkdir();
			}else{
				classDir.mkdir();
			}
			
			// split content of subdirs (paths)
			// into a profile part and a classification part
			for(Map.Entry<String, String[]> e : files.entrySet()){
				// create folder for group
				File subDir = new File(profileDir + File.separator + e.getKey());
				subDir.mkdir();
				// split content from paths
				// start and end index for files to classify
				int start = (e.getValue().length/10*i)+1;
				int end = (e.getValue().length/10)*(i+1);
				// load into files from subdir into 
				// classification dir if they fall inbetween
				// start and end
				for(int j=0; j<e.getValue().length; j++){
					// only filename not entire path
					File file = new File(e.getValue()[j]);
					String fileName = file.getName();
					if(j>=(start-1) && j<=(end-1)){
						// into classification folder
						Files.copy(new File(e.getValue()[j]).toPath(), 
								new File(classDir.getAbsolutePath() + File.separator + fileName).toPath(), 
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
			String profileName = crossValidationLocation 
					+ File.separator  
					+ "crossValidation" 
					+ File.separator 
					+ "profiles" 
					+ File.separator 
					+ "profile" 
					+ i 
					+ ".profile";
			makeProfile(paths, rootPath, binSize, varianceCovered, machineName, profileName);
			// classify
			classify();
		}
		
		// evaluate
		evaluate();
	}
	
	private static void makeProfile(
			String[] profilePaths,
			String rootPath,
			double binSize,
			double varianceCovered,
			String machineName,
			String profileName){
		try{
			SpectraMatrix data = Reader.readData(profilePaths, rootPath, binSize, machineName);
			PCADataSet pca_data = PCA.performPCA(data, varianceCovered);
			LDADataSet lda_data = LDA.performLDA(pca_data, data);
			// create profile
			ProfileBuilder.build(
				pca_data, 
				lda_data,
				data, 
				machineName, 
				rootPath, 
				profileName, 
				1.0);
		}catch (Exception ex) {
			Logger.getLogger(NewProfileWindow.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private static void classify(){
		
	}
	
	private static void evaluate(){
		
	}
}
