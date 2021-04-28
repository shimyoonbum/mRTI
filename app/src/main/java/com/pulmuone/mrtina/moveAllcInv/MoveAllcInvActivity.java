package com.pulmuone.mrtina.moveAllcInv;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pulmuone.mrtina.R;
import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.comm.HttpClient;
import com.pulmuone.mrtina.comm.MyEvent;
import com.pulmuone.mrtina.comm.Network;
import com.pulmuone.mrtina.comm.NetworkEvent;
import com.pulmuone.mrtina.comm.Scan;
import com.pulmuone.mrtina.popupLoca.PopLocaActivity;
import com.pulmuone.mrtina.popupSubInv.PopSubInvActivity;
import com.pulmuone.mrtina.popupTranItem.PopTranItemActivity;
import com.pulmuone.mrtina.popupTranLoca.PopTranLocaActivity;
import com.pulmuone.mrtina.popupTranLot.PopTranLotActivity;
import com.pulmuone.mrtina.popupTranPall.PopTranPallActivity;
import com.pulmuone.mrtina.popupTranSubInv.PopTranSubInvActivity;
import com.pulmuone.mrtina.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;

import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;

public class MoveAllcInvActivity extends Activity {

    private RelativeLayout btn_back = null;
    private LinearLayout layout_from = null;
    private LinearLayout layout_to = null;
    private LinearLayout layout_pallet = null;
    private LinearLayout btn_date = null;
    private LinearLayout btn_pallNo = null;
    private LinearLayout btn_itemNo = null;
    private LinearLayout btn_lot = null;
    private LinearLayout btn_subInvFrom = null;
    private LinearLayout btn_subInvTo = null;
    private LinearLayout btn_locFrom = null;
    private LinearLayout btn_locTo = null;

    private Button btn_move = null;

    private EditText edit_palletNo = null;
    private EditText edit_lot = null;
    private EditText edit_qty = null;
    private EditText edit_itemNo = null;

    private TextView text_date = null;
    private TextView text_uom = null;
    private TextView text_itemName = null;
    private TextView text_subInvFrom = null;
    private TextView text_subInvTo = null;
    private TextView text_locFrom = null;
    private TextView text_locTo = null;

    SharedPreferences pref = null;
    private ProgressDialog progressDialog;
    private String S_USER_ID = null;
    private String OU_ID = null;
    private String ORG_ID = null;

    private String org_code = null;
    private String inventoryLocationId = null;
    private String palletId = null;
    private String itemId = null;
    private String transferInvLocationId = null;
    private String scanSubInv = null;
    private String statusId = "1";
    private int pYear, pMonth, pDay;

    private Scan Scan = null; //SCANNER CODE
    private String selScan = "M"; // M(main), F(from), T(to)

