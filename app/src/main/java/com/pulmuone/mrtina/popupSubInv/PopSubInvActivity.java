package com.pulmuone.mrtina.popupSubInv;

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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pulmuone.mrtina.R;
import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.comm.HttpClient;
import com.pulmuone.mrtina.comm.Network;
import com.pulmuone.mrtina.comm.NetworkEvent;
import com.pulmuone.mrtina.recvPo.RecvPoActivity;
import com.pulmuone.mrtina.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PopSubInvActivity extends Activity{

    private PopSubInvAdapter subInvadapter;
    ArrayList<PopSubInvItem> subInvList = null;

    private RelativeLayout btn_close = null;

    private ListView list_subInv = null;

    private String ORG_ID = null;
    private int list_position = 0;

    private Network network = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_subinv);

        Intent intent = getIntent();
        list_position = intent.getIntExtra("POSITION", 1);
        ORG_ID = intent.getStringExtra("ORG_ID");

        btn_close = (RelativeLayout) findViewById(R.id.layout_close);
        btn_close.setOnClickListener(onClickListener);

        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
        param.put("orgId", Util.nullString(ORG_ID, ""));

        this.network = new Network(this, listener, "/api/cmmPopup/subinvList");
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

            if(api.equals("/api/cmmPopup/subinvList")) {
                try {
                    JSONArray list = obj.getJSONArray("list");

                    checkList(list);

                    for(int i=0; i<list.length(); i++){
                        JSONObject data = list.getJSONObject(i);

                        PopSubInvItem info = new PopSubInvItem(Util.nullString(data.getString("SUBINVENTORY_CODE"), "")
                                , Util.nullString(data.getString("SUBINVENTORY_NAME"), "")
                                , Util.nullString(data.getString("ORG_ID"), "")
                                , Util.nullString(data.getString("LOCATOR_CONTROL"), "")
                                , Util.nullString(data.getString("PALLET_CONTROL"), "")
                                , Util.nullString(data.getString("LOC_PO_RCV_ID"), "")
                                , Util.nullString(data.getString("LOC_PO_RCV_CODE"), "")
                                , Util.nullString(data.getString("LOC_TRO_RCV_ID"), "")
                                , Util.nullString(data.getString("LOC_TRO_RCV_CODE"), "")
                                , Util.nullString(data.getString("LOC_SUBINV_TRSF_ID"), "")
                                , Util.nullString(data.getString("LOC_SUBINV_TRSF_CODE"), "")
                                , Util.nullString(data.getString("LOC_TRO_TRSF_ID"), "")
                                , Util.nullString(data.getString("LOC_TRO_TRSF_CODE"), "")
                                , Util.nullString(data.getString("LOC_BATCH_TXN_ID"), "")
                                , Util.nullString(data.getString("LOC_BATCH_TXN_CODE"), "")
                                , Util.nullString(data.getString("LOC_STAGE_TXN_ID"), "")
                                , Util.nullString(data.getString("LOC_STAGE_TXN_CODE"), "")
                        );
                        subInvList.add(info);
                    }
                    subInvadapter = new PopSubInvAdapter(PopSubInvActivity.this, subInvList);
                    list_subInv.setAdapter(subInvadapter);
                    list_subInv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            Intent intent = new Intent();
                            PopSubInvItem item = (PopSubInvItem) subInvadapter.getItem(position);

                            intent.putExtra("SUBINV_CODE", item.SUBINVENTORY_CODE);
                            intent.putExtra("LOCATOR_CONTROL", item.LOCATOR_CONTROL);
                            intent.putExtra("PALLET_CONTROL", item.PALLET_CONTROL);
                            intent.putExtra("LOC_PO_RCV_ID", item.LOC_PO_RCV_ID);
                            intent.putExtra("LOC_PO_RCV_CODE", item.LOC_PO_RCV_CODE);
                            intent.putExtra("LOC_TRO_RCV_ID", item.LOC_TRO_RCV_ID);
                            intent.putExtra("LOC_TRO_RCV_CODE", item.LOC_TRO_RCV_CODE);
                            intent.putExtra("LOC_SUBINV_TRSF_ID", item.LOC_SUBINV_TRSF_ID);
                            intent.putExtra("LOC_SUBINV_TRSF_CODE", item.LOC_SUBINV_TRSF_CODE);
                            intent.putExtra("LOC_TRO_TRSF_ID", item.LOC_TRO_TRSF_ID);
                            intent.putExtra("LOC_TRO_TRSF_CODE", item.LOC_TRO_TRSF_CODE);
                            intent.putExtra("LOC_BATCH_TXN_ID", item.LOC_BATCH_TXN_ID);
                            intent.putExtra("LOC_BATCH_TXN_CODE", item.LOC_BATCH_TXN_CODE);
                            intent.putExtra("LOC_STAGE_TXN_ID", item.LOC_STAGE_TXN_ID);
                            intent.putExtra("LOC_STAGE_TXN_CODE", item.LOC_STAGE_TXN_CODE);
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

        }
    };

    private void checkList(JSONArray list) {
        if(list.length() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(PopSubInvActivity.this);
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


