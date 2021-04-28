package com.pulmuone.mrtina.intro;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.pulmuone.mrtina.R;
import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.comm.HttpClient;
import com.pulmuone.mrtina.comm.ServerRequest;
import com.pulmuone.mrtina.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import static java.lang.Float.valueOf;

public class IntroActivity extends Activity {
    PackageInfo packageInfo = null;
    private int appVersionCode = 0;
    private String appVersionName = "";
    private String serverVersion = "";
    private ProgressDialog progressDialog;
    public ServerRequest serverRequestTest = null;

    private TextView cur_ver, new_ver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        cur_ver = (TextView) findViewById(R.id.cur_ver);
        new_ver = (TextView) findViewById(R.id.new_ver);

        checkExternalPermission();
        getVersion();
        versionCheck();
        // 임시 버전 체크없이 로그인 화면으로 PASS
        //Intent intent = new Intent(this, LoginActivity.class);
        //startActivity(intent);
        //finish();
    }

    private void checkExternalPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // 안드로이드 마시멜로우 6.0버전과 같거나 그 이상이라면 권한을 유저한테 부여받는 팝업 띄움
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "외부 저장소 사용을 위해 읽기/쓰기 필요", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]
                                {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        2);  //마지막 인자는 체크해야될 권한 갯수
            }
        }
    }

    private void getVersion() {

        PackageManager pm = getPackageManager();
        try {
            packageInfo = pm.getPackageInfo(getPackageName(), 0);
            appVersionCode = packageInfo.versionCode;
            appVersionName = packageInfo.versionName;
        } catch(PackageManager.NameNotFoundException e) {}
    }

    private void versionCheck() {
        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
        param.put("ADMIN","ADMIN");
        new versionGetTask().execute(param);
    }

    private class versionGetTask extends AsyncTask<LinkedHashMap<String, String>, Void, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(IntroActivity.this, "", getString(R.string.search));
        }

        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL+"/api/versionChk");
            http.addAllParameters(params[0]);
            HttpClient post = http.create();
            post.request();

            int statusCode = post.getHttpStatusCode();

            String body = post.getBody();
            Log.v(Comm.LOG_TAG, "statusCode: "+ statusCode);
            Log.v(Comm.LOG_TAG, "body: "+ body);

            if (statusCode == -10) {
                body = "ERROR";
            } else if(statusCode == 404 || statusCode == 502 || statusCode == 503){
                body = "ERROR";
            }

            return body;

        }

        @Override
        protected void onPostExecute(String result) {

            progressDialog.dismiss();
            Log.v(Comm.LOG_TAG, "result: "+ result);

            if("ERROR".equals(result)){

                AlertDialog.Builder builder = new AlertDialog.Builder(IntroActivity.this);
                builder
                        .setTitle(getString(R.string.warning))
                        .setMessage(getString(R.string.server_error))
                        .setNeutralButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }else {

                String versionValue = "";
                try {
                    JSONObject obj = new JSONObject(result);
                    Log.v(Comm.LOG_TAG, "obj :: " + obj);
                    if (obj != null) {
                        String statusText = obj.getString("status");

                        Log.v(Comm.LOG_TAG, "statusText :: " + statusText);

                        JSONArray list = obj.getJSONArray("list");
                        for (int i = 0; i < list.length(); i++) {
                            versionValue = list.getJSONObject(i).getString("VER");
                        }
                    }
                } catch (JSONException e) {
                    Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                } catch (Exception e1) {
                    Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
                }
                serverVersion = versionValue;
                targetView();
            }
        }
    }

    private void targetView () {
        float server = 0;
        float app = 0;
        try {
            server = (Float) valueOf(serverVersion);
            app = (Float) valueOf(appVersionName);

            cur_ver.setText(String.valueOf(app));
            new_ver.setText(String.valueOf(server));

            Log.v(Comm.LOG_TAG, "server.VER : " + server);
            Log.v(Comm.LOG_TAG, "app.VER : " + app);

            if (app >= server) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else if (app < server) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IntroActivity.this);
                builder
                        .setTitle(getString(R.string.update_new_version))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                Uri u = Uri.parse(Comm.DL_URL);
                                i.setData(u);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
        }catch (Exception e){

        }
    }

}
