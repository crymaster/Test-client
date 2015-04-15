package com.tv.xeeng.manager;

import java.io.IOException;
import java.util.Random;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.music.MusicManager;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;

import com.tv.xeeng.game.BaseXeengGame;
import com.tv.xeeng.gamedata.GameData;

public class SoundManager {

	static SoundManager instance = new SoundManager();

	private static final String RES_BA_EFFECT = "UntitledS-15.aac";
	private static final String RES_NHAT_EFFECT = "11052.aac";
	private static final String RES_PERFECT_WIN_EFFECT = "e_Jackpot.aac";
	// PHOM
	private static final String RES_PHOM_RESULT_U_EFFECT_MALE1 = "U_nam.aac";
	private static final String RES_PHOM_RESULT_U_EFFECT_FEMALE1 = "U_nu.aac";
	private static final String RES_PHOM_RESULT_MOM_EFFECT = "1021.aac";
	private static final String RES_PHOM_AN_GA_EFFECT_MALE1 = "UntitledS-21.aac";
	private static final String RES_PHOM_AN_GA_EFFECT_FEMALE1 = "Untitled 1-07.aac";
	private static final String RES_PHOM_AN_GA_EFFECT_FEMALE2 = "Untitled 1-14.aac";
	private static final String RES_PHOM_AN_GA_EFFECT_FEMALE3 = "Untitled 1-16.aac";
	private static final String RES_PHOM_BI_AN_GA_EFFECT_MALE1 = "UntitledS-15.aac";
	private static final String RES_PHOM_BI_AN_GA_EFFECT_FEMALE1 = "Untitled 1-09.aac";
	private static final String RES_PHOM_HA_BAI_EFFECT = "habai.mp3";

	private static final String RES_BI_U_DEN_EFFECT = "biuden.mp3";
	private static final String RES_AN_CHOT_EFFECT = "anchot.mp3";
	private static final String RES_BI_AN_CHOT_EFFECT = "bianchot.mp3";
	// END PHOM

	// TLMN
	private static final String RES_TLMN_CHAT_DOI_THONG = "1021.aac";
	private static final String RES_TLMN_CHAT_TU_QUY = "e_BStraight.aac";
	private static final String RES_TLMN_CHAT_2 = "chat.aac";
	private static final String RES_TLMN_BI_CHAT = "bichat.aac";

	// END TLMN

	// MAUBINH
	private static final String RES_MAUBINH_PERFECT_WIN_2_EFFECT = "e_Jockbo.aac";
	private static final String RES_MAUBINH_BROKEN_EFFECT = "binhlung.aac";
	// END MAUBINH

	// private static final String RES_REACH_MISSION_EFFECT = "nv.mp3";
	// private static final String RES_START_GAME_EFFECT = "Start.mp3";

	// COMMON
	private static final String RES_WIN_EFFECT_MALE1 = "11052.aac";
	private static final String RES_WIN_EFFECT_FEMALE1 = "Untitled 1-18.aac";
	private static final String RES_WIN_EFFECT_FEMALE2 = "12082.aac";
	private static final String RES_LOSE_EFFECT_MALE1 = "UntitledS-15.aac";
	private static final String RES_LOSE_EFFECT_MALE2 = "11111.aac";
	private static final String RES_LOSE_EFFECT_MALE3 = "11112.aac";
	private static final String RES_LOSE_EFFECT_FEMALE1 = "Untitled 1-13.aac";

	private static final String RES_FOLD_CARD_EFFECT = "xepbai.mp3";
	private static final String RES_DEAL_CARD_EFFECT = "e_ms_start.aac";
	private static final String RES_SHUFFLE_CARD_EFFECT = "Zing-03.aac";
	private static final String RES_PLAY_CARD_EFFECT_MALE1 = "UntitledS-19.aac";
	private static final String RES_PLAY_CARD_EFFECT_MALE2 = "UntitledS-16.aac";
	private static final String RES_PLAY_CARD_EFFECT_MALE3 = "UntitledS-17.aac";
	private static final String RES_PLAY_CARD_EFFECT_MALE4 = "UntitledS-05.aac";
	private static final String RES_PLAY_CARD_EFFECT_MALE5 = "UntitledS-13.aac";
	private static final String RES_PLAY_CARD_EFFECT_MALE6 = "UntitledS-40.aac";
	private static final String RES_PLAY_CARD_EFFECT_FEMALE1 = "Untitled 1-05.aac";
	private static final String RES_PLAY_CARD_EFFECT_FEMALE2 = "Untitled 1-06.aac";

	private static final String RES_TURN_EFFECT = "1022.aac";
	// END COMMON

	// private static final String RES_DEAL_CARD_POCKER = "chia_bai.mp3";
	// private static final String RES_ADD_MONEY = "add_money.wav";
	// private static final String RES_COIN_SINGLE = "coin_single.mp3";
	// private static final String RES_COIN_MULTI = "coin_multi.mp3";

	private static final String RES_JOIN_GAME_EFFECT = "knock.aac";
	private static final String RES_LEAVE_GAME_EFFECT = "OutRoom.aac";
	private static final String RES_MAX_BET_EFFECT = "MaxClick.aac";
	private static final String RES_MIN_BET_EFFECT = "MinClick.aac";
	private static final String RES_NORMAL_BET_EFFECT = "NumRoll.aac";

	// A Bright Future

	private static final String RES_TIMER_EFFECT = "Timer.aac";
	private static final String RES_OPEN_CARD_EFFECT = "CardOpen.aac";

