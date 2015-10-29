package io;

import Spectrum.Profile;
import gui.ProfileInfoDialog;
import gui.WaitMessage;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author wens
 */
public class ProfileOpeningThread extends Thread {
	
	private String path;
	private Profile profile;
	private WaitMessage wait;
	
	public ProfileOpeningThread(String path, WaitMessage wait){
		this.path = path;
		this.wait = wait;
	}
	
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
