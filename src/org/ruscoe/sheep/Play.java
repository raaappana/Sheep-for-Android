package org.ruscoe.sheep;

import org.ruscoe.sheep.R;
import org.ruscoe.sheep.constants.GameSettings;
import org.ruscoe.sheep.dao.GamePrefsData;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

/**
 * The activity the user is presented with when they being playing the game.
 * 
 * Creates an instance of GameView
 * 
 * @author Dan Ruscoe (ruscoe.org)
 * @version 1.0
 */
public class Play extends Activity implements SensorEventListener
{
	// The GameView instance used by this activity.
	private GameView mGameView = null;
	
	// The current Context instance.
	private Context mContext = null;
	
	// The GamePrefsData instance containing the user's game settings and score data.
	private GamePrefsData mGamePrefsData = null;
	
	// The GameEnvironment instance containing information about the current device.
	private GameEnvironment mGameEnvironment = null;
	
	// The SensorManager instance used to interface with the device's accelerometer.
	private SensorManager mSensorManager = null;
	
	// The game mode, which defines the level of difficulty.
	private int mGameMode = GameView.GAME_MODE_NORMAL;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mGameEnvironment = new GameEnvironment();
		
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	    
	    mGameEnvironment.setDensity(displayMetrics.density);
		mGameEnvironment.setRotation(getWindowManager().getDefaultDisplay().getRotation());
		
		if (mGameEnvironment.getRotation() == 0)
		{
			mGameEnvironment.setDefaultOrientation(Configuration.ORIENTATION_PORTRAIT);
		}
	    
		mContext = getApplicationContext();
		
		mGameMode = getIntent().getExtras().getInt("gameMode");
		
		mGameView = new GameView(mContext, this, mGameEnvironment, mGameMode);
		
		setContentView(mGameView);
		
		mGamePrefsData = new GamePrefsData(this);
		
		Sound.setSoundEnabled(mGamePrefsData.isSoundEnabled());
		
		if (mGamePrefsData.isSoundEnabled())
		{
			Log.i(GameSettings.LOG_NAME, "Sound is enabled");
		}
		else
		{
			Log.i(GameSettings.LOG_NAME, "Sound is disabled");
		}
		
		Sound.loadSound(mContext);
		Sound.playMusic();
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		String soundMenuTitle = "";
		
		if (mGamePrefsData.isSoundEnabled())
		{
			soundMenuTitle = mContext.getString(R.string.sound_off_label);
		}
		else
		{
			soundMenuTitle = mContext.getString(R.string.sound_on_label);
		}
		
		MenuItem soundMenuItem = menu.findItem(R.id.menuSound);
		soundMenuItem.setTitle(soundMenuTitle);
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menuSound:
				boolean mSoundEnabled = mGamePrefsData.isSoundEnabled();
				
				// Update sound preferences.
    			if (mSoundEnabled)
    			{
    				Log.i(GameSettings.LOG_NAME, "Disabling sound.");
    				mGamePrefsData.setSoundEnabled(false);
    				Sound.pauseMusic();
    				Sound.setSoundEnabled(false);
    				mSoundEnabled = false;
    				item.setTitle(mContext.getString(R.string.sound_on_label));
    			}
    			else
    			{
    				Log.i(GameSettings.LOG_NAME, "Enabling sound.");
    				mGamePrefsData.setSoundEnabled(true);
    				Sound.setSoundEnabled(true);
    				Sound.playMusic();
    				mSoundEnabled = true;
    				item.setTitle(mContext.getString(R.string.sound_off_label));
    			}
				return true;
			case R.id.menuTitle:
				Intent i = new Intent(this, Main.class);
				startActivity(i);
				return true;
			case R.id.menuExplode:
				mGameView.getThread().explode();
				return true;
		}

		return false;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		Sound.release();
	}

	/**
	 * Invoked when the Activity loses user focus.
	 */
	@Override
	protected void onPause()
	{
		super.onPause();
		
		mSensorManager.unregisterListener(this);
		
		Sound.pauseMusic();
		mGameView.getThread().setState(GameView.STATE_PAUSE); // pause game when Activity pauses
	}
	
	@Override
	/**
	 * Invoked when the Activity regains focus.
	 */
	protected void onResume()
	{
		super.onResume();
		
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
		
		Sound.playMusic();
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1)
	{
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		synchronized(this)
		{
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			{
				// X / Y axis depend on the default orientation of the device.
				if (mGameEnvironment.getDefaultOrientation() == Configuration.ORIENTATION_PORTRAIT)
				{
					mGameView.setAccelX(event.values[0]);
				}
				else
				{
					mGameView.setAccelX(event.values[1]);
				}
			}
		}
	}
}