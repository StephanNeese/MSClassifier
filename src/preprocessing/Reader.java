package preprocessing;

import Spectrum.SpectraMatrix;
import Spectrum.Spectrum;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Reader {
	
	public static SpectraMatrix readData(String path) throws IOException{
		String[] csv = readFolder(path);
		Spectrum[] spectra = new Spectrum[csv.length];
		for(int i=0; i<csv.length; i++){
			spectra[i] = new Spectrum(csv[i], 4);
		}
		return new SpectraMatrix(spectra);
	} 
	
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
}
