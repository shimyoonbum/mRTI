package com.pulmuone.mrtina.popupTranTro;

import java.io.Serializable;

public class PopTranTroItem implements Serializable {
	public String  ORG_CODE_TO,
			ORG_ID_TO,
			ORG_CODE_FROM,
			ORG_ID_FROM,
			SHIPMENT_DATE,
			SHIPMENT_NO,
			SHIPMENT_HEADER_ID,
			EXP_RECEIPT_DATE,
			OU_ID;

	public PopTranTroItem(String ORG_CODE_TO, String ORG_ID_TO, String ORG_CODE_FROM,
						  String ORG_ID_FROM, String SHIPMENT_DATE, String SHIPMENT_NO,
						  String SHIPMENT_HEADER_ID, String EXP_RECEIPT_DATE, String OU_ID) {
		this.ORG_CODE_TO = ORG_CODE_TO;
		this.ORG_ID_TO = ORG_ID_TO;
		this.ORG_CODE_FROM = ORG_CODE_FROM;
		this.ORG_ID_FROM = ORG_ID_FROM;
		this.SHIPMENT_DATE = SHIPMENT_DATE;
		this.SHIPMENT_NO = SHIPMENT_NO;
		this.SHIPMENT_HEADER_ID = SHIPMENT_HEADER_ID;
		this.EXP_RECEIPT_DATE = EXP_RECEIPT_DATE;
		this.OU_ID = OU_ID;
	}
}