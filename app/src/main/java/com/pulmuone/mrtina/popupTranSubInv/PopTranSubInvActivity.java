package com.pulmuone.mrtina.popupTranSubInv;

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

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PopTranSubInvActivity extends Activity{

    private PopTranSubInvAdapter subInvadapter;
    ArrayList<PopTranSubInvItem> subInvList = null;

    private RelativeLayout btn_close = null;

    private ListView list_subInv = null;

    private String inv_id = null;
    private String pallet_id = null;
    private String lot_number = null;
    private String ORG_ID = null;

    private Network network = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_subinv);

        Intent intent = getIntent();
        inv_id = intent.getStringExtra("INV_ID");
        pallet_id = intent.getStringExtra("PALLET_ID");
        lot_number = intent.getStringExtra("LOT_NUMBER");
        ORG_ID = intent.getStringExtra("ORG_ID");

        btn_close = (RelativeLayout) findViewById(R.id.layout_close);
        btn_close.setOnClickListener(onClickListener);

        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
        param.put("orgId", Util.nullString(ORG_ID, ""));
        param.put("inventoryItemId", Util.nullString(inv_id, ""));
        param.put("palletId", Util.nullString(pallet_id, ""));
        param.put("lotNumber", Util.nullString(lot_number, ""));

        this.network = new Network(this, listener, "/api/tran/tranSubinvList");
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
            subInvList = new ArrayList<>();
            list_subInv = (ListView)findViewById(R.id.list_pop_subinv);

            if(api.equals("/api/tran/tranSubinvList")) {
                try {
                    JSONArray list = obj.getJSONArray("list");

                    checkList(list);

                    for(int i=0; i<list.length(); i++){
                        JSONObject data = list.getJSONObject(i);

                        PopTranSubInvItem info = new PopTranSubInvItem(Util.nullString(data.getString("SUBINVENTORY_CODE"), "")
                                , Util.nullString(data.getString("SUBINVENTORY_NAME"), "")
                                , Util.nullString(data.getString("LOCATOR_CONTROL"), "")
                                , Util.nullString(data.getString("STATUS_ID"), "")
                        );
                        subInvList.add(info);
                    }
                    subInvadapter = new PopTranSubInvAdapter(PopTranSubInvActivity.this, subInvList);
                    list_subInv.setAdapter(subInvadapter);
                    list_subInv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            Intent intent = new Intent();
                            PopTranSubInvItem item = (PopTranSubInvItem) subInvadapter.getItem(position);

                            intent.putExtra("SUBINV_CODE", item.SUBINVENTORY_CODE);
                            intent.putExtra("LOCATOR_CONTROL", item.LOCATOR_CONTROL);
                            intent.putExtra("STATUS_ID", item.STATUS_ID);

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
            AlertDialog.Builder builder = new AlertDialog.Builder(PopTranSubInvActivity.this);
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


