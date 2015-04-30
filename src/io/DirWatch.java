package io;

import Spectrum.ClassificationResult;
import Spectrum.Profile;
import Spectrum.Spectrum;
import gui.liveWindow;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UnsupportedLookAndFeelException;

/** This class 
 *
 * @author wens
 */
public class DirWatch extends Thread {
	
	private Thread t;
	private String threadName;
	String dir;
	String profilePath;
	String output;
	String distanceMeasure;

	/**
	 * 
	 * @param threadName
	 * @param dir
	 * @param profilePath
	 * @param output
	 * @param distanceMeasure 
	 */
	public DirWatch(
			String threadName, 
			String dir, 
			String profilePath, 
			String output, 
			String distanceMeasure) {
		this.threadName = threadName;
		this.dir = dir;
		this.profilePath = profilePath;
		this.output = output;
		this.distanceMeasure = distanceMeasure;
	}
	
	/**
	 * 
	 */
	public void run(){
        try{
			Path path = new File(dir).toPath();
			FileSystem fs = path.getFileSystem();
			Profile profile = Reader.readProfile(profilePath);
			liveWindow window = new liveWindow();
			window.runProgram();
			
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
							// sleep so file can be loaded completely by the filesystem
							Thread.sleep(100);
							
							Spectrum spectrum = new Spectrum(
									path.toString() + File.separator + file.toString(), 
									(int)profile.getBinSize());
							if(distanceMeasure.equals("euclidean distance")){
								ClassificationResult res = profile.euclideanDistance(spectrum);
								window.filename.setText(file.toString());
								window.substance.setText(res.getAssignedClass());
								window.probability.setText("P=" + res.getScore());
								window.distance.setText("d=" + res.getDistance());
							}else{
								ClassificationResult res = profile.mahalanobisDistance(spectrum);
								window.filename.setText(file.toString());
								window.substance.setText(res.getAssignedClass());
								window.probability.setText("P=" + res.getScore());
								window.distance.setText("d=" + res.getDistance());
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
        } catch (ClassNotFoundException ex) {
			Logger.getLogger(DirWatch.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			Logger.getLogger(DirWatch.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(DirWatch.class.getName()).log(Level.SEVERE, null, ex);
		} catch (UnsupportedLookAndFeelException ex) {
			Logger.getLogger(DirWatch.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ParseException ex) {
			Logger.getLogger(DirWatch.class.getName()).log(Level.SEVERE, null, ex);
		}
    }
	
	/**
	 * 
	 */
	public void start(){
		if(t==null){
			t = new Thread(this, threadName);
			t.start();
		}
	}
	
	/**
	 * 
	 * @param filepath
	 * @return 
	 */
	private static boolean checkCSV(String filepath){
		if(filepath.endsWith(".csv") || filepath.endsWith(".CSV")){
			return true;
		}else{
			return false;
		}
	}
}
