package com.pulmuone.mrtina.labelLoca;

import android.view.View;

import java.io.Serializable;

public class LocaLabelItem implements Serializable {

	public String ORGANIZATION_ID;
	public String ORGANIZATION_CODE;
	public String SUBINVENTORY_CODE;
	public String INVENTORY_LOCATION_ID;
	public String LOCATOR_CODE;
	public String LOCATOR_NAME;
	public String LOCATOR_BAR_CODE;
	public String PALLET_NUMBER;
	public String ITEM_CODE;
	public String ITEM_DESC;
	public String CHECK_ITEM;

	public LocaLabelItem(String ORGANIZATION_ID, String ORGANIZATION_CODE, String SUBINVENTORY_CODE, String INVENTORY_LOCATION_ID,
						 String LOCATOR_CODE, String LOCATOR_NAME, String LOCATOR_BAR_CODE, String PALLET_NUMBER,
						 String ITEM_CODE, String ITEM_DESC, String CHECK_ITEM) {

		this.ORGANIZATION_ID = ORGANIZATION_ID;
		this.ORGANIZATION_CODE = ORGANIZATION_CODE;
		this.SUBINVENTORY_CODE = SUBINVENTORY_CODE;
		this.INVENTORY_LOCATION_ID = INVENTORY_LOCATION_ID;
		this.LOCATOR_CODE = LOCATOR_CODE;
		this.LOCATOR_NAME = LOCATOR_NAME;
		this.LOCATOR_BAR_CODE = LOCATOR_BAR_CODE;
		this.PALLET_NUMBER = PALLET_NUMBER;
		this.ITEM_CODE = ITEM_CODE;
		this.ITEM_DESC = ITEM_DESC;
		this.CHECK_ITEM = CHECK_ITEM;
	}
}