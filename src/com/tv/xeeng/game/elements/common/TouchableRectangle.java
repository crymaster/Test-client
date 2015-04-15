package com.tv.xeeng.game.elements.common;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class TouchableRectangle extends Rectangle {

	public interface OnTouchableRectangleListener {

		public void onTouched(float pTouchAreaLocalX, float pTouchAreaLocalY);
	}

	public OnTouchableRectangleListener mOnTouchableRectangleListener;

	public TouchableRectangle(float pX, float pY, float pWidth, float pHeight,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);

		mIsEnabled = true;
		setColor(0.0f, 0.0f, 0.0f, 0.7f);
	}

	boolean mIsTouched, mTouchStartedOnThis, mIsEnabled;

	public void setIsEnabled(boolean isEnabled) {
		mIsEnabled = isEnabled;
	}

	public void setOnTouchListener(
			OnTouchableRectangleListener pOnTouchableRectangleListener) {

		mOnTouchableRectangleListener = pOnTouchableRectangleListener;
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {

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
			mTouchStartedOnThis = false;
			if (mOnTouchableRectangleListener != null) {

				mOnTouchableRectangleListener.onTouched(pTouchAreaLocalX,
						pTouchAreaLocalY);
			}
			return true;
		}
		return true;
	}
}
