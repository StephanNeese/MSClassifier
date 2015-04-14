package preprocessing;

import Spectrum.SpectraMatrix;
import Spectrum.Spectrum;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/** This class contains the static method readData(String path, int binSize)
 * which creates a SpectraMatrix object from csv files in a directory.
 * 
 * @author Stephan Neese
 */

public class Reader {
	
	/** reads in all the csv files in a directory and creates a SpectraMatrix
	 * 
	 * @param path path to the directory containing the csv files
	 * @param binSize size of a bin in u
	 * @return a SpectraMatrix Object created from all the csv files in the directory
	 * @throws IOException 
	 */
	public static SpectraMatrix readData(String path, int binSize) throws IOException{
		String[] csv = readFolder(path);
		Spectrum[] spectra = new Spectrum[csv.length];
		for(int i=0; i<csv.length; i++){
			spectra[i] = new Spectrum(csv[i], binSize);
		}
		return new SpectraMatrix(spectra);
	} 
	
	/** reads the content of a directory and returns the complete paths
	 * to all csv files found in the folder.
	 * 
	 * @param path the path to the folder
	 * @return a String array containing complete paths to all csv files in the folder
	 */
	private static String[] readFolder(String path){
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
	
	public static void readProfile(String path){
		
	}
}
