/**
 * AI and variables for rogues
 */
package com.magegame;


public final class Enemy_Mage extends Enemy
{
	int shoot = 4;
	int energy = 90;
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
	@ Override
	protected void frameCall()
	{
		shoot++;
		if(shoot>4) shoot = 4;
		energy++;
		if (energy>45) energy=45;
		super.frameCall();
	}
	private int[][] makeFrames()
	{
		//				 move	  roll	    stun   melee   sheild   hide   shoot
		int[] e = {0, 0};
		int[][] temp = {{0, 19}, {20, 31}, e, e, e, e, e};
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
		distanceFound = checkDistance(x, y, control.player.x,  control.player.y);
		if(distanceFound<60)		// MAGES ALWAYS MOVING, DONT STOP TO SHOOT
		{
			rollAway();
		} else if(pathedToHitLength>1 && checkDistance(danger[0][0], danger[1][0], x, y)<100)
		{
			rollSideways();
		} else if(hp<400 && distanceFound < 140)
		{
			runAway();
		} else
		{
			runAround(120, (int)distanceFound);
		}
		if(shoot>3&&energy>14)
		{
			shoot-=4;
			energy -= 15;
			int v = 10;		//projectile velocity
			double saveRads = rads;
			aimAheadOfPlayer(v*2);	// aim closer to player
			rads+=0.1;
			rads-=control.getRandomDouble()*0.2;	// add random factor to shot
			control.spriteController.createProj_TrackerEnemy(rads * r2d, Math.cos(rads) * v, Math.sin(rads) * v, 130, x, y);
			control.activity.playEffect("arrowrelease");
			rads = saveRads;
		}
		rotation = rads * r2d;
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
	private void shoot()
	{
		
	}
}