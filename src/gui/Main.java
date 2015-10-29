package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author wens
 */
public class Main extends JFrame {
	
	JTabbedPane mainPanel;
	NewProfileWindow create;
	TestProfileWindow scorePlot;
	crossValidationWindow cross;
	classificationWindow classify;
	liveClassificationWindow live;
	JButton classifyButton; 
	ActionListener al;
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (InstantiationException e) {
			e.printStackTrace();
		}catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		Main window = new Main();
	}
	
	public Main() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
		setTitle("MS Food classifier");
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int xDim = (dim.width-1024)/2;
		int yDim = (dim.height-600)/2;
		this.setLocation(xDim, yDim);
				
		// create TabbetPlane for Tabs
		mainPanel = new JTabbedPane();
		
		// create tabs
		create = new NewProfileWindow();
		scorePlot = new TestProfileWindow();
		cross = new crossValidationWindow();
		classify = new classificationWindow();
		live = new liveClassificationWindow();
		
		// create icons
		ImageIcon createIcon = new ImageIcon(this.getClass().getResource("img/create.png"));
		ImageIcon scorePlotIcon = new ImageIcon(this.getClass().getResource("img/plot.png"));
		ImageIcon crossIcon = new ImageIcon(this.getClass().getResource("img/cross-validation.png"));
		ImageIcon classIcon = new ImageIcon(this.getClass().getResource("img/class.png"));
		
		// add tabs to pane
		mainPanel.addTab("create profile", createIcon, create);
		mainPanel.addTab("score plot", scorePlotIcon, scorePlot);
		mainPanel.addTab("cross validation", crossIcon, cross);
		mainPanel.addTab("classification", classIcon, classify);
		mainPanel.addTab("live classification", classIcon, live);
		
		// layout settings for mainPanel
		mainPanel.setTabPlacement(JTabbedPane.LEFT);
		
		getContentPane().add(mainPanel);
		
		// we have to declare the listener for the classify button
		// here because we want to make this window invisible
		classifyButton = live.getClassifyButton();
		classifyButton.addActionListener(
				new ActionListener(){
					
					/** Display a JFileChooser when pressing the "search" button
					 * 
					 * @param e ActionEvent that occurs when you press the button
					 */
					public void actionPerformed(ActionEvent e){
						// get data from the fields
						String folderPath = live.getFolder().getText();
						String profilePath = live.getProfile().getText();
						String savePath = live.getSave().getText();
						String distanceMeasure = (String)live.getDistance().getSelectedItem();
						String cutoffTmp = live.getCutoff().getText();
						
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
		
		// make all visible
		this.setVisible(true);
		this.setMinimumSize(new Dimension(820, 500));
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
