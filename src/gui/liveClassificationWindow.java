package gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class liveClassificationWindow extends JFrame {
	
	JPanel main;
	JLabel folderLabel;
	JTextField folder;
	JButton folderSearch;
	JLabel machineLabel;
	JComboBox machine;
	JLabel databaseLabel;
	JPanel innerDatabasePanel;
	JScrollPane databasePane;
	JCheckBox[] database;
	JLabel classificationLabel;
	JCheckBox algA;
	JCheckBox algB;
	JButton cancel;
	JButton classify;
	JButton help;

	public liveClassificationWindow() 
			throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			UnsupportedLookAndFeelException {
		super("setup for classification of Mass-spectograms");
		initGui();
	}
	
	
	private void initGui() 
			throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			UnsupportedLookAndFeelException{
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		setSize(640, 480);
		setVisible(true);
		setResizable(false);
		// positon on screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (dim.width-640)/2;
		int y = (dim.height-480)/2;
		this.setLocation(x, y);
		
		main = new JPanel();
		main.setVisible(true);
		main.setLayout(null); 
		main.setBounds(0, 0, 640, 480);
		
		folderLabel = new JLabel("folder with CSV files");
		folder = new JTextField();
		folderSearch = new JButton("search");
		folderLabel.setBounds(10, 10, 200, 15);
		folder.setBounds(10, 30, 200, 30);
		folderSearch.setBounds(220, 30, 90, 30);
		main.add(folderLabel);
		main.add(folder);
		main.add(folderSearch);
		
		machineLabel = new JLabel("Type of machine");
		machine = new JComboBox();
		machine.addItem("Mini 11");
		machine.addItem("Exactive");
		machineLabel.setBounds(330, 10, 200, 15);
		machine.setBounds(330, 30, 300, 30);
		main.add(machineLabel);
		main.add(machine);
		
		databaseLabel = new JLabel("choose databases");
		databaseLabel.setBounds(10, 80, 200, 15);
		innerDatabasePanel = new JPanel();
		innerDatabasePanel.setLayout(new GridLayout(40,0));
		database = new JCheckBox[40];
		for(int i=0; i<database.length; i++){
			database[i] = new JCheckBox("Databasesubstance " + i);
			innerDatabasePanel.add(database[i]);
		}
		databasePane = new JScrollPane(innerDatabasePanel, 
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		databasePane.setBounds(10, 100, 400, 200);
		main.add(databaseLabel);
		main.add(databasePane);
		
		classificationLabel = new JLabel("classification Algorithm");
		algA = new JCheckBox("Algorithm A");
		algB = new JCheckBox("Algorithm B");
		classificationLabel.setBounds(10, 320, 200, 15);
		algA.setBounds(10, 350, 150, 20);
		algB.setBounds(170, 350, 150, 20);
		main.add(classificationLabel);
		main.add(algA);
		main.add(algB);
		
		cancel = new JButton("cancel");
		cancel.setBounds(420, 410, 100, 30);
		main.add(cancel);
		
		classify = new JButton("classify");
		classify.setBounds(530, 410, 100, 30);
		main.add(classify);
		
		help = new JButton("help");
		help.setBounds(10, 410, 100, 30);
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
		
		classify.addActionListener(
				new ActionListener(){
					
					/** Display a JFileChooser when pressing the "search" button
					 * 
					 * @param e ActionEvent that occurs when you press the button
					 */
					public void actionPerformed(ActionEvent e){
						try {
							liveWindow x = new liveWindow();
							setVisible(false);
							x.runProgram();
						} catch (ClassNotFoundException ex) {
							Logger.getLogger(liveClassificationWindow.class.getName()).log(Level.SEVERE, null, ex);
						} catch (InstantiationException ex) {
							Logger.getLogger(liveClassificationWindow.class.getName()).log(Level.SEVERE, null, ex);
						} catch (IllegalAccessException ex) {
							Logger.getLogger(liveClassificationWindow.class.getName()).log(Level.SEVERE, null, ex);
						} catch (UnsupportedLookAndFeelException ex) {
							Logger.getLogger(liveClassificationWindow.class.getName()).log(Level.SEVERE, null, ex);
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
