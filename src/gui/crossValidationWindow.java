package gui;

import com.jidesoft.swing.CheckBoxTree;
import io.CrossValidationParameterSet;
import io.crossValidation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.apache.commons.io.FileDeleteStrategy;

/** This class constructs and displays the
 * cross validation panel on the main window.
 * 
 * @author Stephan Neese
 */
public class crossValidationWindow extends JPanel {
	
	JLabel databaseLabel;
	DefaultMutableTreeNode root;
	DefaultTreeModel treeModel;
	JList selectedList = new JList();
	String rootPath;
	CheckBoxTree tree;
	HashMap<String, Integer> selectionCounter = new HashMap<>();
	JScrollPane databasePane;
	JButton databaseButton;
	JLabel crossValidationFolderLabel;
	JTextField crossValidationFolder;
	JButton crossValidationFolderSearch;
	JLabel machineLabel;
	JComboBox machine;
	JLabel separatorLabel;
	JComboBox separator;
	JLabel binLabel;
	JTextField bin;
	JLabel algorithmLabel;
	JComboBox algorithm;
	JLabel varianceLabel;
	JTextField variance;
	JLabel dimensionsLabel;
	JTextField dimensions;
	JLabel backgroundLabel;
	JTextField background;
	JButton backgroundSearch;
	JLabel logLabel;
	JCheckBox log;
	JSeparator sep;
	JButton cancel;
	JButton go;
	JButton help;

	/** init a crossValidationWindow
	 * 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws UnsupportedLookAndFeelException 
	 */
	public crossValidationWindow() 
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
		databasePane.setBounds(10, 30, 300, 380);
		databaseLabel.setBounds(10, 10, 280, 15);
		databaseButton = new JButton("choose root folder");
		databaseButton.setBounds(10, 420, 150, 30);
		this.add(databaseLabel);
		this.add(databasePane);
		this.add(databaseButton);
		
		machineLabel = new JLabel("Type of machine");
		machine = new JComboBox();
		machine.addItem("Mini 11");
		machine.addItem("Exactive");
		machineLabel.setBounds(330, 10, 145, 15);
		machine.setBounds(330, 30, 145, 30);
		this.add(machineLabel);
		this.add(machine);
		
		separatorLabel = new JLabel("csv column separator");
		separator = new JComboBox();
		separator.addItem(",");
		separator.addItem(";");
		separator.addItem("TAB");
		separatorLabel.setBounds(485, 10, 145, 15);
		separator.setBounds(485, 30, 145, 30);
		this.add(separatorLabel);
		this.add(separator);
		
		binLabel = new JLabel("Size of a bin");
		bin = new JTextField();
		binLabel.setBounds(330, 80, 200, 15);
		bin.setBounds(330, 100, 300, 30);
		this.add(binLabel);
		this.add(bin);
		
		algorithmLabel = new JLabel("choose PCA Method");
		algorithm = new JComboBox();
		algorithm.addItem("QR Algorithm");
		algorithm.addItem("NIPALS");
		algorithmLabel.setBounds(330, 150, 200, 15);
		algorithm.setBounds(330, 170, 300, 30);
		this.add(algorithmLabel);
		this.add(algorithm);
		
		varianceLabel = new JLabel("variance covered");
		variance = new JTextField();
		varianceLabel.setBounds(330, 220, 145, 15);
		variance.setBounds(330, 240, 145, 30);
		this.add(varianceLabel);
		this.add(variance);
		
		dimensionsLabel = new JLabel("number of dimensions");
		dimensions = new JTextField();
		dimensionsLabel.setForeground(new Color(120,120,120));
		dimensionsLabel.setBounds(485, 220, 145, 15);
		dimensions.setBounds(485, 240, 145, 30);
		dimensions.setEditable(false);
		this.add(dimensionsLabel);
		this.add(dimensions);
		
		crossValidationFolderLabel = new JLabel("where to store the cross validation data");
		crossValidationFolder = new JTextField();
		crossValidationFolderSearch = new JButton("search");
		crossValidationFolderLabel.setBounds(330, 290, 300, 15);
		crossValidationFolder.setBounds(330, 310, 200, 30);
		crossValidationFolderSearch.setBounds(540, 310, 90, 30);
		this.add(crossValidationFolderLabel);
		this.add(crossValidationFolder);
		this.add(crossValidationFolderSearch);
		
