package com.pulmuone.mrtina.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pulmuone.mrtina.changeOrgan.ChangeOrganActivity;
import com.pulmuone.mrtina.changeSubInv.ChangeSubInvActivity;
import com.pulmuone.mrtina.intro.IntroActivity;
import com.pulmuone.mrtina.menu.MenuActivity;
import com.pulmuone.mrtina.utils.SettingsHelper;

import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;


import com.pulmuone.mrtina.comm.NetworkEvent;
import com.pulmuone.mrtina.comm.Network;
import com.pulmuone.mrtina.utils.Util;

import static com.pulmuone.mrtina.comm.Comm.REQUEST_ENABLE_BT;
import static java.lang.Float.valueOf;

public class LoginActivity extends Activity{

    PackageInfo packageInfo = null;
    private int versionCode = 0;
    private String versionName = "";
    private String serverVersion = "";
    private TextView versionInfo = null;
    private Button btn_login = null;
    private ProgressDialog dialog;
    private ProgressDialog progressDialog;
    //private ArrayList<FactoryItem> sp_data = new ArrayList<FactoryItem>();
    //private FactoryAdapter factoryAdapter = null;
    private String user_id = "";
    private String user_name = "";
    private String user_desc = "";
    private String emp_id = "";
    private String ou_id = "";
    private String org_id = "";
    private String org_code = "";
    private String subinv_code = "";

    int mPariedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;

    BluetoothAdapter mBluetoothAdapter;
    String BT_ADDR = "";

    public int schPw = 0;
    private EditText edit_id;
    private EditText edit_pw;
    private CheckBox check_id;
    private TextView txt_svr;
    SharedPreferences pref = null;
    SharedPreferences.Editor editor = null;
    private Network network = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edit_id = (EditText) findViewById(R.id.ext_id);
        edit_pw = (EditText) findViewById(R.id.ext_pw);
        check_id = (CheckBox) findViewById(R.id.cb_id);

        pref = getSharedPreferences("mrtina",Activity.MODE_PRIVATE);
        edit_id.setText(pref.getString("LOGIN_USER_ID", ""));
        edit_pw.setText(pref.getString("LOGIN_USER_PW", ""));
        btn_login = (Button)findViewById(R.id.btn_login);
        versionInfo = (TextView) findViewById(R.id.txt_ver);
        txt_svr = (TextView) findViewById(R.id.txt_svr);

