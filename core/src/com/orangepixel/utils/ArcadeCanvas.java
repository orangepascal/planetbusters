package com.orangepixel.utils;

import java.util.Calendar;
import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.orangepixel.controller.ControllerMapping;
import com.orangepixel.controller.GSGamePadAndroid;
import com.orangepixel.controller.Gamepad;
import com.orangepixel.controller.PS3;
import com.orangepixel.controller.PS4;
import com.orangepixel.controller.PS4OSX;
import com.orangepixel.controller.ProExPowerA;
import com.orangepixel.controller.XBox;
import com.orangepixel.controller.XBoxLinux;
import com.orangepixel.controller.XBoxLinuxAlt;
import com.orangepixel.controller.XBoxOSX;
import com.orangepixel.social.Social;

public class ArcadeCanvas implements ApplicationListener {

	public static final boolean IS_MAC = System.getProperty("os.name").toLowerCase().contains("mac");
	public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("windows");
	public static final boolean IS_LINUX = System.getProperty("os.name").toLowerCase().contains("linux");
	
	public boolean argument_noController=false;
	public boolean argument_forceWindowed=false;
	
	public boolean isDesktop=false;
	public boolean isAndroid=false;
	public boolean isIOS = false;

	public boolean isFullScreen=false;
	
	
	public final static int	ININIT = 1, 			
							INSPLASH = 2,											
							INMENU = 3,
							INITMAP = 19,
							INGAME = 20;
		

	// Collection of sprites (woohoo!)
	public static Texture[] sprites; // array of sprites
	// buffers for rendering the light
	FrameBuffer lightBuffer;
	TextureRegion lightBufferRegion;
	
	
	// batch rendering, shaperendering (rectangles, fillrectangles)
	public OrthographicCamera camera;
	public SpriteBatch batch;
	public ShapeRenderer shapeRenderer;
	
	public static Rect dest=new Rect();
	public static Rect src=new Rect();

	
	
	// DEFAULT KEYBOARD MAPPING
	public final static int	
						keyboardConfig_left=0,
						keyboardConfig_right = 1,
						keyboardConfig_up = 2,
						keyboardConfig_down = 3,
						keyboardConfig_select = 4,
						keyboardConfig_cancel = 5,
						keyboardConfig_inventory = 6,
						keyboardConfig_map = 7,
						keyboardConfig_weapon1 = 8,
						keyboardConfig_weapon2 = 9,
						keyboardConfig_weapon3 = 10,
						keyboardConfig_weapon4 = 11; 
						
	public String[] keyboardConfigNames = new String[] {
			"left",
			"right",
			"up",
			"down",
			"select",
			"cancel",
			"char swap",
			"map",
			"weapon 1",
			"weapon 2",
			"weapon 3",
			"melee"
	};
	
	public int[]	keyboardConfig = new int[] {
			Keys.LEFT, 	
			Keys.RIGHT,
			Keys.UP,
			Keys.DOWN,
			Keys.X,
			Keys.ESCAPE,
			Keys.Z,
			Keys.M,
			Keys.NUM_1,
			Keys.NUM_2,
			Keys.NUM_3,
			Keys.NUM_4
	};
	
	public int[]	keyboardConfigDefault = new int[] {
			Keys.LEFT, 	
			Keys.RIGHT,
			Keys.UP,
			Keys.DOWN,
			Keys.X,
			Keys.ESCAPE,
			Keys.Z,
			Keys.M,
			Keys.NUM_1,
			Keys.NUM_2,
			Keys.NUM_3,
			Keys.NUM_4
	};	
	
	public int lastKeyCode;
	public boolean lastKeyLocked;
	
	
	
	
	// Social - platform specific (if null, nothing is done) (steam, google play, gamecenter, etc)
	public Social mySocial=null;

	
	// global variables
	public int GameState;
	public int worldTicks;
	public boolean secondPassed;
	public boolean paused;
	
	// pixel resolution 
	public static int lowDisplayW;
	public static int lowDisplayH;
	// use a global so we can do setAlpha() and all render calls will use it
	public static int globalAlpha;
	public static int globalRed;
	public static int globalGreen;
	public static int globalBlue;	
	


	// input variables for touch and mouse
	public boolean touchReleased=true;
	public float touchX=-1;
	public float touchY=-1;
	public int[] mTouchX = new int[6];
	public int[] mTouchY = new int[6];
	public float cursorX=-1;
	public float cursorY=-1;
	
	public boolean	switchWeapon=false;
	public boolean	switchWeaponLocked=false;
	public boolean	switchWeaponOne=false;
	public boolean	switchWeaponOneLocked=false;
	public boolean	switchWeaponTwo=false;
	public boolean	switchWeaponTwoLocked=false;
	public boolean	switchWeaponThree=false;
	public boolean	switchWeaponThreeLocked=false;
	public boolean	switchWeaponFour=false;
	public boolean	switchWeaponFourLocked=false;

		
	// controller variables
	public boolean						controllersAllowUnknown=true;
	public int							controllersFound=0;
	// setup optional controllers (keyboard is also a controller)
	public Gamepad						controller1=new Gamepad();
	public Gamepad						controller2=new Gamepad();
	
	public boolean						switchFullScreen;
	public boolean						switchFullScreenLocked;
	
	public boolean						triggerFullScreenSwitch=false;

	// Check if keyboard is being used (so you can hide the onscreen controls if you like)
	public boolean						keyBoardOut=true;

	
	// system variables (framerate, mouse handling ,etc)
	public int myFramerate;
	private int fpsCount;
	private long loopEnd;
	private long loopPause;
	private static int displayW;
	private static int displayH;
		
	
	// rendering buffers
	FrameBuffer m_fbo;
	TextureRegion m_fboRegion=null;
	
	FrameBuffer m_fboTop;
	TextureRegion m_fboTopRegion=null;
	
	
	
	
	
	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		isDesktop=false;
		isAndroid=false;
		isIOS=false;

		switch (Gdx.app.getType()) {
			case Android:
				isAndroid=true;
				controller1.isTouchscreen();
			break;
			
			case iOS:
				isIOS=true;
				controller1.isTouchscreen();
			break;
				
			
			default:
				isDesktop=true;
			break;
		}

		displayW=(int)w;
		displayH=(int)h;


		// find height closest to 160, and matching width
		lowDisplayH=(int)(displayH/Math.floor(displayH/160));
		lowDisplayW=(int)(displayW/(displayH/ (displayH/Math.floor(displayH/160)) ));
		
		camera = new OrthographicCamera();
		
		batch = new SpriteBatch();
		batch.setBlendFunction(GL20.GL_ONE, -1); //(-1, -1);
		
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		
		// we use less, but make sure we have enough slots
		sprites=new Texture[32];

		
		
		engineInit();
		
		loopPause = System.currentTimeMillis();

		
		
		
		Gdx.app.getInput().setInputProcessor(new InputProcessor() {
			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				if (pointer<5) {
					mTouchX[pointer]=-1;
					mTouchY[pointer]=-1;
					touchReleased=true;
				}
				
				if (isDesktop) {
					controller1.isMouse();
					if (button==Input.Buttons.LEFT) {
						controller1.BUTTON_X=false;
						controller1.BUTTON_Xlocked=false;
					}
					
					if (button==Input.Buttons.RIGHT) {
						controller1.BUTTON_Y=false;
						controller1.BUTTON_Ylocked=false;
					}
					
					if (button==Input.Buttons.MIDDLE) {
						controller1.BUTTON_A=false;
						controller1.BUTTON_Alocked=false;
					}

				}
				return false;
			}

			@Override
			public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
				return touchUp(screenX, screenY, pointer, button);
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				controller1.isTouchscreen();
				
				if (pointer<5) {
					touchX = (lowDisplayW / 100f) * ((100f / displayW) * screenX);
					touchY = (lowDisplayH / 100f) * ((100f / displayH) * screenY);
					
					mTouchX[pointer]=(int)(touchX);
					mTouchY[pointer]=(int)(touchY);
				}
				
//				if (isDesktop) {
//					controller1.isMouse();
//					processMouse(screenX,screenY);
//				}
				
				return false;
			}
			
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				controller1.isTouchscreen();
				
