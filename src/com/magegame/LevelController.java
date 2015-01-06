
/** Controls running of battle, calls objects frameCalls, draws and handles all objects, edge hit detection
 * @param DifficultyLevel Chosen difficulty setting which dictates enemy reaction time and DifficultyLevelMultiplier
 * @param DifficultyLevelMultiplier Function of DifficultyLevel which changes enemy health, mana, speed
 * @param EnemyType Mage type of enemy
 * @param PlayerType Mage type of player
 * @param LevelNum Level chosen to fight on
 * @param player Player object that has health etc and generates movement handler
 * @param enemy Enemy object with health etc and ai
 * @param enemies Array of all enemies currently on screen excluding main mage enemy
 * @param proj_Trackers Array of all enemy or player proj_Trackers
 * @param proj_Trackers Array of all enemy or player Proj_Tracker explosions
 * @param spGraphicEnemy Handles the changing of main enemy's sp
 * @param spGraphicPlayer Handles the changing of player's sp
 * @param oRectX1 Array of all walls left x value
 * @param oRectX2 Array of all walls right x value
 * @param oRectY1 Array of all walls top y value
 * @param oRectY2 Array of all walls bottom x value
 * @param oCircX Array of all pillars middle x value
 * @param oCircY Array of all pillars middle y value
 * @param oCircRadius Array of all pillars radius
 * @param currentCircle Current index of oCircX to write to
 * @param currentRectangle Current index of oRectX1 to write to
 * @param teleportSpots Array of levels four teleport spots x and y for enemy mage
 * @param game Game object holding control.imageLibrary
 * @param context Main activity context for returns
 * @param aoeRect Rectangle to draw sized bitmaps
 * @param mHandler Timer for frameCaller
 * @param handleMovement Handles players movement attacks etc
 * @param screenMinX Start of game on screen horizontally
 * @param screenMinY Start of game on screen vertically
 * @param screenDimensionMultiplier
 * @param frameCaller Calls objects and controllers frameCalls
 */
