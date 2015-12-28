package com.orangepixel.planetbusters;

import com.orangepixel.utils.FLight;

public class Player extends Entity {

	// position
	int		floatX;
	int		floatY;
	int		xSpeed;
	int		ySpeed;
	int		myDirection;
	boolean	onGround;
	
	// stats
	int		health;
	int		score;
	int		creditsCollected;
	int		healthDelay;
	
	// interaction
	boolean	leftPressed;
	boolean	rightPressed;
	boolean	upPressed;
	boolean actionPressed;
	boolean downPressed;
	
	// appearance
	int		xOffsetAdd;
	int		animationDelay;
	int		actionDelay;
	boolean visible;
	
	boolean	firstLanding;
	
	
	public Player() {
		
	}
	
	public final void newGameInit() {
		creditsCollected=0;
		score=0;
		health=9;
	}

	
	public final void init(int startX, int startY) {
		x=startX;
		y=startY;
		
		myDirection=Globals.RIGHT;
		
		xOffset=0;
		yOffset=0;
		xOffsetAdd=10;
		w=10;
		h=10;
		xSpeed=0;
		ySpeed=0;
		
		actionDelay=0;
		
		// wait for the ship to give us the BustAPLanet() signal
		visible=false;
	
		
		floatX=x<<4;
		floatY=y<<4;
		healthDelay=0;
	}
	
	
	
	
	public final void resetInput() {
		leftPressed=false;
		rightPressed=false;
		upPressed=false;
		actionPressed=false;
		downPressed=false;
	}
	
	
	
	public void BustAPLanet(int startX, int startY) {
		x=startX;
		y=startY;
		floatX=x<<4;
		floatY=y<<4;
		ySpeed=-24;
		onGround=false;
		visible=true;
		firstLanding=true;
	}
	
	public void hit() {
		if (healthDelay>0) return;
		
		healthDelay=16;
		health--;
		if (health<=0) health=0;
	}
	
	
	public void addHealth( int value) {
		health+=value;
		if (health>9) health=9;
		healthDelay=16;
	}
	
	public void addScore(int value) {
		score+=value;
	}
	
	public void addCredits(int value) {
		creditsCollected+=value;
		
		if (Globals.getRandom(100)>80)
			FX.addFX(FX.fSPEECH, x+Globals.getRandom(32)-16, y, Globals.getRandom(Globals.dudeQuotes.length), null);
	}
	
	
	// throw back player (i.e.: after an explosion)
	public void throwBack(int mDirection) {
		ySpeed=-32;

		switch (mDirection) {
			case Globals.LEFT:
				xSpeed=-48;
			break;

			case Globals.RIGHT:
				xSpeed=48;
			break;
		}
	}
	
