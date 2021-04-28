package com.pulmuone.mrtina.movePickTicket;

public class PickTicketNonDtlItem {
	public String PICK_QTY;
	public String TRANSACTION_UOM;
	public String LOCATOR_CODE;
	public String ITEM_DESC;
	public String SUBINVENTORY_CODE;
	public String ITEM_CODE;
	public String PALLET_NUMBER;
	public String LOT_NUMBER;
	public String CHECK_ITEM;
	public String RESERVATION_ID;

	public PickTicketNonDtlItem(String PICK_QTY, String TRANSACTION_UOM, String LOCATOR_CODE, String ITEM_DESC,
                                String SUBINVENTORY_CODE, String ITEM_CODE, String PALLET_NUMBER, String LOT_NUMBER,
								String CHECK_ITEM, String RESERVATION_ID) {
		this.PICK_QTY = PICK_QTY;
		this.TRANSACTION_UOM = TRANSACTION_UOM;
		this.LOCATOR_CODE = LOCATOR_CODE;
		this.ITEM_DESC = ITEM_DESC;
		this.SUBINVENTORY_CODE = SUBINVENTORY_CODE;
		this.ITEM_CODE = ITEM_CODE;
		this.PALLET_NUMBER = PALLET_NUMBER;
		this.LOT_NUMBER = LOT_NUMBER;
		this.CHECK_ITEM = CHECK_ITEM;
		this.RESERVATION_ID = RESERVATION_ID;
	}
}