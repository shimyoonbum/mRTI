package com.pulmuone.mrtina.popupLoca;

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

public class PopLocaActivity extends Activity{

    private PopLocaAdapter locaAdapter;
    ArrayList<PopLocaItem> locaList = null;

    private ListView list_loca = null;

    private RelativeLayout btn_close = null;
    private EditText edit_loca = null;
    private Button btn_find = null;

    private String ORG_ID = null;

    private String SUBINVENTORY_CODE = null;
    private int list_position = 0;

    private Network network = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_loca);
        Intent intent = getIntent();

        list_position = intent.getIntExtra("POSITION", 1);
        SUBINVENTORY_CODE = intent.getStringExtra("SUBINVENTORY_CODE");
        ORG_ID = intent.getStringExtra("ORG_ID");

        btn_close = (RelativeLayout) findViewById(R.id.layout_close);
        edit_loca = (EditText)findViewById(R.id.edit_loca);
        btn_find = (Button)findViewById(R.id.btn_find);

        edit_loca.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        edit_loca.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_loca.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_loca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_loca.setSelection(edit_loca.length());
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
            if (arg0.equals(btn_close)){
                finish();
            }else if(arg0.equals(btn_find)){
                String locator = edit_loca.getText().toString();
                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                param.put("subinventoryCode", Util.nullString(SUBINVENTORY_CODE,""));
                param.put("orgId", Util.nullString(ORG_ID,""));
                param.put("searchKey", Util.nullString(locator,""));

                network = new Network(PopLocaActivity.this, listener, "/api/cmmPopup/locatorList");
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
            list_loca = (ListView)findViewById(R.id.list_loca);
            locaList = new ArrayList<>();

            if(api.equals("/api/cmmPopup/locatorList")) {
                try {
                    JSONArray list = obj.getJSONArray("list");

                    checkList(list);

                    for(int i=0; i<list.length(); i++){
                        JSONObject data = list.getJSONObject(i);

                        PopLocaItem info = new PopLocaItem(Util.nullString(data.getString("INVENTORY_LOCATION_ID"),"")
                                , Util.nullString(data.getString("LOCATOR_CODE"),"")
                                , Util.nullString(data.getString("LOCATOR_NAME"),"")
                        );
                        locaList.add(info);
                    }
                    locaAdapter = new PopLocaAdapter(PopLocaActivity.this, locaList);
                    list_loca.setAdapter(locaAdapter);

                    list_loca.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            Intent intent = new Intent();
                            PopLocaItem item = (PopLocaItem) locaAdapter.getItem(position);

                            intent.putExtra("LOCATOR_CODE", item.LOCATOR_CODE);
                            intent.putExtra("INVENTORY_LOCATION_ID", item.INVENTORY_LOCATION_ID);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(PopLocaActivity.this);
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