package com.magegame;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
public final class LevelController
{
	private Controller control;
	protected int levelNum = -1;
	protected int levelWidth = 300;
	protected int levelHeight = 300;
	protected ArrayList<ArrayList<int[]>> saveEnemyInformation = new ArrayList<ArrayList<int[]>>();
	protected List<Integer> savedInformationLevels = new ArrayList<>();
	/** 
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	public LevelController(Controller Control)
	{
		control = Control;
	}
	/**
	 * changes the level when you reach a portal type thing
	 */
	protected void checkChangeLevel()
	{
		switch(levelNum)
		{
		case 1:
			if(inBounds(0, 442, 26, 110))
			{
				loadLevel(2);
			}
			if(inBounds(644, 902, 31, 33))
			{
				loadLevel(3);
				control.player.x = 237;
				control.player.y = 74;
			}
			break;
		case 2:
			if(inBounds(0, 442, 26, 110))
			{
				loadLevel(2);
			}
			break;
		case 3:
			if(inBounds(222, 35, 31, 33))
			{
				loadLevel(1);
				control.player.x = 659;
				control.player.y = 895;
			}
			break;
		}
	}
	/**
	 * checks whether player is in bounds of rectangle
	 */
	protected boolean inBounds(int x, int y, int w, int h)
	{
		return (control.player.x>x && control.player.x<x+w&&control.player.y>y && control.player.y<y+h);
	}
	/**
	 * loads a new level, creates walls enemies etc.
	 */
	protected void loadLevel(int toLoad)
	{
		saveEnemies(levelNum);
		endFightSection();
		levelNum = toLoad;
		switch(levelNum)
		{
		case 1:
			//LEVEL
			levelWidth = 1000; // height of level
			levelHeight = 1000; // width of level
			//ENEMIES
			control.imageLibrary.loadEnemy(55, "goblin_swordsman", 110, 70, 0); // length, name,width, height, index
			control.imageLibrary.loadEnemy(49, "goblin_archer", 80, 50, 1);
			control.imageLibrary.loadEnemy(31, "goblin_mage", 30, 34, 2);
			//WALLS
			/*control.wallController.makeWall_Rectangle(78, 122, 41, 24, true, false);
			control.wallController.makeWall_Rectangle(63, -20, 31, 142, true, true);
			control.wallController.makeWall_Rectangle(73, 238, 47, 62, true, true);
			control.wallController.makeWall_Rectangle(94, -19, 25, 152, true, false);
			control.wallController.makeWall_Rectangle(252, 269, 234, 62, true, true);
			control.wallController.makeWall_Rectangle(412, 82, 74, 250, true, true);
			control.wallController.makeWall_Rectangle(382, 133, 30, 83, true, false);
			control.wallController.makeWall_Circle(330, 297, 47, 1, false);
			control.wallController.makeWall_Rectangle(217, -15, 109, 81, true, false);
			control.wallController.makeWall_Rectangle(179, -32, 38, 63, true, true);
			control.wallController.makeWall_Rectangle(318, -41, 66, 63, true, true);*/
			break;
			
			
		}
		makeEnemies(levelNum);
		control.imageLibrary.loadLevel(toLoad, levelWidth, levelHeight);
	}
	protected void saveEnemies(int levelToSave)
	{
		ArrayList<int[]> newSave = new ArrayList<int[]>();
		ArrayList<Enemy> enemies = control.spriteController.enemies;
		for(int i = 0; i < enemies.size(); i++)
		{
			if(!enemies.get(i).deleted)
			{
				int[] enemy = new int[5];
				enemy[0] = enemies.get(i).enemyType;
				enemy[1] = (int) enemies.get(i).x;
				enemy[2] = (int) enemies.get(i).y;
				enemy[3] = (int) enemies.get(i).rotation;
				enemy[4] = enemies.get(i).hp;
				newSave.add(enemy);
			}
		}
		saveEnemyInformation.add(newSave);
		savedInformationLevels.add(levelToSave);
		Log.e("mine", Integer.toString(saveEnemyInformation.size()));
		Log.e("mine", Integer.toString(savedInformationLevels.size()));
	}
	protected void makeEnemies(int toLoad)
	{
		boolean haveSavedEnemies = false;
		int saveIndex = 0;
		for(int i = 0; i < saveEnemyInformation.size(); i++)
		{
			if(savedInformationLevels.get(i)==toLoad)
			{
				haveSavedEnemies = true;
				for(int j = 0; j < saveEnemyInformation.get(i).size(); j++)
				{
					control.spriteController.createEnemy(saveEnemyInformation.get(saveIndex).get(i)); // CREATES SAVED ENEMIES
				}
				Log.e("mine", Integer.toString(i));
				break;
			}
		}
		if(!haveSavedEnemies)
		{
			makeNewEnemies(toLoad);
		}
	}
	protected void makeNewEnemies(int toLoad)
	{
		SpriteController spriteController = control.spriteController;
		switch(toLoad)
		{
		case 1:
			spriteController.makeEnemy(0, 604, 877, -120);
			spriteController.makeEnemy(1, 778, 718, 180);
			spriteController.makeEnemy(2, 830, 751, -165);
			spriteController.makeEnemy(0, 707, 823, -135);
			spriteController.makeEnemy(1, 824, 817, 180);
			spriteController.makeEnemy(0, 922, 229, 120);
			spriteController.makeEnemy(0, 817, 53, 150);
			spriteController.makeEnemy(0, 940, 81, 150);
			spriteController.makeEnemy(2, 723, 871, -165);
			break;
		}
	}
	/**
	 * ends a fight section with no saved enemies
	 */
	private void endFightSection()
	{
		control.spriteController.clearObjectArrays();
		control.wallController.clearWallArrays();
	}
}