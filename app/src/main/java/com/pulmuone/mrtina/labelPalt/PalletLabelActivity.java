package com.pulmuone.mrtina.labelPalt;

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
import android.app.AlertDialog;
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
import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.comm.HttpClient;
import com.pulmuone.mrtina.comm.MyEvent;
import com.pulmuone.mrtina.comm.Network;
import com.pulmuone.mrtina.comm.NetworkEvent;
import com.pulmuone.mrtina.comm.Scan;
import com.pulmuone.mrtina.labelLoca.LocaLabelActivity;
import com.pulmuone.mrtina.popupBatch.PopBatchActivity;
import com.pulmuone.mrtina.popupLoca.PopLocaActivity;
import com.pulmuone.mrtina.popupOrg.PopOrgActivity;
import com.pulmuone.mrtina.popupPall.PopPallActivity;
import com.pulmuone.mrtina.popupSubInv.PopSubInvActivity;
import com.pulmuone.mrtina.recvTro.RecvTroActivity;
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

public class PalletLabelActivity extends Activity {
    private PalletLabelAdapter pltAdapter;
    ArrayList<PalletLabelItem> pltList = null;

    private RelativeLayout btn_back = null;
    private Button btnFind = null;
    private Button btnPrint = null;

    private LinearLayout btn_pallNo = null;
    private LinearLayout btn_batchNo = null;
    private LinearLayout btn_inv = null;
    private LinearLayout btn_loc = null;
    private LinearLayout btn_org = null;

    private EditText edit_batch = null;
    private EditText edit_inv = null;
    private EditText edit_loc = null;
    private EditText edit_pallet = null;
    private EditText edit_org = null;

    RadioButton rdoLarge , rdoSmall;
    private ZebraPrinter printer;
    private Connection connection;

    int mPariedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;

    BluetoothAdapter mBluetoothAdapter;
    boolean tcpYn = false;

    SharedPreferences pref = null;

    private String S_USER_ID = null;
    private String ORG_ID = null;
    private String OU_ID = null;
    private String SUBINVENTORY_CODE = null;

