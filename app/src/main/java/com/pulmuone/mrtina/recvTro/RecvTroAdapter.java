package com.pulmuone.mrtina.recvTro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pulmuone.mrtina.R;
import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.popupLoca.PopLocaActivity;
import com.pulmuone.mrtina.popupPall.PopPallActivity;
import com.pulmuone.mrtina.popupSubInv.PopSubInvActivity;

import java.util.ArrayList;

import static com.pulmuone.mrtina.R.id.btn_new_pall;
import static com.pulmuone.mrtina.R.id.chk_tro_item;
import static com.pulmuone.mrtina.R.id.edit_sub_inv;
//import static com.pulmuone.mrtina.R.id.layout_scan3;
//import static com.pulmuone.mrtina.R.id.layout_scan4;
import static com.pulmuone.mrtina.R.id.layout_scan3;
import static com.pulmuone.mrtina.R.id.layout_scan4;
import static com.pulmuone.mrtina.R.id.layout_search_loc;
import static com.pulmuone.mrtina.R.id.layout_search_palt;
import static com.pulmuone.mrtina.R.id.layout_search_sub_inv;
import static com.pulmuone.mrtina.R.id.txt_item_name;
import static com.pulmuone.mrtina.R.id.txt_loc;
import static com.pulmuone.mrtina.R.id.txt_palt;
import static com.pulmuone.mrtina.R.id.txt_tro_item_num;
import static com.pulmuone.mrtina.R.id.txt_tro_lot;
import static com.pulmuone.mrtina.R.id.txt_tro_qty;
import static com.pulmuone.mrtina.R.id.txt_tro_uom;

