/**
 * behavior for rectangular walls
 */
package com.magegame;

import java.util.ArrayList;

public class Wall_Rectangle extends Wall
{
	private int x;
	private int y;
	protected int oRX1;
	protected int oRX2;
	protected int oRY1;
	protected int oRY2;
	protected int oRX1Player;
	protected int oRX2Player;
	protected int oRY1Player;
	protected int oRY2Player;
	private boolean hitPlayer;
	/**
	 * sets variables and stores some in control object array
	 * @param creator control object
	 * @param ORX x position
	 * @param ORY y position
	 * @param wallWidth width of wall
	 * @param wallHeight height of wall
	 * @param HitPlayer whether wall interacts with the player
	 * @param Tall whether or not the wall is tall enough to stop projectiles
	 */
	public Wall_Rectangle(Controller creator, int ORX, int ORY, int wallWidth, int wallHeight, boolean HitPlayer, boolean Tall)
	{
		tall = Tall;
		oRX1 = ORX;
		oRY1 = ORY;
		oRX2 = ORX+wallWidth;
		oRY2 = ORY+wallHeight;
		hitPlayer = HitPlayer;
		x = (oRX1 + oRX2) / 2;
		y = (oRY1 + oRY2) / 2;
		control = creator;
		oRX1Player = oRX1-humanWidth;
		oRX2Player = oRX2+humanWidth;
		oRY1Player = oRY1-humanWidth;
		oRY2Player = oRY2+humanWidth;
		oRX1 -= humanWidth;
		oRX2 += humanWidth;
		oRY1 -= humanWidth;
		oRY2 += humanWidth;
	}
	/**
	 * checks whether wall hits player or enemies
	 */
        @ Override
        protected void frameCall()
	{
        	if(hitPlayer)
        	{
				if(control.player.x > oRX1Player && control.player.x < oRX2Player && control.player.y > oRY1Player && control.player.y < oRY2Player)
				{
						double holdX;
						double holdY;
						if(control.player.x > x)
						{
							holdX = Math.abs(control.player.x - oRX2Player);
						} else
						{
							holdX = Math.abs(control.player.x - oRX1Player);
						}
						if(control.player.y > y)
						{
							holdY = Math.abs(control.player.y - oRY2Player);
						} else
						{
							holdY = Math.abs(control.player.y - oRY1Player);
						}
						if((holdX) < (holdY))
						{
							if(control.player.x > x)
							{
								control.player.x = oRX2Player;
							}
							else
							{
								control.player.x = oRX1Player;
							}
						} else
						{
							if(control.player.y > y)
							{
								control.player.y = oRY2Player;
							}
							else
							{
								control.player.y = oRY1Player;
							}
						}
				}
        	}
        	ArrayList<Enemy> enemies = control.spriteController.enemies;
		for(int i = 0; i < enemies.size(); i++)
		{
			if(enemies.get(i) != null)
			{
				if(enemies.get(i).x > oRX1Player && enemies.get(i).x < oRX2Player && enemies.get(i).y > oRY1Player && enemies.get(i).y < oRY2Player)
				{
					enemies.get(i).hitWall();
						double holdX;
						double holdY;
						if(enemies.get(i).x > x)
						{
							holdX = Math.abs(enemies.get(i).x - oRX2Player);
						} else
						{
							holdX = Math.abs(enemies.get(i).x - oRX1Player);
						}
						if(enemies.get(i).y > y)
						{
							holdY = Math.abs(enemies.get(i).y - oRY2Player);
						} else
						{
							holdY = Math.abs(enemies.get(i).y - oRY1Player);
						}
						while(enemies.get(i).rotation<0) enemies.get(i).rotation+=360;
						if((holdX) < (holdY))
						{
							if(enemies.get(i).x > x)
							{
								enemies.get(i).x = oRX2Player;
								if(enemies.get(i).rotation>90&&enemies.get(i).rotation<180)
								{
									enemies.get(i).rotation -=2;
								} else
								{
									enemies.get(i).rotation +=2;
								}
							}
							else
							{
								enemies.get(i).x = oRX1Player;
								if(enemies.get(i).rotation>0&&enemies.get(i).rotation<90)
								{
									enemies.get(i).rotation +=2;
								} else
								{
									enemies.get(i).rotation -=2;
								}
							}
						} else
						{
							if(enemies.get(i).y > y)
							{
								enemies.get(i).y = oRY2Player;
								if(enemies.get(i).rotation>180&&enemies.get(i).rotation<270)
								{
									enemies.get(i).rotation -=2;
								} else
								{
									enemies.get(i).rotation +=2;
								}
							}
							else
							{
								enemies.get(i).y = oRY1Player;
								if(enemies.get(i).rotation>0&&enemies.get(i).rotation<90)
								{
									enemies.get(i).rotation -=2;
								} else
								{
									enemies.get(i).rotation +=2;
								}
							}
						}
				}
			}
		}
	}
}