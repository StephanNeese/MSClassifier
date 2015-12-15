package gui;

import Spectrum.SpectraMatrix;
import com.jidesoft.swing.CheckBoxTree;
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
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import preprocessing.LDA;
import preprocessing.LDADataSet;

/** This class inits the window
 * to create a new Profile.
 * 
 * @author Stephan Neese
 */
public class NewProfileWindow extends JPanel {
	
//	JPanel main;
	JLabel databaseLabel;
	DefaultMutableTreeNode root;
	DefaultTreeModel treeModel;
	JList selectedList = new JList();
	String rootPath;
	CheckBoxTree tree;
	HashMap<String, Integer> selectionCounter = new HashMap<>();
	JScrollPane databasePane;
	JButton databaseButton;
	JLabel profileLabel;
	JTextField profile;
	JButton profileSearch;
	JLabel machineLabel;
	JComboBox machine;
	JLabel binLabel;
	JTextField bin;
	JLabel varianceLabel;
	JTextField variance;
	JLabel backgroundLabel;
	JTextField background;
	JButton backgroundSearch;
	JLabel logLabel;
	JCheckBox log;
	JSeparator sep;
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
	public NewProfileWindow() 
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
		
		databaseLabel = new JLabel("chose the folders containing the csv files");
		root = new DefaultMutableTreeNode("please choose folder");
		treeModel = new DefaultTreeModel(root);
		tree = new CheckBoxTree(treeModel);
		databasePane = new JScrollPane(tree, 
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		databasePane.setBounds(10, 30, 300, 310);
		databaseLabel.setBounds(10, 10, 280, 15);
		databaseButton = new JButton("choose root folder");
		databaseButton.setBounds(10, 350, 150, 30);
		this.add(databaseLabel);
		this.add(databasePane);
		this.add(databaseButton);
		
		machineLabel = new JLabel("Type of machine");
		machine = new JComboBox();
		machine.addItem("Mini 11");
		machine.addItem("Exactive");
		machineLabel.setBounds(330, 10, 200, 15);
		machine.setBounds(330, 30, 300, 30);
		this.add(machineLabel);
		this.add(machine);
		
		binLabel = new JLabel("Size of a bin");
		bin = new JTextField();
		binLabel.setBounds(330, 80, 200, 15);
		bin.setBounds(330, 100, 300, 30);
		this.add(binLabel);
		this.add(bin);
		
		varianceLabel = new JLabel("variance covered");
		variance = new JTextField();
		varianceLabel.setBounds(330, 150, 200, 15);
		variance.setBounds(330, 170, 300, 30);
		this.add(varianceLabel);
		this.add(variance);
		
		profileLabel = new JLabel("Name and path of the profile");
		profile = new JTextField();
		profileSearch = new JButton("search");
		profileLabel.setBounds(330, 220, 250, 15);
		profile.setBounds(330, 240, 200, 30);
		profileSearch.setBounds(540, 240, 90, 30);
		this.add(profileLabel);
		this.add(profile);
		this.add(profileSearch);
		
		backgroundLabel = new JLabel("Path to background spectra");
		background = new JTextField();
		backgroundSearch = new JButton("search");
		backgroundLabel.setBounds(330, 290, 250, 15);
		background.setBounds(330, 310, 200, 30);
		backgroundSearch.setBounds(540, 310, 90, 30);
		this.add(backgroundLabel);
		this.add(background);
		this.add(backgroundSearch);
		
		logLabel = new JLabel("log transformation");
		logLabel.setBounds(360, 357, 200, 15);
		this.add(logLabel);
		log = new JCheckBox();
		log.setBounds(330, 350, 30, 30);
		this.add(log);
		
		sep = new JSeparator();
		sep.setBounds(10, 405, 620, 10);
		this.add(sep);
		
		cancel = new JButton("cancel");
		cancel.setBounds(420, 420, 100, 35);
		cancel.setIcon(new ImageIcon(this.getClass().getResource("img/exit.png")));
		this.add(cancel);
		
		create = new JButton("create");
		create.setBounds(530, 420, 100, 35);
		create.setIcon(new ImageIcon(this.getClass().getResource("img/go.png")));
		this.add(create);
		
		help = new JButton("help");
		help.setBounds(10, 420, 100, 35);
		help.setIcon(new ImageIcon(this.getClass().getResource("img/help.png")));
		this.add(help);
	}
	
