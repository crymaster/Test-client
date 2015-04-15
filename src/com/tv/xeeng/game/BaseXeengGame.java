package com.tv.xeeng.game;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSCounter;
import org.andengine.entity.util.ScreenGrabber;
import org.andengine.opengl.font.BitmapFont;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.EmptyBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.decorator.BaseBitmapTextureAtlasSourceDecorator;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.modifier.IModifier.DeepCopyNotSupportedException;
import org.andengine.util.texturepack.TexturePack;
import org.andengine.util.texturepack.TexturePackLoader;
import org.andengine.util.texturepack.TexturePackTextureRegionLibrary;
import org.andengine.util.texturepack.exception.TexturePackParseException;
import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.tv.xeeng.BackgroundThreadManager;
import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.CustomApplication;
import com.tv.xeeng.R;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.GameSocket;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.game.BaseGame;
import com.tv.xeeng.dataLayer.game.BaseGame.GameState;
import com.tv.xeeng.dataLayer.game.BaseGame.KickOutReason;
import com.tv.xeeng.dataLayer.game.TLMNGame;
import com.tv.xeeng.dataLayer.game.actions.BaseXEGameAction;
import com.tv.xeeng.dataLayer.game.actions.XEPlayerKickedAction;
import com.tv.xeeng.dataLayer.game.actions.XEPlayerLeftAction;
import com.tv.xeeng.dataLayer.game.actions.XEShowLeaveMatchFailedAction;
import com.tv.xeeng.dataLayer.game.actions.XEShowMainPlayerIsKickedAction;
import com.tv.xeeng.dataLayer.game.actions.XEShowPlayerJoinedAction;
import com.tv.xeeng.dataLayer.game.actions.XEShowPlayerReadyAction;
import com.tv.xeeng.dataLayer.game.actions.XEShowStartGameFailedAction;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.game.elements.card.BannerBoxSprite;
import com.tv.xeeng.game.elements.card.BannerBoxSprite.OnBannerListener;
import com.tv.xeeng.game.elements.common.BaseGrowButton;
import com.tv.xeeng.game.elements.common.CustomeTickerTextOptions;
import com.tv.xeeng.game.elements.common.OnClickPlayerSpriteListener;
import com.tv.xeeng.game.elements.common.OnClickStartGameButtonListener;
import com.tv.xeeng.game.elements.common.ProgressTimer.OnProgressTimerListener;
import com.tv.xeeng.game.elements.common.StartGameButton;
import com.tv.xeeng.game.elements.common.StartGameButton.StartGameState;
import com.tv.xeeng.game.elements.common.TickerText;
import com.tv.xeeng.game.elements.common.TimerText;
import com.tv.xeeng.game.elements.common.XeengText;
import com.tv.xeeng.game.elements.player.BasePlayerHolder;
import com.tv.xeeng.game.hud.BaseEmotionDialog;
import com.tv.xeeng.game.hud.ChatDialog;
import com.tv.xeeng.game.hud.InvitePlayerDialog;
import com.tv.xeeng.game.hud.RuleGameDialog;
import com.tv.xeeng.game.loader.AsyncTaskLoader;
import com.tv.xeeng.game.loader.IAsyncCallback;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gamedata.entity.CardGameTable;
import com.tv.xeeng.gamedata.entity.Player;
import com.tv.xeeng.gamedata.entity.Player.PlayerState;
import com.tv.xeeng.gui.GameSettingsActivity;
import com.tv.xeeng.gui.LoginActivity;
import com.tv.xeeng.gui.customview.BasicDialog;
import com.tv.xeeng.gui.customview.UserProfileDialog;
import com.tv.xeeng.gui.fragments.ChatDialogFragment;
import com.tv.xeeng.manager.Logger;
import com.tv.xeeng.manager.SoundManager;

public abstract class BaseXeengGame extends BaseGameActivity {
	// NguyenNK: Added volatile keyword. This means any thread calling INSTANCE
	// will get newest update instead of cached one.
	public static volatile BaseXeengGame INSTANCE;

	// ====================================================
	// CONSTANTS
	// ====================================================
	protected static final int USER_AVATAR_WIDTH = 200;
	protected static final int USER_AVATAR_HEIGHT = 200;

	protected boolean reconnectGame;
	protected String reconnectMsg;

	public final TextureOptions mNormalTextureOption = TextureOptions.BILINEAR;
	public final TextureOptions mStretchableBeamTextureOption = new TextureOptions(
			GLES20.GL_LINEAR, GLES20.GL_LINEAR, GLES20.GL_CLAMP_TO_EDGE,
			GLES20.GL_REPEAT, false);;
	public final TextureOptions mTransparentTextureOption = TextureOptions.BILINEAR;
	public final TextureOptions mTransparentRepeatingTextureOption = TextureOptions.REPEATING_BILINEAR;

	public static final float DESIGN_WINDOW_WIDTH_PIXELS = 650;
	public static final float DESIGN_WINDOW_HEIGHT_PIXELS = 340;
	public static final float TABLE_WIDTH = 322;
	public static final float TABLE_HEIGHT = 183;
	public static final float TABLE_CENTER = TABLE_WIDTH / 2 + 31;
	public static float ACTUAL_WINDOW_WIDTH, ACTUAL_WINDOW_HEIGHT;
	public static float SPACING = 8f;
	public static float TOP, LEFT, BOTTOM, RIGHT, CENTER_X, CENTER_Y;

	public static final int Z_BACKGROUND = 1;
	public static final int Z_PLAYER = 50;
	public static final int Z_FIRST_CARD = 60;
	public static final int Z_FUNC_BUTTON = 75;
	public static final int Z_TABLE = 2;
	public static final int Z_START_GAME = 60;
	public static final int Z_CHAT = 200;

	public static final int CARD_WIDTH = 52;
	public static final int CARD_HEIGHT = 67;

	public static final String HIGHLIGHT = "<h>";
	public static final String LEADING = "<l>- ";
	public static final String BREAK_LINE = "#";

	public static final int BOTTOM_BAR_HEIGHT = 31;

	public static float SCREEN_RATIO = -1;

	public static Scene mScene;
	protected Camera mCamera;
	protected LocalBroadcastManager localBroadcastManager;
	protected StartGameButton startGameBtn;
	protected BasicDialog alertDialog;
	protected AsyncTaskLoader mResourceLoader;
	protected IAsyncCallback mLoadBgTask, mLoadCommonTask, mLoadSpecificTask,
			mLoadAddionalResource, mLoadAudioTask;
	protected boolean mLoadResourceComplete = false;
	protected HUD hud;
	/*
	 * texture regione references
	 */
	public ITextureRegion emoticonBtnTR;
	public ITextureRegion chatTR;
	public ITextureRegion backTR;
	public ITextureRegion settingTR;
	public ITextureRegion printScreenBtnTR;
	public ITextureRegion ruleBtnTR;
	

	public ITextureRegion tableInfoBgTR;

	protected BaseGrowButton mBackBtn;
	protected BaseGrowButton mSettingBtn;
	protected BaseGrowButton btnEmoticon;
	protected BaseGrowButton mPrintScreenBtn;
	protected BaseGrowButton mRuleBtn;
	protected BaseGrowButton mChatBtn;

	protected Sprite bottomBar;

	protected BaseEmotionDialog mEmotionDialog;

	public ITiledTextureRegion startGameTTR;
	public ITextureRegion iconTableMasterTR;

	public static ITiledTextureRegion vipIndicatorTR;
	public static ITextureRegion iconReadyTR;
	public static ITextureRegion avatarBgTR;
	public static ITextureRegion avatarTR;
	public static ITextureRegion btnInviteTR;

	public static ITiledTextureRegion chatBoxTTR;
	public static ITiledTextureRegion mSpinTR;

	public static ITextureRegion imgGlowDarkTR;
	public static ITextureRegion imgGlowLightTR;

	public static ITextureRegion timerTextBgTR;

	public static ITextureRegion iconLeavingGameTR;

	public static ITexture startedPlayerTT;
	public static ITextureRegion startedPlayerTR;

	public static ITexture samAlertTT;
	public static ITextureRegion samAlertTR;

	// common font
	public static Font smallRegularFont;
	public static Font smallBoldFont;
	public static Font mediumRegularFont;
	public static Font largeBoldFont;
	public static BitmapFont fontStrokeDark;
	public static BitmapFont fontStrokeLight;
	public static Font largeRegularFont;

	// public static Font largeShadowFont;

	// setting texture
	// public static ITextureRegion settingBgTR;
	public static ITextureRegion closeTR;
	// public static ITiledTextureRegion okTTR;
	// public static ITiledTextureRegion resetTTR;
	// public static ITiledTextureRegion radioTTR;
	// public static ITextureRegion backgroundSliderTR;
	// public static ITextureRegion activeSliderTR;
	// public static ITextureRegion indicatorSliderTR;
	public static ITextureRegion bgEmotionTR;

	public static ITiledTextureRegion emotionTTR;
	public static ITextureRegion closeBtnTR;

	// Entity matchers
	public IEntityMatcher hudEntityMatcher;

	private BitmapTextureAtlas mcloseBmp;
	// private BitmapTextureAtlas mSettingBgBmp, mOkBmp, mRadioBmp,
	// mBackgroundSliderBmp;
	// private BitmapTextureAtlas mActiveSliderBmp, mIndicatorSliderBmp;
	private BitmapTextureAtlas avatarBgBmp, avatarBmp, btnInviteBmp,
			iconTableMasterBmp, iconReadyBmp, chatBoxBmp, imgGlowDarkBmp,
			imgGlowLightBmp, iconLeavingGameBmp, startedPlayerBitmap;

	private BuildableBitmapTextureAtlas startGameTextureAtlas;
	private BuildableBitmapTextureAtlas funcBtnsTextureAtlas;

	private BitmapTextureAtlas mEmotionDialogBgBmp;
	private BuildableBitmapTextureAtlas mEmotionTextureAtlas;

	protected final String dialog_ok_button_res = "ok_button.png";
	protected final String dialog_close_button_res = "btn_close.png";

	// protected final String dialog_radio_res = "radio_btn_setting.png";
	// protected final String dialog_slider_bg_res = "bg_bet.png";

	// protected final String dialog_active_slider_res = "bar_bet.png";
	// protected final String dialog_indicator_slider_res = "icon_kbob.png";

	protected final String BTN_BACK_RES = "game_btn_back.png";

	protected final String BTN_BACK_RES_HD = "game_btn_back_hd.png";
	protected final String BTN_SETTING_RES = "game_btn_setting.png";
	protected final String BTN_SETTING_RES_HD = "game_btn_setting_hd.png";
	protected final String BTN_GAME_RULES_RES = "game_btn_rules.png";
	protected final String BTN_GAME_RULES_RES_HD = "game_btn_rules_hd.png";
	protected final String ICON_LEAVING_GAME = "icon_dangky_roiphong.png";
	protected final String ICON_LEAVING_GAME_HD = "icon_dangky_roiphong_hd.png";

	protected final String ICON_READY_GAME = "icon_ready.png";
	protected final String ICON_READY_GAME_HD = "icon_ready_hd.png";

	protected final String imgAvatarRes = "avatar_default.png";
	protected final String imgAvatarBackgroundRes = "avatar_ingame_bg.png";
	protected final String BTN_INVITE_RES = "btn_add_player.png";
	protected final String BTN_INVITE_RES_HD = "btn_add_player_hd.png";
	protected final String BTN_START_RES = "btn_start.png";
	protected final String BTN_START_RES_HD = "btn_start_hd.png";
	protected final String BTN_PRINTSCREEN_RES = "btn_ingame_printscreen.png";
	protected final String BTN_PRINTSCREEN_RES_HD = "btn_ingame_printscreen_hd.png";
	protected final String readyRes = "icon_ready.png";
	protected final String imgTableMasterRes = "icon_host.png";
	protected final String chatBoxRes = "buble_chat.png";

	protected final String mBgTableInfoRes = "room_status_bg.png";
	protected final String mBgEmotionDialogRes = "bg_emotion.png";

	protected String mBackgroundResName = "normal_bg.jpg";
	protected String mTableBackgroundResName = "bg_table.png";

	protected final String imgGlowDarkRes = "glow1.png";
	protected final String imgGlowLightRes = "glow2.png";

	private static final String TIMER_TEXT_BG_RES = "time_load_bg.png";

	protected ArrayList<? extends BasePlayerHolder> mPlayerHolderList;
	protected Map<Long, ? extends BasePlayerHolder> mPlayerHolderMap;
	protected BasePlayerHolder mMainPlayerHolder;

	// protected BaseSimpleSettingDialog mSettingDialog;

	protected ChatDialog chatDialog;
	protected TickerText tickerTxt;
	protected XeengText minCashTxt;
	protected boolean isClickedPlayer = false;

	protected Sprite leftTableInfoArea;
	protected Rectangle rightTableInfoArea;

	// Texts
	protected XeengText tableIdText;
	protected XeengText roomLevelText;
	protected XeengText minBetText;

	protected BannerBoxSprite mBannerSprite;
	protected RuleGameDialog mRuleGameDialog;

	protected ITextureRegion backgroundTR;
	private BuildableBitmapTextureAtlas backgroundTextureAtlas;

	protected boolean onLoadSettingResoucesComplete = false;

	public static int MARGIN_VERTICAL, MARGIN_HORIZONTAL;

	protected TimerText mAutoHiddenResultTimer;

	protected boolean leavingGame;

	private ChatDialogFragment globalChatDialog;

	public Map<String, Integer> emotionSignalMap = new HashMap<String, Integer>();