    private Network network = null;
    JSONArray printList = null;
    private Scan Scan = null; //SCANNER CODE
    private String locatorBarcode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_palt);

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);
        S_USER_ID = pref.getString("USER_ID", "");

        btn_back = (RelativeLayout) findViewById(R.id.layout_back);
        btnFind = (Button) findViewById(R.id.btn_find);
        btnPrint = (Button) findViewById(R.id.btn_print);
        rdoLarge = (RadioButton) findViewById(R.id.label_large);
        rdoSmall = (RadioButton) findViewById(R.id.label_small);

        btn_pallNo = (LinearLayout) findViewById(R.id.layout_pallet);
        btn_batchNo = (LinearLayout) findViewById(R.id.layout_batch);
        btn_inv = (LinearLayout) findViewById(R.id.layout_sub_inv);
        btn_loc = (LinearLayout) findViewById(R.id.layout_loc);
        btn_org = (LinearLayout) findViewById(R.id.layout_org);

        edit_batch = (EditText) findViewById(R.id.edit_batch);
        edit_inv = (EditText) findViewById(R.id.edit_sub_inv);
        edit_pallet = (EditText) findViewById(R.id.edit_pallet);
        edit_loc = (EditText) findViewById(R.id.edit_loc);
        edit_org = (EditText) findViewById(R.id.edit_org);

        edit_batch.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_loc.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_pallet.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_inv.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_org.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        edit_batch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_batch.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_loc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_loc.onEditorAction(EditorInfo.IME_ACTION_DONE);
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
        btn_batchNo.setOnClickListener(onClickListener);
        btn_loc.setOnClickListener(onClickListener);
        btn_inv.setOnClickListener(onClickListener);
        btn_back.setOnClickListener(onClickListener);
        btn_org.setOnClickListener(onClickListener);

        edit_inv.setOnClickListener(onClickListener);
        edit_pallet.setOnClickListener(onClickListener);
        edit_loc.setOnClickListener(onClickListener);
        edit_batch.setOnClickListener(onClickListener);
        edit_org.setOnClickListener(onClickListener);

        btn_pallNo.setEnabled(false);
        btn_batchNo.setEnabled(false);
        btn_loc.setEnabled(false);
        btn_inv.setEnabled(false);

        edit_batch.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
        edit_pallet.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
        edit_loc.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
        edit_inv.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));

        edit_batch.setEnabled(false);
        edit_pallet.setEnabled(false);
        edit_loc.setEnabled(false);
        edit_inv.setEnabled(false);

        //  [[ SCANNER CODE
        if(Comm.isLibraryInstalled(this,"com.symbol.emdk")) {
            this.Scan = new Scan(this);
            Scan.setEvent(myListener);
        }
        //  ]] SCANNER CODE
    }

    MyEvent myListener = new MyEvent() {
        @Override
        public void onEvent(String data) {
            Log.v(Comm.LOG_TAG, "MyEvent:"+data);
            locatorBarcode = data;
            String[] code = data.split(";");

            //locator 관련 바코드일 때
            if(code.length == 4 && code[0].equals("L")) {
                //scanSubInv = code[2];

            }else if(data.substring(0,1).equals("P")){
                PalletLabelActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                        param.put("palletNo", Util.nullString(locatorBarcode, ""));

                        network = new Network(PalletLabelActivity.this, listener, "/api/label/scanLabelPalletList");
                        network.execute(param);
                    }
                });
            }
        }
    };
    // ]] SCANNER CODE

    View.OnClickListener onClickListener = new View.OnClickListener() {

        private Network network = null;

        @Override
        public void onClick(View arg0) {
            if (arg0.equals(btnFind)){
                getPalletLabelList();

            } else if (arg0.equals(edit_inv)){
                edit_inv.setSelection(edit_inv.length());

            } else if (arg0.equals(edit_pallet)){
                edit_pallet.setSelection(edit_pallet.length());

            } else if (arg0.equals(edit_loc)){
                edit_loc.setSelection(edit_loc.length());

            } else if (arg0.equals(edit_batch)){
                edit_batch.setSelection(edit_batch.length());

            } else if (arg0.equals(edit_org)){
                edit_org.setSelection(edit_org.length());

            } else if(arg0.equals(btnPrint)){

                checkSize();
                boolean checkFlag = false;

                try{
                    int count = pltAdapter.getCount();

                    for(int i = 0; i < count; i++){
                        PalletLabelItem item = (PalletLabelItem) pltAdapter.getItem(i);
                        if(item.CHECK_ITEM.equals("True")){
                            checkFlag = true;
                        }
                    }
                    if(checkFlag)
                        checkBluetooth(); // 프린트 관련 시작은 블루투스 기기 선택 부터
                } catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(PalletLabelActivity.this);
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
                param.put("subinventoryCode", Util.nullString(SUBINVENTORY_CODE, ""));
                param.put("orgId", Util.nullString(ORG_ID, ""));
                param.put("searchKey", Util.nullString(edit_pallet.getText().toString(), ""));
                param.put("itemCode", "");
                param.put("inventoryLocationId", "");
                param.put("locatorCode", "");

                network = new Network(PalletLabelActivity.this, listener, "/api/cmmPopup/palletList");
                network.execute(param);

            }else if(arg0.equals(btn_batchNo)){

                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

                param.put("ouId", Util.nullString(OU_ID, ""));
                param.put("orgId", Util.nullString(ORG_ID, ""));
                param.put("userNo", Util.nullString(S_USER_ID, ""));
                param.put("searchBatch", Util.nullString(edit_batch.getText().toString(), ""));

                network = new Network(PalletLabelActivity.this, listener, "/api/prod/batchNoList");
                network.execute(param);

            }else if(arg0.equals(btn_loc)){

                Intent intent = new Intent(getApplicationContext(), PopLocaActivity.class);
                intent.putExtra("SUBINVENTORY_CODE", edit_inv.getText().toString());
                intent.putExtra("ORG_ID", ORG_ID);
                startActivityForResult(intent, Comm.LOC);

            }else if(arg0.equals(btn_inv)){

                Intent intent = new Intent(getApplicationContext(), PopSubInvActivity.class);
                intent.putExtra("ORG_ID", ORG_ID);
                startActivityForResult(intent, Comm.SUBINV);

            }else if(arg0.equals(btn_org)){

                Intent intent = new Intent(getApplicationContext(), PopOrgActivity.class);
                startActivityForResult(intent, Comm.DETAIL);

            }else if(arg0.equals(btn_back)){
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

            }else if (requestCode == Comm.DETAIL) {
                edit_org.setText(Util.nullString(data.getStringExtra("ORG_CODE"), ""));
                OU_ID = Util.nullString(data.getStringExtra("OU_ID"), "");
                ORG_ID = Util.nullString(data.getStringExtra("ORG_ID"), "");

                Log.v(Comm.LOG_TAG, "OU_ID: " + OU_ID);
                Log.v(Comm.LOG_TAG, "ORG_ID: " + ORG_ID);

                btn_batchNo.setEnabled(true);
                btn_inv.setEnabled(true);

                edit_batch.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input));
                edit_inv.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input));

                edit_batch.setEnabled(true);
                edit_inv.setEnabled(true);

            }else if (requestCode == Comm.SUBINV) {
                edit_loc.setText("");
                edit_pallet.setText("");
                SUBINVENTORY_CODE = Util.nullString(data.getStringExtra("SUBINV_CODE"), "");
                edit_inv.setText(SUBINVENTORY_CODE);

                if(data.getStringExtra("LOCATOR_CONTROL").equals("1")){
                    edit_loc.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
                    edit_loc.setEnabled(false);
                    edit_loc.setFocusable(false);
                    btn_loc.setEnabled(false);
                }else{
                    edit_loc.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input));
                    edit_loc.setEnabled(true);
                    edit_loc.setFocusable(true);
                    btn_loc.setEnabled(true);
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

            }else if (requestCode == Comm.BATCH) {
                edit_batch.setText(Util.nullString(data.getStringExtra("BATCH_NO"), ""));

            }
        }
    }

    private void checkSize(){
        if(rdoLarge.isChecked()){
            tcpYn = true;
        }else{
            tcpYn = false;
        }
    }

    private String createZplReceipt() {
        String wholeZplLabel = "";
        int count = pltAdapter.getCount();
        for(int i = 0; i < count; i++){
            PalletLabelItem item = (PalletLabelItem) pltAdapter.getItem(i);
            if(item.CHECK_ITEM.equals("True")){
                if(tcpYn) {
                    //String[] itemDesc = Util.subStrLen(item.ITEM_DESC,20);

                    wholeZplLabel += Util.labelPalletNet(item.PALLET_NUMBER,item.ITEM_CODE,item.ITEM_DESC,item.LOT_NUMBER,item.EXPIRATION_DATE,item.BATCH_NO);

                    /*wholeZplLabel +=
                            "^XA~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR6,6~SD15^JUS^LRN^CI0^XZ"
                            +"^XA^MMT^PW799^LL2004^LS0^BY448,448^FT494,1513^BXB,32,200,0,0,1,~"
                            +"^FH\\^FD"+item.PALLET_NUMBER+"^FS"
                            +"^FT73,1972^A@B,28,27,TT0003M_^FH\\^CI17^F8^FDItem^FS^CI0"
                            +"^FT215,1971^A@B,28,27,TT0003M_^FH\\^CI17^F8^FDItem Description^FS^CI0"
                            +"^FT390,1971^A@B,28,27,TT0003M_^FH\\^CI17^F8^FDLot^FS^CI0"
                            +"^FT532,1971^A@B,28,27,TT0003M_^FH\\^CI17^F8^FDExpiration Date^FS^CI0"
                            +"^FT675,1971^A@B,28,27,TT0003M_^FH\\^CI17^F8^FDBatch No^FS^CI0"
                            +"^FT707,1575^A@B,28,27,TT0003M_^FH\\^CI17^F8^FDPallet No^FS^CI0"
                            +"^FT720,1432^A@B,56,56,TT0003M_^FH\\^CI17^F8^FD"+item.PALLET_NUMBER+"^FS^CI0"
                            +"^FT151,1961^A@B,68,67,TT0003M_^FH\\^CI17^F8^FD"+item.ITEM_CODE+"^FS^CI0"
                            +"^FT258,1961^A@B,34,33,TT0003M_^FH\\^CI17^F8^FD"+itemDesc[0]+"^FS^CI0"
                            +"^FT444,1961^A@B,45,45,TT0003M_^FH\\^CI17^F8^FD"+item.LOT_NUMBER+"^FS^CI0"
                            +"^FT587,1961^A@B,45,45,TT0003M_^FH\\^CI17^F8^FD"+item.EXPIRATION_DATE+"^FS^CI0"
                            +"^FT729,1961^A@B,45,45,TT0003M_^FH\\^CI17^F8^FD"+item.BATCH_NO+"^FS^CI0"
                            +"^BY448,448^FT494,483^BXB,32,200,0,0,1,~"
                            +"^FH\\^FD"+item.PALLET_NUMBER+"^FS"
                            +"^FT73,942^A@B,28,27,TT0003M_^FH\\^CI17^F8^FDItem^FS^CI0"
                            +"^FT215,941^A@B,28,27,TT0003M_^FH\\^CI17^F8^FDItem Description^FS^CI0"
                            +"^FT390,941^A@B,28,27,TT0003M_^FH\\^CI17^F8^FDLot^FS^CI0"
                            +"^FT532,941^A@B,28,27,TT0003M_^FH\\^CI17^F8^FDExpiration Date^FS^CI0"
                            +"^FT675,941^A@B,28,27,TT0003M_^FH\\^CI17^F8^FDBatch No^FS^CI0"
                            +"^FT707,545^A@B,28,27,TT0003M_^FH\\^CI17^F8^FDPallet No^FS^CI0"
                            +"^FT720,402^A@B,56,56,TT0003M_^FH\\^CI17^F8^FD"+item.PALLET_NUMBER+"^FS^CI0"
                            +"^FT151,931^A@B,68,67,TT0003M_^FH\\^CI17^F8^FD"+item.ITEM_CODE+"^FS^CI0"
                            +"^FT258,931^A@B,34,33,TT0003M_^FH\\^CI17^F8^FD"+itemDesc[0]+"^FS^CI0"
                            +"^FT444,931^A@B,45,45,TT0003M_^FH\\^CI17^F8^FD"+item.LOT_NUMBER+"^FS^CI0"
                            +"^FT587,931^A@B,45,45,TT0003M_^FH\\^CI17^F8^FD"+item.EXPIRATION_DATE+"^FS^CI0"
                            +"^FT729,931^A@B,45,45,TT0003M_^FH\\^CI17^F8^FD"+item.BATCH_NO+"^FS^CI0"
                            +"^FT305,1961^A@B,34,33,TT0003M_^FH\\^CI17^F8^FD"+itemDesc[1]+"^FS^CI0"
                            +"^FT353,1961^A@B,34,33,TT0003M_^FH\\^CI17^F8^FD"+itemDesc[2]+"^FS^CI0"
                            +"^FT308,931^A@B,34,33,TT0003M_^FH\\^CI17^F8^FD"+itemDesc[1]+"^FS^CI0"
                            +"^FT355,931^A@B,34,33,TT0003M_^FH\\^CI17^F8^FD"+itemDesc[2]+"^FS^CI0"
                            +"^PQ1,0,1,Y^XZ";
                    wholeZplLabel +=
                            "^XA~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR6,6~SD15^JUS^LRN^CI0^XZ\n" +
                            "^XA^MMT^PW799^LL2004^LS0\n" +
                            "^FT75,1972^A@B,31,31,TT0003M_^FH\\^CI17^F8^FDItem^FS^CI0\n" +
                            "^FT249,1971^A@B,31,31,TT0003M_^FH\\^CI17^F8^FDItem Description^FS^CI0\n" +
                            "^FT684,1971^A@B,23,22,TT0003M_^FH\\^CI17^F8^FDLot^FS^CI0\n" +
                            "^FT747,1971^A@B,23,22,TT0003M_^FH\\^CI17^F8^FDExpiration Date^FS^CI0\n" +
                            "^FT684,1416^A@B,23,22,TT0003M_^FH\\^CI17^F8^FDBatch No^FS^CI0\n" +
                            "^FT747,1416^A@B,23,22,TT0003M_^FH\\^CI17^F8^FDPallet No^FS^CI0\n" +
                            "^FT743,1232^A@B,34,33,TT0003M_^FH\\^CI17^F8^FD"+item.PALLET_NUMBER+"^FS^CI0\n" +
                            "^FT213,1834^A@B,203,204,TT0003M_^FH\\^CI17^F8^FD"+item.ITEM_CODE+"^FS^CI0\n" +
                            "^FT360,1961^A@B,102,103,TT0003M_^FH\\^CI17^F8^FD"+itemDesc[0]+"^FS^CI0\n" +
                            "^FT686,1787^A@B,34,33,TT0003M_^FH\\^CI17^F8^FD"+item.LOT_NUMBER+"^FS^CI0\n" +
                            "^FT749,1787^A@B,34,33,TT0003M_^FH\\^CI17^F8^FD"+item.EXPIRATION_DATE+"^FS^CI0\n" +
                            "^FT686,1232^A@B,34,33,TT0003M_^FH\\^CI17^F8^FD"+item.BATCH_NO+"^FS^CI0\n" +
                            "^FT487,1961^A@B,102,103,TT0003M_^FH\\^CI17^F8^FD"+itemDesc[1]+"^FS^CI0\n" +
                            "^FT614,1961^A@B,102,103,TT0003M_^FH\\^CI17^F8^FD"+itemDesc[2]+"^FS^CI0\n" +
                            "^BY686,686^FT742,45^BXI,49,200,0,0,1,~\n" +
                            "^FH\\^FD"+item.PALLET_NUMBER+"^FS^PQ1,0,1,Y^XZ";
                  wholeZplLabel +=
                            "^XA~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR6,6~SD15^JUS^LRN^CI0^XZ\n" +
                            "^XA^MMT^PW799^LL2004^LS0\n" +
                            "^FT75,1972^A@B,31,31,TT0003M_^FH\\^CI17^F8^FDItem^FS^CI0\n" +
                            "^FT249,1971^A@B,31,31,TT0003M_^FH\\^CI17^F8^FDItem Description^FS^CI0\n" +
                            "^FT589,1971^A@B,31,31,TT0003M_^FH\\^CI17^F8^FDLot^FS^CI0\n" +
                            "^FT758,1113^A@B,23,22,TT0003M_^FH\\^CI17^F8^FDExpiration Date^FS^CI0\n" +
                            "^FT758,1494^A@B,23,22,TT0003M_^FH\\^CI17^F8^FDBatch No^FS^CI0\n" +
                            "^FT758,1962^A@B,23,22,TT0003M_^FH\\^CI17^F8^FDPallet No^FS^CI0\n" +
                            "^FT762,1830^A@B,34,33,TT0003M_^FH\\^CI17^F8^FD"+item.PALLET_NUMBER+"^FS^CI0\n" +
                            "^FT213,1834^A@B,203,204,TT0003M_^FH\\^CI17^F8^FD"+item.ITEM_CODE+"^FS^CI0\n" +
                            "^FT334,1961^A@B,102,103,TT0003M_^FH\\^CI17^F8^FD"+itemDesc[0]+"^FS^CI0\n" +
                            "^FT714,1969^A@B,135,137,TT0003M_^FH\\^CI17^F8^FD"+item.LOT_NUMBER+"^FS^CI0\n" +
                            "^FT762,929^A@B,34,33,TT0003M_^FH\\^CI17^F8^FD"+item.EXPIRATION_DATE+"^FS^CI0\n" +
                            "^FT762,1362^A@B,34,33,TT0003M_^FH\\^CI17^F8^FD"+item.BATCH_NO+"^FS^CI0\n" +
                            "^FT435,1961^A@B,102,103,TT0003M_^FH\\^CI17^F8^FD"+itemDesc[1]+"^FS^CI0\n" +
                            "^FT536,1961^A@B,102,103,TT0003M_^FH\\^CI17^F8^FD"+itemDesc[2]+"^FS^CI0\n" +
                            "^BY686,686^FT742,45^BXI,49,200,0,0,1,~\n" +
                            "^FH\\^FD"+item.PALLET_NUMBER+"^FS^PQ1,0,1,Y^XZ";*/
                }else {
                    wholeZplLabel += Util.labelPalletBlt(item.PALLET_NUMBER);
                            /*"^XA" +
                            "^POI^PW350^MNN^LL141^LH0,0" + "\n" +
                            "^FO0,20^BY2" + "\n" + "^BCN,70,Y,N,N" + "\n" + "^FD" + item.PALLET_NUMBER + "^FS" + "\n" +
                            "^XZ";*/
                }
            }
        }

        return wholeZplLabel;
    }

    private void getPalletLabelList() {
        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
        param.put("orgId", Util.nullString(ORG_ID,""));
        param.put("palletNo", Util.nullString(edit_pallet.getText().toString(),""));
        param.put("batchNo", Util.nullString(edit_batch.getText().toString(),""));
        param.put("subinvCode", Util.nullString(edit_inv.getText().toString(),""));
        param.put("locatorCode", Util.nullString(edit_loc.getText().toString(),""));

        network = new Network(PalletLabelActivity.this, listener, "/api/label/labelPalletList");
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
            if(api.equals("/api/cmmPopup/palletList")){
                try{
                    JSONArray list = obj.getJSONArray("list");

                    if (list.length() == 1) {
                        JSONObject data = list.getJSONObject(0);

                        edit_pallet.setText(Util.nullString(data.getString("PALLET_NUMBER"), ""));
                    }else{
                        Intent intent = new Intent(getApplicationContext(), PopPallActivity.class);
                        intent.putExtra("ORG_ID", ORG_ID);
                        intent.putExtra("SUBINVENTORY_CODE", SUBINVENTORY_CODE);
                        intent.putExtra("PALLET_NUM", edit_pallet.getText().toString());
                        startActivityForResult(intent, Comm.TRANPALLET);
                    }

                } catch (JSONException e) {
                    Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                } catch (Exception e1) {
                    Log.v(Comm.LOG_TAG, "Exception Error ::"+e1.getMessage());
                }

            } else if(api.equals("/api/prod/batchNoList")){
                try {
                    JSONArray list = obj.getJSONArray("list");

                    if (list.length() == 1) {
                        JSONObject data = list.getJSONObject(0);

                        edit_batch.setText(Util.nullString(data.getString("BATCH_NO"), ""));
                    }else{
                        Intent intent = new Intent(getApplicationContext(), PopBatchActivity.class);
                        intent.putExtra("OU_ID", OU_ID);
                        intent.putExtra("ORG_ID", ORG_ID);
                        intent.putExtra("BATCH_NO", edit_batch.getText().toString());
                        startActivityForResult(intent, Comm.BATCH);
                    }

                } catch (JSONException e) {
                    Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                } catch (Exception e1) {
                    Log.v(Comm.LOG_TAG, "Exception Error ::"+e1.getMessage());
                }

            } else if(api.equals("/api/label/labelPalletList")){
                pltList = new ArrayList<>();
                ListView list_label = (ListView) findViewById(R.id.list_label);

                try {
                    JSONArray list = obj.getJSONArray("list");
                    printList = obj.getJSONArray("print");

                    checkList(list);
/*
                    String[] itemDesc = Util.subStrLen("NS ORGSPRFTOFUVP 16OZ",26);
                    String[] itemDesc1 = Util.subStrLen("NS Org. Tofu XFirm 2pk 15.5oz",26);
                    Log.v(Comm.LOG_TAG, "0-1 :: " + itemDesc[0]);
                    Log.v(Comm.LOG_TAG, "0-2 :: " + itemDesc[1]);
                    Log.v(Comm.LOG_TAG, "0-3 :: " + itemDesc[2]);
                    Log.v(Comm.LOG_TAG, "1-1 :: " + itemDesc1[0]);
                    Log.v(Comm.LOG_TAG, "1-2 :: " + itemDesc1[1]);
                    Log.v(Comm.LOG_TAG, "1-3 :: " + itemDesc1[2]);
*/

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject data = list.getJSONObject(i);

                        PalletLabelItem info = new PalletLabelItem(Util.nullString(data.getString("PALLET_ID"), "")
                                , Util.nullString(data.getString("PALLET_NUMBER"), "")
                                , Util.nullString(data.getString("ORGANIZATION_ID"), "")
                                , Util.nullString(data.getString("ORGANIZATION_CODE"), "")
                                , Util.nullString(data.getString("SUBINVENTORY_CODE"), "")
                                , Util.nullString(data.getString("INVENTORY_LOCATION_ID"), "")
                                , Util.nullString(data.getString("LOCATOR_CODE"), "")
                                , Util.nullString(data.getString("BATCH_ID"), "")
                                , Util.nullString(data.getString("BATCH_NO"), "")
                                , Util.nullString(data.getString("INVENTORY_ITEM_ID"), "")
                                , Util.nullString(data.getString("ITEM_CODE"), "")
                                , Util.nullString(data.getString("ITEM_DESC"), "")
                                , Util.nullString(data.getString("PRIMARY_UOM_CODE"), "")
                                , Util.nullString(data.getString("LOT_NUMBER"), "")
                                , Util.nullString(data.getString("EXPIRATION_DATE"), "")
                                ,"True"
                        );
                        pltList.add(info);
                    }
                    pltAdapter = new PalletLabelAdapter(PalletLabelActivity.this, pltList);
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
            }else if(api.equals("/api/label/scanLabelPalletList")){
                pltList = new ArrayList<>();
                ListView list_label = (ListView) findViewById(R.id.list_label);

                try {
                    JSONArray list = obj.getJSONArray("list");
                    printList = obj.getJSONArray("print");

                    checkList(list);

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject data = list.getJSONObject(i);
                        ORG_ID = Util.nullString(data.getString("ORGANIZATION_ID"), "");
                        edit_org.setText(Util.nullString(data.getString("ORGANIZATION_CODE"), ""));
                        edit_pallet.setText(Util.nullString(data.getString("PALLET_NUMBER"), ""));
                        btn_pallNo.setEnabled(true);
                        edit_pallet.setEnabled(true);
                        edit_pallet.setBackground(ContextCompat.getDrawable(PalletLabelActivity.this, R.drawable.bg_input));
                        btn_batchNo.setEnabled(true);
                        edit_batch.setEnabled(true);
                        edit_batch.setBackground(ContextCompat.getDrawable(PalletLabelActivity.this, R.drawable.bg_input));
                        btn_inv.setEnabled(true);
                        edit_inv.setEnabled(true);
                        edit_inv.setBackground(ContextCompat.getDrawable(PalletLabelActivity.this, R.drawable.bg_input));
                        if(!Util.nullString(data.getString("BATCH_NO"), "").equals("")){
                            edit_batch.setText(Util.nullString(data.getString("BATCH_NO"), ""));
                        }

                        if(!Util.nullString(data.getString("LOCATOR_CODE"), "").equals("")){
                            edit_inv.setText(Util.nullString(data.getString("SUBINVENTORY_CODE"), ""));
                            btn_loc.setEnabled(true);
                            edit_loc.setEnabled(true);
                            edit_loc.setText(Util.nullString(data.getString("LOCATOR_CODE"), ""));
                            edit_loc.setBackground(ContextCompat.getDrawable(PalletLabelActivity.this, R.drawable.bg_input));

                        }

                        PalletLabelItem info = new PalletLabelItem(Util.nullString(data.getString("PALLET_ID"), "")
                                , Util.nullString(data.getString("PALLET_NUMBER"), "")
                                , Util.nullString(data.getString("ORGANIZATION_ID"), "")
                                , Util.nullString(data.getString("ORGANIZATION_CODE"), "")
                                , Util.nullString(data.getString("SUBINVENTORY_CODE"), "")
                                , Util.nullString(data.getString("INVENTORY_LOCATION_ID"), "")
                                , Util.nullString(data.getString("LOCATOR_CODE"), "")
                                , Util.nullString(data.getString("BATCH_ID"), "")
                                , Util.nullString(data.getString("BATCH_NO"), "")
                                , Util.nullString(data.getString("INVENTORY_ITEM_ID"), "")
                                , Util.nullString(data.getString("ITEM_CODE"), "")
                                , Util.nullString(data.getString("ITEM_DESC"), "")
                                , Util.nullString(data.getString("PRIMARY_UOM_CODE"), "")
                                , Util.nullString(data.getString("LOT_NUMBER"), "")
                                , Util.nullString(data.getString("EXPIRATION_DATE"), "")
                                ,"True"
                        );
                        pltList.add(info);
                    }
                    pltAdapter = new PalletLabelAdapter(PalletLabelActivity.this, pltList);
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
            connection = new TcpConnection(SettingsHelper.getIp(PalletLabelActivity.this), Integer.valueOf(Comm.TCP_PORT_NUMBER));
        }else {
            connection = new BluetoothConnection(SettingsHelper.getBluetoothAddress(PalletLabelActivity.this));
        }
        Log.v(Comm.LOG_TAG, "CONNECT NT : " +SettingsHelper.getIp(PalletLabelActivity.this));
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
            AlertDialog.Builder builder = new AlertDialog.Builder(PalletLabelActivity.this)
                    .setTitle(getString(R.string.warning))
                    .setMessage(getString(R.string.printer_error_connected)+":0")
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
                AlertDialog.Builder builder = new AlertDialog.Builder(PalletLabelActivity.this)
                        .setTitle(getString(R.string.warning))
                        .setMessage(getString(R.string.printer_error_connected)+":1")
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
                AlertDialog.Builder builder = new AlertDialog.Builder(PalletLabelActivity.this)
                        .setTitle(getString(R.string.warning))
                        .setMessage(getString(R.string.printer_error_connected)+":2")
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
            //setStatus(getString(R.string.printer_disconnecting), Color.RED);
            if (connection != null) {
                connection.close();
            }
            //setStatus(getString(R.string.printer_disconnected), Color.RED);
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
                setStatus(getString(R.string.printer_ready), Color.RED);
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

                //for(int i=0; i < Comm.TCP_ADDR.length; i++){
                //    listItems.add(Comm.TCP_NAME[i]);
                //}

                mPariedDeviceCount = Comm.TCP_ADDR.length;

            }else {

                for (BluetoothDevice device : mDevices) {

                    listItems.add(device.getAddress());
                }
                //listItems.add(getString(R.string.cancel));  // 취소 항목 추가.
            }
            final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
            listItems.toArray(new CharSequence[listItems.size()]);

            // 초기 값
            if(tcpYn) {
                if(printList.length() > 0) {
                    JSONObject data = printList.getJSONObject(0);
                    SettingsHelper.saveIp(PalletLabelActivity.this, data.getString("IP_CODE"));
                }
            }else{
                if(listItems.size() > 0) {
                    SettingsHelper.saveBluetoothAddress(PalletLabelActivity.this, items[0].toString());
                }
            }


            builder.setSingleChoiceItems(items, 0,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    // TODO Auto-generated method stub
                    if(tcpYn) {
                        try{
                            //SettingsHelper.saveIp(PalletLabelActivity.this, Comm.TCP_ADDR[item]);

                            JSONObject data = printList.getJSONObject(item);
                            SettingsHelper.saveIp(PalletLabelActivity.this, data.getString("IP_CODE"));

                        } catch (JSONException e) {
                            Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                        } catch (Exception e1) {
                            Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
                        }
                    }else{
                        SettingsHelper.saveBluetoothAddress(PalletLabelActivity.this, items[item].toString());
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

