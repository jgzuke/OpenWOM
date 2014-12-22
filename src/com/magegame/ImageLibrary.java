/**
 * Loads, stores and resizes all graphics
 */
package com.magegame;
import com.spritelib.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
public final class ImageLibrary extends ImageLoader
{
	protected Bitmap[] player_Image = new Bitmap[32];
	protected Bitmap[][] enemyImages = new Bitmap[10][100]; //array holding videos for each enemy, null when uneeded
	protected Bitmap structure_Spawn;
	protected Bitmap[] effects = new Bitmap[4];
	protected Bitmap isPlayer;
	protected int isPlayerWidth;
	protected Bitmap shotPlayer;
	protected Bitmap shotAOEPlayer;
	protected Bitmap shotEnemy;
	protected Bitmap shotAOEEnemy;
	protected Bitmap[] powerUps = new Bitmap[11];
	protected Bitmap[] powerUpBigs = new Bitmap[4];
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
		loadAllImages();
	}
	/**
	 * loads players current animation
	 */
	protected void loadPlayerImage()
	{
		player_Image = loadArray1D(32, "human_playerzack", 46, 50);
		isPlayerWidth = 26;
		/*switch(control.activity.currentSkin)
		{
			case 0: player_Image = loadArray1D(31, "human_playerzack", 35, 40);
					isPlayerWidth = 26;
				break;
			case 1: player_Image = loadArray1D(31, "human_playergolden", 35, 40);
					isPlayerWidth = 26;
				break;
			case 2: player_Image = loadArray1D(31, "human_playerleather", 35, 40);
					isPlayerWidth = 26;
				break;
			case 3: player_Image = loadArray1D(31, "human_playerbarbarian", 35, 40);
					isPlayerWidth = 26;
				break;
			case 4: player_Image = loadArray1D(31, "human_playercleric", 35, 40);
					isPlayerWidth = 26;
				break;
			case 5: player_Image = loadArray1D(31, "human_playerent", 80, 76);
					isPlayerWidth = 43;
				break;
			case 6: player_Image = loadArray1D(31, "human_playergargoyle", 60, 69);
					isPlayerWidth = 38;
				break;
			case 7: player_Image = loadArray1D(31, "human_playerdragon", 35, 40);
					isPlayerWidth = 26;
				break;
			default: player_Image = loadArray1D(31, "human_playerzack", 35, 40);
					isPlayerWidth = 26;
				break;
		}*/
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
		powerUps = loadArray1D(11, "icon_powerup", 30, 30);
		powerUpBigs = loadArray1D(5, "icon_powerupbig", 70, 70);
		coins = loadArray1D(2, "icon_menu_coin", 30, 30);
		shotAOEEnemy = loadImage("shotexplodeenemy", 80, 80);
		shotEnemy = loadImage("shotenemy", 40, 3);
		shotAOEPlayer = loadImage("shotexplodeplayer", 80, 80);
		shotPlayer = loadImage("shotplayer", 40, 3);
		effects = loadArray1D(4, "effect", 60, 60);
		isPlayer = loadImage("icon_isplayer", 40, 40);
		loadLevel(control.levelNum, control.levelWidth, control.levelHeight);
		backDrop = loadImage("level_back", 100, 100);
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
		backDrop = loadImage("level_back", 300, 300);
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
		recycleArray(powerUpBigs);
		recycleArray(powerUps);
		recycleArray(effects);
	}
}