package io;

import Spectrum.Profile;
import Spectrum.SpectraMatrix;
import Spectrum.Spectrum;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/** This class contains static methods to read in from the file system 
 * and create a SpectraMatrix or a profile. 
 * 
 * @author Stephan Neese
 */

public class Reader {
	
	/** reads in all the csv files in a directory and creates a SpectraMatrix
	 * 
	 * @param group all chosen directories in the root directory
	 * @param rootPath path to the directory containing the csv files
	 * @param binSize size of a bin in u
	 * @return a SpectraMatrix Object created from all the csv files in the directory
	 * @throws IOException 
	 */
	public static SpectraMatrix readData(String[] group, String rootPath, double binSize, String device, boolean log, String backgroundPath) throws IOException{
		ArrayList<Spectrum> tmp = new ArrayList<>();
		
		for(String path : group){
			// group name
			String groupName = path.replace(rootPath+File.separator, "").replaceAll("/", "-").replaceAll("\\\\", "-");
			String[] csv = readFolder(path);
			for(int i=0; i<csv.length; i++){
				tmp.add(new Spectrum(csv[i], groupName, binSize, device, log));
			}
		}
		
		Spectrum[] spectra = new Spectrum[tmp.size()];
		for(int i=0; i<tmp.size(); i++){
			spectra[i] = tmp.get(i);
		}
		
		// if backgroundPath contains a path then get backgrounddata 
		// and init background substracted matrix by calling constructor
		// SpectraMatrix(Spectrum[] spectra, SpectraMatrix background, double binSize)
		if(!("".equals(backgroundPath))){
			SpectraMatrix background = Reader.readData(group, backgroundPath, binSize, device, log, "");
			return new SpectraMatrix(spectra, background, binSize);
		}else{
			return new SpectraMatrix(spectra, binSize);
		}
	} 
	
	/** reads the content of a directory and returns the complete paths
	 * to all csv files found in the folder.
	 * 
	 * @param path the path to the folder
	 * @return a String array containing complete paths to all csv files in the folder
	 */
	public static String[] readFolder(String path){
		ArrayList<String> tmp = new ArrayList<>();
		File folder = new File(path);
		
		for(File file : folder.listFiles()){
			String filePath = file.getAbsolutePath();
			if(filePath.endsWith(".csv")){
				tmp.add(filePath);
			}
		}
		
		String[] res = new String[tmp.size()];
		for(int i=0; i<tmp.size(); i++){
			res[i] = tmp.get(i);
		}
		return res;
	}
	
