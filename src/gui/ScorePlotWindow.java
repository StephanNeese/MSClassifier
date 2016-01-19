package gui;

import Spectrum.Profile;
import io.ProfileOpeningThread;
import io.Reader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.math.plot.Plot2DPanel;
import org.math.plot.Plot3DPanel;

/**
 *
 * @author wens
 */
public class ScorePlotWindow extends JPanel {
	
	JLabel profileLabel;
	JTextField profile;
	JButton profileSearch;
	JButton profileInfo;
	JLabel dimensionLabel;
	JComboBox dimension;
	JSeparator sep;
	JButton cancel;
	JButton plot;
	JButton help;
	
	/** constructs a new testProfileWindow
	 * 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws UnsupportedLookAndFeelException 
	 */
	public ScorePlotWindow() 
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
			UnsupportedLookAndFeelException {
		
		profileLabel = new JLabel("path to the profile file");
		profile = new JTextField();
		profileSearch = new JButton("search");
		profileInfo = new JButton("Info");
		profileLabel.setBounds(100, 10, 200, 15);
		profile.setBounds(100, 30, 210, 30);
		profileSearch.setBounds(320, 30, 90, 30);
		profileInfo.setBounds(420, 30, 90, 30);
		this.add(profileLabel);
		this.add(profile);
		this.add(profileSearch);
		this.add(profileInfo);
		
		dimensionLabel = new JLabel("chose how many dimensions should be plottet");
		dimension = new JComboBox();
		dimension.addItem("2 Dimensions");
		dimension.addItem("3 Dimensions");
		dimensionLabel.setBounds(100, 80, 300, 15);
		dimension.setBounds(100, 100, 410, 30);
		this.add(dimensionLabel);
		this.add(dimension);
		
		sep = new JSeparator();
		sep.setBounds(10, 475, 620, 10);
		this.add(sep);
		
		cancel = new JButton("cancel");
		cancel.setBounds(420, 490, 100, 35);
		cancel.setIcon(new ImageIcon(this.getClass().getResource("img/exit.png")));
		this.add(cancel);
		
		plot = new JButton("create");
		plot.setBounds(530, 490, 100, 35);
		plot.setIcon(new ImageIcon(this.getClass().getResource("img/go.png")));
		this.add(plot);
		
		help = new JButton("help");
		help.setBounds(10, 490, 100, 35);
		help.setIcon(new ImageIcon(this.getClass().getResource("img/help.png")));
		this.add(help);
	}
	
