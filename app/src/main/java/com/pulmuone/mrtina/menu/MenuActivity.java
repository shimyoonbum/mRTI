package com.pulmuone.mrtina.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.pulmuone.mrtina.R;
import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.labelPalt.PalletLabelActivity;
import com.pulmuone.mrtina.labelLoca.LocaLabelActivity;
import com.pulmuone.mrtina.login.LoginActivity;
import com.pulmuone.mrtina.moveAllcInv.MoveAllcInvActivity;
import com.pulmuone.mrtina.movePickTicket.PickTicketMoveActivity;
import com.pulmuone.mrtina.prodTran.ProdTranActivity;
import com.pulmuone.mrtina.recvPo.RecvPoActivity;
import com.pulmuone.mrtina.recvTro.RecvTroActivity;
import com.pulmuone.mrtina.tranSubInv.TranSubInvActivity;
import com.pulmuone.mrtina.tranTro.TranTroActivity;
import com.pulmuone.mrtina.utils.Util;

/**
 *  Mod date : 20201209
 *  Name : Zero9
 *  Content :  Move Allocated Inventory 추가
 *             layout_inventory - LinearLayout 추가
 *
 * **/

public class MenuActivity extends Activity {
    private LinearLayout layout_recv_po, layout_recv_tro,
            layout_loca_tran, layout_tran_tro, layout_prod_tran,
            layout_locator_label, layout_pallet_label, layout_moving,layout_inventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        layout_recv_po = (LinearLayout)findViewById(R.id.layout_recv_po);
        layout_recv_tro = (LinearLayout)findViewById(R.id.layout_recv_tro);
        layout_loca_tran = (LinearLayout)findViewById(R.id.layout_loca_tran);
        layout_tran_tro = (LinearLayout)findViewById(R.id.layout_tran_tro);
        layout_prod_tran = (LinearLayout)findViewById(R.id.layout_prod_tran);
        layout_locator_label = (LinearLayout)findViewById(R.id.layout_locator_label);
        layout_pallet_label = (LinearLayout)findViewById(R.id.layout_pallet_label);
        layout_moving = (LinearLayout)findViewById(R.id.layout_so_moving);
        layout_inventory = (LinearLayout)findViewById(R.id.layout_move_inventory);

        layout_recv_po.setOnClickListener(onClickListener);
        layout_recv_tro.setOnClickListener(onClickListener);
        layout_loca_tran.setOnClickListener(onClickListener);
        layout_tran_tro.setOnClickListener(onClickListener);
        layout_prod_tran.setOnClickListener(onClickListener);
        layout_locator_label.setOnClickListener(onClickListener);
        layout_pallet_label.setOnClickListener(onClickListener);
        layout_moving.setOnClickListener(onClickListener);
        layout_inventory.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if (arg0.equals(layout_recv_po)){
                Intent intent = new Intent(getApplicationContext(), RecvPoActivity.class);
                startActivity(intent);
            }else if(arg0.equals(layout_recv_tro)){
                Intent intent = new Intent(getApplicationContext(), RecvTroActivity.class);
                startActivity(intent);
            }else if(arg0.equals(layout_loca_tran)){
                Intent intent = new Intent(getApplicationContext(), TranSubInvActivity.class);
                startActivity(intent);
            }else if(arg0.equals(layout_tran_tro)){
                Intent intent = new Intent(getApplicationContext(), TranTroActivity.class);
                startActivity(intent);
            }else if(arg0.equals(layout_prod_tran)){
                Intent intent = new Intent(getApplicationContext(), ProdTranActivity.class);
                startActivity(intent);
            }else if(arg0.equals(layout_pallet_label)){
                Intent intent = new Intent(getApplicationContext(), PalletLabelActivity.class);
                startActivity(intent);
            }else if(arg0.equals(layout_locator_label)){
                Intent intent = new Intent(getApplicationContext(), LocaLabelActivity.class);
                startActivity(intent);
            }else if(arg0.equals(layout_moving)){
                Intent intent = new Intent(getApplicationContext(), PickTicketMoveActivity.class);
                startActivity(intent);
            }else if(arg0.equals(layout_inventory)){
                Intent intent = new Intent(getApplicationContext(), MoveAllcInvActivity.class);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
        builder
                .setTitle(getString(R.string.decision))
                .setMessage(getString(R.string.alert_logout))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 2020-08-24 메뉴에서 로그인 activity를 호출하면 로그인 activity가 두개가 생기므로..finish 로 변경.
                        //Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        //startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.no),null)
                .show();
    }
}
