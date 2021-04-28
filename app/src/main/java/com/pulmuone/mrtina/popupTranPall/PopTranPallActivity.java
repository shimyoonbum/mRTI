package com.pulmuone.mrtina.popupTranPall;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
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
import com.pulmuone.mrtina.comm.HttpClient;
import com.pulmuone.mrtina.comm.Network;
import com.pulmuone.mrtina.comm.NetworkEvent;
import com.pulmuone.mrtina.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PopTranPallActivity extends Activity{

    private PopTranPallAdapter pallAdapter;
    ArrayList<PopTranPallItem> pallList = null;

    private RelativeLayout btn_close = null;
    private EditText edit_palt_no = null;
    private EditText edit_item_num = null;

    private String ORG_ID = null;

    private Button btn_find = null;

    private Network network = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_pall);

        Intent intent = getIntent();
        ORG_ID = intent.getStringExtra("ORG_ID");

        btn_close = (RelativeLayout) findViewById(R.id.layout_close);
        edit_palt_no = (EditText)findViewById(R.id.edit_palt_no);
        edit_item_num = (EditText)findViewById(R.id.edit_item_num);
        btn_find = (Button)findViewById(R.id.btn_find_no);

        edit_palt_no.setText(intent.getStringExtra("PALL_NO"));

        edit_palt_no.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_item_num.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

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

        edit_item_num.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_item_num.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_item_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_item_num.setSelection(edit_item_num.length());
            }
        });

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

            } else if(arg0.equals(btn_find)){
                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                param.put("orgId", Util.nullString(ORG_ID, ""));
                param.put("searchItem", Util.nullString(edit_item_num.getText().toString(), ""));
                param.put("searchPallet", Util.nullString(edit_palt_no.getText().toString(), ""));

                network = new Network(PopTranPallActivity.this, listener, "/api/tran/tranPalletList");
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

            if (api.equals("/api/tran/tranPalletList")) {
                try {
                    JSONArray list = obj.getJSONArray("list");

                    checkList(list);

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject data = list.getJSONObject(i);

                        PopTranPallItem info = new PopTranPallItem(Util.nullString(data.getString("ORGANIZATION_CODE"), "")
                                , Util.nullString(data.getString("PRIMARY_UOM_CODE"), "")
                                , Util.nullString(data.getString("SUBINVENTORY_CODE"), "")
                                , Util.nullString(data.getString("LOCATOR_CODE"), "")
                                , Util.nullString(data.getString("ITEM_DESC"), "")
                                , Util.nullString(data.getString("ITEM_CODE"), "")
                                , Util.nullString(data.getString("ORGANIZATION_ID"), "")
                                , Util.nullString(data.getString("LOCATOR_NAME"), "")
                                , Util.nullString(data.getString("PRIMARY_QUANTITY"), "")
                                , Util.nullString(data.getString("OU_ID"), "")
                                , Util.nullString(data.getString("PALLET_ID"), "")
                                , Util.nullString(data.getString("INVENTORY_LOCATION_ID"), "")
                                , Util.nullString(data.getString("PALLET_NUMBER"), "")
                                , Util.nullString(data.getString("LOCATOR_CONTROL"), "")
                                , Util.nullString(data.getString("LOT_NUMBER"), "")
                                , Util.nullString(data.getString("TRSF_SUBINVENTORY_CODE"), "")
                                , Util.nullString(data.getString("TRSF_INV_LOCATION_ID"), "")
                                , Util.nullString(data.getString("TRSF_LOCATOR_CODE"), "")
                        );
                        pallList.add(info);
                    }
                    pallAdapter = new PopTranPallAdapter(PopTranPallActivity.this, pallList);
                    list_pall.setAdapter(pallAdapter);

                    list_pall.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            Intent intent = new Intent();
                            PopTranPallItem item = (PopTranPallItem) pallAdapter.getItem(position);

                            intent.putExtra("PALLET_NUMBER", item.PALLET_NUMBER);
                            intent.putExtra("ORGANIZATION_CODE", item.ORGANIZATION_CODE);
                            intent.putExtra("ORG_ID", item.ORGANIZATION_ID);
                            intent.putExtra("SUBINVENTORY_CODE", item.SUBINVENTORY_CODE);
                            intent.putExtra("INVENTORY_LOCATION_ID", item.INVENTORY_LOCATION_ID);
                            intent.putExtra("OU_ID", item.OU_ID);
                            intent.putExtra("ITEM_CODE", item.ITEM_CODE);
                            intent.putExtra("PRIMARY_QUANTITY", item.PRIMARY_QUANTITY);
                            intent.putExtra("PRIMARY_UOM_CODE", item.PRIMARY_UOM_CODE);
                            intent.putExtra("LOT_NUMBER", item.LOT_NUMBER);
                            intent.putExtra("PALLET_ID", item.PALLET_ID);
                            intent.putExtra("LOCATOR_CODE", item.LOCATOR_CODE);
                            intent.putExtra("LOCATOR_CONTROL", item.LOCATOR_CONTROL);
                            intent.putExtra("TRSF_SUBINVENTORY_CODE", item.TRSF_SUBINVENTORY_CODE);
                            intent.putExtra("TRSF_INV_LOCATION_ID", item.TRSF_INV_LOCATION_ID);
                            intent.putExtra("TRSF_LOCATOR_CODE", item.TRSF_LOCATOR_CODE);

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
            AlertDialog.Builder builder = new AlertDialog.Builder(PopTranPallActivity.this);
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


