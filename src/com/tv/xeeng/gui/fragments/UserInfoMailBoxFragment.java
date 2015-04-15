package com.tv.xeeng.gui.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.dataLayer.MessageService;
import com.tv.xeeng.dataLayer.utils.NetworkUtils;
import com.tv.xeeng.gui.itemdata.MailItemData;
import com.tv.xeeng.R;

public class UserInfoMailBoxFragment extends Fragment {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String KEY_DATA = "data";
	// ===========================================================
	// Fields
	// ===========================================================
	private ListView mailList;
	private MailListAdapter mailListAdapter;

	private ArrayList<MailItemData> mailListData;
	private MailItemData currentMail;

	private TextView content;

	private LocalBroadcastManager broadcastManager;
	private BroadcastReceiver receiver;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public static UserInfoMailBoxFragment newInstance(
			ArrayList<MailItemData> mailData) {
		UserInfoMailBoxFragment fragment = new UserInfoMailBoxFragment();

		Bundle args = new Bundle();
		args.putParcelableArrayList(KEY_DATA, mailData);

		fragment.setArguments(args);

		return fragment;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_user_info_mail, null);

		mailList = (ListView) view.findViewById(R.id.lv_mail);

		mailListData = getArguments().getParcelableArrayList(KEY_DATA);
		mailListAdapter = new MailListAdapter(mailListData);

		content = (TextView) view.findViewById(R.id.tv_mail_content);

		mailList.setAdapter(mailListAdapter);
		mailList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long itemId) {
				currentMail = mailListData.get(position);
				showMailContent();
			}

		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		broadcastManager.registerReceiver(receiver, new IntentFilter(
				MessageService.INTENT_GET_PRIVATE_MESSAGE));
	}

	@Override
	public void onPause() {
		super.onPause();
		broadcastManager.unregisterReceiver(receiver);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent intent) {
				boolean status = intent.getBooleanExtra(
						NetworkUtils.MESSAGE_STATUS, false);
				if (MessageService.INTENT_GET_PRIVATE_MESSAGE
						.equalsIgnoreCase(intent.getAction())) {
					if (status) {
						MailItemData mail = intent
								.getParcelableExtra(NetworkUtils.MESSAGE_INFO);

						for (MailItemData itemData : mailListData) {
							if (itemData.getId() == mail.getId()) {
								itemData.setContent(mail.getContent());
								showMailContent();
							}
						}
					}
				}
			}
		};

		broadcastManager = LocalBroadcastManager.getInstance(getActivity());
	}

	// ===========================================================
	// Methods
	// ===========================================================
	public void update(ArrayList<MailItemData> mailListData) {
		this.mailListData = mailListData;
		mailListAdapter = new MailListAdapter(mailListData);
		mailList.setAdapter(mailListAdapter);
	}

	private void showMailContent() {
		content.setText("Loading...");
		if (currentMail == null) {
			return;
		}
		if (TextUtils.isEmpty(currentMail.getContent())) {
			BusinessRequester.getInstance().getPrivateMessage(
					currentMail.getId());
			return;
		}
		content.setText(currentMail.getContent());
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private static class MailListAdapter extends BaseAdapter {
		private ArrayList<MailItemData> data;
		private SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");

		public MailListAdapter(ArrayList<MailItemData> data) {
			this.data = data;
		}

		public static class MailListItemViewHolder {
			ImageView avatar;
			TextView content;
			TextView dateSent;
			ImageButton btnDelete;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return this.data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return data.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.fragment_user_info_mail_item, null);

				MailListItemViewHolder holder = new MailListItemViewHolder();
				holder.avatar = (ImageView) convertView
						.findViewById(R.id.iv_mail_item_avatar);
				holder.content = (TextView) convertView
						.findViewById(R.id.tv_event_description);
				holder.dateSent = (TextView) convertView
						.findViewById(R.id.tv_date_sent);
				holder.btnDelete = (ImageButton) convertView
						.findViewById(R.id.ib_mail_item_delete);

				convertView.setTag(holder);
			}

			MailItemData mail = data.get(position);
			if (mail != null) {
				MailListItemViewHolder holder = (MailListItemViewHolder) convertView
						.getTag();

				holder.content.setText(mail.getTitle());
				holder.dateSent.setText(formatter.format(new Date(mail
						.getDateSent())));
			}

			return convertView;
		}

	}
}
