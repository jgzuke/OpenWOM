/**
 * all variables to store as well as some base level behaviors, pause and resume and start functions
 */
package com.magegame;
public class ItemController
{
	//iron, bronze, steel, pine, ash, mahogony, cloth, wool, silk, water
	protected byte materials[] = new byte[12]; // 2 are type, amount, put useables in here too
	protected byte staff = 0;
	protected byte helm = 0;
	protected byte shirt = 0;
	protected double favor = 0;
	public ItemController()
	{
	}
	protected void get(int index, int i)
	{
		materials[index]+=i;
	}
	protected boolean canCraftShirt()
	{
		return canCraft(shirt, 8);
	}
	protected boolean canCraftStaff()
	{
		return canCraft(staff, 4);
	}
	protected boolean canCraftHelm()
	{
		return canCraft(helm, 0);
	}
	protected boolean canCraft(byte level, int arrayPosition)
	{
		int type = (level/2);	// 0:cloth, 1:woolen, 2:silk, 3:posiedons
		if(materials[arrayPosition+type]<type*4+2) return false;
		if(type==3 && materials[arrayPosition+3]<8) return false;
		if(favor<Math.pow(type+1, 3)) return false;
		return true;
	}
	protected void craftShirt()
	{
		shirt = craft(shirt, 8);
	}
	protected void craftStaff()
	{
		staff = craft(staff, 4);
	}
	protected void craftHelm()
	{
		helm = craft(helm, 0);
	}
	protected byte craft(byte level, int arrayPosition)
	{
		if(canCraft(level, arrayPosition))
		{
			int type = (level/2);	// 0:cloth, 1:woolen, 2:silk, 3:posiedons
			materials[arrayPosition+type]-=type*4+2;
			if(type==3) materials[arrayPosition+3]-=8;
			favor-=Math.pow(type+1, 3);
			return (byte)((type*2)+2);
		}
		return level;
	}
}