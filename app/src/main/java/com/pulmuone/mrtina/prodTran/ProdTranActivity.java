package com.pulmuone.mrtina.prodTran;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pulmuone.mrtina.R;
import com.pulmuone.mrtina.batchComp.BatchCompActivity;
import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.comm.HttpClient;
import com.pulmuone.mrtina.comm.MyEvent;
import com.pulmuone.mrtina.comm.Network;
import com.pulmuone.mrtina.comm.NetworkEvent;
import com.pulmuone.mrtina.comm.Scan;
import com.pulmuone.mrtina.intro.IntroActivity;
import com.pulmuone.mrtina.popupBatch.PopBatchActivity;
import com.pulmuone.mrtina.popupOrg.PopOrgActivity;
import com.pulmuone.mrtina.popupProdPall.PopProdPallActivity;
import com.pulmuone.mrtina.popupTranItem.PopTranItemActivity;
import com.pulmuone.mrtina.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;

public class ProdTranActivity extends Activity {

    private ProdTranAdapter prodTranAdapter;
    ArrayList<ProdTranItem> prodList = null;

    private ListView list_prod_tran;

    private LinearLayout layout_plus = null;
    private RelativeLayout layout_back = null;

    private Button btn_find = null;
    private LinearLayout btn_date = null;
    private LinearLayout btn_pallNo = null;
    private LinearLayout btn_batchNo = null;
    private LinearLayout btn_prod = null;
    private LinearLayout btn_org = null;
    private ImageButton btn_plus = null;

    RadioGroup radioGroup;
    RadioButton compY , compN;

    private TextView text_date = null;
    private EditText edit_batch = null;
    private EditText edit_prod = null;
    private EditText edit_pallet = null;
    private EditText edit_org = null;

    private int pYear, pMonth, pDay;

    SharedPreferences pref = null;
    private String S_USER_ID = null;
    private String ORG_ID = null;
    private String OU_ID = null;
    private String complete = null;
    private String batchId = null;

    private Scan Scan = null; //SCANNER CODE

    private Network network = null;

    private boolean findFlag = true;
    JSONArray printList = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_tran);

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);
        S_USER_ID = pref.getString("USER_ID", "");

        list_prod_tran = (ListView) findViewById(R.id.list_prod_tran);

        layout_back = (RelativeLayout) findViewById(R.id.layout_back);
        layout_plus = (LinearLayout) findViewById(R.id.layout_pallet);

        radioGroup = (RadioGroup) findViewById(R.id.rg_complete);
        compY = (RadioButton) findViewById(R.id.rb_yes);
        compN = (RadioButton) findViewById(R.id.rb_no);

        btn_plus = (ImageButton) findViewById(R.id.imb_plus_pallet);
        btn_find = (Button) findViewById(R.id.btn_find);
        btn_pallNo = (LinearLayout) findViewById(R.id.layout_pallet_no);
        btn_batchNo = (LinearLayout) findViewById(R.id.layout_batch_no);
        btn_prod = (LinearLayout) findViewById(R.id.layout_prod);
        btn_date = (LinearLayout) findViewById(R.id.layout_date);
        btn_org = (LinearLayout) findViewById(R.id.layout_org);

        text_date = (TextView) findViewById(R.id.txt_date);
        edit_batch = (EditText) findViewById(R.id.edit_batch_no);
        edit_prod = (EditText) findViewById(R.id.edit_prod);
        edit_pallet = (EditText) findViewById(R.id.edit_pall_no);
        edit_org = (EditText) findViewById(R.id.edit_org);

        edit_pallet.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edit_org.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        edit_pallet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_pallet.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_prod.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_prod.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_batch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_batch.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_org.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    edit_org.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });

        edit_pallet.setOnClickListener(onClickListener);
        edit_prod.setOnClickListener(onClickListener);
        edit_batch.setOnClickListener(onClickListener);
        edit_org.setOnClickListener(onClickListener);

        layout_back.setOnClickListener(onClickListener);
        btn_find.setOnClickListener(onClickListener);
        btn_pallNo.setOnClickListener(onClickListener);
        btn_batchNo.setOnClickListener(onClickListener);
        btn_prod.setOnClickListener(onClickListener);
        btn_date.setOnClickListener(onClickListener);
        btn_plus.setOnClickListener(onClickListener);
        btn_org.setOnClickListener(onClickListener);
        text_date.setOnClickListener(onClickListener);

        radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        Calendar c = Calendar.getInstance();
        pYear = c.get(Calendar.YEAR);
        pMonth = c.get(Calendar.MONTH);
        pDay = c.get(Calendar.DAY_OF_MONTH);
        setDateFormat(pYear, pMonth, pDay);

        compN.setChecked(true);

        btn_pallNo.setClickable(false);
        btn_batchNo.setClickable(false);
        btn_prod.setClickable(false);

        edit_batch.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
        edit_pallet.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
        edit_prod.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_ds));
        edit_batch.setEnabled(false);
        edit_pallet.setEnabled(false);
        edit_prod.setEnabled(false);


