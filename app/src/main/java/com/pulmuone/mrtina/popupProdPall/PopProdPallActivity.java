package com.pulmuone.mrtina.popupProdPall;

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

public class PopProdPallActivity extends Activity{

    private PopProdPallAdapter pallAdapter;
    ArrayList<PopProdPallItem> pallList = null;

    private RelativeLayout btn_close = null;
    private EditText edit_palt_no = null;

    private Button btn_find = null;

    SharedPreferences pref = null;
    private String S_USER_ID = null;
    private String ORG_ID = null;
    private String OU_ID = null;
    private String PALL_NO = null;

    private Network network = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_prod_pall);
        Intent intent = getIntent();

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);
        S_USER_ID = pref.getString("USER_ID", "");

        ORG_ID = intent.getStringExtra("ORG_ID");
        OU_ID = intent.getStringExtra("OU_ID");

        btn_close = (RelativeLayout) findViewById(R.id.layout_close);
        edit_palt_no = (EditText)findViewById(R.id.edit_palt_no);
        btn_find = (Button)findViewById(R.id.btn_find_pall_no);

        edit_palt_no.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_palt_no.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_palt_no.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_palt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_palt_no.setSelection(edit_palt_no.length());
            }
        });

        edit_palt_no.setText(intent.getStringExtra("PALL_NO"));


        btn_close.setOnClickListener(onClickListener);
        btn_find.setOnClickListener(onClickListener);
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
                String searchKey = edit_palt_no.getText().toString();
                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                param.put("orgId", ORG_ID);
                param.put("ouId", OU_ID);
                param.put("userNo", S_USER_ID);
                param.put("searchPallet", Util.nullString(searchKey, ""));

                network = new Network(PopProdPallActivity.this, listener, "/api/prod/batchPalletList");
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

            ListView list_pall = (ListView) findViewById(R.id.list_pall);
            pallList = new ArrayList<>();

            if (api.equals("/api/prod/batchPalletList")) {
                try {
                    JSONArray list = obj.getJSONArray("list");

                    checkList(list);

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject data = list.getJSONObject(i);

                        PopProdPallItem info = new PopProdPallItem(Util.nullString(data.getString("PALLET_ID"), "")
                                , Util.nullString(data.getString("PALLET_NUMBER"), "")
                                , Util.nullString(data.getString("ITEM_CODE"), "")
                                , Util.nullString(data.getString("BATCH_NO"), "")
                                , Util.nullString(data.getString("BATCH_ID"), "")
                                , Util.nullString(data.getString("PLAN_START_DATE"), "")
                        );
                        pallList.add(info);
                    }
                    pallAdapter = new PopProdPallAdapter(PopProdPallActivity.this, pallList);
                    list_pall.setAdapter(pallAdapter);

                    list_pall.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            Intent intent = new Intent();
                            PopProdPallItem item = (PopProdPallItem) pallAdapter.getItem(position);

                            intent.putExtra("PALLET_NUMBER", item.PALLET_NUMBER);
                            intent.putExtra("PALLET_ID", item.PALLET_ID);
                            intent.putExtra("BATCH_NO", item.BATCH_NO);
                            intent.putExtra("BATCH_ID", item.BATCH_ID);
                            intent.putExtra("PLAN_START_DATE", item.PLAN_START_DATE);

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
            AlertDialog.Builder builder = new AlertDialog.Builder(PopProdPallActivity.this);
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