	/** reads a profile file and returns
	 * a Profile Object
	 * 
	 * @param path path to the profile file
	 * @return a Profile Object constructed from the file
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException 
	 */
	public static Profile readProfile(String path) throws FileNotFoundException, IOException, ParseException{
		File profile = new File(path);
		BufferedReader buff = new BufferedReader(new FileReader(profile));
		String profileContent = "";
		
		/** read in **/
		String text = null;
		while ((text = buff.readLine()) != null) {
			profileContent += text + "\n";
		}
		String[] segment = profileContent.split("//#\n");
		
		// identify the segments
		String[] classes = null;
		Date date = null;
		String device = null;
		String inputPath = null;
		double variance = 0.0;
		boolean log = false;
		double[] mzBackground = null;
		double[] voltBackground = null;
		String[] sampleGroups = null;
		double[] originalMeans = null;
		double originalMean = 0;
		double binSize = 0;
		double[][] data = null;
		double[][] features = null;
		double mzStart = 0;
		double mzEnd = 0;
		double[] bins = null;
		HashMap<String, double[][]> invertedCovarianceMatrices = null;
		double[][] mean = null;
		double[][] ldaCovarianceMatrix = null;
		double[] globalMean = null;
		double[] fractions = null;
		
		for(int i=0; i<segment.length; i++){
			String tmp = segment[i].toLowerCase();
			if(tmp.startsWith("classes:")){
				String[] content = segment[i].split("\t");
				classes = new String[content.length-1];
				for(int j=1; j<content.length; j++){
					content[j] = content[j].replaceAll("\n", "");
					classes[j-1] = content[j];
				}
			}else if(tmp.startsWith("date:")){
				String[] content = segment[i].split("\t");
				String dt = content[1];
				DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy:HH:mm");
				date = (Date)formatter.parse(dt);
			}else if(tmp.startsWith("device:")){
				String[] content = segment[i].split("\t");
				device = content[1].replaceAll("\n", "");
			}else if(tmp.startsWith("path:")){
				String[] content = segment[i].split("\t");
				inputPath = content[1].replaceAll("\n", "");
			}else if(tmp.startsWith("variance:")){
				String[] content = segment[i].split("\t");
				variance = Double.parseDouble(content[1]);
			}else if(tmp.startsWith("log:")){
				String[] content = segment[i].split("\t");
				log = Boolean.parseBoolean(content[1].replaceAll("\n", ""));
			}else if(tmp.startsWith("background:")){
				String[] content = segment[i].split("\n");
				mzBackground = new double[content.length - 1];
				voltBackground = new double[content.length - 1];
				for(int j=1; j<content.length; j++){
					String[] lineTmp = content[j].split(":");
					mzBackground[j-1] = Double.parseDouble(lineTmp[0]);
					voltBackground[j-1] = Double.parseDouble(lineTmp[1]);
				}
			}else if(tmp.startsWith("groups:")){
				String[] content = segment[i].split("\n");
				sampleGroups = new String[content.length-1];
				for(int j=1; j<content.length; j++){
					sampleGroups[j-1] = content[j];
				}
			}else if(tmp.startsWith("original-means:")){
				String[] content = segment[i].split("\n");
				originalMeans = new double[content.length-1];
				for(int j=1; j<content.length; j++){
					originalMeans[j-1] = Double.parseDouble(content[j]);
				}
			}else if(tmp.startsWith("original-mean:")){
				String[] content = segment[i].split("\t");
				originalMean = Double.parseDouble(content[1]);
			}else if(tmp.startsWith("bin:")){
				String[] content = segment[i].split("\t");
				binSize = Double.parseDouble(content[1]);
			}else if(tmp.startsWith("data:")){
				String[] row = segment[i].split("\n");
				data = new double[row.length-1][row[1].split("\t").length];
				for(int j=1; j<row.length; j++){
					String[] column = row[j].split("\t");
					for(int k=0; k<column.length; k++){
						data[j-1][k] = Double.parseDouble(column[k]);
					}
				}
			}else if(tmp.startsWith("features:")){
				String[] row = segment[i].split("\n");
				features = new double[row.length-1][row[1].split("\t").length];
				for(int j=1; j<row.length; j++){
					String[] column = row[j].split("\t");
					for(int k=0; k<column.length; k++){
						features[j-1][k] = Double.parseDouble(column[k]);
					}
				}
			}else if(tmp.startsWith("mz-range:")){
				String[] row = segment[i].split("\t");
				mzStart = Double.parseDouble(row[1]);
				mzEnd = Double.parseDouble(row[row.length-1]);
				bins = new double[row.length-1];
				for(int k=1; k<row.length; k++){
					bins[k-1] = Double.parseDouble(row[k]);
				}
			}else if(tmp.startsWith("covariances:")){
				invertedCovarianceMatrices = obtainCovarianceMatrices(segment[i]);
			}else if(tmp.startsWith("mean:")){
				String[] row = segment[i].split("\n");
				mean = new double[row.length-1][row[1].split("\t").length];
				// each row = one class mean vector
				for(int j=1; j<row.length; j++){
					String[] column = row[j].split("\t");
					for(int k=0; k<column.length; k++){
						mean[j-1][k] = Double.parseDouble(column[k]);
					}
				}
			}else if(tmp.startsWith("lda-covariance:")){
				String[] row = segment[i].split("\n");
				ldaCovarianceMatrix = new double[row.length-1][row.length-1];
				for(int j=1; j<row.length; j++){
					String[] column = row[j].split("\t");
					for(int k=0; k<column.length; k++){
						ldaCovarianceMatrix[j-1][k] = Double.parseDouble(column[k]);
					}
				}
			}else if(tmp.startsWith("lda-mean:")){
				String[] row = segment[i].split("\n");
				globalMean = new double[row.length];
				for(int j=1; j<row.length; j++){
					String x = row[j].replaceAll("\n", "");
					globalMean[j] = Double.parseDouble(x);
				}
			}else if(tmp.startsWith("lda-fractions:")){
				String[] row = segment[i].split("\n");
				fractions = new double[row.length];
				for(int j=1; j<row.length; j++){
					String x = row[j].replaceAll("\n", "");
					fractions[j] = Double.parseDouble(x);
				}
			}
		}
		
		return new Profile(
				classes, 
				date, 
				device, 
				inputPath, 
				variance, 
				log,
				mzBackground,
				voltBackground,
				sampleGroups, 
				data, 
				features,
				mzStart,
				mzEnd,
				bins,
				invertedCovarianceMatrices,
				mean, 
				originalMeans, 
				originalMean, 
				binSize,
				ldaCovarianceMatrix,
				globalMean,
				fractions
		);
	}
	
	/** reads a String containing the covariance matrix information from the profile file
	 * and constructs the covariance matrices for the classes
	 * 
	 * @param block the block from the profile file containing information 
	 * about the covariance matrices
	 * @return a HasMap with class (String) => matrix (double[][])
	 */
	private static HashMap<String, double[][]> obtainCovarianceMatrices(String block){
		HashMap<String, double[][]> res = new HashMap<>();
		
		// split by lines
		String[] tmp = block.split("\n");
		ArrayList<String> matrix = new ArrayList<>();
		// check each line with previous line (without header)
		for(int i=2; i<tmp.length; i++){
			matrix.add(tmp[i-1]);
			String[] columnLast = tmp[i-1].split("\t");
			String[] columnCurrent = tmp[i].split("\t");
			if(!(columnLast[0].equals(columnCurrent[0])) && i<tmp.length-1){
				res.put(columnLast[0], readMatrix(matrix));
				matrix = new ArrayList<>();
			}else if(!(columnLast[0].equals(columnCurrent[0])) && i==tmp.length-1){
				res.put(columnLast[0], readMatrix(matrix));
				ArrayList<String> x = new ArrayList<>();
				x.add(tmp[i]);
				res.put(columnCurrent[0], readMatrix(x));
			}else if(columnLast[0].equals(columnCurrent[0]) && i==tmp.length-1){
				matrix.add(tmp[i]);
				res.put(columnCurrent[0], readMatrix(matrix));
			}
		}
		
		return res;
	}
	
	/** takes a list of Strings and returns a 2d double array
	 * 
	 * @param block the list of Strings (lines)
	 * @return a 2d double array
	 */
	private static double[][] readMatrix(ArrayList<String> block){
		double[][] res = new double[block.size()][block.get(0).split("\t").length-1];
		
		for(int i=0; i<block.size(); i++){
			String[] tmp = block.get(i).split("\t");
			for(int j=1; j<tmp.length; j++){
				res[i][j-1] = Double.parseDouble(tmp[j]);
			}
		}
		
		return res;
	}
}
