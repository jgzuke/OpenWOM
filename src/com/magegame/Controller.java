
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
 * @param game Game object holding imageLibrary
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
import android.content.Context;
import android.os.Handler;
import java.util.Random;
public final class Controller
{
	protected int curXShift;
	protected int curYShift;
	protected StartActivity activity;
	protected Player player;
	protected Context context;
	protected ImageLibrary imageLibrary;
	private Random randomGenerator = new Random();
	protected PlayerGestureDetector detect;
	private Handler mHandler = new Handler();
	protected SpriteController spriteController;
	protected WallController wallController;
	protected LevelController levelController;
	protected GraphicsController graphicsController;
	protected SoundController soundController;
	protected ItemController itemControl;
	

	protected byte currentSkin = 0;
	protected byte [] worships = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // all the olympians
	protected boolean [] skins = {false, false, false, false, false, false, false, false}; //skins
	protected boolean paused = false;
	protected Runnable frameCaller = new Runnable()
	{
		/**
		 * calls most objects 'frameCall' method (walls enemies etc)
		 */
		public void run()
		{
			if(!paused)
			{
				frameCall();
				mHandler.postDelayed(this, 50);
			}
		}
	};	
	/** 
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	public Controller(Context startSet, StartActivity activitySet, double [] dimensions)
	{
		activity = activitySet;
		context = startSet;
		itemControl = new ItemController();
		soundController = new SoundController(startSet, activitySet);
		
		wallController = new WallController(startSet, this);
		spriteController = new SpriteController(startSet, this);
		imageLibrary = new ImageLibrary(startSet, this); // creates image library
		levelController = new LevelController(this);
		
		imageLibrary.loadAllImages();
		player = new Player(this); // creates player object
		levelController.loadLevel(1);
		
		player.resetVariables();
		detect = new PlayerGestureDetector(this); // creates gesture detector object
		detect.setPlayer(player);
		graphicsController = new GraphicsController(this, imageLibrary, spriteController, wallController, levelController, player, startSet, dimensions);
		graphicsController.setOnTouchListener(detect);
		detect.setDimensions();
		frameCaller.run();
	}
	protected void die()
	{
		player.resetVariables();
		levelController.loadLevel(1);
		graphicsController.playerBursted=40;
		//TODO
	}
	protected void pause()
	{
		paused = true;
		//TODO
	}
	/**
	 * Sets deleted objects to null to be gc'd and tests player and enemy hitting arena bounds
	 */
	protected void frameCall()
	{
		graphicsController.frameCall();
		spriteController.frameCall();
		player.frameCall();
		wallController.frameCall();
	}
	/**
	 * returns random integer between 0 and i-1
	 * @param i returns int between one less than this and 0
	 * @return random integer between 0 and i-1
	 */
	protected int getRandomInt(int i)
	{
		return randomGenerator.nextInt(i);
	}
	/**
	 * returns random double between 0 and 1
	 * @return random double between 0 and 1
	 */
	protected double getRandomDouble()
	{
		return randomGenerator.nextDouble();
	}
}