package com.tv.xeeng.gui;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.CustomViewPager;
import android.support.v4.view.CustomViewPager.SimpleOnPageChangeListener;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tv.xeeng.BackgroundThreadManager;
import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.CustomApplication;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.MessagesID;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gamedata.entity.Player;
import com.tv.xeeng.gui.UserHomeActivity.CarouselAdapter.OnCarouselItemClickListener;
import com.tv.xeeng.gui.customview.BasicDialog;
import com.tv.xeeng.gui.customview.GiftCodeDialog;
import com.tv.xeeng.gui.customview.UserProfileDialog;
import com.tv.xeeng.gui.itemdata.EventItemData;
import com.tv.xeeng.manager.UserPreference;
import com.tv.xeeng.R;

public class UserHomeActivity extends BaseLayoutXeengActivity {
	private static final String PACKAGE_NAME_TLMN = "tlmn";

	public final static String USER_HOME_ACTIVITY = "user_home";

	public final static float MAX_SCALE = 1f;
	public final static float MIN_SCALE = 0.6f;
	public final static float MIDDLE_SCALE = (MAX_SCALE + MIN_SCALE) / 2;
	public final static float SCALE_DIF = MAX_SCALE - MIDDLE_SCALE;

	public static final String REJOINING = "rejoining";
	public static final String REJOIN_MSG = "rejoin_msg";
	public static final String REJOIN_MATCH_ID = "rejoin_match_id";
	public static final String REJOIN_GAME_ID = "rejoin_game_id";

	public static ViewPager pager;
	private Button btnShopping;
	private Button btnCharging;
	private Button btnGift;
	

	private ArrayList<Integer> mData = new ArrayList<Integer>(0);

	private Button btnTabTop;
	private Button btnTabEvents;
	private Button btnTabNew;
	private ImageButton btnPlay;

	private ImageButton btnJoinEvent1;
	private ImageButton btnJoinEvent2;

	private ListView listView;

