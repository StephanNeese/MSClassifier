package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class liveWindow extends JFrame {
	
	JPanel main;
	JLabel substance;
	JLabel probability;
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
		
		substance = new JLabel("Substance X");
		substance.setFont(substance.getFont().deriveFont(20.0f));
		probability = new JLabel("P = 0.7801");
		probability.setFont(probability.getFont().deriveFont(14.0f));
		close = new JButton("close");
		substance.setBounds(10, 10, 140, 20);
		probability.setBounds(10, 40, 140, 20);
		close.setBounds(10, 70, 80, 20);
		main.add(substance);
		main.add(probability);
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
