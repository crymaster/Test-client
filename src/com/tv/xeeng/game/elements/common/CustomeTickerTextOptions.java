package com.tv.xeeng.game.elements.common;

import org.andengine.entity.text.AutoWrap;
import org.andengine.util.adt.align.HorizontalAlign;

public class CustomeTickerTextOptions extends XeengTextOptions {

	private int mCharecterVisible;
	private float mCharacterPerMiliseconds;

	public CustomeTickerTextOptions(HorizontalAlign pHorizontalAlign,
			float pCharPerMilisecond, int initCharacterVisible) {

		super(pHorizontalAlign);
		mCharecterVisible = initCharacterVisible;
		mCharacterPerMiliseconds = pCharPerMilisecond;
	}

	public CustomeTickerTextOptions(float pCharPerMilisecond,
			int initCharacterVisible) {
		super();
		mCharecterVisible = initCharacterVisible;
		mCharacterPerMiliseconds = pCharPerMilisecond;
	}

	public CustomeTickerTextOptions(AutoWrap pAutoWrap, float pAutoWrapWidth,
			HorizontalAlign pHorizontalAlign, float pLeading,
			float pCharPerMilisecond, int initCharacterVisible) {
		super(pAutoWrap, pAutoWrapWidth, pHorizontalAlign, pLeading);

		mCharecterVisible = initCharacterVisible;
		mCharacterPerMiliseconds = pCharPerMilisecond;
	}

	public CustomeTickerTextOptions(AutoWrap pAutoWrap, float pAutoWrapWidth,
			HorizontalAlign pHorizontalAlign, float pCharPerMilisecond,
			int initCharacterVisible) {
		super(pAutoWrap, pAutoWrapWidth, pHorizontalAlign);
		mCharecterVisible = initCharacterVisible;
		mCharacterPerMiliseconds = pCharPerMilisecond;
	}

	public CustomeTickerTextOptions(AutoWrap pAutoWrap, float pAutoWrapWidth,
			float pCharPerMilisecond, int initCharacterVisible) {
		super(pAutoWrap, pAutoWrapWidth);
		mCharecterVisible = initCharacterVisible;
		mCharacterPerMiliseconds = pCharPerMilisecond;
	}

	public int getCharecterVisible() {
		return mCharecterVisible;
	}

	public void setCharecterVisible(int charecterVisible) {
		mCharecterVisible = charecterVisible;
	}

	public float getCharacterPerMiliseconds() {
		return mCharacterPerMiliseconds;
	}
}
