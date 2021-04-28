package com.pulmuone.mrtina.movePickTicket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pulmuone.mrtina.R;
import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.comm.HttpClient;
import com.pulmuone.mrtina.comm.MyEvent;
import com.pulmuone.mrtina.comm.Network;
import com.pulmuone.mrtina.comm.NetworkEvent;
import com.pulmuone.mrtina.comm.Scan;
import com.pulmuone.mrtina.popupPickTicket.PopPickTicketActivity;
import com.pulmuone.mrtina.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;

public class PickTicketMoveActivity extends Activity {

    private PickTicketMoveAdapter moveAdapter;
    ArrayList<PickTicketMoveItem> itemList = null;

    SharedPreferences pref = null;
    private String S_USER_ID = null;
    private String ORG_ID = null;
    private String OU_ID = null;

    private String headerId = null;
    private String so_tro_codeNo = null;
    private String shipId = null;
    private String pickType = null;
    private String scanId = null;
    private String palletNum = null;
    private String userType = null;
    private int count = 0;

    private int pYear, pMonth, pDay;

    private ListView listView;

    private RelativeLayout btn_back = null;
    private LinearLayout layout_list = null;
    private LinearLayout btn_search = null;
    private LinearLayout btn_date = null;

    private TextView text_date = null;
    private TextView text_alert = null;
    private EditText edit_so_num = null;
    private TextView text_to = null;
    private TextView text_ship_date = null;
    private TextView text_item = null;
    private TextView text_nonplt = null;
    private Button btn_nonplt = null;
    private Button btn_remain = null;
    private Button btn_move = null;

    private Network network = null;

    private ListView list_item;
    private ProgressDialog progressDialog;

    private Scan Scan = null; //SCANNER CODE

