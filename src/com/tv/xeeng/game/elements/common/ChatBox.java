package com.tv.xeeng.game.elements.common;

import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.game.elements.common.PlayerSprite.PlayerLocation;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBackIn;

public class ChatBox extends GroupEntity {

	private TiledSprite mboxSprite;
	private XeengText mcontentTxt;

	public ChatBox(float x, float y, PlayerLocation pLoc) {
		this(x, y);
		
		ChatBoxPositionType type = ChatBoxPositionType.BOTTOM;
		switch (pLoc) {
		case TOP_LEFT:
			type = ChatBoxPositionType.BOTTOM_RIGHT;
			break;
		case TOP_RIGHT:
			type = ChatBoxPositionType.BOTTOM_LEFT;
			break;
		case LEFT:
			type = ChatBoxPositionType.RIGHT;
			break;
		case BOTTOM:
			type = ChatBoxPositionType.TOP;
			break;
		case BOTTOM_LEFT:
			type = ChatBoxPositionType.TOP_RIGHT;
			break;
		case BOTTOM_RIGHT:
			type = ChatBoxPositionType.TOP_LEFT;
			break;
		case RIGHT:
			type = ChatBoxPositionType.LEFT;
			break;
		default:
			break;
		}

		updatePosition(type);
		setVisible(false);
	}
	
	private ChatBox(float x, float y) {
		super(x, y, BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		ChatBox.this.setSize(BaseXeengGame.chatBoxTTR.getWidth(0),
				BaseXeengGame.chatBoxTTR.getHeight(0));
		mboxSprite = new TiledSprite(getWidth() / 2, getHeight() / 2,
				BaseXeengGame.chatBoxTTR,
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		mcontentTxt = new XeengText(getWidth() / 2, getHeight() / 2,
				BaseXeengGame.smallRegularFont, "", 55,
				new XeengTextOptions(AutoWrap.WORDS, getWidth() - 20,
						HorizontalAlign.CENTER),
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
		mcontentTxt.setColor(Color.BLACK);
		attachChild(mboxSprite);
		attachChild(mcontentTxt);
	}
	
	public ChatBox(float x, float y, ChatBoxPositionType positionType) {
		this(x, y);
		
		updatePosition(positionType);
		setVisible(false);
	}
	
	public void updatePosition(ChatBoxPositionType type) {
		mboxSprite.setFlippedHorizontal(false);
		
		switch (type) {
		case TOP_LEFT:
			mboxSprite.setFlippedHorizontal(true);
			mboxSprite.setCurrentTileIndex(0);
			mboxSprite.setScaleCenter(0, 0.5f);
			break;
		case TOP:
			mboxSprite.setFlippedHorizontal(true);
			mboxSprite.setCurrentTileIndex(0);
			mboxSprite.setScaleCenter(0.5f, 0f);
			break;
		case TOP_RIGHT:
			mboxSprite.setCurrentTileIndex(0);
			mboxSprite.setScaleCenter(1, 0.5f);
			break;
		case RIGHT:
			mboxSprite.setFlippedHorizontal(true);
			mboxSprite.setCurrentTileIndex(1);
			mboxSprite.setScaleCenter(0, 0.5f);
			break;
		case LEFT:
			mboxSprite.setCurrentTileIndex(1);
			mboxSprite.setScaleCenter(1, 0.5f);
			break;
		case BOTTOM_LEFT:
			mboxSprite.setFlippedHorizontal(true);
			mboxSprite.setCurrentTileIndex(1);
			mboxSprite.setScaleCenter(0, 0.5f);
			break;
		case BOTTOM:
			mboxSprite.setCurrentTileIndex(1);
			mboxSprite.setScaleCenter(1, 0.5f);
			break;
		case BOTTOM_RIGHT:
			mboxSprite.setCurrentTileIndex(1);
			mboxSprite.setScaleCenter(1, 0.5f);
			break;
		default:
			break;
		}
	}
	
	public void chat(String content) {

		if (content.length() > 14) {

			content = content.substring(0, 13) + "...";
		}
		final String msg = content;
		setVisible(true);
		mcontentTxt.setText(msg);

		clearUpdateHandlers();
		timerHandler.reset();
		registerUpdateHandler(timerHandler);
	}

	public void setReversed(boolean isReversed) {
		if (isReversed) {
			mboxSprite.setRotation(180);
		} else {
			mboxSprite.setRotation(0);
		}
	}

	public void hiddenChat(boolean animated) {

		mcontentTxt.setVisible(false);
		if (animated) {

			ScaleModifier scaleMod = new ScaleModifier(0.5f, 1, 0,
					new IEntityModifier.IEntityModifierListener() {

						@Override
						public void onModifierStarted(
								IModifier<IEntity> pModifier, IEntity pItem) {

						}

						@Override
						public void onModifierFinished(
								IModifier<IEntity> pModifier, IEntity pItem) {

							setVisible(false);
							mboxSprite.setScale(1);
						}
					}, EaseBackIn.getInstance());

			mboxSprite.clearEntityModifiers();
			mboxSprite.registerEntityModifier(scaleMod);
		} else {

			setVisible(false);
		}
	}

	private TimerHandler timerHandler = new TimerHandler(4,
			new ITimerCallback() {

				@Override
				public void onTimePassed(TimerHandler pTimerHandler) {

					hiddenChat(true);
				}
			});

	public enum ChatBoxPositionType {
		TOP_LEFT, TOP, TOP_RIGHT, RIGHT, BOTTOM_RIGHT, BOTTOM, BOTTOM_LEFT, LEFT
	}
}
