package com.pulmuone.mrtina.popupLot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
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
import com.pulmuone.mrtina.comm.HttpClient;
import com.pulmuone.mrtina.comm.Network;
import com.pulmuone.mrtina.comm.NetworkEvent;
import com.pulmuone.mrtina.recvPo.RecvPoAdapter;
import com.pulmuone.mrtina.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PopLotActivity extends Activity {

    private PopLotAdapter poAdapter;
    ArrayList<PopLotItem> lotList = null;

    private RelativeLayout btn_close = null;

    SharedPreferences pref = null;
    private int list_position = 0;

    private Network network = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_lot);

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);

        btn_close = (RelativeLayout) findViewById(R.id.layout_close);
        btn_close.setOnClickListener(onClickListener);

        Intent intent = getIntent();
        list_position = intent.getIntExtra("POSITION", 1);

        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
        param.put("ouId", Util.nullString(intent.getStringExtra("OU_ID"), ""));
        param.put("orgId", Util.nullString(intent.getStringExtra("ORG_ID"), ""));
        param.put("poNo", Util.nullString(intent.getStringExtra("PO_NO"), ""));
        param.put("userNo", Util.nullString(intent.getStringExtra("USER_NO"), ""));
        param.put("poHeaderId", Util.nullString(intent.getStringExtra("PO_HEADER_ID"), ""));
        param.put("poLineId", Util.nullString(intent.getStringExtra("PO_LINE_ID"), ""));
        param.put("itemId", Util.nullString(intent.getStringExtra("ITEM_ID"), ""));
        param.put("searchLot", Util.nullString(intent.getStringExtra("SEARCH_LOT"), ""));

        network = new Network(this, listener, "/api/recv/poLotList");
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
            ListView list_pop_po = (ListView) findViewById(R.id.list_lot);

            lotList = new ArrayList<>();

            try {
                JSONArray list = obj.getJSONArray("list");

                checkList(list);

                for(int i=0; i<list.length(); i++){
                    JSONObject data = list.getJSONObject(i);

                    PopLotItem info = new PopLotItem(Util.nullString(data.getString("LOT_NUMBER"),"")
                            , Util.nullString(data.getString("EXPIRATION_DATE"),"")
                            , Util.nullString(data.getString("ORIGINATION"),"")
                            , Util.nullString(data.getString("SORTING"),"")
                    );
                    lotList.add(info);
                }
                poAdapter = new PopLotAdapter(PopLotActivity.this, lotList);
                list_pop_po.setAdapter(poAdapter);
                list_pop_po.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        Intent intent = new Intent();
                        PopLotItem item = (PopLotItem) poAdapter.getItem(position);

                        intent.putExtra("LOT_NUMBER", item.LOT_NUMBER);
                        intent.putExtra("EXPIRATION_DATE", item.EXPIRATION_DATE);
                        intent.putExtra("POSITION", list_position);

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
    };

    private void checkList(JSONArray list) {
        if(list.length() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(PopLotActivity.this);
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


