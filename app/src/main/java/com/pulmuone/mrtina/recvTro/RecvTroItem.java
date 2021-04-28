package com.pulmuone.mrtina.recvTro;

import android.view.View;

import java.io.Serializable;

public class RecvTroItem implements Serializable {
	public String PALLET_ID;
	public String PALLET_NUMBER;
	public String PALLET_CONTROL;
	public String ITEM_CODE;
	public String ITEM_DESC;
	public String LOT_NUMBER;
	public String REMAIN_QTY;
	public String LOCATOR_CODE;
	public String LOCATOR_CONTROL;
	public String ORGANIZATION_CODE;
	public String SHIPMENT_HEADER_ID;
	public String SHIPMENT_LINE_ID;
	public String VENDOR_ID;
	public String ORDER_UOM_CODE;
	public String SUBINVENTORY_CODE;
	public String INVENTORY_LOCATION_ID;
	public String EXPIRATION_DATE;
	public String RECEIVING_QTY;
	public String BILL_OF_LADING;
	public String OU_ID;
	public String ORG_ID;
	public String RECEIPT_DATE;
	public String USER_NO;
	public String BACKCOLOR;
	public String CHECK_ITEM;
	public View.OnClickListener onClickListener;

	public RecvTroItem(String PALLET_ID, String PALLET_NUMBER, String PALLET_CONTROL, String ITEM_CODE, String ITEM_DESC, String LOT_NUMBER, String REMAIN_QTY,
					   String LOCATOR_CODE, String LOCATOR_CONTROL, String ORGANIZATION_CODE, String SHIPMENT_HEADER_ID, String SHIPMENT_LINE_ID,
					   String VENDOR_ID, String ORDER_UOM_CODE, String SUBINVENTORY_CODE,
					   String INVENTORY_LOCATION_ID, String EXPIRATION_DATE, String RECEIVING_QTY,
					   String BILL_OF_LADING, String OU_ID, String ORG_ID,
					   String RECEIPT_DATE, String USER_NO, String BACKCOLOR, String CHECK_ITEM, View.OnClickListener onClickListener) {
		this.PALLET_ID = PALLET_ID;
		this.PALLET_NUMBER = PALLET_NUMBER;
		this.PALLET_CONTROL = PALLET_CONTROL;
		this.ITEM_CODE = ITEM_CODE;
		this.ITEM_DESC = ITEM_DESC;
		this.LOT_NUMBER = LOT_NUMBER;
		this.REMAIN_QTY = REMAIN_QTY;
		this.LOCATOR_CODE = LOCATOR_CODE;
		this.LOCATOR_CONTROL = LOCATOR_CONTROL;
		this.ORGANIZATION_CODE = ORGANIZATION_CODE;
		this.SHIPMENT_HEADER_ID = SHIPMENT_HEADER_ID;
		this.SHIPMENT_LINE_ID = SHIPMENT_LINE_ID;
		this.VENDOR_ID = VENDOR_ID;
		this.ORDER_UOM_CODE = ORDER_UOM_CODE;
		this.SUBINVENTORY_CODE = SUBINVENTORY_CODE;
		this.INVENTORY_LOCATION_ID = INVENTORY_LOCATION_ID;
		this.EXPIRATION_DATE = EXPIRATION_DATE;
		this.RECEIVING_QTY = RECEIVING_QTY;
		this.BILL_OF_LADING = BILL_OF_LADING;
		this.OU_ID = OU_ID;
		this.ORG_ID = ORG_ID;
		this.RECEIPT_DATE = RECEIPT_DATE;
		this.USER_NO = USER_NO;
		this.CHECK_ITEM = CHECK_ITEM;
		this.BACKCOLOR = BACKCOLOR;
		this.onClickListener = onClickListener;
	}

	public void setSubinventoryCode(String SUBINVENTORY_CODE) {
		this.SUBINVENTORY_CODE = SUBINVENTORY_CODE;
	}

	public void setInventoryLocationId(String INVENTORY_LOCATION_ID) {
		this.INVENTORY_LOCATION_ID = INVENTORY_LOCATION_ID;
	}

	public void setPalletControl(String PALLET_CONTROL) {
		this.PALLET_CONTROL = PALLET_CONTROL;
	}

	public void setPalletId(String PALLET_ID) {
		this.PALLET_ID = PALLET_ID;
	}

	public void setPalletNum(String PALLET_NUMBER) {
		this.PALLET_NUMBER = PALLET_NUMBER;
	}

	public void setLocatorCode(String LOCATOR_CODE) {
		this.LOCATOR_CODE = LOCATOR_CODE;
	}

	public void setLocatorControl(String LOCATOR_CONTROL) {
		this.LOCATOR_CONTROL = LOCATOR_CONTROL;
	}

	public void setBackColor(String BACKCOLOR) {
		this.BACKCOLOR = BACKCOLOR;
	}

	public String getVendorId() {
		return VENDOR_ID;
	}

	public String getItemCode() {
		return ITEM_CODE;
	}

	public String getOrderUomCode() {
		return ORDER_UOM_CODE;
	}

	public String getInventoryLocationId() {
		return INVENTORY_LOCATION_ID;
	}

	public String getLotNumber() {
		return LOT_NUMBER;
	}

	public String getExpriationDate() {
		return EXPIRATION_DATE;
	}

	public String getRemainQty() {
		return REMAIN_QTY;
	}

	public String getOuId() {
		return OU_ID;
	}

	public String getOrgId() {
		return ORG_ID;
	}

	public String getCheckItem() {
		return CHECK_ITEM;
	}

	public String getOrgCode() {
		return ORGANIZATION_CODE;
	}

	public String getShipmentHeaderId() {
		return SHIPMENT_HEADER_ID;
	}

	public String getShipmentLineId() {
		return SHIPMENT_LINE_ID;
	}

	public String getPalletId() {
		return PALLET_ID;
	}

	public String getLocatorControl() {
		return LOCATOR_CONTROL;
	}

	public String getPalletControl() {
		return PALLET_CONTROL;
	}

	public String getSubinventoryCode() {
		return SUBINVENTORY_CODE;
	}

	public String getLocatorCode() {
		return LOCATOR_CODE;
	}

	public String getBackColor() {
		return BACKCOLOR;
	}
}