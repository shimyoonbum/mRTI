package com.pulmuone.mrtina.popupItem;

import java.io.Serializable;

public class PopItemItem implements Serializable {

	public String  INVENTORY_ITEM_ID,
			ITEM_CODE,
			ITEM_DESC,
			PRIMARY_UOM_CODE;

	public PopItemItem(String INVENTORY_ITEM_ID, String ITEM_CODE, String ITEM_DESC, String PRIMARY_UOM_CODE) {
		this.INVENTORY_ITEM_ID = INVENTORY_ITEM_ID;
		this.ITEM_CODE = ITEM_CODE;
		this.ITEM_DESC = ITEM_DESC;
		this.PRIMARY_UOM_CODE = PRIMARY_UOM_CODE;
	}
}