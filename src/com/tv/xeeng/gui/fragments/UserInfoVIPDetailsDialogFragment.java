package com.tv.xeeng.gui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tv.xeeng.R;

public class UserInfoVIPDetailsDialogFragment extends DialogFragment {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String KEY_TITLE = "key_title";
	public static final String KEY_CONTENT = "key_content";
	// ===========================================================
	// Fields
	// ===========================================================
	private TextView tvTitle;
	private TextView tvContent;
	
	private Button btnBack;
	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public static UserInfoVIPDetailsDialogFragment newInstance(String title, String content) {
		UserInfoVIPDetailsDialogFragment fragment = new UserInfoVIPDetailsDialogFragment();
		
		Bundle args = new Bundle();
		args.putString(KEY_TITLE, title);
		args.putString(KEY_CONTENT, content);
		fragment.setArguments(args);
		
		return fragment;
	}
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_user_info_vip_details, container, false);

		Bundle args = getArguments();
		
		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		tvTitle.setText(args.getString(KEY_TITLE));
		
		tvContent = (TextView) view.findViewById(R.id.tv_content);
		tvContent.setText(args.getString(KEY_CONTENT));
		
		btnBack = (Button) view.findViewById(R.id.btn_back);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UserInfoVIPDetailsDialogFragment.this.dismiss();
			}
		});
		
		return view; 
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
