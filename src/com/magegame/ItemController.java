/**
 * all variables to store as well as some base level behaviors, pause and resume and start functions
 */
package com.magegame;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import com.magegame.R;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
public class ItemController extends Activity
{
	protected byte [] items = {0, 0, 0, 0, 0, 0}; // attack, heal
	protected byte materials[][] = new byte[20][2]; // 2 are type, amount
	public ItemController()
	{
	}
	private String matDescribe(String s)
	{
		return matDescribe(matTypeInt(s));
	}
	private String matDescribe(int i)
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
	private String matTypeString(int i)
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
	private int matTypeInt(String s)
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