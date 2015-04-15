package com.tv.xeeng.game.elements.player;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ScaleAtModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackIn;

import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.game.elements.common.ChatBox;
import com.tv.xeeng.game.elements.common.OnClickPlayerSpriteListener;
import com.tv.xeeng.game.elements.common.PlayerSprite;
import com.tv.xeeng.game.elements.common.PlayerSprite.PlayerLocation;
import com.tv.xeeng.gamedata.entity.Player;

public abstract class BasePlayerHolder {

	public final static long IS_AVAILABLE = -1;
	public float defaultPosition[] = new float[2];

	/*
	 * state
	 */
	public long playerId;
	public String playerName;
	public long cash;
	public PlayerLocation playerLoc;
	private Player mPlayerData;

	/*
	 * 
	 * displayed objects
	 */
	public PlayerSprite playerSprite;
	public ChatBox chatBox;
	public TiledSprite mEmotionSprite;

	public BasePlayerHolder(float x, float y, PlayerLocation pPanelType) {
		playerId = IS_AVAILABLE;
		defaultPosition[0] = x;
		defaultPosition[1] = y;
		playerLoc = pPanelType;

		playerSprite = new PlayerSprite(defaultPosition[0], defaultPosition[1],
				null);
		playerSprite.setZIndex(BaseXeengGame.Z_PLAYER);

		chatBox = new ChatBox(0, 0, playerLoc);
		BaseXeengGame.mScene.attachChild(chatBox);
		chatBox.setZIndex(BaseXeengGame.Z_CHAT);
		updateChatBoxPosition(chatBox);
	}

	public abstract void onCreatePlayerSprite();

	public boolean isAvailable() {

		return playerId == IS_AVAILABLE ? true : false;
	}

	public void reset(boolean hasInvitingBtn) {
		playerId = IS_AVAILABLE;
		playerName = null;
		cash = IS_AVAILABLE;
		mPlayerData = null;
		if (hasInvitingBtn) {
			playerSprite.showInviteButton();
		} else {
			playerSprite.hide();
		}

		if (chatBox != null) {

			chatBox.hiddenChat(false);
		}

		if (mEmotionSprite != null) {

			mEmotionSprite.clearUpdateHandlers();
			mEmotionSprite.clearEntityModifiers();
			mEmotionSprite.setIgnoreUpdate(true);
			mEmotionSprite.setVisible(false);
		}
	}

	public void settle(Player pPlayerData) {

		mPlayerData = pPlayerData;
		playerId = mPlayerData.id;
		playerName = mPlayerData.character;
		cash = mPlayerData.cash;

		playerSprite.settle(playerId, playerName, cash);
	}

	public void update() {

		playerSprite.updateCash(mPlayerData.cash, true);
	}

	public Player getPlayerData() {
		return mPlayerData;
	}

	public void setOnPlayerClickListener(
			OnClickPlayerSpriteListener pClickPlayerSpriteListener) {
		playerSprite.setListener(pClickPlayerSpriteListener);
	}

	public void moveToPlayingMode() {

		updateChatBoxPosition(chatBox);
		if (mEmotionSprite != null) {

			mEmotionSprite.setPosition(playerSprite);
		}
	}

	public void moveToWaitingMode() {

		updateChatBoxPosition(chatBox);
		if (mEmotionSprite != null) {
			mEmotionSprite.setPosition(playerSprite);
		}
	}

	public void chat(String msg) {

		chatBox.setIgnoreUpdate(false);
		chatBox.setVisible(true);
		chatBox.chat(msg);
	}

