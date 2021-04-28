package com.pulmuone.mrtina.popupTranTro;

import android.app.Activity;
import android.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PopTranTroActivity extends Activity {

    private PopTranTroAdapter troAdapter;
    ArrayList<PopTranTroItem> troList = null;

    private RelativeLayout btn_close = null;
    private EditText edit_tro_num = null;
    private EditText edit_ship_from = null;
    private EditText edit_ship_to = null;

    private Button btn_find = null;

    private Network network = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_tro);

        Intent intent = getIntent();

        btn_close = (RelativeLayout) findViewById(R.id.layout_close);
        btn_find = (Button) findViewById(R.id.btn_find);
        edit_tro_num = (EditText) findViewById(R.id.edit_tro_num);
        edit_ship_from = (EditText) findViewById(R.id.edit_ship_from);
        edit_ship_to = (EditText) findViewById(R.id.edit_ship_to);

        edit_ship_from.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_ship_to.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_tro_num.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        btn_close.setOnClickListener(onClickListener);
        btn_find.setOnClickListener(onClickListener);

        edit_tro_num.setText(intent.getStringExtra("TRO_NUM"));
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            if (arg0.equals(btn_close)) {
                finish();
            } else if (arg0.equals(btn_find)) {
                String searchTro = edit_tro_num.getText().toString();
                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                param.put("ouId", "");
                param.put("orgId", "");
                param.put("searchTro", Util.nullString(searchTro, ""));
                param.put("searchFrom", Util.nullString(edit_ship_from.getText().toString(), ""));
                param.put("searchTo", Util.nullString(edit_ship_to.getText().toString(), ""));

                network = new Network(PopTranTroActivity.this, listener, "/api/tran/troNoList");
                network.execute(param);
            }
        }
    };

    NetworkEvent listener = new NetworkEvent() {
        @Override
        public void doInBackground(LinkedHashMap<String, String>... params) {
        }

        @Override
        public void onPostExecute(JSONObject obj, String api) {
            troList = new ArrayList<>();
            ListView list_tro = (ListView) findViewById(R.id.list_tro);
            if(api.equals("/api/tran/troNoList")){
                try {
                    JSONArray list = obj.getJSONArray("list");

                    checkList(list);

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject data = list.getJSONObject(i);

                        PopTranTroItem info = new PopTranTroItem(Util.nullString(data.getString("ORG_CODE_TO"), "")
                                , Util.nullString(data.getString("ORG_ID_TO"), "")
                                , Util.nullString(data.getString("ORG_CODE_FROM"), "")
                                , Util.nullString(data.getString("ORG_ID_FROM"), "")
                                , Util.nullString(data.getString("SHIPMENT_DATE"), "")
                                , Util.nullString(data.getString("SHIPMENT_NO"), "")
                                , Util.nullString(data.getString("SHIPMENT_HEADER_ID"), "")
                                , Util.nullString(data.getString("EXP_RECEIPT_DATE"), "")
                                , Util.nullString(data.getString("OU_ID"), "")
                        );
                        troList.add(info);
                    }
                    troAdapter = new PopTranTroAdapter(PopTranTroActivity.this, troList);
                    list_tro.setAdapter(troAdapter);

                    list_tro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            Intent intent = new Intent();
                            PopTranTroItem item = (PopTranTroItem) troAdapter.getItem(position);

                            intent.putExtra("SHIPMENT_NO", item.SHIPMENT_NO);
                            intent.putExtra("ORG_CODE_TO", item.ORG_CODE_TO);
                            intent.putExtra("ORG_CODE_FROM", item.ORG_CODE_FROM);
                            intent.putExtra("SHIPMENT_DATE", item.SHIPMENT_DATE);
                            intent.putExtra("EXP_RECEIPT_DATE", item.EXP_RECEIPT_DATE);
                            intent.putExtra("ORG_ID", item.ORG_ID_FROM);
                            intent.putExtra("OU_ID", item.OU_ID);

                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });

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
            AlertDialog.Builder builder = new AlertDialog.Builder(PopTranTroActivity.this);
            builder
                    .setTitle(getString(R.string.note))
                    .setMessage(getString(R.string.alert_no_item))
                    .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
    }
}


