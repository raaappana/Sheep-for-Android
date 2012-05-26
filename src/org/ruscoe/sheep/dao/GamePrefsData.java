package org.ruscoe.sheep.dao;

import static android.provider.BaseColumns._ID;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Extends the game Data Access Object to provide access to game
 * preferences data.
 * 
 * Contains the user's sound preference and high score values for
 * each game mode.
 * 
 * @author Dan Ruscoe (ruscoe.org)
 * @version 1.0
 */
public class GamePrefsData extends GameDAO
{
	public static final String TABLE_NAME = "gamePrefsData";
	
	public static final String SOUND = "sound";
	public static final String SCORE_EASY = "scoreEasy";
	public static final String SCORE_NORMAL = "scoreNormal";
	public static final String SCORE_UNFAIR = "scoreUnfair";
	
	// Only ever one row in prefs table.
	private static final int ROW_ID = 1;
	
	public GamePrefsData(Context ctx)
	{
		super(ctx);
	}
	
	/**
	 * Determines if sound is enabled.
	 * @return boolean
	 */
	public boolean isSoundEnabled()
	{
		return isEnabled(SOUND);
	}
	
	/**
	 * Sets the sound enabled preference.
	 * @param boolean enabled - The sound enabled preference.
	 * 	True to enable sound.
	 */
	public void setSoundEnabled(boolean enabled)
	{
		setEnabled(SOUND, enabled);
	}
	
	/**
	 * Gets the user's high score for the Easy game mode.
	 * @return int - The user's high score.
	 */
	public int getScoreEasy()
	{
		return getValue(SCORE_EASY);
	}
	
	/**
	 * Sets the user's high score for the Easy game mode.
	 * @param int score - The user's high score to set.
	 */
	public void setScoreEasy(int score)
	{
		setValue(SCORE_EASY, score);
	}
	
	/**
	 * Gets the user's high score for the Normal game mode.
	 * @return int - The user's high score.
	 */
	public int getScoreNormal()
	{
		return getValue(SCORE_NORMAL);
	}
	
	/**
	 * Sets the user's high score for the Normal game mode.
	 * @param int score - The user's high score to set.
	 */
	public void setScoreNormal(int score)
	{
		setValue(SCORE_NORMAL, score);
	}
	
	/**
	 * Gets the user's high score for the Unfair game mode.
	 * @return int - The user's high score.
	 */
	public int getScoreUnfair()
	{
		return getValue(SCORE_UNFAIR);
	}
	
	/**
	 * Sets the user's high score for the Unfair game mode.
	 * @param int score - The user's high score to set.
	 */
	public void setScoreUnfair(int score)
	{
		setValue(SCORE_UNFAIR, score);
	}
	
	/**
	 * Determines if a game preference is enabled by name.
	 * 
	 * @param String preference - The preference name.
	 * 	Defined as constants in this class.
	 * @return boolean
	 */
	public boolean isEnabled(String preference)
	{
    	SQLiteDatabase db = this.getReadableDatabase();
    	
    	String[] from = { _ID, preference };
    	Cursor cursor = db.query(TABLE_NAME, from, _ID + "=" + ROW_ID, null, null, null, null);

    	boolean enabled = true;
    	
    	if (cursor != null)
    	{
    		while (cursor.moveToNext())
        	{    			
    			int prefValue = cursor.getInt(1);
        		enabled = (prefValue == 1);
        	}
    		cursor.close();
    	}
    	
    	db.close();
    	return enabled;
	}
	
	/**
	 * Sets the enabled status of a game preference by name.
	 * 
	 * @param String preference - The preference name.
	 * 	Defined as constants in this class.
	 * @param boolean enabled
	 */
	public void setEnabled(String preference, boolean enabled)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	
    	int value = (enabled)? 1 : 0;
    	values.put(preference, value);
    	
    	int affectedRows = db.update(TABLE_NAME, values, _ID + "=" + ROW_ID, null);
    	
    	if (affectedRows < 1)
    	{
    		values.put(_ID, ROW_ID);
    		db.insertOrThrow(TABLE_NAME, null, values);
    	}
    	
    	db.close();
    }
	
	/**
	 * Gets the value of a game preference by name.
	 * 
	 * @param String preference - The preference name.
	 * 	Defined as constants in this class.
	 * @return int
	 */
	public int getValue(String preference)
	{
    	SQLiteDatabase db = this.getReadableDatabase();
    	
    	String[] from = { _ID, preference };
    	Cursor cursor = db.query(TABLE_NAME, from, _ID + "=" + ROW_ID, null, null, null, null);
    	
    	int prefValue = 0;
    	
    	if (cursor != null)
    	{
    		while (cursor.moveToNext())
        	{    			
    			prefValue = cursor.getInt(1);
        	}
    		cursor.close();
    	}
    	
    	db.close();
    	return prefValue;
	}
		
	/**
	 * Sets the value of a game preference by name.
	 * 
	 * @param String preference - The preference name.
	 * 	Defined as constants in this class.
	 * @param int value - The value to set.
	 */
	public void setValue(String preference, int value)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	
    	values.put(preference, value);
    	
    	int affectedRows = db.update(TABLE_NAME, values, _ID + "=" + ROW_ID, null);
    	
    	if (affectedRows < 1)
    	{
    		values.put(_ID, ROW_ID);
    		db.insertOrThrow(TABLE_NAME, null, values);
    	}
    	
    	db.close();
    }
}
