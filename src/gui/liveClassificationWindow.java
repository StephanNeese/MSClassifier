package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
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
import javax.swing.filechooser.FileNameExtensionFilter;

/** This class is the parameter input window
 * for live classification.
 * 
 * @author Stephan Neese
 */
public class liveClassificationWindow extends JFrame {
	
	private JPanel main;
	private JLabel folderLabel;
	private JTextField folder;
	private JButton folderSearch;
	private JLabel profileLabel;
	private JTextField profile;
	private JButton profileSearch;
	private JLabel distanceLabel;
	private JComboBox distance;
	private JLabel saveLabel;
	private JTextField save;
	private JButton saveSearch;
	private JLabel cutoffLabel;
	private JTextField cutoff;
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
	public liveClassificationWindow() 
			throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			UnsupportedLookAndFeelException {
		super("setup for classification of Mass-spectras");
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
			UnsupportedLookAndFeelException{
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		setSize(640, 310);
		setVisible(true);
		setResizable(false);
		// positon on screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (dim.width-640)/2;
		int y = (dim.height-310)/2;
		this.setLocation(x, y);
		
		main = new JPanel();
		
		folderLabel = new JLabel("folder with CSV files");
		folder = new JTextField();
		folderSearch = new JButton("search");
		folderLabel.setBounds(10, 10, 200, 15);
		folder.setBounds(10, 30, 200, 30);
		folderSearch.setBounds(220, 30, 90, 30);
		main.add(folderLabel);
		main.add(folder);
		main.add(folderSearch);
		
		profileLabel = new JLabel("path to the profile file");
		profile = new JTextField();
		profileSearch = new JButton("search");
		profileLabel.setBounds(330, 10, 200, 15);
		profile.setBounds(330, 30, 200, 30);
		profileSearch.setBounds(540, 30, 90, 30);
		main.add(profileLabel);
		main.add(profile);
		main.add(profileSearch);
		
		distanceLabel = new JLabel("distance measure");
		distance = new JComboBox();
		distance.addItem("euclidean distance");
		distance.addItem("mahalanobis distance");
		distance.addItem("LDA coefficient");
		distanceLabel.setBounds(10, 80, 200, 15);
		distance.setBounds(10, 100, 299, 25);
		main.add(distanceLabel);
		main.add(distance);
		
		saveLabel = new JLabel("where to save the results");
		save = new JTextField();
		saveSearch = new JButton("search");
		saveLabel.setBounds(330, 80, 200, 15);
		save.setBounds(330, 100, 200, 30);
		saveSearch.setBounds(540, 100, 90, 30);
		main.add(saveLabel);
		main.add(save);
		main.add(saveSearch);
		
		cutoffLabel = new JLabel("minimal score for results to classify");
		cutoff = new JTextField();
		cutoffLabel.setBounds(10, 145, 200, 15);
		cutoff.setBounds(10, 165, 299, 30);
		main.add(cutoffLabel);
		main.add(cutoff);
		
		cancel = new JButton("cancel");
		cancel.setBounds(420, 240, 100, 30);
		main.add(cancel);
		
		classify = new JButton("classify");
		classify.setBounds(530, 240, 100, 30);
		main.add(classify);
		
		help = new JButton("help");
		help.setBounds(10, 240, 100, 30);
		main.add(help);
		
		add(main);
		main.setVisible(true);
		main.setLayout(null); 
		main.setBounds(0, 0, 640, 310);
	}
	
	
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
					
					/** Display a JFileChooser when pressing the "search" button
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
					
					/** Display a JFileChooser when pressing the "search" button
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
							setVisible(false);
							liveWindow watch;
							double cutoffValue = Double.parseDouble(cutoffTmp);
							try {
								watch = new liveWindow("live", folderPath, profilePath, savePath, distanceMeasure, cutoffValue);
								watch.start();
							} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | FileNotFoundException | UnsupportedEncodingException ex) {
								Logger.getLogger(liveClassificationWindow.class.getName()).log(Level.SEVERE, null, ex);
							} catch (IOException ex) {
								Logger.getLogger(liveClassificationWindow.class.getName()).log(Level.SEVERE, null, ex);
							} catch (ParseException ex) {
								Logger.getLogger(liveClassificationWindow.class.getName()).log(Level.SEVERE, null, ex);
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
					
					/** Display a JFileChooser when pressing the "search" button
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
