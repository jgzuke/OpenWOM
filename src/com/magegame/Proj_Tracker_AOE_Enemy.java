/**
 * behavior for enemy power ball explosion
 */
package com.magegame;
public final class Proj_Tracker_AOE_Enemy extends Proj_Tracker_AOE
{
	/**
	 * sets image
	 * @param creator control object
	 * @param X starting x coordinate
	 * @param Y starting y coordinate
	 * @param Power power or size to start at
	 * @param Shrinking whether it is shrinking or growing
	 */
	public Proj_Tracker_AOE_Enemy(Controller creator, int X, int Y, double Power, boolean Shrinking, SpriteController spriteController)
	{
		super(creator, X, Y, Power, Shrinking, spriteController, creator.imageLibrary.shotAOEEnemy);
	}
	/**
	 * checks whether it damages player
	 */
	@ Override
	protected void frameCall()
	{
		super.frameCall();
		if(damaging)
		{
			xDif = x - control.player.x;
			yDif = y - control.player.y;
			if(Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2)) < widthDone+15)
			{			
				control.player.getHit(60);
			}
		}
	}
	/**
	 * if is is shrinking, explode creates more growing ones at certain points in time
	 */
	@ Override
	protected void explode(int power)
	{
		control.soundController.playEffect("electric");
		for(int i = 0; i<6; i++)
		{
			spriteController.createProj_TrackerEnemyAOE(x-15+control.getRandomInt(30), y-15+control.getRandomInt(30), power, true);
		}
		/*int power = 130;
		double veloc = 2+(control.getDifficultyLevelMultiplier()*5);
		double radians;
		double xMove;
		double yMove;
		rotation-=20;
		for(int i = 0; i < 4; i++)
		{
			rotation+=5;
			radians=rotation/r2d;
			xMove = Math.cos(radians)*veloc;
			yMove = Math.sin(radians)*veloc;
			control.createProj_TrackerEnemy(rotation,xMove,yMove,power, x+(2*xMove), y+(2*yMove));
		}*/
	}
}