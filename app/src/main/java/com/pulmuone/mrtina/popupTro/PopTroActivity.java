package com.pulmuone.mrtina.popupTro;

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
import android.view.inputmethod.EditorInfo;
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

public class PopTroActivity extends Activity {

    private PopTroAdapter troAdapter;
    ArrayList<PopTroItem> troList = null;

    private RelativeLayout btn_close = null;

    private EditText edit_tro_num = null;
    private EditText edit_ship_from = null;
    private EditText edit_ship_to = null;

    private Button btn_find = null;

    private String ORG_ID = null;
    private String OU_ID = null;

    private Network network = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_tro);

        Intent intent = getIntent();
        ORG_ID = intent.getStringExtra("ORG_ID");
        OU_ID = intent.getStringExtra("OU_ID");

        btn_close = (RelativeLayout) findViewById(R.id.layout_close);

        btn_find = (Button) findViewById(R.id.btn_find);
        edit_tro_num = (EditText) findViewById(R.id.edit_tro_num);
        edit_ship_from = (EditText) findViewById(R.id.edit_ship_from);
        edit_ship_to = (EditText) findViewById(R.id.edit_ship_to);

        edit_ship_from.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_ship_to.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_tro_num.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        edit_ship_from.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_ship_from.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_ship_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_ship_from.setSelection(edit_ship_from.length());
            }
        });
        edit_ship_to.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_ship_to.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_ship_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_ship_to.setSelection(edit_ship_to.length());
            }
        });
        edit_tro_num.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_tro_num.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_tro_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_tro_num.setSelection(edit_tro_num.length());
            }
        });

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
                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                param.put("ouId", Util.nullString(OU_ID, ""));
                param.put("orgId", Util.nullString(ORG_ID, ""));
                param.put("searchTro", Util.nullString(edit_tro_num.getText().toString(), ""));
                param.put("searchFrom", Util.nullString(edit_ship_from.getText().toString(), ""));
                param.put("searchTo", Util.nullString(edit_ship_to.getText().toString(), ""));

                network = new Network(PopTroActivity.this, listener, "/api/cmmPopup/troNoList");
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
            if(api.equals("/api/cmmPopup/troNoList")){
                try {
                    JSONArray list = obj.getJSONArray("list");

                    checkList(list);

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject data = list.getJSONObject(i);

                        PopTroItem info = new PopTroItem(Util.nullString(data.getString("ORG_CODE_TO"), "")
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
                    troAdapter = new PopTroAdapter(PopTroActivity.this, troList);
                    list_tro.setAdapter(troAdapter);

                    list_tro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            Intent intent = new Intent();
                            PopTroItem item = (PopTroItem) troAdapter.getItem(position);

                            intent.putExtra("SHIPMENT_NO", item.SHIPMENT_NO);
                            intent.putExtra("ORG_CODE_TO", item.ORG_CODE_TO);
                            intent.putExtra("ORG_CODE_FROM", item.ORG_CODE_FROM);
                            intent.putExtra("SHIPMENT_DATE", item.SHIPMENT_DATE);
                            intent.putExtra("EXP_RECEIPT_DATE", item.EXP_RECEIPT_DATE);
                            intent.putExtra("OU_ID", item.OU_ID);
                            intent.putExtra("ORG_ID_TO", item.ORG_ID_TO);

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
            AlertDialog.Builder builder = new AlertDialog.Builder(PopTroActivity.this);
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


