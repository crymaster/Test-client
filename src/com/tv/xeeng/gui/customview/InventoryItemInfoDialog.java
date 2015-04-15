package com.tv.xeeng.gui.customview;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.tv.xeeng.dataLayer.BusinessRequester;
import com.tv.xeeng.gui.itemdata.InventoryItemData;
import com.tv.xeeng.R;

public class InventoryItemInfoDialog extends BasicDialog {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private InventoryItem inventoryItem;
	private TextView tvItemName;
	private TextView tvItemDescription;

	protected Button btnUseItem;
	protected Button btnSendGift;
	private View emptyView;

	private InventoryItemData itemData;
	private InventoryItemInfoDialogListener listener;

	// ===========================================================
	// Constructors
	// ===========================================================
	public InventoryItemInfoDialog(Context context, InventoryItemData itemData) {
		this(context, itemData, null);
	}

	public InventoryItemInfoDialog(Context context, InventoryItemData itemData,
			InventoryItemInfoDialogListener listener) {
		super(context, "Thông tin vật phẩm", null, null, null);
		this.itemData = itemData;
		this.listener = listener;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_inventory_item_info);

		inventoryItem = (InventoryItem) findViewById(R.id.inventory_item);
		inventoryItem.setResId(itemData.getResId());

		tvItemName = (TextView) findViewById(R.id.tv_inventory_item_name);
		tvItemName.setText(itemData.getName());

		tvItemDescription = (TextView) findViewById(R.id.tv_inventory_item_description);
		tvItemDescription.setText(itemData.getDescription());

		btnUseItem = (Button) findViewById(R.id.btn_use_item);
		btnUseItem.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO
				InventoryItemInfoDialog.this.dismiss();
				// Toast.makeText(getContext(), "Chưa dùng được đâu...",
				// Toast.LENGTH_SHORT).show();

				// EventRewardDialog dialog = new
				// EventRewardDialog(getContext(),
				// 1000000);
				// dialog.show();
				BusinessRequester.getInstance().useInventoryItem(
						itemData.getItemCode());
				if (listener != null) {
					listener.btnUseItemOnClick(v);
				}
			}
		});

		btnSendGift = (Button) findViewById(R.id.btn_send_gift);
		btnSendGift.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO
				InventoryItemInfoDialog.this.dismiss();
				Toast.makeText(getContext(), R.string.text_developing,
						Toast.LENGTH_SHORT).show();
				if (listener != null) {
					listener.btnSendGiftOnClick(v);
				}
			}
		});

		emptyView = findViewById(R.id.empty_view);

		txtTitle = (TextView) findViewById(R.id.txt_title);
		btnClose = (Button) findViewById(R.id.btn_close);

		setCancelable(true);
	}

	@Override
	public void show() {
		super.show();

		if (itemData.isUsable()) {
			btnUseItem.setVisibility(View.VISIBLE);
			emptyView.setVisibility(View.INVISIBLE);
		} else {
			btnUseItem.setVisibility(View.GONE);
			emptyView.setVisibility(View.GONE);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public interface InventoryItemInfoDialogListener {
		public void btnUseItemOnClick(View v);

		public void btnSendGiftOnClick(View v);
	}
}
