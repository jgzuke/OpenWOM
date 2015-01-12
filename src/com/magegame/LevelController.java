
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
public final class LevelController
{
	private Controller control;
	protected int levelNum = -1;
	protected int levelWidth = 300;
	protected int levelHeight = 300;
	protected List<ArrayList<int[]>> saveEnemyInformation = new ArrayList<ArrayList<int[]>>();
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
			if(inBounds(0, 342, 26, 110))
			{
				loadLevel(2);
				control.player.x = 970;
				control.player.y += 100;
			}
			if(inBounds(544, 702, 31, 33))
			{
				loadLevel(3);
				control.player.x = 237;
				control.player.y = 74;
			}
			break;
		case 2:
			if(inBounds(974, 442, 1000, 110))
			{
				loadLevel(1);
				control.player.x = 30;
				control.player.y -= 100;
			}
			break;
		case 3:
			if(inBounds(222, 35, 31, 33))
			{
				loadLevel(1);
				control.player.x = 559;
				control.player.y = 795;
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
		if(levelNum != -1)
		{
			saveEnemies(levelNum);
			endFightSection();
		}
		levelNum = toLoad;
		WallController w = control.wallController;
		control.imageLibrary.recycleEnemies();
		switch(levelNum)
		{
		case 1:
			//LEVEL
			levelWidth = 800; // height of level
			levelHeight = 800; // width of level
			if(control.graphicsController != null)
			{
				control.graphicsController.playScreenSize = 450;
			}
			//ENEMIES
			control.imageLibrary.loadEnemy(55, "goblin_swordsman", 110, 70, 0); // length, name,width, height, index
			control.imageLibrary.loadEnemy(49, "goblin_archer", 80, 50, 3);
			control.imageLibrary.loadEnemy(31, "goblin_mage", 30, 34, 2);
			//WALLS
			w.makeWall_Rectangle(-85, -182, 111, 528, true, true);
			w.makeWall_Rectangle(-85, 455, 111, 528, true, true);
			w.makeWall_Rectangle(-142, 241, 192, 105, true, true);
			w.makeWall_Rectangle(-142, 456, 192, 105, true, true);
			w.makeWall_Rectangle(343, 762, 307, 338, true, true);
			w.makeWall_Rectangle(383, 717, 66, 168, true, true);
			w.makeWall_Rectangle(515, 731, 87, 158, true, true);
			w.makeWall_Rectangle(530, 696, 13, 70, true, true);
			w.makeWall_Rectangle(575, 696, 13, 70, true, true);
			w.makeWall_Rectangle(668, 489, 32, 12, true, false);
			w.makeWall_Rectangle(671, 374, 22, 69, true, true);
			w.makeWall_Rectangle(696, 495, 10, 47, true, false);
			w.makeWall_Rectangle(664, 535, 37, 11, true, false);
			w.makeWall_Rectangle(696, 592, 10, 47, true, false);
			w.makeWall_Rectangle(741, 594, 10, 47, true, false);
			w.makeWall_Rectangle(687, 634, 58, 11, true, false);
			w.makeWall_Rectangle(701, 588, 45, 12, true, false);
			w.makeWall_Rectangle(671, 374, 22, 69, true, false);
			w.makeWall_Rectangle(660, 409, 22, 94, true, false);
			w.makeWall_Rectangle(642, 483, 22, 125, true, false);
			w.makeWall_Rectangle(663, 535, 22, 139, true, false);
			w.makeWall_Rectangle(649, 602, 22, 48, true, false);
			w.makeWall_Rectangle(685, 616, 20, 181, true, false);
			w.makeWall_Rectangle(663, 708, 105, 181, true, false);
			w.makeWall_Circle(396, 74, 8, 1, true);
			w.makeWall_Circle(478, 45, 8, 1, true);
			w.makeWall_Circle(607, 81, 8, 1, true);
			w.makeWall_Circle(719, 45, 8, 1, true);
			w.makeWall_Circle(698, 138, 8, 1, true);
			w.makeWall_Circle(605, 204, 8, 1, true);
			w.makeWall_Circle(772, 212, 8, 1, true);
			w.makeWall_Circle(737, 247, 8, 1, true);

			break;
		case 2:
			levelWidth = 1000; // height of level
			levelHeight = 1000; // width of level
			if(control.graphicsController != null)
			{
				control.graphicsController.playScreenSize = 350;
			}
			w.makeWall_Rectangle(-171, -114, 196, 483, true, true);
			w.makeWall_Rectangle(-190, 336, 241, 109, true, true);
			w.makeWall_Rectangle(-190, 550, 241, 109, true, true);
			w.makeWall_Rectangle(-171, 623, 196, 483, true, true);
			w.makeWall_Rectangle(-137, -104, 535, 130, true, true);
			w.makeWall_Rectangle(336, -150, 109, 200, true, true);
			w.makeWall_Rectangle(606, -104, 535, 130, true, true);
			w.makeWall_Rectangle(553, -150, 109, 200, true, true);
			w.makeWall_Rectangle(972, -114, 196, 483, true, true);
			w.makeWall_Rectangle(947, 336, 241, 109, true, true);
			w.makeWall_Rectangle(947, 550, 241, 109, true, true);
			w.makeWall_Rectangle(972, 623, 196, 483, true, true);
			w.makeWall_Rectangle(-137, 971, 535, 130, true, true);
			w.makeWall_Rectangle(336, 947, 109, 200, true, true);
			w.makeWall_Rectangle(606, 971, 535, 130, true, true);
			w.makeWall_Rectangle(553, 947, 109, 200, true, true);
			w.makeWall_Rectangle(-94, -276, 196, 483, true, true);
			w.makeWall_Rectangle(-10, -364, 196, 483, true, true);
			w.makeWall_Rectangle(776, 58, 279, 46, true, true);
			w.makeWall_Rectangle(847, -312, 196, 483, true, true);
			w.makeWall_Rectangle(813, 881, 328, 179, true, true);
			w.makeWall_Rectangle(-64, 827, 194, 210, true, true);
			break;
		case 3:
			//LEVEL
			levelWidth = 350; // height of level
			levelHeight = 250; // width of level
			control.graphicsController.playScreenSize = 250;
			//ENEMIES
			control.imageLibrary.loadEnemy(65, "goblin_rogue", 60, 40, 4); // length, name,width, height, index
			control.imageLibrary.loadEnemy(49, "goblin_archer", 80, 50, 3);
			//WALLS
			w.makeWall_Circle(126, 217, 39, 1, false);
			w.makeWall_Circle(60, 51, 39, 1, false);
			w.makeWall_Rectangle(-50, -6, 74, 250, true, true);
			w.makeWall_Rectangle(-8, 2, 70, 84, true, true);
			w.makeWall_Rectangle(45, -41, 103, 84, true, true);
			w.makeWall_Rectangle(129, -13, 68, 103, true, true);
			w.makeWall_Rectangle(142, -49, 68, 103, true, true);
			w.makeWall_Rectangle(264, -47, 68, 103, true, true);
			w.makeWall_Rectangle(282, -9, 68, 103, true, true);
			w.makeWall_Rectangle(325, 37, 68, 141, true, true);
			w.makeWall_Rectangle(282, 161, 68, 103, true, true);
			w.makeWall_Rectangle(181, 193, 129, 103, true, true);
			w.makeWall_Rectangle(-67, 169, 129, 103, true, true);
			w.makeWall_Rectangle(37, 214, 129, 103, true, true);
			w.makeWall_Rectangle(126, 171, 71, 110, true, true);

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
		for(int i = 0; i < savedInformationLevels.size(); i++) // if feild already saved delete old data
		{
			if(savedInformationLevels.get(i)==levelToSave)
			{
				saveEnemyInformation.remove(i);
				savedInformationLevels.remove(i);
			}
		}
		saveEnemyInformation.add(newSave);
		savedInformationLevels.add(levelToSave);
	}
	protected void makeEnemies(int toLoad)
	{
		boolean haveSavedEnemies = false;
		for(int i = 0; i < savedInformationLevels.size(); i++)
		{
			if(savedInformationLevels.get(i)==toLoad)
			{
				haveSavedEnemies = true;
				for(int j = 0; j < saveEnemyInformation.get(i).size(); j++)
				{
					control.spriteController.createEnemy(saveEnemyInformation.get(i).get(j)); // CREATES SAVED ENEMIES
				}
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
		SpriteController s = control.spriteController;
		switch(toLoad)
		{
		case 1:
			s.makeEnemy(0, 504, 677, -120);
			s.makeEnemy(3, 678, 518, 180);
			s.makeEnemy(2, 730, 551, -165);
			s.makeEnemy(0, 607, 623, -135);
			s.makeEnemy(3, 724, 617, 180);
			s.makeEnemy(0, 722, 229, 120);
			s.makeEnemy(0, 617, 53, 150);
			s.makeEnemy(0, 740, 81, 150);
			s.makeEnemy(2, 623, 671, -165);
			s.makeEnemy(2, 559, 695, 60);
			break;
			
			
		case 3:
			s.makeEnemy(3, 153, 96, 90);
			s.makeEnemy(4, 208, 181, 0);
			s.makeEnemy(4, 264, 124, -120);
			s.makeEnemy(4, 145, 135, -21);
			s.makeEnemy(3, 50, 108, 60);
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