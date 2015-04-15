package com.tv.xeeng.game;

import android.os.Vibrator;
import android.util.Log;
import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.game.BaseCardGame;
import com.tv.xeeng.dataLayer.game.BaseGame.GameState;
import com.tv.xeeng.dataLayer.game.TLMNGame;
import com.tv.xeeng.dataLayer.game.TLMNGame.FightCardType;
import com.tv.xeeng.dataLayer.game.TLMNGame.WinType;
import com.tv.xeeng.dataLayer.game.actions.BaseXEGameAction;
import com.tv.xeeng.dataLayer.game.actions.XETLMNShowTurnAction;
import com.tv.xeeng.game.elements.card.CardSprite;
import com.tv.xeeng.game.elements.card.ClickableCard;
import com.tv.xeeng.game.elements.card.ClickableCard.ClickType;
import com.tv.xeeng.game.elements.card.ClickableCard.onClickCardListener;
import com.tv.xeeng.game.elements.card.tlmn.FightingCardHolder;
import com.tv.xeeng.game.elements.common.ActionButton;
import com.tv.xeeng.game.elements.common.PlayerSprite.PlayerLocation;
import com.tv.xeeng.game.elements.common.StartGameButton.StartGameState;
import com.tv.xeeng.game.elements.player.BasePlayerHolder;
import com.tv.xeeng.game.elements.player.TLMNPlayerHolder;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gamedata.entity.Card;
import com.tv.xeeng.gamedata.entity.Player;
import com.tv.xeeng.gamedata.entity.Player.PlayerState;
import com.tv.xeeng.gamedata.entity.TLMNPlayer;
import com.tv.xeeng.gamedata.entity.TLMNPlayer.SortType;
import com.tv.xeeng.manager.Logger;
import com.tv.xeeng.manager.SoundManager;
import com.tv.xeeng.manager.UserPreference;
import com.tv.xeeng.R;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.*;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.sprite.batch.SpriteBatch;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseCircularIn;

import java.util.*;

public class TLMNGameActivity extends BaseCardGameActivity {

	public static final short WIN_INDEX = 0;
	public static final short LOSE_INDEX = 2;
	public static final short TOI_TRANG_INDEX = 1;

	private BuildableBitmapTextureAtlas tlmnBackgroundTextureAtlas;
	private BuildableBitmapTextureAtlas mFightInfoTextureAtlas;

	// public static ITextureRegion iconWinTR, iconLoseTR;
	//
	// private static final String ICON_WIN_RES = "tlmn/icon_thang.png";
	// private static final String ICON_WIN_RES_HD = "tlmn/icon_thang_hd.png";
	//
	// private static final String ICON_LOSE_RES = "tlmn/icon_thua.png";
	// private static final String ICON_LOSE_RES_HD = "tlmn/icon_thua_hd.png";

	private static final String ICON_TRA_LAI_RES = "tlmn/icon_tralai.png";
	private static final String ICON_TRA_LAI_RES_HD = "tlmn/icon_tralai_hd.png";

	private static final String ICON_CHATJ_RES = "tlmn/icon_chatj.png";
	private static final String ICON_CHATJ_RES_HD = "tlmn/icon_chatj_hd.png";

	private static final String RESULT_TOI_TRANG_RES = "tlmn/icon_thang_trang.png";
	private static final String RESULT_TOI_TRANG_5_DOI_THONG_RES = "tlmn/icon_thang_trang_5doithong.png";
	private static final String RESULT_TOI_TRANG_6_DOI_RES = "tlmn/icon_thang_trang_6doi.png";
	private static final String RESULT_TOI_TRANG_TU_QUY_RES = "tlmn/icon_thang_trang_tuquy.png";
	private static final String RESULT_TOI_TRANG_SANH_RONG_RES = "tlmn/icon_thang_trang_sanhrong.png";

	private static final String RESULT_TOI_TRANG_RES_HD = "tlmn/icon_thang_trang_hd.png";
	private static final String RESULT_TOI_TRANG_5_DOI_THONG_RES_HD = "tlmn/icon_thang_trang_5doithong_hd.png";
	private static final String RESULT_TOI_TRANG_6_DOI_RES_HD = "tlmn/icon_thang_trang_6doi_hd.png";
	private static final String RESULT_TOI_TRANG_TU_QUY_RES_HD = "tlmn/icon_thang_trang_tuquy_hd.png";
	private static final String RESULT_TOI_TRANG_SANH_RONG_RES_HD = "tlmn/icon_thang_trang_sanhrong_hd.png";

	public static ITiledTextureRegion mFightInfoTTR;
	public static ITextureRegion iconChatjTR, iconTraLaiTR;

	public static ITextureRegion resultToiTrangTR, resultToiTrang5DoiThongTR,
			resultToiTrang6DoiTR, resultToiTrangTuQuyTR,
			resultToiTrangSanhRongTR;
	private BuildableBitmapTextureAtlas resultToiTrangTextureAtlas;

	public static SpriteBatch mCardBatch;

	private ArrayList<ClickableCard> mCardListMainPlayer;
	private Map<Card, ClickableCard> mCardMap;
	private boolean isSortingCard = false;
	protected TLMNGame mGameData = (TLMNGame) GameData.shareData().getGame();
	private ArrayList<FightingCardHolder> fightingCardList;	//Bai danh tren ban

	private ActionButton btnSkip, btnPlayCard, btnArrangeHand;

	public float mXFirstCard, mSpacingCard;

	@Override
	protected void onLoadBackgroundResource() {
		super.onLoadBackgroundResource();
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/card/");
		tlmnBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(
				getTextureManager(), 256, 256);

		String iconLogoRes = "ingame_logo_tlmn_demla";
		if (SCREEN_RATIO > 1.5f) {
			iconLogoRes += "_hd";
		}
		iconLogoRes += ".png";

		iconLogoTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				tlmnBackgroundTextureAtlas, this, iconLogoRes);

