package com.orangepixel.controller;


// XBox 360 controller mapping for OSX

public class XBoxOSX extends ControllerMapping {
	public XBoxOSX() {
		id="xbox_osx";
		BUTTON_A = 11;
		BUTTON_B = 12;
		BUTTON_X = 13;
		BUTTON_Y = 14;
		BUTTON_LB = 8;
		BUTTON_RB = 9;
		BUTTON_BACK = 5;
		BUTTON_START = 4;
		BUTTON_LS = 6; //Left Stick pressed down
		BUTTON_RS = 7; //Right Stick pressed down
		
		BUTTON_DPAD_UP=0;
		BUTTON_DPAD_DOWN=1;
		BUTTON_DPAD_LEFT=2;
		BUTTON_DPAD_RIGHT=3;
			  
		POV = 0;
			  
		AXIS_LY = 3; //-1 is up | +1 is down
		AXIS_LX = 2; //-1 is left | +1 is right
		AXIS_RX = 4; //-1 is left | +1 is right
		AXIS_RY = 5; //-1 is up | +1 is down
		AXIS_TRIGGER = 4;
	}
}	