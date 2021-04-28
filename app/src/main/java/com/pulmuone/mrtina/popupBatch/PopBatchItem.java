package com.pulmuone.mrtina.popupBatch;

import java.io.Serializable;

public class PopBatchItem implements Serializable {
	public String  BATCH_NO,
			BATCH_ID,
			ITEM_CODE,
			PLAN_START_DATE;

	public PopBatchItem(String BATCH_NO, String BATCH_ID, String ITEM_CODE, String PLAN_START_DATE) {
		this.BATCH_NO = BATCH_NO;
		this.BATCH_ID = BATCH_ID;
		this.ITEM_CODE = ITEM_CODE;
		this.PLAN_START_DATE = PLAN_START_DATE;
	}
}