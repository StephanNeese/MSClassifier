package gui;

import Spectrum.SpectraMatrix;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import preprocessing.PCA;
import preprocessing.PCADataSet;
import preprocessing.ProfileBuilder;
import preprocessing.Reader;

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

	public newDatabaseWindow() 
			throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			UnsupportedLookAndFeelException {
		super("Creation of a new profile");
		initGui();
	}
	
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
		
		create.addActionListener(
				new ActionListener(){
					
					/** Display a JFileChooser when pressing the "search" button
					 * 
					 * @param e ActionEvent that occurs when you press the button
					 */
					@Override
					public void actionPerformed(ActionEvent e){
						// get data from text fields
						String folderPath = folder.getText();
						String machineName = (String)machine.getSelectedItem();
						String profileName = name.getText();
						double binTmp = Double.parseDouble(bin.getText());
						int binSize = (int)binTmp;
						double varianceCovered = Double.parseDouble(variance.getText());
						
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
