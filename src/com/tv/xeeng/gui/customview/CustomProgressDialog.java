package com.tv.xeeng.gui.customview;

import java.lang.reflect.Field;
import java.text.NumberFormat;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class CustomProgressDialog extends ProgressDialog {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private String progressNumberFormat = "%d / %d";
	private NumberFormat progressPercentFormat = NumberFormat
			.getPercentInstance();

	private TextView reflectedProgressNumber;
	private TextView reflectedProgressPercent;

	// ===========================================================
	// Constructors
	// ===========================================================
	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomProgressDialog(Context context) {
		super(context);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			Field[] fields = this.getClass().getSuperclass()
					.getDeclaredFields();

			for (Field field : fields) {
				if (field.getName().equalsIgnoreCase("mProgressNumber")) {
					field.setAccessible(true);
					reflectedProgressNumber = (TextView) field.get(this);
				} else if (field.getName().equalsIgnoreCase("mProgressPercent")) {
					field.setAccessible(true);
					reflectedProgressPercent = (TextView) field.get(this);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setProgressNumberFormat(String format) {
		if (Build.VERSION.SDK_INT >= 11) {
			super.setProgressNumberFormat(format);
		} else {
			this.progressNumberFormat = format;
		}
	}

	@Override
	public void setProgress(int value) {
		super.setProgress(value);
		if (Build.VERSION.SDK_INT < 11) {
			if (reflectedProgressNumber != null) {
				if (progressNumberFormat != null) {
					reflectedProgressNumber.setVisibility(View.VISIBLE);
					reflectedProgressNumber.setText(String.format(
							progressNumberFormat, value, getMax()));
				} else {
					reflectedProgressNumber.setVisibility(View.INVISIBLE);
				}
			}
			if (reflectedProgressPercent != null) {
				if (progressPercentFormat != null) {
					reflectedProgressPercent.setVisibility(View.VISIBLE);
					reflectedProgressPercent
							.setText(progressPercentFormat
									.format((double) getProgress()
											/ (double) getMax()));
				} else {
					reflectedProgressPercent.setVisibility(View.INVISIBLE);
				}
			}
		}
	}

	@Override
	public void setProgressPercentFormat(NumberFormat format) {
		if (Build.VERSION.SDK_INT >= 11) {
			super.setProgressPercentFormat(format);
		} else {
			this.progressPercentFormat = format;
		}
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
