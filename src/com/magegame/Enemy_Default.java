/**
 * AI and variables for rogues
 */
package com.magegame;
public final class Enemy_Default extends Enemy
{
	public Enemy_Default(Controller creator, double X, double Y, int HP, int Worth,
		boolean gun, boolean sheild, boolean hide, boolean sword, boolean Sick, int type)
	{
		super(creator, X, Y, HP, Worth, gun, sheild, hide, sword, Sick, type);
		if(control.getRandomInt(3) == 0)
		{
			runRandom();
		}
		rotation = control.getRandomInt(360);
		rads = rotation/r2d;
	}
	@Override
	protected void frameCall()
	{
		checkLOS((int)control.player.x, (int)control.player.y);
		checkDanger();
		otherActions();
		if(action.equals("Nothing"))
		{
			pickAction();
		}
		image = control.imageLibrary.enemy_Image[frame];
		super.frameCall();
	}
	protected void frameReactionsDangerLOS()
	{
		frameReactionsNoDangerLOS();
	}
	protected void frameReactionsDangerNoLOS()
	{
		distanceFound = checkDistance(danger[0][0], danger[1][0], x, y);
		if(distanceFound < 100)
		{
			if(hasSheild)
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
			frameReactionsNoDangerNoLOS();
		}
	}
	protected void frameReactionsNoDangerLOS()
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
					if(hasSword)
					{
						action = "Melee";
						frame = 46;
					} else
					{
						rollAway();
					}
			} else if(distanceFound < 200)
			{
					if(hasGun)
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
	protected void frameReactionsNoDangerNoLOS()
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
	@Override
	protected void attacking()
	{
		for(int i = 0; i < frames[4].length; i++)
		{
			if(frame==frames[4][i])
			{
				meleeAttack(damage);
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
		if(frame<27) //geting weapon ready+aiming
		{
			aimAheadOfPlayer();
		} else if(frame==36) // shoots
		{
			shootLaser();
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