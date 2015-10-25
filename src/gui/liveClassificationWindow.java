package gui;

import java.awt.BorderLayout;
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
import javax.swing.UIManager;
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
//		setLayout(null);
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		setSize(640, 310);
//		setVisible(true);
//		setResizable(false);
//		// positon on screen
//		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//		int x = (dim.width-640)/2;
//		int y = (dim.height-310)/2;
//		this.setLocation(x, y);
//		
//		main = new JPanel();
		
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
		profileLabel.setBounds(100, 80, 200, 15);
		profile.setBounds(100, 100, 310, 30);
		profileSearch.setBounds(420, 100, 90, 30);
		this.add(profileLabel);
		this.add(profile);
		this.add(profileSearch);
		
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
		sep.setBounds(10, 405, 620, 10);
		this.add(sep);
		
		cancel = new JButton("cancel");
		cancel.setBounds(420, 420, 100, 35);
		cancel.setIcon(new ImageIcon(this.getClass().getResource("img/exit.png")));
		this.add(cancel);
		
		classify = new JButton("classify");
		classify.setBounds(530, 420, 100, 35);
		classify.setIcon(new ImageIcon(this.getClass().getResource("img/go.png")));
		this.add(classify);
		
		help = new JButton("help");
		help.setBounds(10, 420, 100, 35);
		help.setIcon(new ImageIcon(this.getClass().getResource("img/help.png")));
		this.add(help);
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
