package com.orangepixel.planetbusters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.orangepixel.utils.ArcadeCanvas;
import com.orangepixel.utils.FLight;

public class myCanvas extends ArcadeCanvas {
	
	
	public final static int[][] windowedModes=new int[][] {
		{1024, 640, 4, 3}, 
		{1024, 768, 4, 3}, 
		{1080, 720, 3, 2}, 
		{1152, 720, 16, 10}, 
		{1280, 720, 16, 9}, 
		{1280, 800, 16, 10}, 
		{9999, 9999, 16, 10}
	};
	
	public static int windowedModeID=1;
	public int tempMusicVolume;
	public int tempSoundVolume;
	public int currentMusicVolume;
	
	// active profile
	PlayerProfile activePlayer;

	
	// used for our splash logo/intro
	static Texture splashImage;
	int splashFrame;
	boolean splashDone;
	int splashAlpha;
	int splashYSpeed;
	int splashY;

	// controller setup
	static int touchSelected;
	static int touchSelectedTx;
	static int touchSelectedTy;	
	static int charSelectDelay;			
	
	// object-pools
//	static FX[] fxList=new FX[2640];
//	static Bullets[] bulletList=new Bullets[128];
//	static Spriter[] spriteList = new Spriter[3200];
	
	static Player	myPlayer=new Player();
	static World	myWorld=new World();
	
	
	// interface bits and bobs
	int		levelTitleCountdown;
	int		menuSelectedItem;
	int		menuSelectedItem2;
	int[]	CompletedMissionsAlpha= new int[4];
	int[]	CompletedMissionsAlphaTarget = new int[4];
	
	
	// global used for drawing, calculating, etc
	static int tx;
	static int ty;
	static int tx2;
	static int ty2;
	static int tile;

	
	
	public void engineInit() {
		// create our splash image (Orangepixel logo)
		splashImage=new Texture(Gdx.files.internal("spl2.png"), true);
		splashFrame=0;
		splashDone=false;
		splashAlpha=0;
		splashY=0;
		splashYSpeed=-8;

		// start it all
		GameState=ININIT;
	}
	


