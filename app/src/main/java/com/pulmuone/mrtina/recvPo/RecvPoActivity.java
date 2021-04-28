package com.pulmuone.mrtina.recvPo;

import android.annotation.SuppressLint;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pulmuone.mrtina.R;
import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.comm.HttpClient;
import com.pulmuone.mrtina.comm.MyEvent;
import com.pulmuone.mrtina.comm.Network;
import com.pulmuone.mrtina.comm.Scan;
import com.pulmuone.mrtina.labelLoca.LocaLabelActivity;
import com.pulmuone.mrtina.popupLoca.PopLocaActivity;
import com.pulmuone.mrtina.popupLot.PopLotActivity;
import com.pulmuone.mrtina.popupPo.PopPoActivity;
import com.pulmuone.mrtina.popupSubInv.PopSubInvActivity;
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

import static android.view.ViewGroup.FOCUS_AFTER_DESCENDANTS;
import static android.view.ViewGroup.FOCUS_BEFORE_DESCENDANTS;
import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;

public class RecvPoActivity extends Activity{

    private RecvPoAdapter adapter;
    ArrayList<RecvPoItem> poList = null;
    ListView list_po = null;

    private RelativeLayout btn_back = null;
    private LinearLayout btn_date = null;
    private LinearLayout btn_search = null;
    private Button btn_receipt = null;
    private Button btn_find = null;

    private TextView text_date = null;
    private TextView text_ship = null;
    private TextView text_supply = null;
    private TextView text_po_date = null;
    private TextView text_click1 = null;
    private TextView text_click2 = null;
    private TextView text_click3 = null;

    private EditText edit_no = null;
    private EditText edit_BL_no = null;

    SharedPreferences pref = null;

    private ProgressDialog progressDialog;
    private String S_USER_ID = null;
    private String pallet_num = null;
    private String bl_no = null;
    private String OU_ID = null;
    private String ORG_ID = null;
    private String scanSubInv = null;
    private int pYear, pMonth, pDay;

    private int selected = 0;
    private RecvPoItem checkItem = null;
    private int selScan = -1; // -1(main), 0 ~ n-1 (각 행 번호)

    private Scan Scan = null; //SCANNER CODE

    private boolean findFlag = true;

    private ZebraPrinter printer;
    private Connection connection;

    int mPariedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;

    BluetoothAdapter mBluetoothAdapter;
    boolean tcpYn = true;
    private String palletPrint = "";

