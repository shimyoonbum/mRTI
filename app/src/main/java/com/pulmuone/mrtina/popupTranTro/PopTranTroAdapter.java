package com.pulmuone.mrtina.popupTranTro;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pulmuone.mrtina.R;

import java.util.ArrayList;

import static com.pulmuone.mrtina.R.id.txt_from;
import static com.pulmuone.mrtina.R.id.txt_ship_date;
import static com.pulmuone.mrtina.R.id.txt_to;
import static com.pulmuone.mrtina.R.id.txt_tro_num;

public class PopTranTroAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private Activity m_activity;
	private ArrayList<PopTranTroItem> arr;

	public PopTranTroAdapter(Activity act, ArrayList<PopTranTroItem> arr_item) {
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
			convertView = mInflater.inflate(R.layout.item_popup_tro, parent, false);
		}

		TextView TRO_NUM = (TextView)convertView.findViewById(txt_tro_num);
		TextView TRO_FROM = (TextView)convertView.findViewById(txt_from);
		TextView TRO_TO = (TextView)convertView.findViewById(txt_to);
		TextView TRO_SHIP_DATE = (TextView)convertView.findViewById(txt_ship_date);

		TRO_NUM.setText(arr.get(position).SHIPMENT_NO);
		TRO_FROM.setText(arr.get(position).ORG_CODE_FROM);
		TRO_TO.setText(arr.get(position).ORG_CODE_TO);
		TRO_SHIP_DATE.setText(arr.get(position).SHIPMENT_DATE);

		return convertView;
	}

}


