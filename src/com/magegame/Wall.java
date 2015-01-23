/**
 * variables for wall objects
 */
package com.magegame;
abstract public class Wall
{
	protected Controller control;
	protected int playerRollWidth = 5;
	protected int humanWidth = 10;
	protected boolean tall;
	/**
	 * what happens every frame for walls interacting with players and enemies
	 */
	abstract protected void frameCall();
}