// [[ SCANNER CODE
        if(Comm.isLibraryInstalled(this,"com.symbol.emdk")) {
            this.Scan = new Scan(this);
            Scan.setEvent(myListener);
        }
// ]] SCANNER CODE
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Comm.BATCH) {
                edit_batch.setText(Util.nullString(data.getStringExtra("BATCH_NO"), ""));
                batchId = Util.nullString(data.getStringExtra("BATCH_ID"), "");

            } else if (requestCode == Comm.TRANITEM) {
                edit_prod.setText(Util.nullString(data.getStringExtra("ITEM_CODE"), ""));


            } else if (requestCode == Comm.TRANPALLET) {
                edit_pallet.setText(Util.nullString(data.getStringExtra("PALLET_NUMBER"), ""));
                text_date.setText(Util.nullString(data.getStringExtra("PLAN_START_DATE"), ""));
                edit_batch.setText(Util.nullString(data.getStringExtra("BATCH_NO"), ""));
                batchId = Util.nullString(data.getStringExtra("BATCH_ID"), "");

            } else if (requestCode == Comm.DETAIL) {
                findFlag = false;

                getItem();

            } else if (requestCode == Comm.ORG) {
                edit_org.setText(Util.nullString(data.getStringExtra("ORG_CODE"), ""));
                OU_ID = Util.nullString(data.getStringExtra("OU_ID"), "");
                ORG_ID = Util.nullString(data.getStringExtra("ORG_ID"), "");

                Log.v(Comm.LOG_TAG, "OU_ID: " + OU_ID);
                Log.v(Comm.LOG_TAG, "ORG_ID: " + ORG_ID);

                btn_pallNo.setClickable(true);
                btn_batchNo.setClickable(true);
                btn_prod.setClickable(true);

                edit_batch.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input));
                edit_pallet.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input));
                edit_prod.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input));
                edit_batch.setEnabled(true);
                edit_pallet.setEnabled(true);
                edit_prod.setEnabled(true);
            }
        }
    }

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i == R.id.rb_yes){
                complete = "RETURN";

            } else if(i == R.id.rb_no){
                complete = "COMPLETION";
            }
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {

        private Network network = null;

        @Override
        public void onClick(View arg0) {

            if (arg0.equals(btn_find)) {

                findFlag = true;

                if(edit_batch.getText().toString().replace(" ", "").equals("") &&
                        edit_prod.getText().toString().replace(" ", "").equals("") &&
                        edit_pallet.getText().toString().replace(" ", "").equals("")){

                    AlertDialog.Builder builder = new AlertDialog.Builder(ProdTranActivity.this)
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.alert_batch_pallet_no))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
                }else{
                    getItem();

                }

            } else if (arg0.equals(layout_back)) {
                finish();

            } else if (arg0.equals(btn_plus)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ProdTranActivity.this)
                        .setTitle(getString(R.string.decision))
                        .setMessage(getString(R.string.alert_pallet_add))
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

                                param.put("ouId", Util.nullString(OU_ID, ""));
                                param.put("orgId", Util.nullString(ORG_ID, ""));
                                param.put("userNo", Util.nullString(S_USER_ID, ""));
                                param.put("searchPallet", Util.nullString(edit_pallet.getText().toString(), ""));
                                param.put("batchNo", Util.nullString(edit_batch.getText().toString(), ""));
                                param.put("batchId", Util.nullString(batchId, ""));

                                network = new Network(ProdTranActivity.this, listener, "/api/prod/getPallet");
                                network.execute(param);
                            }
                        });
                builder.show();

            } else if (arg0.equals(btn_date)) {
                DatePickerDialog datepicker = new DatePickerDialog(ProdTranActivity.this, mPickSetListener, pYear, pMonth, pDay);
                datepicker.getDatePicker().setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                datepicker.show();

            } else if (arg0.equals(text_date)) {
                DatePickerDialog datepicker2 = new DatePickerDialog(ProdTranActivity.this, mPickSetListener, pYear, pMonth, pDay);
                datepicker2.getDatePicker().setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                datepicker2.show();

            } else if (arg0.equals(edit_batch)) {
                edit_batch.setSelection(edit_batch.length());

            } else if (arg0.equals(edit_pallet)) {
                edit_pallet.setSelection(edit_pallet.length());

            } else if (arg0.equals(edit_prod)) {
                edit_prod.setSelection(edit_prod.length());

            } else if (arg0.equals(edit_org)){
                edit_org.setSelection(edit_org.length());

            } else if (arg0.equals(btn_batchNo)) {
                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

                param.put("ouId", Util.nullString(OU_ID, ""));
                param.put("orgId", Util.nullString(ORG_ID, ""));
                param.put("userNo", Util.nullString(S_USER_ID, ""));
                param.put("searchBatch", Util.nullString(edit_batch.getText().toString(), ""));

                network = new Network(ProdTranActivity.this, listener, "/api/prod/batchNoList");
                network.execute(param);


            } else if (arg0.equals(btn_prod)) {
                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

                param.put("orgId", Util.nullString(ORG_ID, ""));
                param.put("searchCd", Util.nullString(edit_prod.getText().toString(), ""));

                network = new Network(ProdTranActivity.this, listener, "/api/tran/tranItemList");
                network.execute(param);

            } else if (arg0.equals(btn_pallNo)) {
                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

                param.put("ouId", Util.nullString(OU_ID, ""));
                param.put("orgId", Util.nullString(ORG_ID, ""));
                param.put("userNo", Util.nullString(S_USER_ID, ""));
                param.put("searchPallet", Util.nullString(edit_pallet.getText().toString(), ""));
                param.put("searchItem", "");

                network = new Network(ProdTranActivity.this, listener, "/api/prod/batchPalletList");
                network.execute(param);

            } else if(arg0.equals(btn_org)){
                Intent intent = new Intent(getApplicationContext(), PopOrgActivity.class);
                startActivityForResult(intent, Comm.ORG);
            }
        }
    };

    private DatePickerDialog.OnDateSetListener mPickSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            pYear = year;
            pMonth = monthOfYear;
            pDay = dayOfMonth;

            DecimalFormat mmddFormat = new DecimalFormat("00");
            DecimalFormat yyyyFormat = new DecimalFormat("0000");
            String result = mmddFormat.format(pMonth + 1) + "-" + mmddFormat.format(pDay) + "-" + yyyyFormat.format(pYear);
            text_date.setText(result);
        }
    };

    private void setDateFormat(int pYear, int pMonth, int pDay) {
        DecimalFormat mmddFormat = new DecimalFormat("00");
        DecimalFormat yyyyFormat = new DecimalFormat("0000");
        String result = mmddFormat.format(pMonth + 1) + "-" + mmddFormat.format(pDay) + "-" + yyyyFormat.format(pYear);

        text_date.setText(result);
    };

    // [[ SCANNER CODE
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Scan != null) {
            Scan.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Scan != null) {
            Scan.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Scan != null) {
            Scan.onPause();
        }
    }

    MyEvent myListener = new MyEvent() {

        @Override
        public void onEvent(String data) {

            Log.v(Comm.LOG_TAG, "MyEvent:"+data);

            //if(!Util.isNull(OU_ID)) {
                LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();
                param.put("ouId", Util.nullString(OU_ID, ""));
                param.put("orgId", Util.nullString(ORG_ID, ""));
                param.put("userNo", Util.nullString(S_USER_ID, ""));
                param.put("searchPallet", data);

                new scanPalletListTask().execute(param);
            /*}else{
                ProdTranActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProdTranActivity.this)
                                .setTitle(getString(R.string.note))
                                .setMessage(getString(R.string.alert_set_org))
                                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        builder.show();
                    }
                });
            }*/
        }
    };