    private Network network = null;
    JSONArray printList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recv_po);

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);
        S_USER_ID = pref.getString("USER_ID", "");

        btn_back = (RelativeLayout) findViewById(R.id.layout_back);
        btn_date = (LinearLayout) findViewById(R.id.layout_date);
        btn_search = (LinearLayout)findViewById(R.id.layout_search);
        btn_receipt = (Button)findViewById(R.id.btn_rect);
        btn_find = (Button)findViewById(R.id.btn_find);

        text_date = (TextView)findViewById(R.id.txt_date);
        text_ship = (TextView)findViewById(R.id.txt_ship_org);
        text_supply = (TextView)findViewById(R.id.txt_supply);
        text_po_date = (TextView)findViewById(R.id.txt_po_date);
        text_click1 = (TextView)findViewById(R.id.txt_main1);
        text_click2 = (TextView)findViewById(R.id.txt_main2);
        text_click3 = (TextView)findViewById(R.id.txt_main3);
        edit_no = (EditText)findViewById(R.id.edit_num);
        edit_BL_no = (EditText)findViewById(R.id.edit_bl_no);

        edit_no.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_no.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_no.setSelection(edit_no.length());
            }
        });

        edit_BL_no.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_BL_no.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_BL_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_BL_no.setSelection(edit_BL_no.length());
            }
        });

        edit_no.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_BL_no.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        list_po = (ListView) findViewById(R.id.list_po);
        poList = new ArrayList<>();

        btn_back.setOnClickListener(onClickListener);
        btn_date.setOnClickListener(onClickListener);
        btn_search.setOnClickListener(onClickListener);
        btn_receipt.setOnClickListener(onClickListener);
        btn_find.setOnClickListener(onClickListener);
        text_date.setOnClickListener(onClickListener);
        text_click1.setOnClickListener(onClickListener);
        text_click2.setOnClickListener(onClickListener);
        text_click3.setOnClickListener(onClickListener);
        text_ship.setOnClickListener(onClickListener);
        text_supply.setOnClickListener(onClickListener);
        text_po_date.setOnClickListener(onClickListener);

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
    }

    private void setDateFormat(int pYear, int pMonth, int pDay){
        DecimalFormat mmddFormat = new DecimalFormat("00");
        DecimalFormat yyyyFormat = new DecimalFormat("0000");
        String result = mmddFormat.format(pMonth+1) + "-" + mmddFormat.format(pDay) + "-" + yyyyFormat.format(pYear);

        text_date.setText(result);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == Comm.LOT) {
                String lot_num = data.getStringExtra("LOT_NUMBER");
                String exp_date = data.getStringExtra("EXPIRATION_DATE");
                int pos = data.getIntExtra("POSITION", 1);

                RecvPoItem item = (RecvPoItem) adapter.getItem(pos);

                item.setLotNumber(lot_num);
                item.setExpirationDate(exp_date);

                poList.set(pos, item);
                adapter.notifyDataSetChanged();

            } else if (requestCode == Comm.PO) {
                edit_no.setText(data.getStringExtra("PO_NO"));
                text_ship.setText(data.getStringExtra("SHIP_TO_ORG"));
                text_supply.setText(data.getStringExtra("SUPPLIER"));
                text_po_date.setText(data.getStringExtra("PO_DATE"));
                bl_no = data.getStringExtra("BL_TYPE");
                OU_ID = data.getStringExtra("OU_ID");
                ORG_ID = data.getStringExtra("ORGANIZATION_ID");

                initList();

                Log.v(Comm.LOG_TAG, "OU_ID: "+ OU_ID);
                Log.v(Comm.LOG_TAG, "ORG_ID: "+ ORG_ID);

            } else if (requestCode == Comm.SUBINV) {
                String sub_inv = data.getStringExtra("SUBINV_CODE");
                String loc_con = data.getStringExtra("LOCATOR_CONTROL");
                String pallet = data.getStringExtra("PALLET_CONTROL");
                String po_rcv_id = data.getStringExtra("LOC_PO_RCV_ID");
                String po_rcv_code = data.getStringExtra("LOC_PO_RCV_CODE");
                int pos = data.getIntExtra("POSITION", 1);

                RecvPoItem item = (RecvPoItem) adapter.getItem(pos);
                item.setSubinventoryCode(sub_inv);
                item.setLocatorControl(loc_con);
                item.setPalletControl(pallet);
                item.setInventoryLocationId(po_rcv_id);
                item.setLocatorCode(po_rcv_code);

                if(loc_con.equals("1")){
                    item.setLocatorCode("");
                    item.setInventoryLocationId("");
                }

                poList.set(pos, item);
                adapter.notifyDataSetChanged();

            } else if (requestCode == Comm.LOC) {
                String loc = data.getStringExtra("LOCATOR_CODE");
                String loc_id = data.getStringExtra("INVENTORY_LOCATION_ID");
                int pos = data.getIntExtra("POSITION", 1);

                RecvPoItem item = (RecvPoItem) adapter.getItem(pos);
                item.setLocatorCode(loc);
                item.setInventoryLocationId(loc_id);

                poList.set(pos, item);
                adapter.notifyDataSetChanged();

            } else if (requestCode == Comm.PALLET) {
                String pall_no = data.getStringExtra("PALLET_NUMBER");
                String pall_id = data.getStringExtra("PALLET_ID");
                int pos = data.getIntExtra("POSITION", 1);

                RecvPoItem item = (RecvPoItem) adapter.getItem(pos);
                item.setPalletNum(pall_no);
                item.setPalletId(pall_id);

                poList.set(pos, item);
                adapter.notifyDataSetChanged();
            }
        }
    }

    OnClickListener onClickListener = new OnClickListener() {


        @Override
        public void onClick(View v) {
            if (v.equals(btn_back)) {
                finish();

            } else if (v.equals(btn_date)){
                DatePickerDialog datepicker = new DatePickerDialog(RecvPoActivity.this, mPickSetListener, pYear, pMonth, pDay);
                datepicker.getDatePicker().setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                datepicker.show();

            } else if (v.equals(text_date)){
                DatePickerDialog datepicker = new DatePickerDialog(RecvPoActivity.this, mPickSetListener, pYear, pMonth, pDay);
                datepicker.getDatePicker().setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                datepicker.show();

            } else if (v.equals(btn_search)){
                findFlag = true;

                searchPo();

            } else if (v.equals(btn_receipt)) {

                if(edit_no.getText().toString().replace(" ", "").equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecvPoActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.alert_po_num))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
                }else{
                    try{
                        int count = adapter.getCount();
                        String data = "Y";

                        Log.v(Comm.LOG_TAG, "BL_no: "+ bl_no);

                        if(bl_no.equalsIgnoreCase("import") && edit_BL_no.getText().toString().equals("")){

                            new AlertDialog.Builder(RecvPoActivity.this)
                                    .setTitle(getString(R.string.warning))
                                    .setMessage(getString(R.string.alert_po_bl_no))
                                    .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).show();
                        } else {

                            int cnt = 0;

                            for(int i = 0; i < count; i++) {
                                RecvPoItem item = (RecvPoItem) adapter.getItem(i);

                                Log.v(Comm.LOG_TAG, "LOT_CONTROL_CODE: "+ item.LOT_CONTROL_CODE);
                                Log.v(Comm.LOG_TAG, "LOCATOR_CONTROL: "+ item.LOCATOR_CONTROL);
                                Log.v(Comm.LOG_TAG, "PALLET_CONTROL: "+ item.PALLET_CONTROL);

                                if(item.CHECK_ITEM.equals("True")){
                                    cnt++;

                                    if (item.LOT_CONTROL_CODE.equals("1") && item.LOCATOR_CONTROL.equals("1") && item.PALLET_CONTROL.equals("N")) {
                                        if (item.SUBINVENTORY_CODE.isEmpty() || item.PO_QTY.isEmpty()) {
                                            data = "N";
                                        }
                                    }else if(item.LOT_CONTROL_CODE.equals("1") && item.LOCATOR_CONTROL.equals("1") && item.PALLET_CONTROL.equals("Y")){
                                        if (item.SUBINVENTORY_CODE.isEmpty() || item.PO_QTY.isEmpty() || item.PALLET_NUM.isEmpty()) {
                                            data = "N";
                                        }
                                    }else if(item.LOT_CONTROL_CODE.equals("1") && item.LOCATOR_CONTROL.equals("2") && item.PALLET_CONTROL.equals("Y")){
                                        if (item.SUBINVENTORY_CODE.isEmpty() || item.PO_QTY.isEmpty() || item.LOCATOR_CODE.isEmpty() || item.PALLET_NUM.isEmpty()) {
                                            data = "N";
                                        }
                                    }else if(item.LOT_CONTROL_CODE.equals("1") && item.LOCATOR_CONTROL.equals("2") && item.PALLET_CONTROL.equals("N")){
                                        if (item.SUBINVENTORY_CODE.isEmpty() || item.PO_QTY.isEmpty() || item.LOCATOR_CODE.isEmpty()) {
                                            data = "N";
                                        }
                                    }else if(item.LOT_CONTROL_CODE.equals("2") && item.LOCATOR_CONTROL.equals("1") && item.PALLET_CONTROL.equals("N")){
                                        if (item.SUBINVENTORY_CODE.isEmpty() || item.PO_QTY.isEmpty() || item.LOT_NUMBER.isEmpty() || item.EXPIRATION_DATE.isEmpty()) {
                                            data = "N";
                                        }
                                    }else if(item.LOT_CONTROL_CODE.equals("2") && item.LOCATOR_CONTROL.equals("1") && item.PALLET_CONTROL.equals("Y")){
                                        if (item.SUBINVENTORY_CODE.isEmpty() || item.PO_QTY.isEmpty() || item.PALLET_NUM.isEmpty() || item.LOT_NUMBER.isEmpty() || item.EXPIRATION_DATE.isEmpty()) {
                                            data = "N";
                                        }
                                    }else if(item.LOT_CONTROL_CODE.equals("2") && item.LOCATOR_CONTROL.equals("2") && item.PALLET_CONTROL.equals("N")){
                                        if (item.SUBINVENTORY_CODE.isEmpty() || item.PO_QTY.isEmpty() || item.LOT_NUMBER.isEmpty() || item.EXPIRATION_DATE.isEmpty() || item.LOCATOR_CODE.isEmpty()) {
                                            data = "N";
                                        }
                                    }else{
                                        if (item.SUBINVENTORY_CODE.isEmpty() || item.PO_QTY.isEmpty() || item.PALLET_NUM.isEmpty() || item.LOCATOR_CODE.isEmpty() || item.LOT_NUMBER.isEmpty() || item.EXPIRATION_DATE.isEmpty()) {
                                            data = "N";
                                        }
                                    }
                                }
                            }

                            Log.v(Comm.LOG_TAG, "cnt: "+ cnt);
                            Log.v(Comm.LOG_TAG, "data: "+ data);

                            if(cnt > 0){
                                if(data.equals("N")){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RecvPoActivity.this);
                                    builder
                                            .setTitle(getString(R.string.warning))
                                            .setMessage(getString(R.string.alert_no_select))
                                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    return;
                                                }
                                            })
                                            .show();
                                }else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RecvPoActivity.this);
                                    builder
                                            .setTitle(getString(R.string.decision))
                                            .setMessage(getString(R.string.alert_set_recp))
                                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    palletPrint = createZplReceipt();
                                                    new setPoRecpTask().execute();
                                                }
                                            })
                                            .setNegativeButton(getString(R.string.no), null)
                                            .show();
                                }
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(RecvPoActivity.this)
                                        .setTitle(getString(R.string.warning))
                                        .setMessage(getString(R.string.alert_no_find))
                                        .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                builder.show();
                            }
                        }
                    } catch (Exception e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecvPoActivity.this)
                                .setTitle(getString(R.string.warning))
                                .setMessage(getString(R.string.alert_no_info))
                                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        builder.show();
                    }
                }

            }else if (v.equals(btn_find)) {
                findFlag = true;

                if(Util.isNull(OU_ID) || Util.isNull(ORG_ID)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecvPoActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.alert_no_header))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
                }else{
                    getPoList();

                }

            }else if (v.equals(text_click1) || v.equals(text_click2) || v.equals(text_click3)
                    || v.equals(text_po_date) || v.equals(text_ship) || v.equals(text_supply)) {
                selScan = -1;

                try{
                    for(int i = 0; i < poList.size(); i++){
                        poList.get(i).setBackColor("N");
                    }
                    adapter.notifyDataSetChanged();
                }catch (Exception e){}

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
            String result = mmddFormat.format(pMonth+1) + "-" + mmddFormat.format(pDay) + "-" + yyyyFormat.format(pYear);

            text_date.setText(result);
        }
    };

    private DatePickerDialog.OnDateSetListener mPickSetListener2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            pYear = year;
            pMonth = monthOfYear;
            pDay = dayOfMonth;

            DecimalFormat mmddFormat = new DecimalFormat("00");
            DecimalFormat yyyyFormat = new DecimalFormat("0000");
            String result = mmddFormat.format(pMonth+1) + "-" + mmddFormat.format(pDay) + "-" + yyyyFormat.format(pYear);

            RecvPoItem item = (RecvPoItem) adapter.getItem(selected);
            item.setExpirationDate(result);

            poList.set(selected, item);

            adapter.notifyDataSetChanged();
        }
    };

    private void searchPo() {
        Intent intent = new Intent(getApplicationContext(), PopPoActivity.class);
        intent.putExtra("PO_NO", Util.nullString(edit_no.getText().toString(),""));

        startActivityForResult(intent, Comm.PO);
    }

    private void getPoList() {
        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
        param.put("ouId", Util.nullString(OU_ID,""));
        param.put("orgId", Util.nullString(ORG_ID,""));
        param.put("poNo", Util.nullString(edit_no.getText().toString(),""));
        param.put("userNo", Util.nullString(S_USER_ID,""));

        new GetPoListTask().execute(param);
    }

    private class GetPoListTask extends AsyncTask<LinkedHashMap<String, String>, Void, String> implements View.OnClickListener{
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(RecvPoActivity.this, "", getString(R.string.querying));
        }

        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL+"/api/recv/poItemList");
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
                JSONArray list = obj.getJSONArray("list");
                printList = obj.getJSONArray("print");

                if(findFlag){
                    checkList(list);
                }

                initList();

                for (int i = 0; i < list.length(); i++) {
                    JSONObject data = list.getJSONObject(i);


                    RecvPoItem info = new RecvPoItem(Util.nullString(data.getString("ORGANIZATION_CODE"), "")
                            , Util.nullString(data.getString("PO_HEADER_ID"), "")
                            , Util.nullString(data.getString("PO_LINE_ID"), "")
                            , Util.nullString(data.getString("PO_LINE_LOCATION_ID"), "")
                            , Util.nullString(data.getString("VENDOR_ID"), "")
                            , Util.nullString(data.getString("ITEM_CODE"), "")
                            , Util.nullString(data.getString("ORDER_UOM_CODE"), "")
                            , Util.nullString(data.getString("SUBINVENTORY_CODE"), "")
                            , Util.nullString(data.getString("LOCATOR_CODE"), "")
                            , Util.nullString(data.getString("INVENTORY_LOCATION_ID"), "")
                            , Util.nullString(data.getString("LOT_NUMBER"), "")
                            , Util.nullString(data.getString("LOT_CONTROL_CODE"), "")
                            , Util.nullString(data.getString("EXPIRATION_DATE"), "")
                            , Util.nullString(data.getString("RECEIVING_QTY"), "")
                            , Util.nullString(data.getString("BILL_OF_LADING"), "")
                            , Util.nullString(data.getString("PALLET_CONTROL"), "")
                            , Util.nullString(data.getString("PALLET_ID"), "")
                            , Util.nullString(pallet_num, "")
                            , Util.nullString(data.getString("ITEM_DESC"), "")
                            , Util.nullString(data.getString("ITEM_ID"), "")
                            , Util.nullString(data.getString("REMAIN_QTY"), "")
                            , Util.nullString(data.getString("LOCATOR_CONTROL"), "")
                            , OU_ID
                            , ORG_ID
                            , Util.nullString(text_date.getText().toString(), "")
                            , S_USER_ID
                            , Util.nullString(data.getString("PO_NO"), "")
                            , "True"
                            , "P"
                            , "N"
                            , this
                    );
                    poList.add(info);
                }
                adapter = new RecvPoAdapter(RecvPoActivity.this, poList);
                list_po.setAdapter(adapter);

            } catch (JSONException e) {
                Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
            } catch (Exception e1) {
                Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
            }

            progressDialog.dismiss();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.layout_exp_date:
                case R.id.txt_exp_date:
                    selected = (int) v.getTag();
                    DatePickerDialog datepicker = new DatePickerDialog(RecvPoActivity.this, mPickSetListener2, pYear, pMonth, pDay);
                    datepicker.getDatePicker().setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                    datepicker.show();
                    break;
                case R.id.btn_new_pall:
                    selected = (int) v.getTag();
                    RecvPoItem item = (RecvPoItem) adapter.getItem(selected);

                    if(item.getLocatorControl().equals("2")) {
                        if (item.getSubinventoryCode().equals("") || item.getLocatorCode().equals("")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RecvPoActivity.this)
                                    .setTitle(getString(R.string.warning))
                                    .setMessage(getString(R.string.alert_set_loc))
                                    .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            builder.show();
                        }else{
                            getPallet(item);
                        }
                    }else if(item.getLocatorControl().equals("1")) {
                        if (item.getSubinventoryCode().equals("")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RecvPoActivity.this)
                                    .setTitle(getString(R.string.warning))
                                    .setMessage(getString(R.string.alert_set_subinv))
                                    .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            builder.show();
                        }else{
                            getPallet(item);
                        }
                    }
                    break;
                case R.id.layout_po_plus:
                    selected = (int) v.getTag();
                    final RecvPoItem plusItem = (RecvPoItem) adapter.getItem(selected);

                    if(plusItem.getImage().equals("M")){
                        adapter.deleteItem(selected);

                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecvPoActivity.this)
                                .setTitle(getString(R.string.decision))
                                .setMessage(getString(R.string.alert_line_add))
                                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        adapter.addItem(plusItem,selected+1);
                                    }
                                });
                        builder.show();
                    }

                    break;
                case R.id.layout_search_lot:
                    selected = (int) v.getTag();
                    checkItem = (RecvPoItem) adapter.getItem(selected);

                    Intent intent = new Intent(RecvPoActivity.this, PopLotActivity.class);

                    intent.putExtra("OU_ID", OU_ID);
                    intent.putExtra("ORG_ID", ORG_ID);
                    intent.putExtra("USER_NO", S_USER_ID);
                    intent.putExtra("PO_HEADER_ID", checkItem.getPoHeaderId());
                    intent.putExtra("PO_NO", checkItem.getPoNo());
                    intent.putExtra("PO_LINE_ID", checkItem.getPoLineId());
                    intent.putExtra("ITEM_ID", checkItem.getItemId());
                    intent.putExtra("POSITION", selected);
                    intent.putExtra("SEARCH_LOT", Util.nullString(checkItem.getLotNumber(),""));

                    startActivityForResult(intent, Comm.LOT);
                    break;

                case R.id.layout_scan1:
                case R.id.layout_scan2:
                    selected = (int) v.getTag();
                    selScan = selected;

                    for(int i = 0; i < poList.size(); i++){
                        poList.get(i).setBackColor("N");
                    }

                    RecvPoItem backItem = (RecvPoItem) adapter.getItem(selected);
                    backItem.setBackColor("T");
                    poList.set(selected, backItem);

                    adapter.notifyDataSetChanged();
            }
        }
    }

    private void getPallet(RecvPoItem item){
        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

        param.put("orgId", Util.nullString(ORG_ID, ""));
        param.put("subinventoryCode", Util.nullString(item.getSubinventoryCode(), ""));
        param.put("inventoryLocationId", Util.nullString(item.getInventoryLocationId(), ""));
        param.put("batchId", Util.nullString("", ""));
        param.put("userNo", Util.nullString(S_USER_ID, ""));
        param.put("locatorCode", "");

        new GetPalletListTask().execute(param);
    }
