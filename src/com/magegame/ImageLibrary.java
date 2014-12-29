/**
 * Loads, stores and resizes all graphics
 */
package com.magegame;
import com.spritelib.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
public final class ImageLibrary extends ImageLoader
{
	protected Bitmap[] player_Image = new Bitmap[32]; //TODO make images smaller
	protected Bitmap[][] enemyImages = new Bitmap[10][100]; //array holding videos for each enemy, null when uneeded
	protected Bitmap structure_Spawn;
	protected Bitmap isPlayer;
	protected int isPlayerWidth;
	protected Bitmap[] shotPlayer;
	protected Bitmap shotAOEPlayer;
	protected Bitmap[] shotEnemy;
	protected Bitmap shotAOEEnemy;
	protected Bitmap[] coins = new Bitmap[2];
	protected Bitmap currentLevel;
	protected Bitmap currentLevelTop;
	protected Bitmap backDrop;
	private Controller control;
	/**
	 * loads in images and optimizes settings for loading
	 * @param contextSet start activity for getting resources etc
	 * @param controlSet control object
	 */
	public ImageLibrary(Context contextSet, Controller controlSet)
	{
		super(contextSet);
		control = controlSet;
	}
	/**
	 * loads players current animation
	 */
	protected void loadPlayerImage()
	{
		player_Image = loadArray1D(32, "human_playerzack", 35, 40);
		isPlayerWidth = 40;
		isPlayer = loadImage("icon_isplayer", 2*isPlayerWidth, 2*isPlayerWidth);
	}
	/**
	 * loads a selection into enemyImages
	 */
	protected void loadEnemy(int length, String start, int width, int height, int index)
	{
		enemyImages[index]= loadArray1D(length, start, width, height);
	}
	/**
	 * loads all required images for all games
	 */
	protected void loadAllImages()
	{
		loadPlayerImage();
		coins = loadArray1D(2, "icon_menu_coin", 30, 30);
		shotAOEEnemy = loadImage("shootenemyaoe", 80, 80);
		shotEnemy = loadArray1D(5, "shootenemy", 35, 15);
		shotAOEPlayer = loadImage("shootplayeraoe", 80, 80);
		shotPlayer = loadArray1D(5, "shootplayer", 35, 15);
		isPlayer = loadImage("icon_isplayer", 40, 40);
		backDrop = loadImage("leveltile1", 100, 100);
	}
	/**
	 * loads level image layers and background image
	 * @param levelNum level to load
	 * @param width width of level
	 * @param height height of level
	 */
	protected void loadLevel(int levelNum, int width, int height)
	{
		if(currentLevel != null)
		{
			currentLevel.recycle();
			currentLevel = null;
		}
		if(currentLevelTop != null)
		{
			currentLevelTop.recycle();
			currentLevelTop = null;
		}
		if(backDrop!= null)
		{
			backDrop.recycle();
		}
		backDrop = loadImage("leveltile1", 100, 100);
		currentLevel = loadImage("level"+Integer.toString(levelNum), width, height);
		currentLevelTop = loadImage("leveltop"+Integer.toString(levelNum), width, height);
	}
	/**
	 * recycles images to save memory
	 */
	protected void recycleImages()
	{
		if(currentLevel != null)
		{
			currentLevel.recycle();
			currentLevel = null;
		}
		if(currentLevelTop != null)
		{
			currentLevelTop.recycle();
			currentLevelTop = null;
		}
		recycleArray(player_Image);
	}
}