				if (pointer<5) {
					touchX = (lowDisplayW / 100f) * ((100f / displayW) * screenX);
					touchY = (lowDisplayH / 100f) * ((100f / displayH) * screenY);
					
					mTouchX[pointer]=(int)(touchX);
					mTouchY[pointer]=(int)(touchY);
				}
			
				if (isDesktop) {
//					controller1.isMouse();
					if (button==Input.Buttons.LEFT) {
						controller1.BUTTON_X=true;
					}

					// swapped these two with other games
					if (button==Input.Buttons.RIGHT) {
						controller1.BUTTON_Y=true;
					}
					
					if (button==Input.Buttons.MIDDLE) {
						controller1.BUTTON_A=true;
					}
					
//					processMouse(screenX,screenY);
				}
				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				controller1.isMouse();
				Gdx.input.setCursorCatched(true);
				
				processMouse(screenX,screenY);
				return false;
			}

			@Override
			public boolean scrolled(float amountX, float amountY) {
				controller1.isMouse();
				controller1.scrollWheelUp=false;
				controller1.scrollWheelDown=false;
				if (amountY<0) controller1.scrollWheelUp=true;
				else if (amountY>0) controller1.scrollWheelDown=true;
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
				lastKeyCode=-1;
				lastKeyLocked=false;

				if (keycode==keyboardConfig[keyboardConfig_left]) {
					controller1.isKeyboard();
					controller1.leftPressed=false;
					controller1.leftLocked=false;
					return true;
				}

				if (keycode==keyboardConfig[keyboardConfig_right]) {
					controller1.isKeyboard();
					controller1.rightPressed=false;
					controller1.rightLocked=false;
					return true;
				}

				if (keycode==keyboardConfig[keyboardConfig_up]) {
					controller1.isKeyboard();
					controller1.upPressed=false;
					controller1.upLocked=false;
					return true;
				}
				
				if (keycode==keyboardConfig[keyboardConfig_down]) {
					controller1.isKeyboard();
					controller1.downPressed=false;
					controller1.downLocked=false;
					return true;
				}
				
				if (keycode==keyboardConfig[keyboardConfig_select]) {
					controller1.BUTTON_X=false;
					controller1.BUTTON_Xlocked=false;
					return true;
				}
				
				if (keycode==keyboardConfig[keyboardConfig_cancel]) {
					controller1.backPressed=false;
					controller1.backLocked=false;
					return true;
				}

				if (keycode==keyboardConfig[keyboardConfig_map]) {
					controller1.BUTTON_SPECIAL1=false;
					controller1.BUTTON_SPECIAL1locked=false;
					return true;
				}

				if (keycode==keyboardConfig[keyboardConfig_inventory]) {
					controller1.BUTTON_Y=false;
					controller1.BUTTON_Ylocked=false;
					return true;
				}
				
				if (keycode==keyboardConfig[keyboardConfig_weapon1]) {
					switchWeaponOne=false;
					switchWeaponOneLocked=false;
					return true;
				}

				if (keycode==keyboardConfig[keyboardConfig_weapon2]) {
					switchWeaponTwo=false;
					switchWeaponTwoLocked=false;
					return true;
				}

				if (keycode==keyboardConfig[keyboardConfig_weapon3]) {
					switchWeaponThree=false;
					switchWeaponThreeLocked=false;
					return true;
				}
				
				if (keycode==keyboardConfig[keyboardConfig_weapon4]) {
					switchWeaponFour=false;
					switchWeaponFourLocked=false;
					return true;
				}
				
				switch (keycode) {
					case Keys.BACK:
						controller1.backPressed=false;
						controller1.backLocked=false;
						return true;
				}
				
				
				return false;
			}
			
			@Override
			public boolean keyTyped(char character) {
				return false;
			}
			
			@Override
			public boolean keyDown(int keycode) {
				lastKeyCode=keycode;
				
				if (keycode==keyboardConfig[keyboardConfig_left]) {
					controller1.isKeyboard();
					controller1.leftPressed=true;
					return true;
				}

				if (keycode==keyboardConfig[keyboardConfig_right]) {
					controller1.isKeyboard();
					controller1.rightPressed=true;
					return true;
				}

				if (keycode==keyboardConfig[keyboardConfig_up]) {
					controller1.isKeyboard();
					controller1.upPressed=true;
					return true;
				}
				
				if (keycode==keyboardConfig[keyboardConfig_down]) {
					controller1.isKeyboard();
					controller1.downPressed=true;
					return true;
				}
				
				if (keycode==keyboardConfig[keyboardConfig_select]) {
					controller1.BUTTON_X=true;
					return true;
				}
				
				if (keycode==keyboardConfig[keyboardConfig_cancel]) {
					controller1.backPressed=true;
					return true;
				}

				if (keycode==keyboardConfig[keyboardConfig_map]) {
					controller1.BUTTON_SPECIAL1=true;
					return true;
				}

				if (keycode==keyboardConfig[keyboardConfig_inventory]) {
					controller1.BUTTON_Y=true;
					return true;
				}
				
				if (keycode==keyboardConfig[keyboardConfig_weapon1]) {
					switchWeaponOne=true;
					return true;
				}

				if (keycode==keyboardConfig[keyboardConfig_weapon2]) {
					switchWeaponTwo=true;
					return true;
				}

				if (keycode==keyboardConfig[keyboardConfig_weapon3]) {
					switchWeaponThree=true;
					return true;
				}

				if (keycode==keyboardConfig[keyboardConfig_weapon4]) {
					switchWeaponFour=true;
					return true;
				}

				
				switch (keycode) {
					case Keys.BACK:
						controller1.backPressed=true;
						return true;
				}
				
				return false;
			}
		});
		
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);

		initControllers();
	}
	
	
	
	public void processMouse(int screenX, int screenY) {
		if (Gdx.input.isCursorCatched()) {
			if (screenX<1) screenX=1;
			if (screenY<1) screenY=1;
			
			if (screenX>displayW-2) screenX=displayW-2;
			if (screenY>displayH-2) screenY=displayH-2;
			
			Gdx.input.setCursorPosition(screenX, screenY);
		}
		
		if (screenX<1 || screenY<1 || screenX>displayW-2 || screenY>displayH-2) return;
		
		cursorX = (int) ((lowDisplayW / 100f) * ((100f / displayW) * screenX))-4;
		cursorY= (int) ((lowDisplayH / 100f) * ((100f / displayH) * screenY))-4;

		if (cursorX<1) cursorX=1;
		if (cursorY<1) cursorY=1;
	}	
	
	
	
	
	ControllerMapping pad=null;
	
	public void initControllers() {
		
		if (argument_noController) return;
		
		// joystick support!
		controllersFound=0;
		controller1.hashcode=-1;
//		firstdevice=-1;
	
		
//		for(Controller controller: Controllers.getControllers()) {
//			// detect controller if we can
//			if (!controller.getName().isEmpty()) {
//				if (isAndroid) {
//					pad=new GSGamePadAndroid();
//				} else if (controller.getName().toLowerCase().contains("xbox")
//						|| controller.getName().toLowerCase().contains("x-box")
//						|| controller.getName().toLowerCase().contains("gamepad f")) {	// "gamepad fxxx" are logitech gamepads
//					if (IS_MAC) pad=new XBoxOSX();
//					else if (IS_LINUX) {
//						if (controller.getName().toLowerCase().contains("wireless receiver")) {
//							pad=new XBoxLinuxAlt();
//						} else {
//							pad=new XBoxLinux();
//						}
//					} else pad=new XBox();
//				} else if (controller.getName().toLowerCase().contains("playstation")) {
//					// PS3 on windows 8 is detected, but won't register
//					pad=new PS3();
//				} else if (controller.getName().contains("Wireless Controller")) {
//					if (IS_MAC) pad=new PS4OSX();
//					else pad=new PS4();
//				} else if (controller.getName().contains("Pro Ex")) {
//					pad=new ProExPowerA();
//				} else if (!controller.getName().toLowerCase().contains("flight")) {
//					Gdx.app.log("opdebug", "Unknown controller: "+controller.getName() + " hash:"+controller.hashCode());
//					if (controllersAllowUnknown) {
//						// made sure it's not a flight stick or anything
//						// now use generic mapping, and hope it works (basically this is XBox mapping)
//						pad=new ControllerMapping();
//					}
//				}
//			}
//
//
//			if (pad!=null) {
////				Gdx.app.log("opdebug","controller mapped:"+pad.id);
//
//				if (controller1.hashcode<0) {
//					controller1.hashcode=controller.hashCode();
//					controller1.mapping=pad;
//					controller1.name=controller.getName();
//				} else {
//					controller2.hashcode=controller.hashCode();
//					controller2.mapping=pad;
//					controller2.name=controller.getName();
//				}
//				controllersFound++;
//				controller.addListener(new ControllerListener() {
//
//					public boolean ySliderMoved(Controller arg0, int arg1, boolean arg2) { return false; }
//					public boolean xSliderMoved(Controller arg0, int arg1, boolean arg2) { return false; }
//					public void disconnected(Controller arg0) {}
//					public void connected(Controller arg0) {}
//					public boolean accelerometerMoved(Controller arg0, int arg1, Vector3 arg2) { return false; }
//
//					public boolean buttonUp(Controller arg0, int arg1) {
////						last_button=-999;
////						pl2_last_button=-999;
//						if (arg0==null || controller1==null) return false;
//
//						if (arg0.hashCode()==controller1.hashcode && controller1.mapping!=null) {
////							Gdx.app.log("opdebug","buttonup:"+arg1);
//
//							if (arg1==controller1.mapping.BUTTON_X) { // button_a) {
//								controller1.BUTTON_X=false;
//								controller1.BUTTON_Xlocked=false;
//								return true;
//							}
//							if (arg1==controller1.mapping.BUTTON_A) { //button_b) {
//								controller1.BUTTON_A=false;
//								controller1.BUTTON_Alocked=false;
//								return true;
//							}
//							if (arg1==controller1.mapping.BUTTON_B) { // button_c) {
//								controller1.BUTTON_B=false;
//								controller1.BUTTON_Blocked=false;
//								controller1.backPressed=false;
//								controller1.backLocked=false;
//								return true;
//							}
//
//							if (arg1==controller1.mapping.BUTTON_Y) {
//								controller1.BUTTON_Y=false;
//								controller1.BUTTON_Ylocked=false;
//								return true;
//							}
//
//							if (arg1==controller1.mapping.BUTTON_LB) {
//								controller1.BUTTON_LB=false;
//								controller1.BUTTON_LBlocked=false;
//								return true;
//							}
//
//							if (arg1==controller1.mapping.BUTTON_RB) {
//								controller1.BUTTON_RB=false;
//								controller1.BUTTON_RBlocked=false;
//								return true;
//							}
//							if (arg1==controller1.mapping.BUTTON_START) { //button_b) {
//								controller1.BUTTON_SPECIAL2=false;
//								controller1.BUTTON_SPECIAL2locked=false;
//								return true;
//							}
//
//
//							if (arg1==controller1.mapping.BUTTON_DPAD_UP) {
//								controller1.upPressed=false;
//								controller1.upLocked=false;
//								controller1.BUTTON_DPADUp=false;
//								controller1.BUTTON_DPADUpLocked=false;
//								return true;
//							}
//
//							if (arg1==controller1.mapping.BUTTON_DPAD_DOWN) {
//								controller1.downPressed=false;
//								controller1.downLocked=false;
//								controller1.BUTTON_DPADDown=false;
//								controller1.BUTTON_DPADDownLocked=false;
//
//								return true;
//							}
//
//							if (arg1==controller1.mapping.BUTTON_DPAD_RIGHT) {
//								controller1.rightPressed=false;
//								controller1.rightLocked=false;
//								controller1.BUTTON_DPADRight=false;
//								controller1.BUTTON_DPADRightLocked=false;
//								return true;
//							}
//
//							if (arg1==controller1.mapping.BUTTON_DPAD_LEFT) {
//								controller1.leftPressed=true;
//								controller1.leftLocked=false;
//								controller1.BUTTON_DPADLeft=false;
//								controller1.BUTTON_DPADLeftLocked=false;
//								return true;
//							}
//
//						} else if (controller2.mapping!=null) {
//							if (arg1==controller2.mapping.BUTTON_X) {
//								controller2.BUTTON_X=false;
//								controller2.BUTTON_Xlocked=false;
//								return true;
//							}
//							if (arg1==controller2.mapping.BUTTON_A) {
//								controller2.BUTTON_A=false;
//								controller2.BUTTON_Alocked=false;
//								return true;
//							}
//							if (arg1==controller2.mapping.BUTTON_START) { //button_b) {
//								controller2.BUTTON_SPECIAL2=false;
//								controller2.BUTTON_SPECIAL2locked=false;
//								return true;
//							}
//
//							if (arg1==controller2.mapping.BUTTON_Y) {
//								controller2.BUTTON_Y=false;
//								controller2.BUTTON_Ylocked=false;
//								return true;
//							}
//							if (arg1==controller2.mapping.BUTTON_B) {
//								controller2.BUTTON_B=false;
//								controller1.BUTTON_Blocked=false;
//								controller2.backPressed=false;
//								controller2.backLocked=false;
//								return true;
//							}
//
//							if (arg1==controller2.mapping.BUTTON_LB) {
//								controller2.BUTTON_LB=false;
//								controller2.BUTTON_LBlocked=false;
//								return true;
//							}
//
//							if (arg1==controller2.mapping.BUTTON_RB) {
//								controller2.BUTTON_RB=false;
//								controller2.BUTTON_RBlocked=false;
//								return true;
//							}
//
//							if (arg1==controller2.mapping.BUTTON_DPAD_UP) {
//								controller2.upPressed=false;
//								controller2.upLocked=false;
//								controller2.BUTTON_DPADUp=false;
//								controller2.BUTTON_DPADUpLocked=false;
//								return true;
//							}
//
//							if (arg1==controller2.mapping.BUTTON_DPAD_DOWN) {
//								controller2.downPressed=false;
//								controller2.downLocked=false;
//								controller2.BUTTON_DPADDown=false;
//								controller2.BUTTON_DPADDownLocked=false;
//								return true;
//							}
//
//							if (arg1==controller2.mapping.BUTTON_DPAD_RIGHT) {
//								controller2.rightPressed=false;
//								controller2.rightLocked=false;
//								controller2.BUTTON_DPADRight=false;
//								controller2.BUTTON_DPADRightLocked=false;
//								return true;
//							}
//
//							if (arg1==controller2.mapping.BUTTON_DPAD_LEFT) {
//								controller2.leftPressed=true;
//								controller2.leftLocked=false;
//								controller2.BUTTON_DPADLeft=false;
//								controller2.BUTTON_DPADLeftLocked=false;
//								return true;
//							}
//
//						}
//						return false;
//					}
//
//					@Override
//					public boolean buttonDown(Controller arg0, int arg1) {
//						if (arg0==null || controller1==null) return false;
//
//						controller1.isGamepad();
//
//						if (arg0.hashCode()==controller1.hashcode && controller1.mapping!=null) {
////							Gdx.app.log("opdebug","buttondown:"+arg1);
//
////							last_button=arg1;
//
//							if (arg1==controller1.mapping.BUTTON_X) {
//								controller1.BUTTON_X=true;
//								return true;
//							}
//							if (arg1==controller1.mapping.BUTTON_A) {
//								controller1.BUTTON_A=true;
//								return true;
//							}
//							if (arg1==controller1.mapping.BUTTON_B) {
//								controller1.BUTTON_B=true;
//								controller1.backPressed=true;
//								return true;
//							}
//							if (arg1==controller1.mapping.BUTTON_START) { //button_b) {
//								controller1.BUTTON_SPECIAL2=true;
//								return true;
//							}
//
//
//							if (arg1==controller1.mapping.BUTTON_Y) {
//								controller1.BUTTON_Y=true;
//								return true;
//							}
//							if (arg1==controller1.mapping.BUTTON_LB) {
//								controller1.BUTTON_LB=true;
//								return true;
//							}
//							if (arg1==controller1.mapping.BUTTON_RB) {
//								controller1.BUTTON_RB=true;
//								return true;
//							}
//
//
//							if (arg1==controller1.mapping.BUTTON_DPAD_UP) {
//								controller1.upPressed=true;
//								controller1.BUTTON_DPADUp=true;
//								return true;
//							}
//
//							if (arg1==controller1.mapping.BUTTON_DPAD_DOWN) {
//								controller1.downPressed=true;
//								controller1.BUTTON_DPADDown=true;
//								return true;
//							}
//
//							if (arg1==controller1.mapping.BUTTON_DPAD_RIGHT) {
//								controller1.rightPressed=true;
//								controller1.BUTTON_DPADRight=true;
//								return true;
//							}
//
//							if (arg1==controller1.mapping.BUTTON_DPAD_LEFT) {
//								controller1.leftPressed=true;
//								controller1.BUTTON_DPADLeft=true;
//								return true;
//							}
//
//						} else if (controller2.mapping!=null){
////							pl2_last_button=arg1;
//
//							if (arg1==controller2.mapping.BUTTON_X) {
//								controller2.BUTTON_X=true;
//								return true;
//							}
//							if (arg1==controller2.mapping.BUTTON_A) {
//								controller2.BUTTON_A=true;
//								return true;
//							}
//							if (arg1==controller2.mapping.BUTTON_B) {
//								controller2.BUTTON_B=true;
//								controller2.backPressed=true;
//								return true;
//							}
//							if (arg1==controller2.mapping.BUTTON_Y) {
//								controller2.BUTTON_Y=true;
//								return true;
//							}
//							if (arg1==controller2.mapping.BUTTON_LB) {
//								controller2.BUTTON_LB=true;
//								return true;
//							}
//							if (arg1==controller2.mapping.BUTTON_RB) {
//								controller2.BUTTON_RB=true;
//								return true;
//							}
//
//							if (arg1==controller2.mapping.BUTTON_DPAD_UP) {
//								controller2.upPressed=true;
//								controller2.BUTTON_DPADUp=true;
//								return true;
//							}
//
//							if (arg1==controller2.mapping.BUTTON_DPAD_DOWN) {
//								controller2.downPressed=true;
//								controller2.BUTTON_DPADDown=true;
//								return true;
//							}
//
//							if (arg1==controller2.mapping.BUTTON_DPAD_RIGHT) {
//								controller2.rightPressed=true;
//								controller2.BUTTON_DPADRight=true;
//								return true;
//							}
//
//							if (arg1==controller2.mapping.BUTTON_DPAD_LEFT) {
//								controller2.leftPressed=true;
//								controller1.BUTTON_DPADLeft=true;
//								return true;
//							}
//						}
//
//						return false;
//					}
//
//					@Override
//					public boolean axisMoved(Controller arg0, int arg1, float arg2) {
//						if (arg0==null || controller1==null) return false;
//
//
//
//						arg2=arg2*128;
//
//						if (arg0.hashCode()==controller1.hashcode && controller1.mapping!=null) {
//
//							if (arg1==controller1.mapping.AXIS_LX) {
//								if (arg2>-25 && arg2<25) {
//									controller1.AXIS_LX=0;
//								} else {
//									controller1.AXIS_LX=(int)arg2;
//									controller1.isGamepad();
//								}
//
//
//								if (controller1.mapping.reverseXAxis) controller1.AXIS_LX=-controller1.AXIS_LX;
//								// also handle dpad stuff
//								if (arg2<-64) controller1.leftPressed=true;
//								else {
//									controller1.leftPressed=false;
//									controller1.leftLocked=false;
//								}
//
//								if (arg2>64) controller1.rightPressed=true;
//								else {
//									controller1.rightPressed=false;
//									controller1.rightLocked=false;
//								}
//
//
//								return true;
//
//							} else if (arg1==controller1.mapping.AXIS_LY) {
//								if (arg2>-25 && arg2<25) controller1.AXIS_LY=0;
//								else {
//									controller1.AXIS_LY=(int)arg2;
//									controller1.isGamepad();
//								}
//
//								if (controller1.mapping.reverseYAxis) controller1.AXIS_LY=-controller1.AXIS_LY;
//								if (arg2<-64) controller1.upPressed=true;
//								else {
//									controller1.upPressed=false;
//									controller1.upLocked=false;
//								}
//
//
//								if (arg2>64) controller1.downPressed=true;
//								else {
//									controller1.downPressed=false;
//									controller1.downLocked=false;
//								}
//
//								return true;
//							} else if (arg1==controller1.mapping.AXIS_RX) {
//								if (arg2>-25 && arg2<25) controller1.AXIS_RX=0;
//								else {
//									controller1.AXIS_RX=(int)arg2;
//									controller1.isGamepad();
//								}
//								if (controller1.mapping.reverseXAxis) controller1.AXIS_RX=-controller1.AXIS_RX;
//								return true;
//
//							} else if (arg1==controller1.mapping.AXIS_RY) {
//								if (arg2>-25 && arg2<25) controller1.AXIS_RY=0;
//								else {
//									controller1.AXIS_RY=(int)arg2;
//									controller1.isGamepad();
//								}
//
//								if (controller1.mapping.reverseYAxis) controller1.AXIS_RX=-controller1.AXIS_RX;
//								return true;
//							}
//						} else if (controller2.mapping!=null) {
//							// controller 2
//							if (arg1==controller2.mapping.AXIS_LX) {
//								if (arg2>-25 && arg2<25) controller2.AXIS_LX=0;
//								else {
//									controller2.AXIS_LX=(int)arg2;
//									controller2.isGamepad();
//								}
//								if (controller2.mapping.reverseXAxis) controller2.AXIS_LX=-controller2.AXIS_LX;
//
//								if (arg2<-64) controller2.leftPressed=true;
//								else {
//									controller2.leftPressed=false;
//									controller2.leftLocked=false;
//								}
//
//								if (arg2>64) controller2.rightPressed=true;
//								else {
//									controller2.rightPressed=false;
//									controller2.rightLocked=false;
//								}
//
//								return true;
//
//							} else if (arg1==controller2.mapping.AXIS_LY) {
//								if (arg2>-25 && arg2<25) controller2.AXIS_LY=0;
//								else {
//									controller2.AXIS_LY=(int)arg2;
//									controller2.isGamepad();
//								}
//								if (controller2.mapping.reverseYAxis) controller2.AXIS_LY=-controller2.AXIS_LY;
//
//								if (arg2<-64) controller2.upPressed=true;
//								else {
//									controller2.upPressed=false;
//									controller2.upLocked=false;
//								}
//
//								if (arg2>64) controller2.downPressed=true;
//								else {
//									controller2.downPressed=false;
//									controller2.downLocked=false;
//								}
//								return true;
//							} else if (arg1==controller2.mapping.AXIS_RX) {
//								if (arg2>-25 && arg2<25) controller2.AXIS_RX=0;
//								else controller2.AXIS_RX=(int)arg2;
//
//								if (controller2.mapping.reverseXAxis) controller2.AXIS_RX=-controller2.AXIS_RX;
//								return true;
//
//							} else if (arg1==controller2.mapping.AXIS_RY) {
//								if (arg2>-25 && arg2<25) controller2.AXIS_RY=0;
//								else controller2.AXIS_RY=(int)arg2;
//
//								if (controller2.mapping.reverseYAxis) controller2.AXIS_RY=-controller2.AXIS_RY;
//								return true;
//							}
//
//						}
//						return false;
//					}
//
//				});
//			}
//		}
	}
	
	
	

	@Override
	public void dispose() {
		if (mySocial!=null) mySocial.disposeSocial();
		batch.dispose();
	}

	
	

	public static int PowerOf2(int n) {
		  int k=1;
		  while (k<n) k*=2;
		  return k;
	}	

	
	
	
	@Override
	public void resize(int width, int height) {
		displayW=width;
		displayH=height;


		lowDisplayH=(int)(displayH/Math.floor(displayH/160));
		lowDisplayW=(int)(displayW/(displayH/ (displayH/Math.floor(displayH/160)) ));
		
		
		Gdx.app.log("opdebug","Display:"+displayW+"x"+displayH+"  -  pixels:"+lowDisplayW+"x"+lowDisplayH);
		
		if (m_fbo!=null) m_fbo.dispose();
	    m_fbo = new FrameBuffer(Format.RGB888, PowerOf2(lowDisplayW), PowerOf2(lowDisplayH), false);
	    m_fbo.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
	    m_fboRegion = new TextureRegion(m_fbo.getColorBufferTexture(),0,m_fbo.getHeight()-lowDisplayH,lowDisplayW,lowDisplayH);
//	    m_fboRegion = new TextureRegion(m_fbo.getColorBufferTexture(),0,0,m_fbo.getWidth(),m_fbo.getHeight());
	    m_fboRegion.flip(false, false);

		if (m_fboTop!=null) m_fboTop.dispose();
		m_fboTop = new FrameBuffer(Format.RGBA8888, PowerOf2(lowDisplayW), PowerOf2(lowDisplayH), false);
		m_fboTop.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		m_fboTopRegion = new TextureRegion(m_fboTop.getColorBufferTexture(),0,m_fboTop.getHeight()-lowDisplayH,lowDisplayW,lowDisplayH);
		m_fboTopRegion.flip(false, false);

	    
	    
	    // FakedLight
	    if (lightBuffer!=null) lightBuffer.dispose();
	    lightBuffer = new FrameBuffer(Format.RGBA8888, PowerOf2(lowDisplayW), PowerOf2(lowDisplayH), false);
	    lightBuffer.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
	    lightBufferRegion = new TextureRegion(lightBuffer.getColorBufferTexture(),0,lightBuffer.getHeight()-lowDisplayH,lowDisplayW,lowDisplayH);
	    lightBufferRegion.flip(false, false);
	    
	    

		camera.setToOrtho(true, lowDisplayW,lowDisplayH);
	    
		
		// trigger a reinit of the controllers
	    if (controllersFound>0) Controllers.clearListeners();
		controllersFound=0;
	}

	
	
	public final void setARGB(int alpha, int red, int green, int blue) {
		globalAlpha=alpha;
		globalRed=red;
		globalGreen=green;
		globalBlue=blue;
		Gdx.gl.glClearColor(red/255.0f, green/255.0f, blue/255.0f, alpha/255.0f);
		shapeRenderer.setColor(red/255.0f, green/255.0f, blue/255.0f, alpha/255.0f);
	}
	
	

	public final void setAlpha(int alpha) {
		globalAlpha=alpha;
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, alpha/255.0f);
		shapeRenderer.setColor(globalRed/255.0f, globalGreen/255.0f, globalBlue/255.0f, alpha/255.0f);
	}
	
	
	
	public final void drawPaint(int a, int r, int g, int b) {
		if (globalTexture!=null) {
			batch.end();
			globalTexture=null;
		}
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(r/255.0f, g/255.0f, b/255.0f, a/255.0f);
		shapeRenderer.rect(0,0, lowDisplayW, lowDisplayH);
		shapeRenderer.end();
	}

	
	
	public final void drawRect(int x, int y, int w, int h) {
		if (globalTexture!=null) {
			batch.end();
			globalTexture=null;
		}
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(globalRed/255.0f, globalGreen/255.0f, globalBlue/255.0f, globalAlpha/255.0f);
		shapeRenderer.rect(x,y, w,h);
		shapeRenderer.end();
	}	
	
	
	
	public final void fillRect(int x, int y, int w, int h) {
		if (globalTexture!=null) {
			batch.end();
			globalTexture=null;
		}
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(globalRed/255.0f, globalGreen/255.0f, globalBlue/255.0f, globalAlpha/255.0f);
		shapeRenderer.rect(x,y, w,h);
		shapeRenderer.end();
	}
	
	
	
	public Texture globalTexture=null;
	public final void drawBitmap(Texture sprite,Rect src, Rect dest) {
		if (sprite!=globalTexture) {
			if (globalTexture!=null) batch.end();
			
			batch.begin();
			batch.setProjectionMatrix(camera.combined);
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			
			globalTexture=sprite;
		}


		batch.setColor(1f,1f,1f,globalAlpha/255.0f);
		batch.draw(sprite, dest.left,dest.top,
				dest.width,dest.height,
				src.left,src.top, 
				src.width,src.height,
				
				false,true);
	}
	
	// use this for sprites needing rotation
	public final void drawBitmapRotated(Texture sprite,Rect src, Rect dest, float myRotate) {
		if (sprite!=globalTexture) {
			if (globalTexture!=null) batch.end();
			
			batch.begin();
			batch.setProjectionMatrix(camera.combined);
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			
			globalTexture=sprite;
		}


		batch.setColor(1f,1f,1f,globalAlpha/255.0f);

		batch.draw(sprite,dest.left,dest.top,  
					(dest.width>>1), (dest.height>>1),
					dest.width,dest.height,
					1f,1f,
					myRotate, 
					src.left,src.top,
					src.width,src.height,
					false,true);
	}	
	
	
	public final void clipRect(int x, int y, int w, int h) {
		Rectangle scissors = new Rectangle();
		Rectangle clipBounds = new Rectangle(x,y,w,h);
		ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), clipBounds, scissors);
		ScissorStack.pushScissors(scissors);
	}

	
	
	public final void endClip() {
		ScissorStack.popScissors();
	}
	
	
	
	// openGL Render() - 60 frames per second
	@Override
	public void render() {
		
		if (Gdx.input.isKeyPressed(Keys.ENTER) && 
				(Gdx.input.isKeyPressed(Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Keys.ALT_RIGHT)) )  {
			if (!switchFullScreenLocked) {
				switchFullScreen=true;
				switchFullScreenLocked=true;
				triggerFullScreenSwitch=true;
			}
		} else {
			switchFullScreen=false;
			switchFullScreenLocked=false;
		}		
		
		
		if (triggerFullScreenSwitch) {
			if (!controller1.isMouse || (!controller1.BUTTON_X && !controller1.BUTTON_Xlocked)) {
				setDisplayMode(1080, 720, !isFullScreen);
				triggerFullScreenSwitch=false;
			}
		}
		
		if (!controller1.isMouse) {
			if (isFullScreen) Gdx.input.setCursorCatched(true);
		}
		
		
//		loopStart = System.currentTimeMillis();

    	worldTicks++;																// global worldticking
		if (worldTicks>1000) worldTicks=0;											// heart-beat of the game
		
		// transform touch location to pixel-art screen locations
		touchX = -1;
		touchY = -1;
		if (mTouchX[0] >= 0 && mTouchY[0] >= 0) {
			touchX=mTouchX[0];
			touchY=mTouchY[0];
//				touchY = (lowDisplayH / 100f) * ((100f / displayH) * mTouchY[0]);
//				touchX = (lowDisplayW / 100f) * ((100f / displayW) * mTouchX[0]);
		}
	        
			

		// render to pixel-art framebuffer
        m_fbo.begin();		
        	Gdx.gl.glDisable(GL20.GL_BLEND);
        	Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        	camera.setToOrtho(true, m_fbo.getWidth(), m_fbo.getHeight());
        	camera.update();
	        
        	batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        	setAlpha(255);
        	
        	// gamelogic + render
			GameLoop();
			
			if (globalTexture!=null) {
				batch.end();
				globalTexture=null;
			}
		m_fbo.end();

			
		// use FakeLight system?
		if (FLight.isLightRendering) {
        		
        	// FakedLights
        	lightBuffer.begin();
	        Gdx.gl.glClearColor(FLight.ambientColor.r,FLight.ambientColor.g,FLight.ambientColor.b,FLight.ambientColor.a);
	        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
	        Gdx.gl.glEnable(GL20.GL_BLEND);
    
	        batch.begin();
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, -1);

	        
			for (int i=0; i<FLight.myLights.length; i++) {
				if (FLight.myLights[i].isActive()) {
					batch.setColor(FLight.myLights[i].getColor());
					
					float tx=FLight.myLights[i].getX();
					float ty=FLight.myLights[i].getY();
					float tw=(128/100f)*FLight.myLights[i].getDistance();
					
					// 64,92
					
					switch (FLight.myLights[i].getLightType()) {
						case FLight.LightType_Up:
							tx-=((128/100f)*FLight.myLights[i].getDistance())/2;
							ty-=((92/100f)*FLight.myLights[i].getDistance());
							batch.draw(FLight.lightSprite, tx,ty,
									tw,tw,
									0,128, 
									128,128,
									false,true);
						break;

						case FLight.LightType_Right:
							tx-=((128/100f)*FLight.myLights[i].getDistance())/2;
							ty-=((128/100f)*FLight.myLights[i].getDistance());
							batch.draw(FLight.lightSprite,tx,ty,  
									tw/2,tw,
									tw,tw,
									1f,1f,
									90, 
									0,128,
									128,128,
									false,true);
						break;
						

						case FLight.LightType_Down:
							tx-=((128/100f)*FLight.myLights[i].getDistance())/2;
							ty+=((128/100f)*FLight.myLights[i].getDistance());
							batch.draw(FLight.lightSprite,tx,ty,  
									tw/2,0,
									tw,tw,
									1f,1f,
									180, 
									0,128,
									128,128,
									false,true);
						break;			
						

						case FLight.LightType_Left:
							tx-=((128/100f)*FLight.myLights[i].getDistance())/2;
							ty-=((128/100f)*FLight.myLights[i].getDistance());
							batch.draw(FLight.lightSprite,tx,ty,  
									tw/2,tw,
									tw,tw,
									1f,1f,
									270, 
									0,128,
									128,128,
									false,true);
						break;							

						
						case FLight.LightType_SphereTense:
							tx-=(tw/2);
							ty-=(tw/2);
							batch.draw(FLight.lightSprite, tx,ty,
									tw,tw,
									256,0, 
									128,128,
									false,true);
						break;
						
						case FLight.LightType_FLARE:
							tx-=(tw/2);
							ty-=(tw/2);
							batch.draw(FLight.lightSprite, tx,ty,
									tw,tw,
									128,128, 
									128,128,
									false,true);
						break;
						
						default:
							tx-=(tw/2);
							ty-=(tw/2);
							batch.draw(FLight.lightSprite, tx,ty,
									tw,tw,
									0,0, 
									128,128,
									false,true);
						break;
					}
					
				}
//					FLight.myLights[i].setActive(false);
			}

					
	        batch.end();
	        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

	        lightBuffer.end();
        }		
			

		 // render pixel-art buffer to screen
		 if(m_fbo != null) {
			camera.setToOrtho(true, displayW, displayH);
			camera.update();
			
			batch.begin();        
			batch.setProjectionMatrix(camera.combined);
			batch.disableBlending(); //setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			batch.setColor(1,1,1,1);
			batch.draw(m_fboRegion, 0, 0, displayW, displayH);
			batch.end();
			batch.enableBlending();
		 }


		 if (FLight.isLightRendering) {
			batch.begin();
	        batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);
			batch.draw(lightBufferRegion, 0, 0,displayW,displayH);               
			batch.end();
	        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		 }
			 
    	// render "post light" stuff (statusbar, pause screen, etc)
		 
		 m_fboTop.begin();		
			 Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
			 Gdx.gl.glClear(GL20. GL_COLOR_BUFFER_BIT);
			 camera.setToOrtho(true, m_fboTop.getWidth(), m_fboTop.getHeight());
			 camera.update();

			 GameLoopPostLights();
        
			if (globalTexture!=null) {
				batch.end();
				globalTexture=null;
			}
	
		m_fboTop.end();

		
