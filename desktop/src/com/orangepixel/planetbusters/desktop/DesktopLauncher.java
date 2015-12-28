package com.orangepixel.planetbusters.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.orangepixel.planetbusters.myCanvas;

public class DesktopLauncher {
	
	static myCanvas startCanvas;
	
	
	public static void main (String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		
		cfg.title = "Planet Busters - LibGDX Jam";
		cfg.resizable=true;
		cfg.vSyncEnabled=true;

		cfg.width = 1080;
		cfg.height = 720;
		cfg.fullscreen=false;		
		
		
		startCanvas=new myCanvas();
		
		new LwjglApplication(startCanvas, cfg);
		
		// startup options
		for (String s: args) {
			if (s.toLowerCase().contains("nocontroller")) {
				startCanvas.argument_noController=true;
				cfg.title+=" - nocontroller";
			}
			
			if (s.toLowerCase().contains("forcewindow")) {
				startCanvas.argument_forceWindowed=true;
				cfg.title+=" - windowed";
			}			
        }
		
	}
}
