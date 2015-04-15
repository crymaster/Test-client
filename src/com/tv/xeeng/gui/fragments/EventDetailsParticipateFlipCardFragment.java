package com.tv.xeeng.gui.fragments;

import java.util.ArrayList;
import java.util.Collections;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.CustomApplication;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.MessagesID;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gui.BaseLayoutXeengActivity;
import com.tv.xeeng.gui.customview.EventRewardDialog;
import com.tv.xeeng.gui.customview.FlippableView;
import com.tv.xeeng.gui.itemdata.InventoryItemData;
import com.tv.xeeng.R;

public class EventDetailsParticipateFlipCardFragment extends Fragment {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String TAG_EVENT_ID = "event_id";
	public static final String TAG_EVENT_COMPONENTS = "event_components";
	public static final String TAG_EVENT_CONTENT = "event_content";

	private static final int CARD_FLIP_DELAY = 250;
	private static final int SHUFFLE_DURATION = 500;

	// ===========================================================
	// Fields
	// ===========================================================
	private long eventId;

	private GridView gvEventItems;
	private Button btnStart;

	private TextView tvEventDescription;
	private ImageView imvReward;

	private TextView tvEventTitle;

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean status = intent.getBooleanExtra(
					NetworkUtils.MESSAGE_STATUS, false);

