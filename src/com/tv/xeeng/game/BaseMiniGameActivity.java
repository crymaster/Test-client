package com.tv.xeeng.game;

import android.view.View;

import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.game.BaseGame.GameState;
import com.tv.xeeng.game.elements.common.StartGameButton.StartGameState;
import com.tv.xeeng.gamedata.GameData;

import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.opengl.texture.region.ITextureRegion;

public class BaseMiniGameActivity extends BaseXeengGame {
    // ===========================================================
    // Constants
    // ===========================================================
    protected static final String BTN_CHAT_RES = "btn_ingame_chat.png";
    protected static final String BTN_CHAT_RES_HD = "btn_ingame_chat_hd.png";
    protected static final String BTN_EMOTION_RES = "btn_ingame_emoticon.png";
    protected static final String BTN_EMOTION_RES_HD = "btn_ingame_emoticon_hd.png";

    // ===========================================================
    // Fields
    // ===========================================================

    private BuildableBitmapTextureAtlas miniGameFuncBtnsTextureAtlas;

    private BuildableBitmapTextureAtlas miniGameSpecificTextureAtlas;
    public static ITextureRegion bg_scoreRG, winRankTR, loseRankTR, okBtnBgTR,
            rankRg, rankIndicator;

    protected static ITextureRegion iconCenterGlowTR, iconLogoTR;
    private BuildableBitmapTextureAtlas miniGameBackgroundTextureAtlas;
    protected Sprite iconLogo;

