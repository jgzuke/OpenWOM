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
		int[][] temp = {{0, 19}, {0, 0}, {0, 0}, {20, 45}, {46, 55}, {0, 0}, {0, 0}};
		return temp;
	}
	protected void frameNoLOS()
	{
		if(pathedToHitLength>1 && checkDistance(danger[0][0], danger[1][0], x, y)<100)
		{
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
		} else if(hp<800)
		{
				runAway();
		} else
		{
			runTowardsPoint(control.player.x, control.player.y);
		}
	}
	@Override
	protected void attacking()
	{
		for(int i = 0; i < frames[4].length; i++)
		{
			if(frame==frames[4][i])
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