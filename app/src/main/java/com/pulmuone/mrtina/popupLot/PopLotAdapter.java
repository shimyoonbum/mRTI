package com.pulmuone.mrtina.popupLot;

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

import static com.pulmuone.mrtina.R.id.txt_lot_date;
import static com.pulmuone.mrtina.R.id.txt_lot_no;
import static com.pulmuone.mrtina.R.id.txt_po_date;
import static com.pulmuone.mrtina.R.id.txt_po_no;
import static com.pulmuone.mrtina.R.id.txt_supl;

public class PopLotAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private Activity m_activity;
	private ArrayList<PopLotItem> arr;

	public PopLotAdapter(Activity act, ArrayList<PopLotItem> arr_item) {
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
			convertView = mInflater.inflate(R.layout.item_popup_lot, parent, false);
		}

		TextView LOT_NUM 			= (TextView)convertView.findViewById(txt_lot_no);
		TextView EXPIRATION_DATE	= (TextView)convertView.findViewById(txt_lot_date);

		LOT_NUM.setText(arr.get(position).LOT_NUMBER);
		EXPIRATION_DATE.setText(arr.get(position).EXPIRATION_DATE);

		return convertView;
	}
}