			if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_PARTICIPATE_FLIP_CARDS_EVENT)) {
				showEventResult(status,
						intent.getStringExtra(NetworkUtils.MESSAGE_INFO));
			}
		}
	};

	private Handler handler;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public static EventDetailsParticipateFlipCardFragment newInstance(
			long eventId, String[] componentsData, String content) {
		EventDetailsParticipateFlipCardFragment fragment = new EventDetailsParticipateFlipCardFragment();

		Bundle args = new Bundle();

		args.putLong(TAG_EVENT_ID, eventId);
		args.putStringArray(TAG_EVENT_COMPONENTS, componentsData);
		args.putString(TAG_EVENT_CONTENT, content);

		fragment.setArguments(args);

		return fragment;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Bundle args = getArguments();
		String description = args.getString(TAG_EVENT_CONTENT);
		description = description == null ? "" : description;

		eventId = args.getLong(TAG_EVENT_ID);

		View view = inflater.inflate(
				R.layout.fragment_event_details_participate_flip_cards,
				container, false);

		handler = new Handler();

		tvEventTitle = (TextView) view
				.findViewById(R.id.tv_event_details_flip_card_title);
		tvEventTitle.setText(R.string.event_title_begin);

		btnStart = (Button) view.findViewById(R.id.btn_start);
		btnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						startEvent();
					}
				});
			}
		});

		tvEventDescription = (TextView) view
				.findViewById(R.id.tv_event_description);
		tvEventDescription.setText(description);
		imvReward = (ImageView) view.findViewById(R.id.imv_reward);

		String[] components = getArguments().getStringArray(
				TAG_EVENT_COMPONENTS);

		ArrayList<String> componentsList = new ArrayList<String>();
		for (int i = 0; i < components.length - 1; i++) {
			componentsList.add(components[i]);
		}

		gvEventItems = (GridView) view.findViewById(R.id.gv_event_items);
		gvEventItems.setAdapter(new FlippableCardsGridAdapter(componentsList));

		// Last component is reward item.
		int rewardResId = CustomApplication
				.shareApplication()
				.getResources()
				.getIdentifier(
						InventoryItemData.RES_PREFIX
								+ components[components.length - 1],
						"drawable",
						CustomApplication.shareApplication().getPackageName());

		if (rewardResId > 0) {
			imvReward.setImageResource(rewardResId);
		}

		return view;
	}

	@Override
	public void onPause() {
		super.onPause();

		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
				receiver);
	}

	@Override
	public void onResume() {
		super.onResume();

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				receiver,
				new IntentFilter(
						MessageService.INTENT_PARTICIPATE_FLIP_CARDS_EVENT));
	}

	// ===========================================================
	// Methods
	// ===========================================================
	private void startEvent() {
		CommonUtils.setViewEnabled(btnStart, false);

		FlippableCardsGridAdapter adapter = (FlippableCardsGridAdapter) gvEventItems
				.getAdapter();
		adapter.setSelected(-1);
		adapter.notifyDataSetChanged();

		tvEventTitle.setText(R.string.event_title_begin);

		flipDownAll();

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				fakeShuffle();
			}
		}, CARD_FLIP_DELAY * (gvEventItems.getChildCount() + 1));

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				enableFlipping();

				tvEventTitle.setText(R.string.event_title_choose_card);
			}
		}, CARD_FLIP_DELAY * (gvEventItems.getChildCount() + 1)
				+ SHUFFLE_DURATION * 2);
	}

	private void flipDownAll() {
		for (int i = 0; i < gvEventItems.getChildCount(); i++) {
			final FlippableView childView = (FlippableView) gvEventItems
					.getChildAt(i).findViewById(R.id.fv_card);
			if (childView != null && childView.isShowingFrontView()) {
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						childView.flipDown(null);
					}
				}, i * CARD_FLIP_DELAY);
			}
		}
	}

	private void fakeShuffle() {
		// LOL...
		for (int i = 0; i < gvEventItems.getChildCount(); i++) {
			final View childView = gvEventItems.getChildAt(i);

			Animation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
					0, Animation.ABSOLUTE, gvEventItems.getWidth() / 2
							- childView.getLeft() - childView.getWidth() / 2,
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
					0);
			anim.setDuration(SHUFFLE_DURATION);
			anim.setRepeatCount(1);
			anim.setRepeatMode(Animation.REVERSE);

			if (i == gvEventItems.getChildCount() - 1) {
				anim.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation arg0) {
					}

					@Override
					public void onAnimationRepeat(Animation arg0) {
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								((BaseAdapter) gvEventItems.getAdapter())
										.notifyDataSetChanged();
							}
						});
					}

					@Override
					public void onAnimationEnd(Animation arg0) {
					}
				});
			}

			childView.startAnimation(anim);
		}
	}

	private void enableFlipping() {
		gvEventItems.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				FlippableCardsGridAdapter adapter = (FlippableCardsGridAdapter) gvEventItems
						.getAdapter();
				adapter.setSelected(position);

				((BaseLayoutXeengActivity) getActivity()).showLoading();
				BusinessRequester.getInstance().participateEvent(
						MessagesID.PARTICIPATE_FLIP_CARDS_EVENT, eventId);

				gvEventItems.setOnItemClickListener(null);
			}
		});
	}

	private void showEventResult(boolean status, String msg) {
		((BaseLayoutXeengActivity) getActivity()).hideLoading();

		final FlippableCardsGridAdapter adapter = (FlippableCardsGridAdapter) gvEventItems
				.getAdapter();

		if (status) {
			final String[] infos = NetworkUtils.stringSplit(msg,
					NetworkUtils.ELEMENT_SEPERATOR);

			adapter.setSelectedText(infos[1]);
			adapter.notifyDataSetChanged();

			for (int i = 0; i < gvEventItems.getChildCount(); i++) {
				int delay = 0;

				final FlippableView flipView = (FlippableView) gvEventItems
						.getChildAt(i).findViewById(R.id.fv_card);

				if (i == adapter.getSelected()) {
					delay = 0;
				} else {
					delay = FlippableView.FLIP_DURATION;
				}
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						flipView.flipUp(null);
					}
				}, delay);
			}

			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					EventRewardDialog dialog = new EventRewardDialog(
							getActivity(), infos[0]);
					dialog.show();

					dialog.setOnDismissListener(new OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
							CommonUtils.setViewEnabled(btnStart, true);

							tvEventTitle.setText(R.string.event_title_begin);
							adapter.setSelected(-1);
							adapter.notifyDataSetChanged();
						}
					});

					BusinessRequester.getInstance().getUserInfo(
							GameData.shareData().getMyself().id);
				}
			}, FlippableView.FLIP_DURATION * 2 + 1000);
		} else {
			((BaseLayoutXeengActivity) getActivity()).alert(msg);

			adapter.setSelected(-1);
			enableFlipping();
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private static class FlippableCardsGridAdapter extends BaseAdapter {
		private ArrayList<Integer> drawables;

		private ArrayList<String> components;
		private int selected;

		public FlippableCardsGridAdapter(ArrayList<String> components) {
			this.components = components;

			drawables = new ArrayList<Integer>();

			drawables.add(R.drawable.event_card_back_1);
			drawables.add(R.drawable.event_card_back_2);
			drawables.add(R.drawable.event_card_back_3);
			drawables.add(R.drawable.event_card_back_4);
			drawables.add(R.drawable.event_card_back_5);

			selected = -1;
		}

		static class FlippableCardGridViewHolder {
			TextView frontTextView;
			ImageView backImageView;
			ImageView frontOverlay;
			ImageView backOverlay;
		}

		public void setSelected(int position) {
			selected = position;
		}

		public int getSelected() {
			return selected;
		}

		public void setSelectedText(String text) {
			if (components.indexOf(text) != -1) {
				Collections.shuffle(components);
				Collections
						.swap(components, components.indexOf(text), selected);

				notifyDataSetChanged();
			}
		}

		@Override
		public int getCount() {
			return components.size();
		}

		@Override
		public Object getItem(int arg0) {
			return components.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater
						.from(parent.getContext())
						.inflate(
								R.layout.fragment_event_details_participate_flip_cards_item,
								parent, false);

				// For clarity.
				FlippableView flippableView = (FlippableView) convertView
						.findViewById(R.id.fv_card);

				View backView = flippableView.getBackView();
				View frontView = flippableView.getFrontView();

				FlippableCardGridViewHolder holder = new FlippableCardGridViewHolder();
				holder.frontTextView = (TextView) frontView
						.findViewById(R.id.tv_front);
				holder.backImageView = (ImageView) backView
						.findViewById(R.id.imv_back);
				holder.frontOverlay = (ImageView) frontView
						.findViewById(R.id.imv_overlay);
				holder.backOverlay = (ImageView) backView
						.findViewById(R.id.imv_overlay);

				convertView.setTag(holder);
			}

			FlippableCardGridViewHolder holder = (FlippableCardGridViewHolder) convertView
					.getTag();
			if (selected != -1) {
				if (selected == position) {
					holder.frontOverlay.setVisibility(View.GONE);
					holder.backOverlay.setVisibility(View.GONE);
				} else {
					holder.frontOverlay.setVisibility(View.VISIBLE);
					holder.backOverlay.setVisibility(View.VISIBLE);
				}
			} else {
				holder.frontOverlay.setVisibility(View.GONE);
				holder.backOverlay.setVisibility(View.GONE);
			}

			String[] tokens = components.get(position).split(" ");

			holder.frontTextView.setText(CommonUtils.formatGold(Float
					.parseFloat(tokens[0])) + " " + tokens[1]);
			holder.frontTextView
					.setBackgroundResource(R.drawable.event_card_front);

			holder.backImageView.setImageResource(drawables.get(position));

			return convertView;
		}

		@Override
		public void notifyDataSetChanged() {
			if (selected == -1) {
				Collections.shuffle(drawables);
			}
			super.notifyDataSetChanged();
		}
	}
}
