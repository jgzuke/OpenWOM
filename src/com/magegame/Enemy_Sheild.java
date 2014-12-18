/**
 * AI and variables for rogues
 */
package com.magegame;

import android.graphics.Bitmap;

public final class Enemy_Sheild extends Enemy
{
	public Enemy_Sheild(Controller creator, double X, double Y, int HP, int ImageIndex)
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
		image = myImage[frame];
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
			action = "Sheild";
			frame=frames[4][0];
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
				action = "Melee";
				frame=frames[3][0];
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
				meleeAttack(200);
			}
		}
	}
	@Override
	protected void hiding() {}
	@Override
	protected void shooting() {}
	@Override
	protected void finishWandering() {
		if(control.getRandomInt(20) != 0) // we probably just keep wandering
		{
			runRandom();
		}
	}
}