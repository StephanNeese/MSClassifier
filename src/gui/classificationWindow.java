package gui;

import Spectrum.ClassificationResult;
import Spectrum.Profile;
import Spectrum.Spectrum;
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
import preprocessing.Reader;

public class classificationWindow extends JFrame {
	
	JPanel main;
	JLabel folderLabel;
	JTextField folder;
	JButton folderSearch;
	JLabel profileLabel;
	JTextField profile;
	JButton profileSearch;
	JLabel distanceLabel;
	JComboBox distance;
	JLabel saveLabel;
	JTextField save;
	JButton saveSearch;
	JButton cancel;
	JButton classify;
	JButton help;

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
		super("setup for classification of Mass-spectograms");
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
		setSize(640, 240);
		setVisible(true);
		setResizable(false);
		// positon on screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (dim.width-640)/2;
		int y = (dim.height-240)/2;
		this.setLocation(x, y);
		
		main = new JPanel();
		main.setVisible(true);
		main.setLayout(null); 
		main.setBounds(0, 0, 640, 240);
		
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
		
		cancel = new JButton("cancel");
		cancel.setBounds(420, 175, 100, 30);
		main.add(cancel);
		
		classify = new JButton("classify");
		classify.setBounds(530, 175, 100, 30);
		main.add(classify);
		
		help = new JButton("help");
		help.setBounds(10, 175, 100, 30);
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
						
						// check if given parameters are valid
						if(!("".equals(checkParams(folderPath, profilePath, savePath)))){
							JFrame frame = new JFrame();						
							JOptionPane.showMessageDialog(frame, 
									checkParams(folderPath, profilePath, savePath),
									"Invalid Input", 
									JOptionPane.ERROR_MESSAGE);
						}else{
							// obtain all csv files from the folder
							String[] csv = Reader.readFolder(folderPath);
						
							// calculate distances
							Object[][] rowData = new Object[csv.length][4];
							if(distanceMeasure.equals("euclidean distance")){
								for(int i=0; i<csv.length; i++){
									try {
										Profile profile = Reader.readProfile(profilePath);
										Spectrum spectrum = new Spectrum(csv[i], (int)profile.getBinSize());
										ClassificationResult res = profile.euclideanDistance(spectrum);
										rowData[i][0] = csv[i];
										rowData[i][1] = res.getAssignedClass();
										rowData[i][2] = res.getDistance();
										rowData[i][3] = res.getScore();
									} catch (IOException ex) {
										Logger.getLogger(classificationWindow.class.getName()).log(Level.SEVERE, null, ex);
									} catch (ParseException ex) {
										Logger.getLogger(classificationWindow.class.getName()).log(Level.SEVERE, null, ex);
									}
								}
							}else{
								for(int i=0; i<csv.length; i++){
									try {
										Profile profile = Reader.readProfile(profilePath);
										Spectrum spectrum = new Spectrum(csv[i], (int)profile.getBinSize());
										ClassificationResult res = profile.mahalanobisDistance(spectrum);
										rowData[i][0] = csv[i];
										rowData[i][1] = res.getAssignedClass();
										rowData[i][2] = res.getDistance();
										rowData[i][3] = res.getScore();
									} catch (IOException ex) {
										Logger.getLogger(classificationWindow.class.getName()).log(Level.SEVERE, null, ex);
									} catch (ParseException ex) {
										Logger.getLogger(classificationWindow.class.getName()).log(Level.SEVERE, null, ex);
									}
								}
							}
						
							// save results to file
							try{
								PrintWriter writer = new PrintWriter(savePath, "UTF-8");
								writer.println("Filename\tassigned class\tdistance\tscore");
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
							Object columnNames[] = { "Filename", "assigned class", "distance", "score" };
							JTable table = new JTable(rowData, columnNames);

							JScrollPane scrollPane = new JScrollPane(table);
							frame.add(scrollPane, BorderLayout.CENTER);
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
					private String checkParams(String folder, String profile, String save){
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
						
						return res;
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
