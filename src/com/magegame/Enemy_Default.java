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
		if(action.equals("Stun"))
		{
			frame=93;
			stunTimer--;
			if(stunTimer==0) action = "Nothing";		//stun over, go have fun
		} else if(action.equals("Melee"))
		{
			frame++;
			if(frame==53)
			{
				meleeAttack(300);
			} else if(frame==63)
			{
				meleeAttack(420);
			}
			if(frame==71) action = "Nothing";	//attack over
		} else if(action.equals("Sheild"))
		{
			frame++;
			if(frame==81) action = "Nothing";	//block done
		} else if(action.equals("Roll"))
		{
			x += xMove;
			y += yMove;
			frame++;
			if(frame==93) action = "Nothing";	//roll done
		} else if(action.equals("Hide"))
		{
			frame = 94;
			if(checkDistance(x, y, control.player.x,  control.player.y) < 30) //player close enough to attack
			{
				action = "Melee";
				frame = 49;
			}
		} else if(action.equals("Shoot"))
		{
			frame++;
			if(frame<27) //geting weapon ready+aiming
			{
				aimAheadOfPlayer();
			} else if(frame==36) // shoots
			{
				shootLaser();
				checkLOS((int)control.player.x, (int)control.player.y);
				if(LOS&&hp>600) frame=25; // shoots again
			} else if(frame==45) action = "Nothing";   // attack done
		} else if(action.equals("Run"))
		{
			frame++;
			if(frame == 19) frame = 0; // restart walking motion
			x += xMove;
			y += yMove;
			runTimer--;
			if(runTimer<1) action = "Nothing"; // stroll done
		} else if(action.equals("Move")||action.equals("Wander"))
		{
			if(pathedToHitLength > 0 || LOS)
			{
				 action = "Nothing";
			} else
			{
				frame++;
				if(frame == 19) frame = 0; // restart walking motion
				x += xMove;
				y += yMove;
				runTimer--;
				if(runTimer<1) //stroll over
				{
					if(action.equals("Move"))
					{
						frameReactionsNoDangerNoLOS();
					} else
					{
						if(control.getRandomInt(20) != 0) // we probably just keep wandering
						{
							runRandom();
						} else
						{
							action = "Nothing";
						}
					}
				}
			}
		}
		if(action.equals("Nothing"))
		{
			frame=0;
			if(pathedToHitLength > 0)
			{
				if(HasLocation)
				{
					frameReactionsDangerLOS();
				} else
				{
					frameReactionsDangerNoLOS();
				}
			} else
			{
				if(HasLocation)
				{
					frameReactionsNoDangerLOS();
				}
				else
				{
					frameReactionsNoDangerNoLOS();
				}
			}
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
}