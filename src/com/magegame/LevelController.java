
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
public final class LevelController
{
	private Controller control;
	protected int levelNum = 1;
	protected int levelWidth = 300;
	protected int levelHeight = 300;
	protected ArrayList<int[]> saveEnemyInformation = new ArrayList<int[]>();
	/** 
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	public LevelController(Controller Control)
	{
		control = Control;
	}
	/**
	 * loads a new level, creates walls enemies etc.
	 */
	protected void loadLevel(int toLoad)
	{
		control.wallController.clearWallArrays();
		levelNum = toLoad;
		if(toLoad==1)
		{
			//LEVEL
			levelWidth = 1000; // height of level
			levelHeight = 1000; // width of level
			//PLAYER
			control.player.x = 30; // player start x
			control.player.y = 30; // control.player start y
			//ENEMIES
			control.imageLibrary.loadEnemy(55, "goblin_swordsman", 110, 70, 0); // length, name,width, height, index
			control.imageLibrary.loadEnemy(49, "goblin_archer", 80, 50, 1);
			control.imageLibrary.loadEnemy(31, "goblin_mage", 30, 34, 2);
			control.spriteController.makeEnemy(0, 269, 86); //type, x, y
			control.spriteController.makeEnemy(1, 358, 140);
			control.spriteController.makeEnemy(2, 458, 140);
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
		}
		control.imageLibrary.loadLevel(toLoad, levelWidth, levelHeight);
	}
	/**
	 * loads a new section of the current level
	 * @param level id of new section to load
	 */
	protected void loadLevelSection(int level)
	{
		control.wallController.clearWallArrays();
		levelNum = level;
		for(int i = 0; i < control.spriteController.powerUps.size(); i++)
		{
			if(control.spriteController.powerUps.get(i) != null) control.player.getPowerUp(control.spriteController.powerUps.get(i).ID);
		}			 // READS IN AND CREATES ENEMIES IN NEW SECTION, SAVES ENEMIES IN OLD SECTION
		ArrayList<int[]> tempSave = (ArrayList<int[]>)saveEnemyInformation.clone();
		int j = 0;
		for(int i = 0; i < saveEnemyInformation.size(); i++)
		{
			ArrayList<Enemy> enemies = control.spriteController.enemies;
			if(!enemies.get(i).deleted)
			{
				saveEnemyInformation.get(j)[0] = enemies.get(i).enemyType;
				saveEnemyInformation.get(j)[1] = (int) enemies.get(i).x;
				saveEnemyInformation.get(j)[2] = (int) enemies.get(i).y;
				saveEnemyInformation.get(j)[3] = enemies.get(i).hp;
				j++;
			}
		}
		endFightSection(tempSave);
		if(levelNum == 21)
		{
			//LEVEL
			levelWidth = 450; // height of level
			levelHeight = 300; // width of level
			//PLAYER
			control.player.x = 30; // control.player start x
			control.player.y = 30; // player start y
			//ENEMIES
			control.imageLibrary.loadEnemy(95, "human_enemy", 100, 70, 1); // length, name,width, height, index
			control.spriteController.makeEnemy(1, 269, 86); //type, x, y
			control.spriteController.makeEnemy(1, 358, 140);
			control.spriteController.makeEnemy(1, 365, 204);
			control.spriteController.makeEnemy(2, 146, 61);
			control.spriteController.makeEnemy(2, 327, 231);
			//WALLS
			control.wallController.makeWall_Rectangle(78, 122, 41, 24, true, false);
			control.wallController.makeWall_Rectangle(63, -20, 31, 142, true, true);
			control.wallController.makeWall_Rectangle(73, 238, 47, 62, true, true);
			control.wallController.makeWall_Rectangle(94, -19, 25, 152, true, false);
			control.wallController.makeWall_Rectangle(252, 269, 234, 62, true, true);
			control.wallController.makeWall_Rectangle(412, 82, 74, 250, true, true);
			control.wallController.makeWall_Rectangle(382, 133, 30, 83, true, false);
			control.wallController.makeWall_Circle(330, 297, 47, 1, false);
			control.wallController.makeWall_Rectangle(217, -15, 109, 81, true, false);
			control.wallController.makeWall_Rectangle(179, -32, 38, 63, true, true);
			control.wallController.makeWall_Rectangle(318, -41, 66, 63, true, true);
		}
		control.imageLibrary.loadLevel(levelNum, levelWidth, levelHeight);
	}
	/**
	 * end a section of a fight, stored enemies in current states
	 * @param enemyData enemies to create
	 * @param tempEnemies number of enemies to create
	 */
	private void endFightSection(ArrayList<int[]> enemyData)
	{
		endFightSection();
		for(int i = 0; i < enemyData.size(); i++)
		{
			control.spriteController.createEnemy(enemyData.get(i)); // CREATES SAVED ENEMIES
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