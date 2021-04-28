package com.pulmuone.mrtina.changeSubInv;

import java.io.Serializable;

public class ChangeSubInvItem implements Serializable {
	public String  SUBINVENTORY_CODE,
			SUBINVENTORY_NAME
			,ORG_ID
			,LOCATOR_CONTROL
			,PALLET_CONTROL;
	public ChangeSubInvItem(String a0, String a1, String a2, String a3, String a4){
		SUBINVENTORY_CODE = a0;
		SUBINVENTORY_NAME = a1;
		ORG_ID = a2;
		LOCATOR_CONTROL = a3;
		PALLET_CONTROL = a4;
	}
}
