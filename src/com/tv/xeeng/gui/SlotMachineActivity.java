package com.tv.xeeng.gui;

import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gui.customview.BasicDialog;
import com.tv.xeeng.gui.customview.EventRewardDialog;
import com.tv.xeeng.gui.customview.GiftCodeDialog;
import com.tv.xeeng.gui.fragments.EventDetailsFragment;
import com.tv.xeeng.gui.itemdata.MonthlyEventItemData;
import com.tv.xeeng.manager.UserPreference;
import com.tv.xeeng.R;

public class SlotMachineActivity extends BaseLayoutXeengActivity {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final long LED_INTERVAL = 1000;
	private static final int SLOT_INTERVAL = 60;
	private static final int MAX_ROUND = 3;

	public static final String KEY_REWARD = "result";
	public static final String KEY_IS_CONTINUE = "is_continue";
	public static final String KEY_IS_GIFT = "is_gift";

	public static final String INTENT_SLOT_MACHINE_STOP_SPINNING = "intent_slot_machine_stop_spinning";
	// ===========================================================
	// Fields
	// ===========================================================
	private ImageView imvLEDs;

	private Handler handler;
	private Runnable ledOnRunnable;
	private Runnable ledOffRunnable;
	private Runnable demoSlotRunnable;

	// private EventDetailsParticipateSlotMachineFragment controlFragment;

	private int[] slotImvIds = { R.id.imv_machine_slot_1,
			R.id.imv_machine_slot_2, R.id.imv_machine_slot_3,
			R.id.imv_machine_slot_4, R.id.imv_machine_slot_5,
			R.id.imv_machine_slot_6, R.id.imv_machine_slot_7,
			R.id.imv_machine_slot_8, R.id.imv_machine_slot_9,
			R.id.imv_machine_slot_10, R.id.imv_machine_slot_11,
			R.id.imv_machine_slot_12 };
	private int[] rewardImvIds = { R.id.imv_machine_slot_reward_1,
			R.id.imv_machine_slot_reward_2, R.id.imv_machine_slot_reward_3 };

	private int startSlot;
	private int currentSlot;
	private ImageView[] imvSlots;

	private TextView tvMultiplier;
	private ImageView[] imvResults;

	private Map<String, int[]> rewardToSlotMap;

	private int slowDownSteps;

	private boolean shouldClearResults;
	private boolean isSpinning;

