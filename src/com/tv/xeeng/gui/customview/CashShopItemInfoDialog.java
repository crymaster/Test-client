package com.tv.xeeng.gui.customview;

import android.content.Context;
import android.view.View;
import com.tv.xeeng.gui.itemdata.CashShopItemData;
import com.tv.xeeng.gui.itemdata.InventoryItemData;

public class CashShopItemInfoDialog extends InventoryItemInfoDialog {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================
	public CashShopItemInfoDialog(Context context, CashShopItemData itemData,
			InventoryItemInfoDialogListener listener) {
		super(context, new InventoryItemData(itemData.getCode(),
				itemData.getName(), itemData.getDescription(), 0, false),
				listener);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public void show() {
		super.show();

		btnSendGift.setVisibility(View.INVISIBLE);
		btnUseItem.setVisibility(View.INVISIBLE);
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
