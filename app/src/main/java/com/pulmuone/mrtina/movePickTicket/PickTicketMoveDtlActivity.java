package com.pulmuone.mrtina.movePickTicket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pulmuone.mrtina.R;
import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.comm.Network;
import com.pulmuone.mrtina.comm.NetworkEvent;
import com.pulmuone.mrtina.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;

public class PickTicketMoveDtlActivity extends Activity {

    private PickTicketMoveDtlAdapter moveAdapter;

    SharedPreferences pref = null;
    private String S_USER_ID = null;

    private ListView listView;

    private RelativeLayout btn_close = null;
    private Network network = null;

    ArrayList<PickTicketMoveDtlItem> moveArr = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_ticket_dtl);

        pref = getSharedPreferences("mrtina", Activity.MODE_PRIVATE);
        S_USER_ID = pref.getString("USER_ID", "");

        btn_close = (RelativeLayout) findViewById(R.id.layout_close);
        btn_close.setOnClickListener(onClickListener);

        Intent intent = getIntent();

        LinkedHashMap<String, String> param = new LinkedHashMap<String, String>();

        param.put("ouId", Util.nullString(intent.getStringExtra("OU_ID"), ""));
        param.put("orgId", Util.nullString(intent.getStringExtra("ORG_ID"), ""));
        param.put("userNo", Util.nullString(S_USER_ID, ""));
        param.put("scanGroupId", Util.nullString(intent.getStringExtra("scanGroupId"), ""));
        param.put("pickType", Util.nullString(intent.getStringExtra("pickType"), ""));
        param.put("soTroNoBarCode", Util.nullString(intent.getStringExtra("soTroNoBarCode"), ""));

        network = new Network(PickTicketMoveDtlActivity.this, listener, "/api/move/noPickItemList");
        network.execute(param);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.equals(btn_close)) {
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
            listView = (ListView)findViewById(R.id.list_ticket_dtl);
            moveArr = new ArrayList<>();

            if(api.equals("/api/move/noPickItemList")) {
                try {
                    JSONArray list = obj.getJSONArray("list");

                    checkList(list);

                    for(int i=0; i<list.length(); i++){
                        JSONObject data = list.getJSONObject(i);

                        PickTicketMoveDtlItem info = new PickTicketMoveDtlItem(Util.nullString(data.getString("PICK_QTY"),"")
                                , Util.nullString(data.getString("TRANSACTION_UOM"),"")
                                , Util.nullString(data.getString("LOCATOR_CODE"),"")
                                , Util.nullString(data.getString("ITEM_DESC"),"")
                                , Util.nullString(data.getString("SUBINVENTORY_CODE"),"")
                                , Util.nullString(data.getString("ITEM_CODE"),"")
                                , Util.nullString(data.getString("PALLET_NUMBER"),"")
                                , Util.nullString(data.getString("LOT_NUMBER"),"")
                        );
                        moveArr.add(info);
                    }

                    moveAdapter = new PickTicketMoveDtlAdapter(PickTicketMoveDtlActivity.this, moveArr);
                    listView.setAdapter(moveAdapter);

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
            AlertDialog.Builder builder = new AlertDialog.Builder(PickTicketMoveDtlActivity.this);
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
