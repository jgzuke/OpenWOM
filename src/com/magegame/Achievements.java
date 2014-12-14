/**
 * Loads, stores and resizes all graphics
 */
package com.magegame;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
public final class Achievements
{
	protected String BABY_STEPS = "CgkI18iqldACEAIQAQ";
	protected String DIVE_RIGHT_IN = "CgkI18iqldACEAIQAg";
	protected String STEADY_PROGRESS = "CgkI18iqldACEAIQAw";
	protected String KEEP_IT_UP = "CgkI18iqldACEAIQBA";
	protected String DEVOTED = "CgkI18iqldACEAIQBQ";
	protected String TRUE_SHOPPER = "CgkI18iqldACEAIQBg";
	protected String SHAPE_CHANGER = "CgkI18iqldACEAIQBw";
	protected String TEST_THE_WATERS = "CgkI18iqldACEAIQCA";
	protected String SLOW_AND_STEADY = "CgkI18iqldACEAIQCQ";
	protected StartActivity activity;
	protected Context context;
	/**
	 * loads in images and optimizes settings for loading
	 * @param contextSet start activity for getting resources etc
	 * @param controlSet control object
	 */
	public Achievements(Context startSet, StartActivity activitySet)
	{		
		activity = activitySet;
		context = startSet;
	}
}