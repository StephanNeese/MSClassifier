package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UnsupportedLookAndFeelException;

/** This class constructs and displays a
 * wait message while a profile loads into memory.
 * 
 * @author Stephan Neese
 */
public class WaitMessage{
	
	private JDialog wait;
	private JPanel panel;
	JLabel waitLabel1;
	JLabel waitLabel2;
	
	/** construct a waitMessage using a given title
	 * 
	 * @param title title of the dialog window for the message
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws UnsupportedLookAndFeelException 
	 */
	public WaitMessage(String title) 
			throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			UnsupportedLookAndFeelException{
		wait = new JDialog();
		panel = new JPanel();
		panel.setBounds(0, 0, 500, 100);
		panel.setLayout(null);
		JLabel image= new JLabel(new ImageIcon(this.getClass().getResource("img/time.png")));
		image.setBounds(5, 10, 48, 48);
		panel.add(image);
		waitLabel1 = new JLabel("Please wait while the profile loads.");
		waitLabel1.setBounds(60, 15, 400, 20);
		panel.add(waitLabel1);
		waitLabel2 = new JLabel("This can take up several minutes depending on the filesize.");
		panel.add(waitLabel2);
		waitLabel2.setBounds(60, 35, 400, 20);
		wait.setTitle(title);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int xDim = (dim.width-400)/2;
		int yDim = (dim.height-150)/2;
		wait.setBounds(xDim, yDim, 500, 100);
		wait.setModal(false);
		wait.add(panel);
		wait.setVisible(true);
	}
	
	public void close(){
		wait.setVisible(false);
	}
}
