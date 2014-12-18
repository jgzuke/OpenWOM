
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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

import com.spritelib.Sprite;
import com.spritelib.SpriteDrawer;
public final class SpriteController extends SpriteDrawer
{
	private Controller control;
	private Context context;
	protected ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	protected ArrayList<Structure> structures = new ArrayList<Structure>();
	protected ArrayList<PowerUp> powerUps = new ArrayList<PowerUp>();
	protected ArrayList<Proj_Tracker> proj_Trackers = new ArrayList<Proj_Tracker>();
	protected ArrayList<Proj_Tracker_AOE> proj_Tracker_AOEs = new ArrayList<Proj_Tracker_AOE>();
	/**
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	public SpriteController(Context contextSet, Controller controlSet)
	{
		super();
		control = controlSet;
		context = contextSet;
	}
	/*
	 * 
	 */
	void clearObjectArrays()
	{
		control.saveEnemyInformation.clear();
		enemies.clear();
		structures.clear();
		powerUps.clear();
		proj_Trackers.clear();
		proj_Tracker_AOEs.clear();
	}
	/**
	 * creates an enemy based off of saved info
	 * @param info array of stored values
	 * @param index which spot in enemy array to populate
	 */
	void createEnemy(int[] info)
	{
		makeEnemy(info[0], info[1], info[2], info[4]);
		if(info[3] != 0) // if enemy has set health change it, otherwise leave as starting health
		{
			enemies.get(enemies.size()-1).hp = info[3];
		}
	}
	protected void makeEnemy(int type, int x, int y, int ImageIndex)
	{
		if(type==1)
		{
			enemies.add(new Enemy_Sheild(control, x, y, 2000, ImageIndex)); //x, y, hp, sick
		} else if(type==2)
		{
			enemies.add(new Enemy_Default(control, x, y, 2000, ImageIndex)); //x, y, hp, sick
		}
	}
	/**
	 * calls all sprites frame methods
	 */
	protected void frameCall()
	{
		for(int i = 0; i < proj_Trackers.size(); i++)
		{
			if(proj_Trackers.get(i) != null)
			{
				if(proj_Trackers.get(i).deleted)
				{
					proj_Trackers.remove(i);
				}
				else
				{
					proj_Trackers.get(i).frameCall();
				}
			}
		}
		for(int i = 0; i < proj_Tracker_AOEs.size(); i++)
		{
			if(proj_Tracker_AOEs.get(i) != null)
			{
				if(proj_Tracker_AOEs.get(i).deleted)
				{
					proj_Tracker_AOEs.remove(i);
				}
				else
				{
					proj_Tracker_AOEs.get(i).frameCall();
				}
			}
		}
		for(int i = 0; i < powerUps.size(); i++)
		{
			if(powerUps.get(i) != null)
			{
				if(powerUps.get(i).deleted)
				{
					powerUps.remove(i);
				}
				else
				{
					powerUps.get(i).frameCall();
				}
			}
		}
		for(int i = 0; i < enemies.size(); i++)
		{
			if(enemies.get(i) != null)
			{
				if(enemies.get(i).deleted)
				{
					enemies.remove(i);
				}
				else
				{
					enemies.get(i).levelCurrentPosition = 0;
					enemies.get(i).pathedToHitLength = 0;
					if(control.enemyInView(enemies.get(i).x, enemies.get(i).y))
					{
						enemies.get(i).frameCall();
						if(enemies.get(i) != null)
						{
							if(enemies.get(i).x < 10) enemies.get(i).x = 10;
							if(enemies.get(i).x > control.levelWidth - 10) enemies.get(i).x = (control.levelWidth - 10);
							if(enemies.get(i).y < 10) enemies.get(i).y = 10;
							if(enemies.get(i).y > control.levelHeight - 10) enemies.get(i).y = (control.levelHeight - 10);
						}
					}
				}
			}
		}
		for(int i = 0; i < structures.size(); i++)
		{
			if(structures.get(i) != null)
			{
				if(structures.get(i).deleted)
				{
					structures.remove(i);
				}
				else
				{
					if(control.enemyInView(structures.get(i).x, structures.get(i).y))
					{
						structures.get(i).frameCall();
					}
				}
			}
		}
	}
	/**
	 * draws all enemy health bars
	 * @param g canvas to draw to
	 */
	protected void drawHealthBars(Canvas g, Paint paint)
	{
		int minX;
		int maxX;
		int minY;
		int maxY;
		//int offset;
		for(int i = 0; i < enemies.size(); i++)
		{
			if(enemies.get(i) != null)
			{
					minX = (int) enemies.get(i).x - 20;
					maxX = (int) enemies.get(i).x + 20;
					minY = (int) enemies.get(i).y - 30;
					maxY = (int) enemies.get(i).y - 20;
					paint.setColor(Color.RED);
					paint.setStyle(Paint.Style.FILL);
					g.drawRect(minX, minY, minX + (40 * enemies.get(i).getHp() / enemies.get(i).getHpMax()), maxY, paint);
					paint.setColor(Color.BLACK);
					paint.setStyle(Paint.Style.STROKE);
					g.drawRect(minX, minY, maxX, maxY, paint);
			}
		}
		for(int i = 0; i < structures.size(); i++)
		{
			if(structures.get(i) != null)
			{
				minX = (int) structures.get(i).x - 20;
				maxX = (int) structures.get(i).x + 20;
				minY = (int) structures.get(i).y - 30;
				maxY = (int) structures.get(i).y - 20;
				paint.setColor(Color.BLUE);
				paint.setStyle(Paint.Style.FILL);
				g.drawRect(minX, minY, minX + (40 * structures.get(i).hp / structures.get(i).hpMax), maxY, paint);
				paint.setColor(Color.BLACK);
				paint.setStyle(Paint.Style.STROKE);
				g.drawRect(minX, minY, maxX, maxY, paint);
			}
		}
	}
	protected void drawStructures(Canvas g, Paint paint, ImageLibrary imageLibrary)
	{
		for(int i = 0; i < structures.size(); i++)
		{
			drawFlat(structures.get(i), g, paint);
		}
	}
	protected void drawSprites(Canvas g, Paint paint, ImageLibrary imageLibrary, Rect aoeRect)
	{
		if(control.player != null)
		{
			drawFlat(control.player, imageLibrary.isPlayer, g, paint);
			draw(control.player, g, paint);
		}
		for(int i = 0; i < enemies.size(); i++)
		{
			if(enemies.get(i) != null)
			{
				if(enemies.get(i).keyHolder)
				{
					control.drawBitmapLevel(imageLibrary.haskey, (int) enemies.get(i).x - 20, (int) enemies.get(i).y - 20, g);
				}
				draw(enemies.get(i), g, paint);
			}
		}
		for(int i = 0; i < proj_Trackers.size(); i++)
		{
			if(proj_Trackers.get(i) != null)
			{
				draw(proj_Trackers.get(i), g, paint);
			}
		}
		for(int i = 0; i < proj_Tracker_AOEs.size(); i++)
		{
			if(proj_Tracker_AOEs.get(i) != null)
			{
				aoeRect.top = (int)(proj_Tracker_AOEs.get(i).y - (proj_Tracker_AOEs.get(i).getHeight() / 2.5));
				aoeRect.bottom = (int)(proj_Tracker_AOEs.get(i).y + (proj_Tracker_AOEs.get(i).getHeight() / 2.5));
				aoeRect.left = (int)(proj_Tracker_AOEs.get(i).x - (proj_Tracker_AOEs.get(i).getWidth() / 2.5));
				aoeRect.right = (int)(proj_Tracker_AOEs.get(i).x + (proj_Tracker_AOEs.get(i).getWidth() / 2.5));
				paint.setAlpha(proj_Tracker_AOEs.get(i).getAlpha());
				drawRect(proj_Tracker_AOEs.get(i).image, aoeRect, g, paint);
			}
		}
		paint.setAlpha(255);
		for(int i = 0; i < powerUps.size(); i++)
		{
			if(powerUps.get(i) != null)
			{
				if(powerUps.get(i).ID==8)
				{
					drawFlat(powerUps.get(i), imageLibrary.haskey, g, paint);
				}
				drawFlat(powerUps.get(i), g, paint);
			}
		}
	}
	/**
	 * creates an enemy power ball
	 * @param rotation rotation of Proj_Tracker
	 * @param xVel horizontal velocity of ball
	 * @param yVel vertical velocity of ball
	 * @param power power of ball
	 * @param x x position
	 * @param y y position
	 */
	protected void createProj_TrackerEnemy(double rotation, double xVel, double yVel, int power, double x, double y)
	{
		proj_Trackers.add(new Proj_Tracker_Enemy(control, (int) (x+xVel*2), (int) (y+yVel*2), power, xVel, yVel, rotation));
	}
	/**
	 * creates a consumable the player can pick up
	 * @param X x position
	 * @param Y y position
	 * @param ID 0:random power, 1-6:power, 7:coin1, 8:key, 9:coin5, 10:coin20
	 */
	protected void createConsumable(double X, double Y, int ID) // 0: random powerup or
	{															// 1-6:powerups 7:coin1
		powerUps.add(new PowerUp(control, X, Y, ID));				// 9: coin5, 10:coin20, 8:key 
		Log.e("dropped", "dropped");
	}
	/**
	 * creates a player power ball
	 * @param rotation rotation of bolt
	 * @param xVel horizontal velocity of bolt
	 * @param yVel vertical velocity of bolt
	 * @param power power of bolt
	 * @param x x position
	 * @param y y position
	 */
	protected void createProj_TrackerPlayer(double rotation, double Vel, int power, double x, double y)
	{
		proj_Trackers.add(new Proj_Tracker_Player(control, (int)x, (int)y, power, Vel, rotation, this));
	}
	/**
	 * creates an emeny AOE explosion
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 * @param damaging whether it damages player
	 */
	protected void createProj_TrackerEnemyAOE(double x, double y, double power, boolean damaging)
	{
		proj_Tracker_AOEs.add(new Proj_Tracker_AOE_Enemy(control, (int) x, (int) y, power, true, this));
		if(!damaging) proj_Tracker_AOEs.get(proj_Tracker_AOEs.size()-1).damaging = false;
	}
	/**
	 * creates a player AOE explosion
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 */
	protected void createProj_TrackerPlayerAOE(double x, double y, double power, boolean damaging)
	{
		proj_Tracker_AOEs.add(new Proj_Tracker_AOE_Player(control, (int) x, (int) y, power, true, this));
		if(!damaging) proj_Tracker_AOEs.get(proj_Tracker_AOEs.size()-1).damaging = false;
	}
	/**
	 * creates an enemy burst
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 */
	protected void createProj_TrackerEnemyBurst(double x, double y, double power)
	{
		proj_Tracker_AOEs.add(new Proj_Tracker_AOE_Enemy(control, (int) x, (int) y, power, false, this));
	}
	/**
	 * creates a player burst
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 */
	protected void createProj_TrackerPlayerBurst(double x, double y, double power)
	{
		proj_Tracker_AOEs.add(new Proj_Tracker_AOE_Player(control, (int) x, (int) y, power, false, this));
	}
	/**
		 * checks whether object is in view
		 * @param lowx objects low x
		 * @param lowy objects low y
		 * @param width objects width
		 * @param height objects height
		 * @return whether object is in view
		 */
	@Override
	protected boolean onScreen(double x, double y, int width, int height)
	{
		return control.inView(x, y, width, height);
	}
}