		backgroundLabel = new JLabel("Path to background spectra");
		background = new JTextField();
		backgroundSearch = new JButton("search");
		backgroundLabel.setBounds(330, 360, 250, 15);
		background.setBounds(330, 380, 200, 30);
		backgroundSearch.setBounds(540, 380, 90, 30);
		this.add(backgroundLabel);
		this.add(background);
		this.add(backgroundSearch);
		
		logLabel = new JLabel("log transformation");
		logLabel.setBounds(360, 427, 200, 15);
		this.add(logLabel);
		log = new JCheckBox();
		log.setBounds(330, 420, 30, 30);
		this.add(log);
		
		sep = new JSeparator();
		sep.setBounds(10, 475, 620, 10);
		this.add(sep);
		
		cancel = new JButton("cancel");
		cancel.setBounds(420, 490, 100, 35);
		cancel.setIcon(new ImageIcon(this.getClass().getResource("img/exit.png")));
		this.add(cancel);
		
		go = new JButton("go");
		go.setBounds(530, 490, 100, 35);
		go.setIcon(new ImageIcon(this.getClass().getResource("img/go.png")));
		this.add(go);
		
		help = new JButton("help");
		help.setBounds(10, 490, 100, 35);
		help.setIcon(new ImageIcon(this.getClass().getResource("img/help.png")));
		this.add(help);
	}
	
	/** initializes all the ActionListeners 
	 * for the GUI elements.
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
					@Override
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
		
		algorithm.addActionListener(
				new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						String selection = (String)algorithm.getSelectedItem();
						if(selection.equals("NIPALS")){
							varianceLabel.setForeground(new Color(120,120,120));
							variance.setEditable(false);
							dimensionsLabel.setForeground(new Color(0,0,0));
							dimensions.setEditable(true);
						}else{
							dimensionsLabel.setForeground(new Color(120,120,120));
							dimensions.setEditable(false);
							varianceLabel.setForeground(new Color(0,0,0));
							variance.setEditable(true);
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
		
		crossValidationFolderSearch.addActionListener(
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
							crossValidationFolder.setText(fileChooser.getSelectedFile().getAbsolutePath());
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
						String msg = "In this Tab you can you can carry out a leave-10%-out cross validation to test certain profile parameters.\n\n"
								+ "Elements:\n"
								+ "folder containing csv files: This is where you choose what goes into the profile. Choose a root folder. \n"
								+ "    In the subfolders should lie the spectra to each group of food. If you have for ex. cow milk, goat milk and soy milk \n"
								+ "    as food items then there should only be three subfolders in your choosen root folder: cow milk, goat milk, soy milk (or whatever \n"
								+ "    you would like to name them) each group is named by the subfolder it lies in. You can also have multiple layers of subfolders for ex. milk->cow etc.\n"
								+ "    then the group name will be milk_cow for this group. By clicking the desired subfolders you can choose what food items should go into the profile.\n"
								+ "type of machine: The MS device used for measuring.\n"
								+ "csv column separator: The character that separates the column in the csv files containing the spectra. Each spectrum\n"
								+ "    must be in its own csv file with the mz-value in the first column and the intensity value in the second column.\n"
								+ "size of bin: The size used to create the mz-bins.\n"
								+ "PCA Algorithm: The algorithm used to find the eigenvectors which are used to transform our data matrix (the mz-bins of all spectra) into PC space.\n"
								+ "variance covered: This is the amount of variance of the original dataset that should be covered by PCA.\n"
								+ "    This must be a number between 0 and 1 where 0 is no variance at all and 1 ist all the variance from\n"
								+ "    the original dataset (all dimensions (mz-bins) will get transformed). The amount of covered variance is only available for QR algorithm.\n"
								+ "number of dimensions: When using the NIPALS algorithm for transformation into PC space you cannot choose how much\n"
								+ "    variance should be covered. You can only choose the number of dimensions from the original data matrix to transform into PC space.\n"
								+ "where to store the cross valid.: Give a folder where to store all the data that is produced during cross validation.\n"
								+ "Path to background spectra: If you want to substract background data from all of your spectra then you can give the path to a folder\n"
								+ "    containing csv files containing background data. The csv files must be formatted the same way as the other csv files\n"
								+ "    (two columns (mz, intensity) and same separator). The program calculates the means of every mz-bin\n"
								+ "    of the background over all spectra in the folder and substracts them from the mz-bins of the original data.\n"
								+ "log transformation: Should the data input be log transformed.\n\n"
								+ "Once you filled out all the information you can click on the \"go\" button to start the cross validation.";
						JOptionPane.showMessageDialog(frame, 
									msg, 
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
		
		go.addActionListener(new ActionListener(){
					
					/** calculate distances for spectras when pressed
					 * 
					 * @param e ActionEvent that occurs when you press the button
					 */
					@Override
					public void actionPerformed(ActionEvent e){
						// get data from text fields
						String[] profilePaths = getSelected(selectedList.getModel());
						String machineName = (String)machine.getSelectedItem();
						String crossValidationLocation = crossValidationFolder.getText();
						String binTmp = bin.getText();
						String algorithmSelected = (String)algorithm.getSelectedItem();
						String varianceTmp = variance.getText();
						String dimensionsTmp = dimensions.getText();
						String backgroundPath = background.getText();
						
						// check parameters first
						if(!("".equals(checkParams(profilePaths, crossValidationLocation, binTmp, varianceTmp, dimensionsTmp, backgroundPath, algorithmSelected)))){
							JFrame frame = new JFrame();						
							JOptionPane.showMessageDialog(frame, 
									checkParams(profilePaths, crossValidationLocation, binTmp, varianceTmp, dimensionsTmp, backgroundPath, algorithmSelected),
									"Invalid Input", 
									JOptionPane.ERROR_MESSAGE);
						}else{
							// make cross validation folder
							File cvDir = new File(crossValidationLocation + File.separator + "crossValidation");
							if(cvDir.exists() && cvDir.isDirectory()){
								while(!FileDeleteStrategy.FORCE.deleteQuietly(cvDir)){
									System.gc();
									System.out.println("failed to delete cross validation Dir");
								}
								cvDir.mkdir();
							}else{
								cvDir.mkdir();
							}
							// make subfolders data, profiles and results
							File data = new File(cvDir.getAbsolutePath() + File.separator + "data");
							data.mkdir();
							File profiles = new File(cvDir.getAbsolutePath() + File.separator + "profiles");
							profiles.mkdir();
							File results = new File(cvDir.getAbsolutePath() + File.separator + "results");
							results.mkdir();
							try {
								try {
									// set the Algorithm choosen using an Enum type
									CrossValidationParameterSet.Algorithm alg = CrossValidationParameterSet.Algorithm.QR;
									if(algorithmSelected.equals("NIPALS")){
										alg = CrossValidationParameterSet.Algorithm.NIPALS;
									}
									// create parameter object
									CrossValidationParameterSet params = new CrossValidationParameterSet(
											alg,
											profilePaths,
											rootPath,
											backgroundPath,
											machineName,
											cvDir.getAbsolutePath(),
											results.getAbsolutePath(),
											binTmp,
											varianceTmp,
											dimensionsTmp,
											log.isSelected(),
											(String)separator.getSelectedItem()
									);
									// carry out cross validation
									crossValidation.validate(params);
								} catch (FileNotFoundException ex) {
									Logger.getLogger(crossValidationWindow.class.getName()).log(Level.SEVERE, null, ex);
								} catch (ParseException ex) {
									Logger.getLogger(crossValidationWindow.class.getName()).log(Level.SEVERE, null, ex);
								}
							} catch (IOException ex) {
								Logger.getLogger(crossValidationWindow.class.getName()).log(Level.SEVERE, null, ex);
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
					private String checkParams(String[] profilePaths, String profile, String bin, String variance, String dimensions, String background, String algorithm){
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
						
						// check input depending on which algorithm is selected
						if(algorithm.equals("NIPALS")){
							if(!(parseInt(dimensions))){
								res += "Error: The value for the number of dimensions is not a valid number\n";
							}else{
								int tmp = Integer.parseInt(dimensions);
								if(tmp<1 || tmp>60){
									res += "Error: The number of transformed dimensions must be at least 1 and not greater than 60.\n";
								}
							}
						}else{
							if(!(parseDouble(variance))){
								res += "Error: The value for the covered variance is not a valid number\n";
							}else{
								double tmp = Double.parseDouble(variance);
								if(tmp<0 || tmp>1.0){
									res += "Error: The value for the covered variance must be between 0 and 1.0\n";
								}
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
					
					/** checks if a String can be parsed to int
					 * 
					 * @param x the String containing a number (or something else)
					 * @return true if string can  parsed to int false otherwise
					 */
					private boolean parseInt(String x){
						try{
							int tmp = Integer.parseInt(x);
							return true;
						}catch(Exception e){
							return false;
						}
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
					
					/** returns all the selected paths
					 * from the pane to choose your groups.
					 * 
					 * @param x the ListModel of the selection pane
					 * @return all paths to the groups as string array
					 */
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
