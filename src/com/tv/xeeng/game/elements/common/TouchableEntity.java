package com.tv.xeeng.game.elements.common;

import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class TouchableEntity extends Entity {

	protected boolean mIsEnabled;
	private boolean mTouchStartedOnThis;
	private boolean mIsTouched;
	private boolean mIsClicked;
	private OnClickListener mListener;
	protected VertexBufferObjectManager mVertexBufferObjectManager;
	protected Rectangle mDebugRect;

	public TouchableEntity(float pX, float pY,
			VertexBufferObjectManager pVertexBufferObjectManager) {

		super(pX, pY);
		mVertexBufferObjectManager = pVertexBufferObjectManager;
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
				handleTouchEvent();
			}
			return true;
		}

		return false;
	}

	protected void handleTouchEvent() {

		if (mListener != null) {

			mListener.onClick(this);
		}
	}

	public boolean isIsEnabled() {
		return mIsEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		mIsEnabled = isEnabled;
	}

	public void setOnClickListener(OnClickListener pListener) {

		mListener = pListener;
	}

	@Override
	public void setSize(float pWidth, float pHeight) {

		super.setSize(pWidth, pHeight);
		if (mDebugRect != null) {

			mDebugRect.setPosition(pWidth / 2, pHeight / 2);
			mDebugRect.setSize(pWidth, pHeight);
		}
	}

	protected void debugDraw() {

		mDebugRect = new Rectangle(getWidth() / 2, getHeight() / 2, getWidth(),
				getHeight(), mVertexBufferObjectManager);
		mDebugRect.setColor(255, 255, 0, 150);
		this.attachChild(mDebugRect);
	}

	public interface OnClickListener {

		public void onClick(TouchableEntity pTouchableEntity);
	}
}
