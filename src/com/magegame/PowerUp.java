/**
 * Handles behavior for powerups
 */
package com.magegame;

import com.spritelib.Sprite;

public final class PowerUp extends Sprite
{
	protected int ID;
	/**
	 * sets image, dimensions, and position
	 * @param creator control object
	 * @param X starting x value
	 * @param Y starting y value
	 * @param Type type of powerup
	 */
	Controller control;
	public PowerUp(Controller creator, double X, double Y, int Type)
	{
		super(X, Y, 30, 30, 0, null);
		control = creator;
		ID=Type;
		if(ID == 0)
		{
			ID = control.getRandomInt(6)+1;
			image = creator.imageLibrary.powerUps[ID-1];
		} else
		{
			image = creator.imageLibrary.powerUps[Type-1];
		}
	}
	/**
	 * checks whether player is close enough to pick up
	 */
	@ Override
	protected void frameCall()
	{
		if(Math.abs(x - control.player.x)<20&&Math.abs(y - control.player.y)<20)
		{			
			control.player.getPowerUp(ID);
			deleted = true;
		}
	}
}