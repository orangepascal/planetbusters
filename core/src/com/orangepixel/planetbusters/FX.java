package com.orangepixel.planetbusters;

import com.orangepixel.utils.FLight;

public class FX extends Entity {

	
	public final static int	fSPEECH = 0,
							fLANDPLUME = 1,
							fBULLETPUFF = 2,
							fCOREELECTRO = 3,
							fDEBRI = 4,
							fSCOREPLUME = 5,
							fEXPLOSION = 6;
	
	
	
	// we got crazy on FX in this one, so large enough array needed ;)
	static FX[] fxList=new FX[1024];

	
	// position
	int		floatX;
	int		floatY;
	int		xSpeed;
	int		ySpeed;
	int		myDirection;
	int		startX;
	int		startY;
	int		targetX;
	int		targetY;
	boolean	onGround;
	
	
	
	
	// appearance
	int		xOffsetAdd;
	int		animationDelay;
	int		actionDelay;
	boolean visible;
	
	// states
	int		subType;
	int		myType;
	int		aiState;
	int		aiCountdown;
	int		renderPass;
	
	boolean	died;
	boolean	deleted;
	
	
	
	public final static void initFX() {
		for (int i=fxList.length - 1; i >= 0; i--) fxList[i]=new FX();
		killAll();
	}
	
	public final static void killAll() {
		for (int i=fxList.length - 1; i >= 0; i--) fxList[i].deleted=true;
	}

	
	
	
	public final static int addFX(int mType, int myX, int myY, int mSubType, World myWorld) {
		int i=0;
		while (i<fxList.length && !fxList[i].deleted) i++;
		if (i<fxList.length) {
			fxList[i].init(mType, myX, myY, mSubType, myWorld);
			return i;
		}
		return -1;
	}
	
	
	
	
	public final static void updateFX(World myWorld, Player myPlayer) {
		int i=0;
		
		while (i<fxList.length) {
			if (!fxList[i].deleted && !fxList[i].died) {
				fxList[i].update(myWorld,myPlayer);
				
				if (fxList[i].died || fxList[i].y<myWorld.worldOffsetY-128)  {
					// monster died
					fxList[i].deleted=true;
				}
				
			}
			i++;
		}
	}	
	
	
	
	public final void init(int mType, int myX, int myY, int mSubType, World myWorld) {
		deleted=false;
		died=false;
		
		subType=mSubType;
		myType=mType;
		
		visible=true;
		renderPass=1;
		alpha=255;

		x=myX;
		y=myY;
		
		switch (myType) {
			case fSPEECH:
				aiCountdown=128;
				ySpeed=-2;
				renderPass=9;
			break;
			
			case fLANDPLUME:
				w=22;
				h=3;
				animationDelay=4;
				xOffset=0;
				yOffset=64;
			break;

			case fBULLETPUFF:
				w=4;
				h=4;
				xOffset=7;
				yOffset=48;
			break;
			
			case fCOREELECTRO:
				w=10;
				h=5;
				xOffset=25+(Globals.getRandom(2)*11);
				yOffset=47;
				animationDelay=2;
			break;
			
			case fDEBRI:
				w=3;
				h=3;
				xOffset=30+(Globals.getRandom(4)*4);
				yOffset=53;
				
				xSpeed=(Globals.getRandom(4)-2)<<4;
				ySpeed=(Globals.getRandom(6)-4)<<4;
				aiCountdown=64;
			break;
			
			case fSCOREPLUME:
				ySpeed=-48;
				aiCountdown=128;
				renderPass=9;
			break;
			
			case fEXPLOSION:
				w=16;
				h=16;
				xOffset=48;
				yOffset=48;
				aiCountdown=64;
			break;
		}
		
		floatX=x<<4;
		floatY=y<<4;
	}
	
	
	
	
	public final void update(World myWorld, Player myPlayer) {
		int tx;
		int ty;
		
		switch (myType) {
			case fSPEECH:
				floatY+=ySpeed;
				y=floatY>>4;
				if (aiCountdown>0) aiCountdown--;
				else died=true;
			break;
			
			case fLANDPLUME:
				if (animationDelay>0) animationDelay--;
				else {
					animationDelay=4;
					xOffset+=22;
					if (xOffset>66) died=true;
				}
			break;
			
			case fBULLETPUFF:
				if (animationDelay>0) animationDelay--;
				else {
					animationDelay=2;
					xOffset+=4;
					if (xOffset>19) died=true;
				}
			break;
			
			case fCOREELECTRO:
				if (animationDelay>0) animationDelay--;
				else died=true;
			break;
			
			case fDEBRI:
				floatX+=xSpeed;
				x=floatX>>4;

				if (myWorld.isSolid(x>>4, y>>4)) {
					if (xSpeed<0) {
						x=((x>>4)<<4)+16;
						floatX=x<<4;
						xSpeed=-xSpeed>>1;
					} else {
						x=((x>>4)<<4)-w;
						floatX=x<<4;
						xSpeed=-xSpeed>>1;
					}
					
				}
				
				floatY+=ySpeed;
				y=floatY>>4;
				if (ySpeed<64) ySpeed+=6;
				
				tx=(x>>4);
				if (ySpeed<0) {
					ty=(y>>4);
					if (myWorld.isSolid(tx,ty)) {
						y=(ty<<4)+16;
						floatY=y<<4;
						ySpeed=0;
					}
				} else {
					ty=(y+h+4)>>4;
					if (myWorld.isSolid(tx,ty)) {
						y=(ty<<4)-h-3;
						floatY=y<<4;
						ySpeed=-(ySpeed>>1);
						if (ySpeed>=-2) ySpeed=0;
						xSpeed=xSpeed>>1;
						if (xSpeed>-2 && xSpeed<2) xSpeed=0;
					}
				}
			break;
			
			case fSCOREPLUME:
				floatY+=ySpeed;
				y=floatY>>4;
				ySpeed+=4;
				if (ySpeed>=0) ySpeed=0;
				
				if (aiCountdown>0) aiCountdown--;
				else died=true;
				
				alpha-=2;
				if (alpha<=0) {
					alpha=0;
					died=true;
				}
			break;
			
			case fEXPLOSION:
				if (aiCountdown>0) aiCountdown--;
				
				if (animationDelay>0) animationDelay--;
				else {
					animationDelay=2;
					xOffset+=16;
					if (xOffset>144) died=true;
				}
				FLight.addLight(x+8-myWorld.worldOffsetX, y+8-myWorld.worldOffsetY, 64, FLight.LightType_Sphere, 255,128,0,160+aiCountdown);
			break;
					
		}
		
	}
	
	
	


	

}
