package com.orangepixel.controller;


// PS3 on OSX (on windows, it won't register button presses, requires tools/vague drivers

public class PS3 extends ControllerMapping {
	public PS3() {
		id="ps3";
		BUTTON_A = 14;
		BUTTON_B = 13;
		BUTTON_X = 15;
		BUTTON_Y = 12;
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