package com.pulmuone.mrtina.prodTran;

public class ProdTranItem{

    public String ORGANIZATION_CODE,
            PALLET_ID,
            PALLET_NUMBER,
            BATCH_ID,
            BATCH_NO,
            ACTUAL_START_DATE,
            INVENTORY_ITEM_ID,
            ITEM_CODE,
            ITEM_DESC,
            PL_PLAN_QTY,
            PL_SKU_UOM,
            PL_CON_RATE,
            PLAN_QTY,
            PL_ACTUAL_QTY,
            COMP_QTY,
            PRIMARY_UOM_CODE,
            RECIPE_NO,
            RECIPE_VERSION,
            ROUTING_ID,
            ROUTING_NO,
            ROUTING_VERS,
            FORMULA_ID,
            FORMULA_NO,
            FORMULA_VERS,
            MATERIAL_DETAIL_ID,
            TRANSACTION_ID,
            SUBINVENTORY_CODE,
            PALLET_CONTROL,
            LOCATOR_CONTROL,
            LOT_CONTROL_CODE,
            INVENTORY_LOCATION_ID,
            LOCATOR_CODE,
            LOCATOR_NAME,
            LOT_NUMBER,
            EXPIRATION_DATE,
            REASON_DESC,
            REASON_ID,
            PL_TXN_QTY,
            PRIMARY_TXN_QTY,
            BATCH_PALLET_SEQ,
            DETAIL_LINE,
            PER_PALLET,
            DUE_DATE;

    public ProdTranItem(String ORGANIZATION_CODE, String PALLET_ID, String PALLET_NUMBER, String BATCH_ID,
                        String BATCH_NO, String ACTUAL_START_DATE, String INVENTORY_ITEM_ID, String ITEM_CODE,
                        String ITEM_DESC, String PL_PLAN_QTY, String PL_SKU_UOM, String PL_CON_RATE, String PLAN_QTY,
                        String PL_ACTUAL_QTY, String COMP_QTY, String PRIMARY_UOM_CODE, String RECIPE_NO, String RECIPE_VERSION,
                        String ROUTING_ID, String ROUTING_NO, String ROUTING_VERS, String FORMULA_ID,
                        String FORMULA_NO, String FORMULA_VERS, String MATERIAL_DETAIL_ID, String TRANSACTION_ID,
                        String SUBINVENTORY_CODE, String PALLET_CONTROL, String LOCATOR_CONTROL, String LOT_CONTROL_CODE,
                        String INVENTORY_LOCATION_ID, String LOCATOR_CODE, String LOCATOR_NAME, String LOT_NUMBER,
                        String EXPIRATION_DATE, String REASON_DESC, String REASON_ID, String PL_TXN_QTY, String PRIMARY_TXN_QTY,
                        String BATCH_PALLET_SEQ, String DETAIL_LINE, String PER_PALLET, String DUE_DATE) {

        this.ORGANIZATION_CODE = ORGANIZATION_CODE;
        this.PALLET_ID = PALLET_ID;
        this.PALLET_NUMBER = PALLET_NUMBER;
        this.BATCH_ID = BATCH_ID;
        this.BATCH_NO = BATCH_NO;
        this.ACTUAL_START_DATE = ACTUAL_START_DATE;
        this.INVENTORY_ITEM_ID = INVENTORY_ITEM_ID;
        this.ITEM_CODE = ITEM_CODE;
        this.ITEM_DESC = ITEM_DESC;
        this.PL_PLAN_QTY = PL_PLAN_QTY;
        this.PL_SKU_UOM = PL_SKU_UOM;
        this.PL_CON_RATE = PL_CON_RATE;
        this.PLAN_QTY = PLAN_QTY;
        this.PL_ACTUAL_QTY = PL_ACTUAL_QTY;
        this.COMP_QTY = COMP_QTY;
        this.PRIMARY_UOM_CODE = PRIMARY_UOM_CODE;
        this.RECIPE_NO = RECIPE_NO;
        this.RECIPE_VERSION = RECIPE_VERSION;
        this.ROUTING_ID = ROUTING_ID;
        this.ROUTING_NO = ROUTING_NO;
        this.ROUTING_VERS = ROUTING_VERS;
        this.FORMULA_ID = FORMULA_ID;
        this.FORMULA_NO = FORMULA_NO;
        this.FORMULA_VERS = FORMULA_VERS;
        this.MATERIAL_DETAIL_ID = MATERIAL_DETAIL_ID;
        this.TRANSACTION_ID = TRANSACTION_ID;
        this.SUBINVENTORY_CODE = SUBINVENTORY_CODE;
        this.PALLET_CONTROL = PALLET_CONTROL;
        this.LOCATOR_CONTROL = LOCATOR_CONTROL;
        this.LOT_CONTROL_CODE = LOT_CONTROL_CODE;
        this.INVENTORY_LOCATION_ID = INVENTORY_LOCATION_ID;
        this.LOCATOR_CODE = LOCATOR_CODE;
        this.LOCATOR_NAME = LOCATOR_NAME;
        this.LOT_NUMBER = LOT_NUMBER;
        this.EXPIRATION_DATE = EXPIRATION_DATE;
        this.REASON_DESC = REASON_DESC;
        this.REASON_ID = REASON_ID;
        this.PL_TXN_QTY = PL_TXN_QTY;
        this.PRIMARY_TXN_QTY = PRIMARY_TXN_QTY;
        this.BATCH_PALLET_SEQ = BATCH_PALLET_SEQ;
        this.DETAIL_LINE = DETAIL_LINE;
        this.PER_PALLET = PER_PALLET;
        this.DUE_DATE = DUE_DATE;
    }
}
