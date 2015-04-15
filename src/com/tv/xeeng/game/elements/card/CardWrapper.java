package com.tv.xeeng.game.elements.card;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import java.util.Vector;

public class CardWrapper {

	protected VertexBufferObjectManager mVertexBufferObjectManager;
	protected ITiledTextureRegion mCardTTR;
	protected float mX, mY;
	protected Scene mParent;
	protected Vector<Entity> mChildList;
	protected float mOffsetLocalX, mOffsetLocalY;
	protected float mWidth, mHeight;
	protected float mAnchorCenterX, mAnchorCenterY;
	protected int mZIndex;
	protected boolean isAttached;
	protected int mCurrentZIndex;

	public CardWrapper(float pX, float pY, ITiledTextureRegion pCardTTR,
			VertexBufferObjectManager pVertexBufferObjectManager, Scene pParent) {

		mX = pX;
		mY = pY;
		mCardTTR = pCardTTR;
		mVertexBufferObjectManager = pVertexBufferObjectManager;
		mParent = pParent;
		mChildList = new Vector<Entity>();
		mAnchorCenterX = 0.5f;
		mAnchorCenterY = 0.5f;
		mWidth = 0;
		mHeight = 0;
		updateOffsetLocal();
		isAttached = false;
	}

	public void attachChild(Entity pChild) {

		if (mParent != null) {

			mParent.attachChild(pChild);
			pChild.setZIndex(mCurrentZIndex);
			mCurrentZIndex++;
			mChildList.add(pChild);
		}
	}

	public void setSize(float pWidth, float pHeight) {

		mWidth = pWidth;
		mHeight = pHeight;
		updateOffsetLocal();
	}

	public void setWidth(float pWidth) {

		mWidth = pWidth;
		updateOffsetLocal();
	}

	public void setHeight(float pHeight) {

		mHeight = pHeight;
		updateOffsetLocal();
	}

	public void setAnchorCenter(float pAnchorCenterX, float pAnchorCenterY) {

		mAnchorCenterX = pAnchorCenterX;
		mAnchorCenterY = pAnchorCenterY;
		updateOffsetLocal();
	}

	public void setAnchorCenterX(float pAnchorCenterX) {

		mAnchorCenterX = pAnchorCenterX;
		updateOffsetLocal();
	}

	public void setAnchorCenterY(float pAnchorCenterY) {

		mAnchorCenterY = pAnchorCenterY;
		updateOffsetLocal();
	}

	public void setVisible(boolean pVisible) {

		for (Entity child : mChildList) {

			child.setVisible(pVisible);
		}
	}

	public void setIgnoreUpdate(boolean pIgnoreUpdate) {

		for (Entity child : mChildList) {

			child.setIgnoreUpdate(pIgnoreUpdate);
		}
	}

	public void setZIndex(int pZIndex) {

		mZIndex = pZIndex;
		mCurrentZIndex = mZIndex;
		for (Entity child : mChildList) {

			child.setZIndex(mCurrentZIndex);
			mCurrentZIndex++;
		}
	}

	public float getX() {
		return mX;
	}

	public void setX(float x) {
		mX = x;
	}

	public float getY() {
		return mY;
	}

	public void setY(float y) {
		mY = y;
	}

	public void registerTouchArea() {

	}

	public boolean hasParent() {

		return isAttached;
	}

	protected void updateChildPosition() {

	}

	private void updateOffsetLocal() {

		mOffsetLocalX = mX - mAnchorCenterX * mWidth;
		mOffsetLocalY = mY - mAnchorCenterY * mHeight;
		updateChildPosition();
	}
}
