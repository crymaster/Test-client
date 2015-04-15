package com.tv.xeeng.gui.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tv.xeeng.R;

public class InventoryItem extends RelativeLayout {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private String name = "";
	private int count;
	private int resId;
	private boolean isEmpty;
	private boolean isNameVisible;
	private boolean isCountVisible;

	private float nameTextSize;
	private float countTextSize;
	private float scale;

	private ImageView imvIcon;
	private TextView tvName;
	private TextView tvCount;

	// ===========================================================
	// Constructors
	// ===========================================================
	public InventoryItem(Context context) {
		super(context);
		initLayout();
	}

	public InventoryItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		readCustomAttrs(attrs);
		initLayout();
	}

	public InventoryItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		readCustomAttrs(attrs);
		initLayout();
	}

	// public InventoryItem(Context context, String name, int count, int resId,
	// boolean isEmpty, boolean isNameVisible) {
	// this(context);
	//
	// setName(name);
	// setCount(count);
	// setResId(resId);
	//
	// setEmpty(isEmpty);
	// setNameVisible(isNameVisible);
	// }

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		tvName.setText(name);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
		tvCount.setText(count + "");
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
		imvIcon.setImageResource(resId);
	}

	public void setNameVisible(boolean isVisible) {
		this.isNameVisible = isVisible;
		tvName.setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}

	public boolean isNameVisible() {
		return isNameVisible;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
		if (isEmpty) {
			imvIcon.setVisibility(View.INVISIBLE);
			tvName.setVisibility(View.INVISIBLE);
			tvCount.setVisibility(View.INVISIBLE);

			setBackgroundResource(R.drawable.homdo_bg_empty);
		} else {
			imvIcon.setVisibility(View.VISIBLE);

			setBackgroundResource(R.drawable.homdo_bg);
		}
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public boolean isCountvisible() {
		return isCountVisible;
	}

	public void setCountvisible(boolean isCountVisible) {
		this.isCountVisible = isCountVisible;
		tvCount.setVisibility(isCountVisible ? View.VISIBLE : View.INVISIBLE);
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		tvCount.setWidth((int) (tvCount.getLineHeight() * scale * 44 / 12));
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = (int) (MeasureSpec.getSize(widthMeasureSpec) * scale);
		int height = (int) (MeasureSpec.getSize(heightMeasureSpec) * scale);
		this.setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.scale(scale, scale, 0, 0);
		super.onDraw(canvas);
	}

	// ===========================================================
	// Methods
	// ===========================================================
	private void initLayout() {
		RelativeLayout.LayoutParams iconLayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		iconLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		iconLayoutParams.setMargins(5, 5, 5, 5);

		imvIcon = new ImageView(getContext());
		imvIcon.setLayoutParams(iconLayoutParams);
		imvIcon.setScaleType(ScaleType.FIT_XY);
		imvIcon.setImageResource(resId);

		tvName = new TextView(getContext());
		tvName.setLayoutParams(new LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT));
		tvName.setGravity(Gravity.CENTER);
		tvName.setBackgroundResource(R.drawable.homdo_item_title_bg);
		tvName.setTypeface(tvName.getTypeface(), Typeface.BOLD);
		tvName.setTextSize(nameTextSize);
		tvName.setText(name);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

		tvCount = new TextView(getContext());
		tvCount.setLayoutParams(params);
		tvCount.setGravity(Gravity.CENTER);
		tvCount.setBackgroundResource(R.drawable.homdo_item_number_bg);
		tvCount.setTypeface(tvCount.getTypeface(), Typeface.BOLD);
		tvCount.setTextSize(countTextSize);
		tvCount.setText(count + "");

		addView(imvIcon);
		addView(tvName);
		addView(tvCount);

		setEmpty(isEmpty);
		setNameVisible(isNameVisible);
		setCountvisible(isCountVisible);
	}

	private void readCustomAttrs(AttributeSet attrs) {
		TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
				R.styleable.InventoryItem);

		this.name = typedArray.getString(R.styleable.InventoryItem_name);
		this.count = typedArray.getInt(R.styleable.InventoryItem_count, 0);
		this.resId = typedArray.getResourceId(
				R.styleable.InventoryItem_iconSrc, R.drawable.homdo_bg_empty);
		this.scale = typedArray.getFloat(R.styleable.InventoryItem_scale, 1.0f);
		this.isNameVisible = typedArray.getBoolean(
				R.styleable.InventoryItem_isNameVisible, true);
		this.isCountVisible = typedArray.getBoolean(
				R.styleable.InventoryItem_isCountVisible, true);
		this.isEmpty = typedArray.getBoolean(R.styleable.InventoryItem_isEmpty,
				false);
		this.nameTextSize = typedArray.getDimension(
				R.styleable.InventoryItem_nameTextSize, 14f);
		this.countTextSize = typedArray.getDimension(
				R.styleable.InventoryItem_countTextSize, 14f);

		typedArray.recycle();
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
