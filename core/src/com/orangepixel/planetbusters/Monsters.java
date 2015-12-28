package com.orangepixel.planetbusters;

import com.orangepixel.utils.FLight;

public class Monsters extends Entity {

	
	public final static int	mSHIP = 0,
							mSCENERY = 1,
							mSOLIDSCENERY = 2,
							mWORM = 3,
							mCREDIT = 4,
							mPLANETCORE = 5,
							mROBOEYE = 6,
							mHEALTH = 7,
							mBOUNCEXPLODE = 8;

	
	
	static Monsters[] monsterList=new Monsters[320];

	
	// position
	int		floatX;
	int		floatY;
	int		xSpeed;
	int		ySpeed;
	int		ySpeedIncrease;
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
	boolean	activated;
	int		energy;
	int		subType;
	int		myType;
	int		aiState;
	int		aiCountdown;
	int		renderPass;
	int		fireDelay;
	
	boolean	died;
	boolean	deleted;
	
	
	
	public final static void initMonsters() {
		for (int i=monsterList.length - 1; i >= 0; i--) monsterList[i]=new Monsters();
		killAll();
	}
	
	
	public final static void killAll() {
		for (int i=monsterList.length - 1; i >= 0; i--) monsterList[i].deleted=true;
	}
	
	
	
