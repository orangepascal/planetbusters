package com.orangepixel.planetbusters;

import com.orangepixel.utils.FLight;

public class World {
	
	public final static int	tileMapW = 40;
	public final static int	tileMapH = 40;

	public final static int	tEMPTY = 0,
							tSOLID = 1,
							tMONSTER=2,
							tMARKER=3;

	int worldOffsetX;
	int worldOffsetY;
	
	int worldFloorY;
	
	int worldShake;
	int worldShakeOffsetX;
	int worldShakeOffsetY;
	
	int			Spriteset;
	
	int			lockScreen;
	boolean		lockVertical;
	boolean		CameraTakeOver;	// if something else besides player takes focus
	boolean		CameraIsView;	// signal player we are back on camera-position checking
	int			CameraTakeOverCountdown;
	int			lockVerticalValue;
	int			softLock;
	boolean		autoScroll;
	int			cameraTargetX;
	int			cameraTargetY;	
	
	int			difficulty;
	
	boolean		levelCompleted;
	
	
	int displayW;
	int displayH;
	
	
	int[] 	tileMap = new int[tileMapW*tileMapH];
	int[]	renderMap = new int[tileMapW*tileMapH];
	
	int			worldPre;
	int			worldPost;
	int			world;	// galaxy, increases with every X missions completed
	int			level;
	
	
	public final void initWorld() {
		for (int x=0; x<tileMapW; x++) {
			for (int y=0; y<tileMapH; y++) {
				put(x,y,0);
			}
		}
		
		levelCompleted=false;
	}
	
	
	
	public final void initNewGame(int mDisplayW, int mDisplayH) {
		displayW=mDisplayW;
		displayH=mDisplayH;
		
		CameraTakeOver=false;
		
		world=1;
		createGalaxy();
		
		
		worldFloorY=displayH-22;
	}
	
	
	public final void createGalaxy() {
		worldPre=Globals.getRandom(Globals.galaxyNamePre.length);
		worldPost=Globals.getRandom(Globals.galaxyNamePost.length);

		for (int i=0; i<Mission.missionList.length; i++) {
			Mission.set(i, 1+Globals.getRandom(3), world+(i+1), false, (world*5)+(Globals.getRandom(10)*5));
		}
	}
	
	public final void clearType(int myTile) {
		for (int x=0; x<tileMapW; x++) {
			for (int y=0; y<tileMapH; y++) {
				if (tileMap[x+(y*tileMapW)]==myTile) tileMap[x+(y*tileMapW)]=tEMPTY; 
			}
		}
	}
	
	public final int getTile (int myX, int myY) {
		return tileMap[myX+(myY*tileMapW)];
	}
	
	public final void put (int myX, int myY, int myTile) {
		tileMap[myX+(myY*tileMapW)]=myTile;
	}
	
	public final void putArea(int myX, int myY, int myW, int myH, int myTile) {
		for (int x=myX; x<myX+myW; x++) {
			for (int y=myY; y<myY+myH; y++) {
				put(x,y,myTile);
			}
		}
	}

	public final int getRendermap(int myX, int myY) {
		return renderMap[myX+(myY*tileMapW)];
	}
	
	public final void putRendermap(int myX, int myY, int myTile) {
		renderMap[myX+(myY*tileMapW)]=myTile;
	}
	
	public final void putAreaRendermap(int myX, int myY, int myW, int myH, int myTile) {
		for (int x=myX; x<myX+myW; x++) {
			for (int y=myY; y<myY+myH; y++) {
				putRendermap(x,y,myTile);
			}
		}
	}	
	
	
	public final boolean isSolid(int myX, int myY) {
		if (myY<0) return false;
		if (myX<0 || myX>tileMapW || myY>tileMapH) return true;
		if (tileMap[myX+(myY*tileMapW)]>=tSOLID) return true;
		
		return false;
	}
	
	
	public final void setCameraTakeOver(int myx,int myy,int duration) {
		CameraTakeOver=true;
		CameraTakeOverCountdown=duration;
		cameraTargetX=myx;
		cameraTargetY=myy;
	}
	
	public final void setCamera(int x, int y) {
		cameraTargetX=worldOffsetX=x;
		cameraTargetY=worldOffsetY=y;
	}
	
