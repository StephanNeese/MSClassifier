package gui;

import io.ProfileOpeningThread;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

/** This class is the parameter input window
 * for live classification.
 * 
 * @author Stephan Neese
 */
public class liveClassificationWindow extends JPanel {
	
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
	public liveClassificationWindow() 
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
	 * for the GUI elements.
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
					
					/** Display a help dialog
					 * 
					 * @param e ActionEvent that occurs when you press the button
					 */
					public void actionPerformed(ActionEvent e){
						JFrame frame = new JFrame();
						String msg = "Using a created profile you can classify new samples of food during a MS run.\n"
								+ "As for the profile creation the input files must be of csv format\n"
								+ "with the mz-value in the first column and the intensity in the second column.\n"
								+ "Each spectrum must be in its own csv file. The program does not recognize\n"
								+ "multiple spectrum files.\n\n"
								+ "Elements:\n"
								+ "folder with CSV files: The folder where the MS device saves the csv files\n"
								+ "    containing the spectra. You can search for the folder using\n"
								+ "    the search button to the right of the field.\n"
								+ "Path to the profile file: The complete path to the profile file to be used\n"
								+ "    for classification of new samples. The \"Info\" button to the right of the\n"
								+ "    \"search\" button loads the choosen profile and gives a short overview\n"
								+ "    of the profile properties such as MS device, number of dimensions etc.\n"
								+ "distance measure: This dropdown menu lets you choose a classification method\n"
								+ "    for your samples. You can choose between three distance measures, namely\n"
								+ "    the euclidean distance, the mahalanobis distance (which accounts for the\n"
								+ "    different variance in the individual dimensions and is therefore the most\n"
								+ "    robust method for classification) and the linear discriminant analysis.\n"
								+ "Where to save the results: Choose a path and filename to a log file\n"
								+ "    containing the results.\n"
								+ "minimal score: Every distance measure additionally calculates a score\n"
								+ "    to verify the quality of the classification. A low score usually\n"
								+ "    indicates an insignificant classification (how confident are we\n"
								+ "    that our classification is correct). This score works quite well\n"
								+ "    with the euclidean and mahalanobis distance but has shown to be\n"
								+ "    often non significant when using LDA. If you want all spectra\n"
								+ "    classified just type 0 in the field.\n\n"
								+ "When you have everything filled out just click the \"classify\" button.\n"
								+ "The profile will be loaded and a small window will appear on the\n"
								+ "lower right corner of the screen. In this window the classification results\n"
								+ "for just measured spectra will be shown. The results will additionally be saved\n"
								+ "to your log file.";
						JOptionPane.showMessageDialog(frame, 
									msg, 
									"Help", 
									JOptionPane.QUESTION_MESSAGE);
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
	
	public JButton getClassifyButton(){
		return classify;
	}

	public JTextField getFolder() {
		return folder;
	}

	public JTextField getProfile() {
		return profile;
	}

	public JComboBox getDistance() {
		return distance;
	}

	public JTextField getSave() {
		return save;
	}

	public JTextField getCutoff() {
		return cutoff;
	}
	
	
}
