package biemann.android.miner.screens;

import biemann.android.miner.MainGame;
import biemann.android.miner.screens.game.LevelData;
import biemann.android.miner.utilities.GFXLoader;

import com.badlogic.gdx.Screen;

public abstract class DefaultScreen implements Screen {
	protected MainGame mGame;
	protected GFXLoader mGFXLoader;
	protected LevelData mLevelData;

	public DefaultScreen(MainGame game) {
		mGame = game;
		mGFXLoader = game.getGFXLoader();
		mLevelData = game.getLevelData(game.getCurrentLevel());
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() { 
	}

	@Override
	public void dispose() {
	}
	
}
