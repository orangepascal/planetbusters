package com.orangepixel.controller;

// GameStop Red Samurai Gamepad controller support

public class GSGamePad extends ControllerMapping {
	
	public GSGamePad() {
		id="gsgamepad";
		BUTTON_A = 0;		// 96
		BUTTON_B = 1;		// 97
		BUTTON_X = 2;		// 99
		BUTTON_Y = 3;		// 100
		BUTTON_LB = 4;
		BUTTON_RB = 5;
		BUTTON_BACK = 6;
		BUTTON_START = 7;
		BUTTON_LS = 8; //Left Stick pressed down
		BUTTON_RS = 9; //Right Stick pressed down
		  
		POV = 0;
		  
		AXIS_LY = 1; //-1 is up | +1 is down
		AXIS_LX = 0; //-1 is left | +1 is right
		AXIS_RY = 3; //-1 is up | +1 is down
		AXIS_RX = 2; //-1 is left | +1 is right
		AXIS_TRIGGER = 4; //LT and RT are on the same Axis! LT > 0 | RT < 0
	}
}	
