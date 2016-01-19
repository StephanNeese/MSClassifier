/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import Spectrum.Profile;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/** This class creates and displays an info dialog
 * to show some of the properties of a given profile.
 *
 * @author Stephan Neese
 */
public class ProfileInfoDialog extends JFrame {
	
	private JPanel main;
	private JLabel created;
	private JLabel device;
	private JLabel dimensions;
	private JLabel variance;
	private JLabel algorithm;
	private JLabel log;
	private JLabel separator;
	private JLabel commentLabel;
	private JScrollPane commentPane;
	private JTextArea comment;
	
	/** construct a ProfileInfoDialog
	 * 
	 * @param profile the profile to display information
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws UnsupportedLookAndFeelException 
	 */
	public ProfileInfoDialog(Profile profile) 
			throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			UnsupportedLookAndFeelException{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		setTitle("Profile Information");
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int xDim = (dim.width-400)/2;
		int yDim = (dim.height-190)/2;
		this.setLocation(xDim, yDim);
		
		main = new JPanel();
		main.setBounds(0, 0, 400, 180);
		main.setVisible(true);
		main.setLayout(null); 
		
		created = new JLabel("Profile created on: " + profile.getDatetime().toString());
		created.setBounds(10, 10, 350, 30);
		main.add(created);
		device = new JLabel("Profile created for device: " + profile.getDevice());
		device.setBounds(10, 30, 350, 30);
		main.add(device);
		double[][] dims = profile.getLdaCovarianceMatrix();
		dimensions = new JLabel(dims.length + " Dimensions in Profile data");
		dimensions.setBounds(10, 50, 350, 30);
		main.add(dimensions);
		variance = new JLabel("covered variance: " + profile.getVariance());
		variance.setBounds(10, 70, 350, 30);
		main.add(variance);
		algorithm = new JLabel("PCA algorithm used: " + profile.getAlgorithm());
		algorithm.setBounds(10, 90, 350, 30);
		main.add(algorithm);
		if(profile.getLog()){
			log = new JLabel("Data in profile is log scaled");
		}else{
			log = new JLabel("Data in profile is not log scaled");
		}
		log.setBounds(10, 110, 350, 30);
		main.add(log);
		separator = new JLabel("csv column separator: " + profile.getSeparator());
		separator.setBounds(10, 130, 350, 30);
		main.add(separator);
		// format the comment string
		commentLabel = new JLabel("comments:");
		commentLabel.setBounds(10, 150, 350, 40);
		main.add(commentLabel);
		comment = new JTextArea();
		comment.setText(profile.getComment());
		comment.setLineWrap(true);
        comment.setWrapStyleWord(true);
		comment.setEditable(false);
		commentPane = new JScrollPane(comment, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		commentPane.setBounds(10, 180, 370, 60);
		main.add(commentPane);
		
		this.add(main);
		
		// make all visible
		this.setVisible(true);
		this.setMinimumSize(new Dimension(400, 280));
		this.setResizable(false);
	}
}