	private static final String RES_TAKE_DEALER_EFFECT_MALE = "10 diem_Nam.aac";
	private static final String RES_TAKE_DEALER_EFFECT_FEMALE = "10 diem_Nu.aac";

	private static final String RES_END_MATCH_EFFECT = "endgame.aac";

	private static final String RES_SELECT_BAU_CUA_ICON = "e_bet.aac";
	private static final String RES_ROLL_DICE = "Lac xuc sac.aac";
	/*
	 * COMMON MUSIC & SOUND EFFECT
	 */
	private static Sound mJoinGameEffect;
	private static Sound mLeaveGameEffect;

	private static Sound maxBetEffect;
	private static Sound minBetEffect;
	private static Sound betEffect;
	private static Sound timerEffect;
	private static Sound endMatchEffect;

	/*
	 * BAUCUA
	 */

	private static Sound iconSelectEffect;
	private static Sound rollDiceEffect;

	/*
	 * BACAY
	 */
	private static Sound cardOpenEffect;
	private static Sound takeDealerEffectMale;
	private static Sound takeDealerEffectFemale;

	/*
	 * ALTP MUSIC & SOUND EFFECT REFERENCE
	 */
	private static Sound mRightAnswerEffect;
	private static Sound mWrongAnswerEffect;
	private static Sound mInportantEffect;
	private static Sound mHalfHelpEffect;
	private static Sound mHelpButtonEffect;
	private static Music mBackgroundMusic;
	private static Music mLetGoFindEffect;
	private static Music mOutOfTimeEffect;
	private static Music mQuestionEffect[];
	private static Music mWaitingResultEffect;
	private static Music mBackgroundAnswerBasic;
	private static Music mBackgroundAnswerAdvanced;
	private static Music mBackgroundAnswerMillion;
	private static Music[] backgrounds;
	private static Music[] mTrue, mFalse;
	private static TimerHandler delayImportantEffectTimer;

	/*
	 * PIKACHU
	 */
	private static Sound mEateEffect;
	private static Sound mClickEffect;
	private static Sound mNotEateEffect;
	private static Music mPikaBackgroundEffect;

	/*
	 * CARD MUSIC & SOUND EFFECT REFERENCE
	 */
	private static Sound dealCardEffect, mLoseEffect, mWinEffect,
			tlmnPerfectWinEffect, mFoldCardEffect;
	private static Sound playCardEffectMale1, playCardEffectMale2,
			playCardEffectMale3, playCardEffectMale4, playCardEffectMale5,
			playCardEffectMale6, playCardEffectFemale1, playCardEffectFemale2,
			turnEffect, shuffleEffect;

	/*
	 * PHOM
	 */
	private static Sound mWinEffectMale1, mWinEffectFemale1, mWinEffectFemale2,
			mNhiEffect, mBaEffect, mLoseEffectMale1, mLoseEffectMale2,
			mLoseEffectMale3, mLoseEffectFemale1, phomResultUEffectMale1,
			phomResultUEffectFemale1, phomResultMomEffect;
	private static Sound mBiUDenEffect, phomBiAnGaEffectMale1,
			phomBiAnGaEffectFemale1, mBiAnChotEffect, phomAnGaEffectMale1,
			phomAnGaEffectFemale1, phomAnGaEffectFemale2,
			phomAnGaEffectFemale3, mAnChotEffect, mHaBaiEffect;
	/*
	 * TLMN
	 */
	private static Sound mChatDoiThongEffect, mChatTuQuyEffect, mChatHaiEffect,
			mBiChatEffect;

	private static Sound mChiaBaiPockerEffect, mAddMoneyEffect,
			mCoinSingleDropEffect, mCoinMultiDropEffect;

	/**
	 * MauBinh
	 */
	private static Sound perfectWin2Effect, brokenEffect;

	private static boolean isLoadDone = false;;
	private static boolean isBasicPlay = false, isAdvancedPlay = false,
			isMillionPlay = false, isWaitingPlay = false,
			isBackgroundMusicPlay = false;
	private static boolean isPlayed = true;

	public static SoundManager shareSound() {

		return instance;
	}

	private SoundManager() {

		isPlayed = true;
		isLoadDone = false;
	}

	public static void initialize() {

		instance = new SoundManager();
	}

	public static void load() {
		isLoadDone = false;
		loadCommonSound();
		loadSpecificGameSound();
		isLoadDone = true;
	}

	public static void unLoad() {

		unLoadCommon();
		unLoadSpecific();
		instance = null;
	}

	public static void pause() {

		if (instance == null) {

			instance = new SoundManager();
		}
		isPlayed = false;
		switch (GameData.shareData().gameId) {
		case GameData.ALTP_TYPE:
			stopAllBackgroundMusicIfNeed();
			break;

		default:
			break;
		}
	}

	public static void resume() {

		if (instance == null) {

			instance = new SoundManager();
		}
		isPlayed = true;
	}

	public static void playClick(float prate, float pvolumn) {

	}

	public static void playBackgroundMusic() {

		if (SoundManager.isSoundEffectMuted())
			return;
		if (!isLoadDone)
			return;
		if (!isPlayed)
			return;
		if (mBackgroundMusic == null)
			return;
		if (isBackgroundMusicPlay) {
			mBackgroundMusic.seekTo(0);
			mBackgroundMusic.resume();
		} else {
			isBackgroundMusicPlay = true;
			mBackgroundMusic.play();
		}
	}

	public static void pauseBackgroundMusic() {

		if (mBackgroundMusic == null)
			return;
		if (mBackgroundMusic.isPlaying()) {

			mBackgroundMusic.pause();
		}
	}

