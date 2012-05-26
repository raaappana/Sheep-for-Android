package org.ruscoe.sheep.models;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * An item used in the game.
 * 
 * @author Dan Ruscoe (ruscoe.org)
 * @version 1.0
 */
public class GameItem
{
	// The X coordinate of the item.
	protected int mX;
	
	// The Y coordinate of the item.
	protected int mY;
	
	// Determines whether the item is visible or not.
	protected boolean mVisible = true;
	
	// The GameImage instance used to display the item's main image.
	protected GameImage mImage;
	
	// The GameImage instance used to display an icon over the item.
	protected GameImage mIcon;
	
	// The Rect instance used in collision detection.
	Rect mRect = null;
	
	public GameItem(Bitmap bitmap)
	{
		if (bitmap != null)
		{
			this.mImage = new GameImage();
			this.mImage.setBitmap(bitmap);
		}
		else
		{
			this.mImage = null;
		}
	}
	
	public void setIcon(Bitmap bitmap)
	{
		if (bitmap != null)
		{
			this.mIcon = new GameImage();
			this.mIcon.setBitmap(bitmap);
		}
		else
		{
			this.mIcon = null;
		}
	}
	
	/**
	 * Determines if a rectangle of a given width and height at a given
	 * X / Y coordinate collides with this item.
	 * 
	 * @param int x - The X coordinate of the item to test.
	 * @param int y - The Y coordinate of the item to test.
	 * @param int width - The width of the item to test.
	 * @param int height - The height of the item to test.
	 * @return boolean - True if a collision is detected.
	 */
	public boolean isCollision(int x, int y, int width, int height)
	{
		mRect = new Rect(x, y, (x + width), (y + height));
		return (mRect.intersects(this.mX, this.mY, (this.mX + this.getImage().getWidth()), (this.mY + this.getImage().getHeight())));
	}
	
	/**
	 * Determines if a point at a X / Y coordinate impacts this item.
	 * 
	 * @param int x - The X coordinate.
	 * @param int y - The y coordinate.
	 * @return boolean - True if point impacts this item.
	 */
	public boolean getImpact(int x, int y)
	{
		if ((x >= mX) && (x <= (mX + this.getImage().getWidth())))
		{
			if ((y >= mY) && (y <= (mY + this.getImage().getHeight())))
			{
				return true;
			}
		}

		return false;
	}
	
	public int getX()
	{
		return mX;
	}

	public void setX(int x)
	{
		this.mX = x;
	}

	public int getY()
	{
		return mY;
	}

	public void setY(int y)
	{
		this.mY = y;
	}

	public boolean isVisible()
	{
		return mVisible;
	}

	public void setVisible(boolean visible)
	{
		this.mVisible = visible;
	}

	public GameImage getImage()
	{
		return mImage;
	}

	public void setImage(GameImage image)
	{
		this.mImage = image;
	}

	public GameImage getIcon()
	{
		return this.mIcon;
	}
}
