package com.pulmuone.mrtina.popupPickTicket;

public class PopPickTicketItem{

	public String  SO_TRO_NO_BAR_CODE,
			SO_TRO_HEADER_ID,
			SO_TRO_NO,
			SHIP_TO_ORG_CODE,
			SHIP_TO_ORG_ID,
			SHIP_DATE,
			PICK_TYPE,
			OU_ID,
			ORG_ID;

	public PopPickTicketItem(String SO_TRO_NO_BAR_CODE, String SO_TRO_HEADER_ID, String SO_TRO_NO,
							 String SHIP_TO_ORG_CODE, String SHIP_TO_ORG_ID, String SHIP_DATE,
							 String PICK_TYPE, String OU_ID, String ORG_ID) {

		this.SO_TRO_NO_BAR_CODE = SO_TRO_NO_BAR_CODE;
		this.SO_TRO_HEADER_ID = SO_TRO_HEADER_ID;
		this.SO_TRO_NO = SO_TRO_NO;
		this.SHIP_TO_ORG_CODE = SHIP_TO_ORG_CODE;
		this.SHIP_TO_ORG_ID = SHIP_TO_ORG_ID;
		this.SHIP_DATE = SHIP_DATE;
		this.PICK_TYPE = PICK_TYPE;
		this.OU_ID = OU_ID;
		this.ORG_ID = ORG_ID;
	}
}