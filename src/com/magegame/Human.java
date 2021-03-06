/**
 * Enemies and player, regains health, provides variables and universal getHit method
 */
package com.magegame;

import android.graphics.Bitmap;

import com.spritelib.Sprite;

abstract public class Human extends Sprite
{
	public Human(double X, double Y, double Rotation, int Frame,
			boolean IsVideo, boolean Playing, Bitmap Image) {
		super(X, Y, Rotation, Frame, IsVideo, Playing, Image);
		// TODO Auto-generated constructor stub
	}
	public Human(double X, double Y, int Width, int Height, double Rotation,
			int Frame, boolean IsVideo, boolean Playing, Bitmap Image) {
		super(X, Y, Width, Height, Rotation, Frame, IsVideo, Playing, Image);
		// TODO Auto-generated constructor stub
	}
	protected int hp;
	protected int hpMax;
	protected double r2d = 180 / Math.PI;
	protected double rads;
	protected double speedCur;
	protected boolean hitBack;
	protected boolean thisPlayer = false;
	protected Controller control;
	/**
	 * Regains health, ends walk animation, plays animation
	 */
	@
	Override
	protected void frameCall()
	{
		if(hp > hpMax)
		{
			hp = hpMax;
		}
	}
	/**
	 * takes damage
	 * @param damage amount of damage to take
	 */
	protected void getHit(double damage)
	{
		hp -= damage*2;
		if(hp < 1)
		{
			hp = 0;
			deleted = true;
		}
	}
	/**
	 * returns whether this object is the player
	 * @return is it the player
	 */
	protected boolean isThisPlayer() {
		return thisPlayer;
	}
}