package io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class csv {
	
	List<Double> mz;
	List<Double> voltage;
	
	public csv(String path, String device) throws FileNotFoundException, IOException{
		mz = new ArrayList<>();
		voltage = new ArrayList<>();
		
		BufferedReader buff = new BufferedReader(new FileReader(path));
		
		/** read in **/
		String text = null;
		boolean headlineReached = false;
		while ((text = buff.readLine()) != null) {
			if(headlineReached && !(text.isEmpty())){
				String[] lineTmp = null;
				// different separators for different machines
				if(device.equals("Mini 11")){
					lineTmp = text.split(",");
				}else{
					lineTmp = text.split("\t");
				}
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
	
	public void print(){
		for(int i=0; i<mz.size(); i++){
			System.out.println(mz.get(i) + "\t" + voltage.get(i));
		}
	}
	
	public double getFirst(){
		return mz.get(0);
	}
	
	public double getLast(){
		return mz.get(mz.size() - 1);
	}
}
