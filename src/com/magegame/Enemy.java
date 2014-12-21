/**
 * All enemies, sets reaction methods, contains checks and mathematical functions
 * @param danger holds Proj_Trackers headed towards object and their coordinates velocity etc
 */
package com.magegame;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.widget.Toast;

abstract public class Enemy extends Human
{
	private int fromWall = 5;
	protected int runTimer = 0;
	protected int stunTimer = 0;
	protected int worth = 3;
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
	public Enemy(Controller creator, double X, double Y, int HP, int ImageIndex)
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
	protected void otherActions()
	{
		/* int [][] frames[action][start/finish or 1/2/3]
		 * actions	move=0;
		 * 			roll=1;		0:start, 1:end
		 * 			stun=2;
		 * 			melee=3;
		 * 			sheild=4;
		 * 			hide=5;
		 * 			shoot=6;
		 */
		if(action.equals("Stun"))
		{
			frame=frames[2][0];
			stunTimer--;
			if(stunTimer==0) action = "Nothing";		//stun over, go have fun
		} else if(action.equals("Roll"))
		{
			x += xMove;
			y += yMove;
			frame++;
			if(frame==frames[1][1]) action = "Nothing";	//roll done
		} else if(action.equals("Melee"))
		{
			frame++;
			attacking();
			if(frame==frames[3][1]) action = "Nothing";	//attack over
		} else if(action.equals("Sheild"))
		{
			frame++;
			if(frame==frames[4][1]) action = "Nothing";	//block done
		} else if(action.equals("Hide"))
		{
			frame = frames[5][0];
			hiding();
		} else if(action.equals("Shoot"))
		{
			frame++;
			shooting();
			if(frame==frames[6][1]) action = "Nothing"; // attack done
		} else if(action.equals("Run"))
		{
			frame++;
			if(frame == frames[0][1]) frame = 0; // restart walking motion
			x += xMove;
			y += yMove;
			runTimer--;
			if(runTimer<1) action = "Nothing"; // stroll done
		} else if(action.equals("Move")||action.equals("Wander"))
		{
			if(pathedToHitLength > 0 || LOS)
			{
				 action = "Nothing";
			} else
			{
				frame++;
				if(frame == frames[0][1]) frame = 0; // restart walking motion
				x += xMove;
				y += yMove;
				runTimer--;
				if(runTimer<1) //stroll over
				{
					action = "Nothing";
					if(action.equals("Move"))
					{
						frameReactionsNoDangerNoLOS();
					} else
					{
						finishWandering();
					}
				}
			}
		}
	}
	protected void pickAction()
	{
		
	}
	abstract protected void attacking();
	abstract protected void hiding();
	abstract protected void shooting();
	abstract protected void finishWandering();
	abstract protected void frameReactionsNoDangerLOS();
	abstract protected void frameReactionsDangerNoLOS();
	abstract protected void frameReactionsNoDangerNoLOS();
	abstract protected void frameReactionsDangerLOS();
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
			if(!sick)
			{
				if(keyHolder)
				{
					Toast.makeText(control.context, "Key Dropped!", Toast.LENGTH_LONG).show();
					control.spriteController.createConsumable(x, y, 8);
				} else
				{
					if(control.getRandomDouble()>0.7)
					{
						control.spriteController.createConsumable(x, y, 0);
					}
				}
				for(int i = 0; i < worth; i ++)
				{
					double rads = control.getRandomDouble()*6.28;
					if(worth-i>20)
					{
						control.spriteController.createConsumable(x+Math.cos(rads)*12, y+Math.sin(rads)*12, 10);
						i+=19;
					} else if(worth-i>5)
					{
						control.spriteController.createConsumable(x+Math.cos(rads)*12, y+Math.sin(rads)*12, 9);
						i+=4;
					} else
					{
						control.spriteController.createConsumable(x+Math.cos(rads)*12, y+Math.sin(rads)*12, 7);
					}
				}
			}
			control.activity.playEffect("burst");
	}
	/**
	 * Rotates to run away from player 
	 */
	protected void runAway()
	{
		rads = Math.atan2(-(control.player.y - y), -(control.player.x - x));
		rotation = rads * r2d;
		int distance = (int)checkDistance(x, y, control.player.x,  control.player.y);
		if(control.checkObstructions(x, y, rads, distance, true, fromWall))
		{
			int runPathChooseCounter = 0;
			double runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 10;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!control.checkObstructions(x, y, rads, 40, true, fromWall))
				{
					runPathChooseCounter = 300;
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!control.checkObstructions(x, y,  rads, 40, true, fromWall))
					{
						runPathChooseCounter = 300;
					}
				}
			}
			if(runPathChooseCounter == 300)
			{
				run(10);
			}
		} else
		{
			run(10);
		}
	}        
	/**
	 * Aims towards player
	 */
	protected void aimAheadOfPlayer(double projectileVelocity)
	{
			double timeToHit = (checkDistance(x, y, control.player.x, control.player.y))/projectileVelocity;
			timeToHit *= (control.getRandomDouble()*0.7)+0.4;
			double newPX = control.player.x+(pXVelocity*timeToHit);
			double newPY = control.player.y+(pYVelocity*timeToHit);
			double xDif = newPX-x;
			double yDif = newPY-y;
			rads = Math.atan2(yDif, xDif); // ROTATES TOWARDS PLAYER
			rotation = rads * r2d;
	}
	/**
	 * Runs in direction object is rotated for 10 frames
	 */
	protected void run(int time)
	{
		runTimer = time;
		action = "Run";
		xMove = Math.cos(rads) * speedCur;
		yMove = Math.sin(rads) * speedCur;
        if(frame>17) frame = 0;
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
			if(difference>90&&checkDistance(x, y, px, py)>50)
			{
				LOS = false;
			} else
			{
				if(!control.checkObstructionsPoint((float)x, (float)y, (float)px, (float)py, false, fromWall))
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
	protected void turnToward(double nx, double ny)
	{
		LOS=true;
		hadLOSLastTime = 5;
		//rads = Math.atan2((ny - y), (nx - x));
		//rotation = rads*r2d;
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
				if(!control.checkObstructionsPoint((float)danger[0][dangerCheckCounter], (float)danger[1][dangerCheckCounter], (float)x, (float)y, false, fromWall))
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
	 * rolls forward for 11 frames
	 */
	protected void roll()
	{
		frame = 82;
		action = "Roll";
		rotation = rads * r2d;
		xMove = Math.cos(rads) * 8;
		yMove = Math.sin(rads) * 8;
	}
	/**
	 * rolls sideways for 11 frames
	 */
	protected void rollSideways()
	{
		rads = Math.atan2((control.player.y - y), (control.player.x - x));
		rotation = rads * r2d;
		rads = (rotation + 90) / r2d;
		if(control.checkObstructions(x, y, rads, 42, true, fromWall))
		{
			rads = (rotation - 90) / r2d;
			if(control.checkObstructions(x, y, rads, 42, true, fromWall))
			{
				rollAway();
			}
			else
			{
				rotation -= 90;
				rads = rotation / r2d;
				roll();
			}
		} else
		{
			rads = (rotation - 90) / r2d;
			if(control.checkObstructions(x, y, rads, 42, true, fromWall))
			{
				rotation += 90;
				rads = rotation / r2d;
				roll();
			}
			else
			{
				if(Math.random() > 0.5)
				{
					rotation += 90;
					roll();
				}
				else
				{
					rotation -= 90;
					roll();
				}
			}
		}
	}
	/**
	 * Rolls away from player
	 */
	protected void rollAway()
	{
		rads = Math.atan2(-(control.player.y - y), -(control.player.x - x));
		rotation = rads * r2d;
		if(!control.checkObstructions(x, y, rads, 42, true, fromWall))
		{
			roll();
		} else
		{
			int rollPathChooseCounter = 0;
			double rollPathChooseRotationStore = rotation;
			while(rollPathChooseCounter < 180)
			{
				rollPathChooseCounter += 10;
				rotation = rollPathChooseRotationStore + rollPathChooseCounter;
				rads = rotation / r2d;
				if(!control.checkObstructions(x, y, rads, 42, true, fromWall))
				{
					roll();
					rollPathChooseCounter = 180;
				}
				else
				{
					rotation = rollPathChooseRotationStore - rollPathChooseCounter;
					rads = rotation / r2d;
					if(!control.checkObstructions(x, y, rads, 42, true, fromWall))
					{
						roll();
						rollPathChooseCounter = 180;
					}
				}
			}
		}
	}
	protected void hitWall()
	{
		//action="Nothing";
	}
	/**
	 * rolls towards player for 11 frames
	 */
	protected void rollTowards()
	{
		rads = Math.atan2((control.player.y - y), (control.player.x - x));
		roll();
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
	/**
	 * when enemy swings at player, check whether it hits
	 */
	protected void meleeAttack(int damage)
	{
		distanceFound = checkDistance(x + Math.cos(rads) * 25, y + Math.sin(rads) * 25, control.player.x, control.player.y);
		if(distanceFound < 25)
		{
			control.player.getHit((int)(damage));
			control.activity.playEffect("sword2");
			if(control.getRandomInt(3) == 0)
			{
				control.player.rads = Math.atan2(control.player.y-y, control.player.x-x);
				control.player.stun();
			}
		} else
		{
			control.activity.playEffect("swordmiss");
		}
	}
	/**
	 * Runs towards player, if you cant, run randomly
	 */
	protected void runTowardsPoint(double fx, double fy)
	{
		if(control.checkObstructionsPoint((int)fx, (int)fy, (int)x, (int)y, true, fromWall))
		{
			int foundPlayer = -1;			//try to find enemy
			int sX = (int)(fx/20);		//start at player
			int sY = (int)(fy/20);
			int eX = (int)x;
			int eY = (int)y;
			int[] p1 = {sX, sY, sX, sY};
			boolean[][] checked=new boolean[(control.levelWidth/20)][(control.levelHeight/20)];
			ArrayList<int[]> points = new ArrayList<int[]>();
			points.add(p1);
			checked[sX][sY]=true;
			int count = 0;
			while(foundPlayer==-1&&count<40)
			{
				foundPlayer=iterateSearch(points, checked, eX, eY);
				count++;
			}
			if(foundPlayer==-1)
			{
				runRandom();
			} else
			{
				int[] closest = points.get(foundPlayer);
				rads = Math.atan2(closest[3]*20 - y, closest[2]*20 - x);
				rotation = rads * r2d;
				run(8);
			}
		} else
		{
			rads = Math.atan2(fy - y, fx - x);
			rotation = rads * r2d;
			run(8);
		}
	}
	private int iterateSearch(ArrayList<int[]> points, boolean[][] checked, int eX, int eY)
	{
		int numPoints = points.size();
		for(int i = 0; i < numPoints; i++) // for every endpoint we have, expand
		{
			int x = points.get(i)[0];
			int y = points.get(i)[1];
			if(!control.checkObstructionsPoint(x*20, y*20, eX, eY, true, fromWall)) return i;
			if(x>0)
			{	//if were not on the edge, we havent checked it, and its free
				if(!checked[x-1][y]&&!control.checkHitBack((x-1)*20, y*20, true))
				{
					int[] newPoint = {x-1, y, x, y}; // its a new endpoint
					points.add(newPoint);
					checked[x-1][y]=true;			//weve checked this square
				}
			}
			if(x<checked.length-1)
			{
				if(!checked[x+1][y]&&!control.checkHitBack((x+1)*20, y*20, true))
				{
					int[] newPoint = {x+1, y, x, y};
					points.add(newPoint);
					checked[x+1][y]=true;
				}
			}
			if(y>0)
			{
				if(!checked[x][y-1]&&!control.checkHitBack(x*20, (y-1)*20, true))
				{
					int[] newPoint = {x, y-1, x, y};
					points.add(newPoint);
					checked[x][y-1]=true;
				}
			}
			if(y<checked[0].length-1)
			{
				if(!checked[x][y+1]&&!control.checkHitBack(x*20, (y+1)*20, true))
				{
					int[] newPoint = {x, y+1, x, y};
					points.add(newPoint);
					checked[x][y+1]=true;
				}
			}
		}
		for(int i = 0; i < numPoints; i++) // remove all the old points
		{
			points.remove(0);
		}
		return -1;
	}
	/**
	 * runs towards a set x and y
	 * @param towardsX destination x value
	 * @param towardsY destination y value
	 * @param distance distance to run
	 * @return whether it is possible or not to run here
	 */
	protected boolean runTowardDistanceGood(double towardsX, double towardsY, int distance)
	{
		int runPathChooseCounter = 0;
		double runPathChooseRotationStore = rotation;
		boolean goodMove = false;
		while(runPathChooseCounter < 180)
		{
			runPathChooseCounter += 10;
			rotation = runPathChooseRotationStore + runPathChooseCounter;
			rads = rotation / r2d;
			if(!control.checkObstructions(x, y,rads, distance, true, fromWall))
			{
				if(!control.checkObstructionsPoint((float)towardsX, (float)towardsY, (float)(x+Math.cos(rads)*distance), (float)(y+Math.sin(rads)*distance), true, fromWall))
				{
					runPathChooseCounter = 180;
					goodMove = true;
				}
			}
			else
			{
				rotation = runPathChooseRotationStore - runPathChooseCounter;
				rads = rotation / r2d;
				if(!control.checkObstructions(x, y,rads, distance, true, fromWall))
				{
					if(!control.checkObstructionsPoint((float)towardsX, (float)towardsY, (float)(x+Math.cos(rads)*distance), (float)(y+Math.sin(rads)*distance), true, fromWall))
					{
						runPathChooseCounter = 180;
						goodMove = true;
					}
				}
			}
		}
		run(distance/4);
		return goodMove;
	}
	/**
	 * check whether you can run around a corner to player
	 * @param towardsX destination x value
	 * @param towardsY destination y value
	 * @return whether it is possible to get to player going around the corner
	 */
	protected boolean runAroundCorner(double towardsX, double towardsY)
	{
		int runPathChooseCounter = 0;
		double runPathChooseRotationStore = rotation;
		boolean goodMove = false;
		while(runPathChooseCounter < 180)
		{
			runPathChooseCounter += 10;
			rotation = runPathChooseRotationStore + runPathChooseCounter;
			rads = rotation / r2d;
			if(!control.checkObstructions(x, y,rads, 80, true, fromWall))
			{
				double newX = (x+Math.cos(rads)*80);
				double newY = (y+Math.sin(rads)*80);
				if(ranAroundCorner(towardsX, towardsY, newX, newY))
				{
					runPathChooseCounter = 180;
					goodMove = true;
				}
			} else
			{
				rotation = runPathChooseRotationStore - runPathChooseCounter;
				rads = rotation / r2d;
				if(!control.checkObstructions(x, y,rads, 80, true, fromWall))
				{
					float newX = (float)(x+Math.cos(rads)*80);
					float newY = (float)(y+Math.sin(rads)*80);
					if(ranAroundCorner(towardsX, towardsY, newX, newY))
					{
						runPathChooseCounter = 180;
						goodMove = true;
					}
				}
			}
		}
		run(15);
		return goodMove;
	}
	/**
	 * part of runAroundCorner
	 * @param towardsX destination x value
	 * @param towardsY destination y value
	 * @param newX enemies x after first segment of movement
	 * @param newY enemies y after first segment of movement
	 * @return whether you can run to player from here
	 */
	protected boolean ranAroundCorner(double towardsX, double towardsY, double newX, double newY)
	{
		int runPathChooseCounter = 0;
		double runPathChooseRotationStore = rotation;
		boolean goodMove = false;
		double testRads;
		double testRotation;
		while(runPathChooseCounter < 180)
		{
			runPathChooseCounter += 10;
			testRotation = runPathChooseRotationStore + runPathChooseCounter;
			testRads = testRotation / r2d;
			if(!control.checkObstructions(newX, newY,testRads, 80, true, fromWall))
			{
				float endX = (float)(newX+Math.cos(testRads)*80);
				float endY = (float)(newY+Math.sin(testRads)*80);
				if(!control.checkObstructionsPoint((float)towardsX, (float)towardsY, endX, endY, true, fromWall))
				{
					runPathChooseCounter = 180;
					goodMove = true;
				}
			}
			else
			{
				testRotation = runPathChooseRotationStore - runPathChooseCounter;
				testRads = testRotation / r2d;
				if(!control.checkObstructions(newX, newY,testRads, 80, true, fromWall))
				{
					float endX = (float)(newX+Math.cos(testRads)*80);
					float endY = (float)(newY+Math.sin(testRads)*80);
					if(!control.checkObstructionsPoint((float)towardsX, (float)towardsY, endX, endY, true, fromWall))
					{
						runPathChooseCounter = 180;
						goodMove = true;
					}
				}
			}
		}
		return goodMove;
	}	
	/**
	 * Runs random direction for 25 or if not enough space 10 frames
	 */
	protected void runRandom()
	{
		boolean canMove = false;
		rotation += control.getRandomInt(10)-5;
		rads = rotation / r2d;
		if(control.checkObstructions(x, y,rads, (int)(speedCur*20), true, fromWall))
		{
			int runPathChooseCounter = 0;
			double runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 10;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!control.checkObstructions(x, y,rads, (int)(speedCur*20), true, fromWall))
				{
					runPathChooseCounter = 180;
					canMove = true;
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!control.checkObstructions(x, y,rads, (int)(speedCur*20), true, fromWall))
					{
						runPathChooseCounter = 180;
						canMove = true;
					}
				}
			}
		}
		if(control.checkObstructions(x, y,rads, (int)(speedCur*10), true, fromWall))
		{
			int runPathChooseCounter = 0;
			double runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 10;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!control.checkObstructions(x, y,rads, (int)(speedCur*10), true, fromWall))
				{
					runPathChooseCounter = 180;
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!control.checkObstructions(x, y,rads, (int)(speedCur*10), true, fromWall))
					{
						runPathChooseCounter = 180;
					}
				}
			}
		}
		if(canMove)
		{
			run(10);
			action = "Wander";
		} else
		{
			run(5);
			action = "Wander";
		}
	}
	protected void baseHp(int setHP)
	{
		hp = setHP;
		setHpMax(hp);
	}
}