	{
		isVisibleTvPromotion = true;
		isVisibleBtnSettings = true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLayout();

		Intent intent = getIntent();
		boolean isRejoining = intent.getBooleanExtra(REJOINING, false);
		if (isRejoining) {
			String msg = intent.getStringExtra(REJOIN_MSG);
			final long matchId = intent.getLongExtra(REJOIN_MATCH_ID, 0);
			final int gameId = intent.getIntExtra(REJOIN_GAME_ID, 0);

			final BasicDialog dialog = new BasicDialog(this, "Thông báo", msg,
					null, "Đồng ý");

			dialog.setPositiveOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					MessageService.initDataGame(gameId);

					GameData.shareData().currentTableNumber = (int) matchId;

					BusinessRequester.getInstance().joinGame(matchId);
					dialog.dismiss();
				}
			});

			dialog.setCancelable(false);
			dialog.show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setCurrentTab(0);
		if (this.isFinishing()) {
			GameData.shareData().setGame(null);
		}
	}

	@Override
	public void onBackPressed() {
		final BasicDialog dialog = confirm("Bạn có chắc chắn muốn đăng xuất tài khoản?");
		dialog.setPositiveOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				// TODO: receive logout message from server to logout
				logout();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Fragment rewardCashDialog = getSupportFragmentManager()
				.findFragmentByTag("rewardCashDialog");
		if (rewardCashDialog != null) {
			rewardCashDialog.onActivityResult(requestCode, resultCode, data);
		} else {
			Log.d(getClass().getSimpleName(), "rewardCashDialog is null");
		}
	}

	@Override
	protected void registerReceiveNotification() {
		super.registerReceiveNotification();
		// mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
		// MessageService.INTENT_GET_TRANSFER_CASH_MESSAGE));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_OFFLINE_MESSAGE));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_ADVERTISEMENT));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_JOIN_MATCH));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_NEW_MATCH));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_TOP_PLAYER));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_ALL_NEWS));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_EVENTS_LIST));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GIFT_CODE));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_REQUEST_FRIEND));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_USER_INFO));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_RECONNECTION));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_ROULETTE_GAME_INFO));
	}

	@Override
	protected void unregisterReceiveNotification() {
		super.unregisterReceiveNotification();
		mLocalBroadcastManager.unregisterReceiver(mReceiver);
	}

	private void setCurrentTab(int index) {
		btnTabTop.setBackgroundResource(R.drawable.layer_list_home_table_tab);
		btnTabEvents
				.setBackgroundResource(R.drawable.layer_list_home_table_tab);
		btnTabNew.setBackgroundResource(R.drawable.layer_list_home_table_tab);

		switch (index) {
		case 0:
			btnTabTop
					.setBackgroundResource(R.drawable.layer_list_home_table_tab_select);
			BusinessRequester.getInstance().getMessageId(
					MessagesID.GET_RICHESTS);
			break;
		case 1:
			btnTabNew
					.setBackgroundResource(R.drawable.layer_list_home_table_tab_select);
			BusinessRequester.getInstance().getMessageId(
					MessagesID.GET_ALL_NEWS);
			break;
		case 2:
			btnTabEvents
					.setBackgroundResource(R.drawable.layer_list_home_table_tab_select);
			BusinessRequester.getInstance().getEventsList();
			break;
		default:
			break;
		}
	}

	private View.OnClickListener changeTabHandler = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.table_tab_top:
				setCurrentTab(0);
				break;
			case R.id.table_tab_news:
				setCurrentTab(1);
				break;
			case R.id.table_tab_events:
				setCurrentTab(2);
				break;
			default:
				break;
			}
		}

	};

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MessageService.INTENT_ADVERTISEMENT.equalsIgnoreCase(intent
					.getAction())) {

				boolean status = intent.getExtras().getBoolean(
						NetworkUtils.MESSAGE_STATUS);
				if (status) {
					StringBuffer ad = new StringBuffer();
					String content = intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO);
					String[] values = NetworkUtils.stringSplit(content,
							NetworkUtils.ARRAY_SEPERATOR);

					for (String value : values) {
						String[] infoValues = NetworkUtils.stringSplit(value,
								NetworkUtils.ELEMENT_SEPERATOR);

						int type = Integer.parseInt(infoValues[1]);
						if (type == 1) {
							ad.append("\t\t\t\t\t" + infoValues[0]
									+ "\t\t\t\t\t");
						}
					}

					if (TextUtils.isEmpty(ad)) {
						ad.append(getResources().getString(
								R.string.default_welcome));
					}
					tvPromotion.setText(ad);
				}

			} else if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_GET_TOP_PLAYER)) {

				boolean status = intent.getExtras().getBoolean(
						NetworkUtils.MESSAGE_STATUS);
				if (status) {
					ArrayList<Player> data = intent
							.getParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO);
					if (data != null) {
						final AdapterTopPlayer adapter = new AdapterTopPlayer(
								context, data);
						listView.setAdapter(adapter);
//						listView.setOnItemClickListener(new OnItemClickListener() {
//
//							@Override
//							public void onItemClick(AdapterView<?> adapterView,
//									View view, int position, long itemId) {
//								UserProfileDialog profileDialog = new UserProfileDialog(
//										UserHomeActivity.this, (Player) adapter
//												.getItem(position));
//								profileDialog.show();
//							}
//						});
					}
				}

			} else if (MessageService.INTENT_GET_EVENTS_LIST
					.equalsIgnoreCase(intent.getAction())) {

				boolean status = intent.getExtras().getBoolean(
						NetworkUtils.MESSAGE_STATUS);
				if (status) {
					final ArrayList<EventItemData> data = intent.getExtras()
							.getParcelableArrayList(NetworkUtils.MESSAGE_INFO);
					EventItemAdapter adapter = new EventItemAdapter(data);
					listView.setAdapter(adapter);
					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							Intent nextActivity = new Intent(
									UserHomeActivity.this, InfoActivity.class);

							nextActivity.putExtra("position", position);
							nextActivity.putExtra("type",
									InfoActivity.TAB_EVENTS);
							startActivity(nextActivity);
						}
					});
				} else {
					alert(intent.getStringExtra(NetworkUtils.MESSAGE_INFO));
				}

			} else if (MessageService.INTENT_GET_ALL_NEWS
					.equalsIgnoreCase(intent.getAction())) {

				boolean status = intent.getExtras().getBoolean(
						NetworkUtils.MESSAGE_STATUS);
				if (status) {
					final ArrayList<EventItemData> data = intent.getExtras()
							.getParcelableArrayList(NetworkUtils.MESSAGE_INFO);
					EventItemAdapter adapter = new EventItemAdapter(data);
					listView.setAdapter(adapter);
					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							Intent nextActivity = new Intent(
									UserHomeActivity.this, InfoActivity.class);

							nextActivity.putExtra("position", position);
							nextActivity
									.putExtra("type", InfoActivity.TAB_NEWS);
							startActivity(nextActivity);
						}
					});
				} else {
					alert(intent.getStringExtra(NetworkUtils.MESSAGE_INFO));
				}
			} else if (intent.getAction().equalsIgnoreCase(
					MessageService.INTENT_GIFT_CODE)) {

				BusinessRequester.getInstance().getUserInfo(
						GameData.shareData().getMyself().id);
				boolean status = intent.getExtras().getBoolean(
						NetworkUtils.MESSAGE_STATUS);

				if (status) {

					long giftCode = intent.getExtras().getLong(
							NetworkUtils.MESSAGE_INFO);
					String code = "Bạn nhận được số tiền là: " + giftCode
							+ " <img src = 'gold' >";
					Spanned spanned = Html.fromHtml(code,
							UserHomeActivity.this, null);
					alert(spanned);

				} else {

					String msg = intent.getExtras().getString(
							NetworkUtils.MESSAGE_INFO);
					if (msg != null) {
						alert(msg);
					}

				}
			} else if (MessageService.INTENT_REQUEST_FRIEND
					.equalsIgnoreCase(intent.getAction())) {

				boolean status = intent.getExtras().getBoolean(
						NetworkUtils.MESSAGE_STATUS);
				if (status) {
					BusinessRequester.getInstance().getUserInfo(
							GameData.shareData().getMyself().id);
				}
				String content = intent
						.getStringExtra(NetworkUtils.MESSAGE_INFO);
				alert(content);
			} else if (MessageService.INTENT_RECONNECTION
					.equalsIgnoreCase(intent.getAction())) {

				boolean status = intent.getExtras().getBoolean(
						NetworkUtils.MESSAGE_STATUS);

				if (status) {
					String message = intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO);

					if (message != null && message.length() > 0) {
						// reconnect to game
						Intent gameActivity = new Intent(UserHomeActivity.this,
								GameData.shareData().getGame().activityClass);
						if (gameActivity != null) {
							startActivity(gameActivity);
						}
					} else {
						showNetworkError();
					}
				}
			} else if (MessageService.INTENT_GET_ROULETTE_GAME_INFO
					.equals(intent.getAction())) {
				boolean status = intent.getExtras().getBoolean(
						NetworkUtils.MESSAGE_STATUS);
				if (status) {
					Intent i = new Intent(UserHomeActivity.this,
							SlotMachineActivity.class);
					startActivity(i);
				} else {
					alert(intent.getStringExtra(NetworkUtils.MESSAGE_INFO));
				}

				hideLoading();
			}
		}
	};

	/**
	 * Init layout and widgets.
	 */
	public void initLayout() {
		setContentView(R.layout.activity_home);

		initTopBar();
		initBottomBar();

		// btnCharging = (Button) findViewById(R.id.btn_charging);
		// btnCharging.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// Intent intent = new Intent(UserHomeActivity.this,
		// ChargingActivity.class);
		// startActivity(intent);
		// }
		// });

		btnGift = (Button) findViewById(R.id.btn_gift);
		btnGift.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				final BasicDialog dialog = new GiftCodeDialog(
//						UserHomeActivity.this);
//				dialog.show();
			}
		});

		btnShopping = (Button) findViewById(R.id.btn_shopping);
		btnShopping.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				Intent intent = new Intent(UserHomeActivity.this,
//						StoreActivity.class);
//				startActivity(intent);
			}
		});

		btnTabTop = (Button) findViewById(R.id.table_tab_top);
		btnTabEvents = (Button) findViewById(R.id.table_tab_events);
		btnTabNew = (Button) findViewById(R.id.table_tab_news);
		btnPlay = (ImageButton) findViewById(R.id.play_button);

		btnTabTop
				.setBackgroundResource(R.drawable.layer_list_home_table_tab_select);

		btnTabTop.setOnClickListener(changeTabHandler);
		btnTabNew.setOnClickListener(changeTabHandler);
		btnTabEvents.setOnClickListener(changeTabHandler);
		btnTabEvents.setOnClickListener(changeTabHandler);

		listView = (ListView) findViewById(R.id.listview_table);
		// textCategory.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// Intent intent = new Intent(UserHomeActivity.this,
		// EventActivity.class);
		// startActivity(intent);
		// }
		// });

		if (getPackageName().contains(PACKAGE_NAME_TLMN)) {
			mData.add(0, R.drawable.icon_tlmn);
		} else {
			mData.add(R.drawable.icon_tlmn);
		}

		btnPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(UserHomeActivity.this.getLocalClassName(),
						"Clicked position: " + v.getTag());
				showLoading("Loading");
				GameData.shareData().gameId = GameData.TLMN_TYPE;
				BusinessRequester.getInstance().setZone(GameData.TLMN_TYPE, 0);

			}
		});

		if (UserPreference.sharePreference().getNewbieStatus() < 4
				&& CustomApplication.shareApplication().shouldShowHomeTutorial) {
			CustomApplication.shareApplication().shouldShowHomeTutorial = false;
			showTutorialView(R.drawable.huongdan_home);
		}

		btnJoinEvent1 = (ImageButton) findViewById(R.id.btn_join_event1);
		btnJoinEvent1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(UserHomeActivity.this,
//						EventActivity.class);
//				startActivity(intent);
			}
		});

		findViewById(R.id.rl_event_banner1).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
