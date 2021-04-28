package com.pulmuone.mrtina.popupTranSubInv;

import java.io.Serializable;

public class PopTranSubInvItem implements Serializable {
	public String  SUBINVENTORY_CODE,
					SUBINVENTORY_NAME,
					LOCATOR_CONTROL,
					STATUS_ID;

	public PopTranSubInvItem(String SUBINVENTORY_CODE, String SUBINVENTORY_NAME, String LOCATOR_CONTROL, String STATUS_ID) {
		this.SUBINVENTORY_CODE = SUBINVENTORY_CODE;
		this.SUBINVENTORY_NAME = SUBINVENTORY_NAME;
		this.LOCATOR_CONTROL = LOCATOR_CONTROL;
		this.STATUS_ID = STATUS_ID;
	}
}