/**
 * Handles cooldowns and stats for player and executes spells
 */
package com.magegame;

import android.util.Log;
import android.widget.Toast;

public final class Player extends Human
{
	protected int level = 0;
	protected int experience = 0;
	protected int blessingTimer = 0;
	protected int blessing = 0;
	
	protected double touchY;
	protected boolean playing = false;
	protected int rollTimer = 0;
	private double xMoveRoll;
	private double yMoveRoll;
	protected double abilityTimer_roll = 0;
	protected double abilityTimer_burst = 0;
	protected double abilityTimer_Proj_Tracker = 0;
	protected double touchX;
	protected boolean touching;
	protected boolean touchingShoot;
	protected double touchShootX;
	protected double touchShootY;
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
	private double takenDmg = 0.7;		// part of damage player takes
	private int hpStart = 7000;			// plaers base hp
	private double rollCharge = 1;		// how fast roll charges
	private double burstCharge = 1;		// how fast burst charges
	private double shotCharge = 1.8;	// how fast shot charges
	private double stunChance = 1;		// change to actully get stunned
	private int shotHold = 91;			// max shots stored
	protected double chargeCooldown = 1;// how much doing damage charges cooldowns
	protected double tracking = 1;		// how much tracking can turn fireballs
	public Player(Controller creator)
	{
		super(0, 0, 0, 0, true, false, null);
		control = creator;
		x = 500;
		y = 500;
	}
	/**
	 * resets all variables to the start of a match or round
	 */
	public void resetVariables()
	{
		//control.imageLibrary.loadPlayerImage();
		rollTimer = 0;
		abilityTimer_roll = 120;
		abilityTimer_burst = 250;
		abilityTimer_Proj_Tracker = 0;
		touching = false;
		deleted = false;
		playing = false;
		blessingTimer=0;
		blessing = 0;
		setAttributes();
		hp = hpStart;
		hpMax = hp;
	}
	/**
	 * 
	 */
	protected void setAttributes()
	{
		/*		BLESSINGS
		 * 1: attack
		 * 2: armor
		 * 3: speed
		 * 4: cooldown
		 */
		ItemController items = control.itemControl;
		minimumShootTime = 5 - items.staff/3;						// time between shots
		shotDmg = 130 + (items.staff*10);							// damage of shot
		takenDmg = 1 - (double)(items.helm + items.shirt)/20;		// part of damage player takes
		hpStart = 7000 + 500*level;									// plaers base hp
		rollCharge = 1+(level*0.1);									// how fast roll charges
		burstCharge = 1+(level*0.1);								// how fast burst charges
		shotCharge = 3+(level*0.1);									// how fast shot charges
		speedCur = 6+(level*0.2);									// players speed
		stunChance = 1 - (double)(items.helm + items.shirt)/20;		// change to actully get stunned
		shotHold = 91 + (items.staff*5);							// max shots stored
		chargeCooldown = 1+(level*0.1);								// how much doing damage charges cooldowns
		tracking = 2 + (items.staff/2);								// how much tracking can turn fireballs
		switch(blessing)
		{
			case 1:
				shotDmg *= 1.5;
				break;
			case 2:
				takenDmg *= 0.8;
				break;
			case 3:
				speedCur *= 1.3;
				break;
			case 4:
				rollCharge *= 1.4;
				burstCharge *= 1.4;
				shotCharge *= 1.4;
				chargeCooldown *= 1.4;
				break;
			default: break;
		}
	}
	/**
	 * Counts timers and executes movement and predefined behaviors
	 */
	@
	Override
	protected void frameCall()
	{
		minimumShootTime--;
		blessingTimer--;
		if(blessingTimer == 0)
		{
			blessing = 0;
			setAttributes();
		}
		if(blessingTimer > 300) blessingTimer = 300;
		abilityTimer_roll += rollCharge;
		abilityTimer_burst += burstCharge;
		abilityTimer_Proj_Tracker += shotCharge;
		if(abilityTimer_burst >= 500) abilityTimer_burst = 500;
		if(abilityTimer_Proj_Tracker >= shotHold) abilityTimer_Proj_Tracker = shotHold;
		if(abilityTimer_roll >= 120) abilityTimer_roll = 120;
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
			        	control.graphicsController.shootStick.rotation=rads*180/Math.PI;
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
		if(abilityTimer_Proj_Tracker > 30)
		{
			if(rollTimer < 1)
			{
				control.spriteController.createProj_TrackerPlayer(rads*r2d, 10, shotDmg, x, y);
				abilityTimer_Proj_Tracker -= 30;
				control.soundController.playEffect("shoot");
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
				control.spriteController.createProj_TrackerPlayerAOE(x-20+control.getRandomInt(40), y-20+control.getRandomInt(40), shotDmg, true);
			}
			control.spriteController.createProj_TrackerPlayerBurst(x, y, 0);
			abilityTimer_burst -= 300;
			control.soundController.playEffect("burst");
			control.graphicsController.playerBursted = 0;
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
		control.graphicsController.playerHit = 0;
		damage *= takenDmg;
		super.getHit(damage);
		if(deleted) control.die();
	}
	/**
	 * gives player a benefit, ranging from health to transformation
	 * @param PowerID id of power received
	 */
	protected void getPowerUp(int PowerID)
	{
		switch(PowerID)
		{
		case 1:
			hp += 2000;
			if(hp>hpMax) hp=hpMax;
			break;
		case 2:
			abilityTimer_roll = 500;
			abilityTimer_Proj_Tracker = shotHold;
			abilityTimer_burst = 120;
			break;
		}
	}
}