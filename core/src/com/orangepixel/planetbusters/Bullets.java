package com.orangepixel.planetbusters;

import com.orangepixel.utils.FLight;

public class Bullets extends Entity{

	
	public final static int	bDEFAULTBULLET = 0,
							bEXPLOSION = 1; 
	
	
	// bullet-owner types
	public final static int bOWNER_ANYONE = 0,
							bOWNER_PLAYER = 1,
							bOWNER_MONSTER = 2;
	
	
	static Bullets[] bulletList=new Bullets[320];

	
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
	int		myOwner;
	int		energy;
	int		duration;
	int		subType;
	int		myType;
	int		aiState;
	int		aiCountdown;
	int		renderPass;
	
	boolean	died;
	boolean	deleted;
	
	
	
	public final static void initBullets() {
		for (int i=bulletList.length - 1; i >= 0; i--) bulletList[i]=new Bullets();
		killAll();
	}	
	
	
	public final static void killAll() {
		for (int i=bulletList.length - 1; i >= 0; i--) bulletList[i].deleted=true;
	}
	
	

	
	public final static int addBullets(int mType, int myX, int myY, int mSubType, int mDirection, int mOwner, World myWorld) {
		int i=0;
		while (i<bulletList.length && !bulletList[i].deleted) i++;
		if (i<bulletList.length) {
			bulletList[i].init(mType, myX, myY, mSubType, mDirection,mOwner,myWorld);
			return i;
		}
		return -1;
	}	
	
	
	
	
	
	public final static void updateBullets(World myWorld, Player myPlayer) {
		int i=0;
		
		while (i<bulletList.length) {
			if (!bulletList[i].deleted && !bulletList[i].died) {
				bulletList[i].update(myWorld,myPlayer);
				
				// check collisions with monsters
				for (int m=0; m<Monsters.monsterList.length; m++) {
					if (!Monsters.monsterList[m].deleted && !Monsters.monsterList[m].died && bulletList[i].collidesWith(Monsters.monsterList[m])) {
						if (bulletList[i].myOwner==Bullets.bOWNER_PLAYER) {
							if (Monsters.monsterList[m].hit(bulletList[i],myWorld,myPlayer)) {
								bulletList[i].died=true;
							}
						}
					}
				}	
				
				if (bulletList[i].died) {
					// monster died
					FX.addFX(FX.fBULLETPUFF, bulletList[i].x-2, bulletList[i].y-2, 0, myWorld);
					bulletList[i].deleted=true;
				}
				
			}
			i++;
		}
	}		
	
	
	
	public final void init(int mType, int myX, int myY, int mSubType, int mDirection, int mOwner, World myWorld) {
		deleted=false;
		died=false;
		
		subType=mSubType;
		myType=mType;
		myOwner=mOwner;
		
		x=myX;
		y=myY;
		
		visible=true;
		renderPass=1;
		
		switch (myType) {
			case bDEFAULTBULLET:	
				xOffset=0;
				yOffset=49;
				w=6;
				h=3;
				
				if (mDirection==Globals.RIGHT) {
					xSpeed=48;
					x+=6;
				} else if (mDirection==Globals.LEFT) {
					xSpeed=-48;
					x-=12;
				}
				
				
				// threeway? subtype will be -1 or +1
				ySpeed=(subType*16);
				
				if (ySpeed==0) ySpeed=Globals.getRandom(8)-4;
				
				duration=64;
				
				if (myOwner==bOWNER_PLAYER) energy=4;
				else energy=1;
			break;
			
			
			case bEXPLOSION:
				w=subType;
				h=subType;
				x-=(w>>1);
				y-=(h>>1);
				energy=3;
				visible=false;
				aiCountdown=3;
			break;
			
		}
		
		floatX=x<<4;
		floatY=y<<4;		
	}
	
	
	public final void update(World myWorld, Player myPlayer) {
		boolean hitPlayer=false;
		if (myPlayer.x+8>=x && myPlayer.x+1<x+w && myPlayer.y+8>=y && myPlayer.y+1<y+h) hitPlayer=true;

		switch (myType) {
			case bDEFAULTBULLET:
				floatX+=xSpeed;
				floatY+=ySpeed;
				
				x=floatX>>4;
				y=floatY>>4;
		
				if (duration>0) duration--;
				else died=true;
				
				if (myWorld.isSolid(x>>4, y>>4)) {
					for (int i=4; --i>=0;) {
						FX.addFX(FX.fDEBRI, x+Globals.getRandom(3), y, 0, myWorld);
					}
					died=true;
				}
				FLight.addLight(x+3-myWorld.worldOffsetX, y+1-myWorld.worldOffsetY, 48, FLight.LightType_Sphere, 255,155,12,128);
				
				if (myOwner==bOWNER_MONSTER || myOwner==bOWNER_ANYONE) {
					if (hitPlayer) myPlayer.hit();
				}
			break;
			
			case bEXPLOSION:

				if (myOwner==bOWNER_MONSTER && hitPlayer) {
					myPlayer.hit();
					if (myPlayer.x+5<x+(w>>1)) myPlayer.throwBack(Globals.LEFT);
					else myPlayer.throwBack(Globals.RIGHT);
				}
				
				if (aiCountdown>0) aiCountdown--;
				else died=true;
			break;	
		}
		
	}

	
	
	
	public final boolean collidesWith(Monsters myMonster) {
		boolean collide=false;

		if (myOwner!=bOWNER_ANYONE && myOwner==bOWNER_MONSTER) return false;
		int cw=x+w;
		int ch=y+h;
		
		if ((myMonster.x<=cw) && (myMonster.x+myMonster.w>=x) && (myMonster.y<=ch) && (myMonster.y+myMonster.h>=y)) collide=true;

		return collide;
	}		
	
}
