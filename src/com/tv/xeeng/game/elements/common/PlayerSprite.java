package com.tv.xeeng.game.elements.common;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.align.HorizontalAlign;

import android.util.Log;

import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.game.elements.common.ProgressTimer.OnProgressTimerListener;
import com.tv.xeeng.game.elements.common.ProgressTimer.ProgressState;

public class PlayerSprite extends Entity {

	public enum PlayerLocation {
		TOP_LEFT(11), TOP(0), TOP_RIGHT(1), RIGHT_TOP(2), RIGHT(3), RIGHT_BOTTOM(
				4), BOTTOM_RIGHT(5), BOTTOM(6), BOTTOM_LEFT(7), LEFT_BOTTOM(8), LEFT(
				9), LEFT_TOP(10);

		private int value;

		PlayerLocation(int value) {
			this.value = value;
		}

		public int toValue() {
			return value;
		}
	}

	private final float WIDTH = 50;
	private final float HEIGHT = 73;
	private final float CENTER_X = WIDTH / 2;
	private final float CENTER_Y = HEIGHT / 2;
	private final float AVATAR_BOTTOM = 11.5f;
	private final float AVATAR_TOP = 50f + AVATAR_BOTTOM;

	private final int Z_BG = 5;
	private final int Z_AVATAR = 10;
	private final int Z_INVITE = 20;
	private final int Z_TIMER = 8;
	private final int Z_TEXT = 40;
	private final int Z_TABLE_MASTER = 50;

	private TiledSprite vipIndicator;
	private Sprite sBackground;
	private Sprite iconLeavingGame;
	private Sprite iconReady;

	private AvatarSprite defaultAvatarSprite;
	private AvatarSprite userAvatarSprite;

	private XeengText txtName;
	private XeengText txtCash;

	public Sprite sIconTableMaster;

	private BaseGrowButton btnInvite;
	private ProgressTimer mTimerSprite;
	private int mTimeOut = 20;

	private long id;

	private OnClickPlayerSpriteListener listener;

	public PlayerSprite(float pX, float pY,
			OnClickPlayerSpriteListener _listener) {
		super(pX, pY);

		// XamPlayer

		listener = _listener;
		setSize(WIDTH, HEIGHT);

		VertexBufferObjectManager vbom = BaseXeengGame.INSTANCE
				.getVertexBufferObjectManager();

		vipIndicator = new TiledSprite(CENTER_X, CENTER_Y, WIDTH + 15,
				HEIGHT + 15, BaseXeengGame.vipIndicatorTR, vbom);
		vipIndicator.setCurrentTileIndex(0);
		vipIndicator.setZIndex(Z_BG - 1);
		this.attachChild(vipIndicator);

		sBackground = new Sprite(CENTER_X, CENTER_Y, BaseXeengGame.avatarBgTR,
				vbom);
		sBackground.setZIndex(Z_BG);
		this.attachChild(sBackground);

		txtCash = new XeengText(CENTER_X, AVATAR_BOTTOM / 2,
				BaseXeengGame.smallRegularFont, "0123456789.", 20,
				new TextOptions(HorizontalAlign.CENTER), vbom);
		txtCash.setZIndex(Z_TEXT);
		txtCash.setScale(0.8f);
		txtCash.setColor(android.graphics.Color.rgb(236, 209, 60));
		this.attachChild(txtCash);

		defaultAvatarSprite = new AvatarSprite(CENTER_X, CENTER_Y,
				BaseXeengGame.avatarTR, vbom);
		defaultAvatarSprite.setZIndex(Z_AVATAR);
		this.attachChild(defaultAvatarSprite);

		txtName = new XeengText(CENTER_X, AVATAR_TOP + AVATAR_BOTTOM / 2,
				BaseXeengGame.smallRegularFont, "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
				new TextOptions(HorizontalAlign.CENTER), vbom);
		txtName.setZIndex(Z_TEXT);
		txtName.setScale(0.8f);
		this.attachChild(txtName);

		btnInvite = new BaseGrowButton(CENTER_X, CENTER_Y,
				BaseXeengGame.btnInviteTR) {

			@Override
			public void onClick() {

				listener.onClickInvite();
			}
		};
		btnInvite.setSize(26, 26);
		btnInvite.setZIndex(Z_INVITE);
		this.attachChild(btnInvite);

		iconLeavingGame = new Sprite(0, 0, BaseXeengGame.iconLeavingGameTR,
				vbom);
		iconLeavingGame.setSize(25, 25);
		iconLeavingGame.setPosition(iconLeavingGame.getWidth() / 2 - 6,
				iconLeavingGame.getHeight() / 2 + AVATAR_BOTTOM + 3);
		iconLeavingGame.setVisible(false);
		iconLeavingGame.setZIndex(Z_TABLE_MASTER + 10);
		attachChild(iconLeavingGame);

		iconReady = new Sprite(0, 0, BaseXeengGame.iconReadyTR, vbom);
		iconReady.setSize(25, 25);
		iconReady.setPosition(iconLeavingGame.getWidth() / 2 - 6,
				iconLeavingGame.getHeight() / 2 + AVATAR_BOTTOM + 3);
		iconReady.setVisible(false);
		iconReady.setZIndex(Z_TABLE_MASTER + 11);
		attachChild(iconReady);

		BaseXeengGame.INSTANCE.getCurrentScene().registerTouchArea(btnInvite);
		BaseXeengGame.INSTANCE.getCurrentScene().registerTouchArea(
				defaultAvatarSprite);
		sortChildren();

		// default show inviting button
		showInviteButton();
	}