	// Main game initialisation
	public void init() {
		// initialise player profile, for saving/loading settings and progress
		activePlayer=new PlayerProfile("PLANETBUSTER");
		activePlayer.loadSettings();

		currentMusicVolume=tempMusicVolume=activePlayer.musicVolume;
		tempSoundVolume=activePlayer.soundVolume;
		
		// keyboard settings from preferences or defaults
		for (int i=0; i<=10; i++) {
			if (activePlayer.keyboardSettings[i]<0) activePlayer.keyboardSettings[i]=keyboardConfigDefault[i];
			keyboardConfig[i]=activePlayer.keyboardSettings[i];
		}


		// init the sound engine
		Audio.initSounds();


		// generate our entity objects
		Monsters.initMonsters();
		Bullets.initBullets();
		FX.initFX();

		
		
		// world graphics, freed and reloaded at loadWorld
		sprites[1]=new Texture(Gdx.files.internal("m01.png"), true);
		sprites[2]=new Texture(Gdx.files.internal("t01.png"), true);
		
		sprites[25]=new Texture(Gdx.files.internal("uipcfont.png"), true);
		
		

		initControllers();
		
		// set correct resolution
		windowedModeID=activePlayer.storedWindowedModeID;
		if (windowedModeID < 0) {
			// first start? set to 1080x720 and windowed
			windowedModeID=2;
			activePlayer.storedWindowedModeID=windowedModeID;
			activePlayer.saveSettings();
		}
		setDisplayMode(windowedModes[windowedModeID][0], windowedModes[windowedModeID][1], activePlayer.useFullscreen);

		// Light system
		FLight.initLights();
		FLight.isLightRendering=false;
		// set the "light" sprite
		FLight.lightSprite=new Texture(Gdx.files.internal("lights.png"), true);
		
		// start our Splash, including a nice sound
		worldTicks=0;
		Audio.playSound(Audio.FX_SPLASH);
		GameState=INSPLASH;
	}
	
	
	
	
	public final void GameLoop() {

		switch (GameState) {
			case ININIT:
				init();
				break;

				
				
				
				
			case INSPLASH:
				drawPaint(255, 255, 255, 255);

				setAlpha(splashAlpha);
				if (!splashDone || splashAlpha < 255) {
					splashAlpha+=32;
					if (splashAlpha > 255)
						splashAlpha=255;
				}

				if (!splashDone && worldTicks % 2 == 0) {
					splashFrame+=16;
					if (splashFrame == 96) {
						splashFrame=0;
						splashDone=true;
					}
				}

				// jump
				if (splashYSpeed < 6 && worldTicks % 2 == 0) splashYSpeed++;
				splashY+=splashYSpeed;
				if (splashY >= 0) {
					splashY=0;
					splashYSpeed=-(splashYSpeed >> 1);
				}

				// center on screen
				tx=(lowDisplayW >> 1) - 36;
				ty=((lowDisplayH >> 1) - 48) + splashY;
				// render pixel
				dest.set(tx, ty, tx + 72, ty + 72);
				src.set(splashFrame, 0, splashFrame + 16, 16);
				drawBitmap(splashImage, src, dest);

				// render name
				tx=(lowDisplayW >> 1) - 61;
				ty=(lowDisplayH >> 1) + 30;
				dest.set(tx, ty, tx + 122, ty + 26);
				src.set(0, 16, 122, 42);
				drawBitmap(splashImage, src, dest);

				
				if (worldTicks > 48 && splashDone) {

					if (mySocial != null) {
						mySocial.initSocial();
						mySocial.loginSocial();
					}

					activePlayer.resetControls(lowDisplayW, lowDisplayH);

					Globals.fillRandomTable();
					
					initNewGame();
				}
				break;
				
				
			
				

			case INITMAP:
				Monsters.killAll();
				FX.killAll();
				Bullets.killAll();

				myWorld.initWorld();
				
				generateWorld();

				levelTitleCountdown=256;
				
				FLight.isLightRendering=true;
				GameState=INGAME;
			break;
				
				
				
			case INGAME:
				FLight.clearLights();
				handleInput();

				
				myPlayer.update(myWorld);
				myWorld.update();
				myWorld.handleCamera(myPlayer);
				
				// add "planet side sun light"
				FLight.addLight((lowDisplayW>>1)-myWorld.worldOffsetX,(-64)-myWorld.worldOffsetY,256,FLight.LightType_Sphere,29,175,243,255);
				
				
				Monsters.updateMonsters(myWorld, myPlayer);
				Bullets.updateBullets(myWorld, myPlayer);
				FX.updateFX(myWorld,myPlayer);
				
				
				renderScene();
				
				
				if (myWorld.levelCompleted) {
					// choose next mission!
					menuSelectedItem=0;
					FLight.isLightRendering=false;
					
					Monsters.killAll();
					FX.killAll();
					Bullets.killAll();
					
					myPlayer.visible=false;
					
					
					myWorld.level++;
					if (myWorld.level==4) {
						myWorld.world++;
						myWorld.createGalaxy();
						myWorld.level=0;
					}
					GameState=INITMAP;
					
				}
			break;
		}
	}
	
	
	
