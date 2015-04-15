package com.tv.xeeng.game.elements.player;

import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.FadeInModifier;
import org.andengine.entity.modifier.FadeOutModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.modifier.ease.EaseCubicOut;

import android.text.TextUtils;

import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.game.BaseCardGameActivity;
import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.game.elements.card.CardHolderSprite;
import com.tv.xeeng.game.elements.common.PlayerSprite.PlayerLocation;
import com.tv.xeeng.gamedata.entity.Player;

public abstract class CardGamePlayerHolder extends BasePlayerHolder {
	protected FloatText floatText; // Text thắng thua, tiền, ù, móm, ....
	protected CardHolderSprite cardHand;

	// Position parameters
	public float playingPosition[] = new float[2];
	public float showHandPosition[] = new float[2];
	public float cardHandPosition[] = new float[2];
	public float rewardPosition[] = new float[2];
	public float rewardScale = 1f;

	public CardGamePlayerHolder(PlayerLocation position) {
		super(getXForPlayerPosition(position), getYForPlayerPosition(position),
				position);

		float d = 10f;
		playingPosition[0] = defaultPosition[0];
		playingPosition[1] = defaultPosition[1];

		rewardPosition[0] = defaultPosition[0];
		rewardPosition[1] = defaultPosition[1] - 10f;

		showHandPosition[0] = defaultPosition[0];
		showHandPosition[1] = defaultPosition[1];

		cardHandPosition[0] = defaultPosition[0];
		cardHandPosition[1] = defaultPosition[1];
		switch (playerLoc) {
		case BOTTOM:
			playingPosition[0] = BaseXeengGame.LEFT + 35f;
			playingPosition[1] = BaseXeengGame.BOTTOM
					+ BaseXeengGame.BOTTOM_BAR_HEIGHT + 25f + d;
			break;
		case RIGHT:
			// playingPosition[0] += d;
			cardHandPosition[0] = (playingPosition[0] + BaseXeengGame.RIGHT)
					/ 2 + d;
			showHandPosition[0] = (playingPosition[0] + BaseXeengGame.RIGHT) / 2;
			break;
		case LEFT:
			// playingPosition[0] -= d;
			cardHandPosition[0] = (playingPosition[0] + BaseXeengGame.LEFT) / 2
					- d;
			showHandPosition[0] = (playingPosition[0] + BaseXeengGame.LEFT) / 2 - 10f;
			break;
		case TOP:
			// playingPosition[1] += d;
			showHandPosition[0] += 20f;
			cardHandPosition[0] = playingPosition[0] + 50f;
			break;
		default:
			break;
		}
	}

	/**
	 * Get the correct backCardIndex according to vip level
	 * 
	 * @return the index to use in setCurrentTile()
	 */
	public int getBackCardIndex() {
		Player playerData = getPlayerData();
		if (playerData == null) {
			return BaseCardGameActivity.BACK_CARD_INDEX;
		}
		return playerData.vipId * 2 + 52;
	}

	public abstract void onCreatePlayerSprite();

	@Override
	public void moveToPlayingMode() {
		playerSprite.setPosition(playingPosition[0], playingPosition[1]);
		playerSprite.hideInviteButton();
		super.moveToPlayingMode();
	}

	@Override
	public void moveToWaitingMode() {
		playerSprite.setPosition(defaultPosition[0], defaultPosition[1]);
		playerSprite.showInviteButton();
		super.moveToWaitingMode();
	}