	public void settle(long playerId, final String playerName, final long cash) {

		txtName.setText(CommonUtils.formPlayerName(playerName));
		txtCash.setText(CommonUtils.formatCash(cash));
		id = playerId;

		sBackground.setVisible(true);
		if (userAvatarSprite == null) {
			defaultAvatarSprite.setVisible(true);
		} else {
			defaultAvatarSprite.setVisible(false);
			userAvatarSprite.setVisible(true);
		}
		txtName.setVisible(true);
		txtCash.setVisible(true);
		// bgNameInfo.setVisible(true);
		// bgCashInfo.setVisible(true);
		btnInvite.setVisible(false);

		btnInvite.mIsEnabled = false;
		defaultAvatarSprite.setIsEnabled(true);
	}

	public void hideInviteButton() {
		btnInvite.setVisible(false);
		btnInvite.mIsEnabled = false;
	}

	public void showInviteButton() {
		sBackground.setVisible(false);
		defaultAvatarSprite.setVisible(false);
		if (userAvatarSprite != null) {
			userAvatarSprite.setVisible(false);
			userAvatarSprite.setIsEnabled(false);
		}
		txtName.setVisible(false);
		txtCash.setVisible(false);

		if (mTimerSprite != null) {
			mTimerSprite.setVisible(false);
		}

		if (sIconTableMaster != null) {
			sIconTableMaster.setVisible(false);
		}
		btnInvite.setVisible(true);

		btnInvite.mIsEnabled = true;
		defaultAvatarSprite.setIsEnabled(false);

		vipIndicator.setVisible(false);
	}

	public void hide() {

		showInviteButton();
		btnInvite.setVisible(false);
		btnInvite.mIsEnabled = false;
		defaultAvatarSprite.setIsEnabled(false);
	}

	public void updateCash(long cash, boolean isAnimated) {

		if (isAnimated) {

			txtCash.setText(CommonUtils.formatCash(cash));
		} else {

			txtCash.setText(CommonUtils.formatCash(cash));
		}
	}

