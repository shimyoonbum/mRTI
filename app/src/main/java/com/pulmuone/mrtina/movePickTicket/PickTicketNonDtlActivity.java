package com.pulmuone.mrtina.movePickTicket;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.pulmuone.mrtina.R;
import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.comm.HttpClient;
import com.pulmuone.mrtina.comm.Network;
import com.pulmuone.mrtina.comm.NetworkEvent;
import com.pulmuone.mrtina.popupLot.PopLotItem;
import com.pulmuone.mrtina.recvPo.RecvPoActivity;
import com.pulmuone.mrtina.recvPo.RecvPoItem;
import com.pulmuone.mrtina.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PickTicketNonDtlActivity extends Activity {

    private PickTicketNonDtlAdapter moveAdapter;

    SharedPreferences pref = null;
    private String S_USER_ID = null;
    private String S_OU_ID = null;
    private String S_ORG_ID = null;
    private String S_PICK_TYPE = null;
    private String S_BAR_CODE = null;
    private String S_GROUP_ID = null;

    private Button btn_move = null;
    private ListView listView;

    private RelativeLayout btn_close = null;
    private Network network = null;

    ArrayList<PickTicketNonDtlItem> moveArr = null;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_nonplt_dtl);

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);
        S_USER_ID = pref.getString("USER_ID", "");

        btn_close = (RelativeLayout) findViewById(R.id.layout_close);
        btn_close.setOnClickListener(onClickListener);
        btn_move = (Button) findViewById(R.id.btn_move);
        btn_move.setOnClickListener(onClickListener);

        Intent intent = getIntent();
        S_OU_ID = Util.nullString(intent.getStringExtra("OU_ID"), "");
        S_ORG_ID = Util.nullString(intent.getStringExtra("ORG_ID"), "");
        S_PICK_TYPE = Util.nullString(intent.getStringExtra("pickType"), "");
        S_BAR_CODE = Util.nullString(intent.getStringExtra("soTroNoBarCode"), "");
        S_GROUP_ID = Util.nullString(intent.getStringExtra("scanGroupId"), "");
        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

        param.put("ouId", S_OU_ID);
        param.put("orgId", S_ORG_ID);
        param.put("userNo", Util.nullString(S_USER_ID, ""));
        param.put("scanGroupId", S_GROUP_ID);
        param.put("pickType", S_PICK_TYPE);
        param.put("soTroNoBarCode", S_BAR_CODE);

        network = new Network(PickTicketNonDtlActivity.this, listener, "/api/move/pickNonPltItemList");
        network.execute(param);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.equals(btn_close)) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();

            }else if (v.equals(btn_move)) {
                int count = moveAdapter.getCount();
                int cnt = 0;
                for(int i = 0; i < count; i++) {
                    PickTicketNonDtlItem item = (PickTicketNonDtlItem) moveAdapter.getItem(i);

                    if (item.CHECK_ITEM.equals("True")) {
                        cnt++;
                    }
                }

                if(cnt > 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(PickTicketNonDtlActivity.this);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(PickTicketNonDtlActivity.this)
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
        }
    };

    private void setMoving(){


        new setMoving().execute();
    }

    private class setMoving extends AsyncTask<LinkedHashMap<String, String>, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(PickTicketNonDtlActivity.this, "", getString(R.string.progressing));
        }

        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            int count = moveAdapter.getCount();
            String saveData = "";
            Log.v(Comm.LOG_TAG, "count-count-count: "+ count);
            for(int i = 0; i < count; i++){
                PickTicketNonDtlItem item = (PickTicketNonDtlItem) moveAdapter.getItem(i);
                Log.v(Comm.LOG_TAG, "item.CHECK_ITEM: "+ item.CHECK_ITEM);
                Log.v(Comm.LOG_TAG, "item.RESERVATION_ID: "+ item.RESERVATION_ID);
                if(item.CHECK_ITEM.equals("True")){
                    saveData = saveData + "&idx="+ i
                            + "&ouId=" 				+ S_OU_ID
                            + "&orgId=" 			+ S_ORG_ID
                            + "&pickType=" 		    + S_PICK_TYPE
                            + "&soTroNoBarCode=" 	+ S_BAR_CODE
                            + "&scanGroupId=" 		+ S_GROUP_ID
                            + "&reservationId=" 	+ item.RESERVATION_ID
                            + "&userNo=" 			+ S_USER_ID;


                }
            }
            Log.v(Comm.LOG_TAG, "saveData-saveData: "+ saveData);
            LinkedHashMap<String, String> param = new LinkedHashMap<>();
            param.put("list", saveData);

            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL + "/api/move/setNonPickMove");
            http.addAllParameters(param);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(PickTicketNonDtlActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage("Move Fail \n\n" + obj.getString("msg"))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PickTicketNonDtlActivity.this)
                            .setTitle(getString(R.string.note))
                            .setMessage(getString(R.string.alert_comp_move))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String status = null;
                                    try {
                                        status = obj.getString("status");

                                        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

                                        param.put("ouId", S_OU_ID);
                                        param.put("orgId", S_ORG_ID);
                                        param.put("userNo", Util.nullString(S_USER_ID, ""));
                                        param.put("scanGroupId", S_GROUP_ID);
                                        param.put("pickType", S_PICK_TYPE);
                                        param.put("soTroNoBarCode", S_BAR_CODE);

                                        network = new Network(PickTicketNonDtlActivity.this, listener, "/api/move/pickNonPltItemList");
                                        network.execute(param);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                    builder.show();

                }

            } catch (Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PickTicketNonDtlActivity.this)
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
        public void onPostExecute(JSONObject obj, String api) {
            listView = (ListView)findViewById(R.id.list_ticket_dtl);
            moveArr = new ArrayList<>();

            if(api.equals("/api/move/pickNonPltItemList")) {
                try {
                    JSONArray list = obj.getJSONArray("list");

                    //checkList(list);

                    for(int i=0; i<list.length(); i++){
                        JSONObject data = list.getJSONObject(i);

                        PickTicketNonDtlItem info = new PickTicketNonDtlItem(Util.nullString(data.getString("PICK_QTY"),"")
                                , Util.nullString(data.getString("TRANSACTION_UOM"),"")
                                , Util.nullString(data.getString("LOCATOR_CODE"),"")
                                , Util.nullString(data.getString("ITEM_DESC"),"")
                                , Util.nullString(data.getString("SUBINVENTORY_CODE"),"")
                                , Util.nullString(data.getString("ITEM_CODE"),"")
                                , Util.nullString(data.getString("PALLET_NUMBER"),"")
                                , Util.nullString(data.getString("LOT_NUMBER"),"")
                                , "True"
                                , Util.nullString(data.getString("RESERVATION_ID"),"")
                        );
                        moveArr.add(info);
                    }

                    moveAdapter = new PickTicketNonDtlAdapter(PickTicketNonDtlActivity.this, moveArr);
                    listView.setAdapter(moveAdapter);

                } catch (JSONException e) {
                    Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                } catch (Exception e1) {
                    Log.v(Comm.LOG_TAG, "Exception Error ::"+e1.getMessage());
                }

            }


        }
    };

    private void checkList(JSONArray list) {
        if(list.length() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(PickTicketNonDtlActivity.this);
            builder
                    .setTitle(getString(R.string.note))
                    .setMessage(getString(R.string.alert_no_item))
                    .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }
}