public class RecvTroAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private Activity m_activity;
	private ArrayList<RecvTroItem> arr;

	public RecvTroAdapter(Activity act, ArrayList<RecvTroItem> arr_item) {
		this.m_activity = act;
		arr = arr_item;
		mInflater = (LayoutInflater)m_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return arr.size();
	}
	@Override
	public Object getItem(int position) {
		return arr.get(position);
	}
	public long getItemId(int position){
		return position;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.item_recv_tro, parent, false);
		}

		if (position % 2 == 0) {
			convertView.setBackgroundColor(m_activity.getResources().getColor(R.color.white));
		} else {
			convertView.setBackgroundColor(m_activity.getResources().getColor(R.color.lightgray));
		}

		final CheckBox ITEM_CHK 	= (CheckBox)convertView.findViewById(chk_tro_item);
		TextView ITEM_NO 			= (TextView)convertView.findViewById(txt_tro_item_num);
		TextView ITEM_NAME 			= (TextView)convertView.findViewById(txt_item_name);
		TextView ITEM_UOM 			= (TextView)convertView.findViewById(txt_tro_uom);
		TextView ITEM_LOT 			= (TextView)convertView.findViewById(txt_tro_lot);
		final TextView ITEM_QTY 			= (TextView)convertView.findViewById(txt_tro_qty);
		TextView ITEM_PALT 			= (TextView)convertView.findViewById(txt_palt);
		TextView ITEM_LOC 			= (TextView) convertView.findViewById(txt_loc);
		final EditText ITEM_SUBINV 		= (EditText) convertView.findViewById(edit_sub_inv);

		ITEM_SUBINV.addTextChangedListener(new EditInv(ITEM_SUBINV));
		ITEM_SUBINV.setTag(position);
		ITEM_SUBINV.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

		LinearLayout layout_subInv      = (LinearLayout) convertView.findViewById(layout_search_sub_inv);
		LinearLayout layout_Loc         = (LinearLayout) convertView.findViewById(layout_search_loc);
		LinearLayout layout_palt        = (LinearLayout) convertView.findViewById(layout_search_palt);
		LinearLayout layout_scanNo          = (LinearLayout) convertView.findViewById(layout_scan3);
		LinearLayout layout_scanName       = (LinearLayout) convertView.findViewById(layout_scan4);
		Button new_pallet 		    = (Button)convertView.findViewById(btn_new_pall);

		ITEM_SUBINV.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

		ITEM_SUBINV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if(b)
					ITEM_SUBINV.onEditorAction(EditorInfo.IME_ACTION_DONE);
			}
		});

		ITEM_SUBINV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ITEM_SUBINV.setSelection(ITEM_SUBINV.length());
			}
		});

		ITEM_NO.setText(arr.get(position).ITEM_CODE);
		ITEM_NAME.setText(arr.get(position).ITEM_DESC);
		ITEM_UOM.setText(arr.get(position).ORDER_UOM_CODE);
		ITEM_LOT.setText(arr.get(position).LOT_NUMBER);
		ITEM_QTY.setText(arr.get(position).REMAIN_QTY);
		ITEM_PALT.setText(arr.get(position).PALLET_NUMBER);
		ITEM_SUBINV.setText(arr.get(position).SUBINVENTORY_CODE);
		ITEM_LOC.setText(arr.get(position).LOCATOR_CODE);

		if(arr.get(position).getBackColor().equals("T")){
			convertView.setBackground(ContextCompat.getDrawable((Activity)m_activity, R.drawable.listview_layout));
		}else{
			if (position % 2 == 0) {
				convertView.setBackgroundColor(m_activity.getResources().getColor(R.color.white));
			} else {
				convertView.setBackgroundColor(m_activity.getResources().getColor(R.color.lightgray));
			}
		}

		if(arr.get(position).getPalletControl().equals("N")){
			ITEM_PALT.setBackground(ContextCompat.getDrawable((Activity)m_activity, R.drawable.bg_input_ds));
			ITEM_PALT.setClickable(false);
			ITEM_PALT.setEnabled(false);
			ITEM_PALT.setFocusable(false);
			new_pallet.setEnabled(false);
			layout_palt.setClickable(false);
			layout_palt.setEnabled(false);
		}else{
			ITEM_PALT.setBackground(ContextCompat.getDrawable((Activity)m_activity, R.drawable.bg_input));
			ITEM_PALT.setClickable(true);
			ITEM_PALT.setEnabled(true);
			ITEM_PALT.setFocusable(true);
			new_pallet.setEnabled(true);
			layout_palt.setClickable(true);
			layout_palt.setEnabled(true);
		}

		if(arr.get(position).getLocatorControl().equals("1")){
			layout_Loc.setEnabled(false);
			layout_Loc.setClickable(false);
			ITEM_LOC.setBackground(ContextCompat.getDrawable((Activity)m_activity, R.drawable.bg_input_ds));
			ITEM_LOC.setClickable(false);
			ITEM_LOC.setEnabled(false);
			ITEM_LOC.setFocusable(false);
		}else{
			layout_Loc.setEnabled(true);
			layout_Loc.setClickable(true);
			ITEM_LOC.setBackground(ContextCompat.getDrawable((Activity)m_activity, R.drawable.bg_input));
			ITEM_LOC.setClickable(true);
			ITEM_LOC.setEnabled(true);
			ITEM_LOC.setFocusable(true);
		}

		if(arr.get(position).CHECK_ITEM.equals("True")){
			ITEM_CHK.setChecked(true);
		}else{
			ITEM_CHK.setChecked(false);
		}

		ITEM_CHK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ITEM_CHK.isChecked()){
					Log.v(Comm.LOG_TAG, "Checked " );
					ITEM_CHK.setChecked(true);
					arr.get(position).CHECK_ITEM = "True";
				} else{
					Log.v(Comm.LOG_TAG, "unChecked " );
					ITEM_CHK.setChecked(false);
					arr.get(position).CHECK_ITEM = "False";
				}
			}
		});

		layout_subInv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(m_activity, PopSubInvActivity.class);
				RecvTroItem listViewItem = arr.get(position);
				intent.putExtra("POSITION", position);
				intent.putExtra("ORG_ID",    listViewItem.getOrgId());
				((Activity)m_activity).startActivityForResult(intent, Comm.SUBINV);
			}
		});

		layout_Loc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(m_activity, PopLocaActivity.class);
				RecvTroItem listViewItem = arr.get(position);
				intent.putExtra("ORG_ID",    listViewItem.getOrgId());
				intent.putExtra("SUBINVENTORY_CODE",    listViewItem.getSubinventoryCode());
				intent.putExtra("POSITION", position);
				((Activity)m_activity).startActivityForResult(intent, Comm.LOC);
			}
		});

		layout_palt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(m_activity, PopPallActivity.class);
				RecvTroItem listViewItem = arr.get(position);
				intent.putExtra("POSITION", position);
				intent.putExtra("SUBINVENTORY_CODE",    listViewItem.getSubinventoryCode());
				intent.putExtra("INVENTORY_LOCATION_ID", listViewItem.getInventoryLocationId());
				intent.putExtra("ITEM_NO", listViewItem.getItemCode());
				((Activity)m_activity).startActivityForResult(intent, Comm.PALLET);
			}
		});

		new_pallet.setOnClickListener(arr.get(position).onClickListener);
		new_pallet.setTag(position);

		layout_scanNo.setOnClickListener(arr.get(position).onClickListener);
		layout_scanNo.setTag(position);

		layout_scanName.setOnClickListener(arr.get(position).onClickListener);
		layout_scanName.setTag(position);

		return convertView;
	}

	private class EditInv implements TextWatcher {

		private EditText editSubInv;
		private int pos;

		public EditInv(EditText editSubInv) {
			this.editSubInv = editSubInv;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			this.pos = (int) editSubInv.getTag();

			RecvTroItem listViewItem = arr.get(pos);
			if(listViewItem != null){
				listViewItem.setSubinventoryCode(s.toString());
			}
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	}

}


