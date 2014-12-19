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
	protected Controller control;
	protected double screenDimensionMultiplier;
	protected int screenMinX;
	protected int screenMinY;
	protected byte [] items = {0, 0, 0, 0, 0, 0}; // attack, heal
	protected byte [] worships = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // all the olympians
	protected boolean [] skins = {false, false, false, false, false, false, false, false}; //skins
	protected byte currentSkin = 0;
	protected byte materials[][] = new byte[20][2]; // 2 are type, amount
	private FileOutputStream fileWrite;
	private FileInputStream fileRead;
	private int savePoints = 50;
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
		control = new Controller(this, this);
		setContentView(control);
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
	public void optionsClickHandler(View v)
	{
		//setContentView(R.layout.options);
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
		setScreenDimensions();
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
	protected void setScreenDimensions()
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
			screenMinX = (int)(screenWidthstart - (screenHeightstart * 1.5)) / 2;
			screenMinY = 0;
			screenDimensionMultiplier = ((screenHeightstart * 1.5) / 480);
		}
		else
		{
			screenMinY = (int)(screenHeightstart - (screenWidthstart / 1.5)) / 2;
			screenMinX = 0;
			screenDimensionMultiplier = ((screenWidthstart / 1.5) / 320);
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
	/**
	 * returns item description
	 * @param toBuy item to get description for
	 * @return description
	 */
	protected String[] getItemDescribe(String toBuy)
	{
		String[] describe = new String[2];
		if(toBuy.equals("Worship Apollo"))
		{
			describe[0] = "Damage modifier increased while";
			describe[1] = "fighting under Apollo";
		}
		else if(toBuy.equals("Worship Posiedon"))
		{
			describe[0] = "Attack cooldown speed increased";
			describe[1] = "while fighting under Posiedon";
		}
		else if(toBuy.equals("Worship Zues"))
		{
			describe[0] = "Movement speed and roll cooldown";
			describe[1] = "increased while fighting under Zues";
		}
		else if(toBuy.equals("Worship Hades"))
		{
			describe[0] = "Damage reduction increased while";
			describe[1] = "fighting under Hades";
		}
		else if(toBuy.equals("Worship Hephaestus"))
		{
			describe[0] = "Increases health during battles";
			describe[1] = "";
		}
		else if(toBuy.equals("Worship Ares"))
		{
			describe[0] = "Increases damage dealt during";
			describe[1] = "battles";
		}
		else if(toBuy.equals("Worship Athena"))
		{
			describe[0] = "Decreases cooldown time for all";
			describe[1] = "attacks or spells during battles";
		}
		else if(toBuy.equals("Worship Hermes"))
		{
			describe[0] = "Increases movement speed and";
			describe[1] = "decreases roll cooldown time";
		}
		else if(toBuy.equals("Ambrosia"))
		{
			describe[0] = "Heals 2000 of players Hp, ";
			describe[1] = "stopping at full";
		}
		else if(toBuy.equals("Cooldown"))
		{
			describe[0] = "Teleport, burst and roll";
			describe[1] = "cooldowns set to full";
		}
		else if(toBuy.equals("Posiedon's Shell"))
		{
			describe[0] = "Fight under Posiedon for a short";
			describe[1] = "length of time, reducing cooldowns";
		}
		else if(toBuy.equals("Hades' Helm"))
		{
			describe[0] = "Fight under Hades for a short";
			describe[1] = "length of time, increasing armor";
		}
		else if(toBuy.equals("Zues's Armor"))
		{
			describe[0] = "Fight under Zues for a short length";
			describe[1] = "of time, increasing movement speed";
		}
		else if(toBuy.equals("Apollo's Flame"))
		{
			describe[0] = "Fight under Apollo for a short";
			describe[1] = "length of time, increasing damage";
		}
		else if(toBuy.equals("Worship Hera"))
		{
			describe[0] = "Increases rate of drop of";
			describe[1] = "blessings during battles";
		}
		else if(toBuy.equals("1000g"))
		{
			describe[0] = "One thousand gold to spend";
			describe[1] = "on upgrades etc.";
		}
		else if(toBuy.equals("8000g"))
		{
			describe[0] = "Eight thousand gold to";
			describe[1] = "spend on upgrades etc.";
		}
		else if(toBuy.equals("40000g"))
		{
			describe[0] = "Forty thousand gold";
			describe[1] = "to spendon upgrades etc.";
		}
		else if(toBuy.equals("Iron Golem"))
		{
			describe[0] = "Temporarily transforms player";
			describe[1] = "into a large iron golem";
		}
		else if(toBuy.equals("Gold Golem"))
		{
			describe[0] = "Temporarily transforms player";
			describe[1] = "into a large golden golem";
		}
		else if(toBuy.equals("Reserve"))
		{
			describe[0] = "Increases the maximum number of";
			describe[1] = "shots the player can store";
		}
		else if(toBuy.equals("Excess"))
		{
			describe[0] = "Increases profit from completing";
			describe[1] = "levels or killing enemies";
		}
		else if(toBuy.equals("Replentish"))
		{
			describe[0] = "Damaging enemies lowers cooldowns";
			describe[1] = "for spells etc.";
		}
		else if(toBuy.equals("Trailing"))
		{
			describe[0] = "Players shots trail enemies better,";
			describe[1] = "making them more likely to hit";
		}
		return describe;
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
			savedData[i+3]=worships[i];
		}
		savedData[15]=0;
		for(int i = 0; i < 8; i++)						//15 skins as one byte
		{
			if(skins[i]) savedData[15]+=Math.pow(2, i);
		}
		savedData[16] = currentSkin;					//16 current skin
		for(int i = 0; i < 20; i++)						//15 skins as one byte
		{
			savedData[i+17]=materials[i][0];
			savedData[i+37]=materials[i][1];
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
			worships[i]= savedData[i+3];
		}
		int temp = savedData[15];
		for(int i = 0; i <7; i++)
		{
			skins[i]=(temp%2==1);
			temp /=2;
		}
		currentSkin = savedData[16];
		for(int i = 0; i < 20; i++)						//15 skins as one byte
		{
			materials[i][0]=savedData[i+17];
			materials[i][1]=savedData[i+37];
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
	}
	@ Override
	public void onStop()
	{
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