// ]] SCANNER CODE


    private class scanPalletListTask extends AsyncTask<LinkedHashMap<String, String>, Void, String>{
        @Override
        protected String doInBackground(LinkedHashMap<String, String>... params) {
            HttpClient.Builder http = new HttpClient.Builder("POST", Comm.URL+"/api/prod/scanPalletList");
            http.addAllParameters(params[0]);
            HttpClient post = http.create();
            post.request();

            int statusCode = post.getHttpStatusCode();
            Log.v(Comm.LOG_TAG, "statusCode: "+ statusCode);
            String body = post.getBody();
            Log.v(Comm.LOG_TAG, "body: "+ body);
            return body;
        }

        @Override
        protected void onPostExecute(String result){
            try {
                JSONObject obj = new JSONObject(result);

                JSONArray list = obj.getJSONArray("list");

                if(list.length() != 0){
                    JSONObject data = list.getJSONObject(0);
                    edit_pallet.setText(Util.nullString(data.getString("PALLET_NUMBER"), ""));
                    text_date.setText(Util.nullString(data.getString("PLAN_START_DATE"), ""));
                    edit_batch.setText(Util.nullString(data.getString("BATCH_NO"), ""));
                    batchId = Util.nullString(data.getString("BATCH_ID"), "");

                    OU_ID = Util.nullString(data.getString("OU_ID"), "");
                    ORG_ID = Util.nullString(data.getString("ORGANIZATION_ID"), "");
                    edit_org.setText(Util.nullString(data.getString("ORGANIZATION_CODE"), ""));

                    btn_pallNo.setClickable(true);
                    btn_batchNo.setClickable(true);
                    btn_prod.setClickable(true);

                    edit_batch.setBackground(ContextCompat.getDrawable(ProdTranActivity.this, R.drawable.bg_input));
                    edit_pallet.setBackground(ContextCompat.getDrawable(ProdTranActivity.this, R.drawable.bg_input));
                    edit_prod.setBackground(ContextCompat.getDrawable(ProdTranActivity.this, R.drawable.bg_input));
                    edit_batch.setEnabled(true);
                    edit_pallet.setEnabled(true);
                    edit_prod.setEnabled(true);

                    getItem();

                } else{
                    Intent intent = new Intent(getApplicationContext(), PopProdPallActivity.class);
                    intent.putExtra("OU_ID", OU_ID);
                    intent.putExtra("ORG_ID", ORG_ID);
                    intent.putExtra("PALL_NO", edit_pallet.getText().toString());
                    startActivityForResult(intent, Comm.TRANPALLET);

                }

            } catch (JSONException e) {
                Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
            } catch (Exception e1) {
                Log.v(Comm.LOG_TAG, "Exception Error ::" + e1.getMessage());
            }
        }
    };

    NetworkEvent listener = new NetworkEvent() {
        @Override
        public void doInBackground(LinkedHashMap<String, String>... params) {

        }

        @Override
        public void onPostExecute(JSONObject obj, String api) {
            if(api.equals("/api/prod/batchNoList")){

                try {
                    JSONArray list = obj.getJSONArray("list");

                    if (list.length() == 1) {
                        JSONObject data = list.getJSONObject(0);

                        edit_batch.setText(Util.nullString(data.getString("BATCH_NO"), ""));
                        batchId = Util.nullString(data.getString("BATCH_ID"), "");

                    }else{
                        Intent intent = new Intent(getApplicationContext(), PopBatchActivity.class);
                        startActivityForResult(intent, Comm.BATCH);
                    }

                } catch (JSONException e) {
                    Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                } catch (Exception e1) {
                    Log.v(Comm.LOG_TAG, "Exception Error ::"+e1.getMessage());
                }

            } else if(api.equals("/api/tran/tranItemList")){

                try{
                    JSONArray list = obj.getJSONArray("list");

                    if (list.length() == 1) {
                        JSONObject data = list.getJSONObject(0);

                        edit_prod.setText(Util.nullString(data.getString("ITEM_CODE"), ""));
                    }else{
                        Intent intent = new Intent(getApplicationContext(), PopTranItemActivity.class);
                        intent.putExtra("ITEM_NO", edit_prod.getText().toString());
                        startActivityForResult(intent, Comm.TRANITEM);
                    }

                } catch (JSONException e) {
                    Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                } catch (Exception e1) {
                    Log.v(Comm.LOG_TAG, "Exception Error ::"+e1.getMessage());
                }

            } else if(api.equals("/api/prod/batchPalletList")){

                try{
                    JSONArray list = obj.getJSONArray("list");

                    if (list.length() == 1) {
                        JSONObject data = list.getJSONObject(0);

                        edit_pallet.setText(Util.nullString(data.getString("PALLET_NUMBER"), ""));
                        edit_batch.setText(Util.nullString(data.getString("BATCH_NO"), ""));
                        batchId = Util.nullString(data.getString("BATCH_ID"), "");

                    } else{
                        Intent intent = new Intent(getApplicationContext(), PopProdPallActivity.class);
                        intent.putExtra("OU_ID", OU_ID);
                        intent.putExtra("ORG_ID", ORG_ID);
                        intent.putExtra("PALL_NO", edit_pallet.getText().toString());
                        startActivityForResult(intent, Comm.TRANPALLET);
                    }

                } catch (JSONException e) {
                    Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                } catch (Exception e1) {
                    Log.v(Comm.LOG_TAG, "Exception Error ::"+e1.getMessage());
                }

            } else if(api.equals("/api/prod/getPallet")){
                try{
                    JSONArray list = obj.getJSONArray("list");
                    printList = obj.getJSONArray("print");
                    JSONObject data = list.getJSONObject(0);

                    Intent intent = new Intent(ProdTranActivity.this, BatchCompActivity.class);

                    intent.putExtra("ORGANIZATION_CODE", Util.nullString(data.getString("ORGANIZATION_CODE"), ""));
                    intent.putExtra("PALLET_ID", Util.nullString(data.getString("PALLET_ID"),""));
                    intent.putExtra("PALLET_NUMBER", Util.nullString(data.getString("PALLET_NUMBER"),""));
                    intent.putExtra("BATCH_ID", Util.nullString(data.getString("BATCH_ID"),""));
                    intent.putExtra("BATCH_NO", Util.nullString(data.getString("BATCH_NO"),""));
                    intent.putExtra("ACTUAL_START_DATE", Util.nullString(data.getString("ACTUAL_START_DATE"),""));
                    intent.putExtra("INVENTORY_ITEM_ID", Util.nullString(data.getString("INVENTORY_ITEM_ID"),""));
                    intent.putExtra("ITEM_CODE", Util.nullString(data.getString("ITEM_CODE"),""));
                    intent.putExtra("ITEM_DESC", Util.nullString(data.getString("ITEM_DESC"),""));
                    intent.putExtra("PL_PLAN_QTY", Util.nullString(data.getString("PL_PLAN_QTY"),""));
                    intent.putExtra("PL_SKU_UOM", Util.nullString(data.getString("PL_SKU_UOM"),""));
                    intent.putExtra("PL_CON_RATE", Util.nullString(data.getString("PL_CON_RATE"),""));
                    intent.putExtra("PLAN_QTY", Util.nullString(data.getString("PLAN_QTY"),""));
                    intent.putExtra("ACTUAL_QTY", Util.nullString(data.getString("ACTUAL_QTY"),""));
                    intent.putExtra("PRIMARY_UOM_CODE", Util.nullString(data.getString("PRIMARY_UOM_CODE"),""));
                    intent.putExtra("RECIPE_NO", Util.nullString(data.getString("RECIPE_NO"),""));
                    intent.putExtra("RECIPE_VERSION", Util.nullString(data.getString("RECIPE_VERSION"),""));
                    intent.putExtra("ROUTING_ID", Util.nullString(data.getString("ROUTING_ID"),""));
                    intent.putExtra("ROUTING_NO", Util.nullString(data.getString("ROUTING_NO"),""));
                    intent.putExtra("ROUTING_VERS", Util.nullString(data.getString("ROUTING_VERS"),""));
                    intent.putExtra("FORMULA_ID", Util.nullString(data.getString("FORMULA_ID"),""));
                    intent.putExtra("FORMULA_NO", Util.nullString(data.getString("FORMULA_NO"),""));
                    intent.putExtra("FORMULA_VERS", Util.nullString(data.getString("FORMULA_VERS"),""));
                    intent.putExtra("MATERIAL_DETAIL_ID", Util.nullString(data.getString("MATERIAL_DETAIL_ID"),""));
                    intent.putExtra("TRANSACTION_ID", Util.nullString(data.getString("TRANSACTION_ID"),""));
                    intent.putExtra("SUBINVENTORY_CODE", Util.nullString(data.getString("SUBINVENTORY_CODE"),""));
                    intent.putExtra("PALLET_CONTROL", Util.nullString(data.getString("PALLET_CONTROL"),""));
                    intent.putExtra("LOCATOR_CONTROL", Util.nullString(data.getString("LOCATOR_CONTROL"),""));
                    intent.putExtra("LOT_CONTROL_CODE", Util.nullString(data.getString("LOT_CONTROL_CODE"),""));
                    intent.putExtra("INVENTORY_LOCATION_ID", Util.nullString(data.getString("INVENTORY_LOCATION_ID"),""));
                    intent.putExtra("LOCATOR_CODE", Util.nullString(data.getString("LOCATOR_CODE"),""));
                    intent.putExtra("LOCATOR_NAME", Util.nullString(data.getString("LOCATOR_NAME"),""));
                    intent.putExtra("LOT_NUMBER", Util.nullString(data.getString("LOT_NUMBER"),""));
                    intent.putExtra("EXPIRATION_DATE", Util.nullString(data.getString("EXPIRATION_DATE"),""));
                    intent.putExtra("REASON_DESC", Util.nullString(data.getString("REASON_DESC"),""));
                    intent.putExtra("REASON_ID", Util.nullString(data.getString("REASON_ID"),""));
                    intent.putExtra("PL_TXN_QTY", Util.nullString(data.getString("PL_TXN_QTY"),""));
                    intent.putExtra("PRIMARY_TXN_QTY", Util.nullString(data.getString("PRIMARY_TXN_QTY"),""));
                    intent.putExtra("BATCH_PALLET_SEQ", Util.nullString(data.getString("BATCH_PALLET_SEQ"),""));
                    intent.putExtra("DETAIL_LINE", Util.nullString(data.getString("DETAIL_LINE"),""));
                    intent.putExtra("DUE_DATE", Util.nullString(data.getString("DUE_DATE"),""));
                    intent.putExtra("TRANSAC_TYPE", complete);
                    intent.putExtra("PRINT_TYPE", "Y");
                    intent.putExtra("OU_ID", OU_ID);
                    intent.putExtra("ORG_ID", ORG_ID);
                    intent.putExtra("PER_PALLET", Util.nullString(data.getString("PER_PALLET"),""));
                    intent.putExtra("PRINT_LIST", printList.toString());

                    Log.v(Comm.LOG_TAG, "printList.toString() :: " + printList.toString());

                    startActivity(intent);

                } catch (JSONException e) {
                    Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                } catch (Exception e1) {
                    Log.v(Comm.LOG_TAG, "Exception Error ::"+e1.getMessage());
                }

            } else if(api.equals("/api/prod/batchItemList")) {
                try{
                    JSONArray list = obj.getJSONArray("list");
                    printList = obj.getJSONArray("print");

                    list_prod_tran = (ListView)findViewById(R.id.list_prod_tran);
                    prodList = new ArrayList<>();

                    if(compY.isChecked()){
                        layout_plus.setVisibility(View.GONE);

                        if(list.length() == 0){
                            alertNoItem();
                        }

                    }else if(compN.isChecked()){
                        if(findFlag){
                            if(list.length() == 0 && !edit_batch.getText().toString().isEmpty()){
                                layout_plus.setVisibility(View.VISIBLE);
                                alertNoItem();
                            }else if(list.length() == 0 && edit_batch.getText().toString().isEmpty()) {
                                layout_plus.setVisibility(View.GONE);
                                alertNoItem();
                            }else{
                                layout_plus.setVisibility(View.GONE);
                            }
                        }else{
                            if(list.length() == 0 && !edit_batch.getText().toString().isEmpty()){
                                layout_plus.setVisibility(View.VISIBLE);
                            }else if(list.length() == 0 && edit_batch.getText().toString().isEmpty()) {
                                layout_plus.setVisibility(View.GONE);
                            }else{
                                layout_plus.setVisibility(View.GONE);
                            }
                        }
                    }

                    for(int i=0; i<list.length(); i++){
                        JSONObject data = list.getJSONObject(i);

                        ProdTranItem info = new ProdTranItem(Util.nullString(data.getString("ORGANIZATION_CODE"),"")
                                , Util.nullString(data.getString("PALLET_ID"),"")
                                , Util.nullString(data.getString("PALLET_NUMBER"),"")
                                , Util.nullString(data.getString("BATCH_ID"),"")
                                , Util.nullString(data.getString("BATCH_NO"),"")
                                , Util.nullString(data.getString("ACTUAL_START_DATE"),"")
                                , Util.nullString(data.getString("INVENTORY_ITEM_ID"),"")
                                , Util.nullString(data.getString("ITEM_CODE"),"")
                                , Util.nullString(data.getString("ITEM_DESC"),"")
                                , Util.nullString(data.getString("PL_PLAN_QTY"),"")
                                , Util.nullString(data.getString("PL_SKU_UOM"),"")
                                , Util.nullString(data.getString("PL_CON_RATE"),"")
                                , Util.nullString(data.getString("PLAN_QTY"),"0")
                                , Util.nullString(data.getString("PL_ACTUAL_QTY"),"0")
                                , Util.nullString(data.getString("PALLET_ACTUAL_QTY"),"0")
                                , Util.nullString(data.getString("PRIMARY_UOM_CODE"),"")
                                , Util.nullString(data.getString("RECIPE_NO"),"")
                                , Util.nullString(data.getString("RECIPE_VERSION"),"")
                                , Util.nullString(data.getString("ROUTING_ID"),"")
                                , Util.nullString(data.getString("ROUTING_NO"),"")
                                , Util.nullString(data.getString("ROUTING_VERS"),"")
                                , Util.nullString(data.getString("FORMULA_ID"),"")
                                , Util.nullString(data.getString("FORMULA_NO"),"")
                                , Util.nullString(data.getString("FORMULA_VERS"),"")
                                , Util.nullString(data.getString("MATERIAL_DETAIL_ID"),"")
                                , Util.nullString(data.getString("TRANSACTION_ID"),"")
                                , Util.nullString(data.getString("SUBINVENTORY_CODE"),"")
                                , Util.nullString(data.getString("PALLET_CONTROL"),"")
                                , Util.nullString(data.getString("LOCATOR_CONTROL"),"")
                                , Util.nullString(data.getString("LOT_CONTROL_CODE"),"")
                                , Util.nullString(data.getString("INVENTORY_LOCATION_ID"),"")
                                , Util.nullString(data.getString("LOCATOR_CODE"),"")
                                , Util.nullString(data.getString("LOCATOR_NAME"),"")
                                , Util.nullString(data.getString("LOT_NUMBER"),"")
                                , Util.nullString(data.getString("EXPIRATION_DATE"),"")
                                , Util.nullString(data.getString("REASON_DESC"),"")
                                , Util.nullString(data.getString("REASON_ID"),"")
                                , Util.nullString(data.getString("PL_TXN_QTY"),"")
                                , Util.nullString(data.getString("PRIMARY_TXN_QTY"),"")
                                , Util.nullString(data.getString("BATCH_PALLET_SEQ"),"")
                                , Util.nullString(data.getString("DETAIL_LINE"),"")
                                , Util.nullString(data.getString("PER_PALLET"),"")
                                , Util.nullString(data.getString("DUE_DATE"),"")
                        );
                        prodList.add(info);
                    }

                    prodTranAdapter = new ProdTranAdapter(ProdTranActivity.this, prodList);
                    list_prod_tran.setAdapter(prodTranAdapter);
                    list_prod_tran.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            Log.v(Comm.LOG_TAG, "list_prod_tran position :: " + position);
                            Intent intent = new Intent(ProdTranActivity.this, BatchCompActivity.class);

                            ProdTranItem item = (ProdTranItem) prodTranAdapter.getItem(position);

                            intent.putExtra("ORGANIZATION_CODE", item.ORGANIZATION_CODE);
                            intent.putExtra("PALLET_ID", item.PALLET_ID);
                            intent.putExtra("PALLET_NUMBER", item.PALLET_NUMBER);
                            intent.putExtra("BATCH_ID", item.BATCH_ID);
                            intent.putExtra("BATCH_NO", item.BATCH_NO);
                            intent.putExtra("ACTUAL_START_DATE", item.ACTUAL_START_DATE);
                            intent.putExtra("INVENTORY_ITEM_ID", item.INVENTORY_ITEM_ID);
                            intent.putExtra("ITEM_CODE", item.ITEM_CODE);
                            intent.putExtra("ITEM_DESC", item.ITEM_DESC);
                            intent.putExtra("PL_PLAN_QTY", item.PL_PLAN_QTY);
                            intent.putExtra("PL_SKU_UOM", item.PL_SKU_UOM);
                            intent.putExtra("PL_CON_RATE", item.PL_CON_RATE);
                            intent.putExtra("PLAN_QTY", item.PLAN_QTY);
                            intent.putExtra("PL_ACTUAL_QTY", item.PL_ACTUAL_QTY);
                            intent.putExtra("COMP_QTY", item.COMP_QTY);
                            intent.putExtra("PRIMARY_UOM_CODE", item.PRIMARY_UOM_CODE);
                            intent.putExtra("RECIPE_NO", item.RECIPE_NO);
                            intent.putExtra("RECIPE_VERSION", item.RECIPE_VERSION);
                            intent.putExtra("ROUTING_ID", item.ROUTING_ID);
                            intent.putExtra("ROUTING_NO", item.ROUTING_NO);
                            intent.putExtra("ROUTING_VERS", item.ROUTING_VERS);
                            intent.putExtra("FORMULA_ID", item.FORMULA_ID);
                            intent.putExtra("FORMULA_NO", item.FORMULA_NO);
                            intent.putExtra("FORMULA_VERS", item.FORMULA_VERS);
                            intent.putExtra("MATERIAL_DETAIL_ID", item.MATERIAL_DETAIL_ID);
                            intent.putExtra("TRANSACTION_ID", item.TRANSACTION_ID);
                            intent.putExtra("SUBINVENTORY_CODE", item.SUBINVENTORY_CODE);
                            intent.putExtra("PALLET_CONTROL", item.PALLET_CONTROL);
                            intent.putExtra("LOCATOR_CONTROL", item.LOCATOR_CONTROL);
                            intent.putExtra("LOT_CONTROL_CODE", item.LOT_CONTROL_CODE);
                            intent.putExtra("INVENTORY_LOCATION_ID", item.INVENTORY_LOCATION_ID);
                            intent.putExtra("LOCATOR_CODE", item.LOCATOR_CODE);
                            intent.putExtra("LOCATOR_NAME", item.LOCATOR_NAME);
                            intent.putExtra("LOT_NUMBER", item.LOT_NUMBER);
                            intent.putExtra("EXPIRATION_DATE", item.EXPIRATION_DATE);
                            intent.putExtra("REASON_DESC", item.REASON_DESC);
                            intent.putExtra("REASON_ID", item.REASON_ID);
                            intent.putExtra("PL_TXN_QTY", item.PL_TXN_QTY);
                            intent.putExtra("PRIMARY_TXN_QTY", item.PRIMARY_TXN_QTY);
                            intent.putExtra("BATCH_PALLET_SEQ", item.BATCH_PALLET_SEQ);
                            intent.putExtra("DETAIL_LINE", item.DETAIL_LINE);
                            intent.putExtra("DUE_DATE", item.DUE_DATE);
                            intent.putExtra("TRANSAC_TYPE", complete);
                            intent.putExtra("PRINT_TYPE", "N");
                            intent.putExtra("OU_ID", OU_ID);
                            intent.putExtra("ORG_ID", ORG_ID);
                            intent.putExtra("PER_PALLET", item.PER_PALLET);
                            intent.putExtra("PRINT_LIST", printList.toString());
                            Log.v(Comm.LOG_TAG, "item.DUE_DATE: "+ item.DUE_DATE);
                            startActivityForResult(intent, Comm.DETAIL);

                        }
                    });

                    // 20200902 조회 결과가 하나인 경우 상세화면으로 바로 이동.
                    if(list.length() == 1 && complete == "COMPLETION"){
                        Intent intent = new Intent(ProdTranActivity.this, BatchCompActivity.class);

                        ProdTranItem item = (ProdTranItem) prodTranAdapter.getItem(0);

                        intent.putExtra("ORGANIZATION_CODE", item.ORGANIZATION_CODE);
                        intent.putExtra("PALLET_ID", item.PALLET_ID);
                        intent.putExtra("PALLET_NUMBER", item.PALLET_NUMBER);
                        intent.putExtra("BATCH_ID", item.BATCH_ID);
                        intent.putExtra("BATCH_NO", item.BATCH_NO);
                        intent.putExtra("ACTUAL_START_DATE", item.ACTUAL_START_DATE);
                        intent.putExtra("INVENTORY_ITEM_ID", item.INVENTORY_ITEM_ID);
                        intent.putExtra("ITEM_CODE", item.ITEM_CODE);
                        intent.putExtra("ITEM_DESC", item.ITEM_DESC);
                        intent.putExtra("PL_PLAN_QTY", item.PL_PLAN_QTY);
                        intent.putExtra("PL_SKU_UOM", item.PL_SKU_UOM);
                        intent.putExtra("PL_CON_RATE", item.PL_CON_RATE);
                        intent.putExtra("PLAN_QTY", item.PLAN_QTY);
                        intent.putExtra("PL_ACTUAL_QTY", item.PL_ACTUAL_QTY);
                        intent.putExtra("COMP_QTY", item.COMP_QTY);
                        intent.putExtra("PRIMARY_UOM_CODE", item.PRIMARY_UOM_CODE);
                        intent.putExtra("RECIPE_NO", item.RECIPE_NO);
                        intent.putExtra("RECIPE_VERSION", item.RECIPE_VERSION);
                        intent.putExtra("ROUTING_ID", item.ROUTING_ID);
                        intent.putExtra("ROUTING_NO", item.ROUTING_NO);
                        intent.putExtra("ROUTING_VERS", item.ROUTING_VERS);
                        intent.putExtra("FORMULA_ID", item.FORMULA_ID);
                        intent.putExtra("FORMULA_NO", item.FORMULA_NO);
                        intent.putExtra("FORMULA_VERS", item.FORMULA_VERS);
                        intent.putExtra("MATERIAL_DETAIL_ID", item.MATERIAL_DETAIL_ID);
                        intent.putExtra("TRANSACTION_ID", item.TRANSACTION_ID);
                        intent.putExtra("SUBINVENTORY_CODE", item.SUBINVENTORY_CODE);
                        intent.putExtra("PALLET_CONTROL", item.PALLET_CONTROL);
                        intent.putExtra("LOCATOR_CONTROL", item.LOCATOR_CONTROL);
                        intent.putExtra("LOT_CONTROL_CODE", item.LOT_CONTROL_CODE);
                        intent.putExtra("INVENTORY_LOCATION_ID", item.INVENTORY_LOCATION_ID);
                        intent.putExtra("LOCATOR_CODE", item.LOCATOR_CODE);
                        intent.putExtra("LOCATOR_NAME", item.LOCATOR_NAME);
                        intent.putExtra("LOT_NUMBER", item.LOT_NUMBER);
                        intent.putExtra("EXPIRATION_DATE", item.EXPIRATION_DATE);
                        intent.putExtra("REASON_DESC", item.REASON_DESC);
                        intent.putExtra("REASON_ID", item.REASON_ID);
                        intent.putExtra("PL_TXN_QTY", item.PL_TXN_QTY);
                        intent.putExtra("PRIMARY_TXN_QTY", item.PRIMARY_TXN_QTY);
                        intent.putExtra("BATCH_PALLET_SEQ", item.BATCH_PALLET_SEQ);
                        intent.putExtra("DETAIL_LINE", item.DETAIL_LINE);
                        intent.putExtra("DUE_DATE", item.DUE_DATE);
                        intent.putExtra("TRANSAC_TYPE", complete);
                        intent.putExtra("PRINT_TYPE", "N");
                        intent.putExtra("OU_ID", OU_ID);
                        intent.putExtra("ORG_ID", ORG_ID);
                        intent.putExtra("PER_PALLET", item.PER_PALLET);
                        intent.putExtra("PRINT_LIST", printList.toString());

                        startActivityForResult(intent, Comm.DETAIL);
                    }

                } catch (JSONException e) {
                    Log.v(Comm.LOG_TAG, "JSon Error :: " + e.getMessage());
                } catch (Exception e1) {
                    Log.v(Comm.LOG_TAG, "Exception Error ::"+e1.getMessage());
                }
            }

        }
    };

    public void alertNoItem(){
        if(findFlag){
            AlertDialog.Builder builder = new AlertDialog.Builder(ProdTranActivity.this);
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

    public void getItem(){
        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

        param.put("ouId", Util.nullString(OU_ID, ""));
        param.put("orgId", Util.nullString(ORG_ID, ""));
        param.put("userNo", Util.nullString(S_USER_ID, ""));
        param.put("palletNumber", Util.nullString(edit_pallet.getText().toString(), ""));
        param.put("itemCode", Util.nullString(edit_prod.getText().toString(), ""));
        param.put("batchNo", Util.nullString(edit_batch.getText().toString(), ""));
        param.put("transactionType", Util.nullString(complete, ""));

        network = new Network(ProdTranActivity.this, listener, "/api/prod/batchItemList");
        network.execute(param);
    }
}