package com.pulmuone.mrtina.tranTro;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pulmuone.mrtina.R;
import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.comm.HttpClient;
import com.pulmuone.mrtina.comm.MyEvent;
import com.pulmuone.mrtina.comm.Scan;
import com.pulmuone.mrtina.popupLoca.PopLocaActivity;
import com.pulmuone.mrtina.popupSubInv.PopSubInvActivity;
import com.pulmuone.mrtina.popupTranLoca.PopTranLocaActivity;
import com.pulmuone.mrtina.popupTranTro.PopTranTroActivity;
import com.pulmuone.mrtina.popupTro.PopTroActivity;
import com.pulmuone.mrtina.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;

public class TranTroActivity extends Activity {

    private TranTroAdapter adapter;
    ArrayList<TranTroItem> tranList = null;

    private RelativeLayout btn_back = null;
    private LinearLayout btn_search = null;
    private LinearLayout btn_date = null;
    private Button btn_find = null;
    private Button btn_ship = null;

    private TextView text_date = null;
    private EditText edit_tro_num = null;
    private TextView text_tro_org_from = null;
    private TextView text_tro_org_to = null;
    private TextView text_tro_exp_date = null;
    private TextView text_click1 = null;
    private TextView text_click2 = null;
    private TextView text_click3 = null;

    SharedPreferences pref = null;
    private ProgressDialog progressDialog;
    private String S_USER_ID = null;
    private String OU_ID = null;
    private String ORG_ID = null;
    private int pYear, pMonth, pDay;

    private int selected = 0;
    private int selScan = -1; // -1(main), 0 ~ n-1 (각 행 번호)

    private Scan Scan = null; //SCANNER CODE
    private String scanSubInv = null;
    private String scanTro = null;

    private boolean findFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tran_tro);

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);
        S_USER_ID = pref.getString("USER_ID", "");

        edit_tro_num = (EditText) findViewById(R.id.edit_num);
        text_date = (TextView) findViewById(R.id.txt_date);
        text_tro_org_from = (TextView) findViewById(R.id.txt_from_org);
        text_tro_org_to = (TextView) findViewById(R.id.txt_to_org);
        text_tro_exp_date = (TextView) findViewById(R.id.txt_rect_date);
        text_click1 = (TextView) findViewById(R.id.txt_main1);
        text_click2 = (TextView) findViewById(R.id.txt_main2);
        text_click3 = (TextView) findViewById(R.id.txt_main3);

        btn_date = (LinearLayout) findViewById(R.id.layout_date);
        btn_back = (RelativeLayout) findViewById(R.id.layout_back);
        btn_search = (LinearLayout)findViewById(R.id.layout_search);
        btn_ship = (Button)findViewById(R.id.btn_ship);
        btn_find = (Button)findViewById(R.id.btn_find);

        btn_back.setOnClickListener(onClickListener);
        btn_date.setOnClickListener(onClickListener);
        btn_search.setOnClickListener(onClickListener);
        btn_ship.setOnClickListener(onClickListener);
        btn_find.setOnClickListener(onClickListener);
        text_date.setOnClickListener(onClickListener);
        text_click1.setOnClickListener(onClickListener);
        text_click2.setOnClickListener(onClickListener);
        text_click3.setOnClickListener(onClickListener);

        edit_tro_num.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        edit_tro_num.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_tro_num.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_tro_num.setOnClickListener(onClickListener);

        Calendar c = Calendar.getInstance();
        pYear = c.get(Calendar.YEAR);
        pMonth = c.get(Calendar.MONTH);
        pDay = c.get(Calendar.DAY_OF_MONTH);

        tranList = new ArrayList<>();

//         [[ SCANNER CODE
        if(Comm.isLibraryInstalled(this,"com.symbol.emdk")) {
            this.Scan = new Scan(this);
            Scan.setEvent(myListener);
        }
