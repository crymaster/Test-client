package com.tv.xeeng.game.elements.card;

import com.tv.xeeng.game.BaseCardGameActivity;
import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.gamedata.entity.Card;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;
import org.andengine.util.modifier.IModifier;

public class ClickableCard extends TileCardSprite {

	public enum ClickType {

		TURN, FLIP,
	}

	private final int selectedColor = android.graphics.Color
			.parseColor("#fff5c9");
	private ClickType mType = ClickType.TURN;
	private boolean mTouchStartedOnThis;
	private boolean mIsEnabled = true;
	private boolean mIsTouched;
	private boolean mIsClicked = false;;
	private boolean mAutoTurn = true;
	private float mOriginY;
	private float mTurnY;
	private onClickCardListener mListener = null;
	private int mserverValue = -1;
	private int mnumberValue = -1;
	private int mtypeValue = -1;
	private Card mCardData;
	private TimerHandler mWrongCardTimer = new TimerHandler(1f,
			new ITimerCallback() {

				@Override
				public void onTimePassed(TimerHandler pTimerHandler) {

					mIsEnabled = true;
					setColor(Color.WHITE);
				}
			});

	public ClickableCard(ClickType pType, float pX, float pY,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {

		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		mType = pType;
		mOriginY = pY;
		mTurnY = mOriginY + 7;
		BaseXeengGame.INSTANCE.getCurrentScene().registerTouchArea(this);
	}

	public void show() {
		show(false);
	}

	public void show(boolean animated) {
		if (!animated) {
			BaseXeengGame.INSTANCE.getCurrentScene().registerTouchArea(this);
			setIgnoreUpdate(false);
			setVisible(true);
		} else {

		}
	}

	public void disappear() {

		BaseXeengGame.INSTANCE.getCurrentScene().unregisterTouchArea(this);
		setIgnoreUpdate(true);
		setVisible(false);
	}

	public void reset() {

		mIsClicked = false;
		setY(mOriginY);
		setColor(Color.WHITE);
	}

	public void turnCard() {

		mIsClicked = true;
		switch (mType) {
		case TURN:
			turn();
			break;

		case FLIP:
			flip();
			break;
		default:
			break;
		}
	}

	public void wrongCard() {

		mIsEnabled = false;
		setColor(1f, 39f / 255f, 29f / 255f, 1);
		this.clearUpdateHandlers();
		mWrongCardTimer.reset();
		this.registerUpdateHandler(mWrongCardTimer);
	}

	public void faceUp() {

		this.faceUp(false);
	}

	public void faceUp(boolean animation) {
		if (animation) {
			registerEntityModifier(new SequenceEntityModifier(
					new ScaleModifier(0.1f, 1.0f, 0.0f, 1.0f, 1.0f),
					new ScaleModifier(0.1f, 0.0f, 1.0f, 1.0f, 1.0f,
							new IEntityModifierListener() {
								@Override
								public void onModifierStarted(
										IModifier<IEntity> arg0, IEntity arg1) {
								}

								@Override
								public void onModifierFinished(
										IModifier<IEntity> arg0, IEntity arg1) {
									setCurrentTileIndex(mCardData != null ? mCardData.serverValue - 1
											: getCurrentTileIndex());
								}
							})));
		} else {
			setCurrentTileIndex(mCardData != null ? mCardData.serverValue - 1
					: getCurrentTileIndex());
		}
	}

	private void flip() {

	}

	private void turn() {

		if (mIsClicked) {

			setY(mTurnY);
			setColor(selectedColor);

		} else {

			setY(mOriginY);
			setColor(Color.WHITE);
		}
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {

		if (mIsEnabled) {

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
			} else if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP
					&& mIsTouched && mTouchStartedOnThis) {

				mIsTouched = false;
				mIsClicked = !mIsClicked;
				mTouchStartedOnThis = false;
				if (mAutoTurn) {

					switch (mType) {
					case TURN:
						turn();
						break;

					case FLIP:
						flip();
						break;

					default:
						break;
					}
				}

				if (mListener != null) {
					mListener.onClick(this);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean isIsEnabled() {
		return mIsEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		mIsEnabled = isEnabled;
	}

	public boolean isClicked() {
		return mIsClicked;
	}

	public boolean isAutoTurn() {
		return mAutoTurn;
	}

	public void setAutoTurn(boolean autoTurn) {
		mAutoTurn = autoTurn;
	}

	public void setOnClickCardListener(onClickCardListener pOnClickCardListener) {

		mListener = pOnClickCardListener;
	}

	public void setData(Card pCard) {

		mCardData = pCard;
		if (mCardData == null)
			return;
		setCurrentTileIndex(mCardData.serverValue - 1);
	}

	public void updateData(Card pCard) {

		mCardData = pCard;
	}

	public ClickType getType() {
		return mType;
	}

	public Card getCardData() {
		return mCardData;
	}

	public int getServerValue() {
		return mserverValue;
	}

	public int getNumberValue() {
		return mnumberValue;
	}

	public int getTypeValue() {
		return mtypeValue;
	}

	public float getOriginY() {
		return mOriginY;
	}

	public float getTurnY() {
		return mTurnY;
	}

	public interface onClickCardListener {

		public void onClick(ClickableCard pCardSprite);
	}
	
	@Override
	public void setPosition(float pX, float pY) {
		super.setPosition(pX, pY);
		
		mOriginY = pY;
		mTurnY = pY + 7;
	}
}