	public final void handleCamera(Player tmpPlayer) {
		int tx;
		int ty;

		if (CameraTakeOver) {
			int xSpeed=((cameraTargetX- (displayW>>1)) - worldOffsetX) >> 3;
			int ySpeed=((cameraTargetY- (displayH>>1)) - worldOffsetY) >> 3;
			
			worldOffsetX+=xSpeed;
			worldOffsetY+=ySpeed;
		} else {
				
			tx=tmpPlayer.x+8;
			ty=tmpPlayer.y+10;
			
			int xSpeed=((tx- (displayW>>1)) - worldOffsetX) >> 3;
			int ySpeed=((ty- (displayH>>1)) - worldOffsetY) >> 3;
			
			worldOffsetX+=xSpeed;
			worldOffsetY+=ySpeed;
		}
		
		// correct stuff
		if (worldOffsetX<0) worldOffsetX=0;
		if (worldOffsetX>(tileMapW*16)-displayW) worldOffsetX=(tileMapW*16)-displayW;
//		if (worldOffsetY<0) worldOffsetY=0;
		if (worldOffsetY>(tileMapH*16)-displayH) worldOffsetY=(tileMapH*16)-displayH;
	}	
	
	
	
	
	public final void update() {
		if (worldShake>0) worldShake--;
		if (CameraTakeOverCountdown>0) CameraTakeOverCountdown--;
		else CameraTakeOver=false;

		worldShakeOffsetY=0;
		worldShakeOffsetX=0;
		if (worldShake>0) {
			if (worldShake>24) {
				worldShakeOffsetY=Globals.getRandom(8)-4;
				worldShakeOffsetX=Globals.getRandom(8)-4;
			} else if (worldShake>12) {
				worldShakeOffsetY=Globals.getRandom(4)-2;
				worldShakeOffsetX=Globals.getRandom(4)-2;
			} else {
				worldShakeOffsetY=Globals.getRandom(2)-1;
				worldShakeOffsetX=Globals.getRandom(2)-1;
			}
		}	
		
		worldOffsetX+=worldShakeOffsetX;
		worldOffsetY+=worldShakeOffsetY;
	
		// place lights based on tiles
		int tx;
		int ty;
		
		tx=-worldOffsetX;
		for (int x=0; x<World.tileMapW; x++) {
			ty=-worldOffsetY;
			for (int y=0; y<World.tileMapH; y++) {
				
				// only add lights that are near the visible window
				if (tx>=-128 && ty>=-128 && tx<worldOffsetX+displayW+128 && ty<worldOffsetY+displayH+128) {
					
					switch(renderMap[x+(y*World.tileMapW)]) {
						case 3:	// wall light
							switch (Spriteset) {
								case 1:
									FLight.addLight((x<<4)+8-worldOffsetX,(y<<4)+10-worldOffsetY,12,FLight.LightType_SphereTense,117,221,255, 255);
									FLight.addLight((x<<4)+8-worldOffsetX,(y<<4)+10-worldOffsetY,48,FLight.LightType_Sphere,29,175,243,255);
								break;
								
								case 2:
									FLight.addLight((x<<4)+8-worldOffsetX,(y<<4)+10-worldOffsetY,12,FLight.LightType_SphereTense,238,122,255, 255);
									FLight.addLight((x<<4)+8-worldOffsetX,(y<<4)+10-worldOffsetY,48,FLight.LightType_Sphere,207,45,231,255);
								break;

								case 3:
									FLight.addLight((x<<4)+8-worldOffsetX,(y<<4)+10-worldOffsetY,12,FLight.LightType_SphereTense,219,249,171, 255);
									FLight.addLight((x<<4)+8-worldOffsetX,(y<<4)+10-worldOffsetY,48,FLight.LightType_Sphere,202,253,121,255);
								break;
							}
						break;
						
						case 12: // floor lights
							switch (Spriteset) {
								case 1:
									FLight.addLight((x<<4)+4-worldOffsetX,(y<<4)+12-worldOffsetY,6,FLight.LightType_SphereTense,255,223,46, 255);
									FLight.addLight((x<<4)+11-worldOffsetX,(y<<4)+9-worldOffsetY,6,FLight.LightType_SphereTense,255,223,46, 255);
									FLight.addLight((x<<4)+9-worldOffsetX,(y<<4)+15-worldOffsetY,6,FLight.LightType_SphereTense,255,223,46, 255);

									FLight.addLight((x<<4)+8-worldOffsetX,(y<<4)+8-worldOffsetY,32,FLight.LightType_Sphere,255,223,46,255);
								break;
								
								case 2:
									FLight.addLight((x<<4)+7-worldOffsetX,(y<<4)+12-worldOffsetY,12,FLight.LightType_SphereTense,238,122,255, 255);
									FLight.addLight((x<<4)+7-worldOffsetX,(y<<4)+12-worldOffsetY,48,FLight.LightType_Sphere,207,45,231,255);
								break;
								
								case 3:
									FLight.addLight((x<<4)+8-worldOffsetX,(y<<4)+10-worldOffsetY,12,FLight.LightType_SphereTense,219,249,171, 255);
									FLight.addLight((x<<4)+8-worldOffsetX,(y<<4)+10-worldOffsetY,48,FLight.LightType_Sphere,202,253,121,255);
								break;
							}
						break;
					}
					
				}
				ty+=16;
			}
			tx+=16;
		}		
	
	}
	
}
