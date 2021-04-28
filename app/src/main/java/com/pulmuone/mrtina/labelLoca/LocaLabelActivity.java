package com.pulmuone.mrtina.labelLoca;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pulmuone.mrtina.R;
import com.pulmuone.mrtina.batchComp.BatchCompActivity;
import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.comm.MyEvent;
import com.pulmuone.mrtina.comm.Network;
import com.pulmuone.mrtina.comm.NetworkEvent;
import com.pulmuone.mrtina.comm.Scan;
import com.pulmuone.mrtina.labelPalt.PalletLabelActivity;
import com.pulmuone.mrtina.popupLoca.PopLocaActivity;
import com.pulmuone.mrtina.popupOrg.PopOrgActivity;
import com.pulmuone.mrtina.popupSubInv.PopSubInvActivity;
import com.pulmuone.mrtina.popupTranPall.PopTranPallActivity;
import com.pulmuone.mrtina.recvTro.RecvTroActivity;
import com.pulmuone.mrtina.tranTro.TranTroActivity;
import com.pulmuone.mrtina.utils.DemoSleeper;
import com.pulmuone.mrtina.utils.SettingsHelper;
import com.pulmuone.mrtina.utils.Util;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.ZebraPrinterLinkOs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class LocaLabelActivity extends Activity {
    private LocaLabelAdapter pltAdapter;
    ArrayList<LocaLabelItem> pltList = null;
    private RelativeLayout btn_back = null;

    private ZebraPrinter printer;
    private Connection connection;

    int mPariedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;

    BluetoothAdapter mBluetoothAdapter;
    boolean tcpYn = true;
    boolean labelBig = true;

    SharedPreferences pref = null;
    private String S_USER_ID = null;
    private String ORG_ID = null;
    private String OU_ID = null;

    private LinearLayout btn_pallNo = null;
    private LinearLayout btn_inv = null;
    private LinearLayout btn_loc = null;
    private LinearLayout btn_loc1 = null;
    private LinearLayout btn_org = null;

    private Button btnFind = null;
    private Button btnPrint = null;

    private EditText edit_inv = null;
    private EditText edit_loc = null;
    private EditText edit_loc1 = null;
    private EditText edit_pallet = null;
    private EditText edit_org = null;

    RadioButton rdoLarge , rdoSmall;
    private Network network = null;
    JSONArray printList = null;
    private Scan Scan = null; //SCANNER CODE
    private String locatorBarcode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_loca);

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);
        S_USER_ID = pref.getString("USER_ID", "");

        btn_back = (RelativeLayout) findViewById(R.id.layout_back);

        edit_inv = (EditText) findViewById(R.id.edit_sub_inv);
        edit_pallet = (EditText) findViewById(R.id.edit_pallet);
        edit_loc = (EditText) findViewById(R.id.edit_loc);
        edit_loc1 = (EditText) findViewById(R.id.edit_loc1);
        edit_org = (EditText) findViewById(R.id.edit_org);

        btnFind = (Button) findViewById(R.id.btn_find);
        btnPrint = (Button) findViewById(R.id.btn_print);
        rdoLarge = (RadioButton) findViewById(R.id.label_large);
        rdoSmall = (RadioButton) findViewById(R.id.label_small);

        btn_pallNo = (LinearLayout) findViewById(R.id.layout_pallet);
        btn_inv = (LinearLayout) findViewById(R.id.layout_sub_inv);
        btn_loc = (LinearLayout) findViewById(R.id.layout_loc);
        btn_loc1 = (LinearLayout) findViewById(R.id.layout_loc1);
        btn_org = (LinearLayout) findViewById(R.id.layout_org);

        edit_pallet.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_inv.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_loc.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_loc1.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_org.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        edit_loc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_loc.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_loc1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_loc1.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_pallet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_pallet.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_inv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_inv.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_org.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_org.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        rdoLarge.setChecked(true);
        btnFind.setOnClickListener(onClickListener);
        btnPrint.setOnClickListener(onClickListener);
        btn_pallNo.setOnClickListener(onClickListener);
        btn_loc.setOnClickListener(onClickListener);
        btn_loc1.setOnClickListener(onClickListener);
        btn_inv.setOnClickListener(onClickListener);
        btn_back.setOnClickListener(onClickListener);
        btn_org.setOnClickListener(onClickListener);
        edit_inv.setOnClickListener(onClickListener);
        edit_pallet.setOnClickListener(onClickListener);
        edit_loc.setOnClickListener(onClickListener);
        edit_loc1.setOnClickListener(onClickListener);
        edit_org.setOnClickListener(onClickListener);

        btn_pallNo.setEnabled(false);
        btn_loc.setEnabled(false);
        btn_loc1.setEnabled(false);
        btn_inv.setEnabled(false);

        edit_pallet.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
        edit_loc.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
        edit_loc1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
        edit_inv.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));

        edit_pallet.setEnabled(false);
        edit_loc.setEnabled(false);
        edit_loc1.setEnabled(false);
        edit_inv.setEnabled(false);

        //         [[ SCANNER CODE
        if(Comm.isLibraryInstalled(this,"com.symbol.emdk")) {
            this.Scan = new Scan(this);
            Scan.setEvent(myListener);
        }
        //         ]] SCANNER CODE
    }


    MyEvent myListener = new MyEvent() {
        @Override
        public void onEvent(String data) {
            Log.v(Comm.LOG_TAG, "MyEvent:[["+data+"]]");
            locatorBarcode = data;
            String[] code = data.split(";");

            //locator 관련 바코드일 때
            if(code.length == 4 && code[0].equals("L")) {
                    //scanSubInv = code[2];
                LocaLabelActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                        param.put("locatorBarcode", Util.nullString((String)locatorBarcode, ""));

                        network = new Network(LocaLabelActivity.this, listener, "/api/label/scanLabelLocatorList");
                        network.execute(param);
                    }
                });
            }
        }
    };
    // ]] SCANNER CODE

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if (arg0.equals(btnFind)){
                getLocaLabelList();

            } else if (arg0.equals(edit_inv)){
                edit_inv.setSelection(edit_inv.length());

            } else if (arg0.equals(edit_pallet)){
                edit_pallet.setSelection(edit_pallet.length());

            } else if (arg0.equals(edit_loc)){
                edit_loc.setSelection(edit_loc.length());

            } else if (arg0.equals(edit_loc1)){
                edit_loc1.setSelection(edit_loc1.length());

            }else if (arg0.equals(edit_org)){
                edit_org.setSelection(edit_org.length());

            } else if(arg0.equals(btnPrint)){
                checkSize();
                boolean checkFlag = false;

                try{
                    int count = pltAdapter.getCount();

                    for(int i = 0; i < count; i++){
                        LocaLabelItem item = (LocaLabelItem) pltAdapter.getItem(i);
                        if(item.CHECK_ITEM.equals("True")) {
                            checkFlag = true;
                        }
                    }
                    if(checkFlag)
                        checkBluetooth(); // 프린트 관련 시작은 블루투스 기기 선택 부터

                }catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LocaLabelActivity.this);
                    builder
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.alert_no_print_item))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();
                }

            } else if(arg0.equals(btn_pallNo)){

                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

                param.put("ouId", Util.nullString(OU_ID, ""));
                param.put("orgId", Util.nullString(ORG_ID, ""));
                param.put("userNo", Util.nullString(S_USER_ID, ""));
                param.put("searchPallet", Util.nullString(edit_pallet.getText().toString(), ""));
                param.put("searchItem", "");

                network = new Network(LocaLabelActivity.this, listener, "/api/prod/batchPalletList");
                network.execute(param);

            } else if(arg0.equals(btn_loc)){

                Intent intent = new Intent(getApplicationContext(), PopLocaActivity.class);
                intent.putExtra("SUBINVENTORY_CODE", edit_inv.getText().toString());
                intent.putExtra("ORG_ID", ORG_ID);
                startActivityForResult(intent, Comm.LOC);

            }  else if(arg0.equals(btn_loc1)){

                Intent intent = new Intent(getApplicationContext(), PopLocaActivity.class);
                intent.putExtra("SUBINVENTORY_CODE", edit_inv.getText().toString());
                intent.putExtra("ORG_ID", ORG_ID);
                startActivityForResult(intent, Comm.LOC_TO);

            }else if(arg0.equals(btn_inv)){

                Intent intent = new Intent(getApplicationContext(), PopSubInvActivity.class);
                intent.putExtra("ORG_ID", ORG_ID);
                startActivityForResult(intent, Comm.SUBINV);

            } else if(arg0.equals(btn_org)){

                Intent intent = new Intent(getApplicationContext(), PopOrgActivity.class);
                startActivityForResult(intent, Comm.DETAIL);

            } else if(arg0.equals(btn_back)){
                finish();

            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == Comm.REQUEST_ENABLE_BT) {
                //connectionToUnion();
            }else if (requestCode == Comm.LOC) {
                edit_loc.setText(Util.nullString(data.getStringExtra("LOCATOR_CODE"), ""));

            }else if (requestCode == Comm.LOC_TO) {
                edit_loc1.setText(Util.nullString(data.getStringExtra("LOCATOR_CODE"), ""));

            }else if (requestCode == Comm.DETAIL) {
                edit_org.setText(Util.nullString(data.getStringExtra("ORG_CODE"), ""));
                OU_ID = Util.nullString(data.getStringExtra("OU_ID"), "");
                ORG_ID = Util.nullString(data.getStringExtra("ORG_ID"), "");

                Log.v(Comm.LOG_TAG, "OU_ID: " + OU_ID);
                Log.v(Comm.LOG_TAG, "ORG_ID: " + ORG_ID);

                btn_inv.setEnabled(true);
                edit_inv.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input));
                edit_inv.setEnabled(true);

            }else if (requestCode == Comm.SUBINV) {
                edit_loc.setText("");
                edit_inv.setText(Util.nullString(data.getStringExtra("SUBINV_CODE"), ""));

                if(data.getStringExtra("LOCATOR_CONTROL").equals("1")){
                    edit_loc.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
                    edit_loc.setEnabled(false);
                    btn_loc.setEnabled(false);
                    edit_loc1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
                    edit_loc1.setEnabled(false);
                    btn_loc1.setEnabled(false);
                }else{
                    edit_loc.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input));
                    edit_loc.setEnabled(true);
                    btn_loc.setEnabled(true);
                    edit_loc1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input));
                    edit_loc1.setEnabled(true);
                    btn_loc1.setEnabled(true);
                }

                if(data.getStringExtra("PALLET_CONTROL").equals("N")){
                    edit_pallet.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
                    edit_pallet.setEnabled(false);
                    btn_pallNo.setEnabled(false);
                }else{
                    edit_pallet.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input));
                    edit_pallet.setEnabled(true);
                    btn_pallNo.setEnabled(true);
                }

            }else if (requestCode == Comm.TRANPALLET) {
                edit_pallet.setText(Util.nullString(data.getStringExtra("PALLET_NUMBER"), ""));

            }
        }
    }

    private void checkSize(){
        if(rdoLarge.isChecked()){
            labelBig = true;
        }else{
            labelBig = false;
        }
    }

    private String createZplReceipt() {
        String wholeZplLabel = "";
        int count = pltAdapter.getCount();
        String beforeBarcode = "";
        String beforeLocator = "";
        String beforeSubinv = "";

        for(int i = 0; i < count; i++){
            LocaLabelItem item = (LocaLabelItem) pltAdapter.getItem(i);
            if(item.CHECK_ITEM.equals("True")){
                if(tcpYn) {
                    if(labelBig) {
                        wholeZplLabel += Util.labelLocatorNet(item.LOCATOR_BAR_CODE, item.SUBINVENTORY_CODE, item.LOCATOR_CODE);

                    }else {
                        Log.v(Comm.LOG_TAG, "beforeBarcode  1 :: " + beforeBarcode);
                        Log.v(Comm.LOG_TAG, "beforeLocator  1 :: " + beforeLocator);
                        Log.v(Comm.LOG_TAG, "beforeSubinv   1 :: " + beforeSubinv);
                        if(beforeLocator != "") {
                            Log.v(Comm.LOG_TAG, "beforeLOCA     1 :: " + beforeLocator.substring(0, beforeLocator.length() - 1));
                        }
                        Log.v(Comm.LOG_TAG, "beforeBarcode  2 :: " + item.LOCATOR_BAR_CODE);
                        Log.v(Comm.LOG_TAG, "beforeLocator  2 :: " + item.LOCATOR_CODE);
                        Log.v(Comm.LOG_TAG, "beforeSubinv   2 :: " + item.SUBINVENTORY_CODE);
                        Log.v(Comm.LOG_TAG, "beforeLOCA     2 :: " + item.LOCATOR_CODE.substring(0, item.LOCATOR_CODE.length() - 1));

                        if(beforeLocator == ""){
                            beforeLocator = item.LOCATOR_CODE;
                            beforeBarcode = item.LOCATOR_BAR_CODE;
                            beforeSubinv = item.SUBINVENTORY_CODE;
                        }else if (beforeLocator.substring(0, beforeLocator.length() - 1).equals(item.LOCATOR_CODE.substring(0, item.LOCATOR_CODE.length() - 1))) {
                            Log.v(Comm.LOG_TAG, "PRINT  12 :: " + beforeBarcode+"=="+item.LOCATOR_BAR_CODE);
                            wholeZplLabel += Util.labelLocatorNetS(item.LOCATOR_BAR_CODE, item.SUBINVENTORY_CODE + "." + item.LOCATOR_CODE,beforeBarcode, beforeSubinv + "." + beforeLocator);
                            beforeLocator = "";
                            beforeBarcode = "";
                            beforeSubinv = "";
                        }else{
                            String type = beforeLocator.substring( beforeLocator.length() - 1);
                            if(type.equals("B")){
                                Log.v(Comm.LOG_TAG, "PRINT  22 :: " + "??????=="+beforeBarcode);
                                wholeZplLabel += Util.labelLocatorNetS("","",beforeBarcode, beforeSubinv + "." + beforeLocator);
                            }else{ // }else if(type.equals("F")){
                                Log.v(Comm.LOG_TAG, "PRINT  11 :: " + beforeBarcode+"==??????");
                                wholeZplLabel += Util.labelLocatorNetS(beforeBarcode, beforeSubinv + "." + beforeLocator, "", "");
                            }
                            beforeLocator = item.LOCATOR_CODE;
                            beforeBarcode = item.LOCATOR_BAR_CODE;
                            beforeSubinv = item.SUBINVENTORY_CODE;
                        }
                    }
                }else{
                    wholeZplLabel += Util.labelLocatorBlt(item.LOCATOR_BAR_CODE);

                }
            }
        }

        if(!beforeLocator.equals("")) {
            String type = beforeLocator.substring(beforeLocator.length() - 1);
            if (type.equals("F")) {
                wholeZplLabel += Util.labelLocatorNetS(beforeBarcode, beforeSubinv + "." + beforeLocator, "", "");
            } else if (type.equals("B")) {
                wholeZplLabel += Util.labelLocatorNetS("", "", beforeBarcode, beforeSubinv + "." + beforeLocator);
            }
        }
        return wholeZplLabel;
    }

    private void getLocaLabelList() {
        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
        param.put("orgId", Util.nullString(ORG_ID,""));
        param.put("palletNo", Util.nullString(edit_pallet.getText().toString(),""));
        param.put("subinvCode", Util.nullString(edit_inv.getText().toString(),""));
        param.put("locatorCode", Util.nullString(edit_loc.getText().toString(),""));
        param.put("locatorCodeTo", Util.nullString(edit_loc1.getText().toString(),""));

        network = new Network(LocaLabelActivity.this, listener, "/api/label/labelLocatorList");
        network.execute(param);
    }

    private void checkList(JSONArray list) {
        if(list.length() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setTitle(getString(R.string.note))
                    .setMessage(getString(R.string.alert_no_item))
                    .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .show();
        }
    }

    NetworkEvent listener = new NetworkEvent() {
        @Override
        public void doInBackground(LinkedHashMap<String, String>... params) {

        }

        @Override
        public void onPostExecute(JSONObject obj, String api) {
            if(api.equals("/api/prod/batchPalletList")){
                try{
                    JSONArray list = obj.getJSONArray("list");

                    if (list.length() == 1) {
                        JSONObject data = list.getJSONObject(0);

                        edit_pallet.setText(Util.nullString(data.getString("PALLET_NUMBER"), ""));
                    }else{
                        Intent intent = new Intent(getApplicationContext(), PopTranPallActivity.class);
                        intent.putExtra("ORG_ID", ORG_ID);
                        intent.putExtra("PALL_NO", edit_pallet.getText().toString());

                        startActivityForResult(intent, Comm.TRANPALLET);
                    }

                } catch (JSONException e) {
                    Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                } catch (Exception e1) {
                    Log.v(Comm.LOG_TAG, "Exception Error ::"+e1.getMessage());
                }

            } else if(api.equals("/api/label/labelLocatorList")){
                pltList = new ArrayList<>();
                ListView list_label = (ListView) findViewById(R.id.list_locator);

                try {
                    JSONArray list = obj.getJSONArray("list");
                    printList = obj.getJSONArray("print");
                    checkList(list);

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject data = list.getJSONObject(i);

                        LocaLabelItem info = new LocaLabelItem(
                                Util.nullString(data.getString("ORGANIZATION_ID"), "")
                                , Util.nullString(data.getString("ORGANIZATION_CODE"), "")
                                , Util.nullString(data.getString("SUBINVENTORY_CODE"), "")
                                , Util.nullString(data.getString("INVENTORY_LOCATION_ID"), "")
                                , Util.nullString(data.getString("LOCATOR_CODE"), "")
                                , Util.nullString(data.getString("LOCATOR_NAME"), "")
                                , Util.nullString(data.getString("LOCATOR_BAR_CODE"), "")
                                , Util.nullString(data.getString("PALLET_NUMBER"), "")
                                , Util.nullString(data.getString("ITEM_CODE"), "")
                                , Util.nullString(data.getString("ITEM_DESC"), "")
                                ,"True"
                        );
                        pltList.add(info);
                    }
                    pltAdapter = new LocaLabelAdapter(LocaLabelActivity.this, pltList);
                    list_label.setAdapter(pltAdapter);
                    list_label.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        }
                    });
                } catch (JSONException e) {
                    Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                } catch (Exception e1) {
                    Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
                }
            }else if(api.equals("/api/label/scanLabelLocatorList")){
                pltList = new ArrayList<>();
                ListView list_label = (ListView) findViewById(R.id.list_locator);

                try {
                    JSONArray list = obj.getJSONArray("list");
                    printList = obj.getJSONArray("print");
                    checkList(list);

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject data = list.getJSONObject(i);
                        ORG_ID = Util.nullString(data.getString("ORGANIZATION_ID"), "");
                        edit_org.setText(Util.nullString(data.getString("ORGANIZATION_CODE"), ""));
                        edit_inv.setText(Util.nullString(data.getString("SUBINVENTORY_CODE"), ""));
                        edit_loc.setText(Util.nullString(data.getString("LOCATOR_CODE"), ""));
                        edit_loc1.setText(Util.nullString(data.getString("LOCATOR_CODE"), ""));
                        edit_loc.setBackground(ContextCompat.getDrawable(LocaLabelActivity.this, R.drawable.bg_input));
                        edit_loc.setEnabled(true);
                        btn_loc.setEnabled(true);
                        edit_loc1.setBackground(ContextCompat.getDrawable(LocaLabelActivity.this, R.drawable.bg_input));
                        edit_loc1.setEnabled(true);
                        btn_loc1.setEnabled(true);
                        edit_inv.setBackground(ContextCompat.getDrawable(LocaLabelActivity.this, R.drawable.bg_input));
                        btn_inv.setEnabled(true);
                        edit_inv.setEnabled(true);
                        edit_pallet.setBackground(ContextCompat.getDrawable(LocaLabelActivity.this, R.drawable.bg_input));
                        edit_pallet.setEnabled(true);
                        btn_pallNo.setEnabled(true);

                        LocaLabelItem info = new LocaLabelItem(
                                Util.nullString(data.getString("ORGANIZATION_ID"), "")
                                , Util.nullString(data.getString("ORGANIZATION_CODE"), "")
                                , Util.nullString(data.getString("SUBINVENTORY_CODE"), "")
                                , Util.nullString(data.getString("INVENTORY_LOCATION_ID"), "")
                                , Util.nullString(data.getString("LOCATOR_CODE"), "")
                                , Util.nullString(data.getString("LOCATOR_NAME"), "")
                                , Util.nullString(data.getString("LOCATOR_BAR_CODE"), "")
                                , Util.nullString(data.getString("PALLET_NUMBER"), "")
                                , Util.nullString(data.getString("ITEM_CODE"), "")
                                , Util.nullString(data.getString("ITEM_DESC"), "")
                                ,"True"
                        );
                        pltList.add(info);
                    }
                    pltAdapter = new LocaLabelAdapter(LocaLabelActivity.this, pltList);
                    list_label.setAdapter(pltAdapter);
                    list_label.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

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

    @Override
    public void onResume() {
        super.onResume();
        if(Scan!=null) {
            Scan.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(Scan!=null) {
            Scan.onPause();
        }
    }

    /*********************************************
     *
     *        [[ 여기서 부터 프린트 관련 코드 ]]
     *
     ***********************************************/
    @Override
    public void onDestroy() {

        disconnect();
        if(Scan!=null) {
            Scan.onDestroy();
        }
        super.onDestroy();
    }

    private void printStart(){

        printThread thread = new printThread();
        thread.setDaemon(true);
        thread.start();

    }

    private void doConnectionBT() {
        printer = connect();

        if (printer != null) {
            sendLabel();
        } else {
            disconnect();
        }
    }

    public ZebraPrinter connect() {
        setStatus(getString(R.string.printer_connecting), Color.YELLOW);
        connection = null;
        if(tcpYn == true) {
            connection = new TcpConnection(SettingsHelper.getIp(LocaLabelActivity.this), Integer.valueOf(Comm.TCP_PORT_NUMBER));
        }else {
            connection = new BluetoothConnection(SettingsHelper.getBluetoothAddress(LocaLabelActivity.this));
        }
        Log.v(Comm.LOG_TAG, "CONNECT : " +SettingsHelper.getIp(LocaLabelActivity.this));
        try {
            Log.v(Comm.LOG_TAG, "connection.open() : START" );
            connection.open();
            setStatus(getString(R.string.printer_connected) , Color.GREEN);
            Log.v(Comm.LOG_TAG, "connection.open() : END" );
        } catch (ConnectionException e) {
            Log.v(Comm.LOG_TAG, "ConnectionException : "+e.toString() );
            setStatus(getString(R.string.printer_error_disconnected), Color.RED);
            //DemoSleeper.sleep(1000);
            disconnect();
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LocaLabelActivity.this)
                    .setTitle(getString(R.string.warning))
                    .setMessage(e.toString())
                    .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.show();
        }

        //setStatus("프린트 중간~", Color.YELLOW);
        Log.v(Comm.LOG_TAG, "ING~~~~~~~~~~~ : " );
        ZebraPrinter printer = null;

        if (connection.isConnected()) {
            try {

                printer = ZebraPrinterFactory.getInstance(connection);
                setStatus(getString(R.string.printer_loading_lang), Color.YELLOW);
                String pl = SGD.GET("device.languages", connection);
                setStatus(getString(R.string.printer_lang)   + pl, Color.BLUE);
            } catch (ConnectionException e) {
                setStatus(getString(R.string.printer_lang_unknown), Color.RED);
                printer = null;
                //DemoSleeper.sleep(1000);
                Log.v(Comm.LOG_TAG, "loop print_cnt : " );
                disconnect();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LocaLabelActivity.this)
                        .setTitle(getString(R.string.warning))
                        .setMessage(e.toString())
                        .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();
            } catch (ZebraPrinterLanguageUnknownException e) {
                setStatus(getString(R.string.printer_lang_unknown), Color.RED);
                printer = null;
                //DemoSleeper.sleep(1000);
                disconnect();
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LocaLabelActivity.this)
                        .setTitle(getString(R.string.warning))
                        .setMessage(e.toString())
                        .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();
            }
        }

        return printer;
    }

    public void disconnect() {
        try {
            setStatus(getString(R.string.printer_disconnecting), Color.RED);
            if (connection != null) {
                connection.close();
            }
            setStatus(getString(R.string.printer_disconnected), Color.RED);
        } catch (ConnectionException e) {
            setStatus(getString(R.string.printer_unknown_error_disconnected) , Color.RED);
        } finally {
            //finish();
        }
    }

    private void setStatus(final String statusMessage, final int color) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), statusMessage, Toast.LENGTH_LONG).show();
            }
        });
        //DemoSleeper.sleep(1000);
    }

    private void sendLabel() {

        try {

            ZebraPrinterLinkOs linkOsPrinter = ZebraPrinterFactory.createLinkOsPrinter(printer);

            PrinterStatus printerStatus = (linkOsPrinter != null) ? linkOsPrinter.getCurrentStatus() : printer.getCurrentStatus();

            if (printerStatus.isReadyToPrint) {
                Log.v(Comm.LOG_TAG, "printerStatus.isReadyToPrint : " + printerStatus.isReadyToPrint);
                sendTestLabel();
            } else if (printerStatus.isHeadOpen) {
                Log.v(Comm.LOG_TAG, "printerStatus.isHeadOpen : " + printerStatus.isHeadOpen);
                setStatus(getString(R.string.printer_header_open), Color.RED);
            } else if (printerStatus.isPaused) {
                Log.v(Comm.LOG_TAG, "printerStatus.isPaused : " + printerStatus.isPaused);
                setStatus(getString(R.string.printer_stop) , Color.RED);
            } else if (printerStatus.isPaperOut) {
                Log.v(Comm.LOG_TAG, "printerStatus.isPaperOut : " + printerStatus.isPaperOut);
                setStatus(getString(R.string.printer_paper_out) , Color.RED);
            }
            //DemoSleeper.sleep(1500);
            if (connection instanceof BluetoothConnection) {
                String friendlyName = ((BluetoothConnection) connection).getFriendlyName();
                setStatus(friendlyName, Color.MAGENTA);
                //DemoSleeper.sleep(500);
            }
        } catch (ConnectionException e) {
            setStatus(e.getMessage(), Color.RED);
        } finally {
            disconnect();
        }
    }


    private void sendTestLabel() {
        try {
            byte[] configLabel = createZplReceipt().getBytes();
            printer.getConnection().write(configLabel);
            DemoSleeper.sleep(1500);
            if (printer.getConnection() instanceof BluetoothConnection) {
                DemoSleeper.sleep(500);
            }
        } catch (ConnectionException e) {
        }
    }

    void checkBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_bluetooth), Toast.LENGTH_LONG).show();
        }

        else {
            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, Comm.REQUEST_ENABLE_BT);
            }else{

                selectDevice();

            }
        }
    }

    void selectDevice() {
        try{
            mDevices = mBluetoothAdapter.getBondedDevices();
            mPariedDeviceCount = mDevices.size();

            if(mPariedDeviceCount == 0 ) {
                Toast.makeText(getApplicationContext(), getString(R.string.not_pairing), Toast.LENGTH_LONG).show();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(tcpYn==true?getString(R.string.login_network_select):getString(R.string.login_bluetooth_select));

            List<String> listItems = new ArrayList<String>();
            if(tcpYn) {
                for (int i = 0; i < printList.length(); i++) {
                    JSONObject data = printList.getJSONObject(i);
                    listItems.add(data.getString("IP_NAME"));
                }
                mPariedDeviceCount = Comm.TCP_ADDR.length;
            }else {

                for (BluetoothDevice device : mDevices) {

                    listItems.add(device.getAddress());
                }
                //listItems.add(getString(R.string.cancel));  // 취소 항목 추가.
            }
            final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
            listItems.toArray(new CharSequence[listItems.size()]);

            if(tcpYn) {
                if(printList.length() > 0) {
                    JSONObject data = printList.getJSONObject(0);
                    SettingsHelper.saveIp(LocaLabelActivity.this, data.getString("IP_CODE"));
                }
            }else{
                if(listItems.size() > 0) {
                    SettingsHelper.saveBluetoothAddress(LocaLabelActivity.this, items[0].toString());
                }
            }

            builder.setSingleChoiceItems(items, 0,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    // TODO Auto-generated method stub
                    if(tcpYn) {
                        try{
                            //SettingsHelper.saveIp(LocaLabelActivity.this, Comm.TCP_ADDR[item]);
                            JSONObject data = printList.getJSONObject(item);
                            SettingsHelper.saveIp(LocaLabelActivity.this, data.getString("IP_CODE"));

                        } catch (JSONException e) {
                            Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                        } catch (Exception e1) {
                            Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
                        }
                    }else{
                        SettingsHelper.saveBluetoothAddress(LocaLabelActivity.this, items[item].toString());

                    }
                }});

            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //compSaveAlert();

                }
            });

            builder.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    printStart();

                }
            });


            builder.setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();
        } catch (JSONException e) {
            Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
        } catch (Exception e1) {
            Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
        }
    }

    class printThread extends Thread{
        @Override
        public void run() {
            while(true){
                Looper.prepare();
                doConnectionBT();
                Looper.loop();
                Looper.myLooper().quit();
            } // end while
        } // end run()
    } // end class BackThread

}

