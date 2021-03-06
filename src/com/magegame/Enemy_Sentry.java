/**
 * AI and variables for rogues
 */
package com.magegame;


public final class Enemy_Sentry extends Enemy
{
	public Enemy_Sentry(Controller creator, double X, double Y, double R, int HP, int ImageIndex)
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
		int[][] temp = {e, e, e, e, e, e, {20, 49}};
		return temp;
	}
	protected void frameNoLOS()
	{
	}
	protected void frameLOS()
	{
		distanceFound = distanceToPlayer();
		if(distanceFound<240 )
		{
			turnToward();
			action = "Shoot";
			frame=frames[6][0];
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
			aimAheadOfPlayer(v*2); //TODO add extra frames for when you aim
		} else if(frame==36) // shoots
		{
			control.spriteController.createProj_TrackerEnemy(rotation, Math.cos(rads) * v, Math.sin(rads) * v, 130, x, y);
			control.soundController.playEffect("arrowrelease");
			checkLOS();
			double distance = distanceToPlayer();
			if(LOS&&hp>600&&distance<160&&distance>50) frame=25; // shoots again
		}
	}
	@Override
	protected void finishWandering(){}
	@Override
	protected void blocking() {}
}