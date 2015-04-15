package com.tv.xeeng.gui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.tv.xeeng.gamedata.GameData;
import com.tv.xeeng.R;

public class UserInfoVIPFragment extends Fragment {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int[] RADIO_BUTTON_IDS = new int[] { R.id.rb_vip_01,
			R.id.rb_vip_01, R.id.rb_vip_02, R.id.rb_vip_03, R.id.rb_vip_04,
			R.id.rb_vip_05, R.id.rb_vip_06, R.id.rb_vip_07, R.id.rb_vip_08,
			R.id.rb_vip_09, R.id.rb_vip_10 };
	private static final String[] CONTENT_TITLES = new String[] { "" };
	// ===========================================================
	// Fields
	// ===========================================================
	private RadioGroup rgVIPLevels;

	private TextView tvVIPContentTitle;
	private TextView tvVIPRequirementTitle;
	private TextView tvVIPRequirement;
	private TextView tvVIPReward;

	private View contentView;
	private View requirementView;
	private View rewardView;

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
		View view = inflater.inflate(R.layout.fragment_user_info_vip,
				container, false);

		rgVIPLevels = (RadioGroup) view.findViewById(R.id.rg_vip_levels);
		rgVIPLevels.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
			}
		});
		rgVIPLevels.check(-1);
		rgVIPLevels
				.check(RADIO_BUTTON_IDS[GameData.shareData().getMyself().vipId]);

		final FragmentManager fm = getFragmentManager();

		contentView = view.findViewById(R.id.ll_content);
		contentView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("UserInfoVIPFragment", "contentView clicked()");
				DialogFragment fragment = UserInfoVIPDetailsDialogFragment.newInstance(
								"Nội dung", "Nội dung blah blah blah");				
				fragment.show(fm, fragment.getClass().getCanonicalName());
			}
		});
		requirementView = view.findViewById(R.id.ll_requirement);
		requirementView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fm.beginTransaction().show(
						UserInfoVIPDetailsDialogFragment.newInstance(
								"Điều kiện", "Điều kiện blah blah blah")).commit();
			}
		});
		rewardView = view.findViewById(R.id.ll_reward);
		rewardView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fm.beginTransaction().show(
						UserInfoVIPDetailsDialogFragment.newInstance(
								"Quyền lợi", "Quyền lợi blah blah blah")).commit();
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
