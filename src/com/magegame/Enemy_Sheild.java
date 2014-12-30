/**
 * AI and variables for rogues
 */
package com.magegame;


public final class Enemy_Sheild extends Enemy
{
	public Enemy_Sheild(Controller creator, double X, double Y, int HP, int ImageIndex)
	{
		super(creator, X, Y, HP, ImageIndex);
		enemyType = 0;
		speedCur = 3.8;
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
		
	}
	private int[][] makeFrames()
	{
		//				 move	  roll	  stun	 melee		sheild	  hide	 shoot
		int[] e = {0, 0};
		int[][] temp = {{0, 19}, e, e, {20, 45, 27, 36}, {46, 55}, e, e};
		return temp;
	}
	protected void frameNoLOS()
	{
		if(inDanger>0)
		{
			turnToward(closestDanger[0], closestDanger[1]);
			action = "Sheild";
			frame=frames[4][0];
		} else
		{
			searchOrWander();
		}
	}
	protected void frameLOS()
	{
		rads = Math.atan2(( control.player.y - y), (control.player.x - x));
		rotation = rads * r2d;
		distanceFound = checkDistance(x, y, control.player.x,  control.player.y);
		if(distanceFound < 30)
		{
			action = "Melee";
			frame=frames[3][0];
		} else if(inDanger>1)
		{
			turnToward(closestDanger[0], closestDanger[1]);
			action = "Sheild";
			frame=frames[4][0];
		} else if(hp<800)
		{
			runAway();
		} else
		{
			runTowards(control.player.x, control.player.y);
		}
	}
	@Override
	protected void attacking()
	{
		for(int i = 2; i < frames[3].length; i++)
		{
			if(frame==frames[3][i])
			{
				meleeAttack(200, 25, 20);
			}
		}
	}
	@Override
	protected void hiding() {}
	@Override
	protected void shooting() {}
	@Override
	protected void finishWandering()
	{
		if(control.getRandomInt(20) != 0) // we probably just keep wandering
		{
			runRandom();
		}
	}
}