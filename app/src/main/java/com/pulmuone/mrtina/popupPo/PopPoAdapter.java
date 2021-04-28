package com.pulmuone.mrtina.popupPo;

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

import static com.pulmuone.mrtina.R.id.txt_po_date;
import static com.pulmuone.mrtina.R.id.txt_po_no;
import static com.pulmuone.mrtina.R.id.txt_supl;

public class PopPoAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private Activity m_activity;
	private ArrayList<PopPoItem> arr;

	public PopPoAdapter(Activity act, ArrayList<PopPoItem> arr_item) {
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
			convertView = mInflater.inflate(R.layout.item_popup_po, parent, false);
		}

		TextView PO_NO 			= (TextView)convertView.findViewById(txt_po_no);
		TextView SUPPLIER 			= (TextView)convertView.findViewById(txt_supl);
		TextView PO_DATE 			= (TextView)convertView.findViewById(txt_po_date);

		PO_NO.setText(arr.get(position).PO_NO);
		PO_NO.setGravity(Gravity.CENTER);
		SUPPLIER.setText(arr.get(position).SUPPLIER);
		SUPPLIER.setGravity(Gravity.CENTER);
		PO_DATE.setText(arr.get(position).PO_DATE);
		PO_DATE.setGravity(Gravity.CENTER);

		return convertView;
	}

}