    private Network network = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_allcinv);

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);
        S_USER_ID = pref.getString("USER_ID", "");

        btn_back = (RelativeLayout) findViewById(R.id.layout_back);
        layout_from = (LinearLayout) findViewById(R.id.layout_tran_from);
        layout_to = (LinearLayout) findViewById(R.id.layout_tran_to);
        layout_pallet = (LinearLayout) findViewById(R.id.layout_pallet);

        btn_date = (LinearLayout) findViewById(R.id.layout_date);
        btn_pallNo = (LinearLayout) findViewById(R.id.layout_pall_no);
        btn_itemNo = (LinearLayout) findViewById(R.id.layout_item_no);
        btn_lot = (LinearLayout) findViewById(R.id.layout_lot_no);
        btn_subInvFrom = (LinearLayout) findViewById(R.id.layout_inv_from);
        btn_subInvTo = (LinearLayout) findViewById(R.id.layout_inv_to);
        btn_locFrom = (LinearLayout) findViewById(R.id.layout_loc_from);
        btn_locTo = (LinearLayout) findViewById(R.id.layout_loc_to);

        btn_move = (Button) findViewById(R.id.btn_move);

        edit_palletNo = (EditText) findViewById(R.id.edit_pall_no);
        edit_itemNo = (EditText) findViewById(R.id.edit_item_no);
        edit_lot = (EditText) findViewById(R.id.edit_lot);
        edit_qty = (EditText) findViewById(R.id.edit_qty);
        text_uom = (TextView) findViewById(R.id.txt_uom);
        text_date = (TextView) findViewById(R.id.txt_tran_date);
        text_itemName = (TextView) findViewById(R.id.txt_item_name);
        text_subInvFrom = (TextView) findViewById(R.id.txt_subinv_from);
        text_subInvTo = (TextView) findViewById(R.id.txt_subinv_to);
        text_locFrom = (TextView) findViewById(R.id.txt_loc_from);
        text_locTo = (TextView) findViewById(R.id.txt_loc_to);
        text_date = (TextView) findViewById(R.id.txt_tran_date);

        edit_palletNo.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_lot.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_itemNo.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        edit_palletNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_palletNo.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_palletNo.setOnClickListener(onClickListener);

        edit_lot.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_lot.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_lot.setOnClickListener(onClickListener);

        edit_itemNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_itemNo.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_itemNo.setOnClickListener(onClickListener);

        edit_qty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_qty.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_qty.setOnClickListener(onClickListener);

        btn_date.setOnClickListener(onClickListener);
        btn_pallNo.setOnClickListener(onClickListener);
        btn_itemNo.setOnClickListener(onClickListener);
        btn_lot.setOnClickListener(onClickListener);
        btn_subInvFrom.setOnClickListener(onClickListener);
        btn_subInvTo.setOnClickListener(onClickListener);
        btn_locFrom.setOnClickListener(onClickListener);
        btn_locTo.setOnClickListener(onClickListener);
        btn_back.setOnClickListener(onClickListener);
        btn_move.setOnClickListener(onClickListener);
        btn_pallNo.setOnClickListener(onClickListener);
        layout_to.setOnClickListener(onClickListener);
        layout_from.setOnClickListener(onClickListener);
        layout_pallet.setOnClickListener(onClickListener);

        Calendar c = Calendar.getInstance();
        pYear = c.get(Calendar.YEAR);
        pMonth = c.get(Calendar.MONTH);
        pDay = c.get(Calendar.DAY_OF_MONTH);
        setDateFormat(pYear, pMonth, pDay);

        // [[ SCANNER CODE
        if(Comm.isLibraryInstalled(this,"com.symbol.emdk")) {
            this.Scan = new Scan(this);
            Scan.setEvent(myListener);
        }
        // ]] SCANNER CODE

        layout_pallet.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.location_layout));
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Comm.SUBINV_FROM) {
                text_subInvFrom.setText(Util.nullString(data.getStringExtra("SUBINV_CODE"), ""));

                if (Util.nullString(data.getStringExtra("LOCATOR_CONTROL"), "").equals("1")) {
                    text_locFrom.setText("");
                    text_locFrom.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
                    inventoryLocationId = "";
                    btn_locFrom.setEnabled(false);
                } else {
                    text_locFrom.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input));
                    btn_locFrom.setEnabled(true);
                }

            } else if (requestCode == Comm.SUBINV_TO) {
                text_subInvTo.setText(Util.nullString(data.getStringExtra("SUBINV_CODE"), ""));
                transferInvLocationId = Util.nullString(data.getStringExtra("LOC_SUBINV_TRSF_ID"), "");
                String sub_inv_code = Util.nullString(data.getStringExtra("LOC_SUBINV_TRSF_CODE"), "");

                if (data.getStringExtra("LOCATOR_CONTROL").equals("1")) {
                    text_locTo.setText("");
                    text_locTo.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
                    transferInvLocationId = "";
                    btn_locTo.setEnabled(false);
                } else {
                    text_locTo.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input));
                    text_locTo.setText(sub_inv_code);
                    btn_locTo.setEnabled(true);
                }
            } else if (requestCode == Comm.LOC_FROM) {
                text_locFrom.setText(Util.nullString(data.getStringExtra("LOCATOR_CODE"), ""));
                inventoryLocationId = Util.nullString(data.getStringExtra("INVENTORY_LOCATION_ID"), "");

            } else if (requestCode == Comm.LOC_TO) {
                text_locTo.setText(Util.nullString(data.getStringExtra("LOCATOR_CODE"), ""));
                transferInvLocationId = Util.nullString(data.getStringExtra("INVENTORY_LOCATION_ID"), "");

            } else if (requestCode == Comm.TRANPALLET) {
                edit_palletNo.setText(Util.nullString(data.getStringExtra("PALLET_NUMBER"), ""));
                inventoryLocationId = "";
                transferInvLocationId = "";

                edit_itemNo.setText(Util.nullString(data.getStringExtra("ITEM_CODE"), ""));
                edit_lot.setText(Util.nullString(data.getStringExtra("LOT_NUMBER"), ""));
                edit_qty.setText(Util.nullString(data.getStringExtra("PRIMARY_QUANTITY"), ""));
                text_uom.setText(Util.nullString(data.getStringExtra("PRIMARY_UOM_CODE"), ""));

                if (data.getStringExtra("LOCATOR_CONTROL").equals("1")) {
                    text_locFrom.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
                    text_locTo.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
                    text_locFrom.setText("");
                    text_locTo.setText("");
                    btn_locFrom.setBackground(ContextCompat.getDrawable(this, R.color.white));
                    btn_locTo.setBackground(ContextCompat.getDrawable(this, R.color.white));
                    btn_locFrom.setEnabled(false);
                    btn_locTo.setEnabled(false);

                } else {
                    text_locFrom.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input));
                    text_locTo.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input));
                    text_locFrom.setText(Util.nullString(data.getStringExtra("LOCATOR_CODE"), ""));
                    text_locTo.setText(Util.nullString(data.getStringExtra("TRSF_LOCATOR_CODE"), ""));
                    btn_locFrom.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_style));
                    btn_locTo.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_style));
                    btn_locFrom.setEnabled(true);
                    btn_locTo.setEnabled(true);
                }

                text_subInvFrom.setText(Util.nullString(data.getStringExtra("SUBINVENTORY_CODE"), ""));
                text_subInvTo.setText(Util.nullString(data.getStringExtra("TRSF_SUBINVENTORY_CODE"), ""));

                inventoryLocationId = Util.nullString(data.getStringExtra("INVENTORY_LOCATION_ID"), "");
                transferInvLocationId = Util.nullString(data.getStringExtra("TRSF_INV_LOCATION_ID"), "");

                org_code = Util.nullString(data.getStringExtra("ORGANIZATION_CODE"), "");
                OU_ID = Util.nullString(data.getStringExtra("OU_ID"), "");
                ORG_ID = Util.nullString(data.getStringExtra("ORG_ID"), "");
                palletId = Util.nullString(data.getStringExtra("PALLET_ID"), "");

                Log.v(Comm.LOG_TAG, "OU_ID: "+ OU_ID);
                Log.v(Comm.LOG_TAG, "ORG_ID: "+ ORG_ID);

                subinvList();

            } else if (requestCode == Comm.TRANITEM) {

                edit_itemNo.setText(Util.nullString(data.getStringExtra("ITEM_CODE"), ""));
                text_itemName.setText(Util.nullString(data.getStringExtra("ITEM_DESC"), ""));
                text_uom.setText(Util.nullString(data.getStringExtra("PRIMARY_UOM_CODE"), ""));

                edit_palletNo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input_ds));
                edit_palletNo.setEnabled(false);
                btn_pallNo.setEnabled(false);

                if (data.getStringExtra("LOT_CONTROL_CODE").equals("1")) {
                    edit_lot.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input_ds));
                    edit_lot.setEnabled(false);
                    btn_lot.setEnabled(false);

                } else {
                    edit_lot.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));
                    edit_lot.setEnabled(true);
                    btn_lot.setEnabled(true);
                }

                itemId = Util.nullString(data.getStringExtra("INVENTORY_ITEM_ID"), "");
                OU_ID = Util.nullString(data.getStringExtra("OU_ID"), "");
                ORG_ID = Util.nullString(data.getStringExtra("ORG_ID"), "");

                Log.v(Comm.LOG_TAG, "OU_ID: "+ OU_ID);
                Log.v(Comm.LOG_TAG, "ORG_ID: "+ ORG_ID);

            } else if (requestCode == Comm.TRANLOT) {
                edit_lot.setText(Util.nullString(data.getStringExtra("LOT_NUMBER"), ""));
                edit_qty.setText(Util.nullString(data.getStringExtra("PRIMARY_QUANTITY"), ""));
                text_subInvFrom.setText(Util.nullString(data.getStringExtra("SUBINVENTORY_CODE"), ""));

                if (data.getStringExtra("LOCATOR_CONTROL").equals("1")) {
                    text_locFrom.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
                    text_locFrom.setText("");
                    btn_locFrom.setEnabled(false);

                } else {
                    text_locFrom.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input));
                    text_locFrom.setText(Util.nullString(data.getStringExtra("LOCATOR_CODE"), ""));
                    btn_locFrom.setEnabled(true);
                }

                inventoryLocationId = Util.nullString(data.getStringExtra("INVENTORY_LOCATION_ID"), "");

            }
        }
    }

    OnClickListener onClickListener = new OnClickListener() {
        private Network network = null;

        @Override
        public void onClick(View arg0) {
            if (arg0.equals(btn_back)) {
                finish();

            } else if(arg0.equals(edit_qty)){
                edit_qty.setSelection(edit_qty.length());
                setPalletBackground();

            } else if(arg0.equals(edit_palletNo)){
                edit_palletNo.setSelection(edit_palletNo.length());
                setPalletBackground();

            } else if(arg0.equals(edit_itemNo)){
                edit_itemNo.setSelection(edit_itemNo.length());
                setPalletBackground();

            } else if(arg0.equals(edit_lot)){
                edit_lot.setSelection(edit_lot.length());
                setPalletBackground();

            } else if (arg0.equals(btn_subInvFrom)) {
                Intent intent = new Intent(getApplicationContext(), PopTranSubInvActivity.class);
                intent.putExtra("INV_ID", itemId);
                intent.putExtra("PALLET_ID", palletId);
                intent.putExtra("LOT_NUMBER", edit_lot.getText().toString());

                startActivityForResult(intent, Comm.SUBINV_FROM);

            } else if (arg0.equals(btn_subInvTo)) {
                Intent intent = new Intent(getApplicationContext(), PopSubInvActivity.class);
                intent.putExtra("ORG_ID", ORG_ID);
                startActivityForResult(intent, Comm.SUBINV_TO);

            } else if (arg0.equals(btn_locFrom)) {
                Intent intent = new Intent(getApplicationContext(), PopTranLocaActivity.class);
                intent.putExtra("INV_ID", itemId);
                intent.putExtra("PALLET_ID", palletId);
                intent.putExtra("LOT_NUMBER", edit_lot.getText().toString());

                startActivityForResult(intent, Comm.LOC_FROM);

            } else if (arg0.equals(btn_locTo)) {
                Intent intent = new Intent(getApplicationContext(), PopLocaActivity.class);
                intent.putExtra("SUBINVENTORY_CODE", text_subInvTo.getText().toString());
                intent.putExtra("ORG_ID", ORG_ID);
                startActivityForResult(intent, Comm.LOC_TO);

            } else if (arg0.equals(btn_pallNo)) {

                String searchKey = edit_palletNo.getText().toString();

                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                param.put("orgId", Util.nullString(ORG_ID, ""));
                param.put("searchPallet", Util.nullString(searchKey, ""));

                network = new Network(MoveAllcInvActivity.this, listener, "/api/inventory/invenPalletList");
                network.execute(param);

            } else if (arg0.equals(btn_itemNo)) {

                String searchKey = edit_itemNo.getText().toString();

                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                param.put("orgId", Util.nullString(ORG_ID, ""));
                param.put("searchCd", Util.nullString(searchKey, ""));

                network = new Network(MoveAllcInvActivity.this, listener, "/api/inventory/invenItemList");
                network.execute(param);

            } else if (arg0.equals(btn_lot)) {

                String searchKey = edit_lot.getText().toString();

                if(Util.isNull(itemId)){

                    AlertDialog.Builder builder = new AlertDialog.Builder(MoveAllcInvActivity.this);
                    builder
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.alert_no_item_id))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }else{

                    LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                    param.put("orgId", Util.nullString(ORG_ID, ""));
                    param.put("inventoryItemId", Util.nullString(itemId, ""));
                    param.put("searchLot", Util.nullString(searchKey, ""));

                    network = new Network(MoveAllcInvActivity.this, listener, "/api/inventory/invenLotList");
                    network.execute(param);

                }

            } else if (arg0.equals(btn_date)) {
                DatePickerDialog datepicker = new DatePickerDialog(MoveAllcInvActivity.this, mPickSetListener, pYear, pMonth, pDay);
                datepicker.getDatePicker().setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                datepicker.show();

            } else if (arg0.equals(text_date)) {
                DatePickerDialog datepicker = new DatePickerDialog(MoveAllcInvActivity.this, mPickSetListener, pYear, pMonth, pDay);
                datepicker.getDatePicker().setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                datepicker.show();

            } else if (arg0.equals(layout_from)) {
                layout_from.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.location_layout));
                layout_to.setBackgroundColor(getResources().getColor(R.color.white));
                layout_pallet.setBackgroundColor(getResources().getColor(R.color.white));
                selScan = "F";
            } else if (arg0.equals(layout_to)) {
                layout_to.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.location_layout));
                layout_from.setBackgroundColor(getResources().getColor(R.color.white));
                layout_pallet.setBackgroundColor(getResources().getColor(R.color.white));
                selScan = "T";

            } else if (arg0.equals(layout_pallet)) {
                layout_to.setBackgroundColor(getResources().getColor(R.color.white));
                layout_from.setBackgroundColor(getResources().getColor(R.color.white));
                layout_pallet.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.location_layout));
                text_subInvFrom.setText("");
                text_subInvTo.setText("");
                text_locFrom.setText("");
                text_locTo.setText("");
                selScan = "M";

            } else if (arg0.equals(btn_move)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MoveAllcInvActivity.this);
                builder
                        .setTitle(getString(R.string.decision))
                        .setMessage(getString(R.string.alert_set_subtran))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

                                if(Util.isNull(edit_palletNo.getText().toString())){
                                    param.put("itemCode", Util.nullString(edit_itemNo.getText().toString(), ""));
                                    param.put("uomCode", Util.nullString(text_uom.getText().toString(), ""));
                                    param.put("lotNumber", Util.nullString(edit_lot.getText().toString(), ""));
                                    param.put("transferQty", Util.nullString(edit_qty.getText().toString(), ""));
                                }else{
                                    param.put("itemCode", "");
                                    param.put("uomCode", "");
                                    param.put("lotNumber", "");
                                    param.put("transferQty", "");
                                }

                                param.put("orgCode", Util.nullString(org_code, ""));
                                param.put("subinventoryCode", Util.nullString(text_subInvFrom.getText().toString(), ""));
                                param.put("inventoryLocationId", Util.nullString(inventoryLocationId, ""));
                                param.put("palletId", Util.nullString(palletId, ""));
                                param.put("transferSubinventory", Util.nullString(text_subInvTo.getText().toString(), ""));
                                param.put("transferInvLocationId", Util.nullString(transferInvLocationId, ""));
                                param.put("ouId", Util.nullString(OU_ID, ""));
                                param.put("orgId", Util.nullString(ORG_ID, ""));
                                param.put("moveDate", Util.nullString(text_date.getText().toString(), ""));
                                param.put("userNo", Util.nullString(S_USER_ID, ""));

                                new setSubTranTask().execute(param);
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();
            }
        }
    };

    private DatePickerDialog.OnDateSetListener mPickSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            pYear = year;
            pMonth = monthOfYear;
            pDay = dayOfMonth;

            DecimalFormat mmddFormat = new DecimalFormat("00");
            DecimalFormat yyyyFormat = new DecimalFormat("0000");
            String result = mmddFormat.format(pMonth + 1) + "-" + mmddFormat.format(pDay) + "-" + yyyyFormat.format(pYear);
            text_date.setText(result);
        }
    };

    private void setDateFormat(int pYear, int pMonth, int pDay) {
        DecimalFormat mmddFormat = new DecimalFormat("00");
        DecimalFormat yyyyFormat = new DecimalFormat("0000");
        String result = mmddFormat.format(pMonth + 1) + "-" + mmddFormat.format(pDay) + "-" + yyyyFormat.format(pYear);

        text_date.setText(result);
    };

    private void subinvList(){
        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
        param.put("orgId", Util.nullString(ORG_ID, ""));
        param.put("inventoryItemId", Util.nullString(itemId, ""));
        param.put("palletId", Util.nullString(palletId, ""));
        param.put("lotNumber", Util.nullString(edit_lot.getText().toString(), ""));

        this.network = new Network(this, listener, "/api/inventory/invenSubinvList");
        this.network.execute(param);
    }

