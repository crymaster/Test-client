package com.tv.xeeng.gui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.LayoutParams;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.tv.xeeng.CommonUtils;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.gui.SlotMachineActivity;
import com.tv.xeeng.R;

public class EventDetailsParticipateSlotMachineFragment extends Fragment {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String BET_TYPE_XENG = "Xeng";
	private static final String BET_TYPE_GOLD = "Gold";
	// ===========================================================
	// Fields
	// ===========================================================
	private Button btnStart;
	private Button btnEnd;

	private RadioGroup rgXeengBets;
	private RadioGroup rgGoldBets;

	private List<String> xeengBets;
	private List<String> goldBets;

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
					String[] betStr = NetworkUtils.stringSplit(tokens[0],
							NetworkUtils.ARRAY_SEPERATOR);

					boolean isContinue = false;
					boolean isGift = false;
					String fee = "";
					String feeType = "";
					
					if (tokens.length > 1) {
						// Previous result...
						String results[] = NetworkUtils.stringSplit(tokens[1],
								NetworkUtils.ELEMENT_SEPERATOR);
						isContinue = "1".equalsIgnoreCase(results[1]);
						isGift = "1".equalsIgnoreCase(results[2]);
						
						fee = results[4];
						feeType = results[5];
					}
					
					generateBets(betStr, fee, feeType);

