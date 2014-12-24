/**
 * All enemies, sets reaction methods, contains checks and mathematical functions
 * @param danger holds Proj_Trackers headed towards object and their coordinates velocity etc
 */
package com.magegame;

import java.util.ArrayList;
/* provides already made functions including 
 * 
 * run
 * runAway
 * runTowardsPoint
 * runRandom
 * searchOrWander
 * 
 * roll
 * rollSideways
 * rollAway
 * rollTowards
 * 
 * aimAheadOfPlayer
 * meleeAttack
 */

abstract public class Enemy extends EnemyShell
{
	
	/**
	 * sets danger arrays, speed and control object
	 * @param creator control object
	 */
	public Enemy(Controller creator, double X, double Y, int HP, int ImageIndex)
	{
		super(creator, X, Y, HP, ImageIndex);
	}
	@ Override
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
			x += xMove*1.2;
			y += yMove*1.2;
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
						frameNoLOS();
					} else
					{
						finishWandering();
					}
				}
			}
		}
	}
	/**
	 * Rotates to run away from player 
	 */
	protected void runAway()
	{
		rads = Math.atan2(-(control.player.y - y), -(control.player.x - x));
		rotation = rads * r2d;
		int distance = (int)checkDistance(x, y, control.player.x,  control.player.y);
		if(checkObstructions(x, y, rads, distance, true, fromWall))
		{
			int runPathChooseCounter = 0;
			double runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 10;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!checkObstructions(x, y, rads, 40, true, fromWall))
				{
					runPathChooseCounter = 300;
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!checkObstructions(x, y,  rads, 40, true, fromWall))
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
	 * rolls forward for 11 frames
	 */
	protected void roll()
	{
		frame = frames[1][0];
		action = "Roll";
		rotation = rads * r2d;
		xMove = Math.cos(rads) * 8;
		yMove = Math.sin(rads) * 8;
	}
	/**
	 * rolls sideways for 11 frames
	 */
	protected void runSideways()
	{
		rads = Math.atan2((control.player.y - y), (control.player.x - x));
		rotation = rads * r2d;
		boolean right = !checkObstructions(x, y, (rotation + 90) / r2d, 42, true, fromWall);
		boolean left = !checkObstructions(x, y, (rotation - 90) / r2d, 42, true, fromWall);
		if(left||right)
		{
			if(left) rotation -= 90;
			if(right) rotation += 90;
			if(left && right)
			{
				rotation -= 90;
				if(Math.random() > 0.5) rotation += 180;
			}
			rads = rotation / r2d;
			run(5);
		} else
		{
			runAway();
		}
	}
	/**
	 * rolls sideways for 11 frames
	 */
	protected void rollSideways()
	{
		rads = Math.atan2((control.player.y - y), (control.player.x - x));
		rotation = rads * r2d;
		boolean right = !checkObstructions(x, y, (rotation + 90) / r2d, 42, true, fromWall);
		boolean left = !checkObstructions(x, y, (rotation - 90) / r2d, 42, true, fromWall);
		if(left||right)
		{
			if(left) rotation -= 90;
			if(right) rotation += 90;
			if(left && right)
			{
				rotation -= 90;
				if(Math.random() > 0.5) rotation += 180;
			}
			rads = rotation / r2d;
			roll();
		} else
		{
			rollAway();
		}
	}
	/**
	 * Rolls away from player
	 */
	protected void rollAway()
	{
		rads = Math.atan2(-(control.player.y - y), -(control.player.x - x));
		rotation = rads * r2d;
		if(!checkObstructions(x, y, rads, 42, true, fromWall))
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
				if(!checkObstructions(x, y, rads, 42, true, fromWall))
				{
					roll();
					rollPathChooseCounter = 180;
				}
				else
				{
					rotation = rollPathChooseRotationStore - rollPathChooseCounter;
					rads = rotation / r2d;
					if(!checkObstructions(x, y, rads, 42, true, fromWall))
					{
						roll();
						rollPathChooseCounter = 180;
					}
				}
			}
		}
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
	 * when enemy swings at player, check whether it hits
	 */
	protected void meleeAttack(int damage, int range, int ahead)
	{
		distanceFound = checkDistance(x + Math.cos(rads) * ahead, y + Math.sin(rads) * ahead, control.player.x, control.player.y);
		if(distanceFound < range)
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
		if(checkObstructionsPoint((int)fx, (int)fy, (int)x, (int)y, true, fromWall))
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
			if(!checkObstructionsPoint(x*20, y*20, eX, eY, true, fromWall)) return i;
			if(x>0)
			{	//if were not on the edge, we havent checked it, and its free
				if(!checked[x-1][y]&&!checkHitBack((x-1)*20, y*20, true))
				{
					int[] newPoint = {x-1, y, x, y}; // its a new endpoint
					points.add(newPoint);
					checked[x-1][y]=true;			//weve checked this square
				}
			}
			if(x<checked.length-1)
			{
				if(!checked[x+1][y]&&!checkHitBack((x+1)*20, y*20, true))
				{
					int[] newPoint = {x+1, y, x, y};
					points.add(newPoint);
					checked[x+1][y]=true;
				}
			}
			if(y>0)
			{
				if(!checked[x][y-1]&&!checkHitBack(x*20, (y-1)*20, true))
				{
					int[] newPoint = {x, y-1, x, y};
					points.add(newPoint);
					checked[x][y-1]=true;
				}
			}
			if(y<checked[0].length-1)
			{
				if(!checked[x][y+1]&&!checkHitBack(x*20, (y+1)*20, true))
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
	 * Runs random direction for 25 or if not enough space 10 frames
	 */
	protected void runRandom()
	{
		boolean canMove = false;
		rotation += control.getRandomInt(10)-5;
		rads = rotation / r2d;
		if(checkObstructions(x, y,rads, (int)(speedCur*20), true, fromWall))
		{
			int runPathChooseCounter = 0;
			double runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 10;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!checkObstructions(x, y,rads, (int)(speedCur*20), true, fromWall))
				{
					runPathChooseCounter = 180;
					canMove = true;
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!checkObstructions(x, y,rads, (int)(speedCur*20), true, fromWall))
					{
						runPathChooseCounter = 180;
						canMove = true;
					}
				}
			}
		}
		if(checkObstructions(x, y,rads, (int)(speedCur*10), true, fromWall))
		{
			int runPathChooseCounter = 0;
			double runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 10;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!checkObstructions(x, y,rads, (int)(speedCur*10), true, fromWall))
				{
					runPathChooseCounter = 180;
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!checkObstructions(x, y,rads, (int)(speedCur*10), true, fromWall))
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
	protected void searchOrWander()
	{
		distanceFound = checkDistance(x, y, lastPlayerX, lastPlayerY); // lastPlayerX and Y are the last seen coordinates
		if(checkedPlayerLast || distanceFound < 20)
		{
			frame=0;
			action = "Nothing";
			if(control.getRandomInt(20) == 0) // around ten frames of pause between random wandering
			{
				runRandom();
			}
			checkedPlayerLast = true; // has checked where player was last seen
		} else
		{
			boolean temp = LOS;
			checkLOS((int)lastPlayerX, (int)lastPlayerY);
			if(LOS)
			{
				runTowardsPoint(lastPlayerX, lastPlayerY);
				action = "Move";
			} else
			{
				checkedPlayerLast = true;
			}
			LOS = temp;
		}
	}
	private boolean checkHitBack(double X, double Y, boolean objectOnGround)
	{
		return control.wallController.checkHitBack(X, Y, objectOnGround);
	}
	private boolean checkObstructionsPoint(float x1, float y1, float x2, float y2, boolean objectOnGround, int expand)
	{
		return control.wallController.checkObstructionsPoint(x1, y1, x2, y2, objectOnGround, expand);
	}
	private boolean checkObstructions(double x1, double y1, double rads, int distance, boolean objectOnGround, int offset)
	{
		return control.wallController.checkObstructions(x1, y1, rads, distance, objectOnGround, offset);
	}
}