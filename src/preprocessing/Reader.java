package preprocessing;

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
		String[] filenames = null;
		double[][] data = null;
		double[][] features = null;
		double[][] mean = null;
		
		for(int i=0; i<segment.length; i++){
			String tmp = segment[i].toLowerCase();
			if(tmp.startsWith("classes:")){
				String[] content = segment[i].split("\t");
				classes = new String[content.length-1];
				for(int j=1; j<content.length; j++){
					classes[j-1] = content[j];
				}
			}else if(tmp.startsWith("date:")){
				String[] content = segment[i].split("\t");
				String dt = content[1];
				DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy:HH:mm");
				date = (Date)formatter.parse(dt);
			}else if(tmp.startsWith("device:")){
				String[] content = segment[i].split("\t");
				device = content[1];
			}else if(tmp.startsWith("path:")){
				String[] content = segment[i].split("\t");
				inputPath = content[1];
			}else if(tmp.startsWith("variance:")){
				String[] content = segment[i].split("\t");
				variance = Double.parseDouble(content[1]);
			}else if(tmp.startsWith("filenames:")){
				String[] content = segment[i].split("\n");
				filenames = new String[content.length-1];
				for(int j=1; j<content.length; j++){
					filenames[j-1] = content[j];
				}
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
			}else if(tmp.startsWith("mean:")){
				String[] row = segment[i].split("\n");
				mean = new double[row.length-1][row[1].split("\t").length];
				for(int j=1; j<row.length; j++){
					String[] column = row[j].split("\t");
					for(int k=0; k<column.length; k++){
						mean[j-1][k] = Double.parseDouble(column[k]);
					}
				}
			}
		}
		
		return new Profile(classes, date, device, inputPath, variance, filenames, data, features, mean);
	}
}
