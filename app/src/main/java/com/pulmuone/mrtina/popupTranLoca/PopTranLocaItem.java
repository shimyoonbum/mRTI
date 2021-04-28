package com.pulmuone.mrtina.popupTranLoca;

import java.io.Serializable;

public class PopTranLocaItem implements Serializable {
	public String  INVENTORY_LOCATION_ID,
			LOCATOR_CODE,
			LOCATOR_NAME;
	public PopTranLocaItem(String a0, String a1, String a2){
		INVENTORY_LOCATION_ID = a0;
		LOCATOR_CODE = a1;
		LOCATOR_NAME = a2;
	}
}