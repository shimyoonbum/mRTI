package com.pulmuone.mrtina.popupProdSubInv;

import java.io.Serializable;

public class PopProdSubInvItem implements Serializable {
	public String  SUBINVENTORY_CODE,
					SUBINVENTORY_NAME,
					LOCATOR_CONTROL;

	public PopProdSubInvItem(String SUBINVENTORY_CODE, String SUBINVENTORY_NAME, String LOCATOR_CONTROL) {
		this.SUBINVENTORY_CODE = SUBINVENTORY_CODE;
		this.SUBINVENTORY_NAME = SUBINVENTORY_NAME;
		this.LOCATOR_CONTROL = LOCATOR_CONTROL;
	}
}