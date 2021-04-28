package com.pulmuone.mrtina.popupBatch;

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

public class PopBatchActivity extends Activity{

    private PopBatchAdapter batchAdapter;
    ArrayList<PopBatchItem> batchList = null;

    private RelativeLayout btn_close = null;

    SharedPreferences pref = null;
    private String S_USER_ID = null;

    private Network network = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_batch);

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);
        S_USER_ID = pref.getString("USER_ID", "");

        Intent intent = getIntent();

        btn_close = (RelativeLayout) findViewById(R.id.layout_close);
        btn_close.setOnClickListener(onClickListener);

        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
        param.put("ouId", Util.nullString(intent.getStringExtra("OU_ID"), ""));
        param.put("orgId", Util.nullString(intent.getStringExtra("ORG_ID"), ""));
        param.put("userNo", S_USER_ID);
        param.put("searchBatch", "");

        this.network = new Network(this, listener, "/api/prod/batchNoList");
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
            ListView list_batch = (ListView) findViewById(R.id.list_batch);
            batchList = new ArrayList<>();

            if(api.equals("/api/prod/batchNoList")){
                try {
                    JSONArray list = obj.getJSONArray("list");

                    checkList(list);

                    for(int i=0; i<list.length(); i++){
                        JSONObject data = list.getJSONObject(i);

                        PopBatchItem info = new PopBatchItem(Util.nullString(data.getString("BATCH_NO"),"")
                                , Util.nullString(data.getString("BATCH_ID"),"")
                                , Util.nullString(data.getString("ITEM_CODE"),"")
                                , Util.nullString(data.getString("PLAN_START_DATE"),"")
                        );
                        batchList.add(info);
                    }
                    batchAdapter = new PopBatchAdapter(PopBatchActivity.this, batchList);
                    list_batch.setAdapter(batchAdapter);
                    list_batch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            Intent intent = new Intent();
                            PopBatchItem item = (PopBatchItem) batchAdapter.getItem(position);

                            intent.putExtra("BATCH_NO", item.BATCH_NO);
                            intent.putExtra("BATCH_ID", item.BATCH_ID);

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
            AlertDialog.Builder builder = new AlertDialog.Builder(PopBatchActivity.this);
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