	private String previousReward;

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean status = intent.getBooleanExtra(
					NetworkUtils.MESSAGE_STATUS, false);
			if (status) {
				if (intent.getAction().equalsIgnoreCase(
						MessageService.INTENT_GET_ROULETTE_GAME_INFO)) {
					String[] tokens = NetworkUtils.stringSplit(
							intent.getStringExtra(NetworkUtils.MESSAGE_INFO),
							NetworkUtils.DIFF_ARRAY_SEPERATOR);

					if (tokens.length > 1) {
						// Previous result...
						String results[] = NetworkUtils.stringSplit(tokens[1],
								NetworkUtils.ELEMENT_SEPERATOR);
						String reward = results[0];
						boolean isContinue = "1".equalsIgnoreCase(results[1]);
						boolean isGift = "1".equalsIgnoreCase(results[2]);
						String price = results[3];

						processResult(reward, isContinue, isGift, price, false);
					}
				} else if (intent.getAction().equalsIgnoreCase(
						MessageService.INTENT_START_ROULLETE)) {
					String results[] = NetworkUtils.stringSplit(
							intent.getStringExtra(NetworkUtils.MESSAGE_INFO),
							NetworkUtils.ELEMENT_SEPERATOR);
					String reward = results[0];
					boolean isContinue = "1".equalsIgnoreCase(results[1]);
					boolean isGift = "1".equalsIgnoreCase(results[2]);
					String price = results[3];

					processResult(reward, isContinue, isGift, price, true);
				} else if (intent.getAction().equalsIgnoreCase(
						MessageService.INTENT_END_ROULLETE)) {
					EventRewardDialog dialog = new EventRewardDialog(
							SlotMachineActivity.this,
							intent.getStringExtra(NetworkUtils.MESSAGE_INFO));
					dialog.show();

					initialize();

					BusinessRequester.getInstance().getUserInfo(
							GameData.shareData().getMyself().id);

					playSound(R.raw.nhan_thuong);
				}
			} else {
				alert(intent.getStringExtra(NetworkUtils.MESSAGE_INFO));
			}
		}
	};

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public boolean isSpinning() {
		return isSpinning;
	}

	public String getPrevReward() {
		return previousReward;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		isSpinning = false;
		previousReward = "";

		LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
				new IntentFilter(MessageService.INTENT_GET_ROULETTE_GAME_INFO));
		initialize();
		BusinessRequester.getInstance().getRouletteGameInfo();

		super.onActivityResult(arg0, arg1, arg2);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_slot_machine);

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment eventDetailsFragment = EventDetailsFragment
				.newInstance(new MonthlyEventItemData(-1, "Slot machine",
						"Content", "Description", null,
						EventDetailsFragment.EVENT_SLOT_MACHINE));
		ft.replace(R.id.fl_event_details, eventDetailsFragment);
		ft.commit();

		imvLEDs = (ImageView) findViewById(R.id.imv_leds);

		handler = new Handler();
		ledOnRunnable = new Runnable() {

			@Override
			public void run() {
				imvLEDs.setImageResource(R.drawable.led_slot_leds2);
				handler.postDelayed(this, LED_INTERVAL * 2);
			}
		};

		ledOffRunnable = new Runnable() {
			@Override
			public void run() {
				imvLEDs.setImageResource(R.drawable.led_slot_leds1);
				handler.postDelayed(this, LED_INTERVAL * 2);
			}
		};

		isVisibleBtnHelp = true;

		imvSlots = new ImageView[12];

		for (int i = 0; i < 12; i++) {
			imvSlots[i] = (ImageView) findViewById(slotImvIds[i]);
			imvSlots[i].setColorFilter(null);
			Drawable d = imvSlots[i].getBackground().mutate();
			imvSlots[i].setBackgroundDrawable(d);
		}

		demoSlotRunnable = new Runnable() {

			@Override
			public void run() {
				imvSlots[currentSlot % 12].setColorFilter(getResources()
						.getColor(R.color.black_overlay), Mode.SRC_ATOP);
				imvSlots[currentSlot % 12].getBackground().setColorFilter(
						getResources().getColor(R.color.black_overlay),
						Mode.SRC_ATOP);
				if (currentSlot++ == 12) {
					handler.removeCallbacks(this);
				}
				handler.postDelayed(this, 100);
			}
		};

		imvResults = new ImageView[3];
		for (int i = 0; i < 3; i++) {
			imvResults[i] = (ImageView) findViewById(rewardImvIds[i]);
		}

		tvMultiplier = (TextView) findViewById(R.id.tv_slot_machine_multiplier);

		rewardToSlotMap = new TreeMap<String, int[]>(new Comparator<String>() {

			@Override
			public int compare(String lhs, String rhs) {
				return lhs.compareToIgnoreCase(rhs);
			}
		});
		rewardToSlotMap.put("c1", new int[] { 0, 6 });
		rewardToSlotMap.put("c2", new int[] { 8 });
		rewardToSlotMap.put("c3", new int[] { 2 });
		rewardToSlotMap.put("r1", new int[] { 5, 11 });
		rewardToSlotMap.put("r2", new int[] { 4, 10 });
		rewardToSlotMap.put("r3", new int[] { 1, 7 });
		rewardToSlotMap.put("x10", new int[] { 9 });
		rewardToSlotMap.put("x50", new int[] { 3 });

		initialize();
		initBottomBar();
		initTopBar();

		btnHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SlotMachineActivity.this,
						InfoActivity.class);
				intent.putExtra(InfoActivity.KEY_SELECTED_TAB,
						InfoActivity.TAB_HELPS);
				intent.putExtra(InfoActivity.KEY_SELECTED_CELL,
						InfoActivity.GAME_SLOT_MACHINE);

				startActivity(intent);
			}
		});

		Button btnCharging = (Button) findViewById(R.id.btn_charging);
