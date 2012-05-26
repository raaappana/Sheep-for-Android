package org.ruscoe.sheep;

import org.ruscoe.sheep.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * Handles sound used in the game.
 * 
 * @author Dan Ruscoe (ruscoe.org)
 * @version 1.0
 */
public class Sound
{
	// A collection of sounds used in the game.
	private static SoundPool mSounds;
	// The button click sound.
	private static int mButtonClick;
	// The sheep impact pop sound.
	private static int mPop;
	// The sheep impact bounce sound.
	private static int mBounce;
	
	// The game background music.
	private static MediaPlayer mMusic;
	
	// The sound enabled status.
	private static boolean mSoundEnabled = true;

	// The active game Context.
	private static Context mContext = null;
	
	public static void loadSound(Context context)
	{
		mContext = context;
		
		// Create a new sound pool.
		mSounds = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		
		// Add individual sounds to the sound pool.
		mButtonClick = mSounds.load(context, R.raw.button_click, 1);
		mBounce = mSounds.load(context, R.raw.bounce, 1);
		mPop = mSounds.load(context, R.raw.pop, 1);
		
		// Music is handled differently to regular game sounds and cannot
		// be included in the sound pool.
		createMusicPlayer();
	}

	/**
	 * Plays the button click sound.
	 */
	public static void playButtonClick()
	{
		if (mSoundEnabled)
		{
			mSounds.play(mButtonClick, 1, 1, 1, 0, 1);
		}
	}
	
	/**
	 * Plays the sheep impact pop sound.
	 */
	public static void playPop()
	{
		if (mSoundEnabled)
		{
			mSounds.play(mPop, 1, 1, 1, 0, 1);
		}
	}
	
	/**
	 * Plays the sheep impact bounce sound.
	 */
	public static void playBounce()
	{
		if (mSoundEnabled)
		{
			mSounds.play(mBounce, 1, 1, 1, 0, 1);
		}
	}
	
	/**
	 * Plays the game background music.
	 */
	public static final void playMusic()
	{
		try
		{
			if (mSoundEnabled && !mMusic.isPlaying())
			{
				mMusic.seekTo(0);
				mMusic.start();
			}
		}
		catch (IllegalStateException e)
		{
			createMusicPlayer();
			if (mMusic != null)
			{
				mMusic.seekTo(0);
				mMusic.start();
			}
		}
	}

	/**
	 * Pauses the game background music.
	 */
	public static final void pauseMusic()
	{
		try
		{
			if (mSoundEnabled && mMusic.isPlaying())
			{
				mMusic.pause();
			}
		}
		catch (IllegalStateException e)
		{
			createMusicPlayer();
		}
	}

	/**
	 * Stops the game background music.
	 */
	public static final void stopMusic()
	{
		try
		{
			if (mSoundEnabled && mMusic.isPlaying())
			{
				mMusic.stop();
			}
		}
		catch (IllegalStateException e)
		{
			createMusicPlayer();
		}
	}
	
	/**
	 * Creates a MediaPlayer instance to handle game background music.
	 * Enables looping music.
	 */
	private static final void createMusicPlayer()
	{
		mMusic = MediaPlayer.create(mContext, R.raw.music);
		mMusic.setLooping(true);
	}
	
	/**
	 * Cleanly disables sound and stops music playing.
	 * Should be called in onDestroy method of the active activity.
	 */
	public static final void release()
	{
		if (mSoundEnabled)
		{
			mSounds.release();
		}
		
		if (mMusic != null)
		{
			if (mMusic.isPlaying())
			{
				mMusic.stop();
				mMusic.release();
			}
		}
	}
	
	/**
	 * Sets the sound enabled status.
	 * 
	 * @param boolean enabled - True to enable sound.
	 */
	public static final void setSoundEnabled(boolean enabled)
	{
		mSoundEnabled = enabled;
	}
}