	public static float getXForPlayerPosition(PlayerLocation position) {
		switch (position) {
		case TOP:
		case BOTTOM:
			return BaseXeengGame.CENTER_X;
		case TOP_LEFT:
		case BOTTOM_LEFT:
			return BaseXeengGame.CENTER_X - BaseXeengGame.TABLE_WIDTH / 4;
		case TOP_RIGHT:
		case BOTTOM_RIGHT:
			return BaseXeengGame.CENTER_X + BaseXeengGame.TABLE_WIDTH / 4;
		case RIGHT:
			return BaseXeengGame.CENTER_X + BaseXeengGame.TABLE_WIDTH / 2 + 9f;
		case RIGHT_TOP:
		case RIGHT_BOTTOM:
			return BaseXeengGame.CENTER_X + BaseXeengGame.TABLE_WIDTH / 2;
		case LEFT:
			return BaseXeengGame.CENTER_X - BaseXeengGame.TABLE_WIDTH / 2 - 9f;
		case LEFT_TOP:
		case LEFT_BOTTOM:
			return BaseXeengGame.CENTER_X - BaseXeengGame.TABLE_WIDTH / 2;
		}

		return 0;
	}

	public static float getYForPlayerPosition(PlayerLocation position) {
		switch (position) {
		case TOP:
		case TOP_RIGHT:
		case TOP_LEFT:
			return BaseXeengGame.CENTER_Y + BaseXeengGame.TABLE_HEIGHT / 2 + 9f;
		case BOTTOM:
		case BOTTOM_RIGHT:
		case BOTTOM_LEFT:
			return BaseXeengGame.CENTER_Y - BaseXeengGame.TABLE_HEIGHT / 2 - 9f;
		case RIGHT:
		case LEFT:
			return BaseXeengGame.CENTER_Y;
		case RIGHT_TOP:
		case LEFT_TOP:
			return BaseXeengGame.CENTER_Y + BaseXeengGame.TABLE_HEIGHT / 4;
		case RIGHT_BOTTOM:
		case LEFT_BOTTOM:
			return BaseXeengGame.CENTER_Y - BaseXeengGame.TABLE_HEIGHT / 4;
		}

		return 0;
	}

	public void showFloatText(ITextureRegion specialTextTR, long cash,
			boolean isVictory) {
		showFloatText(specialTextTR, cash, null, isVictory);
	}

	public void showFloatText(ITextureRegion specialTextTR, long cash,
			TimerHandler timerHandler, boolean isVictory) {
		showFloatText(specialTextTR, cash, timerHandler, 1f, isVictory);
	}

	public void showFloatText(ITextureRegion specialTextTR, long cash,
			TimerHandler timerHandler, float scale, boolean isVictory) {
		if (floatText == null) {
			floatText = new FloatText(rewardPosition[0], rewardPosition[1], 2);
			floatText.setZIndex(100);
			BaseXeengGame.INSTANCE.getCurrentScene().attachChild(floatText);
		}

		floatText.show(specialTextTR, cash, timerHandler, scale, isVictory);
	}

	public void showFloatText(long cash, boolean isVictory) {
		showFloatText(cash, null, isVictory);
	}

	public void showFloatText(long cash, TimerHandler updateHandler,
			boolean isVictory) {
		if (floatText == null) {
			floatText = new FloatText(rewardPosition[0], rewardPosition[1], 2);
			floatText.setZIndex(100);
			BaseXeengGame.INSTANCE.getCurrentScene().attachChild(floatText);
		}

		floatText.show(cash, rewardScale, updateHandler, isVictory);
	}

	public void showFloatText(String title, long cash, boolean isVictory) {
		showFloatText(title, cash, null, isVictory);
	}

	public void showFloatText(String title, long cash,
			TimerHandler updateHandler, boolean isVictory) {
		if (floatText == null) {
			floatText = new FloatText(rewardPosition[0], rewardPosition[1], 2);
			floatText.setZIndex(100);
			BaseXeengGame.INSTANCE.getCurrentScene().attachChild(floatText);
		}

		floatText.show(title, cash, rewardScale, updateHandler, isVictory);
	}

	public void hideFloatText() {
		if (floatText != null) {
			floatText.hide();
		}
	}

	public class FloatText extends Entity {
		private static final float DARK_SCALE = 1f;
		private static final float SPECIAL_TEXT_SCALE = 0.85f;

