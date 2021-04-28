package com.pulmuone.mrtina.tranTro;

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
import com.pulmuone.mrtina.popupLot.PopLotActivity;
import com.pulmuone.mrtina.popupSubInv.PopSubInvActivity;

import java.util.ArrayList;

import static com.pulmuone.mrtina.R.id.chk_tro_item;
import static com.pulmuone.mrtina.R.id.edit_loc;
import static com.pulmuone.mrtina.R.id.edit_sub_inv;
import static com.pulmuone.mrtina.R.id.edit_tro_lot;
import static com.pulmuone.mrtina.R.id.edit_tro_qty;
import static com.pulmuone.mrtina.R.id.layout_search_loc;
import static com.pulmuone.mrtina.R.id.layout_search_lot;
import static com.pulmuone.mrtina.R.id.layout_search_sub_inv;
import static com.pulmuone.mrtina.R.id.txt_item_name;
import static com.pulmuone.mrtina.R.id.txt_palt;
import static com.pulmuone.mrtina.R.id.txt_tro_item_num;
import static com.pulmuone.mrtina.R.id.txt_tro_uom;
import static com.pulmuone.mrtina.R.id.layout_scan5;
import static com.pulmuone.mrtina.R.id.layout_scan6;

public class TranTroAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private Activity m_activity;
	private ArrayList<TranTroItem> arr;

	public TranTroAdapter(Activity act, ArrayList<TranTroItem> arr_item) {
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
			convertView = mInflater.inflate(R.layout.item_tran_tro, parent, false);
		}

		if (position % 2 == 0) {
			convertView.setBackgroundColor(m_activity.getResources().getColor(R.color.white));
		} else {
			convertView.setBackgroundColor(m_activity.getResources().getColor(R.color.lightgray));
		}

		final CheckBox ITEM_CHK = (CheckBox)convertView.findViewById(chk_tro_item);
		TextView ITEM_PALLET = (TextView)convertView.findViewById(txt_palt);
		TextView ITEM_NAME = (TextView)convertView.findViewById(txt_item_name);
		TextView ITEM_NO = (TextView)convertView.findViewById(txt_tro_item_num);
		TextView ITEM_UOM = (TextView)convertView.findViewById(txt_tro_uom);

		final EditText ITEM_QTY = (EditText)convertView.findViewById(edit_tro_qty);
		final EditText ITEM_LOT = (EditText) convertView.findViewById(edit_tro_lot);
		final EditText ITEM_SUBINV	= (EditText) convertView.findViewById(edit_sub_inv);
		final EditText ITEM_LOC 		= (EditText) convertView.findViewById(edit_loc);

		ITEM_QTY.addTextChangedListener(new EditQty(ITEM_QTY));
		ITEM_QTY.setTag(position);
		ITEM_QTY.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

		ITEM_LOT.addTextChangedListener(new EditLot(ITEM_LOT));
		ITEM_LOT.setTag(position);
		ITEM_LOT.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

		ITEM_SUBINV.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
		ITEM_LOC.setFilters(new InputFilter[]{new InputFilter.AllCaps()});


		ITEM_LOT.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if(b)
					ITEM_LOT.onEditorAction(EditorInfo.IME_ACTION_DONE);
			}
		});

		ITEM_LOT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ITEM_LOT.setSelection(ITEM_LOT.length());
			}
		});

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

		ITEM_LOC.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if(b)
					ITEM_LOC.onEditorAction(EditorInfo.IME_ACTION_DONE);
			}
		});

		ITEM_LOC.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ITEM_LOC.setSelection(ITEM_LOC.length());
			}
		});

		ITEM_QTY.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if(b)
					ITEM_QTY.onEditorAction(EditorInfo.IME_ACTION_DONE);
			}
		});

		ITEM_QTY.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ITEM_QTY.setSelection(ITEM_QTY.length());
			}
		});

		LinearLayout layout_subInv      = (LinearLayout) convertView.findViewById(layout_search_sub_inv);
		LinearLayout layout_loc         = (LinearLayout) convertView.findViewById(layout_search_loc);
		LinearLayout layout_lot         = (LinearLayout) convertView.findViewById(layout_search_lot);

		LinearLayout layout_scanNo          = (LinearLayout) convertView.findViewById(layout_scan5);
		LinearLayout layout_scanName        = (LinearLayout) convertView.findViewById(layout_scan6);

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
			ITEM_PALLET.setBackground(ContextCompat.getDrawable((Activity)m_activity, R.drawable.bg_input_ds));
			ITEM_PALLET.setClickable(false);
			ITEM_PALLET.setEnabled(false);
			ITEM_PALLET.setFocusable(false);
		}else{
			ITEM_PALLET.setClickable(true);
			ITEM_PALLET.setEnabled(true);
			ITEM_PALLET.setFocusable(true);
		}

		if(arr.get(position).getLocatorControl().equals("1")){
			layout_loc.setEnabled(false);
			layout_loc.setClickable(false);
			ITEM_LOC.setBackground(ContextCompat.getDrawable((Activity)m_activity, R.drawable.bg_input_ds));
			ITEM_LOC.setClickable(false);
			ITEM_LOC.setEnabled(false);
			ITEM_LOC.setFocusable(false);
		}else{
			layout_loc.setEnabled(true);
			layout_loc.setClickable(true);
			ITEM_LOC.setBackground(ContextCompat.getDrawable((Activity)m_activity, R.drawable.bg_input));
			ITEM_LOC.setClickable(true);
			ITEM_LOC.setEnabled(true);
			ITEM_LOC.setFocusable(true);
		}

		if(arr.get(position).getLotControlCode().equals("1")){
			ITEM_LOT.setBackground(ContextCompat.getDrawable((Activity)m_activity, R.drawable.bg_input_ds));
			ITEM_LOT.setClickable(false);
			ITEM_LOT.setEnabled(false);
			ITEM_LOT.setFocusable(false);
			layout_lot.setEnabled(false);
			layout_lot.setClickable(false);
		}else{
			ITEM_LOT.setBackground(ContextCompat.getDrawable((Activity)m_activity, R.drawable.bg_input));
			ITEM_LOT.setClickable(true);
			ITEM_LOT.setEnabled(true);
			ITEM_LOT.setFocusable(true);
			layout_lot.setEnabled(true);
			layout_lot.setClickable(true);
		}

		ITEM_PALLET.setText(arr.get(position).PALLET_NUMBER);
		ITEM_NO.setText(arr.get(position).ITEM_CODE);
		ITEM_NAME.setText(arr.get(position).ITEM_DESC);
		ITEM_UOM.setText(arr.get(position).TRANSACTION_UOM);
		ITEM_LOT.setText(arr.get(position).LOT_NUMBER);
		ITEM_QTY.setText(arr.get(position).SHIPMENT_QTY);
		ITEM_SUBINV.setText(arr.get(position).SUBINV_CODE_FROM);
		ITEM_LOC.setText(arr.get(position).LOCATOR_CODE_FROM);

		if(arr.get(position).CHECK_ITEM.equals("True")){
			ITEM_CHK.setChecked(true);
		}else{
			ITEM_CHK.setChecked(false);
		}

		ITEM_CHK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ITEM_CHK.isChecked()){
					ITEM_CHK.setChecked(true);
					arr.get(position).CHECK_ITEM = "True";
				} else{
					ITEM_CHK.setChecked(false);
					arr.get(position).CHECK_ITEM = "False";
				}
			}
		});

		layout_subInv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(m_activity, PopSubInvActivity.class);
				TranTroItem listViewItem = arr.get(position);
				intent.putExtra("POSITION", position);
				intent.putExtra("ORG_ID", listViewItem.getOrgId());
				((Activity)m_activity).startActivityForResult(intent, Comm.SUBINV);
			}
		});

		layout_loc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(m_activity, PopLocaActivity.class);
				TranTroItem listViewItem = arr.get(position);
				intent.putExtra("SUBINVENTORY_CODE", listViewItem.getSubinvCodeFrom());
				intent.putExtra("POSITION", position);
				intent.putExtra("ORG_ID", listViewItem.getOrgId());
				((Activity)m_activity).startActivityForResult(intent, Comm.LOC);
			}
		});

		layout_lot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(m_activity, PopLotActivity.class);
				TranTroItem listViewItem = arr.get(position);
				intent.putExtra("OU_ID", listViewItem.getOuId());
				intent.putExtra("ORG_ID", listViewItem.getOrgId());
				intent.putExtra("PO_HEADER_ID", listViewItem.getShipmentHeaderId());
				intent.putExtra("PO_NO", "");
				intent.putExtra("PO_LINE_ID", listViewItem.getShipmentLineId());
				intent.putExtra("ITEM_ID", listViewItem.getInventoryItemId());
				intent.putExtra("POSITION", position);
				((Activity)m_activity).startActivityForResult(intent, Comm.LOT);
			}
		});

		layout_scanNo.setOnClickListener(arr.get(position).onClickListener);
		layout_scanNo.setTag(position);

		layout_scanName.setOnClickListener(arr.get(position).onClickListener);
		layout_scanName.setTag(position);


		return convertView;
	}

	private class EditQty implements TextWatcher {

		private EditText editTranQty;
		private int pos;

		public EditQty(EditText editTranQty) {
			this.editTranQty = editTranQty;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			this.pos = (int) editTranQty.getTag();

			TranTroItem listViewItem = arr.get(pos);
			if(listViewItem != null){
				listViewItem.setShipmentQty(s.toString());
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	}

	private class EditLot implements TextWatcher{

		private EditText editTranLot;
		private int pos;

		public EditLot(EditText editTranLot) {
			this.editTranLot = editTranLot;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			this.pos = (int) editTranLot.getTag();

			TranTroItem listViewItem = arr.get(pos);
			if(listViewItem != null){
				listViewItem.setLotNumber(s.toString());
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	}

}


