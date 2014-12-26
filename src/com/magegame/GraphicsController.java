
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
import android.app.Activity;
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
public final class GraphicsController
{
	private int playScreenSize = 400;
	protected int screenMinX;
	protected int screenMinY;
	protected int curXShift;
	protected int curYShift;
	protected double screenDimensionMultiplier;
	protected Paint paint = new Paint();
	protected Matrix rotateImages = new Matrix();
	private Rect aoeRect = new Rect();
	private Bitmap background;
	private int healthColor = Color.rgb(150, 0, 0);
	private int cooldownColor = Color.rgb(190, 190, 0);
	protected Sprite shootStick;
	protected int playerHit=0;
	protected int playerBursted = 0;
	private Typeface magicMedieval; 
	private Controller control;
	private ImageLibrary imageLibrary;
	private SpriteController spriteController;
	private WallController wallController;
	private LevelController levelController;
	private Player player;
	private Context context;
	/** 
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	public GraphicsController(Controller c, ImageLibrary i, SpriteController s, WallController w, LevelController l, Player p, Context co, StartActivity st)
	{
		control = c;
		imageLibrary = i;
		spriteController = s;
		wallController = w;
		levelController = l;
		player = p;
		context = co;
		setUpPaintStuff(st);
	}
	private void setUpPaintStuff(StartActivity activity)
	{
		shootStick = new Graphic_shootStick(imageLibrary.loadImage("icon_shoot", 70, 35));
		screenMinX = activity.screenMinX;
		screenMinY = activity.screenMinY;
		screenDimensionMultiplier = activity.screenDimensionMultiplier;
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		background = drawStart(); // redraws play screen
	}
	protected void frameCall()
	{
		playerHit++;
		playerBursted++;
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
		Bitmap drawTo = Bitmap.createBitmap(levelController.levelWidth, levelController.levelHeight, Config.ARGB_8888);
		Canvas g = new Canvas(drawTo);
		for(int w = 0; w<levelController.levelWidth; w+=100)
		{
			for(int h = 0; h<levelController.levelHeight; h+=100)
			{
				drawBitmapLevel(imageLibrary.backDrop, w, h, g);
			}
		}
		g.drawBitmap(imageLibrary.currentLevel, 0, 0, paint);
		spriteController.drawStructures(g, paint, imageLibrary);
		spriteController.drawSprites(g, paint, imageLibrary, aoeRect);
		g.drawBitmap(imageLibrary.currentLevelTop, 0, 0, paint);

		if(player.powerUpTimer > 0)
		{
			drawBitmapLevel(imageLibrary.effects[player.powerID - 1], (int) player.x - 30, (int) player.y - 30, g);
		}
		spriteController.drawHealthBars(g, paint);
		return drawTo;
	}
	protected void drawScreen(Canvas g)
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
		int middle = playScreenSize/2;
		curXShift = middle - (int) player.x;
		curYShift = middle - (int) player.y;
		if(player.x < middle) curXShift = 0;
		if(player.y < middle) curYShift = 0;
		if(player.x > levelController.levelWidth - middle) curXShift = playScreenSize - levelController.levelWidth;
		if(player.y > levelController.levelHeight - middle) curYShift = playScreenSize - levelController.levelHeight;
		Rect src = new Rect(-curXShift, -curYShift, -curXShift+playScreenSize, -curYShift+playScreenSize);
		Rect dst = new Rect(90, 10, 390, 310);
		g.drawBitmap(drawLevel(), src, dst, paint);
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
		/*if(inView(x, y, picture.getWidth(), picture.getHeight()))*/ g.drawBitmap(picture, x, y, paint);
	}
}