/**
 * behavior for all projectiles
 */
package com.magegame;

import android.graphics.Bitmap;

import com.spritelib.Sprite;

abstract public class Proj_Tracker extends Sprite
{
	public Proj_Tracker(double X, double Y,
			double Rotation, Bitmap Image) {
		super(X, Y, Rotation, Image);
		// TODO Auto-generated constructor stub
	}
	protected boolean hitBack;
	protected double xForward;
	protected double yForward;
	protected int power;
	protected double xDif = 0;
	protected double yDif = 0;
	protected double realX;
	protected double realY;
	protected Bitmap [] video;
	Controller control;
	/**
	 * moves ball forward and decreases power
	 */
	@ Override
	protected void frameCall()
	{
		image = video[control.getRandomInt(5)];
		for(int i = 0; i < 8; i++)
		{
			realX += xForward/8;
			realY += yForward/8;
			hitTarget((int)realX, (int)realY);
			hitBack((int)realX, (int)realY);
		}
		x = (int) realX;
		y = (int) realY;
	}
	abstract protected void hitTarget(int x, int y);
	abstract protected void hitBack(int x, int y);
	abstract public void explodeBack();
	abstract public void explode();
}