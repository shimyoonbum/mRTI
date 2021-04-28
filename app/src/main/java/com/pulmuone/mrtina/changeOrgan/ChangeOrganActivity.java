package com.pulmuone.mrtina.changeOrgan;

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
import com.pulmuone.mrtina.changeSubInv.ChangeSubInvActivity;
import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.comm.HttpClient;
import com.pulmuone.mrtina.menu.MenuActivity;
import com.pulmuone.mrtina.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChangeOrganActivity extends Activity {

    private ChangeOrganAdapter adapter1;
    private ChangeOrganAdapter adapter2;

    private ListView list_org1 = null;
    private ListView list_org2 = null;

    private RelativeLayout btn_back = null;

    SharedPreferences pref = null;
    SharedPreferences.Editor editor = null;
    private ProgressDialog progressDialog;

    private TextView txt_org1, txt_org2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_organ);

        txt_org1 = (TextView)findViewById(R.id.txt_org1);
        txt_org2 = (TextView)findViewById(R.id.txt_org2);

        list_org1 = (ListView)findViewById(R.id.list_org1);
        list_org2 = (ListView)findViewById(R.id.list_org2);

        btn_back = (RelativeLayout) findViewById(R.id.layout_back);
        btn_back.setOnClickListener(onClickListener);

        new ouListGetTask().execute();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.equals(btn_back)) {
                finish();
            }
        }
    };

    private class ouListGetTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ChangeOrganActivity.this, "", getString(R.string.querying));
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangeOrganActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.server_error))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
                }else{
                    JSONArray list_ou = obj.getJSONArray("list_ou");
                    ArrayList<String> ouList = new ArrayList<>();
                    ArrayList<String> ouIdList = new ArrayList<>();

                    for (int i = 0; i < list_ou.length(); i++) {
                        JSONObject data = list_ou.getJSONObject(i);
                        String ou_code = data.getString("OU_CODE");
                        ouList.add(ou_code);
                        String ou_id = data.getString("OU_ID");
                        ouIdList.add(ou_id);
                    }
                    txt_org1.setText(ouList.get(0));
                    txt_org2.setText(ouList.get(1));

                    JSONArray list1 = obj.getJSONArray("list_org1");

                    ArrayList<ChangeOrganItem> organList1 = new ArrayList<>();

                    for (int i = 0; i < list1.length(); i++) {
                        JSONObject data = list1.getJSONObject(i);

                        ChangeOrganItem info = new ChangeOrganItem(Util.nullString(data.getString("ORG_CODE"), "")
                                , Util.nullString(data.getString("ORG_NAME"), "")
                                , Util.nullString(data.getString("ORG_ID"), "")
                                , Util.nullString(ouIdList.get(0), "")
                        );

                        organList1.add(info);
                    }

                    adapter1 = new ChangeOrganAdapter(ChangeOrganActivity.this, organList1);
                    list_org1.setAdapter(adapter1);

                    list_org1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            ChangeOrganItem item = (ChangeOrganItem) adapter1.getItem(position);

                            pref = getSharedPreferences("mrtina",Activity.MODE_PRIVATE);
                            editor = pref.edit();
                            editor.putString("OU_ID", item.OU_ID);
                            editor.putString("ORG_CODE", item.ORG_CODE);
                            editor.putString("ORG_ID", item.ORG_ID);
                            editor.putString("ORG_NAME", item.ORG_NAME);
                            editor.apply();

                            if(Util.isNull(pref.getString("SUBINVENTORY_CODE", ""))){
                                Intent intent = new Intent(ChangeOrganActivity.this, ChangeSubInvActivity.class);
                                startActivity(intent);
                            }else{
                                Intent intent = new Intent(ChangeOrganActivity.this, MenuActivity.class);
                                startActivity(intent);
                            }
                        }
                    });

                    JSONArray list2 = obj.getJSONArray("list_org2");
                    ArrayList<ChangeOrganItem> organList2 = new ArrayList<>();

                    for (int i = 0; i < list2.length(); i++) {
                        JSONObject data = list2.getJSONObject(i);

                        ChangeOrganItem info = new ChangeOrganItem(Util.nullString(data.getString("ORG_CODE"), "")
                                , Util.nullString(data.getString("ORG_NAME"), "")
                                , Util.nullString(data.getString("ORG_ID"), "")
                                , Util.nullString(ouIdList.get(1), "")
                        );
                        organList2.add(info);
                    }
                    adapter2 = new ChangeOrganAdapter(ChangeOrganActivity.this, organList2);
                    list_org2.setAdapter(adapter2);

                    list_org2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            ChangeOrganItem item = (ChangeOrganItem) adapter2.getItem(position);

                            pref = getSharedPreferences("mrtina",Activity.MODE_PRIVATE);
                            editor = pref.edit();
                            editor.putString("OU_ID", item.OU_ID);
                            editor.putString("ORG_CODE", item.ORG_CODE);
                            editor.putString("ORG_ID", item.ORG_ID);
                            editor.putString("ORG_NAME", item.ORG_NAME);
                            editor.apply();

                            Intent intent = new Intent(ChangeOrganActivity.this, ChangeSubInvActivity.class);
                            startActivity(intent);
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
