package com.pulmuone.mrtina.tranTro;

import android.view.View;

import java.io.Serializable;

public class TranTroItem implements Serializable {
	public String  SHIPMENT_LINE_ID,
			SHIPMENT_HEADER_ID,
			SHIPMENT_DATE,
			EXPECTED_RECEIPT_DATE,
			ORG_ID_FROM,
			ORG_CODE_FROM,
			SUBINV_CODE_FROM,
			INV_LOCATION_ID_FROM,
			LOCATOR_CODE_FROM,
			INVENTORY_ITEM_ID,
			ITEM_CODE,
			ITEM_DESC,
			LOT_CONTROL_CODE,
			TRANSACTION_UOM,
			SHIPMENT_QTY,
			LOT_NUMBER,
			ORG_ID_TO,
			ORG_CODE_TO,
			SUBINV_CODE_TO,
			INV_LOCATION_ID_TO,
			LOCATOR_CODE_TO,
			LOCATOR_CONTROL,
			PALLET_CONTROL,
			PALLET_ID,
			PALLET_NUMBER,
			OU_ID,
			ORG_ID,
			RECEIPT_DATE,
			USER_NO,
			CHECK_ITEM,
			BACKCOLOR;
	public View.OnClickListener onClickListener;

	public TranTroItem(String SHIPMENT_LINE_ID, String SHIPMENT_HEADER_ID, String SHIPMENT_DATE,
					   String EXPECTED_RECEIPT_DATE, String ORG_ID_FROM, String ORG_CODE_FROM, String SUBINV_CODE_FROM,
					   String INV_LOCATION_ID_FROM, String LOCATOR_CODE_FROM, String INVENTORY_ITEM_ID, String ITEM_CODE,
					   String ITEM_DESC, String LOT_CONTROL_CODE, String TRANSACTION_UOM, String SHIPMENT_QTY, String LOT_NUMBER,
					   String ORG_ID_TO, String ORG_CODE_TO, String SUBINV_CODE_TO, String INV_LOCATION_ID_TO, String LOCATOR_CODE_TO,
					   String LOCATOR_CONTROL, String PALLET_CONTROL,
					   String PALLET_ID, String PALLET_NUMBER, String OU_ID, String ORG_ID, String RECEIPT_DATE, String USER_NO, String CHECK_ITEM,
					   String BACKCOLOR, View.OnClickListener onClickListener) {
		this.SHIPMENT_LINE_ID = SHIPMENT_LINE_ID;
		this.SHIPMENT_HEADER_ID = SHIPMENT_HEADER_ID;
		this.SHIPMENT_DATE = SHIPMENT_DATE;
		this.EXPECTED_RECEIPT_DATE = EXPECTED_RECEIPT_DATE;
		this.ORG_ID_FROM = ORG_ID_FROM;
		this.ORG_CODE_FROM = ORG_CODE_FROM;
		this.SUBINV_CODE_FROM = SUBINV_CODE_FROM;
		this.INV_LOCATION_ID_FROM = INV_LOCATION_ID_FROM;
		this.LOCATOR_CODE_FROM = LOCATOR_CODE_FROM;
		this.INVENTORY_ITEM_ID = INVENTORY_ITEM_ID;
		this.ITEM_CODE = ITEM_CODE;
		this.ITEM_DESC = ITEM_DESC;
		this.LOT_CONTROL_CODE = LOT_CONTROL_CODE;
		this.TRANSACTION_UOM = TRANSACTION_UOM;
		this.SHIPMENT_QTY = SHIPMENT_QTY;
		this.LOT_NUMBER = LOT_NUMBER;
		this.ORG_ID_TO = ORG_ID_TO;
		this.ORG_CODE_TO = ORG_CODE_TO;
		this.SUBINV_CODE_TO = SUBINV_CODE_TO;
		this.INV_LOCATION_ID_TO = INV_LOCATION_ID_TO;
		this.LOCATOR_CODE_TO = LOCATOR_CODE_TO;
		this.LOCATOR_CONTROL = LOCATOR_CONTROL;
		this.PALLET_CONTROL = PALLET_CONTROL;
		this.PALLET_ID = PALLET_ID;
		this.PALLET_NUMBER = PALLET_NUMBER;
		this.OU_ID = OU_ID;
		this.ORG_ID = ORG_ID;
		this.RECEIPT_DATE = RECEIPT_DATE;
		this.USER_NO = USER_NO;
		this.CHECK_ITEM = CHECK_ITEM;
		this.BACKCOLOR = BACKCOLOR;
		this.onClickListener = onClickListener;
	}

