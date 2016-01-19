package io;

import Spectrum.Profile;
import gui.ProfileInfoDialog;
import gui.WaitMessage;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UnsupportedLookAndFeelException;

/** This class starts a new Thread 
 * that opens a profile in the background
 * while displaying a waiting message
 *
 * @author Stephan Neese
 */
public class ProfileOpeningThread extends Thread {
	
	private String path;
	private Profile profile;
	private WaitMessage wait;
	
	/** construct a ProfileOpeningThread
	 * 
	 * @param path path to the profile
	 * @param wait the WaitMessage to be displayed
	 */
	public ProfileOpeningThread(String path, WaitMessage wait){
		this.path = path;
		this.wait = wait;
	}
	
	/** overwritten method from class Thread.
	 * 
	 */
	@Override
	public void run(){
		try {
			profile = Reader.readProfile(path);
			ProfileInfoDialog pid = new ProfileInfoDialog(profile);
			wait.close();
		} catch (IOException ex) {
			Logger.getLogger(ProfileOpeningThread.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ParseException ex) {
			Logger.getLogger(ProfileOpeningThread.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ProfileOpeningThread.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			Logger.getLogger(ProfileOpeningThread.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(ProfileOpeningThread.class.getName()).log(Level.SEVERE, null, ex);
		} catch (UnsupportedLookAndFeelException ex) {
			Logger.getLogger(ProfileOpeningThread.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