		private float normalTextScale = 1f;

		private float WIDTH;
		private float HEIGHT;
		private final int Z = 81;

		private Sprite sGlowLight;
		private Sprite sGlowDark;

		private Text txtLight;
		private Text txtDark;

		private Sprite specialText;

		private int loopCount = 2;

		FloatText(float x, float y, int loopCount) {
			this(x, y, BaseXeengGame.fontStrokeLight,
					BaseXeengGame.fontStrokeDark, loopCount);
		}

		FloatText(float x, float y, IFont fontLight, IFont fontDark,
				int loopCount) {
			super(x, y);
			this.loopCount = loopCount;

			WIDTH = BaseXeengGame.imgGlowLightTR.getWidth();
			HEIGHT = BaseXeengGame.imgGlowLightTR.getHeight();

			VertexBufferObjectManager vbom = BaseXeengGame.INSTANCE
					.getVertexBufferObjectManager();

			sGlowLight = new Sprite(0, 0, BaseXeengGame.imgGlowLightTR, vbom);
			sGlowDark = new Sprite(0, 0, BaseXeengGame.imgGlowDarkTR, vbom);

			sGlowDark.setScale(DARK_SCALE);

			txtLight = new Text(WIDTH / 2, HEIGHT / 2, fontLight, "",
					"THẮNG\n+9.012.345.678".length(), vbom);
			txtDark = new Text(WIDTH / 2, HEIGHT / 2, fontDark, "",
					"THUA\n-9.012.345.678".length(), vbom);

			if (BaseXeengGame.SCREEN_RATIO <= 1.5f) {
				normalTextScale = 0.5f;
			} else {
				normalTextScale = 0.5f;
			}

			sGlowLight.setZIndex(Z);
			sGlowDark.setZIndex(Z);
			txtLight.setZIndex(Z + 2);
			txtDark.setZIndex(Z + 2);
			txtLight.setHorizontalAlign(HorizontalAlign.CENTER);
			txtDark.setHorizontalAlign(HorizontalAlign.CENTER);

			sGlowLight.attachChild(txtLight);
			sGlowDark.attachChild(txtDark);

			attachChild(sGlowLight);
			attachChild(sGlowDark);
			hide();
		}

		void show(ITextureRegion specialTextTR, long bounty,
				TimerHandler timerHandler, float scale, boolean isVictory) {
			show("\t", bounty, rewardScale, timerHandler, isVictory);

			attachSpecialText(specialTextTR, scale, isVictory, 0, 18);
		}

		void show(ITextureRegion specialTextTR, TimerHandler timerHandler,
				float scale, boolean isVictory) {
			show("", rewardScale, timerHandler, isVictory);

			attachSpecialText(specialTextTR, scale, isVictory, 0, 0);
		}

		void show(String title, boolean isVictory) {
			show(title, rewardScale, null, isVictory);
		}

		void show(String title, TimerHandler updateHandler, boolean isVictory) {
			show(title, rewardScale, updateHandler, isVictory);
		}

