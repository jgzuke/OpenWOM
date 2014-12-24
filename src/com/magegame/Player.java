/**
 * Handles cooldowns and stats for player and executes spells
 */
package com.magegame;

import android.util.Log;
import android.widget.Toast;

public final class Player extends Human
{
	protected double touchY;
	protected boolean playing = false;
	protected int rollTimer = 0;
	private double xMoveRoll;
	private double yMoveRoll;
	protected double sp = 1;
	protected double spMod = 1;
	protected double abilityTimer_roll = 0;
	protected double abilityTimer_burst = 0;
	protected double abilityTimer_Proj_Tracker = 0;
	protected double touchX;
	protected boolean touching;
	protected boolean touchingShoot;
	protected double touchShootX;
	protected double touchShootY;
	protected int powerUpTimer = 0;
	protected int powerID = 0;
	
	Controller control;
	/**
	 * Sets all variables to start, sets image
	 * @param creator control object
	 */
	
	
	/*
	 * these variables are for changes and stuff
	 */
	private int minimumShootTime = 4;	// time between shots
	private int shotDmg = 130;			// damage of shot
	private int burstDmg = 130;			// damage of burst
	private double takenDmg = 0.7;		// part of damage player takes
	private int hpStart = 7000;			// plaers base hp
	private double rollCharge = 1;		// how fast roll charges
	private double burstCharge = 1;		// how fast burst charges
	private double shotCharge = 1.8;		// how fast shot charges
	private double mySpeed = 4.2;		// players speed
	private double stunChance = 1;		// change to actully get stunned
	private double shotSpeed = 7;		// speed of shots
	private int shotHold = 91;			// ax shots stored
	private int burstHold = 500;		// max burst stored
	private int rollHold = 120;			// max roll stored
	private double spDrain = 0.0001;	// sp drained every frame
	private double maxSP = 1.5;			// maximum sp
	private double minSP = 0.5;			// minimum sp
	private int powerUpTime = 300;		// time cooldowns last for
	protected double chargeSP = 1;		// how much doing damage charges sp
	protected double chargeCooldown = 1;// how much doing damage charges cooldowns
	protected double tracking = 6;		// how much tracking can turn fireballs
	private double findChance = 1;		// chance of finding items
	private double spLose = 1;			// sp lost on hit
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
		rollTimer = 0;
		sp = 1;
		abilityTimer_roll = 120;
		abilityTimer_burst = 250;
		abilityTimer_Proj_Tracker = 0;
		touching = false;
		x = 370;
		y = 160;
		deleted = false;
		playing = false;
		powerUpTimer=0;
		setAttributes();
		hp = hpStart;
		setHpMax(hp);
	}
	/**
	 * 
	 */
	private void setAttributes()
	{
		minimumShootTime = 4;	// time between shots
		shotDmg = 130;			// damage of shot
		burstDmg = 130;			// damage of burst
		takenDmg = 0.7;		// part of damage player takes
		hpStart = 7000;			// plaers base hp
		rollCharge = 1;		// how fast roll charges
		burstCharge = 1;		// how fast burst charges
		shotCharge = 3;		// how fast shot charges
		mySpeed = 6;		// players speed
		stunChance = 1;		// change to actully get stunned
		shotSpeed = 15;		// speed of shots
		shotHold = 91;			// ax shots stored
		burstHold = 500;		// max burst stored
		rollHold = 120;			// max roll stored
		spDrain = 0.0001;	// sp drained every frame
		maxSP = 1.5;			// maximum sp
		minSP = 0.5;			// minimum sp
		powerUpTime = 300;		// time cooldowns last for
		chargeSP = 1;		// how much doing damage charges sp
		chargeCooldown = 1;// how much doing damage charges cooldowns
		tracking = 6;		// how much tracking can turn fireballs
		findChance = 1;		// chance of finding items
		spLose = 1;			// sp lost on hit
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
		sp -= spDrain;
		spMod = 1;
		speedCur = mySpeed;
		if(sp > maxSP) sp = maxSP;
		if(sp < minSP) sp = minSP;
		abilityTimer_roll += rollCharge;
		abilityTimer_burst += burstCharge;
		abilityTimer_Proj_Tracker += shotCharge;
		if(abilityTimer_burst >= burstHold) abilityTimer_burst = burstHold;
		if(abilityTimer_Proj_Tracker >= shotHold) abilityTimer_Proj_Tracker = shotHold;
		if(abilityTimer_roll >= rollHold) abilityTimer_roll = rollHold;
		if(rollTimer > 0)
		{
			rollTimer--;
			x += xMoveRoll;
			y += yMoveRoll;
		}
		if(frame == 30) // roll finished
		{
			frame = 0;
			playing = false;
		}
		if(frame == 19) frame = 0; // restart walking animation
		if(playing) frame++;
		if(frame > 31) frame = 0; // player stopped shooting
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
		}
		image = control.imageLibrary.player_Image[frame];
		sizeImage();
		if(x < 10) x = (10);
		if(x > control.levelController.levelWidth - 10) x = (control.levelController.levelWidth - 10);
		if(y < 10) y = (10);
		if(y > control.levelController.levelHeight - 10) y = (control.levelController.levelHeight - 10);
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
		Log.e("mine", "sdg");
		if(abilityTimer_Proj_Tracker > 30)
		{
			Log.e("mine", "dsfh");
			if(rollTimer < 1)
			{
				Log.e("mine", "ljh");
				control.spriteController.createProj_TrackerPlayer(rads*r2d, shotSpeed, shotDmg, x, y);
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
				control.spriteController.createProj_TrackerPlayerAOE(x-20+control.getRandomInt(40), y-20+control.getRandomInt(40), burstDmg, true);
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
		if(Math.random()<stunChance)
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
	}
	/**
	 * reduces and amplifies damage based on shields etc.
	 */
	@Override
	protected void getHit(double damage)
	{
		control.playerHit = 0;
			damage *= takenDmg;
			super.getHit(damage);
			sp -= sp*damage/1500*spLose;
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
			if(hp>getHpMax()) hp=getHpMax();
			break;
		case 2:
			abilityTimer_roll = rollHold;
			abilityTimer_Proj_Tracker = shotHold;
			abilityTimer_burst = burstHold;
			break;
		case 3:
			powerUpTimer=powerUpTime;
			powerID=1;
			break;
		case 4:
			powerUpTimer=powerUpTime;
			powerID=2;
			break;
		case 5:
			powerUpTimer=powerUpTime;
			powerID=3;
			break;
		case 6:
			powerUpTimer=powerUpTime;
			powerID=4;
			break;
		}
	}
}