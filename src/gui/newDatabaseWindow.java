package gui;

import Spectrum.SpectraMatrix;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import preprocessing.PCA;
import preprocessing.PCADataSet;
import io.ProfileBuilder;
import io.Reader;

public class newDatabaseWindow extends JFrame {
	
	JPanel main;
	JLabel nameLabel;
	JTextField name;
	JLabel machineLabel;
	JComboBox machine;
	JLabel folderLabel;
	JTextField folder;
	JButton folderSearch;
	JLabel binLabel;
	JTextField bin;
	JLabel varianceLabel;
	JTextField variance;
	JButton cancel;
	JButton create;
	JButton help;

	/** constructs a new newDatabaseWindow
	 * 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws UnsupportedLookAndFeelException 
	 */
	public newDatabaseWindow() 
			throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			UnsupportedLookAndFeelException {
		super("Creation of a new profile");
		initGui();
	}
	
	/** initializes and places all the GUI elements
	 * 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws UnsupportedLookAndFeelException 
	 */
	private void initGui()
			throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			UnsupportedLookAndFeelException {
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		setSize(640, 320);
		setVisible(true);
		setResizable(false);
		// positon on screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (dim.width-640)/2;
		int y = (dim.height-320)/2;
		this.setLocation(x, y);
		
		main = new JPanel();
		main.setVisible(true);
		main.setLayout(null); 
		main.setBounds(0, 0, 640, 320);
		
		nameLabel = new JLabel("Name of the profile");
		name = new JTextField();
		nameLabel.setBounds(10, 10, 200, 15);
		name.setBounds(10, 30, 300, 30);
		main.add(nameLabel);
		main.add(name);
		
		machineLabel = new JLabel("Type of machine");
		machine = new JComboBox();
		machine.addItem("Mini 11");
		machine.addItem("Exactive");
		machineLabel.setBounds(330, 10, 200, 15);
		machine.setBounds(330, 30, 300, 30);
		main.add(machineLabel);
		main.add(machine);
		
		folderLabel = new JLabel("folder with CSV files");
		folder = new JTextField();
		folderSearch = new JButton("search");
		folderLabel.setBounds(10, 80, 200, 15);
		folder.setBounds(10, 100, 200, 30);
		folderSearch.setBounds(220, 100, 90, 30);
		main.add(folderLabel);
		main.add(folder);
		main.add(folderSearch);
		
		binLabel = new JLabel("Size of a bin");
		bin = new JTextField();
		binLabel.setBounds(330, 80, 200, 15);
		bin.setBounds(330, 100, 300, 30);
		main.add(binLabel);
		main.add(bin);
		
		varianceLabel = new JLabel("variance covered");
		variance = new JTextField();
		varianceLabel.setBounds(10, 150, 200, 15);
		variance.setBounds(10, 170, 300, 30);
		main.add(varianceLabel);
		main.add(variance);
		
		cancel = new JButton("cancel");
		cancel.setBounds(420, 250, 100, 30);
		main.add(cancel);
		
		create = new JButton("create");
		create.setBounds(530, 250, 100, 30);
		main.add(create);
		
		help = new JButton("help");
		help.setBounds(10, 250, 100, 30);
		main.add(help);
		
		add(main);
	}
	
