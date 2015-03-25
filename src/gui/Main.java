package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main extends JFrame {
	
	JPanel main;
	JButton newDB;
	JButton classification;
	JButton liveClassification;
	
	public Main() 
			throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			UnsupportedLookAndFeelException{
		super("Mass-Spec image classification");
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
		setSize(400, 300);
		setVisible(true);
		setResizable(false);
		// positon on screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (dim.width-400)/2;
		int y = (dim.height-300)/2;
		this.setLocation(x, y);
		
		main = new JPanel();
		main.setVisible(true);
		main.setLayout(null); 
		main.setBounds(0, 0, 400, 300);
		
		newDB = new JButton("create new profile");
		newDB.setBounds(10, 30, 380, 50);
		main.add(newDB);
		
		classification = new JButton("classify spectograms");
		classification.setBounds(10, 100, 380, 50);
		main.add(classification);
		
		liveClassification = new JButton("live classification");
		liveClassification.setBounds(10, 170, 380, 50);
		main.add(liveClassification);
		
		add(main);
	}
	
	public void runProgram(){
		newDB.addActionListener(
				new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							newDatabaseWindow x = new newDatabaseWindow();
							setVisible(false);
							x.runProgram();
						} catch (ClassNotFoundException ex) {
							Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
						} catch (InstantiationException ex) {
							Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
						} catch (IllegalAccessException ex) {
							Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
						} catch (UnsupportedLookAndFeelException ex) {
							Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
					
				}
		);
		classification.addActionListener(
				new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							classificationWindow x = new classificationWindow();
							setVisible(false);
							x.runProgram();
						} catch (ClassNotFoundException ex) {
							Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
						} catch (InstantiationException ex) {
							Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
						} catch (IllegalAccessException ex) {
							Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
						} catch (UnsupportedLookAndFeelException ex) {
							Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
					
				}
		);
		liveClassification.addActionListener(
				new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							liveClassificationWindow x = new liveClassificationWindow();
							setVisible(false);
							x.runProgram();
						} catch (ClassNotFoundException ex) {
							Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
						} catch (InstantiationException ex) {
							Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
						} catch (IllegalAccessException ex) {
							Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
						} catch (UnsupportedLookAndFeelException ex) {
							Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
					
				}
		);
	}
	
	public static void main(String[] args) 
			throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			UnsupportedLookAndFeelException {
		new Main().runProgram();
	}
}
