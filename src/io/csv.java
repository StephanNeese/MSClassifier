package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** This class is a simple data container for 
 * csv file content. 
 * 
 * @author Stephan Neese
 */
public class csv {
	
	List<Double> mz;
	List<Double> voltage;
	
	/** init a csv object
	 * 
	 * @param path complete path to the csv file
	 * @param separator csv column separator
	 * @throws FileNotFoundException
	 * @throws IOException 
	 */
	public csv(String path, String separator) throws FileNotFoundException, IOException{
		mz = new ArrayList<>();
		voltage = new ArrayList<>();
		
		BufferedReader buff = new BufferedReader(new FileReader(path));
		
		/** read in **/
		String text = null;
		boolean headlineReached = false;
		while ((text = buff.readLine()) != null) {
			if(headlineReached && !(text.isEmpty())){
				String[] lineTmp = null;
				lineTmp = text.split(separator);
				mz.add(Double.parseDouble(lineTmp[0]));
				voltage.add(Double.parseDouble(lineTmp[1]));
			}
			// check if we reached the headline of the csv table
			// check after parse => when headline reached only next line is parsed
			if(text.matches("^(Masse).*") ^ text.matches("^(M/Z,Voltage).*")){
				headlineReached = true;
			}
		}
	}
	
	/** print the csv object to the console.
	 * This method is used for testing purposes
	 * because its faster to print directly to console
	 * than using toString() on huge dataset.
	 */
	public void print(){
		for(int i=0; i<mz.size(); i++){
			System.out.println(mz.get(i) + "\t" + voltage.get(i));
		}
	}
	
	/** getter for the first mz bin
	 * 
	 * @return the value of the first mz bin
	 */
	public double getFirst(){
		return mz.get(0);
	}
	
	/** getter for the last mz bin
	 * 
	 * @return the value of the last mz bin
	 */
	public double getLast(){
		return mz.get(mz.size() - 1);
	}
}
