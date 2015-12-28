package com.orangepixel.controller;



public class Gamepad {
	
	public ControllerMapping	mapping;
	
	// to keep track of 1st or 2nd controller, track the hashcode 
	public int hashcode;
	public String name;
	
	public boolean isTouchscreen;
	public boolean isKeyboard;
	public boolean isGamepad;
	public boolean isMouse;
	
	public boolean backPressed;
	public boolean backLocked;
	
	public boolean scrollWheelUp;
	public boolean scrollWheelDown;
	
	  public boolean BUTTON_A;
	  public boolean BUTTON_B;
	  public boolean BUTTON_X;
	  public boolean BUTTON_Y;
	  public boolean BUTTON_LB;
	  public boolean BUTTON_RB;
	  
	  public boolean BUTTON_SPECIAL1;	// defined by the game code
	  public boolean BUTTON_SPECIAL1locked;
	  
	  public boolean BUTTON_SPECIAL2;	// defined by the game code
	  public boolean BUTTON_SPECIAL2locked;
	  
	  
	  public boolean BUTTON_DPADLeft;
	  public boolean BUTTON_DPADUp;
	  public boolean BUTTON_DPADRight;
	  public boolean BUTTON_DPADDown;

	  
	  public boolean DPAD_UP;
	  public boolean DPAD_RIGHT;
	  public boolean DPAD_LEFT;
	  public boolean DPAD_DOWN;
	  
	  public boolean BUTTON_DPADLeftLocked;
	  public boolean BUTTON_DPADUpLocked;
	  public boolean BUTTON_DPADRightLocked;
	  public boolean BUTTON_DPADDownLocked;
	  
	  public boolean BUTTON_Alocked;
	  public boolean BUTTON_Blocked;
	  public boolean BUTTON_Xlocked;
	  public boolean BUTTON_Ylocked;
	  public boolean BUTTON_LBlocked;
	  public boolean BUTTON_RBlocked;

	  
	  public boolean leftPressed;
	  public boolean rightPressed;
	  public boolean upPressed;
	  public boolean downPressed;
	
	  public boolean leftLocked;
	  public boolean rightLocked;
	  public boolean upLocked;
	  public boolean downLocked;
	  
	  public int AXIS_LY;
	  public int AXIS_LX;
	  public int AXIS_RY;
	  public int AXIS_RX;
	  
	  
	  public final void isKeyboard() {
		  isTouchscreen=false;
		  isGamepad=false;
		  isKeyboard=true;
		  isMouse=false;
	  }
	
	  public final void isGamepad() {
		  isTouchscreen=false;
		  isGamepad=true;
		  isKeyboard=false;
		  isMouse=false;
	  }
	
	  public final void isTouchscreen() {
		  isTouchscreen=true;
		  isGamepad=false;
		  isKeyboard=false;
		  isMouse=false;
	  }
	  
	  public final void isMouse() {
		  isMouse=true;
		  isTouchscreen=false;
		  isGamepad=false;
		  isKeyboard=false;
	  }
	  
	  public Gamepad() {
		  isKeyboard=false;
		  isGamepad=false;
		  
		  backPressed=false;
		  backLocked=false;
	
		  BUTTON_A=false;
		  BUTTON_B=false;
		  BUTTON_X=false;
		  BUTTON_Y=false;
	
		  leftPressed=false;
		  rightPressed=false;
		  upPressed=false;
		  downPressed=false;
	
		  leftLocked=false;
		  rightLocked=false;
		  upLocked=false;
		  downLocked=false;
		  
		  BUTTON_Alocked=false;
		  BUTTON_Blocked=false;
		  BUTTON_Xlocked=false;
		  BUTTON_Ylocked=false;
		  
		  AXIS_LY=0;
		  AXIS_LX=0;
		  AXIS_RY=0;
		  AXIS_RX=0;	  
	  }
}