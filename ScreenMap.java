package biemann.android.miner.screens.map;

import biemann.android.miner.MainGame;
import biemann.android.miner.screens.ActorText;
import biemann.android.miner.screens.DefaultScreen;
import biemann.android.miner.screens.game.LevelData;
import biemann.android.miner.screens.map.ActorBackground.BackLayerHorizontalScrollOffsetListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class ScreenMap extends DefaultScreen implements InputProcessor, BackLayerHorizontalScrollOffsetListener
{
	// Constants
	final private String FPS = "fps: ";
	final private int FPS_FONT_SIZE = 20; //at 1.0 logical density

		
	// Member Variables
	private Stage mStage;
	private InputMultiplexer mInputMultiplexer;
	private int mTouchDownXCoord;
	private int mTouchLastKnownXcoord;
	private long mTouchDownTime;
	private ActorBackground mActorBackground;
	private BitmapFont mSpaceFontFps;
	private ActorText mActorFPS;
	private int mOldFPS;
	private int mNewFPS;
    private GlyphLayout glyphLayout;// used to calculate text width and height
	
	
	// Constructor
	public ScreenMap(MainGame game)
	{
		super(game);
		
		mStage = new Stage(new FitViewport(mGame.getIdealWidth(), mGame.getIdealHeight()));
		
		// create background Actor
		mActorBackground = new ActorBackground(game.getGFXLoader(), game.getIdealWidth(), game.getIdealHeight());
		mActorBackground.setPosition(0,0);
		mStage.addActor(mActorBackground);
		mActorBackground.setBackLayerHorizontalScrollOffsetListener(this);// to receive callbacks when the background changes
		
		// Space Font (small: fps)
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/spacefont.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		final float fScaledFontSize = FPS_FONT_SIZE * Gdx.graphics.getDensity();
		parameter.size = (int) fScaledFontSize;//NOTE: If you use too big of a size, things might explode
		mSpaceFontFps = generator.generateFont(parameter);
		generator.dispose();
		
		// create FPS Actor
        glyphLayout = new GlyphLayout();
		final Color colorFps = new Color(Color.WHITE);
        glyphLayout.setText(mSpaceFontFps, FPS+"000");
		final float fFpsX = ((float)mGame.getIdealWidth()) - glyphLayout.width;
		final float fFPSY = ((float)mGame.getIdealHeight()) - glyphLayout.height;
		mActorFPS = new ActorText(mSpaceFontFps, FPS+"000", colorFps, fFpsX, fFPSY);
		mStage.addActor(mActorFPS);

		
		// catch the BACK button
		Gdx.input.setCatchBackKey(true);
		
//		mStage.addListener(new InputListener()
//		{
//
//			@Override
//			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//				super.touchDown(event, x, y, pointer, button);
//				Gdx.app.log("my app", "Pressed");
//				return true;
//			}
//
//			@Override
//			public void touchUp(InputEvent event, float x, float y,int pointer, int button) {
//				super.touchUp(event, x, y, pointer, button);
//				Gdx.app.log("my app", "Released");
//			}
//
//		});
	}
	
	@Override
	public void render(float delta)
	{
		//Gdx.gl.glClearColor(0, 0, 0, 0);//clear the screen black - not needed since the background image exists
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// update fps
		mNewFPS = Gdx.graphics.getFramesPerSecond();
		if (mNewFPS != mOldFPS)
		{
			mActorFPS.setText(FPS + mNewFPS);
			mOldFPS = mNewFPS;
		}
		
		// update the actors
		mStage.act(delta);
		
		// draw the actors
		mStage.draw();
	}

	// called when this screen is set as the screen with game.setScreen()
	@Override
	public void show()
	{
		// set the stage as the input processor
        //Gdx.input.setInputProcessor(mStage);
		// tutorial: https://github.com/libgdx/libgdx/wiki/Event-handling
		mInputMultiplexer = new InputMultiplexer();
		//multiplexer.addProcessor(new MyUiInputProcessor());//first, handle the actors
		mInputMultiplexer.addProcessor(this);// if actors return false, handle general game UI
		Gdx.input.setInputProcessor(mInputMultiplexer);
        
        // add a fade-in effect to the whole stage
        mStage.getRoot().getColor().a = 0f;
        mStage.addAction( Actions.fadeIn( 1.0f ) );
	}

	// called when current screen changes from this to a different screen
	@Override
	public void hide()
	{
		// dispose the screen when leaving the screen;
        // note that the dispose() method is not called automatically by the
        // framework, so we must figure out when it's appropriate to call it
        dispose();
	}

	// never called automatically
	@Override
	public void dispose()
	{
		mStage.dispose();
	}

	@Override
	public boolean keyDown(int keycode)
	{
		if (keycode == Keys.BACK)
		{
            Gdx.app.exit();
        }
        //return super.keyDown(keycode);
		return true;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		return false;
	}

	@Override
	public boolean keyTyped(char character)
	{
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		if (pointer == 0)
		{
			mTouchDownXCoord = screenX;
			mTouchLastKnownXcoord = screenX;
			mTouchDownTime = System.currentTimeMillis();
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		if (pointer == 0)
		{
			//mActorBackground.setScrollSpeed(0.0f);// don't use this otherwise the scrolling stops right away
			final int iXdelta = mTouchDownXCoord - screenX;
			final int iTimeDelta = (int) (System.currentTimeMillis() - mTouchDownTime);
			if (iTimeDelta != 0)// avoid division by zero
			{
				float speedPixPerMS = iXdelta / iTimeDelta;

				// maximum speed
				if (speedPixPerMS > 2.0f)
					speedPixPerMS = 2.0f;
				else if (speedPixPerMS < -2.0f)
					speedPixPerMS = -2.0f;

				mActorBackground.setScrollSpeed(speedPixPerMS);
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	// Called when a finger or the mouse was dragged
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		if (pointer == 0)
		{
			final int iDeltaX = mTouchLastKnownXcoord - screenX;
			//Gdx.app.log("miner", "touchDragged delta X="+screenX);
			mActorBackground.setScrollSpeed((float)iDeltaX * 0.01f);
			mTouchLastKnownXcoord = screenX;
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		return false;
	}

	// Called when the mouse wheel was scrolled
	@Override
	public boolean scrolled(int amount)
	{
		return false;
	}

	/**
	 * Implementation for BackLayerHorizontalScrollOffsetListener
	 */
	@Override
	public void onBackLayerHorizontalScrollOffsetChanged(float x)
	{
		// called when user drags screen - used to move other actors

		
	}


}