//  [[ SCANNER CODE

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Scan != null) {
            Scan.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Scan != null) {
            Scan.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Scan != null) {
            Scan.onPause();
        }
    }

    MyEvent myListener = new MyEvent() {


        @Override
        public void onEvent(String data) {

            Log.v(Comm.LOG_TAG, "MyEvent:"+data);

            String[] code = data.split(";");

            if (code.length == 4 && code[0].equals("L")) {
                if (selScan.equals("F")) {
                    scanSubInv = code[2];

                    LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                    param.put("orgId", Util.nullString(ORG_ID, ""));
                    param.put("searchLocator", Util.nullString(code[3], ""));
                    param.put("palletId", Util.nullString(palletId, ""));
                    param.put("inventoryItemId", Util.nullString(inventoryLocationId, ""));
                    param.put("lotNumber", Util.nullString(edit_lot.getText().toString(), ""));
                    param.put("searchSubinv", Util.nullString(code[2], ""));

                    new LocatorFromScanListTask().execute(param);

                }else if (selScan.equals("T")) {
                    scanSubInv = code[2];

                    LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                    param.put("orgId", Util.nullString(ORG_ID, ""));

                    if(checkSubinv()) {
                        param.put("subinventoryCode", text_subInvTo.getText().toString());
                    }else{
                        param.put("subinventoryCode", Util.nullString(code[2], ""));
                    }
                    param.put("searchLocator", Util.nullString(code[3], ""));
                    param.put("searchPallet", Util.nullString(edit_palletNo.getText().toString(), ""));
                    param.put("searchSubinv", Util.nullString(code[2], ""));

                    new LocatorToScanListTask().execute(param);
                }
            }else if(code.length == 2){
                if(code[0].equals("A")) {
                    if (code[1].equals("MOVE")) {
                        MoveAllcInvActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

                                if (Util.isNull(edit_palletNo.getText().toString())) {
                                    param.put("itemCode", Util.nullString(edit_itemNo.getText().toString(), ""));
                                    param.put("uomCode", Util.nullString(text_uom.getText().toString(), ""));
                                    param.put("lotNumber", Util.nullString(edit_lot.getText().toString(), ""));
                                    param.put("transferQty", Util.nullString(edit_qty.getText().toString(), ""));
                                } else {
                                    param.put("itemCode", "");
                                    param.put("uomCode", "");
                                    param.put("lotNumber", "");
                                    param.put("transferQty", "");
                                }

                                param.put("orgCode", Util.nullString(org_code, ""));
                                param.put("subinventoryCode", Util.nullString(text_subInvFrom.getText().toString(), ""));
                                param.put("inventoryLocationId", Util.nullString(inventoryLocationId, ""));
                                param.put("palletId", Util.nullString(palletId, ""));
                                param.put("transferSubinventory", Util.nullString(text_subInvTo.getText().toString(), ""));
                                param.put("transferInvLocationId", Util.nullString(transferInvLocationId, ""));
                                param.put("ouId", Util.nullString(OU_ID, ""));
                                param.put("orgId", Util.nullString(ORG_ID, ""));
                                param.put("moveDate", Util.nullString(text_date.getText().toString(), ""));
                                param.put("userNo", Util.nullString(S_USER_ID, ""));

                                new setSubTranTask().execute(param);
                            }
                        });
                    }

                }
            }else if(code.length == 1){

                boolean check = checkBarcode(data);

                if (!check) {

                    LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                    //param.put("orgId", Util.nullString(ORG_ID, ""));
                    param.put("orgId", "");
                    param.put("searchPallet", Util.nullString(data, ""));

                    new PalletScanListTask().execute(param);

                }
            }
        }
    };

    private boolean checkSubinv(){
        if(text_subInvFrom.getText().toString().equals("W02")
                || text_subInvFrom.getText().toString().equals("W12")
                || text_subInvFrom.getText().toString().equals("W13")
                || text_subInvFrom.getText().toString().equals("W14")) {
            text_subInvTo.setText(text_subInvFrom.getText().toString());
            return true;
        }else{
            return false;
        }

    }

    private boolean checkBarcode(String data) {
        if (!data.matches("[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝]*")) {
            return true;
        } else {
            return false;
        }
    }
