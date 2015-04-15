package com.tv.xeeng.game.elements.common;

import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.game.elements.common.ProgressTimer.OnProgressTimerListener;
import org.andengine.entity.Entity;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

public class StartGameButton extends Entity {

	public enum StartGameState {

		START_GAME, READY_GAME, DID_READY_GAME, HIDDEN
	}

	private StartGameState state;

	private OnClickStartGameButtonListener mlistener;
	// private ArcProgressTimer mtimer;
	// private XeengText mtitleBtnTxt;
	// private XeengText mdidReadyTxt;

	private Sprite glowBg;
	// private TiledSprite mbgs;

	private BaseGrowButton startButton;
	private BaseGrowButton readyButton;

	public StartGameButton(float _x, float _y, ITiledTextureRegion _bgTR,
			OnClickStartGameButtonListener _listener) {

		super(_x, _y);
		mlistener = _listener;
		// default enable

		glowBg = new Sprite(0, 0, BaseXeengGame.imgGlowLightTR,
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());

		if (BaseXeengGame.SCREEN_RATIO > 1.5f) {
			glowBg.setSize(BaseXeengGame.imgGlowLightTR.getWidth(),
					BaseXeengGame.imgGlowLightTR.getHeight());
		} else {
			glowBg.setSize(BaseXeengGame.imgGlowLightTR.getWidth(),
					BaseXeengGame.imgGlowLightTR.getHeight());
		}

		attachChild(glowBg);

		// mbgs = new TiledSprite(0, 0, _bgTR,
		// BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		// attachChild(mbgs);

		startButton = new BaseGrowButton(0, 0, _bgTR.getTextureRegion(0)) {

			@Override
			public void onClick() {
				mlistener.onClick();
			}
		};
		readyButton = new BaseGrowButton(0, 0, _bgTR.getTextureRegion(1)) {

			@Override
			public void onClick() {
				mlistener.onClick();
			}
		};

		attachChild(startButton);
		attachChild(readyButton);

		setSize(glowBg.getWidth(), glowBg.getHeight());

		glowBg.setPosition(getWidth() / 2, getHeight() / 2);
		startButton.setSize(99, 63);
		startButton.setPosition(getWidth() / 2, getHeight() / 2);

		readyButton.setSize(99, 63);
		readyButton.setPosition(getWidth() / 2, getHeight() / 2);
	}

	@Override
	public void setSize(float pWidth, float pHeight) {
		super.setSize(pWidth, pHeight);
	}

	public void setEnabled(boolean isEnabled) {
		// mIsEnabled = isEnabled;

		startButton.mIsEnabled = false;
		readyButton.mIsEnabled = false;
	}

	public StartGameState getState() {
		return state;
	}

	public void hide(boolean animated) {

		if (!isVisible()) {
			return;
		}

		setVisible(false);
		setEnabled(false);
	}

	public void show(boolean animated) {
		setIgnoreUpdate(false);
		if (isVisible()) {
			return;
		}

		changeState(getState());
	}

	public void changeState(StartGameState state) {
		this.state = state;
		switch (this.state) {
		case START_GAME:
			startButton.mIsEnabled = true;
			startButton.setVisible(true);

			readyButton.mIsEnabled = false;
			readyButton.setVisible(false);

			super.setVisible(true);

			glowBg.registerEntityModifier(new LoopEntityModifier(
					new SequenceEntityModifier(new FadeInModifier(0.75f),
							new FadeOutModifier(0.75f))));
			break;

		case READY_GAME:
			startButton.mIsEnabled = false;
			startButton.setVisible(false);

			readyButton.mIsEnabled = true;
			readyButton.setVisible(true);

			super.setVisible(true);

			glowBg.registerEntityModifier(new LoopEntityModifier(
					new SequenceEntityModifier(new FadeInModifier(0.75f),
							new FadeOutModifier(0.75f))));
			break;
		case DID_READY_GAME:
		case HIDDEN:
			setVisible(false);
			setEnabled(false);
			break;
		default:
			break;
		}
	}

	public void setTimeoutListener(OnProgressTimerListener pTimerListener) {

		hide(false);
		// mtimer.setOnProgressTimerListener(pTimerListener);
	}

	@Override
	public void setVisible(boolean pVisible) {
		super.setVisible(pVisible);

		startButton.setVisible(pVisible);
		readyButton.setVisible(pVisible);

		if (pVisible) {
			glowBg.registerEntityModifier(new LoopEntityModifier(
					new SequenceEntityModifier(new FadeInModifier(0.75f),
							new FadeOutModifier(0.75f))));
		} else {
			glowBg.clearEntityModifiers();
		}
	}

	@Override
	public void setIgnoreUpdate(boolean pIgnoreUpdate) {

		for (int i = 0; i < getChildCount(); i++) {

			getChildByIndex(i).setIgnoreUpdate(pIgnoreUpdate);
		}
		super.setIgnoreUpdate(pIgnoreUpdate);
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
		startButton.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
				pTouchAreaLocalY);
		readyButton.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
				pTouchAreaLocalY);
		return true;
	}
}
