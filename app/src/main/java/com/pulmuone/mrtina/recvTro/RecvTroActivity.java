package com.pulmuone.mrtina.recvTro;

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
import com.pulmuone.mrtina.comm.Scan;
import com.pulmuone.mrtina.popupLoca.PopLocaActivity;
import com.pulmuone.mrtina.popupSubInv.PopSubInvActivity;
import com.pulmuone.mrtina.popupTro.PopTroActivity;
import com.pulmuone.mrtina.recvPo.RecvPoActivity;
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

public class RecvTroActivity extends Activity{

    private RecvTroAdapter troAdapter;
    ArrayList<RecvTroItem> troList = null;

    private RelativeLayout btn_back = null;
    private LinearLayout btn_date = null;
    private LinearLayout btn_search = null;
    private Button btn_receipt = null;
    private Button btn_find = null;

    private EditText edit_tro_num = null;
    private TextView text_tro_org_from = null;
    private TextView text_tro_org_to = null;
    private TextView text_tro_ship_date = null;
    private TextView text_tro_exp_date = null;
    private TextView text_click1 = null;
    private TextView text_click2 = null;
    private TextView text_click3 = null;
    private TextView text_click4 = null;

    private TextView text_date = null;

    SharedPreferences pref = null;
    private ProgressDialog progressDialog;
    private String S_USER_ID = null;
    private String OU_ID = null;
    private String ORG_ID = null;
    private String scanSubInv = null;
    private int pYear, pMonth, pDay;
    private int selected = 0;

    private Scan Scan = null; //SCANNER CODE
    private int selScan = -1; // -1(main), 0 ~ n-1 (각 행 번호)

    private boolean findFlag = true;

    private ZebraPrinter printer;
    private Connection connection;

    int mPariedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;