//		Gdx.gl.glBlendFunc(GL20.GL_DST_COLOR, -1); //GL20.GL_ZERO);
			
		// render framebuffer to fullscreen
		Gdx.gl.glDisable(GL20.GL_BLEND);
		camera.setToOrtho(true, displayW, displayH);
		camera.update();

		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		batch.setColor(1,1,1,1);
		batch.draw(m_fboTopRegion, 0, 0, displayW, displayH);               
		batch.end();

		 
		 
		fpsCount++;
		if (loopEnd-loopPause>1000) {
			secondPassed=true;
			loopPause=loopEnd;
			if (controllersFound==0) initControllers();
			myFramerate=fpsCount;
			fpsCount=0;
		}
		

		if (mySocial!=null) mySocial.processInfo();

		loopEnd = System.currentTimeMillis();
		/*
		if (loopEnd-loopStart<16) {
			fpsSleep=16-(int)(loopEnd-loopStart);
		} else { 
			fpsSleep=2;  // incase we are trailing behind fps
		}
		try {
			Thread.sleep(fpsSleep);
		} catch (InterruptedException e) {}
		 */		
	}
	

	
	
	public DisplayMode[] modes; 
	public int currentModeID=-1;
	private int fallbackModeID;
	
	public void setDisplayMode(int width, int height, boolean fullscreen) {
		
		if (argument_forceWindowed) fullscreen=false;
		
		// return if requested DisplayMode is already set
        if ((Gdx.app.getGraphics().getWidth() == width) && 
			(Gdx.app.getGraphics().getHeight() == height) && 
			(Gdx.app.getGraphics().isFullscreen() == fullscreen)) {
			return;
		}
		
        if (fullscreen) {
        	// just use current desktop display width*height so we know it's fullscreen correctly
        	width=Gdx.app.getGraphics().getWidth();
        	height=Gdx.app.getGraphics().getHeight();
        }

        
		DisplayMode targetDisplayMode = null;
		DisplayMode fallBackMode = null;

		float 	baseRatio=(float)width/(float)height;	// 1,5
		float	currentRatio;
		
		
		modes = Gdx.app.getGraphics().getDisplayModes();
		
		if (fullscreen) {
			int freq = 0;
			
			for (int i=0;i<modes.length;i++) {
				DisplayMode current = modes[i];
				currentRatio=(float)current.width/(float)current.height;
				
				if (current.width>=width && current.height>=height && currentRatio>=baseRatio) {
					if (current.refreshRate >= freq && current.bitsPerPixel >= Gdx.app.getGraphics().getDisplayMode().bitsPerPixel) {
						targetDisplayMode = current;
						currentModeID=i;
						freq = targetDisplayMode.refreshRate;
					}
				} else {
					// find a "best" resolution if we can't find an exact math on ratio/w/h/bbp
					if ((current.bitsPerPixel == Gdx.app.getGraphics().getDisplayMode().bitsPerPixel ) &&
					    (current.refreshRate == Gdx.app.getGraphics().getDisplayMode().refreshRate )) {
						fallBackMode = current;
						fallbackModeID=i;
					}
				}
			}
			
			if (targetDisplayMode!=null) {
				Gdx.app.getGraphics().setWindowedMode(targetDisplayMode.width, targetDisplayMode.height);
			} else if (fallBackMode!=null) {
				Gdx.app.getGraphics().setWindowedMode(fallBackMode.width, fallBackMode.height);
				currentModeID=fallbackModeID;
			}
		} else {
			Gdx.app.getGraphics().setWindowedMode(width, height);
		}

		
		isFullScreen=fullscreen;
		
		if (isFullScreen) {
			Gdx.input.setCursorPosition((displayW>>1),(displayH>>1));
			Gdx.input.setCursorCatched(true);
		} else {
			Gdx.input.setCursorPosition((displayW>>1),(displayH>>1));
			Gdx.input.setCursorCatched(false);
		}
		
	
		// trigger a reinit of the controllers
		controllersFound=0;
	}	
	
	
	
	
	// FakeLight system
	public final void setAmbientLight(float red, float green, float blue, float alpha) {
		FLight.ambientColor.set(red,green,blue,alpha/4); 
	}
	

	

	
	
	
	
	public final void renderText(String myText, int startCharacter, int myX, int myY, int myWidth, int myLineCount, int fontID) {
		String convertedText=myText.toUpperCase();
		char[] myChars=convertedText.toCharArray();

		int tx=myX;
		int ty=myY;
		int ty2;
		int CharValue;
		int tLineCount=0;
		int wordLength;
		int wordID;

		int yOffset=0;
		int fontWidth=5;
		int fontWidthSprite=6;
		yOffset=(fontID * 8);
		if (fontID==2) fontWidth=6;
		if (fontID==3) {
			yOffset=32;
			fontWidth=7;
			fontWidthSprite=7;
		}

		// center text?
		if (tx == -1) {
			if (fontID>1) {
				// count "Spaces"
				ty2=0;
				for (int i=0; i < myChars.length; i++) {
					if ((int) myChars[i] == 32) ty2++;
				}

				tx=(lowDisplayW >> 1) - ( ((myChars.length-ty2) * fontWidth) >>1);
				tx-=ty2;
			} else {
				tx=(lowDisplayW >> 1) - (myChars.length * 2); // *2 = half
																// width of 1
																// character
			}

			if (myText.indexOf("~") > 0)
				tx+=4;

			myX=tx;
			// Gdx.app.log("opdebug","render text, lowdisplayW:"+lowDisplayW+"  / tx:"+tx);
		}

		int i=startCharacter;

		while (i < myChars.length) {
			CharValue=(int) myChars[i];

			// detect if next word fits in this space
			wordLength=0;
			wordID=i;
			while ((int) myChars[wordID] != 32 && wordID < myChars.length - 1) {
				wordLength++;
				wordID++;
			}

			if (tx + (wordLength*fontWidth) > myX + myWidth) {
				tx=myX;
				ty+=7;
				tLineCount++;
				if (tLineCount >= myLineCount) {
					return;
				}
			}

			switch (CharValue) {
				case 32:// space
					tx+=3;
					break;

				case 33: // !
					tx++;
					dest.set(tx, ty, tx + 3, ty + 7);
					src.set(247, yOffset, 250, yOffset + 7);
					drawBitmap(sprites[25], src, dest);
					tx+=3;
					break;

				case 39: // '
					tx++;
					dest.set(tx, ty, tx + 4, ty + 4);
					src.set(236, yOffset, 240, yOffset + 4);
					drawBitmap(sprites[25], src, dest);
					tx+=3;
					break;

				case 40: // (
					tx++;
					dest.set(tx, ty, tx + 2, ty + 6);
					src.set(223, yOffset, 225, yOffset + 6);
					drawBitmap(sprites[25], src, dest);
					tx+=3;
					break;

				case 41: // )
					tx++;
					dest.set(tx, ty, tx + 2, ty + 6);
					src.set(228, yOffset, 230, yOffset + 6);
					drawBitmap(sprites[25], src, dest);
					tx+=3;
					break;

				case 43: // +
					tx++;
					dest.set(tx, ty + 2, tx + 3, ty + 5);
					src.set(79, 25, 82, 28);
					drawBitmap(sprites[25], src, dest);
					tx+=4;
					break;

				case 45: // -
					tx++;
					dest.set(tx, ty + 3, tx + 3, ty + 4);
					src.set(79, 26, 82, 27);
					drawBitmap(sprites[25], src, dest);
					tx+=3;
					break;

				case 46: // .
					tx++;
					dest.set(tx, ty, tx + 3, ty + 7);
					src.set(232, yOffset, 235, yOffset + 7);
					drawBitmap(sprites[25], src, dest);
					tx+=3;
					break;

				case 47: // /
					tx++;
					dest.set(tx, ty + 1, tx + 3, ty + 6);
					src.set(84, 24, 87, 29);
					drawBitmap(sprites[25], src, dest);
					tx+=3;
					break;

				case 58: // :
					tx++;
					dest.set(tx, ty, tx + 3, ty + 7);
					src.set(250, yOffset, 253, yOffset + 7);
					drawBitmap(sprites[25], src, dest);
					tx+=3;
					break;

				case 60: // <
					dest.set(tx, ty, tx + 4, ty + 7);
					src.set(129, 24, 133, 31);
					drawBitmap(sprites[25], src, dest);
					tx+=5;
					break;

				case 62: // >
					dest.set(tx, ty, tx + 4, ty + 7);
					src.set(135, 24, 139, 31);
					drawBitmap(sprites[25], src, dest);
					tx+=5;
					break;

				case 63: // ?
					tx++;
					dest.set(tx, ty, tx + 5, ty + 7);
					src.set(241, yOffset, 246, yOffset + 7);
					drawBitmap(sprites[25], src, dest);
					tx+=5;
					break;

				case 124: // | used as line-break
					tx=myX;
					ty+=7;
					tLineCount++;
					if (tLineCount >= myLineCount) {
						return;
					}
					break;

				case 126: // used for button/key images
					i++;
					CharValue=(int) myChars[i];
					switch (CharValue - 48) {
						case 0: // gamepad-A
							dest.set(tx, ty - 1, tx + 9, ty + 8);
							src.set(0, 23, 9, 32);
							drawBitmap(sprites[25], src, dest);
							break;

						case 1: // gamepad-B
							dest.set(tx, ty - 1, tx + 9, ty + 8);
							src.set(10, 23, 19, 32);
							drawBitmap(sprites[25], src, dest);
							break;

						case 2: // gamepad-X
							dest.set(tx, ty - 1, tx + 9, ty + 8);
							src.set(20, 23, 29, 32);
							drawBitmap(sprites[25], src, dest);
							break;

						case 3: // gamepad-Y
							dest.set(tx, ty - 1, tx + 9, ty + 8);
							src.set(30, 23, 39, 32);
							drawBitmap(sprites[25], src, dest);
							break;

						case 4: // keyboard-X
							tx+=renderButton(tx,ty,keyboardConfig_select,true);
							break;

						case 5: // keyboard-I
							tx+=renderButton(tx,ty,keyboardConfig_inventory,true);
							break;

						case 6: // keyboard-ESC
							tx+=renderButton(tx,ty,keyboardConfig_cancel,true);
							break;

						case 7: // keyboard-M
							tx+=renderButton(tx,ty,keyboardConfig_map,true);
							break;

						case 8: // keyboard-TAB
							tx+=renderButton(tx,ty,keyboardConfig_map,true);
							break;

						case 9: // xbox |> start
							dest.set(tx, ty - 1, tx + 9, ty + 8);
							src.set(118, 23, 127, 31);
							drawBitmap(sprites[25], src, dest);
							break;

						case 20: // ~d = DPAD / arrow
							dest.set(tx, ty - 1, tx + 9, ty + 8);
							src.set(141, 23, 150, 32);
							drawBitmap(sprites[25], src, dest);
							break;

					}
					tx+=10;
					break;

				default:
					if (CharValue > 64 && CharValue < 91) {
						// letters
						CharValue-=65;

						if (CharValue==12 || CharValue==22) tx++;
						dest.set(tx, ty, tx+fontWidth, ty + 7);
						src.set(CharValue*fontWidthSprite, yOffset, (CharValue*fontWidthSprite)+fontWidth, yOffset + 7);
						drawBitmap(sprites[25], src, dest);
						if (CharValue==12 || CharValue==22) tx++;

						if (fontID==3) tx+=fontWidth;
						else if (fontID==2) tx+=6;
						else tx+=4;
					} else if (CharValue > 47 && CharValue < 58) {
						// numbers
						CharValue-=48;
						
						dest.set(tx, ty, tx+fontWidth, ty + 7);
						src.set(156 + (CharValue*fontWidthSprite), yOffset, 156+(CharValue*fontWidthSprite)+fontWidth, yOffset + 7);
						drawBitmap(sprites[25], src, dest);

						if (fontID==3) tx+=fontWidth;
						else if (fontID == 2) tx+=6;
						else tx+=4;
					} else {
						// unidentified char
					}
					break;
			}

			i++;
		}
	}

	
	
	public final int renderButton(int myx, int myy, int keyboardIDX, boolean renderBackground) {
		// 3letter buttons
		if (keyboardConfig[keyboardIDX]==Keys.ESCAPE || keyboardConfig[keyboardIDX]==Keys.TAB) {
			if (renderBackground) {
				dest.set(myx, myy - 1, myx + 18, myy + 8);
				src.set(160,23,178,32);
				drawBitmap(sprites[25], src, dest);
			}
			
			switch (keyboardConfig[keyboardIDX]) {
				case Keys.ESCAPE:
					dest.set(myx, myy - 1, myx + 18, myy + 8);
					src.set(60,23,78,32);
					drawBitmap(sprites[25], src, dest);
				break;

				case Keys.TAB:
					dest.set(myx, myy - 1, myx + 18, myy + 8);
					src.set(100,23,118,32);
					drawBitmap(sprites[25], src, dest);
				break;
				
				case Keys.ENTER:
										
				break;
			}
			// warning: increase global value TX
			return 9;
		} else if (keyboardConfig[keyboardIDX]==Keys.ENTER) {
			if (renderBackground) {
				dest.set(myx, myy-1, myx+9, myy+8);
				src.set(40, 23, 49, 32);
				drawBitmap(sprites[25], src, dest);
			}
			dest.set(myx+1, myy+1, myx+7, myy+8);
			src.set(152, 25, 158, 32);
			drawBitmap(sprites[25], src, dest);	
		} else if (keyboardConfig[keyboardIDX]==Keys.LEFT) {
			if (renderBackground) {
				dest.set(myx, myy-1, myx+9, myy+8);
				src.set(40, 23, 49, 32);
				drawBitmap(sprites[25], src, dest);
			}
			dest.set(myx+3, myy+1, myx+7, myy+6);
			src.set(129, 25, 133, 30);
			drawBitmap(sprites[25], src, dest);				
		} else if (keyboardConfig[keyboardIDX]==Keys.RIGHT) {
			if (renderBackground) {
				dest.set(myx, myy-1, myx+9, myy+8);
				src.set(40, 23, 49, 32);
				drawBitmap(sprites[25], src, dest);
			}
			dest.set(myx+3, myy+1, myx+7, myy+6);
			src.set(135, 25, 139, 30);
			drawBitmap(sprites[25], src, dest);				
		} else if (keyboardConfig[keyboardIDX]==Keys.UP) {
			if (renderBackground) {
				dest.set(myx, myy-1, myx+9, myy+8);
				src.set(40, 23, 49, 32);
				drawBitmap(sprites[25], src, dest);
			}
			dest.set(myx+2, myy+2, myx+7, myy+6);
			src.set(179, 23, 184, 27);
			drawBitmap(sprites[25], src, dest);				
		} else if (keyboardConfig[keyboardIDX]==Keys.DOWN) {
			if (renderBackground) {
				dest.set(myx, myy-1, myx+9, myy+8);
				src.set(40, 23, 49, 32);
				drawBitmap(sprites[25], src, dest);
			}
			dest.set(myx+2, myy+2, myx+7, myy+6);
			src.set(179, 28, 184, 32);
			drawBitmap(sprites[25], src, dest);		
		} else if (keyboardConfig[keyboardIDX]>=Keys.F1 && keyboardConfig[keyboardIDX]<=Keys.F12) {
			// F
			dest.set(myx+2, myy+1, myx+7, myy+8);
			src.set(30,16, 35,16+7);
			drawBitmap(sprites[25], src, dest);
			
			// #
			int CharValue=keyboardConfig[keyboardIDX]-Keys.F1;
			dest.set(myx+8, myy+1, myx+14, myy+8);
			src.set(162+(CharValue*6), 16, 167+(CharValue*6), 16+7);
			drawBitmap(sprites[25], src, dest);
			return 9;
			
		} else {
			// key-name
			int CharValue=(Keys.toString( keyboardConfig[keyboardIDX] ).charAt(0));
			if (renderBackground) {
				dest.set(myx, myy-1, myx+9, myy+8);
				src.set(40, 23, 49, 32);
				drawBitmap(sprites[25], src, dest);
			}
			
			if (CharValue>=65 && CharValue<=91) {
				CharValue-=65;
				// letters
				dest.set(myx+2, myy+1, myx+7, myy+8);
				src.set(CharValue*6, 16, (CharValue*6)+5, 16+7);
				drawBitmap(sprites[25], src, dest);
			} else if (CharValue>=48 && CharValue<=58) {
				// digits
				CharValue-=48;
				dest.set(myx+2, myy+1, myx+7, myy+8);
				src.set(156+(CharValue*6), 16, 161+(CharValue*6), 16+7);
				drawBitmap(sprites[25], src, dest);
			}
		}
		
		return 0;
	}	

	
	
	
	
	
	
	public void pause() {}
	public void resume() {}	
	public void engineInit(){};
	public void GameLoop(){};
	public void GameLoopPostLights(){};

	
}
;