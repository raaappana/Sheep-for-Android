package org.ruscoe.sheep.models;

import android.graphics.Bitmap;

/**
 * An image used by game items.
 * May be a static image or an animation.
 * 
 * @author Dan Ruscoe (ruscoe.org)
 * @version 1.0
 */
public class GameImage 
{
	// The default length of an animation frame in milliseconds.
	public static final int DEFAULT_ANIMATION_FRAME_LENGTH = 200;
	
	// The Bitmap instance of the image.
	protected Bitmap mBitmap = null;
	// The image width.
	protected int mWidth = 0;
	// The image height.
	protected int mHeight = 0;
	
	// The image visibility status. True when visible.
	private boolean mVisible = true;
	
	// Animation properties.
	
	// An array of animation frame Bitmap instances.
	private Bitmap[] mAnimationFrames;
	// The length of each animation frame in milliseconds.
	private int mAnimationFrameLength = DEFAULT_ANIMATION_FRAME_LENGTH;
	// The animation running status. True when running.
	private boolean mAnimationRunning = false;
	// True when the animation should be played in reverse.
	private boolean mReverseAnimation = false;
	// True when the animation should be looped.
	private boolean mLoopAnimation = true;
	// The index of the last animation frame played.
	private int mLastAnimationFrame = 0;
	// The timestamp in milliseconds indicating when the last animation frame was displayed.
	private long mLastAnimationTime = 0;
	
	public GameImage()
	{
	}
	
	public void updateAnimation(long currentTime)
	{
		if (!mAnimationRunning)
		{
			return;
		}
		
		if (currentTime < (mLastAnimationTime + mAnimationFrameLength))
		{
			return;
		}
		
		int frame = 0;
		int maxFrame = (mAnimationFrames.length - 1);
		
		if (mReverseAnimation)
		{
			// Reached beginning of animation frames.
			if ((mLastAnimationFrame - 1) < 1)
			{
				if (mLoopAnimation)
				{
					frame = maxFrame;
				}
				else
				{
					stopAnimation();
				}
			}
			else
			{
				frame = (mLastAnimationFrame - 1);
			}
		}
		else
		{
			// Reached end of animation frames.
			if ((mLastAnimationFrame + 1) > maxFrame)
			{
				if (mLoopAnimation)
				{
					frame = 0;
				}
				else
				{
					stopAnimation();
				}
			}
			else
			{
				frame = (mLastAnimationFrame + 1);
			}
		}
		
		mLastAnimationFrame = frame;
		mLastAnimationTime = currentTime;
		
		if (mAnimationFrames[frame] != null)
		{
			setBitmap(mAnimationFrames[frame]);
		}
	}
	
	public boolean startAnimation()
	{
		if ((mAnimationFrames != null) && (mAnimationFrames.length > 0) && !mAnimationRunning)
		{
			mAnimationRunning = true;
			return true;
		}
		
		return false;
	}
	
	public boolean stopAnimation()
	{
		if (mAnimationRunning)
		{
			mAnimationRunning = false;
			return true;
		}
		return false;
	}
	
	public void setBitmap(Bitmap bitmap)
	{
		if (bitmap != null)
		{
			this.mBitmap = bitmap;
			this.mWidth = bitmap.getWidth();
			this.mHeight = bitmap.getHeight();
		}
	}
	
	public Bitmap getBitmap()
	{
		return mBitmap;
	}
		
	public int getWidth()
	{
		return mWidth;
	}

	public int getHeight()
	{
		return mHeight;
	}
	
	public boolean isVisible()
	{
		return mVisible;
	}

	public void setVisible(boolean visible)
	{
		this.mVisible = visible;
	}
	
	public Bitmap[] getAnimationFrames()
	{
		return mAnimationFrames;
	}
	
	public void setAnimationFrames(Bitmap[] animationFrames)
	{
		this.mAnimationFrames = animationFrames;
	}
	
	public int getAnimationFrameLength()
	{
		return mAnimationFrameLength;
	}

	public void setAnimationFrameLength(int animationFrameLength)
	{
		this.mAnimationFrameLength = animationFrameLength;
	}
	
	public boolean isReverseAnimation()
	{
		return mReverseAnimation;
	}

	public void setReverseAnimation(boolean reverseAnimation)
	{
		this.mReverseAnimation = reverseAnimation;
	}
	
	public boolean isLoopAnimation()
	{
		return mLoopAnimation;
	}

	public void setLoopAnimation(boolean loopAnimation)
	{
		this.mLoopAnimation = loopAnimation;
	}
}