        btn_login.setOnClickListener(onClickListener);
        //txt_svr.setText(Comm.SVR);
        //facRequest();
        getVersion();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            if (arg0.equals(btn_login)) {
                signUp();
            }
        }
    };

    private void getVersion() {

        PackageManager pm = getPackageManager();
        try {
            packageInfo = pm.getPackageInfo(getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
        } catch(PackageManager.NameNotFoundException e) {}
        //versionName = "v " + versionName;
        versionInfo.setText("v "+versionName);
    }

    private void signUp(){
        if(edit_id.getText().toString() == null || edit_id.getText().toString().trim().length()==0){
            Comm.makeToast(getApplicationContext(), getString(R.string.login_chk_id));
        }else if(edit_pw.getText().toString() == null || edit_pw.getText().toString().trim().length()==0){
            Comm.makeToast(getApplicationContext(), getString(R.string.login_chk_pw));
        }else{
            loginRequest();
        }
    }

    private void loginRequest() {

        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
        param.put("userId", Util.nullString(edit_id.getText().toString(), ""));
        param.put("userPw", Util.nullString(edit_pw.getText().toString(),""));
/* JSON Format
        // [[
        JSONObject json = new JSONObject();
        JSONArray arr = new JSONArray();
         try {
            for(int i=0; i < 3; i++){
                JSONObject obj = new JSONObject();
                obj.put("idx", i+"0");
                obj.put("orgCode", i+"00");
                obj.put("poHeaderId", i+"000");
                arr.put(i,obj);
            }
             json.put("list",arr);
             json.put("userId",Util.nullString(edit_id.getText().toString(), ""));
             json.put("userPw",Util.nullString(edit_pw.getText().toString(),""));
             json.put("dept_code","15111100");
        }catch(Exception e){
            Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
        }

        param.put("list",arr.toString() );
        param.put("json",json.toString().replaceAll("\"",""));
        Log.v(Comm.LOG_TAG, "SEND JSon DATA :: " + json.toString());
        // ]]
*/

        this.network = new Network(this,listener,"/api/login/doLogin");
        this.network.execute(param);
    }

    // [[ 2020-08-19 로그인 시 버전 체크
    private void targetView () {
        float server = 0;
        float app = 0;
        try {
            server = (Float) valueOf(serverVersion);
            app = (Float) valueOf(versionName);

            Log.v(Comm.LOG_TAG, "server.VER : " + server);
            Log.v(Comm.LOG_TAG, "app.VER : " + app);

            if (app < server) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LoginActivity.this);
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
            }else{
                getUserInfoRequest();
            }
        }catch (Exception e){

        }
    }
    // ]]
    private void getUserInfoRequest() {

        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
        param.put("userId", Util.nullString(edit_id.getText().toString(),""));
        param.put("userNo", user_id);

        this.network = new Network(this,listener,"/api/login/userList");
        this.network.execute(param);
    }

    NetworkEvent listener = new NetworkEvent() {
        @Override
        public void doInBackground(LinkedHashMap<String, String>... params){

        }
        @Override
        public void onPostExecute(JSONObject obj,String api) {
            if (api.equals("/api/login/doLogin")) {
                String emp_no = "";
                try {
                    if (obj != null) {
                        JSONArray list = obj.getJSONArray("list");

                        for (int i = 0; i < list.length(); i++) {
                            JSONObject data = list.getJSONObject(i);
                            emp_no = data.getString("USER_ID");
                        }

                        JSONArray list1 = obj.getJSONArray("list1");
                        for (int i = 0; i < list1.length(); i++) {
                            serverVersion = list1.getJSONObject(i).getString("VER");
                        }
                    }
                } catch (JSONException e) {
                    Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                } catch (Exception e1) {
                    Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
                }

                Log.v(Comm.LOG_TAG, "emp_no: " + emp_no);

                if (emp_no.equals("N")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.login_chk_fail))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();
                } else if (emp_no.equals("X")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.login_chk_erp))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();

                } else if (emp_no != "N" && emp_no != "X" && emp_no != "") {
                    user_id = emp_no;

                    // 2020-08-19 로그인 시 버전 체크
                    targetView();
                    //getUserInfoRequest();

                }

            }else if (api.equals("/api/login/userList")) {
                try {
                    if (obj != null) {

                        String showText = obj.getString("status");
                        JSONArray list = obj.getJSONArray("list");
                        Log.v(Comm.LOG_TAG, "userInfo: "+ list);

                        for(int i=0; i<list.length(); i++) {
                            JSONObject data = list.getJSONObject(i);
                            user_id = Util.nullString(data.getString("USER_ID"), "");
                            user_name = data.getString("USER_NAME");
                            user_desc = data.getString("USER_DESC");
                            emp_id = data.getString("EMP_ID");
                            ou_id = data.getString("OU_ID");
                            org_id = data.getString("ORGANIZATION_ID");
                            org_code = data.getString("ORGANIZATION_CODE");
                            subinv_code = data.getString("SUBINVENTORY_CODE");
                        }
                        Log.v(Comm.LOG_TAG, "OU :"+ou_id+", ORG :"+org_id+", SUBINV :"+subinv_code );
                    }
                } catch (JSONException e) {
                    Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                } catch (Exception e1) {
                    Log.v(Comm.LOG_TAG, "Exception Error ::"+e1.getMessage());
                }


                // BT 장치 조회를 한다.
                //TODO 밑 라인 해제해야함

                if(check_id.isChecked()) {
                    editor = pref.edit();
                    editor.putString("LOGIN_USER_ID", edit_id.getText().toString());
                    editor.putString("LOGIN_USER_PW", edit_pw.getText().toString());
                    editor.apply();
                }

                editor = pref.edit();
                editor.putString("USER_ID", user_id);
                editor.putString("USER_NAME", user_name);
                editor.putString("USER_DESC", user_desc);
                editor.putString("EMP_ID", emp_id);
                editor.putString("OU_ID", ou_id);
                editor.putString("ORG_ID", org_id);
                editor.putString("ORG_CODE", org_code);
                editor.putString("SUBINVENTORY_CODE", subinv_code);
                editor.apply();

                Intent intent = null;
//                if(Util.isNull(ou_id) || Util.isNull(org_id)){
                intent = new Intent(getApplicationContext(), MenuActivity.class);
//                }else if(Util.isNull(subinv_code)){
//                    intent = new Intent(getApplicationContext(), ChangeSubInvActivity.class);
//                }else{
//                    intent = new Intent(getApplicationContext(), MenuActivity.class);
//                }

                startActivity(intent);
            }
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(dialog != null)
                dialog.dismiss();
            finish();
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    void checkBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_bluetooth), Toast.LENGTH_LONG).show();
            //finish();  // App 종료
        }

        else {
            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }else{

                selectDevice();

            }
        }
    }

    void selectDevice() {

        mDevices = mBluetoothAdapter.getBondedDevices();
        mPariedDeviceCount = mDevices.size();

        if(mPariedDeviceCount == 0 ) {
            //Toast.makeText(getApplicationContext(), getString(R.string.not_pairing), Toast.LENGTH_LONG).show();
            //finish(); // App 종료.
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.login_bluetooth_select));

        List<String> listItems = new ArrayList<String>();
        for(BluetoothDevice device : mDevices) {

            listItems.add(device.getAddress());
        }
        listItems.add(getString(R.string.cancel));  // 취소 항목 추가.

        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                // TODO Auto-generated method stub
                if(item == mPariedDeviceCount) {
                    //Toast.makeText(getApplicationContext(), getString(R.string.not_select_mac), Toast.LENGTH_LONG).show();
                    //finish();
                }
                else {
                    //connectToSelectedDevice(items[item].toString());
                    //BT_ADDR = items[item].toString();
                    SettingsHelper.saveBluetoothAddress(LoginActivity.this, items[item].toString());
                    //mac_addr.setText(BT_ADDR);

                    //loginIntent = new Intent(getApplicationContext(),LoginFacActivity.class);
                    //loginIntent.putExtra("USER_ID", user_id);
                    //loginIntent.putExtra("USER_NAME", user_name);
                    //loginIntent.putExtra("USER_DESC", user_desc);
                    //loginIntent.putExtra("EMP_ID", emp_id);
                    //startActivity(loginIntent);

                }
            }

        });

        builder.setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }

}