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
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/** This class initializes a small window on the lower right site of the screen
 * and performs a live classification of spectras (during the MS run).
 *
 * @author Stephan Neese
 */
public class liveWindow extends Thread {
	
	// threading
	private Thread t;
	private String threadName;
	// classification info
	String dir;
	String profilePath;
	String output;
	String distanceMeasure;
	// GUI
	JFrame frame;
	JPanel main;
	public JLabel substance;
	public JLabel probability;
	public JLabel distance;
	public JLabel filename;
	JButton close;
	// log
	PrintWriter writer;

	/** constructs a liveWindow from given parameters
	 * 
	 * @param threadName name of the thread to start
	 * @param dir folder where the csv files will be written
	 * @param profilePath path to the profile file
	 * @param output path and name of the output log file
	 * @param distanceMeasure string for type of distance measurement
	 * @throws java.lang.ClassNotFoundException 
	 * @throws java.lang.InstantiationException 
	 * @throws java.lang.IllegalAccessException 
	 * @throws javax.swing.UnsupportedLookAndFeelException 
	 * @throws java.io.FileNotFoundException 
	 * @throws java.io.UnsupportedEncodingException 
	 */
	public liveWindow(
			String threadName, 
			String dir, 
			String profilePath, 
			String output, 
			String distanceMeasure) 
			throws ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException, 
			UnsupportedLookAndFeelException,
			FileNotFoundException,
			UnsupportedEncodingException {
		this.threadName = threadName;
		this.dir = dir;
		this.profilePath = profilePath;
		this.output = output;
		this.distanceMeasure = distanceMeasure;
		frame = new JFrame();
		writer = new PrintWriter(output, "UTF-8");
		initGui();
	}
	
	/** initializes the GUI elements of the liveWindow
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
			UnsupportedLookAndFeelException{
		frame.setLayout(null);
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		frame.setTitle("live");
		frame.setSize(160, 100);
		frame.setUndecorated(true);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		
		main = new JPanel();
		main.setVisible(true);
		main.setLayout(null); 
		main.setBounds(0, 0, 160, 100);
		// positon on screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int width = dim.width;
		int height = dim.height;
		frame.setLocation(width-160, height-130);
		
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
		
		frame.add(main);
		
		close.addActionListener(
				new ActionListener(){
					
					/** cancel and close window
					 * 
					 * @param e ActionEvent that occurs when you press the button
					 */
					public void actionPerformed(ActionEvent e){
						writer.close();
						System.exit(0);
					}
				}
		);
	}
	
	/** Overwritten method of class Thread.
	 * This method runs the windows classification and output process.
	 * 
	 */
	public void run(){
        try{
			Path path = new File(dir).toPath();
			FileSystem fs = path.getFileSystem();
			Profile profile = Reader.readProfile(profilePath);
			
			// write logfile header
			DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			Date date = new Date();
			writer.println("created: " + df.format(date));
			writer.println("csv files from: " + dir);
			writer.println("profile used: " + profilePath);
			writer.println("distance measure: " + distanceMeasure);
			writer.println("worst possible score: " + (1.0 - (1.0/(double)(profile.getClasses().length))));
			writer.println("Filename\tassigned class\tdistance\tscore");
			
			WatchService service = fs.newWatchService();
			// we only watch directory for new files
            path.register(service, ENTRY_CREATE);
            WatchKey key = null;
			// infinite loop
            while(true){
                key = service.take();
                WatchEvent.Kind<?> kind = null;
                for(WatchEvent<?> watchEvent : key.pollEvents()){
                    kind = watchEvent.kind();
					// check for kin of event
                    if(OVERFLOW == kind){
						// events occuring faster than can be polled
                        continue;
                    }else if(ENTRY_CREATE == kind){
                        // new file created
                        Path file = ((WatchEvent<Path>)watchEvent).context();
                        // check for csv ending
						if(checkCSV(file.toString())){
							Spectrum spectrum = new Spectrum(
									path.toString() + File.separator + file.toString(), 
									(int)profile.getBinSize());
							if(distanceMeasure.equals("euclidean distance")){
								ClassificationResult res = profile.euclideanDistance(spectrum);
								filename.setText(file.toString());
								substance.setText(res.getAssignedClass());
								probability.setText("P=" + res.getScore());
								distance.setText("d=" + res.getDistance());
								// write log
								writer.println(
										file.toString() 
												+ "\t" 
												+ res.getAssignedClass() 
												+ "\t" 
												+ res.getDistance() 
												+ "\t" 
												+ res.getScore());
							}else{
								ClassificationResult res = profile.mahalanobisDistance(spectrum);
								filename.setText(file.toString());
								substance.setText(res.getAssignedClass());
								probability.setText("P=" + res.getScore());
								distance.setText("d=" + res.getDistance());
								// write log
								writer.println(
										file.toString() 
												+ "\t" 
												+ res.getAssignedClass() 
												+ "\t" 
												+ res.getDistance() 
												+ "\t" 
												+ res.getScore());
							}
						}
                    }
                }
				// reset key to re-enter the loop
                if(!key.reset()){
                    break;
                }
            }
        } catch(IOException | InterruptedException ioe){
            ioe.printStackTrace();
        } catch (ParseException ex) {
			Logger.getLogger(liveWindow.class.getName()).log(Level.SEVERE, null, ex);
		}
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
	
	/** This method checks if a file is a CSV file
	 * by checking the fileending.
	 * 
	 * @param filepath the filepath
	 * @return true if file is a csv file, false otherwise
	 */
	private static boolean checkCSV(String filepath){
		if(filepath.endsWith(".csv") || filepath.endsWith(".CSV")){
			return true;
		}else{
			return false;
		}
	}
}
