package com.tv.xeeng.game.elements.card;

import java.util.Vector;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.util.modifier.IModifier;

import android.util.Log;

import com.tv.xeeng.game.BaseCardGameActivity;
import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.game.TLMNGameActivity;
import com.tv.xeeng.gamedata.entity.Card;

public class CardHolderSprite extends Entity {

	private static final float SCALE = TLMNGameActivity.MIN_CARD_SCALE; // 0.6f
	private static final float CARD_WIDTH = BaseXeengGame.CARD_WIDTH;
	private static final float CARD_HEIGHT = BaseXeengGame.CARD_HEIGHT;

	private static final int MAX_CARD_PILE_VERTICAL = 8;
	private static final int MAX_CARD_PILE_HORIZONTAL = 9;

	public enum HolderType {
		VERTICAL, HORIZONAL
	}

	private int numVisibleBackCard;
	private Vector<TiledSprite> backCardSprites = new Vector<TiledSprite>(13);
	private Vector<TiledSprite> showCardSprites = new Vector<TiledSprite>();
	private HolderType type;
	private int maxCardPile;

	private int backCardIndex = BaseCardGameActivity.BACK_CARD_INDEX;

	public CardHolderSprite(float pX, float pY, int cardNum,
			HolderType holderType, float scale, int maxCardPile) {
		super(pX, pY);
		type = holderType;
		numVisibleBackCard = cardNum;
		this.maxCardPile = maxCardPile;

		for (int i = 0; i < cardNum; i++) {
			final TiledSprite sCard = new TiledSprite(0, 0,
					BaseCardGameActivity.tiledCardsTTR,
					BaseXeengGame.INSTANCE.getVertexBufferObjectManager());
			sCard.setSize(CARD_WIDTH * scale, CARD_HEIGHT * scale);
			backCardSprites.add(sCard);
			attachChild(sCard);
			update();
		}
	}

	public CardHolderSprite(float pX, float pY, Vector<Card> cardList,
			HolderType holderType, float scale, int maxCardPile) {
		super(pX, pY);
		this.type = holderType;
		this.maxCardPile = maxCardPile;

		if (cardList == null)
			cardList = new Vector<Card>();

		for (Card card : cardList) {
			addShowCard(card, true, scale);
		}
	}

	public CardHolderSprite(float pX, float pY, Vector<Card> cardList,
			HolderType holderType, float scale) {
		this(pX, pY, cardList, holderType, SCALE,
				holderType == HolderType.VERTICAL ? MAX_CARD_PILE_VERTICAL
						: MAX_CARD_PILE_HORIZONTAL);
	}

	public CardHolderSprite(float pX, float pY, int cardNum,
			HolderType holderType, float scale) {
		this(pX, pY, cardNum, holderType, scale,
				holderType == HolderType.VERTICAL ? MAX_CARD_PILE_VERTICAL
						: MAX_CARD_PILE_HORIZONTAL);
	}

	public CardHolderSprite(float pX, float pY, int cardNum,
			HolderType holderType) {
		this(pX, pY, cardNum, holderType, SCALE,
				holderType == HolderType.VERTICAL ? MAX_CARD_PILE_VERTICAL
						: MAX_CARD_PILE_HORIZONTAL);
	}

	public CardHolderSprite(float pX, float pY, Vector<Card> cardList,
			HolderType holderType) {
		this(pX, pY, cardList, holderType, SCALE,
				holderType == HolderType.VERTICAL ? MAX_CARD_PILE_VERTICAL
						: MAX_CARD_PILE_HORIZONTAL);
	}

	public CardHolderSprite(float pX, float pY, Vector<Card> cardList,
			HolderType holderType, int maxCardPile) {
		this(pX, pY, cardList, holderType, SCALE, maxCardPile);
	}

	public void setBackCardIndex(int backCardIndex) {
		this.backCardIndex = backCardIndex;

		for (TiledSprite sprite : backCardSprites) {
			sprite.setCurrentTileIndex(backCardIndex);
		}
	}

	public void setCardNum(int cardNum) {
		try {
			int i = numVisibleBackCard;
			if (numVisibleBackCard < cardNum) {
				for (i = numVisibleBackCard; i < cardNum; i++) {
					backCardSprites.get(i).setVisible(true);
				}

				for (; i < backCardSprites.size(); i++) {
					backCardSprites.get(i).setVisible(false);
				}
			} else {
				for (i = cardNum; i < backCardSprites.size(); i++) {
					backCardSprites.get(i).setVisible(false);
				}
			}

			numVisibleBackCard = cardNum;
			update();
		} catch (ArrayIndexOutOfBoundsException ex) {
			ex.printStackTrace();
		}
	}

