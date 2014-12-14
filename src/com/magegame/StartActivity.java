/**
 * all variables to store as well as some base level behaviors, pause and resume and start functions
 */
package com.magegame;
import android.view.ViewGroup;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.magegame.R;
import com.magegame.util.IabHelper;
import com.magegame.util.IabResult;
import com.magegame.util.Inventory;
import com.magegame.util.Purchase;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
public class StartActivity extends Activity
{
	protected Controller control;
	protected double screenDimensionMultiplier;
	protected int screenMinX;
	protected int screenMinY;
	protected int gameCurrency = 2000;
	protected int realCurrency = 2000;
	protected byte [] boosts = {0, 0, 0, 0, 0, 0}; // attack, heal
	protected byte [] upgrades = {0, 0, 0, 0}; // attack, hp, speed, cooldown
	protected byte [] premiumUpgrades = {0, 0, 0, 0}; // reserve, excess, replentish, tracking
	protected boolean [] skins = {false, false, false, false, false, false, false};
	protected byte currentSkin = 0;
	protected byte levelBeaten = 18;
	protected boolean gameRunning = false;
	protected boolean gameOnAtAll = false;
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
	private String TAG = "game";
	private String SKU_100 = "platinum_100";
	private String SKU_250 = "platinum_250";
	private String SKU_1000 = "platinum_1000";
	private String SKU_5000 = "platinum_5000";
	private int RC_REQUEST = 10001;
	IabHelper mHelper;
	private Context context;@
	Override
	/**
	 * sets screen and window variables and reads in data
	 * creates control object and sets up IAB
	 */
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setWindowAndAudio();
		boolean firstTime = readSavedData();
		context = this;
		startMusic();
		setContentView(R.layout.activity_main);
		setUpPlayStoreBuyingThings();
	}
	private boolean readSavedData()
	{
		read();
		if(savedData[0] == 0)
		{
			savedData[0] = 1;
			setSaveData();
			write();
			return true;
		}
		readSaveData();
		return false;
	}
	private void setUpPlayStoreBuyingThings()
	{
		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiGLP+ZTiqZSbr1GJ7dWrRTBeju8IFdqWFNfejT8fAFcnxptJUsrtqpNdkaJJpEIZbT8XkcGcI3kwOhEfepJDjARZ+k6JFsHc3xPqaT2ACyfctAeUfBIHJA1PWxwnsbfxQIg0fv9lbfJaO9E7KphhtqE51jqSKnnO013sGbqi4QoZL1Ov/6f0pOv2TRnpN7XNbr/EGlUa9AKkyxlWmlxhlJowb03Kwh8e0uUs+kJjRzy+aNdGsNZRDwforn2XLZd9du9CTJ9K65K9/sUVgn5Zkj4bVYK8Y1CkMdiPBJAgz/v9Zh6FVJTTCa1LQmwMI6rZfvCWs6LrcmItis+4U0M9cwIDAQAB";
		mHelper = new IabHelper(this, base64EncodedPublicKey);
		// enable debug logging (for a production application, you should set this to false).
		mHelper.enableDebugLogging(true);
		// TODO change this when ready to release
		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener()
		{
			public void onIabSetupFinished(IabResult result)
			{
				Log.d(TAG, "Setup finished.");
				if(!result.isSuccess())
				{
					// Oh noes, there was a problem.
					complain("Problem setting up in-app billing: " + result);
					return;
				}
				// Have we been disposed of in the meantime? If so, quit.
				if(mHelper == null) return;
				// IAB is fully set up. Now, let's get an inventory of stuff we own.
				Log.d(TAG, "Setup successful. Querying inventory.");
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	Button easyClick;
	Button medClick;
	Button hardClick;
	Button extClick;
	Button sickClick;
	Button hurtClick;
	Button limitedClick;
	Button regenerateClick;
	private int difficultyLevel = 10;
	private LayoutInflater layoutInflater;
	private int levelSelectedToPlay = 10;
	private String[] levelNames = new String[] {"    Tutorial", "    Level 1", "    Level 2"};
	ListView playLevelList;
	private boolean drainHp=false;
	private boolean lowerHp=false;
	private boolean limitSpells=false;
	private boolean enemyRegen=false;
	public void playClickHandler(View v)
	{
		setContentView(R.layout.play);
		easyClick = (Button) findViewById(R.id.easy);
		medClick = (Button) findViewById(R.id.med);
		hardClick = (Button) findViewById(R.id.hard);
		extClick = (Button) findViewById(R.id.ext);
		switch(difficultyLevel)
		{
			case 10: easyClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
				break;
			case 6: medClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
				break;
			case 3: hardClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
				break;
			case 0: extClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
				break;
		}
		levelSelectedToPlay=0;
		sickClick = (Button) findViewById(R.id.sick);
		hurtClick = (Button) findViewById(R.id.hurt);
		limitedClick = (Button) findViewById(R.id.limit);
		regenerateClick = (Button) findViewById(R.id.regen);
		if(drainHp) sickClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
		if(lowerHp) hurtClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
		if(limitSpells) limitedClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
		if(enemyRegen) regenerateClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
		playLevelList = (ListView) findViewById(R.id.scroll);
		playLevelList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, levelNames)
		{
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
			    TextView textView = (TextView) super.getView(position, convertView, parent);
			    textView.setTextColor(Color.parseColor("#FFFFFF"));
			    return textView;
			}
		});
		playLevelList.setOnItemClickListener(new OnItemClickListener()
		{
	         @Override
             public void onItemClick(AdapterView<?> parent, View view,int position, long id)
	         {
	        	 if(levelSelectedToPlay!=0) highlightSelectedLevel((levelSelectedToPlay/10)-1, false);
	        	 levelSelectedToPlay=(position+1)*10;
	        	 highlightSelectedLevel(position, true);
	         }
	    });
		playLevelList.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScroll(AbsListView view, int first, int visibleItemCount, int totalItemCount)
			{
				for(int i = 0; i < visibleItemCount; i++)
				{
					if(i==(levelSelectedToPlay/10)-1) playLevelList.getChildAt(i).setBackgroundColor(Color.parseColor("#33209af1"));
					else playLevelList.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
				}
			}
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
		});
	}
	public void highlightSelectedLevel(int position, boolean selected)
	{
		if(selected)
		{
			playLevelList.getChildAt(position).setBackgroundColor(Color.parseColor("#33209af1"));
		} else
		{
			if(playLevelList.getFirstVisiblePosition()<=position&& position<=playLevelList.getLastVisiblePosition())
			{
				playLevelList.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
			}
		}
	}
	public void startRetry()
	{
		setContentView(R.layout.lostfight);
		easyClick = (Button) findViewById(R.id.easy);
		medClick = (Button) findViewById(R.id.med);
		hardClick = (Button) findViewById(R.id.hard);
		extClick = (Button) findViewById(R.id.ext);
		switch(difficultyLevel)
		{
			case 10: easyClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
				break;
			case 6: medClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
				break;
			case 3: hardClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
				break;
			case 0: extClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
				break;
		}
		sickClick = (Button) findViewById(R.id.sick);
		hurtClick = (Button) findViewById(R.id.hurt);
		limitedClick = (Button) findViewById(R.id.limit);
		regenerateClick = (Button) findViewById(R.id.regen);
		if(drainHp) sickClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
		if(lowerHp) hurtClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
		if(limitSpells) limitedClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
		if(enemyRegen) regenerateClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
	}
	public void optionsClickHandler(View v)
	{
		//setContentView(R.layout.options);
	}
	TextView gameMoneyText;
	TextView realMoneyText;
	private String[] boostNames = new String[] {"    Heal", "    Cooldown", "    Attack Boost", "    Recharge Boost", "    Speed Boost", "    Armor Boost", "    100", "    1000", "    10000"};
	ListView boostList;
	private String[] upgradeNames = new String[] {"    Attack", "    HP", "    Speed", "    Cooldown", "    Reserve", "    Excess", "    Replentish", "    Tracking"};
	ListView upgradeList;
	private String[] skinNames = new String[] {"    1", "    2", "    3", "    4", "    5", "    6", "    7"};
	ListView skinList;
	public void storeClickHandler(View v)
	{
		setContentView(R.layout.store);
		boostList = (ListView) findViewById(R.id.scroll1);
		gameMoneyText = (TextView) findViewById(R.id.gameMoney);
		realMoneyText = (TextView) findViewById(R.id.realMoney);
		boostList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, boostNames)
		{
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
			    TextView textView = (TextView) super.getView(position, convertView, parent);
			    textView.setTextColor(Color.parseColor("#FFFFFF"));
			    return textView;
			}
		});
		upgradeList = (ListView) findViewById(R.id.scroll2);
		upgradeList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, upgradeNames)
		{
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
			    TextView textView = (TextView) super.getView(position, convertView, parent);
			    textView.setTextColor(Color.parseColor("#FFFFFF"));
			    return textView;
			}
		});
		skinList = (ListView) findViewById(R.id.scroll3);
		skinList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, skinNames)
		{
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
			    TextView textView = (TextView) super.getView(position, convertView, parent);
			    textView.setTextColor(Color.parseColor("#FFFFFF"));
			    return textView;
			}
		});
		boostList.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScroll(AbsListView view, int first, int visibleItemCount, int totalItemCount)
			{
				for(int i = 0; i < visibleItemCount; i++)
				{
					if(boostAffordable[i+first]) boostList.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
					else boostList.getChildAt(i).setBackgroundColor(Color.parseColor("#260c3b5d"));
					if(i+first<6)
					{
						if(boosts[i+first]>4) boostList.getChildAt(i).setBackgroundColor(Color.parseColor("#400c3b5d"));
					}
				}
			}
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
		});
		upgradeList.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScroll(AbsListView view, int first, int visibleItemCount, int totalItemCount)
			{
				for(int i = 0; i < visibleItemCount; i++)
				{
					if(upgradeAffordable[i+first]) upgradeList.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
					else upgradeList.getChildAt(i).setBackgroundColor(Color.parseColor("#260c3b5d"));
				}
			}
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
		});
		skinList.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScroll(AbsListView view, int first, int visibleItemCount, int totalItemCount)
			{
				for(int i = 0; i < visibleItemCount; i++)
				{
					if(skins[i+first]) skinList.getChildAt(i).setBackgroundColor(Color.parseColor("#400c3b5d"));
					else if(skinAffordable[i+first]) skinList.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
					else skinList.getChildAt(i).setBackgroundColor(Color.parseColor("#260c3b5d"));
				}
			}
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
		});
		boostList.setOnItemClickListener(new OnItemClickListener()
		{
	         @Override
             public void onItemClick(AdapterView<?> parent, View view,int position, long id)
	         {
	        	 clickBuyItem(position+1);
	         }
	    });
		upgradeList.setOnItemClickListener(new OnItemClickListener()
		{
	         @Override
             public void onItemClick(AdapterView<?> parent, View view,int position, long id)
	         {
	        	 Log.e("k", Integer.toString(position));
	        	 clickBuyItem(position+10);
	         }
	    });
		skinList.setOnItemClickListener(new OnItemClickListener()
		{
	         @Override
             public void onItemClick(AdapterView<?> parent, View view,int position, long id)
	         {
	        	 clickBuyItem(position+18);
	         }
	    });
		greyOutExpensive();
		refreshMoney();
	}
	private void refreshMoney()
	{
		gameMoneyText.setText(Integer.toString(gameCurrency));
		realMoneyText.setText(Integer.toString(realCurrency));
	}
	private boolean [] boostAffordable = new boolean[9];
	private boolean [] upgradeAffordable = new boolean[8];
	private boolean [] skinAffordable = new boolean[7];	
	private void greyOutExpensive()
	{
		for(int i = 0; i < 9; i++) boostAffordable[i]=afford(i+1);
		for(int i = 0; i < 8; i++) upgradeAffordable[i]=afford(i+10);
		for(int i = 0; i < 7; i++) skinAffordable[i]=afford(i+18);
	}
	private int getPrice(int ID)
	{
		double power = 3.4;
		if(ID<7) return 200;
		if(ID==7) return 100;
		if(ID==8) return 600;
		if(ID==9) return 2500;
		if(ID<14) return (int)(Math.pow(upgrades[ID-10], power)/2.49); 
		if(ID<18) return (int)(Math.pow(premiumUpgrades[ID-14], power)/2.49);
		if(ID==18) return 100;
		if(ID==19) return 200;
		if(ID==20) return 300;
		if(ID==21) return 100;
		if(ID==22) return 200;
		if(ID==23) return 300;
		return 400;
	}
	private boolean afford(int ID)
	{
		if(isRealCurrency(ID)&&getPrice(ID)>realCurrency) return false;
		if(!isRealCurrency(ID)&&getPrice(ID)>gameCurrency) return false;
		return true;
	}
	private boolean afford(int ID, int currency)
	{
		if(getPrice(ID)>currency) return false;
		return true;
	}
	private int buyItem(int ID, int currency)
	{
		if(!afford(ID, currency))
		{
			Toast.makeText(context, "Can't Afford", Toast.LENGTH_LONG).show();
		} else
		{
			getItem(ID);
			currency -= getPrice(ID);
		}
		return currency;
	}
	private boolean isRealCurrency(int ID)
	{
		if(ID>20||(ID>6&&ID<10)||(ID>13&&ID<18)) return true;
		return false;
	}
	private void clickBuyItem(int ID)
	{
		boolean real = isRealCurrency(ID);
		if(canGetItem(ID))
		{
			if(real) realCurrency = buyItem(ID, realCurrency);
			if(!real) gameCurrency = buyItem(ID, gameCurrency);
			greyOutExpensive();
			refreshMoney();
		}
	}
	private boolean canGetItem(int ID) 
	{
		if(ID<7)
		{
			if(boosts[ID-1]<6)return true;
			Toast.makeText(context, "Cannot hold more than five of one item", Toast.LENGTH_LONG).show();
			return false;
		}
		if(ID<18) return true;
		if(!skins[ID-18]) return true;
		return false;
	}
	private void getItem(int ID) 
	{
		if(ID<7) boosts[ID-1]++;
		if(ID==7) gameCurrency+=100;
		if(ID==8) gameCurrency+=1000;
		if(ID==9) gameCurrency+=1000;
		if(ID<14) upgrades[ID-10]++;
		if(ID<18) premiumUpgrades[ID-10]++;
		if(ID<24) skins[ID-18]=true;
	}
	public void toMenuClickHandler(View v)
	{
		setContentView(R.layout.activity_main);
	}
	public void playRoundClickHandler(View v)
	{
		if(levelSelectedToPlay!=0)
		{
			gameRunning = true;
			gameOnAtAll = true;
			control = new Controller(this, this, levelSelectedToPlay, difficultyLevel);
			setContentView(control);
		} else
		{
			Toast.makeText(context, "Choose a Level", Toast.LENGTH_LONG).show();
		}
	}
	public void difEasyClickHandler(View v)
	{
		difficultyLevel = 10;
		easyClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
		medClick.setBackgroundResource(R.drawable.menu_text150x40);
		hardClick.setBackgroundResource(R.drawable.menu_text150x40);
		extClick.setBackgroundResource(R.drawable.menu_text150x40);
	}
	public void difMedClickHandler(View v)
	{
		difficultyLevel = 6;
		easyClick.setBackgroundResource(R.drawable.menu_text150x40);
		medClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
		hardClick.setBackgroundResource(R.drawable.menu_text150x40);
		extClick.setBackgroundResource(R.drawable.menu_text150x40);
	}
	public void difHardClickHandler(View v)
	{
		difficultyLevel = 3;
		easyClick.setBackgroundResource(R.drawable.menu_text150x40);
		medClick.setBackgroundResource(R.drawable.menu_text150x40);
		hardClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
		extClick.setBackgroundResource(R.drawable.menu_text150x40);
	}
	public void difExtClickHandler(View v)
	{
		difficultyLevel = 0;
		easyClick.setBackgroundResource(R.drawable.menu_text150x40);
		medClick.setBackgroundResource(R.drawable.menu_text150x40);
		hardClick.setBackgroundResource(R.drawable.menu_text150x40);
		extClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
	}
	public void sickClickHandler(View v)
	{
		drainHp = !drainHp;
		if(drainHp)
		{
			sickClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
		} else
		{
			sickClick.setBackgroundResource(R.drawable.menu_text150x40);
		}
	}
	public void hurtClickHandler(View v)
	{
		lowerHp = !lowerHp;
		if(lowerHp)
		{
			hurtClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
		} else
		{
			hurtClick.setBackgroundResource(R.drawable.menu_text150x40);
		}
	}
	public void limitedClickHandler(View v)
	{
		limitSpells = !limitSpells;
		if(limitSpells)
		{
			limitedClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
		} else
		{
			limitedClick.setBackgroundResource(R.drawable.menu_text150x40);
		}
	}
	public void regenerateClickHandler(View v)
	{
		enemyRegen = !enemyRegen;
		if(enemyRegen)
		{
			regenerateClick.setBackgroundResource(R.drawable.menu_text_selected150x40);
		} else
		{
			regenerateClick.setBackgroundResource(R.drawable.menu_text150x40);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * sets screen variables as well as audio settings
	 */
	protected void setWindowAndAudio()
	{
		layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
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
		Log.e("game", "money");
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
	 * starts the menu screen by turning all negative effects to player off
	 */
	protected void startMenu()
	{
		gameRunning = false;
		gameOnAtAll=false;
		setContentView(R.layout.activity_main);
	}
	Button p1Use;
	Button p2Use;
	Button p3Use;
	Button p4Use;
	Button p5Use;
	Button p6Use;
	protected void pauseGame()
	{
		gameRunning = false;
		setContentView(R.layout.paused);
		p1Use = (Button) findViewById(R.id.u1);
		p2Use = (Button) findViewById(R.id.u2);
		p3Use = (Button) findViewById(R.id.u3);
		p4Use = (Button) findViewById(R.id.u4);
		p5Use = (Button) findViewById(R.id.u5);
		p6Use = (Button) findViewById(R.id.u6);
		p1Use.setText(Integer.toString(boosts[0]));
		p2Use.setText(Integer.toString(boosts[1]));
		p3Use.setText(Integer.toString(boosts[3]));
		p4Use.setText(Integer.toString(boosts[4]));
		p5Use.setText(Integer.toString(boosts[2]));
		p6Use.setText(Integer.toString(boosts[5]));
	}
	public void useP1(View v)
	{
		if(boosts[0]>0)
		{
			control.player.getPowerUp(1);
			boosts[0]--;
		}
		p1Use.setText(Integer.toString(boosts[0]));
	}
	public void useP2(View v)
	{
		if(boosts[1]>0)
		{
			control.player.getPowerUp(2);
			boosts[1]--;
		}
		p2Use.setText(Integer.toString(boosts[1]));
	}
	public void useP3(View v)
	{
		if(boosts[3]>0)
		{
			control.player.getPowerUp(4);
			boosts[3]--;
		}
		p3Use.setText(Integer.toString(boosts[3]));
	}
	public void useP4(View v)
	{
		if(boosts[4]>0)
		{
			control.player.getPowerUp(5);
			boosts[4]--;
		}
		p4Use.setText(Integer.toString(boosts[4]));
	}
	public void useP5(View v)
	{
		if(boosts[2]>0)
		{
			control.player.getPowerUp(3);
			boosts[2]--;
		}
		p5Use.setText(Integer.toString(boosts[2]));
	}
	public void useP6(View v)
	{
		if(boosts[5]>0)
		{
			control.player.getPowerUp(6);
			boosts[5]--;
		}
		p6Use.setText(Integer.toString(boosts[5]));
	}
	public void resumeGame(View v)
	{
		gameRunning = true;
		setContentView(control);
	}
	public void endGame(View v)
	{
		startMenu();
	}
	/**
	 * player loses a fight, start screen
	 */
	protected void loseFight()
	{
		control=null;
		gameRunning = false;
		gameOnAtAll=false;
		startRetry();
	}
	/**
	 * player wins a fight, increases level, starts next level
	 */
	protected void winFight()
	{
		if(control.levelNum < 180)
		{
			control.startingLevel =(int)(control.levelNum/10)-1;
			if(control.difficultyLevel == 10)
			{
				control.moneyMultiplier = 5;
			}
			if(control.difficultyLevel == 6)
			{
				control.moneyMultiplier = 7;
			}
			if(control.difficultyLevel == 3)
			{
				control.moneyMultiplier = 12;
			}
			if(control.difficultyLevel == 0)
			{
				control.moneyMultiplier = 20;
			}
			control.moneyMultiplier = (int)((double)control.moneyMultiplier*control.getLevelWinningsMultiplier(control.startingLevel));
			if(control.drainHp)
			{
				control.moneyMultiplier *= 1.4;
			}
			if(control.lowerHp)
			{
				control.moneyMultiplier *= 1.4;
			}
			if(control.limitSpells)
			{
				control.moneyMultiplier *= 1.4;
			}
			if(control.enemyRegen)
			{
				control.moneyMultiplier *= 1.4;
			}
			if((int)(control.levelNum / 10) - 2 == levelBeaten)
			{
				levelBeaten++;
				realCurrency += control.moneyMultiplier*3;
				Toast.makeText(control.context, "Level Unlocked, Earned "+Integer.toString((int)control.moneyMade)+"g and "+Integer.toString((int)control.moneyMultiplier*3)+"p", Toast.LENGTH_SHORT).show();
			} else
			{
				Toast.makeText(control.context, "Won Round, Earned "+Integer.toString((int)control.moneyMade)+"g", Toast.LENGTH_SHORT).show();
			}
			startFight(control.startingLevel+2);
		} else
		{
			startMenu();
		}
	}
	/**
	 * starts a level from menu
	 * @param levelSet level to start
	 */
	protected void startFight(int levelSet)
	{
		control.startFighting(levelSet * 10);
		if(levelSet == 2)
		{
			control.player.getPowerUp(2);
		}
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
	{
		savedData[1] = 1;
		savedData[2] = 0;
		savedData[3] = 1;
		savedData[29] = 0;
		savedData[4] = levelBeaten;
		savedData[5] = upgrades[0];
		savedData[6] = upgrades[1];
		savedData[7] = upgrades[2];
		savedData[8] = upgrades[3];
		savedData[9] = boosts[0];
		savedData[10] = boosts[1];
		savedData[11] = premiumUpgrades[0];
		savedData[12] = premiumUpgrades[1];
		savedData[13] = premiumUpgrades[2];
		savedData[14] = premiumUpgrades[3];
		savedData[24] = (byte)((int) volumeMusic);
		savedData[25] = (byte)((int) volumeEffect);
		String temp = correctDigits(Integer.toBinaryString(gameCurrency), 21);
		String tempLow = correctDigits(temp.substring(14), 8);
		String tempHigh = correctDigits(temp.substring(7, 14), 8);
		String tempHuge = correctDigits(temp.substring(0, 7), 8);
		byte low = (byte) Integer.parseInt(tempLow, 2);
		byte high = (byte) Integer.parseInt(tempHigh, 2);
		byte huge = (byte) Integer.parseInt(tempHuge, 2);
		savedData[19] = (byte)high;
		savedData[20] = (byte)low;
		savedData[27] = (byte)huge;
		temp = correctDigits(Integer.toBinaryString(realCurrency), 21);
		tempLow = correctDigits(temp.substring(14), 8);
		tempHigh = correctDigits(temp.substring(7, 14), 8);
		tempHuge = correctDigits(temp.substring(0, 7), 8);
		low = (byte) Integer.parseInt(tempLow, 2);
		high = (byte) Integer.parseInt(tempHigh, 2);
		huge = (byte) Integer.parseInt(tempHuge, 2);
		savedData[21] = (byte)high;
		savedData[22] = (byte)low;
		savedData[28] = (byte)huge;
		savedData[30] = 0;
		savedData[31] = 0;
		savedData[32] = 0;
		savedData[33] = 0;
		savedData[34] = 0;
		savedData[35] = 0;
		savedData[36] = 0;
		for(int i = 0; i < 7; i++)
		{
			if(skins[i]) savedData[i+30]=1;
		}
		savedData[37] = currentSkin;
		savedData[44] = 1;
	}
	/**
	 * read data once it has been put into savedData array
	 */
	public void readSaveData()
	{
		levelBeaten = savedData[4];
		upgrades[0] = savedData[5];
		upgrades[1] = savedData[6];
		upgrades[2] = savedData[7];
		upgrades[3] = savedData[8];
		boosts[0] = savedData[9];
		boosts[1] = savedData[10];
		premiumUpgrades[0] = savedData[11];
		premiumUpgrades[1] = savedData[12];
		premiumUpgrades[2] = savedData[13];
		premiumUpgrades[3] = savedData[14];
		gameCurrency = savedData[20] + (128 * savedData[19]) + (16384*savedData[27]);
		realCurrency = savedData[22] + (128 * savedData[21]) + (16384*savedData[28]);
		volumeMusic = savedData[24];
		volumeEffect = savedData[25];
		for(int i = 0; i < 7; i++)
		{
			skins[i]= savedData[i+30]==1;
		}
		currentSkin = savedData[37];
		savedData[44] = 1;
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
		gameRunning = true;
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
		//control.imageLibrary.recycleImages();
		gameRunning = false;
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// Listener that's called when we finish querying the items and subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener()
	{
		public void onQueryInventoryFinished(IabResult result, Inventory inventory)
		{
			Log.d(TAG, "Query inventory finished.");
			// Have we been disposed of in the meantime? If so, quit.
			if(mHelper == null) return;
			// Is it a failure?
			if(result.isFailure())
			{
				complain("Failed to query inventory: " + result);
				return;
			}
			Log.d(TAG, "Query inventory was successful.");
			// Check for gas delivery -- if we own gas, we should fill up the tank immediately
			Purchase c100Purchase = inventory.getPurchase(SKU_100);
			if(c100Purchase != null && verifyDeveloperPayload(c100Purchase))
			{
				Log.d(TAG, "We have gas. Consuming it.");
				mHelper.consumeAsync(inventory.getPurchase(SKU_100), mConsumeFinishedListener);
				return;
			}
			Purchase c250Purchase = inventory.getPurchase(SKU_250);
			if(c250Purchase != null && verifyDeveloperPayload(c250Purchase))
			{
				Log.d(TAG, "We have gas. Consuming it.");
				mHelper.consumeAsync(inventory.getPurchase(SKU_250), mConsumeFinishedListener);
				return;
			}
			Purchase c1000Purchase = inventory.getPurchase(SKU_1000);
			if(c1000Purchase != null && verifyDeveloperPayload(c1000Purchase))
			{
				Log.d(TAG, "We have gas. Consuming it.");
				mHelper.consumeAsync(inventory.getPurchase(SKU_1000), mConsumeFinishedListener);
				return;
			}
			Purchase c5000Purchase = inventory.getPurchase(SKU_5000);
			if(c5000Purchase != null && verifyDeveloperPayload(c5000Purchase))
			{
				Log.d(TAG, "We have gas. Consuming it.");
				mHelper.consumeAsync(inventory.getPurchase(SKU_5000), mConsumeFinishedListener);
				return;
			}
			Log.d(TAG, "Initial inventory query finished; enabling main UI.");
		}
	};
	// User clicked the "Buy Gas" button
	public void buyRealCurrency100()
	{
		Log.d(TAG, "Launching purchase flow for gas.");
		/* TODO: for security, generate your payload here for verification. See the comments on
		 *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
		 *        an empty string, but on a production app you should carefully generate this. */
		String payload = generatePayload();
		mHelper.launchPurchaseFlow(this, SKU_100, RC_REQUEST, mPurchaseFinishedListener, payload);
	}
	public void buyRealCurrency250()
	{
		Log.d(TAG, "Launching purchase flow for gas.");
		/* TODO: for security, generate your payload here for verification. See the comments on
		 *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
		 *        an empty string, but on a production app you should carefully generate this. */
		String payload = generatePayload();
		mHelper.launchPurchaseFlow(this, SKU_250, RC_REQUEST, mPurchaseFinishedListener, payload);
	}
	public void buyRealCurrency1000()
	{
		Log.d(TAG, "Launching purchase flow for gas.");
		/* TODO: for security, generate your payload here for verification. See the comments on
		 *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
		 *        an empty string, but on a production app you should carefully generate this. */
		String payload = generatePayload();
		mHelper.launchPurchaseFlow(this, SKU_1000, RC_REQUEST, mPurchaseFinishedListener, payload);
	}
	public void buyRealCurrency5000()
	{
		Log.d(TAG, "Launching purchase flow for gas.");
		/* TODO: for security, generate your payload here for verification. See the comments on
		 *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
		 *        an empty string, but on a production app you should carefully generate this. */
		String payload = generatePayload();
		mHelper.launchPurchaseFlow(this, SKU_5000, RC_REQUEST, mPurchaseFinishedListener, payload);
	}
	private String generatePayload()
	{
		return "";
	}
	@ Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
		if(mHelper == null) return;
		// Pass on the activity result to the helper for handling
		if(!mHelper.handleActivityResult(requestCode, resultCode, data))
		{
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		}
		else
		{
			Log.d(TAG, "onActivityResult handled by IABUtil.");
		}
	}
	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p)
	{
		//if(p.getDeveloperPayload().equals(""))return true;
		/*
		 * TODO: verify that the developer payload of the purchase is correct. It will be
		 * the same one that you sent when initiating the purchase.
		 *
		 * WARNING: Locally generating a random string when starting a purchase and
		 * verifying it here might seem like a good approach, but this will fail in the
		 * case where the user purchases an item on one device and then uses your app on
		 * a different device, because on the other device you will not have access to the
		 * random string you originally generated.
		 *
		 * So a good developer payload has these characteristics:
		 *
		 * 1. If two different users purchase an item, the payload is different between them,
		 *    so that one user's purchase can't be replayed to another user.
		 *
		 * 2. The payload must be such that you can verify it even when the app wasn't the
		 *    one who initiated the purchase flow (so that items purchased by the user on
		 *    one device work on other devices owned by the user).
		 *
		 * Using your own server to store and verify developer payloads across app
		 * installations is recommended.
		 */
		return true;
	}
	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener()
	{
		public void onIabPurchaseFinished(IabResult result, Purchase purchase)
		{
			Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
			// if we were disposed of in the meantime, quit.
			if(mHelper == null) return;
			if(result.isFailure())
			{
				complain("Error purchasing: " + result);
				return;
			}
			if(!verifyDeveloperPayload(purchase))
			{
				complain("Error purchasing. Authenticity verification failed.");
				return;
			}
			Log.d(TAG, "Purchase successful.");
			if(purchase.getSku().equals(SKU_100)||purchase.getSku().equals(SKU_250)||purchase.getSku().equals(SKU_1000)||purchase.getSku().equals(SKU_5000))
			{
				// bought 1/4 tank of gas. So consume it.
				Log.d(TAG, "Purchase is gas. Starting gas consumption.");
				mHelper.consumeAsync(purchase, mConsumeFinishedListener);
			}
		}
	};
	// Called when consumption is complete
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener()
	{
		public void onConsumeFinished(Purchase purchase, IabResult result)
		{
			Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
			// if we were disposed of in the meantime, quit.
			if(mHelper == null) return;
			// We know this is the "gas" sku because it's the only one we consume,
			// so we don't check which sku was consumed. If you have more than one
			// sku, you probably should check...
			if(result.isSuccess())
			{
				// successfully consumed, so we apply the effects of the item in our
				// game world's logic, which in our case means filling the gas tank a bit
				if(purchase.getSku().equals(SKU_100))
				{
					Log.d(TAG, "Consumption successful. Provisioning.");
					realCurrency += 100;
				}
				if(purchase.getSku().equals(SKU_250))
				{
					Log.d(TAG, "Consumption successful. Provisioning.");
					realCurrency += 250;
				}
				if(purchase.getSku().equals(SKU_1000))
				{
					Log.d(TAG, "Consumption successful. Provisioning.");
					realCurrency += 1000;
				}
				if(purchase.getSku().equals(SKU_5000))
				{
					Log.d(TAG, "Consumption successful. Provisioning.");
					realCurrency += 5000;
				}
			}
			else
			{
				complain("Error while consuming: " + result);
			}
			Log.d(TAG, "End consumption flow.");
		}
	};
	// We're being destroyed. It's important to dispose of the helper here!
	@
	Override
	public void onDestroy()
	{
		super.onDestroy();
		// very important:
		Log.d(TAG, "Destroying helper.");
		if(mHelper != null)
		{
			mHelper.dispose();
			mHelper = null;
		}
	}
	void complain(String message)
	{
		Log.e(TAG, "**** TrivialDrive Error: " + message);
		alert("Error: " + message);
	}
	void alert(String message)
	{
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		Log.d(TAG, "Showing alert dialog: " + message);
		bld.create().show();
	}
}