package com.orangepixel.controller;


// XBox 360 controller mapping for Windows
// XBox One uses same mappings 

public class XBox extends ControllerMapping {
	public XBox() {
		id="xbox";
		BUTTON_A = 0;
		BUTTON_B = 1;
		BUTTON_X = 2;
		BUTTON_Y = 3;
		BUTTON_LB = 4;
		BUTTON_RB = 5;
		BUTTON_BACK = 6;
		BUTTON_START = 7;
		BUTTON_LS = 8; //Left Stick pressed down
		BUTTON_RS = 9; //Right Stick pressed down
			  
		BUTTON_DPAD_UP=10;
		BUTTON_DPAD_DOWN=11;
		BUTTON_DPAD_LEFT=12;
		BUTTON_DPAD_RIGHT=13;
		
		POV = 0;
			  
		AXIS_LY = 0; //-1 is up | +1 is down
		AXIS_LX = 1; //-1 is left | +1 is right
		AXIS_RY = 2; //-1 is up | +1 is down
		AXIS_RX = 3; //-1 is left | +1 is right
		AXIS_TRIGGER = 4; //LT and RT are on the same Axis! LT > 0 | RT < 0
	}
}	