//		btnCharging.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				Intent intent = new Intent(SlotMachineActivity.this,
//						ChargingActivity.class);
//				startActivity(intent);
//			}
//		});

		Button btnGift = (Button) findViewById(R.id.btn_gift);
		btnGift.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final BasicDialog dialog = new GiftCodeDialog(
						SlotMachineActivity.this);
				dialog.show();
			}
		});

		Button btnShopping = (Button) findViewById(R.id.btn_shopping);
		btnShopping.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SlotMachineActivity.this,
						StoreActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
				new IntentFilter(MessageService.INTENT_GET_ROULETTE_GAME_INFO));
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
				new IntentFilter(MessageService.INTENT_START_ROULLETE));
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
				new IntentFilter(MessageService.INTENT_END_ROULLETE));

		pauseBackgroundMusic();
	};

	@Override
	protected void onPause() {
		super.onPause();

		handler.removeCallbacksAndMessages(null);

		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivityForResult(intent, 0);
	}

	// ===========================================================
	// Methods
	// ===========================================================
	private void processResult(final String reward, final boolean isContinue,
			final boolean isGift, final String price, boolean shouldStopSpinning) {
		Random randomizer = new Random();

		final String[] codes = NetworkUtils.stringSplit(reward, ";");
		final int[] slotIndexes = new int[codes.length];

		for (int i = 0; i < codes.length; i++) {
			int[] possibleSlots = rewardToSlotMap.get(codes[i]);
			slotIndexes[i] = possibleSlots[randomizer
					.nextInt(possibleSlots.length)];
		}

		Runnable onRoulleteStop = new Runnable() {
			@Override
			public void run() {
				tvMultiplier.setText("x" + price);

				int i = 0;
				for (i = 0; i < slotIndexes.length; i++) {
					Drawable d = imvSlots[slotIndexes[i]].getDrawable()
							.getConstantState().newDrawable().mutate();
					d.clearColorFilter();
					imvResults[i].setImageDrawable(d);
					imvResults[i]
							.setBackgroundResource(R.drawable.item_slot_bg_small);
				}
				for (; i < 3; i++) {
					imvResults[i].setImageDrawable(null);
					imvResults[i]
							.setBackgroundResource(R.drawable.item_slot_bg_small_empty);
				}

				shouldClearResults = !isContinue;

				Intent intent = new Intent(INTENT_SLOT_MACHINE_STOP_SPINNING);
				intent.putExtra(NetworkUtils.MESSAGE_STATUS, true);
				intent.putExtra(KEY_IS_CONTINUE, isContinue);
				intent.putExtra(KEY_IS_GIFT, isGift);
				intent.putExtra(KEY_REWARD, reward);

				previousReward = reward;

				LocalBroadcastManager.getInstance(SlotMachineActivity.this)
						.sendBroadcast(intent);
			}
		};

		if (shouldStopSpinning) {
			playSpinningEffect(slotIndexes[slotIndexes.length - 1],
					onRoulleteStop);
		} else if (!isSpinning) {
			onRoulleteStop.run();
		}
	}

	private void initialize() {
		handler.removeCallbacks(ledOnRunnable);
		handler.removeCallbacks(ledOffRunnable);

		ledOnRunnable.run();
		handler.postDelayed(ledOffRunnable, LED_INTERVAL);

		for (ImageView imv : imvSlots) {
			imv.setColorFilter(null);
			imv.getBackground().setColorFilter(null);
		}

		handler.postDelayed(demoSlotRunnable, 2000);

		tvMultiplier.setText("");
		for (ImageView imv : imvResults) {
			imv.setImageDrawable(null);
			imv.setBackgroundResource(R.drawable.item_slot_bg_small_empty);
		}
	}

	private void playSpinningEffect(final int position,
			final Runnable onRoulletteStop) {
		// handler.removeCallbacks(slotRunnable);
		handler.removeCallbacks(demoSlotRunnable);

		slowDownSteps = new Random().nextInt(4) + 8;
		final int slowDownInterval = new Random().nextInt(3) * 10 + 50;

		final int totalSteps = slowDownSteps;
		final int startToSlowPosition = (position + 12 - (slowDownSteps % 12)) % 12;

		final Runnable spinningEffectRunnable = new Runnable() {
			@Override
			public void run() {
				if (slowDownSteps != 0) {
					playSound(R.raw.quay);
				}

				int round = (currentSlot - startSlot) / 12;
				int interval = SLOT_INTERVAL;

				interval += round * 20;

				if (round > (MAX_ROUND - 1)) {
					// Should slowing down
					if (slowDownSteps == 0) {
						// This is the end of the journey
						isSpinning = false;
						playSound(R.raw.ket_qua);
						onRoulletteStop.run();
						return;
					}
					if ((currentSlot % 12) == startToSlowPosition
							|| slowDownSteps < totalSteps) {
						interval += (totalSteps - slowDownSteps--)
								* slowDownInterval;
					}
				}

				imvSlots[currentSlot % 12].setColorFilter(getResources()
						.getColor(R.color.black_overlay), Mode.SRC_ATOP);
				imvSlots[currentSlot % 12].getBackground().setColorFilter(
						getResources().getColor(R.color.black_overlay),
						Mode.SRC_ATOP);

				imvSlots[(currentSlot + 1) % 12].setColorFilter(null);
				imvSlots[(currentSlot + 1) % 12].getBackground()
						.setColorFilter(null);

				currentSlot = currentSlot + 1;
				handler.postDelayed(this, interval);
			}
		};

		for (ImageView imv : imvSlots) {
			imv.setColorFilter(getResources().getColor(R.color.black_overlay));
			imv.getBackground().setColorFilter(
					getResources().getColor(R.color.black_overlay),
					Mode.SRC_ATOP);
		}

		if (shouldClearResults) {
			for (ImageView imv : imvResults) {
				imv.setImageDrawable(null);
				imv.setBackgroundResource(R.drawable.item_slot_bg_small_empty);
			}
		}

		currentSlot = (currentSlot + 11) % 12;
		startSlot = currentSlot;

		isSpinning = true;

		handler.removeCallbacks(spinningEffectRunnable);
		spinningEffectRunnable.run();
	}

	/**
	 * Create a temporary player to play a sound effect.
	 * 
	 * @param resId
	 */
	private void playSound(int resId) {
		if (UserPreference.sharePreference().isSoundEffectOn()) {
			MediaPlayer player = MediaPlayer.create(SlotMachineActivity.this,
					resId);
			player.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer player) {
					player.reset();
					player.release();
				}
			});
			player.start();
		}
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