	// Render everything uneffected by lights (statusbar, dialogs, messages, etc)
	public final void GameLoopPostLights() {
		switch (GameState) {
			case INGAME:
				
				
				// render topscene (on top of lights)
				// render solid tiles ("underworld")
				tx=-myWorld.worldOffsetX;
				for (int x=0; x < World.tileMapW; x++) {
					if (tx>-16 && tx<lowDisplayW) {
						ty=48-myWorld.worldOffsetY;
						for (int y=3; y < World.tileMapH; y++) {
							if (ty>-16 && ty<lowDisplayH) {
								if (myWorld.getTile(x,y)==World.tSOLID) {
									dest.set(tx,ty,tx+16,ty+16);
									src.set(0,96,16,112);
									drawBitmap(sprites[1],src,dest);
								}
							}
							ty+=16;
						}
					}
					tx+=16;
				}
				
				renderFX(9);
				
				renderStatusbar();
				
				
				// render level title
				if (levelTitleCountdown>0) {
					levelTitleCountdown--;
					renderText(Globals.galaxyNamePre[myWorld.worldPre]+" "+Globals.galaxyNamePost[myWorld.worldPost],0,-1,32,116,0,2);
					tx=(lowDisplayW>>1)-59;
					ty=40;
					dest.set(tx,ty,tx+117,ty+1);
					src.set(0,137,117,138);
					drawBitmap(sprites[1],src,dest);
					renderText( Mission.getName(myWorld.level) , 0, -1, 42, 320, 0, 0);
					
					// (world.level)
					renderText("("+myWorld.world+"."+(myWorld.level+1)+")",0,-1,52,320,0,0);
				}
			break;
			
			
		}
	}
	
	
	// keyboard or gamepad only
	// default is set to arrow-keys + x  
	public final void handleInput() {
		myPlayer.resetInput();
		
		if (controller1.leftPressed) {
			myPlayer.leftPressed=true;
		}
		
		if (controller1.rightPressed) {
			myPlayer.rightPressed=true;
		}
		
		if (controller1.upPressed) {
			myPlayer.upPressed=true;
		}
		
		if (controller1.BUTTON_X) {
			myPlayer.actionPressed=true;
		}
		
		
		// TEST
		if (controller1.backPressed && !controller1.backLocked) {
			initNewGame();
		}
	}
	
	
	
	
	
	
	// [ INIT CODE! ]----------------------------------------------------------------------------
	public final void initNewGame() {
		myPlayer.newGameInit();
		myWorld.initNewGame(lowDisplayW,lowDisplayH);
		
		myWorld.level=0;
		GameState=INITMAP;
	}
	
	
	
	
	
	
	// [ RENDERING STUFF! ]----------------------------------------------------------------------------
	public final void renderScene() {
		drawPaint(255, 0, 0, 0);
		
		// render planet (top-part)
		tx=-(128+(myWorld.worldOffsetX%96));
		ty=(-44)-myWorld.worldOffsetY;
		//ty=(myWorld.worldFloorY-26)-myWorld.worldOffsetY;
		while (tx<lowDisplayW) {
			dest.set(tx,ty,tx+96,ty+96);
			src.set(0,32,96,128);
			drawBitmap(sprites[2],src,dest);
			tx+=96;
		}

		// render background tiles ("underworld")
		tx=-myWorld.worldOffsetX;
		for (int x=0; x < World.tileMapW; x++) {
			if (tx>-16 && tx<lowDisplayW) {
				ty=48-myWorld.worldOffsetY;
				for (int y=3; y < World.tileMapH; y++) {
					if (ty>-16 && ty<lowDisplayH) {
						if (myWorld.getTile(x,y)!=World.tSOLID) {
							tile=myWorld.renderMap[x+(y*World.tileMapW)];

							dest.set(tx, ty, tx + 16, ty + 16);
							src.set(((tile & 7) << 4), ((tile >> 3) << 4), ((tile & 7) << 4) + 16, ((tile >> 3) << 4) + 16);
							drawBitmap(sprites[2], src, dest);
						}
					}
					ty+=16;
				}
			}
			tx+=16;
		}		
		
		
		renderMonsters(1);
		renderPlayer(myPlayer);
		renderBullets(1);
		renderFX(1);
		
		// render planet (bottom-part) hanging over the first row of the underworld images
		tx=-(128+(myWorld.worldOffsetX%96));
		ty=(-44)-myWorld.worldOffsetY;
		while (tx<lowDisplayW) {
			dest.set(tx,ty+72,tx+96,ty+96);
			src.set(0,104,96,128);
			drawBitmap(sprites[2],src,dest);
			tx+=96;
		}		
		
		
	}
	
	
	
	public final void renderPlayer( Player tmpPlayer ) {
		if (!tmpPlayer.visible) return;
		
		tx=tmpPlayer.x-myWorld.worldOffsetX;
		ty=tmpPlayer.y-myWorld.worldOffsetY;
		
		
		// render weapon? (on our back/behind player when not shooting)
		if (tmpPlayer.actionDelay==0) renderWeapon(tmpPlayer);


		// player sprite
		dest.set(tx,ty,tx+10,ty+10);
		src.set(tmpPlayer.xOffset, tmpPlayer.yOffset, tmpPlayer.xOffset+tmpPlayer.w, tmpPlayer.yOffset+tmpPlayer.h);
		drawBitmap(sprites[1], src, dest);
		
		// render weapon?
		if (tmpPlayer.actionDelay>0) renderWeapon(tmpPlayer);
	}

	

