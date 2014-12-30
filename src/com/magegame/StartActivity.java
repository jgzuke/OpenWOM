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
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
public class StartActivity extends Activity
{
	private Controller control;
	private FileOutputStream fileWrite;
	private FileInputStream fileRead;
	private int savePoints = 120;
	protected byte[] savedData = new byte[savePoints];
	@Override
	/**
	 * sets screen and window variables and reads in data
	 * creates control object and sets up IAB
	 */
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setWindow();
		control = new Controller(this, this, getScreenDimensions());
		setContentView(control.graphicsController);
		read();
		if(savedData[0] == 0)
		{
			savedData[0] = 1;
			setSaveData();
			write();
		}
		readSaveData();
	}
	/**
	 * sets screen variables as well as audio settings
	 */
	protected void setWindow()
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	/**
	 * sets dimensions of screen
	 */
	protected double[] getScreenDimensions()
	{
		int dimension1 = getResources().getDisplayMetrics().heightPixels;
		int dimension2 = getResources().getDisplayMetrics().widthPixels;
		double screenWidthstart;
		double screenHeightstart;
		if(dimension1 > dimension2)
		{
			screenWidthstart = dimension1;
			screenHeightstart = dimension2;
		} else
		{
			screenWidthstart = dimension2;
			screenHeightstart = dimension1;
		}
		if(screenWidthstart/screenHeightstart > 1.5)
		{
			double[] dims = {(screenWidthstart - (screenHeightstart * 1.5)) / 2, 0, ((screenHeightstart * 1.5) / 480)};
			return dims;
		} else
		{
			double[] dims = {0, (screenHeightstart - (screenWidthstart / 1.5)) / 2, ((screenWidthstart / 1.5) / 320)};
			return dims;
		}
	}
	@ Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}
	@ Override
	public void onStart()
	{
		super.onStart();
		control.paused = false;
	}
	/**
	 * reads saved data
	 * starts music, loads images
	 */
	@ Override
	public void onResume()
	{
		super.onResume();
		read();
		if(savedData[0] == 1)
		{
			readSaveData();
		}
		control.soundController.startMusic();
		control.paused = false;
	}
	/**
	 * stops music, stops timer, saves data
	 */
	@ Override
	public void onPause()
	{
		super.onPause();
		setSaveData();
		write();
		control.soundController.stopMusic();
		Log.e("mine", "onPau");
		control.paused = true;
	}
	@ Override
	public void onStop()
	{
		Log.e("mine", "onStop");
		control.paused = true;
		super.onStop();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * saves all required data 
	 */
	protected void saveGame()
	{
		setSaveData();
		write();
		readSaveData();
	}
	/**
	 * set data to write it to save file
	 */
	public void setSaveData()
	{ //TODO change some
		savedData[1] = (byte)((int) control.soundController.volumeMusic);		//1 volume music
		savedData[2] = (byte)((int) control.soundController.volumeEffect);		//2 volume effect
		savedData[15]=control.itemControl.helm;
		savedData[16]=control.itemControl.shirt;
		savedData[17]=control.itemControl.staff;
		savedData[18]=(byte)control.itemControl.favor;
		//savedData[58]=
	}
	/**
	 * read data once it has been put into savedData array
	 */
	public void readSaveData()
	{ //TODO change some
		control.soundController.volumeMusic = savedData[1];
		control.soundController.volumeEffect = savedData[2];
		control.itemControl.helm = savedData[15];
		control.itemControl.shirt = savedData[16];
		control.itemControl.staff = savedData[17];
		control.itemControl.favor = savedData[18];
		//savedData[58]=
	}
	/**
	 * reads data from file and sets variables accordingly
	 */
	private void read()
	{
		try
		{
			fileRead = openFileInput("ProjectSaveData");
		}
		catch(FileNotFoundException e){}
		try
		{
			fileRead.read(savedData, 0, savePoints);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		try
		{
			fileRead.close();
		}
		catch(IOException e){}
	}
	/**
	 * saves data to file
	 */
	private void write()
	{
		try
		{
			fileWrite = openFileOutput("ProjectSaveData", Context.MODE_PRIVATE);
		}
		catch(FileNotFoundException e){}
		try
		{
			fileWrite.write(savedData, 0, savePoints);
		}
		catch(IOException e){}
		try
		{
			fileWrite.close();
		}
		catch(IOException e){}
	}
	
	
	//					PAUSE SCREEN STUFF
	
	
	
	

	/**
	 * starts pause activity thing
	 */
	protected void pause()
	{
		setContentView(R.layout.paused);
	}
	/**
	 * Unpauses game
	 */
	public void unPause(View v)
	{
		setContentView(control.graphicsController);
		control.paused = false;
		control.frameCaller.run();
	}
	
	/**
	 * Blessing game
	 */
	public void requestAttack(View v)
	{
		if(canBless()) blessed(1);
	}
	/**
	 * Blessing game
	 */
	public void requestArmor(View v)
	{
		if(canBless()) blessed(2);
	}
	/**
	 * Blessing game
	 */
	public void requestSpeed(View v)
	{
		if(canBless()) blessed(3);
	}
	/**
	 * Blessing game
	 */
	public void requestCooldown(View v)
	{
		if(canBless()) blessed(4);
	}
	/**
	 * starts a blessing
	 */
	private void blessed(int i)
	{
		control.player.blessing = i;
		control.spriteController.playerBlessing = control.imageLibrary.loadImage("effect000"+Integer.toString(i), 80, 80);
		control.player.blessingTimer = 300;
		control.itemControl.favor -= 50;
		control.player.setAttributes();
	}
	/**
	 * checks whether you can request a blessing
	 */
	private boolean canBless()
	{
		return true;//control.itemControl.favor>50;
	}
}