	public static void playBackgroundAnswerBasicMusic() {

		if (SoundManager.isSoundEffectMuted())
			return;
		if (!isLoadDone)
			return;
		if (!isPlayed)
			return;

		if (mBackgroundAnswerBasic == null)
			return;
		if (isBasicPlay) {

			mBackgroundAnswerBasic.seekTo(0);
			mBackgroundAnswerBasic.resume();
		} else {

			isBasicPlay = true;
			mBackgroundAnswerBasic.play();
		}
	}

	public static void playBackgroundAnswerAdvancedMusic() {

		if (SoundManager.isSoundEffectMuted())
			return;
		if (!isLoadDone)
			return;
		if (!isPlayed)
			return;

		if (mBackgroundAnswerAdvanced == null)
			return;

		stopAllBackgroundMusicIfNeed();
		if (isAdvancedPlay) {

			mBackgroundAnswerAdvanced.seekTo(0);
			mBackgroundAnswerAdvanced.resume();
		} else {

			isAdvancedPlay = true;
			mBackgroundAnswerAdvanced.play();
		}
	}

	public static void playBackgroundAnswerMillionMusic() {

		if (SoundManager.isSoundEffectMuted())
			return;
		if (!isLoadDone)
			return;
		if (!isPlayed)
			return;

		if (mBackgroundAnswerMillion == null)
			return;

		stopAllBackgroundMusicIfNeed();
		if (isMillionPlay) {

			mBackgroundAnswerMillion.seekTo(0);
			mBackgroundAnswerMillion.resume();
		} else {

			isMillionPlay = true;
			mBackgroundAnswerMillion.play();
		}
	}

	public static void playHalfHelpEffect() {

		playSound(mHalfHelpEffect);
	}

	public static void playHelpButtonEffect() {

		playSound(mHelpButtonEffect);
	}

	public static void playLetGoFindEffect() {

		if (mLetGoFindEffect == null)
			return;
		mLetGoFindEffect.play();
	}

	public static void playOutOfTimeEffect() {

		if (mOutOfTimeEffect == null)
			return;
		mOutOfTimeEffect.play();
	}

	public static void playQuestion(int num) {

		if (mQuestionEffect == null)
			return;
		if (num < 0 || num >= mQuestionEffect.length) {

			return;
		}
		if (num < mQuestionEffect.length && num > -1) {

			if (mQuestionEffect[num] != null)
				mQuestionEffect[num].play();
		}

		playBackgroundMusicQuestion(num);

		if ((num + 1) % 5 == 0) {

			int delay = 0;
			if (num + 1 == 5) {

				delay = 2;
			} else if (num + 1 == 10) {

				delay = 3;
			} else if (num + 1 == 15) {

				return;
			}

			final Scene scene = BaseXeengGame.INSTANCE.getCurrentScene();
			if (delayImportantEffectTimer != null) {

				scene.unregisterUpdateHandler(delayImportantEffectTimer);
			}

			delayImportantEffectTimer = new TimerHandler(delay,
					new ITimerCallback() {

						@Override
						public void onTimePassed(TimerHandler pTimerHandler) {

							delayImportantEffectTimer = null;
							playImportantEffect();
						}
					});
			scene.registerUpdateHandler(delayImportantEffectTimer);
		}
	}

	public static void playBackgroundMusicQuestion(int num) {

		if (num == 4) {

			playBackgroundAnswerAdvancedMusic();
		} else if (num == 9 || num == 14) {

			playBackgroundAnswerMillionMusic();
		} else {

			playBackgroundAnswerBasicMusic();
		}
	}

	public static void playRightAnswerEffect() {

		stopImportantEffect();
		stopAllBackgroundMusicIfNeed();
		playSound(mRightAnswerEffect);
	}

	public static void playWrongAnswerEffect() {

		stopImportantEffect();
		stopAllBackgroundMusicIfNeed();
		playSound(mWrongAnswerEffect);
	}

	public static void playWaitingResultEffect() {

		if (SoundManager.isSoundEffectMuted())
			return;
		if (!isPlayed)
			return;

		stopImportantEffect();
		stopAllBackgroundMusicIfNeed();
		if (isWaitingPlay) {

			mWaitingResultEffect.seekTo(0);
			mWaitingResultEffect.resume();
		} else {

			isWaitingPlay = true;
			mWaitingResultEffect.play();
		}
	}

	public static void playImportantEffect() {

		playSound(mInportantEffect);
	}

	public static void playTrue(int pIndex) {

		if (pIndex < 0 || pIndex > 3 || mTrue == null)
			return;
		mTrue[pIndex].play();
	}

	public static void playFalse(int pIndex) {

		if (pIndex < 0 || pIndex > 3 || mFalse == null)
			return;
		mFalse[pIndex].play();
	}

	public static boolean isSoundEffectMuted() {
		return !UserPreference.sharePreference().isSoundEffectOn();
	}

	private static void stopImportantEffect() {

		if (mInportantEffect == null)
			return;
		if (delayImportantEffectTimer != null) {

			BaseXeengGame.INSTANCE.getCurrentScene().unregisterUpdateHandler(
					delayImportantEffectTimer);
			delayImportantEffectTimer = null;
		} else {

			mInportantEffect.stop();
		}
	}

	private static void stopAllBackgroundMusicIfNeed() {

		if (mBackgroundMusic != null) {
			if (mBackgroundMusic.isPlaying()) {
				mBackgroundMusic.pause();
			}
		}

		if (backgrounds != null) {

			for (Music m : backgrounds) {

				if (m.isPlaying()) {

					m.pause();
				}
			}
		}

		if (mWaitingResultEffect != null) {
			if (mWaitingResultEffect.isPlaying()) {
				mWaitingResultEffect.pause();
			}
		}
	}

