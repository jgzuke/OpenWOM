/**
 * All enemies, sets reaction methods, contains checks and mathematical functions
 * @param danger holds Proj_Trackers headed towards object and their coordinates velocity etc
 */
package com.magegame;

import android.graphics.Bitmap;

import com.spritelib.Sprite;

abstract public class Structure extends Sprite
{
	public Structure(double X, double Y, int Width, int Height,
			double Rotation, Bitmap Image) {
		super(X, Y, Width, Height, Rotation, Image);
		// TODO Auto-generated constructor stub
	}
	protected int hp;
	protected int hpMax;
	protected int timer = 0;
	protected int width;
	protected int height;
	protected int worth;
	Controller control;
	/**
	 * Clears danger arrays, sets current dimensions, and counts timers
	 */
	@ Override
	protected void frameCall()
	{
		timer++;
		hp+=5;
		if(hp>hpMax)
		{
			hp=hpMax;
		}
	}
	/**
	 * Takes a sent amount of damage, modifies based on shields etc.
	 * if health below 0 kills enemy
	 * @param damage amount of damage to take
	 */
	protected void getHit(double damage)
	{
		if(!deleted)
		{
			control.player.abilityTimer_burst += damage*control.activity.premiumUpgrades[2]/30;
			control.player.abilityTimer_roll += damage*control.activity.premiumUpgrades[2]/50;
			control.player.abilityTimerTransformed_pound += damage*control.activity.premiumUpgrades[2]/50;
			control.player.abilityTimer_Proj_Tracker += damage*control.activity.premiumUpgrades[2]/100;
			control.player.sp += damage*0.00003;
			hp -= damage;
			if(hp < 1)
			{
				hp = 0;
				deleted = true;
				control.player.sp += 0.30;
				control.spriteController.createProj_TrackerEnemyAOE(x, y, 180, false);
				control.activity.playEffect("burst");
				control.spriteController.createConsumable(x, y, 0);
				for(int i = 0; i < worth; i ++)
				{
					double rads = control.getRandomDouble()*6.28;
					if(worth-i>20)
					{
						control.spriteController.createConsumable(x+Math.cos(rads)*12, y+Math.sin(rads)*12, 10);
						i+=19;
					} else if(worth-i>5)
					{
						control.spriteController.createConsumable(x+Math.cos(rads)*12, y+Math.sin(rads)*12, 9);
						i+=4;
					} else
					{
						control.spriteController.createConsumable(x+Math.cos(rads)*12, y+Math.sin(rads)*12, 7);
					}
				}
			}
		}
	}
	protected void baseHp() {
		hp *= Math.pow(control.getDifficultyLevelMultiplier(), ((double)hp/10000));
	}
}