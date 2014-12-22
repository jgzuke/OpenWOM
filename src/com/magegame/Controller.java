
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
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

import com.spritelib.Sprite;
public final class Controller extends View
{
	private boolean paused = false;
	protected int screenMinX;
	protected int screenMinY;
	protected double screenDimensionMultiplier;
	protected Paint paint = new Paint();
	protected Matrix rotateImages = new Matrix();
	protected StartActivity activity;
	protected ArrayList<int[]> saveEnemyInformation = new ArrayList<int[]>();
	private Rect aoeRect = new Rect();
	protected Player player;
	protected Context context;
	protected ImageLibrary imageLibrary;
	private Random randomGenerator = new Random();
	protected int levelNum = 1;
	private Bitmap background;
	protected PlayerGestureDetector detect;
	protected int levelWidth = 300;
	protected int levelHeight = 300;
	private Handler mHandler = new Handler();
	protected int curXShift;
	protected int curYShift;
	private int healthColor = Color.rgb(150, 0, 0);
	private int cooldownColor = Color.rgb(190, 190, 0);
	protected Sprite shootStick;
	protected int playerHit=0;
	protected int playerBursted = 0;
	SpriteController spriteController;
	WallController wallController;
	Typeface magicMedieval; 
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
	public Controller(Context startSet, StartActivity activitySet)
	{
		super(startSet);
		spriteController = new SpriteController(startSet, this);
		wallController = new WallController(startSet, this);
		activity = activitySet;
		context = startSet;
		imageLibrary = new ImageLibrary(startSet, this); // creates image library
		player = new Player(this); // creates player object
		setUpStuff();
		loadLevel(1); // create enemies walls etc.
		frameCaller.run();
	}
	private void setUpStuff()
	{
		shootStick = new Graphic_shootStick(imageLibrary.loadImage("icon_shoot", 70, 35));
		screenMinX = activity.screenMinX;
		screenMinY = activity.screenMinY;
		screenDimensionMultiplier = activity.screenDimensionMultiplier;
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		setBackgroundColor(Color.BLACK);
		setKeepScreenOn(true); // so screen doesnt shut off when game is left inactive
		detect = new PlayerGestureDetector(this); // creates gesture detector object
		//TODO what is this all about
		setOnTouchListener(detect);
		detect.setPlayer(player);
		background = drawStart(); // redraws play screen
		invalidate();
		activity.saveGame();
	}
	/**
	 * loads a new level, creates walls enemies etc.
	 */
	protected void loadLevel(int toLoad)
	{
		wallController.clearWallArrays();
		levelNum = toLoad;
		if(toLoad==1)
		{
			//LEVEL
			levelWidth = 1000; // height of level
			levelHeight = 1000; // width of level
			//PLAYER
			player.x = 30; // player start x
			player.y = 30; // player start y
			//ENEMIES
			imageLibrary.loadEnemy(55, "goblin_swordsman", 110, 70, 0); // length, name,width, height, index
			spriteController.makeEnemy(0, 269, 86); //type, x, y
			spriteController.makeEnemy(0, 358, 140);
			//WALLS
			/*wallController.makeWall_Rectangle(78, 122, 41, 24, true, false);
			wallController.makeWall_Rectangle(63, -20, 31, 142, true, true);
			wallController.makeWall_Rectangle(73, 238, 47, 62, true, true);
			wallController.makeWall_Rectangle(94, -19, 25, 152, true, false);
			wallController.makeWall_Rectangle(252, 269, 234, 62, true, true);
			wallController.makeWall_Rectangle(412, 82, 74, 250, true, true);
			wallController.makeWall_Rectangle(382, 133, 30, 83, true, false);
			wallController.makeWall_Circle(330, 297, 47, 1, false);
			wallController.makeWall_Rectangle(217, -15, 109, 81, true, false);
			wallController.makeWall_Rectangle(179, -32, 38, 63, true, true);
			wallController.makeWall_Rectangle(318, -41, 66, 63, true, true);*/
		}
		imageLibrary.loadLevel(toLoad, levelWidth, levelHeight);
	}
	protected void die()
	{
		//TODO
	}
	protected void pause()
	{
		//TODO
	}
	/**
	 * loads a new section of the current level
	 * @param level id of new section to load
	 */
	protected void loadLevelSection(int level)
	{
		wallController.clearWallArrays();
		levelNum = level;
		for(int i = 0; i < spriteController.powerUps.size(); i++)
		{
			if(spriteController.powerUps.get(i) != null) player.getPowerUp(spriteController.powerUps.get(i).ID);
		}			 // READS IN AND CREATES ENEMIES IN NEW SECTION, SAVES ENEMIES IN OLD SECTION
		ArrayList<int[]> tempSave = (ArrayList<int[]>)saveEnemyInformation.clone();
		int j = 0;
		for(int i = 0; i < saveEnemyInformation.size(); i++)
		{
			ArrayList<Enemy> enemies = spriteController.enemies;
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
			player.x = 30; // player start x
			player.y = 30; // player start y
			//ENEMIES
			imageLibrary.loadEnemy(95, "human_enemy", 100, 70, 1); // length, name,width, height, index
			spriteController.makeEnemy(1, 269, 86); //type, x, y
			spriteController.makeEnemy(1, 358, 140);
			spriteController.makeEnemy(1, 365, 204);
			spriteController.makeEnemy(2, 146, 61);
			spriteController.makeEnemy(2, 327, 231);
			//WALLS
			wallController.makeWall_Rectangle(78, 122, 41, 24, true, false);
			wallController.makeWall_Rectangle(63, -20, 31, 142, true, true);
			wallController.makeWall_Rectangle(73, 238, 47, 62, true, true);
			wallController.makeWall_Rectangle(94, -19, 25, 152, true, false);
			wallController.makeWall_Rectangle(252, 269, 234, 62, true, true);
			wallController.makeWall_Rectangle(412, 82, 74, 250, true, true);
			wallController.makeWall_Rectangle(382, 133, 30, 83, true, false);
			wallController.makeWall_Circle(330, 297, 47, 1, false);
			wallController.makeWall_Rectangle(217, -15, 109, 81, true, false);
			wallController.makeWall_Rectangle(179, -32, 38, 63, true, true);
			wallController.makeWall_Rectangle(318, -41, 66, 63, true, true);
		}
		imageLibrary.loadLevel(levelNum, levelWidth, levelHeight);
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
			spriteController.createEnemy(enemyData.get(i)); // CREATES SAVED ENEMIES
		}
	}
	/**
	 * ends a fight section with no saved enemies
	 */
	private void endFightSection()
	{
		spriteController.clearObjectArrays();
		wallController.clearWallArrays();
	}
	/**
	 * fixes hp bar so it is on screen
	 * @param minX small x value of bar
	 * @param maxX large x value of bar
	 * @return offset so bar is on screen
	 */
	protected int fixXBoundsHpBar(int minX, int maxX)
	{
		int offset = 0;
		if(minX < 90) // IF TOO FAR LEFT FIX
		{
			offset = 90 - minX;
		}
		else if(maxX > 390) // IF TOO FAR RIGHT FIX
		{
			offset = 390 - maxX;
		}
		return offset;
	}
	/**
	 * fixes hp bar so it is on screen
	 * @param minY small y value of bar
	 * @param maxY large y value of bar
	 * @return offset so bar is on screen
	 */
	protected int fixYBoundsHpBar(int minY, int maxY)
	{
		int offset = 0;
		if(minY < 10) // IF TOO UP LEFT FIX
		{
			offset = 10 - minY;
		}
		else if(maxY > 310) // IF TOO FAR DOWN FIX
		{
			offset = 310 - maxY;
		}
		return offset;
	}
	/**
	 * Draws hp, mp, sp, and cooldown bars for player and enemies
	 * @param g canvas to draw to
	 */
	protected void drawContestantStats(Canvas g)
	{
		paint.setAlpha(255);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(healthColor);
		g.drawRect(14, 169, 14 + (62 * player.getHp() / player.hpMax), 181, paint);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(cooldownColor);
		g.drawRect(404, 94, 404 + (int)((63 * player.abilityTimer_burst) / 500), 104, paint);
		g.drawRect(404, 199, 404 + (int)((63 * player.abilityTimer_roll) / 120), 209, paint);
		g.drawRect(404, 303, 404 + (int)((63 * player.abilityTimer_Proj_Tracker) / 91), 313, paint);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(151);
				if(player.abilityTimer_burst < 300)
				{
					g.drawRect(404, 42, 466, 104, paint);
				}
				if(player.abilityTimer_roll < 40)
				{
					g.drawRect(402, 147, 466, 209, paint);
				}
		paint.setAlpha(255);
		drawBitmapRotated(shootStick, g);
	}
	/**
	 * Sets deleted objects to null to be gc'd and tests player and enemy hitting arena bounds
	 */
	protected void frameCall()
	{
		playerHit++;
		playerBursted++;
		spriteController.frameCall();
		if(!player.deleted) player.frameCall();
		wallController.frameCall();
		invalidate();
	}
	protected boolean playerOnSquare(double x1, double y1, double width, double height)
	{
		double x2 = x1+width;
		double y2 = y1+height;
		return (player.x<x2&&player.x>x1&&player.y<y2&&player.y>y1);
	}
	/**
	 * returns distance squared between two objects
	 * @param x1 first x position
	 * @param y1 first y position
	 * @param x2 second x position
	 * @param y2 second y position
	 * @return distance between points squared
	 */
	protected double distSquared(double x1, double y1, double x2, double y2)
	{
		return Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
	}
	/**
	 * draws background of play screen
	 * @return bitmap of play screen
	 */
	protected Bitmap drawStart()
	{
		paint.setAlpha(255);
		Bitmap drawTo = Bitmap.createBitmap(480, 320, Bitmap.Config.ARGB_8888);
		Canvas g = new Canvas(drawTo);
		g.drawBitmap(imageLibrary.loadImage("menu_screen", 480, 320), 0, 0, paint);
		return drawTo;
	}
	/**
	 * draws the level and objects in it
	 * @return bitmap of level and objects
	 */
	protected Bitmap drawLevel()
	{
		Bitmap drawTo = Bitmap.createBitmap(levelWidth, levelHeight, Config.ARGB_8888);
		Canvas g = new Canvas(drawTo);
		for(int w = 0; w<levelWidth; w+=100)
		{
			for(int h = 0; h<levelHeight; h+=100)
			{
				drawBitmapLevel(imageLibrary.backDrop, w, h, g);
			}
		}
		Rect selection = new Rect(-curXShift, -curYShift, -curXShift+300, -curYShift+300);
		Rect onLevel = new Rect(-curXShift, -curYShift, -curXShift+300, -curYShift+300);
		g.drawBitmap(imageLibrary.currentLevel, selection, onLevel, paint);
		spriteController.drawStructures(g, paint, imageLibrary);
		spriteController.drawSprites(g, paint, imageLibrary, aoeRect);
		g.drawBitmap(imageLibrary.currentLevelTop, selection, onLevel, paint);

		if(player.powerUpTimer > 0)
		{
			drawBitmapLevel(imageLibrary.effects[player.powerID - 1], (int) player.x - 30, (int) player.y - 30, g);
		}
		spriteController.drawHealthBars(g, paint);
		return drawTo;
	}
	/**
	 * checks whether object is in view
	 * @param lowx objects low x
	 * @param lowy objects low y
	 * @param width objects width
	 * @param height objects height
	 * @return whether object is in view
	 */
	protected boolean inView(double x, double y, int width, int height)
	{
		x += curXShift;
		y += curYShift;
		return !(x > 300+width || x < -width || y > 300+height || y < -height);
	}
	/**
	 * checks whether enemy is in view
	 * @param x enemy x
	 * @param y enemy y
	 * @return whether enemy is in view
	 */
	protected boolean enemyInView(double x, double y)
	{
		return !(x + curXShift > 400 || x + curXShift < -100 || y + curYShift > 400 || y + curYShift < -100);
	}
	protected double getLevelWinningsMultiplier(int level)
	{
		if(level==0)
		{
			return 0;
		} else
		{
			return 1 + ((double)(level-1)/(double)10);
		}
	}
	protected String getLevelName(int level)
	{
		switch(level)
		{
		case 0:
			return "Tutorial";
		case 1:
			return "Broken Sanctuary";
		case 2:
			return "Beyond the Gate";
		case 3:
			return "Mouldy Tavern";
		case 4:
			return "The Chambers";
		case 5:
			return "Orientation";
		case 6:
			return "The Outpost";
		case 7:
			return "War Preparation";
		case 8:
			return "The Labyrinth";
		case 9:
			return "Back to Town";
		case 10:
			return "Temple of Fire";
		case 11:
			return "Temple of Ice";
		case 12:
			return "Temple of Earth";
		case 13:
			return "Goblin Nest";
		case 14:
			return "The Hordes Return";
		default:
			return "Default";
		}
	}
	@Override
	protected void onDraw(Canvas g)
	{
			g.translate(screenMinX, screenMinY);
			g.scale((float) screenDimensionMultiplier, (float) screenDimensionMultiplier);
			drawNotPaused(g);
	}
	/**
	 * draw normal unpaused screen
	 * @param g canvas to draw to
	 */
	private void drawNotPaused(Canvas g)
	{
		paint.setTextAlign(Align.LEFT);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.GRAY);
		g.drawRect(90, 10, 390, 310, paint);
		curXShift = 150 - (int) player.x;
		curYShift = 150 - (int) player.y;
			if(player.x < 150)
			{
				curXShift = 0;
			}
			if(player.y < 150)
			{
				curYShift = 0;
			}
			if(player.x > levelWidth - 150)
			{
				curXShift = 300 - levelWidth;
			}
			if(player.y > levelHeight - 150)
			{
				curYShift = 300 - levelHeight;
			}
		g.drawBitmap(drawLevel(), curXShift + 90, curYShift + 10, paint);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		g.drawRect(-1000, -1000, 0, 1320, paint);
		g.drawRect(480, -1000, 1480, 1320, paint);
		g.drawRect(-1000, -1000, 1480, 0, paint);
		g.drawRect(-1000, 320, 1480, 1320, paint);
		if(playerBursted<6)
		{
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.FILL);
			paint.setAlpha(255-(30*playerBursted));
			g.drawRect(90, 10, 390, 310, paint);
		}
		if(playerHit<6)
		{
			paint.setColor(Color.RED);
			paint.setStyle(Paint.Style.FILL);
			paint.setAlpha(100-(15*playerHit));
			g.drawRect(90, 10, 390, 310, paint);
		}
		paint.setAlpha(255);
		paint.setColor(Color.GREEN);
		paint.setAlpha(255);
		g.drawBitmap(background, 0, 0, paint);
		drawContestantStats(g);
		paint.setStyle(Paint.Style.STROKE);
		if(player.powerUpTimer > 0)
		{
			g.drawBitmap(imageLibrary.powerUpBigs[player.powerID - 1], 10, 25, paint);
		}
	}
	/**
	 * Starts warning label
	 * @param warning
	 */
	protected void startWarningImediate(String warning)
	{
		AlertDialog.Builder bld = new AlertDialog.Builder(context);
		bld.setMessage(warning);
		bld.setNeutralButton("OK", null);
		bld.create().show();
	}
	
	/**
	 * returns distance between two points
	 *  @param x1 first x
	 * @param y1 first y
	 * @param x2 second x
	 * @param y2 second y
	 * @return distance between points
	 */
	protected double getDistance(double x1, double y1, double x2, int y2)
	{
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	protected double visualX(double x)
	{
		return(x - screenMinX) / screenDimensionMultiplier;
	}
	/**
	 * converts value from click y point to where on the screen it would be
	 * @param y y value of click
	 * @return y position of click on screen
	 */
	protected double visualY(double y)
	{
		return((y - screenMinY) / screenDimensionMultiplier);
	}
	/**
	 * returns whether a point clicked is on  the screen
	 * @param x x position
	 * @param y y position
	 * @return whether it is on screen
	 */
	protected boolean pointOnScreen(double x, double y)
	{
		x = visualX(x);
		y = visualY(y);
		if(x > 90 && x < 390 && y > 10 && y < 310)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 * returns whether a point is on a given square
	 * @param x x position
	 * @param y y position
	 * @param lowX left hand side of square
	 * @param lowY top of square
	 * @param highX right hand side of square
	 * @param highY bottom of square
	 * @return whether it is on square
	 */
	protected boolean pointOnSquare(double x, double y, double lowX, double lowY, double highX, double highY)
	{
		x = visualX(x);
		y = visualY(y);
		if(x > lowX && x < highX && y > lowY && y < highY)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 * returns whether a point is on a given circle
	 * @param x x position
	 * @param y y position
	 * @param midX x position
	 * @param midY y position
	 * @param radius radius of circle
	 * @return whether it is on circle
	 */
	protected boolean pointOnCircle(double x, double y, double midX, double midY, double radius)
	{
		x = visualX(x);
		y = visualY(y);
		if(Math.sqrt(Math.pow(x - midX, 2) + Math.pow(y - midY, 2)) < radius)
		{
			return true;
		}
		else
		{
			return false;
		}
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
	/**
	 * Replaces canvas.drawBitmap(Bitmap, Matrix, Paint) and auto scales and rotates image based on drawnSprite values
	 */
	protected void drawBitmapRotated(Sprite sprite, Canvas g)
	{
		rotateImages.reset();
		rotateImages.postTranslate(-sprite.width / 2, -sprite.height / 2);
		rotateImages.postRotate((float) sprite.rotation);
		rotateImages.postTranslate((float) sprite.x, (float) sprite.y);
		g.drawBitmap(sprite.image, rotateImages, paint);
		sprite = null;
	}
	/**
	 * Replaces canvas.drawBitmap(Bitmap, Matrix, Paint) and auto scales and only draws object if it is in view
	 */
	protected void drawBitmapLevel(Bitmap picture, int x, int y, Canvas g)
	{
		if(inView(x, y, picture.getWidth(), picture.getHeight())) g.drawBitmap(picture, x, y, paint);
	}
}