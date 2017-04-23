package biemann.android.miner.screens.map;

import biemann.android.miner.utilities.GFXLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ActorBackground extends Actor
{
	// Constants
	final float FRICTION = 0.01f;
	
	// Member Variables
	private Texture mTextureBackground;
	private Texture mTextureForeground;
	private Sprite mSpriteBackground;
	private Sprite mSpriteForeground;
	
	private float mBackLayerHorizontalScrollOffset = 0.0f;
	private float mFrontLayerHorizontalScrollOffset = 0.0f;
	private float mPlanetLayerHorizontalScrollOffset = 0.0f;
	
	private float mScrollspeed = 0.0f;
	private float mScrollspeedBack = 0.0f;//positive will scroll from right to left
	private float mScrollspeedFront = 0.0f;//positive will scroll from right to left
	private float mScrollspeedPlanets = 0.0f;//positive will scroll from right to left
	
	private float mScreenHeight;
	
	private float mBackgroundWidth;
	private float mForegroundWidth;
	
	private float mBackLayerMaxHorizontalUoffset;
	private float mBackLayerMaxHorizontalUoffsetOld;
	
	private Color mBackgroundColor;
	private BackLayerHorizontalScrollOffsetListener mBackLayerHorizontalScrollOffsetCallback;
	
	
	public interface BackLayerHorizontalScrollOffsetListener
	{
		public void onBackLayerHorizontalScrollOffsetChanged(float x);
	}

	
	// Constructor
	public ActorBackground(GFXLoader gfxloader, int screen_width, int screen_height)
	{
		//mScreenWidth = screen_width;
		mScreenHeight = screen_height;
		
		mBackgroundColor = new Color(0, 0, 0, 1.0f);
		
		// 1) Create background
		mTextureBackground = new Texture(gfxloader.loadGraphic("map_background"));
		
		// set wrapping in both directions
		mTextureBackground.setWrap(TextureWrap.Repeat, TextureWrap.ClampToEdge);
		
		float fBackgroundScale = 1.0f;
		if (screen_height > mTextureBackground.getHeight())
		{
			// increase image size
			fBackgroundScale = screen_height / (float) mTextureBackground.getHeight();
		}
		else
		{
			// decrease image size
			fBackgroundScale = mTextureBackground.getHeight() / (float) screen_height;
		}
		
		// create sprite
		mBackgroundWidth = (mTextureBackground.getWidth() * fBackgroundScale);
		mSpriteBackground = new Sprite(mTextureBackground, 0, 0, mTextureBackground.getWidth(), mTextureBackground.getHeight());
		//mSpriteBackground.setSize(mBackgroundWidth, screen_height); //DO NOT bother with setSize - the image is sized in draw()
		
		// Calculate the maximum offset percent for U that the texture may scroll to so that U2 doesn't go beyond the edge of the texture
		mBackLayerMaxHorizontalUoffset = (mBackgroundWidth-screen_width)/mBackgroundWidth;
		
		
		
		// 2) Create Foreground layer
		mTextureForeground = new Texture(gfxloader.loadGraphic("map_stars"));
		
		// set wrapping in both directions
		mTextureForeground.setWrap(TextureWrap.Repeat, TextureWrap.ClampToEdge);
		
		float fForegroundScale = 1.0f;
		if (screen_height > mTextureForeground.getHeight())
		{
			// increase image size
			fForegroundScale = screen_height / (float) mTextureForeground.getHeight();
		}
		else
		{
			// decrease image size
			fForegroundScale = mTextureForeground.getHeight() / (float) screen_height;
		}
		
		// create sprite
		mForegroundWidth = (mTextureForeground.getWidth() * fForegroundScale);
		mSpriteForeground = new Sprite(mTextureForeground, 0, 0, mTextureForeground.getWidth(), mTextureForeground.getHeight());
		//mSpriteForeground.setSize(mForegroundWidth, screen_height); //DO NOT bother with setSize - the image is sized in draw()
	}

	
	public void setBackLayerHorizontalScrollOffsetListener(BackLayerHorizontalScrollOffsetListener listener)
	{
		mBackLayerHorizontalScrollOffsetCallback = listener;
	}
	
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		// needed to make actions work
		super.act(Gdx.graphics.getDeltaTime());
		
		// needed to make fade work
		mBackgroundColor.set(this.getColor().r, this.getColor().g, this.getColor().b, this.getColor().a * parentAlpha);
		batch.setColor(mBackgroundColor);
		
		// calculate horizontal texture scroll offset for background
		mBackLayerHorizontalScrollOffset += Gdx.graphics.getDeltaTime() * mScrollspeedBack;
		if(mBackLayerHorizontalScrollOffset>=mBackLayerMaxHorizontalUoffset)
		{
			//stop scrolling beyond the right edge
			mBackLayerHorizontalScrollOffset = mBackLayerMaxHorizontalUoffset;
			setScrollSpeed(0.0f);
		}
		if(mBackLayerHorizontalScrollOffset<0.0f)
		{
			//stop scrolling beyond the left edge
			mBackLayerHorizontalScrollOffset = 0.0f;
			setScrollSpeed(0.0f);
		}

		mSpriteBackground.setU(mBackLayerHorizontalScrollOffset);
		mSpriteBackground.setU2(mBackLayerHorizontalScrollOffset+1.0f);//Texture coordinates go from 0 to 1
		

		// FRONT LAYER: calculate horizontal texture scroll offset
		mFrontLayerHorizontalScrollOffset+=Gdx.graphics.getDeltaTime() * mScrollspeedFront;
		if(mFrontLayerHorizontalScrollOffset>=1.0f)//Texture coordinates go from 0 to 1
		{
			mFrontLayerHorizontalScrollOffset = mFrontLayerHorizontalScrollOffset - 1.0f;
		}
		mSpriteForeground.setU(mFrontLayerHorizontalScrollOffset);
		mSpriteForeground.setU2(mFrontLayerHorizontalScrollOffset+1.0f);//Texture coordinates go from 0 to 1
		
		
		// PLANETS: calculate the horizontal scroll offset for
		mPlanetLayerHorizontalScrollOffset = Gdx.graphics.getDeltaTime() * mScrollspeedPlanets;
		
		
		//Callback for when background scrolls
		if ((mBackLayerMaxHorizontalUoffsetOld != mBackLayerHorizontalScrollOffset) && (mBackLayerHorizontalScrollOffsetCallback != null))
		{
			mBackLayerMaxHorizontalUoffsetOld = mBackLayerHorizontalScrollOffset;
			// this will move the planet at the same speed as background !
			mBackLayerHorizontalScrollOffsetCallback.onBackLayerHorizontalScrollOffsetChanged(mPlanetLayerHorizontalScrollOffset * mBackgroundWidth);
		}
		
		
		// add friction in either direction
		if (mScrollspeed > 0.0001f)//is it positive
		{
			if (mScrollspeed > FRICTION)
			{
				mScrollspeed -= FRICTION;
			}
			else
			{
				mScrollspeed = 0.0f;
			}
		}
		else if (mScrollspeed < -0.0001f)//is it negative
		{
			if (mScrollspeed < -FRICTION)
			{
				mScrollspeed += FRICTION;
			}
			else
			{
				mScrollspeed = 0.0f;
			}
		}
		calculateLayerScrollSpeed(mScrollspeed);
		
		// show it
		batch.draw(mSpriteBackground, 0, 0, mBackgroundWidth, mScreenHeight);
		batch.draw(mSpriteForeground, 0, 0, mForegroundWidth, mScreenHeight);
		
		// if there's a visual glitch, try using between draws :
		//batch.setColor(1, 1, 1, 1);
	}
	
	public void setScrollSpeed(float scrollSpeed)
	{
		mScrollspeed = scrollSpeed;
		calculateLayerScrollSpeed(scrollSpeed);
	}
	
	private void calculateLayerScrollSpeed(float scrollSpeed)
	{
		mScrollspeedBack = scrollSpeed / 7.0f;
		mScrollspeedFront = scrollSpeed / 3.0f;
		mScrollspeedPlanets = scrollSpeed / 4.0f;
	}
}