	public void runProgram(){
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
		
		plot.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						String profilePath = profile.getText();
						int dim = 2;
						if(((String)dimension.getSelectedItem()).equals("3 Dimensions")){
							dim = 3;
						}
						
						try {
							Profile data = Reader.readProfile(profilePath);
							
							double[][] dataPoints = data.getData();
							String[] classes = data.getClasses();
							int[] proportions = new int[classes.length];
							String[] sampleGroups = data.getSampleGroups();
							
							// obtain which group has how many samples to init arrays
							for(int g=0; g<classes.length; g++){
								proportions[g] = 0;
								for(int i=0; i<dataPoints[0].length; i++){
									if(sampleGroups[i].equals(classes[g])){
										proportions[g]++;
									}
								}
							}
							
							// lookup how many dimensions are in the pca transformed dataset
							if(dataPoints.length>=3){
								HashMap<String, double[][]> groups = new HashMap<>();
								// obtain the subarrays for the classes
								// containing all samples of a certain class
								for(int g=0; g<classes.length; g++){
									double[][] sub = new double[dim][proportions[g]];
									int cnt = 0;
									for(int i=0; i<dataPoints[0].length; i++){
										if(sampleGroups[i].equals(classes[g])){
											sub[0][cnt] = dataPoints[0][i];
											sub[1][cnt] = dataPoints[1][i];
											if(dim==3){
												sub[2][cnt] = dataPoints[2][i];
											}
											cnt++;
										}
									}
									// push subarray of class to Hash
									groups.put(classes[g], sub);
								}
								
								// create plot
								if(dim==3){
									Plot3DPanel plot = new Plot3DPanel("SOUTH");
									
									for(int i=0; i<classes.length; i++){
										double[][] values = groups.get(classes[i]);
										plot.addScatterPlot(classes[i], values[0], values[1], values[2]);
									}
									plot.setAxisLabels("PC1", "PC2", "PC3");
									
									// show plot
									JFrame frame = new JFrame("3D Plot");
									frame.setSize(600, 600);
									frame.setContentPane(plot);
									frame.setVisible(true);
								}else{
									Plot2DPanel plot = new Plot2DPanel("SOUTH");
									
									for(int i=0; i<classes.length; i++){
										double[][] values = groups.get(classes[i]);
										plot.addScatterPlot(classes[i], values[0], values[1]);
									}
									plot.setAxisLabels("PC1", "PC2");
									
									// show plot
									JFrame frame = new JFrame("2D Plot");
									frame.setSize(600, 600);
									frame.setContentPane(plot);
									frame.setVisible(true);
								}
							}else if(dataPoints.length==2){
								
								HashMap<String, double[][]> groups = new HashMap<>();
								// obtain the subarrays for the classes
								// containing all samples of a certain class
								for(int g=0; g<classes.length; g++){
									double[][] sub = new double[2][proportions[g]];
									int cnt = 0;
									for(int i=0; i<dataPoints[0].length; i++){
										if(sampleGroups[i].equals(classes[g])){
											sub[0][cnt] = dataPoints[0][i];
											sub[1][cnt] = dataPoints[1][i];
											cnt++;
										}
									}
									// push subarray of class to Hash
									groups.put(classes[g], sub);
								}
								
								// create plot
								Plot2DPanel plot = new Plot2DPanel("SOUTH");
									
									for(int i=0; i<classes.length; i++){
										double[][] values = groups.get(classes[i]);
										plot.addScatterPlot(classes[i], values[0], values[1]);
									}
									plot.setAxisLabels("PC1", "PC2");
									
									// show plot
									JFrame frame = new JFrame("2D Plot");
									frame.setSize(600, 600);
									frame.setContentPane(plot);
									frame.setVisible(true);
									
									if(dim==3){
										JFrame frame2 = new JFrame();
										JOptionPane.showMessageDialog(frame2, 
											"The dataset only has 2 dimensions to display.", 
											"Notice", 
											JOptionPane.INFORMATION_MESSAGE);
									}
							}else{
								// error message
								JFrame frame = new JFrame();
								JOptionPane.showMessageDialog(frame, 
									"The dataset only has 1 dimension. "
											+ "Too low to display.", 
									"Error", 
									JOptionPane.ERROR_MESSAGE);
							}
							
						} catch (IOException ex) {
							Logger.getLogger(ScorePlotWindow.class.getName()).log(Level.SEVERE, null, ex);
						} catch (ParseException ex) {
							Logger.getLogger(ScorePlotWindow.class.getName()).log(Level.SEVERE, null, ex);
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
						String msg = "The information in every profile can be displayed in a score plot\n"
								+ "if the pca transformed data in the profile has at least two dimensions.\n\n"
								+ "Elements:\n"
								+ "Path to the profile file: This is the complete path including name\n"
								+ "    to the profile file including the transformed data to plot.\n"
								+ "search: When you press the search button on the right side of the textarea\n"
								+ "    a search dialog opens. If you have found your desired file\n"
								+ "    you can either double click on it or mark it and click the \"ok\" button.\n"
								+ "Info: Once you selected a profile file you can load basic info\n"
								+ "    about the profile and display it. The info includes things as\n"
								+ "    number of dimensions, MS device used, comments etc.\n"
								+ "dimensions: The dropdown menu for the number of dimensions\n"
								+ "    to be plottet.\n\n"
								+ "Once you filled everything out you can click on the\"create\" button\n"
								+ "and the plot will be rendered for you.";
						JOptionPane.showMessageDialog(frame, 
									msg, 
									"Help", 
									JOptionPane.QUESTION_MESSAGE);
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
