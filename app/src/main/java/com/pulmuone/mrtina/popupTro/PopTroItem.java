package com.pulmuone.mrtina.popupTro;

import java.io.Serializable;

public class PopTroItem implements Serializable {
	public String  ORG_CODE_TO,
			ORG_ID_TO,
			ORG_CODE_FROM,
			ORG_ID_FROM,
			SHIPMENT_DATE,
			SHIPMENT_NO,
			SHIPMENT_HEADER_ID,
			EXP_RECEIPT_DATE,
			OU_ID;

	public PopTroItem(String a0, String a1, String a2, String a3, String a4, String a5, String a6, String a7, String a8) {
		ORG_CODE_TO = a0;
		ORG_ID_TO = a1;
		ORG_CODE_FROM = a2;
		ORG_ID_FROM = a3;
		SHIPMENT_DATE = a4;
		SHIPMENT_NO = a5;
		SHIPMENT_HEADER_ID = a6;
		EXP_RECEIPT_DATE = a7;
		OU_ID = a8;
	}
}