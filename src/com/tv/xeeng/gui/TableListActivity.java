package com.tv.xeeng.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.CustomApplication;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.GameSocket;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gamedata.entity.CardGameTable;
import com.tv.xeeng.gamedata.entity.GameTable;
import com.tv.xeeng.gui.customview.BasicDialog;
import com.tv.xeeng.gui.customview.CreateTableDialog;
import com.tv.xeeng.gui.fragments.ChatDialogFragment;
import com.tv.xeeng.gui.itemdata.RoomLevelItemData;
import com.tv.xeeng.manager.UserPreference;
import com.tv.xeeng.R;

public class TableListActivity extends BaseLayoutXeengActivity {

	public static int CHARGING_CODE = 2000;
	public static final String TABLE_LIST_ACTIVITY = "table_list";

	private static final long MIN_BETS[] = { 0, 100000, 1000000, 10000000 };

	private SwipeRefreshLayout refreshLayout;

	private ListView tableListView;
	private TableListAdapter tableListAdapter;
	private ArrayList<GameTable> tableList;
	private ArrayList<RoomLevelItemData> roomLevels;

	private Button btnPlayNow;
	private Button btnCreateTable;
	private Button btnChat;
	private RadioGroup tableLevelLayout;

	private EditText search;

	private View txtTableSize;
	private View txtTableBet;

	private TextView tvChatContent;

	private Handler handler;
	private Runnable runnable;

	private CreateTableDialog createTableDialog;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (GameData.shareData().getGame() == null) {
			logout();
			return;
		}

		setContentView(R.layout.activity_table_list);

