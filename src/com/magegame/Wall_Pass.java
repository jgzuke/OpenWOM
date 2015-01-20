/**
 * stores values for passes in control object array
 */
package com.magegame;

public class Wall_Pass extends Wall
{
	private int oRX1;
	private int oRX2;
	private int oRY1;
	private int oRY2;
	/**
	 * sets variables and stores some in control object array
	 * @param creator control object
	 * @param ORX x position
	 * @param ORY y position
	 * @param wallWidth width of wall
	 * @param wallHeight height of wall
	 */
	public Wall_Pass(Controller creator, int ORX, int ORY, int wallWidth, int wallHeight, boolean fullPass)
	{
		oRX1 = ORX;
		oRY1 = ORY;
		oRX2 = ORX+wallWidth;
		oRY2 = ORY+wallHeight;
	}
	@Override
	protected void frameCall() {
		// TODO Auto-generated method stub
		
	}
}