	public void update(World myWorld) {
		if (!visible) return;
		if (healthDelay>0) healthDelay--;
		
		int tx;
		int ty;
		
		if (actionDelay>0) actionDelay--;


		
		
		if (leftPressed) {
			if (xSpeed>-24) xSpeed-=4;
			else xSpeed=-24;
			
			myDirection=Globals.LEFT;
		} else if (rightPressed) {
			if (xSpeed<24) xSpeed+=4;
			else xSpeed=24;
			
			myDirection=Globals.RIGHT;
		} else {
			
			if (xSpeed<0) {
				xSpeed+=2;
				if (xSpeed>=0) xSpeed=0;
			} else if (xSpeed>0) {
				xSpeed-=2;
				if (xSpeed<=0) xSpeed=0;
			}
		}
		
		if (actionPressed && actionDelay==0) {
			actionDelay=8;
			myWorld.worldShake=8;
			
			Bullets.addBullets(Bullets.bDEFAULTBULLET, x+5,y+5, 0, myDirection, Bullets.bOWNER_PLAYER, myWorld);
			
			// 3way
			Bullets.addBullets(Bullets.bDEFAULTBULLET, x+5,y+5, -1, myDirection, Bullets.bOWNER_PLAYER, myWorld);
			Bullets.addBullets(Bullets.bDEFAULTBULLET, x+5,y+5, 1, myDirection, Bullets.bOWNER_PLAYER, myWorld);
			
			if (myDirection==Globals.RIGHT) FLight.addLight(x+11-myWorld.worldOffsetX, y+5-myWorld.worldOffsetY, 24, FLight.LightType_Sphere, 180,180,180,255);
			else FLight.addLight(x-12-myWorld.worldOffsetX, y+5-myWorld.worldOffsetY, 24, FLight.LightType_Sphere, 180,180,180,255);
			
			// kickback
			if (myDirection==Globals.RIGHT) xSpeed-=4;
			else if (myDirection==Globals.LEFT) xSpeed+=4;
		}
				
		
		floatX+=xSpeed;
		x=floatX>>4;
				
		if (x<myWorld.worldOffsetX) {
			x=myWorld.worldOffsetX;
			floatX=x<<4;
		}
				
				
		ty=(y+8)>>4;
		
		if (xSpeed>0) {
			tx=(x+w)>>4;
			if (myWorld.isSolid(tx, ty)) {
				x=(tx<<4)-w;
				xSpeed=0;
				floatX=x<<4;
			}
		} else {
			tx=(x+4)>>4;
			if (myWorld.isSolid(tx, ty)) {
				x=(tx<<4)+12;
				floatX=x<<4;
				xSpeed=0;
			}
		}
				
		
		if (upPressed && onGround) {
			ySpeed=-48;
			onGround=false;
		}
		
		floatY+=ySpeed;
		
		if (ySpeed<64) ySpeed+=4;
		y=floatY>>4;
		
		tx=(x+5)>>4;
		
		if (ySpeed<0) {
			ty=(y+8)>>4;
			if (myWorld.isSolid(tx, ty)) {
				y=(ty<<4)+8;
				floatY=y<<4;
				ySpeed=0;
				onGround=false;
			}
		} else {
			ty=(y+h+4)>>4;
			if (myWorld.isSolid(tx, ty)) {
				if (myWorld.getTile(tx, ty)==World.tMONSTER) y=(ty<<4)-(h-1); 
				else y=(ty<<4)-(h+4);
				floatY=y<<4;
				if (ySpeed>16) {
					FX.addFX(FX.fLANDPLUME, x-6, y+7, 0, myWorld);
					myWorld.worldShake=16;
				}
				ySpeed=0;
				onGround=true;
				if (firstLanding) {
					firstLanding=false;
					FX.addFX(FX.fSPEECH, x+32, y-4, Globals.getRandom(Globals.dudeQuotes.length), myWorld);
				}
			}
			
		}
		
		

		
		animate();
		
		FLight.addLight(x+5-myWorld.worldOffsetX, y+5-myWorld.worldOffsetY, 32, FLight.LightType_Sphere, 244,236,151,150);
	}
	
	
	
	
	
	public void animate() {
		if (myDirection==Globals.LEFT) yOffset=10;
		else if (myDirection==Globals.RIGHT) yOffset=0;
		
		if (xSpeed==0 && ySpeed==0) {
			// IDLE!
			if (animationDelay>0) animationDelay--;
			else {
				animationDelay=8;
				xOffset+=10;
				if (xOffset>20) xOffset=0;
			}
		} else if (ySpeed<-8) {
			xOffset=60;
		} else if (!onGround) {
			xOffset=70;
		} else {
			// RUN
			if (animationDelay>0) animationDelay--;
			else {
				animationDelay=4;
				xOffset+=xOffsetAdd;
				if (xOffset>=50) {
					xOffset=50;
					xOffsetAdd=-10;
				} else if (xOffset<=30) {
					xOffset=30;
					xOffsetAdd=10;
				}
			}
		}
		
	}
	
}
