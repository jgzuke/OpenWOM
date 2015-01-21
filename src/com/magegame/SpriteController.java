
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
import com.spritelib.SpriteDrawer;
public final class SpriteController extends SpriteDrawer
{
	protected Controller control;
	protected ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	protected ArrayList<Structure> structures = new ArrayList<Structure>();
	protected ArrayList<Proj_Tracker_Enemy> proj_TrackerEs = new ArrayList<Proj_Tracker_Enemy>();
	protected ArrayList<Proj_Tracker_Player> proj_TrackerPs = new ArrayList<Proj_Tracker_Player>();
	protected ArrayList<Proj_Tracker_AOE_Player> proj_TrackerP_AOEs = new ArrayList<Proj_Tracker_AOE_Player>();
	protected ArrayList<Proj_Tracker_AOE_Enemy> proj_TrackerE_AOEs = new ArrayList<Proj_Tracker_AOE_Enemy>();
	protected Bitmap playerBlessing;
	/**
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	public SpriteController(Context contextSet, Controller controlSet)
	{
		super();
		control = controlSet;
	}
	/*
	 * 
	 */
	void clearObjectArrays()
	{
		enemies.clear();
		structures.clear();
		proj_TrackerEs.clear();
		proj_TrackerPs.clear();
		proj_TrackerP_AOEs.clear();
		proj_TrackerE_AOEs.clear();
	}
	/**
	 * creates an enemy based off of saved info
	 * @param info array of stored values
	 * @param index which spot in enemy array to populate
	 */
	void createEnemy(int[] info)
	{
		makeEnemy(info[0], info[1], info[2], info[3]);
		if(info[4] != 0) // if enemy has set health change it, otherwise leave as starting health
		{
			enemies.get(enemies.size()-1).hp = info[4];
		}
	}
	protected void makeEnemy(int type, int x, int y, int r)
	{
		switch(type)
		{
		case 0:
			enemies.add(new Enemy_Sheild(control, x, y, r, 3000, type)); //x, y, hp, sick, type is ImageIndex
			break;
		case 1:
			enemies.add(new Enemy_Archer(control, x, y, r, 1700, type));
			break;
		case 2:
			enemies.add(new Enemy_Mage(control, x, y, r, 700, type));
			break;
		case 3:
			enemies.add(new Enemy_Sentry(control, x, y, r, 1700, type));
			break;
		case 4:
			enemies.add(new Enemy_Rogue(control, x, y, r, 1100, type));
			break;
		case 5:
			enemies.add(new Enemy_Cleric(control, x, y, r, 700, type));
			break;
		}
	}
	/**
	 * calls all sprites frame methods
	 */
	protected void frameCall()
	{
		for(int i = 0; i < proj_TrackerEs.size(); i++)
		{
			if(proj_TrackerEs.get(i) != null)
			{
				if(proj_TrackerEs.get(i).deleted)
				{
					proj_TrackerEs.remove(i);
				}
				else
				{
					proj_TrackerEs.get(i).frameCall();
				}
			}
		}
		for(int i = 0; i < proj_TrackerPs.size(); i++)
		{
			if(proj_TrackerPs.get(i) != null)
			{
				if(proj_TrackerPs.get(i).deleted)
				{
					proj_TrackerPs.remove(i);
				}
				else
				{
					proj_TrackerPs.get(i).frameCall();
				}
			}
		}
		for(int i = 0; i < proj_TrackerE_AOEs.size(); i++)
		{
			if(proj_TrackerE_AOEs.get(i) != null)
			{
				if(proj_TrackerE_AOEs.get(i).deleted)
				{
					proj_TrackerE_AOEs.remove(i);
				}
				else
				{
					proj_TrackerE_AOEs.get(i).frameCall();
				}
			}
		}
		for(int i = 0; i < proj_TrackerP_AOEs.size(); i++)
		{
			if(proj_TrackerP_AOEs.get(i) != null)
			{
				if(proj_TrackerP_AOEs.get(i).deleted)
				{
					proj_TrackerP_AOEs.remove(i);
				}
				else
				{
					proj_TrackerP_AOEs.get(i).frameCall();
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
					i--;
				} else
				{
					if(enemies.get(i).x < 10) enemies.get(i).x = 10;
					if(enemies.get(i).x > control.levelController.levelWidth - 10) enemies.get(i).x = (control.levelController.levelWidth - 10);
					if(enemies.get(i).y < 10) enemies.get(i).y = 10;
					if(enemies.get(i).y > control.levelController.levelHeight - 10) enemies.get(i).y = (control.levelController.levelHeight - 10);
					enemies.get(i).frameCall();
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
					structures.get(i).frameCall();
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
					g.drawRect(minX, minY, minX + (40 * enemies.get(i).hp / enemies.get(i).hpMax), maxY, paint);
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
		drawFlat(control.player, imageLibrary.isPlayer, g, paint);
		draw(control.player, g, paint);
		if(control.player.blessing!=0) g.drawBitmap(playerBlessing, (int)control.player.x-40, (int)control.player.y-40, paint);
		for(int i = 0; i < enemies.size(); i++)
		{
			if(enemies.get(i) != null)
			{
				draw(enemies.get(i), g, paint);
			}
		}
		for(int i = 0; i < proj_TrackerPs.size(); i++)
		{
			if(proj_TrackerPs.get(i) != null)
			{
				draw(proj_TrackerPs.get(i), g, paint);
			}
		}
		for(int i = 0; i < proj_TrackerEs.size(); i++)
		{
			if(proj_TrackerEs.get(i) != null)
			{
				draw(proj_TrackerEs.get(i), g, paint);
			}
		}
		for(int i = 0; i < proj_TrackerE_AOEs.size(); i++)
		{
			if(proj_TrackerE_AOEs.get(i) != null)
			{
				aoeRect.top = (int)(proj_TrackerE_AOEs.get(i).y - (proj_TrackerE_AOEs.get(i).getHeight() / 2.5));
				aoeRect.bottom = (int)(proj_TrackerE_AOEs.get(i).y + (proj_TrackerE_AOEs.get(i).getHeight() / 2.5));
				aoeRect.left = (int)(proj_TrackerE_AOEs.get(i).x - (proj_TrackerE_AOEs.get(i).getWidth() / 2.5));
				aoeRect.right = (int)(proj_TrackerE_AOEs.get(i).x + (proj_TrackerE_AOEs.get(i).getWidth() / 2.5));
				paint.setAlpha(proj_TrackerE_AOEs.get(i).getAlpha());
				drawRect(proj_TrackerE_AOEs.get(i).image, aoeRect, g, paint);
			}
		}
		for(int i = 0; i < proj_TrackerP_AOEs.size(); i++)
		{
			if(proj_TrackerP_AOEs.get(i) != null)
			{
				aoeRect.top = (int)(proj_TrackerP_AOEs.get(i).y - (proj_TrackerP_AOEs.get(i).getHeight() / 2.5));
				aoeRect.bottom = (int)(proj_TrackerP_AOEs.get(i).y + (proj_TrackerP_AOEs.get(i).getHeight() / 2.5));
				aoeRect.left = (int)(proj_TrackerP_AOEs.get(i).x - (proj_TrackerP_AOEs.get(i).getWidth() / 2.5));
				aoeRect.right = (int)(proj_TrackerP_AOEs.get(i).x + (proj_TrackerP_AOEs.get(i).getWidth() / 2.5));
				paint.setAlpha(proj_TrackerP_AOEs.get(i).getAlpha());
				drawRect(proj_TrackerP_AOEs.get(i).image, aoeRect, g, paint);
			}
		}
		paint.setAlpha(255);
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
		proj_TrackerEs.add(new Proj_Tracker_Enemy(control, (int) (x+xVel*2), (int) (y+yVel*2), power, xVel, yVel, rotation));
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
		proj_TrackerPs.add(new Proj_Tracker_Player(control, (int)x, (int)y, power, Vel, rotation, this));
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
		proj_TrackerE_AOEs.add(new Proj_Tracker_AOE_Enemy(control, (int) x, (int) y, power, true, this));
		if(!damaging) proj_TrackerE_AOEs.get(proj_TrackerE_AOEs.size()-1).damaging = false;
	}
	/**
	 * creates a player AOE explosion
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 */
	protected void createProj_TrackerPlayerAOE(double x, double y, double power, boolean damaging)
	{
		proj_TrackerP_AOEs.add(new Proj_Tracker_AOE_Player(control, (int) x, (int) y, power, true, this));
		if(!damaging) proj_TrackerP_AOEs.get(proj_TrackerP_AOEs.size()-1).damaging = false;
	}
	/**
	 * creates an enemy burst
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 */
	protected void createProj_TrackerEnemyBurst(double x, double y, double power)
	{
		proj_TrackerE_AOEs.add(new Proj_Tracker_AOE_Enemy(control, (int) x, (int) y, power, false, this));
	}
	/**
	 * creates a player burst
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 */
	protected void createProj_TrackerPlayerBurst(double x, double y, double power)
	{
		proj_TrackerP_AOEs.add(new Proj_Tracker_AOE_Player(control, (int) x, (int) y, power, false, this));
	}
	@Override
	protected boolean onScreen(double x, double y, int width, int height) {
		// TODO Auto-generated method stub
		return true;
	}
}