		btnPlayNow = (Button) findViewById(R.id.btn_play_now);
		btnPlayNow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showLoading("Đang vào bàn...", true);
				int levelId = 1;
				if (GameData.shareData().currentRoomLevel != null) {
					levelId = GameData.shareData().currentRoomLevel.getId();
				}
				BusinessRequester.getInstance().findFastPlayTable(
						GameData.shareData().gameId, levelId);
			}
		});
		// "Xam"
		btnChat = (Button) findViewById(R.id.btn_chat);
		btnChat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				FragmentTransaction ft = getSupportFragmentManager()
						.beginTransaction();
				Fragment prev = getSupportFragmentManager().findFragmentByTag(
						"dialog");
				if (prev != null) {
					ft.remove(prev);
				}
				ft.addToBackStack(null);

				// Create and show the dialog.
				DialogFragment newFragment = ChatDialogFragment.newInstance();
				newFragment.show(ft, "dialog");
			}
		});

		roomLevels = new ArrayList<RoomLevelItemData>();

		tableLevelLayout = (RadioGroup) findViewById(R.id.rg_room_type);
		tableLevelLayout
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup radioGroup,
							int checkedId) {
						if (checkedId == -1) {
							return;
						}
						int i = 0;

						for (RoomLevelItemData roomLevel : roomLevels) {
							if (roomLevel.getId() == checkedId) {
								if (GameData.shareData().getMyself().cash >= MIN_BETS[i]) {
									showLoading("Loading...", 3000);
									BusinessRequester.getInstance()
											.getTableList(roomLevel.getId());
									GameData.shareData().currentRoomLevel = roomLevel;
								} else {
									final BasicDialog dialog = new BasicDialog(
											TableListActivity.this,
											"Thông báo",
											"Bạn cần tối thiểu "
													+ CommonUtils
															.formatCash(MIN_BETS[i])
													+ " gold để vào kênh này",
											"Đóng", "Nạp tiền");

									dialog.setPositiveOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View arg0) {
											dialog.dismiss();
											Intent charging = new Intent(
													TableListActivity.this,
													ChargingActivity.class);
											startActivity(charging);
										}
									});
									dialog.setCancelable(true);
									dialog.show();

									radioGroup.setOnCheckedChangeListener(null);
									radioGroup.check(GameData.shareData().currentRoomLevel
											.getId());
									radioGroup.setOnCheckedChangeListener(this);
								}
								return;
							}
							i++;
						}
					}
				});
		txtTableSize = findViewById(R.id.txtTablePlayerLable);
		txtTableSize.setOnClickListener(new OnClickListener() {
			boolean desc = true;

			@Override
			public void onClick(View v) {
				tableListAdapter.sortByTableSize(desc);
				desc = !desc;
			}
		});

		txtTableBet = findViewById(R.id.txtTableValueLable);
		txtTableBet.setOnClickListener(new OnClickListener() {
			boolean desc = true;

			@Override
			public void onClick(View v) {
				tableListAdapter.sortByTableBet(desc);
				desc = !desc;
			}
		});
		tableList = (ArrayList<GameTable>) GameData.shareData().getGame()
				.getTableList();

		tableList = tableList != null ? tableList : new ArrayList<GameTable>();
		tableListAdapter = new TableListAdapter(tableList);

		tableListView = (ListView) findViewById(R.id.listTables);
		tableListView.setAdapter(tableListAdapter);
		tableListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int index, long id) {

				TableListAdapter adapter = (TableListAdapter) adapterView
						.getAdapter();
				CardGameTable atable = (CardGameTable) adapter.getItem(index);

				GameData.shareData().getGame().setCurrentTable(atable);

				showLoading("Đang vào bàn...", true);

				BusinessRequester.getInstance().joinGame(atable.getId());
			}
		});

		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
		refreshLayout.setColorSchemeColors(Color.YELLOW, Color.GREEN,
				Color.BLUE, Color.RED);
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				refreshLayout.setRefreshing(true);
				
				if (GameData.shareData().currentRoomLevel == null) {
					return;
				}

				BusinessRequester.getInstance().getTableList(
						GameData.shareData().currentRoomLevel.getId());
			}
		});

		// final GestureDetector gestureDectector = new GestureDetector(this,
		// new GestureDetector.SimpleOnGestureListener() {
		// @Override
		// public boolean onScroll(MotionEvent e1, MotionEvent e2,
		// float distanceX, float distanceY) {
		// if (distanceY < 0) {
		// isTableListScrolling = true;
		// }
		// return super.onScroll(e1, e2, distanceX, distanceY);
		// }
		//
		// });
		// tableListView.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View view, MotionEvent ev) {
		// if (gestureDectector.onTouchEvent(ev)) {
		// return true;
		// }
		// if (ev.getAction() == MotionEvent.ACTION_UP
		// && isTableListScrolling) {
		// isTableListScrolling = false;
		//
		// if (GameData.shareData().currentRoomLevel == null) {
		// return true;
		// }
		//
		// showLoading("Đang tải dữ liệu...", 3000);
		//
		// BusinessRequester.getInstance().getTableList(
		// GameData.shareData().currentRoomLevel.getId());
		//
		// return true;
		// }
		// return false;
		// }
		// });

		search = (EditText) findViewById(R.id.edt_search);
		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				tableListAdapter.getFilter().filter(s);
			}
		});

		// Init common layout
		txtTitle = GameData.shareData().getGame().gameName;
		isVisibleBtnHelp = true;
		isVisibleBtnSettings = true;

		initTopBar();
		initBottomBar();

		btnHelp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int selectedGame = 0;
				switch (GameData.shareData().gameId) {
				case GameData.ALTP_TYPE:
					selectedGame = InfoActivity.GAME_ALTP;
					break;
				case GameData.BACAY_TYPE:
					selectedGame = InfoActivity.GAME_BACAY;
					break;
				case GameData.BAUCUA_TYPE:
					selectedGame = InfoActivity.GAME_BAUCUA;
					break;
				case GameData.PHOM_TYPE:
					selectedGame = InfoActivity.GAME_PHOM;
					break;
				case GameData.PIKACHU_TYPE:
					selectedGame = InfoActivity.GAME_PIKACHU;
					break;
				case GameData.TLMN_TYPE:
					selectedGame = InfoActivity.GAME_TLMN;
					break;
				case GameData.SAM_TYPE:
					selectedGame = InfoActivity.GAME_SAM;
					break;
				case GameData.MAU_BINH_TYPE:
					selectedGame = InfoActivity.GAME_MAU_BINH;
					break;
				default:
					Log.d("Error", "Something is wrong with gameId. Checkout");
					return;
				}
				Intent intent = new Intent(TableListActivity.this,
						InfoActivity.class);
				intent.putExtra(InfoActivity.KEY_SELECTED_TAB,
						InfoActivity.TAB_HELPS);
				intent.putExtra(InfoActivity.KEY_SELECTED_CELL, selectedGame);

				startActivity(intent);
			}
		});

		createTableDialog = new CreateTableDialog(TableListActivity.this);

		btnCreateTable = (Button) findViewById(R.id.btn_create_table);
		btnCreateTable.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (GameData.shareData().currentRoomLevel == null) {
					BusinessRequester.getInstance().getRoomLevels(
							GameData.shareData().gameId);
					return;
				}
				createTableDialog.setAvailableBets(GameData.shareData().currentRoomLevel
						.getAvailableBets());
				createTableDialog.setMaxNumberOfPlayer(GameData.shareData()
						.getGame().getMaxPlayers());
				createTableDialog.show();
			}
		});

		tvChatContent = (TextView) findViewById(R.id.tv_chat_content);

		tableLevelLayout.setEnabled(false);

		if (UserPreference.sharePreference().getNewbieStatus() < 4
				&& CustomApplication.shareApplication().shouldShowTableTutorial) {
			CustomApplication.shareApplication().shouldShowTableTutorial = false;
			showTutorialView(R.drawable.huongdan_chonban);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerNotification();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				BusinessRequester.getInstance().getRoomLevels(
						GameData.shareData().gameId);
			}
		}, 100);
		if (handler == null) {
			handler = new Handler();
		}
		if (runnable == null) {
			runnable = new Runnable() {
				private boolean scrollToBottom = true;

				@Override
				public void run() {
					new ChatDialogFragment.PullContentTask(tvChatContent,
							scrollToBottom)
							.execute(ChatDialogFragment.SHOUT_BOX_HOST);
					scrollToBottom = false;
					handler.postDelayed(this,
							ChatDialogFragment.UPDATE_INTERVAL);
				}
			};
		}
		runnable.run();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocalBroadcastManager.unregisterReceiver(mReceiver);
		handler.removeCallbacks(runnable);
	}

	private void update(ArrayList<GameTable> data) {
		tableListAdapter.setData(data);
		tableListAdapter.getFilter().filter(search.getText().toString());
		tableListAdapter.notifyDataSetChanged();

		hideLoading();
		if (refreshLayout != null) {
			refreshLayout.setRefreshing(false);
		}
	}

	private void registerNotification() {
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_TABLES));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_GET_ROOM_LEVELS));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_JOIN_MATCH));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_CREATE_NEW_TABLE));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_TROUBLE_FAST_PLAY));
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(
				MessageService.INTENT_RECONNECTION));
	}

	private static class TableListAdapter extends BaseAdapter implements
			Filterable {
		private ArrayList<GameTable> data;
		private ArrayList<GameTable> filteredData;
		private Filter filter;

		public TableListAdapter(ArrayList<GameTable> data) {
			this.data = data;
			this.filteredData = new ArrayList<GameTable>(data);
		}

		class TableListViewHolders {
			TextView tableIndex;
			TextView tableLimit;
			TextView tablePlayerNumber;
			TextView tableName;

			public void fillData(GameTable gameTable) {
				tableIndex.setText(Long.toString(gameTable.getId()));
				tableLimit.setText(CommonUtils.formatCash(gameTable
						.getCurrentBet()));

				String activePlayerNum = Integer.toString(gameTable
						.getActivePlayer());
				String maxPlayerNum = Integer
						.toString(gameTable.getMaxPlayer());

				tablePlayerNumber.setText(activePlayerNum + " / "
						+ maxPlayerNum);

				if (gameTable.getActivePlayer() == gameTable.getMaxPlayer()) {
					tablePlayerNumber.setTextColor(Color.RED);
				} else {
					tablePlayerNumber.setTextColor(Color.WHITE);
				}
				tableName.setText(gameTable.getTitle());
			}
		}

		public void setData(ArrayList<GameTable> data) {
			this.data = data;
		}

		@Override
		public int getCount() {
			return filteredData.size();
		}

		@Override
		public GameTable getItem(int position) {
			return filteredData.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		// TẠO BÀN
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;

			GameTable gameTable = filteredData.get(position);
			TableListViewHolders viewHolder = new TableListViewHolders();

			if (convertView == null) {
				view = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.activity_table_list_item, null);
				viewHolder.tableName = (TextView) view
						.findViewById(R.id.tv_table_name);
				viewHolder.tableLimit = (TextView) view
						.findViewById(R.id.tv_table_limit);
				viewHolder.tablePlayerNumber = (TextView) view
						.findViewById(R.id.tv_table_player_number);
				viewHolder.tableIndex = (TextView) view
						.findViewById(R.id.tv_table_index);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (TableListViewHolders) view.getTag();
			}

			viewHolder.fillData(gameTable);
			return view;
		}

		public void sortByTableSize(boolean isDesc) {
			sort(isDesc, new Comparator<GameTable>() {
				@Override
				public int compare(GameTable lhs, GameTable rhs) {
					if (lhs.getActivePlayer() < rhs.getActivePlayer()) {
						return -1;
					}
					if (lhs.getActivePlayer() > rhs.getActivePlayer()) {
						return 1;
					}
					return 0;
				}
			});
		}

		public void sortByTableBet(boolean isDesc) {
			sort(isDesc, new Comparator<GameTable>() {
				@Override
				public int compare(GameTable lhs, GameTable rhs) {
					if (lhs.getMinBetCash() < rhs.getMinBetCash()) {
						return -1;
					}
					if (lhs.getMinBetCash() > rhs.getMinBetCash()) {
						return 1;
					}
					return 0;
				}
			});
		}

		private void sort(boolean isDesc, Comparator<GameTable> comparator) {
			if (filteredData == null) {
				return;
			}
			Collections.sort(filteredData, comparator);
			if (isDesc) {
				Collections.reverse(filteredData);
			}
			notifyDataSetChanged();
		}

		@Override
		public Filter getFilter() {
			return filter != null ? filter : new TableListFilter();
		}

		private class TableListFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence cs) {
				FilterResults results = new FilterResults();

				if (cs != null && cs.length() > 0) {
					filteredData = new ArrayList<GameTable>();
					for (GameTable table : data) {
						if (("" + table.getId()).contains(cs.toString())
								|| table.getTitle().toLowerCase()
										.contains(cs.toString().toLowerCase())) {
							filteredData.add(table);
						}
					}
				} else {
					filteredData = new ArrayList<GameTable>(data);
				}

				results.count = filteredData.size();
				results.values = filteredData;
				return results;
			}

			@Override
			protected void publishResults(CharSequence cs, FilterResults results) {
				if (results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@SuppressWarnings("unchecked")
		@Override
		public void onReceive(Context context, Intent intent) {
			hideLoading();
			boolean status = intent.getBooleanExtra(
					NetworkUtils.MESSAGE_STATUS, false);
			if (MessageService.INTENT_CREATE_NEW_TABLE.equalsIgnoreCase(intent
					.getAction())) {

				if (status) {
					Intent gameActivity = null;
					gameActivity = new Intent(TableListActivity.this, GameData
							.shareData().getGame().activityClass);
					if (gameActivity != null) {
						hideLoading();
						startActivity(gameActivity);
					}
				} else {
					final String msg = intent.getExtras().getString(
							NetworkUtils.MESSAGE_INFO);
					hideLoading();
					TableListActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							if (msg != null) {
								showTableMessages(msg);
							} else {
								alert("Có lỗi xảy ra");
							}
						}
					});

				}
			} else if (MessageService.INTENT_GET_ROOM_LEVELS
					.equalsIgnoreCase(intent.getAction())) {
				if (status) {
					roomLevels = intent
							.getParcelableArrayListExtra(NetworkUtils.MESSAGE_INFO);
					roomLevels = roomLevels != null ? roomLevels
							: new ArrayList<RoomLevelItemData>();

					int selectedId = -1;

					for (int i = 0; i < roomLevels.size(); i++) {
						RoomLevelItemData roomLevel = roomLevels.get(i);
						RadioButton button = (RadioButton) tableLevelLayout
								.getChildAt(i);
						if (button == null) {
							button = new RadioButton(TableListActivity.this);

							LinearLayout.LayoutParams params = new LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT);
							params.rightMargin = 5;

							button.setLayoutParams(params);
							button.setTextSize(14);
							button.setGravity(Gravity.CENTER);
							button.setTextColor(R.drawable.selector_switch_button_text_color);
							button.setButtonDrawable(null);
							button.setBackgroundResource(R.drawable.selector_btn_roomtype);

							tableLevelLayout.addView(button);
						}

						button.setClickable(true);
						button.setVisibility(View.VISIBLE);
						button.setText(roomLevel.getName());
						button.setId(roomLevel.getId());

						if (GameData.shareData().getMyself().cash >= MIN_BETS[i >= MIN_BETS.length ? MIN_BETS.length - 1
								: i]) {
							selectedId = roomLevel.getId();
						}
					}

					if (GameData.shareData().currentRoomLevel != null) {
						int index = GameData.shareData().currentRoomLevel
								.getId() - 1;

						if (index < MIN_BETS.length
								&& GameData.shareData().getMyself().cash >= MIN_BETS[index]) {
							selectedId = index + 1;
						}
					}

					final int finalSelectedId = selectedId == -1 ? 0
							: selectedId;

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							new Handler().postDelayed(new Runnable() {

								public void run() {
									tableLevelLayout.check(-1);
									tableLevelLayout.check(finalSelectedId);
								}

								;
							}, 200);
						}
					});
				}
			} else if (MessageService.INTENT_RECONNECTION
					.equalsIgnoreCase(intent.getAction())) {
				if (status) {
					String message = intent
							.getStringExtra(NetworkUtils.MESSAGE_INFO);

					if (message != null && message.length() > 0) {
						// reconnect to game
						Intent gameActivity = new Intent(
								TableListActivity.this, GameData.shareData()
										.getGame().activityClass);
						if (gameActivity != null) {
							startActivity(gameActivity);
						}
					} else {
						showNetworkError();
					}
				} else {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							BusinessRequester.getInstance().getRoomLevels(
									GameData.shareData().gameId);
						} // ID_ZONE_PIKACHU
					}, 500);
				}
			} else if (MessageService.INTENT_GET_TABLES.equalsIgnoreCase(intent
					.getAction())) {
				tableList = (ArrayList<GameTable>) GameData.shareData()
						.getGame().getTableList();

				tableLevelLayout.setEnabled(true);

				update(tableList);
			} else if (MessageService.INTENT_TROUBLE_FAST_PLAY
					.equalsIgnoreCase(intent.getAction())) {
				if (!status) {

					final String msg = intent.getExtras().getString(
							NetworkUtils.MESSAGE_INFO);
					TableListActivity.this.runOnUiThread(new Runnable() {

						public void run() {
							if (msg != null) {
								final BasicDialog dialog = new BasicDialog(
										TableListActivity.this)
										.setTitleText("Thông báo")
										.setMessageText(msg)
										.setPositiveText("Nạp tiền")
										.setNegativeText("Bỏ qua");

								dialog.show();

								dialog.findViewById(R.id.btn_positive)
										.setOnClickListener(
												new OnClickListener() {

													@Override
													public void onClick(View v) {

														dialog.dismiss();
														Intent charging = new Intent(
																TableListActivity.this,
																ChargingActivity.class);
														startActivity(charging);
													}
												});

							} else {
								alert("Có lỗi xảy ra");
							}
						}
					});
				}
			}
		}
	};

	protected void showNetworkError() {
		super.showNetworkError();
		networkDialog.setPositiveOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showLoading();
				networkDialog.dismiss();
				GameSocket.shareSocket().reconnect();
				try {
					BusinessRequester.getInstance().reconnect(3, false);
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	;
}