	/*
	 * BAUCUA
	 */

	public static void playIconSelect() {
		playSound(iconSelectEffect);
	}

	public static void playRollDice() {
		if (rollDiceEffect != null) {
			rollDiceEffect.setLooping(true);
			playSound(rollDiceEffect);
		}
	}

	public static void stopRollDice() {
		if (rollDiceEffect != null) {
			rollDiceEffect.stop();
		}
	}

	/*
	 * BACAY
	 */

	public static void playTakeDealer() {
		if (GameData.shareData().getMyself().sex) {
			playSound(takeDealerEffectMale);
		} else {
			playSound(takeDealerEffectFemale);
		}
	}

	public static void playCardOpen() {
		playSound(cardOpenEffect);
	}

	/*
	 * PIKACHU
	 */
	public static void playBgPika() {
		if (SoundManager.isSoundEffectMuted())
			return;
		if (!isLoadDone)
			return;
		if (!isPlayed)
			return;
		if (mPikaBackgroundEffect == null)
			return;
		if (isBackgroundMusicPlay) {

			mPikaBackgroundEffect.seekTo(0);
			mPikaBackgroundEffect.resume();
		} else {

			isBackgroundMusicPlay = true;
			mPikaBackgroundEffect.play();
		}
	}

	public static void stopPlayBgPika() {
		if (mPikaBackgroundEffect == null)
			return;
		if (mPikaBackgroundEffect.isPlaying()) {

			mPikaBackgroundEffect.pause();
		}
	}

	public static void playEatePikaEffect() {

		playSound(mEateEffect);
	}

	public static void playNotEatePikaEffect() {

		playSound(mNotEateEffect);
	}

	public static void playClickPikaEffect() {

		playSound(mClickEffect);
	}

	/*
	 * CARD
	 */
	public static void playDealCardEffect() {

		playSound(dealCardEffect);
	}

	public static void playFightEffect(boolean isMale) {
		Random rand = new Random();

		if (isMale) {
			switch (rand.nextInt(6)) {
			case 0:
				playSound(playCardEffectMale1);
				break;
			case 1:
				playSound(playCardEffectMale2);
				break;
			case 2:
				playSound(playCardEffectMale3);
				break;
			case 3:
				playSound(playCardEffectMale4);
				break;
			case 4:
				playSound(playCardEffectMale5);
				break;
			case 5:
				playSound(playCardEffectMale6);
				break;
			}
		} else {
			switch (rand.nextInt(2)) {
			case 0:
				playSound(playCardEffectFemale1);
				break;
			case 1:
				playSound(playCardEffectFemale2);
				break;
			}
		}
		playDealCardEffect();
	}

	public static void playFightEffect() {
		boolean isMale = GameData.shareData().getMyself().sex;
		playFightEffect(isMale);
	}

	public static void playTurnEffect() {
		playSound(turnEffect);
	}

	public static void playLoseEffect() {
		playPhomLoseEffect();
	}

	public static void playWinEffect() {
		playPhomWinEffect();
	}

	public static void playTLMNPerfectWinEffect() {
		playSound(tlmnPerfectWinEffect);
		playSound(endMatchEffect);
	}

	public static void playStartGameEffect() {
		playSound(shuffleEffect);
		// playSound(mStartGameEffect);
	}

	public static void playFoldCardEffect() {

		playSound(mFoldCardEffect);
	}

	/*
	 * TLMN
	 */
	public static void playReachMissionEffect() {

		// playSound(mReachMissionEffect);
	}

	public static void playTLMNChatDoiThongEffect() {
		playSound(mChatDoiThongEffect);
	}

	public static void playTLMNChatTuQuyEffect() {
		playSound(mChatTuQuyEffect);
	}

	public static void playTLMNBiChatEffect() {
		playSound(mBiChatEffect);
	}

	public static void playTLMNChatEffect() {
		playSound(mChatHaiEffect);
	}

	/*
	 * PHOM
	 */
	public static void playPhomWinEffect() {
		boolean isMale = GameData.shareData().getMyself().sex;
		Random rand = new Random();

		if (isMale) {
			playSound(mWinEffectMale1);
		} else {
			if (rand.nextInt(2) == 0) {
				playSound(mWinEffectFemale1);
			} else {
				playSound(mWinEffectFemale2);
			}
		}

		playSound(endMatchEffect);
	}

	public static void playNhiEffect() {

		playSound(mNhiEffect);
	}

	public static void playBaEffect() {

		playSound(mBaEffect);
	}

	public static void playPhomLoseEffect() {
		boolean isMale = GameData.shareData().getMyself().sex;
		Random rand = new Random();

		if (isMale) {
			switch (rand.nextInt(3)) {
			case 0:
				playSound(mLoseEffectMale1);
				break;
			case 1:
				playSound(mLoseEffectMale2);
				break;
			case 2:
				playSound(mLoseEffectMale3);
				break;
			}
		} else {
			playSound(mLoseEffectFemale1);
		}
	}

	public static void playPhomMomEffect() {

		playSound(phomResultMomEffect);
	}

	public static void playPhomUEffect() {
		boolean isMale = GameData.shareData().getMyself().sex;

		if (isMale) {
			playSound(phomResultUEffectMale1);
		} else {
			playSound(phomResultUEffectFemale1);
		}

		playEndMatchEffect();
	}

	public static void playBiUDenEffect() {

		playSound(mBiUDenEffect);
	}

