package com.tv.xeeng.game.elements.card;

import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.game.elements.common.XeengText;
import com.tv.xeeng.manager.Logger;
import org.andengine.entity.IEntity;
import org.andengine.entity.clip.ClipEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.util.modifier.IModifier;

import java.util.Vector;

public class BannerBoxSprite extends ClipEntity {

	public interface OnBannerListener {

		public void onComplete(String bannerStr);
	}

	private XeengText mText;
	private Vector<String> mStringList;
	private boolean isRunning;
	private OnBannerListener mListener;

	public BannerBoxSprite(float pX, float pY, float pWidth, float pHeight) {

		super(pX, pY, pWidth, pHeight);
		isRunning = false;
		mStringList = new Vector<String>();
		Rectangle bg = new Rectangle(pWidth / 2, pHeight / 2, pWidth, pHeight,
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		bg.setColor(0, 0, 0, 0.4f);
		attachChild(bg);

		mText = new XeengText(0, pHeight / 2, BaseXeengGame.smallRegularFont,
				"", 300, BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		mText.setAnchorCenterX(0);
		attachChild(mText);
	}

	public void updateText(String pString) {

		setIgnoreUpdate(false);
		setVisible(true);
		mStringList.add(pString);
		if (!isRunning) {

			start();
		}
	}

	public void reset() {

		isRunning = false;
		mStringList.clear();
		setIgnoreUpdate(true);
		setVisible(false);
	}

	private void start() {

		if (mStringList.size() > 0) {

			isRunning = true;
			String runningText = null;
			runningText = mStringList.firstElement();

			if (runningText != null) {

				Logger.getInstance()
						.info(this, "START ANNOUCE: " + runningText);
				mStringList.remove(runningText);
				mText.setText(runningText);
				mText.registerEntityModifier(new MoveXModifier(12, mText
						.getBiengWidth(), -mText.getBiengWidth(),
						new IEntityModifier.IEntityModifierListener() {

							@Override
							public void onModifierStarted(
									IModifier<IEntity> arg0, IEntity arg1) {
							}

							@Override
							public void onModifierFinished(
									IModifier<IEntity> arg0, IEntity arg1) {

								if (mListener != null) {

									mListener.onComplete(mText.getText()
											.toString());
								}
								start();
							}
						}));
			} else {

				isRunning = false;
			}
		} else {

			isRunning = false;
			setVisible(false);
			setIgnoreUpdate(true);
		}
	}

	public void setBannerListener(OnBannerListener pBannerListener) {

		mListener = pBannerListener;
	}

	@Override
	public void setIgnoreUpdate(boolean pIgnoreUpdate) {

		for (int i = 0; i < getChildCount(); i++) {

			getChildByIndex(i).setIgnoreUpdate(pIgnoreUpdate);
		}
		super.setIgnoreUpdate(pIgnoreUpdate);
	}

	@Override
	public void setVisible(boolean pVisible) {

		for (int i = 0; i < getChildCount(); i++) {

			getChildByIndex(i).setVisible(pVisible);
		}
		super.setVisible(pVisible);
	}
}
