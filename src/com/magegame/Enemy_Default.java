/**
 * AI and variables for rogues
 */
package com.magegame;

public final class Enemy_Default extends Enemy
{
	public Enemy_Default(Controller creator, double X, double Y, int HP, int ImageIndex)
	{
		super(creator, X, Y, HP, ImageIndex);
		enemyType = 0;
		speedCur = 1.8;
		frame=0;
		baseHp(HP);
		worth = 5;
		if(control.getRandomInt(3) == 0)
		{
			runRandom();
		}
		rotation = control.getRandomInt(360);
		rads = rotation/r2d;
	}
	protected void frameNoLOS()
	{
		if(pathedToHitLength>1 && checkDistance(danger[0][0], danger[1][0], x, y)<100)
		{
			if(true)//hasSheild
			{
				action = "Sheild";
				frame=72;
			} else
			{
				rads = Math.atan2((danger[1][0] - y), (danger[0][0] - x));
				roll();
			}
		} else
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
	}
	protected void frameLOS()
	{
		rads = Math.atan2(( control.player.y - y), (control.player.x - x));
		rotation = rads * r2d;
		distanceFound = checkDistance(x, y, control.player.x,  control.player.y);
		if(hp<800)
		{
			if(distanceFound < 30)
			{
				rollAway();
			} else
			{
				runAway();
			}
		} else
		{
			if(distanceFound < 30)
			{
					if(true)//hasSword
					{
						action = "Melee";
						frame = 46;
					} else
					{
						rollAway();
					}
			} else if(distanceFound < 200)
			{
					if(true)//hasGun
					{
						if(LOS)
						{
							action = "Shoot";
							frame = 21;
						} else
						{
							runTowardsPoint(control.player.x, control.player.y);
						}
					} else
					{
						runTowardsPoint(control.player.x, control.player.y);
					}
			} else
			{
				runTowardsPoint(control.player.x, control.player.y);
			}
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
	protected void hiding() {
		if(checkDistance(x, y, control.player.x,  control.player.y) < 30) //player close enough to attack
		{
			action = "Melee";
			frame = frames[3][0];
		}
	}
	@Override
	protected void shooting() {
		int velocity = 5; //projectile velocity
		if(frame<27) //geting weapon ready+aiming
		{
			aimAheadOfPlayer(velocity);
		} else if(frame==36) // shoots
		{
			rads = rotation/r2d;
			control.spriteController.createProj_TrackerEnemy(rotation, Math.cos(rads) * velocity, Math.sin(rads) * velocity, 130, x, y);
			control.activity.playEffect("arrowrelease");
			checkLOS((int)control.player.x, (int)control.player.y);
			if(LOS&&hp>600) frame=25; // shoots again
		}
	}
	@Override
	protected void finishWandering() {
		if(control.getRandomInt(20) != 0) // we probably just keep wandering
		{
			runRandom();
		}
	}
}