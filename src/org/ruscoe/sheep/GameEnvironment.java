package org.ruscoe.sheep;

import android.content.res.Configuration;

/**
 * Stores environment information relevant to the user's device.
 * 
 * @author Dan Ruscoe (ruscoe.org)
 * @version 1.0
 */
public class GameEnvironment
{
	// The default orientation of the device during normal use.
	private int mDefaultOrientation = Configuration.ORIENTATION_LANDSCAPE;
	
	// The rotation of the device in degrees.
	private int mRotation;
	
	// The screen density. Used to properly scale item movement distance values.
	private float mDensity;
	
	public int getDefaultOrientation()
	{
		return mDefaultOrientation;
	}
	
	public void setDefaultOrientation(int defaultOrientation)
	{
		this.mDefaultOrientation = defaultOrientation;
	}
	
	public int getRotation()
	{
		return mRotation;
	}

	public void setRotation(int rotation)
	{
		this.mRotation = rotation;
	}

	public float getDensity()
	{
		return mDensity;
	}
	
	public void setDensity(float density)
	{
		this.mDensity = density;
	}
}