    private boolean findFlag = true;
    private String listType = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_ticket);

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);
        S_USER_ID = pref.getString("USER_ID", "");

        btn_back = (RelativeLayout) findViewById(R.id.layout_back);
        layout_list = (LinearLayout) findViewById(R.id.layout_pallet_list);
        btn_date = (LinearLayout) findViewById(R.id.layout_date);
        btn_search = (LinearLayout) findViewById(R.id.layout_search_no);

        text_date = (TextView) findViewById(R.id.txt_tran_date);
        edit_so_num = (EditText) findViewById(R.id.txt_so_no);
        text_to = (TextView) findViewById(R.id.txt_to);
        text_ship_date = (TextView) findViewById(R.id.txt_ship_date);
        text_item = (TextView) findViewById(R.id.txt_remain_item);
        text_alert = (TextView) findViewById(R.id.txt_alert);
        text_nonplt = (TextView) findViewById(R.id.txt_nonplt_item);

        btn_nonplt = (Button) findViewById(R.id.btn_nonplt);
        btn_remain = (Button) findViewById(R.id.btn_remain);
        btn_move = (Button) findViewById(R.id.btn_move);

        btn_nonplt.setOnClickListener(onClickListener);
        btn_remain.setOnClickListener(onClickListener);
        btn_move.setOnClickListener(onClickListener);
        btn_back.setOnClickListener(onClickListener);
        btn_date.setOnClickListener(onClickListener);
        btn_search.setOnClickListener(onClickListener);

        listView = (ListView) findViewById(R.id.list_ticket);
        itemList = new ArrayList<>();

        edit_so_num.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_so_num.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_so_num.setOnClickListener(onClickListener);

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
        if (resultCode == RESULT_OK) {
            if (requestCode == Comm.TRO) {
                edit_so_num.setText(data.getStringExtra("SO_TRO_NO"));
                text_to.setText(data.getStringExtra("SHIP_TO_ORG_CODE"));
                text_ship_date.setText(data.getStringExtra("SHIP_DATE"));

                headerId = data.getStringExtra("SO_TRO_HEADER_ID");
                so_tro_codeNo = data.getStringExtra("SO_TRO_NO_BAR_CODE");
                shipId = data.getStringExtra("SHIP_TO_ORG_ID");
                pickType = data.getStringExtra("PICK_TYPE");

                OU_ID = Util.nullString(data.getStringExtra("OU_ID"), "");
                ORG_ID = Util.nullString(data.getStringExtra("ORG_ID"), "");

                Log.v(Comm.LOG_TAG, "OU_ID: "+ OU_ID);
                Log.v(Comm.LOG_TAG, "ORG_ID: "+ ORG_ID);

                text_alert.setVisibility(View.VISIBLE);

                initList();
                getScanId();
            }else if (requestCode == Comm.MOVE_DTL) {
                searchNoItem();
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
            text_date.setText(result);
        }
    };

    private void setDateFormat(int pYear, int pMonth, int pDay) {
        DecimalFormat mmddFormat = new DecimalFormat("00");
        DecimalFormat yyyyFormat = new DecimalFormat("0000");
        String result = mmddFormat.format(pMonth + 1) + "-" + mmddFormat.format(pDay) + "-" + yyyyFormat.format(pYear);

        text_date.setText(result);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.equals(btn_back)) {
                finish();

            } else if (v.equals(edit_so_num)) {
                edit_so_num.setSelection(edit_so_num.length());

            } else if (v.equals(btn_date)) {
                DatePickerDialog datepicker = new DatePickerDialog(PickTicketMoveActivity.this, mPickSetListener, pYear, pMonth, pDay);
                datepicker.getDatePicker().setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                datepicker.show();

            } else if (v.equals(btn_remain)) {
                Intent intent = new Intent(PickTicketMoveActivity.this, PickTicketMoveDtlActivity.class);
                intent.putExtra("scanGroupId", scanId);
                intent.putExtra("pickType", pickType);
                intent.putExtra("soTroNoBarCode", so_tro_codeNo);
                intent.putExtra("OU_ID", OU_ID);
                intent.putExtra("ORG_ID", ORG_ID);
                startActivity(intent);
            } else if (v.equals(btn_nonplt)) {
                Log.v(Comm.LOG_TAG, "btn_nonplt-btn_nonplt-btn_nonplt-btn_nonplt: ");
                Intent intent = new Intent(PickTicketMoveActivity.this, PickTicketNonDtlActivity.class);
                intent.putExtra("scanGroupId", scanId);
                intent.putExtra("pickType", pickType);
                intent.putExtra("soTroNoBarCode", so_tro_codeNo);
                intent.putExtra("OU_ID", OU_ID);
                intent.putExtra("ORG_ID", ORG_ID);
                startActivityForResult(intent,Comm.MOVE_DTL);

            } else if (v.equals(btn_move)) {
                try{
                    if(count == 0){

                        Log.v(Comm.LOG_TAG, "listType: "+ listType);

                        if(!itemList.isEmpty() && listType.equals("NOP")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(PickTicketMoveActivity.this);
                            builder
                                    .setTitle(getString(R.string.decision))
                                    .setMessage(getString(R.string.alert_set_move))
                                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            setMoving();
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.no),new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(PickTicketMoveActivity.this);
                            builder
                                    .setTitle(getString(R.string.warning))
                                    .setMessage(getString(R.string.alert_wrong_item))
                                    .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            return;
                                        }
                                    })
                                    .show();
                        }
                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(PickTicketMoveActivity.this);
                        builder
                                .setTitle(getString(R.string.decision))
                                .setMessage(getString(R.string.alert_set_move))
                                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        setMoving();
                                    }
                                })
                                .setNegativeButton(getString(R.string.no),new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                    }

                }catch (Exception e){

                    AlertDialog.Builder builder = new AlertDialog.Builder(PickTicketMoveActivity.this);
                    builder
                            .setTitle(getString(R.string.note))
                            .setMessage(getString(R.string.alert_no_move))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();
                }

            } else if (v.equals(btn_search)) {
                findFlag = true;
                searchNum();
                //myListener.onEvent("TR-FT1-00022");

            }
        }
    };

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

            findFlag = true;

            if(data.substring(0,2).equals("SO") || data.substring(0,2).equals("TR")){
                palletNum = "";
                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

                //param.put("ouId", Util.nullString(OU_ID, ""));
                //param.put("orgId", Util.nullString(ORG_ID, ""));
                param.put("ouId", "");
                param.put("orgId", "");
                param.put("userNo", Util.nullString(S_USER_ID, ""));
                param.put("pickNo", Util.nullString(data, ""));

                new PickScanListTask().execute(param);

            }else if(data.substring(0,1).equals("P")){

                palletNum = data;

                searchItemList();
            }
        }
    };

    // ]] SCANNER CODE

    private void searchNum() {
        Intent intent = new Intent(getApplicationContext(), PopPickTicketActivity.class);
        intent.putExtra("PICK_NO", Util.nullString(edit_so_num.getText().toString(),""));
        startActivityForResult(intent, Comm.TRO);
    }

    private void searchNoItem() {

        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

        param.put("ouId", Util.nullString(OU_ID, ""));
        param.put("orgId", Util.nullString(ORG_ID, ""));
        param.put("userNo", Util.nullString(S_USER_ID, ""));
        param.put("scanGroupId", Util.nullString(scanId, ""));
        param.put("pickType", Util.nullString(pickType, ""));
        param.put("soTroNoBarCode", Util.nullString(so_tro_codeNo, ""));

        network = new Network(PickTicketMoveActivity.this, listener, "/api/move/noPickItemList");
        network.execute(param);
    }

    private void searchItemList() {

        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

        param.put("ouId", Util.nullString(OU_ID, ""));
        param.put("orgId", Util.nullString(ORG_ID, ""));
        param.put("userNo", Util.nullString(S_USER_ID, ""));
        param.put("pickType", Util.nullString(pickType, ""));
        param.put("soTroNoBarCode", Util.nullString(so_tro_codeNo, ""));
        param.put("scanGroupId", Util.nullString(scanId, ""));
        param.put("palletNumber", Util.nullString(palletNum, ""));

        new PickItemListTask().execute(param);
    }

    private void getScanId(){
        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

        param.put("userNo", Util.nullString(S_USER_ID, ""));
        param.put("pickType", Util.nullString(pickType, ""));
        param.put("soTroNoBarCode", Util.nullString(so_tro_codeNo, ""));
        param.put("ouId", Util.nullString(OU_ID, ""));
        param.put("orgId", Util.nullString(ORG_ID, ""));

        network = new Network(PickTicketMoveActivity.this, listener, "/api/move/getScanGroupId");
        network.execute(param);
    }

    private void setMoving(){
        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

        param.put("ouId", Util.nullString(OU_ID, ""));
        param.put("orgId", Util.nullString(ORG_ID, ""));
        param.put("userNo", Util.nullString(S_USER_ID, ""));
        param.put("pickType", Util.nullString(pickType, ""));
        param.put("soTroNoBarCode", Util.nullString(so_tro_codeNo, ""));
        param.put("scanGroupId", Util.nullString(scanId, ""));
        param.put("palletNumber", Util.nullString(palletNum, ""));

        new setMoving().execute(param);
    }

    private class PickScanListTask extends AsyncTask<LinkedHashMap<String, String>, Void, String>{
        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL+"/api/move/scanPickNoList");
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

                checkList(list);
                initList();

                for (int i = 0; i < list.length(); i++) {
                    JSONObject data = list.getJSONObject(i);
                    edit_so_num.setText(data.getString("SO_TRO_NO"));
                    text_to.setText(data.getString("SHIP_TO_ORG_CODE"));
                    text_ship_date.setText(data.getString("SHIP_DATE"));

                    headerId = data.getString("SO_TRO_HEADER_ID");
                    so_tro_codeNo = data.getString("SO_TRO_NO_BAR_CODE");
                    shipId = data.getString("SHIP_TO_ORG_ID");
                    pickType = data.getString("PICK_TYPE");

                    OU_ID = Util.nullString(data.getString("OU_ID"), "");
                    ORG_ID = Util.nullString(data.getString("ORGANIZATION_ID"), "");
                    //userType = Util.nullString(data.getString("USER_TYPE"), "");
                    Log.v(Comm.LOG_TAG, "OU_ID: "+ OU_ID);
                    Log.v(Comm.LOG_TAG, "ORG_ID: "+ ORG_ID);
                }
                    getScanId();

            } catch (JSONException e) {
                Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
            } catch (Exception e1) {
                Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
            }
        }
    }

    private class PickItemListTask extends AsyncTask<LinkedHashMap<String, String>, Void, String>{
        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL+"/api/move/pickItemList");
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
                list_item = (ListView) findViewById(R.id.list_ticket);

                JSONObject obj = new JSONObject(result);
                JSONArray list = obj.getJSONArray("list");
                scanId = Util.nullString(obj.getString("SCAN_GROUP_ID"), "");
                listType = obj.getString("LIST_TYPE");
                Log.v(Comm.LOG_TAG, "listType: "+ listType);

                searchNoItem();

                if(itemList.size() > 0){ // 리스트 초기화
                    itemList.clear();
                }

                if(list.length() > 0){
                    text_alert.setVisibility(View.GONE);
                }else{
                    text_alert.setVisibility(View.VISIBLE);
                }

                for(int i=0; i<list.length(); i++){
                    JSONObject data = list.getJSONObject(i);

                    count = Integer.parseInt(obj.getString("count"));

                    PickTicketMoveItem info = new PickTicketMoveItem(
                            (count > 0) ? Util.nullString(data.getString("TRANSACTION_UOM"),"") : Util.nullString(data.getString("PRIMARY_UOM_CODE"),"")
                            , Util.nullString(data.getString("SUBINVENTORY_CODE"),"")
                            , Util.nullString(data.getString("LOCATOR_CODE"),"")
                            , Util.nullString(data.getString("ITEM_DESC"),"")
                            , Util.nullString(data.getString("ITEM_CODE"),"")
                            , Util.nullString(data.getString("ORGANIZATION_ID"),"")
                            , Util.nullString(data.getString("INVENTORY_ITEM_ID"),"")
                            , (count > 0) ? Util.nullString(data.getString("SCAN_QTY"),"") : Util.nullString(data.getString("PRIMARY_QUANTITY"),"")
                            , Util.nullString(data.getString("INVENTORY_LOCATION_ID"),"")
                            , Util.nullString(data.getString("PALLET_NUMBER"),"")
                            , Util.nullString(data.getString("LOT_NUMBER"),"")
                    );

                    // 2020-08-19 yoonbeom 조회 결과 리스트가 존재하고 count가 0 초과인 경우

                    if(count > 0 && list.length() > 0) {
                        layout_list.setBackground(ContextCompat.getDrawable(PickTicketMoveActivity.this, R.drawable.location_layout));
                        itemList.add(info);

                        // 2020-08-19 yoonbeom 조회 결과 리스트가 존재하고 count가 0 인 경우
                    } else if(count == 0 && list.length() > 0) {
                        layout_list.setBackground(ContextCompat.getDrawable(PickTicketMoveActivity.this, R.drawable.alert_layout));

                        Util.alertNoti(PickTicketMoveActivity.this, Comm.SOUND);
                        AlertDialog.Builder builder = new AlertDialog.Builder(PickTicketMoveActivity.this);
                        builder
                                .setTitle(getString(R.string.warning))
                                .setMessage(getString(R.string.alert_wrong_item))
                                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();

                        if(itemList.size() != 0){
                            itemList.clear();
                        }

                        itemList.add(info);

                        break;

                    }
                }

                // 2020-08-19 yoonbeom 조회 결과 리스트가 NULL인 경우
                if(list.length() == 0) {
                    layout_list.setBackground(ContextCompat.getDrawable(PickTicketMoveActivity.this, R.color.white));

                    if(itemList.size() != 0){
                        itemList.clear();
                    }

                    if(findFlag)
                        checkList(list);
                }

                moveAdapter = new PickTicketMoveAdapter(PickTicketMoveActivity.this, itemList);
                list_item.setAdapter(moveAdapter);

            } catch (JSONException e) {
                Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
            } catch (Exception e1) {
                Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
            }

        }
    }

    private class setMoving extends AsyncTask<LinkedHashMap<String, String>, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(PickTicketMoveActivity.this, "", getString(R.string.progressing));
        }

        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {

            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL + "/api/move/setPickMove");
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
                final JSONObject obj = new JSONObject(result);

                if (!obj.getString("status").equals("S") && !obj.getString("status").equals("T")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PickTicketMoveActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage("Move Fail \n\n" + obj.getString("msg"))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PickTicketMoveActivity.this)
                            .setTitle(getString(R.string.note))
                            .setMessage(getString(R.string.alert_comp_move))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    findFlag = false;

                                    String status = null;
                                    try {
                                        status = obj.getString("status");

                                        Log.v(Comm.LOG_TAG, "status : " + status);

                                        if(status.equals("T")){
                                            layout_list.setBackground(ContextCompat.getDrawable(PickTicketMoveActivity.this, R.color.white));

                                            moveAdapter.clear();
                                            moveAdapter.notifyDataSetChanged();

                                            searchNoItem();

                                        }else{
                                            searchItemList();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                    builder.show();

                }

            } catch (Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PickTicketMoveActivity.this)
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

    NetworkEvent listener = new NetworkEvent() {

        @Override
        public void doInBackground(LinkedHashMap<String, String>... params) {
        }

        @Override
        public void onPostExecute(final JSONObject obj, String api) {

            if(api.equals("/api/move/noPickItemList")) {
                try {
                    JSONArray list = obj.getJSONArray("list");
                    JSONArray list1 = obj.getJSONArray("list1");

                    text_item.setText(String.valueOf(list.length()));
                    text_nonplt.setText(String.valueOf(list1.length()));
                } catch (JSONException e) {
                    Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                } catch (Exception e1) {
                    Log.v(Comm.LOG_TAG, "Exception Error ::"+e1.getMessage());
                }

            } else if(api.equals("/api/move/pickNoList")){
                try {
                    JSONArray list = obj.getJSONArray("list");

                    initList();

                    if(list.length() == 1){
                        JSONObject data = list.getJSONObject(0);

                        edit_so_num.setText(Util.nullString(data.getString("SO_TRO_NO"), ""));
                        text_to.setText(Util.nullString(data.getString("SHIP_TO_ORG_CODE"), ""));
                        text_ship_date.setText(Util.nullString(data.getString("SHIP_DATE"), ""));

                        headerId = Util.nullString(data.getString("SO_TRO_HEADER_ID"), "");
                        so_tro_codeNo = data.getString("SO_TRO_NO_BAR_CODE");
                        shipId = Util.nullString(data.getString("SHIP_TO_ORG_ID"), "");
                        pickType = Util.nullString(data.getString("PICK_TYPE"), "");

                        getScanId();

                    }else{
                        Intent intent = new Intent(getApplicationContext(), PopPickTicketActivity.class);
                        startActivityForResult(intent, Comm.TRO);
                    }

                } catch (JSONException e) {
                    Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                } catch (Exception e1) {
                    Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
                }
            } else if(api.equals("/api/move/getScanGroupId")) {
                try {
                    list_item = (ListView) findViewById(R.id.list_ticket);
                    scanId = Util.nullString(obj.getString("SCAN_GROUP_ID"), "");

                    searchNoItem();

                    //JSONArray list = obj.getJSONArray("list");
                    listType = obj.getString("LIST_TYPE");
                    /*
                    Log.v(Comm.LOG_TAG, "listType: "+ listType);

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject data = list.getJSONObject(i);

                        PickTicketMoveItem info = new PickTicketMoveItem(Util.nullString(data.getString("TRANSACTION_UOM"),"")
                                , Util.nullString(data.getString("SUBINVENTORY_CODE"),"")
                                , Util.nullString(data.getString("LOCATOR_CODE"),"")
                                , Util.nullString(data.getString("ITEM_DESC"),"")
                                , Util.nullString(data.getString("ITEM_CODE"),"")
                                , Util.nullString(data.getString("ORGANIZATION_ID"),"")
                                , Util.nullString(data.getString("INVENTORY_ITEM_ID"),"")
                                , Util.nullString(data.getString("PICK_QTY"),"")
                                , Util.nullString(data.getString("INVENTORY_LOCATION_ID"),"")
                                , Util.nullString(data.getString("PALLET_NUMBER"),"")
                                , Util.nullString(data.getString("LOT_NUMBER"),"")
                        );

                        itemList.add(info);
                    }

                    moveAdapter = new PickTicketMoveAdapter(PickTicketMoveActivity.this, itemList);
                    list_item.setAdapter(moveAdapter);
                    */
                } catch (JSONException e) {
                    Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                } catch (Exception e1) {
                    Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
                }
            }
        }
    };

    private void checkList(JSONArray list) {
        if(list.length() == 0){
            layout_list.setBackground(ContextCompat.getDrawable(PickTicketMoveActivity.this, R.color.white));

            AlertDialog.Builder builder = new AlertDialog.Builder(PickTicketMoveActivity.this);
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

        text_alert.setVisibility(View.VISIBLE);
    }

    private void initList(){
        if(!itemList.isEmpty()){
            itemList.clear();
            moveAdapter.notifyDataSetChanged();
            layout_list.setBackground(ContextCompat.getDrawable(PickTicketMoveActivity.this, R.color.white));

            text_alert.setVisibility(View.VISIBLE);
        }
    }
}
