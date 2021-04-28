package com.pulmuone.mrtina.popupPall;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pulmuone.mrtina.R;

import java.util.ArrayList;

import static com.pulmuone.mrtina.R.id.txt_code;
import static com.pulmuone.mrtina.R.id.txt_item_no;
import static com.pulmuone.mrtina.R.id.txt_loc;
import static com.pulmuone.mrtina.R.id.txt_name;
import static com.pulmuone.mrtina.R.id.txt_pall_no;
import static com.pulmuone.mrtina.R.id.txt_sub_inv;

public class PopPallAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private Activity m_activity;
	private ArrayList<PopPallItem> arr;

	public PopPallAdapter(Activity act, ArrayList<PopPallItem> arr_item) {
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
			convertView = mInflater.inflate(R.layout.item_popup_pall, parent, false);
		}

		TextView PALL_NO = (TextView)convertView.findViewById(txt_pall_no);
		TextView ITEM_NO = (TextView)convertView.findViewById(txt_item_no);
		TextView SUB_INV = (TextView)convertView.findViewById(txt_sub_inv);
		TextView LOC = (TextView)convertView.findViewById(txt_loc);

		PALL_NO.setText(arr.get(position).PALLET_NUMBER);
		ITEM_NO.setText(arr.get(position).ITEM_CODE);
		SUB_INV.setText(arr.get(position).SUBINVENTORY_CODE);
		LOC.setText(arr.get(position).LOCATOR_CODE);

		return convertView;
	}

}