	public final void renderMonsters(int myRenderPassID) {
		int i=0;
		int tx;
		int ty;
		
		Monsters tmpMonster;
		
		while (i<Monsters.monsterList.length) {
			tmpMonster=Monsters.monsterList[i];
			
			if (!tmpMonster.deleted 
					&& !tmpMonster.died 
					&& tmpMonster.visible 
					&& tmpMonster.renderPass==myRenderPassID) {
				
					
				
					tx=tmpMonster.x-myWorld.worldOffsetX;
					ty=tmpMonster.y-myWorld.worldOffsetY;
					
					dest.set(tx,ty,tx+tmpMonster.w, ty+tmpMonster.h);
					src.set(tmpMonster.xOffset, tmpMonster.yOffset, tmpMonster.xOffset+tmpMonster.w, tmpMonster.yOffset+tmpMonster.h);
					drawBitmap(sprites[1],src,dest);
					
			}
			i++;
		}
	}
	
	
	
	public final void renderBullets(int myRenderPassID) {
		int i=0;
		int tx;
		int ty;
		
		Bullets tmpBullet;
		
		while (i<Bullets.bulletList.length) {
			tmpBullet=Bullets.bulletList[i];
			
			if (!tmpBullet.deleted 
					&& !tmpBullet.died 
					&& tmpBullet.visible 
					&& tmpBullet.renderPass==myRenderPassID) {
				
					
				
					tx=tmpBullet.x-myWorld.worldOffsetX;
					ty=tmpBullet.y-myWorld.worldOffsetY;
					
					dest.set(tx,ty,tx+tmpBullet.w, ty+tmpBullet.h);
					src.set(tmpBullet.xOffset, tmpBullet.yOffset, tmpBullet.xOffset+tmpBullet.w, tmpBullet.yOffset+tmpBullet.h);
					drawBitmap(sprites[1],src,dest);
					
			}
			i++;
		}
	}	
	
	
	
	public final void renderFX(int myRenderPassID) {
		int i=0;
		int tx;
		int ty;
		
		FX tmpFX;
		
		while (i<FX.fxList.length) {
			tmpFX=FX.fxList[i];
			
			if (!tmpFX.deleted 
					&& !tmpFX.died 
					&& tmpFX.visible 
					&& tmpFX.renderPass==myRenderPassID) {
				
					
				
					tx=tmpFX.x-myWorld.worldOffsetX;
					ty=tmpFX.y-myWorld.worldOffsetY;
					
					setAlpha(tmpFX.alpha);
					if (tmpFX.myType==FX.fSPEECH) {
						renderText(Globals.dudeQuotes[tmpFX.subType],0,tx,ty,180,0,0);
					} else if (tmpFX.myType==FX.fSCOREPLUME) {
						renderText(Integer.toString(tmpFX.subType),0,tx,ty,180,0,6);
					} else {
						dest.set(tx,ty,tx+tmpFX.w, ty+tmpFX.h);
						src.set(tmpFX.xOffset, tmpFX.yOffset, tmpFX.xOffset+tmpFX.w, tmpFX.yOffset+tmpFX.h);
						drawBitmap(sprites[1],src,dest);
					}
					
			}
			i++;
		}
		
		setAlpha(255);
	}		
	
	
	
