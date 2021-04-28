package com.pulmuone.mrtina.popupTranLot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
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

public class PopTranLotActivity extends Activity{

    private PopTranLotAdapter lotAdapter;
    ArrayList<PopTranLotItem> lotList = null;

    private ListView list_lot = null;
    private RelativeLayout btn_close = null;

    SharedPreferences pref = null;

    private String S_ORG_ID = null;
    private String ITEM_ID = null;

    private Network network = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_lot);

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);
        S_ORG_ID = pref.getString("ORG_ID", "");

        btn_close = (RelativeLayout) findViewById(R.id.layout_close);
        btn_close.setOnClickListener(onClickListener);

        Intent intent = getIntent();

        ITEM_ID = intent.getStringExtra("ITEM_ID");

        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
        param.put("orgId", S_ORG_ID);
        param.put("inventoryItemId", ITEM_ID);

        this.network = new Network(this, listener, "/api/tran/tranLotList");
        this.network.execute(param);
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
            list_lot = (ListView)findViewById(R.id.list_lot);
            lotList = new ArrayList<>();

            try {
                JSONArray list = obj.getJSONArray("list");

                checkList(list);

                for(int i=0; i<list.length(); i++){
                    JSONObject data = list.getJSONObject(i);

                    PopTranLotItem info = new PopTranLotItem(Util.nullString(data.getString("LOT_NUMBER"),"")
                            , Util.nullString(data.getString("SUBINVENTORY_CODE"),"")
                            , Util.nullString(data.getString("LOCATOR_CODE"),"")
                            , Util.nullString(data.getString("LOCATOR_CONTROL"),"")
                            , Util.nullString(data.getString("INVENTORY_LOCATION_ID"),"")
                            , Util.nullString(data.getString("PRIMARY_QUANTITY"),"")
                            , Util.nullString(data.getString("EXPIRATION_DATE"),"")
                    );
                    lotList.add(info);
                }
                lotAdapter = new PopTranLotAdapter(PopTranLotActivity.this, lotList);
                list_lot.setAdapter(lotAdapter);
                list_lot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        Intent intent = new Intent();
                        PopTranLotItem item = (PopTranLotItem) lotAdapter.getItem(position);

                        intent.putExtra("LOT_NUMBER", item.LOT_NUMBER);
                        intent.putExtra("SUBINVENTORY_CODE", item.SUBINVENTORY_CODE);
                        intent.putExtra("LOCATOR_CODE", item.LOCATOR_CODE);
                        intent.putExtra("LOCATOR_CONTROL", item.LOCATOR_CONTROL);
                        intent.putExtra("INVENTORY_LOCATION_ID", item.INVENTORY_LOCATION_ID);
                        intent.putExtra("PRIMARY_QUANTITY", item.PRIMARY_QUANTITY);

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
            AlertDialog.Builder builder = new AlertDialog.Builder(PopTranLotActivity.this);
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


