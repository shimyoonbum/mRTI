package com.pulmuone.mrtina.recvPo;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pulmuone.mrtina.R;
import com.pulmuone.mrtina.comm.Comm;
import com.pulmuone.mrtina.popupLoca.PopLocaActivity;
import com.pulmuone.mrtina.popupPall.PopPallActivity;
import com.pulmuone.mrtina.popupSubInv.PopSubInvActivity;

import static com.pulmuone.mrtina.R.id.btn_new_pall;
import static com.pulmuone.mrtina.R.id.chk_po_item;
import static com.pulmuone.mrtina.R.id.edit_lot;
import static com.pulmuone.mrtina.R.id.edit_qty;
import static com.pulmuone.mrtina.R.id.edit_sub_inv;
import static com.pulmuone.mrtina.R.id.img_po_plus;
import static com.pulmuone.mrtina.R.id.layout_exp_date;
import static com.pulmuone.mrtina.R.id.layout_po_plus;
import static com.pulmuone.mrtina.R.id.layout_search_loc;
import static com.pulmuone.mrtina.R.id.layout_search_lot;
import static com.pulmuone.mrtina.R.id.layout_search_palt;
import static com.pulmuone.mrtina.R.id.layout_search_sub_inv;
import static com.pulmuone.mrtina.R.id.txt_exp_date;
import static com.pulmuone.mrtina.R.id.txt_item_name;
import static com.pulmuone.mrtina.R.id.txt_item_no;
import static com.pulmuone.mrtina.R.id.txt_loc;
import static com.pulmuone.mrtina.R.id.txt_palt;
import static com.pulmuone.mrtina.R.id.txt_uom;
import static com.pulmuone.mrtina.R.id.layout_scan1;
import static com.pulmuone.mrtina.R.id.layout_scan2;

import java.util.ArrayList;

