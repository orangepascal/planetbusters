package com.orangepixel.planetbusters;

import java.util.Calendar;
import java.util.Random;

import com.badlogic.gdx.Gdx;

public class Globals {
	
	public final static boolean debugMe=true;

	public final static int UP = 0,
							RIGHT = 1,
							LEFT = 2,
							DOWN = 3;
	
	// WEAPONS
	public final static int WEAPON_GUN = 0;
	
	// spriteXOffset, spriteYOffset, spriteWidth, spriteHeight
	public final static int[][] weaponValues = new int[][] {
		{0,20, 14,6},  
	};
	
	
	// CHATTER QUOTES
	public final static String[] dudeQuotes = new String[] {
			"let's bring the pain!",
			"let's bust a planet...",
			"here comes the storm",
			"wooooohi!",
			"toooo easy!",
			"this and all that!"
	};
	
	
	
	// GALAXY names
	public final static String[] galaxyNamePre = new String[] {
			"omega",
			"armada",
			"lucious",
			"link",
			"toto",
			"altera",
			"altoro",
			"applepie",
			"methodi",
			"sitrus",
			"sinus",
			"armada",
			"lasius",
			"torius",
			"milky",
			"chocola",
			"sour",
			"bread",
			"left"
	};
	
	public final static String[] galaxyNamePost = new String[] {
			"galaxy",
			"space",
			"region",
			"nebula",
			"sphere",
			"stars",
			"system",
			"u2",
			"populus",
			"cube",
			"planetoids",
			"expanse",
			"area"
	};
	
	

	
	public final static void debug(String whatsUp) {
		if (!debugMe) return;
		Gdx.app.log("orangepixelsays",whatsUp);
	}
	
	
	
	
	
	// Random and seeding related code
	// seed values
	public static Random randomGenerator = new Random();
	static int randx=2016; // 123456789;
	static int randy=01; // 362436069;
	static int randz=18; // 521288629;
	static int randw=1; // 88675123;

	static int[] randomTable=new int[60000];
	static int randomNextInt;
	
	// set this true to always have same level (daily challenge for example)
	static boolean getRandomSeeded=false;
	
	public static void fillRandomTable() {
		int t;

		Calendar cal=Calendar.getInstance();

		randx=(cal.get(Calendar.YEAR) * 3);
		randy=(1 + (cal.get(Calendar.MONTH) * 7));
		randz=(1 + (cal.get(Calendar.DAY_OF_MONTH)));

		for (int i=0; i < 60000; i++) {
			t=randx ^ (randx << 11);
			randx=randy;
			randy=randz;
			randz=randw;
			randw=randw ^ (randw >> 19) ^ (t ^ (t >> 8));
			randomTable[i]=randw;
		}

		randomNextInt=0;
	}
	
	public static int getRandomSeeded(int digit1) {
		int value=randomTable[randomNextInt] % digit1;
		randomNextInt++;
		if (randomNextInt == 60000) randomNextInt=0;
		return value;
	}
	
	public static int getRandom(int digit1) {
		if (getRandomSeeded) return getRandomSeeded(digit1);
		else  return  randomGenerator.nextInt(digit1);
	}		
	
}
