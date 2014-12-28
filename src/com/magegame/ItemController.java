/**
 * all variables to store as well as some base level behaviors, pause and resume and start functions
 */
package com.magegame;
public class ItemController
{
	protected byte materials[] = new byte[40]; // 2 are type, amount, put useables in here too
	protected byte staff = 0;
	protected byte helm = 0;
	protected byte shirt = 0;
	//CAP MATERIALS BY STACKS AND TOTAL WEIGHT
	
	//FORMAT FOR RECIPEES each recipe{mattype, amount, favor, level};
	private byte[][] staffs = {
		{0, 0, 0},
		{0, 0, 0},
		{0, 0, 0}
		};
	private byte[][] helms = {
			{0, 0, 0},
			{0, 0, 0},
			{0, 0, 0}
			};
	private byte[][] shirts = {
			{0, 0, 0},
			{0, 0, 0},
			{0, 0, 0}
			};
	protected int maxWeight = 300;
	public ItemController()
	{
	}
	protected void use(int index, int i)
	{
		if(has(index, i)) materials[index]-=i;
	}
	protected void get(int index, int i)
	{
		materials[index]+=i;
	}
	protected boolean has(int index, int i)
	{
		return materials[index]>=i;
	}
}