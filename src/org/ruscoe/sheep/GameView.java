package org.ruscoe.sheep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ruscoe.sheep.R;
import org.ruscoe.sheep.constants.GameSettings;
import org.ruscoe.sheep.dao.GamePrefsData;
import org.ruscoe.sheep.models.GameItem;
import org.ruscoe.sheep.models.JumpingGameItem;
import org.ruscoe.sheep.util.RandomUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * The game view and main game thread.
 * 
 * GameView creates a new thread (GameThread) to handle all calculations and
 * drawing of game components.
 * 
 * GameThread contains the run() function, which serves as the game loop,
 * updating each cycle while the game is running.
 * 
 * @author Dan Ruscoe (ruscoe.org)
 * @version 1.0
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback
{
	// Game state constants.
	public static final int STATE_RUNNING = 1;
	public static final int STATE_PAUSE = 2;

	// Game mode constants.
	public static final int GAME_MODE_EASY = 1;
	public static final int GAME_MODE_NORMAL = 2;
	public static final int GAME_MODE_UNFAIR = 3;

	// The mode which the user the has chosen to play the game in.
	private int mGameMode = GAME_MODE_NORMAL;
	// The current state of the game.
	private int mGameState;
	// True while the game is running.
	private boolean mGameRun = true;

	// True while sheep instances should be updated.
	private boolean mUpdateSheep = true;
	// True while debris instances should be updated.
	private boolean mUpdateDebris = true;
	// True while bounce pad instance should be updated.
	private boolean mUpdateBouncePad = true;

	// Screen dimensions.
	private int mScreenXMin = 0;
	private int mScreenXMax = 0;
	private int mScreenYMax = 0;

	// The active application Context.
	private Context mGameContext;
	// The active application Activity.
	private Play mGameActivity;
	// The active application SurfaceHolder.
	private SurfaceHolder mGameSurfaceHolder = null;

	// The GameEnvironment instance, containing information about the current device.
	private GameEnvironment mGameEnvironment = null;
	
	// The user's game preferences and score data.
	private GamePrefsData mGamePrefsData = null;
	
	// The background image used in the game.
	private Bitmap mBackgroundImage = null;

	// The acceleration rate taken from the device accelerometer.
	// Used to allow the user to control the game's bounce pad item.
	private float mAccelX = 0;

	// The player's current score.
	private int mScore = 0;
	// The player's high score for the current game mode.
	private int mHighScore = 0;

	// The instance of Paint used to draw the UI text on the screen.
	private Paint mUiTextPaint = null;
	
	// The Y coordinate used to represent the ground in the game.s
	private int mGroundY = 0;

	// The maximum number of sheep item instances to exist in the game.
	private int mMaxSheep = GameSettings.MAX_SHEEP_NORMAL;

	// The bitmaps used by items in the game. Maps resource ID to an instance
	// of Bitmap for each resource.
	private HashMap<Integer, Bitmap> mGameBitmaps = new HashMap<Integer, Bitmap>();

	// List of active sheep item instances in the game.
	private List<JumpingGameItem> mSheep = new ArrayList<JumpingGameItem>();
	// List of active debris item instances in the game.
	private List<JumpingGameItem> mDebris = new ArrayList<JumpingGameItem>();

	// The maximum jump height a sheep item reaches before the fall causes damage.
	private int mMaxHeightForDamage = 0;
	
	// The user-controlled bounce pad item instance.
	private GameItem mBouncePad;

	/**
	 * The main game thread.
	 */
	class GameThread extends Thread
	{
		public GameThread(SurfaceHolder surfaceHolder, Context context,
				Handler handler)
		{
			mGameSurfaceHolder = surfaceHolder;
			mGameContext = context;

			Display display = mGameActivity.getWindowManager()
					.getDefaultDisplay();
			mScreenXMax = display.getWidth();
			mScreenYMax = display.getHeight();

			mMaxHeightForDamage = (mScreenYMax / 2);
			
			mBackgroundImage = BitmapFactory.decodeResource(
					mGameContext.getResources(), R.drawable.background);

			setGameStartState();
		}

		@Override
		public void run()
		{
			long sleepTime = 0;

			while (mGameRun)
			{
				Canvas c = null;
				try
				{
					c = mGameSurfaceHolder.lockCanvas(null);
					synchronized (mGameSurfaceHolder)
					{
						long beginTime = System.currentTimeMillis();
						
						// Reset skipped frame count.
						int framesSkipped = 0;

						if (mGameState == STATE_RUNNING)
						{
							// Update the game state.
							doUpdate();
						}

						// Draw to the screen.
						doDraw(c);

						// Calculate the length of the game state update in milliseconds.
						long timeDiff = System.currentTimeMillis() - beginTime;

						// calculate sleep time
						sleepTime = (int) (GameSettings.FRAME_PERIOD - timeDiff);
						
						// If sleepTime is greater than 0, updating the game state completed
						// within the time allocated to show a single frame.
						// This allows the thread time to sleep before the next update.
						if (sleepTime > 0)
						{
							try
							{
								Thread.sleep(sleepTime);
							} catch (InterruptedException e)
							{
								Log.e(GameSettings.LOG_NAME, e.getMessage());
							}
						}

						// If sleepTime less than 0, the game is running behind the
						// desired FPS. The game state must be updated without drawing
						// to the screen until either caught up or the maximum number of
						// skipped frames is reached.
						while (sleepTime < 0
								&& framesSkipped < GameSettings.MAX_FRAME_SKIPS)
						{
							doUpdate();
							// Increment sleepTime and framesSkipped until caught up
							// on negative sleep time or max frame skips is reached.
							sleepTime += GameSettings.FRAME_PERIOD;
							framesSkipped++;
						}

					}
				} finally
				{
					if (c != null)
					{
						mGameSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}

			return;
		}

		/**
		 * Sets the game running state.
		 * 
		 * @param running - The running state. True if game should run.
		 */
		public void setRunning(boolean running)
		{
			mGameRun = running;
		}

		/**
		 * Starts the game.
		 */
		public void doStart()
		{
			setState(STATE_RUNNING);
		}

		/**
		 * Sets the current game state.
		 * @param int state - The game state ID. Defined as STATE_* constants.
		 */
		public void setState(int state)
		{
			mGameState = state;
		}

		/**
		 * Pauses the game.
		 */
		public void pause()
		{
			synchronized (mGameSurfaceHolder)
			{
				if (mGameState == STATE_RUNNING)
				{
					setState(STATE_PAUSE);
				}
			}
		}

		/**
		 * Unpauses the game when paused.
		 */
		public void unpause()
		{
			synchronized (mGameSurfaceHolder)
			{
				if (mGameState != STATE_RUNNING)
				{
					setState(STATE_RUNNING);
				}
			}
		}

		/**
		 * Cleans up non-persistent changes made during a game session.
		 */
		public void cleanUp()
		{
			mSheep.clear();
			mDebris.clear();
		}

		/**
		 * Sets the state for a new game, resetting values that change
		 * during a game session.
		 */
		private void setGameStartState()
		{
			// Set game starting values based on game mode.
			switch (mGameMode)
			{
			case GAME_MODE_EASY:
				mMaxSheep = GameSettings.MAX_SHEEP_EASY;
				mHighScore = mGamePrefsData.getScoreEasy();
				break;
			case GAME_MODE_NORMAL:
				mMaxSheep = GameSettings.MAX_SHEEP_NORMAL;
				mHighScore = mGamePrefsData.getScoreNormal();
				break;
			case GAME_MODE_UNFAIR:
				mMaxSheep = GameSettings.MAX_SHEEP_UNFAIR;
				mHighScore = mGamePrefsData.getScoreUnfair();
				break;
			}

			// Create user-controlled bounce pad item.
			addGameBitmap(R.drawable.bounce_pad_frame_01);
			addGameBitmap(R.drawable.bounce_pad_frame_02);

			mBouncePad = new GameItem(
					loadGameBitmap(R.drawable.bounce_pad_frame_01));

			mGroundY = (mScreenYMax - mBouncePad.getImage().getHeight());

			Bitmap[] animationFrames = {
					loadGameBitmap(R.drawable.bounce_pad_frame_01),
					loadGameBitmap(R.drawable.bounce_pad_frame_02) };

			mBouncePad.getImage().setAnimationFrames(animationFrames);
			mBouncePad.getImage().setLoopAnimation(false);

			mBouncePad.setY(mGroundY - (mBouncePad.getImage().getHeight() / 2));
			mBouncePad.setX((mScreenXMax / 2)
					- (mBouncePad.getImage().getWidth() / 2));

			// Add required sheep item instances.
			int i;
			for (i = 0; i <= mMaxSheep; i++)
			{
				addSheep();
			}
		}

		/**
		 * Updates the game state.
		 */
		private void doUpdate()
		{
			if (mUpdateSheep)
			{
				updateSheep();
			}

			if (mUpdateDebris)
			{
				updateDebris();
			}

			if (mUpdateBouncePad)
			{
				updateBouncePad();
			}
		}

		/**
		 * Updates the state of the sheep items in the game.
		 */
		private void updateSheep()
		{
			JumpingGameItem currentSheep;

			int i;
			for (i = 0; i <= (mSheep.size() - 1); i++)
			{
				currentSheep = mSheep.get(i);

				if (!currentSheep.isActive())
				{
					mSheep.remove(i);
					continue;
				}

				// Update animation.
				currentSheep.getImage().updateAnimation(
						System.currentTimeMillis());

				// Update horizontal position.
				currentSheep.setX(currentSheep.getX()
						+ (int) (currentSheep.getHorizontalSpeed() * mGameEnvironment.getDensity()));

				if ((currentSheep.getDirection() == JumpingGameItem.DIRECTION_LEFT)
						&& (currentSheep.getX() <= mScreenXMin)
						|| (currentSheep.getDirection() == JumpingGameItem.DIRECTION_RIGHT)
						&& ((currentSheep.getX() + currentSheep.getImage()
								.getWidth()) >= mScreenXMax))
				{
					currentSheep.reverseDirection();
				}

				// Update vertical position.
				currentSheep.setY(currentSheep.getY()
						- (int) (currentSheep.getVerticalSpeed() * mGameEnvironment.getDensity()));

				currentSheep.setVerticalEnergy(currentSheep.getVerticalEnergy()
						- GameSettings.GRAVITY);

				if (currentSheep.getY() > (mGroundY - currentSheep.getImage()
						.getHeight()))
				{
					currentSheep.setY(mGroundY
							- currentSheep.getImage().getHeight());

					// Handle fall damage.
					if (currentSheep.getLastHeight() >= (mMaxHeightForDamage * mGameEnvironment.getDensity()))
					{
						// Handle bounce pad impact.
						if (mBouncePad.isCollision(currentSheep.getX(),
								currentSheep.getY(), currentSheep.getImage()
										.getWidth(), currentSheep.getImage()
										.getHeight()))
						{
							currentSheep.setLastHeight(0);
							currentSheep
									.setMaxVerticalEnergy((int) (GameSettings.SHEEP_STARTING_VERTICAL_ENERGY * GameSettings.SHEEP_BOUNCE_ENERGY_MULTIPLIER));
							currentSheep.setVerticalEnergy(currentSheep
									.getMaxVerticalEnergy());
							currentSheep.setIcon(null);

							// Increment score.
							updateScore(GameSettings.POINTS_PER_SHEEP);

							mBouncePad.getImage().startAnimation();

							Sound.playBounce();

							continue;
						} else
						{
							currentSheep.makeInactive();
							generateDebris(currentSheep.getX(),
									currentSheep.getY());

							// Decrement score.
							updateScore(-GameSettings.POINTS_PER_SHEEP);

							Sound.playPop();

							continue;
						}
					}

					if (currentSheep.isJumpExponentially())
					{
						currentSheep
								.setMaxVerticalEnergy((int) (currentSheep
										.getMaxVerticalEnergy() * GameSettings.SHEEP_JUMP_ENERGY_MULTIPLIER));
					}

					currentSheep.setVerticalEnergy(currentSheep
							.getMaxVerticalEnergy());
				}

				// Update last height reached.
				if ((mGroundY - currentSheep.getY()) > currentSheep
						.getLastHeight())
				{
					currentSheep.setLastHeight(mGroundY - currentSheep.getY());
					if ((currentSheep.getLastHeight() >= (mMaxHeightForDamage * mGameEnvironment.getDensity()))
							&& (currentSheep.getIcon() == null))
					{
						currentSheep
								.setIcon(loadGameBitmap(R.drawable.icon_danger));
					}
				}

				// Update exponential jumping.
				if (!currentSheep.isJumpExponentially())
				{
					if (RandomUtil.getRandomNumberWithinRange(0,
							GameSettings.SHEEP_JUMP_CHANCE) == GameSettings.SHEEP_JUMP_CHANCE)
					{
						currentSheep.setJumpExponentially(true);
					}
				}
			}

			// Replenish sheep supply.
			if (mSheep.size() < mMaxSheep)
			{
				for (i = mSheep.size(); i <= mMaxSheep; i++)
				{
					addSheep();
				}
			}
		}

		/**
		 * Updates the state of the debris items in the game.
		 */
		private void updateDebris()
		{
			int removeCount = 0;

			if (mDebris.size() > GameSettings.MAX_DISPLAYED_DEBRIS)
			{
				removeCount = (mDebris.size() - GameSettings.MAX_DISPLAYED_DEBRIS);
			}

			int i;
			JumpingGameItem currentDebris;

			for (i = 0; i <= (mDebris.size() - 1); i++)
			{
				currentDebris = mDebris.get(i);

				if (!currentDebris.isActive())
				{
					if (i <= (removeCount - 1))
					{
						mDebris.remove(i);
						continue;
					}
					continue;
				}

				// Update horizontal position.
				currentDebris
						.setX(currentDebris.getX()
								+ (int) (currentDebris.getHorizontalSpeed() * mGameEnvironment.getDensity()));

				// Update vertical position.
				currentDebris.setY(currentDebris.getY()
						- (int) (currentDebris.getVerticalSpeed() * mGameEnvironment.getDensity()));

				currentDebris.setVerticalEnergy(currentDebris
						.getVerticalEnergy() - GameSettings.GRAVITY);

				if (currentDebris.getY() > (mGroundY - currentDebris.getImage()
						.getHeight()))
				{
					currentDebris.setY(mGroundY
							- currentDebris.getImage().getHeight());

					currentDebris.makeInactive();
				}
			}
		}

		/**
		 * Updates the state of the user-controlled bounce pad item in the game.
		 */
		private void updateBouncePad()
		{
			mBouncePad.getImage().updateAnimation(System.currentTimeMillis());

			if ((mAccelX > GameSettings.ACCEL_SENSOR_BUFFER)
					|| (mAccelX < -GameSettings.ACCEL_SENSOR_BUFFER))
			{
				// Restrict movement with screen boundaries.
				if ((mAccelX < -GameSettings.ACCEL_SENSOR_BUFFER)
						&& (mBouncePad.getX() + mBouncePad.getImage()
								.getWidth()) > mScreenXMax)
				{
					return;
				}
				if ((mAccelX > GameSettings.ACCEL_SENSOR_BUFFER)
						&& (mBouncePad.getX() < mScreenXMin))
				{
					return;
				}

				// Update position on screen.
				int playerMovementDistance = (int) ((mAccelX * GameSettings.ACCEL_MULTIPLIER) * mGameEnvironment.getDensity());

				if (mAccelX > GameSettings.MAX_PAD_MOVEMENT_DISTANCE)
				{
					playerMovementDistance = GameSettings.MAX_PAD_MOVEMENT_DISTANCE;
				} else if (mAccelX < -GameSettings.MAX_PAD_MOVEMENT_DISTANCE)
				{
					playerMovementDistance = -GameSettings.MAX_PAD_MOVEMENT_DISTANCE;
				}

				mBouncePad.setX(mBouncePad.getX() - playerMovementDistance);
			}
		}

		/**
		 * Updates the player's score.
		 * 
		 * @param int change - The change in score, either positive or negative.
		 */
		private void updateScore(int change)
		{
			mScore += change;

			if (mScore > mHighScore)
			{
				mHighScore = mScore;

				switch (mGameMode)
				{
				case GAME_MODE_EASY:
					mGamePrefsData.setScoreEasy(mHighScore);
					break;
				case GAME_MODE_NORMAL:
					mGamePrefsData.setScoreNormal(mHighScore);
					break;
				case GAME_MODE_UNFAIR:
					mGamePrefsData.setScoreUnfair(mHighScore);
					break;
				}
			}
		}

		/**
		 * Draws the game items to the Canvas.
		 * 
		 * @param Canvas canvas - The active Canvas.
		 */
		private void doDraw(Canvas canvas)
		{
			canvas.drawBitmap(mBackgroundImage, 0, 0, null);

			drawSheep(canvas);
			drawDebris(canvas);
			drawBouncePad(canvas);
			drawUi(canvas);
		}

		/**
		 * Draws the sheep game items to the Canvas.
		 * 
		 * @param Canvas canvas - The active Canvas.
		 */
		private void drawSheep(Canvas canvas)
		{
			int i;
			JumpingGameItem currentSheep;

			int sheepCenterX;
			
			int iconX;
			int iconY;

			for (i = 0; i <= (mSheep.size() - 1); i++)
			{
				currentSheep = mSheep.get(i);

				if (currentSheep.isVisible())
				{
					// Sheep images default to face left. The images are reversed if the
					// sheep is moving to the right.
					if (currentSheep.getDirection() == JumpingGameItem.DIRECTION_RIGHT)
					{
						sheepCenterX = (currentSheep.getX() + (currentSheep.getImage().getWidth() / 2));
						
						canvas.save();
						canvas.scale(-1, 1, sheepCenterX, currentSheep.getY());
						
						canvas.drawBitmap(currentSheep.getImage().getBitmap(),
								currentSheep.getX(), currentSheep.getY(), null);
						
						canvas.restore();
					}
					else
					{
						canvas.drawBitmap(currentSheep.getImage().getBitmap(),
							currentSheep.getX(), currentSheep.getY(), null);
					}
					
					if (currentSheep.getIcon() != null)
					{
						iconX = (currentSheep.getX() + (currentSheep.getImage()
								.getWidth() / 2));
						iconY = (currentSheep.getY() - currentSheep.getIcon()
								.getHeight());
						
						canvas.drawBitmap(currentSheep.getIcon().getBitmap(),
								iconX, iconY, null);
					}
				}
			}
		}

		/**
		 * Draws the debris game items to the Canvas.
		 * 
		 * @param Canvas canvas - The active Canvas.
		 */
		private void drawDebris(Canvas canvas)
		{
			int i;
			JumpingGameItem currentDebris;

			for (i = 0; i <= (mDebris.size() - 1); i++)
			{
				currentDebris = mDebris.get(i);

				canvas.drawBitmap(currentDebris.getImage().getBitmap(),
						currentDebris.getX(), currentDebris.getY(), null);
			}
		}

		/**
		 * Draws the bounce pad item to the Canvas.
		 * 
		 * @param Canvas canvas - The active Canvas.
		 */
		private void drawBouncePad(Canvas canvas)
		{
			canvas.drawBitmap(mBouncePad.getImage().getBitmap(),
					mBouncePad.getX(), mBouncePad.getY(), null);
		}

		/**
		 * Draws the player's UI to the Canvas.
		 * Currently only draws the player's score.
		 * 
		 * @param Canvas canvas - The active Canvas.
		 */
		private void drawUi(Canvas canvas)
		{
			canvas.drawText(
					Integer.toString(mScore) + " / "
							+ Integer.toString(mHighScore), 30, 50,
					mUiTextPaint);
		}

		/**
		 * Adds a new sheep instance to the game state.
		 */
		private void addSheep()
		{
			JumpingGameItem newSheep = new JumpingGameItem(
					loadGameBitmap(R.drawable.sheep_frame_01));

			newSheep.setMaxHorizontalEnergy(GameSettings.SHEEP_STARTING_HORIZONTAL_ENERGY);
			newSheep.setMaxVerticalEnergy(GameSettings.SHEEP_STARTING_VERTICAL_ENERGY);

			newSheep.setHorizontalEnergy(newSheep.getMaxHorizontalEnergy());
			newSheep.setVerticalEnergy(newSheep.getMaxVerticalEnergy());

			newSheep.setMinHorizontalSpeed(GameSettings.MIN_SHEEP_HORIZONTAL_SPEED);
			newSheep.setMaxHorizontalSpeed(GameSettings.MAX_SHEEP_HORIZONTAL_SPEED);

			newSheep.setMinVerticalSpeed(GameSettings.MIN_SHEEP_VERTICAL_SPEED);
			newSheep.setMaxVerticalSpeed(GameSettings.MAX_SHEEP_VERTICAL_SPEED);

			newSheep.setX(RandomUtil.getRandomNumberWithinRange(mScreenXMin,
					mScreenXMax));

			if (RandomUtil.getRandomBoolean())
			{
				newSheep.setDirection(JumpingGameItem.DIRECTION_RIGHT);
			}

			newSheep.setY(mGroundY - newSheep.getImage().getHeight());

			Bitmap[] animationFrames = {
					loadGameBitmap(R.drawable.sheep_frame_01),
					loadGameBitmap(R.drawable.sheep_frame_02) };

			newSheep.getImage().setAnimationFrames(animationFrames);
			newSheep.getImage().startAnimation();

			mSheep.add(newSheep);
		}

		/**
		 * Generates debris instances at a given X and Y coordinate.
		 * Debris moves outward from a central location.
		 * 
		 * @param int x - The X coordinate to generate debris at.
		 * @param int y - The Y coordinate to generate debris at.
		 */
		private void generateDebris(int x, int y)
		{
			int i;
			for (i = GameSettings.MIN_DEBRIS_ITEMS; i <= GameSettings.MAX_DEBRIS_ITEMS; i++)
			{
				JumpingGameItem newDebris = new JumpingGameItem(
						loadGameBitmap(R.drawable.debris));

				newDebris
						.setMaxHorizontalEnergy(RandomUtil
								.getRandomNumberWithinRange(
										GameSettings.MIN_DEBRIS_STARTING_HORIZONTAL_ENERGY,
										GameSettings.MAX_DEBRIS_STARTING_HORIZONTAL_ENERGY));

				newDebris
						.setMaxVerticalEnergy(RandomUtil
								.getRandomNumberWithinRange(
										GameSettings.MIN_DEBRIS_STARTING_VERTICAL_ENERGY,
										GameSettings.MAX_DEBRIS_STARTING_VERTICAL_ENERGY));

				newDebris.setHorizontalEnergy(newDebris.getMaxHorizontalEnergy());
				newDebris.setVerticalEnergy(newDebris.getMaxVerticalEnergy());

				newDebris.setMinHorizontalSpeed(GameSettings.MIN_DEBRIS_HORIZONTAL_SPEED);
				newDebris.setMaxHorizontalSpeed(GameSettings.MAX_DEBRIS_HORIZONTAL_SPEED);

				newDebris.setMinVerticalSpeed(GameSettings.MIN_DEBRIS_VERTICAL_SPEED);
				newDebris.setMaxVerticalSpeed(GameSettings.MAX_DEBRIS_VERTICAL_SPEED);

				if (RandomUtil.getRandomBoolean())
				{
					newDebris.setDirection(JumpingGameItem.DIRECTION_RIGHT);
				}

				newDebris.setX(x);
				newDebris.setY(y);

				mDebris.add(newDebris);
			}
		}

		/**
		 * Causes every active sheep item instance to explode, as though taking
		 * fall damage. This serves no real function other than testing
		 * debris generation.
		 */
		public void explode()
		{
			mUpdateSheep = false;

			JumpingGameItem currentSheep;

			int i;
			for (i = 0; i <= (mSheep.size() - 1); i++)
			{
				currentSheep = mSheep.get(i);

				currentSheep.makeInactive();
				generateDebris(currentSheep.getX(), currentSheep.getY());

				Sound.playPop();
			}

			mUpdateSheep = true;
		}

		/**
		 * Callback invoked when the surface dimensions change.
		 * 
		 * @param int width - The surface width.
		 * @param int height - The surface height.
		 */
		public void setSurfaceSize(int width, int height)
		{
			// Synchronized to make sure changes occur atomically.
			synchronized (mGameSurfaceHolder)
			{
				mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage,
						width, height, true);
			}
		}

	}

	// The main game thread.
	private GameThread thread;

	/**
	 * The game view used to display the running game.
	 * 
	 * @param Context context - The active game Context.
	 * @param Play activity - The active game activity.
	 * @param GameEnvironment gameEnvironment - The game environment instance.
	 * @param int gameMode - The mode to start the game in.
	 */
	public GameView(Context context, Play activity, GameEnvironment gameEnvironment, int gameMode)
	{
		super(context);

		mGameContext = context;
		mGameActivity = activity;

		mGameEnvironment = gameEnvironment;
		
		mGamePrefsData = new GamePrefsData(context);

		mGameMode = gameMode;

		// Set up game UI font.
		Typeface typeface = Typeface.createFromAsset(mGameActivity.getAssets(),
				"fonts/Molot.otf");

		mUiTextPaint = new Paint();
		mUiTextPaint.setStyle(Paint.Style.FILL);
		mUiTextPaint.setColor(Color.BLACK);
		mUiTextPaint.setAntiAlias(true);
		if (typeface != null)
		{
			mUiTextPaint.setTypeface(typeface);
		}
		mUiTextPaint.setTextSize(mGameContext.getApplicationContext()
				.getResources().getDimensionPixelSize(R.dimen.ui_text_size));

		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		// Create thread only; it's started in surfaceCreated()
		thread = new GameThread(holder, context, new Handler()
		{
			@Override
			public void handleMessage(Message m)
			{
				showDialog(m);
			}
		});

		// Listen for events triggered by the user.
		setFocusable(true);

		thread.doStart();
	}

	private void showDialog(Message m)
	{
		mGameActivity.showDialog(m.getData().getInt("id"));
	}

	/**
	 * Fetches the game thread associated with this GameView.
	 * 
	 * @return GameThread - The game thread.
	 */
	public GameThread getThread()
	{
		return thread;
	}

	/**
	 * Standard window-focus override. Notice focus lost so we can pause on
	 * focus lost. e.g. user switches to take a call.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus)
	{
		if (!hasWindowFocus)
		{
			// thread.pause();
		}
	}

	/**
	 * Callback invoked when the surface dimensions change.
	 * 
	 * @param SurfaceHolder holder - The active SurfaceHolder.
	 * @param int format - The surface format.
	 * @param int width - The surface width.
	 * @param int height - The surface height.
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
		thread.setSurfaceSize(width, height);
	}

	/**
	 * Callback invoked when the Surface has been created and is ready to be used.
	 * 
	 * @param SurfaceHolder holder - The active SurfaceHolder.
	 */
	public void surfaceCreated(SurfaceHolder holder)
	{
		if (thread.getState() == Thread.State.TERMINATED)
		{
			thread = new GameThread(holder, getContext(), new Handler());
			thread.setRunning(true);
			thread.start();
			thread.doStart();
		} else
		{
			thread.setRunning(true);
			thread.start();
		}
	}

	/**
	 * Callback invoked when the Surface has been destroyed and must no longer be touched.
	 */
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		boolean retry = true;
		thread.setRunning(false);
		while (retry)
		{
			try
			{
				thread.join();
				retry = false;
			} catch (InterruptedException e)
			{
				Log.e(GameSettings.LOG_NAME, e.getMessage());
			}
		}
	}

	/**
	 * Loads, caches and returns a game bitmap from a resource ID.
	 * 
	 * @param int resourceId - The resource ID of the bitmap to load.
	 * @return Bitmap
	 */
	public Bitmap loadGameBitmap(int resourceId)
	{
		addGameBitmap(resourceId);
		return getGameBitmap(resourceId);
	}

	/**
	 * Loads and caches multiple game bitmaps from resource IDs.
	 * 
	 * @param int[] resourceIds - An array of resource IDs to load.
	 */
	public void addMultipleGameBitmaps(int[] resourceIds)
	{
		for (int i = 0; i <= resourceIds.length - 1; i++)
		{
			addGameBitmap(resourceIds[i]);
		}
	}

	/**
	 * Loads and caches a game bitmap from a resource ID.
	 * 
	 * @param int resourceId - The resource ID of the bitmap to load.
	 */
	public void addGameBitmap(int resourceId)
	{
		if (!mGameBitmaps.containsKey(resourceId))
		{
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeResource(
					mGameContext.getResources(), resourceId);

			if (bitmap != null)
			{
				mGameBitmaps.put(resourceId, bitmap);
			}
		}
	}

	/**
	 * Gets a cached bitmap by resource ID.
	 * 
	 * @param int resourceId - The bitmap resource ID.
	 * @return Bitmap
	 */
	public Bitmap getGameBitmap(int resourceId)
	{
		return mGameBitmaps.get(resourceId);
	}

	/**
	 * Sets the horizontal acceleration value from the device's accelerometer.
	 * 
	 * @param float accelX - The horizontal acceleration value.
	 */
	public void setAccelX(float accelX)
	{
		this.mAccelX = accelX;
	}
}
