package com.orangepixel.social;


public interface Social {
	
	
	public boolean isConnected();
	
	
	public void initSocial();
	public void loginSocial();
	
	public void disposeSocial();
	
	public void uploadScore(String leaderBoard, int score, boolean keepBest);
	public void uploadAchievement(int achievementID);
	
	public void getScores(String leaderBoard);
	
	public void uploadDaily(String leaderBoard, int score);
	
	// called on every frame update, steam requires this to process the callbacks
	public void processInfo(); 
	
	public int getMaxHighscoreCount();
	public int getHighScores(int idx);
	public String getHighScoreName(int idx);
	public boolean doneDownloadingScores();
	public boolean doneUploadingScore();
	public boolean noLeaderboardFound();

}
