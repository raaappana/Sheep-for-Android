package org.ruscoe.sheep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * The initial activity the user is presented with
 * when starting the game. Displays menu buttons
 * used to play the game at various difficulties.
 * 
 * @author Dan Ruscoe (ruscoe.org)
 * @version 1.0
 */
public class Main extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
	public void buttonClick(View v)
	{
		Intent i = null;
		
    	switch (v.getId())
    	{
			case R.id.playEasyButton:
				i = new Intent(this, Play.class);
				i.putExtra("gameMode", GameView.GAME_MODE_EASY);
				startActivity(i);
				break;
			case R.id.playNormalButton:
				i = new Intent(this, Play.class);
				i.putExtra("gameMode", GameView.GAME_MODE_NORMAL);
				startActivity(i);
				break;
			case R.id.playUnfairButton:
				i = new Intent(this, Play.class);
				i.putExtra("gameMode", GameView.GAME_MODE_UNFAIR);
				startActivity(i);
				break;
    	}
	}
}