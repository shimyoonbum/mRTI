package com.pulmuone.mrtina.popupPall;

import java.io.Serializable;

public class PopPallItem implements Serializable {
	public String  PALLET_ID,
			PALLET_NUMBER,
			ITEM_CODE,
			SUBINVENTORY_CODE,
			INVENTORY_LOCATION_ID,
			LOCATOR_CODE;
	public PopPallItem(String a0, String a1, String a2, String a3, String a4, String a5) {
		PALLET_ID = a0;
		PALLET_NUMBER = a1;
		ITEM_CODE = a2;
		SUBINVENTORY_CODE = a3;
		INVENTORY_LOCATION_ID = a4;
		LOCATOR_CODE = a5;
	}
}