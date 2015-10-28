package gui;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author wens
 */
public class WaitMessage extends Thread{
	
	// threading
	private Thread t;
	private String threadName;
	private JFrame frame;
	private JLabel image;
	private JLabel text;
	private JLabel text2;
	
	public WaitMessage() 
			throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			UnsupportedLookAndFeelException{
		threadName = "loading";
		frame = new JFrame();
		frame.setLayout(null);
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		frame.setTitle("Please Wait");
		frame.setSize(500, 100);
		frame.setVisible(true);
		frame.setResizable(false);
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		frame.setLocation((width-320)/2, (height-100)/2);
		
		image= new JLabel(new ImageIcon(this.getClass().getResource("img/time.png")));
		image.setBounds(5, 5, 48, 48);
		frame.add(image);
		
		text = new JLabel("Please wait while the profile loads.");
		text2 = new JLabel( "This can take up several minutes depending on the filesize.");
		text.setFont(text.getFont().deriveFont(12.0f));
		text2.setFont(text2.getFont().deriveFont(12.0f));
		text.setBounds(60, 5, 450, 20);
		text2.setBounds(60, 25, 450, 20);
		frame.add(text);
		frame.add(text2);
	}
	
	/** Overwritten method from class Thread. 
	 * This method starts the thread
	 * 
	 */
	public void start(){
		if(t==null){
			t = new Thread(this, threadName);
			t.start();
		}
	}
	
	public void close(){
		frame.setVisible(false);
	}
}