    protected Sprite centerGlow;

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
    protected void onLoadFuncResource() {
        super.onLoadFuncResource();
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/common/");

        String btnEmoticonRes, btnChatRes;
        if (SCREEN_RATIO > 1.5f) {
            miniGameFuncBtnsTextureAtlas = new BuildableBitmapTextureAtlas(
                    getTextureManager(), 256, 256);
            btnEmoticonRes = BTN_EMOTION_RES_HD;
            btnChatRes = BTN_CHAT_RES_HD;
        } else {
            miniGameFuncBtnsTextureAtlas = new BuildableBitmapTextureAtlas(
                    getTextureManager(), 64, 64);
            btnEmoticonRes = BTN_EMOTION_RES;
            btnChatRes = BTN_CHAT_RES;
        }

        emoticonBtnTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                miniGameFuncBtnsTextureAtlas, this, btnEmoticonRes);
        chatTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                miniGameFuncBtnsTextureAtlas, this, btnChatRes);

        try {
            miniGameFuncBtnsTextureAtlas
                    .build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
                            0, 1, 0));
            miniGameFuncBtnsTextureAtlas.load();
        } catch (TextureAtlasBuilderException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unloadFuncResource() {
        super.unloadFuncResource();
        miniGameFuncBtnsTextureAtlas.unload();
    }

    @Override
    protected void onCreateFuncButton() {
        super.onCreateFuncButton();
        mChatBtn.setPosition(mBackBtn.getX() + mBackBtn.getWidth() / 2
                + mChatBtn.getWidth() / 2 + SPACING, mPrintScreenBtn.getY());
        mChatBtn.setSize(30, 24);
        hud.attachChild(mChatBtn);
        hud.registerTouchArea(mChatBtn);

        btnEmoticon.setPosition(mRuleBtn.getX() - mRuleBtn.getWidth() / 2
                - mRuleBtn.getWidth() / 2 - SPACING, mRuleBtn.getY());
        btnEmoticon.setSize(30, 24);
        hud.attachChild(btnEmoticon);
        hud.registerTouchArea(btnEmoticon);
    }

    @Override
    protected void onLoadSpecificGameResource() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/common/");

        miniGameSpecificTextureAtlas = new BuildableBitmapTextureAtlas(
                this.getTextureManager(), 512, 512,
                BitmapTextureFormat.RGBA_4444, TextureOptions.BILINEAR);

        bg_scoreRG = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                miniGameSpecificTextureAtlas, this, "popup_bg.png");
        winRankTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                miniGameSpecificTextureAtlas, this, "tiled_rank_win.png");
        loseRankTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                miniGameSpecificTextureAtlas, this, "tiled_rank.png");
        okBtnBgTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                miniGameSpecificTextureAtlas, this, "btn_ok_bg.png");
        rankRg = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                miniGameSpecificTextureAtlas, this, "result_progress_bg.png");
        rankIndicator = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                miniGameSpecificTextureAtlas, this,
                "result_progress_indicator.png");

        try {
            miniGameSpecificTextureAtlas
                    .build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
                            0, 1, 0));
            miniGameSpecificTextureAtlas.load();
        } catch (TextureAtlasBuilderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void unloadSpecificResources() {
        miniGameFuncBtnsTextureAtlas.unload();
    }

    @Override
    protected void handleEventPlayerLeft(long leftPlayerId) {
    }

    @Override
    protected void onLoadBackgroundResource() {
        super.onLoadBackgroundResource();

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/common/");

        String centerGlowRes = "center_glow.png";

        if (SCREEN_RATIO > 1.5f) {
            centerGlowRes = "center_glow_hd.png";
            miniGameBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(
                    getTextureManager(), 1024, 1024);
        } else {
            miniGameBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(
                    getTextureManager(), 512, 512);
        }

        iconCenterGlowTR = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(miniGameBackgroundTextureAtlas, this,
                        centerGlowRes);

        try {
            miniGameBackgroundTextureAtlas
                    .build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
                            0, 1, 0));
            miniGameBackgroundTextureAtlas.load();
        } catch (TextureAtlasBuilderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void unloadBackgroundResource() {
        super.unloadBackgroundResource();
        miniGameBackgroundTextureAtlas.unload();
    }

    @Override
    protected void onCreateBackground() {
        super.onCreateBackground();
        SpriteBackground sbg = (SpriteBackground) mScene.getBackground();
        Sprite sprite = sbg.getSprite();

        centerGlow = new Sprite(CENTER_X, CENTER_Y, iconCenterGlowTR,
                getVertexBufferObjectManager());

        if (SCREEN_RATIO > 1.5f) {
            centerGlow.setSize(iconCenterGlowTR.getWidth() / 2,
                    iconCenterGlowTR.getHeight() / 2);
        }

        iconLogo = new Sprite(centerGlow.getWidth() / 2,
                centerGlow.getHeight() / 2, iconLogoTR,
                getVertexBufferObjectManager());

        iconLogo.setVisible(!GameData.shareData().getGame().getMainPlayer().isTableMaster);

        centerGlow.attachChild(iconLogo);
        centerGlow.setZIndex(Z_START_GAME + 1);

        sprite.attachChild(centerGlow);
    }

    @Override
    protected void showStartGameIfNeed(boolean animated) {
        super.showStartGameIfNeed(animated);

        if (GameData.shareData().getGame().state == GameState.PLAYING
                || !GameData.shareData().getGame().getMainPlayer().isReady || GameData
                .shareData().getGame().getMainPlayer().isTableMaster) {
            centerGlow.setVisible(false);
            iconLogo.setVisible(false);
        } else {
            centerGlow.setVisible(true);
            iconLogo.setVisible(true);
        }
    }

    @Override
    public void onBackPressed() {
        showLeaveMatchConfirm();
    }

    @Override
    protected void startGameConfirmIfNeed() {
        if (GameData.shareData().getGame().getPlayerList().size() < 2) {
            showConfirmTrainingDialog();
        } else {
            super.startGameConfirmIfNeed();
        }
    }

    // @Override
    // protected void onInitSettingDialog() {
    // super.onInitSettingDialog();
    // ArrayList<NUM_PLAYER> players = new
    // ArrayList<BaseSimpleSettingDialog.NUM_PLAYER>();
    // players.add(NUM_PLAYER.PRATICE);
    // players.add(NUM_PLAYER.SOLO);
    // players.add(NUM_PLAYER.FOUR_PLAYER);
    // mSettingDialog.updatePlayer(players);
    // }

    // ===========================================================
    // Methods
    // ===========================================================

    private void showConfirmTrainingDialog() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                initAlertDialogIfNeed();
                alertDialog
                        .setMessageText("Bạn có muốn chơi ở chế độ luyện tập không ?");
                alertDialog
                        .setPositiveOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                alertDialog.hide();
                                BusinessRequester.getInstance().startGame();
                                startGameBtn.changeState(StartGameState.HIDDEN);
                                GameData.shareData().getGame().isTrainingMode = true;
                            }
                        });
                alertDialog.show();
            }
        });
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