					processResults(isContinue, isGift);
				} else if (intent.getAction().equalsIgnoreCase(
						MessageService.INTENT_END_ROULLETE)) {
					btnStart.setText("Bắt đầu");
					btnEnd.setVisibility(View.INVISIBLE);
					
					setBetsEnable(true);
				} else if (intent.getAction().equalsIgnoreCase(
						MessageService.INTENT_START_ROULLETE)) {
					btnStart.setVisibility(View.INVISIBLE);
					btnEnd.setVisibility(View.INVISIBLE);
					
					setBetsEnable(false);
				} else if (intent.getAction().equalsIgnoreCase(
						SlotMachineActivity.INTENT_SLOT_MACHINE_STOP_SPINNING)) {
					boolean isContinue = intent.getBooleanExtra(SlotMachineActivity.KEY_IS_CONTINUE,
							false);
					boolean isGift = intent.getBooleanExtra(SlotMachineActivity.KEY_IS_GIFT, false);

					processResults(isContinue, isGift);
				}
			}
		}
	};

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.fragment_event_details_participate_slot_machine,
				container, false);

		xeengBets = new ArrayList<String>();
		goldBets = new ArrayList<String>();

		rgXeengBets = (RadioGroup) view.findViewById(R.id.rg_xeeng_bets);
		rgGoldBets = (RadioGroup) view.findViewById(R.id.rg_gold_bets);

		OnCheckedChangeListener rgListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Log.d("EventDetailsParticipateSlotMachine",
						"OnCheckedChanged in RadioGroup called: id = "
								+ group.getId() + ", checkedId = " + checkedId);
				if (group.getId() == rgXeengBets.getId()) {
					rgGoldBets.setOnCheckedChangeListener(null);
					rgGoldBets.clearCheck();
					rgGoldBets.setOnCheckedChangeListener(this);
				} else if (group.getId() == rgGoldBets.getId()) {
					rgXeengBets.setOnCheckedChangeListener(null);
					rgXeengBets.clearCheck();
					rgXeengBets.setOnCheckedChangeListener(this);
				}
			}
		};

		rgXeengBets.setOnCheckedChangeListener(rgListener);
		rgGoldBets.setOnCheckedChangeListener(rgListener);

		btnStart = (Button) view.findViewById(R.id.btn_start_roullete);
		btnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				int id = rgXeengBets.getCheckedRadioButtonId();
				String type = BET_TYPE_XENG;
				String value = "";

				if (id == -1) {
					id = rgGoldBets.getCheckedRadioButtonId();
					type = BET_TYPE_GOLD;
					value = goldBets.get(id);
				} else {
					value = xeengBets.get(id);
				}

				BusinessRequester.getInstance().startRoullete(value, type);

				BusinessRequester.getInstance().getUserInfo(
						GameData.shareData().getMyself().id);

				v.setEnabled(false);

				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								v.setEnabled(true);
							}
						}, 3000);
					}
				});
			}
		});
		btnEnd = (Button) view.findViewById(R.id.btn_end_roullete);
		btnEnd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				BusinessRequester.getInstance().endRoullete();
				v.setEnabled(false);

				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								v.setEnabled(true);
							}
						}, 3000);
					}
				});
			}
		});

		btnStart.setText("Bắt đầu");

		btnStart.setVisibility(View.INVISIBLE);
		btnEnd.setVisibility(View.INVISIBLE);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				receiver,
				new IntentFilter(MessageService.INTENT_GET_ROULETTE_GAME_INFO));
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				receiver, new IntentFilter(MessageService.INTENT_END_ROULLETE));
		LocalBroadcastManager.getInstance(getActivity())
				.registerReceiver(receiver,
						new IntentFilter(MessageService.INTENT_START_ROULLETE));
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				receiver,
				new IntentFilter(
						SlotMachineActivity.INTENT_SLOT_MACHINE_STOP_SPINNING));

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				BusinessRequester.getInstance().getRouletteGameInfo();
			}
		}, 400);
	}

	@Override
	public void onPause() {
		super.onPause();

		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
				receiver);
	}

	// ===========================================================
	// Methods
	// ===========================================================
	public void processResults(boolean isContinue, boolean isGift) {
		Activity act = getActivity();
		if (act != null && act instanceof SlotMachineActivity) {
			if (((SlotMachineActivity) act).isSpinning()) {
				setBetsEnable(false);
				return;
			}
		}
		btnStart.setVisibility(View.VISIBLE);
		if (isContinue) {
			btnStart.setText("Quay tiếp");
		} else {
			btnStart.setText("Bắt đầu");
		}
		
		setBetsEnable(!isContinue);

		if (isGift) {
			btnEnd.setVisibility(View.VISIBLE);
		} else {
			btnEnd.setVisibility(View.INVISIBLE);
		}

	}

	private void generateBets(String[] bets, String fee, String feeType) {
		goldBets.clear();
		xeengBets.clear();

		for (String bet : bets) {
			String[] betInfo = NetworkUtils.stringSplit(bet,
					NetworkUtils.ELEMENT_SEPERATOR);
			if (betInfo[1].equalsIgnoreCase("Gold")) {
				goldBets.add(betInfo[0]);
			} else {
				xeengBets.add(betInfo[0]);
			}
		}

		initRadioGroup(rgXeengBets, xeengBets,
				R.drawable.icon_coin_xeeng_padding);
		initRadioGroup(rgGoldBets, goldBets, R.drawable.icon_coin_gold_padding);
		
		rgXeengBets.check(-1);
		rgGoldBets.check(-1);
		
		if (feeType.equalsIgnoreCase(BET_TYPE_GOLD)) {
			rgGoldBets.check(goldBets.indexOf(fee));
		} else if (feeType.equalsIgnoreCase(BET_TYPE_XENG)) {
			rgXeengBets.check(xeengBets.indexOf(fee));
		} else {
			rgGoldBets.check(0);
		}
				
	}

	private void initRadioGroup(RadioGroup radioGroup, List<String> infos,
			int resId) {
		radioGroup.removeAllViews();
		LayoutInflater inflater = LayoutInflater.from(radioGroup.getContext());
		for (int i = 0; i < infos.size(); i++) {
			RadioButton rb = (RadioButton) inflater.inflate(
					R.layout.radio_button_slot_machine_bet, radioGroup, false);

			SpannableString ss = new SpannableString(
					CommonUtils.formatGold(Long.parseLong(infos.get(i))) + "i");

			Drawable d = radioGroup.getContext().getResources()
					.getDrawable(resId).mutate();
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			d.setColorFilter(R.color.black_overlay, Mode.SRC_ATOP);

			final ImageSpan is = new ImageSpan(d);
			ss.setSpan(is, ss.toString().indexOf("i"), ss.toString().length(),
					SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);

			rb.setId(i);
			rb.setGravity(Gravity.CENTER);
			rb.setText(ss);

			rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					Log.d("EventDetailsParticipateSlotMachine",
							"OnCheckedChanged called: id = "
									+ buttonView.getId() + ", isChecked = "
									+ isChecked);
					if (!isChecked) {
						is.getDrawable().setColorFilter(R.color.black_overlay,
								Mode.SRC_ATOP);
						buttonView.setTextColor(Color.rgb(64, 64, 62));
					} else {
						is.getDrawable().clearColorFilter();
						buttonView.setTextColor(Color.WHITE);
					}
				}
			});

			radioGroup.addView(rb, new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

			if (i != infos.size() - 1) {
				View v = new View(radioGroup.getContext());
				LayoutParams params = new LayoutParams(0, 1);
				params.weight = 1;
				radioGroup.addView(v, params);
			}
		}
	}
	
	private void setBetsEnable(boolean isEnable) {
		for (int i = 0; i < rgGoldBets.getChildCount(); i++) {
			rgGoldBets.getChildAt(i).setEnabled(isEnable);
		}
		
		for (int i = 0; i < rgXeengBets.getChildCount(); i++) {
			rgXeengBets.getChildAt(i).setEnabled(isEnable);
		}
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
