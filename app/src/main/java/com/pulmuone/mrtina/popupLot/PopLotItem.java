package com.pulmuone.mrtina.popupLot;

import java.io.Serializable;

public class PopLotItem implements Serializable {
	public String  LOT_NUMBER,
			EXPIRATION_DATE,
			ORIGINATION,
			SORTING;
	public PopLotItem(String a0, String a1, String a2, String a3){
		LOT_NUMBER = a0;
		EXPIRATION_DATE = a1;
		ORIGINATION = a2;
		SORTING = a3;
	}
}