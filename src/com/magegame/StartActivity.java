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
public class StartActivity extends Activity
{
	private Controller control;
	private FileOutputStream fileWrite;
	private FileInputStream fileRead;
	private int savePoints = 120;
	protected byte[] savedData = new byte[savePoints];
	protected MediaPlayer backMusic;
	private SoundPool spool;
	private int[] soundPoolMap = new int[15];
	protected AudioManager audioManager;
	protected double volumeMusic = 127;
	protected double volumeEffect = 100;
	@Override
	/**
	 * sets screen and window variables and reads in data
	 * creates control object and sets up IAB
	 */
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setWindowAndAudio();
		readSavedData();
		startMusic();
		control = new Controller(this, this, getScreenDimensions());
		setContentView(control.graphicsController);
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
	 * sets screen variables as well as audio settings
	 */
	protected void setWindowAndAudio()
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		soundPoolMap[0] = spool.load(this, R.raw.shoot_burn, 1);
		soundPoolMap[1] = spool.load(this, R.raw.shoot_electric, 1);
		soundPoolMap[2] = spool.load(this, R.raw.shoot_water, 1);
		soundPoolMap[3] = spool.load(this, R.raw.shoot_earth, 1);
		soundPoolMap[4] = spool.load(this, R.raw.shoot_burst, 1);
		soundPoolMap[5] = spool.load(this, R.raw.shoot_shoot, 1);
		soundPoolMap[6] = spool.load(this, R.raw.shoot_teleport, 1);
		soundPoolMap[7] = spool.load(this, R.raw.enemy_sword1, 1);
		soundPoolMap[8] = spool.load(this, R.raw.enemy_sword2, 1);
		soundPoolMap[9] = spool.load(this, R.raw.enemy_swordmiss, 1);
		soundPoolMap[10] = spool.load(this, R.raw.enemy_arrowhit, 1);
		soundPoolMap[11] = spool.load(this, R.raw.enemy_arrowrelease, 1);
		soundPoolMap[12] = spool.load(this, R.raw.effect_pageflip, 1);
		soundPoolMap[13] = spool.load(this, R.raw.money_1, 1);
		soundPoolMap[14] = spool.load(this, R.raw.money_2, 1);
	}
	/**
	 * plays a random money effect
	 */
	protected void playMoney()
	{
		float newV = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		newV = (float)(newV * volumeEffect / 127);
		if(Math.random()>0.5)
		{
			spool.play(soundPoolMap[13], newV, newV, 1, 0, 1f);
		} else
		{
			spool.play(soundPoolMap[14], newV, newV, 1, 0, 1f);
		}
		//TODO
	}
	/**
	 * resets volume
	 */
	protected void resetVolume()
	{
		float systemVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		systemVolume = (float)(systemVolume * volumeMusic / 127);
		backMusic.setVolume(systemVolume, systemVolume);
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
	/**
	 * starts background music
	 */
	protected void startMusic()
	{
		stopMusic();
		backMusic = MediaPlayer.create((Context) this, R.raw.busqueda);
		backMusic.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
		{@
			Override
			public void onPrepared(MediaPlayer backMusic)
			{
				backMusic.start();
			}
		});
		backMusic.setLooping(true);
		float systemVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		systemVolume = (float)(systemVolume * volumeMusic / 127);
		backMusic.setVolume(systemVolume, systemVolume);
	}
	/**
	 * plays effect based on sent integer
	 * @param toPlay id of effect to play
	 */
	protected void playEffect(String toPlay)
	{
		float newV = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		newV = (float)(newV * volumeEffect / 127);
		if(toPlay.equals("burn")) spool.play(soundPoolMap[0], newV, newV, 1, 0, 1f);
		if(toPlay.equals("electric")) spool.play(soundPoolMap[1], newV, newV, 1, 0, 1f);
		if(toPlay.equals("water")) spool.play(soundPoolMap[2], newV, newV, 1, 0, 1f);
		if(toPlay.equals("earth")) spool.play(soundPoolMap[3], newV, newV, 1, 0, 1f);
		if(toPlay.equals("burst")) spool.play(soundPoolMap[4], newV, newV, 1, 0, 1f);
		if(toPlay.equals("shoot")) spool.play(soundPoolMap[5], newV, newV, 1, 0, 1f);
		if(toPlay.equals("powerup")) spool.play(soundPoolMap[6], newV, newV, 1, 0, 1f);
		if(toPlay.equals("sword1")) spool.play(soundPoolMap[7], newV, newV, 1, 0, 1f);
		if(toPlay.equals("sword2")) spool.play(soundPoolMap[8], newV, newV, 1, 0, 1f);
		if(toPlay.equals("swordmiss")) spool.play(soundPoolMap[9], newV, newV, 1, 0, 1f);
		if(toPlay.equals("arrowhit")) spool.play(soundPoolMap[10], newV, newV, 1, 0, 1f);
		if(toPlay.equals("arrowrelease")) spool.play(soundPoolMap[11], newV, newV, 1, 0, 1f);
		if(toPlay.equals("pageflip")) spool.play(soundPoolMap[12], newV, newV, 1, 0, 1f);
	}
	/**
	 * stops background music and releases it
	 */
	protected void stopMusic()
	{
		if(backMusic != null)
		{
			backMusic.stop();
			backMusic.release();
			backMusic = null;
		}
	}
	@ Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}
	/**
	 * adds 0's to the start of a string until it reaches a certain number of digits
	 * @param end starting string
	 * @param digits number or desired digits
	 * @return complete full length string
	 */
	protected String correctDigits(String end, int digits)
	{
		while(end.length() < digits)
		{
			end = "0" + end;
		}
		return end;
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
	@ Override
	public void onStart()
	{
		super.onStart();
		control.paused = false;
	}
	
	
	
	/**
	 * set data to write it to save file
	 */
	public void setSaveData()
	{ //TODO change some
		savedData[1] = (byte)((int) volumeMusic);		//1 volume music
		savedData[2] = (byte)((int) volumeEffect);		//2 volume effect
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
	/**
	 * read data once it has been put into savedData array
	 */
	public void readSaveData()
	{ //TODO change some
		volumeMusic = savedData[1];
		volumeEffect = savedData[2];
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
		startMusic();
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
		stopMusic();
		control.paused = true;
	}
	@ Override
	public void onStop()
	{
		control.paused = true;
		super.onStop();
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