package com.pulmuone.mrtina.batchComp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pulmuone.mrtina.R;
import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.comm.HttpClient;
import com.pulmuone.mrtina.labelPalt.PalletLabelActivity;
import com.pulmuone.mrtina.popupLoca.PopLocaActivity;
import com.pulmuone.mrtina.popupProdSubInv.PopProdSubInvActivity;
import com.pulmuone.mrtina.utils.DemoSleeper;
import com.pulmuone.mrtina.utils.SettingsHelper;
import com.pulmuone.mrtina.utils.Util;

import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.ZebraPrinterLinkOs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;

public class BatchCompActivity extends Activity {

    SharedPreferences pref = null;

    private String S_USER_ID = null;
    private String ORG_ID = null;
    private String OU_ID = null;

    private String batchId = null;
    private String orgCode = null;
    private String transactionType = null;
    private String subinventoryCode = null;
    private String inventoryLocationId = null;
    private String plTxnQty = null;
    private String plConRate = null;
    private String itemId = null;
    private String reasonId = null;
    private String materialDetailId = null;
    private String transactionId = null;
    private String palletId = null;
    private String pallet_con = null;
    private String sku_uom = null;
    private String primaryTxnQty = null;
    private String perPallet = null;

    private int pYear, pMonth, pDay;
    private int pYear2, pMonth2, pDay2;

    private TextView text_batch = null;
    private TextView text_pallet = null;
    private TextView text_item = null;
    private TextView text_item_name = null;
    private TextView text_recipe = null;
    private TextView text_recipe_ver = null;
    private TextView text_plan_qty = null;
    private TextView text_date = null;
    private TextView text_tran_date = null;
    private TextView text_uom1 = null;
    private TextView text_uom2 = null;
    private TextView text_subinv = null;
    private TextView text_loc = null;
    private EditText edit_transact_qty = null;
    private EditText edit_lot = null;

    private LinearLayout btn_subInv = null;
    private LinearLayout btn_loc = null;
    private LinearLayout btn_date = null;
    private RelativeLayout btn_close = null;
    private LinearLayout btn_tran_date = null;
    private Button btn_comp = null;

    private ProgressDialog progressDialog;

    private ZebraPrinter printer;
    private Connection connection;

    int mPariedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;

    BluetoothAdapter mBluetoothAdapter;
    boolean tcpYn = true;