	public final static int addMonster(int mType, int myX, int myY, int mSubType, World myWorld) {
		int i=0;
		while (i<monsterList.length && !monsterList[i].deleted) i++;
		if (i<monsterList.length) {
			monsterList[i].init(mType, myX, myY, mSubType, myWorld);
			return i;
		}
		return -1;
	}
	
	
	
	
	public final static void updateMonsters(World myWorld, Player myPlayer) {
		int i=0;
		
		while (i<monsterList.length) {
			if (!monsterList[i].deleted && !monsterList[i].died) {
				monsterList[i].update(myWorld,myPlayer);
				
				if (monsterList[i].died) {
					// monster died
					monsterList[i].deleted=true;
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
		aiState=0;
		
		visible=true;
		renderPass=1;
		
		activated=false;
		
		x=myX;
		y=myY;
		
		fireDelay=0;
		
		switch (myType) {
			case mSHIP:
				xOffset=0;
				yOffset=33;
				w=29;
				h=15;
				xSpeed=0;
				ySpeed=0;

				targetX=myX<<4;
				targetY=myY<<4;

				x=myWorld.worldOffsetX-128;
				y=myWorld.worldOffsetY+myWorld.displayH-64;
				
				startX=x;
				startY=y;
			break;
			
			case mSCENERY:
				// hole in the floor
				w=28;
				h=9;
				xOffset=0;
				yOffset=53;
			break;
			
			case mSOLIDSCENERY:
				// "metal crate thingies"
				w=16;
				h=16;
				xOffset=0;
				yOffset=68;
				aiState=0;
				h=13;
				
				energy=32+(myWorld.difficulty*4);
			break;
			
			case mWORM:
				w=12;
				h=9;
				y+=4;
				xOffset=0+Globals.getRandom(2)*13;
				yOffset=82;
				aiState=0;
				aiCountdown=Globals.getRandom(64);
				animationDelay=4;
				if (Globals.getRandom(100)>50) myDirection=Globals.RIGHT;
				else myDirection=Globals.LEFT;
				
				energy=4+(1*myWorld.difficulty);
			break;
			
			
			case mCREDIT:
				w=7;
				h=7;
				xOffset=17;
				yOffset=68;
				ySpeed=-24;
				aiState=0;
			break;
			
			case mPLANETCORE:
				w=16;
				h=15;
				xOffset=24;
				yOffset=68;
				aiState=0;
				y-=6;
			break;
			
			case mROBOEYE:
				w=10;
				h=10;
				y+=3;
				xOffset=32+(Globals.getRandom(2)*10);
				yOffset=21;
				
				if (Globals.getRandom(100)>50) myDirection=Globals.RIGHT;
				else myDirection=Globals.LEFT;
				
				energy=2+(3*myWorld.difficulty);
			break;
			
			
			case mHEALTH:
				w=5;
				h=7;
				y+=7;
				
				xOffset=40;
				yOffset=68;
				ySpeed=-8;
				ySpeedIncrease=-4;
			break;
			
			case mBOUNCEXPLODE:
				w=10;
				h=10;
				y-=7;
				xOffset=62;
				yOffset=21;
				ySpeed=-8;
				
				energy=2+(myWorld.difficulty);
				aiState=0;
			break;
		}
		
		floatX=x<<4;
		floatY=y<<4;
	}
	
	
	
	
	public final void update(World myWorld, Player myPlayer) {
		int tx;
		int ty;
		boolean onScreen;
		
		if (x+w>myWorld.worldOffsetX-16 && x<myWorld.worldOffsetX+myWorld.displayW+16 
			&& y+h>myWorld.worldOffsetY-16 && y<myWorld.worldOffsetY+myWorld.displayH+16) onScreen=true;
		else onScreen=false;
		
		if (!onScreen && myType!=mSHIP) return;
		
		
		boolean hitPlayer=false;
		if (myPlayer.x+8>=x && myPlayer.x+1<x+w && myPlayer.y+8>=y && myPlayer.y+1<y+h) hitPlayer=true;
		
		switch (myType) {
			case mSHIP:
				switch (aiState) {
					case 0:
						x=myWorld.worldOffsetX-128;
						y=myWorld.worldOffsetY;
						myWorld.setCamera(x, y);
						myWorld.setCameraTakeOver(x, y, 16);
						
						startX=x;
						startY=y;
						floatX=x<<4;
						floatY=y<<4;
						aiState=1;
					break;
					
					
					case 1:
						// fly in
						xSpeed=(targetX-floatX)>>2;
						ySpeed=(targetY-floatY)>>2;
		
						if (xSpeed>48) xSpeed=48;
						if (ySpeed>48) ySpeed=48;
		
						floatX+=xSpeed;
						floatY+=ySpeed;
						
						x=floatX>>4;
						y=floatY>>4;
						
						myWorld.setCameraTakeOver(x, y, 16);
						
						if (floatX>=targetX-3 && floatY>=targetY-3) {
							aiState=2;
							myPlayer.BustAPLanet(x,y);
						}
						
					break;
					
					case 2:
						// hover - idle
					break;
				}
				
				FLight.addLight(x-1-myWorld.worldOffsetX, y+7-myWorld.worldOffsetY, 8, FLight.LightType_SphereTense, 255,207,17,255);
				FLight.addLight(x-1-myWorld.worldOffsetX, y+7-myWorld.worldOffsetY, 64, FLight.LightType_Sphere, 255,174,17,255);
			break;
			
			case mSOLIDSCENERY:
				myWorld.put(x>>4, y>>4, World.tMONSTER);
			break;
			
			case mWORM:
				switch (aiState) {
					case 0:
						if (aiCountdown<=8) xOffset=13;
						if (aiCountdown>0) aiCountdown--;
						else {
							aiState=1;
							if (myDirection==Globals.RIGHT) xSpeed=32;
							else xSpeed=-32;
							xOffset=0;
						}
					break;
					
					case 1:
						doHorizontal(myWorld);
						
						xSpeed=xSpeed>>1;
						if (xSpeed>-2 && xSpeed<2) {
							xSpeed=0;
							aiState=0;
							aiCountdown=16;
						}
						
						
						ty=(y>>4);
						if (xSpeed<0) {
							tx=(x>>4);
							if (myWorld.isSolid(tx, ty)) {
								x=(tx<<4)+16;
								floatX=x<<4;
								xSpeed=0;
								aiState=0;
								aiCountdown=16;
								myDirection=Globals.RIGHT;
							}
						} else {
							tx=(x+w)>>4;
							if (myWorld.isSolid(tx, ty)) {
								x=(tx<<4)-w;
								floatX=x<<4;
								xSpeed=0;
								
								aiState=0;
								aiCountdown=16;
								myDirection=Globals.LEFT;
							}							
						}
						
						if (hitPlayer) myPlayer.hit();
					break;
					
				}
			break;
			
			case mCREDIT:
				switch (aiState) {
					case 0:
						floatY+=ySpeed;
						y=floatY>>4;
									
						if (ySpeed<64) ySpeed+=8;
						ty=(y+h+4)>>4;
						if (myWorld.isSolid(x>>4, ty)) {
							y=(ty<<4)-(h+4);
							floatY=y<<4;
							ySpeed=0;
							aiState=1;
							aiCountdown=200;
						}
					break;
					
					case 1:
						if (aiCountdown>0) aiCountdown--;
						else {
							died=true;
						}
						
						visible=(aiCountdown>16 || aiCountdown%4<2);
						
						if (hitPlayer) {
							myPlayer.addCredits(5);
							FX.addFX(FX.fSCOREPLUME, x,y, 5, myWorld);
							died=true;
						}
					break;
				}
				
				FLight.addLight(x+3-myWorld.worldOffsetX, y+3-myWorld.worldOffsetY, 64, FLight.LightType_Sphere, 255,206,12,128);
			break;
			
			case mPLANETCORE:
				FLight.addLight(x+8-myWorld.worldOffsetX, y+7-myWorld.worldOffsetY, 24, FLight.LightType_SphereTense, 100,205,244,200);
				FLight.addLight(x+8-myWorld.worldOffsetX, y+7-myWorld.worldOffsetY, 80, FLight.LightType_Sphere, 100,205,244,128);
				FX.addFX(FX.fCOREELECTRO, x+3, y-4, 0, myWorld);
				
				if (hitPlayer && myPlayer.xSpeed==0 && myPlayer.onGround) myWorld.levelCompleted=true;
			break;
			
			case mROBOEYE:
				if (aiState==0 || aiState==2) {
					// idle animation
					if (animationDelay>0) animationDelay--;
					else {
						animationDelay=8;
						if (xOffset==32) xOffset=42;
						else xOffset=32;
					}					
				}
				
				
				
				switch (aiState) {
					case 0:
						if (playerInOurSight(myPlayer)) {
							activated=true;
							
							targetX=x;
							if (x<myPlayer.x-24) targetX=myPlayer.x-24;
							else if (x>myPlayer.x+24) targetX=myPlayer.x+24;
							
							if (targetX>x) {
								xSpeed=16;
								myDirection=Globals.RIGHT;
							} else if (targetX<x) {
								xSpeed=-16;
								myDirection=Globals.LEFT;
							}
							aiState=1;
						}
						
						if (aiCountdown>0) aiCountdown--;
						else {
							if (myDirection==Globals.LEFT) myDirection=Globals.RIGHT;
							else myDirection=Globals.LEFT;
							aiCountdown=80;
						}
					break;
					
					
					case 1:
						doHorizontal(myWorld);
						

						if (xSpeed==0 || (x>targetX-4 && x<targetX+4)) {
							xSpeed=0;
							aiState=0;
							aiCountdown=32;
						}
						
						if (animationDelay>0) animationDelay--;
						else {
							animationDelay=8;
							if (xOffset==42) xOffset=52;
							else xOffset=42;
						}
						
						if (fireDelay>0) fireDelay--;
						else {
							fireDelay=8;
							Bullets.addBullets(Bullets.bDEFAULTBULLET, x, y+4, 0, myDirection, Bullets.bOWNER_MONSTER, myWorld);
						}
						
						
						if (hitPlayer) myPlayer.hit();
					break;
					
				}
				
				if (myDirection==Globals.RIGHT) {
					yOffset=21;
					FLight.addLight(x+8-myWorld.worldOffsetX, y+3-myWorld.worldOffsetY, 16, FLight.LightType_Sphere, 255,0,0,128);
				} else {
					yOffset=31;
					FLight.addLight(x+1-myWorld.worldOffsetX, y+3-myWorld.worldOffsetY, 16, FLight.LightType_Sphere, 255,0,0,128);
				}
			break;
			
			
			
			case mHEALTH:
				floatY+=ySpeed;
				y=floatY>>4;
				ySpeed+=ySpeedIncrease;
				if (ySpeed<=-24 && ySpeedIncrease<0) {
					ySpeedIncrease=-ySpeedIncrease;
				} else if (ySpeed>=24 && ySpeedIncrease>0) {
					ySpeedIncrease=-ySpeedIncrease;
				}
				
				if (hitPlayer) {
					myPlayer.addHealth(1);
					died=true;
				}
				
				FLight.addLight(x+3-myWorld.worldOffsetX, y+4-myWorld.worldOffsetY, 16, FLight.LightType_SphereTense, 255,0,16,255);
				FLight.addLight(x+3-myWorld.worldOffsetX, y+4-myWorld.worldOffsetY, 48, FLight.LightType_Sphere, 255,0,16,128);
			break;
			
			case mBOUNCEXPLODE:
				doVertical(myWorld);
				
				if (ySpeed<48) ySpeed+=4;
				
				if (onGround) {
					ySpeed=-32;
					onGround=false;
				}
				
				if (aiState==1) {
					doHorizontal(myWorld);
					if (aiCountdown>0) aiCountdown--;
					if (aiCountdown==0 || hitPlayer) {
						aiState=2;
					}
				} else if (aiState==2) {
					died=true;
					FX.addFX(FX.fEXPLOSION, x, y, 0, myWorld);
					Bullets.addBullets(Bullets.bEXPLOSION, x+5, y+5, 32, 0, Bullets.bOWNER_MONSTER, myWorld);
				}
			break;
			
		}
		
	}
	
	
	
	public final void doHorizontal(World myWorld) {
		int tx;
		int ty;
		
		floatX+=xSpeed;
		x=floatX>>4;
		
		ty=(y+(h>>1))>>4;
		
		if (xSpeed>0) {
			tx=(x+w)>>4;
			if (myWorld.isSolid(tx, ty)) {
				x=(tx<<4)-w;
				floatX=x<<4;
				xSpeed=0;
			}
		} else {
			tx=x>>4;
			if (myWorld.isSolid(tx, ty)) {
				x=(tx<<4)+16;
				floatX=x<<4;
				xSpeed=0;
			}
		}
		
	
	}
	
	
	public final void doVertical(World myWorld) {
		int tx;
		int ty;
		
		floatY+=ySpeed;
		y=floatY>>4;
		
		tx=(x+(w>>1))>>4;
		
		if (ySpeed>0) {
			ty=(y+h+3)>>4;
			if (myWorld.isSolid(tx, ty)) {
				onGround=true;
				y=(ty<<4)-(h+3);
				floatY=y<<4;
				ySpeed=0;
			}
		} else {
			onGround=false;
			ty=y>>4;
			if (myWorld.isSolid(tx, ty)) {
				y=(ty<<4)+16;
				floatY=y<<4;
				ySpeed=0;
			}
		}
		
	
	}	
	
	
	
	public final boolean playerInOurRoom(Player myPlayer) {
		if (myPlayer.y+10>y-20 && myPlayer.y<y+20) return true;
		else return false;
	}
	
	public final boolean playerInOurSight(Player myPlayer) {
		if (myPlayer.y+10>y-20 && myPlayer.y<y+20) {
			if (myPlayer.x+5>x && myDirection==Globals.RIGHT) return true;
			if (myPlayer.x+5<x && myDirection==Globals.LEFT) return true;
		}
		
		return false;
	}
	

	public final boolean hit(Bullets myBullet, World myWorld, Player myPlayer) {
		boolean result=false;
		
		
		switch (myType) {
			case mSOLIDSCENERY:
				energy-=myBullet.energy;

				if (energy<=0) {
					died=true;
					myWorld.put(x>>4, y>>4, World.tEMPTY);
					
//					myPlayer.addScore(10);

					if (Globals.getRandom(100)>80) addMonster(mCREDIT, x, y, 0, myWorld);
					
					FX.addFX(FX.fEXPLOSION, x, y, 0, myWorld);
					
					for (int i=6; --i>=0;) {
						FX.addFX(FX.fDEBRI, x+Globals.getRandom(3), y, 0, myWorld);
					}
				}
				result=true;					
			break;
			
			case mWORM:
				energy-=myBullet.energy;
				xSpeed=myBullet.myDirection<<4;
				if (energy<=0) {
					died=true;
					
//					myPlayer.addScore(10);
//					FX.addFX(FX.fSCOREPLUME, x,y, 5, myWorld);
					
					addMonster(mCREDIT, x, y, 0, myWorld);
					
					
					for (int i=4; --i>=0;) {
						FX.addFX(FX.fDEBRI, x+Globals.getRandom(3), y, 0, myWorld);
					}
				}
				result=true;
			break;
			
			
			case mROBOEYE:
				energy-=myBullet.energy;
				
				// shortcircuit our shooting
				fireDelay=8;
				
				xSpeed=myBullet.myDirection<<4;
				if (energy<=0) {
					died=true;
					
//					myPlayer.addScore(10);
//					FX.addFX(FX.fSCOREPLUME, x,y, 5, myWorld);
					
					addMonster(mCREDIT, x, y, 0, myWorld);
					
					FX.addFX(FX.fEXPLOSION, x, y, 0, myWorld);
					
					for (int i=4; --i>=0;) {
						FX.addFX(FX.fDEBRI, x+Globals.getRandom(3), y, 0, myWorld);
					}
				}
				result=true;				
			break;
			
			
			
			
			case mBOUNCEXPLODE:
				
				energy-=myBullet.energy;
				if (energy<=0) {
					if (aiState==1) aiState=2;
					else {
						if (myBullet.xSpeed>0) xSpeed=-24;
						else xSpeed=24;
						
						energy=24+(myWorld.difficulty*2);
						
						aiState=1;
						aiCountdown=64;
					}
				}
				result=true;
			break;
		}
		
		
		return result;
	}
	
	
	
}
