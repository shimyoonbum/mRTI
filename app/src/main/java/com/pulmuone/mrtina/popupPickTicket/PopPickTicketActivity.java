package com.pulmuone.mrtina.popupPickTicket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.pulmuone.mrtina.R;
import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.comm.Network;
import com.pulmuone.mrtina.comm.NetworkEvent;
import com.pulmuone.mrtina.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PopPickTicketActivity extends Activity{

    private PopPickTicketAdapter ticketAdapter;
    ArrayList<PopPickTicketItem> ticketList = null;

    private ListView list_ticket = null;

    private RelativeLayout btn_close = null;

    SharedPreferences pref = null;
    private String S_USER_ID = null;
    private String ORG_ID = null;
    private String OU_ID = null;
    private String PICK_NO = null;

    private Network network = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_ticket);
        Intent intent = getIntent();

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);
        S_USER_ID = pref.getString("USER_ID", "");
        PICK_NO = intent.getStringExtra("PICK_NO");

        btn_close = (RelativeLayout) findViewById(R.id.layout_close);
        btn_close.setOnClickListener(onClickListener);

        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

        param.put("ouId", Util.nullString(OU_ID, ""));
        param.put("orgId", Util.nullString(ORG_ID, ""));
        param.put("userNo", Util.nullString(S_USER_ID, ""));
        param.put("pickNo", Util.nullString(PICK_NO, ""));

        network = new Network(PopPickTicketActivity.this, listener, "/api/move/pickNoList");
        network.execute(param);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if (arg0.equals(btn_close)){
                finish();
            }
        }
    };

    NetworkEvent listener = new NetworkEvent() {
        @Override
        public void doInBackground(LinkedHashMap<String, String>... params) {
        }

        @Override
        public void onPostExecute(JSONObject obj, String api) {
            list_ticket = (ListView)findViewById(R.id.list_ticket);
            ticketList = new ArrayList<>();

            if(api.equals("/api/move/pickNoList")) {
                try {
                    JSONArray list = obj.getJSONArray("list");

                    checkList(list);

                    for(int i=0; i<list.length(); i++){
                        JSONObject data = list.getJSONObject(i);

                        if(list.length() == 1){
                            Intent intent = new Intent();

                            intent.putExtra("SO_TRO_NO_BAR_CODE", Util.nullString(data.getString("SO_TRO_NO_BAR_CODE"),""));
                            intent.putExtra("SO_TRO_HEADER_ID",  Util.nullString(data.getString("SO_TRO_HEADER_ID"),""));
                            intent.putExtra("SO_TRO_NO", Util.nullString(data.getString("SO_TRO_NO"),""));
                            intent.putExtra("SHIP_TO_ORG_CODE", Util.nullString(data.getString("SHIP_TO_ORG_CODE"),""));
                            intent.putExtra("SHIP_TO_ORG_ID", Util.nullString(data.getString("SHIP_TO_ORG_ID"),""));
                            intent.putExtra("SHIP_DATE", Util.nullString(data.getString("SHIP_DATE"),""));
                            intent.putExtra("PICK_TYPE", Util.nullString(data.getString("PICK_TYPE"),""));
                            intent.putExtra("OU_ID", Util.nullString(data.getString("OU_ID"),""));
                            intent.putExtra("ORG_ID", Util.nullString(data.getString("ORGANIZATION_ID"),""));

                            setResult(RESULT_OK, intent);
                            finish();
                        }else{
                            PopPickTicketItem info = new PopPickTicketItem(Util.nullString(data.getString("SO_TRO_NO_BAR_CODE"),"")
                                    , Util.nullString(data.getString("SO_TRO_HEADER_ID"),"")
                                    , Util.nullString(data.getString("SO_TRO_NO"),"")
                                    , Util.nullString(data.getString("SHIP_TO_ORG_CODE"),"")
                                    , Util.nullString(data.getString("SHIP_TO_ORG_ID"),"")
                                    , Util.nullString(data.getString("SHIP_DATE"),"")
                                    , Util.nullString(data.getString("PICK_TYPE"),"")
                                    , Util.nullString(data.getString("OU_ID"),"")
                                    , Util.nullString(data.getString("ORGANIZATION_ID"),"")
                            );

                            ticketList.add(info);
                        }
                    }

                    ticketAdapter = new PopPickTicketAdapter(PopPickTicketActivity.this, ticketList);
                    list_ticket.setAdapter(ticketAdapter);

                    list_ticket.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            Intent intent = new Intent();
                            PopPickTicketItem item = (PopPickTicketItem) ticketAdapter.getItem(position);

                            intent.putExtra("SO_TRO_NO_BAR_CODE", item.SO_TRO_NO_BAR_CODE);
                            intent.putExtra("SO_TRO_HEADER_ID", item.SO_TRO_HEADER_ID);
                            intent.putExtra("SO_TRO_NO", item.SO_TRO_NO);
                            intent.putExtra("SHIP_TO_ORG_CODE", item.SHIP_TO_ORG_CODE);
                            intent.putExtra("SHIP_TO_ORG_ID", item.SHIP_TO_ORG_ID);
                            intent.putExtra("SHIP_DATE", item.SHIP_DATE);
                            intent.putExtra("PICK_TYPE", item.PICK_TYPE);
                            intent.putExtra("OU_ID", item.OU_ID);
                            intent.putExtra("ORG_ID", item.ORG_ID);

                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
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
            AlertDialog.Builder builder = new AlertDialog.Builder(PopPickTicketActivity.this);
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


