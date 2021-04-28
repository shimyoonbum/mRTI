package com.pulmuone.mrtina.popupProdPall;

import java.io.Serializable;

public class PopProdPallItem implements Serializable {
	public String PALLET_ID,
			PALLET_NUMBER,
			ITEM_CODE,
			BATCH_NO,
			BATCH_ID,
			PLAN_START_DATE;

	public PopProdPallItem(String PALLET_ID, String PALLET_NUMBER, String ITEM_CODE, String BATCH_NO, String BATCH_ID, String PLAN_START_DATE) {
		this.PALLET_ID = PALLET_ID;
		this.PALLET_NUMBER = PALLET_NUMBER;
		this.ITEM_CODE = ITEM_CODE;
		this.BATCH_NO = BATCH_NO;
		this.BATCH_ID = BATCH_ID;
		this.PLAN_START_DATE = PLAN_START_DATE;
	}
}