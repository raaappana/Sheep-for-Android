package org.ruscoe.sheep.constants;

/**
 * Constants used to define game settings.
 * 
 * @author Dan Ruscoe (ruscoe.org)
 * @version 1.0
 */
public class GameSettings
{
	public static final String LOG_NAME = "Sheep";
	
	public static final int MAX_FPS = 60;
	public static final int MAX_FRAME_SKIPS = 5;
	public static final int FRAME_PERIOD = 1000 / MAX_FPS;

	public static final int MAX_SHEEP_EASY = 5;
	public static final int MAX_SHEEP_NORMAL = 10;
	public static final int MAX_SHEEP_UNFAIR = 40;

	public static final int SHEEP_STARTING_HORIZONTAL_ENERGY = 20;
	public static final int SHEEP_STARTING_VERTICAL_ENERGY = 15;
	
	public static final int MIN_SHEEP_HORIZONTAL_SPEED = -4;
	public static final int MAX_SHEEP_HORIZONTAL_SPEED = 3;
	
	public static final int MIN_SHEEP_VERTICAL_SPEED = -8;
	public static final int MAX_SHEEP_VERTICAL_SPEED = 7;
	
	public static final double SHEEP_JUMP_ENERGY_MULTIPLIER = 1.4;
	public static final double SHEEP_BOUNCE_ENERGY_MULTIPLIER = 6;
	
	public static final int GRAVITY = 5;

	public static final int SHEEP_JUMP_CHANCE = 600;

	public static final int MIN_DEBRIS_ITEMS = 5;
	public static final int MAX_DEBRIS_ITEMS = 10;
		
	public static final int MIN_DEBRIS_STARTING_HORIZONTAL_ENERGY = 10;
	public static final int MAX_DEBRIS_STARTING_HORIZONTAL_ENERGY = 60;
	
	public static final int MIN_DEBRIS_STARTING_VERTICAL_ENERGY = 30;
	public static final int MAX_DEBRIS_STARTING_VERTICAL_ENERGY = 100;
	
	public static final int MIN_DEBRIS_HORIZONTAL_SPEED = -10;
	public static final int MAX_DEBRIS_HORIZONTAL_SPEED = 10;
	
	public static final int MIN_DEBRIS_VERTICAL_SPEED = -10;
	public static final int MAX_DEBRIS_VERTICAL_SPEED = 10;
	
	public static final int MAX_DISPLAYED_DEBRIS = 70;

	public static final double ACCEL_MULTIPLIER = 1.5;
	public static final double ACCEL_SENSOR_BUFFER = 0.3;

	public static final int MAX_PAD_MOVEMENT_DISTANCE = 6;

	public static final int POINTS_PER_SHEEP = 1;
}
