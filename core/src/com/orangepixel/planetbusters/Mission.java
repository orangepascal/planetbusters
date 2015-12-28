package com.orangepixel.planetbusters;

public class Mission {

	public final static Mission[]	missionList = new Mission[16];

	
	private final static String[]	planetFirstname = new String[] {
			"planet",
			"totopolis",
			"uruga",
			"epsylon",
			"garth",
			"galaci",
			"gdx",
			"robovm",
			"robotality",
			"pixelius",
			"orange",
			"red",
			"blue",
			"black",
			"tauri"
	};
	
	private final static String[]	planetLastname = new String[] {
			"turi",
			"xl-1",
			"sirius",
			"tempora",
			"xyilk",
			"pixel",
			"pixie",
			"lib",
			"gdx",
			"mark 2",
			"melok",
			"ceti"
	};

	
	
	
	String		missionName;
	
	int			Spriteset;
	int			difficulty;		// 1=easy --> hard
	int			cost;
	
	boolean		completed;
	boolean		hasRadiation;
	
	
	
	public final static void set(int id, int mSet, int mDifficulty, boolean mCompleted, int mCost) {
		missionList[id]=new Mission();
		missionList[id].setName();
		missionList[id].Spriteset=mSet;
		missionList[id].difficulty=mDifficulty;
		missionList[id].completed=mCompleted;
		missionList[id].cost=mCost;	
		missionList[id].hasRadiation=false;
		
		// radiation? 
		if (Globals.getRandom(100)>90 && missionList[id].difficulty>3) missionList[id].hasRadiation=true;
		
	}
	
	public final static void setCompleted(int id) {
		missionList[id].completed=true;
	}
	
	public final static boolean getCompleted(int id) {
		return missionList[id].completed;
	}
	
	public final static boolean hasRadiation(int id) {
		return missionList[id].hasRadiation;
	}
	
	public final static String getName(int id) {
		return missionList[id].missionName;
	}
	
	public final static int getSpriteset(int id) {
		return missionList[id].Spriteset;
	}

	public final static int getDifficulty(int id) {
		return missionList[id].difficulty;
	}

	public final static int getCost(int id) {
		return missionList[id].cost;
	}
	
	
	// create a random planet name
	public final void setName() {
		missionName=planetFirstname[Globals.getRandom(planetFirstname.length)]+" "+planetLastname[Globals.getRandom(planetLastname.length)];
	}
	
}