    BluetoothAdapter mBluetoothAdapter;
    boolean tcpYn = true;
    private String palletPrint="";
    JSONArray printList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recv_tro);

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);
        S_USER_ID = pref.getString("USER_ID", "");

        edit_tro_num = (EditText) findViewById(R.id.edit_tro_num);
        text_date = (TextView) findViewById(R.id.txt_date);
        text_tro_org_from = (TextView) findViewById(R.id.txt_from_org);
        text_tro_org_to = (TextView) findViewById(R.id.txt_to_org);
        text_tro_ship_date = (TextView) findViewById(R.id.txt_ship_date);
        text_tro_exp_date = (TextView) findViewById(R.id.txt_rect_date);
        text_click1 = (TextView) findViewById(R.id.txt_main4);
        text_click2 = (TextView) findViewById(R.id.txt_main5);
        text_click3 = (TextView) findViewById(R.id.txt_main6);
        text_click4 = (TextView) findViewById(R.id.txt_main7);

        btn_date = (LinearLayout) findViewById(R.id.layout_date);
        btn_back = (RelativeLayout) findViewById(R.id.layout_back);
        btn_search = (LinearLayout)findViewById(R.id.layout_search);
        btn_receipt = (Button)findViewById(R.id.btn_rect);
        btn_find = (Button)findViewById(R.id.tro_btn_find);

        troList = new ArrayList<>();

        btn_back.setOnClickListener(onClickListener);
        btn_date.setOnClickListener(onClickListener);
        btn_search.setOnClickListener(onClickListener);
        btn_receipt.setOnClickListener(onClickListener);
        btn_find.setOnClickListener(onClickListener);
        text_date.setOnClickListener(onClickListener);
        text_click1.setOnClickListener(onClickListener);
        text_click2.setOnClickListener(onClickListener);
        text_click3.setOnClickListener(onClickListener);
        text_click4.setOnClickListener(onClickListener);
        text_tro_org_from.setOnClickListener(onClickListener);
        text_tro_org_to.setOnClickListener(onClickListener);
        text_tro_exp_date.setOnClickListener(onClickListener);
        text_tro_ship_date.setOnClickListener(onClickListener);

        edit_tro_num.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_tro_num.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_tro_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_tro_num.setSelection(edit_tro_num.length());
            }
        });

        edit_tro_num.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == Comm.TRO) {
                edit_tro_num.setText(data.getStringExtra("SHIPMENT_NO"));
                text_tro_org_from.setText(data.getStringExtra("ORG_CODE_FROM"));
                text_tro_org_to.setText(data.getStringExtra("ORG_CODE_TO"));
                text_tro_ship_date.setText(data.getStringExtra("SHIPMENT_DATE"));
                text_tro_exp_date.setText(data.getStringExtra("EXP_RECEIPT_DATE"));

                OU_ID = data.getStringExtra("OU_ID");
                ORG_ID = data.getStringExtra("ORG_ID_TO");

                Log.v(Comm.LOG_TAG, "OU_ID: "+ OU_ID);
                Log.v(Comm.LOG_TAG, "ORG_ID: "+ ORG_ID);

            } else if (requestCode == Comm.SUBINV) {
                String sub_inv = data.getStringExtra("SUBINV_CODE");
                String con = data.getStringExtra("LOCATOR_CONTROL");
                String pallet = data.getStringExtra("PALLET_CONTROL");
                String tro_rcv_id = data.getStringExtra("LOC_TRO_RCV_ID");
                String tro_rcv_code = data.getStringExtra("LOC_TRO_RCV_CODE");
                int pos = data.getIntExtra("POSITION", 1);

                RecvTroItem item = (RecvTroItem) troAdapter.getItem(pos);
                item.setSubinventoryCode(sub_inv);
                item.setLocatorControl(con);
                item.setPalletControl(pallet);
                item.setInventoryLocationId(tro_rcv_id);
                item.setLocatorCode(tro_rcv_code);

                if(con.equals("1")){
                    item.setLocatorCode("");
                    item.setInventoryLocationId("");
                }
                if(pallet.equals("N")){
                    item.setPalletNum("");
                    item.setPalletId("");
                }

                troList.set(pos, item);
                troAdapter.notifyDataSetChanged();

            } else if (requestCode == Comm.LOC) {
                String loc = data.getStringExtra("LOCATOR_CODE");
                String loc_id = data.getStringExtra("INVENTORY_LOCATION_ID");
                int pos = data.getIntExtra("POSITION", 1);

                RecvTroItem item = (RecvTroItem) troAdapter.getItem(pos);
                item.setLocatorCode(loc);
                item.setInventoryLocationId(loc_id);

                troList.set(pos, item);
                troAdapter.notifyDataSetChanged();

             } else if (requestCode == Comm.PALLET) {
                String pall_no = data.getStringExtra("PALLET_NUMBER");
                String pall_id = data.getStringExtra("PALLET_ID");
                int pos = data.getIntExtra("POSITION", 1);

                RecvTroItem item = (RecvTroItem) troAdapter.getItem(pos);
                item.setPalletNum(pall_no);
                item.setPalletId(pall_id);

                troList.set(pos, item);
                troAdapter.notifyDataSetChanged();

            }
        }
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(btn_back)){
                finish();

            }else if (v.equals(btn_search)) {
                searchTro();

            }else if (v.equals(btn_find)) {
                findFlag = true;

                if(edit_tro_num.getText().toString().replace(" ", "").equals("")){
                    new AlertDialog.Builder(RecvTroActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.alert_tro_search))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }else{
                    getTroList();
                }

            }else if (v.equals(btn_receipt)) {
                if(edit_tro_num.getText().toString().replace(" ", "").equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecvTroActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.alert_tro_num))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
                }else{
                    int count = troAdapter.getCount();
                    int cnt = 0;
                    String data = "Y";

                    for(int i = 0; i < count; i++){
                        RecvTroItem item = (RecvTroItem) troAdapter.getItem(i);

                        if(item.getCheckItem().equals("True")){

                            cnt++;

                            Log.v(Comm.LOG_TAG, "LOCATOR_CONTROL: "+ item.LOCATOR_CONTROL);
                            Log.v(Comm.LOG_TAG, "PALLET_CONTROL: "+ item.PALLET_CONTROL);

                            if (item.LOCATOR_CONTROL.equals("1") && item.PALLET_CONTROL.equals("N")) {
                                if (item.SUBINVENTORY_CODE.isEmpty()) {
                                    data = "N";
                                }
                            }else if(item.LOCATOR_CONTROL.equals("1") && item.PALLET_CONTROL.equals("Y")){
                                if (item.SUBINVENTORY_CODE.isEmpty() || item.PALLET_NUMBER.isEmpty()) {
                                    data = "N";
                                }
                            }else if(item.LOCATOR_CONTROL.equals("2") && item.PALLET_CONTROL.equals("Y")){
                                if (item.SUBINVENTORY_CODE.isEmpty() || item.LOCATOR_CODE.isEmpty() || item.PALLET_NUMBER.isEmpty()) {
                                    data = "N";
                                }
                            }
                        }
                    }

                    if(cnt > 0){
                        if(data.equals("N")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(RecvTroActivity.this);
                            builder
                                    .setTitle(getString(R.string.warning))
                                    .setMessage(getString(R.string.alert_no_enough_tro))
                                    .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            return;
                                        }
                                    })
                                    .show();
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(RecvTroActivity.this);
                            builder
                                    .setTitle(getString(R.string.decision))
                                    .setMessage(getString(R.string.alert_set_recp))
                                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            palletPrint = createZplReceipt();
                                            new setTroRecpTask().execute();
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.no), null)
                                    .show();
                        }

                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecvTroActivity.this)
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

            }else if (v.equals(btn_date)) {
                DatePickerDialog datepicker = new DatePickerDialog(RecvTroActivity.this, mPickSetListener, pYear, pMonth, pDay);
                datepicker.getDatePicker().setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                datepicker.show();
            }else if (v.equals(text_date)) {
                DatePickerDialog datepicker = new DatePickerDialog(RecvTroActivity.this, mPickSetListener, pYear, pMonth, pDay);
                datepicker.getDatePicker().setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                datepicker.show();
            }else if (v.equals(text_click1) || v.equals(text_click2) || v.equals(text_click3) || v.equals(text_click4) ||
                    v.equals(text_tro_org_from) || v.equals(text_tro_org_to) || v.equals(text_tro_ship_date) || v.equals(text_tro_exp_date)) {
                selScan = -1;

                try{
                    for(int i = 0; i < troList.size(); i++){
                        troList.get(i).setBackColor("N");
                    }

                    troAdapter.notifyDataSetChanged();
                }catch (Exception e){}

            }
        }
    };

    private DatePickerDialog.OnDateSetListener mPickSetListener =	new DatePickerDialog.OnDateSetListener() {
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

    private void setDateFormat(int pYear, int pMonth, int pDay){
        DecimalFormat mmddFormat = new DecimalFormat("00");
        DecimalFormat yyyyFormat = new DecimalFormat("0000");
        String result = mmddFormat.format(pMonth+1) + "-" + mmddFormat.format(pDay) + "-" + yyyyFormat.format(pYear);

        text_date.setText(result);
    }

    private void searchTro() {
        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
        param.put("ouId", Util.nullString(OU_ID,""));
        param.put("orgId", Util.nullString(ORG_ID,""));
        param.put("searchTro", Util.nullString(edit_tro_num.getText().toString(),""));

        new TroNoSearchTask().execute(param);
    }

    private void getTroList() {
        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
        param.put("ouId", Util.nullString(OU_ID,""));
        param.put("orgId", Util.nullString(ORG_ID,""));
        param.put("shipmentNo", Util.nullString(edit_tro_num.getText().toString(),""));
        param.put("userNo", Util.nullString(S_USER_ID,""));

        new GetTroListTask().execute(param);
    }

    private class TroNoSearchTask extends AsyncTask<LinkedHashMap<String, String>, Void, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(RecvTroActivity.this, "", getString(R.string.querying));
        }

        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {

            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL+"/api/cmmPopup/troNoList");
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
                if(!obj.getString("status").equals("S")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecvTroActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.server_error))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
                }else{
                    JSONArray list = obj.getJSONArray("list");
                    if(list.length()==1){
                        JSONObject data = list.getJSONObject(0);

                        edit_tro_num.setText(data.getString("SHIPMENT_NO"));
                        text_tro_org_from.setText(data.getString("ORG_CODE_FROM"));
                        text_tro_org_to.setText(data.getString("ORG_CODE_TO"));
                        text_tro_ship_date.setText(data.getString("SHIPMENT_DATE"));
                        text_tro_exp_date.setText(data.getString("EXP_RECEIPT_DATE"));

                        OU_ID = data.getString("OU_ID");
                        ORG_ID = data.getString("ORG_ID_TO");

                        Log.v(Comm.LOG_TAG, "OU_ID: "+ OU_ID);
                        Log.v(Comm.LOG_TAG, "ORG_ID: "+ ORG_ID);

                    }else{
                        Intent intent = new Intent(getApplicationContext(), PopTroActivity.class);
                        intent.putExtra("TRO_NUM", edit_tro_num.getText().toString());
                        startActivityForResult(intent, Comm.TRO);
                    }
                }

            } catch (JSONException e) {
                Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
            } catch (Exception e1) {
                Log.v(Comm.LOG_TAG, "Exception Error ::"+e1.getMessage());
            }

            progressDialog.dismiss();
        }
    }

    private class GetTroListTask extends AsyncTask<LinkedHashMap<String, String>, Void, String> implements View.OnClickListener{
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(RecvTroActivity.this, "", getString(R.string.querying));
        }

        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL+"/api/recv/troItemList");
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

            ListView list_tro = (ListView) findViewById(R.id.list_recv_tro);

            try {
                JSONObject obj = new JSONObject(result);
                JSONArray list = obj.getJSONArray("list");
                printList = obj.getJSONArray("print");

                if(findFlag){
                    checkList(list);
                }

                for (int i = 0; i < list.length(); i++) {
                    JSONObject data = list.getJSONObject(i);

                    RecvTroItem info = new RecvTroItem(Util.nullString(data.getString("PALLET_ID"), "")
                            , Util.nullString(data.getString("PALLET_NUMBER"), "")
                            , "N"
                            , Util.nullString(data.getString("ITEM_CODE"), "")
                            , Util.nullString(data.getString("ITEM_DESC"), "")
                            , Util.nullString(data.getString("LOT_NUMBER"), "")
                            , Util.nullString(data.getString("REMAIN_QTY"), "")
                            , ""
                            , "1"
                            , Util.nullString(data.getString("ORGANIZATION_CODE"), "")
                            , Util.nullString(data.getString("SHIPMENT_HEADER_ID"), "")
                            , Util.nullString(data.getString("SHIPMENT_LINE_ID"), "")
                            , Util.nullString(data.getString("VENDOR_ID"), "")
                            , Util.nullString(data.getString("ORDER_UOM_CODE"), "")
                            , ""
                            , Util.nullString(data.getString("INVENTORY_LOCATION_ID"), "")
                            , Util.nullString(data.getString("EXPIRATION_DATE"), "")
                            , Util.nullString(data.getString("RECEIVING_QTY"), "")
                            , Util.nullString(data.getString("BILL_OF_LADING"), "")
                            , OU_ID
                            , ORG_ID
                            , Util.nullString(text_date.getText().toString(), "")
                            , S_USER_ID
                            , "N"
                            , "True"
                            , this
                    );
                    troList.add(info);
                }
                troAdapter = new RecvTroAdapter(RecvTroActivity.this, troList);
                list_tro.setAdapter(troAdapter);

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
                case R.id.btn_new_pall:
                    selected = (int) v.getTag();
                    RecvTroItem item = (RecvTroItem) troAdapter.getItem(selected);

                    if(item.getLocatorControl().equals("2")) {
                        if (item.getSubinventoryCode().equals("") || item.getLocatorCode().equals("")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RecvTroActivity.this)
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(RecvTroActivity.this)
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
                case R.id.layout_scan3:
                case R.id.layout_scan4:
                    selected = (int) v.getTag();
                    selScan = selected;

                    for(int i = 0; i < troList.size(); i++){
                        troList.get(i).setBackColor("N");
                    }

                    RecvTroItem backItem = (RecvTroItem) troAdapter.getItem(selected);
                    backItem.setBackColor("T");
                    troList.set(selected, backItem);

                    troAdapter.notifyDataSetChanged();
            }
        }
    }

    private void getPallet(RecvTroItem item){
        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

        param.put("orgId", Util.nullString(ORG_ID, ""));
        param.put("subinventoryCode", Util.nullString(item.getSubinventoryCode(), ""));
        param.put("inventoryLocationId", Util.nullString(item.getInventoryLocationId(), ""));
        param.put("batchId", Util.nullString("", ""));
        param.put("userNo", Util.nullString(S_USER_ID, ""));
        param.put("locatorCode", "");

        new GetPalletListTask().execute(param);
    }

    private class setTroRecpTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(RecvTroActivity.this, "", getString(R.string.progressing));
        }

        @Override
        protected String doInBackground(Void... voids) {
            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            int count = troAdapter.getCount();
            String saveData = null;

            for(int i = 0; i < count; i++){
                RecvTroItem item = (RecvTroItem) troAdapter.getItem(i);
                if(item.getCheckItem().equals("True")){
                    saveData = saveData + "&idx="+ i
                            + "&orgCode=" 				+ item.getOrgCode()
                            + "&shipmentHeaderId=" 		+ item.getShipmentHeaderId()
                            + "&shipmentLineId=" 		+ item.getShipmentLineId()
                            + "&vendorId=" 			    + item.getVendorId()
                            + "&itemCode=" 			    + item.getItemCode()
                            + "&orderUomCode=" 			+ item.getOrderUomCode()
                            + "&subinventoryCode=" 		+ item.getSubinventoryCode()
                            + "&inventoryLocationId=" 	+ item.getInventoryLocationId()
                            + "&lotNumber=" 			+ item.getLotNumber()
                            + "&expirationDate=" 		+ item.getExpriationDate()
                            + "&receivingQty=" 			+ item.getRemainQty()
                            + "&billOfLading=" 			+ ""
                            + "&palletId=" 				+ item.getPalletId()
                            + "&ouId=" 				    + OU_ID
                            + "&orgId=" 				+ ORG_ID
                            + "&receiptDate=" 			+ text_date.getText().toString()
                            + "&userNo=" 				+ S_USER_ID;
                }
            }
            params.put("list", saveData);

            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL+"/api/recv/setTroRecp");
            http.addAllParameters(params);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecvTroActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage("Receipt Fail \n\n" + obj.getString("msg"))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecvTroActivity.this);
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
            } catch (JSONException e) {
                Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
            } catch (Exception e1) {
                Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
            }
            progressDialog.dismiss();
        }
    }

    private class GetPalletListTask extends AsyncTask<LinkedHashMap<String, String>, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(RecvTroActivity.this, "", getString(R.string.querying));
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecvTroActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.server_error))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();

                }else{
                    RecvTroItem item = (RecvTroItem) troAdapter.getItem(selected);

                    JSONArray list = obj.getJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject data = list.getJSONObject(i);

                        item.setPalletNum(Util.nullString(data.getString("PALLET_NUMBER"), ""));
                        item.setPalletId(Util.nullString(data.getString("PALLET_ID"), ""));
                    }

                    troList.set(selected, item);

                    troAdapter.notifyDataSetChanged();

                }
            } catch (JSONException e) {
                Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
            } catch (Exception e1) {
                Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
            }

            progressDialog.dismiss();
        }
    }

    // [[ SCANNER CODE
    @Override
    protected void onDestroy() {
        super.onDestroy();
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

            if(code.length == 4 && code[0].equals("L")) {
                if(selScan == -1){
                    RecvTroActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RecvTroActivity.this)
                                    .setTitle(getString(R.string.warning))
                                    .setMessage(getString(R.string.alert_scan_tro))
                                    .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            builder.show();
                        }
                    });
                }else{
                    scanSubInv = code[2];

                    LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                    param.put("orgId", Util.nullString(ORG_ID, ""));
                    param.put("subinventoryCode", Util.nullString(code[2], ""));
                    param.put("locatorCode", Util.nullString(code[3], ""));
                    param.put("inventoryLocationId", "");

                    new LocatorScanListTask().execute(param);
                }
            }else if(code.length == 1 && code[0].substring(0,2).equals("TR")){
                if(selScan == -1){
                    edit_tro_num.setText(data);

                    LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                    param.put("ouId", Util.nullString(OU_ID, ""));
                    param.put("orgId", Util.nullString(ORG_ID, ""));
                    param.put("troNo", Util.nullString(data, ""));

                    new TroListScanTask().execute(param);
                }else{
                    RecvTroActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RecvTroActivity.this)
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
            }else if(!code[0].equals("L") && !code[0].substring(0,2).equals("TR")){
                RecvTroActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecvTroActivity.this)
                                .setTitle(getString(R.string.warning))
                                .setMessage(getString(R.string.alert_scan_barcode))
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
        int count = troAdapter.getCount();
        for(int i = 0; i < count; i++){
            RecvTroItem item = (RecvTroItem) troAdapter.getItem(i);
            if(item.CHECK_ITEM.equals("True")){
                if(tcpYn) {
                    wholeZplLabel += Util.labelPalletNet(item.PALLET_NUMBER,item.ITEM_CODE,item.ITEM_DESC,item.LOT_NUMBER,item.EXPIRATION_DATE," ");

                }else {
                    wholeZplLabel += Util.labelPalletBlt(item.PALLET_NUMBER);
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
                    SettingsHelper.saveIp(RecvTroActivity.this, data.getString("IP_CODE"));
                }
            }else{
                if(listItems.size() > 0) {
                    SettingsHelper.saveBluetoothAddress(RecvTroActivity.this, items[0].toString());
                }
            }

            builder.setSingleChoiceItems(items, 0,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    // TODO Auto-generated method stub
                    if(tcpYn) {
                        try{
                            JSONObject data = printList.getJSONObject(item);
                            SettingsHelper.saveIp(RecvTroActivity.this, data.getString("IP_CODE"));

                        } catch (JSONException e) {
                            Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                        } catch (Exception e1) {
                            Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
                        }
                    }else{
                        SettingsHelper.saveBluetoothAddress(RecvTroActivity.this, items[item].toString());

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

        RecvTroActivity.printThread thread = new RecvTroActivity.printThread();
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

        connection = new TcpConnection(SettingsHelper.getIp(RecvTroActivity.this), Integer.valueOf(Comm.TCP_PORT_NUMBER));
        Log.v(Comm.LOG_TAG, "CONNECT : " + SettingsHelper.getBluetoothAddress(RecvTroActivity.this));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(RecvTroActivity.this)
                .setTitle(getString(R.string.note))
                .setMessage(getString(R.string.alert_set_tro_recv))
                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                        param.put("ouId", Util.nullString(OU_ID, ""));
                        param.put("orgId", Util.nullString(ORG_ID, ""));
                        param.put("shipmentNo", Util.nullString(edit_tro_num.getText().toString(), ""));
                        param.put("userNo", Util.nullString(S_USER_ID, ""));

                        findFlag = false;

                        new GetTroListTask().execute(param);
                    }
                });
        builder.show();
    }

    private class TroListScanTask extends AsyncTask<LinkedHashMap<String, String>, Void, String>{
        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL+"/api/recv/troNoScanList");
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

                if(list.length() == 0){
                    Intent intent = new Intent(getApplicationContext(), PopTroActivity.class);
                    intent.putExtra("TRO_NUM", edit_tro_num.getText().toString());
                    startActivityForResult(intent, Comm.TRO);
                }else {
                    JSONObject data = list.getJSONObject(0);

                    text_tro_org_from.setText(data.getString("ORG_CODE_FROM"));
                    text_tro_org_to.setText(data.getString("ORG_CODE_TO"));
                    text_tro_ship_date.setText(data.getString("SHIPMENT_DATE"));
                    text_tro_exp_date.setText(data.getString("EXP_RECEIPT_DATE"));

                    OU_ID = data.getString("OU_ID");
                    ORG_ID = data.getString("ORG_ID_TO");

                    Log.v(Comm.LOG_TAG, "OU_ID: "+ OU_ID);
                    Log.v(Comm.LOG_TAG, "ORG_ID: "+ ORG_ID);

                    initList();
                }

            } catch (JSONException e) {
                Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
            } catch (Exception e1) {
                Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
            }
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

                    final RecvTroItem item = (RecvTroItem) troAdapter.getItem(selScan);

                    item.setInventoryLocationId("");
                    item.setLocatorCode("");
                    item.setSubinventoryCode("");
                    item.setPalletId("");
                    item.setPalletNum("");
                    item.setLocatorControl(itemList.getString("LOCATOR_CONTROL"));
                    item.setPalletControl(itemList.getString("PALLET_CONTROL"));

                    if(list.length() == 0 && itemList.getString("LOCATOR_CONTROL").equals("2")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecvTroActivity.this);
                        builder
                                .setTitle(getString(R.string.note))
                                .setMessage(getString(R.string.alert_no_item))
                                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent(getApplicationContext(), PopLocaActivity.class);

                                        Log.v(Comm.LOG_TAG, "selScan: " + selScan);

                                        item.setSubinventoryCode(scanSubInv);

                                        troList.set(selected, item);
                                        troAdapter.notifyDataSetChanged();

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

                        troList.set(selected, item);
                        troAdapter.notifyDataSetChanged();

                    }

                }else if(list1.length() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecvTroActivity.this);
                    builder
                            .setTitle(getString(R.string.note))
                            .setMessage(getString(R.string.alert_no_item))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(getApplicationContext(), PopSubInvActivity.class);
                                    RecvTroItem troItem = (RecvTroItem) troAdapter.getItem(selScan);
                                    intent.putExtra("POSITION", selScan);
                                    intent.putExtra("ORG_ID", troItem.getOrgId());
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
            AlertDialog.Builder builder = new AlertDialog.Builder(RecvTroActivity.this);
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

    private void initList() {
        if(!troList.isEmpty()){
            troList.clear();
            troAdapter.notifyDataSetChanged();
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