//         ]] SCANNER CODE
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == Comm.TRANTRO) {

                edit_tro_num.setText(Util.nullString(data.getStringExtra("SHIPMENT_NO"), ""));
                text_tro_org_from.setText(Util.nullString(data.getStringExtra("ORG_CODE_FROM"), ""));
                text_tro_org_to.setText(Util.nullString(data.getStringExtra("ORG_CODE_TO"), ""));
                text_date.setText(Util.nullString(data.getStringExtra("SHIPMENT_DATE"), ""));
                text_tro_exp_date.setText(Util.nullString(data.getStringExtra("EXP_RECEIPT_DATE"), ""));

                OU_ID = Util.nullString(data.getStringExtra("OU_ID"), "");
                ORG_ID = Util.nullString(data.getStringExtra("ORG_ID"), "");

                Log.v(Comm.LOG_TAG, "OU_ID: "+ OU_ID);
                Log.v(Comm.LOG_TAG, "ORG_ID: "+ ORG_ID);

                initList();

            } else if (requestCode == Comm.SUBINV) {
                String sub_inv = data.getStringExtra("SUBINV_CODE");
                String con = data.getStringExtra("LOCATOR_CONTROL");
                String pallet = data.getStringExtra("PALLET_CONTROL");
                String tro_tran_id = data.getStringExtra("LOC_TRO_TRSF_ID");
                String tro_tran_code = data.getStringExtra("LOC_TRO_TRSF_CODE");
                int pos = data.getIntExtra("POSITION", 1);

                TranTroItem item = (TranTroItem) adapter.getItem(pos);
                item.setSubInvCodeFrom(sub_inv);
                item.setLocatorControl(con);
                item.setPalletControl(pallet);
                item.setInvLocIDFrom(tro_tran_id);
                item.setLocatorCodeFrom(tro_tran_code);

                tranList.set(pos, item);
                adapter.notifyDataSetChanged();

            } else if (requestCode == Comm.LOC) {
                String loc = data.getStringExtra("LOCATOR_CODE");
                String loc_id = data.getStringExtra("INVENTORY_LOCATION_ID");
                int pos = data.getIntExtra("POSITION", 1);

                TranTroItem item = (TranTroItem) adapter.getItem(pos);
                item.setLocatorCodeFrom(loc);
                item.setInvLocIDFrom(loc_id);

                tranList.set(pos, item);
                adapter.notifyDataSetChanged();

            } else if (requestCode == Comm.PALLET) {
                String pall_no = data.getStringExtra("PALLET_NUMBER");
                String pall_id = data.getStringExtra("PALLET_ID");
                int pos = data.getIntExtra("POSITION", 1);

                TranTroItem item = (TranTroItem) adapter.getItem(pos);
                item.setPalletNum(pall_no);
                item.setPalletId(pall_id);

                tranList.set(pos, item);
                adapter.notifyDataSetChanged();

            } else if (requestCode == Comm.LOT) {
                String lot_num = data.getStringExtra("LOT_NUMBER");
                int pos = data.getIntExtra("POSITION", 1);

                TranTroItem item = (TranTroItem) adapter.getItem(pos);

                item.setLotNumber(lot_num);

                tranList.set(pos, item);
                adapter.notifyDataSetChanged();
            }
        }
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(btn_back)){
                finish();

            }else if (v.equals(btn_ship)) {
                if(edit_tro_num.getText().toString().replace(" ", "").equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(TranTroActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.alert_tro_num))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
                }else{
                    int count = adapter.getCount();
                    int saveCount = 0;

                    for(int i = 0; i < count; i++){
                        TranTroItem item = (TranTroItem) adapter.getItem(i);

                        if(item.getCheckItem().equals("True")){
                            saveCount++;
                        }
                    }

                    if(saveCount==0){
                        AlertDialog.Builder builder = new AlertDialog.Builder(TranTroActivity.this);
                        builder
                                .setTitle(getString(R.string.warning))
                                .setMessage(getString(R.string.alert_no_select_tro))
                                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                })
                                .show();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(TranTroActivity.this);
                        builder
                                .setTitle(getString(R.string.decision))
                                .setMessage(getString(R.string.alert_set_ship))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        new setTroTranTask().execute();
                                    }
                                })
                                .setNegativeButton(getString(R.string.no), null)
                                .show();
                    }

                }

            }else if (v.equals(btn_search)) {
                searchTro();

            }else if (v.equals(edit_tro_num)) {
                edit_tro_num.setSelection(edit_tro_num.length());

            }else if (v.equals(btn_find)) {
                findFlag = true;
                initList();
                getTroList();

            }else if (v.equals(btn_date)) {
                DatePickerDialog datepicker = new DatePickerDialog(TranTroActivity.this, mPickSetListener, pYear, pMonth, pDay);
                datepicker.getDatePicker().setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                datepicker.show();
            }else if (v.equals(text_click1) || v.equals(text_click2) || v.equals(text_click3) ||
                    v.equals(text_tro_org_from) || v.equals(text_tro_org_to) || v.equals(text_tro_exp_date)) {
                selScan = -1;

                try{
                    for(int i = 0; i < tranList.size(); i++){
                        tranList.get(i).setBackColor("N");
                    }

                    adapter.notifyDataSetChanged();
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

    private void searchTro() {
        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
        param.put("ouId", Util.nullString(OU_ID, ""));
        param.put("orgId",  Util.nullString(ORG_ID, ""));
        param.put("searchTro",  Util.nullString(edit_tro_num.getText().toString(), ""));

        new TroNoSearchTask().execute(param);
    }

    private void getTroList() {
        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
        param.put("ouId",  Util.nullString(OU_ID, ""));
        param.put("orgId",  Util.nullString(ORG_ID, ""));
        param.put("troNo",  Util.nullString(edit_tro_num.getText().toString(), ""));
        param.put("userNo",  Util.nullString(S_USER_ID, ""));

        new GetTranTroListTask().execute(param);
    }

    private class TroNoSearchTask extends AsyncTask<LinkedHashMap<String, String>, Void, String>{
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(TranTroActivity.this, "", getString(R.string.querying));
        }

        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL+"/api/tran/troNoList");
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(TranTroActivity.this)
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

                        edit_tro_num.setText(Util.nullString(data.getString("SHIPMENT_NO"), ""));
                        text_tro_org_from.setText(Util.nullString(data.getString("ORG_CODE_FROM"), ""));
                        text_tro_org_to.setText(Util.nullString(data.getString("ORG_CODE_TO"), ""));
                        text_date.setText(Util.nullString(data.getString("SHIPMENT_DATE"), ""));
                        text_tro_exp_date.setText(Util.nullString(data.getString("EXP_RECEIPT_DATE"), ""));

                        OU_ID = Util.nullString(data.getString("OU_ID"), "");
                        ORG_ID = Util.nullString(data.getString("ORG_ID_FROM"), "");

                        Log.v(Comm.LOG_TAG, "OU_ID: "+ OU_ID);
                        Log.v(Comm.LOG_TAG, "ORG_ID: "+ ORG_ID);

                        initList();

                    }else{
                        Intent intent = new Intent(getApplicationContext(), PopTranTroActivity.class);
                        intent.putExtra("TRO_NUM", edit_tro_num.getText().toString());
                        startActivityForResult(intent, Comm.TRANTRO);
                    }
                }
            } catch (JSONException e) {
                Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
            } catch (Exception e1) {
                Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
            }

            progressDialog.dismiss();
        }
    }

    private class GetTranTroListTask extends AsyncTask<LinkedHashMap<String, String>, Void, String> implements View.OnClickListener{
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(TranTroActivity.this, "", getString(R.string.querying));
        }

        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL+"/api/tran/troNoItemList");
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

            ListView list_tran_tro = (ListView) findViewById(R.id.list_tran_tro);

            try {
                JSONObject obj = new JSONObject(result);
                if(!obj.getString("status").equals("S")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(TranTroActivity.this)
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

                    if(findFlag){
                        checkList(list);
                    }

                    initList();

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject data = list.getJSONObject(i);
                        TranTroItem info = null;

                        String ship_qty = Util.nullString(data.getString("SHIPMENT_QTY"), "");
                        if(!ship_qty.isEmpty()){
                            info = new TranTroItem(Util.nullString(data.getString("SHIPMENT_LINE_ID"), "")
                                    , Util.nullString(data.getString("SHIPMENT_HEADER_ID"), "")
                                    , Util.nullString(data.getString("SHIPMENT_DATE"), "")
                                    , Util.nullString(data.getString("EXPECTED_RECEIPT_DATE"), "")
                                    , Util.nullString(data.getString("ORG_ID_FROM"), "")
                                    , Util.nullString(data.getString("ORG_CODE_FROM"), "")
                                    , Util.nullString(data.getString("SUBINV_CODE_FROM"), "")
                                    , Util.nullString(data.getString("INV_LOCATION_ID_FROM"), "")
                                    , Util.nullString(data.getString("LOCATOR_CODE_FROM"), "")
                                    , Util.nullString(data.getString("INVENTORY_ITEM_ID"), "")
                                    , Util.nullString(data.getString("ITEM_CODE"), "")
                                    , Util.nullString(data.getString("ITEM_DESC"), "")
                                    , Util.nullString(data.getString("LOT_CONTROL_CODE"), "")
                                    , Util.nullString(data.getString("TRANSACTION_UOM"), "")
                                    , Util.nullString(data.getString("SHIPMENT_QTY"), "")
                                    , Util.nullString(data.getString("LOT_NUMBER"), "")
                                    , Util.nullString(data.getString("ORG_ID_TO"), "")
                                    , Util.nullString(data.getString("ORG_CODE_TO"), "")
                                    , Util.nullString(data.getString("SUBINV_CODE_TO"), "")
                                    , Util.nullString(data.getString("INV_LOCATION_ID_TO"), "")
                                    , Util.nullString(data.getString("LOCATOR_CODE_TO"), "")
                                    , Util.nullString(data.getString("LOCATOR_CONTROL_FROM"), "")
                                    , Util.nullString(data.getString("PALLET_CONTROL_FROM"), "")
                                    , Util.nullString(data.getString("PALLET_ID"), "")
                                    , Util.nullString(data.getString("PALLET_NUMBER"), "")
                                    , OU_ID
                                    , ORG_ID
                                    , Util.nullString(text_date.getText().toString(), "")
                                    , S_USER_ID
                                    ,"True"
                                    , "N"
                                    , this
                            );
                        }

                        tranList.add(info);
                    }
                    adapter = new TranTroAdapter(TranTroActivity.this, tranList);
                    list_tran_tro.setAdapter(adapter);
                    list_tran_tro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        }
                    });
                }
            } catch (JSONException e) {
                Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
            } catch (Exception e1) {
                Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
            }

            progressDialog.dismiss();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_scan5:
                case R.id.layout_scan6:
                    selected = (int) v.getTag();
                    selScan = selected;

                    for(int i = 0; i < tranList.size(); i++){
                        tranList.get(i).setBackColor("N");
                    }

                    TranTroItem backItem = (TranTroItem) adapter.getItem(selected);
                    backItem.setBackColor("T");
                    tranList.set(selected, backItem);

                    adapter.notifyDataSetChanged();
            }
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

            //locator 관련 바코드일 때
            if(code.length == 4 && code[0].equals("L")) {
                if(selScan == -1) {
                    TranTroActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(TranTroActivity.this)
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

                //tro 관련 바코드일 때
            }else if(code.length == 1 && code[0].substring(0,2).equals("TR")) {
                if(selScan == -1) {
                    scanTro = data;

                    LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                    param.put("ouId", Util.nullString(OU_ID, ""));
                    param.put("orgId", Util.nullString(ORG_ID, ""));
                    param.put("searchTro", Util.nullString(data, ""));

                    new TranTroScanTask().execute(param);
                }else{
                    TranTroActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(TranTroActivity.this)
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
                TranTroActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(TranTroActivity.this)
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

    private class TranTroScanTask extends AsyncTask<LinkedHashMap<String, String>, Void, String>{
        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL+"/api/tran/troNoScanList");
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
                    Intent intent = new Intent(getApplicationContext(), PopTranTroActivity.class);
                    intent.putExtra("TRO_NUM", scanTro);
                    startActivityForResult(intent, Comm.TRANTRO);
                }else{
                    JSONObject data = list.getJSONObject(0);

                    edit_tro_num.setText(Util.nullString(data.getString("SHIPMENT_NO"), ""));
                    text_tro_org_from.setText(Util.nullString(data.getString("ORG_CODE_FROM"), ""));
                    text_tro_org_to.setText(Util.nullString(data.getString("ORG_CODE_TO"), ""));
                    text_date.setText(Util.nullString(data.getString("SHIPMENT_DATE"), ""));
                    text_tro_exp_date.setText(Util.nullString(data.getString("EXP_RECEIPT_DATE"), ""));

                    OU_ID = Util.nullString(data.getString("OU_ID"), "");
                    ORG_ID = Util.nullString(data.getString("ORG_ID_FROM"), "");

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
    };

    private class setTroTranTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(TranTroActivity.this, "", getString(R.string.progressing));
        }

        @Override
        protected String doInBackground(Void... voids) {
            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            int count = adapter.getCount();
            String saveData = null;

            for(int i = 0; i < count; i++){
                TranTroItem item = (TranTroItem) adapter.getItem(i);
                if(item.getCheckItem().equals("True")){
                    saveData = saveData + "&idx="+ i
                            + "&shipmentHeaderId=" 		+ item.getShipmentHeaderId()
                            + "&shipmentLineId=" 		+ item.getShipmentLineId()
                            + "&orgCodeFrom=" 			+ item.getOrgCodeFrom()
                            + "&subinvCodeFrom=" 		+ item.getSubinvCodeFrom()
                            + "&invLocationIdFrom=" 	+ item.getInvLocationIdFrom()
                            + "&orgCodeTo=" 			+ item.getOrgCodeTo()
                            + "&subinvCodeTo=" 		    + item.getSubInvCodeTo()
                            + "&invLocationIdTo=" 	    + item.getInvLocationIdTo()
                            + "&palletId=" 			    + item.getPalletId()
                            + "&itemCode=" 		        + item.getItemCode()
                            + "&transactionUom=" 		+ item.getTransactionUom()
                            + "&lotNumber=" 			+ item.getLotNumber()
                            + "&shipmentQty=" 			+ item.getShipmentQty()
                            + "&ouId=" 				    + OU_ID
                            + "&orgId=" 				+ ORG_ID
                            + "&userNo=" 				+ S_USER_ID;
                }
            }

            params.put("list", saveData);

            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL+"/api/tran/setTroTran");
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

                if (!obj.getString("status").equals("S")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TranTroActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage("Shipment Fail \n\n" +obj.getString("msg"))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TranTroActivity.this)
                            .setTitle(getString(R.string.note))
                            .setMessage(getString(R.string.alert_comp_tro_tran))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

                                    param.put("ouId", Util.nullString(OU_ID, ""));
                                    param.put("orgId", Util.nullString(ORG_ID, ""));
                                    param.put("troNo", Util.nullString(edit_tro_num.getText().toString(), ""));
                                    param.put("userNo", Util.nullString(S_USER_ID, ""));

                                    findFlag = false;

                                    new GetTranTroListTask().execute(param);
                                }
                            });
                    builder.show();

                }

            } catch (Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TranTroActivity.this)
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

                    final TranTroItem item = (TranTroItem) adapter.getItem(selScan);

                    item.setInvLocIDFrom("");
                    item.setLocatorControl(itemList.getString("LOCATOR_CONTROL"));
                    item.setPalletControl(itemList.getString("PALLET_CONTROL"));
                    item.setLocatorCodeFrom("");
                    item.setSubInvCodeFrom("");
                    item.setPalletId("");
                    item.setPalletNum("");

                    if(list.length() == 0){
                        AlertDialog.Builder builder = new AlertDialog.Builder(TranTroActivity.this);
                        builder
                                .setTitle(getString(R.string.note))
                                .setMessage(getString(R.string.alert_no_item))
                                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent(getApplicationContext(), PopLocaActivity.class);

                                        Log.v(Comm.LOG_TAG, "selScan: " + selScan);

                                        item.setSubInvCodeFrom(scanSubInv);

                                        tranList.set(selected, item);
                                        adapter.notifyDataSetChanged();

                                        intent.putExtra("SUBINVENTORY_CODE", item.getSubinvCodeFrom());
                                        intent.putExtra("POSITION", selScan);
                                        intent.putExtra("ORG_ID", item.getOrgId());

                                        startActivityForResult(intent, Comm.LOC);
                                    }
                                })
                                .show();
                    }else{

                        if(list.length() != 0){
                            JSONObject data = list.getJSONObject(0);
                            item.setInvLocIDFrom(data.getString("INVENTORY_LOCATION_ID"));
                            item.setLocatorCodeFrom(data.getString("LOCATOR_CODE"));
                        }

                        if(list1.length() != 0){
                            JSONObject data1 = list1.getJSONObject(0);
                            item.setPalletControl(data1.getString("PALLET_CONTROL"));
                            item.setSubInvCodeFrom(data1.getString("SUBINVENTORY_CODE"));
                            item.setLocatorControl(data1.getString("LOCATOR_CONTROL"));
                        }

                        if(list2.length() == 1) {
                            JSONObject data2 = list2.getJSONObject(0);
                            item.setPalletId(data2.getString("PALLET_ID"));
                            item.setPalletNum(data2.getString("PALLET_NUMBER"));
                        }

                        tranList.set(selected, item);
                        adapter.notifyDataSetChanged();
                    }

                }else if(list1.length() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(TranTroActivity.this);
                    builder
                            .setTitle(getString(R.string.note))
                            .setMessage(getString(R.string.alert_no_item))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(getApplicationContext(), PopSubInvActivity.class);
                                    TranTroItem troItem = (TranTroItem) adapter.getItem(selScan);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(TranTroActivity.this);
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
        if(!tranList.isEmpty()){
            tranList.clear();
            adapter.notifyDataSetChanged();
        }
    }
}


