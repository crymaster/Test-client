package com.tv.xeeng.gui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;

import com.tv.xeeng.R;

public class EventDetailsContentFragment extends Fragment {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String TAG_EVENT_CONTENT = "event_content";
	// ===========================================================
	// Fields
	// ===========================================================
	private WebView wvEventContent;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public static EventDetailsContentFragment newInstance(String content) {
		EventDetailsContentFragment fragment = new EventDetailsContentFragment();

		Bundle bundle = new Bundle();

		bundle.putString(TAG_EVENT_CONTENT, content);
		fragment.setArguments(bundle);

		return fragment;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@SuppressLint("InlinedApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.fragment_event_details_content, container, false);

		wvEventContent = (WebView) view
				.findViewById(R.id.wv_event_details_content);

		String data = "<html><body> <font color=\"white\" size=\"4\">"
				+ getArguments().getString(TAG_EVENT_CONTENT)
				+ "</font></body></html>";
		wvEventContent.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		wvEventContent.loadDataWithBaseURL(null, data, "text/html", "utf-8",
				null);

		wvEventContent.setBackgroundColor(0x00000000);
//
//		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
//			wvEventContent.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
//		}

		return view;
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
