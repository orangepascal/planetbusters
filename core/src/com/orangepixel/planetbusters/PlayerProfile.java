package com.orangepixel.planetbusters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;


/**
 * Keeps track of a player profile and all the data that corresponds
 * to such a profile
 * 
 * @author Pascal
 *
 */


public class PlayerProfile {
	
	String				fileName;

	// audio?
	boolean 			useMusic;
	boolean				useSFX;
	boolean				useFullscreen;
	int					storedWindowedModeID;
	int					musicVolume;
	int					soundVolume;
	

	// input settings (touch,gamepad,keyboard)
	int[]				stickX;
	int[]				stickY;
	int[]				keyboardSettings;
	int[]				controller1;
	int[]				controller2;

	// simple check for cheating
	boolean				CRCcheckPassed;
	

	
	

	/**
	 * Main constructor, initialise a blank profile
	 */
	public PlayerProfile(String myFile) {
		fileName=myFile;
		
		stickX=new int[6];
		stickY=new int[6];
		
		keyboardSettings=new int[16];
		

		// signal a reset is needed
		stickX[0]=-999;
		stickY[0]=-999;

		controller1=new int[12];
		controller2=new int[12];
		
		musicVolume=4;
		soundVolume=7;
	}
	
	

	/**
	 * 
	 * Load the settings for this profile
	 *
	 * @param mFileName Filename of the preferences file to use
	 * @return Map object (basically all data that was saved to the specified profile file)
	 */
	public final void loadSettings() {
		int myCRC=0;
		
		Preferences prefs = Gdx.app.getPreferences(fileName);

		// get profile preferences and settings
		useMusic=prefs.getBoolean("usemusic", true);
		useSFX=prefs.getBoolean("usesfx",true);
		useFullscreen=prefs.getBoolean("useFullscreen",false);
		storedWindowedModeID=prefs.getInteger("storedWindowedModeID",2); //	1080x720 by default -1);
		
		musicVolume=prefs.getInteger("musicvolume",3);
		soundVolume=prefs.getInteger("soundvolume",7);
		
		

		for (int i=12; --i>=0;) {
			controller1[i]=prefs.getInteger("controller1"+i,-999);
			
			if (controller1[i]!=-999) controller2[i]=prefs.getInteger("controller2"+i,controller1[i]);
			else controller2[i]=prefs.getInteger("controller2"+i,-999);
		}
		for (int i=6; --i>=0;) {
			stickX[i]=prefs.getInteger("stickx"+i,-999);
			stickY[i]=prefs.getInteger("sticky"+i,-999);
		}
		
		for (int i=16; --i>=0;) {
			keyboardSettings[i]=prefs.getInteger("keyboardSettings"+i,-1);
		}
		
		
		int CRCcheck=prefs.getInteger("LastMinuteEvent", -1);
		
		CRCcheckPassed=false;
		if (CRCcheck>-1) {
			if (CRCcheck==myCRC) CRCcheckPassed=true;
			else CRCcheckPassed=false;
		} else {
			CRCcheckPassed=true;
		}
		
		if (CRCcheckPassed) Gdx.app.log("opdebug","CRC valid");
		else Gdx.app.log("opdebug","CRC invalid");
	}

	
	
	/**
	 * Save the settings for this profile
	 */
	public final void saveSettings() {
		int myCRC=0;
		
		Preferences prefsEditor = Gdx.app.getPreferences(fileName);
		
		// save settings and preferences
		prefsEditor.putBoolean("usemusic", useMusic);
		prefsEditor.putBoolean("usesfx", useSFX);
		prefsEditor.putBoolean("useFullscreen",useFullscreen);
		prefsEditor.putInteger("storedWindowedModeID",storedWindowedModeID);
		
		prefsEditor.putInteger("musicvolume",musicVolume);
		prefsEditor.putInteger("soundvolume",soundVolume);


		for (int i=6; --i>=0;) {
			prefsEditor.putInteger("stickx"+i,stickX[i]);
			prefsEditor.putInteger("sticky"+i,stickY[i]);
		}		
		
		for (int i=16; --i>=0;) {
			prefsEditor.putInteger("keyboardSettings"+i,keyboardSettings[i]);
		}
		
		
		for (int i=12; --i>=0;) {
			prefsEditor.putInteger("controller1"+i,controller1[i]);
			prefsEditor.putInteger("controller2"+i,controller2[i]);
		}
		
		prefsEditor.putInteger("LastMinuteEvent", myCRC);
		
		prefsEditor.flush();
		
		prefsEditor=null;
	}

	
	
	
	
	public final void resetControls(int displayW, int displayH) {
		int tx=48;
		int ty=displayH-48;
		tx-=24;
		ty-=24;
		
		
		// left
		stickX[0]=tx-12;
		stickY[0]=ty+12;

		// right
		stickX[1]=tx+36;
		stickY[1]=ty+12;

		// up
		stickX[2]=tx+12;
		stickY[2]=ty-12;

		// down
		stickX[3]=tx+12;
		stickY[3]=ty+36;
	}	

	
	
	
	
	
}