	{
		emotionSignalMap.put("(may quá nhỉ)", 0);
		emotionSignalMap.put("(tím mắt rồi)", 1);
		emotionSignalMap.put("(vỡ mặt rồi)", 2);
		emotionSignalMap.put("(lêu..lêu!!!)", 3);
		emotionSignalMap.put("(like a boss!!!)", 4);
		emotionSignalMap.put("(cay quá nhỉ)", 5);
		emotionSignalMap.put("(hơ..hơ..)", 6);
		emotionSignalMap.put("(phù..phù..!!)", 7);
		emotionSignalMap.put("(phân vân quá)", 8);
		emotionSignalMap.put("(yêu quá cơ)", 9);

		emotionSignalMap.put("(ngon vãi)", 10);
		emotionSignalMap.put("(chơi hay nghỉ đây)", 11);
		emotionSignalMap.put("(hí..hí..hí..)", 12);
		emotionSignalMap.put("(kinh vl)", 13);
		emotionSignalMap.put("(clgt???)", 14);
		emotionSignalMap.put("(cáu vãi)", 15);
		emotionSignalMap.put("(sợ chưa?)", 16);
		emotionSignalMap.put("(chết cmnr)", 17);
		emotionSignalMap.put("(thôi!xong rồi)", 18);
		emotionSignalMap.put("(không quan tâm)", 19);

		emotionSignalMap.put("(chết toi)", 20);
		emotionSignalMap.put("(hoành tráng)", 21);
		emotionSignalMap.put("(chết chắc)", 22);
		emotionSignalMap.put("(phê quá nhỉ)", 23);
		emotionSignalMap.put("(cố lên đê)", 24);
		emotionSignalMap.put("(móm nặng)", 25);
		emotionSignalMap.put("(đi ngủ đây)", 26);
	}

	public ArrayList<String> emotionKeyList = new ArrayList<String>();

	{
		emotionKeyList.add("(may quá nhỉ)");
		emotionKeyList.add("(tím mắt rồi)");
		emotionKeyList.add("(vỡ mặt rồi)");
		emotionKeyList.add("(lêu..lêu!!!)");
		emotionKeyList.add("(like a boss!!!)");
		emotionKeyList.add("(cay quá nhỉ)");
		emotionKeyList.add("(hơ..hơ..)");
		emotionKeyList.add("(phù..phù..!!)");
		emotionKeyList.add("(phân vân quá)");
		emotionKeyList.add("(yêu quá cơ)");

		emotionKeyList.add("(ngon vãi)");
		emotionKeyList.add("(chơi hay nghỉ đây)");
		emotionKeyList.add("(hí..hí..hí..)");
		emotionKeyList.add("(kinh vl)");
		emotionKeyList.add("(clgt???)");
		emotionKeyList.add("(cáu vãi)");
		emotionKeyList.add("(sợ chưa?)");
		emotionKeyList.add("(chết cmnr)");
		emotionKeyList.add("(thôi!xong rồi)");
		emotionKeyList.add("(không quan tâm)");

		emotionKeyList.add("(chết toi)");
		emotionKeyList.add("(hoành tráng)");
		emotionKeyList.add("(chết chắc)");
		emotionKeyList.add("(phê quá nhỉ)");
		emotionKeyList.add("(cố lên đê)");
		emotionKeyList.add("(móm nặng)");
		emotionKeyList.add("(đi ngủ đây)");
	}

	private BitmapTextureAtlas avatarBitmapTextureAtlas;

	// get wifi lock to prevent wifi turn off
	private WifiManager mWifiManager;
	private WifiLock mWifiLock;

	// Many threads using this variable. Need newest update then.
	protected volatile boolean isReadyShow = false;

	public boolean isReadyShow() {
		return isReadyShow;
	}

	protected void forceCloseSocket() {

		CustomApplication.shareApplication().wasCloseSocket = true;
		GameSocket.shareSocket().close();
	}

