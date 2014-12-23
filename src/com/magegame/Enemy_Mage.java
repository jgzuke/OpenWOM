/**
 * AI and variables for rogues
 */
package com.magegame;


public final class Enemy_Mage extends Enemy
{
	public Enemy_Mage(Controller creator, double X, double Y, int HP, int ImageIndex)
	{
		super(creator, X, Y, HP, ImageIndex);
		enemyType = 0;
		speedCur = 3.5;
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
		//				 move	  roll	    stun   melee   sheild   hide   shoot
		int[] e = {0, 0};
		int[][] temp = {{0, 19}, {20, 30}, e, e, e, e, e};
		return temp;
	}
	protected void frameNoLOS()
	{
		if(pathedToHitLength>1 && checkDistance(danger[0][0], danger[1][0], x, y)<100)
		{
			rollSideways();
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
		if(distanceFound<60)
		{
			rollAway();
		} else if(hp<400)
		{
			if(distanceFound < 140)
			{
				if(distanceFound<100)
				{
					runAway();
				} else
				{
					action = "Shoot";
					frame=frames[6][0];
				}
			}
		} else
		{
			if(distanceFound < 140)
			{
				action = "Shoot";
				frame=frames[6][0];
			} else
			{
				runTowardsPoint(control.player.x, control.player.y);
			}
		}
	}
	@Override
	protected void attacking() {}
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