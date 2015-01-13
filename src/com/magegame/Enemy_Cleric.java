/**
 * AI and variables for rogues
 */
package com.magegame;


public final class Enemy_Cleric extends Enemy
{
	int shoot = 4;
	int energy = 90;
	Enemy target;
	public Enemy_Cleric(Controller creator, double X, double Y, double R, int HP, int ImageIndex)
	{
		super(creator, X, Y, R, HP, ImageIndex);
		speedCur = 3;
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
		if(shoot>6) shoot = 6;
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
			getTarget();
			if(target == null)
			{
				searchOrWander();
			} else
			{
				healTarget();
			}
		}
	}
	protected void frameLOS()
	{
		getTarget();
		distanceFound = distanceToPlayer();
		if(distanceFound<80)		// MAGES ALWAYS MOVING, DONT STOP TO SHOOT
		{
			rollAway();
		} else if(inDanger>0)
		{
			rollSideways();
		} else
		{
			if(target == null)
			{
				if(distanceFound<120)
				{
					runAway();
				} else if(distanceFound < 180)
				{
					runAround(150, (int)distanceFound);
				} else
				{
					runTowards();
				}
				if(shoot>5&&energy>21&& distanceFound < 180)
				{
					shoot();
				}
			} else
			{
				healTarget();
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
	private void shoot()
	{
		shoot-=6;
		energy -= 22;
		int v = 10;		//projectile velocity
		double saveRads = rotation/r2d;
		aimAheadOfPlayer(v*2);	// aim closer to player
		rads+=0.1;
		rads-=control.getRandomDouble()*0.2;	// add random factor to shot
		control.spriteController.createProj_TrackerEnemy(rads * r2d, Math.cos(rads) * v, Math.sin(rads) * v, 130, x, y);
		control.soundController.playEffect("arrowrelease");
		rads = saveRads;
		rotation = rads*r2d;
	}
	private void getTarget()
	{
		if(target == null)
		{
			for(int i = 0; i < control.spriteController.enemies.size(); i++)
			{
				Enemy enemy = control.spriteController.enemies.get(i);
				if(enemy.hp < enemy.hpMax && distanceTo(enemy.x, enemy.y)<200 && checkLOS((int)enemy.x, (int)enemy.y))
				{
					target = enemy;
					i = 999;
				}
			}
		}
	}
	private void healTarget()
	{
		turnToward(target.x, target.y);
		target.hp += 20;
		frame = 5;
		playing = false;
		if(target.hp > target.hpMax)
		{
			target.hp = target.hpMax;
			target = null;
		}
	}
	@Override
	protected void blocking() {}
}