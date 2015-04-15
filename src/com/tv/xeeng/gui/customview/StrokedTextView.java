package com.tv.xeeng.gui.customview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.TextView;

import com.tv.xeeng.R;

/**
 * This class adds a stroke to the generic TextView allowing the text to stand
 * out better against the background (ie. in the AllApps button).
 */
public class StrokedTextView extends TextView {
	private boolean stroke = false;  
    private float strokeWidth = 0.0f;  
    private int strokeColor;  
  
    public StrokedTextView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
          
        initView(context, attrs);  
    }  
  
    public StrokedTextView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
          
        initView(context, attrs);  
    }  
  
    public StrokedTextView(Context context) {  
        super(context);  
    }  
      
    private void initView(Context context, AttributeSet attrs) {  
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StrokedTextView);  
        stroke = a.getBoolean(R.styleable.StrokedTextView_textStroke, false);  
        strokeWidth = a.getFloat(R.styleable.StrokedTextView_strokedTextViewStrokeWidth, 0.0f);  
        strokeColor = a.getColor(R.styleable.StrokedTextView_strokedTextViewStrokeColor, 0xffffffff);         
    }  
  
    @Override  
    protected void onDraw(Canvas canvas) {  
          
        if (stroke) {  
            ColorStateList states = getTextColors();  
            getPaint().setStyle(Style.STROKE);  
            getPaint().setStrokeWidth(strokeWidth);  
            setTextColor(strokeColor);    
            super.onDraw(canvas);  
              
            getPaint().setStyle(Style.FILL);  
            setTextColor(states);  
        }  
          
        super.onDraw(canvas);  
    }
}
