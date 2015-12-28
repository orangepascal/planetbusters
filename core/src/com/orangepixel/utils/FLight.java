package com.orangepixel.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class FLight {
	
	public final static int LightType_Up = 0,
							LightType_Right = 1,
							LightType_Down = 2,
							LightType_Left = 3,
							LightType_SphereTense = 4,
							LightType_Sphere = 5,
							LightType_FLARE = 6,
							
							LightType_CLIP = 7;
	
	
	
	// light stuff
	public static Texture lightSprite;
	public static FLight[] myLights = new FLight[256];
	public static boolean isLightRendering;	 // switch this on/off when needed
	public static Color ambientColor=new Color();

	
	
	// type of light
	int	  lightType;
	
	// position and dimensions
	float x;
	float y;
	float w;
	float h;
	
	float distance;
	
	// colors
	float r;
	float g;
	float b;
	float a;
	
	boolean active;
	
	
	public final static void initLights() {
		for (int i=myLights.length - 1; i >= 0; i--) {
			myLights[i]=new FLight(); // 128);
			myLights[i].setActive(false);
		}
	}

	
	// light handling
	public final static void clearLights() {
		for (int i=0; i < myLights.length; i++) {
			myLights[i].setActive(false);
		}
	}

	
	public final static void addLight(int ax, int ay, float myDist, int myType, int myR, int myG, int myB, int myA) {
		int i=0;
		// FakeLight
		while (i < myLights.length && myLights[i].isActive()) i++;
		if (i < myLights.length) {
			myLights[i].setLightType(myType);
			
			myLights[i].setPosition(ax,ay);
			myLights[i].setColor(myR/255f, myG/255f, myB/255f, myA/255f);
			myLights[i].setDistance(myDist);
			myLights[i].setActive(true);
		}

	}		
	
	
	
	
	
	public int getLightType() {
		return lightType;
	}
	
	public void setLightType(int myType) {
		lightType=myType;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public void setPosition(float ax, float ay) {
		x=ax;
		y=ay;
	}
	
	public Color getColor() {
		return new Color(r,g,b,a);
	}
	
	public void setColor(float ar, float ag, float ab, float aa) {
		r=ar;
		g=ag;
		b=ab;
		a=aa;
	}
	
	public float getDistance() {
		return distance;
	}
	
	public void setDistance(float adistance) {
		distance=adistance;
	}

	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean mActive) {
		active=mActive;
	}
	

}
