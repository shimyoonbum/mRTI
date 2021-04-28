package com.pulmuone.mrtina.popupLoca;

import java.io.Serializable;

public class PopLocaItem implements Serializable {
	public String  INVENTORY_LOCATION_ID,
			LOCATOR_CODE,
			LOCATOR_NAME;
	public PopLocaItem(String a0, String a1, String a2){
		INVENTORY_LOCATION_ID = a0;
		LOCATOR_CODE = a1;
		LOCATOR_NAME = a2;
	}
}