/**
 * Defines behavior for enemy crossbowmans projectiles
 */
package com.magegame;
public final class Proj_Tracker_Enemy extends Proj_Tracker
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
	public Proj_Tracker_Enemy(Controller creator, int X, int Y, int Power, double Xforward, double Yforward, double Rotation)
	{
		super(X, Y, Rotation, creator.imageLibrary.shotEnemy[0]);
		video = creator.imageLibrary.shotEnemy;
		control = creator;
		realX = x;
		realY = y;
		xForward = Xforward;
		yForward = Yforward;
		power = Power;
		if((control.player.frame < 22 || control.player.frame>28)&& !deleted) // currentframe under 22 because if player rolls it doesnt hit
		{
			xDif = x - control.player.x;
			yDif = y - control.player.y;
			if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 100) // if player within 10 pixels
			{
				control.player.getHit(power*4);
				deleted = true;
				control.soundController.playEffect("arrowhit");
				if(control.getRandomDouble() > 0.8) // chance of stunning player
				{
					control.player.rads = Math.atan2(yForward, xForward);
					control.player.stun();
				}
			}
		}
	}
	/**
	 * Checks whether projectile hits obstacles or player
	 */
	@ Override
	protected void frameCall()
	{
		super.frameCall();
		xDif = x - control.player.x;
		yDif = y - control.player.y;
		double newRotation = Math.atan2(yDif, xDif) * r2d;
		newRotation -= 180;
		double rotChange = 2;
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
		xForward = Math.cos(rotation/r2d) * 10;
		yForward = Math.sin(rotation/r2d) * 10;
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
		control.spriteController.createProj_TrackerEnemyAOE((int) realX, (int) realY, 30, false);
		deleted = true;
	}
	@ Override
	/**
	 * explodes power ball when it hits enemy
	 */
	public void explode()
	{
		control.spriteController.createProj_TrackerEnemyAOE((int) realX, (int) realY, power/2, true);
		deleted = true;
	}
	@Override
	protected void hitTarget(int px, int py)
	{
		if(control.player.frame < 22 && !deleted) // currentframe under 22 because if player rolls it doesnt hit
		{
			xDif = px - control.player.x;
			yDif = py - control.player.y;
			if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 225) // if player within 10 pixels
			{
				control.player.getHit(power*4);
				deleted = true;
				control.soundController.playEffect("arrowhit");
				if(control.getRandomDouble() > 0.8) // chance of stunning player
				{
					control.player.rads = Math.atan2(yForward, xForward);
					control.player.stun();
				}
			}
		}
	}
	@Override
	protected void hitBack(int px, int py)
	{
		if(control.wallController.checkHitBack(px, py, false) && !deleted)
		{
			explodeBack();
		}
	}
}