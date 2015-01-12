/**
 * AI and variables for rogues
 */
package com.magegame;


public final class Enemy_Archer extends Enemy
{
	public Enemy_Archer(Controller creator, double X, double Y, double R, int HP, int ImageIndex)
	{
		super(creator, X, Y, R, HP, ImageIndex);
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
		//				 move	  roll	  stun	 melee		sheild	  hide	 shoot
		int[] e = {0, 0};
		int[][] temp = {{0, 19}, e, e, e, e, e, {20, 49}};
		return temp;
	}
	protected void frameNoLOS()
	{
		searchOrWander();
	}
	protected void frameLOS()
	{
		distanceFound = distanceToPlayer();
		if(distanceFound < 50 || hp<600 && distanceFound<100)
		{
			runAway();
		} else if(distanceFound<140)
		{
			turnToward();
			action = "Shoot";
			frame=frames[6][0];
		} else
		{
			runTowards();
		}
	}
	@Override
	protected void attacking() {}
	@Override
	protected void hiding() {}
	@Override
	protected void shooting()
	{
		int v = 10; //projectile velocity
		if(frame<34) //geting weapon ready+aiming
		{
			aimAheadOfPlayer(v); //TODO add extra frames for when you aim
		} else if(frame==36) // shoots
		{
			control.spriteController.createProj_TrackerEnemy(rotation, Math.cos(rads) * v, Math.sin(rads) * v, 130, x, y);
			control.soundController.playEffect("arrowrelease");
			checkLOS((int)control.player.x, (int)control.player.y);
			double distance = checkDistance(x, y, control.player.x,  control.player.y);
			if(LOS&&hp>600&&distance<160&&distance>50) frame=25; // shoots again
		}
	}
	@Override
	protected void finishWandering()
	{
		if(control.getRandomInt(20) != 0) // we probably just keep wandering
		{
			runRandom();
		}
	}
}