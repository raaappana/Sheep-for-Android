package org.ruscoe.sheep.dao;

import static android.provider.BaseColumns._ID;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Data Access Object for the game database.
 * Handles initial table creation. Extend this class to allow access
 * to specific data.
 * 
 * @author Dan Ruscoe (ruscoe.org)
 * @version 1.0
 */
public class GameDAO extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "sheep.db";
	private static final int DATABASE_VERSION = 1;
	
	// Create table statement.
	private static final String CREATE_TABLE_GAME_PREFS = "CREATE TABLE "
	+ GamePrefsData.TABLE_NAME + " ("
	+ _ID + " INTEGER PRIMARY KEY, "
	+ GamePrefsData.SOUND + " INTEGER DEFAULT 1, "
	+ GamePrefsData.SCORE_EASY + " INTEGER DEFAULT 0,"
	+ GamePrefsData.SCORE_NORMAL + " INTEGER DEFAULT 0,"
	+ GamePrefsData.SCORE_UNFAIR + " INTEGER DEFAULT 0"
	+ ");";
		
	public GameDAO(Context ctx)
	{
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		Log.d("Sheep", "Creating DB tables");
		
		db.execSQL(CREATE_TABLE_GAME_PREFS);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		onCreate(db);
	}

}
