package com.orangepixel.controller;


// Dualshock 4 (ps4) on windows

// Wireless Controller
// OS:  osx
public class PS4OSX extends ControllerMapping {
	public PS4OSX() {
		id="ps4";
		BUTTON_A=1;
		BUTTON_B=2;
		BUTTON_X=0;
		BUTTON_Y=3;
		BUTTON_Y=3;

		// not confirmed, copied from ps3
		BUTTON_LB = 4;
		BUTTON_RB = 5;
		BUTTON_BACK = 6;
		BUTTON_START = 7;
		BUTTON_LS = 8; //Left Stick pressed down
		BUTTON_RS = 9; //Right Stick pressed down
		
		POV = 0;

		AXIS_LX=0;
		AXIS_LY=1;
		AXIS_RX=2;
		AXIS_RY=3;		

		AXIS_TRIGGER = 4; //LT and RT are on the same Axis! LT > 0 | RT < 0
	}
}	