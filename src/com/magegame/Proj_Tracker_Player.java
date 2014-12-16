/**
 * behavior for player power ball
 */
package com.magegame;

import com.spritelib.Sprite;

import android.util.Log;

public final class Proj_Tracker_Player extends Proj_Tracker
{
	/**
	 * Sets position, speed, power, and direction of travel
	 * @param creator control object
	 * @param X starting x position
	 * @param Y starting y position
	 * @param Power power bolt was fired with
	 * @param Xforward bolts x velocity
	 * @param Yforward bolts y velocity
	 * @param Rotation bolts direction of travel
	 */
	private double r2d = 180/Math.PI;
	private double speed;
	private SpriteController spriteController;
	protected Proj_Tracker_Player(Controller creator, int X, int Y, int Power, double Speed, double Rotation, SpriteController spriteControllerSet)
	{
		super(X, Y, Rotation, creator.imageLibrary.shotPlayer);
		spriteController = spriteControllerSet;
		control = creator;
		speed = Speed;
		xForward = Math.cos(Rotation/r2d) * Speed;
		yForward = Math.sin(Rotation/r2d) * Speed;
		if(control.checkHitBack(x, y, false))
		{
			explodeBack();
		}
		x +=(xForward);
		y +=(yForward);
		if(control.checkHitBack(x, y, false) && !deleted)
		{
			explodeBack();
		}
		realX = x;
		realY = y;
		power = Power;
		while(rotation<0)
		{
			rotation+=360;
		}
	}
	/**
	 * checks whether power ball hits any enemies
	 */
	@ Override
	protected void frameCall()
	{
		super.frameCall();
		if(control.enemyInView(x, y))
		{
			for(int i = 0; i < spriteController.enemies.size(); i++)
			{
				if(spriteController.enemies.get(i) != null && !deleted && spriteController.enemies.get(i).action.equals("Nothing"))
				{
					spriteController.enemies.get(i).setLevels(spriteController.enemies.get(i).levelCurrentPosition, x, y, xForward, yForward);
					spriteController.enemies.get(i).levelCurrentPosition++;
				}
			}
		}
	}
	@ Override
	/**
	 * explodes power ball when it hits back
	 */
	public void explodeBack()
	{
		spriteController.createProj_TrackerPlayerAOE((int) realX, (int) realY, 30, false);
		deleted = true;
	}
	@ Override
	/**
	 * explodes power ball when it hits enemy
	 */
	public void explode()
	{
		spriteController.createProj_TrackerPlayerAOE((int) realX, (int) realY, power/2, true);
		deleted = true;
	}
	@Override
	protected void hitTarget(int x, int y)
	{
		for(int i = 0; i < spriteController.enemies.size(); i++)
		{
			if(spriteController.enemies.get(i) != null && !deleted && !spriteController.enemies.get(i).action.equals("Roll"))
			{
				xDif = x - spriteController.enemies.get(i).x;
				yDif = y - spriteController.enemies.get(i).y;
				double distance = Math.pow(xDif, 2) + Math.pow(yDif, 2);
				if(distance < 600)
				{
					spriteController.enemies.get(i).getHit((int)power);
					explode();
				}
			}
		}
		for(int i = 0; i < spriteController.structures.size(); i++)
		{
			if(spriteController.structures.get(i) != null && !deleted)
			{
				xDif = x - spriteController.structures.get(i).x;
				yDif = y - spriteController.structures.get(i).y;
				double distance = Math.pow(xDif, 2) + Math.pow(yDif, 2);
				if(distance < 600)
				{
					spriteController.structures.get(i).getHit((int)power);
					explode();
				}
			}
		}
	}
	@Override
	protected void hitBack(int x, int y)
	{
		if(control.checkHitBack(x, y, false) && !deleted)
		{
			explodeBack();
		}
	}
}