//						Intent intent = new Intent(UserHomeActivity.this,
//								EventActivity.class);
//						startActivity(intent);
					}
				});

		btnJoinEvent2 = (ImageButton) findViewById(R.id.btn_join_event2);
		btnJoinEvent2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				showLoading();
//				BusinessRequester.getInstance().getRouletteGameInfo();
			}
		});

		findViewById(R.id.rl_event_banner2).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
//						showLoading();
//						BusinessRequester.getInstance().getRouletteGameInfo();
					}
				});

		Animation anim = new TranslateAnimation(0, 10, 0, 0);
		anim.setInterpolator(new CycleInterpolator(5));
		anim.setDuration(300);
		anim.setStartOffset(2000);
		anim.setRepeatMode(Animation.REVERSE);
		anim.setRepeatCount(Animation.INFINITE);

		btnJoinEvent1.startAnimation(anim);
		btnJoinEvent2.startAnimation(anim);

		BusinessRequester.getInstance().getMessageId(MessagesID.GET_RICHESTS);
	}

	private class AdapterTopPlayer extends BaseAdapter {

		private ArrayList<Player> listTopPlayer;

		public AdapterTopPlayer(Context context, ArrayList<Player> objects) {
			listTopPlayer = objects;
		}

		@Override
		public int getCount() {
			return listTopPlayer.size();
		}

		@Override
		public Object getItem(int position) {
			return listTopPlayer.get(position);
		}

		@Override
		public long getItemId(int position) {
			return listTopPlayer.get(position).id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view;
			TopPlayerItemViewHolder viewHolder;

			if (convertView == null) {
				view = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.item_home_table, parent, false);

				viewHolder = new TopPlayerItemViewHolder();
				viewHolder.txtIndex = (TextView) view
						.findViewById(R.id.txt_index);
				viewHolder.txtPlayerName = (TextView) view
						.findViewById(R.id.txt_player_name);
				viewHolder.txtValue = (TextView) view
						.findViewById(R.id.txt_player_gold);
				viewHolder.avatar = (ImageView) view
						.findViewById(R.id.imv_profile_avatar);

				// viewHolder.imgTopThree = (ImageView) view
				// .findViewById(R.id.imgTopThree);

				view.setTag(viewHolder);

			} else {
				view = convertView;
				viewHolder = (TopPlayerItemViewHolder) view.getTag();
			}

			Player item = listTopPlayer.get(position);

			viewHolder.fillData(position, item);

			return view;
		}

	}

	public static class EventItemAdapter extends BaseAdapter {
		private ArrayList<EventItemData> dataList;

		public EventItemAdapter(ArrayList<EventItemData> items) {
			dataList = items;
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public EventItemData getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return dataList.get(position).eventId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			EventItemCellViewHolder viewHolder;

			if (convertView == null) {
				view = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.item_home_table_events, parent, false);

				viewHolder = new EventItemCellViewHolder();
				viewHolder.txtIndex = (TextView) view
						.findViewById(R.id.txt_index);
				viewHolder.txtDescription = (TextView) view
						.findViewById(R.id.txt_description);

				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (EventItemCellViewHolder) view.getTag();
			}

			EventItemData data = getItem(position);
			viewHolder.fillData(data);

			return view;
		}
	}

	static class EventItemCellViewHolder {
		private TextView txtIndex;
		private TextView txtDescription;

		public void fillData(EventItemData data) {
			txtIndex.setText(Integer.toString(data.index));
			txtDescription.setText(data.eventName);
		}
	}

	private ArrayList<Long> progressingUserIds = new ArrayList<Long>();

	class TopPlayerItemViewHolder {
		private TextView txtIndex;
		private TextView txtPlayerName;
		private TextView txtValue;
		private ImageView avatar;

		private void fillData(int index, final Player player) {
			txtIndex.setText(Integer.toString(index + 1));
			txtPlayerName.setText(player.character);
			Bitmap bitmap = CommonUtils.getBitmapFromMemCache(player.id);

			if (bitmap != null) {
				avatar.setImageBitmap(bitmap);
			} else {
				avatar.setImageResource(R.drawable.avatar_default);
				if (!progressingUserIds.contains(player.id)) {
					progressingUserIds.add(player.id);
					BackgroundThreadManager.post(new Runnable() {
						@Override
						public void run() {
							final boolean result = BusinessRequester
									.getInstance().getUserAvatar(player.id);
							if (result) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										if (listView.getAdapter() instanceof AdapterTopPlayer) {
											((AdapterTopPlayer) listView
													.getAdapter())
													.notifyDataSetChanged();
										}
									}
								});
							}
						}
					});
				}
			}

			Float f = (float) player.cash;
			if (f >= 1000000000) {
				txtValue.setText(String.format("%.1f B", f / 1000000000));
			} else if (f >= 1000000)
				txtValue.setText(String.format("%.1f M", f / 1000000));
			else if (f >= 1000)
				txtValue.setText(String.format("%.1f K", f / 1000));
			else
				txtValue.setText(String.format("%.0f", f));
		}
	}

	// private class GetAvatarTask extends AsyncTask<Long, Void, Boolean> {
	//
	// @Override
	// protected Boolean doInBackground(Long... params) {
	// if (params[0] > 0) {
	// return BusinessRequester.getInstance().getUserAvatar(params[0]);
	// }
	// return false;
	// }
	//
	// @Override
	// protected void onPostExecute(Boolean result) {
	// super.onPostExecute(result);
	// if (result) {
	// if (listView.getAdapter() instanceof AdapterTopPlayer) {
	// ((AdapterTopPlayer) listView.getAdapter())
	// .notifyDataSetChanged();
	// }
	// }
	// }
	// }

	public static class CarouselAdapter extends PagerAdapter {
		private ArrayList<Integer> mData = new ArrayList<Integer>(0);
		private OnCarouselItemClickListener listener;

		public void setListener(OnCarouselItemClickListener listener) {
			this.listener = listener;
		}

		public void setData(ArrayList<Integer> data) {
			mData = data;
		}

		@Override
		public int getCount() {
			return 1000;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			CustomImageView image = new CustomImageView(container.getContext());

			image.setLayoutParams(new LinearLayout.LayoutParams(20, 20));
			image.setId(mData.get(position % mData.size()));
			image.setImageResource(mData.get(position % mData.size()));
			image.setScale(UserHomeActivity.MIN_SCALE);
			image.setTag(position);
			image.setOnClickListener(listener);

			container.addView(image, 0);
			return image;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		public static interface OnCarouselItemClickListener extends
				OnClickListener {
			@Override
			public void onClick(View v);
		}
	}

	public static class CustomImageView extends ImageView {
		public CustomImageView(Context context) {
			super(context);
		}

		public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

		public CustomImageView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		private float scale;

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.scale(scale, scale, getWidth() / 2, getHeight() / 2);
			super.onDraw(canvas);
		}

		public void setScale(float scale) {
			this.scale = scale;
			invalidate(getLeft(), getTop(), getRight(), getBottom());
		}
	}
}
