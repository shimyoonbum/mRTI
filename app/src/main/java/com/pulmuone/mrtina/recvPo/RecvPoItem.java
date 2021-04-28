package com.pulmuone.mrtina.recvPo;

import android.view.View;

import java.io.Serializable;

public class RecvPoItem{
	public String ORG_CODE,
			PO_HEADER_ID,
			PO_LINE_ID,
			PO_LINE_LOCATION_ID,
			VENDOR_ID,
			ITEM_CODE,
			ORDER_UOM_CODE,
			SUBINVENTORY_CODE,
			LOCATOR_CODE,
			INVENTORY_LOCATION_ID,
			LOT_NUMBER,
			LOT_CONTROL_CODE,
			EXPIRATION_DATE,
			RECEIVING_QTY,
			BILL_OF_LADING,
			PALLET_CONTROL,
			PALLET_ID,
			PALLET_NUM,
			ITEM_DESC,
			ITEM_ID,
			PO_QTY,
			LOCATOR_CONTROL,
			OU_ID,
			ORG_ID,
			RECEIPT_DATE,
			USER_NO,
			PO_NO,
			CHECK_ITEM,
			IMAGE,
			BACKCOLOR;
	public View.OnClickListener onClickListener;

	public RecvPoItem(String ORG_CODE, String PO_HEADER_ID, String PO_LINE_ID, String PO_LINE_LOCATION_ID, String VENDOR_ID,
					  String ITEM_CODE, String ORDER_UOM_CODE, String SUBINVENTORY_CODE, String LOCATOR_CODE, String INVENTORY_LOCATION_ID,
					  String LOT_NUMBER, String LOT_CONTROL_CODE, String EXPIRATION_DATE, String RECEIVING_QTY, String BILL_OF_LADING, String PALLET_CONTROL, String PALLET_ID, String PALLET_NUM,
					  String ITEM_DESC, String ITEM_ID, String PO_QTY, String LOCATOR_CONTROL, String OU_ID, String ORG_ID, String RECEIPT_DATE,
					  String USER_NO, String PO_NO, String CHECK_ITEM, String IMAGE, String BACKCOLOR, View.OnClickListener onClickListener) {
		this.ORG_CODE = ORG_CODE;
		this.PO_HEADER_ID = PO_HEADER_ID;
		this.PO_LINE_ID = PO_LINE_ID;
		this.PO_LINE_LOCATION_ID = PO_LINE_LOCATION_ID;
		this.VENDOR_ID = VENDOR_ID;
		this.ITEM_CODE = ITEM_CODE;
		this.ORDER_UOM_CODE = ORDER_UOM_CODE;
		this.SUBINVENTORY_CODE = SUBINVENTORY_CODE;
		this.LOCATOR_CODE = LOCATOR_CODE;
		this.INVENTORY_LOCATION_ID = INVENTORY_LOCATION_ID;
		this.LOT_NUMBER = LOT_NUMBER;
		this.LOT_CONTROL_CODE = LOT_CONTROL_CODE;
		this.EXPIRATION_DATE = EXPIRATION_DATE;
		this.RECEIVING_QTY = RECEIVING_QTY;
		this.BILL_OF_LADING = BILL_OF_LADING;
		this.PALLET_CONTROL = PALLET_CONTROL;
		this.PALLET_ID = PALLET_ID;
		this.PALLET_NUM = PALLET_NUM;
		this.ITEM_DESC = ITEM_DESC;
		this.ITEM_ID = ITEM_ID;
		this.PO_QTY = PO_QTY;
		this.LOCATOR_CONTROL = LOCATOR_CONTROL;
		this.OU_ID = OU_ID;
		this.ORG_ID = ORG_ID;
		this.RECEIPT_DATE = RECEIPT_DATE;
		this.USER_NO = USER_NO;
		this.PO_NO = PO_NO;
		this.CHECK_ITEM = CHECK_ITEM;
		this.IMAGE = IMAGE;
		this.BACKCOLOR = BACKCOLOR;
		this.onClickListener = onClickListener;
	}

	public RecvPoItem() {
	}


	public void setLotNumber(String LOT_NUMBER) {
		this.LOT_NUMBER = LOT_NUMBER;
	}

	public void setExpirationDate(String EXPIRATION_DATE) {
		this.EXPIRATION_DATE = EXPIRATION_DATE;
	}

	public void setSubinventoryCode(String SUBINVENTORY_CODE) {
		this.SUBINVENTORY_CODE = SUBINVENTORY_CODE;
	}

	public void setInventoryLocationId(String INVENTORY_LOCATION_ID) {
		this.INVENTORY_LOCATION_ID = INVENTORY_LOCATION_ID;
	}

	public void setPalletNum(String PALLET_NUM) {
		this.PALLET_NUM = PALLET_NUM;
	}

	public void setPoQty(String PO_QTY) {
		this.PO_QTY = PO_QTY;
	}

	public void setPalletId(String PALLET_ID) {
		this.PALLET_ID = PALLET_ID;
	}

	public void setLocatorControl(String LOCATOR_CONTROL) {
		this.LOCATOR_CONTROL = LOCATOR_CONTROL;
	}

	public void setLocatorCode(String LOCATOR_CODE) {
		this.LOCATOR_CODE = LOCATOR_CODE;
	}

	public void setImage(String IMAGE) {
		this.IMAGE = IMAGE;
	}

	public void setPalletControl(String PALLET_CONTROL) {
		this.PALLET_CONTROL = PALLET_CONTROL;
	}

	public void setBackColor(String BACKCOLOR) {
		this.BACKCOLOR = BACKCOLOR;
	}

	public String getOrgCode() {
		return ORG_CODE;
	}

	public String getPoHeaderId() {
		return PO_HEADER_ID;
	}

	public String getPoLineId() {
		return PO_LINE_ID;
	}

	public String getPoLineLocationId() {
		return PO_LINE_LOCATION_ID;
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

	public String getSubinventoryCode() {
		return SUBINVENTORY_CODE;
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

	public String getPoQty() {
		return PO_QTY;
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

	public String getPalletId() {
		return PALLET_ID;
	}

	public String getLocatorControl() {
		return LOCATOR_CONTROL;
	}

	public String getLocatorCode() {
		return LOCATOR_CODE;
	}

	public String getImage() {
		return IMAGE;
	}

	public String getPalletControl() {
		return PALLET_CONTROL;
	}


	public String getPoNo() {
		return PO_NO;
	}

	public String getItemId() {
		return ITEM_ID;
	}

	public String getLotControlCode() {
		return LOT_CONTROL_CODE;
	}

	public String getBackColor() {
		return BACKCOLOR;
	}
}