package com.tv.xeeng.game.elements.common;

import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.manager.SoundManager;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.Constants;

/**
 * The GrowButton class simply shows an image that grows to a specific scale
 * while the player is touching it and returns to its original scale when the
 * touch is lifted or lost.
 * <p/>
 * ** @author Brian Broyles - IFL Game Studio
 */
public abstract class BaseGrowButton extends TiledSprite {

	// ====================================================
	// CONSTANTS
	// ====================================================
	protected static final float GROW_DURATION_SECONDS = 0.05f;
	protected static final float NORMAL_SCALE_DEFAULT = 1;
	protected static final float GROWN_SCALE_DEFAULT = 1.4f;
	protected static final float ENABLED_ALPHA = 1f;
	protected static final float DISABLED_ALPHA = 0.5f;

	// ====================================================
	// VARIABLES
	// ====================================================
	public boolean mIsEnabled = true;
	protected float mNormalScale = NORMAL_SCALE_DEFAULT;
	protected float mGrownScale = GROWN_SCALE_DEFAULT;
	protected boolean mIsTouched = false;
	protected boolean mIsLarge = false;
	protected boolean mIsClicked = false;
	protected boolean mTouchStartedOnThis = false;

	private TouchEvent touchEvent;

	// ====================================================
	// ABSTRACT METHOD
	// ====================================================
	public abstract void onClick();

	// ====================================================
	// CONSTRUCTOR
	// ====================================================
	public BaseGrowButton(final float pX, final float pY,
			final ITextureRegion pTextureRegion) {
		this(pX, pY, new TiledTextureRegion(pTextureRegion.getTexture(),
				pTextureRegion));
	}

	public BaseGrowButton(final float pX, final float pY,
			final ITiledTextureRegion pTiledTR) {
		super(pX, pY, pTiledTR, BaseXeengGame.INSTANCE
				.getVertexBufferObjectManager());
	}

	// ====================================================
	// METHODS
	// ====================================================
	public void setScales(final float pNormalScale, final float pGrownScale) {
		mNormalScale = pNormalScale;
		mGrownScale = pGrownScale;
		this.setScale(pNormalScale);
	}

	@Override
	protected void onManagedUpdate(final float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);

		if (touchEvent != null) {
			final TouchEvent pSceneTouchEvent = touchEvent;

			final float rawPoints[] = convertSceneCoordinatesToLocalCoordinates(
					touchEvent.getX(), touchEvent.getY());

			final float pTouchAreaLocalX = rawPoints[Constants.VERTEX_INDEX_X];
			final float pTouchAreaLocalY = rawPoints[Constants.VERTEX_INDEX_Y];

			if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
				if (pTouchAreaLocalX > this.getWidth() || pTouchAreaLocalX < 0f
						|| pTouchAreaLocalY > this.getHeight()
						|| pTouchAreaLocalY < 0f) {
					mTouchStartedOnThis = false;
				} else {
					mTouchStartedOnThis = true;
				}
				if (mIsEnabled)
					mIsTouched = true;
			} else if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_MOVE) {
				if (pTouchAreaLocalX > this.getWidth() || pTouchAreaLocalX < 0f
						|| pTouchAreaLocalY > this.getHeight()
						|| pTouchAreaLocalY < 0f) {
					if (mIsTouched) {
						mIsTouched = false;
					}
				} else {
					if (mTouchStartedOnThis && !mIsTouched)
						if (mIsEnabled)
							mIsTouched = true;
				}
			} else if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
				if (mIsTouched && mTouchStartedOnThis) {
					mIsTouched = false;
					mIsClicked = true;
					mTouchStartedOnThis = false;
					SoundManager.playClick(1f, 0.5f);
				}
				touchEvent = null;
			}
		}

		if (!mIsLarge && mIsTouched) {
			this.registerEntityModifier(new ScaleModifier(
					GROW_DURATION_SECONDS, mNormalScale, mGrownScale) {
				@Override
				protected void onModifierFinished(final IEntity pItem) {
					super.onModifierFinished(pItem);
					mIsLarge = true;
				}
			});
		}
		if (mIsLarge && !mIsTouched) {
			this.registerEntityModifier(new ScaleModifier(
					GROW_DURATION_SECONDS, mGrownScale, mNormalScale) {
				@Override
				protected void onModifierFinished(final IEntity pItem) {
					super.onModifierFinished(pItem);
					mIsLarge = false;
					if (mIsClicked) {
						onClick();
						mIsClicked = false;
					}
				}
			});
			mIsLarge = false;
		}
		if (getTileCount() > 1) {

			if (mIsEnabled) {

				setCurrentTileIndex(0);
			} else {

				setCurrentTileIndex(1);
			}
		} else {

			if (mIsEnabled) {
				if (this.getAlpha() != ENABLED_ALPHA)
					this.setAlpha(ENABLED_ALPHA);
			} else {
				if (this.getAlpha() != DISABLED_ALPHA)
					this.setAlpha(DISABLED_ALPHA);
			}
		}
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
		// Handling touch event will be moved to onManagedUpdate.
		touchEvent = pSceneTouchEvent;

		return true;
	}

	@Override
	public void setIgnoreUpdate(boolean pIgnoreUpdate) {
		super.setIgnoreUpdate(pIgnoreUpdate);

		if (pIgnoreUpdate == true) {
			touchEvent = null;

			mIsTouched = false;
			mIsLarge = false;
			mIsClicked = false;
			mTouchStartedOnThis = false;
		}
	}
}