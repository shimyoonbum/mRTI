package com.pulmuone.mrtina.movePickTicket;

public class PickTicketMoveDtlItem {
	public String PICK_QTY;
	public String TRANSACTION_UOM;
	public String LOCATOR_CODE;
	public String ITEM_DESC;
	public String SUBINVENTORY_CODE;
	public String ITEM_CODE;
	public String PALLET_NUMBER;
	public String LOT_NUMBER;

	public PickTicketMoveDtlItem(String PICK_QTY, String TRANSACTION_UOM, String LOCATOR_CODE, String ITEM_DESC,
								 String SUBINVENTORY_CODE, String ITEM_CODE, String PALLET_NUMBER, String LOT_NUMBER) {
		this.PICK_QTY = PICK_QTY;
		this.TRANSACTION_UOM = TRANSACTION_UOM;
		this.LOCATOR_CODE = LOCATOR_CODE;
		this.ITEM_DESC = ITEM_DESC;
		this.SUBINVENTORY_CODE = SUBINVENTORY_CODE;
		this.ITEM_CODE = ITEM_CODE;
		this.PALLET_NUMBER = PALLET_NUMBER;
		this.LOT_NUMBER = LOT_NUMBER;
	}
}