package com.tv.xeeng.game.elements.common;

import com.tv.xeeng.game.BaseXeengGame;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.adt.align.HorizontalAlign;

public class XeengTextOptions extends TextOptions {

	public XeengTextOptions() {
		super();
	}

	public XeengTextOptions(AutoWrap pAutoWrap, float pAutoWrapWidth,
			HorizontalAlign pHorizontalAlign, float pLeading) {

		super(pAutoWrap, pAutoWrapWidth / BaseXeengGame.INSTANCE.textScale,
				pHorizontalAlign, pLeading);
	}

	public XeengTextOptions(AutoWrap pAutoWrap, float pAutoWrapWidth,
			HorizontalAlign pHorizontalAlign) {
		super(pAutoWrap, pAutoWrapWidth / BaseXeengGame.INSTANCE.textScale,
				pHorizontalAlign);
	}

	public XeengTextOptions(AutoWrap pAutoWrap, float pAutoWrapWidth) {
		super(pAutoWrap, pAutoWrapWidth / BaseXeengGame.INSTANCE.textScale);
	}

	public XeengTextOptions(HorizontalAlign pHorizontalAlign) {
		super(pHorizontalAlign);
	}
}