	/** initializes all the ActionListeners 
	 * for the GUI elements
	 */
	public void runProgram(){
		folderSearch.addActionListener(
				new ActionListener(){
					
					/** Display a JFileChooser when pressing the "search" button
					 * 
					 * @param e ActionEvent that occurs when you press the button
					 */
					public void actionPerformed(ActionEvent e){
						JFileChooser fileChooser = new JFileChooser();
						// set only directories and disable "all files" option
						fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						fileChooser.setAcceptAllFileFilterUsed(false);
						
						JFrame frame = new JFrame();
						int result = fileChooser.showOpenDialog(frame);
						
						// if file is selected 
						if (result == JFileChooser.APPROVE_OPTION){
							folder.setText(fileChooser.getSelectedFile().getAbsolutePath());
							frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
						}else if(result != JFileChooser.APPROVE_OPTION){
							frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
						}
					}
				}
		);
		
		help.addActionListener(
				new ActionListener(){
					
					/** Display a help dialog
					 * 
					 * @param e ActionEvent that occurs when you press the button
					 */
					public void actionPerformed(ActionEvent e){
						JFrame frame = new JFrame();
						
						JOptionPane.showMessageDialog(frame, 
									"Help Dialog goes here!", 
									"Help", 
									JOptionPane.QUESTION_MESSAGE);
					}
				}
		);
		
		create.addActionListener(
				new ActionListener(){
					
					/** calculate distances for spectras when pressed
					 * 
					 * @param e ActionEvent that occurs when you press the button
					 */
					@Override
					public void actionPerformed(ActionEvent e){
						// get data from text fields
						String folderPath = folder.getText();
						String machineName = (String)machine.getSelectedItem();
						String profileName = name.getText();
						String binTmp = bin.getText();
						String varianceTmp = variance.getText();
						
						// check parameters first
						if(!("".equals(checkParams(folderPath, profileName, binTmp, varianceTmp)))){
							JFrame frame = new JFrame();						
							JOptionPane.showMessageDialog(frame, 
									checkParams(folderPath, profileName, binTmp, varianceTmp),
									"Invalid Input", 
									JOptionPane.ERROR_MESSAGE);
						}else{
							int binSize = (int)Double.parseDouble(binTmp);
							double varianceCovered = Double.parseDouble(varianceTmp);
							try{
								SpectraMatrix data = Reader.readData(folderPath, binSize);
								PCADataSet pca_data = PCA.performPCA(data, varianceCovered);
								// make profile directory
								File dir = new File(folderPath+File.separator+"profile");
								if(!dir.exists()){
									try{
										dir.mkdir();
									}catch(Exception ex){
										ex.printStackTrace();
									}
								}
								// create profile
								ProfileBuilder.build(
										pca_data, 
										data, 
										machineName, 
										folderPath, 
										folderPath+File.separator+"profile"+File.separator+profileName+".profile", 
										1.0);
							
								// show success message
								JFrame frame = new JFrame();						
								JOptionPane.showMessageDialog(frame, 
										"Profile has been created.", 
										"Done", 
										JOptionPane.INFORMATION_MESSAGE);
							}catch (Exception ex) {
								Logger.getLogger(newDatabaseWindow.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
					}
					
					/** checks if the parameters given are valid
					 * 
					 * @param path path to the csv file folder
					 * @param profile path and name to the profile file
					 * @param bin size of a ms bin
					 * @param variance the variance covered
					 * @return an empty string if all parameters are valid, 
					 * a string with error messages otherwise
					 */
					private String checkParams(String path, String profile, String bin, String variance){
						String res = "";
						
						File x = new File(path);
						if("".equals(path)){
							res += "Error: The path to the folder containing the csv is empty\n";
						}else if(!(x.exists()) && !(x.isDirectory())){
							res += "Error: The foldername containing the csv files does not exist\n";
						}
						if("".equals(profile)){
							res += "Error: The name of the profile file is empty\n";
						}else if(!x.exists()){
							res += "Error: A file with the same name as the profile file already exists\n";
						}
						if(!(parseDouble(bin))){
							res += "Error: The value for the bin size is not a valid number\n";
						}
						if(!(parseDouble(variance))){
							res += "Error: The value for the covered variance is not a valid number\n";
						}else{
							double tmp = Double.parseDouble(variance);
							if(tmp<0 || tmp>1.0){
								res += "Error: The value for the covered variance must be between 0 and 1.0\n";
							}
						}
						
						return res;
					}
				
				/** checks if a String can be parsed to double
				 * 
				 * @param x the String containing a number (or something else)
				 * @return true if string can  parsed to double false otherwise
				 */
				private boolean parseDouble(String x){
					final String Digits     = "(\\p{Digit}+)";
					final String HexDigits  = "(\\p{XDigit}+)";
					// an exponent is 'e' or 'E' followed by an optionally
					// signed decimal integer.
					final String Exp        = "[eE][+-]?"+Digits;
					final String fpRegex    =
							("[\\x00-\\x20]*"+  // Optional leading "whitespace"
								"[+-]?(" + // Optional sign character
								"NaN|" +           // "NaN" string
								"Infinity|" +      // "Infinity" string

								// A decimal floating-point string representing a finite positive
								// number without a leading sign has at most five basic pieces:
								// Digits . Digits ExponentPart FloatTypeSuffix
								//
								// Since this method allows integer-only strings as input
								// in addition to strings of floating-point literals, the
								// two sub-patterns below are simplifications of the grammar
								// productions from section 3.10.2 of
								// The Java™ Language Specification.

								// Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
								"((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

								// . Digits ExponentPart_opt FloatTypeSuffix_opt
								"(\\.("+Digits+")("+Exp+")?)|"+

								// Hexadecimal strings
								"((" +
								// 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
								"(0[xX]" + HexDigits + "(\\.)?)|" +

								// 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
								"(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

								")[pP][+-]?" + Digits + "))" +
								"[fFdD]?))" +
								"[\\x00-\\x20]*");// Optional trailing "whitespace"

					if (Pattern.matches(fpRegex, x))
						return true;
					else {
						return false;
					}
				}
			}
		);
		
		cancel.addActionListener(
				new ActionListener(){
					
					/** cancel and close window
					 * 
					 * @param e ActionEvent that occurs when you press the button
					 */
					public void actionPerformed(ActionEvent e){
						System.exit(0);
					}
				}
		);
	}
}
