package com.tv.xeeng.game.hud;

import android.util.Log;
import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.game.elements.common.ActionButton;
import com.tv.xeeng.game.elements.common.TouchableRectangle;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import java.util.ArrayList;

public abstract class BaseEmotionDialog extends BaseGameDialog {

	private final int EMOTION_SIZE = 30;

	private ArrayList<EmotionSprite> mEmotionSpriteList;
	private TouchableRectangle mOverlayRect;
	private Sprite mBackgroundSprite;
	private ActionButton closeButton;

	public BaseEmotionDialog(Scene scene, Camera camera) {
		super(scene, camera);
		setTouchAreaBindingOnActionMoveEnabled(true);
		mOverlayRect = new TouchableRectangle(camera.getWidth() / 2,
				camera.getHeight() / 2, camera.getWidth()
						- BaseXeengGame.MARGIN_HORIZONTAL * 2 - 10,
				camera.getHeight() - BaseXeengGame.MARGIN_VERTICAL * 2,
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		mOverlayRect.setColor(0f, 0f, 0f, 0f);
		mOverlayRect
				.setOnTouchListener(new TouchableRectangle.OnTouchableRectangleListener() {

					@Override
					public void onTouched(float pTouchAreaLocalX,
							float pTouchAreaLocalY) {

						if (!mBackgroundSprite.contains(pTouchAreaLocalX,
								pTouchAreaLocalY)) {
							BaseXeengGame.INSTANCE
									.runOnUpdateThread(new Runnable() {
										public void run() {
											BaseEmotionDialog.this.detachSelf();
										}
									});
							// dispose();
						}
					}
				});
		attachChild(mOverlayRect);
		registerTouchArea(mOverlayRect);

		mBackgroundSprite = new Sprite(mOverlayRect.getWidth() / 2,
				mOverlayRect.getHeight() / 2, mOverlayRect.getWidth(),
				mOverlayRect.getHeight() / 2 - 40, BaseXeengGame.bgEmotionTR,
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		mBackgroundSprite.setPosition(mOverlayRect.getWidth() / 2,
				30 + mBackgroundSprite.getHeight() / 2);
		mOverlayRect.attachChild(mBackgroundSprite);

		mEmotionSpriteList = new ArrayList<EmotionSprite>();
		EmotionSprite emotionSprite;
		for (int i = 0; i < BaseXeengGame.INSTANCE.emotionKeyList.size(); i++) {

			float x = ((mBackgroundSprite.getWidth() - 30) * (((i % 9) * 2) + 1)) / 18;
			float y = mBackgroundSprite.getHeight() / 6 * (5 - (i / 9) * 2);
			emotionSprite = new EmotionSprite(x, y, BaseXeengGame.emotionTTR,
					BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
			emotionSprite.setSize(EMOTION_SIZE, EMOTION_SIZE);
			mEmotionSpriteList.add(emotionSprite);
			emotionSprite.update(BaseXeengGame.INSTANCE.emotionKeyList.get(i));
			mBackgroundSprite.attachChild(emotionSprite);
			registerTouchArea(emotionSprite);
		}

		closeButton = new ActionButton(0, 0, BaseXeengGame.closeBtnTR) {

			@Override
			public void onClick() {
				BaseXeengGame.INSTANCE.runOnUpdateThread(new Runnable() {
					public void run() {
						BaseEmotionDialog.this.detachSelf();
					}
				});
			}
		};

		closeButton
				.setPosition(
						mBackgroundSprite.getWidth() - closeButton.getWidth()
								/ 2 - 5, mBackgroundSprite.getHeight()
								- closeButton.getHeight() / 2 - 5);

		mBackgroundSprite.attachChild(closeButton);

		registerTouchArea(closeButton);
	}

	public abstract void exposeEmotion(String pSignal);

	private class EmotionSprite extends TiledSprite {

		private String mSignalStr;

		EmotionSprite(float pX, float pY,
				ITiledTextureRegion pTiledTextureRegion,
				VertexBufferObjectManager pVertexBufferObjectManager) {
			super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		}

		void update(String pSignal) {

			mSignalStr = pSignal;
			Integer val = BaseXeengGame.INSTANCE.emotionSignalMap
					.get(mSignalStr);
			if (val != null) {
				setCurrentTileIndex(val.intValue());
				Log.e("checking ", "value: " + val.intValue());
			}
		}

		@Override
		public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
				float pTouchAreaLocalX, float pTouchAreaLocalY) {

			exposeEmotion(mSignalStr);
			return true;
		}
	}
}
