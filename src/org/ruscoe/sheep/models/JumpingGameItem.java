package org.ruscoe.sheep.models;

import android.graphics.Bitmap;

/**
 * A game item capable of moving horizontally and vertically and speeds
 * calculated from energy values.
 * 
 * @author Dan Ruscoe (ruscoe.org)
 * @version 1.0
 */
public class JumpingGameItem extends GameItem
{
	// Movement direction constants.
	public static final int DIRECTION_LEFT = 0;
	public static final int DIRECTION_RIGHT = 1;
	
	// The units of speed gained from each unit of energy.
	// This controls how quickly the item will move on-screen
	// with a given energy value.
	private static final double SPEED_PER_ENERGY_UNIT = 0.1;
	
	// The minimum possible horizontal speed of the item. May be negative.
	private int mMinHorizontalSpeed = 0;
	// The maximum possible horizontal speed of the item.
	private int mMaxHorizontalSpeed = 0;
	
	// The minimum possible vertical speed of the item. May be negative.
	private int mMinVerticalSpeed = 0;
	// The maximum possible vertical speed of the item.
	private int mMaxVerticalSpeed = 0;
	
	// The maximum horizontal energy of the item.
	private int mMaxHorizontalEnergy = 0;
	// The maximum vertical energy of the item.
	private int mMaxVerticalEnergy = 0;
	
	// The current horizontal energy of the item. Used to determine rate of horizontal movement.
	private int mHorizontalEnergy = 0;
	// The current vertical energy of the item. Used to determine rate of vertical movement.
	private int mVerticalEnergy = 0;

	// The current movement direction of the item.
	private int mDirection = DIRECTION_LEFT;
	
	// The last height in pixels reached by the item.
	private int mLastHeight = 0;
	
	// True if this item should jump exponentially higher after completion of each jump.
	private boolean mJumpExponentially = false;
	
	// True if this item is currently active in the game.
	private boolean mActive = true;
	
	/**
	 * Instantiates a new JumpingGameItem with an exiting Bitmap.
	 * @param bitmap
	 */
	public JumpingGameItem(Bitmap bitmap)
	{
		super(bitmap);
	}
	
	/**
	 * Makes this JumpingGameItem inactive in the game.
	 */
	public void makeInactive()
	{
		mMaxHorizontalEnergy = 0;
		mMaxVerticalEnergy = 0;
		mHorizontalEnergy = 0;
		mVerticalEnergy = 0;
		
		mJumpExponentially = false;
		mActive = false;
		mVisible = false;
	}
	
	/**
	 * Gets the current vertical speed calculated from vertical energy and
	 * capped within pre-set boundaries.
	 * 
	 * @return int - The vertical speed.
	 */
	public int getVerticalSpeed()
	{
		int speed = (int) (SPEED_PER_ENERGY_UNIT * mVerticalEnergy);
		
		if (speed > mMaxVerticalSpeed)
		{
			speed = mMaxVerticalSpeed;
		}
		else if (speed < mMinVerticalSpeed)
		{
			speed = mMinVerticalSpeed;
		}
		
		return speed;
	}
	
	/**
	 * Gets the current horizontal speed calculated from horizontal energy
	 * and capped within pre-set boundaries.
	 * 
	 * @return int - The horizontal speed.
	 */
	public int getHorizontalSpeed()
	{
		int speed = (int) (SPEED_PER_ENERGY_UNIT * mHorizontalEnergy);
		
		if (speed > mMaxHorizontalSpeed)
		{
			speed = mMaxHorizontalSpeed;
		}
		else if (speed < mMinHorizontalSpeed)
		{
			speed = mMinHorizontalSpeed;
		}
		
		if (mDirection == DIRECTION_LEFT)
		{
			return -speed;
		}
		else
		{
			return speed;
		}
	}
	
	/**
	 * Reverses the horizontal direction of the item.
	 */
	public void reverseDirection()
	{
		if (mDirection == DIRECTION_LEFT)
		{
			mDirection = DIRECTION_RIGHT;
		}
		else
		{
			mDirection = DIRECTION_LEFT;
		}
	}

	public int getMinHorizontalSpeed()
	{
		return mMinHorizontalSpeed;
	}

	public void setMinHorizontalSpeed(int minHorizontalSpeed)
	{
		this.mMinHorizontalSpeed = minHorizontalSpeed;
	}

	public int getMaxHorizontalSpeed()
	{
		return mMaxHorizontalSpeed;
	}

	public void setMaxHorizontalSpeed(int maxHorizontalSpeed)
	{
		this.mMaxHorizontalSpeed = maxHorizontalSpeed;
	}

	public int getMinVerticalSpeed()
	{
		return mMinVerticalSpeed;
	}

	public void setMinVerticalSpeed(int minVerticalSpeed)
	{
		this.mMinVerticalSpeed = minVerticalSpeed;
	}

	public int getMaxVerticalSpeed()
	{
		return mMaxVerticalSpeed;
	}

	public void setMaxVerticalSpeed(int maxVerticalSpeed)
	{
		this.mMaxVerticalSpeed = maxVerticalSpeed;
	}

	public int getMaxHorizontalEnergy()
	{
		return mMaxHorizontalEnergy;
	}

	public void setMaxHorizontalEnergy(int maxHorizontalEnergy)
	{
		this.mMaxHorizontalEnergy = maxHorizontalEnergy;
	}

	public int getMaxVerticalEnergy()
	{
		return mMaxVerticalEnergy;
	}

	public void setMaxVerticalEnergy(int maxVerticalEnergy)
	{
		this.mMaxVerticalEnergy = maxVerticalEnergy;
	}

	public int getHorizontalEnergy()
	{
		return mHorizontalEnergy;
	}

	public void setHorizontalEnergy(int horizontalEnergy)
	{
		this.mHorizontalEnergy = horizontalEnergy;
	}

	public int getVerticalEnergy()
	{
		return mVerticalEnergy;
	}

	public void setVerticalEnergy(int verticalEnergy)
	{
		this.mVerticalEnergy = verticalEnergy;
	}

	public int getDirection()
	{
		return mDirection;
	}

	public void setDirection(int direction)
	{
		this.mDirection = direction;
	}

	public int getLastHeight()
	{
		return mLastHeight;
	}

	public void setLastHeight(int lastHeight)
	{
		this.mLastHeight = lastHeight;
	}

	public boolean isJumpExponentially()
	{
		return mJumpExponentially;
	}

	public void setJumpExponentially(boolean jumpExponentially)
	{
		this.mJumpExponentially = jumpExponentially;
	}

	public boolean isActive()
	{
		return mActive;
	}

	public void setActive(boolean active)
	{
		this.mActive = active;
	}
}
