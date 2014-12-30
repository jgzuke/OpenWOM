/**
 * behavior for player power ball
 */
package com.magegame;

import com.spritelib.Sprite;
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
	private Sprite target;
	private double r2d = 180/Math.PI;
	private double speed;
	private double rotChange;
	private SpriteController spriteController;
	protected Proj_Tracker_Player(Controller creator, int X, int Y, int Power, double Speed, double Rotation, SpriteController spriteControllerSet)
	{
		super(X, Y, Rotation, creator.imageLibrary.shotPlayer[0]);
		spriteController = spriteControllerSet;
		video = creator.imageLibrary.shotPlayer;
		control = creator;
		speed = Speed;
		xForward = Math.cos(Rotation/r2d) * Speed;
		yForward = Math.sin(Rotation/r2d) * Speed;
		if(control.wallController.checkHitBack(x, y, false))
		{
			explodeBack();
		}
		x +=(xForward);
		y +=(yForward);
		if(control.wallController.checkHitBack(x, y, false) && !deleted)
		{
			explodeBack();
		}
		realX = x;
		realY = y;
		power = Power;
		rotChange = control.player.tracking;
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
		if(target != null)
		{
			xDif = target.x-x;
			yDif = target.y-y;
			double newRotation = Math.atan2(yDif, xDif) * r2d;
			double fix = compareRot(newRotation/r2d);
			if(fix>rotChange/2)
			{
				rotation += rotChange;
			} else if(fix<-rotChange/2)
			{
				rotation -= rotChange;
			} else
			{
				rotation += fix;
			}
			xForward = Math.cos(rotation/r2d) * speed;
			yForward = Math.sin(rotation/r2d) * speed;
			double needToTurn = Math.abs(rotation-newRotation);
			if(needToTurn>180) needToTurn = Math.abs(needToTurn-360);
			if(needToTurn>20||target.deleted) target = null;
		} else
		{
			for(int i = 0; i < spriteController.enemies.size(); i++)
			{
				if(spriteController.enemies.get(i) != null && !deleted)
				{
					if(goodTarget(spriteController.enemies.get(i), 200)) target = spriteController.enemies.get(i);
				}
			}
			for(int i = 0; i < spriteController.structures.size(); i++)
			{
				if(spriteController.structures.get(i) != null && !deleted)
				{
					if(goodTarget(spriteController.structures.get(i), 200)) target = spriteController.structures.get(i);
				}
			}
		}
	}
	protected boolean goodTarget(Sprite s, int d)
	{
		xDif = s.x-x;
		yDif = s.y-y;
		double distance = Math.sqrt(Math.pow(xDif, 2)+Math.pow(yDif, 2));
		double newRotation = Math.atan2(yDif, xDif) * r2d;
		double needToTurn = Math.abs(rotation-newRotation);
		if(needToTurn>180) needToTurn = 360-needToTurn;
		return needToTurn<20&&distance<d;
	}
	public double compareRot(double newRotation)
	{
		newRotation*=r2d;
		double fix = 400;
		while(newRotation<0) newRotation+=360;
		while(rotation<0) rotation+=360;
		if(newRotation>290 && rotation<70) newRotation-=360;
		if(rotation>290 && newRotation<70) rotation-=360;
		fix = newRotation-rotation;
		return fix;
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
		if(control.wallController.checkHitBack(x, y, false) && !deleted)
		{
			explodeBack();
		}
	}
}