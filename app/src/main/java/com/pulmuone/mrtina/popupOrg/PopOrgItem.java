package com.pulmuone.mrtina.popupOrg;

import java.io.Serializable;

public class PopOrgItem implements Serializable {
	public String ORG_CODE, ORG_NAME, ORG_ID, OU_ID;

	public PopOrgItem(String a0, String a1, String a2, String a3){
		ORG_CODE = a0;
		ORG_NAME = a1;
		ORG_ID = a2;
		OU_ID = a3;
	}
}