	public void setShipmentQty(String SHIPMENT_QTY) {
		this.SHIPMENT_QTY = SHIPMENT_QTY;
	}

	public void setLotNumber(String LOT_NUMBER) {
		this.LOT_NUMBER = LOT_NUMBER;
	}

	public void setSubInvCodeFrom(String SUBINV_CODE_FROM) {
		this.SUBINV_CODE_FROM = SUBINV_CODE_FROM;
	}

	public void setLocatorCodeFrom(String LOCATOR_CODE_FROM) {
		this.LOCATOR_CODE_FROM = LOCATOR_CODE_FROM;
	}

	public void setInvLocIDFrom(String INV_LOCATION_ID_FROM) {
		this.INV_LOCATION_ID_FROM = INV_LOCATION_ID_FROM;
	}

	public void setBackColor(String BACKCOLOR) {
		this.BACKCOLOR = BACKCOLOR;
	}

	public void setLocatorControl(String LOCATOR_CONTROL) {
		this.LOCATOR_CONTROL = LOCATOR_CONTROL;
	}

	public void setLotControlCode(String LOT_CONTROL_CODE) {
		this.LOT_CONTROL_CODE = LOT_CONTROL_CODE;
	}

	public void setPalletId(String PALLET_ID) {
		this.PALLET_ID = PALLET_ID;
	}

	public void setPalletNum(String PALLET_NUMBER) {
		this.PALLET_NUMBER = PALLET_NUMBER;
	}

	public void setPalletControl(String PALLET_CONTROL) {
		this.PALLET_CONTROL = PALLET_CONTROL;
	}

	public String getCheckItem() {
		return CHECK_ITEM;
	}

	public String getLocatorControl() {
		return LOCATOR_CONTROL;
	}

	public String getSubInvCodeTo() {
		return SUBINV_CODE_TO;
	}

	public String getShipmentLineId() {
		return SHIPMENT_LINE_ID;
	}

	public String getShipmentHeaderId() {
		return SHIPMENT_HEADER_ID;
	}

	public String getOrgCodeFrom() {
		return ORG_CODE_FROM;
	}

	public String getSubinvCodeFrom() {
		return SUBINV_CODE_FROM;
	}

	public String getInvLocationIdFrom() {
		return INV_LOCATION_ID_FROM;
	}

	public String getItemCode() {
		return ITEM_CODE;
	}

	public String getTransactionUom() {
		return TRANSACTION_UOM;
	}

	public String getShipmentQty() {
		return SHIPMENT_QTY;
	}

	public String getLotNumber() {
		return LOT_NUMBER;
	}

	public String getOrgCodeTo() {
		return ORG_CODE_TO;
	}

	public String getSubinvCodeTo() {
		return SUBINV_CODE_TO;
	}

	public String getInvLocationIdTo() {
		return INV_LOCATION_ID_TO;
	}

	public String getPalletId() {
		return PALLET_ID;
	}

	public String getOuId() {
		return OU_ID;
	}

	public String getOrgId() {
		return ORG_ID;
	}

	public String getBackColor() {
		return BACKCOLOR;
	}

    public Object getPalletControl() {
		return PALLET_CONTROL;
    }

	public String getLotControlCode() {
		return LOT_CONTROL_CODE;
	}

	public String getInvLocCodeFrom() {
		return SUBINV_CODE_FROM;
	}

	public String getLocatorCodeFrom() {
		return LOCATOR_CODE_FROM;
	}

	public String getInventoryItemId() {
		return INVENTORY_ITEM_ID;
	}
}