		void show(String title, float scale, final TimerHandler updateHanlder,
				boolean isVictory) {
			if (specialText != null) {
				specialText.detachSelf();
				specialText = null;
			}
			float start = HEIGHT / 2 - 20;
			float end = HEIGHT / 2;
			txtLight.setY(start);
			txtDark.setY(start);

			if (isVictory) {

				disable(sGlowDark);
				txtLight.setText(title);

				txtLight.setScale(scale * normalTextScale);
				enable(sGlowLight);
				txtLight.clearEntityModifiers();
				txtLight.registerEntityModifier(new SequenceEntityModifier(
						new DelayModifier(0), new MoveYModifier(.8f, start,
								end, EaseCubicOut.getInstance())));

				sGlowLight.registerEntityModifier(new LoopEntityModifier(
						new SequenceEntityModifier(new FadeOutModifier(0.75f),
								new FadeInModifier(0.75f)), loopCount));
			} else {

				disable(sGlowLight);
				txtDark.setText(title);

				txtDark.setScale(scale * normalTextScale / DARK_SCALE);
				enable(sGlowDark);
				txtDark.clearEntityModifiers();
				txtDark.registerEntityModifier(new SequenceEntityModifier(
						new DelayModifier(0), new MoveYModifier(.8f, start,
								end, EaseCubicOut.getInstance())));

				sGlowDark.registerEntityModifier(new LoopEntityModifier(
						new SequenceEntityModifier(new FadeOutModifier(0.75f),
								new FadeInModifier(0.75f)), loopCount));
			}

			clearUpdateHandlers();
			if (updateHanlder != null) {
				registerUpdateHandler(updateHanlder);
			}
		}

		void show(ITextureRegion specialTextTR, long bounty,
				TimerHandler timerHandler, boolean isVictory) {
			show(specialTextTR, bounty, timerHandler, 1f, isVictory);
		}

		void show(ITextureRegion specialTextTR, long bounty, boolean isVictory) {
			show(specialTextTR, bounty, null, isVictory);
		}

		void show(long bounty, boolean isVictory) {
			show(bounty, 1f, null, isVictory);
		}

		void show(long bounty, float scale, boolean isVictory) {
			show(bounty, scale, null, isVictory);
		}

		void show(long bounty, float scale, TimerHandler updateHanlder,
				boolean isVictory) {
			show("", bounty, scale, updateHanlder, isVictory);
		}

		void show(String title, long bounty, boolean isVictory) {
			show(title, bounty, 1f, null, isVictory);
		}

		void show(String title, long bounty, float scale, boolean isVictory) {
			show(title, bounty, scale, null, isVictory);
		}

		void show(String title, long bounty, float scale,
				TimerHandler updateHanlder, boolean isVictory) {
			title = title.toUpperCase();

			String content = CommonUtils.formatRewardCash(bounty);
			if (!TextUtils.isEmpty(title)) {
				content = title + "\n" + CommonUtils.formatRewardCash(bounty);
			}

			show(content, scale, updateHanlder, isVictory);
		}

		void hide() {
			disable(sGlowLight);
			disable(sGlowDark);
		}

		private void disable(Entity e) {
			e.setVisible(false);
			e.setIgnoreUpdate(true);
		}

		private void enable(Entity e) {
			e.setVisible(true);
			e.setIgnoreUpdate(false);
		}

		private void attachSpecialText(ITextureRegion specialTextTR,
				float scale, boolean isVictory, float offsetX, float offsetY) {
			if (this.specialText != null) {
				this.specialText.detachSelf();
				this.specialText = null;
			}

			this.specialText = new Sprite(WIDTH / 2 + offsetX, HEIGHT / 2
					+ offsetY, specialTextTR,
					BaseXeengGame.INSTANCE.getVertexBufferObjectManager());

			specialText.setSize(specialTextTR.getWidth() * SPECIAL_TEXT_SCALE
					* scale, specialTextTR.getHeight() * SPECIAL_TEXT_SCALE
					* scale);

			if (BaseXeengGame.SCREEN_RATIO > 1.5f) {
				specialText.setSize(specialTextTR.getWidth()
						* SPECIAL_TEXT_SCALE * scale / 2,
						specialTextTR.getHeight() * SPECIAL_TEXT_SCALE * scale
								/ 2);
			}

			float start = specialText.getY() - 20;
			float end = specialText.getY();
			specialText.registerEntityModifier(new SequenceEntityModifier(
					new DelayModifier(0), new MoveYModifier(.8f, start, end,
							EaseCubicOut.getInstance())));

			if (isVictory) {
				sGlowLight.attachChild(specialText);
			} else {
				sGlowDark.attachChild(specialText);
			}
		}
	}
}