//     [[ SCANNER CODE
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
        if(Scan!=null) {
            Scan.onDestroy();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(Scan!=null) {
            Scan.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Scan!=null) {
            Scan.onPause();
        }
    }

    MyEvent myListener = new MyEvent() {
        @Override
        public void onEvent(String data) {
            Log.v(Comm.LOG_TAG, "MyEvent:"+data);

            String[] code = data.split(";");

            if(code.length == 4 && code[0].equals("L")){
                scanSubInv = code[2];

                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                param.put("orgId", Util.nullString(ORG_ID, ""));
                param.put("subinventoryCode", Util.nullString(code[2], ""));
                param.put("locatorCode", Util.nullString(code[3], ""));
                param.put("inventoryLocationId", "");

                new LocatorScanListTask().execute(param);
            }else{
                RecvPoActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecvPoActivity.this)
                                .setTitle(getString(R.string.warning))
                                .setMessage(getString(R.string.alert_scan_loca))
                                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        builder.show();
                    }
                });
            }
        }
    };
// ]] SCANNER CODE

    private String createZplReceipt() {
        String wholeZplLabel = "";
        int count = adapter.getCount();
        for(int i = 0; i < count; i++){
            RecvPoItem item = (RecvPoItem) adapter.getItem(i);
            if(item.CHECK_ITEM.equals("True")){
                if(tcpYn) {
                    wholeZplLabel += Util.labelPalletNet(item.PALLET_NUM,item.ITEM_CODE,item.ITEM_DESC,item.LOT_NUMBER,item.EXPIRATION_DATE," ");

                }else {
                    wholeZplLabel += Util.labelPalletBlt(item.PALLET_NUM);
                }
            }
        }

        return wholeZplLabel;
    }

    void selectDevice() {
        try{
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.login_network_select));

            List<String> listItems = new ArrayList<String>();

            for (int i = 0; i < printList.length(); i++) {
                JSONObject data = printList.getJSONObject(i);
                listItems.add(data.getString("IP_NAME"));
            }
            mPariedDeviceCount = Comm.TCP_ADDR.length;

            final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
            listItems.toArray(new CharSequence[listItems.size()]);

            if(tcpYn) {
                if(printList.length() > 0) {
                    JSONObject data = printList.getJSONObject(0);
                    SettingsHelper.saveIp(RecvPoActivity.this, data.getString("IP_CODE"));
                }
            }else{
                if(listItems.size() > 0) {
                    SettingsHelper.saveBluetoothAddress(RecvPoActivity.this, items[0].toString());
                }
            }

            builder.setSingleChoiceItems(items, 0,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    // TODO Auto-generated method stub
                    if(tcpYn) {
                        try{
                            JSONObject data = printList.getJSONObject(item);
                            SettingsHelper.saveIp(RecvPoActivity.this, data.getString("IP_CODE"));

                        } catch (JSONException e) {
                            Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                        } catch (Exception e1) {
                            Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
                        }
                    }else{
                        SettingsHelper.saveBluetoothAddress(RecvPoActivity.this, items[item].toString());

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

    private void printStart(){

        RecvPoActivity.printThread thread = new RecvPoActivity.printThread();
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

        connection = new TcpConnection(SettingsHelper.getIp(RecvPoActivity.this), Integer.valueOf(Comm.TCP_PORT_NUMBER));
        Log.v(Comm.LOG_TAG, "CONNECT : " +SettingsHelper.getBluetoothAddress(RecvPoActivity.this));
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
                compSaveAlert();
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

    private void sendTestLabel() {
        try {
            byte[] configLabel = palletPrint.getBytes();
            printer.getConnection().write(configLabel);
            DemoSleeper.sleep(1500);
            if (printer.getConnection() instanceof BluetoothConnection) {
                DemoSleeper.sleep(500);
            }
        } catch (ConnectionException e) {
        }
    }

    private void compSaveAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(RecvPoActivity.this)
                .setTitle(getString(R.string.note))
                .setMessage(getString(R.string.alert_set_comp))
                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                        param.put("ouId", Util.nullString(OU_ID,""));
                        param.put("orgId", Util.nullString(ORG_ID,""));
                        param.put("poNo", Util.nullString(edit_no.getText().toString(),""));
                        param.put("userNo", Util.nullString(S_USER_ID,""));

                        findFlag = false;

                        new GetPoListTask().execute(param);
                    }
                });
        builder.show();
    }

    private class GetPalletListTask extends AsyncTask<LinkedHashMap<String, String>, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(RecvPoActivity.this, "", getString(R.string.querying));
        }

        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL+"/api/recv/getPallet");
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
                if(!obj.getString("status").equals("S")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecvPoActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.server_error))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();

                }else{
                    RecvPoItem item = (RecvPoItem) adapter.getItem(selected);

                    JSONArray list = obj.getJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject data = list.getJSONObject(i);

                        item.setPalletNum(Util.nullString(data.getString("PALLET_NUMBER"), ""));
                        item.setPalletId(Util.nullString(data.getString("PALLET_ID"), ""));
                    }

                    poList.set(selected, item);

                    adapter.notifyDataSetChanged();

                }
            } catch (JSONException e) {
                Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
            } catch (Exception e1) {
                Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
            }

            progressDialog.dismiss();
        }
    }

    private class setPoRecpTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(RecvPoActivity.this, "", getString(R.string.progressing));
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(Void... voids) {
            int count = adapter.getCount();
            String saveData = null;

            for(int i = 0; i < count; i++){
                RecvPoItem item = (RecvPoItem) adapter.getItem(i);
                if(item.getCheckItem().equals("True")){
                    saveData = saveData + "&idx="+ i
                            + "&orgCode=" 				+ item.getOrgCode()
                            + "&poHeaderId=" 			+ item.getPoHeaderId()
                            + "&poLineId=" 				+ item.getPoLineId()
                            + "&poLineLocationId=" 		+ item.getPoLineLocationId()
                            + "&vendorId=" 			    + item.getVendorId()
                            + "&itemCode=" 			    + item.getItemCode()
                            + "&orderUomCode=" 			+ item.getOrderUomCode()
                            + "&subinventoryCode=" 		+ item.getSubinventoryCode()
                            + "&inventoryLocationId=" 	+ item.getInventoryLocationId()
                            + "&lotNumber=" 			+ item.getLotNumber()
                            + "&expirationDate=" 		+ item.getExpriationDate()
                            + "&receivingQty=" 			+ item.getPoQty()
                            + "&billOfLading=" 			+ edit_BL_no.getText().toString()
                            + "&palletId=" 				+ item.getPalletId()
                            + "&ouId=" 				    + item.getOuId()
                            + "&orgId=" 				+ item.getOrgId()
                            + "&receiptDate=" 			+ text_date.getText().toString()
                            + "&userNo=" 				+ S_USER_ID;
                }
            }

            LinkedHashMap<String, String> param = new LinkedHashMap<>();
            param.put("list", saveData);
            Log.v(Comm.LOG_TAG, "saveData-saveData: "+ saveData);

            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL+"/api/recv/setPoRecp");
            http.addAllParameters(param);
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
                if(!obj.getString("status").equals("S")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecvPoActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage("Receipt Fail \n\n" + obj.getString("msg"))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
                }else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(RecvPoActivity.this);
                    builder
                            .setTitle(getString(R.string.decision))
                            .setMessage(getString(R.string.printer_pallet_print))
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    selectDevice();

                                }
                            })
                            .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    compSaveAlert();

                                }
                            })
                            .show();
                }
            } catch (Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RecvPoActivity.this)
                        .setTitle(getString(R.string.warning))
                        .setMessage(getString(R.string.server_error))
                        .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();
            }

            progressDialog.dismiss();
        }
    }

    private class LocatorScanListTask extends AsyncTask<LinkedHashMap<String, String>, Void, String>{
        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL+"/api/recv/locatorScanList");
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
        protected void onPostExecute(String result){
            try {
                JSONObject obj = new JSONObject(result);

                JSONArray list = obj.getJSONArray("list");
                JSONArray list1 = obj.getJSONArray("list1");
                JSONArray list2 = obj.getJSONArray("list2");

                if (list1.length() == 1) {
                    JSONObject itemList = list1.getJSONObject(0);

                    final RecvPoItem item = (RecvPoItem) adapter.getItem(selScan);

                    item.setInventoryLocationId("");
                    item.setLocatorCode("");
                    item.setLocatorControl(itemList.getString("LOCATOR_CONTROL"));
                    item.setPalletControl(itemList.getString("PALLET_CONTROL"));
                    item.setSubinventoryCode("");
                    item.setPalletId("");
                    item.setPalletNum("");

                    if(list.length() == 0 && itemList.getString("LOCATOR_CONTROL").equals("2")){

                        AlertDialog.Builder builder = new AlertDialog.Builder(RecvPoActivity.this);
                        builder
                                .setTitle(getString(R.string.note))
                                .setMessage(getString(R.string.alert_no_item))
                                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent(getApplicationContext(), PopLocaActivity.class);

                                        Log.v(Comm.LOG_TAG, "selScan: " + selScan);

                                        item.setSubinventoryCode(scanSubInv);

                                        poList.set(selected, item);
                                        adapter.notifyDataSetChanged();

                                        intent.putExtra("SUBINVENTORY_CODE", item.getSubinventoryCode());
                                        intent.putExtra("POSITION", selScan);
                                        intent.putExtra("ORG_ID", item.getOrgId());

                                        startActivityForResult(intent, Comm.LOC);
                                    }
                                })
                                .show();
                    }else{

                        if(list.length() != 0){
                            JSONObject data = list.getJSONObject(0);
                            item.setInventoryLocationId(data.getString("INVENTORY_LOCATION_ID"));
                            item.setLocatorCode(data.getString("LOCATOR_CODE"));
                        }

                        if(list1.length() != 0){
                            JSONObject data1 = list1.getJSONObject(0);
                            item.setPalletControl(data1.getString("PALLET_CONTROL"));
                            item.setSubinventoryCode(data1.getString("SUBINVENTORY_CODE"));
                            item.setLocatorControl(data1.getString("LOCATOR_CONTROL"));
                        }

                        if(list2.length() == 1) {
                            JSONObject data2 = list2.getJSONObject(0);
                            item.setPalletId(data2.getString("PALLET_ID"));
                            item.setPalletNum(data2.getString("PALLET_NUMBER"));
                        }

                        poList.set(selected, item);
                        adapter.notifyDataSetChanged();
                    }

                }else if(list1.length() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecvPoActivity.this);
                    builder
                            .setTitle(getString(R.string.note))
                            .setMessage(getString(R.string.alert_no_item))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(getApplicationContext(), PopSubInvActivity.class);
                                    RecvPoItem poItem = (RecvPoItem) adapter.getItem(selScan);
                                    intent.putExtra("POSITION", selScan);
                                    intent.putExtra("ORG_ID", poItem.getOrgId());
                                    startActivityForResult(intent, Comm.SUBINV);
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

    private void checkList(JSONArray list) {
        if(list.length() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(RecvPoActivity.this);
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

    private void initList(){
        if(!poList.isEmpty()){
            poList.clear();
            adapter.notifyDataSetChanged();
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


