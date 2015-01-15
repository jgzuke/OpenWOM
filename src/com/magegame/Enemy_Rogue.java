/**
 * AI and variables for rogues
 */
package com.magegame;


public final class Enemy_Rogue extends Enemy
{
	private boolean firstHit = true;
	public Enemy_Rogue(Controller creator, double X, double Y, double R, int HP, int ImageIndex)
	{
		super(creator, X, Y, R, HP, ImageIndex);
		speedCur = 4.5;
		frame=0;
		baseHp(HP);
		worth = 5;
		if(control.getRandomInt(3) == 0)
		{
			runRandom();
		}
		rotation = control.getRandomInt(360);
		rads = rotation/r2d;
		frames = makeFrames();
		action = "Hide";
		frame = frames[5][0];
	}
	private int[][] makeFrames()
	{
		//				 move	  roll	  stun	 melee		sheild	  hide	 shoot
		int[] e = {0, 0};
		int[][] temp = {{0, 19}, {54, 65}, e, {20, 45, 28, 36}, e, {46, 52}, e};
		return temp;
	}
	protected void frameNoLOS()
	{
		if(inDanger>0)
		{
			if(rollTimer<0)
			{
				rollSideways();
			} else
			{
				runSideways();
			}
		} else
		{
			if(findWall())
			{
				action = "Hide";
				frame = frames[5][0];
				firstHit = true;
			}
		}
	}
	protected void frameLOS()
	{
		distanceFound = distanceToPlayer();
		if(distanceFound < 30)
		{
			turnToward();
			action = "Melee";
			frame=frames[3][0];
		} else if(inDanger>1)
		{
			rollSideways();
		} else
		{
			runTowards();
		}
	}
	@Override
	protected void attacking()
	{
		for(int i = 2; i < frames[3].length; i++)
		{
			if(frame==frames[3][i])
			{
				if(firstHit)
				{
					meleeAttack(600, 25, 20);
					firstHit = false;
				} else
				{
					meleeAttack(120, 25, 20);
				}
			}
		}
	}
	@Override
	protected void hiding()
	{
		distanceFound = distanceToPlayer();
		action = "Nothing";
		if(distanceFound < 30)
		{
			turnToward();
			action = "Melee";
			frame=frames[3][0];
		} else if(inDanger>1)
		{
			rollSideways();
		} else
		{
			action = "Hide";
		}
	}
	@Override
	protected void shooting() {}
	@Override
	protected void finishWandering(){}
	@Override
	protected void blocking() {}
	protected boolean findWall()
	{
		int op200 = -1;
		int op100 = -1;
		for(int i = 0; i < 6; i++)
		{
			if(checkObstructions(x, y, rotation + (i*60) / r2d, 10, true, fromWall)) return true;
			else if(checkObstructions(x, y, rotation + (i*60) / r2d, 100, true, fromWall)) op200 = i;
			else if(checkObstructions(x, y, rotation + (i*60) / r2d, 200, true, fromWall)) op100 = i;
		}
		if(op100 != -1)
		{
			rotation += op100*60;
			rads = rotation/r2d;
			run(4);
		} else if(op200 != -1)
		{
			rotation += op200*60;
			rads = rotation/r2d;
			run(4);
		} else
		{
			runRandom();
		}
		return false;
	}
}