	public final void renderWeapon(Player tmpPlayer) { 
		int weaponYOffset=0;
		if (tmpPlayer.xOffset==10 || tmpPlayer.xOffset==40) weaponYOffset=1;

		int myX=tmpPlayer.x-myWorld.worldOffsetX+5;
		int myY=tmpPlayer.y-myWorld.worldOffsetY+5+weaponYOffset;
		int weaponID=0;
		
		if (tmpPlayer.myDirection==Globals.RIGHT) {
			if (tmpPlayer.actionDelay>0) {
				myX-=1;
				myY-=2;
				
				dest.set(myX,myY,myX+Globals.weaponValues[weaponID][2],myY+Globals.weaponValues[weaponID][3]);
				src.set( Globals.weaponValues[weaponID][0], Globals.weaponValues[weaponID][1],
						 Globals.weaponValues[weaponID][0]+Globals.weaponValues[weaponID][2], Globals.weaponValues[weaponID][1]+Globals.weaponValues[weaponID][3]);
				drawBitmap(sprites[1], src, dest);
			} else {
				// weapon on our back
				myX-=12;
				myY-=6;
				dest.set(myX,myY,myX+Globals.weaponValues[weaponID][2],myY+Globals.weaponValues[weaponID][3]);
				src.set( Globals.weaponValues[weaponID][0], Globals.weaponValues[weaponID][1],
						 Globals.weaponValues[weaponID][0]+Globals.weaponValues[weaponID][2], Globals.weaponValues[weaponID][1]+Globals.weaponValues[weaponID][3]);
				drawBitmapRotated(sprites[1], src, dest,-120);
			}
		} else {
			if (tmpPlayer.actionDelay>0) {
				myX+=1;
				myY-=2;
				
				dest.set(myX-Globals.weaponValues[weaponID][2],myY,myX,myY+Globals.weaponValues[weaponID][3]);
				src.set( Globals.weaponValues[weaponID][0],  (Globals.weaponValues[weaponID][1]+Globals.weaponValues[weaponID][3]),
						 Globals.weaponValues[weaponID][0]+Globals.weaponValues[weaponID][2], 
						 (Globals.weaponValues[weaponID][1]+Globals.weaponValues[weaponID][3])+Globals.weaponValues[weaponID][3]);
				drawBitmap(sprites[1], src, dest);
			} else {
				myX+=12;
				myY-=6;
				dest.set(myX-Globals.weaponValues[weaponID][2],myY,myX,myY+Globals.weaponValues[weaponID][3]);
				src.set( Globals.weaponValues[weaponID][0],  (Globals.weaponValues[weaponID][1]+Globals.weaponValues[weaponID][3]),
						 Globals.weaponValues[weaponID][0]+Globals.weaponValues[weaponID][2], 
						 (Globals.weaponValues[weaponID][1]+Globals.weaponValues[weaponID][3])+Globals.weaponValues[weaponID][3]);
				drawBitmapRotated(sprites[1], src, dest,120);
			}
			
		}

	}
	
	
	public final void renderStatusbar() {
		// render score
//		renderText(String.format("%08d", myPlayer.score),0, -1,16,80,0,0);
		
		// money collected?
		renderCredits();
		
		// render heart above player (blinking if just hit)
		if (myPlayer.visible && (myPlayer.healthDelay==0 || myPlayer.healthDelay%4<2) ) {
			tx=(myPlayer.x-myWorld.worldOffsetX);
			ty=(myPlayer.y-myWorld.worldOffsetY)-9+(myPlayer.healthDelay>>2);
			dest.set(tx,ty,tx+7,ty+7);
			src.set(17,96, 24,103);
			drawBitmap(sprites[1],src,dest);
			tx+=9;
			dest.set(tx,ty,tx+5,ty+7);
			src.set(156+(myPlayer.health*6), 0,161+(myPlayer.health*6), 7);
			drawBitmap(sprites[25],src,dest);
		}
	}

	
	public final void renderCredits() {
		tx=lowDisplayW-64;
		renderText( Integer.toString(myPlayer.creditsCollected), 0, tx,16,64,0,0);
		tx-=8;
		dest.set(tx,16,tx+7,23);
		src.set(17,68,24,75);
		drawBitmap(sprites[1],src,dest);
	}
	
	
	
	
	// [ LEVEL GENERATOR ]----------------------------------------------------------------------------
	
