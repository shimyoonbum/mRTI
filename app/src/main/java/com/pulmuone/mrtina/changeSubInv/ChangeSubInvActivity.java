package com.pulmuone.mrtina.changeSubInv;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pulmuone.mrtina.R;
import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.comm.HttpClient;
import com.pulmuone.mrtina.comm.Network;
import com.pulmuone.mrtina.menu.MenuActivity;
import com.pulmuone.mrtina.recvPo.RecvPoActivity;
import com.pulmuone.mrtina.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ChangeSubInvActivity extends Activity {
    private ChangeSubInvAdapter subInvadapter;
    ArrayList<ChangeSubInvItem> subInvList = null;

    private RelativeLayout btn_back = null;
    private ListView list_subInv = null;
    private TextView text_subInv = null;

    SharedPreferences pref = null;
    SharedPreferences.Editor editor = null;

    private ProgressDialog progressDialog;
    private String S_ORG_CODE = null;
    private String S_ORG_ID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_subinv);

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);
        S_ORG_CODE = pref.getString("ORG_CODE", "");
        S_ORG_ID = pref.getString("ORG_ID", "");

        text_subInv = (TextView)findViewById(R.id.txt_subInv);
        text_subInv.setText(S_ORG_CODE);

        btn_back = (RelativeLayout)findViewById(R.id.layout_back);
        btn_back.setOnClickListener(onClickListener);

        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("orgId", Util.nullString(S_ORG_ID, ""));

        new subInvGetTask().execute(param);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if (arg0.equals(btn_back)){
                finish();
            }
        }
    };

    private class subInvGetTask extends AsyncTask<LinkedHashMap<String, String>, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ChangeSubInvActivity.this, "", getString(R.string.querying));
        }

        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL + "/api/main/subinvList");
            HttpClient post = http.create();
            http.addAllParameters(params[0]);
            post.request();

            String body = post.getBody();
            Log.v(Comm.LOG_TAG, "body: " + body);
            return body;
        }

        @Override
        protected void onPostExecute(String result) {
            subInvList = new ArrayList<>();
            list_subInv = (ListView)findViewById(R.id.list_subinv);

            try {
                JSONObject obj = new JSONObject(result);

                JSONArray list = obj.getJSONArray("list");
                for (int i = 0; i < list.length(); i++) {
                    JSONObject data = list.getJSONObject(i);

                    ChangeSubInvItem info = new ChangeSubInvItem(Util.nullString(data.getString("SUBINVENTORY_CODE"), "")
                            , Util.nullString(data.getString("SUBINVENTORY_NAME"), "")
                            , Util.nullString(data.getString("ORG_ID"), "")
                            , Util.nullString(data.getString("LOCATOR_CONTROL"), "")
                            , Util.nullString(data.getString("PALLET_CONTROL"), "")
                    );
                    subInvList.add(info);
                }
                subInvadapter = new ChangeSubInvAdapter(ChangeSubInvActivity.this, subInvList);
                list_subInv.setAdapter(subInvadapter);
                list_subInv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        Intent intent = new Intent(ChangeSubInvActivity.this, MenuActivity.class);
                        ChangeSubInvItem item = (ChangeSubInvItem) subInvadapter.getItem(position);

                        pref = getSharedPreferences("mrtina",Activity.MODE_PRIVATE);
                        editor = pref.edit();
                        editor.putString("SUBINVENTORY_CODE", item.SUBINVENTORY_CODE);
                        editor.putString("SUBINVENTORY_NAME", item.SUBINVENTORY_NAME);
                        editor.putString("LOCATOR_CONTROL", item.LOCATOR_CONTROL);
                        editor.putString("PALLET_CONTROL", item.PALLET_CONTROL);

                        editor.apply();

                        startActivity(intent);
                    }
                });
            } catch (JSONException e) {
                Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
            } catch (Exception e1) {
                Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
            }

            progressDialog.dismiss();
        }
    }
}
