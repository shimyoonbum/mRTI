package com.pulmuone.mrtina.popupPo;

import java.io.Serializable;

public class PopPoItem implements Serializable {
	public String  PO_NO,
			OU_ID,
			ORGANIZATION_CODE,
			ORGANIZATION_ID,
					SUPPLIER,
					PO_DATE,
					BL_TYPE;

	public PopPoItem(String a0, String a1, String a2, String a3, String a4, String a5, String a6){
		PO_NO = a0;
		OU_ID = a1;
		ORGANIZATION_CODE = a2;
		ORGANIZATION_ID = a3;
		SUPPLIER = a4;
		PO_DATE = a5;
		BL_TYPE = a6;
	}
}