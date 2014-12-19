/**
 * all variables to store as well as some base level behaviors, pause and resume and start functions
 */
package com.magegame;
public class ItemController
{
	protected byte materials[] = new byte[40]; // 2 are type, amount, put useables in here too
	//CAP MATERIALS BY STACKS AND TOTAL WEIGHT
	
	//FORMAT FOR RECIPEES each recipe{{{matType, matAmount}, {A, T}, {A, T}}, {{endType, endAmount}, {A, T}, {A, T}}};
	private byte[][][][] recipees = {
		{{{0, 0}, {0, 0}, {0, 0}}, {{0, 0}, {0, 0}, {0, 0}}},
		{{{0, 0}, {0, 0}, {0, 0}}, {{0, 0}, {0, 0}, {0, 0}}},
		{{{0, 0}, {0, 0}, {0, 0}}, {{0, 0}, {0, 0}, {0, 0}}}
		};
	public ItemController()
	{
	}
	protected void useItem(String s)
	{
		materials[typeOf(s)]--;
	}
	protected void getItem(String s)
	{
		materials[typeOf(s)]++;
	}
	protected void useItem(int index)
	{
		materials[index]--;
	}
	protected void getItem(int index)
	{
		materials[index]++;
	}
	protected boolean canCraft(int recipeIndex)
	{
		byte [][] need = recipees[recipeIndex][0];
		for(int i = 0; i < 3; i ++)
		{
			if(materials[need[i][0]]<need[i][1]) return false; // if we dont have enough of any item
		}
		return true;
	}
	protected void craft(int recipeIndex)
	{
		byte [][] need = recipees[recipeIndex][0];
		byte [][] result = recipees[recipeIndex][0];
		if(canCraft(recipeIndex))
		{
			for(int i = 0; i < 3; i ++)
			{
				materials[need[i][0]] -= need[i][1];		// remove used mats
				materials[result[i][0]] += result[i][1];	// reap rewards
			}
		}
	}
	private String describe(String s)
	{
		return describe(typeOf(s));
	}
	private String describe(int i)
	{
		switch(i)
		{
			case 0: return "";
			case 1: return "";
			case 2: return "";
			case 3: return "";
			case 4: return "";
			case 5: return "";
			case 6: return "";
			case 7: return "";
			case 8: return "";
			case 9: return "";
			case 10: return "";
			case 11: return "";
			case 12: return "";
			case 13: return "";
			case 14: return "";
			case 15: return "";
			case 16: return "";
			case 17: return "";
			case 18: return "";
			case 19: return "";
			default: return "";
		}
	}
	private int weigh(String s)
	{
		return weigh(typeOf(s));
	}
	private int weigh(int i)
	{
		switch(i)
		{
			case 0: return 1;
			case 1: return 1;
			case 2: return 1;
			case 3: return 1;
			case 4: return 1;
			case 5: return 1;
			case 6: return 1;
			case 7: return 1;
			case 8: return 1;
			case 9: return 1;
			case 10: return 1;
			case 11: return 1;
			case 12: return 1;
			case 13: return 1;
			case 14: return 1;
			case 15: return 1;
			case 16: return 1;
			case 17: return 1;
			case 18: return 1;
			case 19: return 1;
			default: return 0;
		}
	}
	private String nameOf(int i)
	{
		switch(i)
		{
			case 0: return "";
			case 1: return "";
			case 2: return "";
			case 3: return "";
			case 4: return "";
			case 5: return "";
			case 6: return "";
			case 7: return "";
			case 8: return "";
			case 9: return "";
			case 10: return "";
			case 11: return "";
			case 12: return "";
			case 13: return "";
			case 14: return "";
			case 15: return "";
			case 16: return "";
			case 17: return "";
			case 18: return "";
			case 19: return "";
			default: return "";
		}
	}
	private int typeOf(String s)
	{
		if(s.equals("rock")) return 0;
		else if(s.equals("rock")) return 1;
		else if(s.equals("rock")) return 2;
		else if(s.equals("rock")) return 3;
		else if(s.equals("rock")) return 4;
		else if(s.equals("rock")) return 5;
		else if(s.equals("rock")) return 6;
		else if(s.equals("rock")) return 7;
		else if(s.equals("rock")) return 8;
		else if(s.equals("rock")) return 9;
		else if(s.equals("rock")) return 10;
		else if(s.equals("rock")) return 11;
		else if(s.equals("rock")) return 12;
		else if(s.equals("rock")) return 13;
		else if(s.equals("rock")) return 14;
		else if(s.equals("rock")) return 15;
		else if(s.equals("rock")) return 16;
		else if(s.equals("rock")) return 17;
		else if(s.equals("rock")) return 18;
		else if(s.equals("rock")) return 19;
		return 0;
	}
}