package com.pulmuone.mrtina.popupTranItem;

import java.io.Serializable;

public class PopTranItemItem implements Serializable {

	public String  INVENTORY_ITEM_ID,
			ITEM_CODE,
			ITEM_DESC,
			PRIMARY_UOM_CODE,
			LOT_CONTROL_CODE,
			OU_ID,
			ORG_ID,
			ORGANIZATION_CODE;

	public PopTranItemItem(String INVENTORY_ITEM_ID, String ITEM_CODE, String ITEM_DESC, String PRIMARY_UOM_CODE
							, String LOT_CONTROL_CODE, String OU_ID, String ORG_ID, String ORGANIZATION_CODE) {
		this.INVENTORY_ITEM_ID = INVENTORY_ITEM_ID;
		this.ITEM_CODE = ITEM_CODE;
		this.ITEM_DESC = ITEM_DESC;
		this.PRIMARY_UOM_CODE = PRIMARY_UOM_CODE;
		this.LOT_CONTROL_CODE = LOT_CONTROL_CODE;
		this.OU_ID = OU_ID;
		this.ORG_ID = ORG_ID;
		this.ORGANIZATION_CODE = ORGANIZATION_CODE;
	}
}