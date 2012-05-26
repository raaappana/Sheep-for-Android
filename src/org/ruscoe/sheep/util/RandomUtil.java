package org.ruscoe.sheep.util;

import java.util.Random;

/**
 * Random utility functions.
 * 
 * @author Dan Ruscoe (ruscoe.org)
 * @version 1.0
 */
public class RandomUtil
{
	private static Random random = new Random();
	
	public static int getRandomNumberWithinRange(Integer min, Integer max)
	{
		int random = min + (int)(Math.random() * ((max - min) + 1));
		
		return random;
	}
	
	public static boolean getRandomBoolean()
	{
		return random.nextBoolean();
	}
}
