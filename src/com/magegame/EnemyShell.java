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
	protected int rollTimer = 0;
	protected int worth = 3;
	protected int stunTimer = 0;
	protected double lastPlayerX;
	protected double lastPlayerY;
	protected boolean sick = false;
	protected boolean checkedPlayerLast = true;
	protected Bitmap [] myImage;
	protected int imageIndex;
	protected int inDanger = 0;
	protected double[] closestDanger = new double[2];
	protected boolean HasLocation = false;
	protected boolean LOS = false;
	protected double distanceFound;
	private int dangerCheckCounter;
	protected boolean keyHolder = false;
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
	protected String action = "Nothing"; //"Nothing", "Move", "Alert", "Shoot", "Melee", "Roll", "Hide", "Sheild", "Stun"
	/**
	 * sets danger arrays, speed and control object
	 * @param creator control object
	 */
	public EnemyShell(Controller creator, double X, double Y, double R, int HP, int ImageIndex)
	{
		super(X, Y, 0, 0, true, false, creator.imageLibrary.enemyImages[ImageIndex][0]);
		control = creator;
		x = X;
		y = Y;
		width = 30;
		height = 30;
		lastPlayerX = x;
		lastPlayerY = y;
		imageIndex = ImageIndex;
		enemyType = ImageIndex;
		myImage = creator.imageLibrary.enemyImages[ImageIndex];
		image = myImage[frame];
	}
	/**
	 * Clears danger arrays, sets current dimensions, and counts timers
	 */
	@
	Override
	protected void frameCall()
	{
		otherActions();
		if(action.equals("Nothing"))
		{
			checkLOS();
			checkDanger();
			if(LOS)
			{
				frameLOS();
			} else
			{
				frameNoLOS();
			}
		}
		image = myImage[frame];
		rollTimer --;
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
		sizeImage();
		pushOtherPeople();
	}
	abstract protected void attacking();
	abstract protected void hiding();
	abstract protected void shooting();
	abstract protected void blocking();
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
	/**
	 * Takes a sent amount of damage, modifies based on shields etc.
	 * if health below 0 kills enemy
	 * @param damage amount of damage to take
	 */
	protected void getHit(double damage)
	{
		if(!deleted)
		{
			if(action.equals("Sheild")) damage /= 9;
			getPlayerLocation();
			if(action.equals("Hide")) action = "Nothing";
			damage /= 1.2;
			super.getHit(damage);
			control.player.abilityTimer_burst += damage/30*control.player.chargeCooldown;
			control.player.abilityTimer_roll += damage/50*control.player.chargeCooldown;
			control.player.abilityTimer_Proj_Tracker += damage/100*control.player.chargeCooldown;
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
			control.spriteController.createProj_TrackerEnemyAOE(x, y, 140, false);
			control.soundController.playEffect("burst");
			control.itemControl.favor+= (double)worth/10;
			control.player.experience += worth;
			if(control.player.blessingTimer>0) // if blessing active get more
			{
				control.itemControl.favor+= (double)worth/2;
				control.player.blessingTimer += 20;
			}
	}
	protected boolean checkLOS(int px, int py)
	{
		return !control.wallController.checkObstructionsPoint((float)x, (float)y, px, py, false, fromWall);
	}
	/**
	 * Checks whether object can 'see' player
	 */
	protected void checkLOS()
	{
		int px = (int)control.player.x;
		int py = (int)control.player.y;
		if(control.player.rollTimer>0 && hadLOSLastTime<1)
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
		HasLocation = hadLOSLastTime>0;
		if(HasLocation)	//tell others where player is
		{
			callPlayerLocation();
		}
	}
	/**
	 * tells other enemies where player is
	 */
	protected void callPlayerLocation()
	{
		for(int i = 0; i < control.spriteController.enemies.size(); i++)
		{
			Enemy enemy = control.spriteController.enemies.get(i);
			if(!enemy.HasLocation&&checkDistance(x, y, enemy.x, enemy.y)<200)
			{
				enemy.getPlayerLocation();
			}
		}
	}
	/**
	 * hears where player is
	 */
	protected void getPlayerLocation()
	{
		lastPlayerX = control.player.x;
		lastPlayerY = control.player.y;
		if(!LOS)
		{
			rads = Math.atan2((control.player.y - y), (control.player.x - x));
			rotation = rads * r2d;
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
		inDanger = 0;
		closestDanger[0] = 0;
		closestDanger[1] = 0;
		for(int i = 0; i < control.spriteController.proj_TrackerP_AOEs.size(); i++)
		{
			Proj_Tracker_AOE_Player AOE = control.spriteController.proj_TrackerP_AOEs.get(i);
			if(AOE.timeToDeath>7 && Math.pow(x-AOE.x, 2)+Math.pow(y-AOE.y, 2)<Math.pow(AOE.widthDone+25, 2))
			{
				closestDanger[0]+=AOE.x;
				closestDanger[1]+=AOE.y;
				inDanger++;
			}
		}
		for(int i = 0; i < control.spriteController.proj_TrackerPs.size(); i++)
		{
			Proj_Tracker_Player shot = control.spriteController.proj_TrackerPs.get(i);
			if(shot.goodTarget(this, 110))
			{
				closestDanger[0]+=shot.x*2;
				closestDanger[1]+=shot.y*2;
				inDanger+=2;
			}
		}
		closestDanger[0]/=inDanger;
		closestDanger[1]/=inDanger;
	}
	/**
	 * Checks distance to player
	 * @return Returns distance
	 */
	protected double distanceToPlayer()
	{
		return checkDistance(x, y, control.player.x, control.player.y);
	}
	/**
	 * Checks distance between two points
	 * @return Returns distance
	 */
	protected double distanceTo(double toX, double toY)
	{
		return checkDistance(x, y, toX, toY);
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
	protected void baseHp(int setHP)
	{
		hp = setHP;
		hpMax = hp;
	}
}