	public static void playPhomAnGaEffect(boolean isBiAn) {
		boolean isMale = GameData.shareData().getMyself().sex;
		Random rand = new Random();

		if (isBiAn) {
			if (isMale) {
				playSound(phomBiAnGaEffectMale1);
			} else {
				playSound(phomBiAnGaEffectFemale1);
			}
		} else {
			if (isMale) {
				playSound(phomAnGaEffectMale1);
			} else {
				switch (rand.nextInt(3)) {
				case 0:
					playSound(phomAnGaEffectFemale1);
					break;
				case 1:
					playSound(phomAnGaEffectFemale2);
					break;
				case 2:
					playSound(phomAnGaEffectFemale3);
					break;
				}
			}

		}
	}

	public static void playPhomAnChotEffect(boolean isBiAn) {
		if (isBiAn) {
			playSound(mBiAnChotEffect);
		} else {
			playSound(mAnChotEffect);
		}
		playPhomAnGaEffect(isBiAn);
	}

	public static void playHaBaiEffect() {

		playSound(mHaBaiEffect);
	}

	/**
	 * MauBinh
	 */
	public static void playLoseShowdownEffect() {
		playLoseEffect();
	}

	public static void playWinShowdownEffect() {
		playFightEffect();
	}

	public static void playMauBinhPerfectWinEffect() {
		playSound(tlmnPerfectWinEffect);
	}

	public static void playMauBinhPerfectWinEffect2() {
		playSound(perfectWin2Effect);
	}

	public static void playMauBinhBrokenEffect() {
		playSound(brokenEffect);
	}

	/*
	 * LIENG
	 */
	public static void playDealCardPocker() {

		playSound(mChiaBaiPockerEffect);
	}

	public static void playAddMoney() {

		playSound(mAddMoneyEffect);
	}

	public static void playCoinSingleDropEffect() {

		playSound(mCoinSingleDropEffect);
	}

	public static void playCoinMultiDropEffect() {

		playSound(mCoinMultiDropEffect);
	}

	/*
	 * COMMON
	 */
	public static void playJoinGameEffect() {

		playSound(mJoinGameEffect);
	}

	public static void playLeaveGameEffect() {

		playSound(mLeaveGameEffect);
	}

	public static void playEndMatchEffect() {
		playSound(endMatchEffect);
	}

	public static void playTimerEffect() {
		playTimerEffect(false);
	}
	
	public static void playTimerEffect(boolean isLooped) {
		if (timerEffect != null) {
			timerEffect.setLooping(isLooped);
		}
		playSound(timerEffect);
	}
	
	public static void stopTimerEffect() {
		if (timerEffect != null) {
			timerEffect.stop();
		}
	}

	public static void playMaxBetEffect() {
		playSound(maxBetEffect);
	}

	public static void playMinBetEffect() {
		playSound(minBetEffect);
	}

	public static void playNormalBetEffect() {
		playSound(betEffect);
	}

	private static void playSound(final Sound pSound) {
		if (SoundManager.isSoundEffectMuted())
			return;
		if (!isLoadDone)
			return;
		if (!isPlayed)
			return;
		if (pSound == null)
			return;

		pSound.play();
	}

