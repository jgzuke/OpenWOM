/**
 * Handles cooldowns and stats for player and executes spells
 */
package com.magegame;

import android.widget.Toast;

public final class Player extends Human
{
	protected double touchY;
	private double damageMultiplier = 1;
	protected boolean playing = false;
	protected int rollTimer = 0;
	private double xMoveRoll;
	private double yMoveRoll;
	protected double sp = 1;
	protected double spMod = 1;
	protected double abilityTimer_roll = 0;
	protected double abilityTimer_burst = 0;
	protected double abilityTimer_Proj_Tracker = 0;
	private double xSave = 0;
	private double ySave = 0;
	private boolean usedDionysusWine = false;
	protected int projectileSpeed = 40;
	protected double touchX;
	protected boolean touching;
	protected boolean touchingShoot;
	protected double touchShootX;
	protected double touchShootY;
	protected int powerUpTimer = 0;
	protected int powerID = 0;
	private int minimumShootTime = 4;
	private double hpAccurate;
	Controller control;
	/**
	 * Sets all variables to start, sets image
	 * @param creator control object
	 */
	public Player(Controller creator)
	{
		super(0, 0, 0, 0, true, false, creator.imageLibrary.player_Image[0]);
		control = creator;
		resetVariables();
		/*if(control.activity.useHestiasBlessing>0)
		{
			control.activity.useHestiasBlessing --;
			damageMultiplier /= 2;
		}
		if(control.activity.useArtemisArrow>0)
		{
			control.activity.useArtemisArrow --;
			projectileSpeed = 17;
		}*/
	}
	/**
	 * resets all variables to the start of a match or round
	 */
	public void resetVariables()
	{
		control.imageLibrary.loadPlayerImage();
		damageMultiplier = 1;
		rollTimer = 0;
		sp = 1;
		abilityTimer_roll = 120;
		abilityTimer_burst = 250;
		abilityTimer_Proj_Tracker = 0;
		usedDionysusWine = false;
		projectileSpeed = 40;
		touching = false;
		x = 370;
		y = 160;
		hp = 7000;
		setHpMax(hp);
		deleted = false;
		playing = false;
		powerUpTimer=0;
	}
	/**
	 * Counts timers and executes movement and predefined behaviors
	 */
	@
	Override
	protected void frameCall()
	{
		minimumShootTime--;
		powerUpTimer--;
		if(usedDionysusWine)
		{
			super.getHit(100);
		}
		sp -= 0.0001;
		spMod = 1;
		speedCur = 3.7;
		if(sp > 1.5)
		{
			sp = 1.5;
		}
		if(sp < 0.5)
		{
			sp = 0.5;
		}
			double cooldown = 1;
			abilityTimer_roll += cooldown*1.4;
			if(abilityTimer_roll >= 120)
			{
				abilityTimer_roll = 120;
			}
			abilityTimer_burst += cooldown*1.4;
			abilityTimer_Proj_Tracker += cooldown*5;
			if(abilityTimer_burst >= 500)
			{
				abilityTimer_burst = 500;
			}
			if(abilityTimer_Proj_Tracker >= 91)
			{
				abilityTimer_Proj_Tracker = 91;
			}
			rollTimer--;
			if(frame == 30) // roll finished
			{
				frame = 0;
				playing = false;
			}
			if(frame == 19)frame = 0; // restart walking animation
			if(playing) frame++;
			if(frame > 31)frame = 0; // player stopped shooting
			super.frameCall();
			if(rollTimer < 1)
			{
				if(!deleted)
				{
					if(touchingShoot)
					{
						frame = 31;
						playing = false;
						rads = Math.atan2(touchShootY, touchShootX);
				        rotation=rads*180/Math.PI;
				        if(abilityTimer_Proj_Tracker > 30&&minimumShootTime<1)
		            	{
				        	releaseProj_Tracker();
				        	control.shootStick.rotation=rads*180/Math.PI;
			            	minimumShootTime = 2;
		            	}
					} else
					{
						if(!touching || (Math.abs(touchX) < 5 && Math.abs(touchY) < 5))
						{
							playing = false;
							frame = 0;
						}
						else
						{
							rads = Math.atan2(touchY, touchX);
							rotation = rads * r2d;
							movement();
						}
					}
				}
			} else
			{			
					x += xMoveRoll;
					y += yMoveRoll;
			}
		image = control.imageLibrary.player_Image[frame];
		sizeImage();
	}
	/**
	 * moves player at a set speed, direction is based of move stick
	 */
	protected void movement()
	{
		playing = true;
		rads = Math.atan2(touchY, touchX);
		rotation = rads * r2d;
		x += Math.cos(rads) * speedCur;
		y += Math.sin(rads) * speedCur;
	}
	/**
	 * shoots a power ball
	 */
	protected void releaseProj_Tracker()
	{
		if(abilityTimer_Proj_Tracker > 30)
		{
			if(rollTimer < 0)
			{
					control.spriteController.createProj_TrackerPlayer(rads*r2d, projectileSpeed, 130, x, y);
					abilityTimer_Proj_Tracker -= 30;
					control.activity.playEffect("shoot");
			}
		} else
		{
			//control.coolDown();
		}
	}
	/**
	 * rolls forward
	 */
	protected void roll()
	{
		if(abilityTimer_roll > 40)
		{
			rads = Math.atan2(touchY, touchX);
			rotation = rads * r2d;
			rollTimer = 11;
			playing = true;
			frame = 21;
			xMoveRoll = Math.cos(rads) * 8;
			yMoveRoll = Math.sin(rads) * 8;
			abilityTimer_roll -= 40;
		} else
		{
			Toast.makeText(control.context, "Cool Down", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * players burst attack
	 */
	protected void burst()
	{
		if(abilityTimer_burst > 300)
		{
			for(int i = 0; i<6; i++)
			{	
				control.spriteController.createProj_TrackerPlayerAOE(x-20+control.getRandomInt(40), y-20+control.getRandomInt(40), 130, true);
			}
			control.spriteController.createProj_TrackerPlayerBurst(x, y, 0);
			abilityTimer_burst -= 300;
			control.activity.playEffect("burst");
			control.activity.playEffect("burst");
			control.activity.playEffect("burst");
			control.playerBursted = 0;
		} else
		{
			Toast.makeText(control.context, "Cool Down", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * stuns player
	 */
	protected void stun()
	{
			if(rollTimer<2)
			{
				rotation = rads * r2d + 180;
		        roll();
		        frame = 0;
		        xMoveRoll /= 3;
		        yMoveRoll /= 3;
		        abilityTimer_roll += 20;
		        Toast.makeText(control.context, "Stunned!", Toast.LENGTH_SHORT).show();
			}
	}
	/**
	 * reduces and amplifies damage based on shields etc.
	 */
	@Override
	protected void getHit(double damage)
	{
		control.playerHit = 0;
		damage *= 0.7;
			damage *= damageMultiplier;
			super.getHit(damage);
			sp -= sp*damage/1500;
			if(deleted) control.die();
	}
	/**
	 * gives player a benefit, ranging from health to transformation
	 * @param PowerID id of power received
	 */
	protected void getPowerUp(int PowerID)
	{
		//if(PowerID<7||PowerID>10) control.activity.playEffect("powerup");
		//if(PowerID>6&&PowerID<11) control.activity.playMoney();
		switch(PowerID)
		{
		case 1:
			hp += 2000;
			if(hp>getHpMax())hp=getHpMax();
			break;
		case 2:
			abilityTimer_roll = 120;
			abilityTimer_Proj_Tracker = 90;
			abilityTimer_burst = 500;
			break;
		case 3:
			powerUpTimer=300;
			powerID=1;
			break;
		case 4:
			powerUpTimer=300;
			powerID=2;
			break;
		case 5:
			powerUpTimer=300;
			powerID=3;
			break;
		case 6:
			powerUpTimer=300;
			powerID=4;
			break;
		}
	}
	/**
	 * checks distance between two points
	 * @param fromX point one x
	 * @param fromY point one y
	 * @param toX point 2 x
	 * @param toY point 2 y
	 * @return distance between points
	 */
	private double checkDistance(double fromX, double fromY, double toX, double toY)
	{
		return Math.sqrt((Math.pow(fromX - toX, 2)) + (Math.pow(fromY - toY, 2)));
	}
}