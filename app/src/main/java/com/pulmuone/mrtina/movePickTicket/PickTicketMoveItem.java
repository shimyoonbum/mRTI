package com.pulmuone.mrtina.movePickTicket;

public class PickTicketMoveItem {

	public String PRIMARY_UOM_CODE;
	public String SUBINVENTORY_CODE;
	public String LOCATOR_CODE;
	public String ITEM_DESC;
	public String ITEM_CODE;
	public String ORGANIZATION_ID;
	public String INVENTORY_ITEM_ID;
	public String ITEM_QTY;
	public String INVENTORY_LOCATION_ID;
	public String PALLET_NUMBER;
	public String LOT_NUMBER;

	public PickTicketMoveItem(String PRIMARY_UOM_CODE, String SUBINVENTORY_CODE, String LOCATOR_CODE,
							  String ITEM_DESC, String ITEM_CODE, String ORGANIZATION_ID, String INVENTORY_ITEM_ID,
							  String ITEM_QTY, String INVENTORY_LOCATION_ID,
							  String PALLET_NUMBER, String LOT_NUMBER) {
		this.PRIMARY_UOM_CODE = PRIMARY_UOM_CODE;
		this.SUBINVENTORY_CODE = SUBINVENTORY_CODE;
		this.LOCATOR_CODE = LOCATOR_CODE;
		this.ITEM_DESC = ITEM_DESC;
		this.ITEM_CODE = ITEM_CODE;
		this.ORGANIZATION_ID = ORGANIZATION_ID;
		this.INVENTORY_ITEM_ID = INVENTORY_ITEM_ID;
		this.ITEM_QTY = ITEM_QTY;
		this.INVENTORY_LOCATION_ID = INVENTORY_LOCATION_ID;
		this.PALLET_NUMBER = PALLET_NUMBER;
		this.LOT_NUMBER = LOT_NUMBER;
	}
}