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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author wens
 */
public class ProfileInfoDialog extends JFrame {
	
	private JPanel main;
	private JLabel created;
	private JLabel dimensions;
	private JLabel variance;
	private JLabel log;
	
	public ProfileInfoDialog(Profile profile) 
			throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			UnsupportedLookAndFeelException{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		setTitle("Profile Information");
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int xDim = (dim.width-400)/2;
		int yDim = (dim.height-150)/2;
		this.setLocation(xDim, yDim);
		
		main = new JPanel();
		main.setBounds(0, 0, 400, 160);
		main.setVisible(true);
		main.setLayout(null); 
		
		created = new JLabel("Profile created on: " + profile.getDatetime().toString());
		created.setBounds(10, 10, 350, 30);
		main.add(created);
		double[][] dims = profile.getLdaCovarianceMatrix();
		dimensions = new JLabel(dims.length + " Dimensions in Profile data");
		dimensions.setBounds(10, 40, 350, 30);
		main.add(dimensions);
		variance = new JLabel("covered variance: " + profile.getVariance());
		variance.setBounds(10, 70, 350, 30);
		main.add(variance);
		if(profile.getLog()){
			log = new JLabel("Data in profile is log scaled");
		}else{
			log = new JLabel("Data in profile is not log scaled");
		}
		log.setBounds(10, 100, 350, 30);
		main.add(log);
		
		this.add(main);
		
		// make all visible
		this.setVisible(true);
		this.setMinimumSize(new Dimension(400, 160));
		this.setResizable(false);
	}
}