		try {
			tlmnBackgroundTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			tlmnBackgroundTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void onLoadSpecificGameResource() {
		super.onLoadSpecificGameResource();

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/card/");

		// Match result icon win/lose
		// matchResultIconsTextureAtlas = new BuildableBitmapTextureAtlas(
		// getTextureManager(), 256, 256);
		//
		// String iconWinRes = ICON_WIN_RES;
		// String iconLoseRes = ICON_LOSE_RES;
		// if (SCREEN_RATIO > 1.5f) {
		// iconWinRes = ICON_WIN_RES_HD;
		// iconLoseRes = ICON_LOSE_RES_HD;
		// }
		//
		// iconWinTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
		// matchResultIconsTextureAtlas, this, iconWinRes);
		// iconLoseTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
		// matchResultIconsTextureAtlas, this, iconLoseRes);

		//
		ITextureRegion iconChat3DoiThong, iconChat4DoiThong, iconChatTuQuy;
		String iconChat3DoiThongRes, iconChat4DoiThongRes, iconChatTuQuyRes, iconChatRes, iconTraLaiRes;

		if (SCREEN_RATIO > 1.5f) {
			iconChat3DoiThongRes = "tlmn/icon_3doithong_hd.png";
			iconChat4DoiThongRes = "tlmn/icon_4doithong_hd.png";
			iconChatTuQuyRes = "tlmn/icon_tuquy_hd.png";
			iconChatRes = ICON_CHATJ_RES_HD;
			iconTraLaiRes = ICON_TRA_LAI_RES_HD;
		} else {
			iconChat3DoiThongRes = "tlmn/icon_3doithong.png";
			iconChat4DoiThongRes = "tlmn/icon_4doithong.png";
			iconChatTuQuyRes = "tlmn/icon_tuquy.png";
			iconChatRes = ICON_CHATJ_RES;
			iconTraLaiRes = ICON_TRA_LAI_RES;
		}

		mFightInfoTextureAtlas = new BuildableBitmapTextureAtlas(
				getTextureManager(), 1024, 1024);

		iconChat3DoiThong = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mFightInfoTextureAtlas, this,
						iconChat3DoiThongRes);
		iconChat4DoiThong = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mFightInfoTextureAtlas, this,
						iconChat4DoiThongRes);
		iconChatTuQuy = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				mFightInfoTextureAtlas, this, iconChatTuQuyRes);
		iconChatjTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				mFightInfoTextureAtlas, this, iconChatRes);
		iconTraLaiTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				mFightInfoTextureAtlas, this, iconTraLaiRes);

		mFightInfoTTR = new TiledTextureRegion(iconChat3DoiThong.getTexture(),
				iconChat3DoiThong, iconChat4DoiThong, iconChatTuQuy);

		// Perfect win resources
		resultToiTrangTextureAtlas = new BuildableBitmapTextureAtlas(
				getTextureManager(), 1024, 1024, mNormalTextureOption);
		String toiTrang5DoiThongRes, toiTrang6DoiRes, toiTrangTuQuyRes, toiTrangSanhRongRes, toiTrangRes;

		if (SCREEN_RATIO > 1.5f) {
			toiTrangRes = RESULT_TOI_TRANG_RES_HD;
			toiTrang5DoiThongRes = RESULT_TOI_TRANG_5_DOI_THONG_RES_HD;
			toiTrang6DoiRes = RESULT_TOI_TRANG_6_DOI_RES_HD;
			toiTrangTuQuyRes = RESULT_TOI_TRANG_TU_QUY_RES_HD;
			toiTrangSanhRongRes = RESULT_TOI_TRANG_SANH_RONG_RES_HD;
		} else {
			toiTrangRes = RESULT_TOI_TRANG_RES;
			toiTrang5DoiThongRes = RESULT_TOI_TRANG_5_DOI_THONG_RES;
			toiTrang6DoiRes = RESULT_TOI_TRANG_6_DOI_RES;
			toiTrangTuQuyRes = RESULT_TOI_TRANG_TU_QUY_RES;
			toiTrangSanhRongRes = RESULT_TOI_TRANG_SANH_RONG_RES;
		}

		resultToiTrang5DoiThongTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(resultToiTrangTextureAtlas, this,
						toiTrang5DoiThongRes);
		resultToiTrang6DoiTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(resultToiTrangTextureAtlas, this,
						toiTrang6DoiRes);
		resultToiTrangTuQuyTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(resultToiTrangTextureAtlas, this,
						toiTrangTuQuyRes);
		resultToiTrangSanhRongTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(resultToiTrangTextureAtlas, this,
						toiTrangSanhRongRes);
		resultToiTrangTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(resultToiTrangTextureAtlas, this, toiTrangRes);

		try {
			resultToiTrangTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			resultToiTrangTextureAtlas.load();

			mFightInfoTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			mFightInfoTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onCreateBackground() {
		super.onCreateBackground();
		mScene.attachChild(getChairSprite(PlayerLocation.TOP));
		mScene.attachChild(getChairSprite(PlayerLocation.RIGHT));
		mScene.attachChild(getChairSprite(PlayerLocation.BOTTOM));
		mScene.attachChild(getChairSprite(PlayerLocation.LEFT));
	}

	// @Override
	// protected void onInitSettingDialog() {
	// if (mSettingDialog == null) {
	// mSettingDialog = new BaseTLMNSettingDialog(mScene, mCamera) {
	//
	// @Override
	// public void onChangeSetting() {
	// onChangeSettingData();
	// }
	// };
	// }
	// ArrayList<NUM_PLAYER> players = new
	// ArrayList<BaseSimpleSettingDialog.NUM_PLAYER>();
	// players.add(NUM_PLAYER.SOLO);
	// players.add(NUM_PLAYER.SIX_PLAYER);
	// mSettingDialog.updatePlayer(players);
	// }

	@Override
	protected void updateRuleGameDialog() {

		String content = "Sau khi bắt đầu, người chơi sẽ được hệ thống chia cho 13 lá bài, bạn chọn nút xếp bài để hệ thống xếp bài cho bạn theo 2 cách khác nhau ( theo bài ngang hoặc theo sảnh ).\n\n"
				+ "• Người có lá bài 3 Bích được đánh trước ở ván đầu tiên. Từ ván thứ 2 trở đi người nào thắng ván trước đó được đánh bài trước. Nếu người về Nhất thoát khỏi game trước khi ván mới bắt đầu thì người về Nhì được đánh trước.\n\n"
				+ "• Khi người chơi đánh ra một quân bài/nhóm bài, người tiếp theo phải bắt bằng quân bài / nhóm bài lớn hơn.\n\n"
				+ "• Chơi theo vòng : Vòng đánh bài là ngược chiều kim đồng hồ.\n\n"
				+ "• Nếu người chơi đánh mà không còn ai bắt nữa thì một vòng chơi kết thúc. Người này có quyền đánh quân tiếp theo tùy ý.\n\n"
				+ "• Tứ quý chặt được 1 con heo(2 ) đen, 1 con heo đỏ, 2 heo đen, 2 heo đỏ, 2 heo vừa đen vừa đỏ, 3 đôi thông hoặc tứ quý nhỏ hơn.\n\n"
				+ "• Bốn đôi thông chặt được 1 con heo đen, 1 con heo đỏ, 2 heo đen hoặc 2 heo đỏ, hoặc 2 heo vừa đen vừa đỏ, 3 đôi thông nhỏ hơn và tứ quý nhỏ hơn.\n\n"
				+ "• Ba đôi thông chặt được 1 con heo đen, 1 con heo đỏ, 3 đôi thông nhỏ hơn.\n\n"
				+ "Các bộ bài người chơi hay xếp:\n\n"
				+ "- Thông ( Cặp, Đôi ) : 2 lá bài cùng hàng, cùng giá trị, có thể khác chất.\n\n"
				+ "- Xám cô ( dây ba, bộ ba ) : 3 là bài cùng hàng và cùng giá trị.\n\n"
				+ "- Sảnh ( các quân bài có giá trị liên tiếp nhau ). Các quân Hai ( Heo ) không được nằm trong sảnh, dây.\n\n"
				+ "- Sảnh rồng ( đặc biệt giá trị, có thể ăn trắng, là có đủ các quân bài từ 3 tới át )\n\n"
				+ "- Tứ quý: 4 quân bài giống nhau về số.\n\n"
				+ "- Ba đôi thông: 3 đôi liên tiếp nhau.\n\n"
				+ "- Bốn đôi thông: 4 đôi liên tiếp nhau, nếu may mắn bạn có thể có 5 đôi liền nhau.\n\n"
				+ "• Phạt tiền:\n\n"
				+ "- Trường hợp bình thường : Số cây còn lại trên tay người chơi nhân với số tiền cược( tiền bàn).\n\n"
				+ "Quy định khi trên tay còn một số quân đặc biệt được tính nhiều hơn:\n\n"
				+ "• Trên tay có 2 đen được tính là 2 cây, 2 đỏ được tính là 5 cây, tứ quý được tính là 5 cây, 3 đôi thông tính là 7 cây, 4 đôi thông tính là 13 cây.\n\n"
				+ "• Trường hợp xử tới trắng:\n\n"
				+ "Bài được coi là tới trắng khi có các trường hợp sau:\n\n"
				+ "- 6 đôi bất kỳ,5 đôi thông, tứ quý 2, sảnh rồng từ 3-2, sảnh 12 lá từ 3-A.\n\n"
				+ "- Phạt 26 lần tiền bàn. ";
		mRuleGameDialog.update(content.toString(),
				getResources().getString(R.string.label_tlmn_short));
	}

	// @Override
	// protected void onUpdateSettingDialog() {
	// super.onUpdateSettingDialog();
	// if (mGameData.getCurrentTable().getMaxPlayer() <= 2) {
	// mSettingDialog.setCurrentPlayer(NUM_PLAYER.SOLO);
	// } else {
	// mSettingDialog.setCurrentPlayer(NUM_PLAYER.FOUR_PLAYER);
	// }
	// ((BaseTLMNSettingDialog) mSettingDialog)
	// .updateTLMNRule(mGameData.isHideCard);
	//
	// }

	@Override
	protected void onChangeSettingData() {
		// Bundle bundle = new Bundle();
		// bundle.putBoolean(CardGameTable.HIDE_POCKER_KEY,
		// ((BaseTLMNSettingDialog) mSettingDialog).isCurrentShowNumCard());
		// BusinessRequester.getInstance().changeGameSetting(
		// mSettingDialog.getCurrentCash(),
		// mSettingDialog.getCurrentNumPlayer().getValue(), bundle);
	}

	@Override
	protected void renderSpecificElements(
			RenderElementCallBack pRenderElementCallBack) {
		// Create cac graphic elements (sprites) của từng người chơi.
		// "PlayerHolder" = ghế, chỗ ngồi của người chơi
		// PlayerHolder = avatar + các function buttons + các lá bài úp +
		// floating text(tiền, thắng thua cuối ván)
		for (TLMNPlayerHolder ph : getPlayerHolderList()) {
			ph.onCreatePlayerSprite();
		}

		mScene.sortChildren(); // NOTE: should run in updatethread of AndEngine

		float maxCardArea = DESIGN_WINDOW_WIDTH_PIXELS - 2 * MARGIN_HORIZONTAL
				- 120f;
		mSpacingCard = (int) maxCardArea / numCardPlayer;
		if (mSpacingCard > tiledCardsTTR.getWidth(0)) {
			mSpacingCard = tiledCardsTTR.getWidth(0);
		}
		mXFirstCard = getMainPlayerHolder().playingPosition[0] + CARD_WIDTH + 5;

		mCardBatch = new SpriteBatch(tiledCardsTTR.getTexture(), 100,
				getVertexBufferObjectManager());
		mCardBatch.setZIndex(Z_PLAYER + 5);
		mScene.attachChild(mCardBatch);

		if (GameData.shareData().getGame().getState() == GameState.PLAYING) {
			prepareForNewMatch();
			// hideReadyState(); // cờ sẵn sàng (ready indicator) bên cạnh
			// avatar người chơi

			if (mGameData.mNextPlayerId > -1) { // Nếu người đánh lượt tới là
				// hợp lệ ==> khởi động timer
				// thời gian chờ người chơi đánh

				BasePlayerHolder pc = getPlayer(mGameData.mNextPlayerId);
				if (pc != null) {
					pc.playerSprite.startTimer();
				}
			}

			// Duyệt qua danh sách người chơi
			for (final TLMNPlayer pd : mGameData.getPlayerList()) {

				if (pd.state == PlayerState.PLAYING) { // Nếu đang chơi
					if (pd.id == GameData.shareData().getMyself().id) { // bản
						// thân
						// người
						// chơi
						this.loadGameActionButtons(); // khởi tạo các nút đánh

						// prepareForNewMatch(); 
						getMainPlayerHolder().moveToPlayingMode();
						showCardMainPlayer();
						showFuncPanel();
					} else {
						final TLMNPlayerHolder pc = (TLMNPlayerHolder) getPlayer(pd.id);
						pc.moveToPlayingMode();
						Log.d("TLMNGameActivity", "player data numhand "
								+ pd.numHand);

						// Added by TungHX
						pc.updateNumHand(13);
						pc.updateNumHand(pd.numHand);

						// Commented by TungHX
						// runOnUiThread(new Runnable() {
						//
						// @Override
						// public void run() {
						// pc.updateNumHand(13);
						// pc.updateNumHand(pd.numHand);
						// }
						// });
					}
				}
			}

			// show last card
			if (mGameData.lastCards.size() > 0) {

				FightingCardHolder fch = new FightingCardHolder(
						mGameData.lastCards.get(mGameData.lastCards.size() - 1),
						null, null, null);
				if (fightingCardList == null) {

					fightingCardList = new ArrayList<FightingCardHolder>();
				}

				fightingCardList.add(fch);
				mCardBatch.attachChild(fch);
				fch.showNow(generateFighingCardEndPoint(fightingCardList.size()));
			}
		}

		if (GameData.shareData().getGame().getMainPlayer().state == PlayerState.OBSERVING) {
			showObservingView(true);
		} else {
			showObservingView(false);
		}

		pRenderElementCallBack.onRenderElementComplete();
	}

	@Override
	protected void unloadSpecificResources() {
		super.unloadSpecificResources();
		resultToiTrangTextureAtlas.unload();
		tlmnBackgroundTextureAtlas.unload();

		mFightInfoTextureAtlas.unload();
	}

	protected void addCardSpriteToList(ClickableCard pCardSprite, Card pCardData) {

		if (mCardListMainPlayer == null) {

			mCardListMainPlayer = new ArrayList<ClickableCard>();
		}

		if (mCardMap == null) {
			mCardMap = new TreeMap<Card, ClickableCard>(new Comparator<Card>() {
				@Override
				public int compare(Card lhs, Card rhs) {
					if (lhs.serverValue < rhs.serverValue) {
						return -1;
					}
					if (lhs.serverValue > rhs.serverValue) {
						return 1;
					}
					return 0;
				}
			});
		}

		mCardListMainPlayer.add(pCardSprite);
		mCardMap.put(pCardData, pCardSprite);
	}

	protected void clearCardListMainPlayer() {

		if (mCardListMainPlayer != null) {

			if (mCardListMainPlayer.size() > 0) {

				for (ClickableCard cc : mCardListMainPlayer) {

					cc.disappear();
				}
				mCardListMainPlayer.clear();
			}
		}

		if (mCardMap != null) {

			mCardMap.clear();
		}
	}

	protected int numCardPlayer = 13;

	protected void dealCard(int pTotalCard) {

		SoundManager.playStartGameEffect();
		float delay = 0;
		final float step = 0.1f;
		final float dealTime = 0.5f;
		CardSprite cardSprite;
		final float startX = CENTER_X;
		final float startY = CENTER_Y;
		float endX = 0, endY = 0;

		updateNumHandPlayer();
		showCardMainPlayer();

		for (ClickableCard cc : mCardListMainPlayer) {
			cc.setVisible(false);
		}

		for (int i = 0; i < numCardPlayer; i++) {

			delay += step;
			for (final Player pd : GameData.shareData().getGame()
					.getPlayerList()) {

				if (pd.state == PlayerState.PLAYING) {

					final TLMNPlayerHolder pc = (TLMNPlayerHolder) getPlayer(pd.id);
					if (pc == null)
						continue;

					// pc.updateNumHand(0);
					cardSprite = new CardSprite(startX, startY,
							tiledCardsTTR.getTextureRegion(pc
									.getBackCardIndex()),
							getVertexBufferObjectManager());
					cardSprite.setSize(CARD_WIDTH, CARD_HEIGHT);
					mScene.attachChild(cardSprite);
					cardSprite.setZIndex(Z_FIRST_CARD + pTotalCard - i);
					cardSprite.setScale(MIN_CARD_SCALE);
					cardSprite.setVisible(false);
					if (pd.id == GameData.shareData().getMyself().id) {
						pc.hideCardHand();
						final ClickableCard maincard = mCardListMainPlayer
								.get(i);
						endX = maincard.getX();
						endY = maincard.getY();
						cardSprite
								.registerEntityModifier(new SequenceEntityModifier(
										new DelayModifier(delay),
										new MoveModifier(
												dealTime,
												cardSprite.getX(),
												cardSprite.getY(),
												endX,
												endY,
												new IEntityModifier.IEntityModifierListener() {

													@Override
													public void onModifierStarted(
															IModifier<IEntity> pModifier,
															IEntity pItem) {
														pItem.setVisible(true);
													}

													@Override
													public void onModifierFinished(
															IModifier<IEntity> pModifier,
															final IEntity pItem) {

														maincard.setVisible(true);
														pItem.setVisible(false);
														pItem.setIgnoreUpdate(true);
														TLMNGameActivity.this
																.runOnUpdateThread(new Runnable() {
																	public void run() {

																		pItem.detachSelf();
																	}
																});
													}
												})));
					} else {

						endX = pc.playingPosition[0];
						endY = pc.playingPosition[1];
						final int ii = i + 1;
						// first deal turn card
						if (i == 0) {

							cardSprite
									.registerEntityModifier(new SequenceEntityModifier(
											new DelayModifier(delay),
											new MoveModifier(
													dealTime,
													cardSprite.getX(),
													cardSprite.getY(),
													endX,
													endY,
													new IEntityModifier.IEntityModifierListener() {

														@Override
														public void onModifierStarted(
																IModifier<IEntity> pModifier,
																IEntity pItem) {
															pItem.setVisible(true);
														}

														@Override
														public void onModifierFinished(
																IModifier<IEntity> pModifier,
																final IEntity pItem) {

															pItem.setVisible(false);
															pItem.setIgnoreUpdate(true);
															TLMNGameActivity.this
																	.runOnUpdateThread(new Runnable() {
																		public void run() {

																			pItem.detachSelf();
																		}
																	});
														}
													})));
						} else {
							pc.updateNumHand(0);
							cardSprite
									.registerEntityModifier(new SequenceEntityModifier(
											new DelayModifier(delay),
											new MoveModifier(
													dealTime,
													cardSprite.getX(),
													cardSprite.getY(),
													endX,
													endY,
													new IEntityModifier.IEntityModifierListener() {

														@Override
														public void onModifierStarted(
																IModifier<IEntity> pModifier,
																IEntity pItem) {

															pItem.setVisible(true);
														}

														@Override
														public void onModifierFinished(
																IModifier<IEntity> pModifier,
																final IEntity pItem) {
															pItem.setVisible(false);
															pItem.setIgnoreUpdate(true);
															TLMNGameActivity.this
																	.runOnUpdateThread(new Runnable() {
																		public void run() {
																			pc.updateNumHand(ii);
																			pItem.detachSelf();
																		}
																	});
														}
													})));
						}
					}
				}
			}
		}
	}

	protected void updateCardSpritePosition(boolean rearrange) {

		if (mCardListMainPlayer == null)
			return;
		if (mCardMap == null)
			return;

		if (rearrange) {

			reArrangeCardListData();
		} else {

			updatePositionCard();
		}
	}

	@Override
	protected void runBannerComplete(String bannerStr) {
		super.runBannerComplete(bannerStr);
		if (mGameData.mMissionType != null) {

			if (bannerStr.equalsIgnoreCase(mGameData.mMissionType
					.getDescription())) {

				// showDutyHolder();
			}
		}
	}

	protected float xOriginReachedMission, xOriginMissionSprite,
			yOriginMission;
	private FightInfoEntity mFightInfoEntity;

	@Override
	protected void onInitPlayer(InitPlayerCallBack pInitPlayerCallBack) {

		mPlayerHolderList = new ArrayList<TLMNPlayerHolder>();
		TLMNPlayerHolder playerHolder = null;

		// BOTTOM
		playerHolder = new TLMNPlayerHolder(PlayerLocation.BOTTOM);
		mScene.attachChild(playerHolder.playerSprite);
		getPlayerHolderList().add(playerHolder);
		mMainPlayerHolder = playerHolder;

		// RIGHT
		playerHolder = new TLMNPlayerHolder(PlayerLocation.RIGHT);
		mScene.attachChild(playerHolder.playerSprite);
		getPlayerHolderList().add(playerHolder);

		// TOP
		playerHolder = new TLMNPlayerHolder(PlayerLocation.TOP);
		mScene.attachChild(playerHolder.playerSprite);
		getPlayerHolderList().add(playerHolder);

		// LEFT
		playerHolder = new TLMNPlayerHolder(PlayerLocation.LEFT);
		mScene.attachChild(playerHolder.playerSprite);
		getPlayerHolderList().add(playerHolder);

		pInitPlayerCallBack.onInitPlayerComplete();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<TLMNPlayerHolder> getPlayerHolderList() {
		return (ArrayList<TLMNPlayerHolder>) super.getPlayerHolderList();
	}

	@Override
	public TLMNPlayerHolder getMainPlayerHolder() {

		return (TLMNPlayerHolder) super.getMainPlayerHolder();
	}

	private void showFightCase(Map<String, Long> pData, FightCardType pFightType) {

		if (pData == null)
			return;

		Long fightenPlayerId = pData.get(TLMNGame.FIGHT_ID);
		Long beFightenPlayerId = pData.get(TLMNGame.BE_FIGHTEN_ID);
		Long preBeFightenPlayerId = pData.get(TLMNGame.PRE_BE_FIGHTEN_ID);
		Long lostMoney = pData.get(TLMNGame.LOST_MONEY);
		Long gainMoney = pData.get(TLMNGame.GAIN_MONEY);
		Long preMoney = pData.get(TLMNGame.OLD_MONEY);

		if (mFightInfoEntity == null) {

			mFightInfoEntity = new FightInfoEntity(CENTER_X, CENTER_Y);
			mFightInfoEntity
					.setFightInfoListener(new OnFightInfoEntityListener() {

						@Override
						public void onHide() {

							if (GameData.shareData().getGame().getState() == GameState.PLAYING) {

								for (TLMNPlayerHolder cardPc : getPlayerHolderList()) {

									cardPc.hideFloatText();
								}
							}
						}
					});
			mScene.attachChild(mFightInfoEntity);
			mFightInfoEntity.setZIndex(Z_FUNC_BUTTON);
			mScene.sortChildren();
		}

		mFightInfoEntity.show(pFightType);

		// show money
		final TLMNPlayerHolder fighten = ((TLMNPlayerHolder) getPlayer(fightenPlayerId
				.longValue()));
		final TLMNPlayerHolder befighten = ((TLMNPlayerHolder) getPlayer(beFightenPlayerId
				.longValue()));

		if (preBeFightenPlayerId != null) {

			TLMNPlayerHolder prefighten = ((TLMNPlayerHolder) getPlayer(preBeFightenPlayerId
					.longValue()));
			if (prefighten != null)
				prefighten.showFloatText(iconTraLaiTR, preMoney.longValue(),
						true);
		}
		if (fighten != null) {
			fighten.showFloatText(iconChatjTR, lostMoney.longValue(),
					lostMoney.longValue() >= 0);
			// if (fightenPlayerId == GameData.shareData().getMyself().id) {
			// if (pFightType == FightCardType.TU_QUY) {
			// SoundManager.playTLMNChatTuQuyEffect();
			// } else {
			// SoundManager.playTLMNChatDoiThongEffect();
			// }
			// }
		}

		if (befighten != null) {
			befighten.showFloatText("", -gainMoney.longValue(), false);
			if (beFightenPlayerId == GameData.shareData().getMyself().id) {
				SoundManager.playTLMNBiChatEffect();
			} else {
				SoundManager.playTLMNChatEffect();
			}
		}
	}

	protected void showCardMainPlayer() {
		int i = 0;
		for (Card card : mGameData.getMainPlayer().cardList) {
			ClickableCard cs = new ClickableCard(ClickType.TURN, mXFirstCard
					+ i * mSpacingCard, BaseXeengGame.BOTTOM
					+ BaseXeengGame.BOTTOM_BAR_HEIGHT + 41f, tiledCardsTTR,
					getVertexBufferObjectManager());
			cs.setSize(CARD_WIDTH, CARD_HEIGHT);
			mCardBatch.attachChild(cs);
			cs.setData(card);
			cs.setAutoTurn(false);
			cs.setOnClickCardListener(mClickCardHandler);
			addCardSpriteToList(cs, card);
			i++;
		}
		updateTouchPriority();
	}

	protected boolean hiddenAllResultElement() {
		// if (!super.hiddenAllResultElement())
		// return true;
		Log.d("SamGameActivity", "Real cleaning");
		for (TLMNPlayerHolder pc : getPlayerHolderList()) {
			pc.moveToWaitingMode();

			// if (pc.isAvailable())
			// continue;
			pc.hideFloatText();
			pc.hideScoreBoard();
			pc.hideCardHand();
		}

		IEntity entity;
		for (int i = 0; i < mCardBatch.getChildCount(); i++) {

			entity = mCardBatch.getChildByIndex(i);
			entity.setIgnoreUpdate(true);
			entity.setVisible(false);
			entity.detachSelf();
		}
		mCardBatch.detachChildren();

		// clean all entity in card batch
		// this.runOnUpdateThread(new Runnable() {
		// public void run() {
		//
		// synchronized (mCardBatch) {
		//
		// for (int i = 0; i < mCardBatch.getChildCount(); i++) {
		//
		// mCardBatch.getChildByIndex(i).detachSelf();
		// }
		// }
		// }
		// });

		mShowFuncBtnTimer.reset();
		mScene.unregisterUpdateHandler(mShowFuncBtnTimer);
		showFuncPanel(false);
		if (mCardListMainPlayer != null) {
			mCardListMainPlayer.clear();
		}

		if (mCardMap != null) {
			mCardMap.clear();
		}

		prepareForWaitingMode();
		return true;
	}

	protected void reArrangeCardListData() {
		if (!isSortingCard) {
			Logger.getInstance().warn("TLMNGameActivity", "Before rearrange");
			mGameData.printCardListMainPlayer();
			if (mGameData.getMainPlayer().sortType == SortType.HORIZONTAL) {
				mGameData.getMainPlayer().cardList = (Vector<Card>) mGameData
						.sortCardListWithVerticalType(mGameData.getMainPlayer()
								.getCardList());
				mGameData.getMainPlayer().sortType = SortType.VERTICAL;
			} else if (mGameData.getMainPlayer().sortType == SortType.VERTICAL) {
				mGameData.getMainPlayer().cardList = (Vector<Card>) mGameData
						.sortCardListWithHorizontalType(mGameData
								.getMainPlayer().getCardList());
				mGameData.getMainPlayer().sortType = SortType.HORIZONTAL;
			}

			Logger.getInstance().warn("TLMNGameActivity", "After rearrange");
			mGameData.printCardListMainPlayer();
			SoundManager.playFoldCardEffect();
			updatePositionCard();
		}
	}

	protected void updatePositionCard() {
		isSortingCard = true;
		float spacing = mSpacingCard;
		for (int i = 0; i < mGameData.getMainPlayer().cardList.size(); i++) {

			Card cd = mGameData.getMainPlayer().cardList.get(i);
			ClickableCard cc = mCardMap.get(cd);
			if (cc != null) {

				cc.setX(mXFirstCard + spacing * i);
				cc.setZIndex(Z_FIRST_CARD + i);
				mScene.unregisterTouchArea(cc);
			}
		}

		mCardBatch.sortChildren();
		isSortingCard = false;
		updateTouchPriority();
	}

	protected void showFuncPanel() {
		showFuncPanel(true);
	}

	private void updateTouchPriority() {
		Log.e("checking here", "value check touch");
		if (mCardMap != null) {
			for (int i = 0; i < mGameData.getMainPlayer().cardList.size(); i++) {
				Card cd = mGameData.getMainPlayer().cardList.get(i);
				ClickableCard cc = mCardMap.get(cd);
				mScene.registerTouchArea(cc);
			}
		}

		mScene.unregisterTouchArea(btnSkip);
		mScene.unregisterTouchArea(btnArrangeHand);
		mScene.unregisterTouchArea(btnPlayCard);
		mScene.registerTouchArea(btnSkip);
		mScene.registerTouchArea(btnArrangeHand);
		mScene.registerTouchArea(btnPlayCard);

		if (mEmotionDialog != null) {
			if (mEmotionDialog.detachSelf()) {
				mScene.attachChild(mEmotionDialog);
			}
		}
	}

	protected void updateNumHandPlayer() {
		for (TLMNPlayer player : mGameData.getPlayerList()) {
			TLMNPlayerHolder playerHolder = (TLMNPlayerHolder) getPlayer(player.id);
			if (!playerHolder.isAvailable()
					&& player.id != GameData.shareData().getMyself().id) {
				playerHolder.updateNumHand(player.numHand);
			}
		}
	}

	@Override
	protected void onCreateGameActionButtons(Sprite bottomBar) {
		float y = BOTTOM_BAR_HEIGHT / 2;
		if (btnPlayCard == null) {
			btnPlayCard = new ActionButton(0, y, btnGameActionTR,
					mediumRegularFont,
					getString(R.string.game_action_play_card)) {

				@Override
				public void onClick() {
					ArrayList<ClickableCard> fightedCardSpriteList = null;
					for (ClickableCard cc : mCardListMainPlayer) {
						if (cc.isClicked()) {
							if (fightedCardSpriteList == null) {
								fightedCardSpriteList = new ArrayList<ClickableCard>();
							}
							fightedCardSpriteList.add(cc);
						}
					}

					if (fightedCardSpriteList != null
							&& fightedCardSpriteList.size() > 0) {

						Logger.getInstance().info(this,
								"fight list has some cards");
						List<Card> list = getDataCardFightedList(fightedCardSpriteList);
						Logger.getInstance().warn(this,
								"PRINT fighted card list");
						mGameData.printCardList((Vector<Card>) list);
						boolean isFighted = false;
						if (mGameData.lastCards.size() < 1) {
							if (mGameData.isValidTurn(list)) {
								isFighted = true;
								Logger.getInstance().info(this,
										"fight card is valid to fight");
							}
						} else {
							Logger.getInstance().warn(this,
									"PRINT LAST CARD LIST");
							mGameData.printCardList(mGameData.lastCards
									.get(mGameData.lastCards.size() - 1));
							Logger.getInstance().info(this,
									"Will check valid fighted card list");
							if (mGameData.isValidFight(list,
									mGameData.lastCards.get(mGameData.lastCards
											.size() - 1))) {
								Logger.getInstance().info(this,
										"fight card is valid to fight");
								isFighted = true;
							}
						}

						if (isFighted) {
							BusinessRequester.getInstance()
									.playTLMNTurn(
											GameData.shareData().getGame()
													.getMatchId(),
											BaseCardGame.buildCardStr(mGameData
													.sortCardList(list)));
						} else {
							BaseXeengGame.INSTANCE.toastOnUiThread(
									"Bài không hợp lệ", 1);
							// reset card sprite to origin location
						}
					}
					for (ClickableCard cc : mCardListMainPlayer) {
						cc.reset();
					}
				}
			};

			btnPlayCard.setPosition(RIGHT - btnEmoticon.getWidth()
					- btnPlayCard.getWidth() / 2 - 4 * SPACING, y);
			btnPlayCard.setZIndex(Z_FUNC_BUTTON);
			bottomBar.attachChild(btnPlayCard);
			btnPlayCard.setVisible(false);
			mScene.registerTouchArea(btnPlayCard);
		}

		if (btnArrangeHand == null) {
			btnArrangeHand = new ActionButton(0, y, btnGameActionTR,
					mediumRegularFont,
					getString(R.string.game_action_arrange_hand)) {

				@Override
				public void onClick() {
					updateCardSpritePosition(true);
				}
			};

			btnArrangeHand.setPosition(
					btnPlayCard.getX() - btnArrangeHand.getWidth() - SPACING
							* 4, y);
			btnArrangeHand.setZIndex(Z_FUNC_BUTTON);
			mScene.registerTouchArea(btnArrangeHand);
			bottomBar.attachChild(btnArrangeHand);
			btnArrangeHand.setVisible(false);
		}

		if (btnSkip == null) {
			btnSkip = new ActionButton(0, y, btnGameActionTR,
					mediumRegularFont,
					getString(R.string.game_action_skip_turn)) {

				@Override
				public void onClick() {
					for (ClickableCard cc : mCardListMainPlayer) {
						cc.reset();
					}
					BusinessRequester.getInstance().playTLMNTurn(
							GameData.shareData().getGame().getMatchId(), null);
					// disableTurnFunc();
				}
			};

			btnSkip.setPosition(
					btnArrangeHand.getX() - btnArrangeHand.getWidth() - SPACING
							* 4, y);
			btnSkip.setZIndex(Z_FUNC_BUTTON);
			mScene.registerTouchArea(btnSkip);
			bottomBar.attachChild(btnSkip);
			btnSkip.setVisible(false);
		}
		mScene.sortChildren();
	}

	private void prepareForNewMatch() {
		hideReadyState();
		showStartGameIfNeed(false);
	}

	private float[] generateFighingCardEndPoint(int turnedCardCount) {
		float possibleResuls[][] = new float[4][2];
		float d = 25f;

		possibleResuls[0][0] = CENTER_X;
		possibleResuls[0][1] = CENTER_Y + d;

		possibleResuls[1][0] = CENTER_X - CARD_WIDTH * MIN_CARD_SCALE / 2;
		possibleResuls[1][1] = CENTER_Y - CARD_WIDTH * MIN_CARD_SCALE / 2 + d;

		possibleResuls[2][0] = CENTER_X;
		possibleResuls[2][1] = CENTER_Y - CARD_WIDTH * MIN_CARD_SCALE - 10f + d;

		// For Special Win. The card will be centered.
		if (turnedCardCount == 1000) {
			possibleResuls[3][0] = CENTER_X;
			possibleResuls[3][1] = CENTER_Y;

			return possibleResuls[3];
		}

		// float x, y;
		// if (isRandom) {
		//
		// Random rand = new Random();
		// int range = 15;
		// x = CENTER_X - range + rand.nextInt(2 * range);
		// y = CENTER_Y - range + rand.nextInt(2 * range);
		// } else {
		//
		// x = CENTER_X;
		// y = CENTER_Y;
		// }
		// float result[] = { x, y };

		return possibleResuls[turnedCardCount % 3];
	}

	private void showFuncPanel(boolean show) {
		// Log.v("TLMN Activity", "-----> show function panel ");
		if (btnPlayCard != null) {
			btnPlayCard.setVisible(show);
			btnPlayCard.setIgnoreUpdate(!show);
		}

		if (btnSkip != null) {
			btnSkip.setVisible(show);
			btnSkip.setIgnoreUpdate(!show);
		}

		if (btnArrangeHand != null) {
			btnArrangeHand.setVisible(show);
			btnArrangeHand.setIgnoreUpdate(!show);
		}
	}

	private void enableFuncPanel(boolean isEnable) {

		updateTouchPriority();

		if (btnPlayCard != null) {

			btnPlayCard.mIsEnabled = isEnable;
		}

		if (btnSkip != null)
			btnSkip.mIsEnabled = isEnable;
		if (btnArrangeHand != null)
			btnArrangeHand.mIsEnabled = isEnable;
	}

	public void enableFight() {
		updateTouchPriority();

		if (btnPlayCard != null) {
			btnPlayCard.mIsEnabled = true;
		}

		if (btnSkip != null) {
			btnSkip.mIsEnabled = true;
		}
	}

	public void disableTurnFunc() {

		if (btnPlayCard != null)
			btnPlayCard.mIsEnabled = false;
		if (btnSkip != null)
			btnSkip.mIsEnabled = false;
	}

	private void flyFightCard(FightingCardHolder pFightCardList) {

		if (fightingCardList == null) {
			fightingCardList = new ArrayList<FightingCardHolder>();
		}

		fightingCardList.add(pFightCardList);
		mCardBatch.attachChild(pFightCardList);

		pFightCardList.fly();
	}

	private void clearTurnedCardList() {

		if (fightingCardList != null) {

			for (FightingCardHolder ch : fightingCardList) {

				ch.disappear();
			}
			fightingCardList.clear();
		}
	}

	private void showRewardCashForAllPlayer(WinType pType) {
		short index = 2;
		if (pType == WinType.NORMAL) {
			index = LOSE_INDEX;
		} else {
			index = TOI_TRANG_INDEX;
		}

		for (TLMNPlayer player : mGameData.getPlayerList()) {
			TLMNPlayerHolder playerHolder = (TLMNPlayerHolder) getPlayer(player.id);
			if (playerHolder.isAvailable())
				continue;

			if (player.state == PlayerState.RESULT) {
				if (index == TOI_TRANG_INDEX
						&& player.id == GameData.shareData().getMyself().id) {
					// TODO:
				} else {
					if (mGameData.mWinPlayerId == player.id) {
						playerHolder.showFloatText(iconWinTR,
								player.rewardCash, true);
					} else {
						playerHolder.showFloatText(iconLoseTR,
								player.rewardCash, false);
					}
				}
			}
		}
	}

	private void showRestCardForAllPlayer(boolean isIgnoreWinner) {

		for (TLMNPlayerHolder pc : getPlayerHolderList()) {

			if (pc.isAvailable())
				continue;
			if (pc.playerId == GameData.shareData().getMyself().id)
				continue;
			if (pc.playerId == mGameData.mWinPlayerId && isIgnoreWinner)
				continue;

			TLMNPlayer pd = (TLMNPlayer) mGameData.getPlayer(pc.playerId);
			if (pd != null) {

				if (pd.state == PlayerState.RESULT) {

					if (pd.cardList != null) {
						pc.showCardHand(pd.getCardList());
					}
				}
			}
		}
	}

	private List<Card> getDataCardFightedList(
			ArrayList<ClickableCard> fightedCardSpriteList) {

		List<Card> result = null;
		if (fightedCardSpriteList == null || fightedCardSpriteList.size() < 1)
			return null;
		result = new Vector<Card>();
		for (ClickableCard cc : fightedCardSpriteList) {

			result.add(cc.getCardData());
		}
		return result;
	}

	private void moveAllPlayerToPlayingPosition() {

		for (TLMNPlayerHolder ph : getPlayerHolderList()) {
			if (!ph.isAvailable()) {
				ph.moveToPlayingMode();
			}
		}
	}

	public void showGetPocker() {
		// redrawGetPocker();
		// hiddenAllResultElement();

		// Log.d("TLMNGameActivity", "Real cleaning");
		startGameBtn.changeState(StartGameState.HIDDEN);

		if (mAutoHiddenResultTimer != null
				&& mAutoHiddenResultTimer.isVisible()) {
			// mAutoHiddenResultTimer.setIgnoreUpdate(true);
			Log.d("TLMNGameActivity", "ReDraw get Pocker");
			// mAutoHiddenResultTimer.detachChildren();
			// mAutoHiddenResultTimer.detachSelf();
			// mAutoHiddenResultTimer = null;
			redrawGetPocker();
			return;
		}
		for (TLMNPlayerHolder pc : getPlayerHolderList()) {
			// pc.moveToWaitingMode();

			// if (pc.isAvailable())
			// continue;
			pc.hideFloatText();
			pc.hideCardHand1();
		}

		IEntity entity;
		if (mCardBatch != null) {
			for (int i = 0; i < mCardBatch.getChildCount(); i++) {
				entity = mCardBatch.getChildByIndex(i);
				entity.setIgnoreUpdate(true);
				entity.setVisible(false);
				entity.detachSelf();
			}
		}

		// prepareForWaitingMode();

		showGetPocker1();
	}

	public void showGetPocker1() {
		if (!isReadyShow) {
			return;
		}
		Log.d("TLMNGameActivity", "PrepareForNewMatch ...");
		if (iconLogo != null) {
			iconLogo.setVisible(false);
			iconLogo.setIgnoreUpdate(true);
		}

		prepareForNewMatch();
		for (Player pd : GameData.shareData().getGame().getPlayerList()) {

			if (pd.isTableMaster) {

				// get player sprite
				BasePlayerHolder pc = getPlayer(pd.id);
				if (pc != null) {
					pc.playerSprite.stopTimer();
				}
			}
		}

		moveAllPlayerToPlayingPosition();
		mShowFuncBtnTimer.reset();
		mScene.registerUpdateHandler(mShowFuncBtnTimer);
		Log.d("TLMNGameActivity", "DealCard ...");
		dealCard(52);

		// show turn of begin player
		long beginPlayerId = mGameData.mNextPlayerId;
		if (beginPlayerId > -1) {

			TLMNPlayerHolder beginPc = (TLMNPlayerHolder) getPlayer(beginPlayerId);
			if (beginPc != null) {
				beginPc.playerSprite.startTimer();
			}

			if (beginPlayerId == GameData.shareData().getMyself().id) {
				enableFight();
			} else {
				disableTurnFunc();
			}
		}

		if (GameData.shareData().getGame().getMainPlayer().state == PlayerState.OBSERVING) {
			showObservingView(true);

		} else {
			showObservingView(false);
		}
	}

	public void showTurn(boolean pIsNewRound, boolean pIsSkip,
			HashMap<String, Long> fightCase, FightCardType pType,
			Vector<Card> lastCard) {
		showTurn(pIsNewRound, pIsSkip, fightCase, pType,
				mGameData.prevPlayerId, mGameData.mNextPlayerId, lastCard);
	}

	public void showTurn(boolean pIsNewRound, boolean pIsSkip,
			HashMap<String, Long> fightCase, FightCardType pType,
			long prevPlayerId, long nextPlayerId, Vector<Card> lastCard) {
		if (!isReadyShow) {
			return;
		}

		boolean isNewRound = pIsNewRound;
		boolean isSkip = pIsSkip;

		TLMNPlayerHolder pc = (TLMNPlayerHolder) getPlayer(prevPlayerId);
		TLMNPlayerHolder nextPc = (TLMNPlayerHolder) getPlayer(nextPlayerId);
		TLMNPlayer tlmnpd = (TLMNPlayer) mGameData.getPlayer(prevPlayerId);
		TLMNPlayer nextpd = (TLMNPlayer) mGameData.getPlayer(nextPlayerId);

		if (nextPc != null) {
			if (!nextPc.isShowingCardHand()
					&& nextPc.playerId != mGameData.getMainPlayer().id) {

				nextPc.showCardHolder(nextpd.getCardList());
				Log.d("TLMNGameActivity", "Next player data card list "
						+ nextpd.getCardList());
			}
		}

		if (pc != null) {
			pc.playerSprite.stopTimer();
		}

		if (isNewRound) {

			clearTurnedCardList();
			if (nextPlayerId == GameData.shareData().getMyself().id) {

				SoundManager.playTurnEffect();
				enableFight();
			} else {
				disableTurnFunc();
			}
		} else if (!isSkip) {

			if (nextPlayerId == GameData.shareData().getMyself().id) {

				SoundManager.playTurnEffect();
				enableFuncPanel(true);
			} else {
				disableTurnFunc();
			}

			// blurry prev turn cards
			if (fightingCardList != null && fightingCardList.size() > 0) {

				fightingCardList.get(fightingCardList.size() - 1).blurry();
			}

			float startPoint[] = new float[2];

			SoundManager.playFightEffect();

			// lastCard = mGameData.lastCards.lastElement();

			if (prevPlayerId == GameData.shareData().getMyself().id) {

				// SoundManager.playFightEffect();
				Vector<Card> list = mGameData.getMainPlayer().getCardFromList(
						lastCard);
				if (list != null) {

					// compute position of fighted card list
					float total = 0;
					for (Card cd : list) {
						total += mCardMap.get(cd).getX();
					}
					startPoint[0] = total / list.size();
					BasePlayerHolder mpc = getMainPlayerHolder();
					startPoint[1] = mpc.defaultPosition[1];
					// remove card from main player's hand
					for (Card cd : list) {
						mCardMap.get(cd).disappear();
						mCardMap.get(cd).reset();
						mCardMap.remove(cd);

						mGameData.getMainPlayer().cardList.remove(cd);
					}
					disableTurnFunc();
					updateCardSpritePosition(false);
				}
			} else {

				startPoint[0] = pc.playingPosition[0];
				startPoint[1] = pc.playingPosition[1];
				if (tlmnpd != null) {

					SoundManager.playDealCardEffect();
					pc.updateNumHand(tlmnpd.numHand);
				}
			}

			float endPoint[] = fightingCardList != null ? generateFighingCardEndPoint(fightingCardList
					.size()) : generateFighingCardEndPoint(0);
			flyFightCard(new FightingCardHolder(lastCard, startPoint, endPoint,
					pc.playerLoc));

		} else {

			if (prevPlayerId == GameData.shareData().getMyself().id) {

				disableTurnFunc();
			}

			if (nextPlayerId == GameData.shareData().getMyself().id) {

				SoundManager.playTurnEffect();

				if (UserPreference.sharePreference().isVibrationOn()) {
					((Vibrator) getSystemService(VIBRATOR_SERVICE))
							.vibrate(800);
				}
				enableFuncPanel(true);
			} else {

				disableTurnFunc();
			}
		}

		// show chat tu quy hoac doi thong neu co
		showFightCase(fightCase, pType);

		if (nextPc != null) {
			nextPc.playerSprite.startTimer();
		}

		// Attempting to hide all other player's show hand as a desperate
		// method....
		for (TLMNPlayerHolder playerHolder : getPlayerHolderList()) {
			playerHolder.hideCardHand();
		}
	}

	@Override
	public boolean showEndMatch(boolean isAnimated) {
		if (!super.showEndMatch(isAnimated))
			return false;
		showFuncPanel(false);

		// show last card of win player
		TLMNPlayerHolder winPlayerConf = (TLMNPlayerHolder) getPlayer(mGameData.mWinPlayerId);
		if (winPlayerConf != null) {

			Logger.getInstance().info(this, "SHOW LAST CARD OF WIN PLAYER");
			winPlayerConf.playerSprite.stopTimer();
			float startPoint[] = new float[2];

			// blurry prev turn cards
			if (fightingCardList != null && fightingCardList.size() > 0) {

				fightingCardList.get(fightingCardList.size() - 1).blurry();
			}

			if (mGameData.mWinPlayerId == GameData.shareData().getMyself().id) {

				Logger.getInstance().info(this, "WINNER IS MAIN PLAYER");
				if (mGameData.mWinType != WinType.TOI_TRANG) {

					if (mCardListMainPlayer != null
							&& mCardListMainPlayer.size() > 0) {

						for (ClickableCard cc : mCardListMainPlayer) {
							cc.disappear();
						}
					}

					if (mGameData.lastCards != null
							&& mGameData.lastCards.size() > 0) {

						FightingCardHolder fch = new FightingCardHolder(
								mGameData.lastCards.get(mGameData.lastCards
										.size() - 1), null, null, null);
						if (fightingCardList == null) {
							fightingCardList = new ArrayList<FightingCardHolder>();
						}

						fch.blurry();
						fightingCardList.add(fch);
						mCardBatch.attachChild(fch);
						fch.showNow(generateFighingCardEndPoint(0));
					}
				}

			} else {

				if (mGameData.lastCards != null
						&& mGameData.lastCards.size() > 0) {

					if (!winPlayerConf.isAvailable()) {

						startPoint[0] = winPlayerConf.playingPosition[0];
						startPoint[1] = winPlayerConf.playingPosition[1];

						float endPoint[] = generateFighingCardEndPoint(0);
						FightingCardHolder fch = new FightingCardHolder(
								mGameData.lastCards.get(mGameData.lastCards
										.size() - 1), startPoint, endPoint,
								winPlayerConf.playerLoc);
						fch.blurry();
						flyFightCard(fch);
					}
				}
			}
		}

		switch (mGameData.mWinType) {
		case TOI_TRANG:
		case TU_QUY_HAI:
		case DOI_THONG_5:
		case DOI_THONG_6:
		case SANH_RONG:
			hideReadyState();
			startGameBtn.changeState(StartGameState.HIDDEN);
			showStartGameIfNeed(false);
			getMainPlayerHolder().moveToPlayingMode();

			ITextureRegion resultSpecialWinTR = null;

			switch (mGameData.mWinType) {
			case TOI_TRANG:
				resultSpecialWinTR = resultToiTrangTuQuyTR;
				break;
			case TU_QUY_HAI:
				resultSpecialWinTR = resultToiTrangTuQuyTR;
				break;
			case DOI_THONG_5:
				resultSpecialWinTR = resultToiTrang5DoiThongTR;
				break;
			case DOI_THONG_6:
				resultSpecialWinTR = resultToiTrang6DoiTR;
				break;
			case SANH_RONG:
				resultSpecialWinTR = resultToiTrangSanhRongTR;
				break;
			default:
				resultSpecialWinTR = null;
				break;
			}
			if (resultSpecialWinTR != null) {
				if (mGameData.mWinPlayerId == GameData.shareData().getMyself().id) {
					TLMNPlayer pd = mGameData.getMainPlayer();
					showSpecialEndMatchEntity(resultSpecialWinTR,
							resultToiTrangTR,
							CommonUtils.formatRewardCash(pd.rewardCash), true);
					showRewardCashForAllPlayer(mGameData.mWinType);
				} else {
					for (final TLMNPlayer player : mGameData.getPlayerList()) {
						final TLMNPlayerHolder playerHolder = (TLMNPlayerHolder) getPlayer(player.id);

						if (playerHolder.isAvailable())
							continue;

						if (player.state == PlayerState.RESULT) {
							if (mGameData.mWinPlayerId == player.id) {
								playerHolder.showFloatText(resultSpecialWinTR,
										player.rewardCash, true);

								playerHolder.hideCardHand();

								List<Card> sortedCardList = mGameData
										.sortCardList(player.getCardList());

								clearTurnedCardList();
								flyFightCard(new FightingCardHolder(
										sortedCardList,
										playerHolder.playingPosition,
										generateFighingCardEndPoint(1000),
										playerHolder.playerLoc));
							} else {
								playerHolder.showFloatText(iconLoseTR,
										player.rewardCash, false);
							}
						}
					}
				}
			}

			SoundManager.playTLMNPerfectWinEffect();
			showCardMainPlayer();

			break;
		case NORMAL:
			// mScene.registerUpdateHandler(new TimerHandler(3f, new
			// ITimerCallback() {
			//
			// @Override
			// public void onTimePassed(TimerHandler arg0) {
			// runOnUpdateThread(new Runnable() {
			//
			// @Override
			// public void run() {
			if (GameData.shareData().getGame().getMainPlayer().state == PlayerState.RESULT) {
				if (mGameData.mWinPlayerId == GameData.shareData().getMyself().id) {
					SoundManager.playWinEffect();
				} else {
					SoundManager.playLoseEffect();
				}
			} else {
				SoundManager.playEndMatchEffect();
			}

			showRewardCashForAllPlayer(mGameData.mWinType);
			// }
			// });
			// }
			// }));
			break;
		}

		showRestCardForAllPlayer(true);

		if (getPlayerHolderList() != null) {

			for (TLMNPlayerHolder pc : getPlayerHolderList()) {

				if (pc.isAvailable())
					continue;
				TLMNPlayer pd = (TLMNPlayer) mGameData.getPlayer(pc.playerId);
				pc.showCardHolder(pd.getCardList());
				pc.hideCardHolder();

				if (pd.state == PlayerState.RESULT) {
					pc.showScoreBoard(pd.numHand);
				}
			}
		}

		startResultTimer();

		showObservingView(false);
		return true;
	}

	@Override
	public void startResultTimer() {
		super.startResultTimer();

		mScene.registerUpdateHandler(new TimerHandler(3f, new ITimerCallback() {

			@Override
			public void onTimePassed(TimerHandler arg0) {
				for (TLMNPlayerHolder pc : getPlayerHolderList()) {
					pc.hideFloatText();
				}
				mScene.unregisterUpdateHandler(arg0);
			}
		}));
	}

	//Apply current table context after receiving reconnection message
	public void showReconnection(boolean status) {
		//Reconnect success
		if (status) {

			Logger.getInstance().info(this, "reconnection successfully");
			if (mGameData.getState() == GameState.PLAYING) {
				//Reset main player card in hand
				if (mCardListMainPlayer != null) {

					for (ClickableCard cc : mCardListMainPlayer) {

						mScene.unregisterTouchArea(cc);
						cc.setIgnoreUpdate(true);
						cc.setVisible(false);
					}
					mCardListMainPlayer.clear();
					mCardMap.clear();
				}
				
				//Reset fighting card on the table
				if (fightingCardList != null) {

					for (final FightingCardHolder fch : fightingCardList) {

						fch.setIgnoreUpdate(true);
						fch.setVisible(false);
						TLMNGameActivity.this.runOnUpdateThread(new Runnable() {
							public void run() {

								fch.detachSelf();
							}
						});
					}
					fightingCardList.clear();
				}

				for (TLMNPlayerHolder pc : getPlayerHolderList()) {

					if (pc.isAvailable())
						continue;
					if (pc.playerId == GameData.shareData().getMyself().id) {
						continue;
					}
					pc.playerSprite.stopTimer();
					TLMNPlayer pd = (TLMNPlayer) mGameData
							.getPlayer(pc.playerId);
					if (pd != null) {
						pc.updateNumHand(pd.numHand);
					}
				}

				showCardMainPlayer();

				long nextPlayerId = mGameData.mNextPlayerId;
				if (nextPlayerId != -1) {

					TLMNPlayerHolder pc = (TLMNPlayerHolder) getPlayer(nextPlayerId);
					if (pc != null) {

						pc.playerSprite.startTimer();
					}
				}

				// showGetPocker();
			} else {
				Log.d("TLMNGameActivity", "Reconnection case");
				hiddenAllResultElement();
			}
		} else {

			Logger.getInstance().warn(this,
					"Reconnect successfully but state of game is break");
			BusinessRequester.getInstance().leaveGame(
					GameData.shareData().getGame().getMatchId());
			finish();
		}
	}

	protected TimerHandler mShowFuncBtnTimer = new TimerHandler(2,
			new ITimerCallback() {

				@Override
				public void onTimePassed(TimerHandler arg0) {

					showFuncPanel();
				}
			});

	private onClickCardListener mClickCardHandler = new onClickCardListener() {

		@Override
		public void onClick(ClickableCard pCardSprite) {

			if (pCardSprite.isClicked()) {

				pCardSprite.turnCard();
			} else {

				pCardSprite.reset();
			}
		}
	};

	private class FightInfoEntity extends Entity {

		private OnFightInfoEntityListener mListener;
		private TiledSprite mFightCardTypeSprite;
		private TimerHandler mAutoHideTimer = new TimerHandler(3,
				new ITimerCallback() {

					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {

						setIgnoreUpdate(true);
						setVisible(false);
						if (mListener != null) {

							mListener.onHide();
						}
					}
				});

		private FightInfoEntity(float pX, float pY) {

			super(pX, pY);

			mFightCardTypeSprite = new TiledSprite(0, 0, mFightInfoTTR,
					getVertexBufferObjectManager());

			if (BaseXeengGame.SCREEN_RATIO > 1.5f) {
				mFightCardTypeSprite.setSize(mFightInfoTTR.getWidth() / 2,
						mFightInfoTTR.getHeight() / 2);
			}

			this.attachChild(mFightCardTypeSprite);

			setIgnoreUpdate(true);
			setVisible(false);
		}

		private void show(FightCardType pType) {

			if (pType.toValue() < 0)
				return;
			mAutoHideTimer.reset();
			this.clearUpdateHandlers();
			setIgnoreUpdate(false);
			setVisible(true);

			if (pType.toValue() > -1 && pType.toValue() < 5) {

				switch (pType) {
				case DOI_THONG_3:
				case DOI_THONG_4:
				case DOI_THONG_5:
				case DOI_THONG_6:
					SoundManager.playTLMNChatDoiThongEffect();
					switch (pType) {
					case DOI_THONG_3:
						mFightCardTypeSprite.setCurrentTileIndex(0);
						break;
					case DOI_THONG_4:
						mFightCardTypeSprite.setCurrentTileIndex(1);
						break;
					default:
						break;
					}
					break;
				case TU_QUY:
					mFightCardTypeSprite.setCurrentTileIndex(2);
					SoundManager.playTLMNChatTuQuyEffect();
					break;
				default:
					break;
				}
			}

			// animation
			mFightCardTypeSprite.setScale(2);
			mFightCardTypeSprite.registerEntityModifier(new ScaleModifier(0.5f,
					2, 1, new IEntityModifier.IEntityModifierListener() {

						@Override
						public void onModifierStarted(
								IModifier<IEntity> pModifier, IEntity pItem) {
						}

						@Override
						public void onModifierFinished(
								IModifier<IEntity> pModifier, IEntity pItem) {

							FightInfoEntity.this
									.registerUpdateHandler(mAutoHideTimer);
						}
					}, EaseCircularIn.getInstance()));
		}

		private void setFightInfoListener(OnFightInfoEntityListener pListener) {

			mListener = pListener;
		}

		@Override
		public void setVisible(boolean pVisible) {

			for (int i = 0; i < getChildCount(); i++) {

				getChildByIndex(i).setVisible(pVisible);
			}
			super.setVisible(pVisible);
		}

		@Override
		public void setIgnoreUpdate(boolean pIgnoreUpdate) {

			for (int i = 0; i < getChildCount(); i++) {

				getChildByIndex(i).setIgnoreUpdate(pIgnoreUpdate);
			}
			super.setIgnoreUpdate(pIgnoreUpdate);
		}
	}

	public interface OnFightInfoEntityListener {

		public void onHide();
	}

	@Override
	protected void handleEventPlayerLeft(long leftPlayerId) {

	}

	@Override
	protected void redrawGameAction(BaseXEGameAction action) throws Exception {
		super.redrawGameAction(action);
		switch (action.getType()) {
		case BaseXEGameAction.ACTION_TLMN_SHOW_TURN: {
			XETLMNShowTurnAction tlmnShowTurnAction = (XETLMNShowTurnAction) action;
			showTurn(tlmnShowTurnAction.isNewRound(),
					tlmnShowTurnAction.isSkip(),
					tlmnShowTurnAction.getFightMaps(),
					tlmnShowTurnAction.getFightType(),
					tlmnShowTurnAction.getPrevPlayerId(),
					tlmnShowTurnAction.getCurrentPlayerId(),
					tlmnShowTurnAction.getLastCard());
			break;
		}
		case BaseXEGameAction.ACTION_TLMN_SHOW_GET_POCKER: {
			redrawGetPocker();
			break;
		}
		}
	}

	private void redrawGetPocker() {
		hiddenAllResultElement();
		for (Player pd : mGameData.getPlayerList()) {
			Log.d("RedrawGame", "Set player state to PLAYING " + pd.id);
			pd.state = PlayerState.PLAYING;
		}
		if (mAutoHiddenResultTimer != null
				&& mAutoHiddenResultTimer.isVisible()) {
			mAutoHiddenResultTimer.stop();
			mAutoHiddenResultTimer.setVisible(false);
			mAutoHiddenResultTimer.setIgnoreUpdate(true);
			mAutoHiddenResultTimer.detachSelf();
		}
		showGetPocker1();
	}
}