	public final void generateWorld() {
		Levelroom[]	myRooms = new Levelroom[96];

		int roomID=0;
		int nextY=0;
		int corridorHeight;
		int itterateCount;
		
		// create a list of rooms
		for (int i=0; i<myRooms.length; i++) myRooms[i]=new Levelroom();

		// add top-planet floor to world
		for (int x=0; x<World.tileMapW; x++) {
			myWorld.put(x, 1,World.tSOLID);
		}

		// make underworld part completely solid
		for (int x=0; x<World.tileMapW; x++) {
			for (int y=3; y<World.tileMapH; y++) {
				myWorld.put(x, y, World.tSOLID);
			}
		}
		
		
		// 
		myWorld.Spriteset=Mission.getSpriteset(myWorld.level);
		myWorld.difficulty=Mission.getDifficulty(myWorld.level);
		
		switch (myWorld.Spriteset) {
			case 1:
				sprites[2]=new Texture(Gdx.files.internal("t01.png"), true);
				setAmbientLight(0.1f, 0.1f, 0.2f, 1f);

			break;
			
			case 2:
				sprites[2]=new Texture(Gdx.files.internal("t02.png"), true);
				setAmbientLight(0.2f, 0.1f, 0.28f, 1f);
			break;

			case 3:
				sprites[2]=new Texture(Gdx.files.internal("t03.png"), true);
				setAmbientLight(0.1f, 0.3f, 0.18f, 1f);
			break;
		}
		
		
		// location of entry point to underworld
		int startX=14;
		int previousStartX=14;
		nextY=5;
		
		// create starting corridor (entry into the underworld)
		for (int y=0; y<=nextY; y++) {
			myWorld.put(startX, y, World.tEMPTY);
			myWorld.putRendermap(startX, y, 0);
		}
		nextY++;
		
		// add a solid crate to fall on in first room (so you can't fall on enemies)
		Monsters.addMonster(Monsters.mSOLIDSCENERY, startX<<4,(nextY+1)<<4, 0, myWorld);
		
		// start building the underworld
		while (nextY+4<World.tileMapH-4) {
			
			if (roomID!=0) {
				nextY+=2;
				itterateCount=24;
				while (Math.abs(startX-previousStartX)<4 && itterateCount>0) {
					startX=myRooms[roomID-1].x+Globals.getRandom(myRooms[roomID-1].width);
					itterateCount--;
				}
				
				if (itterateCount==0) break;
				
				// create corridor (entry into the underworld)
				corridorHeight=1+Globals.getRandom(3);
				
				for (int y=0; y<corridorHeight; y++) {
					myWorld.put(startX, nextY+y, World.tEMPTY);
					myWorld.putRendermap(startX, nextY+y, 0);
				}
				
				// put opening to corridor in floor texture of previous room
				myWorld.putRendermap(startX, myRooms[roomID-1].y+1, 11);
				
			
				nextY+=corridorHeight;
				
				// fill our previously created room with some inhabitants and pickups
				populateRoom(myRooms[roomID-1]);
			}
			
			
			
			
			// create room
			myRooms[roomID].width=8+Globals.getRandom(8);
			myRooms[roomID].height=2;
			myRooms[roomID].x=startX- Globals.getRandom(myRooms[roomID].width-2);
			if (myRooms[roomID].x<1) myRooms[roomID].x=1;
			if (myRooms[roomID].x+myRooms[roomID].width>World.tileMapW-1) myRooms[roomID].x=World.tileMapW-myRooms[roomID].width-1; 
			myRooms[roomID].y=nextY;
			
			// cut out the room from the solid underworld
			myWorld.putArea(myRooms[roomID].x,myRooms[roomID].y,myRooms[roomID].width,myRooms[roomID].height,World.tEMPTY);
			
			// texture the room
			myWorld.putAreaRendermap(myRooms[roomID].x,myRooms[roomID].y,myRooms[roomID].width,1, 2);
			myWorld.putAreaRendermap(myRooms[roomID].x,myRooms[roomID].y+1,myRooms[roomID].width,1, 10);
			myWorld.putRendermap(myRooms[roomID].x,myRooms[roomID].y,1);
			myWorld.putRendermap(myRooms[roomID].x,myRooms[roomID].y+1,9);
			
			

			
			// randomly add light-tiles to corridor + floors
			itterateCount=myRooms[roomID].width>>2;
			while (itterateCount>0) {
				// add lights into the wall textures at random spots
				myWorld.putRendermap(myRooms[roomID].x+1+Globals.getRandom(myRooms[roomID].width-2), myRooms[roomID].y, 3);
				
				// add lights on the floor at random spots
				if (myWorld.Spriteset==2)
					myWorld.putRendermap(myRooms[roomID].x+1+Globals.getRandom(myRooms[roomID].width-2), myRooms[roomID].y+1, 12);
				
				itterateCount--;
			}
			
			
			// randomly add "pilar" tiles (if nothing else was placed)
			itterateCount=myRooms[roomID].width>>2;
			while (itterateCount>0) {
				tx=Globals.getRandom(myRooms[roomID].width-2);
				
				if (myWorld.getRendermap(myRooms[roomID].x+1+tx, myRooms[roomID].y)==2
					|| myWorld.getRendermap(myRooms[roomID].x+1+tx, myRooms[roomID].y)==10) {
						myWorld.putRendermap(myRooms[roomID].x+1+tx, myRooms[roomID].y, 5);
						myWorld.putRendermap(myRooms[roomID].x+1+tx, myRooms[roomID].y+1, 13);
				}
				
				itterateCount--;
			}
						
			
			roomID++;		
			previousStartX=startX;
			
		}
		
		
		// add planet core to last room
		itterateCount=myRooms[roomID-1].width;
		tx=myRooms[roomID-1].x+1+Globals.getRandom(myRooms[roomID-1].width-2);
		while (itterateCount>0) {
			ty=myRooms[roomID-1].y+1;
			if (!myWorld.isSolid(tx, ty) && myWorld.isSolid(tx, ty+1)) {
				Monsters.addMonster(Monsters.mPLANETCORE, tx<<4,ty<<4, 0, myWorld);
				itterateCount=0;
			} else {
				tx++;
				if (tx>myRooms[roomID].x+myRooms[roomID].width) tx=myRooms[roomID].x+1;
			}
			itterateCount--;
		}
		
		// clear markers
		myWorld.clearType(World.tMARKER);
		
		
		myPlayer.init( 48, lowDisplayH-32);
		myWorld.setCamera(0, -256);
		

		
		// Add the player ship, triggering the player's drop
		Monsters.addMonster(Monsters.mSHIP, 48, (-3)<<4, 0, myWorld);
		Monsters.addMonster(Monsters.mSCENERY, (14<<4)-8, 8, 0, myWorld);
		

	}
	
	
	
