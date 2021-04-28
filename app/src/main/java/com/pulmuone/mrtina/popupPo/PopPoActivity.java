package com.pulmuone.mrtina.popupPo;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

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

public class PopPoActivity extends Activity  implements Serializable {

    private PopPoAdapter poAdapter;
    ArrayList<PopPoItem> poList = null;

    private ListView list_po = null;
    private RelativeLayout btn_close = null;

    SharedPreferences pref = null;
    private String S_USER_ID = null;
    private String S_ORG_ID = null;
    private String S_OU_ID = null;
    private String PO_NO = null;

    private Network network = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_po);
        Intent intent = getIntent();

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);
        S_USER_ID = pref.getString("USER_ID", "");
        PO_NO = intent.getStringExtra("PO_NO");

        btn_close = (RelativeLayout)findViewById(R.id.layout_close);
        btn_close.setOnClickListener(onClickListener);

        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
        param.put("ouId", "");
        param.put("orgId", "");
        param.put("searchPo", Util.nullString(PO_NO, ""));

        this.network = new Network(this, listener, "/api/cmmPopup/poNoList");
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
            list_po = (ListView)findViewById(R.id.list_po_no);
            poList = new ArrayList<>();

            if (api.equals("/api/cmmPopup/poNoList")) {
                try {
                    JSONArray list = obj.getJSONArray("list");

                    checkList(list);

                    for(int i=0; i<list.length(); i++){
                        JSONObject data = list.getJSONObject(i);

                        if(list.length() == 1){
                            Intent intent = new Intent();

                            intent.putExtra("PO_NO", Util.nullString(data.getString("PO_NUMBER"), ""));
                            intent.putExtra("SHIP_TO_ORG", Util.nullString(data.getString("ORGANIZATION_CODE"), ""));
                            intent.putExtra("OU_ID", Util.nullString(data.getString("OU_ID"), ""));
                            intent.putExtra("ORGANIZATION_ID", Util.nullString(data.getString("ORGANIZATION_ID"), ""));
                            intent.putExtra("SUPPLIER", Util.nullString(data.getString("VENDOR_NAME"), ""));
                            intent.putExtra("PO_DATE", Util.nullString(data.getString("PO_DATE"), ""));
                            intent.putExtra("BL_TYPE", Util.nullString(data.getString("VENDOR_TYPE_LOOKUP_CODE"), ""));

                            setResult(RESULT_OK, intent);
                            finish();
                        }else {
                            PopPoItem info = new PopPoItem(Util.nullString(data.getString("PO_NUMBER"), "")
                                    , Util.nullString(data.getString("OU_ID"), "")
                                    , Util.nullString(data.getString("ORGANIZATION_CODE"), "")
                                    , Util.nullString(data.getString("ORGANIZATION_ID"), "")
                                    , Util.nullString(data.getString("VENDOR_NAME"), "")
                                    , Util.nullString(data.getString("PO_DATE"), "")
                                    , Util.nullString(data.getString("VENDOR_TYPE_LOOKUP_CODE"), "")
                            );
                            poList.add(info);
                        }
                    }
                    poAdapter = new PopPoAdapter(PopPoActivity.this, poList);
                    list_po.setAdapter(poAdapter);
                    list_po.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            Intent intent = new Intent();
                            PopPoItem item = (PopPoItem) poAdapter.getItem(position);

                            intent.putExtra("PO_NO", item.PO_NO);
                            intent.putExtra("OU_ID", item.OU_ID);
                            intent.putExtra("SHIP_TO_ORG", item.ORGANIZATION_CODE);
                            intent.putExtra("ORGANIZATION_ID", item.ORGANIZATION_ID);
                            intent.putExtra("SUPPLIER", item.SUPPLIER);
                            intent.putExtra("PO_DATE", item.PO_DATE);
                            intent.putExtra("BL_TYPE", item.BL_TYPE);

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
            AlertDialog.Builder builder = new AlertDialog.Builder(PopPoActivity.this);
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