    private String L_PALLET_NUMBER = "";
    private String L_ITEM_CODE = "";
    private String L_ITEM_DESC = "";
    private String L_LOT_NUMBER = "";
    private String L_EXPIRATION_DATE = "";
    private String L_BATCH_NO = "";
    private String L_PRINT_TYPE = "";
    private String L_PRINT_LIST = "";
    JSONArray printList = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_comp);

        Intent intent = getIntent();

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);
        S_USER_ID = pref.getString("USER_ID", "");

        // 라벨 인쇄용 변수
        L_PRINT_TYPE        = intent.getStringExtra("PRINT_TYPE");
        L_PALLET_NUMBER     = intent.getStringExtra("PALLET_NUMBER");
        L_ITEM_CODE         = intent.getStringExtra("ITEM_CODE");
        L_ITEM_DESC         = intent.getStringExtra("ITEM_DESC");
        L_LOT_NUMBER        = intent.getStringExtra("LOT_NUMBER");
        L_EXPIRATION_DATE   = intent.getStringExtra("EXPIRATION_DATE");
        L_BATCH_NO          = intent.getStringExtra("BATCH_NO");
        L_PRINT_LIST        = intent.getStringExtra("PRINT_LIST");
        try{
            printList = new JSONArray(L_PRINT_LIST);
        } catch (JSONException e) {
            Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
        } catch (Exception e1) {
            Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
        }

        btn_subInv = (LinearLayout) findViewById(R.id.layout_search_sub_inv);
        btn_loc = (LinearLayout) findViewById(R.id.layout_search_loc);
        btn_tran_date = (LinearLayout) findViewById(R.id.layout_tran_date);
        btn_date = (LinearLayout) findViewById(R.id.layout_exp_date);
        btn_close = (RelativeLayout) findViewById(R.id.layout_close);
        btn_comp = (Button) findViewById(R.id.btn_comp);

        text_batch = (TextView) findViewById(R.id.txt_batch_no);
        text_pallet = (TextView) findViewById(R.id.txt_pall_no);
        text_item = (TextView) findViewById(R.id.txt_item_no);
        text_item_name = (TextView) findViewById(R.id.txt_item_name);
        text_recipe = (TextView) findViewById(R.id.txt_recipe_no);
        text_recipe_ver = (TextView) findViewById(R.id.txt_recipe_ver);
        text_plan_qty = (TextView) findViewById(R.id.txt_planned_qty);
        text_tran_date = (TextView) findViewById(R.id.rel_date);
        text_date = (TextView) findViewById(R.id.txt_date);
        text_uom1 = (TextView) findViewById(R.id.txt_uom);
        text_uom2 = (TextView) findViewById(R.id.txt_uom_2);
        text_subinv = (TextView) findViewById(R.id.txt_sub_inv);
        text_loc = (TextView) findViewById(R.id.txt_loc);
        edit_transact_qty = (EditText) findViewById(R.id.edit_tran_qty);
        edit_lot = (EditText) findViewById(R.id.edit_lot_number);

        text_batch.setText(intent.getStringExtra("BATCH_NO"));
        text_pallet.setText(intent.getStringExtra("PALLET_NUMBER"));
        text_item.setText(intent.getStringExtra("ITEM_CODE"));
        text_item_name.setText(intent.getStringExtra("ITEM_DESC"));
        text_recipe.setText(intent.getStringExtra("RECIPE_NO"));
        text_recipe_ver.setText(intent.getStringExtra("RECIPE_VERSION"));
        text_plan_qty.setText(intent.getStringExtra("PL_PLAN_QTY"));
        text_tran_date.setText(intent.getStringExtra("DUE_DATE"));
        text_date.setText(intent.getStringExtra("EXPIRATION_DATE"));
        text_uom1.setText(intent.getStringExtra("PL_SKU_UOM"));
        text_uom2.setText(intent.getStringExtra("PL_SKU_UOM"));
        edit_transact_qty.setText(Util.nullString(intent.getStringExtra("PL_TXN_QTY"),"0"));// 2020-11-09
        text_subinv.setText(intent.getStringExtra("SUBINVENTORY_CODE"));
        OU_ID = Util.nullString(intent.getStringExtra("OU_ID"), "");
        ORG_ID = Util.nullString(intent.getStringExtra("ORG_ID"), "");


        Log.v(Comm.LOG_TAG, "OU_ID: "+ OU_ID);
        Log.v(Comm.LOG_TAG, "ORG_ID: "+ ORG_ID);
        Log.v(Comm.LOG_TAG, "DUE_DATE: "+ intent.getStringExtra("DUE_DATE"));

        edit_transact_qty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_transact_qty.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_lot.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_lot.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_transact_qty.setOnClickListener(onClickListener);
        edit_lot.setOnClickListener(onClickListener);

        if(intent.getStringExtra("LOT_CONTROL_CODE").equals("2")){
            edit_lot.setText(intent.getStringExtra("LOT_NUMBER"));
            edit_lot.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input));

        }else{
            edit_lot.setText("");
            edit_lot.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));

        }

        if(intent.getStringExtra("LOCATOR_CONTROL").equals("2")){
            text_loc.setText(intent.getStringExtra("LOCATOR_CODE"));
            text_loc.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input));
            text_loc.setFocusable(true);
            text_loc.setEnabled(true);

        }else {
            text_loc.setText("");
            text_loc.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
            text_loc.setFocusable(false);
            text_loc.setEnabled(false);
        }

        batchId = intent.getStringExtra("BATCH_ID");
        orgCode = intent.getStringExtra("ORGANIZATION_CODE");
        transactionType = intent.getStringExtra("TRANSAC_TYPE");
        inventoryLocationId = intent.getStringExtra("INVENTORY_LOCATION_ID");
        plTxnQty = intent.getStringExtra("PL_TXN_QTY");
        plConRate = intent.getStringExtra("PL_CON_RATE");
        reasonId = intent.getStringExtra("REASON_ID");
        materialDetailId = intent.getStringExtra("MATERIAL_DETAIL_ID");
        transactionId = intent.getStringExtra("TRANSACTION_ID");
        palletId = intent.getStringExtra("PALLET_ID");
        pallet_con = intent.getStringExtra("PALLET_CONTROL");
        sku_uom = intent.getStringExtra("PL_SKU_UOM");
        itemId = intent.getStringExtra("INVENTORY_ITEM_ID");
        primaryTxnQty = Util.nullString(intent.getStringExtra("PRIMARY_TXN_QTY"), "0");
        perPallet  = Util.nullString(intent.getStringExtra("PER_PALLET"), "0"); // 2020-11-09

        if(transactionType.equals("RETURN")){
            text_date.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
            edit_lot.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
            text_subinv.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
            text_loc.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));

            text_date.setClickable(false);
            edit_lot.setFocusable(false);
            text_subinv.setFocusable(false);
            text_loc.setFocusable(false);

            btn_date.setEnabled(false);
            btn_date.setClickable(false);
            btn_subInv.setEnabled(false);
            btn_subInv.setClickable(false);
            btn_loc.setEnabled(false);
            btn_loc.setClickable(false);
        }

        text_tran_date.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
        btn_tran_date.setClickable(false);
        text_tran_date.setFocusable(false);
        edit_lot.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
        edit_lot.setFocusable(false);

        btn_subInv.setOnClickListener(onClickListener);
        btn_loc.setOnClickListener(onClickListener);
        btn_comp.setOnClickListener(onClickListener);
        btn_close.setOnClickListener(onClickListener);
        btn_date.setOnClickListener(onClickListener);
        //btn_tran_date.setOnClickListener(onClickListener);
        //text_tran_date.setOnClickListener(onClickListener);
        text_date.setOnClickListener(onClickListener);

        Calendar c = Calendar.getInstance();
        pYear = c.get(Calendar.YEAR);
        pMonth = c.get(Calendar.MONTH);
        pDay = c.get(Calendar.DAY_OF_MONTH);
        //setDateFormat(pYear, pMonth, pDay);

        pYear2 = c.get(Calendar.YEAR);
        pMonth2 = c.get(Calendar.MONTH);
        pDay2 = c.get(Calendar.DAY_OF_MONTH);

        if(L_PRINT_TYPE.equals("Y")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BatchCompActivity.this);
            builder
                    .setTitle(getString(R.string.decision))
                    .setMessage(getString(R.string.printer_pallet_print))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkBluetooth();

                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Comm.SUBINV) {
                text_subinv.setText(Util.nullString(data.getStringExtra("SUBINV_CODE"), ""));

                if (Util.nullString(data.getStringExtra("LOCATOR_CONTROL"), "").equals("1")) {

                    text_loc.setText("");
                    text_loc.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
                    inventoryLocationId = "";
                    btn_loc.setClickable(false);
                    btn_loc.setEnabled(false);

                } else {
                    text_loc.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input));
                    btn_loc.setClickable(true);
                    btn_loc.setEnabled(true);
                }

            } else if (requestCode == Comm.LOC) {

                text_loc.setText(Util.nullString(data.getStringExtra("LOCATOR_CODE"), ""));
                inventoryLocationId = Util.nullString(data.getStringExtra("INVENTORY_LOCATION_ID"), "");

            }
        }
    }

    private DatePickerDialog.OnDateSetListener mPickSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            pYear = year;
            pMonth = monthOfYear;
            pDay = dayOfMonth;

            DecimalFormat mmddFormat = new DecimalFormat("00");
            DecimalFormat yyyyFormat = new DecimalFormat("0000");
            String result = mmddFormat.format(pMonth + 1) + "-" + mmddFormat.format(pDay) + "-" + yyyyFormat.format(pYear);
            text_tran_date.setText(result);
        }
    };

    private DatePickerDialog.OnDateSetListener mPickSetListener2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            pYear2 = year;
            pMonth2 = monthOfYear;
            pDay2 = dayOfMonth;

            DecimalFormat mmddFormat = new DecimalFormat("00");
            DecimalFormat yyyyFormat = new DecimalFormat("0000");
            String result = mmddFormat.format(pMonth2 + 1) + "-" + mmddFormat.format(pDay2) + "-" + yyyyFormat.format(pYear2);
            text_date.setText(result);
        }
    };

    private void setDateFormat(int pYear, int pMonth, int pDay) {
        DecimalFormat mmddFormat = new DecimalFormat("00");
        DecimalFormat yyyyFormat = new DecimalFormat("0000");
        String result = mmddFormat.format(pMonth + 1) + "-" + mmddFormat.format(pDay) + "-" + yyyyFormat.format(pYear);

        text_tran_date.setText(result);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(btn_close)) {
                finish();

            }else if (v.equals(btn_date)) {
                DatePickerDialog datepicker = new DatePickerDialog(BatchCompActivity.this, mPickSetListener2, pYear2, pMonth2, pDay2);
                datepicker.getDatePicker().setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                datepicker.show();

            }else if (v.equals(btn_tran_date)) {
                DatePickerDialog datepicker = new DatePickerDialog(BatchCompActivity.this, mPickSetListener, pYear, pMonth, pDay);
                datepicker.getDatePicker().setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                datepicker.show();

            }else if (v.equals(text_date)) {
                DatePickerDialog datepicker = new DatePickerDialog(BatchCompActivity.this, mPickSetListener, pYear, pMonth, pDay);
                datepicker.getDatePicker().setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                datepicker.show();

            }else if (v.equals(text_tran_date)) {
                DatePickerDialog datepicker = new DatePickerDialog(BatchCompActivity.this, mPickSetListener2, pYear2, pMonth2, pDay2);
                datepicker.getDatePicker().setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                datepicker.show();

            }else if (v.equals(edit_transact_qty)) {
                edit_transact_qty.setSelection(edit_transact_qty.length());

            }else if (v.equals(edit_lot)) {
                edit_lot.setSelection(edit_lot.length());

            }else if (v.equals(btn_subInv)) {
                Intent intent = new Intent(BatchCompActivity.this, PopProdSubInvActivity.class);
                intent.putExtra("ITEM_ID", itemId);
                intent.putExtra("ORG_ID", ORG_ID);
                startActivityForResult(intent, Comm.SUBINV);

            }else if (v.equals(btn_loc)) {
                Intent intent = new Intent(BatchCompActivity.this, PopLocaActivity.class);
                intent.putExtra("SUBINVENTORY_CODE", text_subinv.getText().toString());
                intent.putExtra("ORG_ID", ORG_ID);
                startActivityForResult(intent, Comm.LOC);

            }else if (v.equals(btn_comp)) {
                if(Double.parseDouble(edit_transact_qty.getText().toString()) > Double.parseDouble(perPallet)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BatchCompActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.alert_batch_pallet_qty))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BatchCompActivity.this);
                    builder
                            .setTitle(getString(R.string.decision))
                            .setMessage(getString(R.string.alert_set_batch_comp))
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                                    param.put("orgCode", Util.nullString(orgCode, ""));
                                    param.put("batchId", Util.nullString(batchId, ""));
                                    param.put("transactionType", Util.nullString(transactionType, ""));
                                    param.put("subinventoryCode", Util.nullString(text_subinv.getText().toString(), ""));
                                    param.put("inventoryLocationId", Util.nullString(inventoryLocationId, ""));
                                    param.put("itemCode", Util.nullString(text_item.getText().toString(), ""));
                                    param.put("lotNumber", Util.nullString(edit_lot.getText().toString(), ""));
                                    param.put("expirationDate", Util.nullString(text_date.getText().toString(), ""));
                                    param.put("plTxnQty", Util.nullString(edit_transact_qty.getText().toString(), ""));
                                    param.put("plConRate", Util.nullString(plConRate, ""));
                                    param.put("reasonId", Util.nullString(reasonId, ""));
                                    param.put("materialDetailId", Util.nullString(materialDetailId, ""));
                                    param.put("transactionId", Util.nullString(transactionId, ""));
                                    param.put("palletId", Util.nullString(palletId, ""));
                                    param.put("ouId", Util.nullString(OU_ID, ""));
                                    param.put("orgId", Util.nullString(ORG_ID, ""));
                                    param.put("transactionDate", Util.nullString(text_tran_date.getText().toString(), ""));
                                    param.put("userNo", Util.nullString(S_USER_ID, ""));

                                    new setSubBatchTask().execute(param);
                                }
                            })
                            .setNegativeButton(getString(R.string.no), null)
                            .show();
                }
            }
        }
    };

    private class setSubBatchTask extends AsyncTask<LinkedHashMap<String, String>, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(BatchCompActivity.this, "", getString(R.string.progressing));
        }

        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {

            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL+"/api/prod/setProd");
            http.addAllParameters(params[0]);
            HttpClient post = http.create();
            post.request();

            int statusCode = post.getHttpStatusCode();
            Log.v(Comm.LOG_TAG, "statusCode: "+ statusCode);
            String body = post.getBody();
            Log.v(Comm.LOG_TAG, "body: "+ body);
            return body;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject obj = new JSONObject(result);

                if (!obj.getString("status").equals("S")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BatchCompActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage(obj.getString("msg"))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.setCancelable(false);
                    builder.show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BatchCompActivity.this)
                            .setTitle(getString(R.string.note))
                            .setMessage(getString(R.string.alert_comp_batch))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();

                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            });
                    builder.setCancelable(false);
                    builder.show();

                }

            } catch (Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BatchCompActivity.this)
                        .setTitle(getString(R.string.warning))
                        .setMessage(getString(R.string.server_error))
                        .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.setCancelable(false);
                builder.show();
            }
            progressDialog.dismiss();
        }
    }

    private String createZplReceipt() {
        String wholeZplLabel = "";
                if(tcpYn) {
                    wholeZplLabel += Util.labelPalletNet(L_PALLET_NUMBER,L_ITEM_CODE,L_ITEM_DESC,L_LOT_NUMBER,L_EXPIRATION_DATE,L_BATCH_NO);

                }else {
                    wholeZplLabel += Util.labelPalletBlt(L_PALLET_NUMBER);
                }

        return wholeZplLabel;
    }

    private void printStart(){

        BatchCompActivity.printThread thread = new BatchCompActivity.printThread();
        thread.setDaemon(true);
        thread.start();

    }

    private void doConnectionBT() {
        printer = connect();

        if (printer != null) {
            sendLabel();
        } else {
            disconnect();
        }
    }

    public ZebraPrinter connect() {
        setStatus(getString(R.string.printer_connecting), Color.YELLOW);
        connection = null;
        if(tcpYn == true) {
            connection = new TcpConnection(SettingsHelper.getIp(BatchCompActivity.this), Integer.valueOf(Comm.TCP_PORT_NUMBER));
        }else {
            connection = new BluetoothConnection(SettingsHelper.getBluetoothAddress(BatchCompActivity.this));
        }
        Log.v(Comm.LOG_TAG, "CONNECT : " +SettingsHelper.getBluetoothAddress(BatchCompActivity.this));
        try {
            Log.v(Comm.LOG_TAG, "connection.open() : START" );
            connection.open();
            setStatus(getString(R.string.printer_connected) , Color.GREEN);
            Log.v(Comm.LOG_TAG, "connection.open() : END" );
        } catch (ConnectionException e) {
            Log.v(Comm.LOG_TAG, "ConnectionException : "+e.toString() );
            setStatus(getString(R.string.printer_error_disconnected), Color.RED);
            //DemoSleeper.sleep(1000);
            disconnect();
        }

        //setStatus("프린트 중간~", Color.YELLOW);
        Log.v(Comm.LOG_TAG, "ING~~~~~~~~~~~ : " );
        ZebraPrinter printer = null;

        if (connection.isConnected()) {
            try {

                printer = ZebraPrinterFactory.getInstance(connection);
                setStatus(getString(R.string.printer_loading_lang), Color.YELLOW);
                String pl = SGD.GET("device.languages", connection);
                setStatus(getString(R.string.printer_lang)   + pl, Color.BLUE);
            } catch (ConnectionException e) {
                setStatus(getString(R.string.printer_lang_unknown), Color.RED);
                printer = null;
                //DemoSleeper.sleep(1000);
                Log.v(Comm.LOG_TAG, "loop print_cnt : " );
                disconnect();
            } catch (ZebraPrinterLanguageUnknownException e) {
                setStatus(getString(R.string.printer_lang_unknown), Color.RED);
                printer = null;
                //DemoSleeper.sleep(1000);
                disconnect();
            }
        }

        return printer;
    }

    public void disconnect() {
        try {
            setStatus(getString(R.string.printer_disconnecting), Color.RED);
            if (connection != null) {
                connection.close();
            }
            setStatus(getString(R.string.printer_disconnected), Color.RED);
        } catch (ConnectionException e) {
            setStatus(getString(R.string.printer_unknown_error_disconnected) , Color.RED);
        } finally {
            //finish();
        }
    }

    private void setStatus(final String statusMessage, final int color) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
            }
        });
        //DemoSleeper.sleep(1000);
    }

    private void sendLabel() {

        try {

            ZebraPrinterLinkOs linkOsPrinter = ZebraPrinterFactory.createLinkOsPrinter(printer);

            PrinterStatus printerStatus = (linkOsPrinter != null) ? linkOsPrinter.getCurrentStatus() : printer.getCurrentStatus();

            if (printerStatus.isReadyToPrint) {
                Log.v(Comm.LOG_TAG, "printerStatus.isReadyToPrint : " + printerStatus.isReadyToPrint);
                sendTestLabel();
            } else if (printerStatus.isHeadOpen) {
                Log.v(Comm.LOG_TAG, "printerStatus.isHeadOpen : " + printerStatus.isHeadOpen);
                setStatus(getString(R.string.printer_header_open), Color.RED);
            } else if (printerStatus.isPaused) {
                Log.v(Comm.LOG_TAG, "printerStatus.isPaused : " + printerStatus.isPaused);
                setStatus(getString(R.string.printer_stop) , Color.RED);
            } else if (printerStatus.isPaperOut) {
                Log.v(Comm.LOG_TAG, "printerStatus.isPaperOut : " + printerStatus.isPaperOut);
                setStatus(getString(R.string.printer_paper_out) , Color.RED);
            }
            //DemoSleeper.sleep(1500);
            if (connection instanceof BluetoothConnection) {
                String friendlyName = ((BluetoothConnection) connection).getFriendlyName();
                setStatus(friendlyName, Color.MAGENTA);
                //DemoSleeper.sleep(500);
            }
        } catch (ConnectionException e) {
            setStatus(e.getMessage(), Color.RED);
        } finally {
            disconnect();
        }
    }


    private void sendTestLabel() {
        try {
            byte[] configLabel = createZplReceipt().getBytes();
            printer.getConnection().write(configLabel);
            DemoSleeper.sleep(1500);
            if (printer.getConnection() instanceof BluetoothConnection) {
                DemoSleeper.sleep(500);
            }
        } catch (ConnectionException e) {
        }
    }

    void checkBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_bluetooth), Toast.LENGTH_LONG).show();
        }

        else {
            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, Comm.REQUEST_ENABLE_BT);
            }else{

                selectDevice();

            }
        }
    }

    void selectDevice() {
        try{
            mDevices = mBluetoothAdapter.getBondedDevices();
            mPariedDeviceCount = mDevices.size();

            if(mPariedDeviceCount == 0 ) {
                Toast.makeText(getApplicationContext(), getString(R.string.not_pairing), Toast.LENGTH_LONG).show();
            }

            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setTitle(tcpYn==true?getString(R.string.login_network_select):getString(R.string.login_bluetooth_select));
            Log.v(Comm.LOG_TAG, "printList.length() :: " + printList.length());
            List<String> listItems = new ArrayList<String>();
            if(tcpYn) {
                for (int i = 0; i < printList.length(); i++) {
                    JSONObject data = printList.getJSONObject(i);
                    listItems.add(data.getString("IP_NAME"));
                }
                mPariedDeviceCount = Comm.TCP_ADDR.length;

            }else {

                for (BluetoothDevice device : mDevices) {

                    listItems.add(device.getAddress());
                }
                //listItems.add(getString(R.string.cancel));  // 취소 항목 추가.
            }
            final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
            listItems.toArray(new CharSequence[listItems.size()]);

            if(tcpYn) {
                if(printList.length() > 0) {
                    JSONObject data = printList.getJSONObject(0);
                    SettingsHelper.saveIp(BatchCompActivity.this, data.getString("IP_CODE"));
                }
            }else{
                if(listItems.size() > 0) {
                    SettingsHelper.saveBluetoothAddress(BatchCompActivity.this, items[0].toString());
                }
            }

            builder.setSingleChoiceItems(items, 0,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    // TODO Auto-generated method stub
                    if(tcpYn) {
                        try{

                            JSONObject data = printList.getJSONObject(item);
                            SettingsHelper.saveIp(BatchCompActivity.this, data.getString("IP_CODE"));

                        } catch (JSONException e) {
                            Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                        } catch (Exception e1) {
                            Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
                        }
                    }else{
                        SettingsHelper.saveBluetoothAddress(BatchCompActivity.this, items[item].toString());

                    }
                }});

            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //compSaveAlert();

                }
            });

            builder.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    printStart();

                }
            });
            builder.setCancelable(false);
            android.support.v7.app.AlertDialog alert = builder.create();
            alert.show();
        } catch (JSONException e) {
            Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
        } catch (Exception e1) {
            Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
        }
    }

    class printThread extends Thread{
        @Override
        public void run() {
            while(true){
                Looper.prepare();
                doConnectionBT();
                Looper.loop();
                Looper.myLooper().quit();
            } // end while
        } // end run()
    } // end class BackThread
}
