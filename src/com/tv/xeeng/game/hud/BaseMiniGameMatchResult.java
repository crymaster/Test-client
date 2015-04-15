package com.tv.xeeng.game.hud;

import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.game.BaseMiniGameActivity;
import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.game.elements.common.ActionButton;
import com.tv.xeeng.game.elements.common.TouchableRectangle;
import com.tv.xeeng.game.elements.common.XeengText;
import com.tv.xeeng.game.elements.common.XeengTextOptions;
import com.tv.xeeng.gamedata.entity.MiniGamePlayer;
import com.tv.xeeng.manager.Logger;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.clip.ClipEntity;
import org.andengine.entity.modifier.*;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackIn;
import org.andengine.util.modifier.ease.EaseBackOut;

import java.util.ArrayList;

public abstract class BaseMiniGameMatchResult extends BaseGameDialog {

	private float yFirstRow;
	private float spacingRow;

	private TouchableRectangle mOverlayRect;
	private Sprite mDialogBg;
	private ActionButton btnOk;

	private ArrayList<ResultRow> rowList;

	private Text title;

	public BaseMiniGameMatchResult(Camera camera) {
		super(BaseXeengGame.INSTANCE.getCurrentScene(), camera);
		setTouchAreaBindingOnActionMoveEnabled(true);
		setTouchAreaBindingOnActionDownEnabled(true);
		mOverlayRect = new TouchableRectangle(
				BaseXeengGame.DESIGN_WINDOW_WIDTH_PIXELS / 2,
				BaseXeengGame.DESIGN_WINDOW_HEIGHT_PIXELS / 2,
				BaseXeengGame.DESIGN_WINDOW_WIDTH_PIXELS,
				BaseXeengGame.DESIGN_WINDOW_HEIGHT_PIXELS,
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		attachChild(mOverlayRect);
		registerTouchArea(mOverlayRect);

		mDialogBg = new Sprite(BaseXeengGame.CENTER_X,
				BaseXeengGame.DESIGN_WINDOW_HEIGHT_PIXELS / 2,
				BaseMiniGameActivity.bg_scoreRG,
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		attachChild(mDialogBg);

		rowList = new ArrayList<BaseMiniGameMatchResult.ResultRow>();
		yFirstRow = mDialogBg.getHeight() - 55;
		spacingRow = 25;
		ResultRow arow = null;
		for (int i = 0; i < 4; i++) {
			arow = new ResultRow(mDialogBg.getWidth() / 2, yFirstRow
					- spacingRow * i, i);
			mDialogBg.attachChild(arow);
			rowList.add(arow);
		}

		showAllElement(false);

		title = new Text(mDialogBg.getWidth() / 2, mDialogBg.getHeight() - 20,
				BaseXeengGame.mediumRegularFont, "Kết quả", new TextOptions(
						HorizontalAlign.CENTER),
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());

		mDialogBg.attachChild(title);

		btnOk = new ActionButton(0, 0, BaseMiniGameActivity.okBtnBgTR,
				BaseXeengGame.mediumRegularFont, "Tiếp tục") {

			@Override
			public void onClick() {
				hide(true);
				closeByClick();
			}
		};

		btnOk.setTextColor(Color.BLACK);
		btnOk.setPosition(mDialogBg.getWidth() / 2, btnOk.getHeight() / 2 + 10);

		mDialogBg.attachChild(btnOk);
		registerTouchArea(btnOk);
	}

	public abstract void closeByClick();

	public abstract void closeByTimeOut();

	public void showResult(
			ArrayList<? extends MiniGamePlayer> pRankingPlayerList) {

		showAllElement(true);
		mOverlayRect.setAlpha(0);
		mOverlayRect.registerEntityModifier(new AlphaModifier(0.3f, 0, 0.6f));

		mDialogBg.setVisible(false);
		mDialogBg.registerEntityModifier(new SequenceEntityModifier(
				new DelayModifier(0.3f), new ScaleModifier(0.3f, 0.4f, 1,
						new IEntityModifier.IEntityModifierListener() {

							@Override
							public void onModifierStarted(
									IModifier<IEntity> pModifier, IEntity pItem) {

								mDialogBg.setVisible(true);
							}

							@Override
							public void onModifierFinished(
									IModifier<IEntity> pModifier, IEntity pItem) {
							}
						}, EaseBackOut.getInstance())));

		for (ResultRow row : rowList) {

			row.setVisible(false);
		}

		ResultRow arow;
		int i = 0;
		for (MiniGamePlayer itemData : pRankingPlayerList) {

			arow = rowList.get(i);
			arow.update(itemData.character, itemData.rewardCash,
					itemData.getScore(), itemData.getMaximumScores());
			arow.setVisible(false);
			arow.registerEntityModifier(new DelayModifier(1.5f,
					new IEntityModifier.IEntityModifierListener() {

						@Override
						public void onModifierStarted(
								IModifier<IEntity> pModifier, IEntity pItem) {
						}

						@Override
						public void onModifierFinished(
								IModifier<IEntity> pModifier, IEntity pItem) {

							pItem.setVisible(true);
						}
					}));
			i++;
			rowList.add(arow);
		}
	}

	private void hide(boolean animated) {

		if (animated) {

			for (ResultRow arow : rowList) {

				arow.setVisible(false);
			}

			mDialogBg.registerEntityModifier(new SequenceEntityModifier(
					new DelayModifier(0.4f), new ScaleModifier(0.3f, 1, 0,
							new IEntityModifier.IEntityModifierListener() {

								@Override
								public void onModifierStarted(
										IModifier<IEntity> pModifier,
										IEntity pItem) {

								}

								@Override
								public void onModifierFinished(
										IModifier<IEntity> pModifier,
										IEntity pItem) {

									BaseXeengGame.INSTANCE
											.runOnUpdateThread(new Runnable() {
												public void run() {
													detachSelf();
												}
											});
								}
							}, EaseBackIn.getInstance())));
		} else {

			detachSelf();
		}
	}

	private void showAllElement(boolean isShowed) {

		mOverlayRect.setVisible(isShowed);
		mDialogBg.setVisible(isShowed);
		for (ResultRow arow : rowList) {
			arow.setVisible(isShowed);
		}
	}

	private class ResultRow extends Entity {

		private Sprite mRankSprite;
		private XeengText mPlayerNameTxt;
		private Sprite mProgressBg;
		private Sprite mProgressIndicator;
		private ClipEntity mMaskProgressEntity;
		XeengText mCashTxt;
		XeengText mProgressTxt;
		private ITextureRegion mProgressTR;
		private Text rankText;

		private ResultRow(float pX, float pY, int pRank) {

			super(pX, pY, BaseMiniGameActivity.bg_scoreRG.getWidth(), 20);
			mProgressTR = BaseMiniGameActivity.rankIndicator;

			ITextureRegion rankTR = pRank > 1 ? BaseMiniGameActivity.loseRankTR
					: BaseMiniGameActivity.winRankTR;
			mRankSprite = new Sprite(0, getHeight() / 2, getHeight(),
					getHeight(), rankTR,
					BaseXeengGame.INSTANCE.getVertexBufferObjectManager());

			rankText = new Text(mRankSprite.getWidth() / 2,
					mRankSprite.getHeight() / 2,
					BaseXeengGame.smallRegularFont, "" + (pRank + 1),
					BaseXeengGame.INSTANCE.getVertexBufferObjectManager());

			rankText.setColor(pRank > 1 ? Color.WHITE : Color.BLACK);

			mRankSprite.attachChild(rankText);
			mRankSprite.setPosition(mRankSprite.getWidth() / 2 + 5,
					mRankSprite.getY());
			attachChild(mRankSprite);

			mPlayerNameTxt = new XeengText(0, getHeight() / 2,
					BaseXeengGame.smallRegularFont,
					"ABCDEFGHIJKLMNOPQRSTUVWXYZ", new XeengTextOptions(
							AutoWrap.NONE, 50, HorizontalAlign.LEFT),
					BaseXeengGame.INSTANCE.getVertexBufferObjectManager());

			mPlayerNameTxt.setAnchorCenterX(0);
			mPlayerNameTxt.setX(mRankSprite.getWidth() + 10);
			attachChild(mPlayerNameTxt);

			// create cash text
			mCashTxt = new XeengText(0, getHeight() / 2,
					BaseXeengGame.smallRegularFont, "0123456789.+-", 20,
					new XeengTextOptions(HorizontalAlign.RIGHT),
					BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
			mCashTxt.setAnchorCenterX(1f);
			mCashTxt.setPosition(getWidth() - 5, mCashTxt.getY());
			mCashTxt.setColor(Color.GREEN);
			attachChild(mCashTxt);

			mProgressBg = new Sprite(180, getHeight() / 2, getWidth() * 1 / 2,
					getHeight(), BaseMiniGameActivity.rankRg,
					BaseXeengGame.INSTANCE.getVertexBufferObjectManager());

			mMaskProgressEntity = new ClipEntity(0, getHeight() / 2,
					mProgressBg.getWidth(), mProgressBg.getHeight());
			mMaskProgressEntity.setAnchorCenterX(0f);
			mProgressBg.attachChild(mMaskProgressEntity);

			mProgressIndicator = new Sprite(0, getHeight() / 2,
					mProgressBg.getWidth(), mProgressBg.getHeight(),
					mProgressTR,
					BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
			mProgressIndicator.setAnchorCenterX(0);
			mMaskProgressEntity.attachChild(mProgressIndicator);

			mProgressTxt = new XeengText(mProgressBg.getWidth() / 2,
					getHeight() / 2, BaseXeengGame.smallRegularFont,
					"0123456789%",
					BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
			mProgressBg.attachChild(mProgressTxt);

			attachChild(mProgressBg);
		}

		void update(String pName, long pCash, int pProgress, float total) {

			if (pName.length() > 12) {

				pName = pName.substring(0, 12) + "...";
			}

			mPlayerNameTxt.setText(pName);
			mCashTxt.setText(pCash >= 0 ? CommonUtils.formatRewardCash(pCash)
					: CommonUtils.formatRewardCash(pCash));
			mProgressTxt.setText((int) (pProgress * 100.0 / total) + " %");
			float percent = (float) pProgress / (float) total;
			Logger.getInstance().info(
					this,
					"PROGRESS: " + percent + " origin width: "
							+ mProgressBg.getWidth() + " width: " + percent
							* mProgressBg.getWidth());
			mMaskProgressEntity.setWidth(percent * mProgressBg.getWidth());
		}

		@Override
		public void setVisible(boolean pVisible) {

			for (int i = 0; i < getChildCount(); i++) {

				getChildByIndex(i).setVisible(pVisible);
			}
			super.setVisible(pVisible);
		}

		@Override
		public void setScale(float pScale) {

			for (int i = 0; i < getChildCount(); i++) {

				getChildByIndex(i).setScale(pScale);
			}
			super.setScale(pScale);
		}
	}
}