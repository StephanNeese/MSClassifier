package gui;

import Spectrum.ClassificationResult;
import Spectrum.Profile;
import Spectrum.Spectrum;
import io.ProfileOpeningThread;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;

/** This class is the window for
 * non live classification.
 * 
 * @author Stephan Neese
 */
public class classificationWindow extends JPanel {
	
	private JLabel folderLabel;
	private JTextField folder;
	private JButton folderSearch;
	private JLabel profileLabel;
	private JTextField profile;
	private JButton profileSearch;
	private JButton profileInfo;
	private JLabel distanceLabel;
	private JComboBox distance;
	private JLabel saveLabel;
	private JTextField save;
	private JButton saveSearch;
	private JLabel cutoffLabel;
	private JTextField cutoff;
	private JSeparator sep;
	private JButton cancel;
	private JButton classify;
	private JButton help;

	/** constructs a classificationWindow
	 * 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws UnsupportedLookAndFeelException 
	 */
	public classificationWindow() 
			throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			UnsupportedLookAndFeelException {
		super(new BorderLayout());
		setLayout(null);
		initGui();
		runProgram();
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
			UnsupportedLookAndFeelException{
		
		folderLabel = new JLabel("folder with CSV files");
		folder = new JTextField();
		folderSearch = new JButton("search");
		folderLabel.setBounds(100, 10, 200, 15);
		folder.setBounds(100, 30, 310, 30);
		folderSearch.setBounds(420, 30, 90, 30);
		this.add(folderLabel);
		this.add(folder);
		this.add(folderSearch);
		
		profileLabel = new JLabel("path to the profile file");
		profile = new JTextField();
		profileSearch = new JButton("search");
		profileInfo = new JButton("Info");
		profileLabel.setBounds(100, 80, 200, 15);
		profile.setBounds(100, 100, 210, 30);
		profileSearch.setBounds(320, 100, 90, 30);
		profileInfo.setBounds(420, 100, 90, 30);
		this.add(profileLabel);
		this.add(profile);
		this.add(profileSearch);
		this.add(profileInfo);
		
		distanceLabel = new JLabel("distance measure");
		distance = new JComboBox();
		distance.addItem("euclidean distance");
		distance.addItem("mahalanobis distance");
		distance.addItem("LDA coefficient");
		distanceLabel.setBounds(100, 150, 200, 15);
		distance.setBounds(100, 170, 410, 25);
		this.add(distanceLabel);
		this.add(distance);
		
		saveLabel = new JLabel("where to save the results");
		save = new JTextField();
		saveSearch = new JButton("search");
		saveLabel.setBounds(100, 220, 200, 15);
		save.setBounds(100, 240, 310, 30);
		saveSearch.setBounds(420, 240, 90, 30);
		this.add(saveLabel);
		this.add(save);
		this.add(saveSearch);
		
		cutoffLabel = new JLabel("minimal score for results to classify");
		cutoff = new JTextField();
		cutoffLabel.setBounds(100, 290, 300, 15);
		cutoff.setBounds(100, 310, 410, 30);
		this.add(cutoffLabel);
		this.add(cutoff);
		
		sep = new JSeparator();
		sep.setBounds(10, 475, 620, 10);
		this.add(sep);
		
		cancel = new JButton("cancel");
		cancel.setBounds(420, 490, 100, 35);
		cancel.setIcon(new ImageIcon(this.getClass().getResource("img/exit.png")));
		this.add(cancel);
		
		classify = new JButton("classify");
		classify.setBounds(530, 490, 100, 35);
		classify.setIcon(new ImageIcon(this.getClass().getResource("img/go.png")));
		this.add(classify);
		
		help = new JButton("help");
		help.setBounds(10, 490, 100, 35);
		help.setIcon(new ImageIcon(this.getClass().getResource("img/help.png")));
		this.add(help);
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
		
		profileSearch.addActionListener(
				new ActionListener(){
					
					/** Display a JFileChooser when pressing the "search" button
					 * 
					 * @param e ActionEvent that occurs when you press the button
					 */
					public void actionPerformed(ActionEvent e){
						JFileChooser fileChooser = new JFileChooser();
						// set only directories and disable "all files" option
						FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter(
								"profile files (*.profile)", 
								"profile");
						fileChooser.setFileFilter(xmlfilter);
						fileChooser.setDialogTitle("Open profile file");
						
						JFrame frame = new JFrame();
						int result = fileChooser.showOpenDialog(frame);
						
						// if file is selected 
						if (result == JFileChooser.APPROVE_OPTION){
							profile.setText(fileChooser.getSelectedFile().getAbsolutePath());
							frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
						}else if(result != JFileChooser.APPROVE_OPTION){
							frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
						}
					}
				}
		);
		
		profileInfo.addActionListener(
				new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						if("".equals(profile.getText())){
							JFrame frame = new JFrame();						
							JOptionPane.showMessageDialog(frame, 
									"Please select a Profile to load first.",
									"No Profile", 
									JOptionPane.ERROR_MESSAGE);
						}else{
							try {
								// display waiting message
								String path = profile.getText();
								WaitMessage wait = new WaitMessage("Please wait");
								// display info
								ProfileOpeningThread x = new ProfileOpeningThread(path, wait);
								x.start();
							} catch (Exception ex) {
								ex.printStackTrace();
								JFrame frame2 = new JFrame();
								JOptionPane.showMessageDialog(frame2, 
									ex.toString(),
									"An error occured", 
									JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
		);
		
		saveSearch.addActionListener(
				new ActionListener(){
					
					/** Display a JFileChooser when pressing the "search" button
					 * 
					 * @param e ActionEvent that occurs when you press the button
					 */
					public void actionPerformed(ActionEvent e){
						JFileChooser fileChooser = new JFileChooser();
						// set only directories and disable "all files" option
						
						JFrame frame = new JFrame();
						int result = fileChooser.showOpenDialog(frame);
						
						// if file is selected 
						if (result == JFileChooser.APPROVE_OPTION){
							save.setText(fileChooser.getSelectedFile().getAbsolutePath());
							frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
						}else if(result != JFileChooser.APPROVE_OPTION){
							frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
						}
					}
				}
		);
		
		help.addActionListener(
				new ActionListener(){
					
					/** Display a Help dialog
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
		
		classify.addActionListener(
				new ActionListener(){
					
					/** calculate the distances and show results in a table + save them to file
					 * 
					 * @param e ActionEvent that occurs when you press the button
					 */
					public void actionPerformed(ActionEvent e){
						// get data from the fields
						String folderPath = folder.getText();
						String profilePath = profile.getText();
						String savePath = save.getText();
						String distanceMeasure = (String)distance.getSelectedItem();
						String cutoffTmp = cutoff.getText();
						
						// check if given parameters are valid
						if(!("".equals(checkParams(folderPath, profilePath, savePath, cutoffTmp)))){
							// if not valid output an ERROR MSG
							JFrame frame = new JFrame();						
							JOptionPane.showMessageDialog(frame, 
									checkParams(folderPath, profilePath, savePath, cutoffTmp),
									"Invalid Input", 
									JOptionPane.ERROR_MESSAGE);
						}else{
							// obtain all csv files from the folder
							String[] csv = Reader.readFolder(folderPath);
							double cutoffValue = Double.parseDouble(cutoffTmp);
						
							// calculate distances
							Object[][] rowData = new Object[csv.length][4];
							if(distanceMeasure.equals("euclidean distance")){
								try {
									Profile profile = Reader.readProfile(profilePath);
									// get worst possible score from first result
									for(int i=0; i<csv.length; i++){
										Spectrum spectrum = new Spectrum(
												csv[i], 
												null, 
												profile.getMzBins(), 
												profile.getBinSize(),
												profile.getDevice(), 
												profile.getLog(),
												profile.getSeparator());
										ClassificationResult res = profile.euclideanDistance(spectrum);
										rowData[i][0] = spectrum.getFilename();
										if(res.getScore()<cutoffValue){
											rowData[i][1] = "NA";
											rowData[i][2] = "NA";
											rowData[i][3] = "NA";
										}else{
											rowData[i][1] = res.getAssignedClass();
											rowData[i][2] = res.getDistance();
											rowData[i][3] = res.getScore();
										}
									}
								} catch (IOException | ParseException ex) {
									Logger.getLogger(classificationWindow.class.getName()).log(Level.SEVERE, null, ex);
								}
							}else if(distanceMeasure.equals("mahalanobis distance")){
								try {
									Profile profile = Reader.readProfile(profilePath);
									// get worst possible score from first result
									for(int i=0; i<csv.length; i++){
										Spectrum spectrum = new Spectrum(
												csv[i], 
												null, 
												profile.getMzBins(), 
												profile.getBinSize(),
												profile.getDevice(), 
												profile.getLog(),
												profile.getSeparator());
										ClassificationResult res = profile.mahalanobisDistance(spectrum);
										rowData[i][0] = spectrum.getFilename();
										if(res.getScore()<cutoffValue){
											rowData[i][1] = "NA";
											rowData[i][2] = "NA";
											rowData[i][3] = "NA";
										}else{
											rowData[i][1] = res.getAssignedClass();
											rowData[i][2] = res.getDistance();
											rowData[i][3] = res.getScore();
										}
									}
								} catch (IOException | ParseException ex) {
									Logger.getLogger(classificationWindow.class.getName()).log(Level.SEVERE, null, ex);
								}
							}else{
								try {
									Profile profile = Reader.readProfile(profilePath);
									// get worst possible score from first result
									for(int i=0; i<csv.length; i++){
										Spectrum spectrum = new Spectrum(
												csv[i], 
												null, 
												profile.getMzBins(), 
												profile.getBinSize(),
												profile.getDevice(), 
												profile.getLog(),
												profile.getSeparator());
										ClassificationResult res = profile.ldaCoefficient(spectrum);
										rowData[i][0] = spectrum.getFilename();
										if(res.getScore()<cutoffValue){
											rowData[i][1] = "NA";
											rowData[i][2] = "NA";
											rowData[i][3] = "NA";
										}else{
											rowData[i][1] = res.getAssignedClass();
											rowData[i][2] = res.getDistance();
											rowData[i][3] = res.getScore();
										}
									}
								} catch (IOException | ParseException ex) {
									Logger.getLogger(classificationWindow.class.getName()).log(Level.SEVERE, null, ex);
								}
							}
						
							// save results to file
							try{
								PrintWriter writer = new PrintWriter(savePath, "UTF-8");
								DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
								Date date = new Date();
								writer.println("created: " + df.format(date));
								writer.println("csv files from: " + folderPath);
								writer.println("profile used: " + profilePath);
								writer.println("distance measure: " + distanceMeasure);
								writer.println("minimum score: " + cutoffValue);
								if(distanceMeasure.equals("LDA coefficient")){
									writer.println("Filename\tassigned class\tcoefficient\tscore");
								}else{
									writer.println("Filename\tassigned class\tdistance\tscore");
								}
								for(int i=0; i<rowData.length; i++){
									writer.println(rowData[i][0] + "\t" + rowData[i][1] + "\t" + rowData[i][2] + "\t" + rowData[i][3]);
								}
								writer.close();
							}catch(IOException ex){
								ex.printStackTrace();
							}
						
							JFrame frame = new JFrame("Results");
							frame.setSize(640, 480);
							frame.setVisible(true);
							frame.setResizable(false);
							// positon on screen
							Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
							int x = (dim.width-640)/2;
							int y = (dim.height-480)/2;
							frame.setLocation(x, y);
							// create table with results
							if(distanceMeasure.equals("LDA coefficient")){
								Object columnNames[] = { "Filename", "assigned class", "coefficient", "score" };
								JTable table = new JTable(rowData, columnNames);
								JScrollPane scrollPane = new JScrollPane(table);
								frame.add(scrollPane, BorderLayout.CENTER);
							}else{
								Object columnNames[] = { "Filename", "assigned class", "distance", "score" };
								JTable table = new JTable(rowData, columnNames);
								JScrollPane scrollPane = new JScrollPane(table);
								frame.add(scrollPane, BorderLayout.CENTER);
							}
							
							
						}
					}
					
					/** check the parameters if they are valid
					 * 
					 * @param folder the folder to the csv files
					 * @param profile the path and name of the profile file
					 * @param save the path and name of the results file
					 * @return an empty string if all parameters are valid, 
					 * a string with error messages otherwise
					 */
					private String checkParams(String folder, String profile, String save, String cutoffValue){
						String res = "";
						
						File x = new File(folder);
						if("".equals(folder)){
							res += "Error: The path to the folder containing the csv files is empty\n";
						}else if(!(x.exists())){
							res += "Error: The path to the folder containing the csv files does not exist\n";
						}
						x = new File(profile);
						if("".equals(profile)){
							res += "Error: The path to the profile file is empty\n";
						}else if(!(x.exists())){
							res += "Error: The profile file does not exist\n";
						}
						x = new File(save);
						if("".equals(save)){
							res += "Error: The path to save the results to is empty\n";
						}else if(x.exists()){
							res += "Error: A file with the same name and path as the results file already exists\n";
						}
						if(!(parseDouble(cutoffValue))){
							res += "Error: The value for the minimum score is not a valid number\n";
						}else{
							double tmp = Double.parseDouble(cutoffValue);
							if(tmp<0 || tmp>1.0){
								res += "Error: The value for the minimum score must be between 0 and 1.0\n";
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
					
					/** exit the program
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
