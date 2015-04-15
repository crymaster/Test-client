package com.tv.xeeng.game.elements.player;

import android.util.Log;
import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.game.elements.card.CardHolder;
import com.tv.xeeng.game.elements.card.CardHolderSprite;
import com.tv.xeeng.game.elements.card.CardHolderSprite.HolderType;
import com.tv.xeeng.game.elements.common.PlayerSprite.PlayerLocation;
import com.tv.xeeng.game.elements.common.ScoreBoard;
import com.tv.xeeng.gamedata.entity.Card;

import java.util.Vector;

public class TLMNPlayerHolder extends CardGamePlayerHolder {
	// private CardHolderSprite cardHand; // các lá bài úp
	private CardHolderSprite showHand; // các lá bài ngửa

	private CardHolder cardHand;
	private ScoreBoard scoreBoard;

	public TLMNPlayerHolder(PlayerLocation location) {
		super(location);

		playerSprite.setTimeOut(30);
	}

	public void onCreatePlayerSprite() {
		if (playerLoc == PlayerLocation.BOTTOM) {
			rewardPosition[0] = playingPosition[0] + 100f;
			rewardPosition[1] = playingPosition[1];
		} else {
			rewardScale = 1f;
		}

		switch (playerLoc) {
		case BOTTOM:
			break;
		case RIGHT:
			// showHandPosition[1] = defaultPosition[1] + 50f;
			showHandPosition[0] += 5f;
			showHandPosition[1] = playingPosition[1] + playerSprite.getHeight()
					/ 4;
			break;
		case LEFT:
			// showHandPosition[1] = defaultPosition[1] + 50f;
			showHandPosition[0] += 5f;
			showHandPosition[1] = playingPosition[1] + playerSprite.getHeight()
					/ 4;
			break;
		case TOP:
			showHandPosition[0] = playingPosition[0] + playerSprite.getWidth();
			showHandPosition[1] = playingPosition[1];
			break;
		default:
			break;
		}

		scoreBoard = new ScoreBoard(0, 0);
		scoreBoard.setPosition(playingPosition[0], playingPosition[1] + 30f);
		scoreBoard.setVisible(false);
		scoreBoard.setZIndex(100);

		BaseXeengGame.INSTANCE.getCurrentScene().attachChild(scoreBoard);
		BaseXeengGame.INSTANCE.getCurrentScene().sortChildren();
	}

	public void showCardHolder(int cardNum) {
		if (cardHand == null) {
			cardHand = new CardHolder(0, 0);
			cardHand.setVisible(false);
			cardHand.setPosition(cardHandPosition[0], cardHandPosition[1]);
			cardHand.setZIndex(BaseXeengGame.Z_PLAYER - 1);
			if (playerLoc == PlayerLocation.BOTTOM) {
				hideCardHolder();
			}
			playerSprite.setZIndex(BaseXeengGame.Z_PLAYER);
			BaseXeengGame.INSTANCE.getCurrentScene().attachChild(cardHand);
			BaseXeengGame.INSTANCE.getCurrentScene().sortChildren();
		}
		cardHand.setBackCardIndex(getBackCardIndex());
		cardHand.setVisible(true);
		cardHand.setIgnoreUpdate(false);
	}

	public void showCardHolder(final Vector<Card> cardList) {
		if (cardList == null) {
			Log.d("TLMNPlayerHolder", "cardList is null");
			return;
		}

		Log.d("TLMNPlayerHolder", "cardList count " + cardList.size());
		if (cardHand == null) {
			cardHand = new CardHolder(0, 0);
			cardHand.setPosition(cardHandPosition[0], cardHandPosition[1]);
			cardHand.setZIndex(BaseXeengGame.Z_PLAYER - 1);
			if (playerLoc == PlayerLocation.BOTTOM) {
				hideCardHolder();
			}
			playerSprite.setZIndex(BaseXeengGame.Z_PLAYER);
			BaseXeengGame.INSTANCE.getCurrentScene().attachChild(cardHand);
			BaseXeengGame.INSTANCE.getCurrentScene().sortChildren();

		}
		cardHand.setBackCardIndex(getBackCardIndex());
		cardHand.setVisible(true);
		cardHand.setIgnoreUpdate(false);
	}

	public void hideCardHolder() {
		if (cardHand != null) {
			cardHand.setVisible(false);
			cardHand.setIgnoreUpdate(true);
		}
	}

	public boolean isShowingCardHand() {
		if (cardHand != null)
			return cardHand.isVisible();

		return false;
	}

	public void updateNumHand(int pNumHand) {
		if (cardHand == null) {
			Log.d("TLMNPlayerHolder", "cardHand is null");
			showCardHolder(pNumHand);
		} else {
			Log.d("TLMNPlayerHolder", "cardHand show card " + pNumHand);
			// cardHand.setCardNum(pNumHand);
			cardHand.setNumHand(pNumHand);
			cardHand.setVisible(true);
			cardHand.setIgnoreUpdate(false);
		}
	}

	public void showCardHand(Vector<Card> pData) {
		if (showHand == null) {
			HolderType type;
			switch (playerLoc) {
			case TOP:
				type = HolderType.HORIZONAL;
				break;
			case LEFT:
			case RIGHT:
				type = HolderType.VERTICAL;
				break;
			default:
				type = HolderType.HORIZONAL;
				break;
			}
			showHand = new CardHolderSprite(0, 0, pData, type);
			showHand.setPosition(showHandPosition[0], showHandPosition[1]);
			showHand.setZIndex(BaseXeengGame.Z_PLAYER + 10);
			BaseXeengGame.INSTANCE.getCurrentScene().attachChild(showHand);
			BaseXeengGame.INSTANCE.getCurrentScene().sortChildren();
		}
		// showHand.setVisible(true);

		// Lets try some flipping here
		showHand.setBackCardIndex(getBackCardIndex());
		showHand.show(true);

		showHand.setIgnoreUpdate(false);
	}

	public void hideCardHand() {
		if (showHand != null) {
			showHand.setIgnoreUpdate(true);
			showHand.setVisible(false);
			BaseXeengGame.INSTANCE.runOnUpdateThread(new Runnable() {
				public void run() {
					if (showHand.detachSelf()) {
						showHand = null;
					}
				}
			});
		}
	}

	public void hideCardHand1() {
		if (showHand != null) {
			showHand.setIgnoreUpdate(true);
			showHand.setVisible(false);
			if (showHand.detachSelf()) {
				showHand = null;
			}
		}
	}

	@Override
	public void showFloatText(long pRewardCash, boolean isVictory) {
		if (pRewardCash > 0) {
			showFloatText("THẮNG", pRewardCash, isVictory);
		} else {
			showFloatText("THUA", pRewardCash, isVictory);
		}
	}

	@Override
	public void hideFloatText() {
		super.hideFloatText();
	}

	@Override
	public void reset(boolean hasInvitingBtn) {
		super.reset(hasInvitingBtn);
		hideFloatText();
	}

	public void showScoreBoard(int score) {
		if (scoreBoard == null || score < 1) {
			return;
		}
		scoreBoard.setPoint(score + " lá");
		scoreBoard.setIgnoreUpdate(false);
		scoreBoard.setVisible(true);
	}

	public void hideScoreBoard() {
		if (scoreBoard == null) {
			return;
		}

		scoreBoard.setVisible(false);
		scoreBoard.setIgnoreUpdate(true);
	}

}
