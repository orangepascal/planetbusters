package com.orangepixel.planetbusters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Audio {

	public static boolean useMusic;
	public static boolean useSFX;
	public static int SoundVolume;
	public static int MusicVolume;
	
	public static Music myGameMusic;
	
	// sound effects
	public static Sound 	FX_SPLASH;
	
	
	
	public final static void initSounds() {
		String root="audio/";

		FX_SPLASH=Gdx.audio.newSound(Gdx.files.internal(root + "fxsplash.mp3"));
	}
	
	
	
	// play specified sound
	public final static long playSound(Sound sound) {
		long soundID=-1;
		if (useSFX) {
			soundID=sound.play(SoundVolume / 10.0f);
		}
		return soundID;
	}


	// play specified sound , randomly changes pitch slightly, to vary often used sound effects
	public final static long playSoundPitched(Sound sound) {
		long soundID=-1;
		
		if (useSFX) {
			soundID=sound.play(SoundVolume / 10.0f);
			sound.setPitch(soundID, (88+Globals.getRandom(16))/100.0f);
		}

		return soundID;
	}
	
	
	// stops sound (sound effect auto stop
	public final static void stopAllSounds() {
		if (myGameMusic != null) myGameMusic.stop();
	}

	public final static void playBackgroundMusic() {
		if (useMusic && myGameMusic != null) {
			myGameMusic.play();
			myGameMusic.setVolume(MusicVolume / 10.0f);
		}
	}

	
	public final static void stopBackgroundMusic() {
		if (useMusic) {
			if (myGameMusic != null && myGameMusic.isPlaying())
				myGameMusic.pause();
		}
	}	
	
}