	// background.mp3
	private static void loadCommonSound() {

		BaseXeengGame gameActivity = BaseXeengGame.INSTANCE;
		org.andengine.audio.sound.SoundManager soundManager = gameActivity
				.getSoundManager();
		SoundFactory.setAssetBasePath("sfx/card/");
		try {

			mJoinGameEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_JOIN_GAME_EFFECT);
			mLeaveGameEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_LEAVE_GAME_EFFECT);
			endMatchEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_END_MATCH_EFFECT);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private static void loadCommonCardSound() {

		BaseXeengGame gameActivity = BaseXeengGame.INSTANCE;
		org.andengine.audio.sound.SoundManager soundManager = gameActivity
				.getSoundManager();
		SoundFactory.setAssetBasePath("sfx/card/");
		try {
			mFoldCardEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_FOLD_CARD_EFFECT);
			// mStartGameEffect =
			// SoundFactory.createSoundFromAsset(soundManager,
			// gameActivity, RES_START_GAME_EFFECT);

			dealCardEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_DEAL_CARD_EFFECT);

			playCardEffectMale1 = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_PLAY_CARD_EFFECT_MALE1);
			playCardEffectMale2 = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_PLAY_CARD_EFFECT_MALE2);
			playCardEffectMale3 = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_PLAY_CARD_EFFECT_MALE3);
			playCardEffectMale4 = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_PLAY_CARD_EFFECT_MALE4);
			playCardEffectMale5 = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_PLAY_CARD_EFFECT_MALE5);
			playCardEffectMale6 = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_PLAY_CARD_EFFECT_MALE6);
			playCardEffectFemale1 = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_PLAY_CARD_EFFECT_FEMALE1);
			playCardEffectFemale2 = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_PLAY_CARD_EFFECT_FEMALE2);

			turnEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_TURN_EFFECT);

			shuffleEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_SHUFFLE_CARD_EFFECT);

			mWinEffectMale1 = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_WIN_EFFECT_MALE1);
			mWinEffectFemale1 = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_WIN_EFFECT_FEMALE1);
			mWinEffectFemale2 = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_WIN_EFFECT_FEMALE2);
			// mNhiEffect = SoundFactory.createSoundFromAsset(soundManager,
			// gameActivity, RES_NHI_EFFECT);
			// mBaEffect = SoundFactory.createSoundFromAsset(soundManager,
			// gameActivity, RES_BA_EFFECT);
			mLoseEffectMale1 = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_LOSE_EFFECT_MALE1);
			mLoseEffectMale2 = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_LOSE_EFFECT_MALE2);
			mLoseEffectMale3 = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_LOSE_EFFECT_MALE3);
			mLoseEffectFemale1 = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_LOSE_EFFECT_FEMALE1);
		} catch (IOException ioe) {

			Logger.getInstance().error(instance,
					"Problem in load sound common card");
		}
	}

	private static void loadSpecificGameSound() {

		BaseXeengGame gameActivity = BaseXeengGame.INSTANCE;
		org.andengine.audio.sound.SoundManager soundManager = gameActivity
				.getSoundManager();
		String assetPath = null;
		switch (GameData.shareData().gameId) {
		case GameData.ALTP_TYPE:
			assetPath = "sfx/altp/";
			MusicFactory.setAssetBasePath(assetPath);
			SoundFactory.setAssetBasePath(assetPath);
			try {

				mHalfHelpEffect = SoundFactory.createSoundFromAsset(
						soundManager, gameActivity, "half_help.mp3");
				mHelpButtonEffect = SoundFactory.createSoundFromAsset(
						soundManager, gameActivity, "help_button_effect.mp3");
				mRightAnswerEffect = SoundFactory.createSoundFromAsset(
						soundManager, gameActivity, "right_answer.mp3");
				mWrongAnswerEffect = SoundFactory.createSoundFromAsset(
						soundManager, gameActivity, "wrong_answer.mp3");
				mInportantEffect = SoundFactory.createSoundFromAsset(
						soundManager, gameActivity, "important.mp3");

				/*
				 * MUSIC
				 */
				final MusicManager mm = gameActivity.getMusicManager();
				mBackgroundMusic = MusicFactory.createMusicFromAsset(
						gameActivity.getMusicManager(), gameActivity,
						"music.mp3");
				mBackgroundMusic.setLooping(true);
				mWaitingResultEffect = MusicFactory.createMusicFromAsset(mm,
						gameActivity, "waiting_result.mp3");
				mWaitingResultEffect.setLooping(true);
				mBackgroundAnswerBasic = MusicFactory.createMusicFromAsset(mm,
						gameActivity, "background_answer_basic.mp3");
				mBackgroundAnswerAdvanced = MusicFactory.createMusicFromAsset(
						mm, gameActivity, "background_answer_advanced.mp3");
				mBackgroundAnswerMillion = MusicFactory.createMusicFromAsset(
						mm, gameActivity, "background_answer_milion.mp3");
				backgrounds = new Music[3];
				backgrounds[0] = mBackgroundAnswerBasic;
				backgrounds[1] = mBackgroundAnswerAdvanced;
				backgrounds[2] = mBackgroundAnswerMillion;
				mBackgroundAnswerBasic.setLooping(true);
				mBackgroundAnswerAdvanced.setLooping(true);
				mBackgroundAnswerMillion.setLooping(true);

				mLetGoFindEffect = MusicFactory.createMusicFromAsset(mm,
						gameActivity, "let_go_find.mp3");
				mOutOfTimeEffect = MusicFactory.createMusicFromAsset(mm,
						gameActivity, "out_of_time.mp3");
				mTrue = new Music[4];
				mFalse = new Music[4];
				for (int i = 0; i < 4; i++) {

					int j = i + 1;
					mTrue[i] = MusicFactory.createMusicFromAsset(mm,
							gameActivity, "true_" + j + ".mp3");
					mFalse[i] = MusicFactory.createMusicFromAsset(mm,
							gameActivity, "lose_" + j + ".mp3");
				}

				String prefixQuesName = "ques";
				String specificName = "";
				mQuestionEffect = new Music[15];
				for (int i = 1; i < mQuestionEffect.length; i++) {

					if (i < 10) {

						specificName = prefixQuesName + "0" + i + ".mp3";
					} else {

						specificName = prefixQuesName + i + ".mp3";
					}

					mQuestionEffect[i - 1] = MusicFactory.createMusicFromAsset(
							mm, gameActivity, specificName);
				}
			} catch (IOException e) {
			}
			break;
		case GameData.MAU_BINH_TYPE:
			loadCommonCardSound();
			loadMauBinhSound();
			loadTimerSound();
			break;
		case GameData.BACAY_TYPE:
			loadCommonCardSound();
			loadBaCay();
			loadBetSound();
			loadTimerSound();
			break;

		case GameData.PHOM_TYPE:
			loadCommonCardSound();
			loadPhom();
			break;
		case GameData.PIKACHU_TYPE:
			assetPath = "sfx/pika/";
			MusicFactory.setAssetBasePath(assetPath);
			SoundFactory.setAssetBasePath(assetPath);
			try {
				mPikaBackgroundEffect = MusicFactory.createMusicFromAsset(
						gameActivity.getMusicManager(), gameActivity, "bg.mp3");
				mPikaBackgroundEffect.setLooping(true);
				mEateEffect = SoundFactory.createSoundFromAsset(soundManager,
						gameActivity, "pikachu_boom.mp3");
				mClickEffect = SoundFactory.createSoundFromAsset(soundManager,
						gameActivity, "pikachu_click.mp3");
				mNotEateEffect = SoundFactory.createSoundFromAsset(
						soundManager, gameActivity, "pikachu_no.wav");
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case GameData.SAM_TYPE:
		case GameData.TLMN_TYPE:
			loadCommonCardSound();
			loadTLMN();
			break;
		case GameData.BAUCUA_TYPE:
			loadCommonCardSound();
			loadBauCua();
			loadBetSound();
			loadTimerSound();
		default:
			break;
		}
	}

	private static void loadMauBinhSound() {
		try {
			BaseXeengGame gameActivity = BaseXeengGame.INSTANCE;
			org.andengine.audio.sound.SoundManager soundManager = gameActivity
					.getSoundManager();
			SoundFactory.setAssetBasePath("sfx/card/");

			cardOpenEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_OPEN_CARD_EFFECT);
			tlmnPerfectWinEffect = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_PERFECT_WIN_EFFECT);
			perfectWin2Effect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_MAUBINH_PERFECT_WIN_2_EFFECT);
			brokenEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_MAUBINH_BROKEN_EFFECT);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadBauCua() {
		try {
			BaseXeengGame gameActivity = BaseXeengGame.INSTANCE;
			org.andengine.audio.sound.SoundManager soundManager = gameActivity
					.getSoundManager();
			SoundFactory.setAssetBasePath("sfx/card/");

			rollDiceEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_ROLL_DICE);
			iconSelectEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_SELECT_BAU_CUA_ICON);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void unloadBauCua() {
		unLoadSound(rollDiceEffect, iconSelectEffect);
	}

	private static void loadTimerSound() {
		try {
			BaseXeengGame gameActivity = BaseXeengGame.INSTANCE;
			org.andengine.audio.sound.SoundManager soundManager = gameActivity
					.getSoundManager();
			SoundFactory.setAssetBasePath("sfx/card/");

			timerEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_TIMER_EFFECT);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void unloadTimerSound() {
		unLoadSound(timerEffect);
	}

	private static void loadBetSound() {
		try {
			BaseXeengGame gameActivity = BaseXeengGame.INSTANCE;
			org.andengine.audio.sound.SoundManager soundManager = gameActivity
					.getSoundManager();
			SoundFactory.setAssetBasePath("sfx/common/");

			maxBetEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_MAX_BET_EFFECT);
			minBetEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_MIN_BET_EFFECT);
			betEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_NORMAL_BET_EFFECT);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void unloadBetSound() {
		unLoadSound(maxBetEffect, minBetEffect, betEffect);
	}

	private static void loadBaCay() {
		try {
			BaseXeengGame gameActivity = BaseXeengGame.INSTANCE;
			org.andengine.audio.sound.SoundManager soundManager = gameActivity
					.getSoundManager();
			SoundFactory.setAssetBasePath("sfx/card/");

			takeDealerEffectMale = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_TAKE_DEALER_EFFECT_MALE);
			takeDealerEffectFemale = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_TAKE_DEALER_EFFECT_FEMALE);
			cardOpenEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_OPEN_CARD_EFFECT);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadTLMN() {
		try {
			BaseXeengGame gameActivity = BaseXeengGame.INSTANCE;
			org.andengine.audio.sound.SoundManager soundManager = gameActivity
					.getSoundManager();
			SoundFactory.setAssetBasePath("sfx/card/");

			mLoseEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_BA_EFFECT);
			mWinEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_NHAT_EFFECT);
			tlmnPerfectWinEffect = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_PERFECT_WIN_EFFECT);
			// mReachMissionEffect = SoundFactory.createSoundFromAsset(
			// soundManager, gameActivity, RES_REACH_MISSION_EFFECT);
			mChatDoiThongEffect = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_TLMN_CHAT_DOI_THONG);
			mChatTuQuyEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_TLMN_CHAT_TU_QUY);
			mChatHaiEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_TLMN_CHAT_2);
			mBiChatEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_TLMN_BI_CHAT);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadPhom() {
		try {
			BaseXeengGame gameActivity = BaseXeengGame.INSTANCE;
			org.andengine.audio.sound.SoundManager soundManager = gameActivity
					.getSoundManager();
			SoundFactory.setAssetBasePath("sfx/card/");

			phomResultUEffectMale1 = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_PHOM_RESULT_U_EFFECT_MALE1);
			phomResultUEffectFemale1 = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity,
					RES_PHOM_RESULT_U_EFFECT_FEMALE1);
			phomResultMomEffect = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_PHOM_RESULT_MOM_EFFECT);
			phomAnGaEffectMale1 = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_PHOM_AN_GA_EFFECT_MALE1);
			phomAnGaEffectFemale1 = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_PHOM_AN_GA_EFFECT_FEMALE1);
			phomAnGaEffectFemale2 = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_PHOM_AN_GA_EFFECT_FEMALE2);
			phomAnGaEffectFemale3 = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_PHOM_AN_GA_EFFECT_FEMALE3);
			phomBiAnGaEffectMale1 = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity, RES_PHOM_BI_AN_GA_EFFECT_MALE1);
			phomBiAnGaEffectFemale1 = SoundFactory.createSoundFromAsset(
					soundManager, gameActivity,
					RES_PHOM_BI_AN_GA_EFFECT_FEMALE1);

			mBiUDenEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_BI_U_DEN_EFFECT);
			mAnChotEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_AN_CHOT_EFFECT);
			mBiAnChotEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_BI_AN_CHOT_EFFECT);
			mHaBaiEffect = SoundFactory.createSoundFromAsset(soundManager,
					gameActivity, RES_PHOM_HA_BAI_EFFECT);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void unLoadCommon() {
		unLoadSound(mJoinGameEffect, mLeaveGameEffect, endMatchEffect);
		mJoinGameEffect = null;
		mLeaveGameEffect = null;
		isLoadDone = false;
	}

	private static void unLoadSpecific() {

		switch (GameData.shareData().gameId) {
		case GameData.ALTP_TYPE:
			unLoadALTP();
			break;

		case GameData.BACAY_TYPE:
			unloadCommonCardSound();
			unloadBaCay();
			unloadTimerSound();
			unloadBetSound();
			break;
		case GameData.BAUCUA_TYPE:
			unloadCommonCardSound();
			unloadBauCua();
			unloadTimerSound();
			unloadBetSound();
			break;
		case GameData.PHOM_TYPE:
			unloadCommonCardSound();
			unLoadPhom();
			break;
		case GameData.MAU_BINH_TYPE:
			unloadCommonCardSound();
			unloadMauBinhSound();
			unloadTimerSound();
			break;
		case GameData.TLMN_TYPE:
			unloadCommonCardSound();
			unLoadTLMN();
			break;
		case GameData.PIKACHU_TYPE:
			unloadPika();
			break;
		default:
			break;
		}
	}

	private static void unloadMauBinhSound() {
		unLoadSound(perfectWin2Effect, brokenEffect, tlmnPerfectWinEffect,
				cardOpenEffect);
	}

	private static void unloadLieng() {

		unLoadSound(mAddMoneyEffect, mChiaBaiPockerEffect,
				mCoinSingleDropEffect, mCoinMultiDropEffect);
	}

	public static void unloadPika() {
		if (mPikaBackgroundEffect != null)
			mPikaBackgroundEffect.release();
		if (mClickEffect != null)
			mClickEffect.release();
		if (mEateEffect != null)
			mEateEffect.release();
		if (mNotEateEffect != null)
			mNotEateEffect.release();
	}

	private static void unLoadALTP() {

		if (mBackgroundMusic != null)
			mBackgroundMusic.release();
		if (mBackgroundAnswerAdvanced != null)
			mBackgroundAnswerAdvanced.release();
		if (mBackgroundAnswerBasic != null)
			mBackgroundAnswerBasic.release();
		if (mBackgroundAnswerMillion != null)
			mBackgroundAnswerMillion.release();
		if (mLetGoFindEffect != null)
			mLetGoFindEffect.release();
		if (mOutOfTimeEffect != null)
			mOutOfTimeEffect.release();
		if (mWaitingResultEffect != null)
			mWaitingResultEffect.release();
		if (mQuestionEffect != null) {

			for (Music q : mQuestionEffect) {

				if (q != null)
					q.release();
			}
		}
		if (mTrue != null) {

			for (Music t : mTrue) {

				if (t != null)
					t.release();
			}
		}

		if (mFalse != null) {

			for (Music f : mFalse) {

				if (f != null)
					f.release();
			}
		}
		mTrue = null;
		mFalse = null;
		mBackgroundMusic = null;
		mBackgroundAnswerAdvanced = null;
		mBackgroundAnswerBasic = null;
		mBackgroundAnswerMillion = null;
		mLetGoFindEffect = null;
		mOutOfTimeEffect = null;
		mQuestionEffect = null;
		mWaitingResultEffect = null;
		backgrounds = null;

		if (mRightAnswerEffect != null)
			mRightAnswerEffect.release();
		if (mWrongAnswerEffect != null)
			mWrongAnswerEffect.release();
		if (mInportantEffect != null)
			mInportantEffect.release();
		if (mHalfHelpEffect != null)
			mHalfHelpEffect.release();
		if (mHelpButtonEffect != null)
			mHelpButtonEffect.release();
		mRightAnswerEffect = null;
		mWrongAnswerEffect = null;
		mInportantEffect = null;
		mHalfHelpEffect = null;
		mHelpButtonEffect = null;

		isAdvancedPlay = false;
		isMillionPlay = false;
		isBackgroundMusicPlay = false;
	}

	private static void unLoadTLMN() {
		unLoadSound(dealCardEffect, playCardEffectMale1, playCardEffectMale2,
				playCardEffectMale3, playCardEffectMale4, playCardEffectMale5,
				playCardEffectMale6, playCardEffectFemale1,
				playCardEffectFemale2, turnEffect, mLoseEffect, mWinEffect,
				tlmnPerfectWinEffect, mFoldCardEffect, shuffleEffect,
				mChatDoiThongEffect, mChatTuQuyEffect, mChatHaiEffect,
				mBiChatEffect);
	}

	private static void unLoadPhom() {
		unLoadSound(dealCardEffect, playCardEffectMale1, playCardEffectMale2,
				playCardEffectMale3, playCardEffectMale4, playCardEffectMale5,
				playCardEffectMale6, playCardEffectFemale1,
				playCardEffectFemale2, turnEffect, shuffleEffect,
				mFoldCardEffect, phomResultUEffectMale1,
				phomResultUEffectFemale1, phomResultMomEffect, mBiAnChotEffect,
				phomBiAnGaEffectMale1, phomBiAnGaEffectFemale1, mBiUDenEffect,
				mAnChotEffect, phomAnGaEffectMale1, phomAnGaEffectFemale1,
				phomAnGaEffectFemale2, phomAnGaEffectFemale3, mHaBaiEffect);
	}

	private static void unloadCommonCardSound() {
		unLoadSound(mWinEffectMale1, mWinEffectFemale1, mWinEffectFemale2,
				mNhiEffect, mBaEffect, mLoseEffectMale1, mLoseEffectMale2,
				mLoseEffectMale3, mLoseEffectFemale1);
	}

	private static void unloadBaCay() {
		unLoadSound(mWinEffect, mLoseEffect, takeDealerEffectMale,
				takeDealerEffectFemale, cardOpenEffect);
	}

	@SuppressWarnings("unused")
	private static void unLoadMusic(Music... ms) {

		for (Music m : ms) {

			if (m != null)
				m.release();
			m = null;
		}
	}

	private static void unLoadSound(Sound... sounds) {

		for (Sound s : sounds) {

			if (s != null)
				s.release();
			s = null;
		}
	}
}
