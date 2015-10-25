package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author wens
 */
public class TabbedPane extends JFrame {
	
	JTabbedPane mainPanel;
	NewProfileWindow create;
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (InstantiationException e) {
			e.printStackTrace();
		}catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		TabbedPane window = new TabbedPane();
	}
	
	public TabbedPane() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
		setTitle("MS Food classifier");
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int xDim = (dim.width-1024)/2;
		int yDim = (dim.height-600)/2;
		this.setLocation(xDim, yDim);
		
		// create TabbetPlane for Tabs
		mainPanel = new JTabbedPane();
		
		// create tabs
		create = new NewProfileWindow();
//		createModelTab create = new createModelTab("create model");
//		simulateModelTab simulate = new simulateModelTab("simulation");
//		drawModelTab draw = new drawModelTab("draw model");
//		statisticsTab statistics = new statisticsTab("statistics");
//		exportModelTab export = new exportModelTab("export Model");
		
		// create icons
		ImageIcon createIcon = new ImageIcon(this.getClass().getResource("img/create.png"));
//		ImageIcon simulateIcon = new ImageIcon(this.getClass().getResource("img/simulation.png"));
//		ImageIcon drawIcon = new ImageIcon(this.getClass().getResource("img/draw.png"));
//		ImageIcon statisticsIcon = new ImageIcon(this.getClass().getResource("img/statistics.png"));
//		ImageIcon exportIcon = new ImageIcon(this.getClass().getResource("img/export.png"));
		
		// add tabs to pane
		mainPanel.addTab("create Profile", createIcon, create);
//		mainPanel.addTab(simulate.getTitle(), simulateIcon, simulate);
//		mainPanel.addTab(draw.getTitle(), drawIcon, draw);
//		mainPanel.addTab(statistics.getTitle(), statisticsIcon, statistics);
//		mainPanel.addTab(export.getTitle(), exportIcon, export);
		
		// layout settings for mainPanel
		mainPanel.setTabPlacement(JTabbedPane.LEFT);
		
		getContentPane().add(mainPanel);
		
		// make all visible
		this.setVisible(true);
		this.setMinimumSize(new Dimension(1024, 600));
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
