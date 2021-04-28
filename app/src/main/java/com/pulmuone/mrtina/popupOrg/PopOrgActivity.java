package com.pulmuone.mrtina.popupOrg;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PopOrgActivity extends Activity {

    private PopOrgAdapter orgAdapter;
    private ProgressDialog progressDialog;
    private RelativeLayout btn_close = null;

    ListView listView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_org);

        btn_close = (RelativeLayout) findViewById(R.id.layout_close);
        btn_close.setOnClickListener(onClickListener);

        listView = (ListView) findViewById(R.id.list_org);

        new ouListGetTask().execute();
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

    private class ouListGetTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(PopOrgActivity.this, "", getString(R.string.querying));
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL + "/api/main/ouList");
            HttpClient post = http.create();
            post.request();

            String body = post.getBody();
            Log.v(Comm.LOG_TAG, "body: " + body);
            return body;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject obj = new JSONObject(result);

                if(!obj.getString("status").equals("S")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(PopOrgActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.server_error))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
                }else{
                    JSONArray list1 = obj.getJSONArray("list_org1");

                    ArrayList<PopOrgItem> organList = new ArrayList<>();

                    for (int i = 0; i < list1.length(); i++) {
                        JSONObject data = list1.getJSONObject(i);

                        PopOrgItem item = new PopOrgItem(Util.nullString(data.getString("ORG_CODE"), "")
                                , Util.nullString(data.getString("ORG_NAME"), "")
                                , Util.nullString(data.getString("ORG_ID"), "")
                                , Util.nullString(data.getString("OU_ID"), "")
                        );

                        organList.add(item);
                    }

                    JSONArray list2 = obj.getJSONArray("list_org2");

                    for (int i = 0; i < list2.length(); i++) {
                        JSONObject data = list2.getJSONObject(i);

                        PopOrgItem item = new PopOrgItem(Util.nullString(data.getString("ORG_CODE"), "")
                                , Util.nullString(data.getString("ORG_NAME"), "")
                                , Util.nullString(data.getString("ORG_ID"), "")
                                , Util.nullString(data.getString("OU_ID"), "")
                        );

                        organList.add(item);
                    }

                    orgAdapter = new PopOrgAdapter(PopOrgActivity.this, organList);
                    listView.setAdapter(orgAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                            Intent intent = new Intent();
                            PopOrgItem item = (PopOrgItem) orgAdapter.getItem(position);

                            intent.putExtra("OU_ID", item.OU_ID);
                            intent.putExtra("ORG_ID", item.ORG_ID);
                            intent.putExtra("ORG_CODE", item.ORG_CODE);

                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                }

            } catch (JSONException e) {
                Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
            } catch (Exception e1) {
                Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
            }

            progressDialog.dismiss();
        }
    }
}


