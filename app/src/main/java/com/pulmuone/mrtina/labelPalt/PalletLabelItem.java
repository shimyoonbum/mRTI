package com.pulmuone.mrtina.labelPalt;

import android.view.View;

import java.io.Serializable;

public class PalletLabelItem implements Serializable {
	public String PALLET_ID;
	public String PALLET_NUMBER;
	public String ORGANIZATION_ID;
	public String ORGANIZATION_CODE;
	public String SUBINVENTORY_CODE;
	public String INVENTORY_LOCATION_ID;
	public String LOCATOR_CODE;
	public String BATCH_ID;
	public String BATCH_NO;
	public String INVENTORY_ITEM_ID;
	public String ITEM_CODE;
	public String ITEM_DESC;
	public String PRIMARY_UOM_CODE;
	public String LOT_NUMBER;
	public String EXPIRATION_DATE;
	public String CHECK_ITEM;

	public PalletLabelItem(String PALLET_ID, String PALLET_NUMBER, String ORGANIZATION_ID, String ORGANIZATION_CODE, String SUBINVENTORY_CODE,
						   String INVENTORY_LOCATION_ID, String LOCATOR_CODE, String BATCH_ID, String BATCH_NO, String INVENTORY_ITEM_ID,
						   String ITEM_CODE, String ITEM_DESC, String PRIMARY_UOM_CODE, String LOT_NUMBER, String EXPIRATION_DATE ,String CHECK_ITEM
						   ) {
		this.PALLET_ID = PALLET_ID;
		this.PALLET_NUMBER = PALLET_NUMBER;
		this.ORGANIZATION_ID = ORGANIZATION_ID;
		this.ORGANIZATION_CODE = ORGANIZATION_CODE;
		this.SUBINVENTORY_CODE = SUBINVENTORY_CODE;
		this.INVENTORY_LOCATION_ID = INVENTORY_LOCATION_ID;
		this.LOCATOR_CODE = LOCATOR_CODE;
		this.BATCH_ID = BATCH_ID;
		this.BATCH_NO = BATCH_NO;
		this.INVENTORY_ITEM_ID = INVENTORY_ITEM_ID;
		this.ITEM_CODE = ITEM_CODE;
		this.ITEM_DESC = ITEM_DESC;
		this.PRIMARY_UOM_CODE = PRIMARY_UOM_CODE;
		this.LOT_NUMBER = LOT_NUMBER;
		this.EXPIRATION_DATE = EXPIRATION_DATE;
		this.CHECK_ITEM = CHECK_ITEM;
	}
}