	@Override
	protected void onCreate(Bundle pSavedInstanceState) {

		super.onCreate(pSavedInstanceState);
		if (GameData.shareData().getGame() == null) {

			BusinessRequester.getInstance().logout();
			GameData.shareData().clean();

			// Clear all activities and go back to LoginActivity
			Intent i = new Intent(this, LoginActivity.class);
			ComponentName cn = i.getComponent();
			Intent loginIntent = IntentCompat.makeRestartActivityTask(cn);
			startActivity(loginIntent);
			return;
		}
		CustomApplication.shareApplication().wasInGame = true;
		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		mWifiLock = mWifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL,
				"inBiengGame");
		GameData.shareData().getGame().setGameActivity(this);
		INSTANCE = this;
	}

	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}

	@Override
	protected synchronized void onResume() {

		super.onResume();
		if (mWifiLock != null && mWifiLock.isHeld()) {

			mWifiLock.release();
		}

		CustomApplication.shareApplication().currentActivity = this;
		CustomApplication.shareApplication().wasInBackground = false;
		CustomApplication.shareApplication().stopAppInBackgroundTimer();

		INSTANCE = this;

		if (GameData.shareData().getGame() != null
				&& GameData.shareData().getGame().getGameActivity() == null) {
			GameData.shareData().getGame().setGameActivity(this);
		}

		if (SCREEN_RATIO == -1) {
			SCREEN_RATIO = (float) CommonUtils.getScreenSize(this).y
					/ (float) DESIGN_WINDOW_HEIGHT_PIXELS;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d("BaseXeengGame", "OnPause called");
		INSTANCE = null;
		BaseGame game = GameData.shareData().getGame();
		// if (game != null) {
		game.setGameActivity(null);
		// }

		CustomApplication.shareApplication().startAppInBackgroundTimer();
	}

	protected void redrawGameAction(BaseXEGameAction action) throws Exception {
		Log.d("RedrawGame", "redrawGameAction super");
		switch (action.getType()) {
		case (BaseXEGameAction.ACTION_PLAYER_LEFT): {
			Log.d("RedrawGame", "Show player left");
			XEPlayerLeftAction playerLeftAction = (XEPlayerLeftAction) action;
			showPlayerLeft(playerLeftAction.getUserId());
			break;
		}
		case (BaseXEGameAction.ACTION_PLAYER_KICKED): {
			Log.d("RedrawGame", "Show player kicked");
			XEPlayerKickedAction playerKickedAction = (XEPlayerKickedAction) action;
			showPlayerKicked(playerKickedAction.getUserId(),
					playerKickedAction.getReason(),
					playerKickedAction.getMessage());
			break;
		}
		case (BaseXEGameAction.ACTION_SHOW_END_MATCH): {
			showEndMatch(false);
			/*
			 * BaseXeengGame.INSTANCE.runOnUpdateThread(new Runnable() {
			 * 
			 * @Override public void run() { showEndMatch(false); } });
			 */
			break;
		}
		case (BaseXEGameAction.ACTION_SHOW_PLAYER_JOINED): {
			final XEShowPlayerJoinedAction showPlayerJoinedAction = (XEShowPlayerJoinedAction) action;
			Log.d("RedrawGame", "Show player joined");
			showPlayerJoined(showPlayerJoinedAction.getUserId());
			break;
		}
		case (BaseXEGameAction.ACTION_SHOW_PLAYER_READY): {
			final XEShowPlayerReadyAction showPlayerReadyAction = (XEShowPlayerReadyAction) action;
			Log.d("RedrawGame", "Show player ready");
			showPlayerReady(showPlayerReadyAction.getPlayerId());
			break;
		}
		case (BaseXEGameAction.ACTION_SHOW_MAIN_PLAYER_IS_KICKED): {
			final XEShowMainPlayerIsKickedAction showMainPlayerKicked = (XEShowMainPlayerIsKickedAction) action;
			Log.d("RedrawGame", "Redraw main player kicked");
			if (showMainPlayerKicked.getReason() != KickOutReason.NONE) {
				showMainPlayerIsKickedDialog(showMainPlayerKicked.getReason(),
						showMainPlayerKicked.getMessage());
			} else {
				finish();
			}
			break;
		}
		case (BaseXEGameAction.ACTION_SHOW_START_GAME_FAILED): {
			final XEShowStartGameFailedAction showStartGameFailedAction = (XEShowStartGameFailedAction) action;
			Log.d("RedrawGame", "Redraw start game failed");
			showStartGameFailed(showStartGameFailedAction.getMessage());
			break;
		}
		case (BaseXEGameAction.ACTION_SHOW_LEAVE_MATCH_FAILED): {
			final XEShowLeaveMatchFailedAction showLeaveMatchFailedAction = (XEShowLeaveMatchFailedAction) action;
			Log.d("RedrawGame", "Redraw leave match failed");
			showLeaveMatchFailed(showLeaveMatchFailedAction.getMessage());
			break;
		}
		}
	}

	public void showLeaveMatchFailed(final String msg) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				initAlertDialogIfNeed();

				alertDialog.setTitleText("Không thể rời bàn");
				alertDialog.setMessageText(msg);
				alertDialog.setNegativeText(null);
				alertDialog.setPositiveText("Đóng");
				alertDialog.show();
			}
		});
	}

	@Override
	public synchronized void onResumeGame() {
		super.onResumeGame();
		SoundManager.resume();

		Log.d("BaseXeengGame", "OnResumeGame called");

		if (isReadyShow) {
			// runOnUpdateThread(new Runnable() {
			//
			// @Override
			// public void run() {
			Log.d("RedrawGame", "About to redraw at onResumeGame");
			processRedrawActions();
			// }
			// });
		}
	}

	protected void processRedrawActions() {
		synchronized (this) {
			BaseGame game = GameData.shareData().getGame();
			Log.d("RedrawGame", "Queue length = " + game.getQueueLength());
			BaseXEGameAction action = game.dequeue();
			Log.d("RedrawGame", "1 Queue length = " + game.getQueueLength());

			while (action != null) {
				try {
					Log.d("RedrawGame",
							"Process redraw action. Queue length = "
									+ game.getQueueLength());
					redrawGameAction(action);
				} catch (Exception e) {
					e.printStackTrace();
				}
				action = game.dequeue();
			}

			// Newer version
			Queue<Runnable> runnableQueue = game.getRunnableQueue();
			Runnable nextAction = runnableQueue.poll();
			while (nextAction != null) {
				nextAction.run();
				nextAction = runnableQueue.poll();
			}
		}
	}

	@Override
	public synchronized void onPauseGame() {
		super.onPauseGame();
		SoundManager.pause();
	}

	@Override
	public void onDestroyResources() throws IOException {

		unregisterBroadcastReceiver();
		if (mResourceLoader != null)
			mResourceLoader.cancel(true);
		smallRegularFont.unload();
		smallBoldFont.unload();
		mediumRegularFont.unload();
		largeBoldFont.unload();
		fontStrokeDark.unload();
		fontStrokeLight.unload();
		// largeShadowFont.unload();
		largeRegularFont.unload();

		unloadFuncResource();
		onUnloadPlayerResource();
		onUnLoadAdditionalResource();
		unloadSpecificResources();
		unloadSettingResource();
		SoundManager.unLoad();
		super.onDestroyResources();
	}

	protected void unloadResources(BitmapTextureAtlas... atlas) {

		for (BitmapTextureAtlas bta : atlas) {

			if (bta != null)
				bta.unload();
			bta = null;
		}
	}

	@Override
	public synchronized void onGameDestroyed() {

		INSTANCE = null;
		// if (startGameBtn != null) {
		// startGameBtn.stopTimer();
		// }
		CustomApplication.shareApplication().wasInGame = false;
		super.onGameDestroyed();
	}

	@Override
	public void onBackPressed() {
		if (GameData.shareData().getGame().state != GameState.PLAYING
				|| GameData.shareData().getGame().getMainPlayer().state != PlayerState.PLAYING) {
			showLeaveMatchConfirm();
		} else {
			leavingGame = !leavingGame;

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (leavingGame) {
						toastOnUiThread("Bạn sẽ rời bàn khi kết thúc ván.",
								Toast.LENGTH_SHORT);
					} else {
						toastOnUiThread("Đã hủy đăng kí rời bàn.",
								Toast.LENGTH_SHORT);
					}
				}
			});

			getMainPlayerHolder().playerSprite
					.setIconLeavingVisible(leavingGame);
		}
	}

	@Override
	public EngineOptions onCreateEngineOptions() {

		mCroppedResolutionPolicy = new CroppedResolutionPolicy(
				DESIGN_WINDOW_WIDTH_PIXELS, DESIGN_WINDOW_HEIGHT_PIXELS);
		mCamera = new Camera(0, 0, DESIGN_WINDOW_WIDTH_PIXELS,
				DESIGN_WINDOW_HEIGHT_PIXELS);
		mCamera.setCenter(DESIGN_WINDOW_WIDTH_PIXELS / 2,
				DESIGN_WINDOW_HEIGHT_PIXELS / 2);
		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_SENSOR, mCroppedResolutionPolicy,
				mCamera);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		engineOptions.getRenderOptions().setDithering(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		return engineOptions;
	}

	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		return new LimitedFPSEngine(pEngineOptions, 60);
	}

	public float textScale;

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws IOException {
		textScale = 1;
		INSTANCE = this;

		onLoadBackgroundResource();

		pOnCreateResourcesCallback.onCreateResourcesFinished();

		onLoadFontResource();
		onLoadFuncResource();
		onLoadPlayerResource();
		
		onCreateFuncButton();
		onCreatePlayer();

		mLoadSpecificTask = new IAsyncCallback() {

			@Override
			public void workToDo() {
				onLoadSpecificGameResource();
			}

			@Override
			public void onComplete() {
				BaseXeengGame.this.runOnUpdateThread(new Runnable() {
					public void run() {
						onCreateSpecificGame();
					}
				});
			}
		};

		mLoadAddionalResource = new IAsyncCallback() {

			@Override
			public void workToDo() {
				onLoadAdditionalResource();
				onLoadSettingResource();
			}

			@Override
			public void onComplete() {
				BaseXeengGame.this.runOnUpdateThread(new Runnable() {
					public void run() {
						mLoadResourceComplete = true;
						onGFXReady();
					}
				});
			}
		};

		mLoadAudioTask = new IAsyncCallback() {

			@Override
			public void workToDo() {

				onLoadAudioResource();
			}

			@Override
			public void onComplete() {

				BaseXeengGame.this.runOnUpdateThread(new Runnable() {

					public void run() {

						onSFXReady();
					}
				});
			}
		};

		runOnUiThread(new Runnable() {

			public void run() {

				mResourceLoader = new AsyncTaskLoader();
				mResourceLoader.execute(
				/* mLoadCommonTask, */mLoadSpecificTask, mLoadAddionalResource,
						mLoadAudioTask);
			}
		});
	}

	protected void onLoadFontResource() {

		float fontSize1 = 11;
		float fontSize2 = 15;
		float fontSize3 = 20;
		// float fontSize4 = 32;
		// float strokeSize4 = 1f;

		smallRegularFont = FontFactory.create(getFontManager(),
				getTextureManager(), 256, 256,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA,
				Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), fontSize1
						/ textScale, true, Color.WHITE_ABGR_PACKED_INT);
		smallRegularFont.load();

		smallBoldFont = FontFactory.create(getFontManager(),
				getTextureManager(), 256, 256,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), fontSize1
						/ textScale, true, Color.WHITE_ABGR_PACKED_INT);
		smallBoldFont.load();

		mediumRegularFont = FontFactory.create(getFontManager(),
				getTextureManager(), 512, 512,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA,
				Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), fontSize2
						/ textScale, true, Color.WHITE_ARGB_PACKED_INT);
		mediumRegularFont.load();

		largeBoldFont = FontFactory.create(getFontManager(),
				getTextureManager(), 1024, 512,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), fontSize3
						/ textScale, true, Color.WHITE_ARGB_PACKED_INT);
		largeBoldFont.load();

		// Color textColor1 = new Color((float) 196f / 255f, (float) 196f /
		// 255f,
		// (float) 196f / 255f);
		//
		// Color strokeColor1 = new Color((float) 53f / 255f, (float) 53f /
		// 255f,
		// (float) 53f / 255f);
		//
		// final ITexture strokeFontTexture1 = new BitmapTextureAtlas(
		// getTextureManager(), 1024, 512,
		// TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		// fontStrokeDark = new Font(getFontManager(), strokeFontTexture1,
		// Typeface.createFromAsset(getAssets(),
		// "fonts/MYRIADPRO-SEMIBOLD.OTF"), fontSize4 / textScale,
		// true, textColor1);
		fontStrokeDark = new BitmapFont(getTextureManager(), getAssets(),
				"fonts/text_custom_lose_hd-export.fnt", TextureOptions.BILINEAR);
		fontStrokeDark.load();

		// Color textColor2 = new Color((float) 236f / 255f, (float) 209f /
		// 255f,
		// (float) 60f / 255f);
		// Color strokeColor2 = new Color((float) 0f / 255f, (float) 0f / 255f,
		// (float) 0f / 255f);
		//
		// final ITexture strokeFontTexture2 = new BitmapTextureAtlas(
		// getTextureManager(), 1024, 512,
		// TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		// fontStrokeLight = new Font(getFontManager(), strokeFontTexture2,
		// Typeface.createFromAsset(getAssets(),
		// "fonts/MYRIADPRO-SEMIBOLD.OTF"), fontSize4 / textScale,
		// true, textColor2);

		fontStrokeLight = new BitmapFont(getTextureManager(), getAssets(),
				"fonts/text_custom_win_hd-export.fnt", TextureOptions.BILINEAR);
		fontStrokeLight.load();

		// final ITexture largeShadowFontTexture = new BitmapTextureAtlas(
		// getTextureManager(), 1024, 512,
		// TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		// largeShadowFont = new Font(getFontManager(), largeShadowFontTexture,
		// Typeface.createFromAsset(getAssets(),
		// "fonts/MYRIADPRO-SEMIBOLD.OTF"), fontSize4 / textScale,
		// true, Color.BLACK_ABGR_PACKED_INT);

		// largeShadowFont.load();

		largeRegularFont = FontFactory.create(getFontManager(),
				getTextureManager(), 1024, 512,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA,
				Typeface.create(Typeface.DEFAULT, Typeface.NORMAL),
				20 / textScale, true, Color.WHITE_ARGB_PACKED_INT);
		largeRegularFont.load();
	}

	protected void onLoadFuncResource() {

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/common/");
		final TextureManager textureManager = BaseXeengGame.this
				.getTextureManager();

		funcBtnsTextureAtlas = new BuildableBitmapTextureAtlas(textureManager,
				1024, 256, mNormalTextureOption);

		String btnBackRes, btnPrintScreenRes, btnSettingRes, btnRulesRes;

		if (SCREEN_RATIO > 1.5f) {
			btnBackRes = BTN_BACK_RES_HD;
			btnPrintScreenRes = BTN_PRINTSCREEN_RES_HD;
			btnSettingRes = BTN_SETTING_RES_HD;
			btnRulesRes = BTN_GAME_RULES_RES_HD;
			// started_player_icon = "icon_cai_hd.png";
		} else {
			btnBackRes = BTN_BACK_RES;
			btnPrintScreenRes = BTN_PRINTSCREEN_RES;
			btnSettingRes = BTN_SETTING_RES;
			btnRulesRes = BTN_GAME_RULES_RES;
			// started_player_icon = "icon_cai.png";
		}

		// for back button
		backTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				funcBtnsTextureAtlas, BaseXeengGame.this, btnBackRes);
		// for setting button
		settingTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				funcBtnsTextureAtlas, BaseXeengGame.this, btnSettingRes);
		// for printscreen button
		printScreenBtnTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(funcBtnsTextureAtlas, BaseXeengGame.this,
						btnPrintScreenRes);
		// for rule button
		ruleBtnTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				funcBtnsTextureAtlas, BaseXeengGame.INSTANCE, btnRulesRes);
		// for table info background
		tableInfoBgTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				funcBtnsTextureAtlas, BaseXeengGame.INSTANCE, mBgTableInfoRes);

		// TimerText as used by many games...
		timerTextBgTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				funcBtnsTextureAtlas, this, TIMER_TEXT_BG_RES);

		try {
			funcBtnsTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			funcBtnsTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void unloadFuncResource() {
		funcBtnsTextureAtlas.unload();
	}

	protected void onLoadPlayerResource() {

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/common/");
		final String basePath = BitmapTextureAtlasTextureRegionFactory
				.getAssetBasePath();
		final TextureManager textureManager = this.getTextureManager();

		// for player default avatar
		IBitmapTextureAtlasSource btas = AssetBitmapTextureAtlasSource.create(
				getAssets(), basePath + imgAvatarRes);
		avatarBmp = new BitmapTextureAtlas(textureManager,
				btas.getTextureWidth(), btas.getTextureHeight(),
				mNormalTextureOption);
		avatarTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				avatarBmp, this, imgAvatarRes, 0, 0);
		avatarBmp.load();

		//
		String startedPlayerIconPath = "";
		if (SCREEN_RATIO > 1.5) {
			startedPlayerIconPath = "icon_cai_hd.png";
		} else {
			startedPlayerIconPath = "icon_cai.png";
		}
		startedPlayerBitmap = new BitmapTextureAtlas(textureManager,
				btas.getTextureWidth(), btas.getTextureHeight(),
				mNormalTextureOption);
		startedPlayerTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(startedPlayerBitmap, this,
						startedPlayerIconPath, 0, 0);
		startedPlayerBitmap.load();

		//

		// for user avatar
		avatarBitmapTextureAtlas = new BitmapTextureAtlas(getTextureManager(),
				1024, 1024);
		avatarBitmapTextureAtlas.load();

		String btnStartRes, btnReadyRes;
		if (SCREEN_RATIO > 1.5f) {
			btnStartRes = BTN_START_RES_HD;
			btnReadyRes = "btn_ready.png";
		} else {
			btnStartRes = BTN_START_RES;
			btnReadyRes = "btn_ready_hd.png";
		}
		startGameTextureAtlas = new BuildableBitmapTextureAtlas(textureManager,
				512, 256, mNormalTextureOption);
		ITextureRegion startGameTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(startGameTextureAtlas, this, btnStartRes);
		ITextureRegion readyGameTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(startGameTextureAtlas, this, btnReadyRes);

		startGameTTR = new TiledTextureRegion(startGameTR.getTexture(),
				startGameTR, readyGameTR);

		try {
			startGameTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 1));
			startGameTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String btnInviteRes, iconLeavingGame, iconReady;
		if (SCREEN_RATIO > 1.5f) {
			btnInviteRes = BTN_INVITE_RES_HD;
			iconLeavingGame = ICON_LEAVING_GAME_HD;
			iconReady = ICON_READY_GAME_HD;
		} else {
			btnInviteRes = BTN_INVITE_RES;
			iconLeavingGame = ICON_LEAVING_GAME;
			iconReady = ICON_READY_GAME;
		}

		btas = AssetBitmapTextureAtlasSource.create(getAssets(), basePath
				+ btnInviteRes);
		btnInviteBmp = new BitmapTextureAtlas(textureManager,
				btas.getTextureWidth(), btas.getTextureHeight(),
				mNormalTextureOption);
		btnInviteTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				btnInviteBmp, this, btnInviteRes, 0, 0);
		btnInviteBmp.load();

		btas = AssetBitmapTextureAtlasSource.create(getAssets(), basePath
				+ iconReady);
		iconReadyBmp = new BitmapTextureAtlas(textureManager,
				btas.getTextureWidth(), btas.getTextureHeight(),
				mNormalTextureOption);
		iconReadyTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				iconReadyBmp, this, "icon_ready.png", 0, 0);
		iconReadyBmp.load();

		btas = AssetBitmapTextureAtlasSource.create(getAssets(), basePath
				+ imgTableMasterRes);
		iconTableMasterBmp = new BitmapTextureAtlas(textureManager,
				btas.getTextureWidth(), btas.getTextureHeight(),
				mNormalTextureOption);
		iconTableMasterTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(iconTableMasterBmp, this, imgTableMasterRes,
						0, 0);
		iconTableMasterBmp.load();

		btas = AssetBitmapTextureAtlasSource.create(getAssets(), basePath
				+ "buble_chat.png");
		chatBoxBmp = new BitmapTextureAtlas(textureManager,
				btas.getTextureWidth(), btas.getTextureHeight(),
				mNormalTextureOption);
		chatBoxTTR = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(chatBoxBmp, this, "buble_chat.png", 0, 0,
						2, 1);
		chatBoxBmp.load();

		btas = AssetBitmapTextureAtlasSource.create(getAssets(), basePath
				+ imgGlowDarkRes);
		imgGlowDarkBmp = new BitmapTextureAtlas(textureManager,
				btas.getTextureWidth(), btas.getTextureHeight(),
				mNormalTextureOption);
		imgGlowDarkTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				imgGlowDarkBmp, this, imgGlowDarkRes, 0, 0);
		imgGlowDarkBmp.load();

		btas = AssetBitmapTextureAtlasSource.create(getAssets(), basePath
				+ imgGlowLightRes);
		imgGlowLightBmp = new BitmapTextureAtlas(textureManager,
				btas.getTextureWidth(), btas.getTextureHeight(),
				mNormalTextureOption);
		imgGlowLightTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(imgGlowLightBmp, this, imgGlowLightRes, 0, 0);
		imgGlowLightBmp.load();

		btas = AssetBitmapTextureAtlasSource.create(getAssets(), basePath
				+ iconLeavingGame);
		iconLeavingGameBmp = new BitmapTextureAtlas(textureManager,
				btas.getTextureWidth(), btas.getTextureHeight(),
				mNormalTextureOption);
		iconLeavingGameTR = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(iconLeavingGameBmp, this, iconLeavingGame, 0,
						0);
		iconLeavingGameBmp.load();
	}

	protected void onUnloadPlayerResource() {

		unloadResource(avatarBgBmp, avatarBmp, btnInviteBmp, iconReadyBmp,
				iconTableMasterBmp, chatBoxBmp, imgGlowDarkBmp,
				imgGlowLightBmp, iconLeavingGameBmp, avatarBitmapTextureAtlas);
		startGameTextureAtlas.unload();
	}

	protected void onLoadAdditionalResource() {

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/common/");
		final String basePath = BitmapTextureAtlasTextureRegionFactory
				.getAssetBasePath();
		final TextureManager textureManager = this.getTextureManager();
		IBitmapTextureAtlasSource btas = AssetBitmapTextureAtlasSource.create(
				getAssets(), basePath + dialog_close_button_res);
		mcloseBmp = new BitmapTextureAtlas(textureManager,
				btas.getTextureWidth(), btas.getTextureHeight(),
				mNormalTextureOption);
		closeTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				mcloseBmp, this, dialog_close_button_res, 0, 0);
		mcloseBmp.load();

		btas = AssetBitmapTextureAtlasSource.create(getAssets(), basePath
				+ mBgEmotionDialogRes);
		mEmotionDialogBgBmp = new BitmapTextureAtlas(textureManager,
				btas.getTextureWidth(), btas.getTextureHeight(),
				mNormalTextureOption);
		bgEmotionTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				mEmotionDialogBgBmp, this, mBgEmotionDialogRes, 0, 0);
		mEmotionDialogBgBmp.load();

		// Load emoticon
		if (SCREEN_RATIO > 1.5f) {
			BitmapTextureAtlasTextureRegionFactory
					.setAssetBasePath("gfx/emoticon/hd/");
			mEmotionTextureAtlas = new BuildableBitmapTextureAtlas(
					textureManager, 2048, 2048);
		} else {
			BitmapTextureAtlasTextureRegionFactory
					.setAssetBasePath("gfx/emoticon/");
			mEmotionTextureAtlas = new BuildableBitmapTextureAtlas(
					textureManager, 1024, 512);
		}
		closeBtnTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				mEmotionTextureAtlas, this, "btn_close.png");
		ITextureRegion[] emotionTR = new ITextureRegion[27];

		for (int i = 0; i < 27; i++) {
			String path = String.format("icon_%02d.png", i + 1);
			emotionTR[i] = BitmapTextureAtlasTextureRegionFactory
					.createFromAsset(mEmotionTextureAtlas, this, path);
		}
		try {
			mEmotionTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			mEmotionTextureAtlas.load();
			emotionTTR = new TiledTextureRegion(emotionTR[0].getTexture(),
					emotionTR);
		} catch (TextureAtlasBuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			Log.d("Exception", e.getMessage());
		}
	}

	protected void onUnLoadAdditionalResource() {

		unloadResource(mcloseBmp, mEmotionDialogBgBmp);
		mEmotionTextureAtlas.unload();
	}

	protected void onLoadAudioResource() {

		SoundManager.initialize();
		SoundManager.load();
	}

	protected void onLoadSettingResource() {

		if (!onLoadSettingResoucesComplete) {

			onLoadSettingResoucesComplete = true;
			// BitmapTextureAtlasTextureRegionFactory
			// .setAssetBasePath("gfx/common/");
			// final String basePath = BitmapTextureAtlasTextureRegionFactory
			// .getAssetBasePath();
			// final TextureManager textureManager = this.getTextureManager();

			// IBitmapTextureAtlasSource btas = AssetBitmapTextureAtlasSource
			// .create(getAssets(), basePath + dialog_ok_button_res);
			// mOkBmp = new BitmapTextureAtlas(textureManager,
			// btas.getTextureWidth(), btas.getTextureHeight(),
			// mNormalTextureOption);
			// okTTR = BitmapTextureAtlasTextureRegionFactory
			// .createTiledFromAsset(mOkBmp, this, dialog_ok_button_res,
			// 0, 0, 2, 1);
			// mOkBmp.load();

			// btas = AssetBitmapTextureAtlasSource.create(getAssets(), basePath
			// + dialog_radio_res);
			// mRadioBmp = new BitmapTextureAtlas(textureManager,
			// btas.getTextureWidth(), btas.getTextureHeight(),
			// mNormalTextureOption);
			// radioTTR = BitmapTextureAtlasTextureRegionFactory
			// .createTiledFromAsset(mRadioBmp, this, dialog_radio_res, 0,
			// 0, 4, 1);
			// mRadioBmp.load();

			// btas = AssetBitmapTextureAtlasSource.create(getAssets(), basePath
			// + dialog_slider_bg_res);
			// mBackgroundSliderBmp = new BitmapTextureAtlas(textureManager,
			// btas.getTextureWidth(), btas.getTextureHeight(),
			// mNormalTextureOption);
			// backgroundSliderTR = BitmapTextureAtlasTextureRegionFactory
			// .createFromAsset(mBackgroundSliderBmp, this,
			// dialog_slider_bg_res, 0, 0);
			// mBackgroundSliderBmp.load();

			// btas = AssetBitmapTextureAtlasSource.create(getAssets(), basePath
			// + dialog_active_slider_res);
			// mActiveSliderBmp = new BitmapTextureAtlas(textureManager,
			// btas.getTextureWidth(), btas.getTextureHeight(),
			// mNormalTextureOption);
			// activeSliderTR = BitmapTextureAtlasTextureRegionFactory
			// .createFromAsset(mActiveSliderBmp, this,
			// dialog_active_slider_res, 0, 0);
			// mActiveSliderBmp.load();
			//
			// btas = AssetBitmapTextureAtlasSource.create(getAssets(), basePath
			// + dialog_indicator_slider_res);
			// mIndicatorSliderBmp = new BitmapTextureAtlas(textureManager,
			// btas.getTextureWidth(), btas.getTextureHeight(),
			// mNormalTextureOption);
			// indicatorSliderTR = BitmapTextureAtlasTextureRegionFactory
			// .createFromAsset(mIndicatorSliderBmp, this,
			// dialog_indicator_slider_res, 0, 0);
			// mIndicatorSliderBmp.load();
		}
	}

	private void unloadSettingResource() {
		// unloadResource();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback arg0) throws IOException {

		MARGIN_VERTICAL = (int) mCroppedResolutionPolicy.getMarginVertical();
		MARGIN_HORIZONTAL = (int) mCroppedResolutionPolicy
				.getMarginHorizontal();
		ACTUAL_WINDOW_WIDTH = DESIGN_WINDOW_WIDTH_PIXELS - 2
				* MARGIN_HORIZONTAL;
		ACTUAL_WINDOW_HEIGHT = DESIGN_WINDOW_HEIGHT_PIXELS - 2
				* MARGIN_VERTICAL;
		LEFT = MARGIN_HORIZONTAL;
		RIGHT = DESIGN_WINDOW_WIDTH_PIXELS - LEFT;
		BOTTOM = MARGIN_VERTICAL;
		TOP = DESIGN_WINDOW_HEIGHT_PIXELS - BOTTOM;
		CENTER_X = DESIGN_WINDOW_WIDTH_PIXELS / 2;
		CENTER_Y = (DESIGN_WINDOW_HEIGHT_PIXELS + 31) / 2;
		Logger.getInstance().info(
				this,
				"MARGIN - VERTICAL: " + MARGIN_VERTICAL + " ; HORIZONAL: "
						+ MARGIN_HORIZONTAL);

		mScene = new Scene();
		mScene.setOnAreaTouchTraversalFrontToBack();
		// mScene.setTouchAreaBindingOnActionMoveEnabled(true);
		// mScene.setTouchAreaBindingOnActionDownEnabled(true);
		mScene.setSize(DESIGN_WINDOW_WIDTH_PIXELS, DESIGN_WINDOW_HEIGHT_PIXELS);
		mScene.setAnchorCenter(0, 0);

		onCreateBackground();

		arg0.onCreateSceneFinished(mScene);
	}

	@Override
	public void onPopulateScene(Scene arg0, OnPopulateSceneCallback arg1)
			throws IOException {

		arg1.onPopulateSceneFinished();
	}

	protected void onCreateBackground() {
		Sprite sbg = new Sprite(DESIGN_WINDOW_WIDTH_PIXELS / 2,
				DESIGN_WINDOW_HEIGHT_PIXELS / 2, DESIGN_WINDOW_WIDTH_PIXELS,
				DESIGN_WINDOW_HEIGHT_PIXELS, backgroundTR,
				getVertexBufferObjectManager());

		mScene.setBackground(new SpriteBackground(sbg));
	}

	protected void onCreateFuncButton() {
		hud = new HUD();
		mCamera.setHUD(hud);

		float x;
		float y;

		// Back Button
		mBackBtn = new BaseGrowButton(0, 0, backTR) {

			@Override
			public void onClick() {
				BaseXeengGame.this.onBackPressed();
			}
		};
		mBackBtn.setSize(30, 24);
		x = mBackBtn.getWidth() / 2 + SPACING + LEFT;
		y = DESIGN_WINDOW_HEIGHT_PIXELS - mBackBtn.getHeight() / 2;
		mBackBtn.setPosition(x, y);

		hud.attachChild(mBackBtn);

		// PrintScreen Button
		mPrintScreenBtn = new BaseGrowButton(0, 0, printScreenBtnTR) {

			@Override
			public void onClick() {
				// TODO: Print screen function go here
				takeScreenShot();
			}
		};
		mPrintScreenBtn.setSize(30, 24);
		x += mPrintScreenBtn.getWidth() + SPACING;
		mPrintScreenBtn.setPosition(x, y);

		hud.attachChild(mPrintScreenBtn);
		mPrintScreenBtn.setVisible(false);

		// Setting Button
		mSettingBtn = new BaseGrowButton(0, 0, settingTR) {

			@Override
			public void onClick() {
				if (!mLoadResourceComplete)
					return;
				onCreateSettingDialog();
			}
		};
		mSettingBtn.setSize(30, 24);
		x = RIGHT - SPACING - mSettingBtn.getWidth() / 2;
		mSettingBtn.setPosition(x, y);

		hud.attachChild(mSettingBtn);

		// Rule Button
		mRuleBtn = new BaseGrowButton(0, 0, ruleBtnTR) {
			@Override
			public void onClick() {
				if (!mLoadResourceComplete)
					return;
				if (mRuleGameDialog == null) {
					BaseXeengGame.this.runOnUiThread(new Runnable() {
						public void run() {
							mRuleGameDialog = new RuleGameDialog(
									BaseXeengGame.this);
							updateRuleGameDialog();
							mRuleGameDialog
									.setDismissListener(new RuleGameDialog.OnDismissRuleDialogListener() {
									});
						}
					});
				}

				BaseXeengGame.this.runOnUiThread(new Runnable() {

					public void run() {

						mRuleGameDialog.show();
					}
				});
			}
		};
		mRuleBtn.setSize(30, 24);
		x -= (SPACING + mSettingBtn.getWidth());
		mRuleBtn.setPosition(x, y);

		hud.attachChild(mRuleBtn);

		// TableInfo
		createTableInfo();

		// create start game
		startGameBtn = new StartGameButton(CENTER_X, CENTER_Y, startGameTTR,
				new OnClickStartGameButtonListener() {

					@Override
					public void onClick() {

						Logger.getInstance().warn(BaseXeengGame.this,
								"start game clicked");
						if (GameData.shareData().getGame().getMainPlayer().isTableMaster) {
							startGameConfirmIfNeed();
						} else {
							BusinessRequester.getInstance().readyGame(true);
						}
					}
				});

		startGameBtn.changeState(StartGameState.START_GAME);
		// startGameBtn.stopTimer();
		startGameBtn.setZIndex(Z_START_GAME);

		mScene.attachChild(startGameBtn);
		mScene.registerTouchArea(startGameBtn);

		correctStartGameType();
		// showStartGameIfNeed(false);

		/*
		 * log fps for observing performance
		 */
		// registerFPSLogger();
		hud.registerTouchArea(mBackBtn);
		// hud.registerTouchArea(mPrintScreenBtn);
		hud.registerTouchArea(mRuleBtn);
		hud.registerTouchArea(mSettingBtn);

		// Emotion Button
		btnEmoticon = new BaseGrowButton(DESIGN_WINDOW_WIDTH_PIXELS, 0,
				emoticonBtnTR) {

			@Override
			public void onClick() {
				if (!mLoadResourceComplete)
					return;
				if (mEmotionDialog == null) {
					mEmotionDialog = new BaseEmotionDialog(mScene, mCamera) {
						@Override
						public void exposeEmotion(final String pSignal) {
							final Integer val = emotionSignalMap.get(pSignal);
							if (val != null) {
								getMainPlayerHolder().exposeEmotion(
										val.intValue());
								detachSelf();
								BusinessRequester.getInstance().chat(pSignal);
							}
						}
					};
					mEmotionDialog.setZIndex(1000);
				}
				runOnUpdateThread(new Runnable() {
					@Override
					public void run() {
						if (!mEmotionDialog.hasParent()) {
							mScene.attachChild(mEmotionDialog);
						}
					}
				});
			}
		};
		btnEmoticon.setSize(28, 25);

		// Chat button
		mChatBtn = new BaseGrowButton(0, 0, chatTR) {
			@Override
			public void onClick() {
				if (!mLoadResourceComplete)
					return;
				BaseXeengGame.this.runOnUiThread(new Runnable() {
					public void run() {
						if (chatDialog == null) {
							chatDialog = new ChatDialog(BaseXeengGame.this);
							chatDialog
									.setDismissListener(new ChatDialog.OnDismissChatDialogListener() {

										@Override
										public void onIExposeEmotion(int pIndex) {
											mMainPlayerHolder
													.exposeEmotion(pIndex);
										}

										@Override
										public void onIChat(String msg) {

											mMainPlayerHolder.chat(msg);
										}
									});
						}
						chatDialog.show();

						// if (globalChatDialog == null) {
						// globalChatDialog = new ChatDialogFragment();
						// }
						//
						// globalChatDialog
						// .show(getSupportFragmentManager(), null);
					}
				});
			}
		};
		mChatBtn.setSize(45, 25);

		hudEntityMatcher = new IEntityMatcher() {
			@Override
			public boolean matches(IEntity entity) {
				if (entity == mPrintScreenBtn) {
					return false;
				}
				return true;
			}
		};
	}

	private void takeScreenShot() {
		runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				final ScreenGrabber sc = new ScreenGrabber();

				mScene.attachChild(sc);

				final int width = (int) mCamera.getSurfaceWidth();
				final int height = (int) mCamera.getSurfaceHeight();

				sc.grab(width, height,
						new ScreenGrabber.IScreenGrabberCallback() {

							@Override
							public void onScreenGrabbed(Bitmap bitmap) {
								File dir = new File(Environment
										.getExternalStorageDirectory()
										+ File.separator + "xeeng");
								dir.mkdirs();

								final String filePath = Environment
										.getExternalStorageDirectory()
										+ File.separator
										+ "xeeng"
										+ File.separator
										+ "Screen_"
										+ System.currentTimeMillis() + ".png";

								ByteArrayOutputStream bytes = new ByteArrayOutputStream();
								FileOutputStream os;

								bitmap.compress(Bitmap.CompressFormat.PNG, 100,
										bytes);

								try {
									os = new FileOutputStream(filePath);
									os.write(bytes.toByteArray());
									os.flush();
									os.close();

									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											Toast.makeText(
													BaseXeengGame.this,
													"Lưu ảnh thành công tại: "
															+ filePath,
													Toast.LENGTH_SHORT).show();
										}
									});
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} finally {
									sc.detachSelf();
								}

							}

							@Override
							public void onScreenGrabFailed(Exception arg0) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										toastOnUiThread("Có lỗi xảy ra",
												Toast.LENGTH_SHORT);
									}
								});
								Log.e("BaseXeengGame", arg0.getMessage());
								sc.detachSelf();
							}
						});
			}
		});
	}

	private void createTableInfo() {
		// Table Info Left area
		float tableInfoAreaWidth = 110;
		float tableInfoAreaHeight = 30;

		leftTableInfoArea = new Sprite(0, 0, tableInfoAreaWidth,
				tableInfoAreaHeight, tableInfoBgTR,
				getVertexBufferObjectManager());

		leftTableInfoArea.setWidth(tableInfoAreaWidth);
		leftTableInfoArea.setHeight(tableInfoAreaHeight);

		leftTableInfoArea.setPosition(LEFT + leftTableInfoArea.getWidth() / 2
				- 5, TOP - mBackBtn.getHeight() - SPACING / 2
				- leftTableInfoArea.getHeight() / 2);

		// Titles
		XeengText tableIdTitle = new XeengText(0, 0, smallRegularFont,
				"Phòng chơi: ", new TextOptions(HorizontalAlign.LEFT),
				getVertexBufferObjectManager());
		tableIdTitle.setColor(Color.YELLOW);
		tableIdTitle.setPosition(SPACING + tableIdTitle.getWidth() / 2,
				leftTableInfoArea.getHeight() * 3 / 4);

		leftTableInfoArea.attachChild(tableIdTitle);

		XeengText roomLevelTitle = new XeengText(0, 0, smallRegularFont,
				"Kênh: ", new TextOptions(HorizontalAlign.LEFT),
				getVertexBufferObjectManager());
		roomLevelTitle.setColor(Color.YELLOW);
		roomLevelTitle.setPosition(SPACING + roomLevelTitle.getWidth() / 2,
				leftTableInfoArea.getHeight() * 1 / 4);

		leftTableInfoArea.attachChild(roomLevelTitle);

		// table id
		tableIdText = new XeengText(0, 0, smallRegularFont, "0123456789",
				new TextOptions(HorizontalAlign.LEFT),
				getVertexBufferObjectManager());

		tableIdText.setColor(Color.WHITE);
		tableIdText.setText(GameData.shareData().getGame().getMatchId() + "");
		tableIdText.setPosition(SPACING * 2 + tableIdTitle.getWidth()
				+ tableIdText.getWidth() / 2, tableIdTitle.getY());
		leftTableInfoArea.attachChild(tableIdText);

		// room level
		roomLevelText = new XeengText(SPACING * 2 + roomLevelTitle.getWidth(),
				roomLevelTitle.getY(), smallRegularFont,
				"ABCDEFGHIJKLMNOPQRSTUVWXYZ", getVertexBufferObjectManager());
		roomLevelText.setColor(Color.WHITE);
		roomLevelText.setAnchorCenterX(0);
		if (GameData.shareData().currentRoomLevel != null) {
			roomLevelText.setText(GameData.shareData().currentRoomLevel
					.getName());
		} else {
			roomLevelText.setText("");
		}

		leftTableInfoArea.attachChild(roomLevelText);
		leftTableInfoArea.setZIndex(Z_TABLE + 1);

		hud.attachChild(leftTableInfoArea);

		// Right table info area
		tableInfoAreaWidth = 70;

		rightTableInfoArea = new Rectangle(0, 0, tableInfoAreaWidth,
				tableInfoAreaHeight, getVertexBufferObjectManager());

		Sprite rightTableInfoAreaBg = new Sprite(
				rightTableInfoArea.getWidth() / 2,
				rightTableInfoArea.getHeight() / 2, tableInfoAreaWidth,
				tableInfoAreaHeight, tableInfoBgTR,
				getVertexBufferObjectManager());
		rightTableInfoAreaBg.setRotation(180);

		rightTableInfoArea.setColor(Color.TRANSPARENT);
		rightTableInfoArea.attachChild(rightTableInfoAreaBg);

		rightTableInfoArea.setPosition(RIGHT - rightTableInfoArea.getWidth()
				/ 2, TOP - mSettingBtn.getHeight() - SPACING / 2
				- rightTableInfoArea.getHeight() / 2);

		// Title
		XeengText minBetTitle = new XeengText(0, 0, smallRegularFont,
				"Mức cược", new TextOptions(HorizontalAlign.RIGHT),
				getVertexBufferObjectManager());

		minBetTitle.setPosition(rightTableInfoArea.getWidth() - SPACING,
				rightTableInfoArea.getHeight() * 3 / 4);
		minBetTitle.setColor(Color.YELLOW);

		minBetTitle.setAnchorCenterX(1f);
		rightTableInfoArea.attachChild(minBetTitle);

		// Min bet text
		XeengText minBetText = new XeengText(0, 0, smallRegularFont,
				"0123456789.0123456789",
				new TextOptions(HorizontalAlign.RIGHT),
				getVertexBufferObjectManager());

		minBetText.setPosition(rightTableInfoArea.getWidth() - SPACING,
				rightTableInfoArea.getHeight() * 1 / 4);
		minBetText.setColor(Color.WHITE);
		minBetText
				.setText(CommonUtils.formatCash(GameData.shareData().minBetCash));

		minBetText.setAnchorCenterX(1f);
		rightTableInfoArea.attachChild(minBetText);

		hud.attachChild(rightTableInfoArea);
	}

	protected void onCreatePlayer() {

		onInitPlayer(mOnInitPlayerCallBack);
		organizePlayer(true);
		mScene.sortChildren();

		refreshReadyState();
		// processTableMasterTimer();
	}

	protected void onInitPlayer(InitPlayerCallBack pInitPlayerCallBack) {

	}

	private final InitPlayerCallBack mOnInitPlayerCallBack = new InitPlayerCallBack() {

		@Override
		public void onInitPlayerComplete() {

			for (BasePlayerHolder ph : mPlayerHolderList) {
				ph.setOnPlayerClickListener(playerListener);
				Log.e("checking click", "register");
			}
		}
	};

	protected void onCreateSettingDialog() {

		Intent intent = new Intent(this, GameSettingsActivity.class);
		startActivity(intent);

		// onInitSettingDialog();
		// onUpdateSettingDialog();
		// mScene.attachChild(mSettingDialog);
	}

	protected void onCreateSpecificGame() {
		renderSpecificElements(mOnRenderElementCallBack);
		registerBroadcastReceiver();

		if (GameData.shareData().getGame().getMainPlayer().state == PlayerState.OBSERVING
				&& GameData.shareData().getGame().state == GameState.PLAYING) {
			showObservingView(true);
		} else {
			showObservingView(false);
		}
	}

	protected void onGFXReady() {

		// showRuleBtn();
	}

	protected void onSFXReady() {

	}

	// protected void onInitSettingDialog() {
	//
	// if (mSettingDialog == null) {
	// mSettingDialog = new BaseSimpleSettingDialog(mScene, mCamera) {
	// @Override
	// public void onChangeSetting() {
	// onChangeSettingData();
	// }
	// };
	// }
	// }

	protected void onChangeSettingData() {

		// BusinessRequester.getInstance().changeGameSetting(
		// mSettingDialog.getCurrentCash(),
		// mSettingDialog.getCurrentNumPlayer().getValue(), null);
	}

	protected void onUpdateSettingDialog() {

		// BaseGame game = GameData.shareData().getGame();
		//
		// mSettingDialog.enable(game.getMainPlayer().isTableMaster);
		// long maxCash = game.getMainPlayer().cash / 4;
		// Log.e("checking max cash", "value: " + maxCash);
		// mSettingDialog.updateCash(game.getCurrentTable().getCurrentBet(),
		// game
		// .getCurrentTable().getMinBetCash(), maxCash);
		//
		// if (game.getCurrentTable().getMaxPlayer() <= 2) {
		//
		// mSettingDialog.setCurrentPlayer(NUM_PLAYER.SOLO);
		// } else {
		// // mSettingDialog.setCurrentPlayer(NUM_PLAYER.FOUR_PLAYER);
		// }
	}

	/*
	 * getter && setter
	 */
	public Scene getCurrentScene() {

		return mScene;
	}

	public ArrayList<? extends BasePlayerHolder> getPlayerHolderList() {

		return mPlayerHolderList;
	}

	public BasePlayerHolder getMainPlayerHolder() {

		return mMainPlayerHolder;
	}

	protected void registerFPSLogger() {

		if (!Logger.getInstance().isEnable())
			return;
		final FPSCounter fpsCounter = new FPSCounter();
		this.mEngine.registerUpdateHandler(fpsCounter);

		final Text fpsText = new Text(215, mBackBtn.getY(), smallRegularFont,
				"FPS:", "FPS: XX".length(), getVertexBufferObjectManager());

		mScene.attachChild(fpsText);

		mScene.registerUpdateHandler(new TimerHandler(1 / 20.0f, true,
				new ITimerCallback() {
					@Override
					public void onTimePassed(final TimerHandler pTimerHandler) {
						fpsText.setText("FPS: "
								+ Math.round(fpsCounter.getFPS()));
					}
				}));
	}

	protected void organizePlayer(boolean isReorganize) {

		if (isReorganize) {

			resetPlayerHolder();
			chooseOrganizeType();
		}

		refreshTableMaster();
		refreshReadyState();
		showInvitingButton();

		for (Player player : GameData.shareData().getGame().getPlayerList()) {
			setAvatar(player.id);
			setVip(player);
		}
	}

	protected void chooseOrganizeType() {

		if (needOrderingGame()) {

			if (GameData.shareData().getGame().getPlayerList().size() == 2) { // solo
				organizeSolo();
			} else {
				organizeInOrder();
			}
		} else {
			organizeInAny();
		}
	}

	protected void organizeSolo() {

		BasePlayerHolder player;
		long mainPlayerId = GameData.shareData().getMyself().id;
		for (Player pd : GameData.shareData().getGame().getPlayerList()) {

			if (mainPlayerId == pd.id) {

				player = mPlayerHolderList.get(0);
				Player mainPlayerData = GameData.shareData().getGame()
						.getMainPlayer();
				player.settle(mainPlayerData);
			} else {

				player = mPlayerHolderList.get(2);
				player.settle(pd);
			}
		}
	}

	protected void organizeInOrder() {

		BasePlayerHolder player;
		long mainPlayerId = GameData.shareData().getMyself().id;
		// find index of main player in player list data
		int mainPlayerIndex = -1;
		@SuppressWarnings("unchecked")
		Vector<Player> playerList = (Vector<Player>) GameData.shareData()
				.getGame().getPlayerList();
		for (int i = 0, t = playerList.size(); i < t; i++) {

			Player pd = playerList.get(i);
			if (mainPlayerId == pd.id) {

				mainPlayerIndex = i;
				break;
			}
		}
		if (mainPlayerIndex < 0)
			return;

		// rearrange player to temp list
		ArrayList<Player> tempList = new ArrayList<Player>();
		tempList.add(playerList.get(mainPlayerIndex));
		for (int i = mainPlayerIndex + 1; i < playerList.size(); i++) {

			tempList.add(playerList.get(i));
		}
		for (int i = 0; i < mainPlayerIndex; i++) {

			tempList.add(playerList.get(i));
		}

		player = null;
		for (int i = 0; i < tempList.size(); i++) {

			Player pd = tempList.get(i);
			if (mainPlayerId == pd.id) {

				player = mMainPlayerHolder;
				player.settle(pd);
				player = null;
			} else {

				player = getAvailablePlayerHolder();
				if (player != null)
					player.settle(pd);
				player = null;
			}
		}
	}

	protected void organizeInAny() {
		BasePlayerHolder player;
		long mainPlayerId = GameData.shareData().getMyself().id;
		for (Player pd : GameData.shareData().getGame().getPlayerList()) {

			if (mainPlayerId == pd.id) {

				player = mPlayerHolderList.get(0);
				Player mainPlayerData = GameData.shareData().getGame()
						.getMainPlayer();
				player.settle(mainPlayerData);
			} else {

				BasePlayerHolder nextPlayerHolder = getAvailablePlayerHolder();
				if (nextPlayerHolder != null) {

					nextPlayerHolder.settle(pd);
				} else {

					// WTF
					break;
				}
			}
		}
	}

	protected void placeNewPlayer(long newPlayerId) {
		if (needOrderingGame()
				&& GameData.shareData().getGame().getState() != GameState.PLAYING) {
			organizePlayer(true);
		} else {
			Player pd = GameData.shareData().getGame().getPlayer(newPlayerId);
			BasePlayerHolder pc = getAvailablePlayerHolder();
			if (pc == null)
				return;
			if (pd == null)
				return;
			pc.settle(pd);

			setAvatar(newPlayerId);
			setVip(pd);
		}

		SoundManager.playJoinGameEffect();
	}

	protected boolean needOrderingGame() {
		return GameData.shareData().gameId == GameData.TLMN_TYPE;
	}

	private void setVip(Player player) {
		if (player.id == GameData.shareData().getMyself().id) {
			player.vipId = GameData.shareData().getMyself().vipId;
		}
		BasePlayerHolder playerHolder = getPlayer(player.id);
		if (playerHolder != null) {
			playerHolder.playerSprite.setVipIndicator(player.vipId);
		}
	}

	private volatile ArrayList<Long> requestedAvatarIds = new ArrayList<Long>();
	private LruCache<AvatarKey, ITextureRegion> cachedAvatarTRs = new LruCache<AvatarKey, ITextureRegion>(
			6);

	private class AvatarKey {
		public long playerId;
		public int index;

		public AvatarKey(long playerId) {
			this(playerId, 0);
		}

		public AvatarKey(long playerId, int index) {
			this.playerId = playerId;
			this.index = index;
		}

		@Override
		public boolean equals(Object o) {
			return this.playerId == ((AvatarKey) o).playerId;
		}

		@Override
		public int hashCode() {
			return (int) this.playerId;
		}
	}

	private void setAvatar(final long playerId) {
		BackgroundThreadManager.post(new Runnable() {
			@Override
			public void run() {
				final Bitmap bitmap = CommonUtils
						.getBitmapFromMemCache(playerId);
				final BasePlayerHolder playerHolder = getPlayer(playerId);

				if (bitmap != null) {
					if (playerHolder != null && !playerHolder.isAvailable()) {
						int index = 0;
						ITextureRegion avatarTextureRegion = cachedAvatarTRs
								.get(new AvatarKey(playerId));

						if (avatarTextureRegion == null) {
							Set<AvatarKey> keySet = cachedAvatarTRs.snapshot()
									.keySet();
							if (keySet.size() < 6) {
								index = keySet.size();
							} else {
								index = ((AvatarKey) keySet.toArray()[5]).index;
							}
							final int width = bitmap.getWidth() < USER_AVATAR_WIDTH ? bitmap
									.getWidth() : USER_AVATAR_WIDTH;
							final int height = bitmap.getHeight() < USER_AVATAR_HEIGHT ? bitmap
									.getHeight() : USER_AVATAR_HEIGHT;

							IBitmapTextureAtlasSource baseTextureSource = new EmptyBitmapTextureAtlasSource(
									width, height);
							IBitmapTextureAtlasSource avatarTextureSource = new BaseBitmapTextureAtlasSourceDecorator(
									baseTextureSource) {
								protected void onDecorateBitmap(
										android.graphics.Canvas canvas)
										throws Exception {
									canvas.drawBitmap(bitmap, 0, 0, this.mPaint);
								}

								@Override
								public BaseBitmapTextureAtlasSourceDecorator deepCopy() {
									throw new DeepCopyNotSupportedException();
								}
							};

							BitmapTextureAtlasTextureRegionFactory
									.createFromSource(avatarBitmapTextureAtlas,
											new EmptyBitmapTextureAtlasSource(
													USER_AVATAR_WIDTH,
													USER_AVATAR_HEIGHT),
											(index % 3) * USER_AVATAR_WIDTH,
											((index / 3) % 2)
													* USER_AVATAR_HEIGHT);

							avatarTextureRegion = BitmapTextureAtlasTextureRegionFactory
									.createFromSource(avatarBitmapTextureAtlas,
											avatarTextureSource, (index % 3)
													* USER_AVATAR_WIDTH,
											((index / 3) % 2)
													* USER_AVATAR_HEIGHT);

							cachedAvatarTRs.put(new AvatarKey(playerId, index),
									avatarTextureRegion);
						}

						final ITextureRegion finalATR = avatarTextureRegion;
						try {
							runOnUpdateThread(new Runnable() {
								@Override
								public void run() {
									playerHolder.playerSprite
											.setUserAvatarSprite(
													finalATR,
													getVertexBufferObjectManager());
								}
							});
						} catch (NullPointerException ex) {
							ex.printStackTrace();
						}
					}
				} else {
					Log.d("BaseXeengGame",
							"BaseXeengGame - Avatar cached file not found. Will try get it from server");
					if (requestedAvatarIds.contains(playerId)) {
						try {
							runOnUpdateThread(new Runnable() {
								@Override
								public void run() {
									if (playerHolder != null) {
										if (!playerHolder.isAvailable()) {
											playerHolder.playerSprite
													.unSetUserAvatarSprite();
										} else {
											playerHolder.reset(true);
										}
									}
								}
							});
						} catch (NullPointerException ex) {
							ex.printStackTrace();
						}
						return;
					}

					requestedAvatarIds.add(playerId);
					if (playerHolder != null) {
						playerHolder.playerSprite.unSetUserAvatarSprite();

						final boolean result = BusinessRequester.getInstance()
								.getUserAvatar(playerId);
						if (result) {
							setAvatar(playerId);
						} else {
							// final BasePlayerHolder playerHolder =
							// getPlayer(playerId);
							if (playerHolder != null
									&& BaseXeengGame.this != null
									&& !playerHolder.isAvailable()) {
								try {
									BaseXeengGame.this
											.runOnUpdateThread(new Runnable() {

												@Override
												public void run() {
													playerHolder.playerSprite
															.unSetUserAvatarSprite();
												}
											});
								} catch (NullPointerException ex) {
									ex.printStackTrace();
								}
							}
						}
					}
				}
			}
		});
	}

	protected void removeLeavedPlayer(final long leavedPlayerId) {
		final BasePlayerHolder pc = getPlayer(leavedPlayerId);

		// Its time for some trick here. Try to "access" every other members of
		// the cache to reset its used state.
		for (AvatarKey key : cachedAvatarTRs.snapshot().keySet()) {
			if (key.playerId != leavedPlayerId) {
				cachedAvatarTRs.get(key);
			}
		}

		if (GameData.shareData().gameId == GameData.TLMN_TYPE) {

			if (GameData.shareData().getGame().getState() == GameState.PLAYING) {
				if (pc != null) {
					pc.reset(true);
				}
			} else {
				organizePlayer(true);
			}
		} else {
			if (pc != null) {
				pc.reset(true);
			}
		}

		if (pc != null) {
			SoundManager.playLeaveGameEffect();
		}
	}

	protected void refreshTableMaster() {

		// first, hide table master of all player
		for (BasePlayerHolder pc : mPlayerHolderList) {

			pc.playerSprite.setShowTableMaster(false);
		}

		// find table master and show
		for (Player pd : GameData.shareData().getGame().getPlayerList()) {

			if (pd.isTableMaster) {

				// get player sprite
				BasePlayerHolder pc = getPlayer(pd.id);

				pd.isReady = false;
				if (pc != null) {
					pc.playerSprite.setShowTableMaster(true);
					pc.playerSprite.setIconReadyVisible(false);
				}
			}
		}
	}

	protected void setReadyForPlayer(long playerId) {

		BasePlayerHolder pc = getPlayer(playerId);
		if (pc != null) {
			pc.playerSprite.setIconReadyVisible(true);
			// pc.playerSprite.ready(true, true);
		}
	}

	protected void refreshReadyState() {

		for (BasePlayerHolder pc : mPlayerHolderList) {

			if (pc.isAvailable()) {

				pc.playerSprite.setIconReadyVisible(false);
				continue;
			}
			Player pd = GameData.shareData().getGame().getPlayer(pc.playerId);
			if (pd != null) {
				if (!pd.isTableMaster && pd.state != PlayerState.PLAYING) {
					pc.playerSprite.setIconReadyVisible(pd.isReady);
				} else {
					pc.playerSprite.setIconReadyVisible(false);
				}
			} else {
				pc.playerSprite.setIconReadyVisible(false);
			}
		}
	}

	protected void hideReadyState() {
		hideLeaveMatchConfirmDialog();

		for (BasePlayerHolder pc : mPlayerHolderList) {
			pc.playerSprite.setIconReadyVisible(false);
			pc.playerSprite.setIconLeavingVisible(false);
		}
	}

	protected void hideLeaveMatchConfirmDialog() {
		if (confirmLeaveMatchDialog != null
				&& confirmLeaveMatchDialog.isShowing()) {
			confirmLeaveMatchDialog.dismiss();
		}

		// mScene.unjisterUpdateHandler(leaveMatchTimeOutHandler);
	}

	protected void showInvitingButton() {

		int numberPlayerMax = GameData.shareData().getGame().getCurrentTable()
				.getMaxPlayer();
		int playerCounter = 0;
		for (BasePlayerHolder pd : mPlayerHolderList) {
			if (!pd.isAvailable()) {
				playerCounter++;
			}
		}
		int numberInvitingBtn = numberPlayerMax - playerCounter;
		for (BasePlayerHolder pc : mPlayerHolderList) {
			if (pc.isAvailable()) {

				pc.reset(true);
				if (numberInvitingBtn > 0) {
					pc.reset(true);
					numberInvitingBtn--;
				} else {
					pc.reset(false);
				}
			}
		}

	}

	protected void resetPlayerHolder() {

		for (BasePlayerHolder pc : mPlayerHolderList) {

			pc.reset(false);
		}
	}

	protected BasePlayerHolder getAvailablePlayerHolder() {
		for (int i = 1; i < getPlayerHolderList().size(); i++) {

			BasePlayerHolder pc = mPlayerHolderList.get(i);
			if (pc.isAvailable()) {

				return pc;
			}
		}

		return null;
	}

	public BasePlayerHolder getPlayer(long playerId) {

		BasePlayerHolder player = null;
		if (mPlayerHolderList == null)
			return null;
		ArrayList<BasePlayerHolder> temp = new ArrayList<BasePlayerHolder>(
				mPlayerHolderList);
		for (BasePlayerHolder pc : temp) {

			if (playerId == pc.playerId) {

				player = pc;
				break;
			}
		}
		return player;
	}

	protected boolean canShowInvitingButton() {

		boolean result = true;

		return result;
	}

	protected void correctStartGameType() {

		// check start game type
		Log.e("checking ", "value:"
				+ GameData.shareData().getGame().getMainPlayer().id);
		if (GameData.shareData().getGame().getState() == GameState.WAITING) {
			if (GameData.shareData().getGame().getMainPlayer().isTableMaster) {
				startGameBtn.changeState(StartGameState.START_GAME);
			} else {
				if (GameData.shareData().getGame().getMainPlayer().isReady) {
					startGameBtn.changeState(StartGameState.HIDDEN);
				} else {
					startGameBtn.changeState(StartGameState.READY_GAME);
				}
			}
		} else {
			startGameBtn.changeState(StartGameState.HIDDEN);
		}
	}

	public void showStartGameFailed(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				initAlertDialogIfNeed();

				alertDialog.setTitleText("Không thể bắt đầu chơi");
				alertDialog.setMessageText(msg);
				alertDialog.setNegativeText(null);
				alertDialog.setPositiveText("Đóng");
				alertDialog.show();
			}
		});
	}

	protected void showStartGameIfNeed(boolean animated) {
		GameState gameState = GameData.shareData().getGame().getState();
		if (gameState == GameState.WAITING) {
			if (GameData.shareData().getGame().getMainPlayer().isTableMaster) {
				startGameBtn.show(false);
			}
		} else if (gameState == GameState.PLAYING) {
			startGameBtn.hide(false);
		}

		refreshTableMaster();
		refreshReadyState();
		processTableMasterTimer();
	}

	protected void processTableMasterTimer() {
		if (GameData.shareData().getGame().getState() != GameState.PLAYING) {
			if (GameData.shareData().getGame().isValidWatchDogTimer()) {
				Log.d("BaseXeengGame", "should start timer");
				for (Player player : GameData.shareData().getGame()
						.getPlayerList()) {
					Log.d("BaseXeengGame", "Player id " + player.id
							+ ", Name: " + player.character
							+ " isTableMaster: " + player.isTableMaster);

					BasePlayerHolder playerHolder = getPlayer(player.id);

					if (player.isTableMaster) {
						if (player.id == GameData.shareData().getMyself().id) {

							playerHolder.playerSprite.startTimer(30, 0,
									new OnProgressTimerListener() {

										@Override
										public void onTimeout() {
											runOnUiThread(new Runnable() {

												@Override
												public void run() {
													BaseGame game = GameData
															.shareData()
															.getGame();
													game.setKickedReason(KickOutReason.TIME_OUT_TABLE_MASTER_ROLE);
													game.setKickedMessage("");

													BusinessRequester
															.getInstance()
															.leaveGame(
																	game.getMatchId());
												}
											});

										}

										@Override
										public void onLowTime() {
											// Do nothing
										}
									});
						} else {
							playerHolder.playerSprite.startTimer(30);
						}
					}
				}
			} else if (!GameData.shareData().getGame().isValidWatchDogTimer()) {
				Log.d("BaseXeengGame", "should stop timer");
				for (Player player : GameData.shareData().getGame()
						.getPlayerList()) {
					BasePlayerHolder holder = getPlayer(player.id);
					if (holder != null) {
						holder.playerSprite.stopTimer();
					}
				}
			}
		}
	}

	/**
	 * @Note Need override in subclass to change position of view
	 */
	protected void initObservingView() {

		if (tickerTxt == null) {
			tickerTxt = new TickerText(
					CENTER_X,
					BOTTOM + mediumRegularFont.getLineHeight() / 2 + SPACING,
					mediumRegularFont,
					"Đang xem...",
					11,
					new CustomeTickerTextOptions(HorizontalAlign.LEFT, 0.2f, 7),
					getVertexBufferObjectManager());
			hud.attachChild(tickerTxt);
			tickerTxt.setVisible(false);
		}
	}

	/**
	 * @Note Need override in subclass to act correctly
	 */
	protected void startGameConfirmIfNeed() {
		if (GameData.shareData().getGame().isValidStartGame()) {
			BusinessRequester.getInstance().startGame();
			// startGameBtn.changeState(StartGameState.HIDDEN);
		} else {
			this.runOnUiThread(new Runnable() {
				public void run() {

					initAlertDialogIfNeed();
					alertDialog.setTitleText("Không thể bắt đầu chơi");
					alertDialog.setMessageText(getResources().getString(
							R.string.text_not_enough_player));
					alertDialog.setNegativeText(null);
					alertDialog.setPositiveText("Đóng");
					alertDialog.show();
				}
			});
		}
	}

	protected void showObservingView(boolean isVisibled) {

		initObservingView();
		tickerTxt.setVisible(isVisibled);
	}

	/**
	 * @Note Need override in subclass to add more intent filter
	 */

	// public abstract void begin();
	protected void registerBroadcastReceiver() {

		localBroadcastManager = LocalBroadcastManager.getInstance(this);

		localBroadcastManager
				.registerReceiver(commonReceiver, new IntentFilter(
						CustomApplication.INTENT_APP_WAS_IN_BACKGROUND));
		localBroadcastManager
				.registerReceiver(commonReceiver, new IntentFilter(
						MessageService.INTENT_NAVIGATE_LOGIN_ACTIVITY));

		localBroadcastManager.registerReceiver(commonReceiver,
				new IntentFilter(MessageService.INTENT_RECONNECTION));

		localBroadcastManager.registerReceiver(commonReceiver,
				new IntentFilter(MessageService.INTENT_NETWORK_DEVICE_PROBLEM));

		localBroadcastManager.registerReceiver(commonReceiver,
				new IntentFilter(MessageService.INTENT_INVITE_PLAY_NEW_GAME));

		localBroadcastManager.registerReceiver(commonReceiver,
				new IntentFilter(MessageService.INTENT_JOIN_MATCH));

		localBroadcastManager.registerReceiver(commonReceiver,
				new IntentFilter(MessageService.INTENT_RECEIVE_EVENT_ITEM));

		// localBroadcastManager.registerReceiver(commonReceiver, new
		// IntentFilter(MessageService.INTENT_NEED_RECONNECTION));

	}

	/**
     *
     */
	protected void unregisterBroadcastReceiver() {

		if (localBroadcastManager != null) {
			if (commonReceiver != null) {
				localBroadcastManager.unregisterReceiver(commonReceiver);
			}
		}
	}

	protected void updateTableInfo() {

		if (minBetText != null)
			minBetText.setText(CommonUtils.formatCash(GameData.shareData()
					.getGame().getCurrentTable().getCurrentBet()));
	}

	protected void updateBanner(String pString) {

		if (pString == null)
			return;

		if (mBannerSprite == null) {

			mBannerSprite = new BannerBoxSprite(2 * backTR.getWidth() + 3
					* SPACING + MARGIN_HORIZONTAL, DESIGN_WINDOW_HEIGHT_PIXELS
					- MARGIN_VERTICAL - SPACING, DESIGN_WINDOW_WIDTH_PIXELS - 2
					* MARGIN_HORIZONTAL - 4 * backTR.getWidth() - 6 * SPACING,
					mBackBtn.getHeight());
			mBannerSprite.setAnchorCenter(0, 1);
			mBannerSprite.setZIndex(Z_FUNC_BUTTON);
			mBannerSprite.setBannerListener(mBannerListener);
			mScene.attachChild(mBannerSprite);
		}

		mBannerSprite.updateText(pString);
	}

	protected void hiddenAnnouceBox() {

		if (mBannerSprite != null) {

			mBannerSprite.reset();
		}
	}

	protected void initAlertDialogIfNeed() {

		if (alertDialog == null) {
			alertDialog = new BasicDialog(BaseXeengGame.this);
			alertDialog.setCancelable(false);
		}

		alertDialog.setTitleText("THÔNG BÁO");
		alertDialog.setPositiveText("Có");
		alertDialog.setNegativeText("Không");
		alertDialog.setPositiveOnClickListener(null);
		alertDialog.setNegativeOnClickListener(null);
	}

	private BasicDialog confirmLeaveMatchDialog;

	// private TimerHandler leaveMatchTimeOutHandler = new TimerHandler(3,
	// new ITimerCallback() {
	//
	// @Override
	// public void onTimePassed(TimerHandler arg0) {
	// exitGame();
	// }
	// });

	protected void showLeaveMatchConfirm() {
		if (!mLoadResourceComplete)
			return;

		this.runOnUiThread(new Runnable() {

			public void run() {
				if (confirmLeaveMatchDialog == null) {
					confirmLeaveMatchDialog = new BasicDialog(
							BaseXeengGame.this);
					confirmLeaveMatchDialog.setTitle("THÔNG BÁO");
					confirmLeaveMatchDialog
							.setMessageText("Bạn có chắc chắn muốn rời khỏi bàn không ?");
					confirmLeaveMatchDialog.setPositiveText("Đồng ý");
					confirmLeaveMatchDialog
							.setPositiveOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									BusinessRequester.getInstance().leaveGame(
											GameData.shareData().getGame()
													.getMatchId());
									confirmLeaveMatchDialog.dismiss();

									// leaveMatchTimeOutHandler.reset();
									// mScene.registerUpdateHandler(leaveMatchTimeOutHandler);
								}
							});
					confirmLeaveMatchDialog.setNegativeText("Hủy bỏ");
				}
				confirmLeaveMatchDialog.show();
			}
		});
	}

	protected void showPlayerIsKickedDialog(final String msg) {

		this.runOnUiThread(new Runnable() {

			public void run() {

				initAlertDialogIfNeed();
				alertDialog.setMessageText(msg);
				alertDialog.setPositiveText("Xác nhận");
				alertDialog.setNegativeText(null);
				alertDialog.show();
			}
		});
	}

	public void showMainPlayerIsKickedDialog(final KickOutReason reason,
			final String pMsg) {

		unregisterBroadcastReceiver();

		this.runOnUiThread(new Runnable() {

			public void run() {
				initAlertDialogIfNeed();
				alertDialog.setNegativeText(null);
				alertDialog.setPositiveText("Xác nhận");
				String msg = "";
				switch (reason) {
				case TABLE_MASTER_KICK:
					msg = reason.toString();
					alertDialog
							.setPositiveOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									alertDialog.dismiss();
									exitGame();
								}
							});
					break;

				case BANKRUPT:
					msg = reason.toString();
					alertDialog
							.setNegativeOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									alertDialog.dismiss();
									exitGame();
								}
							});
					alertDialog
							.setPositiveOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									GameData.shareData().isShowChargingActivity = true;
									alertDialog.dismiss();
									exitGame();
								}
							});
					break;

				case SERVER_MESSAGE:

					msg = pMsg;
					alertDialog
							.setPositiveOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									if (GameData.shareData().getMyself().cash <= 4 * GameData
											.shareData().getGame()
											.getCurrentTable().getMinBetCash()) {

										GameData.shareData().isShowChargingActivity = true;
									}
									alertDialog.dismiss();
									exitGame();
								}
							});
					break;

				case TIME_OUT_TABLE_MASTER_ROLE:
					msg = reason.toString();
					alertDialog
							.setPositiveOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									alertDialog.dismiss();
									exitGame();
								}
							});
					break;
				/*
				 * case TIME_OUT_READY_ROLE: msg = reason.toString();
				 * alertDialog .setPositiveOnClickListener(new
				 * View.OnClickListener() {
				 * 
				 * @Override public void onClick(View v) { exitGame(); } });
				 * break;
				 */
				default:
					msg = "aaa";
					break;
				}

				alertDialog.setMessageText(msg);
				alertDialog.show();
			}
		});

	}

	protected void showNotificationDialog(final String msg) {

		this.runOnUiThread(new Runnable() {

			public void run() {

				initAlertDialogIfNeed();
				alertDialog.setTitleText("THÔNG BÁO");
				alertDialog.setMessageText(msg);
				alertDialog.setPositiveText("Đóng");
				alertDialog.setNegativeText(null);
				alertDialog.show();
			}
		});
	}

	protected void handleEventPlayerJoined(long joinedPlayerId) {

		// default, do nothing
	}

	protected boolean prepareForWaitingMode() {
		boolean result = GameData.shareData().getGame()
				.removePlayerIsKickIfHave();
		if (result) {

			GameData.shareData().getGame().prepareForNextMatch();
			organizePlayer(true);
			correctStartGameType();

			showStartGameIfNeed(true);
		}

		return result;
	}

	protected void runBannerComplete(String bannerStr) {

		// default, do nothing
	}

	protected void updateRuleGameDialog() {

	}

	protected void showRuleBtn() {

		if (mRuleBtn == null) {
			return;
		}

		mRuleBtn.setVisible(true);
	}

	protected void onLoadBackgroundResource() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/common/");
		backgroundTextureAtlas = new BuildableBitmapTextureAtlas(
				getTextureManager(), 512, 512);

		backgroundTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				backgroundTextureAtlas, this, mBackgroundResName);

		avatarBgTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				backgroundTextureAtlas, this, imgAvatarBackgroundRes);

		String avatarVipRes = "avatar_vip.xml";
		if (SCREEN_RATIO > 1.5f) {
			avatarVipRes = "avatar_vip_hd.xml";
		}

		try {
			TexturePack texturePack = new TexturePackLoader(getAssets(),
					getTextureManager()).loadFromAsset("gfx/common/"
					+ avatarVipRes, "gfx/common/");
			TexturePackTextureRegionLibrary texturePackLib = texturePack
					.getTexturePackTextureRegionLibrary();
			ITextureRegion[] regions = new ITextureRegion[10];

			texturePack.loadTexture();
			for (int i = 0; i < regions.length; i++) {
				regions[i] = texturePackLib.get(i);
			}

			vipIndicatorTR = new TiledTextureRegion(texturePack.getTexture(),
					regions);
		} catch (TexturePackParseException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		try {
			backgroundTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			backgroundTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void unloadBackgroundResource() {
		backgroundTextureAtlas.unload();
	}

	/*
	 * abstract methods
	 */
	protected abstract void onLoadSpecificGameResource();

	protected abstract void unloadSpecificResources();

	protected abstract void handleEventPlayerLeft(long leftPlayerId);

	protected void renderSpecificElements(
			RenderElementCallBack pRenderElementCallBack) {

	}

	private final RenderElementCallBack mOnRenderElementCallBack = new RenderElementCallBack() {

		@Override
		public void onRenderElementComplete() {
			Log.v("", " -----> show hang di em: " + isReadyShow);
			isReadyShow = true;

			// BaseXeengGame.this.runOnUiThread(new Runnable() {
			//
			// @Override
			// public void run() {
			Log.d("RedrawGame", "About to redraw at onRenderElement callback");
			processRedrawActions();
			// }
			// });
		}
	};

	private BasicDialog dialog;

	protected void showNetworkProblemError() {
		if (dialog == null) {
			dialog = new BasicDialog(this);
		}

		dialog.setMessageText("Lỗi kết nối. Vui lòng kiểm tra lại kết nối trên thiết bị và thử lại sau");
		dialog.setTitle(getString(R.string.default_dialog_title));
		dialog.setPositiveText("Thử lại");
		dialog.setNegativeText("Về login");
		dialog.setCancelable(false);
		dialog.setPositiveOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				GameSocket.shareSocket().reconnect();
				try {
					BusinessRequester.getInstance().reconnect(4, true);
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		dialog.setNegativeOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

				Intent intent = new Intent(BaseXeengGame.this,
						LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});
		dialog.show();
	}

	public void showConnectionSlowWarning() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				if (dialog == null) {
					dialog = new BasicDialog(BaseXeengGame.this);
				}

				dialog.setMessageText("Kết nối chậm");
				dialog.setTitle(getString(R.string.default_dialog_title));
				dialog.setPositiveText("OK");
				dialog.setNegativeText("");
				dialog.setPositiveOnClickListener(null);
				dialog.setNegativeOnClickListener(null);

				dialog.setCancelable(true);
				dialog.show();
			}
		});
	}

	protected InvitePlayerDialog dialogInvitePlayer;
	protected UserProfileDialog dialogUserProfile;
	protected OnClickPlayerSpriteListener playerListener = new OnClickPlayerSpriteListener() {

		@Override
		public void onClickPlayer(final long playerId) {
			Logger.getInstance().warn(BaseXeengGame.this, "on click player");
			BaseXeengGame.this.runOnUiThread(new Runnable() {
				public void run() {
					if (dialogUserProfile != null)
						dialogUserProfile.dismiss();

					dialogUserProfile = new UserProfileDialog(
							BaseXeengGame.this, GameData.shareData().getGame()
									.getPlayer(playerId), true);
					dialogUserProfile.show();
				}
			});
		}

		@Override
		public void onClickInvite() {
			// BaseXeengGame.this.toastOnUiThread("Tính năng đang phát triển",
			// Toast.LENGTH_SHORT);
			BaseXeengGame.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (dialogInvitePlayer != null) {
						dialogInvitePlayer.dismiss();
					}

					dialogInvitePlayer = new InvitePlayerDialog(
							BaseXeengGame.this);
					dialogInvitePlayer.show();
				}
			});
		}
	};

	private OnBannerListener mBannerListener = new OnBannerListener() {

		@Override
		public void onComplete(String bannerStr) {

			runBannerComplete(bannerStr);
		}
	};

	public boolean showEndMatch(boolean isAnimated) {

		if (!isReadyShow) {
			return false;
		}

		if (CustomApplication.shareApplication().wasInBackground) {

			if (mWifiLock.isHeld()) {

				mWifiLock.release();
			}
			CustomApplication.shareApplication().startAppInBackgroundTimer();
			exitGame();
		}
		return true;
	}

	public void showPlayerJoined(long id) {
		// Log.v(" ", " -----> is ready to show: " + isReadyShow);
		if (!isReadyShow) {
			return;
		}

		if (id > -1) {

			// Check if player already in the table.
			int count = 0;
			for (Player player : GameData.shareData().getGame().getPlayerList()) {
				if (player.id == id) {
					if (++count > 1) {
						return;
					}
				}
			}

			Logger.getInstance().info(this,
					"Will place joined player into table");
			placeNewPlayer(id);
			showStartGameIfNeed(false);
			handleEventPlayerJoined(id);
		}
	}

	public void showPlayerLeft(final long id) {

		if (!isReadyShow) {
			return;
		}

		if (id > -1) {

			if (id != GameData.shareData().getMyself().id) {
				removeLeavedPlayer(id);

				refreshTableMaster();
				refreshReadyState();

				if (GameData.shareData().getGame().getMainPlayer().isTableMaster) {
					correctStartGameType();
				}

				showStartGameIfNeed(false);

				processTableMasterTimer();
				handleEventPlayerLeft(id);
			}
		}
	}

	public void showPlayerKicked(long id, KickOutReason reason, String msgIfHas) {
		if (!isReadyShow) {
			return;
		}
		if (id == GameData.shareData().getMyself().id) {

			unregisterBroadcastReceiver();
			if (GameData.shareData().gameId == GameData.ALTP_TYPE) {

				SoundManager.playOutOfTimeEffect();
			}
			if (CustomApplication.shareApplication().wasInBackground) {
				if (mWifiLock.isHeld()) {
					mWifiLock.release();
				}
				CustomApplication.shareApplication().wasInGame = false;
				CustomApplication.shareApplication()
						.startAppInBackgroundTimer();
			} else {
				showMainPlayerIsKickedDialog(reason, msgIfHas);
			}
		} else if (id > -1) {

			removeLeavedPlayer(id);

			refreshTableMaster();
			refreshReadyState();

			showStartGameIfNeed(false);
			processTableMasterTimer();

			handleEventPlayerLeft(id);
			// show dialog notify a player is kicked
			showPlayerIsKickedDialog(msgIfHas);
		}
	}

	protected void exitGame() {
		if (INSTANCE != null)
			finish();
	}

	public void showPlayerReady(long readyPlayerId) {

		if (!isReadyShow)
			return;

		// if readyplayer is main player
		if (readyPlayerId == GameData.shareData().getMyself().id) {
			if (!GameData.shareData().getGame().getMainPlayer().isTableMaster) {
				setReadyForPlayer(readyPlayerId);
				if (GameData.shareData().getGame().getState() == GameState.WAITING) {
					showStartGameIfNeed(false);
					startGameBtn.changeState(StartGameState.HIDDEN);
				}
			}
		} else {
			setReadyForPlayer(readyPlayerId);
			if (GameData.shareData().getGame().getState() == GameState.WAITING) {
				showStartGameIfNeed(false);
			}
		}

		if (GameData.shareData().getGame().getMainPlayer().isTableMaster) {

			if (GameData.shareData().getGame().getState() == GameState.WAITING) {

				correctStartGameType();
				showStartGameIfNeed(false);
			}
		}

		processTableMasterTimer();
	}

	public void showSettingChanged(boolean status, String failMsg) {

		if (!isReadyShow)
			return;

		if (status) {
			organizePlayer(true);
			refreshReadyState();
			correctStartGameType();
			showStartGameIfNeed(false);
			StringBuffer msgBuilder = new StringBuffer();
			CardGameTable table = (CardGameTable) GameData.shareData()
					.getGame().getCurrentTable();
			if (GameData.shareData().getGame().getMainPlayer().isTableMaster) {

				msgBuilder
						.append("Đã thay đổi cấu hình bàn chơi\nSố tiền cược tối thiểu là ");
			} else {

				msgBuilder
						.append("Chủ bàn đã thay đổi cấu hình bàn chơi\nSố tiền cược tối thiểu là ");
			}
			msgBuilder.append(CommonUtils.formatCash(table.getCurrentBet()));
			msgBuilder.append("\nSố người chơi tối đa là ");
			msgBuilder.append(table.getMaxPlayer());
			msgBuilder.append(" người");
			switch (GameData.shareData().gameId) {
			case GameData.TLMN_TYPE:

				TLMNGame game = (TLMNGame) GameData.shareData().getGame();
				if (game.isHideCard) {

					msgBuilder.append("\nẨn số quân bài");
				} else {

					msgBuilder.append("\nHiện số quân bài");
				}
				break;

			default:
				break;
			}
			showNotificationDialog(msgBuilder.toString());
			// update min bet cash
			updateTableInfo();
		} else {

			showNotificationDialog(failMsg);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (getPlayerHolderList() != null) {
			for (BasePlayerHolder playerHolder : getPlayerHolderList()) {
				playerHolder.playerSprite.stopTimer();
			}
		}
	}

	public void showPlayerChat(long chatPlayerId, String msg) {

		if (!isReadyShow)
			return;

		if (chatPlayerId > -1) {

			BasePlayerHolder pc = getPlayer(chatPlayerId);
			if (pc != null) {

				Integer asset = emotionSignalMap.get(msg);
				if (asset == null) {

					pc.chat(msg);
				} else {

					pc.exposeEmotion(asset.intValue());
				}
			}
		}
	}

	public void showFriendRequest(boolean status, final long playerId,
			String playerName, boolean isAddFriend, String message) {

		if (!isReadyShow)
			return;

		if (status) {

			if (playerName == null) {
				showNotificationDialog(message);
			} else {
				if (playerId == -1)
					return;
				final String msg = playerName
						+ " muốn kết bạn với bạn. Bạn có đồng ý không ?";
				BaseXeengGame.this.runOnUiThread(new Runnable() {
					public void run() {
						initAlertDialogIfNeed();
						alertDialog.setTitleText("Kết bạn");
						alertDialog.setMessageText(msg);
						alertDialog
								.setNegativeOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										BusinessRequester.getInstance()
												.addFriend(playerId, false);
									}
								});
						alertDialog
								.setPositiveOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										BusinessRequester.getInstance()
												.addFriend(playerId, true);
									}
								});
						alertDialog.show();
					}
				});
			}
		} else {
			showNotificationDialog(message);
		}
	}

	public void showNetWorkError() {

		Logger.getInstance()
				.warn(BaseXeengGame.this,
						"Did received INTENT_NETWORK_DEVICE_PROBLEM, need check device network and reOpen or exit game");
		BaseXeengGame.this.runOnUiThread(new Runnable() {
			public void run() {

				showNetworkProblemError();
			}
		});
	}

	public void sendReconnection(boolean status) {

		if (!isReadyShow)
			return;

		if (status) {

			Logger.getInstance()
					.warn(BaseXeengGame.this,
							"Did received INTENT_NEED_RECONNECTION, need reconnect NOW");
			try {
				// BusinessRequester.getInstance().reconnect(4, true);
			} catch (Exception e) {
				e.printStackTrace();
				showNetworkProblemError();
			}
		}
	}

	protected void setVisibleEntity(boolean pVisible, Entity... pEntities) {

		for (Entity entity : pEntities) {

			if (entity == null)
				continue;
			entity.setIgnoreUpdate(!pVisible);
			entity.setVisible(pVisible);
		}
	}

	protected void unloadResource(BitmapTextureAtlas... atlas) {

		for (BitmapTextureAtlas bta : atlas) {

			if (bta != null)
				bta.unload();
		}
	}

	/*
	 * 
	 * only receive common broadcast message for all game
	 */
	protected BroadcastReceiver commonReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (CustomApplication.INTENT_APP_WAS_IN_BACKGROUND
					.equalsIgnoreCase(intent.getAction())) {

				if (mWifiLock != null) {

					if (GameData.shareData().getGame().getMainPlayer().isTableMaster) {

						if (GameData.shareData().getGame().getPlayerList()
								.size() < 2
								&& !GameData.shareData().getGame().isTrainingMode) {
							BusinessRequester.getInstance()
									.leaveGame(
											GameData.shareData().getGame()
													.getMatchId());
							CustomApplication.shareApplication().wasInGame = false;
							CustomApplication.shareApplication().finishGameInBackground = true;
							exitGame();
						}
					} else {

						mWifiLock.acquire();
					}
				}
			} else if (MessageService.INTENT_NETWORK_DEVICE_PROBLEM
					.equalsIgnoreCase(intent.getAction())) {

				Logger.getInstance().error(BaseXeengGame.this,
						"NETWORK OF DEVICE has a PROBLEM");
				// hideLoading();
				showNetworkProblemError();
			} else if (MessageService.INTENT_NEED_RECONNECTION
					.equalsIgnoreCase(intent.getAction())) {
				showNetworkProblemError();
			} else if (MessageService.INTENT_MULTIPLE_LOGIN
					.equalsIgnoreCase(intent.getAction())) {
				boolean status = intent.getBooleanExtra(
						NetworkUtils.MESSAGE_STATUS, false);
				if (!status) {
					String message = intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO);
					if (dialog == null) {
						dialog = new BasicDialog(BaseXeengGame.this);
					}

					dialog.setTitle("Thông báo");
					dialog.setMessageText(message);
					dialog.setPositiveText("Về login");
					dialog.setNegativeText("");
					dialog.setPositiveOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();

							Intent intent = new Intent(BaseXeengGame.this,
									LoginActivity.class);

							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
					});

					dialog.setCancelable(false);
					dialog.show();
				}
			} else if (MessageService.INTENT_INVITE_PLAY_NEW_GAME
					.equalsIgnoreCase(intent.getAction())) {
				boolean status = intent.getBooleanExtra(
						NetworkUtils.MESSAGE_STATUS, false);
				if (!status) {
					BaseXeengGame.this.toastOnUiThread(
							intent.getStringExtra(NetworkUtils.MESSAGE_INFO),
							Toast.LENGTH_SHORT);
				}
			} else if (MessageService.INTENT_RECONNECTION
					.equalsIgnoreCase(intent.getAction())) {
				String msg = intent.getStringExtra(NetworkUtils.MESSAGE_INFO);
				if (dialog == null) {
					dialog = new BasicDialog(BaseXeengGame.this);
				}

				dialog.setMessageText(msg);
				dialog.setTitle("Thông báo");
				dialog.setPositiveText("OK");
				dialog.setNegativeText(null);
				dialog.setPositiveOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						BusinessRequester.getInstance().leaveGame(
								GameData.shareData().getGame().getMatchId());

						mScene.registerUpdateHandler(new TimerHandler(3,
								new ITimerCallback() {

									@Override
									public void onTimePassed(TimerHandler arg0) {
										exitGame();
									}
								}));
					}
				});

				dialog.setCancelable(false);
				dialog.show();
			} else if (MessageService.INTENT_JOIN_MATCH.equalsIgnoreCase(intent
					.getAction())) {
				boolean status = intent.getBooleanExtra(
						NetworkUtils.MESSAGE_STATUS, false);
				if (status) {
					if (Build.VERSION.SDK_INT >= 11) {
						recreate();
					} else {
						Intent newIntent = getIntent();
						newIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

						startActivity(newIntent);
						overridePendingTransition(0, 0);

						finish();
						overridePendingTransition(0, 0);
					}
				} else {
					if (dialog == null) {
						dialog = new BasicDialog(BaseXeengGame.this);
					}

					dialog.setMessageText(intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO));
					dialog.setTitle("Thông báo");
					dialog.setPositiveText("OK");
					dialog.setNegativeText(null);
					dialog.setPositiveOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							exitGame();
						}
					});

					dialog.setCancelable(false);
					dialog.show();
				}
			} else if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_RECEIVE_EVENT_ITEM)) {
				final String mes = intent
						.getStringExtra(NetworkUtils.MESSAGE_INFO);

				// BaseXeengGame.this.runOnUpdateThread(new Runnable() {
				//
				// @Override
				// public void run() {
				//
				// }
				// });

				Toast toast = Toast.makeText(BaseXeengGame.this, mes,
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 40);
				toast.show();
			}
		}
	};

	public void toastOnUiThread(final CharSequence text, final int duration) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				final Toast toast = Toast.makeText(BaseXeengGame.this, text,
						duration);
				((TextView) toast.getView().findViewById(android.R.id.message))
						.setGravity(Gravity.CENTER);
				toast.setGravity(Gravity.CENTER, 0, (int) getResources()
						.getDimension(R.dimen.toast_offset_y));
				toast.show();
			}
		});
	}

	public void toastOnUiThread(final CharSequence text) {
		toastOnUiThread(text, Toast.LENGTH_SHORT);
	}

	private CroppedResolutionPolicy mCroppedResolutionPolicy;

	public static interface RenderElementCallBack {

		public void onRenderElementComplete();
	}

	public static interface InitPlayerCallBack {

		public void onInitPlayerComplete();
	}

	public void checkGameStart() {

	}

	protected void runOnUpdateThread(final Runnable runnable, float time) {
		if (mScene == null) {
			return;
		}

		mScene.registerUpdateHandler(new TimerHandler(time,
				new ITimerCallback() {

					@Override
					public void onTimePassed(TimerHandler arg0) {
						runOnUpdateThread(runnable);
					}
				}));
	}
}