	public void addShowCard(Card card) {
		addShowCard(card, true, SCALE);
	}

	public void addShowCard(Card card, float scale) {
		addShowCard(card, true, scale);
	}

	private void addShowCard(Card card, final boolean update, float scale) {
		final TiledSprite sCard = new TiledSprite(0, 0,
				BaseCardGameActivity.tiledCardsTTR,
				BaseXeengGame.INSTANCE.getVertexBufferObjectManager());

		sCard.setCurrentTileIndex(card.serverValue > 0 ? card.serverValue - 1
				: backCardIndex);
		sCard.setUserData(card);

		sCard.setSize(CARD_WIDTH * scale, CARD_HEIGHT * scale);
		showCardSprites.add(sCard);
		attachChild(sCard);
		if (update) {
			update();
		}
	}

	public void clearShowCards() {
		for (Sprite sCard : showCardSprites) {
			detachChild(sCard);
		}
		showCardSprites.clear();
	}

	public int countShowCards() {
		return showCardSprites.size();
	}

	private void update() {
		int i = 0, z = 20;

		int total = 0;

		if (type == HolderType.VERTICAL) {
			total = showCardSprites.size() + numVisibleBackCard;
			total = total > 3 ? (total > 8 ? 8 : total) : 0;
		}

		for (Sprite sCard : showCardSprites) {
			float x = 0, y = 0;

			switch (type) {
			case VERTICAL:
				if (i >= maxCardPile) {
					x = sCard.getWidth() / 2;
				} else {
					x = 0;
				}
				y = 0 - (i % maxCardPile) * sCard.getHeight() / 3 + total
						* sCard.getHeight() / 9 + 4f;
				break;
			case HORIZONAL:
				x = (i % maxCardPile) * sCard.getWidth() / 2.5f;
				y = -(i / maxCardPile) * sCard.getHeight() / 2;
				z++;
				break;
			default:
				break;
			}

			sCard.setPosition(x, y);
			sCard.setZIndex(z);
			i++;
		}

		for (int j = 0; j < numVisibleBackCard; j++) {
			if (j >= backCardSprites.size()) {
				break;
			}
			Sprite sCard = backCardSprites.get(j);
			float x = 0, y = 0;

			switch (type) {
			case VERTICAL:
				if ((j + i) >= maxCardPile) {
					x = sCard.getWidth() / 2;
				} else {
					x = 0;
				}
				y = 0 - ((j + i) % maxCardPile) * sCard.getHeight() / 3 + total
						* sCard.getHeight() / 9 + 4f;
				z++;
				break;
			case HORIZONAL:
				x = ((j + i) % maxCardPile) * sCard.getWidth() / 2.5f;
				y = -((j + i) / maxCardPile) % sCard.getHeight() / 2;
				z++;
				break;
			default:
				break;
			}

			sCard.setPosition(x, y);
			sCard.setZIndex(z);
		}

		sortChildren();
	}

	public void show(boolean isAnimated) {
		if (!isAnimated) {
			setVisible(true);

			for (TiledSprite showCard : showCardSprites) {
				showCard.setVisible(true);
				showCard.setIgnoreUpdate(false);

				showCard.setCurrentTileIndex(backCardIndex);
			}
		} else {
			try {
				for (TiledSprite showCard : showCardSprites) {
					showCard.setVisible(true);
					showCard.setIgnoreUpdate(false);

					showCard.setCurrentTileIndex(backCardIndex);
				}

				setVisible(true);

				int i = 0;
				for (final TiledSprite showCard : showCardSprites) {
					showCard.registerEntityModifier(new SequenceEntityModifier(
							new DelayModifier(i++ * 0.05f), new ScaleModifier(
									0.1f, 1.0f, 0f, 1.0f, 1.0f),
							new ScaleModifier(0.1f, 0f, 1.0f, 1.0f, 1.0f,
									new IEntityModifierListener() {

										@Override
										public void onModifierStarted(
												IModifier<IEntity> arg0,
												IEntity arg1) {
										}

										@Override
										public void onModifierFinished(
												IModifier<IEntity> arg0,
												IEntity arg1) {
											Card cardData = (Card) showCard
													.getUserData();

											if (cardData.serverValue > 0) {
												showCard.setCurrentTileIndex(cardData.serverValue - 1);
											} else {
												showCard.setVisible(false);
											}
										}
									})));
				}
			} catch (Exception ex) {
				Log.d("CardHolder",
						"FLipping animation down. I repeat flipping animation down...");
				ex.printStackTrace();
			}
		}
	}
}
