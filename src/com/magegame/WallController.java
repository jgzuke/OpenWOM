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
import java.util.ArrayList;
public final class WallController
{
	private Controller control;
	protected boolean [][][] pathing; //x, y, isFree, left, right, up, down
	private ArrayList<Wall_Rectangle> wallRects = new ArrayList<Wall_Rectangle>();
	private ArrayList<Wall_Ring> wallRings = new ArrayList<Wall_Ring>();
	private ArrayList<Wall_Circle> wallCircles = new ArrayList<Wall_Circle>();
	private ArrayList<int[]> wallPassageValues = new ArrayList<int[]>(); // int[] is x1, x2, y1, y2, and correspondant ring position
	private ArrayList<int[]> wallRingValues = new ArrayList<int[]>(); // int[] is x, y, radiusInner, radiusOuter, tall or not
	private ArrayList<int[]> wallRectValues = new ArrayList<int[]>(); // int[] is x1, x2, y1, y2, tall or not
	private ArrayList<int[]> wallCircleValues = new ArrayList<int[]>(); // int[] is x, y, radius, tall or not
	private int extraWidth = 3;
	/**
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	public WallController(Context contextSet, Controller controlSet)
	{
		control = controlSet;
	}
	protected void frameCall()
	{
		for(int i = 0; i < wallRects.size(); i++) wallRects.get(i).frameCall();
		for(int i = 0; i < wallCircles.size(); i++) wallCircles.get(i).frameCall();
		for(int i = 0; i < wallRings.size(); i++) wallRings.get(i).frameCall();
	}
	protected void clearWallArrays()
	{
		wallPassageValues.clear();
		wallRingValues.clear();
		wallRectValues.clear();
		wallCircleValues.clear();
		wallRects.clear();
		wallRings.clear();
		wallCircles.clear();
		//TODO
	}
	/**
	 * creates a rectangle wall object
	 * @param x x position
	 * @param y y position
	 * @param width wall width
	 * @param height wall height
	 * @param HitPlayer whether wall interacts with player
	 * @param tall whether wall is tall enough to block projectiles
	 * @return wall object
	 */
	protected void makeWall_Rectangle(int x, int y, int width, int height, boolean tall)
	{
		wallRects.add(new Wall_Rectangle(control, x-extraWidth, y-extraWidth, width+(2*extraWidth), height+(2*extraWidth), tall));
		if(tall)setORect(x, x+width, y, y+height, 1);
		else setORect(x, x+width, y, y+height, 0);
	}
	/**
	 * creates a ring wall object
	 * @param x x position
	 * @param y y position
	 * @param radIn inner radius
	 * @param radOut outer radius
	 * @return wall object
	 */
	protected void makeWall_Ring(int x, int y, int radIn, int radOut, boolean tall)
	{
		wallRings.add(new Wall_Ring(control, x, y, radIn-extraWidth, radOut+extraWidth, tall));
		if(tall)setORing(x, y, radIn, radOut, 1);
		else setORing(x, y, radIn, radOut, 0);
	}
	/**
	 * creates a passage through a ring
	 * @param x x position
	 * @param y y position
	 * @param width passage width
	 * @param height passage height
	 */
	protected void makeWall_Pass(int x, int y, int width, int height, boolean tall, int ringID)
	{
		if(tall)setOPassage(x, x+width, y, y+height, 1, ringID);
		else setOPassage(x, x+width, y, y+height, 0, ringID);
	}
	/**
	 * creates circular wall object
	 * @param x x position
	 * @param y y position
	 * @param rad radius
	 * @param ratio ratio between width and height
	 * @param tall whether object is tall enough to block projectiles
	 * @return wall object
	 */
	protected void makeWall_Circle(int x, int y, int rad, boolean tall)
	{
		wallCircles.add(new Wall_Circle(control, x, y, rad + extraWidth, tall));
		if(tall) setOCirc(x, y, rad, 1);
		else setOCirc(x, y, rad, 0);
	}
	/**
	 * checks whether a projectile could travel between two points
	 * @param x1 first x
	 * @param y1 first y
	 * @param x2 second x
	 * @param y2 second y
	 * @return whether it could travel between points
	 */
	protected boolean checkObstructionsPoint(float x1, float y1, float x2, float y2, boolean objectOnGround, int expand)
	{
		expand /= 2;
		if(x1 < 0 || x1 > control.levelController.levelWidth || y1 < 0 || y1 > control.levelController.levelHeight)
		{
			return true;
		}
		if(x2 < 0 || x2 > control.levelController.levelWidth || y2 < 0 || y2 > control.levelController.levelHeight)
		{
			return true;
		}
		float m1 = (y2 - y1) / (x2 - x1);
		float b1 = y1 - (m1 * x1);
		float circM = -(1 / m1);
		float circB;
		float tempX;
		float tempY;
		for(int i = 0; i < wallCircleValues.size(); i++)
		{
			int [] values = wallCircleValues.get(i).clone();
			if(values[3]==1||objectOnGround) // OBJECT IS TALL OR OBJECT ON GROUND
			{
				values[2]+=expand;
				circB = values[1] - (circM * values[0]);
				tempX = (circB - b1) / (m1 - circM);
				if(x1 < tempX && tempX < x2)
				{
					tempY = (circM * tempX) + circB;
					if(distanceSqr(tempX, values[0], tempY, values[1]) < Math.pow(values[2], 2))
					{
						return true;
					}
				}
			}
		}
		for(int i = 0; i < wallRingValues.size(); i++)
		{
			int [] values = wallRingValues.get(i).clone();
			if(values[4]==1||objectOnGround) // OBJECT IS TALL
			{
				double dist1 = distanceSqr(x1, values[0], y1, values[1]);
				double dist2 = distanceSqr(x2, values[0], y2, values[1]);
				double dist = Math.pow((values[2]+values[3])/2, 2);
				if(dist1>dist&&dist2>dist) // if both outside
				{
					circB = values[1] - (circM * values[0]);
					tempX = (circB - b1) / (m1 - circM);
					if(x1 < tempX && tempX < x2)
					{
						tempY = (circM * tempX) + circB;
						if(distanceSqr(tempX, values[0], tempY, values[1]) < dist)
						{
							return true;
						}
					}
				} else if(dist1>dist||dist2>dist) // if only one outside
				{
					if(!checkObstructionsPath(x1, y1, x2, y2, objectOnGround, expand, i)) return true;
				}
			}
		}
		if(x1 > x2)
		{
			tempX = x1;
			x1 = x2;
			x2 = tempX;
		}
		if(y1 > y2)
		{
			tempY = y1;
			y1 = y2;
			y2 = tempY;
		}
		for(int i = 0; i < wallRectValues.size(); i++)
		{
			int [] values = wallRectValues.get(i).clone();
			if(values[4]==1||objectOnGround) // OBJECT IS TALL
			{
				values[0]-=expand;
				values[1]+=expand;
				values[2]-=expand;
				values[3]+=expand;
					//Right and left Checks
					if(x1 < values[0] && values[0] < x2) // if left sid of wall 
					{
						tempY = (m1 * values[0]) + b1;
						if(values[2] < tempY && tempY < values[3])
						{
							return true;
						}
					}
					if(x1 < values[1] && values[1] < x2)
					{
						tempY = (m1 * values[1]) + b1;
						if(values[2] < tempY && tempY < values[3])
						{
							return true;
						}
					}
					//Top and Bottom checks
					if(y1 < values[2] && values[2] < y2)
					{
						tempX = (values[2] - b1) / m1;
						if(values[0] < tempX && tempX < values[1])
						{
							return true;
						}
					} else if(y1 < values[3] && values[3] < y2)
					{
						tempX = (values[2] - b1) / m1;
						if(values[0] < tempX && tempX < values[1])
						{
							return true;
						}
					}
			}
		}
		return false;
	}
	protected boolean checkObstructionsPath(float x1, float y1, float x2, float y2, boolean objectOnGround, int expand, int ringID)
	{
		float m1 = (y2 - y1) / (x2 - x1);
		float b1 = y1 - (m1 * x1);
		float tempX;
		float tempY;
		if(x1 > x2)
		{
			tempX = x1;
			x1 = x2;
			x2 = tempX;
		}
		if(y1 > y2)
		{
			tempY = y1;
			y1 = y2;
			y2 = tempY;
		}
		for(int i = 0; i < wallPassageValues.size(); i++)
		{
			int [] values = wallPassageValues.get(i).clone();
			if(values[5] == ringID && (values[4]==1||objectOnGround)) // OBJECT IS TALL
			{
				values[0]-=expand;
				values[1]+=expand;
				values[2]-=expand;
				values[3]+=expand;
					//Right and left Checks
					if(x1 < values[0] && values[0] < x2) // if left sid of wall 
					{
						tempY = (m1 * values[0]) + b1;
						if(values[2] < tempY && tempY < values[3])
						{
							return true;
						}
					}
					if(x1 < values[1] && values[1] < x2)
					{
						tempY = (m1 * values[1]) + b1;
						if(values[2] < tempY && tempY < values[3])
						{
							return true;
						}
					}
					//Top and Bottom checks
					if(y1 < values[2] && values[2] < y2)
					{
						tempX = (values[2] - b1) / m1;
						if(values[0] < tempX && tempX < values[1])
						{
							return true;
						}
					} else if(y1 < values[3] && values[3] < y2)
					{
						tempX = (values[2] - b1) / m1;
						if(values[0] < tempX && tempX < values[1])
						{
							return true;
						}
					}
			}
		}
		return false;
	}
	/**
	 * checks whether a projectile could travel along a given line
	 * @param x1 start x
	 * @param y1 start y
	 * @param rads direction to travel
	 * @param distance distance to travel
	 * @return whether it could travel along the given line
	 */
	protected boolean checkObstructions(double x1, double y1, double rads, int distance, boolean objectOnGround, int offset)
	{
		double x2 = x1 + (Math.cos(rads) * distance);
		double y2 = y1 + (Math.sin(rads) * distance);
		return checkObstructionsPoint((float) x1, (float) y1, (float) x2, (float) y2, objectOnGround, offset);
	}
	/**x
	 * checks whether a given point hits any obstacles
	 * @param X x point
	 * @param Y y point
	 * @return whether it hits
	 */
	protected boolean checkHitBack(double X, double Y, boolean objectOnGround)
	{
		if(X < 0 || X > control.levelController.levelWidth || Y < 0 || Y > control.levelController.levelHeight)
		{
			return true;
		}
			for(int i = 0; i < wallRectValues.size(); i++)
			{
				int [] values = wallRectValues.get(i);
				if(values[4]==1||objectOnGround) // OBJECT IS TALL
				{
					if(X > values[0] && X < values[1])
					{
						if(Y > values[2] && Y < values[3])
						{
							return true;
						}
					}
				}
			}
			for(int i = 0; i < wallCircleValues.size(); i++)
			{
				int [] values = wallCircleValues.get(i);
				if(values[3]==1||objectOnGround) // OBJECT IS TALL
				{
					double dist = distanceSqr(X, values[0], Y, values[1]);
					if(dist < Math.pow(values[2], 2))
					{
						return true;
					}
				}
			}
			for(int i = 0; i < wallRingValues.size(); i++)
			{
				int [] values = wallRingValues.get(i);
				if(values[4]==1||objectOnGround) // OBJECT IS TALL
				{
					double dist = distanceSqr(X, values[0], Y, values[1]);
					if(dist < Math.pow(values[3], 2) && dist > Math.pow(values[2], 2))
					{
						if(!checkHitBackPass(X, Y, objectOnGround))
						{
							return true;
						}
					}
				}
			}
		return false;
	}
	/**
	 * checks whether a given point hits any passages
	 * @param X x point
	 * @param Y y point
	 * @return whether it hits
	 */
	protected boolean checkHitBackPass(double X, double Y, boolean objectOnGround)
	{
		for(int i = 0; i < wallPassageValues.size(); i++)
		{
			int [] values = wallPassageValues.get(i);
			if(values[4]==1||objectOnGround) // OBJECT IS TALL
			{
				if(X > values[0] && X < values[1])
				{
					if(Y > values[2] && Y < values[3])
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * sets values for an index of all rectangle tall wall value arrays
	 * @param i index to set values to
	 * @param oRectX1 left x
	 * @param oRectX2 right x
	 * @param oRectY1 top y
	 * @param oRectY2 bottom y
	 */
	protected void setORect(int left, int right, int top, int bottom, int tall)
	{
		int [] vals = {left, right, top, bottom, tall};
		wallRectValues.add(vals);
	}
	/**
	 * sets values for an index of all passage wall value arrays
	 * @param i index to set values to
	 * @param oRectX1 left x
	 * @param oRectX2 right x
	 * @param oRectY1 top y
	 * @param oRectY2 bottom y
	 */
	protected void setOPassage(int left, int right, int top, int bottom, int tall, int ringID)
	{
		int [] vals = {left, right, top, bottom, tall, ringID};
		wallPassageValues.add(vals);
	}
	/**
	 * sets values for an index of all tall circle wall value arrays
	 * @param i index to set values to
	 * @param oCircX x position
	 * @param oCircY y position
	 * @param oCircRadius radius
	 * @param oCircRatio ratio between width and height
	 */
	protected void setOCirc(int xVal, int yVal, int radiusVal, int tall)
	{
		int [] vals = {xVal, yVal, radiusVal, tall};
		wallCircleValues.add(vals);
	}
	/**
	 * sets values for an index of all ring wall value arrays
	 * @param i index to set values to
	 * @param oCircX x position
	 * @param oCircY y position
	 * @param oRingIn inner ring radius
	 * @param oRingOut outer ring radius
	 */
	protected void setORing(int xVal, int yVal, int radiusInVal, int radiusOutVal, int tall)
	{
		int [] vals = {xVal, yVal, radiusInVal, radiusOutVal, tall};
		wallRingValues.add(vals);
	}
	protected boolean checkPoint(double x, double y)
	{
		return !checkHitBack(x*20, y*20, true);
	}
	protected boolean checkPath(int x, int y, double xmove, double ymove)
	{
		for(int i = 1; i < 5; i++)
		{
			if(checkPoint(x+(xmove*i/4), y+(ymove*i/4))) return false;
		}
		return true;
	}
	/**
	 * makes paths for enemy search
	 */
	protected void makePaths()
	{
		int width = control.levelController.levelWidth/20;
		int height = control.levelController.levelHeight/20;
		pathing = new boolean[width][height][5]; //x, y, isFree, right, left, down, up
		for(int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{
				pathing[i][j][0] = checkPoint(i, j);
				pathing[i][j][1] = checkPath(i, j, 1, 0);
				pathing[i][j][2] = checkPath(i, j, -1, 0);
				pathing[i][j][3] = checkPath(i, j, 0, 1);
				pathing[i][j][4] = checkPath(i, j, 0, -1);
			}
		}
	}
	protected double distanceSqr(double x1, double x2, double y1, double y2)
	{
		return Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2);
	}
}