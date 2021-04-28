package com.pulmuone.mrtina.movePickTicket;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pulmuone.mrtina.R;

import java.util.ArrayList;

import static com.pulmuone.mrtina.R.id.txt_item_name;
import static com.pulmuone.mrtina.R.id.txt_item_num;
import static com.pulmuone.mrtina.R.id.txt_loc;
import static com.pulmuone.mrtina.R.id.txt_lot;
import static com.pulmuone.mrtina.R.id.txt_palt;
import static com.pulmuone.mrtina.R.id.txt_qty;
import static com.pulmuone.mrtina.R.id.txt_sub_inv;
import static com.pulmuone.mrtina.R.id.txt_uom;

public class PickTicketMoveAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private Activity m_activity;
	private ArrayList<PickTicketMoveItem> arr;

	public PickTicketMoveAdapter(Activity act, ArrayList<PickTicketMoveItem> arr_item) {
		this.m_activity = act;
		arr = arr_item;
		mInflater = (LayoutInflater)m_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void clear(){
		arr.clear();
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
			convertView = mInflater.inflate(R.layout.item_move_ticket, parent, false);
		}

		if (position % 2 == 0) {
			convertView.setBackgroundColor(m_activity.getResources().getColor(R.color.white));
		} else {
			convertView.setBackgroundColor(m_activity.getResources().getColor(R.color.lightgray));
		}

		TextView ITEM_NO 			= (TextView)convertView.findViewById(txt_item_num);
		TextView ITEM_NAME 			= (TextView)convertView.findViewById(txt_item_name);
		TextView ITEM_UOM 			= (TextView)convertView.findViewById(txt_uom);
		TextView ITEM_LOT 			= (TextView)convertView.findViewById(txt_lot);
		TextView ITEM_QTY 			= (TextView)convertView.findViewById(txt_qty);
		TextView ITEM_PALT 			= (TextView)convertView.findViewById(txt_palt);
		TextView ITEM_LOC 			= (TextView) convertView.findViewById(txt_loc);
		TextView ITEM_SUBINV 		= (TextView) convertView.findViewById(txt_sub_inv);

		ITEM_NO.setText(arr.get(position).ITEM_CODE);
		ITEM_NAME.setText(arr.get(position).ITEM_DESC);
		ITEM_UOM.setText(arr.get(position).PRIMARY_UOM_CODE);
		ITEM_LOT.setText(arr.get(position).LOT_NUMBER);
		ITEM_QTY.setText(arr.get(position).ITEM_QTY);
		ITEM_PALT.setText(arr.get(position).PALLET_NUMBER);
		ITEM_SUBINV.setText(arr.get(position).SUBINVENTORY_CODE);
		ITEM_LOC.setText(arr.get(position).LOCATOR_CODE);

		return convertView;
	}
}