public class RecvPoAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	private Activity m_activity;
	private ArrayList<RecvPoItem> arr;

	public RecvPoAdapter(Activity act, ArrayList<RecvPoItem> arr_item) {
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
	@Override
	public long getItemId(int position){
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		// "item_recv_po" Layout을 inflate하여 convertView 참조 획득.
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.item_recv_po, parent, false);
		}

		// 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
		if (position % 2 == 0) {
			convertView.setBackgroundColor(m_activity.getResources().getColor(R.color.white));
		} else {
			convertView.setBackgroundColor(m_activity.getResources().getColor(R.color.lightgray));
		}

		final CheckBox ITEM_CHK 	 = (CheckBox)convertView.findViewById(chk_po_item);
		TextView ITEM_NO 			 = (TextView)convertView.findViewById(txt_item_no);
		TextView ITEM_DESC 			 = (TextView)convertView.findViewById(txt_item_name);
		TextView ITEM_UOM 			 = (TextView)convertView.findViewById(txt_uom);
		TextView text_date			 = (TextView)convertView.findViewById(txt_exp_date);
		TextView ITEM_LOC 		     = (TextView)convertView.findViewById(txt_loc);
		TextView ITEM_PALT 			 = (TextView)convertView.findViewById(txt_palt);

		final EditText ITEM_QTY 	 = (EditText)convertView.findViewById(edit_qty);
		final EditText ITEM_LOT 	 = (EditText)convertView.findViewById(edit_lot);
		final EditText SUB_INV 		 = (EditText)convertView.findViewById(edit_sub_inv);

		ITEM_QTY.addTextChangedListener(new EditQty(ITEM_QTY));
		ITEM_QTY.setTag(position);
		ITEM_QTY.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

		ITEM_LOT.addTextChangedListener(new EditLot(ITEM_LOT));
		ITEM_LOT.setTag(position);
		ITEM_LOT.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

		SUB_INV.addTextChangedListener(new EditInv(SUB_INV));
		SUB_INV.setTag(position);
		SUB_INV.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

		Button new_pallet 		     = (Button)convertView.findViewById(btn_new_pall);

		LinearLayout layout_lotList     = (LinearLayout) convertView.findViewById(layout_search_lot) ;
        LinearLayout layout_subInv      = (LinearLayout) convertView.findViewById(layout_search_sub_inv);
        LinearLayout layout_Loc         = (LinearLayout) convertView.findViewById(layout_search_loc);
        LinearLayout layout_palt        = (LinearLayout) convertView.findViewById(layout_search_palt);
		LinearLayout layout_plus  		 = (LinearLayout) convertView.findViewById(layout_po_plus);
        LinearLayout layout_date        = (LinearLayout) convertView.findViewById(layout_exp_date);
		LinearLayout layout_scanNo   = (LinearLayout) convertView.findViewById(layout_scan1);
        LinearLayout layout_scanName = (LinearLayout) convertView.findViewById(layout_scan2);

		ImageView img_plus = (ImageView) convertView.findViewById(img_po_plus);

		if(arr.get(position).IMAGE.equals("M"))
			img_plus.setBackground(ContextCompat.getDrawable((Activity)m_activity, R.drawable.icon_minus));
		else
			img_plus.setBackground(ContextCompat.getDrawable((Activity)m_activity, R.drawable.icon_plus));

		ITEM_NO.setText(arr.get(position).getItemCode());
		ITEM_DESC.setText(arr.get(position).ITEM_DESC);
		ITEM_UOM.setText(arr.get(position).ORDER_UOM_CODE);
		ITEM_QTY.setText(arr.get(position).PO_QTY);
		ITEM_LOT.setText(arr.get(position).LOT_NUMBER);
		SUB_INV.setText(arr.get(position).SUBINVENTORY_CODE);
		ITEM_LOC.setText(arr.get(position).LOCATOR_CODE);
		ITEM_PALT.setText(arr.get(position).PALLET_NUM);
		text_date.setText(arr.get(position).EXPIRATION_DATE);

		SUB_INV.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

		SUB_INV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if(b)
					SUB_INV.onEditorAction(EditorInfo.IME_ACTION_DONE);
			}
		});

		SUB_INV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SUB_INV.setSelection(SUB_INV.length());
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
			ITEM_LOC.setBackground(ContextCompat.getDrawable((Activity)m_activity, R.drawable.bg_input_ds));
			ITEM_LOC.setClickable(false);
			ITEM_LOC.setEnabled(false);
			ITEM_LOC.setFocusable(false);
			layout_Loc.setEnabled(false);
			layout_Loc.setClickable(false);
		}else{
			ITEM_LOC.setBackground(ContextCompat.getDrawable((Activity)m_activity, R.drawable.bg_input));
			ITEM_LOC.setClickable(true);
			ITEM_LOC.setEnabled(true);
			ITEM_LOC.setFocusable(true);
			layout_Loc.setEnabled(true);
			layout_Loc.setClickable(true);
		}

		if(arr.get(position).getLotControlCode().equals("1")){
			ITEM_LOT.setBackground(ContextCompat.getDrawable((Activity)m_activity, R.drawable.bg_input_ds));
			ITEM_LOT.setClickable(false);
			ITEM_LOT.setEnabled(false);
			ITEM_LOT.setFocusable(false);
			text_date.setBackground(ContextCompat.getDrawable((Activity)m_activity, R.drawable.bg_input_ds));
			text_date.setClickable(false);
			text_date.setEnabled(false);
			text_date.setFocusable(false);
			layout_lotList.setEnabled(false);
			layout_lotList.setClickable(false);
			layout_date.setEnabled(false);
			layout_date.setClickable(false);
		}else{
			ITEM_LOT.setBackground(ContextCompat.getDrawable((Activity)m_activity, R.drawable.bg_input));
			ITEM_LOT.setClickable(true);
			ITEM_LOT.setEnabled(true);
			ITEM_LOT.setFocusable(true);
			text_date.setBackground(ContextCompat.getDrawable((Activity)m_activity, R.drawable.bg_input));
			text_date.setClickable(true);
			text_date.setEnabled(true);
			text_date.setFocusable(true);
			layout_lotList.setEnabled(true);
			layout_lotList.setClickable(true);
			layout_date.setEnabled(true);
			layout_date.setClickable(true);
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
				RecvPoItem listViewItem = arr.get(position);
				intent.putExtra("POSITION", position);
				intent.putExtra("ORG_ID", listViewItem.getOrgId());
				((Activity)m_activity).startActivityForResult(intent, Comm.SUBINV);
			}
		});

		layout_Loc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(m_activity, PopLocaActivity.class);
				RecvPoItem listViewItem = arr.get(position);
				intent.putExtra("SUBINVENTORY_CODE",    listViewItem.getSubinventoryCode());
				intent.putExtra("ORG_ID", listViewItem.getOrgId());
				intent.putExtra("POSITION", position);
				((Activity)m_activity).startActivityForResult(intent, Comm.LOC);
			}
		});

		layout_palt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(m_activity, PopPallActivity.class);
				RecvPoItem listViewItem = arr.get(position);
				intent.putExtra("POSITION", position);
				intent.putExtra("ORG_ID", listViewItem.getOrgId());
				intent.putExtra("SUBINVENTORY_CODE",    listViewItem.getSubinventoryCode());
				intent.putExtra("INVENTORY_LOCATION_ID", listViewItem.getInventoryLocationId());
				intent.putExtra("LOCATOR_CODE", listViewItem.getLocatorCode());
				intent.putExtra("ITEM_NO", listViewItem.getItemCode());

				((Activity)m_activity).startActivityForResult(intent, Comm.PALLET);
			}
		});

		layout_plus.setTag(position);
		layout_plus.setOnClickListener(arr.get(position).onClickListener);

        text_date.setOnClickListener(arr.get(position).onClickListener);
        text_date.setTag(position);

		layout_date.setOnClickListener(arr.get(position).onClickListener);
		layout_date.setTag(position);

		layout_lotList.setOnClickListener(arr.get(position).onClickListener);
		layout_lotList.setTag(position);

		new_pallet.setOnClickListener(arr.get(position).onClickListener);
		new_pallet.setTag(position);

		layout_scanNo.setOnClickListener(arr.get(position).onClickListener);
		layout_scanNo.setTag(position);

		layout_scanName.setOnClickListener(arr.get(position).onClickListener);
		layout_scanName.setTag(position);

		return convertView;
	}

	public void addItem(RecvPoItem plusItem, int position){

		RecvPoItem item = new RecvPoItem();

		item.ORG_CODE = plusItem.ORG_CODE;
		item.PO_HEADER_ID = plusItem.PO_HEADER_ID;
		item.PO_LINE_ID = plusItem.PO_LINE_ID;
		item.PO_LINE_LOCATION_ID = plusItem.PO_LINE_LOCATION_ID;
		item.VENDOR_ID = plusItem.VENDOR_ID;
		item.ITEM_CODE = plusItem.ITEM_CODE;
		item.ORDER_UOM_CODE = plusItem.ORDER_UOM_CODE;
		item.SUBINVENTORY_CODE = plusItem.SUBINVENTORY_CODE;
		item.LOCATOR_CODE = plusItem.LOCATOR_CODE;
		item.INVENTORY_LOCATION_ID = plusItem.INVENTORY_LOCATION_ID;
		item.LOT_NUMBER = plusItem.LOT_NUMBER;
		item.LOT_CONTROL_CODE = plusItem.LOT_CONTROL_CODE;
		item.EXPIRATION_DATE = plusItem.EXPIRATION_DATE;
		item.RECEIVING_QTY = plusItem.RECEIVING_QTY;
		item.BILL_OF_LADING = plusItem.BILL_OF_LADING;
		item.PALLET_CONTROL = plusItem.PALLET_CONTROL;
		item.PALLET_ID = plusItem.PALLET_ID;
		item.PALLET_NUM = plusItem.PALLET_NUM;
		item.ITEM_DESC = plusItem.ITEM_DESC;
		item.ITEM_ID = plusItem.ITEM_ID;
		item.PO_QTY = "";
		item.LOCATOR_CONTROL = plusItem.LOCATOR_CONTROL;
		item.OU_ID = plusItem.OU_ID;
		item.ORG_ID = plusItem.ORG_ID;
		item.RECEIPT_DATE = plusItem.RECEIPT_DATE;
		item.USER_NO = plusItem.USER_NO;
		item.PO_NO = plusItem.PO_NO;
		item.CHECK_ITEM = plusItem.CHECK_ITEM;
		item.BACKCOLOR = "N";
		item.onClickListener = plusItem.onClickListener;
		item.setImage("M");

		arr.add(position, item);
		notifyDataSetChanged();
	}

	public void deleteItem(int position){
		arr.remove(position);
		notifyDataSetChanged();
	}

	private class EditQty implements TextWatcher{

        private EditText editRecvQty;
        private int pos;

        public EditQty(EditText editRecQty) {
            this.editRecvQty = editRecQty;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            this.pos = (int) editRecvQty.getTag();

			RecvPoItem listViewItem = arr.get(pos);
            if(listViewItem != null){
				listViewItem.setPoQty(s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

	private class EditLot implements TextWatcher{

		private EditText editRecvLOT;
		private int pos;

		public EditLot(EditText editRecvLOT) {
			this.editRecvLOT = editRecvLOT;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			this.pos = (int) editRecvLOT.getTag();

			RecvPoItem listViewItem = arr.get(pos);
			if(listViewItem != null){
				listViewItem.setLotNumber(s.toString());
			}
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	}

	private class EditInv implements TextWatcher{

		private EditText editRecvInv;
		private int pos;

		public EditInv(EditText editRecvInv) {
			this.editRecvInv = editRecvInv;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			this.pos = (int) editRecvInv.getTag();

			RecvPoItem listViewItem = arr.get(pos);
			if(listViewItem != null){
				listViewItem.setSubinventoryCode(s.toString());
			}
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	}
}