	public final void populateRoom(Levelroom myRoom) {
		// add metal crates randomly
		int itterateCount=myRoom.width>>3;
		while (itterateCount>0) {
			tx=myRoom.x+1+Globals.getRandom(myRoom.width-2);
			ty=myRoom.y+1;
			if (!myWorld.isSolid(tx, ty) && myWorld.isSolid(tx, ty+1)) {
				Monsters.addMonster(Monsters.mSOLIDSCENERY, tx<<4,ty<<4, 0, myWorld);
			}
			itterateCount--;
		}
		
		// add health
		itterateCount=Globals.getRandom(myRoom.width>>2);
		while (itterateCount>0) {
			tx=myRoom.x+1+Globals.getRandom(myRoom.width-2);
			ty=myRoom.y;
			if (!myWorld.isSolid(tx, ty)) {
				Monsters.addMonster(Monsters.mHEALTH, tx<<4,ty<<4, 0, myWorld);
			}
			itterateCount--;
		}		
		
		
		
		// add some monsters
		itterateCount=myRoom.width>>2;
		int monsterType;
		
		int maxDifficulty=8*myWorld.difficulty;
		
		while (itterateCount>0 && maxDifficulty>0) {
			
			monsterType=Globals.getRandom(3);
			
			tx=myRoom.x+1+Globals.getRandom(myRoom.width-2);
			ty=myRoom.y+1;

			if (!myWorld.isSolid(tx, ty) && myWorld.isSolid(tx, ty+1)) {
			
				switch (monsterType) {
					case 0: 
						if (myWorld.difficulty<3 || Globals.getRandom(100)>90) { 
							Monsters.addMonster(Monsters.mWORM, tx<<4,ty<<4, 0, myWorld);
							maxDifficulty--;
						}
					break;
					
					case 1:
						if (myWorld.difficulty>1 || Globals.getRandom(100)>90) {
							Monsters.addMonster(Monsters.mROBOEYE, tx<<4,ty<<4, 0, myWorld);
							maxDifficulty-=2;
						}
					break;
					
					case 2:
						if (myWorld.difficulty>1 || Globals.getRandom(100)>90) {
							Monsters.addMonster(Monsters.mBOUNCEXPLODE, tx<<4,ty<<4, 0, myWorld);
							maxDifficulty-=2;
						}
					break;
					
				}
			}
			itterateCount--;
		}			
	}
}
