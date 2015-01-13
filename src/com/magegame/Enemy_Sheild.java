/**
 * AI and variables for rogues
 */
package com.magegame;


public final class Enemy_Sheild extends Enemy
{
	public Enemy_Sheild(Controller creator, double X, double Y, double R, int HP, int ImageIndex)
	{
		super(creator, X, Y, R, HP, ImageIndex);
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
			turnToward();
			action = "Sheild";
			frame=frames[4][0];
		} else
		{
			searchOrWander();
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
			turnToward();
			action = "Sheild";
			frame=frames[4][0];
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
	@Override
	protected void blocking()
	{
		turnToward();
		if(frame<55 && frame> 47)
		{
			checkDanger();
			if(inDanger>0) frame = 50;
		}
	}
}