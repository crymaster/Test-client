package com.tv.xeeng.gui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.tv.xeeng.gui.itemdata.MonthlyEventItemData;
import com.tv.xeeng.R;

public class EventDetailsFragment extends Fragment {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String TAG_EVENT_DATA = "event_data";
	public static final String EVENT_SLOT_MACHINE = "event_slot_machine";

	// ===========================================================
	// Fields
	// ===========================================================
	private RadioGroup rgEventTabs;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public static EventDetailsFragment newInstance(MonthlyEventItemData data) {
		EventDetailsFragment fragment = new EventDetailsFragment();

		Bundle args = new Bundle();
		args.putParcelable(TAG_EVENT_DATA, data);

		fragment.setArguments(args);

		return fragment;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final MonthlyEventItemData data = getArguments().getParcelable(
				TAG_EVENT_DATA);

		int layoutId = R.layout.fragment_event_details;

		if (data.getType() == EVENT_SLOT_MACHINE) {
			layoutId = R.layout.fragment_event_details_slot_machine;
		}

		View view = inflater.inflate(layoutId, container, false);

		FragmentTransaction ft = getChildFragmentManager().beginTransaction();

		ft.replace(R.id.fl_event_tabbed_detail, getParticipateFragment(data));
		ft.commit();

		rgEventTabs = (RadioGroup) view.findViewById(R.id.rg_event_tabs);
		rgEventTabs.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int id) {
				Fragment fragment = null;
				switch (id) {
				case R.id.rb_event_description:
					Log.d("EventDetailsFragment", "Event description");
					fragment = getContentFragment(data);
					break;
				case R.id.rb_event_participate:
					Log.d("EventDetailsFragment", "Event participation");
					fragment = getParticipateFragment(data);
					break;
				case R.id.rb_event_history:
					Log.d("EventDetailsFragment", "Event history");
					fragment = EventDetailsHistoryFragment.newInstance(data
							.getId());
					break;
				default:
					break;
				}

				if (fragment != null) {
					FragmentTransaction ft = getChildFragmentManager()
							.beginTransaction();
					ft.replace(R.id.fl_event_tabbed_detail, fragment);

					ft.commitAllowingStateLoss();
				}
			}
		});

		return view;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * Get the corresponding ParticipateFragment passed MonthlyEventItemData
	 * 
	 * @param data
	 *            The information needed to initiate Event Participate Fragment.
	 * @return
	 */
	private Fragment getParticipateFragment(MonthlyEventItemData data) {
		Fragment fragment = new EventDetailsParticipateDefaultFrament();
		if (data.getType().equalsIgnoreCase(
				MonthlyEventItemData.EVENT_TYPE_GHEP_CHU)) {
			fragment = EventDetailsParticipateJoinItemsFragment.newInstance(
					data.getId(), data.getComponents(), data.getDescription());
		} else if (data.getType().equalsIgnoreCase(
				MonthlyEventItemData.EVENT_TYPE_LAT_BAI)) {
			fragment = EventDetailsParticipateFlipCardFragment.newInstance(
					data.getId(), data.getComponents(), data.getDescription());
		} else if (data.getType().equalsIgnoreCase(EVENT_SLOT_MACHINE)) {
			fragment = new EventDetailsParticipateSlotMachineFragment();
		}

		return fragment;
	}

	private Fragment getContentFragment(MonthlyEventItemData data) {
		Fragment fragment = EventDetailsContentFragment.newInstance(data.getContent());
		
		if (data.getType().equalsIgnoreCase(EVENT_SLOT_MACHINE)) {
			fragment = new EventDetailsContentSlotMachineFragment();
		}

		return fragment;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static class EventDetailsParticipateDefaultFrament extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater,
				@Nullable ViewGroup container,
				@Nullable Bundle savedInstanceState) {
			View view = inflater.inflate(
					R.layout.fragment_event_details_participate_default,
					container, false);

			return view;
		}
	}
}
