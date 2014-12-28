/**
 * All enemies, sets reaction methods, contains checks and mathematical functions
 * @param danger holds Proj_Trackers headed towards object and their coordinates velocity etc
 */
package com.magegame;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.widget.Toast;

abstract public class EnemyShell extends Human
{
	protected int fromWall = 5;
	protected int runTimer = 0;
	protected int worth = 3;
	protected int stunTimer = 0;
	protected double lastPlayerX;
	protected double lastPlayerY;
	protected boolean sick = false;
	protected boolean checkedPlayerLast = true;
	protected Bitmap [] myImage;
	protected int imageIndex;
	protected double danger[][] = new double[4][30];
	private double levelX[] = new double[30];
	private double levelY[] = new double[30];
	private double levelXForward[] = new double[30];
	private double levelYForward[] = new double[30];
	protected int levelCurrentPosition = 0;
	protected int pathedToHitLength = 0;
	protected boolean HasLocation = false;
	protected boolean LOS = false;
	protected double distanceFound;
	private int dangerCheckCounter;
	protected boolean keyHolder = false;
	private double pathedToHit[] = new double[30];
	protected int radius = 20;
	protected double pXVelocity=0;
	protected double pYVelocity=0;
	private double pXSpot=0;
	private double pYSpot=0;
	protected double xMove;
	protected double yMove;
	protected int enemyType;
	protected int hadLOSLastTime=-1;
	int [][] frames;
	protected String action; //"Nothing", "Move", "Alert", "Shoot", "Melee", "Roll", "Hide", "Sheild", "Stun"
	/**
	 * sets danger arrays, speed and control object
	 * @param creator control object
	 */
	public EnemyShell(Controller creator, double X, double Y, int HP, int ImageIndex)
	{
		super(X, Y, 0, 0, true, false, creator.imageLibrary.enemyImages[ImageIndex][0]);
		control = creator;
		danger[0] = levelX;
		danger[1] = levelY;
		danger[2] = levelXForward;
		danger[3] = levelYForward;
		x = X;
		y = Y;
		width = 30;
		height = 30;
		lastPlayerX = x;
		lastPlayerY = y;
		action = "Nothing";
		imageIndex = ImageIndex;
		myImage = creator.imageLibrary.enemyImages[ImageIndex];
		image = myImage[frame];
	}
	/**
	 * clears desired array
	 * @param array array to clear
	 * @param length length of array to clear
	 */
	protected void clearArray(double[] array, int length)
	{
		for(int i = 0; i < length; i++)
		{
			array[i] = -11111;
		}
	}
	/**
	 * Clears danger arrays, sets current dimensions, and counts timers
	 */
	@
	Override
	protected void frameCall()
	{
		checkLOS((int)control.player.x, (int)control.player.y);
		checkDanger();
		otherActions();
		if(action.equals("Nothing"))
		{
			if(LOS)
			{
				frameLOS();
			} else
			{
				frameNoLOS();
			}
		}
		image = myImage[frame];
		hadLOSLastTime--;
		if(sick)
		{
			hp -= 20;
			getHit(0);
		}
		pXVelocity = control.player.x-pXSpot;
		pYVelocity = control.player.y-pYSpot;
		pXSpot = control.player.x;
		pYSpot = control.player.y;
		hp += 4;
		super.frameCall();
		clearArray(levelX, 30);
		clearArray(levelY, 30);
		clearArray(levelXForward, 30);
		clearArray(levelYForward, 30);
		clearArray(pathedToHit, 30);
		sizeImage();
		pushOtherPeople();
	}
	abstract protected void attacking();
	abstract protected void hiding();
	abstract protected void shooting();
	abstract protected void finishWandering();
	abstract protected void frameLOS();
	abstract protected void frameNoLOS();
	abstract protected void otherActions();
	/**
	 * checks who else this guy is getting in the way of and pushes em
	 */
	private void pushOtherPeople()
	{
		double movementX;
		double movementY;
		double moveRads;
		double xdif = x - control.player.x;
		double ydif = y - control.player.y;
		if(Math.pow(xdif, 2) + Math.pow(ydif, 2) < Math.pow(radius, 2))
		{
			moveRads = Math.atan2(ydif, xdif);
			movementX = (x - (Math.cos(moveRads) * radius) - control.player.x)/2;
			movementY = (y - (Math.sin(moveRads) * radius) - control.player.y)/2;
			if(control.player.rollTimer<1)
			{
				control.player.x += movementX;
				control.player.y += movementY;
				x -= movementX;
				y -= movementY;
			}
		}
		ArrayList<Enemy> enemies = control.spriteController.enemies;
		for(int i = 0; i < enemies.size(); i++)
		{
			if(enemies.get(i) != null&& enemies.get(i).x != x)
			{
				xdif = x - enemies.get(i).x;
				ydif = y - enemies.get(i).y;
				if(Math.pow(xdif, 2) + Math.pow(ydif, 2) < Math.pow(radius, 2))
				{
					moveRads = Math.atan2(ydif, xdif);
					movementX = (x - (Math.cos(moveRads) * radius) - enemies.get(i).x)/2;
					movementY = (y - (Math.sin(moveRads) * radius) - enemies.get(i).y)/2;
					enemies.get(i).x += movementX;
					enemies.get(i).y += movementY;
					x -= movementX;
					y -= movementY;
				}
			}
		}
	}
	protected void turnToward(double nx, double ny)
	{
		LOS=true;
		hadLOSLastTime = 5;
		//rads = Math.atan2((ny - y), (nx - x));
		//rotation = rads*r2d;
	}
	/**
	 * Takes a sent amount of damage, modifies based on shields etc.
	 * if health below 0 kills enemy
	 * @param damage amount of damage to take
	 */
	protected void getHit(double damage)
	{
		turnToward(control.player.x, control.player.y);
		if(!deleted)
		{
			if(action.equals("Hide")) action = "Nothing";
			damage /= 1.2;
			super.getHit(damage);
			control.player.abilityTimer_burst += damage/30*control.player.chargeCooldown;
			control.player.abilityTimer_roll += damage/50*control.player.chargeCooldown;
			control.player.abilityTimer_Proj_Tracker += damage/100*control.player.chargeCooldown;
			control.player.sp += damage*0.00003*control.player.chargeSP;
			if(deleted)
			{
				dieDrops();
			}
		}
	}
	/**
	 * Drops items and stuff if enemy dead
	 */
	protected void dieDrops()
	{
			control.player.sp += 0.15;
			control.spriteController.createProj_TrackerEnemyAOE(x, y, 140, false);
			control.soundController.playEffect("burst");
			control.itemControl.favor+= (double)worth/10;
			control.player.experience += worth;
			if(control.player.blessingTimer>0) // if blessing active get more
			{
				control.itemControl.favor+= (double)worth/2;
				control.player.blessingTimer += 20;
			}
			if(!sick) control.spriteController.createConsumable(x, y, 0);
	}
	/**
	 * Checks whether object can 'see' player
	 */
	protected void checkLOS(int px, int py)
	{
		double rads2 = Math.atan2((py - y), (px - x));
		if(control.player.rollTimer>0 && hadLOSLastTime<1)
		{
			LOS = false;
		} else
		{
			double rot2 = rads2*r2d;
			double difference = Math.abs(rotation-rot2);
			if(difference>180) difference = 360-difference;
			if(false)//difference>110&&checkDistance(x, y, px, py)>50)
			{
				LOS = false;
			} else
			{
				if(!control.wallController.checkObstructionsPoint((float)x, (float)y, (float)px, (float)py, false, fromWall))
				{
					LOS = true;
					hadLOSLastTime = 25;
					lastPlayerX = px;
					lastPlayerY = py;
					checkedPlayerLast = false;
				} else
				{
					LOS = false;
				}
			}
		}
		HasLocation = hadLOSLastTime>0;
		if(HasLocation)	//tell others where player is
		{
			for(int i = 0; i < control.spriteController.enemies.size(); i++)
			{
				Enemy enemy = control.spriteController.enemies.get(i);
				if(!enemy.HasLocation&&checkDistance(x, y, enemy.x, enemy.y)<200)
				{
					enemy.turnToward(px, py);
				}
			}
		}
	}
	/**
	 * what happens when an enemy hits a wall
	 */
	protected void hitWall()
	{
		//TODO what do we do...
	}
	/**
	 * Checks whether any Proj_Trackers are headed for object
	 */
	protected void checkDanger()
	{           
		dangerCheckCounter = 0;
		while(dangerCheckCounter < levelCurrentPosition)
		{
			distanceFound = checkDistance(danger[0][dangerCheckCounter], danger[1][dangerCheckCounter], x, y);
			distanceFound = checkDistance((int) Math.abs(danger[0][dangerCheckCounter] + (danger[2][dangerCheckCounter] / 10 * distanceFound)), (int) Math.abs(danger[1][dangerCheckCounter] + (danger[3][dangerCheckCounter] / 10 * distanceFound)), x, y);
			if(distanceFound < 20)
			{
				if(!control.wallController.checkObstructionsPoint((float)danger[0][dangerCheckCounter], (float)danger[1][dangerCheckCounter], (float)x, (float)y, false, fromWall))
				{
					pathedToHit[pathedToHitLength] = dangerCheckCounter;
					pathedToHitLength++;         
				}
			}
			dangerCheckCounter++;
		}
	}
	/**
	 * Checks distance between two points
	 * @return Returns distance
	 */
	protected double checkDistance(double fromX, double fromY, double toX, double toY)
	{
		return Math.sqrt((Math.pow(fromX - toX, 2)) + (Math.pow(fromY - toY, 2)));
	}
	/**
	 * stuns enemy
	 * @param time time to stun enemy for
	 */
	protected void stun(int time)
	{
		action ="Stun";
		stunTimer=time;
	}
	/**
	 * sets a certain index in danger arrays
	 * @param i index to set
	 * @param levelX x position of danger
	 * @param levelY y position of danger
	 * @param levelXForward x velocity of danger
	 * @param levelYForward y velocity of danger
	 */
	protected void setLevels(int i, double levelX, double levelY, double levelXForward, double levelYForward) {
		this.levelX[i] = levelX;
		this.levelY[i] = levelY;
		this.levelXForward[i] = levelXForward;
		this.levelYForward[i] = levelYForward;
	}
	protected void baseHp(int setHP)
	{
		hp = setHP;
		setHpMax(hp);
	}
}