// ]] SCANNER CODE

    private class PalletScanListTask extends AsyncTask<LinkedHashMap<String, String>, Void, String> {
        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL + "/api/inventory/palletScanList");
            http.addAllParameters(params[0]);
            HttpClient post = http.create();
            post.request();

            int statusCode = post.getHttpStatusCode();
            Log.v(Comm.LOG_TAG, "statusCode: " + statusCode);
            String body = post.getBody();
            Log.v(Comm.LOG_TAG, "body: " + body);
            return body;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject obj = new JSONObject(result);

                JSONArray list = obj.getJSONArray("list");

                checkList(list);

                for (int i = 0; i < list.length(); i++) {
                    JSONObject data = list.getJSONObject(i);
                    edit_palletNo.setText(Util.nullString(data.getString("PALLET_NUMBER"), ""));
                    edit_itemNo.setText(Util.nullString(data.getString("ITEM_CODE"), ""));
                    edit_lot.setText(Util.nullString(data.getString("LOT_NUMBER"), ""));
                    edit_qty.setText(Util.nullString(data.getString("PRIMARY_QUANTITY"), ""));
                    text_uom.setText(Util.nullString(data.getString("PRIMARY_UOM_CODE"), ""));

                    text_subInvFrom.setText(Util.nullString(data.getString("SUBINVENTORY_CODE"), ""));
                    text_subInvTo.setText(Util.nullString(data.getString("TRSF_SUBINVENTORY_CODE"), ""));

                    if (data.getString("LOCATOR_CONTROL").equals("1")) {
                        text_locFrom.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input_ds));
                        text_locTo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input_ds));
                        text_locFrom.setText("");
                        text_locTo.setText("");
                        inventoryLocationId = "";
                        transferInvLocationId = "";
                        btn_locFrom.setEnabled(false);
                        btn_locTo.setEnabled(false);

                    } else {
                        text_locFrom.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));
                        text_locTo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));
                        text_locFrom.setText(Util.nullString(data.getString("LOCATOR_CODE"), ""));
                        text_locTo.setText(Util.nullString(data.getString("TRSF_LOCATOR_CODE"), ""));
                        inventoryLocationId = Util.nullString(data.getString("INVENTORY_LOCATION_ID"), "");
                        transferInvLocationId = Util.nullString(data.getString("TRSF_INV_LOCATION_ID"), "");
                        btn_locFrom.setEnabled(true);
                        btn_locTo.setEnabled(true);
                    }

                    org_code = Util.nullString(data.getString("ORGANIZATION_CODE"), "");
                    OU_ID = Util.nullString(data.getString("OU_ID"), "");
                    ORG_ID = Util.nullString(data.getString("ORGANIZATION_ID"), "");
                    palletId = Util.nullString(data.getString("PALLET_ID"), "");

                    Log.v(Comm.LOG_TAG, "OU_ID: "+ OU_ID);
                    Log.v(Comm.LOG_TAG, "ORG_ID: "+ ORG_ID);

                    subinvList();
                }

                if(list.length() != 0){
                    layout_to.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.location_layout));
                    layout_from.setBackgroundColor(getResources().getColor(R.color.white));
                    layout_pallet.setBackgroundColor(getResources().getColor(R.color.white));
                    selScan = "T";
                }

                Log.v(Comm.LOG_TAG, "selScan" + selScan);

            } catch (JSONException e) {
                Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
            } catch (Exception e1) {
                Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
            }
        }
    }

    private class LocatorFromScanListTask extends AsyncTask<LinkedHashMap<String, String>, Void, String> {
        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL + "/api/inventory/locatorFromScanList");
            http.addAllParameters(params[0]);
            HttpClient post = http.create();
            post.request();

            int statusCode = post.getHttpStatusCode();
            Log.v(Comm.LOG_TAG, "statusCode: " + statusCode);
            String body = post.getBody();
            Log.v(Comm.LOG_TAG, "body: " + body);
            return body;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject obj = new JSONObject(result);

                JSONArray list = obj.getJSONArray("list");
                JSONArray list1 = obj.getJSONArray("list1");

                if (list1.length() == 1) {
                    if(list.length() == 0){

                        AlertDialog.Builder builder = new AlertDialog.Builder(MoveAllcInvActivity.this);
                        builder
                                .setTitle(getString(R.string.note))
                                .setMessage(getString(R.string.alert_no_item))
                                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent(getApplicationContext(), PopTranLocaActivity.class);
                                        intent.putExtra("INV_ID", itemId);
                                        intent.putExtra("PALLET_ID", palletId);
                                        intent.putExtra("LOT_NUMBER", edit_lot.getText().toString());
                                        intent.putExtra("ORG_ID", ORG_ID);

                                        startActivityForResult(intent, Comm.LOC_FROM);
                                    }
                                })
                                .show();
                    }else{
                        JSONObject data1 = list1.getJSONObject(0);

                        text_subInvFrom.setText(Util.nullString(data1.getString("SUBINVENTORY_CODE"), ""));

                        if (data1.getString("LOCATOR_CONTROL").equals("1")) {
                            text_locFrom.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input_ds));
                            text_locFrom.setText("");
                            btn_locFrom.setEnabled(false);

                        } else {
                            text_locFrom.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));

                            if (list.length() == 1) {
                                JSONObject data = list.getJSONObject(0);

                                text_locFrom.setText(Util.nullString(data.getString("LOCATOR_CODE"), ""));
                                inventoryLocationId = Util.nullString(data.getString("INVENTORY_LOCATION_ID"), "");
                                btn_locFrom.setEnabled(true);
                            } else {
                                text_locFrom.setText("");
                                inventoryLocationId = "";
                            }
                        }
                    }
                }else if(list1.length() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MoveAllcInvActivity.this);
                    builder
                            .setTitle(getString(R.string.note))
                            .setMessage(getString(R.string.alert_no_item))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(getApplicationContext(), PopTranSubInvActivity.class);
                                    intent.putExtra("INV_ID", itemId);
                                    intent.putExtra("PALLET_ID", palletId);
                                    intent.putExtra("LOT_NUMBER", edit_lot.getText().toString());
                                    intent.putExtra("ORG_ID", ORG_ID);

                                    startActivityForResult(intent, Comm.SUBINV_FROM);
                                }
                            })
                            .show();
                }
            } catch (JSONException e) {
                Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
            } catch (Exception e1) {
                Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
            }
        }
    }

    private class LocatorToScanListTask extends AsyncTask<LinkedHashMap<String, String>, Void, String> {
        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL + "/api/inventory/locatorScanList");
            http.addAllParameters(params[0]);
            HttpClient post = http.create();
            post.request();

            int statusCode = post.getHttpStatusCode();
            Log.v(Comm.LOG_TAG, "statusCode: " + statusCode);
            String body = post.getBody();
            Log.v(Comm.LOG_TAG, "body: " + body);
            return body;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject obj = new JSONObject(result);

                JSONArray list = obj.getJSONArray("list");
                JSONArray list1 = obj.getJSONArray("list1");

                if (list1.length() == 1) {
                    if(list.length() == 0){

                        AlertDialog.Builder builder = new AlertDialog.Builder(MoveAllcInvActivity.this);
                        builder
                                .setTitle(getString(R.string.note))
                                .setMessage(getString(R.string.alert_no_item))
                                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent(getApplicationContext(), PopLocaActivity.class);

                                        intent.putExtra("SUBINVENTORY_CODE", scanSubInv);
                                        intent.putExtra("ORG_ID", ORG_ID);

                                        startActivityForResult(intent, Comm.LOC_TO);
                                    }
                                })
                                .show();
                    }else{
                        JSONObject data1 = list1.getJSONObject(0);
                        if(checkSubinv()){

                        }else {
                            text_subInvTo.setText(Util.nullString(data1.getString("SUBINVENTORY_CODE"), ""));
                        }
                        if (Util.nullString(data1.getString("LOCATOR_CONTROL"), "").equals("1")) {
                            text_locTo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input_ds));
                            text_locTo.setText("");
                            btn_locTo.setEnabled(false);

                        } else {
                            text_locTo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));

                            if (list.length() == 1) {
                                JSONObject data = list.getJSONObject(0);

                                text_locTo.setText(Util.nullString(data.getString("LOCATOR_CODE"), ""));
                                transferInvLocationId = Util.nullString(data.getString("INVENTORY_LOCATION_ID"), "");
                                btn_locTo.setEnabled(true);
                            } else {
                                text_locTo.setText("");
                                transferInvLocationId = "";
                            }
                        }
                    }

                }else if(list1.length() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MoveAllcInvActivity.this);
                    builder
                            .setTitle(getString(R.string.note))
                            .setMessage(getString(R.string.alert_no_item))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(getApplicationContext(), PopSubInvActivity.class);
                                    intent.putExtra("ORG_ID", ORG_ID);

                                    startActivityForResult(intent, Comm.SUBINV_TO);
                                }
                            })
                            .show();
                }

            } catch (JSONException e) {
                Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
            } catch (Exception e1) {
                Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
            }
        }
    }

    private class setSubTranTask extends AsyncTask<LinkedHashMap<String, String>, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MoveAllcInvActivity.this, "", getString(R.string.progressing));
        }

        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL + "/api/inventory/setSubInven");
            http.addAllParameters(params[0]);
            HttpClient post = http.create();
            post.request();

            int statusCode = post.getHttpStatusCode();
            Log.v(Comm.LOG_TAG, "statusCode: " + statusCode);
            String body = post.getBody();
            Log.v(Comm.LOG_TAG, "body: " + body);
            return body;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject obj = new JSONObject(result);

                if (!obj.getString("status").equals("S")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MoveAllcInvActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage("Move Fail \n\n" + obj.getString("msg"))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MoveAllcInvActivity.this)
                            .setTitle(getString(R.string.note))
                            .setMessage(getString(R.string.alert_move_comp))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    initTran();
                                    setPalletBackground();

                                }
                            });
                    builder.show();
                }

            } catch (JSONException e) {
                Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
            } catch (Exception e1) {
                Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
            }

            progressDialog.dismiss();
        }
    }

    NetworkEvent listener = new NetworkEvent() {

        @Override
        public void doInBackground(LinkedHashMap<String, String>... params) {

        }

        @Override
        public void onPostExecute(JSONObject obj, String api) {

            if (api.equals("/api/inventory/invenPalletList")) {
                try{
                    JSONArray list = obj.getJSONArray("list");

                    if (list.length() == 1) {
                        JSONObject data = list.getJSONObject(0);

                        edit_palletNo.setText(Util.nullString(data.getString("PALLET_NUMBER"), ""));

                        edit_itemNo.setText(Util.nullString(data.getString("ITEM_CODE"), ""));
                        edit_lot.setText(Util.nullString(data.getString("LOT_NUMBER"), ""));
                        edit_qty.setText(Util.nullString(data.getString("PRIMARY_QUANTITY"), ""));
                        text_uom.setText(Util.nullString(data.getString("PRIMARY_UOM_CODE"), ""));

                        if (data.getString("LOCATOR_CONTROL").equals("1")) {
                            text_locFrom.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input_ds));
                            text_locTo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input_ds));
                            text_locFrom.setText("");
                            text_locTo.setText("");
                            btn_locFrom.setEnabled(false);
                            btn_locTo.setEnabled(false);
                            btn_locFrom.setBackgroundColor(getResources().getColor(R.color.white));
                            btn_locTo.setBackgroundColor(getResources().getColor(R.color.white));

                        } else {
                            text_locFrom.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));
                            text_locTo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));
                            text_locFrom.setText(Util.nullString(data.getString("LOCATOR_CODE"), ""));
                            text_locTo.setText(Util.nullString(data.getString("TRSF_LOCATOR_CODE"), ""));
                            btn_locFrom.setEnabled(true);
                            btn_locTo.setEnabled(true);
                            btn_locFrom.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.layout_style));
                            btn_locTo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.layout_style));
                        }

                        text_subInvFrom.setText(Util.nullString(data.getString("SUBINVENTORY_CODE"), ""));
                        text_subInvTo.setText(Util.nullString(data.getString("TRSF_SUBINVENTORY_CODE"), ""));

                        org_code = Util.nullString(data.getString("ORGANIZATION_CODE"), "");
                        palletId = Util.nullString(data.getString("PALLET_ID"), "");
                        inventoryLocationId = Util.nullString(data.getString("INVENTORY_LOCATION_ID"), "");
                        transferInvLocationId = Util.nullString(data.getString("TRSF_INV_LOCATION_ID"), "");

                    } else {
                        Intent intent = new Intent(getApplicationContext(), PopTranPallActivity.class);
                        intent.putExtra("PALL_NO", Util.nullString(edit_palletNo.getText().toString(), ""));
                        startActivityForResult(intent, Comm.TRANPALLET);
                    }

                }catch (Exception e){Log.v(Comm.LOG_TAG, "Exception Error ::"+e.getMessage());}

            }else if(api.equals("/api/inventory/invenItemList")){
                try{
                    JSONArray list = obj.getJSONArray("list");

                    if (list.length() == 1) {
                        JSONObject data = list.getJSONObject(0);

                        edit_itemNo.setText(Util.nullString(data.getString("ITEM_CODE"), ""));
                        text_itemName.setText(Util.nullString(data.getString("ITEM_DESC"), ""));
                        text_uom.setText(Util.nullString(data.getString("PRIMARY_UOM_CODE"), ""));

                        edit_palletNo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input_ds));
                        edit_palletNo.setEnabled(false);
                        btn_pallNo.setEnabled(false);

                        if (data.getString("LOT_CONTROL_CODE").equals("1")) {
                            edit_lot.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input_ds));
                            edit_lot.setEnabled(false);
                            btn_lot.setEnabled(false);

                        } else {
                            edit_lot.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));
                            edit_lot.setEnabled(true);
                            btn_lot.setEnabled(true);
                        }

                        itemId = Util.nullString(data.getString("INVENTORY_ITEM_ID"), "");
                        Log.v(Comm.LOG_TAG, "itemId" + itemId);

                    } else {
                        Intent intent = new Intent(getApplicationContext(), PopTranItemActivity.class);
                        intent.putExtra("ITEM_NO", Util.nullString(edit_itemNo.getText().toString(), ""));
                        startActivityForResult(intent, Comm.TRANITEM);
                    }

                }catch (Exception e){Log.v(Comm.LOG_TAG, "Exception Error ::"+e.getMessage());}
            }else if(api.equals("/api/inventory/invenLotList")){
                try{
                    JSONArray list = obj.getJSONArray("list");

                    if (list.length() == 1) {
                        JSONObject data = list.getJSONObject(0);

                        edit_lot.setText(Util.nullString(data.getString("LOT_NUMBER"), ""));
                        edit_qty.setText(Util.nullString(data.getString("PRIMARY_QUANTITY"), ""));

                        if (data.getString("LOCATOR_CONTROL").equals("1")) {
                            text_locFrom.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input_ds));
                            text_locTo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input_ds));
                            text_locFrom.setText("");
                            text_locTo.setText("");
                            btn_locFrom.setEnabled(false);
                            btn_locTo.setEnabled(false);

                        } else {
                            text_locFrom.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));
                            text_locTo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));
                            text_locFrom.setText(Util.nullString(data.getString("LOCATOR_CODE"), ""));
                            text_locTo.setText(Util.nullString(data.getString("TRSF_LOCATOR_CODE"), ""));
                            btn_locFrom.setEnabled(true);
                            btn_locTo.setEnabled(true);
                        }

                        text_subInvFrom.setText(Util.nullString(data.getString("SUBINVENTORY_CODE"), ""));

                        org_code = Util.nullString(data.getString("ORGANIZATION_CODE"), "");
                        palletId = Util.nullString(data.getString("PALLET_ID"), "");
                        inventoryLocationId = Util.nullString(data.getString("INVENTORY_LOCATION_ID"), "");

                    } else {
                        Intent intent = new Intent(getApplicationContext(), PopTranLotActivity.class);
                        intent.putExtra("ITEM_ID", Util.nullString(itemId, ""));

                        startActivityForResult(intent, Comm.TRANLOT);
                    }
                }catch (Exception e){
                    Log.v(Comm.LOG_TAG, "Exception Error ::"+e.getMessage());
                }

            }else if(api.equals("/api/inventory/invenSubinvList")){
                try{
                    JSONArray list = obj.getJSONArray("list");

                    if (list.length() == 1) {
                        JSONObject data = list.getJSONObject(0);
                        //statusId = Util.nullString(data.getString("STATUS_ID"), "");
                        if(Util.nullString(data.getString("STATUS_ID"), "").equals("1")){
                            text_subInvTo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));
                            btn_subInvTo.setEnabled(true);
                            btn_subInvTo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.layout_style));
                            btn_locTo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.layout_style));
                            btn_locFrom.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.layout_style));
                        }else{
                            text_subInvTo.setText(Util.nullString(data.getString("SUBINVENTORY_CODE"), ""));
                            text_subInvTo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input_ds));
                            btn_subInvTo.setEnabled(false);
                            btn_subInvTo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.color.white));
                            btn_locTo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.color.white));
                            btn_locFrom.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.color.white));
                        }
                    }

                }catch (Exception e){
                    Log.v(Comm.LOG_TAG, "Exception Error ::"+e.getMessage());
                }
            }
        }
    };

    private void checkList(JSONArray list) {
        if (list.length() == 0) {

            initTran();
            setPalletBackground();

            AlertDialog.Builder builder = new AlertDialog.Builder(MoveAllcInvActivity.this);
            builder
                    .setTitle(getString(R.string.note))
                    .setMessage(getString(R.string.alert_no_item))
                    .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .show();
        }
    }

    private void setPalletBackground(){
        layout_pallet.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.location_layout));
        layout_to.setBackgroundColor(getResources().getColor(R.color.white));
        layout_from.setBackgroundColor(getResources().getColor(R.color.white));
    }

    private void initTran(){
        edit_palletNo.setText("");
        edit_itemNo.setText("");
        text_itemName.setText("");
        edit_qty.setText("");
        text_uom.setText("");
        edit_lot.setText("");
        text_subInvFrom.setText("");
        text_subInvTo.setText("");
        text_locTo.setText("");
        text_locFrom.setText("");

        transferInvLocationId = "";
        inventoryLocationId = "";
        selScan = "M";

        edit_itemNo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));
        edit_lot.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));
        edit_qty.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));
        edit_palletNo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));
        text_locTo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));
        text_locFrom.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));
        text_subInvTo.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));
        text_subInvFrom.setBackground(ContextCompat.getDrawable(MoveAllcInvActivity.this, R.drawable.bg_input));
        edit_itemNo.setEnabled(true);
        edit_lot.setEnabled(true);
        edit_qty.setEnabled(true);
        edit_palletNo.setEnabled(true);
        btn_itemNo.setEnabled(true);
        btn_lot.setEnabled(true);
        btn_pallNo.setEnabled(true);
    }
}


