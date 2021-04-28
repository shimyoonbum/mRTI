package com.pulmuone.mrtina.popupItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PopItemActivity extends Activity  implements Serializable {

    private PopItemAdapter itemAdapter;
    ArrayList<PopItemItem> itemList = null;

    private RelativeLayout btn_close = null;

    private EditText edit_item_no = null;
    private EditText edit_item_desc = null;
    private Button btn_desc_find = null;

    private static final long serialVersionUID = 1L;

    SharedPreferences pref = null;
    private String S_ORG_ID = null;
    private Network network = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_item);

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);
        S_ORG_ID = pref.getString("ORG_ID", "");

        btn_close = (RelativeLayout) findViewById(R.id.layout_close);

        edit_item_no = (EditText)findViewById(R.id.edit_item_no);

        edit_item_desc = (EditText)findViewById(R.id.edit_item_desc);
        btn_desc_find = (Button)findViewById(R.id.btn_find_item_desc);

        edit_item_desc.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_item_no.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        edit_item_no.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_item_no.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_item_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_item_no.setSelection(edit_item_no.length());
            }
        });

        edit_item_desc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_item_desc.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_item_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_item_desc.setSelection(edit_item_desc.length());
            }
        });

        btn_close.setOnClickListener(onClickListener);
        btn_desc_find.setOnClickListener(onClickListener);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if (arg0.equals(btn_close)){
                finish();

            }else if(arg0.equals(btn_desc_find)){
                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                param.put("orgId", Util.nullString(S_ORG_ID, ""));
                param.put("searchCd", Util.nullString(edit_item_no.getText().toString(),""));
                param.put("searchNm", Util.nullString(edit_item_desc.getText().toString(),""));

                network = new Network(PopItemActivity.this, listener, "/api/cmmPopup/itemList");
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
            ListView list_item = (ListView) findViewById(R.id.list_item);

            itemList = new ArrayList<>();

            if(api.equals("/api/cmmPopup/itemList")) {
                try {
                    JSONArray list = obj.getJSONArray("list");

                    checkList(list);

                    for(int i=0; i<list.length(); i++){
                        JSONObject data = list.getJSONObject(i);

                        PopItemItem info = new PopItemItem(Util.nullString(data.getString("INVENTORY_ITEM_ID"),"")
                                , Util.nullString(data.getString("ITEM_CODE"),"")
                                , Util.nullString(data.getString("ITEM_DESC"),"")
                                , Util.nullString(data.getString("PRIMARY_UOM_CODE"),"")
                        );
                        itemList.add(info);
                    }
                    itemAdapter = new PopItemAdapter(PopItemActivity.this, itemList);
                    list_item.setAdapter(itemAdapter);

                    list_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            Intent intent = new Intent();
                            PopItemItem item = (PopItemItem) itemAdapter.getItem(position);

                            intent.putExtra("ITEM_CODE", item.ITEM_CODE);

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
            AlertDialog.Builder builder = new AlertDialog.Builder(PopItemActivity.this);
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


