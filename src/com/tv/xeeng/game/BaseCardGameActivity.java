package com.tv.xeeng.game;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;
import org.andengine.util.texturepack.TexturePack;
import org.andengine.util.texturepack.TexturePackLoader;
import org.andengine.util.texturepack.TexturePackTextureRegionLibrary;
import org.andengine.util.texturepack.exception.TexturePackParseException;

import android.util.Log;

import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.game.elements.common.PlayerSprite.PlayerLocation;
import com.tv.xeeng.game.elements.common.SpecialEndMatchEntity;
import com.tv.xeeng.game.elements.common.TimerText;
import com.tv.xeeng.game.elements.common.TimerText.OnTimerTextListener;
import com.tv.xeeng.game.elements.player.CardGamePlayerHolder;
import com.tv.xeeng.gamedata.GameData;

public abstract class BaseCardGameActivity extends BaseXeengGame {

	public static class CHAIR_TAG {
		public static int BOTTOM_CHAIR = 1000;
		public static int LEFT_CHAIR = 1001;
		public static int RIGHT_CHAIR = 1002;
		public static int TOP_LEFT_CHAIR = 1003;
		public static int TOP_RIGHT_CHAIR = 1004;
		public static int BOTTOM_lEFT_CHAIR = 1005;
		public static int BOTTOM_RIGHT_CHAIR = 1006;
		public static int TOP_CHAIR = 1007;
	}

	// ===========================================================
	// Constants
	// ===========================================================\

	public static final short BACK_CARD_INDEX = 52;
	public static final short BAI_BOC_CARD_INDEX = 53;

	public static float MIN_CARD_SCALE = 0.75f;

	public static ITextureRegion iconWinTR, iconLoseTR;

	private static final String ICON_WIN_RES = "tlmn/icon_thang.png";
	private static final String ICON_WIN_RES_HD = "tlmn/icon_thang_hd.png";

	private static final String ICON_LOSE_RES = "tlmn/icon_thua.png";
	private static final String ICON_LOSE_RES_HD = "tlmn/icon_thua_hd.png";

	protected static final String SCORE_BG = "score_board_bg.png";

	protected static final String SLIDER_BG_RES = "bet_slide_bg.png";
	protected static final String INDICATOR_RES = "bet_slide_money_bg.png";
	protected static final String THUMB_RES = "bet_slide_track_slider.png";

	protected static final String BTN_CHAT_RES = "game_btn_chat.png";
	protected static final String BTN_CHAT_RES_HD = "game_btn_chat_hd.png";
	protected static final String BTN_EMOTION_RES = "game_btn_emotion.png";
	protected static final String BTN_EMOTION_RES_HD = "game_btn_emotion_hd.png";
	protected static final String BTN_GAME_ACTION_RES = "btn_ingame_action.png";

	protected static final String BG_BOTTOM_BAR_RES = "bottom_bar_ingame_bg.png";
	protected static final String TILED_CARDS_RES = "gfx/card/icon_cards_hd.xml";
	protected static final String TILED_CARDS_HD_RES = "gfx/card/icon_cards_hd.xml";
	protected static final String CARD_PILE_NUMBER_RES = "cards_number_bg.png";
	// ===========================================================
	// Fields
	// ===========================================================
	protected boolean cleared = false;
	// icon%02d
	public ITextureRegion bottomBarBgTR;
	public ITextureRegion btnGameActionTR;

	protected Sprite iconLogo;
	protected ITextureRegion iconLogoTR;

	private BuildableBitmapTextureAtlas backgroundTextureAtlas;
	private ITextureRegion tableTR;
	private ITextureRegion chairTR;

	private BuildableBitmapTextureAtlas cardGameFuncBtnsTextureAtlas;

	// Bet slider resources
	protected BuildableBitmapTextureAtlas sliderTextureAtlas;
	protected ITextureRegion sliderBackgroundTR;
	protected ITextureRegion thumbTR;
	protected ITextureRegion indicatorTR;

