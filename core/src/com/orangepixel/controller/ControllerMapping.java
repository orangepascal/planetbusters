package com.orangepixel.controller;

import com.badlogic.gdx.controllers.PovDirection;

public class ControllerMapping {
	public String id="generic";
	  public int BUTTON_A = 0;
	  public int BUTTON_B = 1;
	  public int BUTTON_X = 2;
	  public int BUTTON_Y = 3;
	  public int BUTTON_LB = 4;
	  public int BUTTON_RB = 5;
	  public int BUTTON_BACK = 6;
	  public int BUTTON_START = 7;
	  public int BUTTON_LS = 8; //Left Stick pressed down
	  public int BUTTON_RS = 9; //Right Stick pressed down
	  
	  public PovDirection DPAD_UP = PovDirection.north;
	  public PovDirection DPAD_DOWN = PovDirection.south;
	  public PovDirection DPAD_LEFT = PovDirection.west;
	  public PovDirection DPAD_RIGHT = PovDirection.east;
	  
	  public int BUTTON_DPAD_UP=0;
	  public int BUTTON_DPAD_DOWN=1;
	  public int BUTTON_DPAD_LEFT=2;
	  public int BUTTON_DPAD_RIGHT=3;
	  
	  public int POV = 0;
	  
	  public int AXIS_LX = 1; //-1 is left | +1 is right
	  public int AXIS_LY = 0; //-1 is up | +1 is down
	  public int AXIS_RX = 3; //-1 is left | +1 is right
	  public int AXIS_RY = 2; //-1 is up | +1 is down
	  public int AXIS_TRIGGER = 4; //LT and RT are on the same Axis! LT > 0 | RT < 0
	  
	public boolean reverseXAxis=false;
	public boolean reverseYAxis=false;
	  
}