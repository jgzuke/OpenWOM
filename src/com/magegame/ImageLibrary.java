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
		isPlayerWidth = 30;
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
		shotAOEEnemy = loadImage("shootenemyaoe", 80, 80);
		shotEnemy = loadArray1D(5, "shootenemy", 35, 15);
		shotAOEPlayer = loadImage("shootplayeraoe", 80, 80);
		shotPlayer = loadArray1D(5, "shootplayer", 35, 15);
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
		switch(levelNum)
		{
		case 1:
		case 2:
		case 3:
			backDrop = loadImage("leveltile1", 100, 100);
			break;
		case 5:
		case 6:
		case 7:
		case 8:
			backDrop = loadImage("leveltile2", 100, 100);
			break;
		default:
			backDrop = loadImage("leveltile1", 100, 100);
			break;
		}
		currentLevel = loadImage("level"+Integer.toString(levelNum), width, height);
		currentLevelTop = loadImage("leveltop"+Integer.toString(levelNum), width, height);
	}
	protected void recycleEnemies()
	{
		for(int i = 0; i < enemyImages.length; i++)
		{
			recycleArray(enemyImages[i]);
		}
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
		recycleEnemies();
	}
}