	public void setShowTableMaster(boolean showed) {

		if (sIconTableMaster == null) {

			sIconTableMaster = new Sprite(WIDTH - 10, AVATAR_BOTTOM + 10,
					BaseXeengGame.INSTANCE.iconTableMasterTR,
					BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
			attachChild(sIconTableMaster);
			sIconTableMaster.setZIndex(Z_TABLE_MASTER);
			sortChildren();
			sIconTableMaster.setVisible(false);
		}

		sIconTableMaster.setVisible(showed);
	}

	public void startTimer() {
		startTimer(mTimeOut, 0, null);
	}

	public void startTimer(int timeOut) {
		startTimer(timeOut, 0, null);
	}

	public void startTimer(int timeOut, int lowTimeTrigger,
			OnProgressTimerListener listener) {
		if (mTimerSprite == null) {

			mTimerSprite = new ProgressTimer(CENTER_X, AVATAR_TOP
					+ AVATAR_BOTTOM / 2, (int) WIDTH,
					(int) Math.ceil(AVATAR_BOTTOM), mTimeOut,
					BaseXeengGame.INSTANCE);

			attachChild(mTimerSprite);
			mTimerSprite.setZIndex(Z_TIMER);
			sortChildren();
		}
		if (mTimerSprite.mstate == ProgressState.RUNNING) {
			mTimerSprite.stop();
		}
		mTimerSprite.setTimeOut(timeOut);
		mTimerSprite.setLowTimeTrigger(lowTimeTrigger);
		mTimerSprite.setOnProgressTimerListener(listener);
		mTimerSprite.start();
	}

	public void stopTimer() {
		if (mTimerSprite != null) {
			mTimerSprite.stop();
		}
	}

	public void setTimeOut(int timeOut) {
		mTimeOut = timeOut;
		if (mTimerSprite != null) {

			mTimerSprite.setTimeOut(mTimeOut);
		}
	}

	public void setListener(OnClickPlayerSpriteListener listener) {
		this.listener = listener;
	}

	public void setEnable(boolean pEnable) {

		defaultAvatarSprite.mIsEnabled = pEnable;
		btnInvite.mIsEnabled = pEnable;
	}

	public void setIconLeavingVisible(boolean visible) {
		iconLeavingGame.setVisible(visible);
	}

	private class AvatarSprite extends Sprite {
		public AvatarSprite(float pX, float pY, float pWidth, float pHeight,
				ITextureRegion pTextureRegion,
				VertexBufferObjectManager pVertexBufferObjectManager) {
			super(pX, pY, pWidth, pHeight, pTextureRegion,
					pVertexBufferObjectManager);
		}

		public AvatarSprite(float pX, float pY, ITextureRegion pTextureRegion,
				VertexBufferObjectManager pVertexBufferObjectManager) {
			super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		}

		boolean mIsTouched, mTouchStartedOnThis, mIsEnabled;

		private void setIsEnabled(boolean isEnabled) {
			mIsEnabled = isEnabled;
		}

		@Override
		public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
				float pTouchAreaLocalX, float pTouchAreaLocalY) {
			if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
				Log.e("checking player", "id: " + id);
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
			} else if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP
					&& mIsTouched && mTouchStartedOnThis) {
				mIsTouched = false;
				mTouchStartedOnThis = false;
				listener.onClickPlayer(id);
			}

			if (mIsEnabled) {

				return true;
			} else {

				return false;
			}
		}
	}

	public void setUserAvatarSprite(ITextureRegion avatarTR,
			VertexBufferObjectManager vbom) {
		if (userAvatarSprite != null) {
			userAvatarSprite.detachSelf();
			userAvatarSprite = null;
		}
		this.defaultAvatarSprite.setVisible(false);

		userAvatarSprite = new AvatarSprite(CENTER_X, CENTER_Y, 49, 49,
				avatarTR, vbom);
		userAvatarSprite.setZIndex(Z_AVATAR);

		this.attachChild(userAvatarSprite);

		sortChildren();
	}

	public void unSetUserAvatarSprite() {
		if (userAvatarSprite != null) {
			userAvatarSprite.detachSelf();
			userAvatarSprite = null;
		}

		this.defaultAvatarSprite.setVisible(true);
	}

	public void setIconReadyVisible(boolean visible) {
		iconReady.setVisible(visible);
	}

	public void setVipIndicator(int vipId) {
		if (vipId < 1 || vipId > 10) {
			setVipIndicatorVisible(false);
		} else {
			setVipIndicatorVisible(true);
			vipIndicator.setCurrentTileIndex(vipId - 1);
		}
	}

	public void setVipIndicatorVisible(boolean isVisible) {
		vipIndicator.setVisible(isVisible);
	}
}
