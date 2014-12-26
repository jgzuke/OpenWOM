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
import android.view.Menu;
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
		readSavedData();
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
		double ratio;
		if(dimension1 > dimension2)
		{
			screenWidthstart = dimension1;
			screenHeightstart = dimension2;
		}
		else
		{
			screenWidthstart = dimension2;
			screenHeightstart = dimension1;
		}
		ratio = (double)(screenWidthstart / screenHeightstart);
		
		if(ratio > 1.5)
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
		control.paused = true;
	}
	@ Override
	public void onStop()
	{
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
		for(int i = 0; i < 12; i++)						//3-14 worships
		{
			savedData[i+3]=control.worships[i];
		}
		savedData[15]=0;
		for(int i = 0; i < 8; i++)						//15 skins as one byte
		{
			if(control.skins[i]) savedData[15]+=Math.pow(2, i);
		}
		savedData[16] = control.currentSkin;					//16 current skin
		for(int i = 0; i < 40; i++)						//15 skins as one byte
		{
			savedData[i+17]=control.itemControl.materials[i];
		}
		//savedData[58]=
	}
	private void readSavedData()
	{
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
	 * read data once it has been put into savedData array
	 */
	public void readSaveData()
	{ //TODO change some
		control.soundController.volumeMusic = savedData[1];
		control.soundController.volumeEffect = savedData[2];
		for(int i = 0; i < 12; i++)
		{
			control.worships[i]= savedData[i+3];
		}
		int temp = savedData[15];
		for(int i = 0; i <7; i++)
		{
			control.skins[i]=(temp%2==1);
			temp /=2;
		}
		control.currentSkin = savedData[16];
		for(int i = 0; i < 40; i++)						//15 skins as one byte
		{
			control.itemControl.materials[i]=savedData[i+17];
		}
		//savedData[58]=
	}
	/**
	 * reads data from file and sets variables accordingly
	 */
	private void read()
	{
		openRead();
		try
		{
			fileRead.read(savedData, 0, savePoints);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		closeRead();
	}
	/**
	 * saves data to file
	 */
	private void write()
	{
		openWrite();
		try
		{
			fileWrite.write(savedData, 0, savePoints);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		closeWrite();
	}
	/**
	 * opens the save file to be read from
	 */
	private void openRead()
	{
		try
		{
			fileRead = openFileInput("ProjectSaveData");
		}
		catch(FileNotFoundException e)
		{
			openWrite();
			closeWrite();
			openRead();
		}
	}
	/**
	 * opens the save file to be written to
	 */
	private void openWrite()
	{
		try
		{
			fileWrite = openFileOutput("ProjectSaveData", Context.MODE_PRIVATE);
		}
		catch(FileNotFoundException e)
		{
		}
	}
	/**
	 * closes the save file from reading
	 */
	private void closeRead()
	{
		try
		{
			fileRead.close();
		}
		catch(IOException e)
		{
		}
	}
	/**
	 * closes the save file from writing
	 */
	private void closeWrite()
	{
		try
		{
			fileWrite.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}