package gui;

import Spectrum.ClassificationResult;
import Spectrum.Profile;
import Spectrum.Spectrum;
import io.Reader;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.ParseException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class liveWindow extends JFrame {
	
	JPanel main;
	public JLabel substance;
	public JLabel probability;
	public JLabel distance;
	public JLabel filename;
	JButton close;
	
	public liveWindow() 
			throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			UnsupportedLookAndFeelException{
		super("classification");
		initGui();
	}
	
	private void initGui() 
			throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			UnsupportedLookAndFeelException{
		setLayout(null);
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		setSize(160, 100);
		setUndecorated(true);
		setVisible(true);
		setResizable(false);
		
		main = new JPanel();
		main.setVisible(true);
		main.setLayout(null); 
		main.setBounds(0, 0, 160, 100);
		// positon on screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int width = dim.width;
		int height = dim.height;
		this.setLocation(width-160, height-100);
		
		filename = new JLabel("Filename");
		filename.setFont(filename.getFont().deriveFont(10.0f));
		filename.setBounds(10, 0, 140, 20);
		main.add(filename);
		substance = new JLabel("Class");
		substance.setFont(substance.getFont().deriveFont(20.0f));
		substance.setBounds(10, 15, 140, 20);
		main.add(substance);
		probability = new JLabel("Confidence Score");
		probability.setFont(probability.getFont().deriveFont(12.0f));
		probability.setBounds(10, 38, 140, 20);
		main.add(probability);
		distance = new JLabel("Distance");
		distance.setFont(distance.getFont().deriveFont(12.0f));
		distance.setBounds(10, 54, 140, 20);
		main.add(distance);
		close = new JButton("close");
		close.setBounds(10, 75, 80, 20);
		main.add(close);
		
		add(main);
	}
	
	public void runProgram(){
		close.addActionListener(
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