	public void exposeEmotion(int pIndex) {

		if (mEmotionSprite == null) {
			mEmotionSprite = new TiledSprite(playerSprite.getX(),
					playerSprite.getY(), BaseXeengGame.emotionTTR,
					BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
			BaseXeengGame.INSTANCE.getCurrentScene()
					.attachChild(mEmotionSprite);
			mEmotionSprite.setZIndex(BaseXeengGame.Z_CHAT + 1);
			mEmotionSprite.setSize(65, 65);
		}

		mEmotionSprite.setPosition(playerSprite);
		mEmotionSprite.clearEntityModifiers();
		mEmotionSprite.clearUpdateHandlers();

		mEmotionSprite.setCurrentTileIndex(pIndex);
		mEmotionSprite.setIgnoreUpdate(false);
		mEmotionSprite.setVisible(true);
		mEmotionSprite.setScale(1);

		float oy = mEmotionSprite.getY();
		float ay = oy + 3;
		mEmotionSprite.registerEntityModifier(new LoopEntityModifier(
				new SequenceEntityModifier(new MoveYModifier(0.3f, oy, ay),
						new MoveYModifier(0.3f, ay, oy), new ScaleAtModifier(
								0.2f, mEmotionSprite.getScaleX(),
								mEmotionSprite.getScaleX(), mEmotionSprite
										.getScaleY(), mEmotionSprite
										.getScaleY() * 0.8f, 0.5f, 0),
						new ScaleAtModifier(0.2f, mEmotionSprite.getScaleX(),
								mEmotionSprite.getScaleX(), mEmotionSprite
										.getScaleY() * 0.8f, mEmotionSprite
										.getScaleY(), 0.5f, 0)), 4));

		mEmotionTimer.reset();
		mEmotionSprite.registerUpdateHandler(mEmotionTimer);
	}

	TimerHandler mEmotionTimer = new TimerHandler(4, new ITimerCallback() {

		@Override
		public void onTimePassed(TimerHandler pTimerHandler) {

			ScaleModifier scaleMod = new ScaleModifier(0.5f, 1, 0,
					new IEntityModifier.IEntityModifierListener() {

						@Override
						public void onModifierStarted(
								IModifier<IEntity> pModifier, IEntity pItem) {

						}

						@Override
						public void onModifierFinished(
								IModifier<IEntity> pModifier, IEntity pItem) {

							mEmotionSprite.setVisible(false);
						}
					}, EaseBackIn.getInstance());
			mEmotionSprite.registerEntityModifier(scaleMod);
		}
	});

	protected void updateChatBoxPosition(Entity pEntity) {
		float x = 0, y = 0;
		switch (playerLoc) {
		case TOP:
			pEntity.setAnchorCenter(1, 0.5f);
			x = playerSprite.getX() - playerSprite.getWidth() / 2;
			y = playerSprite.getY();
			break;
		case TOP_LEFT:
			pEntity.setAnchorCenter(1, 0.5f);
			x = playerSprite.getX() - playerSprite.getWidth() / 2;
			y = playerSprite.getY();
		case TOP_RIGHT:
			pEntity.setAnchorCenter(0, 0.5f);
			x = playerSprite.getX() + playerSprite.getWidth() / 2;
			y = playerSprite.getY();
			break;

		case LEFT:
			float d = 10f;
			pEntity.setAnchorCenter(0, 0.5f);
			x = playerSprite.getX() + playerSprite.getWidth() / 2 + d;
			y = playerSprite.getY();
			break;
		case BOTTOM:
			pEntity.setAnchorCenter(0.5f, 0f);
			x = playerSprite.getX() + 20f;
			y = playerSprite.getY() + playerSprite.getHeight() / 2;
			break;
		case BOTTOM_LEFT:
			pEntity.setAnchorCenter(1, 0.5f);
			x = playerSprite.getX() - playerSprite.getWidth() / 2;
			y = playerSprite.getY();
			break;
		case BOTTOM_RIGHT:
			pEntity.setAnchorCenter(0, 0.5f);
			x = playerSprite.getX() + playerSprite.getWidth() / 2;
			y = playerSprite.getY();
			break;

		case RIGHT:
			pEntity.setAnchorCenter(1, 0.5f);
			x = playerSprite.getX() - playerSprite.getWidth() / 2;
			y = playerSprite.getY();
			break;

		default:
			break;
		}
		pEntity.setPosition(x, y);
	}
}