	// Cards
	private BuildableBitmapTextureAtlas specificTextureAtlas;
	public static ITiledTextureRegion tiledCardsTTR;
	public ITextureRegion cardPileNumberTR;

	// Special end match
	private SpecialEndMatchEntity specialEndMatchEntity;

	public static ITextureRegion scoreBgTR;

	public static ITexture animator;
	public static ITextureRegion animatorRegion;

	public static ITexture label;
	public static ITextureRegion labelRegion;

	// public static ITexture startedPlayerTT;
	// public static ITextureRegion startedPlayerTR;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onLoadBackgroundResource() {
		super.onLoadBackgroundResource();

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/card/");

		String chairRes;

		try {
			this.animator = new BitmapTexture(getTextureManager(),
					new IInputStreamOpener() {

						@Override
						public InputStream open() throws IOException {
							// TODO Auto-generated method stub
							// Log.v("SamEffect", " ---> screen_ratio: " +
							// SCREEN_RATIO);
							if (SCREEN_RATIO > 1.5) {
								return getAssets().open(
										"gfx/card/sam/icon_sam_light_hd.png");
							}
							return getAssets().open(
									"gfx/card/sam/icon_sam_light.png");
						}
					});

			this.animator.load();
			this.animatorRegion = TextureRegionFactory
					.extractFromTexture(animator);

			this.label = new BitmapTexture(getTextureManager(),
					new IInputStreamOpener() {

						@Override
						public InputStream open() throws IOException {
							// TODO Auto-generated method stub
							Log.v("SamEffect", " ---> screen_ratio: "
									+ SCREEN_RATIO);
							if (SCREEN_RATIO > 1.5) {
								return getAssets().open(
										"gfx/card/sam/icon_sam_hd.png");
							}
							return getAssets()
									.open("gfx/card/sam/icon_sam.png");
						}
					});
			this.label.load();
			this.labelRegion = TextureRegionFactory.extractFromTexture(label);

			samAlertTT = new BitmapTexture(getTextureManager(),
					new IInputStreamOpener() {

						@Override
						public InputStream open() throws IOException {
							// TODO Auto-generated method stub
							// Log.v("SamEffect", " ---> screen_ratio: " +
							// SCREEN_RATIO);
							if (SCREEN_RATIO > 1.5) {
								return getAssets().open(
										"gfx/card/sam/icon_sam_bao1_hd.png");
							}
							return getAssets().open(
									"gfx/card/sam/icon_sam_bao1.png");
						}
					});

			this.samAlertTT.load();
			this.samAlertTR = TextureRegionFactory
					.extractFromTexture(samAlertTT);

		} catch (IOException e) {
			Debug.e(e);
		}

		if (SCREEN_RATIO > 1.5f) {
			backgroundTextureAtlas = new BuildableBitmapTextureAtlas(
					getTextureManager(), 2048, 1024, mNormalTextureOption);
			if (GameData.shareData().currentRoomLevel != null
					&& GameData.shareData().currentRoomLevel.isVip()) {
				// VIP RESOURCE YAY
				mTableBackgroundResName = "bg_table_vip_hd.png";
				chairRes = "chair_vip_hd.png";
			} else {
				mTableBackgroundResName = "bg_table_hd.png";
				chairRes = "chair_normal_hd.png";
			}
		} else {
			backgroundTextureAtlas = new BuildableBitmapTextureAtlas(
					getTextureManager(), 1024, 512, mNormalTextureOption);
			if (GameData.shareData().currentRoomLevel != null
					&& GameData.shareData().currentRoomLevel.isVip()) {
				// VIP RESOURCE YAY :D
				mTableBackgroundResName = "bg_table_vip.png";
				chairRes = "chair_vip.png";
			} else {
				mTableBackgroundResName = "bg_table.png";
				chairRes = "chair_normal.png";
			}
		}

		tableTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				backgroundTextureAtlas, this, mTableBackgroundResName);

		chairTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				backgroundTextureAtlas, this, chairRes);

		try {
			backgroundTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			backgroundTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			// TODO
			e.printStackTrace();
		}
	}

	public void startResultTimer() {
		cleared = false;

		// TODO
		// Lets try something interesting here.
		// As long as the end match's result is showing, enqueue further
		// actions.

		if (mAutoHiddenResultTimer == null) {
			mAutoHiddenResultTimer = new TimerText(CENTER_X, CENTER_Y,
					largeBoldFont, "", 2, new TextOptions(
							HorizontalAlign.CENTER),
					getVertexBufferObjectManager());
			mAutoHiddenResultTimer.setColor(Color.WHITE);
			mAutoHiddenResultTimer.setZIndex(Z_START_GAME);
			mAutoHiddenResultTimer
					.setOnTimerListener(new OnTimerTextListener() {
						@Override
						public void onTimeOut() {
							Log.d("BaseCardGame", "Timeout case");

							// Check this out
							isReadyShow = true;
							hiddenAllResultElement();

							if (leavingGame) {
								BusinessRequester.getInstance().leaveGame(
										GameData.shareData().getGame()
												.getMatchId());
							} else {
								runOnUpdateThread(new Runnable() {
									@Override
									public void run() {
										processRedrawActions();
									}
								});
							}
						}
					});
			mScene.attachChild(mAutoHiddenResultTimer);
		}

		Log.d("BaseCardGame", "Start timer");
		mAutoHiddenResultTimer.setupTimer(5);

		mAutoHiddenResultTimer.setVisible(false);

		isReadyShow = false;
		mAutoHiddenResultTimer.start();
	}

	protected boolean hiddenAllResultElement() {
		if (cleared == true) {
			return false;
		}
		cleared = true;
		return true;
	}

	@Override
	protected void unloadBackgroundResource() {
		super.unloadBackgroundResource();
		backgroundTextureAtlas.unload();
	}

	@Override
	protected void onCreateBackground() {
		super.onCreateBackground();

		Sprite sTable = new Sprite(CENTER_X, CENTER_Y, tableTR,
				getVertexBufferObjectManager());
		sTable.setWidth(TABLE_WIDTH);
		sTable.setHeight(TABLE_HEIGHT);
		sTable.setZIndex(30);

		if (iconLogoTR != null) {
			iconLogo = new Sprite(sTable.getWidth() / 2,
					sTable.getHeight() / 2, 116, 97, iconLogoTR,
					getVertexBufferObjectManager());
			iconLogo.setVisible(!GameData.shareData().getMyself().isTableMaster);
			iconLogo.setIgnoreUpdate(!GameData.shareData().getMyself().isTableMaster);
			sTable.attachChild(iconLogo);
		}
		mScene.attachChild(sTable);
	}

	protected Sprite getChairSprite(PlayerLocation position) {
		Sprite sprite = new Sprite(CENTER_X, CENTER_Y, chairTR,
				getVertexBufferObjectManager());
		sprite.setZIndex(15);
		sprite.setX(CardGamePlayerHolder.getXForPlayerPosition(position));
		sprite.setY(CardGamePlayerHolder.getYForPlayerPosition(position));
		sprite.setSize(75, 75);

		switch (position) {
		case TOP:
		case TOP_RIGHT:
		case TOP_LEFT:
			break;

		case RIGHT:
		case RIGHT_TOP:
		case RIGHT_BOTTOM:
			sprite.setRotation(90);
			break;

		case BOTTOM:
		case BOTTOM_RIGHT:
		case BOTTOM_LEFT:
			sprite.setRotation(180);
			break;

		case LEFT:
		case LEFT_TOP:
		case LEFT_BOTTOM:
			sprite.setRotation(-90);
			break;
		}

		return sprite;
	}

	protected void eraseChairSprite(PlayerLocation position) {

	}

	@Override
	protected void onLoadFuncResource() {
		super.onLoadFuncResource();
		cardGameFuncBtnsTextureAtlas = new BuildableBitmapTextureAtlas(
				getTextureManager(), 512, 512);

		String btnEmoticonRes, btnChatRes;
		if (SCREEN_RATIO > 1.5f) {
			btnEmoticonRes = BTN_EMOTION_RES_HD;
			btnChatRes = BTN_CHAT_RES_HD;
		} else {
			btnEmoticonRes = BTN_EMOTION_RES;
			btnChatRes = BTN_CHAT_RES;
		}
		// for emotion button
		emoticonBtnTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				cardGameFuncBtnsTextureAtlas, this, btnEmoticonRes);
		// for bottom bar background
		bottomBarBgTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				cardGameFuncBtnsTextureAtlas, this, BG_BOTTOM_BAR_RES);
		// for action buttons background
		btnGameActionTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(cardGameFuncBtnsTextureAtlas, this,
						BTN_GAME_ACTION_RES);
		// for chat button
		chatTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				cardGameFuncBtnsTextureAtlas, this, btnChatRes);

		try {
			cardGameFuncBtnsTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			cardGameFuncBtnsTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void unloadFuncResource() {
		super.unloadFuncResource();
		cardGameFuncBtnsTextureAtlas.unload();
	}

	@Override
	protected void onLoadSpecificGameResource() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/card/");

		String cardRes;
		if (SCREEN_RATIO > 1.5f) {
			specificTextureAtlas = new BuildableBitmapTextureAtlas(
					getTextureManager(), 1024, 512, mNormalTextureOption);
			cardRes = TILED_CARDS_HD_RES;
		} else {
			specificTextureAtlas = new BuildableBitmapTextureAtlas(
					getTextureManager(), 1024, 512, mNormalTextureOption);
			cardRes = TILED_CARDS_RES;
		}
		try {
			TexturePack texturePack = new TexturePackLoader(getAssets(),
					getTextureManager()).loadFromAsset(cardRes,
					"gfx/card/");
			TexturePackTextureRegionLibrary texturePackLibrary = texturePack
					.getTexturePackTextureRegionLibrary();
			texturePack.loadTexture();

			int[] ids = new int[] { 18, 22, 26, 30, 34, 38, 42, 46, 50, 2,
					6, 10, 14, 19, 23, 27, 31, 35, 39, 43, 47, 51, 3, 7,
					11, 15, 20, 24, 28, 32, 36, 40, 44, 48, 52, 4, 8, 12,
					16, 21, 25, 29, 33, 37, 41, 45, 49, 53, 5, 9, 13, 17,
					0, 1, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65,
					66, 67, 68, 69, 70, 71, 72 };
			ITextureRegion[] regions = new ITextureRegion[ids.length];
			for (int i = 0; i < ids.length; i++) {
				regions[i] = texturePackLibrary.get(ids[i]);
			}
			tiledCardsTTR = new TiledTextureRegion(texturePack.getTexture(),
					regions);
		} catch (TexturePackParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cardPileNumberTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(specificTextureAtlas, this,
						CARD_PILE_NUMBER_RES);
		scoreBgTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				specificTextureAtlas, this, SCORE_BG);

		String iconWinRes = ICON_WIN_RES;
		String iconLoseRes = ICON_LOSE_RES;
		if (SCREEN_RATIO > 1.5f) {
			iconWinRes = ICON_WIN_RES_HD;
			iconLoseRes = ICON_LOSE_RES_HD;
		}

		iconWinTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				specificTextureAtlas, this, iconWinRes);
		iconLoseTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				specificTextureAtlas, this, iconLoseRes);

		try {
			specificTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			specificTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			// TODO
			e.printStackTrace();
		}
	}

	@Override
	protected void renderSpecificElements(
			RenderElementCallBack pRenderElementCallBack) {
	}

	@Override
	protected void unloadSpecificResources() {
		specificTextureAtlas.unload();
	}

	@Override
	protected void handleEventPlayerLeft(long leftPlayerId) {
	}

	private volatile boolean actionButtonLoaded = false;

	protected void loadGameActionButtons() {
		if (actionButtonLoaded == true)
			return;
		onCreateGameActionButtons(bottomBar);
		actionButtonLoaded = true;
	}

	protected abstract void onCreateGameActionButtons(Sprite bottomBar);

	protected void onCreateSpecificGame() {
		super.onCreateSpecificGame();
		loadGameActionButtons();
	}

	@Override
	protected void onCreateFuncButton() {
		super.onCreateFuncButton();
		createBottomBar();
	}

	@Override
	protected void showStartGameIfNeed(boolean animated) {
		super.showStartGameIfNeed(animated);
		if (iconLogo != null) {
			iconLogo.setVisible(!GameData.shareData().getMyself().isTableMaster);
			iconLogo.setIgnoreUpdate(GameData.shareData().getMyself().isTableMaster);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	protected void showSpecialEndMatchEntity(ITextureRegion specialText,
			ITextureRegion secondSpecialText, CharSequence text,
			boolean isVictory) {
		if (specialEndMatchEntity == null) {
			specialEndMatchEntity = new SpecialEndMatchEntity();
			specialEndMatchEntity.setZIndex(95);
			specialEndMatchEntity.setVisible(false);
			mScene.attachChild(specialEndMatchEntity);
			// mScene.sortChildren();
		}

		if (secondSpecialText != null) {
			specialEndMatchEntity.show(specialText, secondSpecialText, text,
					isVictory);
		} else {
			specialEndMatchEntity.show(specialText, text, isVictory);
		}
		mScene.registerTouchArea(specialEndMatchEntity);

		mScene.registerUpdateHandler(new TimerHandler(5f, new ITimerCallback() {

			@Override
			public void onTimePassed(TimerHandler arg0) {
				specialEndMatchEntity.setVisible(false);
				mScene.unregisterUpdateHandler(arg0);
				mScene.unregisterTouchArea(specialEndMatchEntity);
			}
		}));
	}

	protected void showSpecialEndMatchEntity(ITextureRegion specialText,
			CharSequence text, boolean isVictory) {
		showSpecialEndMatchEntity(specialText, null, text, isVictory);
	}

	protected void showSpecialEndMatchEntity(ITextureRegion specialText,
			long text) {
		showSpecialEndMatchEntity(specialText, "" + text, text >= 0);
	}

	protected void showSpecialEndMatchEntity(CharSequence text,
			boolean isVictory) {
		showSpecialEndMatchEntity(null, text, isVictory);
	}

	private void createBottomBar() {
		float x = 0;
		float y = 0;
		bottomBar = new Sprite(DESIGN_WINDOW_WIDTH_PIXELS / 2,
				bottomBarBgTR.getHeight() / 2, bottomBarBgTR,
				getVertexBufferObjectManager());

		bottomBar.setWidth(DESIGN_WINDOW_WIDTH_PIXELS);

		mChatBtn.setSize(45, 25);
		x = LEFT + SPACING / 2 + mChatBtn.getWidth() / 2;
		y = bottomBar.getHeight() / 2;
		mChatBtn.setPosition(x, y);
		mChatBtn.setZIndex(0);

		bottomBar.attachChild(mChatBtn);

		btnEmoticon.setSize(28, 25);
		x = RIGHT - SPACING - btnEmoticon.getWidth() / 2;
		btnEmoticon.setPosition(x, y);
		btnEmoticon.setZIndex(0);

		bottomBar.attachChild(btnEmoticon);
		hud.attachChild(bottomBar);

		hud.registerTouchArea(mChatBtn);
		hud.registerTouchArea(btnEmoticon);
	}

	protected void loadBetSliderResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/common/");
		sliderTextureAtlas = new BuildableBitmapTextureAtlas(
				getTextureManager(), 256, 512);

		sliderBackgroundTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(sliderTextureAtlas, this, SLIDER_BG_RES);
		thumbTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				sliderTextureAtlas, this, THUMB_RES);
		indicatorTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				sliderTextureAtlas, this, INDICATOR_RES);

		try {
			sliderTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			sliderTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void unloadBetSliderResources() {
		sliderTextureAtlas.unload();
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