	/** initializes all the ActionListeners 
	 * for the GUI elements
	 */
	public void runProgram(){
		/** adds a Listener to the button for searching
		 * for the root folder to create a folder tree.
		 * 
		 */
		databaseButton.addActionListener(
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
							
							rootPath = fileChooser.getSelectedFile().getAbsolutePath();
							root.removeAllChildren();
							root.setUserObject(fileChooser.getSelectedFile().getName());
							listFolders(rootPath, root);
							treeModel.reload(root);
							tree.repaint();
							frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
						}else if(result != JFileChooser.APPROVE_OPTION){
							frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
						}
					}
					
					/** adds folders as subnodes to a TreeNode
					 * 
					 * @param rootPath path of the folder
					 * @param root node of the folder
					 */
					private void listFolders(String rootPath, DefaultMutableTreeNode root){
						File rootFolder = new File(rootPath);
						File[] files = rootFolder.listFiles();
						for(File f : files){
							if(f.isDirectory()){
								DefaultMutableTreeNode folder = new DefaultMutableTreeNode(f.getName());
								root.add(folder);
								listFolders(f.getAbsolutePath(), folder);
							}
						}
					}
				}
		);
		
		selectedList = new JList();
        final JList eventsList = new JList();
        final DefaultListModel eventsModel = new DefaultListModel();
		tree.getCheckBoxTreeSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath[] paths = e.getPaths();
                for (TreePath path : paths) {
                    eventsModel.addElement((e.isAddedPath(path) ? "Added - " : "Removed - ") + path);
                }
                eventsModel.addElement("---------------");
                eventsList.ensureIndexIsVisible(eventsModel.size() - 1);

                TreePath[] treePaths = tree.getCheckBoxTreeSelectionModel().getSelectionPaths();
                DefaultListModel selectedModel = new DefaultListModel();
                if (treePaths != null) {
                    for (TreePath path : treePaths) {
                        selectedModel.addElement(path);
                        for (TreePath childPath : getChildrenElement(path)) {
                            selectedModel.addElement(childPath);
                        }
                    }
                }
                selectedList.setModel(selectedModel);
            }

            private List<TreePath> getChildrenElement(TreePath parentPath) {
                List<TreePath> childList = new ArrayList<TreePath>();
                Object parentNode = parentPath.getLastPathComponent();
                int childCount = tree.getModel().getChildCount(parentNode);
                for (int i = 0; i < childCount; i++) {
                    Object child = tree.getModel().getChild(parentNode, i);
                    final TreePath childPath = parentPath.pathByAddingChild(child);
                    childList.add(childPath);
                    childList.addAll(getChildrenElement(childPath));
                }
                return childList;
            }
        });
		
		profileSearch.addActionListener(
				new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						JFileChooser fileChooser = new JFileChooser();
						// set only directories and disable "all files" option
						fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						fileChooser.setAcceptAllFileFilterUsed(false);
						
						JFrame frame = new JFrame();
						int result = fileChooser.showOpenDialog(frame);
						
						// if file is selected 
						if (result == JFileChooser.APPROVE_OPTION){
							profile.setText(fileChooser.getSelectedFile().getAbsolutePath() + ".profile");
							frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
						}else if(result != JFileChooser.APPROVE_OPTION){
							frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
						}
					}
					
				});
		
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
		
		backgroundSearch.addActionListener(
				new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						JFileChooser fileChooser = new JFileChooser();
						// set only directories and disable "all files" option
						fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						fileChooser.setAcceptAllFileFilterUsed(false);
						
						JFrame frame = new JFrame();
						int result = fileChooser.showOpenDialog(frame);
						
						// if file is selected 
						if (result == JFileChooser.APPROVE_OPTION){
							background.setText(fileChooser.getSelectedFile().getAbsolutePath());
							frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
						}else if(result != JFileChooser.APPROVE_OPTION){
							frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
						}
					}
				}
		);
		
		create.addActionListener(new ActionListener(){
					
					/** calculate distances for spectras when pressed
					 * 
					 * @param e ActionEvent that occurs when you press the button
					 */
					@Override
					public void actionPerformed(ActionEvent e){
						// get data from text fields
						String[] profilePaths = getSelected(selectedList.getModel());
						String machineName = (String)machine.getSelectedItem();
						String profileName = profile.getText();
						String binTmp = bin.getText();
						String varianceTmp = variance.getText();
						String backgroundPath = background.getText();
						
						// check parameters first
						if(!("".equals(checkParams(profilePaths, profileName, binTmp, varianceTmp, backgroundPath)))){
							JFrame frame = new JFrame();						
							JOptionPane.showMessageDialog(frame, 
									checkParams(profilePaths, profileName, binTmp, varianceTmp, backgroundPath),
									"Invalid Input", 
									JOptionPane.ERROR_MESSAGE);
						}else{
							double binSize = Double.parseDouble(binTmp);
							double varianceCovered = Double.parseDouble(varianceTmp);
							try{
								SpectraMatrix data = Reader.readData(profilePaths, rootPath, binSize, machineName, log.isSelected(), backgroundPath);
								data.deleteEmptyBins();
								data.calculateDimensionMeans();
								PCADataSet pca_data = PCA.performPCA(data, varianceCovered);
								LDADataSet lda_data = LDA.performLDA(pca_data, data);
								// create profile
								ProfileBuilder.build(
										pca_data, 
										lda_data,
										data, 
										machineName, 
										rootPath, 
										profileName, 
										1.0);
							
								// show success message
								JFrame frame = new JFrame();
								JOptionPane.showMessageDialog(frame, 
										"Profile has been created.", 
										"Done", 
										JOptionPane.INFORMATION_MESSAGE);
							}catch (Exception ex) {
								Logger.getLogger(NewProfileWindow.class.getName()).log(Level.SEVERE, null, ex);
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
					private String checkParams(String[] profilePaths, String profile, String bin, String variance, String background){
						String res = "";
						
						boolean check = true;
						if(profilePaths.length==0){
							check = false;
						}
						for(String path : profilePaths){
							File x = new File(path);
							if("".equals(path)){
								check = false;
							}else if(!(x.exists()) && !(x.isDirectory())){
								check = false;
							}
						}
						if(!check){
							res += "Error: No folders have been chosen containing csv files\n"
									+ "for creating profiles or the folders are invalid\n"
									+ "or not readable. Please check folder permissions.\n";
						}
						if("".equals(profile)){
							res += "Error: The name of the profile file is empty\n";
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
						if(!("".equals(background))){
							File bg = new File(background);
							if(!(bg.exists() && bg.isDirectory())){
								res += "The path to the background folder is invalid. "
										+ "The path either does not exist or is not a directory.";
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

						if (Pattern.matches(fpRegex, x)){
							return true;
						}else {
							return false;
						}
					}
					
					private String[] getSelected(ListModel x){
						ArrayList<String> tmp = new ArrayList<>();
						int listSize = x.getSize();
						
						// change treepaths to actual folder paths
						for(int i=0; i<listSize; i++){
							String a = x.getElementAt(i).toString().replaceAll("[\\[\\]]", "");
							String[] b = a.split(", ");
							String c = "";
							for(int j=1; j<b.length; j++){
								c += File.separator + b[j];
							}
							// filter out root
							if(!(c.equals(""))){
								tmp.add(c);
							}
						}
						
						ArrayList<String> tmp2 = new ArrayList<>();
						// check if there are parent folders in the list
						// and delete them
						for(int i=0; i<tmp.size(); i++){
							boolean unique = true;
							for(int j=0; j<tmp.size(); j++){
								// if path in index i is subpath of another one
								// then it is a parent and kicked out
								if(i!=j && tmp.get(j).startsWith(tmp.get(i))){
									unique = false;
								}
							}
							if(unique){
								tmp2.add(tmp.get(i));
							}
						}
						
						String[] res = new String[tmp2.size()];
						for(int i=0; i<tmp2.size(); i++){
							res[i] = rootPath + tmp2.get(i);
						}
						
						return res;
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
